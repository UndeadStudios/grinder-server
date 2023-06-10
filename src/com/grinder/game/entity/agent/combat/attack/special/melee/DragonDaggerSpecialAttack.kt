package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.util.Priority
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import java.util.*

/**
 * https://oldschool.runescape.wiki/w/Dragon_dagger
 *
 * "The dragon dagger's special attack, Puncture,
 * deals two hits at once with an extra 15% accuracy and 15% damage
 * for each of the two hits, consuming 25% of the wielder's special attack energy.
 * Puncture rolls against opponent's slash defence roll."
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   12/05/2020
 * @version 1.0
 */
class DragonDaggerSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.PUNCTURE

    override fun secondaryAccuracyModifier(context: AttackContext) = 1.15

    override fun secondaryDamageModifier(context: AttackContext) = 1.15

    override fun createHits(actor: Agent, target: Agent): Array<Hit> {
        val templateBuilder = HitTemplate
                .builder(AttackType.MELEE)
                .setDefenceStat(EquipmentBonuses.DEFENCE_SLASH)

        val hit1 = Hit(actor, target, this, templateBuilder.setDelay(0).build())
        val hit2 = Hit(actor, target, this, templateBuilder.setDelay(if(target is NPC) 1 else 0).build())
        return arrayOf(hit1, hit2)
    }

    class Provider : SpecialAttackProvider {

        override fun getAttackAnimation(type: AttackType?) =
                Animation(1062, Priority.HIGH)

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(252, 0, 80, Priority.HIGH))

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.DRAGON_DAGGER_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 4
    }

}