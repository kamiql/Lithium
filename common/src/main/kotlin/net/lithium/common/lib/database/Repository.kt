package net.lithium.common.lib.database

abstract class Repository<K : Any, V : Any>(
    protected val source: DataSource<K, V>
) {
    abstract fun version(): Int

    open suspend fun find(key: K): V? =
        source.find(key)

    open suspend fun findAll(): Map<K, V> =
        source.findAll()

    open suspend fun save(key: K, value: V) =
        source.save(key, value)

    open suspend fun delete(key: K) =
        source.delete(key)

    open suspend fun exists(key: K): Boolean =
        source.find(key) != null
}