package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.entity.agent.Agent
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
import com.grinder.util.Priority
import java.util.*
import kotlin.math.max

/**
 * @author _jordan <https://www.rune-server.ee/members/_jordan/>
 */
class StatiusWarhammerSpecialAttack : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.STATIUS_WARHAMMER_SMASH

    override fun createHits(actor: Agent, target: Agent): Array<Hit> {

        val hit = Hit(actor, target, this, HitTemplate
                .builder(AttackType.MELEE)
                .setDelay(0)
                .build(), false)

        val maxHit = hit.maxHit
        val minHit = maxHit.times(0.25).toInt()
        val newMaxHit = maxHit.times(1.25).toInt()

        hit.setDamageRange(minHit..newMaxHit)
        hit.createHits(1)

        return arrayOf(hit)
    }

    override fun postHitEffect(hit: Hit) {
        if (hit.totalDamage > 0) {
            val target = hit.target
            val targetSkills = target.skills

            val currentLevel = targetSkills.getLevel(Skill.DEFENCE)
            val maxLevel = targetSkills.getMaximumLevel(Skill.DEFENCE)
            val maxDecrease = currentLevel.times(0.3).toInt()

            //This spec stacks debuffs.
            targetSkills.set(Skill.DEFENCE, max(1, currentLevel - maxDecrease), maxLevel)

            target.ifPlayer {
                it.skillManager.updateSkill(Skill.DEFENCE)
                it.message("You feel drained!", 1000)
            }
        }
    }

    class Provider : SpecialAttackProvider {

        override fun getAttackAnimation(type: AttackType?) =
                Animation(1378, Priority.HIGH)

        //TODO: The correct gfx is 1840 but it is not supported by the client.

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(2541))

        override fun fetchAttackDuration(type: AttackType?) = 5
    }
}
