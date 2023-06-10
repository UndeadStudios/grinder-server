package com.grinder.game.model.commands.impl;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.commands.Command;

public class FinishAllTasksCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Completes all achievement tasks.";
	}

	@Override
	public void execute(Player player, String command, String[] parts) {
		for (int i = 0; i < AchievementType.values().length; i++) {
			AchievementManager.processFor(AchievementType.values()[i], AchievementType.values()[i].getAmount(), player);
			AchievementManager.complete(player, AchievementType.values()[i]);
			}
		player.getPacketSender().sendMessage("<img=742> All tasks has been set to completed!");
	}
	
	
    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER/* || player.getUsername().equals("Mod Hellmage")*/);
    }
}
