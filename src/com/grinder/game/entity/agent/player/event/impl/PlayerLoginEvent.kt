package com.grinder.game.entity.agent.player.event.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.event.PlayerEvent

/**
 * @author Zach (zach@findzach.com)
 * @since 1/17/2021
 *
 * Will only execute if the player has successfully logged in
 */
class PlayerLoginEvent(var player: Player) : PlayerEvent {

    /**
     * checks if the player logging in, is a new account
     */
    fun isNewPlayer(): Boolean {
        return player.isNewPlayer
    }
}