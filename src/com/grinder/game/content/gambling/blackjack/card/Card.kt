package com.grinder.game.content.gambling.blackjack.card

/**
 * A class that represents a single card in a deck.
 *
 * @author Blake
 */
class Card(val suit: Suit, var value: CardValues) {

    override fun toString() = "$value of $suit"

    /**
     * The sprite id for the client.
     */
    val spriteId = suit.ordinal * 12 + value.ordinal + if (suit.ordinal > 0) 1 else 0
}