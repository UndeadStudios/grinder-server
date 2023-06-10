package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.Position;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.util.Misc;

public class WestsCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Teleports you to the west Wilderness.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {
		if (TeleportHandler.checkReqs(player, new Position(3111, 3706), true, false, player.getSpellbook().getTeleportType())) {
			TeleportHandler.teleport(player, new Position(3111 + Misc.getRandomInclusive(3), 3706 + Misc.getRandomInclusive(3), 0), TeleportType.NORMAL, true, true);
		}
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
