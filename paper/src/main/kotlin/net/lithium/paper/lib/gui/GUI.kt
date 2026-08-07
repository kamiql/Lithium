package net.lithium.paper.lib.gui

import net.lithium.common.lib.text.c
import net.lithium.paper.lib.LithiumDsl
import net.lithium.paper.lib.item.IB
import net.lithium.paper.lib.item.ib
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.koin.core.component.KoinComponent

@LithiumDsl
class GUI private constructor(
    val title: String,
    val type: GuiType,
    private val size: Int?,
) : InventoryHolder, KoinComponent {

    private val inv: Inventory = size?.let { size ->
        Bukkit.createInventory(this, size, title.c())
    } ?: Bukkit.createInventory(this, type.type)

    constructor(title: String, type: GuiType) : this(
        title = title,
        type = type,
        size = null,
    )

    constructor(title: String, size: Int) : this(
        title = title,
        type = GuiType.CHEST,
        size = size,
    )

    override fun getInventory(): Inventory = inv

    val items = linkedMapOf<Int, Item>()

    private val directItems = linkedMapOf<Int, Item>()
    private val panes = mutableListOf<PanePlacement>()

    var cancelClick = true
    var cancelClose = false

    var closeCallback: ((GUIEvent<InventoryCloseEvent>) -> Unit)? = null

    fun filler(slot: Int) {
        setItem(slot, ib(Material.GRAY_STAINED_GLASS_PANE) {
            meta {
                isHideTooltip = true
            }
        })
    }

    fun back(slot: Int, callback: (GUIEvent<InventoryClickEvent>) -> Unit) {
        setItem(slot, ib(Material.BARRIER) {
            displayName("<red><b>Back")
        }) { event -> callback(event) }
    }

    fun setItem(
        slot: Int,
        ib: IB,
        callback: (GUIEvent<InventoryClickEvent>) -> Unit = {},
    ) {
        directItems[slot] = Item(
            ib = ib,
            callback = callback,
        )
    }

    fun setPane(
        pane: Pane,
        x: Int = 0,
        y: Int = 0,
    ) {
        panes += PanePlacement(
            pane = pane,
            x = x,
            y = y,
        )
    }

    fun onClose(callback: (GUIEvent<InventoryCloseEvent>) -> Unit) {
        closeCallback = callback
    }

    fun render() {
        inv.clear()
        items.clear()

        val guiWidth = type.width
        val guiHeight = inv.size / guiWidth

        panes.forEach { placement ->
            val pane = placement.pane

            require(placement.x >= 0) {
                "Pane x-position must not be negative"
            }

            require(placement.y >= 0) {
                "Pane y-position must not be negative"
            }

            require(placement.x + pane.width <= guiWidth) {
                "Pane exceeds GUI width"
            }

            require(placement.y + pane.height <= guiHeight) {
                "Pane exceeds GUI height"
            }

            pane.items().forEach { (localSlot, item) ->
                val localX = localSlot % pane.width
                val localY = localSlot / pane.width

                val absoluteX = placement.x + localX
                val absoluteY = placement.y + localY

                val absoluteSlot = absoluteY * guiWidth + absoluteX

                items[absoluteSlot] = item
            }
        }

        items.putAll(directItems)

        items.forEach { (slot, item) ->
            inv.setItem(slot, item.ib())
        }
    }

    fun show(player: HumanEntity) {
        render()
        player.openInventory(inv)
    }

    data class Item(
        val ib: IB,
        val callback: (GUIEvent<InventoryClickEvent>) -> Unit,
    )

    data class GUIEvent<E : Event>(
        val event: E,
        val player: Player,
        val gui: GUI,
    )

    private data class PanePlacement(
        val pane: Pane,
        val x: Int,
        val y: Int,
    )
}

fun gui(
    title: String,
    type: GuiType = GuiType.CHEST,
    configure: GUI.() -> Unit,
): GUI {
    return GUI(title, type).apply(configure)
}

fun gui(
    title: String,
    size: Int,
    configure: GUI.() -> Unit,
): GUI {
    return GUI(title, size).apply(configure)
}