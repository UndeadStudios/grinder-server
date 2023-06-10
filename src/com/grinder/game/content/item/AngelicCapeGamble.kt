package com.grinder.game.content.item



import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.model.Animation
import com.grinder.game.model.interfaces.dialogue.*
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.game.task.TaskManager
import com.grinder.util.DiscordBot
import com.grinder.util.Misc
import com.grinder.util.NpcID

/**
 * Exchanging regular angelic cape for the colorful angelic cape
 */
class AngelicCapeGamble {

    companion object {
        private var angelicCape: Int = 0

        /**
         * The rate of success
         */
        private const val REGULAR_RATE = 15

        /**
         * Gambling firecape
         *
         * @param player the player
         * @param type   the type
         */
		@JvmStatic
		fun exchange(player: Player, type: Int) {
            if (type == 0) {
                DialogueManager.start(player, 2882)
                player.dialogueOptions = object : DialogueOptions() {
                    override fun handleOption(player: Player, option: Int) {
                        when (option) {
                            1 -> gambleAngelic(player)
                            2 -> player.packetSender.sendInterfaceRemoval()
                        }
                    }
                }
            } else {
                gambleAngelic(player)
            }
        }

        fun gambleAngelic(player: Player) {
            if (player.inventory.containsAny(15806, 15807, 15808, 15809, 15810, 1581, 15811, 15812, 15813, 15814, 15815, 15816, 15817, 15818, 15819, 15820, 15821, 15822, 15823, 15855, 15856)) {
                for (i in angelicCapeIds.indices) {
                    if (player.inventory.contains(angelicCapeIds[i])) {
                        angelicCape = angelicCapeIds[i]
                        break
                    }
                }
                player.packetSender.sendMessage("@dre@Gambling...")
                player.localNpcs.stream().filter { npc: NPC -> npc.id == NpcID.GAMER_1012 }
                    .min(Comparator.comparingInt { npc: NPC -> npc.position.getDistance(player.position) })
                    .ifPresent { leonNpc: NPC ->
                        player.positionToFace = leonNpc.position
                        leonNpc.setEntityInteraction(player)
                        leonNpc.say("Good luck on winning, " + player.username + "!")
                        leonNpc.performAnimation(Animation(6703))
                        TaskManager.submit(4) {
                            leonNpc.resetEntityInteraction()
                            leonNpc.handlePositionFacing()
                        }
                    }
                player.packetSender.sendInterfaceRemoval()
                player.BLOCK_ALL_BUT_TALKING = true
                TaskManager.submit(4) {
                    player.inventory.delete(Item(angelicCape))
                    var random = Misc.getRandomInclusive(REGULAR_RATE)
                    tries += 1

                    if (tries >= 15) {
                        random = 15;
                    }
                    /*
                     * The rate
                     */if (random == 15) {
                    player.performAnimation(Animation(2106))
                    DialogueManager.start(player, 2884)
                    PlayerUtil.broadcastMessage(
                        "<img=770> " + PlayerUtil.getImages(player) + "" + player.username + " has gambled his @dre@" + ItemDefinition.forId(
                            angelicCape).name +" cape</col> to win the @dre@Colorful angelic cape</col>!"
                    )
                    // Disable loggin for spawn game modes
                    if (!player.gameMode.isSpawn) {
                        if (DiscordBot.ENABLED) DiscordBot.INSTANCE.sendServerLogs(
                            "" + player.username + " has gambled his " + ItemDefinition.forId(
                                angelicCape
                            ).name + " cape and won the Colorful angelic cape on the " + tries + " try!"
                        );
                    }
                    // Send jinglebit for finishing the tutorial replay
                    player.packetSender.sendJinglebitMusic(269, 0)
                    player.localNpcs.stream().filter { npc: NPC -> npc.id == NpcID.GAMER_1012 }
                        .min(Comparator.comparingInt { npc: NPC -> npc.position.getDistance(player.position) })
                        .ifPresent { gambler: NPC ->
                            player.positionToFace = gambler.position
                            gambler.setEntityInteraction(player)
                            gambler.performAnimation(Animation(862))
                            gambler.say("You have done it beast " + player.username + "! Congratulations!")
                        }
                    DialogueBuilder(DialogueType.NPC_STATEMENT)
                        .setNpcChatHead(NpcID.GAMER_1012)
                        .setText(
                            "Congratulations! you have gambled your @dre@angelic cape</col>",
                            "and won the @dre@Colorful angelic cape</col>!"
                        ).setExpression(DialogueExpression.HAPPY)
                        .start(player)
                    if (player.inventory.countFreeSlots() >= 1) {
                        player.inventory.add(Item(15901, 1))
                    } else {
                        ItemContainerUtil.dropUnder(player, 15901, 1)
                        player.sendMessage("@red@The Colorful angelic cape is dropped under you.")
                    }
                } else {
                    DialogueBuilder(DialogueType.NPC_STATEMENT)
                        .setNpcChatHead(NpcID.GAMER_1012)
                        .setText("You have lost the gamble. Your hit was: $random")
                        .setExpression(DialogueExpression.DISTRESSED)
                        .start(player)
                    player.localNpcs.stream().filter { npc: NPC -> npc.id == NpcID.GAMER_1012 }
                        .min(Comparator.comparingInt { npc: NPC -> npc.position.getDistance(player.position) })
                        .ifPresent { leonNpc: NPC ->
                            player.positionToFace = leonNpc.position
                            leonNpc.setEntityInteraction(player)
                            leonNpc.say("@red@Better luck next time " + player.username + "!")
                        }
                }
                    player.BLOCK_ALL_BUT_TALKING = false
                }
            } else {
                DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(NpcID.GAMER_1012)
                    .setText("You don't have any Angelic cape to gamble in exchange", "for the Colorful angelic cape.")
                    .setExpression(DialogueExpression.DISTRESSED)
                    .start(player)
            }
        }


        var tries = 0
        fun gambleAngelicByItem(player: Player, capeToGamble: Int) {
            if (player.inventory.containsAny(15806, 15807, 15808, 15809, 15810, 1581, 15811, 15812, 15813, 15814, 15815, 15816, 15817, 15818, 15819, 15820, 15821, 15822, 15823, 15855, 15856)) {
                if (!player.inventory.contains(capeToGamble)) {
                    DialogueBuilder(DialogueType.NPC_STATEMENT)
                        .setNpcChatHead(NpcID.GAMER_1012)
                        .setText("You don't have any Angelic cape to gamble in exchange", "for the Colorful angelic cape.")
                        .setExpression(DialogueExpression.DISTRESSED)
                        .start(player)
                    return;
                }
                player.packetSender.sendMessage("@dre@Gambling...")
                player.localNpcs.stream().filter { npc: NPC -> npc.id == NpcID.GAMER_1012 }
                    .min(Comparator.comparingInt { npc: NPC -> npc.position.getDistance(player.position) })
                    .ifPresent { leonNpc: NPC ->
                        player.positionToFace = leonNpc.position
                        leonNpc.setEntityInteraction(player)
                        leonNpc.say("Good luck on winning, " + player.username + "!")
                        leonNpc.performAnimation(Animation(6703))
                        TaskManager.submit(4) {
                            leonNpc.resetEntityInteraction()
                            leonNpc.handlePositionFacing()
                        }
                    }
                player.packetSender.sendInterfaceRemoval()
                player.BLOCK_ALL_BUT_TALKING = true
                TaskManager.submit(4) {
                    player.inventory.delete(Item(capeToGamble))
                    var random = Misc.getRandomInclusive(REGULAR_RATE)
                    tries += 1

                    if (tries >= 15) {
                        random = 15;
                    }
                    /*
                     * The rate
                     */if (random == 15) {
                    player.performAnimation(Animation(2106))
                    DialogueManager.start(player, 2884)
                    PlayerUtil.broadcastMessage(
                        "<img=770> " + PlayerUtil.getImages(player) + "" + player.username + " has gambled his @dre@" + ItemDefinition.forId(
                            angelicCape).name +" cape</col> to win the @dre@Colorful angelic cape</col>!"
                    )

                    // Disable loggin for spawn game modes
                    if (!player.gameMode.isSpawn) {
                        if (DiscordBot.ENABLED) DiscordBot.INSTANCE.sendServerLogs(
                            "" + player.username + " has gambled his " + ItemDefinition.forId(
                                angelicCape
                            ).name + " cape and won the Colorful angelic cape on the " + tries + " try!"
                        );

                    }
                    // Send jinglebit for finishing the tutorial replay
                    player.packetSender.sendJinglebitMusic(269, 0)
                    player.localNpcs.stream().filter { npc: NPC -> npc.id == NpcID.GAMER_1012 }
                        .min(Comparator.comparingInt { npc: NPC -> npc.position.getDistance(player.position) })
                        .ifPresent { gambler: NPC ->
                            player.positionToFace = gambler.position
                            gambler.setEntityInteraction(player)
                            gambler.performAnimation(Animation(862))
                            gambler.say("You have done it beast " + player.username + "! Congratulations!")
                        }
                    DialogueBuilder(DialogueType.NPC_STATEMENT)
                        .setNpcChatHead(NpcID.GAMER_1012)
                        .setText(
                            "Congratulations! you have gambled your @dre@angelic cape</col>",
                            "and won the @dre@Colorful angelic cape</col>!"
                        ).setExpression(DialogueExpression.HAPPY)
                        .start(player)
                    if (player.inventory.countFreeSlots() >= 1) {
                        player.inventory.add(Item(15901, 1))
                    } else {
                        ItemContainerUtil.dropUnder(player, 15901, 1)
                        player.sendMessage("@red@The Colorful angelic cape is dropped under you.")
                    }
                } else {
                    DialogueBuilder(DialogueType.NPC_STATEMENT)
                        .setNpcChatHead(NpcID.GAMER_1012)
                        .setText("You have lost the gamble. Your hit was: $random")
                        .setExpression(DialogueExpression.DISTRESSED)
                        .start(player)
                    player.localNpcs.stream().filter { npc: NPC -> npc.id == NpcID.GAMER_1012 }
                        .min(Comparator.comparingInt { npc: NPC -> npc.position.getDistance(player.position) })
                        .ifPresent { leonNpc: NPC ->
                            player.positionToFace = leonNpc.position
                            leonNpc.setEntityInteraction(player)
                            leonNpc.say("@red@Better luck next time " + player.username + "!")
                        }
                }
                    player.BLOCK_ALL_BUT_TALKING = false
                }
            } else {
                DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(NpcID.GAMER_1012)
                    .setText("You don't have any Angelic cape to gamble in exchange", "for the Colorful angelic cape.")
                    .setExpression(DialogueExpression.DISTRESSED)
                    .start(player)
            }
        }

        var angelicCapeIds = intArrayOf(15806, 15807, 15808, 15809, 15810, 15811, 1581, 15812, 15813, 15814, 15815, 15816, 15817, 15818, 15819, 15820, 15821, 15822, 15823, 15855, 15856)
    }
}