package com.grinder.game.content.gambling.blackjack.card

/**
 * A class that represents a deck of playing cards.
 *
 * @author Blake
 */
class Deck {

    /**
     * A collection that holds the cards for this deck's instance.
     */
    private val deck = ArrayList(CARDS)

    /**
     * Gets a single card from the deck by first shuffling the deck.
     *
     * @return The card.
     */
    fun drawCard(): Card {
        deck.shuffle()
        val card = deck.random()!!
        deck.remove(card)
        return card
    }

    override fun toString(): String {
        return "Deck(cards=$deck)"
    }


    companion object {

        /**
         * A collection that holds all of the available playing cards.
         */
        private val CARDS: MutableList<Card> = ArrayList()

        /*
         * Initializes the class.
         */
        init {
            for (suit in Suit.values()) {
                for (value in CardValues.values()) {
                    CARDS.add(Card(suit, value))
                }
            }
        }
    }
}