package net.lithium.paper.lib.dialog

import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.action.DialogAction
import net.kyori.adventure.dialog.DialogLike
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback
import net.lithium.paper.lib.LithiumDsl
import io.papermc.paper.dialog.Dialog as PaperDialog

@LithiumDsl
class DialogScope internal constructor(
    private val title: Component,
) {
    private val inputs = mutableListOf<DialogInputSpec<*>>()

    private var type: DialogTypeSpec =
        DialogTypes.submit(Component.text("Submit"))

    private var callback: (Map<String, *>) -> Unit = {}

    fun type(type: DialogTypeSpec) {
        this.type = type
    }

    fun input(input: DialogInputSpec<*>) {
        require(input.id.isNotBlank()) {
            "Dialog input id must not be blank"
        }

        require(inputs.none { it.id == input.id }) {
            "A dialog input with the id '${input.id}' already exists"
        }

        inputs += input
    }

    fun callback(callback: (Map<String, *>) -> Unit) {
        this.callback = callback
    }

    internal fun build(): DialogLike {
        val action = DialogAction.customClick(
            { response, _ ->
                callback(
                    inputs.associate { input ->
                        input.id to input.read(response)
                    },
                )
            },
            ClickCallback.Options.builder().build(),
        )

        return PaperDialog.create { builder ->
            builder
                .empty()
                .base(
                    DialogBase.builder(title)
                        .inputs(
                            inputs.map { it.build() },
                        )
                        .build(),
                )
                .type(
                    type.build(action),
                )
        }
    }
}