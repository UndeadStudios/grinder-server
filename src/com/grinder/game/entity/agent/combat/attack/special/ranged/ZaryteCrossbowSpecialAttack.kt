package com.grinder.game.entity.agent.combat.attack.special.ranged

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.RangedSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.RangedSpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Ammunition
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeaponType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.setBoolean
import com.grinder.game.model.Animation
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Priority

/**
 * @author R-Y-M-R
 * @date 5/26/2022
 * @see <a href="https://www.rune-server.ee/members/necrotic/">RuneServer</a>
 */
class ZaryteCrossbowSpecialAttack : RangedSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.EVOKE

    override fun weaponType() = RangedWeaponType.ZARYTE_CROSSBOW

    override fun secondaryAccuracyModifier(context: AttackContext) = 2.00

    override fun ignoreEnchantedBoltEffect() = false

    /**
     * Sets the ZARYTE_CROSSBOW attribute to true and does super.sequence
     */
    override fun sequence(actor: Agent, target: Agent) {
        if (actor is Player)
            actor.setBoolean(Attribute.ZARYTE_CROSSBOW, true);
        super.sequence(actor, target);
    }

    /**
     * This postHit hook will make *SURE* that the attribute is reset. In my testing it was essential to have this method, or players could have some unexpected results when attacking Dummies.
     */
    override fun postHitEffect(hit: Hit) {
        if (hit.attacker is Player) {
            hit.attacker.attributes.reset(Attribute.ZARYTE_CROSSBOW)
        }
    }

    class Provider : RangedSpecialAttackProvider() {

        override fun fetchProjectiles(type: AttackType, ammunition: Ammunition) = ProjectileTemplate
            .builder(1995)
            .setDepartureSound(Sound(Sounds.CROSSBOW_ATTACK_SOUND))
            .setSourceOffset(0)
            .setDelay(50)
            .setSpeed(8)
            .setStartHeight(38)
            .setEndHeight(38)
            .setCurve(1)
            .buildAsStream()

        override fun fetchHits(type: AttackType?) = HitTemplate
            .builder(type)
            .setDelay(2)
            .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
            Animation(9168, Priority.HIGH) // should be 9168, placeholder: 7552
    }
}