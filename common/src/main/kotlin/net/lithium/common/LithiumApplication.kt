package net.lithium.common

import net.lithium.common.lib.process.ProcessManager
import org.koin.core.component.KoinComponent
import org.koin.core.module.Module
import java.io.File
import java.util.logging.Logger

interface LithiumApplication : KoinComponent {
    val meta: ApplicationMeta
    val modules: List<Module>
    val applicationLogger: Logger
    val processManager: ProcessManager
    val applicationDataFolder: File
}