package com.grinder.net.packet.impl;

import com.grinder.game.content.minigame.MinigameManager;
import com.grinder.game.entity.agent.player.LogoutPolicy;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.bot.BotPlayer;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.sound.Sounds;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.util.DiscordBot;
import com.grinder.util.Misc;

public class PlayerInactivePacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {
		if (player == null)
			return;
		if (!player.isActive()) {
			return;
		}
		if (player instanceof BotPlayer)
			return;

		if (player.getMinigame() != null) { // Minigame

			MinigameManager.leaveMinigame(player, MinigameManager.publicMinigame);

			// Disable loggin for spawn game modes
			if (!player.getGameMode().isSpawn()) {
				// Discord logging
				if (DiscordBot.ENABLED)
					DiscordBot.INSTANCE.sendServerLogs("[MINIGAME]: " + player.getUsername() + " has been kicked from the minigame for being afk for too long.");
			}
		}

    	if (player.getArea() != null) {
			if (AreaManager.inWilderness(player)) {
				player.logout(LogoutPolicy.IDLE);
				return;
			}
		}

		if (player.getArea() != null) {
			if (AreaManager.BANK_AREAS.contains(player) && Misc.getRandomInclusive(10) == 1) {
				player.getPacketSender().sendSound(Misc.randomInt(Sounds.SOMETHING_IN_GRAND_EXCHANGE));
			}
		}
    }
}
