package com.grinder.game.entity.agent.npc.monster.boss

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.Attack
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType

/**
 * Represents an [Attack] to be carried about by [Boss] monsters.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-26
 *
 * @param provider the [AttackProvider] providing details.
 */
open class BossAttack(provider: AttackProvider) : Attack<Boss>(provider) {

    private var preferredType: AttackType? = null

    override fun requiredDistance(actor: Agent): Int {
        if (actor.isNpc && actor != null) {
            if (actor.asNpc.movementCoordinator.isRetreating) {
                return 0
            }
        }
        if (preferredType == null) {
            //System.err.println("BossAttack: preferred type was not initialised for $actor")
            return 1
        }
        return if (actor is Boss)
            actor.attackRange(preferredType!!)
        else
            0
    }

    final override fun type() = preferredType ?: AttackType.MELEE

    /**
     * Check whether the [preferredType] field is initialised.
     *
     * @return 'true' if [preferredType] is initialised,
     *          'false' if not.
     */
    fun hasPreferredType() = preferredType != null

    fun setType(type: AttackType) {
        preferredType = type
    }
}
