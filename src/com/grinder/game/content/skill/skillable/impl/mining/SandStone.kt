package com.grinder.game.content.skill.skillable.impl.mining

import com.grinder.util.ItemID
import com.grinder.util.Misc

/**
 *
 * Details for Sandstone Mining
 */
enum class SandStone(val sandstoneId: Int, val mineRarity: Int) {

    SANDSTONE_1KG(ItemID.SANDSTONE_1KG_, 55),
    SANDSTONE_2KG(ItemID.SANDSTONE_2KG_, 20),
    SANDSTONE_5KG(ItemID.SANDSTONE_5KG_, 10),
    SANDSTONE_10KG(ItemID.SANDSTONE_10KG_, 2);

    companion object {

        @JvmStatic
        fun generateSandStoneType(): SandStone {
            var stoneChance = Misc.random(1, 100)

            var currlowestRarity = 100;
            var lowestStoneType = SANDSTONE_1KG

            values().forEach { sandstoneType ->
                if (stoneChance <= sandstoneType.mineRarity && stoneChance < currlowestRarity) {
                    lowestStoneType = sandstoneType;
                }
            }
            return lowestStoneType
        }
    }

}