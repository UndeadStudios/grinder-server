package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MeleeAttackStrategy
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.model.Skill
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplateBuilder
import com.grinder.util.ItemID
import com.grinder.util.oldgrinder.EquipSlot

/**
 * CockatriceAttack. Uses melee but has a special attack that can send a RANGED attack towards the player.
 * The special decreases the stats of the player but can be negated by a mirror shield or V's shield.
 */
class CockatriceAttack: MeleeAttackStrategy() {
    companion object {
        private val affectedSkills = arrayListOf(Skill.ATTACK, Skill.DEFENCE, Skill.STRENGTH, Skill.RANGED,
                Skill.MAGIC, Skill.AGILITY, Skill.PRAYER)
        private val gazeProj = ProjectileTemplateBuilder(75)
                .setHeights(43, 30)
                .setCurve(280)
                .setDelay(30)
                .setSpeed(50)
                .build()
    }
    var isProtected = false

    override fun canAttack(actor: Agent, target: Agent): Boolean {
        val attack =  super.canAttack(actor, target)
        if (!attack)
            return false
        if (target.isPlayer)
            // TODO: Need V's shield also
            isProtected = target.asPlayer.equipment[EquipSlot.SHIELD].id == ItemID.MIRROR_SHIELD
        return true
    }

    override fun requiredDistance(actor: Agent) = if (isProtected) 1 else 6

    override fun sequence(actor: Agent, target: Agent) {
        if (isProtected)
            return
        Projectile(actor, target, gazeProj).sendProjectile()
    }

    override fun createHits(actor: Agent, target: Agent): Array<Hit> {
        val template = HitTemplate
                .builder(if (isProtected) AttackType.MELEE else AttackType.SPECIAL)
                .setDelay(if (isProtected) 0 else 2)
                .setIgnoreAttackStats(!isProtected)
                .setIgnoreStrengthStats(!isProtected)

        if (!isProtected)
            template.setDamageRange(IntRange(10, 10))
        return arrayOf(Hit(actor, target, this, template.build()))
    }

    override fun postHitEffect(hit: Hit) {
        if (hit.target.isPlayer && !isProtected) {
            val pl = hit.target.asPlayer
            affectedSkills.forEach { skill -> pl.skillManager.decreaseLevelTemporarily(skill, 11, 0) }
        }
    }
}