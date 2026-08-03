package net.lithium.common.lib.i18n

import kotlinx.serialization.Serializable

@Serializable
data class TranslationConfig(
    val translations: Map<String, List<String>> = mapOf()
)