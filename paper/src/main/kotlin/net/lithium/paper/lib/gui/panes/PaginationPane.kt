package net.lithium.paper.lib.gui.panes

import net.lithium.common.lib.text.c
import net.lithium.paper.lib.LithiumDsl
import net.lithium.paper.lib.dialog.Dialog
import net.lithium.paper.lib.dialog.inputs.TextInput
import net.lithium.paper.lib.gui.GUI
import net.lithium.paper.lib.item.IB
import net.lithium.paper.lib.item.ib
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

@LithiumDsl
class PaginationPane<T>(
    height: Int,
    width: Int,
    private val values: List<T>,
    private val mapper: (T) -> IB,
    private val onClick: (T, GUI.GUIEvent<InventoryClickEvent>) -> Unit = { _, _ -> },
    paginationSlots: List<Int> = defaultPaginationSlots(height, width),
) : GridPane(
    height = height,
    width = width,
    targetSlots = paginationSlots,
) {

    private var searchPredicate: ((query: String, value: T) -> Boolean)? = null
    var query: String = ""
    private var currentPage: Int = 0

    private val pageSize: Int
        get() = targetSlots.size

    private val filteredValues: List<T>
        get() = values.filter { value ->
            searchPredicate?.invoke(query, value) ?: true
        }

    private val pageCount: Int
        get() = maxOf(
            1,
            (filteredValues.size + pageSize - 1) / pageSize,
        )

    private val navigationRow: Int
        get() = height - 1

    private val centerX: Int
        get() = width / 2

    private val backSlot: Int
        get() = navigationRow * width + centerX - 2

    private val searchSlot: Int
        get() = navigationRow * width + centerX

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

        require(paginationSlots.isNotEmpty()) {
            "Pagination slots must not be empty"
        }

        require(paginationSlots.distinct().size == paginationSlots.size) {
            "Pagination slots must be unique"
        }

        require(paginationSlots.all { it in 0 until height * width }) {
            "Pagination slots must be within the pane bounds"
        }

        require(
            backSlot !in paginationSlots &&
                    searchSlot !in paginationSlots &&
                    nextSlot !in paginationSlots,
        ) {
            "Pagination slots must not contain navigation slots"
        }

        render()
    }

    /**
     * Configures the predicate used for filtering.
     *
     * Calling this method resets the current page.
     */
    fun search(query: String = "", predicate: (query: String, value: T) -> Boolean) {
        this.query = query
        searchPredicate = predicate
        currentPage = 0
        render()
    }

    private fun render() {
        currentPage = currentPage.coerceIn(0, pageCount - 1)

        clearItems()
        renderNavigationBackground()
        renderPageItems()
        renderNavigation()
    }

    private fun renderNavigationBackground() {
        for (slot in navigationRow * width until height * width) {
            filler(slot)
        }
    }

    private fun renderPageItems() {
        val pageItems = filteredValues
            .drop(currentPage * pageSize)
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
    }

    private fun renderNavigation() {
        if (currentPage > 0) {
            renderBackButton()
        }

        if (searchPredicate != null) {
            renderSearchButton()
        }

        if (currentPage < pageCount - 1) {
            renderNextButton()
        }
    }

    private fun renderBackButton() {
        itemAtLocalSlot(
            localSlot = backSlot,
            ib = ib(Material.ARROW) {
                displayName("<red>Back")
            },
            callback = { event ->
                if (currentPage == 0) return@itemAtLocalSlot

                currentPage--
                render()
                event.gui.render()
            },
        )
    }

    private fun renderNextButton() {
        itemAtLocalSlot(
            localSlot = nextSlot,
            ib = ib(Material.ARROW) {
                displayName("<green>Next")
            },
            callback = { event ->
                if (currentPage >= pageCount - 1) return@itemAtLocalSlot

                currentPage++
                render()
                event.gui.render()
            },
        )
    }

    private fun renderSearchButton() {
        itemAtLocalSlot(
            localSlot = searchSlot,
            ib = ib(Material.OAK_SIGN) {
                displayName("${this@PaginationPane.query.takeIf { it.isNotEmpty() } ?: "<yellow>Search"} ")

                addInstructions(
                    ClickType.LEFT to "Search",
                    ClickType.RIGHT to "Reset",
                )
            },
            callback = { event ->
                when (event.event.click) {
                    ClickType.LEFT -> {
                        event.player.showDialog(
                            Dialog("Search") {
                                input(
                                    TextInput(
                                        id = "query",
                                        label = "Search query".c(),
                                        initialValue = this@PaginationPane.query,
                                    )
                                )

                                callback { data ->
                                    val query = data["query"] as String
                                    this@PaginationPane.query = query
                                    this@PaginationPane.render()
                                    event.gui.render()
                                }
                            }
                        )
                    }
                    ClickType.RIGHT -> {
                        query = ""
                        render()
                        event.gui.render()
                    }
                    else -> Unit
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
                for (row in 0 until height - 1) {
                    for (column in 0 until width) {
                        add(row * width + column)
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
    onClick: (
        item: T,
        event: GUI.GUIEvent<InventoryClickEvent>,
    ) -> Unit = { _, _ -> },
    paginationSlots: List<Int> = PaginationPane.defaultPaginationSlots(
        height = height,
        width = width,
    ),
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