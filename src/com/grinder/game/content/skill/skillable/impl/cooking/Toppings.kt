@file:JvmName("Toppings")
package com.grinder.game.content.skill.skillable.impl.cooking

import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.addExperience
import com.grinder.game.entity.agent.player.getLevel
import com.grinder.game.model.Skill
import com.grinder.util.ItemID
// This might be worth merging into Recipes.
/**
 * Mapping of toppings for the foodToppings. By using toppings we avoid conflict.
 */
val foodTopping = HashMap<Int, FoodTopping>()

/**
 * Provides information on adding toppings to certain foods.
 * @param baseID Base itemID required to combine with topping.
 * @param topping ToppingID required to make the product.
 * @param product Finished product after combing toppings.
 * @param lv Level required to cook.
 * @param exp Experience gained.
 */
enum class FoodTopping(val baseID:Int, val topping:Int, val product:Int, val lv:Int, val exp:Int) {
    SPICY_SAUCE(7074,2169, 7072,9,25),
    CHILLI_CON_CARNE(7072,2142, 7062,11,25),
    EGG_AND_TOMATO(7078,1982,7064,23,45),
    POTATO_WITH_BUTTER(6697,6701,6703,39,40),
    CHILLI_POTATO(6703,7062, 7054,41,15),
    MEAT_PIZZA_CHICKEN(2289, 2140,2293, 45, 26),
    MEAT_PIZZA_MEAT(2289, ItemID.COOKED_MEAT,2293, 45, 26),
    CHEESE_POTATO(6703,1985,6705,47,40),
    CHOCOLATE_CAKE(1891, 1973,1897, 50, 30),
    EGG_POTATO(6703,7064,7056,51,45),
    ANCHOVIE_PIZZA(2289, 319,19138, 55, 39),
    MUSHROOM_ONION(7082,7084,7066,57,120),
    MUSHROOM_POTATO(6703,7066,7058,64,55),
    PINEAPPLE_PIZZA(2289, 2301,2301, 65, 45),
    TUNA_AND_CORN(7086,7088,7068,67,0),
    TUNA_POTATO(6703,7068,7060,68,10);

    companion object {
        init {
            for (topping in values())
                foodTopping[topping.topping] = topping
        }
    }
}

/**
 * Check to see if the given two slots are items that can be combined for cooking.
 *
 * @param player The player trying to add items together.
 * @param slot1 The slot of the item being used.
 * @param slot2 The slot of the item that was used on.
 */
fun addToppings(player: Player, slot1: Int, slot2: Int): Boolean {
    val item1 = player.inventory.atSlot(slot1)
    val item2 = player.inventory.atSlot(slot2)
    // check if at least one of the items is a baseItem.
    var topping = foodTopping[item1.id]
    if (topping == null) {
        topping = foodTopping[item2.id]
        if (topping == null)
            return false
    }
    if ((item1.id != topping.baseID && item2.id != topping.baseID)
            || (item1.id != topping.topping && item2.id != topping.topping))
        return false
    if (player.getLevel(Skill.COOKING) < topping.lv) {
        player.sendMessage("You need a Cooking level of at least ${topping.lv} to do this.")
        return true
    }
    player.inventory.delete(topping.baseID, 1)
    player.inventory.delete(topping.topping, 1)
    player.inventory.add(topping.product, 1)
    player.addExperience(Skill.COOKING, topping.exp)
    player.sendMessage("You add the topping to the ${ItemDefinition.forId(topping.baseID).name}.")
    return true
}