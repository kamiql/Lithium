package net.lithium.paper.heads.commands

import net.lithium.paper.heads.HeadsService
import net.lithium.paper.heads.gui.HeadsBrowseGUI
import net.lithium.paper.heads.gui.HeadsOverviewGUI
import net.lithium.paper.heads.toItem
import org.bukkit.entity.Player
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.CommandPlaceholder
import revxrsal.commands.annotation.Optional
import revxrsal.commands.annotation.Subcommand

@Command("heads", "hdb")
object HeadsCommands {
    @CommandPlaceholder
    fun default(sender: Player) {
        HeadsOverviewGUI().show(sender)
    }

    @Subcommand("find")
    fun find(sender: Player, id: String) {
        val head = HeadsService.allHeads.find { it.id == id.toInt() } ?: run {
            return
        }
        sender.inventory.addItem(head.toItem())
    }

    @Subcommand("search")
    fun search(sender: Player, @Optional query: String = "") {
        HeadsBrowseGUI(query).show(sender)
    }
}