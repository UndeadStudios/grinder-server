package com.grinder.game.content.pvm

import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeNpc
import com.grinder.game.content.minigame.aquaisneige.monsters.TheInadequacy
import com.grinder.game.content.minigame.fightcave.FightCaveNpc
import com.grinder.game.content.minigame.fightcave.monsters.TzTokJad
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerManager
import com.grinder.game.content.task_new.PlayerTaskManager
import com.grinder.game.definition.NpcDefinition
import com.grinder.game.entity.agent.combat.event.impl.KilledTargetEvent
import com.grinder.game.entity.agent.inWilderness
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.zaros.NexGuard
import com.grinder.game.entity.agent.npc.name
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.model.CombatActions

/**
 * Handles miscellaneous behaviour on [NPC] kill.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   22/04/2020
 * @version 1.0
 */
object MonsterKilling {

    private val BOSSES_LIST = intArrayOf(
            319, 6503, 2054, 6505, 6492, 6494, 6495, 6496, 6497, 6498, 6499, 6501, 6506, 4067, 4922, 6618, 6615, 6619, 6611, 6593,
            6636, 6640, 6360, 6356, 6354, 6350, 6351, 3162, 2215, 2205, 5822, 1101, 5779, 5862, 7307, 600, 6504, 6609, 239, 3127, 6612, 3129,
            1443, 3458, 3459, 3473, 3474, 3475, 3962, 3964, 6477, 2042, 2264, 2265, 2266, 1227, 1230, 1233, 7144, 4315, 6766, 6767, 7573, 7574, 7744, 7745, 882, 2043, 2044, 8061, 8615, 8616, 8617, 8618, 6819, 8620, 8621, 8622,
            7573, 7574, 7744, 7745, 7144, 7145, 7146, 7147, 7148, 7149, 7152, 7416, 8195, 11278, 11279, 11280, 11281, 11282 )

    init {
        CombatActions.onEvent(KilledTargetEvent::class) {
            combatEvent.ifPlayerKilledNpc { player, npc ->
                if (npc is Boss || BOSSES_LIST.contains(npc.id)) {
                    if (!npc.inWilderness()) { // Exclude announcement for wilderness bosses/npcs
                        if (npc is FightCaveNpc) {
                            if (npc is TzTokJad)
                                PlayerUtil.broadcastMessage("<img=789> @red@" + PlayerUtil.getImages(player) + "" + player.username + " has just defeated ${npc.name()} in the Fight Caves minigame!");
                        } else if (npc is AquaisNeigeNpc) {
                            if (npc is TheInadequacy)
                                PlayerUtil.broadcastMessage("<img=789> @red@" + PlayerUtil.getImages(player) + "" + player.username + " has just defeated ${npc.name()} in the Aquais Neige minigame!");
                        } else if (!(npc is NexGuard)) {
                            PlayerUtil.broadcastMessage("<img=789> @whi@" + PlayerUtil.getImages(player) + "" + player.username + " has just defeated ${npc.name()}!");
                        }
                    }
                }
                if (npc.isActive && npc.id <= 9000) {
                    SlayerManager.progress(player, npc)
                    PlayerTaskManager.progressCombatTask(player, npc)
                }
            }
        }
    }

    /**
     * Check if the [npc] is a boss.
     */
    fun isBoss(npc: NPC) = npc is Boss || BOSSES_LIST.contains(npc.id)

    fun isBossesId(npc: NpcDefinition) = BOSSES_LIST.contains(npc.id)
}