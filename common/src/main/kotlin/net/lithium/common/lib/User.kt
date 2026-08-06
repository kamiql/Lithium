package net.lithium.common.lib

import net.kyori.adventure.audience.Audience
import java.util.*

abstract class User<P: Audience> {
    abstract val player: P

    abstract fun locale(): Locale
}