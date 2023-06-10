@file:JvmName("DragonfireAttack")
package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster

import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.event.impl.DragonFireEvent
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplateBuilder
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.projectile.ProjectileTemplateBuilder
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc

/**
 * Represents the dragonfire attack
 *
 * Performs either a close range dragon fire attack, or long range.
 */
class DragonFireAttack(val range: Boolean): AttackStrategy<Agent> {

    companion object {
        // This the animation used for both long range, and short ranged.
        val DragonfireAnimation = Animation(81)
        val DragonfireGfx = Graphic(1, GraphicHeight.MIDDLE)

        // Long range dragonfire attack
        val LongRange = DragonFireAttack(true)
        // short range dragonfire attack. Melee distance.
        val closeRange = DragonFireAttack(false)

        /**
         * Determines if the dragonfire attack should be long range, or short range. Mostly used for metal dragons.
         */
        fun variableDragonfireStrategy(actor: Agent?, target: Agent?): AttackStrategy<Agent> {
            if (DistanceUtil.isWithinDistance(actor, target, 1))
                return closeRange
            return LongRange
        }
    }

    override fun animate(actor: Agent) {
        actor.performAnimation(DragonfireAnimation)
        if (!range)
            actor.performGraphic(DragonfireGfx)
    }

    override fun postHitAction(actor: Agent, target: Agent) {
        target.combat.submit(DragonFireEvent())
    }

    override fun sequence(actor: Agent, target: Agent) {
        if (range) {
        val builder = ProjectileTemplate
            .builder(54)
            .setCurve(3)
            .setHeights(24, 42)
            .setSpeed(if (target != null) (1 + (DistanceUtil.getChebyshevDistance(actor.position, target.position) * 3).coerceAtMost(50)) else 1)
            .setDelay(40)
        Projectile(actor, target, builder.build()).sendProjectile()
        }
    }

    override fun postHitEffect(hit: Hit) {
        if (hit.target!!.isPlayer) {
            hit.target.asPlayer!!.packetSender.sendSound(3750)
        }
        hit.target.messageIfPlayer(when {
            hit.totalDamage < 0 -> "You're protected from the dragon breath!"
            hit.totalDamage <= 25 ->"The dragon fire burns you!"
            else -> "You're badly burnt by the dragon fire!"
        }, 1000)
    }

    override fun duration(actor: Agent) = 6

    override fun requiredDistance(actor: Agent) = if (range) 8 else 1

    override fun createHits(actor: Agent, target: Agent): Array<Hit> {
        val builder = HitTemplateBuilder(AttackType.SPECIAL).setDelay(if (target == null) 2 else MagicAttackStrategy.getMagicSpellHitDelay(actor, target))
        val hit = Hit(actor, target, this, builder.build())
        if (target is Player) {
            var extendedHit = 46
            if (EquipmentUtil.isWearingDragonFireProtection(target))
                extendedHit -= 40
            if (!target.combat.fireImmunityTimer.finished() || !target.combat.superFireImmunityTimer.finished())
                extendedHit -= 32
            if (PrayerHandler.isActivated(target, PrayerHandler.PROTECT_FROM_MAGIC))
                extendedHit -= 23
            if (extendedHit > 0)
                hit.totalDamage = Misc.random(0, extendedHit)
            else
                hit.totalDamage = 0
        }
        return arrayOf(hit)
    }

    override fun type() = AttackType.SPECIAL
}