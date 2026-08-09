package net.lithium.paper.lib.dialog

import net.kyori.adventure.dialog.DialogLike
import net.kyori.adventure.text.Component
import net.lithium.paper.lib.LithiumDsl

/**
 * Erzeugt einen nativen Paper-Dialog.
 */
fun Dialog(
    title: Component,
    block: DialogScope.() -> Unit,
): DialogLike {
    return DialogScope(title)
        .apply(block)
        .build()
}

/**
 * Convenience-Overload für einfache Strings.
 */
fun Dialog(
    title: String,
    block: DialogScope.() -> Unit,
): DialogLike {
    return Dialog(
        title = Component.text(title),
        block = block,
    )
}