package com.grinder.game.content.skill.skillable.impl.runecrafting.pouch

import com.grinder.util.ItemID

enum class PouchType(
        val itemId : Int,
        val levelReq : Int,
        val capacity : Int,
        val degradeChance : Double,
        val degradeAmount : Int
) {
    SMALL(ItemID.SMALL_POUCH, 1, 3, 0.0, 0),
    MEDIUM(ItemID.MEDIUM_POUCH, 25,6, 1.0 / (45 * 9), 3),
    LARGE(ItemID.LARGE_POUCH, 50, 9, 1.0 / (29 * 9), 2),
    GIANT(ItemID.GIANT_POUCH, 75, 12, 1.0 / (10 * 12), 3),
    COLOSSAL(ItemID.COLOSSAL_POUCH, 85, 40, 0.0, 0)
    ;

    /**
     * Creates an empty [EssencePouch] with the correct capacity and name.
     */
    fun emptyPouch() = EssencePouch(capacity, this)

    /**
     * Name of the pouch as it appears in messages to the player.
     */
    fun getName() = "${toString().toLowerCase()} pouch"

    companion object {

        /**
         *  Finds the pouch with the same item id.
         */
        fun getPouchForItem(itemId: Int) = values().find { it.itemId == itemId }

        /**
         *  Whether the given item id corresponds to a pouch.
         */
        fun itemIsPouch(itemId: Int) = getPouchForItem(itemId) != null

    }
}