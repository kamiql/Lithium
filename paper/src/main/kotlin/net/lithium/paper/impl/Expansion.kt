package net.lithium.paper.impl

import kotlin.time.Duration

fun Duration.toBukkitTicks(): Long {
    return this.inWholeMilliseconds / 50
}