package com.grinder.game.entity.agent.combat.attack.special.ranged

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.*
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Ammunition
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeaponType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.Skill
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Priority
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * https://oldschool.runescape.wiki/w/Dragon_knife
 *
 * The dragon knife has a special attack, Duality,
 * which consumes 25% of the player's special attack energy.
 * It causes the player to throw two dragon knives at once,
 * with each knife having their own accuracy and damage rolls.
 *
 * This special attack is similar to dragon dagger's,
 * albeit without an extra increase in accuracy and damage.
 *
 * @author  Kyle
 * @since   8/12/2021
 * @version 1.0
 */
class DragonKnifeSpecialAttack : RangedSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.DUALITY
    override fun weaponType() = RangedWeaponType.KNIVES

    override fun sequence(actor: Agent, target: Agent) {
        val ammunition = actor.combat?.ammunition
        if(ammunition == null){
            System.err.println("No ammunition for actor sequencing $this, skipping sequence")
            return
        }

        provider.fetchAttackSound(type())
            .ifPresent {
                if (actor is Player) {
                    when (target) {
                        is Player -> actor.packetSender.sendAreaPlayerSound(it.id, it.delay)
                        else -> actor.packetSender.sendSound(it)
                    }
                }
            }

        val projectiles = provider.fetchProjectiles(type(), ammunition)
            .map { Projectile(actor, target, it)  }
            .collect(Collectors.toList())

        val templateBuilder = HitTemplate
            .builder(AttackType.RANGED)
            .setDelay(0)

        val hits = Array(2) {
            Hit(actor, target, this, templateBuilder.build())
        }

        if(actor.isPlayer) {
            hits.forEach { hit->
                actor.asPlayer.skillManager.addExperience(Skill.RANGED, (hit.totalDamage * 4))
            }
        }

        projectiles.forEach {
            it.sendProjectile()
            it.onArrival {
                hits.forEach(target.combat::queue)
            }
        }

        if (actor is Player) {
            val cost = ammunitionCost()
            RangedAttackStrategy.processAmmunitionProjectile(actor, ammunition, projectiles.first(), cost)
        }
    }

    class Provider : RangedSpecialAttackProvider() {

        override fun fetchProjectiles(type: AttackType, ammunition: Ammunition): Stream<ProjectileTemplate> = ProjectileTemplate
            .builder(699)
            .setSourceSize(1)
            .setSourceOffset(0)
            .setStartHeight(45)
            .setSpeed(35)
            .setDelay(20)
            .setCurve(15)
            .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) = Animation(8291, Priority.HIGH)
        override fun fetchAttackSound(type: AttackType?) = Optional.of(Sound(Sounds.RUNE_THROWNAXE_SPECIAL_SOUND, 0))

    }

}