package com.grinder.game.content.item;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.interfaces.dialogue.*;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;

/**
 * Exchanging firecape for pet
 * 
 * @author 2012
 *
 */
public class ExchangeFireCape {

	/**
	 * The rate of success
	 */
	private static final int REGULAR_RATE = 50;
	private static final int MEMBERS_RATE = 40;

	/**
	 * Gambling firecape
	 * 
	 * @param player
	 *            the player
	 * @param type
	 *            the type
	 */
	public static void exchange(Player player, int type) {
		if (type == 0) {
			DialogueManager.start(player, 2524);
			player.setDialogueOptions(new DialogueOptions() {
				@Override
				public void handleOption(Player player, int option) {
					switch (option) {
					case 1:
						gamble(player);
						break;
					}
					player.getPacketSender().sendInterfaceRemoval();
				}
			});
		} else {
			gamble(player);
		}
	}

	/**
	 * Gambling firecape
	 * 
	 * @param player
	 *            the player
	 */
	private static void gamble(Player player) {
		/*
		 * Checks firecape
		 */
		if (player.getInventory().contains(6570)) {
			player.getInventory().delete(new Item(6570));
			int random = 0;
			if (PlayerUtil.isMember(player)) {
				random = Misc.getRandomInclusive(MEMBERS_RATE);
			} else {
				random = Misc.getRandomInclusive(REGULAR_RATE);
			}
			/*
			 * The rate
			 */
			if (random == 1) {
				// Broadcast
				PlayerUtil.broadcastMessage(
						"<img=770> " + PlayerUtil.getImages(player) + "" + player.getUsername() +" has gambled his @dre@Firecape</col> to win the @dre@Tzrek-jad</col>!");

				// Process Achievement
				AchievementManager.processFor(AchievementType.FIRE_GAMBLING, player);

				// Send Message
				player.sendMessage(
						"<img=770> Congratulations! you have gambled your @dre@Firecape</col> and won the @dre@Tzrek-jad</col> pet!");

				// NPC Chat
				new DialogueBuilder(DialogueType.NPC_STATEMENT)
						.setNpcChatHead(NpcID.TZHAARMEJJAL)
						.setText("Congratulations! you have gambled your @dre@Firecape</col>", "to win the @dre@Tzrek-jad</col>!").setExpression(DialogueExpression.DISTRESSED)
						.start(player);

				// Add reward
				if (player.getInventory().countFreeSlots() >= 1) {
					player.getInventory().add(new Item(13225, 1));
				} else {
					ItemContainerUtil.dropUnder(player, 13225, 1);
					player.sendMessage("@red@Tzrek-jad pet is dropped under you!");

				}
			} else {

				// Lost gamble chat
				new DialogueBuilder(DialogueType.NPC_STATEMENT)
						.setNpcChatHead(NpcID.TZHAARMEJJAL)
						.setText("You have lost the gamble. Your hit was: " + random +"").setExpression(DialogueExpression.DISTRESSED)
						.start(player);
			}
		} else {
			// Doesn't have a cape chat
			new DialogueBuilder(DialogueType.NPC_STATEMENT)
					.setNpcChatHead(NpcID.TZHAARMEJJAL)
					.setText("You don't have a Firecape to gamble in exchange", "for a Tzrek-jad pet.").setExpression(DialogueExpression.DISTRESSED)
					.start(player);
		}
	}
}
