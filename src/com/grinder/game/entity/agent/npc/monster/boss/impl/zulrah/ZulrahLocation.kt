package com.grinder.game.entity.agent.npc.monster.boss.impl.zulrah

import com.grinder.game.model.Position
import com.grinder.game.model.areas.instanced.ZulrahShrine

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/12/2019
 * @version 1.0
 */
enum class ZulrahLocation(val position: Position) {

	NORTH(Position(2266 - ZulrahShrine.MAP_BASE.x, 3073 - ZulrahShrine.MAP_BASE.y)),
	SOUTH(Position(2266 - ZulrahShrine.MAP_BASE.x, 3063 - ZulrahShrine.MAP_BASE.y)),
	WEST(Position(2256 - ZulrahShrine.MAP_BASE.x, 3071 - ZulrahShrine.MAP_BASE.y)),
	EAST(Position(2277 - ZulrahShrine.MAP_BASE.x, 3071 - ZulrahShrine.MAP_BASE.y));

}