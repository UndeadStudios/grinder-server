package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.model.Position
import com.grinder.util.NpcID

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   09/04/2020
 * @version 1.0
 */
class CrazyArcheologistBoss(position: Position)
    : Boss(NpcID.CRAZY_ARCHAEOLOGIST, position) {
    override fun generateAttack(): BossAttack {
        TODO("Not yet implemented")
    }

    override fun attackTypes(): AttackTypeProvider {
        TODO("Not yet implemented")
    }

    override fun maxTargetsHitPerAttack(type: AttackType): Int {
        TODO("Not yet implemented")
    }

    override fun attackRange(type: AttackType): Int {
        TODO("Not yet implemented")
    }
}