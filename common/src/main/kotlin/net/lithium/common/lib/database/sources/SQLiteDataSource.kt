package net.lithium.common.lib.database.sources

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import net.lithium.common.LithiumApplication
import org.koin.core.component.inject
import java.sql.Connection
import java.sql.DriverManager

class SQLiteDataSource<K : Any, V : Any>(
    id: String,
    private val keySerializer: KSerializer<K>,
    private val valueSerializer: KSerializer<V>
) : CachedDataSource<K, V>() {

    private val app: LithiumApplication by inject()
    private val json: Json by inject()

    private val file = app.applicationDataFolder.resolve("storage/$id.db")

    private val connection: Connection

    init {
        file.parentFile.mkdirs()

        connection = DriverManager.getConnection("jdbc:sqlite:${file.absolutePath}")

        connection.createStatement().use { statement ->
            statement.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS entries (
                    key TEXT PRIMARY KEY NOT NULL,
                    value TEXT NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    override fun load(key: K): V? {
        val serializedKey = json.encodeToString(keySerializer, key)

        connection.prepareStatement(
            """
            SELECT value FROM entries
            WHERE key = ?
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, serializedKey)

            return statement.executeQuery().use { resultSet ->
                if (!resultSet.next()) {
                    null
                } else {
                    val serializedValue = resultSet.getString("value")
                    json.decodeFromString(valueSerializer, serializedValue)
                }
            }
        }
    }

    override fun loadAll(): Map<K, V> {
        val result = mutableMapOf<K, V>()

        connection.prepareStatement(
            """
            SELECT key, value FROM entries
            """.trimIndent()
        ).use { statement ->
            statement.executeQuery().use { resultSet ->
                while (resultSet.next()) {
                    val serializedKey = resultSet.getString("key")
                    val serializedValue = resultSet.getString("value")

                    val key = json.decodeFromString(keySerializer, serializedKey)
                    val value = json.decodeFromString(valueSerializer, serializedValue)

                    result[key] = value
                }
            }
        }

        return result
    }

    override fun saveInternal(key: K, value: V) {
        val serializedKey = json.encodeToString(keySerializer, key)
        val serializedValue = json.encodeToString(valueSerializer, value)

        connection.prepareStatement(
            """
            INSERT INTO entries(key, value)
            VALUES (?, ?)
            ON CONFLICT(key) DO UPDATE SET value = excluded.value
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, serializedKey)
            statement.setString(2, serializedValue)
            statement.executeUpdate()
        }
    }

    override fun deleteInternal(key: K) {
        val serializedKey = json.encodeToString(keySerializer, key)

        connection.prepareStatement(
            """
            DELETE FROM entries
            WHERE key = ?
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, serializedKey)
            statement.executeUpdate()
        }
    }
}