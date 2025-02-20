package com.grinder.game.content.gambling.blackjack.card

/**
 * An enumerated type that represents the values of individual cards.
 *
 * @author Blake
 */
enum class CardValues(val numericValue: Int) {

    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    JACK(10),
    QUEEN(10),
    KING(10),
    ACE(11);

    override fun toString() = name.toLowerCase()
}