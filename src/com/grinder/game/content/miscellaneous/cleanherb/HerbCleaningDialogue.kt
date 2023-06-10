package com.grinder.game.content.miscellaneous.cleanherb

import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.skill.skillable.impl.herblore.PotionDecantOutcome
import com.grinder.game.content.skill.skillable.impl.herblore.PotionDecanting
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.NPCActions
import com.grinder.game.model.interfaces.dialogue.*
import com.grinder.game.model.sound.Sounds
import com.grinder.util.NpcID
import java.text.NumberFormat


class HerbCleaningDialogue {
    companion object {
        val dialogue = startDialogue()
                .setText(
                        "Hello there!",
                        "Would you like me to clean any noted herbs for you?")
                .add(DialogueType.OPTION)
                .firstOption("Yes please.") { player ->
                    val herbs = HerbCleaning.findHerbsToClean(player)
                    if (herbs.isEmpty()) {
                        startDialogue()
                                .setText("Looks like you don't have anything.")
                                .setText("Come back to me when you have noted grimy herbs.")
                                .start(player)
                        return@firstOption
                    }
                    var price = herbs.map { herb -> HerbCleaning.getPriceForHerb(herb.key) * herb.value }.sum()
                    if (price <= 0) {
                        println("Yeah below")
                        println(price)
                    }
                    startDialogue()
                            .setText(
                                    "Fantastic! That will be",
                                    "${NumberFormat.getIntegerInstance().format(price)} coins.")
                            .add(DialogueType.OPTION)
                            .firstOption("That's fine.") {
                                if (player.inventory.getAmount(995) < price) {
                                    startDialogue().setText("You do not seem to have enough coins.")
                                            .start(player)
                                    return@firstOption
                                }
                                HerbCleaning.cleanHerbs(player, herbs, price)
                                player.packetSender.sendInterfaceRemoval()
                            }
                            .addCancel("That's too much.").start(player)
                }
                .addCancel("No thanks.")

        init {
            NPCActions.onClick(NpcID.ZAHUR) { action ->
                val player = action.player
                when (action.type) {
                    NPCActions.ClickAction.Type.FIRST_OPTION -> dialogue.start(player)
                    NPCActions.ClickAction.Type.SECOND_OPTION -> decantDialogue(player)
                    NPCActions.ClickAction.Type.THIRD_OPTION -> {
                        val herbs = HerbCleaning.findHerbsToClean(player)
                        if (herbs.isEmpty()) {
                            startDialogue()
                                .setText("Looks like you don't have anything.")
                                .setText("Come back to me when you have noted grimy herbs.")
                                .start(player)
                        }
                        val price = herbs.map { herb -> HerbCleaning.getPriceForHerb(herb.key) * herb.value }.sum()
                        startDialogue()
                            .setText(
                                "Fantastic! That will cost you",
                                "${NumberFormat.getIntegerInstance().format(price)} coins to clean your herbs."
                            )
                            .add(DialogueType.OPTION)
                            .firstOption("That's fine.") {
                                if (player.inventory.getAmount(995) < price) {
                                    startDialogue().setText("You do not seem to have enough coins.")
                                        .start(player)
                                    return@firstOption
                                }
                                HerbCleaning.cleanHerbs(player, herbs, price)
                                player.packetSender.sendInterfaceRemoval()
                            }
                            .addCancel("That's too much.").start(player)
                    }
                    NPCActions.ClickAction.Type.FOURTH_OPTION -> DialogueManager.sendStatement(player, "This feature is coming soon.");
                    else -> return@onClick false
                }
                return@onClick true
            }
        }


        private fun startDialogue(): DialogueBuilder {
            return DialogueBuilder()
                    .setNpcChatHead(NpcID.ZAHUR)
                    .setExpression(DialogueExpression.DEFAULT)
        }

        /**
         * Decant dialogue
         */
        private fun decantDialogue(player: Player) {
            DialogueManager.start(player, 2644)
            player.dialogueOptions = object : DialogueOptions() {
                override fun handleOption(player1: Player, option: Int) {
                    player1.packetSender.sendInterfaceRemoval()
                    if (option in 1..4) {
                        decantInventory(player1, option, true)
                    }
                }
            }
        }

        /**
         * Attempts to decant all potions in a player's inventory to be a specified dosage.
         */
        fun decantInventory(player: Player, doses: Int, dialogue: Boolean) {
            val unnoted = PotionDecanting.getPotionsToDecant(player, doses, false)
            val noted = PotionDecanting.getPotionsToDecant(player, doses, true)
            val newUnnoted = PotionDecanting.getPotionsAfterDecanting(doses, unnoted)
            val newNoted = PotionDecanting.getPotionsAfterDecanting(doses, noted)
            val newUnnotedOverflowed = newUnnoted == null
            val newNotedOverflowed = newNoted == null
            if (newUnnotedOverflowed || newNotedOverflowed) {
                if (dialogue) {
                    DialogueManager.start(player, 2839) // You are trying to decant too many potions at once!
                }
                return
            }
            val unnotedOutcome = PotionDecanting.decantPotions(player, unnoted, newUnnoted, false)
            val notedOutcome = PotionDecanting.decantPotions(player, noted, newNoted, true)
            if (dialogue) {
                // No potions found in either
                if (unnotedOutcome === PotionDecantOutcome.NO_POTS_FOUND && notedOutcome === PotionDecantOutcome.NO_POTS_FOUND) {
                    DialogueManager.start(player, 2840) // I don't think you've got anything that I can decant.
                    return
                }

                // Not enough inventory space
                if (unnotedOutcome === PotionDecantOutcome.NO_INV_SPACE && notedOutcome === PotionDecantOutcome.NO_POTS_FOUND || unnotedOutcome === PotionDecantOutcome.NO_POTS_FOUND && notedOutcome === PotionDecantOutcome.NO_INV_SPACE || unnotedOutcome === PotionDecantOutcome.NO_INV_SPACE && notedOutcome === PotionDecantOutcome.NO_INV_SPACE) {
                    DialogueManager.start(player, 2841) // You're a bit short of inventory space.
                    return
                }

                // Can't make purchase
                if (unnotedOutcome === PotionDecantOutcome.CANT_PAY && notedOutcome === PotionDecantOutcome.NO_POTS_FOUND || unnotedOutcome === PotionDecantOutcome.NO_POTS_FOUND && notedOutcome === PotionDecantOutcome.CANT_PAY || unnotedOutcome === PotionDecantOutcome.CANT_PAY && notedOutcome === PotionDecantOutcome.CANT_PAY) {
                    DialogueManager.start(player, 2842) // You're a bit short of empty vessels or cash. Empty vials
                    return
                }

                // Only decanted one due to lack of payment for the other
                if (unnotedOutcome === PotionDecantOutcome.CANT_PAY && notedOutcome === PotionDecantOutcome.SUCCESS || unnotedOutcome === PotionDecantOutcome.SUCCESS && notedOutcome === PotionDecantOutcome.CANT_PAY) {
                    DialogueManager.start(player, 2843)
                    player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
                    AchievementManager.processFor(AchievementType.DECANTER, player)
                    return
                }

                // Decanted
                if (unnotedOutcome === PotionDecantOutcome.SUCCESS && notedOutcome === PotionDecantOutcome.NO_POTS_FOUND || unnotedOutcome === PotionDecantOutcome.NO_POTS_FOUND && notedOutcome === PotionDecantOutcome.SUCCESS || unnotedOutcome === PotionDecantOutcome.SUCCESS && notedOutcome === PotionDecantOutcome.SUCCESS) {
                    DialogueManager.start(player, 2844) // There, all done.
                    player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
                    AchievementManager.processFor(AchievementType.DECANTER, player)
                }
            }
        }


    }
}