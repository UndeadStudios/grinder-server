package com.grinder.game.content.item.degrading

/**
 * Represents ways in which an item can degrade.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 */
enum class DegradingType {

    /**
     * Expires after some fixed amount of health lost.
     */
    LOSE_HEALTH,

    /**
     * Expires after some fixed amount of attacks.
     */
    ATTACK,

    /**
     * Expires after some fixed amount of time that
     * degradeable items of this were worn.
     */
    WEAR_TIME,

    /**
     * Expires after some fixed amount of time that
     * degradeable items of this type were worn during combat.
     */
    WEAR_COMBAT_TIME,

    /**
     * Expires after some fixed amount of skilling operations performed.
     */
    SKILLING
}