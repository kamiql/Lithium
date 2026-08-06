package net.lithium.paper.lib.gui.panes

import net.lithium.paper.lib.LithiumDsl
import net.lithium.paper.lib.gui.Pane

@LithiumDsl
class PatternPane(
    height: Int,
    width: Int,
    pattern: List<Int>,
) : Pane(
    height = height,
    width = width,
    targetSlots = pattern,
)

fun patternPane(
    height: Int,
    width: Int,
    vararg pattern: Int,
    configure: PatternPane.() -> Unit,
): PatternPane {
    return PatternPane(
        height = height,
        width = width,
        pattern = pattern.toList(),
    ).apply(configure)
}