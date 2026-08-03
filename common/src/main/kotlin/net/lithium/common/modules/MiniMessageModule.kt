package net.lithium.common.modules

import net.kyori.adventure.text.minimessage.MiniMessage
import org.koin.dsl.module

val MiniMessageModule = module {
    single { MiniMessage.miniMessage() }
}