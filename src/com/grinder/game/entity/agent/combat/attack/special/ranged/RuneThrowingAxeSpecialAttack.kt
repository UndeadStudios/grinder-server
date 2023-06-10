package com.grinder.game.entity.agent.combat.attack.special.ranged

import com.grinder.game.collision.CollisionPolicy
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.AgentUtil
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.combat.attack.special.RangedSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.RangedSpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeaponType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.util.Priority
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplateBuilder
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * https://oldschool.runescape.wiki/w/Rune_thrownaxe
 *
 * "The rune thrownaxe has a special attack, Chainhit,
 * that ricochets off the target and hits multiple other opponents in a multi-combat area.
 * This can target up to 5 opponents.
 * Each hit consumes 10% of the player's special attack energy."
 *
 * TODO: make start gfx into rune throwing axe (client doesn't colour it I think)
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class RuneThrowingAxeSpecialAttack
    : RangedSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.CHAINHIT

    override fun weaponType() = RangedWeaponType.THROWING_AXES

    override fun sequence(actor: Agent, target: Agent) {
        val template = ProjectileTemplateBuilder(258)
                .setSourceSize(1)
                .setSourceOffset(0)
                .setDelay(30)
                .setSpeed(58)
                .setStartHeight(42)
                .setEndHeight(target, 0.8)
                .setCurve(3)


        val hitCount = AtomicInteger(0)

        val targets = AgentUtil
                .getAgentsInProximity(target.position, target.size, 6, CollisionPolicy.PROJECTILE)
                .filter { it != actor && it != target }
                .limit(4)
                .collect(Collectors.toSet())

        val projectile = sendProjectile(template, targets, actor, target, actor, hitCount)

        if(actor is Player) {
            val ammunition = actor.combat.ammunition
            val cost = ammunitionCost()
            RangedAttackStrategy.processAmmunitionProjectile(actor, ammunition, projectile, cost)
        }
    }

    private fun sendProjectile(template: ProjectileTemplateBuilder, targets: MutableSet<Agent>, source: Agent, target: Agent, actor: Agent, hitCount: AtomicInteger): Projectile {

        val p1 = Projectile(source, target, template.build())

        p1.sendProjectile()
        p1.onArrival {

            val hit1 = Hit(actor, target, this, HitTemplate
                    .builder(AttackType.RANGED)
                    .setDelay(0)
                    .build(), true)

            targets.remove(target)

            hitCount.incrementAndGet()

            actor.combat.queueOutgoingHit(hit1)

            if(actor.specialPercentage < 10)
                return@onArrival

            SpecialAttackType.drain(actor, 10)

            val nextTarget = targets
                .filter { it.isAlive && actor.position.isWithinDistance(it.position, 20) }
                .filter { AreaManager.inMulti(it) && AreaManager.canAttack(actor, it) }
                .minByOrNull { it.position.getDistance(p1.target) } ?: return@onArrival

            template.setDelay(4)
            template.setSpeed(34)
            template.setCurve(0)
            template.setStartHeight(p1.endHeight)
            sendProjectile(template, targets, target, nextTarget, actor, hitCount)
        }

        return p1
    }

    class Provider : RangedSpecialAttackProvider() {

        override fun getAttackAnimation(type: AttackType?) =
                Animation(1068, Priority.HIGH)

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(257, 0, 90))

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.RUNE_THROWNAXE_SPECIAL_SOUND))
    }
}