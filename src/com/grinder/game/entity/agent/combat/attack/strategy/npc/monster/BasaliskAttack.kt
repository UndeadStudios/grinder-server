package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MeleeAttackStrategy
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.model.Animation
import com.grinder.game.model.Skill
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplateBuilder
import com.grinder.util.ItemID
import com.grinder.util.oldgrinder.EquipSlot

/**
 * BasaliskAttack. Uses melee but has a special attack that can send a RANGED attack towards the player.
 * The special decreases the stats of the player but can be negated by a mirror shield or V's shield.
 */
class BasaliskAttack: MeleeAttackStrategy() {
    companion object {
        private val affectedSkills = arrayListOf(Skill.ATTACK, Skill.DEFENCE, Skill.STRENGTH, Skill.RANGED,
            Skill.MAGIC, Skill.AGILITY, Skill.PRAYER)
        private val gazeProj = ProjectileTemplateBuilder(75)
            .setHeights(10, 30)
            .setCurve(0)
            .setDelay(10)
            .setSpeed(50)
            .build()
    }
    var isProtected = false

    override fun canAttack(actor: Agent, target: Agent): Boolean {
        if (actor.isPlayer) {
            // TODO: Need V's shield also
            isProtected = target.asPlayer.equipment[EquipSlot.SHIELD].id == ItemID.MIRROR_SHIELD
        }
        return true
    }

    // Apparently a different attack anim is used for the gaze
    override fun animate(actor: Agent) {
        /*if (isProtected)
            actor.performAnimation(Animation(actor.asNpc.fetchDefinition().magicAnim))
        else*/
            super.animate(actor)
    }

    override fun requiredDistance(actor: Agent) = if (isProtected) 1 else 6

    override fun sequence(actor: Agent, target: Agent) {
        isProtected = target.asPlayer.equipment[EquipSlot.SHIELD].id == ItemID.MIRROR_SHIELD
        if (isProtected)
            return
        Projectile(actor, target, gazeProj).sendProjectile()
    }

    override fun createHits(actor: Agent, target: Agent): Array<Hit> {
        val template = HitTemplate
            .builder(if (isProtected) AttackType.MELEE else AttackType.SPECIAL)
            .setDelay(0)
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