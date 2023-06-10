package com.grinder.game.entity.agent.combat.attack.special.ranged

import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.RangedSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.RangedSpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Ammunition
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeaponType
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.model.Animation
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Priority
import java.util.*

/**
 * https://oldschool.runescape.wiki/w/Armadyl_crossbow
 *
 * "The Armadyl crossbow has a special attack, Armadyl Eye,
 * which doubles the player's overall accuracy (level + gear accuracy) for that shot,
 * consuming 40% of the player's special attack energy."
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class ArmadylCrossbowSpecialAttack
    : RangedSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.ARMADYL_EYE

    override fun weaponType() = RangedWeaponType.ARMADYL_CROSSBOW

    override fun secondaryAccuracyModifier(context: AttackContext) = 2.00

    override fun ignoreEnchantedBoltEffect() = false

    class Provider : RangedSpecialAttackProvider() {

        override fun fetchProjectiles(type: AttackType, ammunition: Ammunition) = ProjectileTemplate
                .builder(301)
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
                Animation(7552, Priority.HIGH)

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.ARMADYL_CROSSBOW_SPECIAL_SOUND))
    }
}