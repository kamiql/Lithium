package net.lithium.common

import org.koin.core.module.Module
import java.util.logging.Logger

interface LithiumApplication {
    val meta: ApplicationMeta
    val modules: List<Module>

    fun logger(): Logger
}