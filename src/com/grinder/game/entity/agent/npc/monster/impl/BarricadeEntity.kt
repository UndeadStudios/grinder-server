package com.grinder.game.entity.agent.npc.monster.impl

import com.grinder.game.collision.CollisionManager
import com.grinder.game.content.minigame.castlewars.CastleWars
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.event.CombatState
import com.grinder.game.entity.agent.combat.subscribe
import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.npc.monster.Monster
import com.grinder.game.entity.agent.npc.monster.MonsterEvents
import com.grinder.game.model.Position

/**
 * Represents a rock crab [Monster]. These crabs are initially spawned in a 'hidden' form.
 * Once they are attacked or a player is within 1 tile distance of the crab, it transforms
 * into its 'active' form.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   16/12/2020
 * @version 1.0
 */
class BarricadeEntity(id: Int, position: Position?) : Monster(id, position!!) {
    init {
        motion.update(MovementStatus.DISABLED)

        onEvent {
            if (it == MonsterEvents.DYING){
                if (position != null) {
                    CollisionManager.removeClipping(
                        position.x,
                        position.y,
                        position.z,
                        CollisionManager.BLOCKED_TILE
                    )
                }
            }
        }
    }

    override fun attackRange(type: AttackType) = 1
}