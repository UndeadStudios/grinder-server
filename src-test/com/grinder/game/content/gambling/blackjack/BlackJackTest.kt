package com.grinder.game.content.gambling.blackjack

import com.grinder.GrinderBiPlayerTest
import com.grinder.game.content.gambling.GambleType
import com.grinder.game.content.gambling.blackjack.card.Deck
import com.grinder.game.model.item.Item
import com.grinder.game.task.TaskManager
import com.grinder.util.ServerClassPreLoader
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Contains tests for [BlackJack].
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/09/2020
 */
internal class BlackJackTest : GrinderBiPlayerTest("gambler") {

    lateinit var game: BlackJackGame

    @BeforeEach
    fun setUp() {
        ServerClassPreLoader.forceInit(Deck::class.java)
    }

    private fun createGame() {
        if(this::game.isInitialized){
            game.onEnd(player1)
            game.onEnd(player2)
        }
        player1.gambling.type = GambleType.BLACKJACK
        player2.gambling.type = GambleType.BLACKJACK
        val items1 = arrayListOf(Item(1, 1))
        val items2 = arrayListOf(Item(2, 1))
        game = BlackJack.addGame(player1, player2, items1, items2)!!
    }

    @Test
    fun testIsPlayingCheck(){
        createGame()
        Assertions.assertTrue(BlackJack.isPlaying(player1))
        Assertions.assertTrue(BlackJack.isPlaying(player2))
    }

    @Test
    fun testGame(){

        createGame()

        // open interface after 1 cycle
        TaskManager.sequence()

        // draw player1 card after 2 cycles
        TaskManager.sequence()

        val player1Hand = game.getHand(player1)
        val player2Hand = game.getHand(player2)

        Assertions.assertTrue(player1Hand.size == 1)
        Assertions.assertTrue(player2Hand.size == 1)

        // draw player2 card after 4 cycles
        repeat(2) {
            TaskManager.sequence()
        }

        Assertions.assertTrue(player1Hand.size == 2)
        Assertions.assertTrue(player2Hand.size == 2)

        while(!game.finished){
            game.hit(player1)
            game.hit(player2)
            TaskManager.sequence()
        }

        Assertions.assertTrue(game.finished)

        Assertions.assertFalse(BlackJack.isPlaying(player1))
        Assertions.assertFalse(BlackJack.isPlaying(player2))
    }

    @Test
    fun testPerpetualWaitingGame(){

        repeat(100) {
            do {
                createGame()

                // open interface after 1 cycle
                // draw first card after 2 cycles
                // draw second card after 4 cycles
                repeat(4) {
                    TaskManager.sequence()
                }

                while (!game.finished) {
                    TaskManager.sequence()
                }
            } while (!game.hasWinner())

            Assertions.assertFalse(game.winner.inventory.isEmpty)
            Assertions.assertTrue(game.finished)

            Assertions.assertFalse(BlackJack.isPlaying(player1))
            Assertions.assertFalse(BlackJack.isPlaying(player2))

        }
    }
}