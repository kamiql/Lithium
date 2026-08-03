package net.lithium.common.lib.database.sources

import net.lithium.common.lib.database.DataSource

class MemoryDataSource<K : Any, V : Any> : DataSource<K, V>() {

    val data = mutableMapOf<K, V>()

    override suspend fun load(key: K): V? {
        return data[key]
    }

    override suspend fun loadAll(): Map<K, V> {
        return data.toMap()
    }

    override suspend fun saveInternal(key: K, value: V) {
        data[key] = value
    }

    override suspend fun deleteInternal(key: K) {
        data.remove(key)
    }
}