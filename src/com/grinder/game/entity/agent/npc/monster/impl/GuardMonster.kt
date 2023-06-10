package com.grinder.game.entity.agent.npc.monster.impl

import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MeleeAttackStrategy
import com.grinder.game.entity.agent.npc.monster.Monster
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Position

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   20/12/2019
 * @version 1.0
 */
class GuardMonster(val player: Player, id: Int, position: Position) : Monster(id, position) {

    override fun sequence() {
        combat.target(player)
        super.sequence()
    }

    override fun attackRange(type: AttackType) = 1

    override fun getAttackStrategy() = MeleeAttackStrategy.INSTANCE

    override fun respawn() {}
}