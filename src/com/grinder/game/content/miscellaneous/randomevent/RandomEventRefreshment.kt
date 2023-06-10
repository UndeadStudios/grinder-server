package com.grinder.game.content.miscellaneous.randomevent

import com.grinder.game.World
import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.getInt
import com.grinder.game.entity.passedTime
import com.grinder.game.entity.setBoolean
import com.grinder.game.model.*
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueManager
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.TaskFunctions.delayBy
import java.text.NumberFormat
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * TODO: add documentation
 * Not sure who authored the original code of this :)
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   10/04/2020
 * @version 1.0
 */
enum class RandomEventRefreshment(val buttonId: Int) {

    PIE(16141),
    KEBAB(16142),
    CHOCOLATE(16143),
    BAGUETTE(16137),
    TRIANGLE(16138),
    SQUARE(16139),
    BREAD(16140);

    fun asSelectText() = "Please select the ${name.toLowerCase()}"


    companion object {

        var RANDOM_EVENT_REWARDS = intArrayOf(
            6962,
            23312,
            23315,
            23318,
            24872,
            24874,
            24876,
            24878,
            ItemID.SPINACH_ROLL,
            ItemID.MYSTERY_BOX
        )
        init {
            ButtonActions.onClick(4550, 4551, 4552, 4553) {

                if (player.interfaceId != 4543 && player.interfaceId != 16135)
                    return@onClick

                if (!player.passedTime(Attribute.GENERIC_ACTION, 1, TimeUnit.SECONDS))
                    return@onClick

                if (!player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT))
                    return@onClick

                if(!player.getBoolean(Attribute.DOING_FOOD_PUZZLE))
                    return@onClick

                val correct = when(player.getInt(Attribute.RANDOM_EVENT_PUZZLE)) {
                    1 -> id == 4550
                    2 -> id == 4551
                    3 -> id == 4552
                    else -> false
                }

                if(id != 4553) {
                    if (correct) {
                        player.message("The answer was correct.")
                        if (!player.getBoolean(Attribute.RANDOM_FORFEIT, false)) {
                            player.setBoolean(Attribute.HAS_PENDING_RANDOM_EVENT, false)
                            val amount = 500000 + Misc.getRandomInclusive(500000)
                            delayBy(4) {
                                player.addInventoryItem(Item(ItemID.COINS, amount))
                            }
                            player.message("You have received " + NumberFormat.getIntegerInstance().format(amount.toLong()) + " coins as a reward!")

                            val rewardItem = RANDOM_EVENT_REWARDS[Misc.getRandomInclusive(RANDOM_EVENT_REWARDS.size - 1)]
                            if (Misc.getRandomInclusive(25) == 1) {
                                delayBy(4) {
                                    player.addInventoryItem(Item(rewardItem, 1))
                                }
                                player.message("@red@You have received a bonus random event reward for your participation.")
                                player.getCollectionLog().createOrUpdateEntry(player, "Random Events",
                                    Item(ItemID.FROG_TOKEN)
                                )
                            } else if (Misc.random(2) == 1) {
                                player.sendMessage("@red@You got a bonus Book of knowledge reward for finishing the event.");
                                delayBy(4) {
                                    ItemContainerUtil.addOrDrop(
                                        player.inventory,
                                        player,
                                        Item(ItemID.BOOK_OF_KNOWLEDGE, 1)
                                    )
                                }
                            }

                            player.playSound(Sounds.USING_LAMP_REWARD)
                        }
                        player.setBoolean(Attribute.RANDOM_FORFEIT, false)
                    } else {
                        player.message("Incorrect. You have forfeited your chance at obtaining a Random Event Point.")
                        player.playSound(Sounds.INVENTORY_FULL_SOUND)
                        player.setBoolean(Attribute.RANDOM_FORFEIT, true)
                    }

                    onPuzzleCompletion(player)
                }
            }
            values().forEach { refreshment ->
                ButtonActions.onClick(refreshment.buttonId) {

                    if (player.interfaceId != 4543 && player.interfaceId != 16135)
                        return@onClick

                    if (!player.passedTime(Attribute.GENERIC_ACTION, 1, TimeUnit.SECONDS))
                        return@onClick

                    if (!player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2))
                        return@onClick

                    if(!player.getBoolean(Attribute.DOING_FOOD_PUZZLE))
                        return@onClick

                    player.removeInterfaces()

                    val selected = values().find { id == it.buttonId }

                    if(player.selectedRefreshment == selected){
                        player.message("The answer was correct.")
                        if (!player.getBoolean(Attribute.RANDOM_FORFEIT, false)) {
                            player.setBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2, false)
                            val amount = Random.nextInt(500_000, 1_000_000)
                            val reward = Item(ItemID.COINS, amount)
                            ItemContainerUtil.addOrDrop(player.inventory, player, reward)
                            val message = "@red@You have received " + NumberFormat.getIntegerInstance().format(amount.toLong()) + " coins as a reward!"
                            DialogueBuilder(DialogueType.ITEM_STATEMENT)
                                    .setItem(ItemID.COINS, 200, "Reward")
                                    .setText(message)
                                    .start(player)
                            player.message(message)
                            val rewardItem = RANDOM_EVENT_REWARDS[Misc.getRandomInclusive(RANDOM_EVENT_REWARDS.size - 1)]
                            if (Misc.getRandomInclusive(25) == 1) {
                                player.addInventoryItem(Item(rewardItem, 1))
                                player.message("@red@You have received a bonus random event reward for your participation.")
                                player.getCollectionLog().createOrUpdateEntry(player, "Random Events",
                                    Item(ItemID.FROG_TOKEN)
                                )
                            } else if (Misc.random(2) == 1) {
                                player.sendMessage("@red@You got a bonus Book of knowledge reward for finishing the event.");
                                ItemContainerUtil.addOrDrop(player.inventory, player,  Item(ItemID.BOOK_OF_KNOWLEDGE, 1))
                            }
                            player.playSound(Sounds.USING_LAMP_REWARD)
                        }
                        player.setBoolean(Attribute.RANDOM_FORFEIT, false)
                    } else {
                        val message = "@red@Incorrect. You have forfeited your chance at obtaining a Random Event Point."
                        DialogueManager.sendStatement(player, message)
                        player.message(message)
                        player.playSound(Sounds.INVENTORY_FULL_SOUND)
                        player.setBoolean(Attribute.RANDOM_FORFEIT, true)
                    }
                    player.setBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2, false)
                    if (player.getBoolean(Attribute.RANDOM_FORFEIT, false)) {
                        val randomLocation = if (Misc.random(3) == 1)
                            Position(1839 + Misc.random(2), 4912 + Misc.random(1))
                            else if (Misc.random(2) == 1)
                            Position(3179 + Misc.random(2), 10024 + Misc.random(1))
                            else Position(3228 + Misc.random(1), 9899 + Misc.random(1))
                        player.moveTo(randomLocation)
                        player.performGraphic(Graphic(721))
                        player.message("..!? Whhaat happened?..")
                        player.message("A magical power lifts you to a random dead place...")
                        player.packetSender.sendJinglebitMusic(4, 0)
                        World.spawn(TileGraphic(randomLocation, Graphic(86, GraphicHeight.MIDDLE)))
                        World.spawn(TileGraphic(randomLocation, Graphic(721, 20, GraphicHeight.MIDDLE)))
                        player.packetSender.sendAreaPlayerSound(1930, 5, 1, 0)
                    }
                    player.motion.update(MovementStatus.NONE)
                    player.motion.clearSteps()
                    player.setBoolean(Attribute.DOING_FOOD_PUZZLE, false)
                }
            }
        }

        private fun onPuzzleCompletion(player: Player){
            player.setBoolean(Attribute.HAS_PENDING_RANDOM_EVENT, false)
            player.packetSender.sendInterfaceRemoval()
            player.motion.update(MovementStatus.DISABLED)
            player.motion.clearSteps()
            player.setBoolean(Attribute.DOING_FOOD_PUZZLE, false)
            player.packetSender.sendJinglebitMusic(150, 0)
            TaskManager.submit(3) {
                if (player.oldPosition != null) {
                    player.moveTo(player.oldPosition.clone())
                } else {
                    player.moveTo(Position(3086 + Misc.getRandomInclusive(2), 3496 + Misc.getRandomInclusive(2), 0))
                }
                player.performGraphic(Graphic(188, GraphicHeight.HIGH))
                player.packetSender.sendAreaPlayerSound(1930, 5, 1, 0)
                player.motion.update(MovementStatus.NONE)
                player.motion.clearSteps()
            }
        }
    }
}