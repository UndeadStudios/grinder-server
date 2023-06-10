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
import com.grinder.util.Priority
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import java.util.*

/**
 * see https://oldschool.runescape.wiki/w/Heavy_ballista#Special_attack
 *
 * "The heavy ballista has a special attack, Concentrated Shot,
 * which it shares with the light ballista.
 * It performs an attack with 25% increased accuracy and damage."
 *
 * TODO: check if projectile id is the same for each type of ammunition
 * TODO: find hit graphic
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class BallistaSpecialAttack
    : RangedSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.CONCENTRATED_SHOT

    override fun weaponType() = RangedWeaponType.BALLISTA

    override fun secondaryAccuracyModifier(context: AttackContext) = 1.25

    override fun secondaryDamageModifier(context: AttackContext) = 1.25

    class Provider : RangedSpecialAttackProvider() {

        override fun fetchProjectiles(type: AttackType, ammunition: Ammunition) = ProjectileTemplate
                .builder(ammunition.projectileId)
                .setDelay(50)
                .setSpeed(8)
                .setCurve(3)
                .buildAsStream()

        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(type)
                .setDelay(2)
                .buildAsStream()

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.BALLISTA_SPECIAL_SOUND))

        override fun getAttackAnimation(type: AttackType?) =
                Animation(7222, Priority.HIGH)
    }
}