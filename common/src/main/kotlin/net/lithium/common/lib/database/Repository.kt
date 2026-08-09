package net.lithium.common.lib.database

abstract class Repository<K : Any, V : Any>(
    protected val source: DataSource<K, V>
) {
    abstract fun version(): Int

    open fun find(key: K): V? =
        source.find(key)

    open fun findAll(): Map<K, V> =
        source.findAll()

    open fun save(key: K, value: V) =
        source.save(key, value)

    open fun delete(key: K) =
        source.delete(key)

    open fun exists(key: K): Boolean =
        source.find(key) != null

    fun findPaginated(limit: Int, page: Int = 0) =
        source.findPaginated(limit, page)
}