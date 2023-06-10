package com.grinder.game.content.skill.skillable.impl.mining

import com.grinder.util.ItemID
import com.grinder.util.Misc

/**
 * @author Zach (zach@findzach.com)
 * @since 12/17/2020
 *
 * Details for Gem Mining
 */
enum class GemType(val uncutId: Int, val mineRarity: Int) {

    OPAL(ItemID.UNCUT_OPAL, 55),
    JADE(ItemID.UNCUT_JADE, 45),
    RED_TOPAZ(ItemID.UNCUT_RED_TOPAZ, 30),
    SAPPHIRE(ItemID.UNCUT_SAPPHIRE, 20),
    EMERALD(ItemID.UNCUT_EMERALD, 20),
    RUBY(ItemID.UNCUT_RUBY, 15),
    DIAMOND(ItemID.UNCUT_DIAMOND, 10),
    DRAGON_STONE(ItemID.UNCUT_DRAGONSTONE, 2);

    companion object {

        @JvmStatic
        fun generateGemType(): GemType {
            var gemChance = Misc.random(1, 100)

            var currlowestRarity = 100;
            var lowestGemType = OPAL

            values().forEach { gemType ->
                if (gemChance <= gemType.mineRarity && gemChance < currlowestRarity) {
                    lowestGemType = if (gemType.mineRarity != 20) gemType else if (Misc.randomBoolean()) SAPPHIRE else EMERALD;
                }
            }
            return lowestGemType
        }
    }

}