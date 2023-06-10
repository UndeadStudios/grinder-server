package com.grinder.game.content.skill.skillable.impl.runecrafting

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Position
import com.grinder.util.ItemID

enum class Talisman(val itemId: Int, val tiara: Int, val ruin: Int, val altarPos: Position) {

    AIR(
            ItemID.AIR_TALISMAN,
            ItemID.AIR_TIARA,
            30723,
            Position(2842, 4828)
    ),
    MIND(
            ItemID.MIND_TALISMAN,
            ItemID.MIND_TIARA,
            30722,
            Position(2793, 4827)
    ),
    WATER(
            ItemID.WATER_TALISMAN,
            ItemID.WATER_TIARA,
            30721,
            Position(2726, 4832)
    ),
    EARTH(
            ItemID.EARTH_TALISMAN,
            ItemID.EARTH_TIARA,
            30720,
            Position(2655, 4830)
    ),
    FIRE(
            ItemID.FIRE_TALISMAN,
            ItemID.FIRE_TIARA,
            30719,
            Position(2574, 4849)
    ),
    BODY(
            ItemID.BODY_TALISMAN,
            ItemID.BODY_TIARA,
            30718,
            Position(2522, 4834)
    ),
    COSMIC(
            ItemID.COSMIC_TALISMAN,
            ItemID.COSMIC_TIARA,
            30719,
            Position(2162, 4833)
    ),
    CHAOS(
            ItemID.CHAOS_TALISMAN,
            ItemID.CHAOS_TIARA,
            30718,
            Position(2281, 4837)
    ),
    NATURE(
            ItemID.NATURE_TALISMAN,
            ItemID.NATURE_TIARA,
            30717,
            Position(2400, 4835)
    ),
    LAW(
            ItemID.LAW_TALISMAN,
            ItemID.LAW_TIARA,
            30716,
            Position(2464, 4818)
    ),
    DEATH(
            ItemID.DEATH_TALISMAN,
            ItemID.DEATH_TIARA,
            30715,
            Position(2208, 4830)
    ),
    BLOOD(
            5516,
            5516,
            30714,
            Position(1722, 3826)
    ),
    ASTRAL(
            -1,
            ItemID.ASTRAL_TIARA,
            30713,
            Position(2153, 3861)
    ),
    WRATH(
            ItemID.WRATH_TALISMAN,
            ItemID.WRATH_TIARA,
            30712,
            Position(2335, 4831)
    )
    ;

    /**
     * Check if the player has the talisman in their inventory or is wearing the tiara.
     */
    fun playerHasTalisman(player: Player): Boolean {
        return player.inventory.contains(itemId) || player.equipment.contains(tiara)
    }


    companion object {

        /**
         * Find the [Talisman] corresponding to the given ruin object id or null.
         */
        fun getTalismanForRuin(ruin: Int) = values().find { it.ruin == ruin }

        /**
         * Finds all [Talisman]s in the player's inventory.
         */
        fun getTalismansInInventory(player: Player): Set<Talisman> {
            return player.inventory.items
                    .filterNotNull()
                    .mapNotNull { item -> values().find { it.itemId == item.id } }
                    .toSet()
        }

    }

}