package com.grinder.game.entity.agent.combat.attack.special.ranged

import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.RangedSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.RangedSpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Ammunition
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeaponType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.*
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Priority
import java.util.*
import kotlin.math.max

/**
 * https://oldschool.runescape.wiki/w/Seercull
 *
 * "The seercull has a special attack, Soulshot,
 * which consumes 100% of the player's special attack energy.
 * It is guaranteed to hit and lowers the Magic level of the opponent by the damage dealt.
 * However, damage inflicted by Soulshot is unaffected by Ranged prayers
 * (such as Eagle Eye) and the slayer helmet (i)."
 *
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class SeercullSpecialAttack
    : RangedSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.SOULSHOT

    override fun weaponType() = RangedWeaponType.SEERCULL_BOW

    override fun postHitEffect(hit: Hit) {

        val drainAmount = hit.totalDamage

        if (drainAmount > 0) {

            val target = hit.target
            val targetSkills = target.skills
            val currentLevel = targetSkills.getLevel(Skill.MAGIC)
            val maxLevel = targetSkills.getMaximumLevel(Skill.MAGIC)
            val maxDecrease = currentLevel.coerceAtMost(drainAmount)

            targetSkills.set(Skill.MAGIC, max(1, currentLevel - maxDecrease), maxLevel)

            target.ifPlayer {
                it.skillManager.updateSkill(Skill.MAGIC)
                it.message("You feel drained!", 1000)
            }
        }
    }

    class Provider : RangedSpecialAttackProvider() {

        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(type)
                .setDelay(2)
                .setIgnoreAttackStats(true)
                .setSuccessOrFailedGraphic(Graphic(474, Priority.HIGH))
                .buildAsStream()

        override fun fetchProjectiles(type: AttackType, ammunition: Ammunition) = ProjectileTemplate
                .builder(473)
                .setSourceOffset(0)
                .setDelay(45)
                .setSpeed(8)
                .setStartHeight(45)
                .setEndHeight(33)
                .setCurve(12)
                .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
                Animation(426, Priority.HIGH)

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.SEERCULL_SPECIAL_SOUND))

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(472, GraphicHeight.HIGH, Priority.HIGH))
    }
}