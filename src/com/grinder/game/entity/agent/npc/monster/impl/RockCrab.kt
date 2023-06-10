package com.grinder.game.entity.agent.npc.monster.impl

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MeleeAttackStrategy
import com.grinder.game.entity.agent.combat.event.CombatState
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.subscribe
import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.npc.monster.Monster
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.Position
import com.grinder.util.NpcID

/**
 * Represents a rock crab [Monster]. These crabs are initially spawned in a 'hidden' form.
 * Once they are attacked or a player is within 1 tile distance of the crab, it transforms
 * into its 'active' form.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   16/12/2020
 * @version 1.0
 */
class RockCrab(id: Int, position: Position) : Monster(id, position) {

    private val meleeCrushStrategy = object : MeleeAttackStrategy() {
        override fun createHits(actor: Agent, target: Agent) = arrayOf(Hit(actor, target, this, HitTemplate
                .builder(AttackType.MELEE)
                .setDelay(0)
                .setAttackStat(EquipmentBonuses.ATTACK_CRUSH)
                .setDefenceStat(EquipmentBonuses.DEFENCE_CRUSH)
                .build()))
    }

    init {
        combat.subscribe {
            if (it == CombatState.LOCKED_TARGET)
                show()
            return@subscribe false
        }
        hide()
        isHide = true
        fetchDefinition().isAggressive = true

    }

    override fun getAttackStrategy() = meleeCrushStrategy

    private fun hide() {
        motion.update(MovementStatus.DISABLED)
        npcTransformationId = getHiddenId(id)
        updateAppearance()
        isHide = true
    }

    private fun show(){
        if (isMorphed)
            resetTransformation()
        updateAppearance()
        motion.update(MovementStatus.NONE)
        isHide = false
    }

    override fun attackRange(type: AttackType) = 1

    companion object {

        private fun getHiddenId(activeId: Int) : Int {
            return when(activeId) {
                NpcID.ROCK_CRAB -> NpcID.ROCKS
                NpcID.ROCK_CRAB_102 -> NpcID.ROCKS_103
                NpcID.GIANT_ROCK_CRAB -> NpcID.BOULDER_2262
                NpcID.GIANT_ROCK_CRAB_5940 -> NpcID.BOULDER_5941
                NpcID.SAND_CRAB_7206 -> NpcID.SANDY_ROCKS_7207
                NpcID.SAND_CRAB -> NpcID.SANDY_ROCKS
                NpcID.KING_SAND_CRAB -> NpcID.SANDY_BOULDER
                NpcID.AMMONITE_CRAB -> NpcID.FOSSIL_ROCK
                else -> activeId
            }
        }

        fun isCrab(id: Int) = getHiddenId(id) != id
    }
}