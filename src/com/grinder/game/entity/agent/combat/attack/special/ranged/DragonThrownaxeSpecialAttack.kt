package com.grinder.game.entity.agent.combat.attack.special.ranged

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.RangedSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.RangedSpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Ammunition
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeaponType
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.util.Priority
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import java.util.*

/**
 * https://oldschool.runescape.wiki/w/Dragon_thrownaxe
 *
 * "The rune thrownaxe has a special attack, Chainhit,
 * that ricochets off the target and hits multiple other opponents in a multi-combat area.
 * This can target up to 5 opponents.
 * Each hit consumes 10% of the player's special attack energy."
 *
 * TODO: see if fighttype (rapid) influence attack duration
 * TODO: find start gfx id
 * TODO: ITS NOT PROPERLY DONE, I HAVE ALL DATA DUMPED PM ME @RACHAEL_ELOU#0001
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class DragonThrownaxeSpecialAttack
    : RangedSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.MOMENTUM_THROW

    override fun weaponType() = RangedWeaponType.THROWING_AXES

    override fun secondaryAccuracyModifier(context: AttackContext) = 1.25

    override fun duration(actor: Agent): Int {
        return if (actor.combat.fightType.toString().toLowerCase().contains("rapid")) {
            1
        } else
            2
    }

    class Provider : RangedSpecialAttackProvider() {

        override fun fetchProjectiles(type: AttackType, ammunition: Ammunition) = ProjectileTemplate
                .builder(1319)
                .setStartHeight(47)
                .setEndHeight(40)
                .setDelay(40)
                .setSpeed(62)
                .setCurve(15)
                .buildAsStream()

        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(type)
                .setDelay(1)
                .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
                Animation(7617, Priority.HIGH)

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(1320, 0, 90, Priority.HIGH))

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.DRAGON_THROWNAXE_SPECIAL_SOUND))
    }
}