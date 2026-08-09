package net.lithium.common.lib.database

import org.koin.core.component.KoinComponent

abstract class DataSource<K : Any, V : Any> : KoinComponent {

    open fun find(key: K): V? {
        return load(key)
    }

    open fun findAll(): Map<K, V> {
        return loadAll()
    }

    open fun save(key: K, value: V) {
        saveInternal(key, value)
    }

    open fun delete(key: K) {
        deleteInternal(key)
    }

    open fun findPaginated(limit: Int, page: Int = 0): Map<K, V> =
        findAll().entries
            .drop((page -1) * limit)
            .take(limit)
            .associate { it.key to it.value }

    protected abstract fun load(key: K): V?

    protected abstract fun loadAll(): Map<K, V>

    protected abstract fun saveInternal(key: K, value: V)

    protected abstract fun deleteInternal(key: K)
}