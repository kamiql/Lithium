package net.lithium.paper.lib.gui

import net.lithium.paper.lib.LithiumDsl
import net.lithium.paper.lib.item.IB

typealias ClickCallback =
            (GUI.GUIEvent<org.bukkit.event.inventory.InventoryClickEvent>) -> Unit

@LithiumDsl
abstract class Pane(
    val height: Int,
    val width: Int,
    protected val targetSlots: List<Int>,
) {
    init {
        require(height > 0) {
            "Pane height must be greater than zero"
        }

        require(width > 0) {
            "Pane width must be greater than zero"
        }

        require(targetSlots.distinct().size == targetSlots.size) {
            "A Pane must not contain duplicate target slots"
        }

        require(targetSlots.all { it in 0 until width * height }) {
            "All target slots must be inside the Pane bounds"
        }
    }

    /**
     * Anzahl der logischen Slots.
     *
     * Der erste logische Index ist immer 0.
     */
    val slots: Int
        get() = targetSlots.size

    private val paneItems = linkedMapOf<Int, GUI.Item>()

    fun item(
        index: Int,
        ib: IB,
        callback: ClickCallback = {},
    ) {
        require(index in targetSlots.indices) {
            "Index $index is outside of the Pane range 0 until $slots"
        }

        itemAtLocalSlot(
            localSlot = targetSlots[index],
            ib = ib,
            callback = callback,
        )
    }

    /**
     * Platziert ein Item direkt auf einem lokalen Slot des Panes.
     *
     * Diese Methode ist für spezielle Pane-Implementierungen gedacht,
     * beispielsweise für Navigationsbuttons.
     */
    protected fun itemAtLocalSlot(
        localSlot: Int,
        ib: IB,
        callback: ClickCallback = {},
    ) {
        require(localSlot in 0 until width * height) {
            "Local slot $localSlot is outside of the Pane bounds"
        }

        paneItems[localSlot] = GUI.Item(
            ib = ib,
            callback = callback,
        )
    }

    protected fun clearItems() {
        paneItems.clear()
    }

    internal fun items(): Map<Int, GUI.Item> {
        return paneItems
    }
}