package com.grinder.game.entity.agent.npc.monster.boss.minion

import com.grinder.game.World
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.npc.monster.Monster
import com.grinder.game.entity.agent.npc.monster.MonsterEvents
import com.grinder.game.entity.agent.npc.monster.aggression.MonsterAggressionProcess.Companion.getAggressionDistance
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.impl.KalphiteQueenBoss
import com.grinder.game.model.Position
import java.util.*

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/08/2020
 */
open class BossMinion<B : Boss>(
    protected val bossNPC: B,
    id: Int,
    position: Position,
    private vararg val policies: BossMinionPolicy
) : Monster(id, position) {

    init {

        if (position.z != bossNPC.position.z)
            System.err.println("$this was created at a height different from their boss!")

        onEvent {
            if (it == MonsterEvents.POST_SEQUENCE) {
                if (!bossNPC.isActive || bossNPC.isDying) {
                    debug("boss is not registered or dying!")
                    if (hasPolicy(BossMinionPolicy.REMOVE_WHEN_BOSS_REMOVED)) {
                        motion.resetTargetFollowing()
                        combat.reset(true)
                        World.npcRemoveQueue.add(this)
                    }
                } else if (!combat.isInCombat) {
                    debug("not in combat yet!")
                    if (bossNPC.bossAttack.hasPreferredType()) {
                        if (hasPolicy(BossMinionPolicy.ATTACK_PREFERRED_OPPONENT)) {
                            val optionalAgent = bossNPC.combat.findPreferredOpponent()
                            if (optionalAgent.isEmpty) {
                                val aggressionDistance = getAggressionDistance(this)
                                bossNPC.combat.findCurrentEmptyOrClosestTarget(aggressionDistance)
                                        .ifPresentOrElse({ target ->
                                            combat.initiateCombat(target, !motion.movementDisabled())
                                        }) {
                                            debug("could not find opponent in aggression range")
                                        }
                            } else combat.initiateCombat(optionalAgent.get())
                        }
                    }
                }
            }
        }
    }


    override fun respawn() {
        if (hasPolicy(BossMinionPolicy.NO_RESPAWN))
            return
        super.respawn()
    }

    override fun attackRange(type: AttackType): Int {
        return Optional.ofNullable(attackStrategy)
                .map { it.requiredDistance(this) }
                .orElse(-1)
    }

    private fun hasPolicy(policy: BossMinionPolicy) = policies.contains(policy)
}