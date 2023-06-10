package com.grinder.game.entity.agent.npc.monster.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.*
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.sound.Sound
import com.grinder.util.ItemID
import com.grinder.util.NpcID
import com.grinder.util.ObjectID
import java.util.function.Consumer

object Cows {

    init {
        ObjectActions.onClick(ObjectID.DAIRY_COW, ObjectID.DAIRY_COW_2) {
            val player = it.player
            when {
                it.isFirstOption() -> milk(player)
                it.isSecondOption() -> player.message("You should not do that.")
            }
            true
        }
        NPCActions.onClick(NpcID.GILLIE_GROATS) {
            createMilkMaidDialogue().start(it.player)
            true
        }
        onSecondInventoryAction(ItemID.BUCKET_OF_MILK) {
            player.inventory.replaceFirst(ItemID.BUCKET_OF_MILK, ItemID.BUCKET, true)
            player.message("You empty the bucket.")
        }
    }


    fun milk(player: Player) {
        if (player.inventory.contains(ItemID.BUCKET)) {
            while (player.inventory.contains(ItemID.BUCKET)) {
                player.performAnimation(Animation(2305))
                player.playSound(Sound(372))
                player.inventory.replaceFirst(ItemID.BUCKET, ItemID.BUCKET_OF_MILK, true)
            }
        } else {
            DialogueBuilder(DialogueType.NPC_STATEMENT)
                .setNpcChatHead(NpcID.GILLIE_GROATS)
                .setText("Tee hee! You've never milked a cow before, have you?")
                .setNext(
                    DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("Erm... No. How could you tell?")
                        .setNext(
                            DialogueBuilder(DialogueType.NPC_STATEMENT)
                                .setNpcChatHead(NpcID.GILLIE_GROATS)
                                .setText(
                                    "Because you're spilling milk all over the floor. What a",
                                    "waste! you need something to hold the milk."
                                )
                                .setNext(
                                    DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                        .setText("Ah yes, I really should have guessed that one, shouldn't", "I?")
                                        .setNext(
                                            DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                .setNpcChatHead(NpcID.GILLIE_GROATS)
                                                .setText(
                                                    "You're from the city, aren't you... Try it again with an",
                                                    "empty bucket."
                                                )
                                                .setNext(
                                                    DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                        .setText("Right, I'll do that.")
                                                )
                                        )
                                )
                        )
                ).start(player)
        }
    }


    private fun createMilkMaidDialogue() = DialogueBuilder(DialogueType.NPC_STATEMENT)
        .setNpcChatHead(NpcID.GILLIE_GROATS)
        .setText("Hello, I'm Gillie the Milkmaid. What can i do for you?")
        .setNext(
            DialogueBuilder(DialogueType.OPTION)
                .firstOption("Who are you?", Consumer { player: Player? ->
                    DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("Who are you?")
                        .setNext(
                            DialogueBuilder(DialogueType.NPC_STATEMENT)
                                .setNpcChatHead(NpcID.GILLIE_GROATS)
                                .setText(
                                    "My name's Gillie Groats. My father is a farmer and I",
                                    "milk the cows for him."
                                )
                                .setNext(
                                    DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                        .setText("Do you have any buckets of milk spare?")
                                        .setNext(
                                            DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                .setNpcChatHead(NpcID.GILLIE_GROATS)
                                                .setText(
                                                    "I'm afraid not. We need all of our milk to sell to",
                                                    "market, but you can  milk the cow yourself if you need",
                                                    "milk."
                                                )
                                                .setNext(
                                                    DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                        .setText("Thanks.")
                                                )
                                        )
                                )
                        ).start(player!!)
                })
                .secondOption("Can you tell me how to milk a cow?", Consumer { player: Player? ->
                    DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("Can you tell me how to milk a cow?")
                        .setNext(
                            DialogueBuilder(DialogueType.NPC_STATEMENT)
                                .setNpcChatHead(NpcID.GILLIE_GROATS)
                                .setText(
                                    "It's very easy. First you need an empty bucket to hold",
                                    "the milk."
                                )
                                .setNext(
                                    DialogueBuilder(DialogueType.NPC_STATEMENT)
                                        .setNpcChatHead(NpcID.GILLIE_GROATS)
                                        .setText("Then find a dairy cow to milk ' you can't just milk any cow.")
                                        .setNext(
                                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("How do I find a dairy cow?")
                                                .setNext(
                                                    DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                        .setNpcChatHead(NpcID.GILLIE_GROATS)
                                                        .setText(
                                                            "They are easy to spot ' they are dark brown and",
                                                            "white, unlike beef cows, which are light brown and white.",
                                                            "we also tether them to a post to stop them wandering",
                                                            "around all over the place."
                                                        )
                                                        .setNext(
                                                            DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                .setNpcChatHead(NpcID.GILLIE_GROATS)
                                                                .setText("There are a couple very near, in the field.")
                                                                .setNext(
                                                                    DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                        .setNpcChatHead(NpcID.GILLIE_GROATS)
                                                                        .setText(
                                                                            "Then just milk the cow and your bucket will fill with",
                                                                            "tasty, nutritious milk."
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        ).start(player!!)
                })
                .thirdOption("I'm fine, thanks.", Consumer { player: Player? ->
                    DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("I'm fine, thanks.")
                        .start(player!!)
                })
        )
}