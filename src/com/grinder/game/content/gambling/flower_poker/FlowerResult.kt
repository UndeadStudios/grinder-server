package com.grinder.game.content.gambling.flower_poker

/**
 * @since   20/01/2020
 * @version 1.0
 */
enum class FlowerResult(val identifier: String) {

    BUST("a busted hand"),
    ONE_PAIR("one pair"),
    TWO_PAIRS("two pairs"),
    THREE_OF_A_KIND("three of a kind"),
    FULL_HOUSE("a full house"),
    FOUR_OF_A_KIND("four of a kind"),
    FIVE_OF_A_KIND("five of a kind");
}