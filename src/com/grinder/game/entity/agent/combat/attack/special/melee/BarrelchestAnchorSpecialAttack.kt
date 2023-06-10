package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.*
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Priority
import java.util.*
import kotlin.math.max

/**
 * https://oldschool.runescape.wiki/w/Barrelchest_anchor
 *
 * "The barrelchest anchor has a special attack, Sunder,
 * which doubles the player's accuracy, increases max damage by 10%,
 * and lowers the opponent's Attack, Defence, Ranged, or Magic level
 * (chosen at random from among those still above 1)
 * by 10% of the damage dealt.[1]
 * This consumes 50% of the player's special attack energy."
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class BarrelchestAnchorSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.SUNDER

    override fun secondaryAccuracyModifier(context: AttackContext) = 3.0

    override fun secondaryDamageModifier(context: AttackContext) = 1.10

    override fun postHitEffect(hit: Hit) {

        val target = hit.target

        if (hit.isAccurate && target.isAlive) {

            val targetSkills = target.skills

            val drainSkill = arrayOf(Skill.DEFENCE, Skill.ATTACK, Skill.MAGIC, Skill.RANGED).random()
            val drainAmount = (hit.totalDamage * 0.10).toInt()

            if (drainAmount > 0) {
                val currentLevel = targetSkills.getLevel(drainSkill)
                val maxLevel = targetSkills.getMaximumLevel(drainSkill)
                val maxDecrease = currentLevel.coerceAtMost(drainAmount)

                if (maxDecrease > 0) {
                    targetSkills.set(drainSkill, max(1, currentLevel - maxDecrease), maxLevel)
                    target.ifPlayer {
                        it.skillManager.updateSkill(drainSkill)
                        it.message("You feel drained!", 1000)
                    }
                }
            }
        }
    }
    class Provider : SpecialAttackProvider {

        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(type)
                .setDelay(1)
                .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
                Animation(5870, Priority.HIGH)

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(1027, GraphicHeight.MIDDLE, Priority.HIGH))

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.BARRELCHEST_ANCHOR_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 6
    }
}
