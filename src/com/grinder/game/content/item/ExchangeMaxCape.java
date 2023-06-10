package com.grinder.game.content.item;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Animation;
import com.grinder.game.model.interfaces.dialogue.*;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.task.TaskManager;
import com.grinder.util.DiscordBot;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;

import java.util.Comparator;

/**
 * Exchanging max cape for the recolored max cape
 */
public class ExchangeMaxCape {

    /**
     * The rate of success
     */
    private static final int REGULAR_RATE = 100;
    private static final int MEMBERS_RATE = 95;

    /**
     * Gambling firecape
     *
     * @param player the player
     * @param type   the type
     */
    public static void exchange(Player player, int type) {
        if (type == 0) {

            player.getLocalNpcs().stream().filter(npc -> npc.getId() == NpcID.LEON_DCOUR)
                    .min(Comparator.comparingInt(npc -> npc.getPosition().getDistance(player.getPosition())))
                    .ifPresent(leonNpc -> {
                        player.setPositionToFace(leonNpc.getPosition());
                        leonNpc.setEntityInteraction(player);
                        TaskManager.submit(8, () -> {
                            leonNpc.resetEntityInteraction();
                            leonNpc.handlePositionFacing();
                        });
                    });

            DialogueManager.start(player, 2705);
            player.setDialogueOptions(new DialogueOptions() {
                @Override
                public void handleOption(Player player, int option) {
                    switch (option) {
                        case 1:
                            gamble(player);
                            break;
                        case 2:
                            player.getPacketSender().sendInterfaceRemoval();
                            break;
                    }
                    //player.getPacketSender().sendInterfaceRemoval();
                }
            });
        } else {
            gamble(player);
        }
    }

    /**
     * Gambling firecape
     *
     * @param player the player
     */
    public static void gamble(Player player) {
        /*
         * Checks firecape
         */
        if (player.getInventory().contains(13342) && player.getInventory().contains(13281)) {
            player.getPacketSender().sendMessage("@dre@Gambling...");
            player.getLocalNpcs().stream().filter(npc -> npc.getId() == NpcID.LEON_DCOUR)
                    .min(Comparator.comparingInt(npc -> npc.getPosition().getDistance(player.getPosition())))
                    .ifPresent(leonNpc -> {

                        player.setPositionToFace(leonNpc.getPosition());
                        leonNpc.setEntityInteraction(player);
                        leonNpc.say("Good luck winning " + player.getUsername() + "!");
						leonNpc.performAnimation(new Animation(6703));
                        TaskManager.submit(4, () -> {
                            leonNpc.resetEntityInteraction();
                            leonNpc.handlePositionFacing();
                        });
                    });
            player.getPacketSender().sendInterfaceRemoval();
            player.BLOCK_ALL_BUT_TALKING = true;
            TaskManager.submit(4, () -> {
				player.getInventory().delete(new Item(13342));
				player.getInventory().delete(new Item(13281));
				int random;
				if (PlayerUtil.isMember(player)) {
					random = Misc.getRandomInclusive(MEMBERS_RATE);
				} else {
					random = Misc.getRandomInclusive(REGULAR_RATE);
				}
				/*
				 * The rate
				 */
				if (random <= 3) {
					player.performAnimation(new Animation(2106));
					DialogueManager.start(player, 2707);

					// Process Achievement
					AchievementManager.processFor(AchievementType.COLOR_ARTIST, player);

					PlayerUtil.broadcastMessage(
							"<img=770> " + PlayerUtil.getImages(player) + "" + player.getUsername() +" has gambled his @dre@Max cape</col> and @dre@Max hood</col> to win @dre@Colorful max cape</col>!");
					//player.sendMessage(
					//		"<img=770> Congratulations! you have gambled your @dre@Max cape</col> ");
					player.getLocalNpcs().stream().filter(npc -> npc.getId() == NpcID.LEON_DCOUR)
							.min(Comparator.comparingInt(npc -> npc.getPosition().getDistance(player.getPosition())))
							.ifPresent(leonNpc -> {

								player.setPositionToFace(leonNpc.getPosition());
								leonNpc.setEntityInteraction(player);
								leonNpc.performAnimation(new Animation(862));
								leonNpc.say("You have done it beast " + player.getUsername() + "! Congratulations!");
								TaskManager.submit(8, () -> {
									leonNpc.resetEntityInteraction();
									leonNpc.handlePositionFacing();
								});
							});
					new DialogueBuilder(DialogueType.NPC_STATEMENT)
							.setNpcChatHead(NpcID.LEON_DCOUR)
							.setText("Congratulations! you have gambled your @dre@Max cape</col>", "and @dre@Max hood</col> and won the @dre@Colorful max cape</col>!").setExpression(DialogueExpression.HAPPY)
							.start(player);
					// Disable loggin for spawn game modes
					if (!player.getGameMode().isSpawn()) {
						if (DiscordBot.ENABLED)
							DiscordBot.INSTANCE.sendServerLogs("" + player.getUsername() + " has gambled his @dre@Max cape</col> and @dre@Max hood</col> to win @dre@Colorful max cape</col>!");
					}
					if (player.getInventory().countFreeSlots() >= 2) {
						player.getInventory().add(new Item(15195, 1));
						player.getInventory().add(new Item(15271, 1));
					} else {
						ItemContainerUtil.dropUnder(player, 15195, 1);
						ItemContainerUtil.dropUnder(player, 15271, 1);
						player.sendMessage("@red@The Colorful Max cape and hood is dropped under you.");
					}
				} else {
					//player.getPacketSender().sendMessage("You have lost the gamble. Your hit was: " + random);
					new DialogueBuilder(DialogueType.NPC_STATEMENT)
							.setNpcChatHead(NpcID.LEON_DCOUR)
							.setText("You have lost the gamble. Your hit was: " + random + "").setExpression(DialogueExpression.DISTRESSED)
							.start(player);
					player.getLocalNpcs().stream().filter(npc -> npc.getId() == NpcID.LEON_DCOUR)
							.min(Comparator.comparingInt(npc -> npc.getPosition().getDistance(player.getPosition())))
							.ifPresent(leonNpc -> {

								player.setPositionToFace(leonNpc.getPosition());
								leonNpc.setEntityInteraction(player);
								leonNpc.say("@red@Better luck next time " + player.getUsername() + "!");
								TaskManager.submit(4, () -> {
									leonNpc.resetEntityInteraction();
									leonNpc.handlePositionFacing();
								});
							});
				}
				player.BLOCK_ALL_BUT_TALKING = false;
			});
        } else {
            //player.getPacketSender().sendMessage("You don't have a Max cape or Max hood to gamble in exchange for Colorful max cape.");
            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(NpcID.LEON_DCOUR)
                    .setText("You don't have a Max cape or Max hood to", "gamble in exchange for the Colorful max cape.").setExpression(DialogueExpression.DISTRESSED)
                    .start(player);
        }
    }
}
