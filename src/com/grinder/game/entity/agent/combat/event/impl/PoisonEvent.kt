package com.grinder.game.entity.agent.combat.event.impl

import com.grinder.game.definition.NpcDefinition
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonEffectTask
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonType
import com.grinder.game.entity.agent.combat.event.ApplicableCombatEvent
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil
import com.grinder.game.entity.agent.player.message
import com.grinder.game.task.TaskManager

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/08/2020
 */
class PoisonEvent(val poisonType: PoisonType) : ApplicableCombatEvent {

    override fun isApplicableTo(agent: Agent): Boolean {

        if(agent.isPoisoned)
            return false

        if(agent is Player) {

            if (!agent.combat.poisonImmunityTimer.finished()) return false

            return !EquipmentUtil.isImmuneToPoison(agent.equipment, poisonType)
        }

        if(agent is NPC)
            return !NpcDefinition.isImmuneToPoison(agent)

        return true
    }

    override fun applyTo(agent: Agent) {

        if(agent is NPC)
            agent.debug("I have been poisoned!")
        else if(agent is Player)
            agent.message("<col=00FF00>You have been poisoned!</col>")

        agent.poisonDamage = poisonType.damage
        TaskManager.submit(PoisonEffectTask(agent))
    }
}