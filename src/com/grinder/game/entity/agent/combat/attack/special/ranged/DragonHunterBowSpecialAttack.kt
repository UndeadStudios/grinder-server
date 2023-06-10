package com.grinder.game.entity.agent.combat.attack.special.ranged

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.combat.attack.special.RangedSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.RangedSpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Ammunition
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeaponType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.playAreaSound
import com.grinder.game.entity.agent.player.playSound
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.util.Priority
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplateBuilder
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Misc
import com.grinder.util.TaskFunctions.delayBy

/**
 * https://oldschool.runescape.wiki/w/Dark_bow
 *
 * "The dark bow has a unique special attack comprised of two different attacks,
 * both of which consume 55% of the player's special attack energy"
 *
 * "The 1st special attack, Descent of Darkness,
 * deals up to 30% more damage with a minimum of 5 damage per arrow.
 * The animation shows two arrows fired at the target,
 * surrounded in a black shroud."
 *
 * "If the player has dragon arrows equipped, the 2nd special attack,
 * Descent of Dragons, is used instead.
 * This attack deals up to 50% more damage with a minimum of 8 damage per arrow,
 * while capping the max hit at 48 damage per arrow.
 * The animation shows two black dragon heads fired at the target."
 *
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class DragonHunterBowSpecialAttack
    : RangedSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.DESCENT_OF_DARKNESS_OR_DRAGONS

    override fun weaponType() = RangedWeaponType.DRAGON_HUNTER_BOW

    override fun secondaryDamageModifier(context: AttackContext) = if (context.usedAny(*Ammunition.DRAGON_ARROWS))
        1.50 else 1.30

    override fun ammunitionCost() = 2

    override fun sequence(actor: Agent, target: Agent) {
        val builder = ProjectileTemplateBuilder(631)
                .setSourceSize(1)
                .setSourceOffset(0)
                .setDelay(55)
                .setSpeed(13)
                .setStartHeight(45)
                .setEndHeight(target, 0.8)
                .setCurve(12)

        val p1 = Projectile(actor, target, builder.build())
        builder.setDelay(40)
        builder.setSpeed(8)
        val p2 = Projectile(actor, target, builder.build())
        p1.sendProjectile()
        p2.sendProjectile()

        p1.onArrival {
            target.performGraphic(Graphic(155, GraphicHeight.MIDDLE, Priority.HIGH))
            delayBy(3) {
                target.performGraphic(Graphic(157, GraphicHeight.MIDDLE, Priority.HIGH))
                target.combat.queue(Damage(Misc.random(10), DamageMask.REGULAR_HIT));
            }
            if(actor is Player){
                if(target is Player){
                    actor.playAreaSound(3737, 12, 1, 0)
                    actor.playAreaSound(3737, 12, 1, 35)
                } else {
                    actor.playSound(3737, 0)
                    actor.playSound(3737, 35)
                }
            }
        }

        if(actor is Player) {
            val ammunition = actor.combat.ammunition
            val cost = ammunitionCost()
            RangedAttackStrategy.processAmmunitionProjectile(actor, ammunition, p1, cost)
            if(target is Player){
                actor.playAreaSound(Sounds.DARK_BOW_SPECIAL_SOUND, 12, 1, 0)
                actor.playAreaSound(Sounds.DARK_BOW_SPECIAL_SOUND_2, 12, 1, 35)
            } else {
                actor.playSound(Sounds.DARK_BOW_SPECIAL_SOUND, 0)
                actor.playSound(Sounds.DARK_BOW_SPECIAL_SOUND_2, 35)
            }
        }
    }

    override fun createHits(actor: Agent, target: Agent): Array<Hit> {

        val builder =  HitTemplate
                .builder(AttackType.RANGED)
                .setDelay(2)

        if(Ammunition.DRAGON_ARROWS.contains(actor.combat.ammunition))
            builder.setDamageRange(0..48)
        else
            builder.setDamageRange(0..48)

        return Array(2) {
            Hit(actor, target, this, builder.build())
        }
    }

    override fun onHit(actor: Agent, target: Agent) {

    }

    class Provider : RangedSpecialAttackProvider() {

        override fun getAttackAnimation(type: AttackType?) =
                Animation(426, Priority.HIGH)
    }
}