package com.grinder.game.content.gambling.blackjack

import com.grinder.game.content.gambling.GambleType
import com.grinder.game.content.gambling.blackjack.card.Card
import com.grinder.game.content.gambling.blackjack.card.CardValues
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.block
import com.grinder.game.entity.agent.player.removeInterfaces
import com.grinder.game.model.item.Item
import com.grinder.game.task.TaskManager

/**
 * A class that handles the functionality of blackjack.
 *
 * @author Blake
 * @author Stan van der Bend
 */
object BlackJack {

    /**
     * A collection of the currently active [games][BlackJackGame].
     */
    private val activeGames: MutableList<BlackJackGame> = ArrayList()

    const val BLACKJACK = 21
    const val INTERFACE_ID = 52000
    const val PLAYER_CARD_START_ID = 52010
    const val GAMBLING_WITH_ID = 52003
    const val PLAYER_SCORE_ID = 52032
    const val OPPONENT_NAME_ID = 52023
    const val OPPONENT_SCORE_ID = 52033
    const val OPPONENT_CARD_START_ID = 52016
    const val MAX_CARDS_PER_HAND = 6

    /**
     * Creates a [game][BlackJackGame] between the [first] and [second] players,
     * where the bet items are contained in the [firstItems] and [secondItems] lists.
     *
     * Also adds the created game to the [activeGames] list.
     */
	@JvmStatic
	fun addGame(first: Player, second: Player, firstItems: List<Item>, secondItems: List<Item>) : BlackJackGame? {

        if (first.gambling.type != GambleType.BLACKJACK || second.gambling.type != GambleType.BLACKJACK)
            return null

        val game = BlackJackGame(first, second, firstItems, secondItems)
        first.block()
        second.block()
        activeGames.add(game)
        game.start()
        return game
    }

    /**
     * Removes the argued [game] from the [activeGames] list.
     */
	@JvmStatic
	fun removeGame(game: BlackJackGame) {
        activeGames.remove(game)
        TaskManager.cancelTasks(game)
    }

    /**
     * Handles the button clicking.
     */
	@JvmStatic
	fun clickButton(player: Player, buttonId: Int): Boolean {
        if (isPlaying(player)) {

            val game =  activeGames.find { it.contains(player) }?:return false

            if (buttonId == 52028) {
                game.hit(player)
                return true
            } else if (buttonId == 52024) {
                game.stand(player)
                return true
            }
        } else if(buttonId == 52004)
            player.removeInterfaces()
        return false
    }

    /**
     * Calculates the hand value.
     */
	@JvmStatic
	fun calculateHand(hand: List<Card>): Int {
        var total = 0
        var aces = 0
        for (card in hand) {
            total += card.value.numericValue
            if (card.value == CardValues.ACE)
                aces++
        }
        while (aces > 0 && total > BLACKJACK) {
            total -= 10
            aces--
        }
        return total
    }

    /**
     * Check if the specified player is playing.
     */
    fun isPlaying(player: Player): Boolean {
        return activeGames.any { it.contains(player) }
    }

    @JvmStatic
	fun negateButton(player: Player, buttonId: Int): Boolean {
        return player.interfaceId == INTERFACE_ID && isPlaying(player) && buttonId != 52028 && buttonId != 52024 && buttonId != 52004
    }
}