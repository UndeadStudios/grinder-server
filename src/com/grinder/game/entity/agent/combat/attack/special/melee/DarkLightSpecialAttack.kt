package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.util.Priority
import com.grinder.game.model.Skill
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import java.util.*
import kotlin.math.max

/**
 * https://oldschool.runescape.wiki/w/Darklight
 *
 * "Darklight has a special attack, Weaken,
 * which consumes 50% of the player's special attack energy
 * and lowers the opponent's Strength, Attack, and Defence levels by 5% (10% for demons)
 * of their max level.
 * This special is only activated when the player hits accurately
 * and multiple special attacks will stack"
 *
 * TODO: check if darklight special prompts a message to the target player "you feel drained"
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class DarkLightSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.WEAKEN

    override fun tertiaryDamageModifier(context: AttackContext): Double {
        if (context.isFighting(MonsterRace.DEMON)){
            if (context.used(SpecialAttackType.WEAKEN))
                return 1.60
        }
        return 1.0
    }
    
    override fun postHitEffect(hit: Hit) {

        if(hit.isAccurate) {

            val target = hit.target
            val targetSkills = target.skills

            val drainSkills = arrayOf(Skill.STRENGTH, Skill.ATTACK, Skill.DEFENCE)
            val drainPercentage = if(MonsterRace.isRace(target, MonsterRace.DEMON))
                0.10
            else
                0.05

            for (skill in drainSkills) {
                val currentLevel = targetSkills.getLevel(skill)

                if(currentLevel <= 1)
                    continue

                val maxLevel = targetSkills.getMaximumLevel(skill)
                val maxDecrease = maxLevel
                        .times(drainPercentage)
                        .toInt()
                        .coerceAtMost(currentLevel)

                targetSkills.set(skill, max(1, currentLevel - maxDecrease), maxLevel)
            }

            target.ifPlayer {
                if (drainPercentage < hit.totalDamage) {
                    for(skill in drainSkills)
                        it.skillManager.updateSkill(skill)
                    it.message("You feel drained!", 1000)
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
                Animation(2890, Priority.HIGH)

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(483, Priority.HIGH))

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.DARKLIGHT_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 5
    }
}