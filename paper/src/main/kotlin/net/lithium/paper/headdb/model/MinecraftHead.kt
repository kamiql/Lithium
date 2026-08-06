package net.lithium.paper.headdb.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

@Serializable
data class MinecraftHead(
    /**
     * Stabiler Cache-Key.
     *
     * Normalerweise ist dies die API-UUID.
     */
    val cacheKey: String,

    /**
     * Unveränderte Head-Daten aus der API.
     */
    val data: JsonObject,

    /**
     * Kategorien, in denen dieser Head vorkommt.
     */
    val categories: List<String> = emptyList()
) {
    val uuid: String?
        get() = data.stringValue("uuid")
            ?: data.stringValue("id")
            ?: data.stringValue("textureUuid")

    val name: String
        get() = data.stringValue("name")
            ?: data.stringValue("displayName")
            ?: "Unnamed Head"

    val value: String?
        get() = data.stringValue("value")
            ?: data.stringValue("texture")
            ?: data.stringValue("textureValue")
            ?: data.stringValue("base64")

    val tags: List<String>
        get() = data.stringList("tags")
}

internal fun JsonObject.stringValue(key: String): String? {
    return (this[key] as? JsonPrimitive)
        ?.contentOrNull
        ?.takeIf { it.isNotBlank() }
}

internal fun JsonObject.stringList(key: String): List<String> {
    return when (val element = this[key]) {
        is JsonArray -> {
            element
                .mapNotNull { (it as? JsonPrimitive)?.contentOrNull }
                .map(String::trim)
                .filter(String::isNotBlank)
        }

        is JsonPrimitive -> {
            element.contentOrNull
                ?.split(",")
                ?.map(String::trim)
                ?.filter(String::isNotBlank)
                ?: emptyList()
        }

        else -> {
            emptyList()
        }
    }
}