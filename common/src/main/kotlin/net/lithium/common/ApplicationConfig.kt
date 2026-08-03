package net.lithium.common

import kotlinx.serialization.Serializable

interface ApplicationConfig {
    val prefix: String
    val database: Database

    @Serializable
    data class Database(
        val uri: String = "mongodb://localhost:27017",
    )
}