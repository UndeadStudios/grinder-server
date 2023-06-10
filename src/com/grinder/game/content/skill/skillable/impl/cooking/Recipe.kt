@file:JvmName("Recipe")
package com.grinder.game.content.skill.skillable.impl.cooking

import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.content.skill.skillable.DefaultSkillable
import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.getLevel
import com.grinder.game.model.Skill
import com.grinder.game.model.interfaces.dialogue.DialogueManager
import com.grinder.game.model.interfaces.menu.CreationMenu
import com.grinder.game.model.interfaces.menu.impl.SingleItemCreationMenu
import com.grinder.util.ItemID
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.min

/**
 * A hashmap of simple recipes.
 */
val simpleRecipe = HashMap<Int, Recipes>()

/**
 * Food recipe that is of 2 types of food.
 *
 * @param item1 The first item required.
 * @param ingredients The second item required.
 * @param product The result of combing both items.
 * @param level The level required to preform this recipe.
 */
enum class Recipes(val item1:Int, val ingredients:IntArray, val product:Int, val level: Int,
                   val message:String="You combine the ingredients to make ${ItemDefinition.forId(product).name}.") {
    SLICED_MUSHROOMS(ItemID.MUSHROOM, intArrayOf(ItemID.BOWL, ItemID.KNIFE), ItemID.SLICED_MUSHROOMS, 1),
    CHOPPED_UGTHANKI(ItemID.UGTHANKI_MEAT, intArrayOf(ItemID.BOWL, ItemID.KNIFE), ItemID.CHOPPED_UGTHANKI, 1),
    UGTHANKI_ONION(ItemID.UGTHANKI_MEAT, intArrayOf(ItemID.CHOPPED_ONION, ItemID.KNIFE), ItemID.UGTHANKI_AND_ONION, 1),
    ONION_UGTHANKI(ItemID.CHOPPED_ONION, intArrayOf(ItemID.UGTHANKI_MEAT, ItemID.KNIFE), ItemID.UGTHANKI_AND_ONION, 1),
    CHOPPED_TOMATO(ItemID.TOMATO, intArrayOf(ItemID.BOWL, ItemID.KNIFE), ItemID.CHOPPED_TOMATO, 1),
    CHOPPED_GARLIC(ItemID.GARLIC, intArrayOf(ItemID.BOWL, ItemID.KNIFE), ItemID.CHOPPED_GARLIC, 1),
    CHOPPED_ONION(ItemID.ONION, intArrayOf(ItemID.BOWL, ItemID.KNIFE), ItemID.CHOPPED_ONION, 1),
    CHOPPED_TUNA(ItemID.TUNA, intArrayOf(ItemID.BOWL, ItemID.KNIFE), ItemID.CHOPPED_TUNA, 1),
    MINCED_MEAT(ItemID.COOKED_MEAT, intArrayOf(ItemID.BOWL, ItemID.KNIFE), ItemID.MINCED_MEAT, 1),
    SWEETCORN_BOWL(ItemID.SWEETCORN, intArrayOf(ItemID.BOWL, ItemID.KNIFE), ItemID.SWEETCORN_3, 1),
    ONION_TOMATO(ItemID.CHOPPED_ONION, intArrayOf(ItemID.TOMATO, ItemID.KNIFE), ItemID.ONION_AND_TOMATO, 1),
    TOMATO_ONION(ItemID.CHOPPED_TOMATO, intArrayOf(ItemID.ONION, ItemID.KNIFE), ItemID.ONION_AND_TOMATO, 1),
    CRUNCHIES(ItemID.CRUNCHY_TRAY, intArrayOf(ItemID.GIANNE_DOUGH), ItemID.RAW_CRUNCHIES, 1,
        message="You put the Gianne dough into the crunch tray."),
    PIE_SHELL(ItemID.PIE_DISH, intArrayOf(ItemID.PASTRY_DOUGH), ItemID.PIE_SHELL, 10),
    REDBERRY_PIE(ItemID.REDBERRIES, intArrayOf(ItemID.PIE_SHELL), ItemID.UNCOOKED_BERRY_PIE, 10),
    UNCOOK_EGG(ItemID.EGG, intArrayOf(ItemID.BOWL), ItemID.UNCOOKED_EGG, 13),
    MEAT_PIE(ItemID.COOKED_MEAT, intArrayOf(ItemID.PIE_SHELL), ItemID.UNCOOKED_MEAT_PIE, 20),
    STEW_POTATO(ItemID.POTATO, intArrayOf(ItemID.BOWL_OF_WATER), ItemID.INCOMPLETE_STEW, 25),
    STEW(ItemID.INCOMPLETE_STEW, intArrayOf(ItemID.COOKED_MEAT), ItemID.UNCOOKED_STEW, 25),
    STEW2(ItemID.COOKED_CHICKEN, intArrayOf(ItemID.INCOMPLETE_STEW), ItemID.UNCOOKED_STEW, 25),
    BATTA(ItemID.BATTA_TIN, intArrayOf(ItemID.GIANNE_DOUGH), ItemID.RAW_BATTA, 25),
    COMPOST_SHELL(ItemID.COMPOST, intArrayOf(ItemID.PIE_SHELL), ItemID.PART_MUD_PIE, 29),
    WATER_SHELL(ItemID.PART_MUD_PIE, intArrayOf(ItemID.BUCKET_OF_WATER), ItemID.PART_MUD_PIE_3, 29),
    MUD_PIE(ItemID.PART_MUD_PIE_2, intArrayOf(ItemID.CLAY), ItemID.RAW_MUD_PIE, 29),
    APPLE_PIE(ItemID.COOKING_APPLE, intArrayOf(ItemID.PIE_SHELL), ItemID.UNCOOKED_APPLE_PIE, 30),
    GNOMEBOWL(ItemID.GNOMEBOWL_MOULD, intArrayOf(ItemID.GIANNE_DOUGH), ItemID.RAW_GNOMEBOWL, 1,
            message="You use the mould to shape the dough into a rough bowl."),
    TOMATO_SHELL(ItemID.TOMATO, intArrayOf(ItemID.PIE_SHELL), ItemID.PART_GARDEN_PIE, 34),
    ONION_SHELL(ItemID.PART_GARDEN_PIE, intArrayOf(ItemID.ONION), ItemID.PART_GARDEN_PIE_3, 34),
    GARDEN_PIE(ItemID.PART_GARDEN_PIE_2, intArrayOf(ItemID.CABBAGE), ItemID.RAW_GARDEN_PIE, 34),
    PIZZA_BASE(ItemID.PIZZA_BASE, intArrayOf(ItemID.TOMATO), ItemID.INCOMPLETE_PIZZA, 35),
    PIZZA_BASE2(ItemID.INCOMPLETE_PIZZA, intArrayOf(ItemID.CHEESE), ItemID.UNCOOKED_PIZZA, 35),
    TROUT_SHELL(ItemID.TROUT, intArrayOf(ItemID.PIE_SHELL), ItemID.PART_FISH_PIE, 47),
    COD_SHELL(ItemID.PART_FISH_PIE, intArrayOf(ItemID.COD), ItemID.PART_FISH_PIE_3, 47),
    FISH_PIE(ItemID.PART_FISH_PIE_2, intArrayOf(ItemID.POTATO), ItemID.RAW_FISH_PIE, 47),
    BOTANICAL_PIE(ItemID.GOLOVANOVA_FRUIT_TOP, intArrayOf(ItemID.PIE_SHELL), ItemID.UNCOOKED_BOTANICAL_PIE, 52),
    MUSHROOM_PIE(ItemID.MUSHROOM,intArrayOf(ItemID.PIE_SHELL), 22795, 60),
    SALMON_SHELL(ItemID.SALMON, intArrayOf(ItemID.PIE_SHELL), ItemID.PART_ADMIRAL_PIE, 70),
    TUNA_SHELL(ItemID.PART_ADMIRAL_PIE, intArrayOf(ItemID.TUNA), ItemID.PART_ADMIRAL_PIE_3, 70),
    ADMIRAL_PIE(ItemID.PART_ADMIRAL_PIE_2, intArrayOf(ItemID.POTATO), ItemID.RAW_ADMIRAL_PIE, 70),
    DRAGONFRUIT_PIE(22929,intArrayOf(ItemID.PIE_SHELL), 22795, 73),
    BEAR_SHELL(ItemID.RAW_BEAR_MEAT, intArrayOf(ItemID.PIE_SHELL), ItemID.PART_WILD_PIE, 85),
    CHOMPY_SHELL(ItemID.PART_WILD_PIE, intArrayOf(ItemID.RAW_CHOMPY), ItemID.PART_WILD_PIE_3, 85),
    WILD_PIE(ItemID.PART_WILD_PIE_2, intArrayOf(ItemID.RAW_RABBIT), ItemID.RAW_WILD_PIE, 85),
    STRAWBERRY_SHELL(ItemID.STRAWBERRY, intArrayOf(ItemID.PIE_SHELL), ItemID.PART_SUMMER_PIE, 95),
    WATERMELON_SHELL(ItemID.PART_SUMMER_PIE, intArrayOf(ItemID.WATERMELON), ItemID.PART_SUMMER_PIE_3, 95),
    SUMMER_PIE(ItemID.PART_SUMMER_PIE_2, intArrayOf(ItemID.COOKING_APPLE), ItemID.RAW_SUMMER_PIE, 95);

    companion object {
        init {
            for (recipe in values())
                simpleRecipe[recipe.item1] = recipe
        }
    }
}

/**
 * Ues the provided items to create a recipe.
 *
 * @param player The player.
 * @param slot1 The item used.
 * @param slot2 The item being used on.
 */
fun makeRecipe(player: Player, slot1: Int, slot2: Int): Boolean {
    val item1 = player.inventory[slot1]
    val item2 = player.inventory[slot2]
    val recipe = if (simpleRecipe[item1.id] != null) simpleRecipe[item1.id] else simpleRecipe[item2.id]
    if (recipe != null) {
        val otherItem = if (recipe.item1 == item1.id) item2.id else item1.id
        if (otherItem in recipe.ingredients) {
            // check if we have all the ingredients.
            val missing = recipe.ingredients.filter { item -> !player.inventory.contains(item) }
            if (missing.isNotEmpty()) {
                val str = missing.map { item -> ItemDefinition.forId(item).name }.toTypedArray()
                DialogueManager.sendStatement(player,"You also need ${str.joinToString(separator=" and ")}" +
                        " to make a ${ItemDefinition.forId(recipe.product).name}.")
                return false
            }
            val menu = SingleItemCreationMenu(player, recipe.product, "How many would you like to make?",
                    RecipeAction(player, recipe))
            player.creationMenu = Optional.of(menu.open())
            return true
        }
    }
    return false
}

/**
 * Action that is performed when clicking the item to be created in the ItemCreationMenu.
 *
 * @param player The player performing the action.
 * @param recipeRules The recipe that is being done.
 */
class RecipeAction(val player: Player, val recipeRules: Recipes) : CreationMenu.CreationMenuAction, DefaultSkillable() {
    var amount = 0
    override fun startAnimationLoop(player: Player?) {}
    override fun startGraphicsLoop(player: Player?) {}

    override fun startSoundLoop(player: Player?) {}

    override fun cyclesRequired(player: Player?): Int { return 2 }

    override fun finishedCycle(player: Player?) {
        checkNotNull(player)
        player.inventory.delete(recipeRules.item1, 1)
        for (ingredient in recipeRules.ingredients)
            if (ingredient != ItemID.KNIFE)
                player.inventory.delete(ingredient, 1)
        player.inventory.add(recipeRules.product, 1)
        player.sendMessage(recipeRules.message)
    }

    override fun loopRequirements(): Boolean { return true }
    override fun hasRequirements(player: Player?): Boolean {
        checkNotNull(player)
        if (recipeRules.level > player.getLevel(Skill.COOKING)) {
            player.sendMessage("You need a cooking level of at least ${recipeRules.level} to make " +
                    "${ItemDefinition.forId(recipeRules.product).name}.")
            return false
        }
        if (!player.inventory.contains(recipeRules.item1))
            return false
        return true
    }

    override fun allowFullInventory(): Boolean { return true }
    override fun execute(index: Int, item: Int, amount: Int) {
        this.amount = min(amount, player.inventory.getAmount(recipeRules.item1))
        for (ingredient in recipeRules.ingredients)
            this.amount = min(amount, player.inventory.getAmount(ingredient))
        SkillUtil.startSkillable(player, this)
    }
}