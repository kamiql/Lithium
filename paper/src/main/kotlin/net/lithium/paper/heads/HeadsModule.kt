package net.lithium.paper.heads

import com.destroystokyo.paper.profile.ProfileProperty
import dev.kamiql.model.Category
import dev.kamiql.model.Head
import kotlinx.serialization.builtins.serializer
import net.lithium.common.lib.database.sources.SQLiteDataSource
import net.lithium.paper.heads.repo.HeadsRepository
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.koin.dsl.module

val HeadsModule = module {
    single<HeadsRepository> {
        HeadsRepository(
            source = SQLiteDataSource(
                id = "minecraft-heads",
                keySerializer = String.serializer(),
                valueSerializer = HeadsData.serializer()
            )
        )
    }
}

fun Head.toItem(): ItemStack {
    val item = ItemStack(Material.PLAYER_HEAD)
    this.textureValue ?: return item
    val meta = item.itemMeta as SkullMeta
    meta.playerProfile = Bukkit.getServer().createProfile(this.id.toString()).apply {
        setProperty(ProfileProperty(
            "textures", this@toItem.textureValue!!
        ))
    }
    item.itemMeta = meta
    return item
}

fun Category.heads(): List<Head> {
    return HeadsService.heads[this.id] ?: emptyList()
}