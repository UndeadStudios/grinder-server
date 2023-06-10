package com.grinder.game.entity.agent.combat.attack.special.magic

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpellType
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.*
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Priority
import java.util.*

/**
 * This is a custom special attack for a custom weapon.
 * This special uses up 50% of the player's special attack meter.
 *
 * TODO: find sound id to use
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class DragonGodswordSpecialAttack
    : SpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.FLAME_OF_THE_GODS

    override fun type() = AttackType.MAGIC

    override fun requiredDistance(actor: Agent) = 6

    override fun secondaryAccuracyModifier(context: AttackContext) = 2.0

    override fun createHits(actor: Agent, target: Agent): Array<Hit> {

        val hit = Hit(actor, target, this, HitTemplate
                .builder(AttackType.MAGIC)
                .setAttackStat(EquipmentBonuses.ATTACK_MAGIC)
                .setIgnorePrayer(true)
                .setDelay(1)
                .setSuccessGraphic(Graphic(1154, GraphicHeight.LOW, Priority.HIGH))
                .setFailedGraphic(Graphics.SPLASH_GRAPHIC)
                .build(), false)

        hit.context.setSpell(CombatSpellType.FIRE_SURGE)
        hit.createHits(4)

        val projectile = Projectile(actor, target, ProjectileTemplate
                .builder(393)
                .setSourceOffset(0)
                .setDelay(43)
                .setSpeed(7)
                .setStartHeight(50)
                .setEndHeight(42)
                .setCurve(2)
                .build())

        val projectile2 = Projectile(actor, target, ProjectileTemplate
            .builder(394)
            .setSourceOffset(0)
            .setDelay(33)
            .setSpeed(7)
            .setStartHeight(50)
            .setEndHeight(42)
            .setCurve(2)
            .build())

        val projectile3 = Projectile(actor, target, ProjectileTemplate
            .builder(395)
            .setSourceOffset(0)
            .setDelay(22)
            .setSpeed(7)
            .setStartHeight(50)
            .setEndHeight(42)
            .setCurve(2)
            .build())

        val projectile4 = Projectile(actor, target, ProjectileTemplate
            .builder(396)
            .setSourceOffset(0)
            .setDelay(10)
            .setSpeed(7)
            .setStartHeight(50)
            .setEndHeight(42)
            .setCurve(2)
            .build())

        projectile.sendProjectile()
        projectile2.sendProjectile()
        projectile3.sendProjectile()
        projectile4.sendProjectile()

        projectile3.onArrival {
            if (target!!.isUntargetable || target!!.isTeleporting)
                return@onArrival
            target.performGraphic(Graphic(1154, GraphicHeight.LOW, Priority.HIGH))
            //target!!.combat.queue(hit)
        }
        return arrayOf(hit)
    }

    class Provider : SpecialAttackProvider {

        override fun getAttackAnimation(type: AttackType?) =
                Animation(2927, Priority.HIGH)

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(1173, 30, GraphicHeight.HIGH, Priority.HIGH))

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.DRAGON_GODSWORD_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 6
    }
}