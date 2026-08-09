package net.lithium.paper.lib.dialog

import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.type.DialogType

fun interface DialogTypeSpec {

    fun build(action: DialogAction): DialogType
}