package com.grinder.game.content.skill.skillable.impl.herblore

/**
 * Represents a possible outcome of a decant action.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-25
 */
enum class PotionDecantOutcome {
    SUCCESS,
    NO_POTS_FOUND,
    NO_INV_SPACE,
    CANT_PAY
}