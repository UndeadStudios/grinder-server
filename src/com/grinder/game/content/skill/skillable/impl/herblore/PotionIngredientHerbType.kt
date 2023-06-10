package com.grinder.game.content.skill.skillable.impl.herblore

import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import java.util.*

/**
 * Represents a herb which can be cleaned in order for it to be used as an
 * ingridient in potions for the Herblore skill.
 *
 * @author Professor Oak
 */
enum class PotionIngredientHerbType(val grimyHerb: Int, val cleanHerb: Int, val levelReq: Int, val exp: Int) {
    GUAM(ItemID.GRIMY_GUAM_LEAF, ItemID.GUAM_LEAF, 1, 2),
    ROGUES_PURSE(ItemID.GRIMY_ROGUES_PURSE, ItemID.ROGUES_PURSE, 3, 3),
    SNAKE_WEED(ItemID.GRIMY_SNAKE_WEED, ItemID.SNAKE_WEED, 3, 3),
    MARRENTILL(ItemID.GRIMY_MARRENTILL, ItemID.MARRENTILL, 5, 4),
    TARROMIN(ItemID.GRIMY_TARROMIN, ItemID.TARROMIN, 11, 5),
    HARRALANDER(ItemID.GRIMY_HARRALANDER, ItemID.HARRALANDER, 20, 6),
    RANARR(ItemID.GRIMY_RANARR_WEED, ItemID.RANARR_WEED, 25, 7),
    TOADFLAX(ItemID.GRIMY_TOADFLAX, ItemID.TOADFLAX, 30, 8),
    IRIT(ItemID.GRIMY_IRIT_LEAF, ItemID.IRIT_LEAF, 40, 10),
    AVANTOE(ItemID.GRIMY_AVANTOE, ItemID.AVANTOE, 48, 12),
    KWUARM(ItemID.GRIMY_KWUARM, ItemID.KWUARM, 54, 13),
    SNAPDRAGON(ItemID.GRIMY_SNAPDRAGON, ItemID.SNAPDRAGON, 59, 13),
    CADANTINE(ItemID.GRIMY_CADANTINE, ItemID.CADANTINE, 65, 14),
    LANTADYME(ItemID.GRIMY_LANTADYME, ItemID.LANTADYME, 67, 16),
    DWARFWEED(ItemID.GRIMY_DWARF_WEED, ItemID.DWARF_WEED, 70, 18),
    TORSTOL(ItemID.GRIMY_TORSTOL, ItemID.TORSTOL, 75, 21),
    GOLPAR(ItemID.GRIMY_GOLPAR, ItemID.GOLPAR, 47, 1),
    BUCHU(ItemID.GRIMY_BUCHU_LEAF, ItemID.BUCHU_LEAF, 52, 2),
    NOXIFER(ItemID.GRIMY_NOXIFER, ItemID.NOXIFER, 60, 3),

    ;

    companion object {
        @JvmField
        var herbs: MutableMap<Int, PotionIngredientHerbType> = HashMap()

        init {
            for (herb in values()) {
                herbs[herb.grimyHerb] = herb
            }
        }
    }
}