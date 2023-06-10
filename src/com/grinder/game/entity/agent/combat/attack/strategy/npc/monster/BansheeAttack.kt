package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster

import com.grinder.game.content.skill.skillable.impl.slayer.hasAnySlayerHelmet
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MeleeAttackStrategy
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.model.Animation
import com.grinder.game.model.Skill
import com.grinder.game.model.item.container.player.Equipment
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplateBuilder
import com.grinder.game.model.sound.Sound
import com.grinder.util.ItemID
import com.grinder.util.Priority
import com.grinder.util.oldgrinder.EquipSlot

/**
 * BansheeAttack is simply a extended MeleeAttackStrategy with a requirement of earmuffs (or slayer helm).
 */
class BansheeAttack : MeleeAttackStrategy() {
    companion object {
        private val affectedSkills = arrayListOf(Skill.ATTACK, Skill.DEFENCE, Skill.STRENGTH, Skill.RANGED,
                Skill.MAGIC, Skill.AGILITY, Skill.PRAYER)
        private fun hasEarProtection(equip:Equipment) = hasAnySlayerHelmet(equip) || equip[EquipSlot.HAT]?.id == ItemID.EARMUFFS
        private val earCoverAnim = Animation(1572, Priority.HIGH)
        private val screechSound = Sound(284)
        private val screech = ProjectileTemplateBuilder(337)
                .setSourceOffset(0)
                .setDelay(5)
                .setSpeed(100)
                .setStartHeight(43)
                .setEndHeight(43)
                .setCurve(0)
                .build()
    }

    // local var to determine if the target has ear protection at beginning of attack.
    var hasProtection = false

    override fun animate(actor: Agent) {}

    override fun sequence(actor: Agent, target: Agent) {
        if (target.isPlayer) {
            hasProtection = hasEarProtection(target.asPlayer.equipment)
            if (!hasProtection) {
                target.performAnimation(earCoverAnim)
                target.asPlayer.playSound(screechSound)
                Projectile(actor, target, screech).sendProjectile()
            }
        }
        // we will use the magicAnim for the scream of the banshee
        val def = actor.asNpc.fetchDefinition()
        actor.performAnimation(Animation(if(hasProtection) def.attackAnim else def.magicAnim))
        super.sequence(actor, target)
    }

    override fun createHits(actor: Agent, target: Agent): Array<Hit> {
            val template = HitTemplate
                    .builder(if (hasProtection) AttackType.MELEE else AttackType.SPECIAL)
                    .setDelay(0)
                    .setIgnoreAttackStats(!hasProtection)
                    .setIgnoreStrengthStats(!hasProtection)

            if (!hasProtection)
                template.setDamageRange(IntRange(6, 6))
            return arrayOf(Hit(actor, target, this, template.build()))
    }

    override fun postHitEffect(hit: Hit) {
        if (hit.target.isPlayer && !hasProtection) {
            val pl = hit.target.asPlayer
            affectedSkills.forEach { skill -> pl.skillManager.decreaseLevelTemporarily(skill, 11, 0) }
        }
    }
}