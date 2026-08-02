package net.lithium.paper

import net.lithium.common.ApplicationMeta
import net.lithium.common.LithiumApplication
import net.lithium.common.LithiumBootstrap
import net.lithium.common.lib.config.ConfigService
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.inject
import org.koin.core.module.Module
import java.io.File
import java.util.logging.Logger

class PaperMain : JavaPlugin(), LithiumApplication {
    private val config: Config by inject()

    override val meta: ApplicationMeta = ApplicationMeta(
        pluginMeta.version,
        pluginMeta.name,
        pluginMeta.authors
    )

    override val modules: List<Module> = listOf(

    )

    override val applicationLogger: Logger = logger
    override val applicationDataFolder: File = dataFolder

    override fun onLoad() {
        LithiumBootstrap.bootstrap<PaperMain, Config>(this)
    }

    override fun onEnable() {

    }

    override fun onDisable() {

    }
}