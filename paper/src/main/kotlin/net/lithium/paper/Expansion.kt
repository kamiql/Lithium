package net.lithium.paper

import net.lithium.common.lib.User
import org.bukkit.entity.Player
import java.util.Locale

fun Player.user(): User<Player> = object : User<Player>() {
    override val player: Player = this@user
    override fun locale(): Locale = player.locale()
}