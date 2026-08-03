package net.lithium.common.lib.database

import org.koin.core.component.KoinComponent

abstract class DataSource<K : Any, V : Any> : KoinComponent {

    open suspend fun find(key: K): V? {
        return load(key)
    }

    open suspend fun findAll(): Map<K, V> {
        return loadAll()
    }

    open suspend fun save(key: K, value: V) {
        saveInternal(key, value)
    }

    open suspend fun delete(key: K) {
        deleteInternal(key)
    }

    protected abstract suspend fun load(key: K): V?

    protected abstract suspend fun loadAll(): Map<K, V>

    protected abstract suspend fun saveInternal(key: K, value: V)

    protected abstract suspend fun deleteInternal(key: K)
}