package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.setBoolean
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.util.Priority
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import java.util.*

/**
 * https://oldschool.runescape.wiki/w/Dragon_mace
 *
 * "The dragon mace has a special attack called Shatter,
 * which increases the player's accuracy by 25%
 * and the strength of the player's next hit by 50%,
 * while rolling against the target's crush defence.
 * This attack consumes 25% of the player's special attack energy."
 *
 * TODO: figure out whether the next hit's 50% extra damage applies even if player switches weapon, atm it does
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class DragonMaceSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.SHATTER

    override fun secondaryAccuracyModifier(context: AttackContext) = 1.25

    override fun sequence(actor: Agent, target: Agent) {
        super.sequence(actor, target)
        actor.setBoolean(Attribute.SHATTER_EFFECT, true)
    }

    class Provider : SpecialAttackProvider {

        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(type)
                .setDelay(0)
                .setDefenceStat(EquipmentBonuses.DEFENCE_CRUSH)
                .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
                Animation(1060, Priority.HIGH)

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(251, GraphicHeight.HIGH, Priority.HIGH))

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.DRAGON_MACE_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 5
    }
}
