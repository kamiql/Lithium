package net.lithium.paper.heads.repo

import net.lithium.common.lib.database.DataSource
import net.lithium.common.lib.database.Repository
import net.lithium.paper.heads.HeadsData

class HeadsRepository(
    source: DataSource<String, HeadsData>
) : Repository<String, HeadsData>(source) {

    override fun version(): Int {
        return 1
    }

    suspend fun findCurrent(): HeadsData? {
        return find(CACHE_KEY)
    }

    suspend fun saveCurrent(data: HeadsData) {
        save(CACHE_KEY, data)
    }

    suspend fun deleteCurrent() {
        delete(CACHE_KEY)
    }

    companion object {
        private const val CACHE_KEY = "current"
    }
}