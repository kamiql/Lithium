package net.lithium.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
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

    val applicationScope: CoroutineScope

    fun launchCoroutine(
        block: suspend CoroutineScope.() -> Unit
    ): Job = applicationScope.launch(block = block)

    fun shutdownCoroutines() {
        applicationScope.cancel()
    }
}