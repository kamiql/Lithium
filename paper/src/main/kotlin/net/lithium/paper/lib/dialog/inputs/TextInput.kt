package net.lithium.paper.lib.dialog.inputs

import io.papermc.paper.dialog.DialogResponseView
import io.papermc.paper.registry.data.dialog.input.DialogInput
import net.kyori.adventure.text.Component
import net.lithium.paper.lib.dialog.DialogInputSpec

class TextInput(
    override val id: String,
    val label: Component,
    val initialValue: String? = null,
    val width: Int? = null,
    val maxLength: Int? = null,
) : DialogInputSpec<String> {

    override fun build(): DialogInput {
        val builder = DialogInput.text(
            id,
            label,
        )

        initialValue?.let(builder::initial)
        width?.let(builder::width)
        maxLength?.let(builder::maxLength)

        return builder.build()
    }

    override fun read(response: DialogResponseView): String {
        return response
            .getText(id)
            .orEmpty()
    }
}