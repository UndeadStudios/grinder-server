package com.grinder.game.content.gambling

/**
 * An enumerated type that consists of the different gambling states.
 *
 * @author Blake
 */
internal enum class GambleState {

    /**
     * There is no active or requested gamble session.
     */
    NONE,

    /**
     * There is a request to start a gamble session.
     */
    REQUESTED,

    /**
     * A gamble session has been initiated, rules have to be selected,
     * items can be staked.
     */
    SELECT_RULES,

    /**
     * The rules have been accepted by the player.
     */
    ACCEPTED_RULES,

    /**
     * The rules of the session have been finalised, the game starts.
     */
    STARTED;

    fun canDecline() = this == SELECT_RULES
}