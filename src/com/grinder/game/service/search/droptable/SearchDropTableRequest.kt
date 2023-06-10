package com.grinder.game.service.search.droptable

import com.grinder.game.entity.agent.player.Player

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   13/12/2019
 * @version 1.0
 */
data class SearchDropTableRequest(val player: Player, val input: String, val type: SearchDropTableType)