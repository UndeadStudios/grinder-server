package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.util.Priority
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import java.util.*
import kotlin.random.Random

/**
 * This is a custom special attack for a custom weapon.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   12/05/2020
 * @version 1.0
 */
class DragonWhipSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.DRAGON_TENTACLE

    override fun secondaryAccuracyModifier(context: AttackContext) = 1.05

    override fun secondaryDamageModifier(context: AttackContext) = 1.35

    override fun createHits(actor: Agent, target: Agent): Array<Hit> {
        val hit1 = Hit(actor, target, this, true, 1)
        val hit2 = Hit(actor, target, this, true, 3)
        val hit3 = Hit(actor, target, this, HitTemplate
                .builder(AttackType.MELEE)
                .setIgnoreAttackStats(true)
                .setIgnoreStrengthStats(true)
                .setDamageRange(2..2)
                .setDelay(2)
                .build())
        if (target is NPC) {
            hit2.damages.first()?.also { damage ->
                damage.incrementDamage(25 + Random.nextInt(25))
            }
        } else {
            hit1.multiplyDamage(0.75)
            hit2.multiplyDamage(0.65)
            hit3.multiplyDamage(0.70)
        }
        return arrayOf(hit1, hit2, hit3)
    }

    override fun onHit(actor: Agent, target: Agent) {
        target.performGraphic(Graphic(547, 80, GraphicHeight.LOW, Priority.HIGH))
    }

    class Provider : SpecialAttackProvider {

        override fun getAttackAnimation(type: AttackType?) =
                Animation(1658, Priority.HIGH)

        override fun fetchProjectiles(type: AttackType?) = ProjectileTemplate
                .builder(542)
                .setStartHeight(35)
                .setEndHeight(5)
                .setCurve(30)
                .setSpeed(70)
                .setDelay(40)
                .buildAsStream()

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.DRAGON_WHIP_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 4
    }
}