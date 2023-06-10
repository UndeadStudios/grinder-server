package com.grinder.game.content.npc

import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.task_new.PlayerTaskManager
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.attribute.AttributeManager
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueExpression
import com.grinder.game.model.interfaces.dialogue.DialogueManager
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.item.Item
import com.grinder.game.model.onFirstNPCAction
import com.grinder.game.model.onSecondNPCAction
import com.grinder.game.model.onThirdNPCAction
import com.grinder.util.Misc
import com.grinder.util.NpcID
import java.util.function.Consumer

object SirLancelot {

    init {
        onFirstNPCAction(NpcID.SIR_LANCELOT) {
            startDialogue(player, npc)
        }
        onSecondNPCAction(NpcID.SIR_LANCELOT) {
            AchievementManager.open(player)
        }
        onThirdNPCAction(NpcID.SIR_LANCELOT) {
            PlayerTaskManager.openInterface(player);
        }
    }

    private fun startDialogue(player: Player, npc: NPC) {
        when {
            Misc.random(3) == 1 -> {
                DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("Hello there, Sir!?")
                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                        .setText("Good day chief!")
                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                        .setText("What's up?").setExpression(DialogueExpression.CALM)
                        .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                        .firstOption("What are achievements for?", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("What are achievements for?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Achievements is a set of small challenges that", "players may complete in order to earn rewards.", "The achievements are tasks that are usually tied", "to a specific area and are meant to test the player's...")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("knowledge about that area. The tasks of each", "area are split into four categories based on their", "difficulty: @gre@Easy</col>, @yel@Medium</col>, @red@Hard</col>, and @red@Elite</col>, and @dre@Master</col>.", "The easiest tasks usually do not require any significant...")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("skill levels to complete, but most higher level tasks", "require the player to have high skill levels as well", " as in-depth knowledge of a particular area.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Players who have completed all the achievements", "can claim an @dre@Achievements diary cape (t)</col>.")
                                    .start(player)
                        }).secondOption("Can I do achievements on Iron Man?", Consumer { player2: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("Can I do achievements on Iron Man?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Yes, it is possible to do that. However, as an Iron Man", "mode challenge there are no rewards for completing tasks.")
                                    .start(player)
                        }).thirdOption("Can I claim my Achivements cape (t)?", Consumer { player2: Player? ->
                            if (player.points[AttributeManager.Points.ACHIEVEMENT_POINTS_NEW] >= 556) {
                                // Eligible to claim
                                DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                        .setText("I want to claim my Achievements diary cape (t).")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id).setExpression(DialogueExpression.HAPPY)
                                        .setText("I can see you are eligible for this cape!")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id).setExpression(DialogueExpression.HAPPY)
                                        .setText("However this will cost you 2,000,000,000 coins.", "Do you want to proceed?") // Show options
                                        .add(DialogueType.OPTION).setOptionTitle("Claim @dre@Achievements diary cape (t)</col>?")
                                        .firstOption("Claim cape.", Consumer { player1: Player? ->
                                            if (player.inventory.countFreeSlots() > 2) {
                                                if (player.inventory.getAmount(995) >= 2000000000) {
                                                    player.inventory.delete(995, 2000000000)
                                                    player.inventory.add(Item(13069, 1)) // Achievements cape (t)
                                                    player.inventory.add(Item(13070, 1)) // Achievements cape (t)
                                                    DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13069, 200) //.setOptionTitle("@red@Don't forget to vote!")
                                                            .setText("The master hands you the @dre@Achievement diary cape (t)</col>", "and @dre@Achievement diary hood</col>!")
                                                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                            .setText("Enjoy chief!.").setExpression(DialogueExpression.HAPPY)
                                                            .start(player)
                                                } else {
                                                    DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id).setExpression(DialogueExpression.ANNOYED)
                                                            .setText("You don't have enough money for this!", "Come back when you enough later.")
                                                            .start(player)
                                                }
                                            } else {
                                                DialogueManager.sendStatement(player, "You must have at least 2 free inventory slots to claim the cape.")
                                            }
                                        }).addCancel("Not now.").start(player)
                            } else {
                                DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("I want to claim my Achievements diary cape (t).")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                        .setText("I'm sorry, but you are not yet eligible for the cape.").setExpression(DialogueExpression.DISTRESSED_2)
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                        .setText("You need to have at least 556 achievment points to claim it.")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                        .setText("Come back at any time later once you're qualified!")
                                        .start(player)
                            }
                        }).fourthOption("Complete the Pre-achievements.", Consumer { player2: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("I have pre-completed achievements, can you fix them?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Definitely! those achievements can be completed by using", "the @red@::tasks</col> command.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("It's also very beneficial to auto complete tasks that cannot", "be completed by Iron Man mode accounts.")
                                    .start(player)
                        }).addCancel("Nothing.").start(player)
            }
            Misc.random(3) == 2 -> {
                DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("I have some questions about achievement tasks.")
                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                        .setText("Huh? Go ahead chief!")
                        .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                        .firstOption("What are achievements for?", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("What are achievements for?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Achievements is a set of small challenges that", "players may complete in order to earn rewards.", "The achievements are tasks that are usually tied", "to a specific area and are meant to test the player's...")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("knowledge about that area. The tasks of each", "area are split into four categories based on their", "difficulty: @gre@Easy</col>, @yel@Medium</col>, @red@Hard</col>, and @red@Elite</col>, and @dre@Master</col>.", "The easiest tasks usually do not require any significant...")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("skill levels to complete, but most higher level tasks", "require the player to have high skill levels as well", " as in-depth knowledge of a particular area.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Players who have completed all the achievements", "can claim an @dre@Achievements diary cape (t)</col>.")
                                    .start(player)
                        }).secondOption("Can I do achievements on Iron Man?", Consumer { player2: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("Can I do achievements on Iron Man?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Yes, it is possible to do that. However, as an Iron Man", "mode challenge there are no rewards for completing tasks.")
                                    .start(player)
                        }).thirdOption("Can I claim my Achievements diary cape (t)?", Consumer { player2: Player? ->
                            if (player.points[AttributeManager.Points.ACHIEVEMENT_POINTS_NEW] >= 556) {
                                // Eligible to claim
                                DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                        .setText("I want to claim my Achievements diary cape (t).")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id).setExpression(DialogueExpression.HAPPY)
                                        .setText("I can see you are eligible for this cape!")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id).setExpression(DialogueExpression.HAPPY)
                                        .setText("However this will cost you 2,000,000,000 coins.", "Do you want to proceed?") // Show options
                                        .add(DialogueType.OPTION).setOptionTitle("Claim @dre@Achievements diary cape (t)</col>?")
                                        .firstOption("Claim cape.", Consumer { player1: Player? ->
                                            if (player.inventory.countFreeSlots() > 2) {
                                                if (player.inventory.getAmount(995) >= 2000000000) {
                                                    player.inventory.delete(995, 2000000000)
                                                    player.inventory.add(Item(13069, 1)) // Achievements cape (t)
                                                    player.inventory.add(Item(13070, 1)) // Achievements cape (t)
                                                    DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13069, 200) //.setOptionTitle("@red@Don't forget to vote!")
                                                            .setText("The master hands you the @dre@Achievement diary cape (t)</col>", "and @dre@Achievement diary hood</col>!")
                                                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                            .setText("Enjoy chief!.").setExpression(DialogueExpression.HAPPY)
                                                            .start(player)
                                                } else {
                                                    DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id).setExpression(DialogueExpression.ANNOYED)
                                                            .setText("You don't have enough money for this!", "Come back when you enough later.")
                                                            .start(player)
                                                }
                                            } else {
                                                DialogueManager.sendStatement(player, "You must have at least 2 free inventory slots to claim the cape.")
                                            }
                                        }).addCancel("Not now.").start(player)
                            } else {
                                DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                        .setText("I want to claim my Achievements diary cape (t).")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                        .setText("I'm sorry, but you are not yet eligible for the cape.").setExpression(DialogueExpression.DISTRESSED_2)
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                        .setText("You need to have at least 556 achievment points to claim it.")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                        .setText("Come back at any time later once you're qualified!")
                                        .start(player)
                            }
                        }).fourthOption("Complete the Pre-achievements.", Consumer { player2: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("I have pre-completed achievements, can you fix them?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Definitely! those achievements can be completed by using", "the @red@::tasks</col> command.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("It's also very beneficial to auto complete tasks that cannot", "be completed by Iron Man mode accounts.")
                                    .start(player)
                        }).addCancel("Nothing.").start(player)
            }
            else -> {
                DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("I'm looking for an adventure!")
                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                        .setText("You've come to the right place " + player.username + "!")
                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                        .setText("I'm here to help you with any task.", "What you're looking for?").setExpression(DialogueExpression.CALM)
                        .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                        .firstOption("What are achievements for?", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("What are achievements for?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Achievements is a set of small challenges that", "players may complete in order to earn rewards.", "The achievements are tasks that are usually tied", "to a specific area and are meant to test the player's...")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("knowledge about that area. The tasks of each", "area are split into four categories based on their", "difficulty: @gre@Easy</col>, @yel@Medium</col>, @red@Hard</col>, and @red@Elite</col>, and @dre@Master</col>.", "The easiest tasks usually do not require any significant...")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("skill levels to complete, but most higher level tasks", "require the player to have high skill levels as well", " as in-depth knowledge of a particular area.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Players who have completed all the achievements", "can claim an @dre@Achievements diary cape (t)</col>.")
                                    .start(player)
                        }).secondOption("Can I do achievements on Iron Man?", Consumer { player2: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("Can I do achievements on Iron Man?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Yes, it is possible to do that. However, as an Iron Man", "mode challenge there are no rewards for completing tasks.")
                                    .start(player)
                        }).thirdOption("Can I claim my Achivements cape (t)?", Consumer { player2: Player? ->
                            if (player.points[AttributeManager.Points.ACHIEVEMENT_POINTS_NEW] >= 556) {
                                // Eligible to claim
                                DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                        .setText("I want to claim my master cape.")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id).setExpression(DialogueExpression.HAPPY)
                                        .setText("I can see you are eligible for this cape!")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id).setExpression(DialogueExpression.HAPPY)
                                        .setText("However this will cost you 2,000,000,000 coins.", "Do you want to proceed?") // Show options
                                        .add(DialogueType.OPTION).setOptionTitle("Claim @dre@Achievements diary cape (t)</col>?")
                                        .firstOption("Claim cape.", Consumer { player1: Player? ->
                                            if (player.inventory.countFreeSlots() > 2) {
                                                if (player.inventory.getAmount(995) >= 2000000000) {
                                                    player.inventory.delete(995, 2000000000)
                                                    player.inventory.add(Item(13069, 1)) // Achievements cape (t)
                                                    player.inventory.add(Item(13070, 1)) // Achievements cape (t)
                                                    DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13069, 200) //.setOptionTitle("@red@Don't forget to vote!")
                                                            .setText("The master hands you the @dre@Achievement diary cape (t)</col>", "and @dre@Achievement diary hood</col>!")
                                                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                            .setText("Enjoy chief!.").setExpression(DialogueExpression.HAPPY)
                                                            .start(player)
                                                } else {
                                                    DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id).setExpression(DialogueExpression.ANNOYED)
                                                            .setText("You don't have enough money for this!", "Come back when you enough later.")
                                                            .start(player)
                                                }
                                            } else {
                                                DialogueManager.sendStatement(player, "You must have at least 2 free inventory slots to claim the cape.")
                                            }
                                        }).addCancel("Not now.").start(player)
                            } else {
                                DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                        .setText("I want to claim my master cape.")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                        .setText("I'm sorry, but you are not yet eligible for the cape.").setExpression(DialogueExpression.DISTRESSED_2)
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                        .setText("You need to have at least 556 achievment points to claim it.")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                        .setText("Come back at any time later once you're qualified!")
                                        .start(player)
                            }
                        }).fourthOption("Complete the Pre-achievements.", Consumer { player2: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("I have pre-completed achievements, can you fix them?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Definitely! those achievements can be completed by using", "the @red@::tasks</col> command.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("It's also very beneficial to auto complete tasks that cannot", "be completed by Iron Man mode accounts.")
                                    .start(player)
                        }).addCancel("Nothing.").start(player)
            }
        }
    }
}