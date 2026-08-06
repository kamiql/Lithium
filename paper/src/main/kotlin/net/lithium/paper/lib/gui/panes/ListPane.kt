package net.lithium.paper.lib.gui.panes

import net.lithium.paper.lib.LithiumDsl
import net.lithium.paper.lib.gui.GUI
import net.lithium.paper.lib.item.IB
import org.bukkit.event.inventory.InventoryClickEvent

@LithiumDsl
class ListPane<T>(
    height: Int,
    width: Int,
    private val values: List<T>,
    private val mapper: (T) -> IB,
    private val onClick: (
        T,
        GUI.GUIEvent<InventoryClickEvent>,
    ) -> Unit = { _, _ -> },
    listSlots: List<Int> = defaultListSlots(
        height = height,
        width = width,
    ),
) : GridPane(
    height = height,
    width = width,
    targetSlots = listSlots,
) {
    init {
        require(listSlots.distinct().size == listSlots.size) {
            "List slots must be unique"
        }

        require(
            listSlots.all { it in 0 until height * width },
        ) {
            "List slots must be within the pane bounds"
        }

        require(values.size <= listSlots.size) {
            "ListPane cannot display ${values.size} items in ${listSlots.size} slots"
        }

        renderItems()
    }

    private fun renderItems() {
        clearItems()

        values.forEachIndexed { index, value ->
            item(
                index = index,
                ib = mapper(value),
                callback = { event ->
                    onClick(value, event)
                },
            )
        }
    }

    companion object {
        internal fun defaultListSlots(
            height: Int,
            width: Int,
        ): List<Int> {
            return buildList {
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        add(y * width + x)
                    }
                }
            }
        }
    }
}

fun <T> listPane(
    height: Int,
    width: Int,
    items: List<T>,
    mapper: (T) -> IB,
    listSlots: List<Int> = ListPane.defaultListSlots(
        height = height,
        width = width,
    ),
    onClick: (
        item: T,
        event: GUI.GUIEvent<InventoryClickEvent>,
    ) -> Unit = { _, _ -> },
    configure: ListPane<T>.() -> Unit = {},
): ListPane<T> {
    return ListPane(
        height = height,
        width = width,
        values = items,
        mapper = mapper,
        onClick = onClick,
        listSlots = listSlots,
    ).apply(configure)
}