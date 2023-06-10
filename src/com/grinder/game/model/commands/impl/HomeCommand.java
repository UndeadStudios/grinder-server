package com.grinder.game.model.commands.impl;

import com.grinder.game.GameConstants;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.commands.Command;
import com.grinder.util.Misc;

public class HomeCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Teleports you to the home area.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {
		Position teleportPosition = GameConstants.DEFAULT_POSITION.clone();
		if (TeleportHandler.checkReqs(player, GameConstants.DEFAULT_POSITION, true, false, player.getSpellbook().getTeleportType())) {
			TeleportHandler.teleport(player, GameConstants.DEFAULT_POSITION.randomize(3), TeleportType.HOME_QUICK, false, true);
		}
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
