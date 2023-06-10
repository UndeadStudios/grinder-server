package com.grinder.game.entity.agent.npc.monster.boss.impl.zulrah

import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.model.Animation
import com.grinder.util.Priority
import com.grinder.util.NpcID

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   02/12/2019
 * @version 1.0
 */
enum class ZulrahState(
        val npcId: Int,
        val type: AttackType,
        val animation: Animation){

    SERPENTINE(NpcID.ZULRAH, AttackType.RANGED, Animation(5068, Priority.HIGH)),
    MAGMA(NpcID.ZULRAH_2043, AttackType.MELEE, Animation(5806, Priority.HIGH)),
    TANZANITE(NpcID.ZULRAH_2044, AttackType.MAGIC, Animation(5068, Priority.HIGH))

}