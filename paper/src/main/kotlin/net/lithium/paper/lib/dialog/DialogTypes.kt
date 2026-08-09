package net.lithium.paper.lib.dialog

import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.kyori.adventure.text.Component

object DialogTypes {

    fun submit(
        label: Component,
    ): DialogTypeSpec {
        return DialogTypeSpec { action ->
            DialogType.multiAction(
                listOf(
                    ActionButton.builder(label)
                        .action(action)
                        .build(),
                ),
            ).build()
        }
    }

    fun multiAction(
        vararg labels: Component,
    ): DialogTypeSpec {
        return DialogTypeSpec { action ->
            DialogType.multiAction(
                labels.map { label ->
                    ActionButton.builder(label)
                        .action(action)
                        .build()
                },
            ).build()
        }
    }

    fun confirm(): DialogTypeSpec {
        return submit(Component.text("Confirm"))
    }
}