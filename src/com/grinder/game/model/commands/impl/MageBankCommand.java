package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.commands.Command;
import com.grinder.util.Misc;

public class MageBankCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Teleports you to mage bank safe zone.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {
		if (TeleportHandler.checkReqs(player, new Position(2538, 4715), true, false, player.getSpellbook().getTeleportType())) {
			TeleportHandler.teleport(player, new Position(2538 + Misc.getRandomInclusive(3), 4715 + Misc.getRandomInclusive(3), 0), TeleportType.NORMAL, false, true);
		}
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
