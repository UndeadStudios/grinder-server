package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.util.Priority
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import java.util.*

/**
 * https://oldschool.runescape.wiki/w/Abyssal_dagger
 *
 * "The abyssal dagger has a special attack, Abyssal Puncture,
 * which hits twice in quick succession with a 25% increase in accuracy
 * and 15% reduced damage, consuming 50% of the player's special attack energy.
 * A single attack roll is performed, meaning that either both hits are successful,
 * or both hits miss."
 *
 * TODO: find sound id
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   12/05/2020
 * @version 1.0
 */
class AbyssalDaggerSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.ABYSSAL_PUNCTURE

    override fun secondaryAccuracyModifier(context: AttackContext) = 1.25

    override fun secondaryDamageModifier(context: AttackContext) = 0.85

    override fun createHits(actor: Agent, target: Agent): Array<Hit> {
        val hit1 = Hit(actor, target, this, true, 0)
        val hit2 = Hit(actor, target, this, false, 0)
        if (hit1.missed()) {
            hit2.totalDamage = 0
            hit2.isAccurate = false
        }
        return arrayOf(hit1, hit2)
    }

    class Provider : SpecialAttackProvider {

        override fun getAttackAnimation(type: AttackType?) =
                Animation(3300, Priority.HIGH)

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(1283, Priority.HIGH))

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.DRAGON_DAGGER_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 4
    }

}