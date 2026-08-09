package net.lithium.paper.heads.gui

import dev.kamiql.model.Category
import net.lithium.paper.heads.HeadsService
import net.lithium.paper.heads.heads
import net.lithium.paper.heads.toItem
import net.lithium.paper.lib.gui.GUI
import net.lithium.paper.lib.gui.gui
import net.lithium.paper.lib.gui.panes.paginationPane
import net.lithium.paper.lib.item.ib
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

fun HeadsOverviewGUI(): GUI = gui("Heads", 27) {
    setPane(
        paginationPane(3, 9, HeadsService.categories, { category ->
            ib(category.heads().firstOrNull()?.toItem() ?: ItemStack(Material.BOOK)) {
                displayName("<green>${category.name}")
                setLore(
                    "Heads: ${category.heads().size}",
                )
            }
        }, onClick = { category, event ->
            HeadsCategoryGUI(category).show(event.player)
        }), 0, 0
    )
}

fun HeadsCategoryGUI(category: Category): GUI = gui(category.name, 54) {
    setPane(
        paginationPane(
            6, 9, category.heads(), { head ->
                ib(head.id) {
                    displayName(head.name)
                    setLore(
                        "<gray>ID: ${head.id}",
                    )
                }
            }, onClick = { head, event ->
                event.player.inventory.addItem(head.toItem())
            }
        ) {
            search { query, value ->
                value.name.contains(query, true) || value.id.toString().contains(query, true)
            }
        }, 0, 0
    )

    back(53) { event ->
        HeadsOverviewGUI().show(event.player)
    }
}

fun HeadsBrowseGUI(query: String = ""): GUI = gui("Heads", 54) {
    setPane(
        paginationPane(
            6, 9, HeadsService.allHeads, { head ->
                ib(head.toItem()) {
                    displayName(head.name)
                    setLore(
                        "<gray>ID: ${head.id}",
                    )
                }
            }, onClick = { head, event ->
                event.player.inventory.addItem(head.toItem())
            }
        ) {
            search(query) { query, value ->
                value.name.contains(query, true) || value.id.toString().contains(query, true)
            }
        }, 0, 0
    )
}