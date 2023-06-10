package com.grinder.game.content.item

import com.grinder.game.entity.agent.player.addInventoryItem
import com.grinder.game.entity.agent.player.checkFreeInventorySlots
import com.grinder.game.entity.agent.player.playSound
import com.grinder.game.entity.agent.player.removeInventoryItem
import com.grinder.game.entity.passedTime
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.item.Item
import com.grinder.game.model.onFirstInventoryAction
import com.grinder.game.model.sound.Sounds
import kotlin.random.Random

object CombatStarterPacks {

    init {
        onFirstInventoryAction(24130) {
            if (player.passedTime(Attribute.GENERIC_ACTION, 1)) {
                if (player.checkFreeInventorySlots(3)){
                    if (player.removeInventoryItem(Item(getItemId()), -1)){
                        player.playSound(Sounds.CASKET_OPEN)
                        val rewards = Array(3) { REWARDS.random() }
                        for (reward in rewards) {
                            val item = Item(reward.itemID, reward.getAmount())
                            player.addInventoryItem(item, -1)
                        }
                        player.inventory.refreshItems()
                    }
                }
            }
        }
    }

    private class CombatStarterPack(val itemID: Int, val minAmount: Int = 1, val maxAmount: Int = 1) {
        fun getAmount() = when (minAmount) {
            maxAmount -> minAmount
            else -> Random.nextInt(minAmount, maxAmount)
        }
    }

    private val REWARDS = arrayOf(
            CombatStarterPack(4151),
            CombatStarterPack(22978),
            CombatStarterPack(15152),
            CombatStarterPack(15155),
            CombatStarterPack(13271),
            CombatStarterPack(20724),
            CombatStarterPack(22003),
            CombatStarterPack(11284),
            CombatStarterPack(21634),
            CombatStarterPack(19553),
            CombatStarterPack(11828),
            CombatStarterPack(11830),
            CombatStarterPack(11826),
            CombatStarterPack(11832),
            CombatStarterPack(11834),
            CombatStarterPack(10551),
            CombatStarterPack(21000),
            CombatStarterPack(10828),
            CombatStarterPack(6918),
            CombatStarterPack(6916),
            CombatStarterPack(6924),
            CombatStarterPack(6920),
            CombatStarterPack(2579),
            CombatStarterPack(2577),
            CombatStarterPack(11235),
            CombatStarterPack(22622),
            CombatStarterPack(22613),
            CombatStarterPack(22296),
            CombatStarterPack(20716),
            CombatStarterPack(21733),
            CombatStarterPack(15153),
            CombatStarterPack(15156),
            CombatStarterPack(15157),
            CombatStarterPack(15164),
            CombatStarterPack(21012),
            CombatStarterPack(6570),
            CombatStarterPack(6524),
            CombatStarterPack(6528),
            CombatStarterPack(21944, 150, 500),
            CombatStarterPack(21928, 150, 500),
            CombatStarterPack(21326, 150, 500),
            CombatStarterPack(13442, 150, 500),
            CombatStarterPack(12696, 150, 500),
            CombatStarterPack(21880, 150, 500),
            CombatStarterPack(6728, 150, 500))

}