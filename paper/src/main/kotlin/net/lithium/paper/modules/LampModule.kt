package net.lithium.paper.modules

import net.lithium.paper.PaperMain
import org.koin.dsl.module
import revxrsal.commands.Lamp
import revxrsal.commands.bukkit.BukkitLamp
import revxrsal.commands.bukkit.actor.BukkitCommandActor

val LampModule = module {
    single {
        BukkitLamp.builder(get<PaperMain>()).accept {

        }.build()
    }
}