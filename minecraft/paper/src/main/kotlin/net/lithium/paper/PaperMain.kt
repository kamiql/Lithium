package net.lithium.paper

import net.lithium.common.ApplicationMeta
import net.lithium.common.LithiumApplication
import net.lithium.common.LithiumBootstrap
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.module.Module
import java.util.logging.Logger

class PaperMain : JavaPlugin(), LithiumApplication {
    override val meta: ApplicationMeta = ApplicationMeta(
        pluginMeta.version,
        pluginMeta.name,
        pluginMeta.authors
    )

    override val modules: List<Module> = listOf(

    )

    override fun logger(): Logger = logger

    override fun onLoad() {
        LithiumBootstrap.bootstrap(this)
    }

    override fun onEnable() {

    }

    override fun onDisable() {

    }
}