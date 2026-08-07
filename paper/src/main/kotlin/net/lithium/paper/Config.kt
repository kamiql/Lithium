package net.lithium.paper

import kotlinx.serialization.Serializable
import net.lithium.common.ApplicationConfig

@Serializable
data class Config(
    override val prefix: String = "<blue>Lithium",
    val heads: HeadsApi = HeadsApi(),
) : ApplicationConfig {

    @Serializable
    data class HeadsApi(
        val uuid: String = "your-id",
        val token: String = "your-token",
    )
}