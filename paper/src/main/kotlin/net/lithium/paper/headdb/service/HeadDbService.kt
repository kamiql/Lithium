package net.lithium.paper.headdb.service

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import net.lithium.common.LithiumApplication
import net.lithium.paper.headdb.Heads
import net.lithium.paper.headdb.model.HeadCache
import net.lithium.paper.headdb.model.MinecraftHead
import net.lithium.paper.headdb.repo.HeadCacheRepository
import java.io.Closeable
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.LinkedHashMap
import java.util.Locale
import java.util.logging.Level
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

class HeadDbService(
    application: LithiumApplication,
    private val config: Heads,
    private val json: Json,
    private val repository: HeadCacheRepository
) : Closeable {

    private val logger = application.applicationLogger

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Default
    )

    private val httpClient = HttpClient.newBuilder()
        .connectTimeout(
            config.requestTimeout.toJavaDuration()
        )
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()

    private val refreshMutex = Mutex()

    /**
     * Atomarer In-Memory-Snapshot.
     *
     * Der Snapshot wird erst ersetzt, nachdem der komplette Cache
     * erfolgreich in SQLite gespeichert wurde.
     */
    @Volatile
    private var snapshot: Map<String, MinecraftHead> = emptyMap()

    /**
     * Lazy gestarteter Bootstrap-Job.
     *
     * Der Service startet erst, sobald start() oder awaitReady()
     * aufgerufen wird.
     */
    private val startup = scope.async(
        start = CoroutineStart.LAZY
    ) {
        bootstrap()
    }

    /**
     * Startet den Bootstrap asynchron.
     *
     * Die Methode blockiert nicht.
     */
    fun start(): Job {
        startup.start()
        return startup
    }

    /**
     * Wartet, bis der lokale Cache geladen und gegebenenfalls
     * von der API aktualisiert wurde.
     */
    suspend fun awaitReady() {
        startup.await()
    }

    /**
     * Sucht einen Head über seine UUID beziehungsweise seinen Cache-Key.
     */
    suspend fun findByUuid(
        uuid: String
    ): MinecraftHead? {
        awaitReady()

        return snapshot[uuid]
            ?: snapshot.values.firstOrNull { head ->
                head.uuid.equals(
                    other = uuid,
                    ignoreCase = true
                )
            }
    }

    /**
     * Sucht Heads anhand von:
     *
     * - UUID
     * - Name
     * - Tags
     * - Kategorie
     */
    suspend fun search(
        query: String,
        limit: Int = 100
    ): List<MinecraftHead> {
        require(limit > 0) {
            "limit must be greater than zero."
        }

        awaitReady()

        val normalizedQuery = query
            .trim()
            .lowercase(Locale.ROOT)

        if (normalizedQuery.isBlank()) {
            return snapshot.values
                .take(limit)
        }

        return snapshot.values
            .asSequence()
            .filter { head ->
                head.uuid
                    ?.lowercase(Locale.ROOT)
                    ?.contains(normalizedQuery) == true ||

                        head.name
                            .lowercase(Locale.ROOT)
                            .contains(normalizedQuery) ||

                        head.tags.any { tag ->
                            tag.lowercase(Locale.ROOT)
                                .contains(normalizedQuery)
                        } ||

                        head.categories.any { category ->
                            category.lowercase(Locale.ROOT)
                                .contains(normalizedQuery)
                        }
            }
            .take(limit)
            .toList()
    }

    /**
     * Gibt alle gecachten Heads zurück.
     */
    suspend fun findAll(): List<MinecraftHead> {
        awaitReady()

        return snapshot.values.toList()
    }

    /**
     * Gibt die Anzahl der gecachten Heads zurück.
     */
    suspend fun size(): Int {
        awaitReady()

        return snapshot.size
    }

    /**
     * Erzwingt ein sofortiger API-Refresh.
     *
     * Alle Kategorien werden parallel geladen.
     */
    suspend fun refreshNow() {
        awaitReady()

        refreshMutex.withLock {
            refreshInternal()
        }
    }

    /**
     * Lädt beim Start zunächst den lokalen Cache.
     *
     * Ist der Cache nicht vorhanden oder abgelaufen, wird die API
     * abgefragt. Bei einem API-Fehler bleibt ein vorhandener alter
     * Cache nutzbar.
     */
    private suspend fun bootstrap() {
        val localCache = loadLocalCache()

        if (localCache != null) {
            snapshot = localCache.heads
        }

        val shouldRefresh =
            localCache == null ||
                    localCache.heads.isEmpty() ||
                    !isFresh(localCache)

        if (shouldRefresh) {
            try {
                refreshMutex.withLock {
                    refreshInternal()
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Throwable) {
                val hasUsableCache =
                    localCache != null &&
                            localCache.heads.isNotEmpty()

                if (!hasUsableCache) {
                    throw exception
                }

                logger.log(
                    Level.WARNING,
                    "Could not refresh Minecraft-Heads cache. " +
                            "Using stale local cache instead.",
                    exception
                )
            }
        }

        logger.info(
            "HeadDbService ready with ${snapshot.size} cached heads."
        )
    }

    /**
     * Lädt den lokalen SQLite-Cache.
     */
    private suspend fun loadLocalCache(): HeadCache? {
        return try {
            repository.loadCache()
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Throwable) {
            logger.log(
                Level.WARNING,
                "Could not load local Minecraft-Heads cache.",
                exception
            )

            null
        }
    }

    /**
     * Prüft, ob der Cache noch innerhalb der konfigurierten TTL liegt.
     *
     * Die Berechnung verwendet ausschließlich kotlin.time.Duration.
     */
    private fun isFresh(
        cache: HeadCache
    ): Boolean {
        val cacheTtl = config.cacheTtl

        if (cacheTtl <= ZERO) {
            return false
        }

        val ageMillis = (
                System.currentTimeMillis() -
                        cache.fetchedAtEpochMillis
                ).coerceAtLeast(0L)

        return ageMillis < cacheTtl.inWholeMilliseconds
    }

    /**
     * Lädt alle Kategorien, erstellt einen neuen Cache und speichert
     * diesen als einen Datensatz in SQLite.
     */
    private suspend fun refreshInternal() {
        val heads = fetchAllCategories()

        if (heads.isEmpty()) {
            throw HeadApiException(
                message = "Minecraft-Heads API returned no heads.",
                category = "all"
            )
        }

        val cache = HeadCache(
            fetchedAtEpochMillis = System.currentTimeMillis(),
            heads = heads
        )

        /*
         * Der vollständige Cache wird in einem SQLite-Write gespeichert.
         */
        repository.saveCache(cache)

        /*
         * Der Memory-Snapshot wird erst nach erfolgreichem Persistieren
         * aktualisiert.
         */
        snapshot = heads

        logger.info(
            "Fetched and cached ${heads.size} Minecraft heads."
        )
    }

    /**
     * Lädt alle konfigurierten Kategorien parallel.
     *
     * Die Semaphore verhindert, dass mehr Requests gleichzeitig
     * ausgeführt werden, als in der Konfiguration erlaubt sind.
     */
    private suspend fun fetchAllCategories():
            Map<String, MinecraftHead> = coroutineScope {
        val categories = config.categories
            .distinct()

        val semaphore = Semaphore(
            permits = minOf(
                config.maxParallelRequests,
                categories.size
            )
        )

        val responses = categories
            .map { category ->
                async {
                    semaphore.withPermit {
                        category to fetchCategory(category)
                    }
                }
            }
            .awaitAll()

        val merged = LinkedHashMap<String, MinecraftHead>()

        for ((category, categoryHeads) in responses) {
            for (head in categoryHeads) {
                val existing = merged[head.cacheKey]

                if (existing == null) {
                    merged[head.cacheKey] = head.copy(
                        categories = listOf(category)
                    )
                } else {
                    merged[head.cacheKey] = existing.copy(
                        categories = (
                                existing.categories + category
                                ).distinct()
                    )
                }
            }
        }

        merged.toMap()
    }

    /**
     * Lädt eine einzelne Kategorie mit Retry-Verhalten.
     */
    private suspend fun fetchCategory(
        category: String
    ): List<MinecraftHead> {
        var lastFailure: Throwable? = null

        for (attempt in 0..config.maxRetries) {
            try {
                val request = HttpRequest.newBuilder(
                    buildCategoryUri(category)
                )
                    .timeout(
                        config.requestTimeout.toJavaDuration()
                    )
                    .header("Accept", "application/json")
                    .header(
                        "User-Agent",
                        "Lithium-HeadDbService"
                    )
                    .header(
                        "api-key",
                        config.apiToken
                    )
                    .GET()
                    .build()

                val response = httpClient
                    .sendAsync(
                        request,
                        HttpResponse.BodyHandlers.ofString(
                            StandardCharsets.UTF_8
                        )
                    )
                    .await()

                val statusCode = response.statusCode()
                val responseBody = response.body()

                if (statusCode in 200..299) {
                    val root = json.parseToJsonElement(
                        responseBody
                    )

                    return extractHeadObjects(root)
                        .mapNotNull { raw ->
                            parseHead(
                                raw = raw,
                                category = category
                            )
                        }
                }

                val failure = HeadApiException(
                    message = "Minecraft-Heads API returned HTTP " +
                            "$statusCode for category '$category'.",
                    category = category,
                    statusCode = statusCode,
                    responseBody = responseBody
                )

                if (
                    !isRetryable(statusCode) ||
                    attempt == config.maxRetries
                ) {
                    throw failure
                }

                lastFailure = failure
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: HeadApiException) {
                throw exception
            } catch (exception: Throwable) {
                lastFailure = exception

                if (attempt == config.maxRetries) {
                    throw HeadApiException(
                        message = "Could not fetch category " +
                                "'$category'.",
                        category = category,
                        cause = exception
                    )
                }
            }

            delay(
                duration = (
                        250L * (attempt + 1L)
                        ).milliseconds
            )
        }

        throw HeadApiException(
            message = "Could not fetch category '$category'.",
            category = category,
            cause = lastFailure
        )
    }

    /**
     * Erstellt die URI für eine Kategorie.
     *
     * Der API-Key wird nicht als Query-Parameter versendet,
     * sondern ausschließlich als Header "api-key".
     */
    private fun buildCategoryUri(
        category: String
    ): URI {
        val encodedCategory = URLEncoder
            .encode(
                category,
                StandardCharsets.UTF_8
            )
            .replace("+", "%20")

        val path = config.categoryPath
            .replace(
                oldValue = "{category}",
                newValue = encodedCategory
            )
            .trimStart('/')

        val query = if (config.includeTags) {
            "?tags=true"
        } else {
            ""
        }

        return URI.create(
            "${config.baseUrl.trimEnd('/')}/$path$query"
        )
    }

    /**
     * Extrahiert Head-Objekte aus verschiedenen möglichen JSON-Strukturen.
     *
     * Unterstützt beispielsweise:
     *
     * [
     *   { ... }
     * ]
     *
     * oder:
     *
     * {
     *   "data": [
     *     { ... }
     *   ]
     * }
     *
     * oder:
     *
     * {
     *   "heads": [
     *     { ... }
     *   ]
     * }
     */
    private fun extractHeadObjects(
        root: JsonElement
    ): List<JsonObject> {
        val result = ArrayList<JsonObject>()

        fun visit(element: JsonElement) {
            when (element) {
                is JsonArray -> {
                    element.forEach(::visit)
                }

                is JsonObject -> {
                    val nestedHead = sequenceOf(
                        element["head"],
                        element["data"]
                    )
                        .filterIsInstance<JsonObject>()
                        .firstOrNull(::looksLikeHead)

                    when {
                        nestedHead != null -> {
                            result += nestedHead
                        }

                        looksLikeHead(element) -> {
                            result += element
                        }

                        else -> {
                            element.values.forEach(::visit)
                        }
                    }
                }

                else -> {
                    // Primitive und JsonNull enthalten keine Heads.
                }
            }
        }

        visit(root)

        return result
    }

    /**
     * Prüft, ob ein JSON-Objekt wie ein Head-Datensatz aussieht.
     */
    private fun looksLikeHead(
        data: JsonObject
    ): Boolean {
        return data.stringValue("uuid") != null ||
                data.stringValue("id") != null ||
                data.stringValue("textureUuid") != null ||
                data.stringValue("value") != null ||
                data.stringValue("texture") != null ||
                data.stringValue("base64") != null
    }

    /**
     * Konvertiert ein JSON-Objekt in dein MinecraftHead-Modell.
     */
    private fun parseHead(
        raw: JsonObject,
        category: String
    ): MinecraftHead? {
        val data = sequenceOf(
            raw["head"],
            raw["data"]
        )
            .filterIsInstance<JsonObject>()
            .firstOrNull(::looksLikeHead)
            ?: raw

        if (!looksLikeHead(data)) {
            return null
        }

        /*
         * UUID beziehungsweise ID werden bevorzugt als Cache-Key
         * verwendet. Falls die API keine ID liefert, wird ein stabiler
         * SHA-256-Key aus dem JSON-Objekt erzeugt.
         */
        val cacheKey = data.stringValue("uuid")
            ?: data.stringValue("id")
            ?: data.stringValue("textureUuid")
            ?: data.stringValue("key")
            ?: sha256(data.toString())

        return MinecraftHead(
            cacheKey = cacheKey,
            data = data,
            categories = listOf(category)
        )
    }

    /**
     * Stabiler Fallback-Key für Head-Objekte ohne ID.
     */
    private fun sha256(
        value: String
    ): String {
        val digest = MessageDigest
            .getInstance("SHA-256")
            .digest(
                value.toByteArray(
                    StandardCharsets.UTF_8
                )
            )

        return digest.joinToString("") { byte ->
            "%02x".format(
                byte.toInt() and 0xff
            )
        }
    }

    /**
     * HTTP-Statuscodes, bei denen ein erneuter Versuch sinnvoll ist.
     */
    private fun isRetryable(
        statusCode: Int
    ): Boolean {
        return statusCode == 408 ||
                statusCode == 429 ||
                statusCode in 500..599
    }

    /**
     * Liest String-Werte aus einem JsonObject.
     */
    private fun JsonObject.stringValue(
        key: String
    ): String? {
        return (this[key] as? JsonPrimitive)
            ?.contentOrNull
            ?.takeIf { it.isNotBlank() }
    }

    /**
     * Beendet den Coroutine-Scope.
     */
    override fun close() {
        scope.cancel()
    }
}

/**
 * Fehler bei API-Requests oder beim Parsen der API-Antwort.
 */
class HeadApiException(
    message: String,
    val category: String,
    val statusCode: Int? = null,
    val responseBody: String? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause)