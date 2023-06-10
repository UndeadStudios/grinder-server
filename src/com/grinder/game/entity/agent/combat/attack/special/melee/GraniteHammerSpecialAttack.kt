package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.util.Priority
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import java.util.*

/**
 * https://oldschool.runescape.wiki/w/Granite_hammer
 *
 * "The granite hammer has a special attack, Hammer Blow,
 * which consumes 60% of the player's special attack energy,
 * and deals an attack with 50% increased accuracy, while guaranteeing 5 additional damage to that attack."
 *
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   12/05/2020
 * @version 1.0
 */
class GraniteHammerSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun secondaryAccuracyModifier(context: AttackContext) = 1.5

    override fun special() = SpecialAttackType.HAMMER_BLOW

    override fun createHits(actor: Agent, target: Agent): Array<Hit> {
        val hit = Hit(actor, target, this, true, 1, 0)

        return if (hit.missed()) {
            arrayOf(Hit(actor, target, this, HitTemplate
                    .builder(AttackType.MELEE)
                    .setDamageRange(5..5)
                    .build()))
        } else {
            hit.totalDamage += 5
            arrayOf(hit)
        }
    }

    class Provider : SpecialAttackProvider {

        override fun getAttackAnimation(type: AttackType?) =
                Animation(1378, 0, 2, Priority.HIGH)

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(1450, Priority.HIGH))

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.GRANITE_HAMMER_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 4
    }

}