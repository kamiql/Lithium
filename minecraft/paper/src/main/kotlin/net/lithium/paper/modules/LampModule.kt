package net.lithium.paper.modules

import net.lithium.paper.PaperMain
import org.koin.dsl.module
import revxrsal.commands.bukkit.BukkitLamp

val LampModule = module {
    single {
        BukkitLamp.builder(get<PaperMain>()).accept {

        }
    }
}