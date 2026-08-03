package net.lithium.velocity.modules

import net.lithium.velocity.VelocityMain
import org.koin.dsl.module
import revxrsal.commands.velocity.VelocityLamp

val LampModule = module {
    single {
        VelocityLamp.builder(get<VelocityMain>(), get()).accept {

        }.build()
    }
}