package com.grinder.game.content.item

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.ButtonActions
import com.grinder.game.model.ItemActions
import com.grinder.util.ItemID
import kotlin.math.max
import kotlin.math.min

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   28/12/2019
 * @version 1.0
 */
class Book(
    val bookItemId: Int,
    val bookTitleString: String,
    val bookInterfaceId: Int = 837,
    val bookTitleId: Int = 903,
    bookPreviousPageButtonId: Int = 839,
    bookNextPageButtonId: Int = 841,
    val bookLeftPageId: Int = 14165,
    val bookRightPageId: Int = 14166,
    val bookLineRange: IntRange = 843..864,
    var text: HashMap<Int, Array<String>> = HashMap()
) {

    companion object {
        private val trackers = HashMap<String, BookTracker>()

        init {
            val bob = Book(292, bookTitleString = "Book on Baxtorian")

            bob.addPage(0,
                "",
                    "A pebbble will lead me to",
                    " the treasure. Once acquired",
                    " salvage a tomb for the ",
                    " ultimate treasure. ",
            )

            val book = Book(
                ItemID.BOOK_ON_CHEMICALS,
                bookTitleString = "Book on Chemicals"
            )
            book.addPage(
                0,
                "- ADDICTIVIO",
                "  This additive adds a highly",
                "addictive component to the",
                "host potion.",
                "",
                "@red@! CAUTION !",
                "  Do not try out this additive",
                "or one might be irreversibly",
                "addicted to the host potion.",
                "",
                "",
                "Ingredients:",
                "   - 2x Wolfbane Leaves",
                "   - 1x Dragon Bones",
                "Enchantment:",
                "   'Addictivo'",
                "   'Momento'",
                "   'Impregnato'",
                "",
                "Add the ingredients and",
                "say the enchantment."
            )
            book.addPage(
                1,
                "Counter Effect",
                "  To counter the addictivo",
                "additive, one must perform",
                "the following steps.",
                "",
                "Mix the ingredients",
                "   - 1x Bones",
                "   - 1x Banana",
                "   - 1x Weeds",
                "together in a vial."
            )
        }
    }

    fun addPage(index: Int, vararg text: String) {
        this.text[index] = text.asList().toTypedArray()
    }

    init {
        ItemActions.onClick(bookItemId) {
            if (isDropAction()) { //Allows users to drop items
                return@onClick false
            }
            if (isInInventory()) {
                trackers[player.username] = BookTracker(this@Book).also {
                    it.update(player)
                }
                return@onClick true
            }
            return@onClick false
        }
        ButtonActions.onClick(bookPreviousPageButtonId) {
            trackers.putIfAbsent(player.username, BookTracker(this@Book))
            trackers[player.username]!!.previousPage()
            trackers[player.username]!!.update(player)
        }
        ButtonActions.onClick(bookNextPageButtonId) {
            trackers.putIfAbsent(player.username, BookTracker(this@Book))
            trackers[player.username]!!.nextPage()
            trackers[player.username]!!.update(player)
        }
    }

    class BookTracker(private val book: Book) {
        private var pagesIndex = 0

        fun nextPage() {
            pagesIndex = max(0, min(pagesIndex + 1, book.text.size - 1))
        }

        fun previousPage() {
            pagesIndex = max(0, pagesIndex - 1)
        }

        fun update(player: Player) {

            val sender = player.packetSender

            sender.sendInterface(book.bookInterfaceId)
            sender.sendString(book.bookTitleId, book.bookTitleString)
            sender.sendString(book.bookLeftPageId, "Page ${pagesIndex * 2 + 1}")
            sender.sendString(book.bookRightPageId, "Page ${pagesIndex * 2 + 2}")
            sender.clearInterfaceText(book.bookLineRange.first, book.bookLineRange.last)

            book.text[pagesIndex]?.forEachIndexed { i, line ->
                sender.sendString(book.bookLineRange.first + i, line)
            }
        }
    }
}