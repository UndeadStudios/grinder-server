package com.grinder.game.content.skill.skillable.impl.crafting

import com.grinder.util.ItemID

/**
 * Glass Blowing Data
 * @author Alex241
 * @since 16/12/2020
 **/


enum class GlassBlowingData(val itemId: Int, val levelRequired: Int, val rewardExperience: Double, val itemName: String, val buttonId: Int) {

    BEER_GLASS(ItemID.BEER_GLASS, 1, 17.0, "Beer Glass", 12400),
    EMPTY_CANDLE_LANTERN(ItemID.EMPTY_CANDLE_LANTERN, 4, 19.0, "Empty Candle Lantern", 12404),
    EMPTY_OIL_LAMP(ItemID.EMPTY_OIL_LAMP, 12, 25.0, "Empty Oil Lamp", 12408),
    VIAL(ItemID.EMPTY_VIAL, 33, 35.0, "Vial", 11474),
    FISHBOWL(ItemID.FISHBOWL, 42, 42.5, "Fishbowl", 6203),
    UNPOWERED_ORB(ItemID.UNPOWERED_ORB, 46, 52.5, "Unpowered Orb", 12396),
    LANTERN_LENS(ItemID.LANTERN_LENS, 49, 55.0, "Lantern Lens", 12412),
}