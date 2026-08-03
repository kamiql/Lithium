package net.lithium.velocity

import com.velocitypowered.api.proxy.Player
import net.lithium.common.lib.User
import java.util.Locale

fun Player.user(): User<Player> = object : User<Player>() {
    override val player: Player = this@user
    override fun locale(): Locale = player.effectiveLocale ?: Locale.ENGLISH
}