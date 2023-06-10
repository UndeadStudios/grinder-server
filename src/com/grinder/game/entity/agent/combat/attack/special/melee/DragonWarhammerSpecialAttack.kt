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
import com.grinder.util.Priority
import com.grinder.game.model.Skill
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import java.util.*
import kotlin.math.max

/**
 * https://oldschool.runescape.wiki/w/Dragon_warhammer
 *
 * "The dragon warhammer has a special attack, Smash, which deals
 * 50% more damage, while lowering the target's current Defence
 * level by 30% on a hit other than zero (the hit has to deal damage).[1]
 * When used against Tekton, it will reduce 5% of his Defence level if it fails to hit.
 * This attack consumes 50% of the player's special attack energy."
 *
 * TODO: "against Tekton, it will reduce 5% of his Defence level if it fails to hit"
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class DragonWarhammerSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.SMASH

    override fun secondaryDamageModifier(context: AttackContext) = 1.50

    override fun postHitEffect(hit: Hit) {

        if(hit.totalDamage > 0) {

            val target = hit.target
            val targetSkills = target.skills

            val currentLevel = targetSkills.getLevel(Skill.DEFENCE)
            val maxLevel = targetSkills.getMaximumLevel(Skill.DEFENCE)
            val maxDecrease = currentLevel.times(0.3).toInt()

            targetSkills.set(Skill.DEFENCE, max(1, currentLevel - maxDecrease), maxLevel)

            target.ifPlayer {
                it.skillManager.updateSkill(Skill.DEFENCE)
                it.message("You feel drained!", 1000)
            }
        }
    }

    class Provider : SpecialAttackProvider {

        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(type)
                .setDelay(0)
                .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
                Animation(1378, Priority.HIGH)

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(1292, Priority.HIGH))

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.DRAGON_WARHAMMER_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 6
    }
}