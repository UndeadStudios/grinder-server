package com.grinder.game.content.pvm

import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeNpc
import com.grinder.game.content.minigame.fightcave.FightCaveNpc
import com.grinder.game.entity.agent.combat.event.impl.KilledTargetEvent
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.combatLevel
import com.grinder.game.entity.agent.npc.monster.boss.impl.GiantSeaSnakeBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.SeaTrollQueenBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.zulrah.ZulrahBoss
import com.grinder.game.entity.agent.npc.name
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.entity.grounditem.ItemOnGroundManager
import com.grinder.game.model.CombatActions
import com.grinder.game.model.Position
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import com.grinder.util.Misc

object MonsterCaskets {

    private const val CASKET_RATE = 0.4F // 1/250

    init {
        CombatActions.onEvent(KilledTargetEvent::class) {
            combatEvent.ifPlayerKilledNpc { player, npc ->
                val npcName = npc.name()
                val npcCombatLevel = npc.combatLevel()
                if (npcCombatLevel > 20) {
                    if (Misc.randomChance(CASKET_RATE)) {
                       dropCaskets(player, npc)
                    }
                }
            }
        }
    }

    /**
     * Dropping random caskets
     */
    private fun dropCaskets(player: Player, npc: NPC) {

        val level = npc.fetchDefinition().combatLevel

        if (npc is GiantSeaSnakeBoss) {
            ItemOnGroundManager.register(player, Item(ItemID.CASKET_HARD_), Position(2463, 4781, 0))
            return
        }

        if (player.minigame != null) {
            return
        }

        if (npc is FightCaveNpc) {
            return;
        }

        if (npc is AquaisNeigeNpc) {
            return;
        }

        if (npc is SeaTrollQueenBoss) {
            ItemOnGroundManager.register(player, Item(ItemID.CASKET_HARD_), Position(2505, 3898, 0))
            return
        }

        if (npc is ZulrahBoss || npc is ZulrahBoss.SnakelingMinion) {
            ItemOnGroundManager.register(player, Item(ItemID.CASKET_ELITE_), player.position)
            return
        }

        val casketId = when {
            level in 2..70 -> ItemID.CASKET_EASY_
            level in 72..140 -> ItemID.CASKET_MEDIUM_
            level in 142..220 -> ItemID.CASKET_HARD_
            level in 222..360 -> ItemID.CASKET_ELITE_
            level > 361 -> ItemID.CASKET_ELITE_
            else -> ItemID.CASKET_EASY_
        }
        PlayerUtil.broadcastMessage("<img=767> " + PlayerUtil.getImages(player) + "" + player.username +" has just received a rare casket drop from @dre@${npc.name()}</col>!")
        ItemOnGroundManager.register(player, Item(casketId), npc.position.clone())
    }
}