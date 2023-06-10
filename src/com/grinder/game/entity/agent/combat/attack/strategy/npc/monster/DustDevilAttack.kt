package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MeleeAttackStrategy
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.HitTemplateBuilder
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.util.DistanceUtil
import java.util.stream.Stream

/**
 * Dust devil attack strategy.
 * @author Blake
 */
class DustDevilAttack: AttackStrategy<NPC> {

    var attackType: AttackStrategy<Agent> = MeleeAttackStrategy.INSTANCE

    override fun animate(actor: NPC) {
        attackType.animate(actor)
    }

    override fun postHitAction(actor: NPC, target: Agent) {
        attackType.postHitAction(actor, target)
    }

    override fun postHitEffect(hit: Hit) {
        attackType.postHitEffect(hit)
    }

    override fun duration(actor: NPC): Int {
        return attackType.duration(actor)
    }

    override fun sequence(actor: NPC, target: Agent) {
        attackType.sequence(actor, target)
    }

    override fun requiredDistance(actor: Agent) = 8

    override fun canAttack(actor: NPC, target: Agent): Boolean {
        attackType = if (!DistanceUtil.isWithinDistance(actor, target, 2))
            RangedAttackStrategy.INSTANCE
        else MeleeAttackStrategy.INSTANCE
        return true
    }

    override fun createHits(actor: NPC, target: Agent): Array<Hit> {
        var template: HitTemplateBuilder = if (attackType == MeleeAttackStrategy.INSTANCE) {
            HitTemplate
                .builder(AttackType.MELEE)
                .setDelay(0)
                .setDefenceStat(EquipmentBonuses.DEFENCE_RANGE) // Melee ranged attack
        } else {
            HitTemplate
                .builder(AttackType.RANGED)
                .setDelay(RangedAttackStrategy.getNPCRangeHitDelay(actor, target))
        }

        return arrayOf(Hit(actor, target, this, template.build()))
    }

    override fun type(): AttackType? {
        return attackType.type()
    }
}