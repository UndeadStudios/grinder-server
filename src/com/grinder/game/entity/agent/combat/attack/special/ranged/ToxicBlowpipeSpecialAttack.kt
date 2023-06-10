package com.grinder.game.entity.agent.combat.attack.special.ranged

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.RangedSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.RangedSpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Ammunition
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeaponType
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonEffect
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil
import com.grinder.game.model.*
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Misc
import com.grinder.util.Priority
import java.util.*
import kotlin.math.roundToInt

/**
 * https://oldschool.runescape.wiki/w/Toxic_blowpipe
 *
 * "The toxic blowpipe has a special attack, Toxic Siphon,
 * which increases damage by 50%, while healing the user
 * by half of the damage dealt (rounded down).
 * This consumes 50% of the player's special attack energy."
 *
 * TODO: find special attack sound
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class ToxicBlowpipeSpecialAttack
    : RangedSpecialAttack(Provider()) {

    private fun fightingNpcWithSerpentineHelm(target: Agent?, attacker: Agent?) =
            target is NPC && attacker is Player && attacker.equipment.containsAny(EquipmentUtil.SERPENTINE_HELM_ITEM_ID, EquipmentUtil.MAGMA_HELM_ITEM_ID, EquipmentUtil.TANZANITE_HELM_ITEM_ID)

    override fun special() = SpecialAttackType.TOXIC_SIPHON

    override fun weaponType() = RangedWeaponType.TOXIC_BLOWPIPE

    override fun secondaryAccuracyModifier(context: AttackContext) = 1.50

    override fun secondaryDamageModifier(context: AttackContext) = 1.50

    override fun postHitEffect(hit: Hit) {

        if(hit.isAccurate){

            val attacker = hit.attacker
            val totalDamage = hit.totalDamage
            val restoreHealthAmount = totalDamage.times(0.5).roundToInt()

            if(restoreHealthAmount > 0) {

                val skills = attacker.skills

                val maxHealth = skills.getMaximumLevel(Skill.HITPOINTS)
                val newHealth = (skills.getLevel(Skill.HITPOINTS) + restoreHealthAmount)
                        .coerceAtMost(maxHealth)

                skills.set(Skill.HITPOINTS, newHealth, maxHealth)

                attacker.ifPlayer {
                    it.skillManager.updateSkill(Skill.HITPOINTS)
                }
            }

            val target = hit.target
            val envenomTarget = if(fightingNpcWithSerpentineHelm(target, attacker))
                true
            else
                Misc.randomChance(25F)

            if(envenomTarget)
                PoisonEffect.applyPoisonTo(target, PoisonType.VENOM)
        }
    }

    class Provider : RangedSpecialAttackProvider() {

        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(type)
                .setDelay(2)
                .buildAsStream()

        override fun fetchProjectiles(type: AttackType, ammunition: Ammunition) = ProjectileTemplate
                .builder(1043)
                .setSourceOffset(0)
                .setDelay(37)
                .setSpeed(8)
                .setStartHeight(28)
                .setEndHeight(28)
                .setCurve(10)
                .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
                Animation(5061, Priority.HIGH)

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.TOXIC_PIPE_SPEC_SOUND))
    }
}