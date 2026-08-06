package net.lithium.paper

import net.lithium.common.ApplicationMeta
import net.lithium.common.LithiumApplication
import net.lithium.common.LithiumBootstrap
import net.lithium.common.lib.process.ProcessManager
import net.lithium.paper.impl.process.BukkitProcessManager
import net.lithium.paper.listeners.GuiListener
import net.lithium.paper.modules.LampModule
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module
import revxrsal.commands.Lamp
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import java.io.File
import java.util.logging.Logger

class PaperMain : JavaPlugin(), LithiumApplication {
    override val meta: ApplicationMeta = ApplicationMeta(
        pluginMeta.version,
        pluginMeta.name,
        pluginMeta.authors
    )

    override val modules: List<Module> = listOf(
        module {
            single<JavaPlugin> { this@PaperMain }
        },
        LampModule,
    )

    override val applicationLogger: Logger = logger
    override val processManager: ProcessManager = BukkitProcessManager(this)
    override val applicationDataFolder: File = dataFolder

    override fun onLoad() {
        LithiumBootstrap.bootstrap<PaperMain, Config>(this)
    }

    override fun onEnable() {
        val lamp: Lamp<BukkitCommandActor> = get()
        lamp.register(

        )

        server.pluginManager.registerEvents(GuiListener, this)
    }

    override fun onDisable() {

    }
}

fun test(b: (Player) -> Unit) {}