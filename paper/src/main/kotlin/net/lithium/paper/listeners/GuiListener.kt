package net.lithium.paper.listeners

import net.lithium.paper.lib.gui.GUI
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

object GuiListener : Listener {
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        (event.inventory.holder as? GUI)?.let {
            if (event.clickedInventory != event.view.topInventory) return@let
            if (it.cancel) event.isCancelled = true

            it.items[event.slot]?.callback(GUI.GUIEvent(
                event,
                event.whoClicked as Player,
                it
            ))
        }
    }
}