package com.grinder.game.entity.agent.npc.slayer

import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.npc.monster.Monster
import com.grinder.game.model.Position

/**
 * SlayerMonster represents a monster that requires a certain level of slayer skill to vanquish. Usually has unique
 * circumstances to slayer, or when in combat.
 *
 * @param id Identifier of the NPC.
 * @param spawn Position of it's entry-point.
 */
open class SlayerMonster(id:Int, spawn: Position): Monster(id, spawn) {

    override fun attackRange(type: AttackType): Int {
        return if(type == AttackType.MELEE) 1 else 8
    }
}