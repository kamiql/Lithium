package net.lithium.paper

import kotlinx.serialization.Serializable
import net.lithium.common.ApplicationConfig
import net.lithium.paper.headdb.Heads

@Serializable
data class Config(
    override val prefix: String = "<blue>Lithium",
    val heads: Heads = Heads(),
) : ApplicationConfig