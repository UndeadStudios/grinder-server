package com.grinder.game.content.skill.skillable.impl.mining

import com.grinder.util.ItemID
import com.grinder.util.Misc

/**
 *
 * Details for Granite Mining
 */
enum class Granite(val graniteId: Int, val mineRarity: Int) {

    GRANITE_500G(ItemID.GRANITE_500G_, 55),
    GRANITE_2KG(ItemID.GRANITE_2KG_, 20),
    GRANITE_5KG(ItemID.GRANITE_5KG_, 2);

    companion object {

        @JvmStatic
        fun generateGraniteRockType(): Granite {
            var graniteChance = Misc.random(1, 100)

            var currlowestRarity = 100;
            var lowestGraniteType = GRANITE_500G

            values().forEach { graniteType ->
                if (graniteChance <= graniteType.mineRarity && graniteChance < currlowestRarity) {
                    lowestGraniteType = graniteType;
                }
            }
            return lowestGraniteType
        }
    }

}