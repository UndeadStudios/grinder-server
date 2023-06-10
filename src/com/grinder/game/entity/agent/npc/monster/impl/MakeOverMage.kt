package com.grinder.game.entity.agent.npc.monster.impl

import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.npc.monster.Monster
import com.grinder.game.entity.agent.npc.monster.scheduleRandomSpeech
import com.grinder.game.entity.agent.player.Appearance
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.*
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueExpression
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.util.ItemID
import com.grinder.util.NpcID
import com.grinder.util.Priority
import java.util.function.Consumer

/**
 * @author L E G E N D
 * Date: 2/5/2021
 * Time: 3:52 AM
 * Discord: "L E G E N D#4380"
 */
class MakeOverMage(id: Int, position: Position) : Monster(id, position) {

    init {
        val chats = arrayOf("", "Change your style..", "Change how you look..", "Err!!", "Almost got that rat!!")
        scheduleRandomSpeech(48..52, *chats) { chatIndex ->
            when(chatIndex) {
                0, 1, 2 -> {
                    performAnimation(Animation(1164, 50))
                    performGraphic(Graphic(110, 50, GraphicHeight.MIDDLE, Priority.HIGH))
                }
                3 -> {
                    performAnimation(Animation(811))
                    performGraphic(Graphic(308, GraphicHeight.MIDDLE, Priority.HIGH))
                }
                4 -> {
                    performAnimation(Animation(1166))
                    performGraphic(Graphic(147, GraphicHeight.MIDDLE, Priority.HIGH))
                }
            }
        }
    }

    companion object {
        fun sendInterface(player: Player) {
            player.packetSender.sendAppearanceConfig(if (player.appearance.isMale) 324 else 325, 0)
            player.packetSender.sendAppearanceConfig(301, player.appearance.look[Appearance.HEAD])
            if (player.appearance.isMale) {
                player.packetSender.sendAppearanceConfig(302, player.appearance.look[Appearance.BEARD])
            }
            player.packetSender.sendAppearanceConfig(304, player.appearance.look[Appearance.CHEST])
            player.packetSender.sendAppearanceConfig(306, player.appearance.look[Appearance.ARMS])
            player.packetSender.sendAppearanceConfig(308, player.appearance.look[Appearance.HANDS])
            player.packetSender.sendAppearanceConfig(310, player.appearance.look[Appearance.LEGS])
            player.packetSender.sendAppearanceConfig(312, player.appearance.look[Appearance.FEET])
            player.packetSender.sendAppearanceConfig(314, player.appearance.look[Appearance.HAIR_COLOUR])
            player.packetSender.sendAppearanceConfig(316, player.appearance.look[Appearance.TORSO_COLOUR])
            player.packetSender.sendAppearanceConfig(318, player.appearance.look[Appearance.LEG_COLOUR])
            player.packetSender.sendAppearanceConfig(322, player.appearance.look[Appearance.SKIN_COLOUR])
            player.packetSender.sendInterfaceRemoval().sendInterface(3559)
            player.appearance.setCanChangeAppearance(true)
        }

        init {
            NPCActions.onClick(NpcID.MAKEOVER_MAGE, NpcID.MAKEOVER_MAGE_1307) { action ->
                val player: Player = action.player
                if (action.type === NPCActions.ClickAction.Type.FIRST_OPTION) {
                    DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.MAKEOVER_MAGE)
                        .setText(
                            "Hello there! I am known as the make-over mage! I have",
                            "spent many years researching magics that can change",
                            "your physical appearance!"
                        )
                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.MAKEOVER_MAGE)
                        .setText(
                            "I can alter your physical form for a small fee of only",
                            "10,000,000 coins! Would you like me to perform my",
                            "magics upon you?"
                        )
                        .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                        .firstOption("Tell more more about this 'make-over'.", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("Tell me more about this 'make-over'.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.MAKEOVER_MAGE)
                                .setText(
                                    "Why, of course! Basically, and I will try and explaind",
                                    "this so that you will understand it correctly."
                                )
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.MAKEOVER_MAGE)
                                .setText(
                                    "I use my secret magical technique to melt your body",
                                    "down into puddle of its elements."
                                )
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.MAKEOVER_MAGE)
                                .setText(
                                    "When I have broken down all trace of your body, I",
                                    "then rebuild it into the form I am thinking of!"
                                )
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.MAKEOVER_MAGE)
                                .setText("Or, you know, somewhere vaguely close enough", "anyway.")
                                .add(DialogueType.PLAYER_STATEMENT)
                                .setText("Uh... that doesn't sound particularly safe to me...")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.MAKEOVER_MAGE)
                                .setText(
                                    "It's as safe as houses! Why, I have only had thirty-six",
                                    "major accidents this month!"
                                )
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.MAKEOVER_MAGE)
                                .setText("So what do you say? Feel like a change?", "It's only 10,000,00 coins.")
                                .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                                .firstOption("Sure, I'll pay 10,000,000 coins.", Consumer { player3: Player? ->
                                    /*new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                            .setText("Sure, I'll pay 10,000,000 coins.")
                                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(MAKEOVER_MAGE)
                                            .setText("You of course agree that if by some accident you are", "turned into a frog you have no rights for compensation", " or refund.")
                                            .setAction(player4 -> {*/if (player.getInventory()
                                        .getAmount(ItemID.COINS) >= 10000000
                                ) {
                                    sendInterface(player)
                                    player.getInventory()
                                        .delete(ItemID.COINS, 10000000)
                                    player.sendMessage("You have paid 10,000,000 to the make-over mage to help change your looks.")
                                } else {
                                    DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.MAKEOVER_MAGE)
                                        .setText("OFF! You better pay me 10,000,000 first to change", "your looks.")
                                        .start(player)
                                }
                                }).secondOption("No thanks.", Consumer { player3: Player? ->
                                    DialogueBuilder(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.ANGRY_4)
                                        .setText("No thanks. I'm happy as Saradomin made me.")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.MAKEOVER_MAGE)
                                        .setExpression(DialogueExpression.DISTRESSED)
                                        .setText("Ehhh... suit youreslf.")
                                        .start(player)
                                }).start(player)
                        }).secondOption("Sure, I'll pay 10,000,000 coins.", Consumer { player1: Player? ->
                            if (player.getInventory()
                                    .getAmount(ItemID.COINS) >= 10000000
                            ) {
                                /*new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                            .setText("Sure, I'll pay 10,000,000 coins.")
                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(MAKEOVER_MAGE)
                            .setText("You of course agree that if by some accident you are", "turned into a frog you have no rights for compensation", " or refund.")
                            .setAction(player3 -> {*/
                                sendInterface(player)
                                player.getInventory()
                                    .delete(ItemID.COINS, 10000000)
                                player.sendMessage("You have paid 10,000,000 to the make-over mage to help change your looks.")
                            } else {
                                DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.MAKEOVER_MAGE)
                                    .setText("OFF! You better pay me 10,000,000 first to change", "your looks.")
                                    .start(player)
                            }
                        }).thirdOption("Cool amulet! Can I have one?", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("Cool amulet! can I have one?")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.MAKEOVER_MAGE)
                                .setText(
                                    "No problem, but please remember that the amulet I will",
                                    "sell you is only a copy of my own. It contains no",
                                    "magical powers, and as such will only cost you",
                                    "50,000,000 coins."
                                )
                                .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                                .firstOption("Sure, here you go.", Consumer { player2: Player? ->
                                    if (player.getInventory()
                                            .getAmount(ItemID.COINS) >= 50000000
                                    ) {
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                            .setText("Sure, here you go.").add(DialogueType.ITEM_STATEMENT_NO_HEADER)
                                            .setItem(7803, 200)
                                            .setText("You receive an amulet in exchange for 50,000,000 coins.")
                                            .setAction(Consumer { player3: Player? ->
                                                player.getInventory()
                                                    .delete(ItemID.COINS, 50000000)
                                                ItemContainerUtil.addOrDrop(
                                                    player.getInventory(),
                                                    player,
                                                    Item(7803, 1)
                                                )
                                            }).start(player)
                                    } else {
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                            .setText("Ohh, I don't have enough money for that.")
                                            .start(player)
                                    }
                                }).secondOption("No way! That's far too expensive.", Consumer { player2: Player? ->
                                    DialogueBuilder(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.ANGRY_4)
                                        .setText("No way! That's far too expensive.")
                                        .start(player)
                                }).start(player)
                        }).fourthOption("No thanks.", Consumer { player3: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.ANGRY_4)
                                .setText("No thanks. I'm happy as Saradomin made me.")
                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.MAKEOVER_MAGE)
                                .setExpression(DialogueExpression.DISTRESSED)
                                .setText("Ehhh... suit youreslf.")
                                .start(player)
                        }).start(player)
                } else {
                    DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.MAKEOVER_MAGE)
                        .setText("That's rude, speak to me first!").start(player)
                }
                true
            }
        }
    }

    override fun attackRange(type: AttackType) = 0
}