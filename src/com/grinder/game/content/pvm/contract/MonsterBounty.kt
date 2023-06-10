package com.grinder.game.content.pvm.contract

import com.grinder.game.definition.NpcDefinition
import com.grinder.game.model.item.Item

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   04/04/2020
 * @version 1.0
 */
class MonsterBounty(val npcId: Int, val expirationTime: Int, val itemReward: Item, val pointReward: Int) {

    fun getNpcName() = NpcDefinition.forId(npcId).name
}