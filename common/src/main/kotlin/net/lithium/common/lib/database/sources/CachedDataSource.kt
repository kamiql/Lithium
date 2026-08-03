package net.lithium.common.lib.database.sources

import net.lithium.common.lib.database.DataSource

abstract class CachedDataSource<K : Any, V : Any> : DataSource<K, V>() {

    protected val cache = mutableMapOf<K, V>()

    private var fullyLoaded = false

    override suspend fun find(key: K): V? {
        cache[key]?.let { return it }

        val value = load(key) ?: return null
        cache[key] = value

        return value
    }

    override suspend fun findAll(): Map<K, V> {
        if (!fullyLoaded) {
            cache.clear()
            cache.putAll(loadAll())
            fullyLoaded = true
        }

        return cache.toMap()
    }

    override suspend fun save(key: K, value: V) {
        saveInternal(key, value)

        cache[key] = value
    }

    override suspend fun delete(key: K) {
        deleteInternal(key)

        cache.remove(key)
    }

    suspend fun reload() {
        cache.clear()
        cache.putAll(loadAll())
        fullyLoaded = true
    }

    fun clearCache() {
        cache.clear()
        fullyLoaded = false
    }

    fun invalidate(key: K) {
        cache.remove(key)
        fullyLoaded = false
    }
}