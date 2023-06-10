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
import com.grinder.game.entity.agent.player.playAreaSound
import com.grinder.game.entity.agent.player.playSound
import com.grinder.game.model.Animation
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplateBuilder
import com.grinder.util.Priority

/**
 * https://oldschool.runescape.wiki/w/Magic_longbow
 * https://oldschool.runescape.wiki/w/Magic_comp_bow
 *
 * "The magic longbow's special attack, Powershot, is guaranteed to hit the target.
 * This consumes 35% of the player's special attack energy.
 * This special attack is also shared with the magic comp bow."
 *
 * TODO: find correct start gfx
 * TODO: check if sound is correct (currently same as [MagicShortbowSpecialAttack])
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class MagicLongbowSpecialAttack
    : RangedSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.POWERSHOT

    override fun weaponType() = RangedWeaponType.LONGBOWS

    override fun sequence(actor: Agent, target: Agent) {
        val builder = ProjectileTemplateBuilder(249)
                .setSourceSize(1)
                .setSourceOffset(0)
                .setDelay(50)
                .setSpeed(60)
                .setStartHeight(43)
                .setEndHeight(target, 0.8)
                .setCurve(5)

        val p1 = Projectile(actor, target, builder.build())
        p1.sendProjectile()

        if(actor is Player) {
            val ammunition = actor.combat.ammunition
            val cost = ammunitionCost()
            RangedAttackStrategy.processAmmunitionProjectile(actor, ammunition, p1, cost)
            if(target is Player){
                actor.playAreaSound(2545, 12, 1, 0)
            } else {
                actor.playSound(2545, 0)
            }
        }
    }

    override fun createHits(actor: Agent, target: Agent): Array<Hit> {

        val builder =  HitTemplate
                .builder(AttackType.RANGED)
                .setIgnoreAttackStats(true)
                .setDelay(RangedAttackStrategy
                    .getHitDelay(RangedWeapon.MAGIC_LONGBOW, actor, target))

        return Array(1) {
            Hit(actor, target, this, builder.build())
        }
    }

    class Provider : RangedSpecialAttackProvider() {

        override fun getAttackAnimation(type: AttackType?) =
                Animation(1074, Priority.HIGH)
    }
}