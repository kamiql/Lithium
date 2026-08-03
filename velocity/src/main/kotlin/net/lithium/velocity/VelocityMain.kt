package net.lithium.velocity

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import net.lithium.common.ApplicationMeta
import net.lithium.common.LithiumApplication
import net.lithium.common.LithiumBootstrap
import net.lithium.velocity.modules.LampModule
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module
import revxrsal.commands.Lamp
import revxrsal.commands.velocity.VelocityVisitors.brigadier
import revxrsal.commands.velocity.actor.VelocityCommandActor
import java.io.File
import java.util.logging.Logger

@Plugin(
    id = "lithium",
    name = "Lithium",
    version = "1.0.0",
    authors = ["kamiql"]
)
class VelocityMain @Inject constructor(
    server: ProxyServer,
    plugin: PluginContainer,
    logger: Logger,
    @DataDirectory dataFolder: File,
) : LithiumApplication {
    override val meta: ApplicationMeta = ApplicationMeta(
        plugin.description.version.get(),
        plugin.description.name.get(),
        plugin.description.authors
    )

    override val modules: List<Module> = listOf(
        module {
            single { server }
            single { plugin }
        },
        LampModule
    )

    override val applicationLogger: Logger = logger
    override val applicationDataFolder: File = dataFolder

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        LithiumBootstrap.bootstrap<VelocityMain, Config>(this)

        val lamp: Lamp<VelocityCommandActor> = get()
        lamp.register(

        )
        lamp.accept(brigadier(get()))
    }
}