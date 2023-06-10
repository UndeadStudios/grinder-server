package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.Position;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.util.Misc;

public class ShopsAreaCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Teleports you to the shops area.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {
		if (TeleportHandler.checkReqs(player, new Position(3080, 3504), true, false, player.getSpellbook().getTeleportType())) {
			TeleportHandler.teleport(player, new Position(3080 + Misc.getRandomInclusive(2), 3504 + Misc.getRandomInclusive(2), 0), player.getSpellbook().getTeleportType(), false, true);
		}
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
