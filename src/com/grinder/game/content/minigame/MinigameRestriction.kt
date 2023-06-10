package com.grinder.game.content.minigame

/**
 * Restriction that may be applied to [minigames][Minigame].
 */
enum class MinigameRestriction {

    /**
     * No items allowed
     */
    NO_ITEMS,

    /**
     * No inventory items allowed
     */
    NO_INVENTORY,

    /**
     * No equipment allowed
     */
    NO_EQUIPMENT,

    /**
     * Minimum 70 combat required
     */
    SEVENTY_COMBAT_LEVEL_REQUIRED,

    /**
     * No restriction
     */
    NONE
}