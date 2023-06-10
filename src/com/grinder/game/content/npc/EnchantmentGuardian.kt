package com.grinder.game.content.npc

import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueExpression
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.item.container.shop.ShopManager
import com.grinder.game.model.onFirstNPCAction
import com.grinder.game.model.onSecondNPCAction
import com.grinder.util.Misc
import com.grinder.util.NpcID
import com.grinder.util.ShopIdentifiers
import java.util.function.Consumer

object EnchantmentGuardian {

    init {
        onFirstNPCAction(NpcID.ENCHANTMENT_GUARDIAN) {
            promptTeleportDialogue(player, npc)
        }
        onSecondNPCAction(NpcID.ENCHANTMENT_GUARDIAN) {
            ShopManager.open(player, ShopIdentifiers.MINIGAME_STORE)
        }
    }

    private fun promptTeleportDialogue(player: Player, npc: NPC) {
        val random = Misc.random(3)
        if (random == 0 || random == 1) {
            DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                    .setText("Ahh...it's you again " + player.username + "!").setExpression(DialogueExpression.DISTRESSED)
                    .add(DialogueType.NPC_STATEMENT)
                    .setText("So what's up for today?").setExpression(DialogueExpression.SLEEPY)
                    .add(DialogueType.PLAYER_STATEMENT)
                    .setText("I'm looking for some information.").setExpression(DialogueExpression.DEFAULT).add(DialogueType.OPTION).setOptionTitle("Select an Option")
                    .firstOption("What are you doing here?", Consumer { player1: Player? ->
                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("What are you doing here?")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("I'm here to monitor and reward the true heroes", "that can accomplish victories in my minigames.", "The winners will be rewarded very generously.", "Only the strongest and bravest shall enter.").setExpression(DialogueExpression.HAPPY)
                                .start(player)
                    }).secondOption("Tell me about the Weapon Minigame.", Consumer { player1: Player? ->
                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("Tell me about the Weapon Minigame.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("This is one of my favorite Minigames!")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("Basically, each player spawns at a different location", "and your goal is to score 24 points", "Each player kill counts as one point.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("Keep in mind that for every player you defeat", "you will be rewarded with random supplies that", "will help you with your journey.", "Dying does not affect your score.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("Make sure you have enough inventory space", "upon killing a player so that the random", "items will have space in your inventory.", "You can drop the items you don't need")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("There are also stone crates that you can search", "for quick supplies such as runes and ammunition.", "I wouldn't suggest you spending time on that unless", "you have an important need for it.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("You can view the scoreboard mural to view", "all the other players rankings in the minigame.", "If you get disconnected during the game you will", "be removed and you would have to join a new one.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("All the items you gather from the minigame", "will @red@vanish</col> after the minigame is over.", "The minigame is @gre@safe</col> for all.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("Winning the minigame rewards you @red@100,000 x Blood money</col>", "Huge amount of participation points and finally", "200-350 minigame points that you can spend in my store.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("All the other players who participated will still", "get the same rewards but in smaller amounts,", "and based on their activity stats.")
                                .start(player)
                    }).thirdOption("Tell me about the Battle Royale.", Consumer { player1: Player? ->
                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("Tell me about the Battle Royale.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("The name says it all! The minigame starts off by", "moving everyone to the deserted island.", "@red@Your goal is to be the last person to survive.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("For the first 30 seconds the area is @gre@safe</col> and a lot", "of random items will spawn on the ground.", "You have to gather as much items as you can", "before they disappear from the ground.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("After that the area becomes @red@dangerous</col> and this", "means that players can now attack each other.", "Dying will get you out of the minigame.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("All the items you gather from the minigame", "will @red@vanish</col> after the minigame is over.", "The minigame is @gre@safe</col> for all.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("Winning the minigame rewards you @red@100,000 x Blood money</col>", "Huge amount of participation points and finally", "200-350 minigame points that you can spend in my store.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("All the other players who participated will still", "get the same rewards but in smaller amounts,", "and based on their activity stats.")
                                .start(player)
                    }).fourthOption("What do you have for sale?", Consumer { player1: Player? -> ShopManager.open(player, ShopIdentifiers.MINIGAME_STORE) }).fifthOption("Goodbye.", Consumer { player1: Player? ->
                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("Goodbye.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("Bye bye!").start(player)
                    }).start(player)
        } else if (random == 2) {
            DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                    .setText("Comrad...I'm busy guarding!").setExpression(DialogueExpression.DISTRESSED_2)
                    .add(DialogueType.PLAYER_STATEMENT)
                    .setText("I'm looking for some information.").setExpression(DialogueExpression.DEFAULT)
                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                    .setText("What are you looking for exactly?").setExpression(DialogueExpression.DISTRESSED_2)
                    .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                    .firstOption("What are you doing here?", Consumer { player1: Player? ->
                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("What are you doing here?")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("I'm here to monitor and reward the true heroes", "that can accomplish victories in my minigames.", "The winners will be rewarded very generously.", "Only the strongest and bravest shall enter.").setExpression(DialogueExpression.HAPPY)
                                .start(player)
                    }).secondOption("Tell me about the Weapon minigame.", Consumer { player1: Player? ->
                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("Tell me about the Weapon Minigame.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("This is one of my favorite Minigames!")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("Basically, each player spawns at a different location", "and your goal is to score 24 points", "Each player kill counts as one point.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("Keep in mind that for every player you defeat", "you will be rewarded with random supplies that", "will help you with your journey.", "Dying does not affect your score.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("Make sure you have enough inventory space", "upon killing a player so that the random", "items will have space in your inventory.", "You can drop the items you don't need")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("There are also stone crates that you can search", "for quick supplies such as runes and ammunition.", "I wouldn't suggest you spending time on that unless", "you have an important need for it.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("You can view the scoreboard mural to view", "all the other players rankings in the minigame.", "If you get disconnected during the game you will", "be removed and you would have to join a new one.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("All the items you gather from the minigame", "will @red@vanish</col> after the minigame is over.", "The minigame is @gre@safe</col> for all.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("Winning will reward you @red@100,000 x Blood money</col>", "Huge amount of participation points and finally", "200-350 minigame points that you can spend in my store.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("All the other players who participated will still", "get the same rewards but in smaller amounts,", "and based on their activity stats.")
                                .start(player)
                    }).thirdOption("Tell me about the Battle Royale.", Consumer { player1: Player? ->
                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("Tell me about the Battle Royale.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("The name says it all! The minigame starts off by", "moving everyone to the deserted island.", "@red@Your goal is to be the last person to survive.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("For the first 30 seconds the area is @gre@safe</col> and a lot", "of random items will spawn on the ground.", "You have to gather as much items as you can", "before they disappear from the ground.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("After that the area becomes @red@dangerous</col> and this", "means that players can now attack each other.", "Dying will get you out of the minigame.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("All the items you gather from the minigame", "will @red@vanish</col> after the minigame is over.", "The minigame is @gre@safe</col> for all.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("Winning will reward you @red@100,000 x Blood money</col>", "Huge amount of participation points and finally", "200-350 minigame points that you can spend in my store.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("All the other players who participated will still", "get the same rewards but in smaller amounts,", "and based on their activity stats.")
                                .start(player)
                    }).fourthOption("What do you have for sale?", Consumer { player1: Player? -> ShopManager.open(player, ShopIdentifiers.MINIGAME_STORE) }).fifthOption("Goodbye.", Consumer { player1: Player? ->
                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("Goodbye.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("Bye bye!").start(player)
                    }).start(player)
        } else {
            DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                    .setText("Squive. Swish. Pish.").setExpression(DialogueExpression.EVIL_4)
                    .add(DialogueType.NPC_STATEMENT)
                    .setText("What do you need?").setExpression(DialogueExpression.SLEEPY)
                    .add(DialogueType.PLAYER_STATEMENT)
                    .setText("I'm looking for some information.").setExpression(DialogueExpression.DEFAULT).add(DialogueType.OPTION).setOptionTitle("Select an Option")
                    .firstOption("What are you doing here?", Consumer { player1: Player? ->
                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("What are you doing here?")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("I'm here to monitor and reward the true heroes", "that can accomplish victories in my Minigames.", "The winners will be rewarded very generously.", "Only the strongest and bravest shall enter.").setExpression(DialogueExpression.HAPPY)
                                .start(player)
                    }).secondOption("Tell me about the Weapon minigame.", Consumer { player1: Player? ->
                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("Tell me about the Weapon minigame.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("This is one of my favorite Minigames!")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("Basically, each player spawns at a different location", "and your goal is to score 24 points", "Each player kill counts as one point.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("Keep in mind that for every player you defeat", "you will be rewarded with random supplies that", "will help you with your journey.", "Dying does not affect your score.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("Make sure you have enough inventory space", "upon killing a player so that the random", "items will have space in your inventory.", "You can drop the items you don't need")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("There are also stone crates that you can search", "for quick supplies such as runes and ammunition.", "I wouldn't suggest you spending time on that unless", "you have an important need for it.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("You can view the scoreboard mural to view", "all the other players rankings in the minigame.", "If you get disconnected during the game you will", "be removed and you would have to join a new one.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("All the items you gather from the minigame", "will @red@vanish</col> after the minigame is over.", "The minigame is @gre@safe</col> for all.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("Winning will reward you @red@100,000 x Blood money</col>", "Huge amount of participation points and finally", "200-350 minigame points that you can spend in my store.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("All the other players who participated will still", "get the same rewards but in smaller amounts,", "and based on their activity stats.")
                                .start(player)
                    }).thirdOption("Tell me about the Battle Royale.", Consumer { player1: Player? ->
                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("Tell me about the Battle Royale.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("The name says it all! The minigame starts off by", "moving everyone to the deserted island.", "@red@Your goal is to be the last person to survive.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("For the first 30 seconds the area is @gre@safe</col> and a lot", "of random items will spawn on the ground.", "You have to gather as much items as you can", "before they disappear from the ground.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("After that the area becomes @red@dangerous</col> and this", "means that players can now attack each other.", "Dying will get you out of the minigame.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("All the items you gather from the minigame", "will @red@vanish</col> after the minigame is over.", "The minigame is @gre@safe</col> for all.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("Winning will reward you @red@100,000 x Blood money</col>", "Huge amount of participation points and finally", "200-350 minigame points that you can spend in my store.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("All the other players who participated will still", "get the same rewards but in smaller amounts,", "and based on their activity stats.")
                                .start(player)
                    }).fourthOption("What do you have for sale?", Consumer { player1: Player? -> ShopManager.open(player, ShopIdentifiers.MINIGAME_STORE) }).fifthOption("Goodbye.", Consumer { player1: Player? ->
                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("Goodbye.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                .setText("Bye bye!").start(player)
                    }).start(player)
        }
    }
}