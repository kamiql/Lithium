package net.lithium.paper.headdb.repo

import net.lithium.common.lib.database.DataSource
import net.lithium.common.lib.database.Repository
import net.lithium.paper.headdb.model.HeadCache

class HeadCacheRepository(
    source: DataSource<String, HeadCache>
) : Repository<String, HeadCache>(source) {

    override fun version(): Int = 1

    suspend fun loadCache(): HeadCache? {
        return find(CACHE_KEY)
    }

    suspend fun saveCache(cache: HeadCache) {
        save(CACHE_KEY, cache)
    }

    companion object {
        private const val CACHE_KEY = "all-heads"
    }
}