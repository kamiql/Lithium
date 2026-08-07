package net.lithium.paper.heads.commands

import net.lithium.paper.heads.gui.HeadsOverviewGUI
import org.bukkit.entity.Player
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.CommandPlaceholder

@Command("heads", "hdb")
object HeadsCommands {
    @CommandPlaceholder
    fun default(sender: Player) {
        HeadsOverviewGUI().show(sender)
    }
}