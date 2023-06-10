package com.grinder.game.content.miscellaneous.randomevent

import com.grinder.game.entity.agent.player.Player

object RandomEvents {

    /**
     * Triggers a food puzzle random event for clicking objects such as stalls.
     * Teleports the player to another location
     */
    @JvmStatic
    fun triggerPuzzle(player: Player?) {
        RandomEvent.FOOD_PUZZLE.trigger(player!!)
    }

    /**
     * Puzzle random event interface
     */
    @JvmStatic
    fun foodPuzzle(player: Player?) {
        RandomEvent.FOOD_PUZZLE.accept(player!!)
    }

    /**
     * Triggers the refreshments random event
     */
    @JvmStatic
    fun triggerRefreshments(player: Player?) {
        RandomEvent.REFRESHMENTS_PUZZLE.trigger(player!!)
    }

    /**
     * Refreshments random event interface
     */
    @JvmStatic
    fun refreshmentsPuzzle(player: Player?) {
        RandomEvent.REFRESHMENTS_PUZZLE.accept(player!!)
    }
}