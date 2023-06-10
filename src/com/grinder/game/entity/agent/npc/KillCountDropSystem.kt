package com.grinder.game.entity.agent.npc

import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import com.grinder.util.NpcID


enum class KillCountDropSystem(val npcId: Int, val killCountDropCount: Int, val item: Item) {

    VORKATH_ASSEMBLER(NpcID.VORKATH_8059, 30, Item(21907, 1));
}