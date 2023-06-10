package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Priority
import java.util.*

/**
 * This is a custom special attack for a custom weapon.
 *
 * TODO: find a nice sound
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   12/05/2020
 * @version 1.0
 */
class GoldWhipSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.GOLDEN_WINDS

    override fun secondaryAccuracyModifier(context: AttackContext) = 1.25

    override fun secondaryDamageModifier(context: AttackContext) = 1.35

    class Provider : SpecialAttackProvider {

        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(type)
                .setIgnoreAttackStats(true)
                .setDelay(0)
                .setSuccessOrFailedGraphic(Graphic(284, GraphicHeight.MIDDLE, Priority.HIGH))
                .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
                Animation(1658, Priority.HIGH)

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.GOLD_WHIP_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 4
    }
}