package com.grinder.game.model.commands.impl;

import com.grinder.Config;
import com.grinder.ServerIO;
import com.grinder.game.GameConstants;
import com.grinder.game.content.miscellaneous.Broadcast;
import com.grinder.game.content.miscellaneous.StoreBonusEvent;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Position;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.DiscordBot;
import com.grinder.util.Misc;

public class StoreEventCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Triggers random store bonus event.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {

		StoreBonusEvent.runRandomStoreEvent(player);

    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || rights == PlayerRights.CO_OWNER || rights == PlayerRights.ADMINISTRATOR);
    }
}
