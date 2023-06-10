package com.grinder.game.content.gambling.blackjack.card

/**
 * An enumerated type that represents the suit of a card.
 *
 * @author Blake
 */
enum class Suit {

    CLUBS,
    DIAMONDS,
    HEARTS,
    SPADES;

    override fun toString() = name.toLowerCase()
}