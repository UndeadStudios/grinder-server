package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.model.Animation
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate

/**
 * Handles the thrower troll combat.
 */
class TorcherAttack : AttackStrategy<NPC> {

    override fun type() = AttackType.RANGED

    override fun createHits(actor: NPC, target: Agent): Array<Hit> {
        return arrayOf(Hit(actor, target, this, true, 2))
    }

    override fun sequence(actor: NPC, target: Agent) {
        Projectile(actor, target, PROJECTILE).sendProjectile()
    }

    override fun duration(actor: NPC) = actor.baseAttackSpeed

    override fun requiredDistance(actor: Agent) = 12

    override fun animate(actor: NPC) {
        val animation = actor.attackAnim
        if (animation != -1)
            actor.performAnimation(Animation(animation))
    }

    companion object {
        private val PROJECTILE = ProjectileTemplate
                .builder(646)
                .setDelay(55)
                .setSpeed(90)
                .setHeights(31, 31)
                .setCurve(0)
                .build()
    }
}