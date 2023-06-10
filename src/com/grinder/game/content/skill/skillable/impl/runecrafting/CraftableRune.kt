package com.grinder.game.content.skill.skillable.impl.runecrafting

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Skill
import com.grinder.util.ItemID
import kotlin.math.log10

enum class CraftableRune(val itemId : Int, val pureEss : Boolean, val exp : Double, val forLevel : Map<Int, Int>) {

    AIR(ItemID.AIR_RUNE, false,5.0, mapOf(1 to 1, 2 to 11, 3 to 22, 4 to 33, 5 to 44, 6 to 55, 7 to 66, 8 to 77, 9 to 88, 10 to 99)),
    MIND(ItemID.MIND_RUNE, false, 5.5, mapOf(1 to 2, 2 to 14, 3 to 28, 4 to 42, 5 to 56, 6 to 70, 8 to 84, 9 to 98)),
    WATER(ItemID.WATER_RUNE, false, 6.0, mapOf(1 to 5, 2 to 19, 3 to 38, 4 to 57, 5 to 76, 6 to 95)),
    EARTH(ItemID.EARTH_RUNE, false, 6.5, mapOf(1 to 9, 2 to 26, 3 to 52, 4 to 78)),
    FIRE(ItemID.FIRE_RUNE, false, 7.0, mapOf(1 to 14, 2 to 35, 3 to 70)),
    BODY(ItemID.BODY_RUNE, false, 7.5, mapOf(1 to 20, 2 to 46, 3 to 92)),
    COSMIC(ItemID.COSMIC_RUNE, true, 8.0, mapOf(1 to 27, 2 to 59)),
    CHAOS(ItemID.CHAOS_RUNE, true, 8.5, mapOf(1 to 35, 2 to 74)),
    ASTRAL(ItemID.ASTRAL_RUNE, true, 8.7, mapOf(1 to 40, 2 to 82)),
    WRATH(ItemID.WRATH_RUNE, true, 8.0, mapOf(1 to 95)),
    NATURE(ItemID.NATURE_RUNE, true, 9.0, mapOf(1 to 44, 2 to 91)),
    LAW(ItemID.LAW_RUNE, true, 9.5, mapOf(1 to 54, 2 to 95)),
    DEATH(ItemID.DEATH_RUNE, true, 10.0, mapOf(1 to 65, 2 to 99)),
    // TODO: ADD SUPPORT FOR PROPER SOUL RUNE CRAFTING WITH FRAGMENTS
    BLOOD(ItemID.BLOOD_RUNE, true, 11.9, mapOf(1 to 77))
    ;

    /**
     * Finds the number of runes that can be crafted per essence at the given level.
     */
    fun amountForLevel(level : Int): Int {
        val list = forLevel.toSortedMap().filter { it.value <= level}
        return if(list.isEmpty()) 0 else list.keys.last()
    }

    /**
     * Finds the minimum level required to craft the rune.
     */
    fun requiredLevel() : Int = forLevel.toSortedMap().values.first()

    /**
     * Whether the rune can be crafted at the given level.
     */
    fun canCraft(level : Int) = amountForLevel(level) > 0

    /**
     * Finds the correct essence item id, pure of regular as required.
     */
    fun getEssenceId() = if(pureEss) ItemID.PURE_ESSENCE else ItemID.RUNE_ESSENCE

    /**
     * Calculate the probability of crafting a specific rune at the Ourania altar.
     */
    fun getOuraniaProbability(player : Player): Double {
        val levelCoeff = log10(player.skills.getLevel(Skill.RUNECRAFTING).toDouble() + 10.0)
        val reqCoeff = 2*forLevel.values.first().toDouble()+ 30.0
        return 5.0 / reqCoeff * levelCoeff
    }

    /**
     *  Finds the [Altar] corresponding to this rune.
     */
    fun getAltar() = Altar.values().find { it.rune == this }

    /**
     *  Finds the [Talisman] corresponding to this rune.
     */
    fun getTalisman() = getAltar()?.talisman

    companion object {

        /**
         * Finds the craftable rune corresponding to a given rune item id.
         */
        fun getRune(itemId: Int) = values().find { it.itemId == itemId }
    }
}