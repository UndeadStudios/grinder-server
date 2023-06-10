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
import com.grinder.util.Priority
import com.grinder.game.model.sound.Sound
import java.util.*

/**
 * https://oldschool.runescape.wiki/w/Vesta%27s_longsword
 *
 * "Vesta's longsword has a special attack, Feint,
 * that consumes 25% of the player's special attack energy
 * and deals between 20% and 120% of the user's max hit,
 * with the accuracy of this attack rolled against 25% of the opponent's defence."
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class VestaLongswordSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.FEINT

    override fun secondaryAccuracyModifier(context: AttackContext) = 1.25

    override fun createHits(actor: Agent, target: Agent): Array<Hit> {

        val hit = Hit(actor, target, this, HitTemplate
                .builder(AttackType.MELEE)
                .setDelay(0)
                .build(), false)

        val maxHit = hit.maxHit
        val minHit = maxHit.times(0.20).toInt()
        val newMaxHit = maxHit.times(1.20).toInt()

        hit.setDamageRange(minHit..newMaxHit)
        hit.multiplyDefenceRoll(0.25)
        hit.createHits(1)

        return arrayOf(hit)
    }

    class Provider : SpecialAttackProvider {

        override fun getAttackAnimation(type: AttackType?) =
                Animation(7515, Priority.HIGH)

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(2765))

        override fun fetchAttackDuration(type: AttackType?)
                = 5
    }
}
