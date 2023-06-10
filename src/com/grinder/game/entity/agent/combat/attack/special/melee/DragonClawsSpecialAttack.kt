package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Misc
import com.grinder.util.Priority
import java.util.*

/**
 * https://oldschool.runescape.wiki/w/Dragon_claws
 *
 * "These claws feature a special attack, Slice and Dice,
 * which drains 50% of the special attack bar
 * and hits an enemy four times in succession,
 * which is popularly used by PKers as a finisher.
 * The damage calculation rolls against the opponent's slash defence."
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   12/05/2020
 * @version 1.0
 */
class DragonClawsSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.SLICE_AND_DICE

    override fun createHits(actor: Agent, target: Agent): Array<Hit> {
        val template = HitTemplate
                .builder(AttackType.MELEE)
                .setDelay(0)
                .setDefenceStat(EquipmentBonuses.DEFENCE_SLASH)
                .build()
        val hit1 = Hit(actor, target, this, template)
        val hit2 = Hit(actor, target, this, template)
        val hit3 = Hit(actor, target, this, template)
        val hit4 = Hit(actor, target, this, template)

        if (hit1.missed()) {
            if (hit2.missed()) {
                if (hit3.missed()) {
                    if (hit4.missed()) {
                        hit3.totalDamage = 1
                        hit4.totalDamage = 1
                    } else if (hit4.totalDamage > 0) {
                        hit4.multiplyDamage(1.50)
                    }
                } else {
                    val newDamage = (hit1.maxHit * 0.75).toInt()
                    hit3.totalDamage = newDamage
                    hit4.totalDamage = newDamage
                }
            } else {
                val newDamage = (hit2.totalDamage / 2.0).toInt()
                hit3.totalDamage = newDamage
                hit4.totalDamage = newDamage
            }
        } else {
            hit2.totalDamage = (hit1.totalDamage / 2.0).toInt()
            val newDamage = (hit2.totalDamage / 2.0).toInt()
            hit3.totalDamage = newDamage
            hit4.totalDamage = newDamage
        }

        if (Misc.randomBoolean())
            hit4.totalDamage = hit4.totalDamage + 1

        return arrayOf(hit1, hit2, hit3, hit4)
    }

    class Provider : SpecialAttackProvider {

        override fun getAttackAnimation(type: AttackType?) =
                Animation(7514, 0, 2, Priority.HIGH)

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(1171, Priority.HIGH))

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.DRAGON_CLAWS_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 4
    }
}