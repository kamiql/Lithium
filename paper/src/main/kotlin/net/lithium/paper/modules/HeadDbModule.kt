package net.lithium.paper.modules

import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import net.lithium.common.LithiumApplication
import net.lithium.common.lib.database.sources.SQLiteDataSource
import net.lithium.paper.Config
import net.lithium.paper.headdb.Heads
import net.lithium.paper.headdb.model.HeadCache
import net.lithium.paper.headdb.repo.HeadCacheRepository
import net.lithium.paper.headdb.service.HeadDbService
import org.koin.dsl.module

val HeadDbModule = module {
    single<Heads> {
        get<Config>().heads
    }

    single<HeadCacheRepository> {
        HeadCacheRepository(
            source = SQLiteDataSource(
                id = "heads",
                keySerializer = String.serializer(),
                valueSerializer = HeadCache.serializer()
            )
        )
    }

    single<HeadDbService> {
        HeadDbService(
            application = get<LithiumApplication>(),
            config = get<Heads>(),
            json = get<Json>(),
            repository = get<HeadCacheRepository>()
        )
    }
}