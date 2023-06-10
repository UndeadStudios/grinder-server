package com.grinder.game.entity.agent.npc.monster.boss.impl.zulrah

import com.grinder.game.entity.agent.combat.attack.AttackType.MAGIC
import com.grinder.game.entity.agent.combat.attack.AttackType.RANGED
import com.grinder.game.entity.agent.npc.monster.boss.impl.zulrah.ZulrahAttackSwitchType.ALTERNATING
import com.grinder.game.entity.agent.npc.monster.boss.impl.zulrah.ZulrahAttackSwitchType.RANDOM
import com.grinder.game.entity.agent.npc.monster.boss.impl.zulrah.ZulrahCloudLocation.LEFT
import com.grinder.game.entity.agent.npc.monster.boss.impl.zulrah.ZulrahCloudLocation.RIGHT
import com.grinder.game.entity.agent.npc.monster.boss.impl.zulrah.ZulrahLocation.*
import com.grinder.game.entity.agent.npc.monster.boss.impl.zulrah.ZulrahState.*

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/12/2019
 * @version 1.0
 */
enum class ZulrahPatternType(vararg val patterns: ZulrahPattern) {

    ALFA(
            ZulrahPattern(NORTH, SERPENTINE),
            ZulrahPattern(NORTH, MAGMA),
            ZulrahPattern(NORTH, TANZANITE, null, null, MAGIC),
            ZulrahPattern(SOUTH, SERPENTINE, RIGHT,null,  RANGED),
            ZulrahPattern(NORTH, MAGMA),
            ZulrahPattern(WEST, TANZANITE, null, null, MAGIC),
            ZulrahPattern(SOUTH, SERPENTINE, RIGHT),
            ZulrahPattern(SOUTH, TANZANITE, LEFT, null, MAGIC),
            ZulrahPattern(WEST, SERPENTINE, null,  ALTERNATING, RANGED, MAGIC),
            ZulrahPattern(NORTH, MAGMA)
    ),
    BRAVO(
            ZulrahPattern(NORTH, SERPENTINE),
            ZulrahPattern(NORTH, MAGMA),
            ZulrahPattern(NORTH, TANZANITE, null, null, MAGIC),
            ZulrahPattern(WEST, SERPENTINE, LEFT, RANDOM, RANGED, MAGIC),
            ZulrahPattern(SOUTH, TANZANITE, null, null, MAGIC),
            ZulrahPattern(NORTH, MAGMA),
            ZulrahPattern(EAST, SERPENTINE, null,null, RANGED),
            ZulrahPattern(SOUTH, TANZANITE, LEFT, null, MAGIC),
            ZulrahPattern(WEST, SERPENTINE, null, ALTERNATING, RANGED, MAGIC),
            ZulrahPattern(NORTH, MAGMA)
    ),
    CHARLIE(
            ZulrahPattern(NORTH, SERPENTINE),
            ZulrahPattern(EAST, SERPENTINE, null, null, RANGED),
            ZulrahPattern(NORTH, MAGMA),
            ZulrahPattern(WEST, TANZANITE, null, null, MAGIC),
            ZulrahPattern(SOUTH, SERPENTINE, null, null, RANGED),
            ZulrahPattern(EAST, TANZANITE, null, null, MAGIC),
            ZulrahPattern(NORTH, SERPENTINE, LEFT),
            ZulrahPattern(WEST, SERPENTINE, LEFT, null, RANGED),
            ZulrahPattern(NORTH, TANZANITE, RIGHT),
            ZulrahPattern(EAST, SERPENTINE, RIGHT, ALTERNATING, MAGIC, RANGED),
            ZulrahPattern(NORTH, TANZANITE, RIGHT)
    ),
    DELTA(
            ZulrahPattern(NORTH, SERPENTINE),
            ZulrahPattern(EAST, TANZANITE, null,null, MAGIC),
            ZulrahPattern(SOUTH, SERPENTINE, null,null, RANGED),
            ZulrahPattern(WEST, TANZANITE, null, null, MAGIC),
            ZulrahPattern(NORTH, MAGMA),
            ZulrahPattern(EAST, SERPENTINE, RIGHT, null, RANGED),
            ZulrahPattern(SOUTH, SERPENTINE),
            ZulrahPattern(WEST, TANZANITE, LEFT, null, MAGIC),
            ZulrahPattern(NORTH, SERPENTINE, RIGHT, null, RANGED),
            ZulrahPattern(NORTH, TANZANITE, RIGHT, null, MAGIC),
            ZulrahPattern(EAST, SERPENTINE, RIGHT, ALTERNATING, MAGIC, RANGED),
            ZulrahPattern(NORTH, TANZANITE, RIGHT)
    )

}