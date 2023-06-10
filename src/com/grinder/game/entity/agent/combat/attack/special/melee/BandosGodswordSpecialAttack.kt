package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.content.item.GoldenGodswordSpecialAttacks
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.util.Priority
import com.grinder.game.model.Skill
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import java.util.*
import kotlin.collections.HashSet
import kotlin.math.max

/**
 * https://oldschool.runescape.wiki/w/Bandos_godsword
 *
 * "The Bandos godsword's special attack, Warstrike,
 * has doubled accuracy, inflicts 21% more damage and drains
 * the opponent's combat levels equivalent to the damage
 * hit in the following order: Defence, Strength, Prayer, Attack, Magic, Ranged.
 * Warstrike consumes 50% of the wielder's special attack energy."
 *
 * Version 1.1: Adds golden spec
 *
 * TODO: "Against Tekton, it drains his combat skills by 10 (following the stat drain order) if it fails to hit."
 * TODO: confirm if sound is the correct one
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @author  R-Y-M-R
 * @see     <a href="https://www.rune-server.ee/members/necrotic/">RuneServer</a>
 * @since   14/05/2020
 * @version 1.1
 */
class BandosGodswordSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.WARSTRIKE

    override fun secondaryAccuracyModifier(context: AttackContext) = 2.0

    override fun secondaryDamageModifier(context: AttackContext) = 1.10

    override fun tertiaryDamageModifier(context: AttackContext) = 1.11

    override fun sequence(actor: Agent, target: Agent) {
        if (actor is Player) {
            if (actor.attributes.bool(Attribute.GOLDEN_BGS)) {
                actor.performGraphic(Graphic(GoldenGodswordSpecialAttacks.BANDOS.goldenSpecId, Priority.HIGH))
            } else {
                actor.performGraphic(Graphic(1212, Priority.HIGH))
            }
        }
        super.sequence(actor, target)
    }

    override fun postHitEffect(hit: Hit) {

        if(hit.isAccurate) {

            val target = hit.target
            val targetSkills = target.skills

            val drainSkills = arrayOf(Skill.DEFENCE, Skill.STRENGTH, Skill.PRAYER, Skill.ATTACK, Skill.MAGIC, Skill.RANGED)
            var drainAmount = hit.totalDamage

            if (drainAmount > 0) {

                val drainedSkills = HashSet<Skill>()

                for (skill in drainSkills) {
                    val currentLevel = targetSkills.getLevel(skill)
                    val maxLevel = targetSkills.getMaximumLevel(skill)
                    val maxDecrease = currentLevel.coerceAtMost(drainAmount)

                    if(maxDecrease > 0)
                        drainedSkills.add(skill)

                    targetSkills.set(skill, max(1, currentLevel - maxDecrease), maxLevel)

                    drainAmount -= maxDecrease

                    if (drainAmount <= 0)
                        break
                }

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
                .builder(AttackType.MELEE)
                .setAttackStat(EquipmentBonuses.ATTACK_SLASH)
                .setDelay(0)
                .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
                Animation(7642, Priority.HIGH)

        /*
        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(1212, Priority.HIGH))
         */

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.GODSWORD_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 6
    }
}