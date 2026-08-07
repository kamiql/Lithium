package net.lithium.paper.heads

import dev.kamiql.HeadsApi
import dev.kamiql.api.builder.CategoriesBuilder
import dev.kamiql.api.builder.CollectionsBuilder
import dev.kamiql.api.builder.CustomHeadsBuilder
import dev.kamiql.model.Category
import dev.kamiql.model.Head
import dev.kamiql.model.HeadCollection
import net.lithium.paper.heads.repo.HeadsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

object HeadsService : AutoCloseable, KoinComponent {
    private val repository: HeadsRepository by inject()

    private val reloadInProgress = AtomicBoolean(false)

    @Volatile
    private var currentData: HeadsData? = null

    @Volatile
    private var closed = false

    val categories: List<Category>
        get() = requireData().categories

    val collections: List<HeadCollection>
        get() = requireData().collections

    val heads: Map<Int, List<Head>>
        get() = requireData().headsByCategory

    val allHeads: List<Head>
        get() = requireData().allHeads

    private val RELOAD_INTERVAL_MILLIS =
        TimeUnit.DAYS.toMillis(7)

    /**
     * Lädt beim Start die gespeicherten Daten.
     *
     * Wenn die gespeicherten Daten älter als sieben Tage sind
     * oder keine Daten vorhanden sind, werden die Daten neu von
     * der API geladen.
     */
    fun start() {
        check(!closed) {
            "HeadsService is already closed"
        }

        loadInitialBlocking()
    }

    /**
     * Lädt die Daten unabhängig vom Alter direkt neu von der API.
     */
    fun reload() {
        check(!closed) {
            "HeadsService is already closed"
        }

        reloadBlocking()
    }

    /**
     * Lädt die Daten nur neu, wenn sie älter als sieben Tage sind.
     *
     * @return true, wenn ein Reload durchgeführt wurde
     */
    fun reloadIfExpired(): Boolean {
        check(!closed) {
            "HeadsService is already closed"
        }

        val data = currentData ?: run {
            reloadBlocking()
            return true
        }

        if (!data.isExpired()) {
            return false
        }

        reloadBlocking()
        return true
    }

    fun data(): HeadsData {
        return requireData()
    }

    override fun close() {
        closed = true
    }

    private fun loadInitialBlocking() {
        val cachedData = await {
            repository.findCurrent()
        }

        if (cachedData != null && !cachedData.isExpired()) {
            currentData = cachedData
            return
        }

        reloadBlocking()
    }

    private fun reloadBlocking() {
        if (!reloadInProgress.compareAndSet(false, true)) {
            return
        }

        try {
            val loadedData = loadFromApiBlocking()

            await {
                repository.saveCurrent(loadedData)
            }

            currentData = loadedData
        } finally {
            reloadInProgress.set(false)
        }
    }

    private fun loadFromApiBlocking(): HeadsData {
        val categories = HeadsApi
            .build<CategoriesBuilder>()
            .build()
            .awaitBlocking()
            .data

        val collections = HeadsApi
            .build<CollectionsBuilder>()
            .demo()
            .build()
            .awaitBlocking()
            .data

        val requests = categories.associate { category ->
            category.id to HeadsApi
                .build<CustomHeadsBuilder>()
                .demo()
                .categoryId(category.id)
                .includeId()
                .includePublishedAt()
                .includeTags()
                .includeUuid()
                .includeValue()
                .build()
        }

        CompletableFuture
            .allOf(*requests.values.toTypedArray())
            .awaitBlocking()

        val headsByCategory = requests.mapValues { (_, request) ->
            request.join().data
        }

        return HeadsData(
            categories = categories,
            collections = collections,
            headsByCategory = headsByCategory,
            loadedAt = System.currentTimeMillis()
        )
    }

    private fun requireData(): HeadsData {
        return currentData
            ?: error(
                "HeadsService.start() must be called before accessing data"
            )
    }

    private fun <T> await(
        block: suspend () -> T
    ): T {
        return kotlinx.coroutines.runBlocking {
            block()
        }
    }

    private fun <T> CompletableFuture<T>.awaitBlocking(): T {
        return try {
            get()
        } catch (exception: InterruptedException) {
            Thread.currentThread().interrupt()

            throw IllegalStateException(
                "The API request was interrupted",
                exception
            )
        } catch (exception: ExecutionException) {
            throw exception.cause ?: exception
        }
    }

    private fun HeadsData.isExpired(): Boolean {
        return System.currentTimeMillis() - loadedAt >=
                RELOAD_INTERVAL_MILLIS
    }
}