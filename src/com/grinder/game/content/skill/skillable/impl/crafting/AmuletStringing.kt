package com.grinder.game.content.skill.skillable.impl.crafting

import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.CommandActions
import com.grinder.game.model.ItemActions
import com.grinder.game.model.Skill
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import java.util.*

/**
 * Handles amulet stringing, i.e. using an amulet (u) with a ball of wool.
 */
object AmuletStringing {

    init {
        for (amuletType in AmuletType.values()) {
            ItemActions.onItemOnItem(ItemID.BALL_OF_WOOL to amuletType.amuletUID) {
                if (player.checkLevel(Skill.CRAFTING, amuletType.levelRequired)) {
                    val ballOfWoolItem = player.inventory[getSlot(ItemID.BALL_OF_WOOL)]
                    if (player.removeInventoryItem(ballOfWoolItem, -1)) {
                        val amuletUItemSlot = getOtherSlot(ItemID.BALL_OF_WOOL)
                        player.addExperience(Skill.CRAFTING, amuletType.experienceGain)
                        player.setInventoryItem(amuletUItemSlot, Item(amuletType.amuletID, 1), 0)
                        player.message("You put some string on your amulet.")
                    }
                }
                return@onItemOnItem true
            }
        }
        CommandActions.onCommand("amulets", PlayerRights.HIGH_STAFF) {
            for (amuletType in AmuletType.values())
                player.addInventoryItem(Item(amuletType.amuletUID, 1), -1)
            player.inventory.refreshItems()
            return@onCommand true
        }
    }

    enum class AmuletType(
            val amuletUID: Int,
            val amuletID: Int,
            val levelRequired: Int,
            val experienceGain: Int = 4)
    {
        GOLD_AMULET(ItemID.GOLD_AMULET_U_, ItemID.GOLD_AMULET, 8),
        SYMBOL(ItemID.UNSTRUNG_SYMBOL, ItemID.UNBLESSED_SYMBOL, 16),
        EMBLEM(ItemID.UNSTRUNG_EMBLEM, ItemID.UNPOWERED_SYMBOL, 17),
        SAPPHIRE_AMULET(ItemID.SAPPHIRE_AMULET_U_, ItemID.SAPPHIRE_AMULET_2, 24),
        OPAL_AMULET(ItemID.OPAL_AMULET_U_, ItemID.OPAL_AMULET, 27),
        EMERALD_AMULET(ItemID.EMERALD_AMULET_U_, ItemID.EMERALD_AMULET_2, 31),
        JADE_AMULET(ItemID.JADE_AMULET_U_, ItemID.JADE_AMULET, 34),
        TOPAZ_AMULET(ItemID.TOPAZ_AMULET_U_, ItemID.TOPAZ_AMULET, 45),
        RUBY_AMULET(ItemID.RUBY_AMULET_U_, ItemID.RUBY_AMULET_2, 50),
        DIAMOND_AMULET(ItemID.DIAMOND_AMULET_U_, ItemID.DIAMOND_AMULET_2, 70),
        DRAGONSTONE_AMULET(ItemID.DRAGONSTONE_AMULET_U_, ItemID.DRAGONSTONE_AMULET, 80),
        ONYX_AMULET(ItemID.ONYX_AMULET_U_, ItemID.ONYX_AMULET_2, 90),
        ZENYTE_AMULET(ItemID.ZENYTE_AMULET_U_, ItemID.ZENYTE_AMULET_2, 98),
    }
}