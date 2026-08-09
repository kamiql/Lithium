package net.lithium.paper.heads

import dev.kamiql.model.Category
import dev.kamiql.model.Head
import dev.kamiql.model.HeadCollection
import kotlinx.serialization.Serializable

@Serializable
data class HeadsData(
    val categories: List<Category>,
    val collections: List<HeadCollection>,
    val headsByCategory: Map<Int, List<Head>>,
    val loadedAt: Long
) {
    val allHeads: List<Head> get() = headsByCategory.values
        .flatten()
        .distinctBy { head ->
            head.id ?: "${head.categoryId}:${head.name}"
        }
}