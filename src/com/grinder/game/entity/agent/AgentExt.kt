package com.grinder.game.entity.agent

import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.attribute.Attribute

fun Agent.inWilderness() = AreaManager.inWilderness(this)

fun Agent.getWildernessLevel() = attributes.numInt(Attribute.WILDERNESS_LEVEL);

fun Agent.combatLevel() = when (this) {
    is Player -> skillManager.calculateCombatLevel()
    is NPC -> statsDefinition.combatLevel
    else -> 0
}

fun Agent.combatLevelCapped_126() = when (this) {
    is Player -> skillManager.calculateCombatLevelCapped_126()
    is NPC -> statsDefinition.combatLevel
    else -> 0
}