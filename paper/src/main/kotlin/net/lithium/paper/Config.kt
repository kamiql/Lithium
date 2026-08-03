package net.lithium.paper

import kotlinx.serialization.Serializable
import net.lithium.common.ApplicationConfig

@Serializable
data class Config(
    override val prefix: String = "<blue>Lithium",
    override val database: ApplicationConfig.Database = ApplicationConfig.Database(),
) : ApplicationConfig