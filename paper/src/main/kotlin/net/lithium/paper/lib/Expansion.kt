package net.lithium.paper.lib

import kotlin.time.Duration

fun Duration.toBukkitTicks(): Long {
    return this.inWholeMilliseconds / 50
}