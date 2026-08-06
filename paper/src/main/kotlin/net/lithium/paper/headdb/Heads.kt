package net.lithium.paper.headdb

import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

@Serializable
data class Heads(
    val apiToken: String = "",
    val baseUrl: String = "https://minecraft-heads.com/api/v2",
    val categoryPath: String = "/categories/{category}",
    val categories: List<String> = DEFAULT_CATEGORIES,
    val includeTags: Boolean = true,
    val cacheTtl: Duration = 24.hours,
    val requestTimeout: Duration = 30.seconds,
    val maxParallelRequests: Int = categories.size,
    val maxRetries: Int = 2
) {
    init {
        require(apiToken.isNotBlank()) {
            "Minecraft-Heads API token must not be blank."
        }

        require(baseUrl.isNotBlank()) {
            "Minecraft-Heads API base URL must not be blank."
        }

        require("{category}" in categoryPath) {
            "categoryPath must contain {category}."
        }

        require(categories.isNotEmpty()) {
            "At least one Minecraft-Heads category is required."
        }

        require(maxParallelRequests > 0) {
            "maxParallelRequests must be greater than zero."
        }

        require(maxRetries >= 0) {
            "maxRetries must not be negative."
        }
    }

    companion object {
        val DEFAULT_CATEGORIES = listOf(
            "alphabet",
            "animals",
            "blocks",
            "decoration",
            "food-drinks",
            "humanoid",
            "humans",
            "miscellaneous",
            "monsters",
            "plants"
        )
    }
}