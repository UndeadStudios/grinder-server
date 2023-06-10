package com.grinder.game.entity.agent.npc

/**
 * This file contains extension methods of the NPC class.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   22/04/2020
 * @version 1.0
 */


fun NPC.combatLevel() = fetchDefinition()?.combatLevel?:0

fun NPC.name() = fetchDefinition()?.name?:"null-$id"

fun NPC.isRevenant() = name().contains("revenant", true)
