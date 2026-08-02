package net.lithium.common.lib.database

import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class DataSource<K : Any, V : Any> : KoinComponent {
    protected val json: Json by inject()

    protected val cache = mutableMapOf<K, V>()

    suspend fun find(key: K): V? {
        cache[key]?.let { return it }

        val value = load(key) ?: return null
        cache[key] = value
        return value
    }

    suspend fun findAll(): Map<K, V> {
        if (cache.isEmpty()) {
            cache.putAll(loadAll())
        }

        return cache
    }

    suspend fun save(key: K, value: V) {
        cache[key] = value
        saveInternal(key, value)
    }

    suspend fun delete(key: K) {
        cache.remove(key)
        deleteInternal(key)
    }

    suspend fun reload() {
        cache.clear()
        cache.putAll(loadAll())
    }

    protected abstract suspend fun load(key: K): V?

    protected abstract suspend fun loadAll(): Map<K, V>

    protected abstract suspend fun saveInternal(key: K, value: V)

    protected abstract suspend fun deleteInternal(key: K)
}