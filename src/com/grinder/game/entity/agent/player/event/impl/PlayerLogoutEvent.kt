package com.grinder.game.entity.agent.player.event.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.event.PlayerEvent

/**
 * @author Zach (zach@findzach.com)
 * @since 1/17/2021
 *
 * Will be executed if a user is requested to logout or is forced logged out
 */
class PlayerLogoutEvent(player: Player): PlayerEvent {
}