package net.lithium.paper.headdb.model

import kotlinx.serialization.Serializable

@Serializable
data class HeadCache(
    val fetchedAtEpochMillis: Long,
    val heads: Map<String, MinecraftHead>,
    val schemaVersion: Int = 1
)