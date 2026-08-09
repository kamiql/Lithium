package net.lithium.paper.lib.dialog

import io.papermc.paper.dialog.DialogResponseView
import io.papermc.paper.registry.data.dialog.input.DialogInput

interface DialogInputSpec<T> {

    val id: String

    fun build(): DialogInput

    fun read(response: DialogResponseView): T
}