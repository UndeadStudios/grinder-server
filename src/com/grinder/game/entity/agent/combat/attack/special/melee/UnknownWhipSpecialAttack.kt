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
import kotlin.math.roundToInt

/**
 * This is a custom special attack for a custom weapon.
 *
 * TODO: set proper modifiers
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   12/05/2020
 * @version 1.0
 */
class UnknownWhipSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.SUDDEN_DRAINAGE

    override fun secondaryAccuracyModifier(context: AttackContext) = 1.25

    override fun secondaryDamageModifier(context: AttackContext) = 1.35

    override fun postHitEffect(hit: Hit) {

        if(hit.isAccurate) {

            val target = hit.target
            val targetSkills = target.skills

            val drainSkills = arrayOf(Skill.ATTACK, Skill.DEFENCE)
            val drainedSkills = HashSet<Skill>()

            for (skill in drainSkills) {
                val currentLevel = targetSkills.getLevel(skill)
                val maxLevel = targetSkills.getMaximumLevel(skill)
                val maxDecrease = currentLevel.times(0.35).roundToInt()

                if(maxDecrease > 0)
                    drainedSkills.add(skill)

                targetSkills.set(skill, max(1, currentLevel - maxDecrease), maxLevel)
            }

            target.ifPlayer {
                for(skill in drainedSkills)
                    it.skillManager.updateSkill(skill)
                it.message("You feel drained!", 1000)
            }
        }
    }


    class Provider : SpecialAttackProvider {

        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(type)
                .setDelay(0)
                .setSuccessOrFailedGraphic(Graphic(606, GraphicHeight.MIDDLE, Priority.HIGH))
                .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
                Animation(1658, Priority.HIGH)

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.UNKNOWN_WHIP_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 4
    }
}