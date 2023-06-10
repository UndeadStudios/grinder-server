@file:JvmName("Gnome")
package com.grinder.game.content.skill.skillable.impl.cooking

import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.addExperience
import com.grinder.game.entity.agent.player.hasItemInInventory
import com.grinder.game.model.Skill
import com.grinder.game.model.interfaces.dialogue.DialogueManager
import com.grinder.game.model.item.Item

val message = "You either don't have all the ingredients, or the cooking level required (or both) to make this."
enum class Crunchies(val halfBakedID:Int, val ingredients: Array<Item>, val preparedID:Int, val prepMsg:String,
                     val preGarnishID:Int=-1, val garnishes:Array<Item>, val garnishedProduct:Int=-1, val level:Int, val garnishExp:Int=30) {
    // Prep EXP is 30
    TOAD_CRUNCHIES(2201, arrayOf(Item(2152, 2), Item(2169)), 9581,
            "You just need to bake this then garnish this crunchies with equa leaves to complete.",
            9582, arrayOf(Item(2128)), 2217, 12, 40),
    SPICE_CRUNCHIES(2201, arrayOf(Item(2169), Item(2128)), 9579,
            "You just need to bake this then garnish this crunchies with gnome spices to complete.",
            9580, arrayOf(Item(2169)), 2213, 12, 42),
    WORM_CRUNCHIES(2201, arrayOf(Item(2162, 2), Item(2169), Item(2128)), 9583,
            "You just need to bake this then garnish this crunchies with gnome spices to complete.",
            9584, arrayOf(Item(2169)), 2205, 14, 44),
    CHOC_CRUNCHIES(2201, arrayOf(Item(1973, 2), Item(2128)), 9577,
            "You just need to bake this then garnish this crunchies with chocolate dust to complete.",
            9580, arrayOf(Item(1975)), 2209, 16, 46);
}
enum class Batta(val halfBakedID:Int, val ingredients: Array<Item>, val preparedID:Int, val prepMsg:String,
val preGarnishID:Int=-1, val garnishes:Array<Item>, val garnishedProduct:Int=-1, val level:Int, val garnishExp:Int=40) {
    // prepare exp is 40
    FRUIT_BATTA(2249, arrayOf(Item(2116), Item(2122), Item(2110), Item(2128, 4)),
            9483, "You just need to bake this then garnish this batta with gnome spices to complete.",
            9578, arrayOf(Item(9479)), 2259, 25, 80),
    TOAD_BATTA(2249, arrayOf(Item(2152), Item(2128), Item(2169), Item(1985)),
            9482, "You just need to bake this to complete.", level = 26, garnishes = emptyArray()),
    WORM_BATTA(2249, arrayOf(Item(2162), Item(1985), Item(2169)),
            9480, "You just need to bake this then garnish this batta with equa leaves to complete.",
            9481, arrayOf(Item(2128)), 2253, 27, 84),
    VEGETABLE_BATTA(-1, arrayOf(Item(1982, 2), Item(2126), Item(1957), Item(1965), Item(1985)),
            9485, "You just need to bake this then garnish this batta with equa leaves to complete.",
            9486, arrayOf(Item(9479)), 2281, 28),
    CHEESE_AND_TOMATO_BATTA(2128, arrayOf(Item(1982), Item(1985)),9483,
            "You just need to bake this then garnish this batta with equa leaves to complete.",
            9484, arrayOf(Item(2128)), 2259, 29, 88);
}
enum class Bowl(val halfBakedID:Int, val ingredients: Array<Item>, val preparedID:Int, val prepMsg:String,
                  val preGarnishID:Int=-1, val garnishes:Array<Item>, val garnishedProduct:Int=-1, val level:Int, val garnishExp:Int=50) {
    // prepare exp is 50
    WORM_HOLE(2177, arrayOf(Item(2162, 4), Item(1957, 2), Item(2169)),
            9559, "You just need to bake this then garnish with equa leaves to complete.",
            9560, arrayOf(Item(2128)), 2191, 30, 90),
    VEGETABLE_BALL(2177, arrayOf(Item(1942, 2), Item(1957, 2), Item(2169)),
            9561, "You just need to bake this then garnish with equa leaves to complete.",
            9562, arrayOf(Item(2128)), 2195, 35, 95),
    TANGLED_TOADS_LEGS(2177, arrayOf(Item(2152, 4), Item(2169), Item(1985, 2), Item(2126), Item(2128, 2)),
            9558, "You just need to bake this to complete.",
            level = 35, garnishes =arrayOf()),
    CHOCOLATE_BOMB(2177, arrayOf(Item(1973, 4), Item(2128, 1)),
            9563, "You just need to bake this then garnish with two pots of cream and chocolate dust to complete.",
            9561, arrayOf(Item(2130, 2), Item(1975)), 2185, 42, 110);
}

/**
 * Garnishes the gnome dishes.
 *
 * @param player The player performing the action.
 * @param slot1 The slot of the item used.
 * @param slot2 The slot of the item being used on.
 */
fun garnishDish(player: Player, slot1: Int, slot2: Int): Boolean {
    val item1 = player.inventory.atSlot(slot1)!!
    val item2 = player.inventory.atSlot(slot2)!!
    for (food in Crunchies.values()) {
        if (garnish(player, item1.id, item2.id, food.preGarnishID, food.garnishes, food.garnishedProduct, food.garnishExp))
            return true
    }
    for (food in Batta.values()) {
        if (garnish(player, item1.id, item2.id, food.preGarnishID, food.garnishes, food.garnishedProduct, food.garnishExp))
            return true
    }
    for (food in Bowl.values()) {
        if (garnish(player, item1.id, item2.id, food.preGarnishID, food.garnishes, food.garnishedProduct, food.garnishExp))
            return true
    }
    return false
}

/**
 * Garnish method that is shared between all gnome foods.
 *
 * @param player The player performing the garnish.
 * @param item1 The slot of the item used.
 * @param item2 The slot of the item being used on.
 * @param preGarnishID ItemID of the garnish prior.
 * @param garnishes List of items required to garnish.
 * @param garnishId ID of the garnished product.
 * @param garnishExp Experience rewarded upon garnish.
 */
private fun garnish(player:Player, item1:Int, item2:Int, preGarnishID: Int, garnishes: Array<Item>, garnishId:Int, garnishExp:Int): Boolean {
    val req = garnishes.map { it.id }
    if (item2 == preGarnishID && item1 in req || item2 in req && item1 == preGarnishID) {
        for (garnish in garnishes)
            if (!player.hasItemInInventory(garnish))
                return false
        garnishes.forEach { player.inventory.delete(it) }
        player.inventory.delete(preGarnishID, 1)
        player.inventory.add(garnishId, 1)
        player.addExperience(Skill.COOKING, garnishExp)
        DialogueManager.sendStatement(player, "You add some ${ItemDefinition.forId(garnishes[0].id)} on the top for that final touch..")
        return true
    }
    return false
}