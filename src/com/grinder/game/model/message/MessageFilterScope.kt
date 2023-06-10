package com.grinder.game.model.message

/**
 * Represents the scope at which a filter is applied.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   26/03/2020
 * @version 1.0
 */
enum class MessageFilterScope {
    ALL,
    PRIVATE_ONLY,
    CLAN_ONLY,
    PUBLIC_ONLY
}