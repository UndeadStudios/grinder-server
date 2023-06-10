package com.grinder.game.entity.agent.player

/**
 * Represents a player's privilege rights.
 *
 * @author relex lawl
 */
enum class PlayerInteractingOption {

    NONE,
    CHALLENGE,
    ATTACK;

    companion object {
        @JvmStatic
        fun forName(name: String): PlayerInteractingOption? {
            if (name.toLowerCase().contains("null")) return NONE
            for (option in values()) {
                if (option.toString().equals(name, ignoreCase = true)) {
                    return option
                }
            }
            return null
        }
    }
}