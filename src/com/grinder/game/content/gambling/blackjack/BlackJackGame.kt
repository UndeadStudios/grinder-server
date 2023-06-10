package com.grinder.game.content.gambling.blackjack

import com.grinder.game.content.gambling.blackjack.BlackJack.GAMBLING_WITH_ID
import com.grinder.game.content.gambling.blackjack.BlackJack.OPPONENT_CARD_START_ID
import com.grinder.game.content.gambling.blackjack.BlackJack.OPPONENT_NAME_ID
import com.grinder.game.content.gambling.blackjack.BlackJack.OPPONENT_SCORE_ID
import com.grinder.game.content.gambling.blackjack.BlackJack.PLAYER_CARD_START_ID
import com.grinder.game.content.gambling.blackjack.BlackJack.PLAYER_SCORE_ID
import com.grinder.game.content.gambling.blackjack.BlackJack.calculateHand
import com.grinder.game.content.gambling.blackjack.BlackJack.removeGame
import com.grinder.game.content.gambling.blackjack.card.Deck
import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.removeAttribute
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.attribute.value.CardsValueHolder
import com.grinder.game.model.item.Item
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager

/**
 * A class that handles a single blackjack game between two players.
 *
 * @author Blake
 * @author Stan van der Bend
 */
class BlackJackGame(
    val player1: Player,
    val player2: Player,
    private val player1Items: List<Item>,
    private val player2Items: List<Item>)
{
    private val deck = Deck()

    lateinit var winner : Player
    var finished = false

    private var player1SelectedState = State.NONE
    private var player2SelectedState = State.NONE

    /**
     * Starts up the game.
     */
    fun start() {

        onStart(player1, player2)
        onStart(player2, player1)

        TaskManager.submit(1) {
            player1.openInterface(BlackJack.INTERFACE_ID)
            player2.openInterface(BlackJack.INTERFACE_ID)
        }

        // draw 2 cards
        TaskManager.submit(object : Task(2, this, false) {
            var amt = 0
            override fun execute() {
                hit(player1)
                hit(player2)
                if (++amt == 2) {
                    player1.packetSender.sendMessage("bj_timer:60")
                    player2.packetSender.sendMessage("bj_timer:60")
                    stop()
                }
            }

            override fun stop() {
                player1.unblock()
                player2.unblock()
                super.stop()
            }
        })

        // this task cancels when a game is over
        TaskManager.submit(object : Task(100, this, false) {
            override fun execute() {

                if(finished) {
                    stop()
                    return
                }

                player1.packetSender.sendMessage("bj_timer:60")
                player2.packetSender.sendMessage("bj_timer:60")
                if(player1SelectedState == State.NONE)
                    stand(player1)
                if(player2SelectedState == State.NONE)
                    stand(player2)
            }
        })
    }

    /**
     * Blocks all actions, disables movement, updates interface.
     */
    private fun onStart(player: Player, opponent: Player){
        player.removeAttribute(Attribute.BLACKJACK_HAND)
        player.block(blockDisconnect = true, blockMovement = false)
        player.motion.update(MovementStatus.DISABLED)
        player.packetSender.sendString(GAMBLING_WITH_ID, "Gambling with: " + opponent.username)
        player.packetSender.sendString(OPPONENT_NAME_ID, opponent.username + ":")
        player.packetSender.sendString(PLAYER_SCORE_ID, "0")
        player.packetSender.sendString(OPPONENT_SCORE_ID, "0")
    }


    /**
     * Draw's a random card from the deck.
     */
    private fun drawCard(player: Player) :State {
        val hand = getHand(player)
        if (hand.size > 5) {
            player.message("Table full!")
            return  State.STAND
        }
        hand.add(deck.drawCard())
        updateCardView(recipient = player, subject = player)
        return if(calculateHand(hand) == BlackJack.BLACKJACK) State.BLACKJACK else State.HIT
    }

    private fun updateCardView(recipient: Player, subject: Player) {
        val hand = getHand(subject)
        val handValue = calculateHand(hand)
        val card = hand.lastOrNull()?:return
        val (scoreId, deckStart) = if ((getOpponent(recipient) == subject))
            OPPONENT_SCORE_ID to OPPONENT_CARD_START_ID
        else
            PLAYER_SCORE_ID to PLAYER_CARD_START_ID
        recipient.packetSender.sendString(scoreId, handValue.toString())
        recipient.packetSender.sendInterfaceSpriteChange(deckStart + hand.size - 1, card.spriteId)
    }

    /**
     * Checks if the player has won.
     */
    fun checkForWin() {

        val firstValue = calculateHand(getHand(player1))
        val secondValue = calculateHand(getHand(player2))

        var draw = false


        when {
            isBust(firstValue) && isBust(secondValue) -> draw = true
            isBust(firstValue) -> winner = player2
            isBust(secondValue) -> winner = player1
            firstValue == BlackJack.BLACKJACK && secondValue == BlackJack.BLACKJACK -> draw = true
            (player1SelectedState == State.STAND || player1SelectedState == State.BLACKJACK)
                    && (player2SelectedState == State.STAND || player2SelectedState == State.BLACKJACK) -> {
                when {
                    firstValue > secondValue -> winner = player1
                    secondValue > firstValue -> winner = player2
                    firstValue == secondValue -> draw = true
                }
            }
        }

        if (draw || this::winner.isInitialized) {
            finished = true
            if(draw){
                player1.message("<img=770> It's a draw!", Color.MAGENTA)
                player2.message("<img=770> It's a draw!", Color.MAGENTA)
                player1.gambling.end(player1, player2, player1Items, player2Items, true, false)
            } else {
                winner.gambling.end(winner, getOpponent(winner), player1Items, player2Items, false, true)
            }
            removeGame(this)
            onEnd(player1)
            onEnd(player2)
        }
    }

    /**
     * Resets the attributes for the player.
     */
    fun onEnd(player: Player) {
        player.packetSender.sendMessage("bj_timer:0")
        player.removeAttribute(Attribute.BLACKJACK_HAND)
        player.BLOCK_ALL_BUT_TALKING = false
        player.unblock(unblockDisconnect = true, unblockMovement = true)
    }

    fun hasWinner() = this::winner.isInitialized

    /**
     * Handles the stand function.
     */
    fun stand(player: Player) {
        selectState(player, State.STAND)
    }

    /**
     * Handles the hit function.
     */
    fun hit(player: Player) {
        selectState(player, State.HIT)
    }

    private fun sendStateMessage(recipient: Player, subject: Player, subjectState: State) {
        val (color, body) = if (getOpponent(subject) == recipient)
            subjectState.opponentColor to subjectState.opponentMessage
        else
            subjectState.playerColor to subjectState.playerMessage
        recipient.message("<img=770> $body", color)
    }

    private fun selectState(player: Player, state: State) {
        val previousSelectedState = getNextState(player)
        if (previousSelectedState == State.NONE) {
            val newSelected = if (state == State.HIT) drawCard(player) else state
            sendStateMessage(player, player, newSelected)
            if (player == player1)
                player1SelectedState = state
            else
                player2SelectedState = state
            if (player1SelectedState != State.NONE && player2SelectedState != State.NONE) {
                sendStateMessage(player1, subject = player2, subjectState = player2SelectedState)
                sendStateMessage(player2, subject = player1, subjectState = player1SelectedState)
                if (player2SelectedState == State.HIT)
                    updateCardView(player1, player2)
                if (player1SelectedState == State.HIT)
                    updateCardView(player2, player1)
                checkForWin()
                player1SelectedState = State.NONE
                player2SelectedState = State.NONE
            }
        } else
            player.message("<img=770> ${previousSelectedState.alreadySetMessage}", previousSelectedState.playerColor)
    }

    private fun getNextState(player: Player) : State {
        return if(player == player1) player1SelectedState else player2SelectedState
    }

    fun printState(){
        println("$player1 has "+getHand(player1))
        println("$player2 has "+getHand(player2))
    }

    operator fun contains(player: Player) = player1 === player || player2 === player

    fun getHand(player: Player) = player.attributes
            .getValue(Attribute.BLACKJACK_HAND) {CardsValueHolder()}

    /**
     * Checks if the value is bust.
     */
    fun isBust(value: Int) = value > BlackJack.BLACKJACK

    fun getOpponent(player: Player) = if (player1 == player) player2 else player1

    /**
     * An enumerated type that holds the state of the game.
     */
    internal enum class State(
        val alreadySetMessage: String = "?",
        val playerMessage: String = "?",
        val opponentMessage: String = "?",
        val playerColor: Color = Color.DARK_RED,
        val opponentColor: Color = Color.RED
    ) {
        NONE,
        HIT(
            alreadySetMessage = "You have already selected hit.",
            playerMessage = "You hit!",
            opponentMessage = "Your opponent hit!"
        ),
        STAND(
            alreadySetMessage = "You have already selected hit.",
            playerMessage = "You stand!",
            opponentMessage = "Your opponent stands!"),
        BLACKJACK(
            alreadySetMessage = "You already hit blackjack, you automatically stand.",
            playerMessage = "You hit @mag@Black Jack@yel@!",
            opponentMessage = "Your opponent hit @mag@Black Jack@yel@!")
    }
}