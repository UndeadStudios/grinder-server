@file:JvmName("Cooking")
package com.grinder.game.content.skill.skillable.impl.cooking

import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.quest.QuestManager
import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.content.skill.skillable.DefaultSkillable
import com.grinder.game.content.skill.task.SkillMasterType
import com.grinder.game.content.skill.task.SkillTaskManager
import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.`object`.name
import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.Animation
import com.grinder.game.model.Skill
import com.grinder.game.model.attribute.AttributeManager
import com.grinder.game.model.interfaces.dialogue.DialogueManager
import com.grinder.game.model.interfaces.menu.impl.SingleItemCreationMenu
import com.grinder.game.model.item.Item
import com.grinder.game.model.sound.Sound
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.ObjectID
import com.grinder.util.TaskFunctions
import com.grinder.util.oldgrinder.EquipSlot
import java.lang.Integer.min
import java.util.*

/**
 * Represents a cook animation that can be used as a loop.
 *
 * @param player Player associated with animation.
 * @param anim Animation ID to be played.
 */
class CookAnimation(val player: Player, val anim: Int) : Task(4, player, true) {
    override fun execute() {
        player.performAnimation(Animation(anim))
        player.playSound(Sound(2577))
    }
}

/**
 * Determines if the attempt at cooking was a success.
 *
 * @param player The player performing action.
 * @param reqLevel required level to perform the action.
 * @return Success/Failure
 */
fun cookSuccess(player: Player, levelReq: Int, burnBonus: Int = 3, stopBurn: Int = 100): Boolean {
    // cooking cape
    if (player.equipment.containsAtSlot(EquipSlot.CAPE, 9801)
            || player.equipment.containsAtSlot(EquipSlot.CAPE, 9802) ||
        player.equipment.contains(ItemID.MAX_CAPE) || player.equipment.contains(ItemID.AVAS_MAX_CAPE) || player.equipment.contains(ItemID.ARDOUGNE_MAX_CAPE)
        || player.equipment.contains(ItemID.FIRE_MAX_CAPE)
        || player.equipment.contains(ItemID.FIRE_MAX_CAPE_2)
        || player.equipment.contains(ItemID.GUTHIX_MAX_CAPE)
        || player.equipment.contains(ItemID.INFERNAL_MAX_CAPE)
        || player.equipment.contains(ItemID.SARADOMIN_MAX_CAPE)
        || player.equipment.contains(ItemID.ZAMORAK_MAX_CAPE)
        || player.equipment.contains(ItemID.MAX_CAPE_2)
        || player.equipment.contains(ItemID.MAX_CAPE_3)
        || player.getEquipment().contains(ItemID.MYTHICAL_MAX_CAPE)
    )
        return true
    var lv = player.getLevel(Skill.COOKING).toDouble()
    var burnChance: Double = 45.0 - burnBonus
    var levNeeded = levelReq.toDouble()
    val burnStop = stopBurn.toDouble()
    val multiA = burnStop - levNeeded
    val burnDec = burnChance / multiA
    if (player.equipment.contains(ItemID.COOKING_GAUNTLETS)) {
        lv += 6.0
        levNeeded -= 5.0
    }
    if (player.equipment.contains(ItemID.WHITE_APRON) || player.equipment.contains(ItemID.BROWN_APRON)
        || player.equipment.contains(ItemID.GOLDEN_APRON)) {
        lv += 1.0
        levNeeded -= 1.0
    }
    if (lv >= stopBurn)
        return true
    val multiB = lv - levNeeded
    burnChance -= multiB * burnDec
    val randNum = Misc.getRandomDouble() * 100.0
    return burnChance <= randNum
}

/**
 * Opens a dialogue to promp the user what he would like to cook based on the item used.
 *
 * @param player The player performing the action.
 * @param item The item being used on the object.
 * @param obj The object the item is being used on.
 * @return Action handled.
 */
fun itemOnCookingObject(player: Player, item: Item, obj: Optional<GameObject>): Boolean {
    if (obj.isEmpty) return false
    val objUsed = obj.get()
    if (objUsed.definition.name == null)
        return false
    if ((objUsed.definition.name.toLowerCase().startsWith("range") || objUsed.definition.name.toLowerCase().startsWith("stove"))
                    && objUsed.definition.hasActions() && objUsed.definition.actions.contains("Cook")
            || objUsed.definition.name.toLowerCase().contains("fire") || objUsed.definition.id == ObjectID.OVEN) {
        val cookObj = cookReadyFood[item.id]
        if (cookObj == null) {
            DialogueManager.sendStatement(player, "You can't cook that.")
            return true
        }
        val cookMenu = SingleItemCreationMenu(player,
                item.id, "How many would you like to cook?") { _: Int, itemID: Int, amount: Int ->
            val maxAmount = min(amount, player.inventory.getAmount(itemID))
            SkillUtil.startSkillable(player, CookAction(cookObj, maxAmount, objUsed))
        }
        player.creationMenu = Optional.of(cookMenu.open())
        return true
    }
    return false
}

/**
 * Performs the cooking action when using a Object. Searches through the users inventory for cookable items.
 * @param player The player performing the action.
 * @param obj The gameObject being interacted with.
 */
fun useCookingObject(player: Player, obj: GameObject): Boolean {

    val name = obj.name()

    if (isFire(name) || isOven(obj) || isCookableRangeOrStove(name, obj)) {
        if (obj.definition.id == 114 && !QuestManager.hasCompletedQuest(player, "Cook's Assistant")) {
            player.sendMessage("You must complete the quest 'Cook's Assistant' to be able to use this cooking range.")
            return true;
        }
        for (meat in cookReadyFood) {
            if (player.inventory.contains(meat.key)) {
                val cookMenu = SingleItemCreationMenu(player,
                        meat.key, "How many would you like to cook?") { _: Int, _: Int, amount: Int ->
                    val maxAmount = min(amount, player.inventory.getAmount(meat.key))
                    SkillUtil.startSkillable(player, CookAction(meat.value, maxAmount, obj))
                }
                player.creationMenu = Optional.of(cookMenu.open())
                return true
            }
        }
        DialogueManager.sendStatement(player, "You haven't got anything to cook.")
        return true
    }
    return false
}

private fun isCookableRangeOrStove(
    name: String,
    obj: GameObject
) = (name.startsWith("range", ignoreCase = true) || name.startsWith("stove", ignoreCase = true)
        && obj.definition.hasActions()
        && obj.definition.actions.contains("Cook"))

private fun isOven(obj: GameObject) = obj.id == ObjectID.OVEN || obj.definition.id == 40286 || obj.definition.id == 114

private fun isFire(name: String) = name.contains("fire", ignoreCase = true)

/*
* Messages that are sent to the player while training Cooking skill
 */
private val COOKING_MESSAGES = arrayOf(
    arrayOf("@whi@You can train in the Cooking guild after reaching level 32 Cooking!"),
    arrayOf("@whi@Players wearing Cooking gauntlets, Chef’s hat, or Aprons helps reduce the food burn rate."),
    arrayOf("@whi@You can take a Cooking skill task from your master for bonus rewards."),
    arrayOf("@whi@The best location to train Cooking is in the Cooking guild."),
    arrayOf("@whi@Cooking on a cooking range provides better chances to cook food than on fire."),
    arrayOf("@whi@Cooking in the Wilderness Resource Area provides 20% bonus experience gain!"),
    arrayOf("@whi@Cooking with the skillcape equipped will give you 20% bonus experience gain!"),
    arrayOf("@whi@Players with Members rank can train in unique skilling zones with close ranges.")
)

var currentMessage: String? = null

fun sendSkillRandomMessages(player: Player) {
    currentMessage = COOKING_MESSAGES[Misc.getRandomInclusive(COOKING_MESSAGES.size - 1)][0]
    player.packetSender.sendMessage("<img=779> $currentMessage")
}

/**
 * Skilling action performed for cooking.
 *
 * @param amount The amount of attempted items to cook.
 * @param obj The Gameobject that is being used to cook.
 */
class CookAction(val food: Uncooked, var amount: Int, val obj: GameObject) : DefaultSkillable() {
    override fun startAnimationLoop(player: Player?) {
        checkNotNull(player)
        var anim = CookAnimation(player, 896)
        if (obj.definition.name.toLowerCase().contains("fire"))
            anim = CookAnimation(player, 897)
        TaskManager.submit(anim)
        tasks.add(anim)
    }

    override fun startGraphicsLoop(player: Player?) {}

    // play the sound with the animation to allow it to sync
    override fun startSoundLoop(player: Player?) {}

    override fun allowFullInventory(): Boolean { return food != Uncooked.CAKE }

    override fun finishedCycle(player: Player?) {
        checkNotNull(player)
        player.removeInventoryItem(Item(food.uncooked))
        if (food == Uncooked.CAKE)
            player.addInventoryItem(Item(ItemID.CAKE_TIN))
        if (food.burnID != -1 && !cookSuccess(player, food.req_level, stopBurn = food.burnLevel)) {

            // Process achievement
            AchievementManager.processFor(AchievementType.COOKING_APPRENTICE, player)

            // Add item
            player.addInventoryItem(Item(food.burnID))

            // Send message
            player.sendMessage("You accidentally burn the ${ItemDefinition.forId(food.cookId).name}")

            // Add points
            player.points.increase(AttributeManager.Points.COOKING_FAILURES, 1) // Increase points

        } else {
            // Process achievement
            AchievementManager.processFor(AchievementType.CHEFS_TASTE, player)

            if(player.instance != null) {
                player.cox.points += food.req_level;
            }

            // Add cooked item
            player.addInventoryItem(Item(food.cookId))

            // Add xp
            player.addExperience(Skill.COOKING, food.exp)

            // Send message
            player.sendMessage(food.message)

            player.points.increase(AttributeManager.Points.SUCCESFULL_COOKS, 1) // Increase points

            // Process skilling task
            SkillTaskManager.perform(player, food.cookId, 1, SkillMasterType.COOKING)

            // Send points message
            if ((player.points.get(AttributeManager.Points.SUCCESFULL_COOKS) % 50) == 0) {
                player.sendMessage("@blu@You have a total of " + player.points.get(AttributeManager.Points.SUCCESFULL_COOKS) +" successful cooks.")
            }

            // Send random skill messages
            if (Misc.getRandomInclusive(8) == Misc.getRandomInclusive(8) && player.skillManager.getMaxLevel(Skill.COOKING) < SkillUtil.maximumAchievableLevel()) {
                TaskFunctions.delayBy(3) {
                    sendSkillRandomMessages(player)
                }
            }

        }
        // legacy cooking; random event.
        if (player.tryRandomEventTrigger(odds = 2F))
            cancel(player)

        if (--amount <= 0)
            cancel(player)
    }

    override fun loopRequirements(): Boolean { return true }

    override fun hasRequirements(player: Player?): Boolean {
        checkNotNull(player)
        val lv = player.getLevel(Skill.COOKING)
        if (lv < food.req_level) {
            DialogueManager.sendStatement(player, "You need to be at least level ${food.req_level} Cooking to cook ${ItemDefinition.forId(food.cookId).name}.")
            return false
        }
        return player?.hasItemInInventory(Item(food.uncooked))
    }

    override fun cyclesRequired(player: Player?): Int { return 2 }
}