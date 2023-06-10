package com.grinder.game.entity.agent.combat.attack

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.hit.Hit

/**
 * Represents a strategy for attacking a target.
 *
 * @author Stan van der Bend
 */
interface AttackStrategy<T : Agent> {

    fun animate(actor: T) {}
    fun sequence(actor: T, target: Agent) {}

    @Deprecated("Use OutgoingHitApplied or OutgoingHitQueued event!")
    fun postHitAction(actor: T, target: Agent) {}

    @Deprecated("Use OutgoingHitApplied or OutgoingHitQueued event!")
    fun postHitEffect(hit: Hit) {}

    @Deprecated("Use IncomingHitApplied or IncomingHitQueued event!")
    fun postIncomingHitEffect(hit: Hit) {}

    fun duration(actor: T): Int
    fun requiredDistance(actor: Agent): Int

    /**
     * canAttack determines if the attacker is able to attack the other actor.
     * @param actor The actor attacking.
     * @param target The actor targted.
     * @return Whether you may perform the attack.
     */
    fun canAttack(actor: T, target: Agent): Boolean {
        //actor.positionToFace = target.position
        return true
    }

    fun createHits(actor: T, target: Agent): Array<Hit>
    fun type(): AttackType?
}