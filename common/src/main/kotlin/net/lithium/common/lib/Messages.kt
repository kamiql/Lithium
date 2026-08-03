package net.lithium.common.lib

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.MiniMessage
import net.lithium.common.ApplicationConfig
import net.lithium.common.lib.i18n.I18n
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object Messages : KoinComponent {
    val mm: MiniMessage by inject()
    val config: ApplicationConfig by inject()
}

fun <P: Audience> User<P>.sendMessage(key: String, placeholders: Map<String, Any>) {
    val text = I18n.translation(this.locale(), key, placeholders)

    text.forEach {
        this.player.sendMessage(
            Messages.mm.deserialize("${Messages.config.prefix} $it")
        )
    }
}
