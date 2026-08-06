package net.lithium.paper.lib.gui.panes

import net.lithium.paper.lib.LithiumDsl
import net.lithium.paper.lib.gui.ClickCallback
import net.lithium.paper.lib.gui.Pane
import net.lithium.paper.lib.item.IB

@LithiumDsl
open class GridPane(
    height: Int,
    width: Int,
    targetSlots: List<Int> = (0 until height * width).toList(),
) : Pane(
    height = height,
    width = width,
    targetSlots = targetSlots,
) {
    fun item(
        x: Int,
        y: Int,
        ib: IB,
        callback: ClickCallback = {},
    ) {
        require(x in 0 until width) {
            "x must be inside 0 until $width"
        }

        require(y in 0 until height) {
            "y must be inside 0 until $height"
        }

        val localSlot = y * width + x
        val index = targetSlots.indexOf(localSlot)

        require(index >= 0) {
            "Position ($x, $y) is not part of this Pane pattern"
        }

        item(
            index = index,
            ib = ib,
            callback = callback,
        )
    }
}

fun gridPane(
    height: Int,
    width: Int,
    configure: GridPane.() -> Unit,
): GridPane {
    return GridPane(
        height = height,
        width = width,
    ).apply(configure)
}