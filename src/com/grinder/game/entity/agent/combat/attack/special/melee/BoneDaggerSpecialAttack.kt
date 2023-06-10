package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.Skill
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Priority
import java.util.*
import kotlin.collections.HashSet
import kotlin.math.max

/**
 * https://oldschool.runescape.wiki/w/Bone_dagger#Poison++
 *
 * The bone dagger has a special attack, Backstab, (75%) which has a guaranteed hit if you weren't the last one to attack the target, and lowers the
 * target's Defence by the amount of damage dealt, similar to the Bandos godsword's and Dorgeshuun crossbow's special attacks.
 * Note that a successful Backstab can still deal 0 damage, as a guaranteed hit is not the same as guaranteed damage.
 * This is because Backstab guarantees a success on the hit accuracy roll, but does not guarantee an above-zero value on the hit damage roll.
 *
 *
 */
class BoneDaggerSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.BACKSTAB

    override fun secondaryAccuracyModifier(context: AttackContext) = 1.00

    override fun secondaryDamageModifier(context: AttackContext) = 1.00

    override fun tertiaryDamageModifier(context: AttackContext) = 1.00

    override fun postHitEffect(hit: Hit) {

        if (hit.isAccurate) {

            val target = hit.target
            val targetSkills = target.skills

            var drainAmount = hit.totalDamage

            if (drainAmount > 0) {

                val drainedSkills = HashSet<Skill>()


                val currentLevel = targetSkills.getLevel(Skill.DEFENCE)
                val maxLevel = targetSkills.getMaximumLevel(Skill.DEFENCE)
                val maxDecrease = currentLevel.coerceAtMost(drainAmount)

                if (maxDecrease > 0)
                    drainedSkills.add(Skill.DEFENCE)

                targetSkills.set(Skill.DEFENCE, max(1, currentLevel - maxDecrease), maxLevel)

                drainAmount -= maxDecrease

                if (drainAmount <= 0)
                    return

                target.ifPlayer {
                    if (drainAmount < hit.totalDamage) {
                        for (skill in drainedSkills)
                            it.skillManager.updateSkill(skill)
                        it.message("You feel drained!", 1000)
                    }
                }
            }
        }
    }

    class Provider : SpecialAttackProvider {

        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(type)
                .setIgnoreAttackStats(true)
                .setDelay(0)
                .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
                Animation(4198, Priority.HIGH)

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(704, Priority.HIGH))

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.BONE_DAGGER_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?) = 4
    }
}