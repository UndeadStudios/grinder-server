package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster

import com.grinder.game.content.`object`.DwarfMulticannon
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.model.Animation
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.util.DistanceUtil
import java.util.stream.Stream

class AviansieAttack : AttackStrategy<NPC> {

    override fun type() = AttackType.RANGED

    override fun createHits(actor: NPC, target: Agent): Array<Hit> {
        return arrayOf(Hit(actor, target, this, true, if (actor.combat.target == null) 0 else RangedAttackStrategy.getNPCRangeHitDelay(actor, actor.combat.target)))
    }

    override fun animate(actor: NPC) {
        val animation = actor.attackAnim
        if (animation != -1)
            actor.performAnimation(Animation(animation))
    }

    override fun sequence(actor: NPC, target: Agent) {
        val projBldr = ProjectileTemplate.builder(1192)
            .setSourceOffset(2)
            .setStartHeight(144)
            .setEndHeight(11)
            .setCurve(280)
            .setSpeed(if (actor.combat.target != null) (10 + (DistanceUtil.getChebyshevDistance(actor.position, actor.combat.target.position) * 3).coerceAtMost(30)) else 10)
            .setDelay(35)
            .setCurve(2)

        val projectile = Projectile(actor, target, projBldr.build())
        projectile.sendProjectile()
    }

    override fun duration(actor: NPC) = actor.baseAttackSpeed

    override fun requiredDistance(actor: Agent) = 7

}