package net.lithium.common.lib

import net.kyori.adventure.audience.Audience
import java.util.Locale

abstract class User<P: Audience> {
    abstract val player: P

    abstract fun locale(): Locale
}