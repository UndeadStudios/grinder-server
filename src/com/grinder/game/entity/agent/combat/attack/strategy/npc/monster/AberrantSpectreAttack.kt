package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster

import com.grinder.game.content.skill.skillable.impl.slayer.hasAnySlayerHelmet
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Skill
import com.grinder.game.model.item.container.player.Equipment
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.util.ItemID
import com.grinder.util.oldgrinder.EquipSlot

/**
 * AberrantSpectreAttackStrategy.
 *
 * Uses magic based attack, if no nose protection the attack becomes a never missing magic attack that lowers stats.
 *
 * A1507 P335 G336
 */
class AberrantSpectreAttack : MagicAttackStrategy() {
    companion object {
        private val affectedSkills = arrayListOf(Skill.ATTACK, Skill.DEFENCE, Skill.STRENGTH, Skill.RANGED,
                Skill.MAGIC, Skill.AGILITY, Skill.PRAYER)
        // determine if a container has a nosepeg or slayer helmet
        private fun hasNoseProtection(equip: Equipment):Boolean = hasAnySlayerHelmet(equip) || equip[EquipSlot.HAT]?.id == ItemID.NOSE_PEG
        private val hitGFX = Graphic(336, 0, 500)
        private val startGFX = Graphic(336, 0, 700)
        private val PROJECTILE = ProjectileTemplate
            .builder(335)
            .setSourceOffset(1)
            .setDelay(30)
            .setSpeed(40)
            .setHeights(160, 0)
            .setCurve(280)
            .build()
    }
    // Keep tab on if our target is protected, this way they cannot bypass it on hit.
    private var targetProtected = false

    override fun sequence(actor: Agent, target: Agent) {
        if (target != null && target.isPlayer)
            targetProtected = hasNoseProtection(target.asPlayer.equipment)
        actor.performGraphic(startGFX)
        Projectile(actor, target, Companion.PROJECTILE).sendProjectile()
        super.sequence(actor, target)
    }

    override fun createHits(actor: Agent, target: Agent): Array<Hit> {
        val template = HitTemplate
                .builder(if (targetProtected) AttackType.MAGIC else AttackType.SPECIAL)
                .setDelay(getMagicSpellHitDelay(actor, target))
                .setIgnoreAttackStats(!targetProtected)
                .setIgnoreStrengthStats(!targetProtected)
                .setSuccessOrFailedGraphic(hitGFX)
        if (!targetProtected)
            template.setDamageRange(IntRange(1, actor.asNpc.getMaxHit(null)))
        return arrayOf(Hit(actor, target, this, template.build()))
    }

    override fun postHitEffect(hit: Hit) {
        if (!targetProtected && hit.target.isPlayer) {
            val pl = hit.target.asPlayer
            affectedSkills.forEach { skill -> pl.skillManager.decreaseLevelTemporarily(skill, 19, 0) }
        }
    }


}