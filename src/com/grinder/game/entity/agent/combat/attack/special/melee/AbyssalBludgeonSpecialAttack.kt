package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.entity.agent.combat.formula.CombatFormulaType
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.util.Priority
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import java.util.*

/**
 * https://oldschool.runescape.wiki/w/Abyssal_bludgeon
 *
 * "The abyssal bludgeon has a special attack, Penance,
 * which increases damage dealt by 0.5%  for every
 * prayer point that the wielder is missing,
 * consuming 50% of the wielder's special attack energy."
 *
 * @see CombatFormulaType.MELEE.calculateStrength for prayer point effect
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   12/05/2020
 * @version 1.0
 */
class AbyssalBludgeonSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.PENANCE

    class Provider : SpecialAttackProvider {

        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(type)
                .setDelay(0)
                .setSuccessOrFailedGraphic(Graphic(1284, Priority.HIGH))
                .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
                Animation(3299, Priority.HIGH)

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.ABYSSAL_BLUDGEON_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 4
    }
}