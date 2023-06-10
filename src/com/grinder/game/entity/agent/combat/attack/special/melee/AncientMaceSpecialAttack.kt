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
class AncientMaceSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.FAVOUR_OF_THE_WAR_GOD

    override fun secondaryAccuracyModifier(context: AttackContext) = 1.00

    override fun secondaryDamageModifier(context: AttackContext) = 1.00

    override fun tertiaryDamageModifier(context: AttackContext) = 1.00

    override fun postHitEffect(hit: Hit) {

        if(hit.isAccurate) {

            val target = hit.target
            val targetSkills = target.skills

            var drainAmount = hit.totalDamage

            if (drainAmount > 0) {

                //Increase current prayer
                if (hit.attacker.isPlayer) {
                    hit.attacker.skills.set(Skill.PRAYER, max(1, hit.attacker.skills.getLevel(Skill.PRAYER) + hit.totalDamage), hit.attacker.skills.getMaximumLevel(Skill.PRAYER))
                    hit.attacker.asPlayer.skillManager.updateSkill(Skill.PRAYER)
                }

                val drainedSkills = HashSet<Skill>()

                val currentLevel = targetSkills.getLevel(Skill.PRAYER)
                val maxLevel = targetSkills.getMaximumLevel(Skill.PRAYER)
                val maxDecrease = currentLevel.coerceAtMost(drainAmount)

                if(maxDecrease > 0)
                    drainedSkills.add(Skill.PRAYER)

                targetSkills.set(Skill.PRAYER, max(1, currentLevel - maxDecrease), maxLevel)

                drainAmount -= maxDecrease

                if (drainAmount <= 0)
                    return

                target.ifPlayer {
                    if (drainAmount < hit.totalDamage) {
                        for(skill in drainedSkills)
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
                Animation(6147, Priority.HIGH)

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(1052, Priority.HIGH))

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.ANCIENT_MACE_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 5
    }
}