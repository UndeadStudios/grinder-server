package com.grinder.game.content.skill.skillable.impl.runecrafting

import com.grinder.util.ItemID

enum class CombinationRune(
        val first: CraftableRune,
        val second: CraftableRune,
        val itemId: Int,
        val levelReq: Int
) {

    MIST(CraftableRune.AIR, CraftableRune.WATER, ItemID.MIST_RUNE, 6),
    DUST(CraftableRune.AIR, CraftableRune.EARTH, ItemID.DUST_RUNE, 10),
    MUD(CraftableRune.EARTH, CraftableRune.WATER, ItemID.MUD_RUNE, 13),
    SMOKE(CraftableRune.AIR, CraftableRune.FIRE, ItemID.SMOKE_RUNE, 15),
    STEAM(CraftableRune.WATER, CraftableRune.FIRE, ItemID.STEAM_RUNE, 19),
    LAVA(CraftableRune.FIRE, CraftableRune.EARTH, ItemID.LAVA_RUNE, 21)
    ;

    /**
     *  Finds the [Talisman] for both elemental runes.
     */
    fun getTalismans() = listOf(first.getTalisman()!!, second.getTalisman()!!)

    /**
     * Finds the [Altar] for both elemental runes.
     */
    fun getAltars() = listOf(first.getAltar()!!, second.getAltar()!!)

    /**
     * Finds the altar of the other rune.
     */
    fun getOtherForAltar(altar: Altar) = getAltars().first { it != altar }.rune

    /**
     * Finds the altar of the other rune.
     */
    fun getOthertalismanForAltar(altar: Altar) = getAltars().first { it != altar }.talisman


    companion object {

        /**
         * Finds the combination rune corresponding to the altar and talisman, or null.
         */
        fun getCombination(altar: Altar, talisman: Talisman): CombinationRune? {
            return values().find { it.getAltars().contains(altar) && it.getTalismans().contains(talisman) }
        }

        /**
         * Finds the comination rune that can be used as an alternative to the given rune item id.
         */
        fun getCombinationAlternativeTo(itemId: Int) = values()
                .filter { it.getAltars().map { altar -> altar.rune.itemId }.contains(itemId) }
    }
}