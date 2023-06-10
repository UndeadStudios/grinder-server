package com.grinder.game.model.commands.impl;

import com.grinder.game.content.gambling.lottery.Lottery;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.commands.Command;


public class DrawLottery implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Draws the lottery.";
	}

	@Override
	public void execute(Player player, String command, String[] parts) {
//		MinigameManager.publicMinigame = MinigameManager.BATTLE_ROYALE;
//		MinigameManager.startPublicMinigame();
		Lottery.pickWinner();
		player.getPacketSender().sendMessage("<img=784> You have drawn the lottery and declared the winner!");
	}

	@Override
	public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
		return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER|| rights == PlayerRights.ADMINISTRATOR || rights == PlayerRights.CO_OWNER
				|| rights == PlayerRights.GLOBAL_MODERATOR);
	}
}
