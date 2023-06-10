package com.grinder.game.content.miscellaneous

import com.grinder.game.World
import com.grinder.game.World.npcAddQueue
import com.grinder.game.World.npcRemoveQueue
import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.NPCFactory.create
import com.grinder.game.entity.agent.player.Appearance
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerStatus
import com.grinder.game.entity.agent.player.removeInterfaces
import com.grinder.game.entity.getBoolean
import com.grinder.game.model.*
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.areas.instanced.FightCaveArea
import com.grinder.game.model.areas.instanced.PestControlArea
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.*
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.game.model.item.container.shop.ShopManager
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import com.grinder.util.NpcID
import com.grinder.util.ShopIdentifiers
import com.grinder.util.oldgrinder.Area
import java.util.function.Consumer

/**
 * A frog prince event where you have to talk to the correct frog
 * and receive a token after a small dialogue which makes the npcs vanish
 * If no interaction within 30 ticks they will all vanish themselves.
 *
 * @author Blake
 */
object Thessalia {

    init {
        onFirstNPCAction(534, NpcID.THESSALIA, NpcID.THESSALIA_10478) {
            thessaliaFirstAction(player, npc)
        }
        onSecondNPCAction(534, NpcID.THESSALIA, NpcID.THESSALIA_10478) {
            thessaliaSecondAction(player, npc)
        }
        onThirdNPCAction(534, NpcID.THESSALIA, NpcID.THESSALIA_10478) {
            thessaliaThirdAction(player, npc)
        }
        onFourthNPCAction(534, NpcID.THESSALIA, NpcID.THESSALIA_10478) {
            player.sendMessage("There are no other services to offer right now.")
        }
    }

    /**
     * First action dialogue for Thessalia
     */
    private fun thessaliaFirstAction(player: Player, npc: NPC) {
        if (player.inventory.contains(ItemID.FROG_TOKEN)) {
            DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                .setText("Do you want to buy any fine clothes?").add(DialogueType.OPTION)
                .setOptionTitle("Select an Option")
                .firstOption("What have you got?",
                    Consumer { player1: Player? ->
                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                            .setText("What have you got?")
                            .setNext(
                                DialogueBuilder()
                                    .setPostAction(Consumer {
                                        ShopManager.open(
                                            player1,
                                            ShopIdentifiers.TEAMCAPE_STORE
                                        )
                                    })
                            ).start(player)
                    }).secondOption("No, thank you..",
                    Consumer {
                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                            .setText("No, thank you..")
                            .setNext(
                                DialogueBuilder()
                                    .setPostAction(Consumer {
                                        player.packetSender.sendInterfaceRemoval()
                                    })
                            ).start(player)
                    }).thirdOption("I have a frog token...",
                    Consumer {
                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                            .setText("I have a frog token...")
                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                            .setText(
                                "That entitles you to a free costume! Do yu want a ",
                                "frog mask or a prince outfit?"
                            )
                            .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                            .firstOption("A frog mask, please!",
                                Consumer { player2: Player? ->
                                    // FROG MASK
                                    DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                        .setText("A frog mask, please!")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                        .setText("There you go!")
                                        .setNext(
                                            DialogueBuilder()
                                                .setPostAction(Consumer {
                                                    player.inventory.delete(ItemID.FROG_TOKEN, 1, true)
                                                    ItemContainerUtil.addOrDrop(
                                                        player.inventory,
                                                        player,
                                                        Item(ItemID.FROG_MASK)
                                                    )
                                                    player.packetSender.sendInterfaceRemoval()
                                                })
                                        ).start(player)
                                }).secondOption("A frog prince outfit, please!",
                                Consumer {
                                    // PRINCE OUTFIT
                                    DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                        .setText("A frog prince outfit, please!")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                        .setText("There you go!")
                                        .setNext(
                                            DialogueBuilder()
                                                .setPostAction(Consumer {
                                                    player.inventory.delete(ItemID.FROG_TOKEN, 1, true)
                                                    ItemContainerUtil.addOrDrop(
                                                        player.inventory,
                                                        player,
                                                        Item(ItemID.PRINCE_TUNIC)
                                                    )
                                                    ItemContainerUtil.addOrDrop(
                                                        player.inventory,
                                                        player,
                                                        Item(ItemID.PRINCE_LEGGINGS)
                                                    )
                                                    player.packetSender.sendInterfaceRemoval()
                                                })
                                        ).start(player)
                                }).start(player)
                    }).start(player)
        } else {
            DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                .setText("Do you want to buy any fine clothes?").add(DialogueType.OPTION)
                .setOptionTitle("Select an Option")
                .firstOption("What have you got?",
                    Consumer { player1: Player? ->
                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                            .setText("What have you got?")
                            .setNext(
                                DialogueBuilder()
                                    .setPostAction(Consumer {
                                        ShopManager.open(
                                            player1,
                                            ShopIdentifiers.TEAMCAPE_STORE
                                        )
                                    })
                            ).start(player)
                    }).secondOption("No, thank you..",
                    Consumer {
                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                            .setText("No, thank you..")
                            .setNext(
                                DialogueBuilder()
                                    .setPostAction(Consumer {
                                        player.packetSender.sendInterfaceRemoval()
                                    })
                            ).start(player)
                    }).start(player)
        }
    }

    /**
     * Second action dialogue for Thessalia
     */
    private fun thessaliaSecondAction(player: Player, npc: NPC) {
        ShopManager.open(player, ShopIdentifiers.TEAMCAPE_STORE)
    }

    /**
     * Third action dialogue for Thessalia
     */
    private fun thessaliaThirdAction(player: Player, npc: NPC) {
        DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
            .setText("Do you want to change your appearance look", "for 10,000,000 coins?")
            .add(DialogueType.OPTION).setOptionTitle("Select an Option")
            .firstOption("Yes.",
                Consumer {
                    if (player.inventory.getAmount(ItemID.COINS) >= 10000000) {
                        player.packetSender.sendAppearanceConfig(
                            if (player.appearance.isMale) 324 else 325,
                            0
                        )
                        player.packetSender.sendAppearanceConfig(
                            301,
                            player.appearance.look[Appearance.HEAD]
                        )
                        if (player.appearance.isMale) {
                            player.packetSender
                                .sendAppearanceConfig(302, player.appearance.look[Appearance.BEARD])
                        }
                        player.packetSender.sendAppearanceConfig(
                            304,
                            player.appearance.look[Appearance.CHEST]
                        )
                        player.packetSender.sendAppearanceConfig(
                            306,
                            player.appearance.look[Appearance.ARMS]
                        )
                        player.packetSender.sendAppearanceConfig(
                            308,
                            player.appearance.look[Appearance.HANDS]
                        )
                        player.packetSender.sendAppearanceConfig(
                            310,
                            player.appearance.look[Appearance.LEGS]
                        )
                        player.packetSender.sendAppearanceConfig(
                            312,
                            player.appearance.look[Appearance.FEET]
                        )
                        player.packetSender
                            .sendAppearanceConfig(314, player.appearance.look[Appearance.HAIR_COLOUR])
                        player.packetSender
                            .sendAppearanceConfig(316, player.appearance.look[Appearance.TORSO_COLOUR])
                        player.packetSender
                            .sendAppearanceConfig(318, player.appearance.look[Appearance.LEG_COLOUR])
                        player.packetSender
                            .sendAppearanceConfig(322, player.appearance.look[Appearance.SKIN_COLOUR])
                        player.packetSender.sendInterfaceRemoval().sendInterface(3559)
                        player.appearance.setCanChangeAppearance(true)
                        player.inventory.delete(995, 10000000)
                        player.sendMessage("You have paid 10,000,000 to the make-over mage to help change your looks.")
                    } else {
                        DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                            .setText("OFF! You better pay me 10,000,000 first to change", "your looks.")
                            .start(player)
                    }
                }).secondOption("No thanks.", Consumer { player1: Player? ->
                DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                    .setText("No thanks.")
                    .start(player)
            }).start(player)
    }
}
