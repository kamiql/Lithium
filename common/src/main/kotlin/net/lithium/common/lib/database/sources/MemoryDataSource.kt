package net.lithium.common.lib.database.sources

import net.lithium.common.lib.database.DataSource

class MemoryDataSource<K : Any, V : Any> : DataSource<K, V>() {

    val data = mutableMapOf<K, V>()

    override fun load(key: K): V? {
        return data[key]
    }

    override fun loadAll(): Map<K, V> {
        return data.toMap()
    }

    override fun saveInternal(key: K, value: V) {
        data[key] = value
    }

    override fun deleteInternal(key: K) {
        data.remove(key)
    }
}