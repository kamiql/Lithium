package net.lithium.paper.listeners

import net.lithium.paper.lib.gui.GUI
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryEvent

object GuiListener : Listener {
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        (event.inventory.holder as? GUI)?.let {

        }

        event.gui {
            if (event.clickedInventory != event.view.topInventory) return@gui
            if (this.cancelClick) event.isCancelled = true

            this.items[event.slot]?.callback(GUI.GUIEvent(
                event,
                event.whoClicked as Player,
                this
            ))
        }
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        event.gui {
            if (this.cancelClose) event.player.openInventory(event.inventory)

            this.closeCallback?.invoke(GUI.GUIEvent(
                event,
                event.player as Player,
                this
            ))
        }
    }

    fun InventoryEvent.gui(callback: GUI.() -> Unit) {
        (this.inventory.holder as? GUI)?.callback()
    }
}