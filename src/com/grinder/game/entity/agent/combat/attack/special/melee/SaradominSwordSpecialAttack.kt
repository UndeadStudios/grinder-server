package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpellType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.util.Priority
import com.grinder.game.model.Skill
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID
import java.util.*

/**
 * https://oldschool.runescape.wiki/w/Saradomin_sword
 *
 * "The Saradomin sword has a special attack, Saradomin's Lightning,
 * that deals 10% more melee damage and 1-16 extra Magic damage.
 * This special attack consumes 100% of the wielder's special attack energy."
 *
 * https://oldschool.runescape.wiki/w/Saradomin%27s_blessed_sword
 *
 * "Saradomin's blessed sword has a special attack, Saradomin's Lightning,
 * which deals a Magic-based attack (Magical melee) that
 * increases the player's max hit by 25%,
 * consuming 65% of the player's special attack energy."
 *
 * TODO: rolls against the opponent's Magic defence bonus using the player's slash attack bonus
 * TODO: see if the accuracy bonus should be applied, bitterkoekje's thread conflicts with wiki
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class SaradominSwordSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    private fun hasBlessedSaradominSword(actor: Agent) =
            actor is Player && actor.equipment.contains(ItemID.SARADOMINS_BLESSED_SWORD)

    private fun getAttackAnimation(actor: Agent) =
            Animation(if(hasBlessedSaradominSword(actor))
                1133 else 1132, Priority.HIGH)

    override fun special() = SpecialAttackType.SARADOMINS_LIGHNING

    override fun secondaryAccuracyModifier(context: AttackContext) = 2.0

    override fun secondaryDamageModifier(context: AttackContext): Double {
        if(context.used(ItemID.SARADOMINS_BLESSED_SWORD))
            return 1.25
        return 1.10
    }

    override fun drainAmount(actor: Agent): Int {
        if(hasBlessedSaradominSword(actor))
            return 65
        return 100
    }

    override fun sequence(actor: Agent, target: Agent) {
        // effective overruling animate method in Attack
        actor.performAnimation(getAttackAnimation(actor))
        super.sequence(actor, target)
    }

    override fun createHits(actor: Agent, target: Agent): Array<Hit> {

        val meleeHit = Hit(actor, target, this, HitTemplate
                .builder(AttackType.MELEE)
                .setAttackStat(EquipmentBonuses.ATTACK_SLASH)
                .setDefenceStat(EquipmentBonuses.DEFENCE_MAGIC)
                .setSuccessOrFailedGraphic(Graphic(1196, Priority.HIGH))
                .setDelay(0)
                .build())

        if(!hasBlessedSaradominSword(actor)) {

            val magicHit = Hit(actor, target, this, HitTemplate
                    .builder(AttackType.MAGIC)
                    .setDamageRange(1..16)
                    .setIgnoreAttackStats(true)
                    .setIgnoreStrengthStats(true)
                    .build())

            if (meleeHit.missed() || CombatSpellType.isImmuneToMagicDamage(target))
                magicHit.multiplyDamage(0.0)

            actor.ifPlayer {
                // todo: add a multiplier maybe
                val magicExperience = magicHit.totalDamage.times(2)
                if(magicExperience > 0)
                    it.skillManager.addExperience(Skill.MAGIC, magicExperience)
            }

            return arrayOf(meleeHit, magicHit)
        } else
            return arrayOf(meleeHit)
    }

    class Provider : SpecialAttackProvider {

        override fun getAttackAnimation(type: AttackType?) =
                Animation(0, Priority.LOW) // cheaphax, is overridden in sequence, bad design

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(1213, Priority.HIGH))

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.SARADOMIN_SWORD_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 4
    }
}