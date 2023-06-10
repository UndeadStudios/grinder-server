package com.grinder.game.entity.agent.combat.attack.special.ranged

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.RangedSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.RangedSpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeapon
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeaponType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.game.entity.agent.player.playAreaSound
import com.grinder.game.entity.agent.player.playSound
import com.grinder.game.model.Animation
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplateBuilder
import com.grinder.util.ItemID
import com.grinder.util.Priority

/**
 * https://oldschool.runescape.wiki/w/Magic_shortbow
 * https://oldschool.runescape.wiki/w/Magic_shortbow_(i)
 *
 * "This weapon retains the standard magic shortbow's special attack, Snapshot,
 * which fires two arrows in rapid succession but with lowered accuracy,
 * using 50% (55% for the regular shortbow) of the player's special attack energy.
 * With perfect timing, this bow can hit three times in one game tick."
 *
 * TODO: find correct start gfx
 * TODO: figure out by how much the accuracy is lowered
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class MagicShortbowSpecialAttack
    : RangedSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.SNAPSHOT

    override fun weaponType() = RangedWeaponType.SHORTBOWS

    override fun ammunitionCost() = 2

    override fun drainAmount(actor: Agent): Int {
        if(actor is Player){
            val weapon = actor.equipment[EquipmentConstants.WEAPON_SLOT]
            if(weapon?.id == ItemID.MAGIC_SHORTBOW_I_ || weapon?.id == 15433)
                return 50
        }
        return 55
    }

    override fun sequence(actor: Agent, target: Agent) {
        val builder = ProjectileTemplateBuilder(249)
                .setSourceSize(1)
                .setSourceOffset(0)
                .setDelay(50)
                .setSpeed(-2)
                .setStartHeight(43)
                .setEndHeight(target, 0.8)
                .setCurve(5)

        val p1 = Projectile(actor, target, builder.build())
        builder.setDelay(30)
        builder.setSpeed(0)
        val p2 = Projectile(actor, target, builder.build())
        p1.sendProjectile()
        p2.sendProjectile()

        if(actor is Player) {
            val ammunition = actor.combat.ammunition
            val cost = ammunitionCost()
            RangedAttackStrategy.processAmmunitionProjectile(actor, ammunition, p1, cost)
            if(target is Player){
                actor.playAreaSound(2545, 12, 1, 0)
                //actor.playAreaSound(2545, 12, 1, 35)
            } else {
                actor.playSound(2545, 0)
                //actor.playSound(2545, 35)
            }
        }
    }

    override fun createHits(actor: Agent, target: Agent): Array<Hit> {

        val builder = HitTemplate
                .builder(AttackType.RANGED)
                .setDelay(RangedAttackStrategy
                    .getHitDelay(RangedWeapon.MAGIC_SHORTBOW, actor, target))

        return Array(2) {
            Hit(actor, target, this, builder.build())
        }
    }

    class Provider : RangedSpecialAttackProvider() {

        override fun getAttackAnimation(type: AttackType?) =
                Animation(1074, Priority.HIGH)
    }
}