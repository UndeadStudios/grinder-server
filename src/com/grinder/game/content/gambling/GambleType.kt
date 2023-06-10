package com.grinder.game.content.gambling

/**
 * An enumerated type that consists of the different gambling modes.
 *
 * @author Blake
 *
 * @param winChance  an integer in the range 0..100 representing
 *                   the chance of winning a game of this type.
 */
enum class GambleType(val winChance: Int) {
    YOU_HOST(55),
    OTHER_HOST(45),
    BOTH_HOST(50),
    FLOWER_POKER(50),
    BLACKJACK(50);
}