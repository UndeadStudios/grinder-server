package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.Position;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.util.Misc;

public class AdminTeleportCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Teleports you to admins black zone.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {
		TeleportHandler.teleport(player, new Position(1901 + Misc.getRandomInclusive(3), 5358 + Misc.getRandomInclusive(3), 0), TeleportType.NORMAL, false, false);
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || rights == PlayerRights.CO_OWNER || rights == PlayerRights.ADMINISTRATOR);
    }
}
