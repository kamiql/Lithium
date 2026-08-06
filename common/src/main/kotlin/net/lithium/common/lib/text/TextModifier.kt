package net.lithium.common.lib.text

import net.kyori.adventure.text.Component

enum class TextModifier(val apply: String.() -> Component) {
    C({
        this.c()
    }),
    CC({
        this.cc().component()
    }),
    CCC({
        this.ccc()
    })
}