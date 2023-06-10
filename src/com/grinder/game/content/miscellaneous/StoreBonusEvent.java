package com.grinder.game.content.miscellaneous;

import com.grinder.game.GameConstants;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.DiscordBot;
import com.grinder.util.Misc;

public class StoreBonusEvent {

	/**
	 * Runs a random store event
	 */
	public static void runRandomStoreEvent(Player player) {
		int totalEvents = Misc.randomInclusive(0, 8);


		// Make sure no event is already running
		if (GameConstants.PLAYERS_SPENDING_HUNDRED_ORMORE_10K_BONUS_PREMIUM_POINTS_EVENT || GameConstants.BONUS_FOURTY_PERCENT_PREMIUM_POINTS_EVENT || GameConstants.DOUBLE_REWARDS_FIRST_PURCHASE_ABOVE_500_TOTAL_LEVEL_EVENT
				|| GameConstants.PLAYERS_SPENDING_250_ORMORE_FREE_TWISTEDBOW_EVENT || GameConstants.FIRST_PLAYER_50_ORMORE_FREE_BOND_EVENT || GameConstants.ALL_ORDERS_ABOVE_250_PHAT_SET_EVENT
				|| GameConstants.ALL_ORDERS_ABOVE_100_GETS_50K_PREMIUM_POINTS_EVENT || GameConstants.FIRST_PLAYER_SPEND_500_GETS_500K_PREM_POINTS_EVENT) {
			player.sendMessage("There is already an active event running. Try again later!");
			return;
		}

		// Turn all events off
		GameConstants.PLAYERS_SPENDING_HUNDRED_ORMORE_10K_BONUS_PREMIUM_POINTS_EVENT = false;
		GameConstants.BONUS_FOURTY_PERCENT_PREMIUM_POINTS_EVENT = false;
		GameConstants.DOUBLE_REWARDS_FIRST_PURCHASE_ABOVE_500_TOTAL_LEVEL_EVENT = false;
		GameConstants.PLAYERS_SPENDING_250_ORMORE_FREE_TWISTEDBOW_EVENT = false;
		GameConstants.FIRST_PLAYER_50_ORMORE_FREE_BOND_EVENT = false;
		GameConstants.ALL_ORDERS_ABOVE_250_PHAT_SET_EVENT = false;
		GameConstants.ALL_ORDERS_ABOVE_100_GETS_50K_PREMIUM_POINTS_EVENT = false;
		GameConstants.FIRST_PLAYER_SPEND_500_GETS_500K_PREM_POINTS_EVENT = false;

		if (totalEvents == 0 || totalEvents == 1) {
			GameConstants.PLAYERS_SPENDING_HUNDRED_ORMORE_10K_BONUS_PREMIUM_POINTS_EVENT = true;
			Broadcast.broadcast(null, 3600, "[LIMITED]: Players who spend $100.00 or more will receive bonus 10k premium points.", "https://www.grinderscape.org/store/");
			if(DiscordBot.ENABLED)
				DiscordBot.INSTANCE.sendServerLogs("[LIMITED STORE EVENT created by " + player.getUsername() +"]: Players who spend $100.00 or more will receive bonus 10k premium points.");

			TaskManager.submit(new Task(18000) {
				@Override
				public void execute() {
					GameConstants.PLAYERS_SPENDING_HUNDRED_ORMORE_10K_BONUS_PREMIUM_POINTS_EVENT = false;
					Broadcast.removeGlobalBroadcast(null, "[LIMITED]: Players who spend $100.00 or more will receive bonus 10k premium points.");
					stop();
				}
			});

			//Ending
			TaskManager.submit(new Task(17000) {
				@Override
				public void execute() {
					stop();
					Broadcast.broadcast(null, 10, "[LIMITED STORE EVENT]: The event is ending in 10 minutes!", "");
				}
			});


		} else if (totalEvents == 2) {
			GameConstants.BONUS_FOURTY_PERCENT_PREMIUM_POINTS_EVENT = true;
			Broadcast.broadcast(null, 3600, "[LIMITED]: All orders will receive extra 20% bonus premium points.", "https://www.grinderscape.org/store/");
			if(DiscordBot.ENABLED)
				DiscordBot.INSTANCE.sendServerLogs("[LIMITED STORE EVENT created by " + player.getUsername() +"]: All orders will receive extra 20% bonus premium points.");


			TaskManager.submit(new Task(18000) {
				@Override
				public void execute() {
					stop();
					GameConstants.BONUS_FOURTY_PERCENT_PREMIUM_POINTS_EVENT = false;
					Broadcast.removeGlobalBroadcast(null, "[LIMITED]: All orders will receive extra 20% bonus premium points.");
				}
			});

			//Ending
			TaskManager.submit(new Task(17000) {
				@Override
				public void execute() {
					stop();
					Broadcast.broadcast(null, 10, "[LIMITED STORE EVENT]: The event is ending in 10 minutes!", "");
				}
			});


		} else if (totalEvents == 3) {
			GameConstants.DOUBLE_REWARDS_FIRST_PURCHASE_ABOVE_500_TOTAL_LEVEL_EVENT = true;
			Broadcast.broadcast(null, 3600, "[LIMITED]: Doubled rewards for players first ever purchase with a total level above 500.", "https://www.grinderscape.org/store/");
			if(DiscordBot.ENABLED)
				DiscordBot.INSTANCE.sendServerLogs("[LIMITED STORE EVENT created by " + player.getUsername() +"]: Doubled rewards for players first ever purchase with a total level above 500.");

			TaskManager.submit(new Task(18000) {
				@Override
				public void execute() {
					stop();
					GameConstants.DOUBLE_REWARDS_FIRST_PURCHASE_ABOVE_500_TOTAL_LEVEL_EVENT = false;
					Broadcast.removeGlobalBroadcast(null, "[LIMITED]: Doubled rewards for players first ever purchase with a total level above 500.");
				}
			});

			//Ending
			TaskManager.submit(new Task(17000) {
				@Override
				public void execute() {
					stop();
					Broadcast.broadcast(null, 10, "[LIMITED STORE EVENT]: The event is ending in 10 minutes!", "");
				}
			});


		} else if (totalEvents == 4) {
			GameConstants.PLAYERS_SPENDING_250_ORMORE_FREE_TWISTEDBOW_EVENT = true;
			Broadcast.broadcast(null, 3600, "[LIMITED]: Players to redeem $250.00 or more will receive a bonus free Twisted bow.", "https://www.grinderscape.org/store/");
			if(DiscordBot.ENABLED)
				DiscordBot.INSTANCE.sendServerLogs("[LIMITED STORE EVENT created by " + player.getUsername() +"]: Players to redeem $250.00 or more will receive a bonus free Twisted bow.");

			TaskManager.submit(new Task(18000) {
				@Override
				public void execute() {
					stop();
					GameConstants.PLAYERS_SPENDING_250_ORMORE_FREE_TWISTEDBOW_EVENT = false;
					Broadcast.removeGlobalBroadcast(null, "[LIMITED]: Players to redeem $250.00 or more will receive a bonus free Twisted bow.");
				}
			});

			//Ending
			TaskManager.submit(new Task(17000) {
				@Override
				public void execute() {
					stop();
					Broadcast.broadcast(null, 10, "[LIMITED STORE EVENT]: The event is ending in 10 minutes!", "");
				}
			});


		} else if (totalEvents == 5) {
			GameConstants.FIRST_PLAYER_50_ORMORE_FREE_BOND_EVENT = true;
			Broadcast.broadcast(null, 3600, "[LIMITED]: First to redeem $50.00 or more will receive a bonus free $50.00 bond.", "https://www.grinderscape.org/store/");
			if(DiscordBot.ENABLED)
				DiscordBot.INSTANCE.sendServerLogs("[LIMITED STORE EVENT created by " + player.getUsername() +"]: First to redeem $50.00 or more will receive a bonus free $50.00 bond.");

			TaskManager.submit(new Task(18000) {
				@Override
				public void execute() {
					stop();
					GameConstants.FIRST_PLAYER_50_ORMORE_FREE_BOND_EVENT = false;
					Broadcast.removeGlobalBroadcast(null, "[LIMITED]: First to redeem $50.00 or more will receive a bonus free $50.00 bond.");
				}
			});

			//Ending
			TaskManager.submit(new Task(17000) {
				@Override
				public void execute() {
					stop();
					Broadcast.broadcast(null, 10, "[LIMITED STORE EVENT]: The event is ending in 10 minutes!", "");
				}
			});


		} else if (totalEvents == 6) {
			GameConstants.ALL_ORDERS_ABOVE_250_PHAT_SET_EVENT = true;
			Broadcast.broadcast(null, 3600, "[LIMITED]: All orders above or $250.00 will receive a free partyhat set.", "https://www.grinderscape.org/store/");
			if (DiscordBot.ENABLED)
				DiscordBot.INSTANCE.sendServerLogs("[LIMITED STORE EVENT created by " + player.getUsername() + "]: All orders above or $250.00 will receive a free partyhat set.");

			TaskManager.submit(new Task(18000) {
				@Override
				public void execute() {
					stop();
					GameConstants.ALL_ORDERS_ABOVE_250_PHAT_SET_EVENT = false;
					Broadcast.removeGlobalBroadcast(null, "[LIMITED]: All orders above or $250.00 will receive a free partyhat set.");
				}
			});

			//Ending
			TaskManager.submit(new Task(17000) {
				@Override
				public void execute() {
					stop();
					Broadcast.broadcast(null, 10, "[LIMITED STORE EVENT]: The event is ending in 10 minutes!", "");
				}
			});

		} else if (totalEvents == 7) {
			GameConstants.FIRST_PLAYER_SPEND_500_GETS_500K_PREM_POINTS_EVENT = true;
			Broadcast.broadcast(null, 3600, "[LIMITED]: The first player to spend $500.00 will receive bonus 100k premium points.", "https://www.grinderscape.org/store/");
			if (DiscordBot.ENABLED)
				DiscordBot.INSTANCE.sendServerLogs("[LIMITED STORE EVENT created by " + player.getUsername() + "]: The first player to spend $500.00 will receive bonus 100k premium points.");

			TaskManager.submit(new Task(18000) {
				@Override
				public void execute() {
					stop();
					GameConstants.FIRST_PLAYER_SPEND_500_GETS_500K_PREM_POINTS_EVENT = false;
					Broadcast.removeGlobalBroadcast(null, "[LIMITED]: The first player to spend $500.00 will receive bonus 500k premium points.");
				}
			});

			//Ending
			TaskManager.submit(new Task(17000) {
				@Override
				public void execute() {
					stop();
					Broadcast.broadcast(null, 10, "[LIMITED STORE EVENT]: The event is ending in 10 minutes!", "");
				}
			});

		} else {
			GameConstants.ALL_ORDERS_ABOVE_100_GETS_50K_PREMIUM_POINTS_EVENT = true;
			Broadcast.broadcast(null, 3600, "[LIMITED]: All orders above or $100.00 will receive extra bonus 50,000 premium points.", "https://www.grinderscape.org/store/");
			if (DiscordBot.ENABLED)
				DiscordBot.INSTANCE.sendServerLogs("[LIMITED STORE EVENT created by " + player.getUsername() + "]: All orders above or $100.00 will receive extra bonus 50,000 premium points.");

			TaskManager.submit(new Task(18000) {
				@Override
				public void execute() {
					stop();
					GameConstants.ALL_ORDERS_ABOVE_100_GETS_50K_PREMIUM_POINTS_EVENT = false;
					Broadcast.removeGlobalBroadcast(null, "[LIMITED]: All orders above or $100.00 will receive extra bonus 50,000 premium points.");
				}
			});

			//Ending
			TaskManager.submit(new Task(17000) {
				@Override
				public void execute() {
					stop();
					Broadcast.broadcast(null, 10, "[LIMITED STORE EVENT]: The event is ending in 10 minutes!", "");
				}
			});


		}
	}
}