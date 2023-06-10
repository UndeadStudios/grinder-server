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
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.util.Priority
import com.grinder.game.model.Skill
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import java.util.*
import kotlin.math.roundToInt

/**
 * https://oldschool.runescape.wiki/w/Saradomin_godsword
 *
 * "The Saradomin godsword has a special attack called Healing Blade,
 * which consumes 50% of the player's special attack energy.
 * When used, the player's max hit is increased by 10%,
 * their accuracy is doubled, and it restores health equal to 50% of the hit
 * and prayer by 25% of the hit (rounded up in even intervals).
 * This effect only occurs when the player lands a successful hit,
 * and any hit that deals below 22 damage will always
 * heal the player for 10 hitpoints and restore 5 prayer points."

 * Version 1.1: Adds Golden Spec
 *
 * TODO: check if sound id is correct one
 * TODO: check if there is a chatbox message for health restoration
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @author  R-Y-M-R
 * @since   14/05/2020
 * @version 1.1
 */
class SaradominGodswordSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.HEALING_BLADE

    override fun secondaryAccuracyModifier(context: AttackContext) = 2.0

    override fun secondaryDamageModifier(context: AttackContext) = 1.10

    override fun sequence(actor: Agent, target: Agent) {
        if (actor is Player) {
            if (actor.attributes.bool(Attribute.GOLDEN_SGS)) {
                actor.performGraphic(Graphic(GoldenGodswordSpecialAttacks.SARA.goldenSpecId, Priority.HIGH))
            } else {
                actor.performGraphic(Graphic(1209, Priority.HIGH))
            }
        }
        super.sequence(actor, target)
    }

    override fun postHitEffect(hit: Hit) {

        if(hit.isAccurate){

            val totalDamage = hit.totalDamage
            var restoreHealthAmount = totalDamage.times(0.5).roundToInt()
            var restorePrayerAmount = totalDamage.times(0.25).roundToInt()

            if(totalDamage < 22){
                restoreHealthAmount = restoreHealthAmount.coerceAtLeast(10)
                restorePrayerAmount = restorePrayerAmount.coerceAtLeast(5)
            }

            val attacker = hit.attacker
            val skills = attacker.skills

            val maxHealth = skills.getMaximumLevel(Skill.HITPOINTS)
            val newHealth = (skills.getLevel(Skill.HITPOINTS) + restoreHealthAmount)
                    .coerceAtMost(maxHealth)
            val maxPrayer = skills.getMaximumLevel(Skill.PRAYER)
            val newPrayer = (skills.getLevel(Skill.PRAYER) + restorePrayerAmount)
                    .coerceAtMost(maxPrayer)

            skills.set(Skill.HITPOINTS, newHealth, maxHealth)
            skills.set(Skill.PRAYER, newPrayer, maxPrayer)

            attacker.ifPlayer {
                it.skillManager.updateSkill(Skill.HITPOINTS)
                it.skillManager.updateSkill(Skill.PRAYER)
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
                Animation(7640, Priority.HIGH)

        /*override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(1209, Priority.HIGH))*/

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.GODSWORD_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 6
    }
}