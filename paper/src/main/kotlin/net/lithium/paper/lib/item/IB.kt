package net.lithium.paper.lib.item

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.lithium.common.lib.text.TextModifier
import net.lithium.paper.lib.LithiumDsl
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

@LithiumDsl
class IB private constructor(private val item: ItemStack) {
    companion object {
        fun material(material: Material): IB {
            return IB(ItemStack(material))
        }

        fun item(stack: ItemStack): IB {
            return IB(stack)
        }

        fun item(key: NamespacedKey): IB {
            return IB.material(Registry.MATERIAL.get(key)!!)
        }

        fun head(id: String): IB {
            // TODO
            return IB(ItemStack(Material.PLAYER_HEAD))
        }
    }

    fun displayName(text: String, modifier: TextModifier = TextModifier.C): IB {
        item.editMeta { meta ->
            meta.displayName(modifier.apply(text))
        }
        return this
    }

    fun setLore(lore: List<Pair<TextModifier, String>>): IB {
        item.editMeta { meta ->
            meta.lore(lore.map { it.first.apply(it.second) })
        }
        return this
    }

    fun setLore(vararg lore: Pair<TextModifier, String>): IB {
        return setLore(lore.toList())
    }

    fun setLore(lore: List<String>, modifier: TextModifier = TextModifier.C): IB {
        return setLore(lore.associateWith { modifier }.map { (key, value) -> value to key })
    }

    fun setLore(vararg lore: String, modifier: TextModifier = TextModifier.C): IB {
        return setLore(lore.toList(), modifier)
    }

    fun enchant(enchant: Enchantment, level: Int): IB {
        item.editMeta { meta ->
            meta.addEnchant(enchant, level, true)
        }
        return this
    }

    fun enchant(key: NamespacedKey, level: Int): IB {
        val enchant = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)[key] ?: error("enchantment $key not found in registry")
        return enchant(enchant, level)
    }

    fun glint(enabled: Boolean = true): IB {
        item.editMeta { meta ->
            meta.setEnchantmentGlintOverride(enabled)
        }
        return this
    }

    fun flags(flags: List<ItemFlag>): IB {
        item.itemFlags.addAll(flags)
        return this
    }

    fun meta(configure: ItemMeta.() -> Unit): IB {
        item.editMeta(configure)
        return this
    }

    operator fun invoke(): ItemStack {
        return item
    }
}

fun ib(material: Material, configure: IB.() -> Unit): IB {
    return IB.material(material).apply(configure)
}

fun ib(stack: ItemStack, configure: IB.() -> Unit): IB {
    return IB.item(stack).apply(configure)
}

fun ib(key: NamespacedKey, configure: IB.() -> Unit): IB {
    return IB.item(key).apply(configure)
}

fun ib(head: String, configure: IB.() -> Unit): IB {
    return IB.head(head).apply(configure)
}