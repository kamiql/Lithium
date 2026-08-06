package net.lithium.paper.lib.gui.panes

import net.lithium.paper.lib.LithiumDsl
import net.lithium.paper.lib.gui.GUI
import net.lithium.paper.lib.item.IB
import net.lithium.paper.lib.item.ib
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent

@LithiumDsl
class PaginationPane<T>(
    height: Int,
    width: Int,
    private val values: List<T>,
    private val mapper: (T) -> IB,
    private val onClick: (T, GUI.GUIEvent<InventoryClickEvent>) -> Unit = { _, _ -> },
    paginationSlots: List<Int> = defaultPaginationSlots(
        height = height,
        width = width,
    ),
) : GridPane(
    height = height,
    width = width,
    targetSlots = paginationSlots,
) {
    private var currentPage = 0

    private val pageSize: Int
        get() = targetSlots.size

    private val pageCount: Int
        get() = maxOf(
            1,
            (values.size + pageSize - 1) / pageSize,
        )

    private val navigationRow: Int
        get() = height - 1

    private val centerX: Int
        get() = width / 2

    private val backSlot: Int
        get() = navigationRow * width + centerX - 2

    private val nextSlot: Int
        get() = navigationRow * width + centerX + 2

    init {
        require(height >= 2) {
            "PaginationPane height must be at least 2"
        }

        require(width >= 5) {
            "PaginationPane width must be at least 5"
        }

        require(width % 2 == 1) {
            "PaginationPane width must be odd"
        }

        require(paginationSlots.distinct().size == paginationSlots.size) {
            "Pagination slots must be unique"
        }

        require(
            paginationSlots.all { it in 0 until height * width },
        ) {
            "Pagination slots must be within the pane bounds"
        }

        require(backSlot !in paginationSlots && nextSlot !in paginationSlots) {
            "Pagination slots must not contain navigation slots"
        }

        renderPage()
    }

    private fun renderPage() {
        clearItems()

        val fromIndex = currentPage * pageSize
        val pageItems = values
            .drop(fromIndex)
            .take(pageSize)

        pageItems.forEachIndexed { index, value ->
            item(
                index = index,
                ib = mapper(value),
                callback = { event ->
                    onClick(value, event)
                },
            )
        }

        itemAtLocalSlot(
            localSlot = backSlot,
            ib = ib(Material.ARROW) {
                displayName("<red>Back")
            },
            callback = { event ->
                if (currentPage > 0) {
                    currentPage--
                    renderPage()
                    event.gui.render()
                }
            },
        )

        itemAtLocalSlot(
            localSlot = nextSlot,
            ib = ib(Material.ARROW) {
                displayName("<green>Next")
            },
            callback = { event ->
                if (currentPage < pageCount - 1) {
                    currentPage++
                    renderPage()
                    event.gui.render()
                }
            },
        )
    }

    companion object {
        internal fun defaultPaginationSlots(
            height: Int,
            width: Int,
        ): List<Int> {
            return buildList {
                for (y in 0 until height - 1) {
                    for (x in 0 until width) {
                        add(y * width + x)
                    }
                }
            }
        }
    }
}

fun <T> paginationPane(
    height: Int,
    width: Int,
    items: List<T>,
    mapper: (T) -> IB,
    paginationSlots: List<Int> = PaginationPane.defaultPaginationSlots(
        height,
        width
    ),
    onClick: (
        item: T,
        event: GUI.GUIEvent<InventoryClickEvent>,
    ) -> Unit = { _, _ -> },
    configure: PaginationPane<T>.() -> Unit = {},
): PaginationPane<T> {
    return PaginationPane(
        height = height,
        width = width,
        values = items,
        mapper = mapper,
        onClick = onClick,
        paginationSlots = paginationSlots,
    ).apply(configure)
}