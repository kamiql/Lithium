package net.lithium.common.lib.text

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

val mm = MiniMessage.miniMessage()

val cleanCharacters = mapOf(
    "a" to 'ᴀ',
    "b" to 'ʙ',
    "c" to 'ᴄ',
    "d" to 'ᴅ',
    "e" to 'ᴇ',
    "f" to 'ꜰ',
    "g" to 'ɢ',
    "h" to 'ʜ',
    "i" to 'ɪ',
    "j" to 'ᴊ',
    "k" to 'ᴋ',
    "l" to 'ʟ',
    "m" to 'ᴍ',
    "n" to 'ɴ',
    "o" to 'ᴏ',
    "p" to 'ᴘ',
    "q" to 'q',
    "r" to 'ʀ',
    "s" to 'ꜱ',
    "t" to 'ᴛ',
    "u" to 'ᴜ',
    "v" to 'v',
    "w" to 'ᴡ',
    "x" to 'x',
    "y" to 'ʏ',
    "z" to 'ᴢ'
)

fun String.component(): Component = Component.text(this)

fun String.c(): Component {
    return mm.deserialize(this)
}

fun String.cc(): String = """(<[^>]+>)|([^<]+)""".toRegex().replace(this) { match ->
    match.groups[2]?.value
        ?.map { cleanCharacters[it.lowercaseChar().toString()] ?: it }
        ?.joinToString("")
        ?: match.value
}

fun String.ccc(): Component {
    return this.cc().c()
}