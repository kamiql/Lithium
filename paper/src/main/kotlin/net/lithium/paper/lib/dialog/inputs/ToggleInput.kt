package net.lithium.paper.lib.dialog.inputs

import io.papermc.paper.dialog.DialogResponseView
import io.papermc.paper.registry.data.dialog.input.DialogInput
import net.kyori.adventure.text.Component
import net.lithium.paper.lib.dialog.DialogInputSpec

class ToggleInput(
    override val id: String,
    val label: Component,
    val initialValue: Boolean = false,
) : DialogInputSpec<Boolean> {

    override fun build(): DialogInput {
        return DialogInput
            .bool(id, label)
            .initial(initialValue)
            .build()
    }

    override fun read(response: DialogResponseView): Boolean {
        return response.getBoolean(id) ?: initialValue
    }
}