package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Skill
import com.grinder.util.Priority
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import java.util.*
import kotlin.math.max

/**
 * https://oldschool.runescape.wiki/w/Brine_sabre
 *
 * "The brine sabre has a special attack, Liquify,
 * which doubles the player's accuracy, and increases
 * the player's Strength, Attack and Defence levels
 * by 25% of the damage dealt.
 *
 * The special attack only works underwater, and consumes
 * 75% of the player's special attack energy."
 *
 * @author  Kyle
 * @since   8/12/2021
 * @version 1.0
 */
class BrineSabreSpecialAttack : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.LIQUIFY

    override fun secondaryAccuracyModifier(context: AttackContext) = 2.0

    override fun canAttack(actor: Agent, target: Agent): Boolean {

        return super.canAttack(actor, target)
    }

    override fun postHitEffect(hit: Hit) {

        if (hit.isAccurate) {

            val attacker = hit.attacker
            val attackerSkills = attacker.skills
            val amount = (hit.totalDamage * HIT_PERC).toInt()

            if (amount > 0) {
                SKILLS.forEach { skill ->
                    val currentLevel = attackerSkills.getLevel(skill)
                    val maxLevel = attackerSkills.getMaximumLevel(skill)

                    attackerSkills.set(skill, currentLevel + amount, maxLevel)
                    attacker.ifPlayer { it.skillManager.updateSkill(skill) }
                }
            }
        }
    }

    class Provider : SpecialAttackProvider {

        override fun fetchHits(type: AttackType?) = HitTemplate
            .builder(type)
            .setDelay(0)
            .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) = Animation(6118, Priority.HIGH)
        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(Graphic(1048, GraphicHeight.HIGH, Priority.HIGH))
        override fun fetchAttackSound(type: AttackType?) = Optional.of(Sound(Sounds.BRINE_SABRE_SPECIAL_SOUND))
        override fun fetchAttackDuration(type: AttackType?) = 4

    }

    private companion object {

        private const val HIT_PERC = 0.25
        private val SKILLS = arrayOf(Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE)

    }

}