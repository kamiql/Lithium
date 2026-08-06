package net.lithium.paper.lib.gui

import net.lithium.paper.lib.LithiumDsl
import org.bukkit.event.inventory.InventoryType

@LithiumDsl
enum class GuiType(val type: InventoryType, val width: Int, val rectangular: Boolean = false) {
    CHEST(InventoryType.CHEST, 9, true)
}