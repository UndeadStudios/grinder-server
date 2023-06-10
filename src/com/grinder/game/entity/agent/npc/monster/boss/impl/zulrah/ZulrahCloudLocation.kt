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
enum class ZulrahCloudLocation(vararg val positions: Position) {
    MIDDLE(Position(2267 - ZulrahShrine.MAP_BASE.x, 3068 - ZulrahShrine.MAP_BASE.y), Position(2264 - ZulrahShrine.MAP_BASE.x, 3068 - ZulrahShrine.MAP_BASE.y), Position(2270 - ZulrahShrine.MAP_BASE.x, 3068 - ZulrahShrine.MAP_BASE.y)),
    RIGHT(Position(2272 - ZulrahShrine.MAP_BASE.x, 3070 - ZulrahShrine.MAP_BASE.y), Position(2272 - ZulrahShrine.MAP_BASE.x, 3073 - ZulrahShrine.MAP_BASE.y), Position(2272 - ZulrahShrine.MAP_BASE.x, 3076 - ZulrahShrine.MAP_BASE.y)),
    LEFT(Position(2262 - ZulrahShrine.MAP_BASE.x, 3070 - ZulrahShrine.MAP_BASE.y), Position(2262 - ZulrahShrine.MAP_BASE.x, 3073 - ZulrahShrine.MAP_BASE.y), Position(2262 - ZulrahShrine.MAP_BASE.x, 3076 - ZulrahShrine.MAP_BASE.y))
}