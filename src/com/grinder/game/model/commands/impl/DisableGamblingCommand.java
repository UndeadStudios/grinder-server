package com.grinder.game.model.commands.impl;

import com.grinder.Config;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.commands.Command;
import com.grinder.util.DiscordBot;

public class DisableGamblingCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Disable's server [GAMBLING] mechanics.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {
		Config.gambling_enabled = !Config.gambling_enabled;
		if (!Config.gambling_enabled) {
			player.sendMessage("The server [GAMBLING] system has been switched @red@OFF</col>!");
			PlayerUtil.broadcastMessage("@red@[SERVER]:</col> The @red@[GAMBLING]</col> system has been switched @red@OFF</col> by " + PlayerUtil.getImages(player) + "" + player.getUsername() +".");
			if(DiscordBot.ENABLED)
				DiscordBot.INSTANCE.sendModMessage("[SERVER]: The [GAMBLING] system has been switched OFF by " + player.getUsername() +".");
		} else {
			player.sendMessage("The server [GAMBLING] system has been switched @gre@ON</col>!");
			PlayerUtil.broadcastMessage("@gre@[SERVER]:</col> The @gre@[GAMBLING]</col> system has been switched back @gre@ON</col> by " + PlayerUtil.getImages(player) + "" + player.getUsername() +".");
			if(DiscordBot.ENABLED)
				DiscordBot.INSTANCE.sendModMessage("[SERVER]: The [GAMBLING] system has been switched ON by " + player.getUsername() +".");
		}
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || rights == PlayerRights.CO_OWNER || rights == PlayerRights.ADMINISTRATOR || rights == PlayerRights.GLOBAL_MODERATOR);
    }
}
