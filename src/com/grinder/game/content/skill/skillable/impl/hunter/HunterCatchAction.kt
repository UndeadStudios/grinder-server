package com.grinder.game.content.skill.skillable.impl.hunter

import com.grinder.game.content.skill.SkillRequirement
import com.grinder.game.content.skill.skillable.SkillActionTask
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Skill
import kotlin.random.Random

/**
 * This class represents a [SkillActionTask] for the hunter skill.
 *
 * @param type  the type of the prey
 * @param npc   the npc instance of the prey
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   29/11/2019
 * @version 1.0
 */
class HunterCatchAction(private val type: HunterCatchType, val npc: NPC)
    : SkillActionTask(
        arrayOf(type.tool.emptyItem, type.tool.requiredItem),
        arrayOf(type.tool.emptyItem),
        arrayOf(type.tool.containedItem),
        SkillRequirement(
                Skill.HUNTER,
                type.requiredLevel,
                type.experienceGain
        ),
        Skill.HUNTER) {

    override fun success(player: Player): Boolean {

        val requiredLevel = type.requiredLevel
        val playerLevel = player.skillManager.getCurrentLevel(Skill.HUNTER)
        val success = Random.nextInt(requiredLevel) <= Random.nextInt(playerLevel + 8)

        val state = if(success)
            HunterCatchState.SUCCESS
        else
            HunterCatchState.FAILED

        val result = HunterCatchResult(player, npc, type, state)

        type.technique.function.invoke(result)

        return success
    }
}