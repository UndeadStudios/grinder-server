package com.grinder.game.model.commands.impl;

import com.grinder.Config;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.commands.Command;
import com.grinder.util.DiscordBot;

public class DisableShoppingCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Disable's server [SHOPPING] mechanics.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {
		Config.shopping_enabled = !Config.shopping_enabled;
		if (!Config.shopping_enabled) {
			player.sendMessage("The server [SHOPPING] system has been switched @red@OFF</col>!");
			PlayerUtil.broadcastMessage("@red@[SERVER]:</col> The @red@[SHOPPING]</col> system has been switched @red@OFF</col> by " + PlayerUtil.getImages(player) + "" + player.getUsername() +".");
			if(DiscordBot.ENABLED)
				DiscordBot.INSTANCE.sendModMessage("[SERVER]: The [SHOPPING] system has been switched OFF by " + player.getUsername() +".");
		} else {
			player.sendMessage("The server [SHOPPING] system has been switched @gre@ON</col>!");
			PlayerUtil.broadcastMessage("@gre@[SERVER]:</col> The @gre@[SHOPPING]</col> system has been switched back @gre@ON</col> by " + PlayerUtil.getImages(player) + "" + player.getUsername() +".");
			if(DiscordBot.ENABLED)
				DiscordBot.INSTANCE.sendModMessage("[SERVER]: The [SHOPPING] system has been switched ON by " + player.getUsername() +".");
		}
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || rights == PlayerRights.CO_OWNER || rights == PlayerRights.ADMINISTRATOR || rights == PlayerRights.GLOBAL_MODERATOR);
    }
}
