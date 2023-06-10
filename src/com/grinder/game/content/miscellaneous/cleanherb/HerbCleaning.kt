package com.grinder.game.content.miscellaneous.cleanherb

import com.grinder.game.content.skill.skillable.impl.herblore.PotionIngredientHerbType
import com.grinder.game.content.skill.skillable.impl.herblore.PotionIngredientHerbType.*
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID

object HerbCleaning {

    /**
     * Price charges per cleaning each herb
     */
    val COSTS = mapOf(
            GUAM to 20_000,
            MARRENTILL to 30_000,
            TARROMIN to 35_000,
            HARRALANDER to 40_000,
            RANARR to 50_000,
            TOADFLAX to 75_000,
            IRIT to 40_000,
            AVANTOE to 40_000,
            KWUARM to 100_000,
            SNAPDRAGON to 100_000,
            CADANTINE to 100_000,
            LANTADYME to 75_000,
            DWARFWEED to 125_000,
            TORSTOL to 200_000
    )

    /**
     * Cleans all noted herbs in the player's inventory and removes the cost.
     */
    fun cleanHerbs(player: Player, toClean: Map<PotionIngredientHerbType, Int>, cost: Int) {
        player.inventory.delete(Item(ItemID.COINS, cost))

        // Replace all grimy notes with clean notes
        toClean.forEach {
            player.inventory.delete(it.key.grimyHerb + 1, it.value)
            player.inventory.add(Item(it.key.cleanHerb + 1, it.value))
        }

        player.message("The herbs in your inventory have been cleaned.")
    }

    /**
     * Finds all noted grimy herbs in the player's inventory and creates
     * a map of the [PotionIngredientHerbType] and the amount.
     */
    fun findHerbsToClean(player: Player): Map<PotionIngredientHerbType, Int> {
        return player.inventory.items
                .filterNotNull()
                .filter { it.definition.isNoted }
                .filter { isGrimyHerb(it.definition.unNote()) }
                .associateBy ({ getGrimyHerb(it.definition.unNote())!! }, {it.amount.coerceAtMost(250)}) // 500 Amount cap due to issues showing negative value when cleaning a lot of herbs and it goes beyond 2147m
    }

    /**
     * Maps the item id to the relevant grimy herb id, or null.
     */
    fun getGrimyHerb(id : Int): PotionIngredientHerbType? {
        return PotionIngredientHerbType.values().find { it.grimyHerb == id }
    }

    /**
     * Whether the item id corresponds to a grimy herb defined in [PotionIngredientHerbType]
     */
    fun isGrimyHerb(id : Int) = getGrimyHerb(id) !== null

    /**
     * Finds the price of cleaning the herb from [COSTS] and thrown an error if it is not found.
     */
    fun getPriceForHerb(herb : PotionIngredientHerbType): Int {
        return COSTS[herb] ?: error("Trying to clean herb $herb that does not have a cost set.")
    }
}