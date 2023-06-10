package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.commands.Command;
import com.grinder.util.Misc;

public class DiceCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Teleports you to dice area.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {
		player.getPacketSender().sendMessage("@red@It is advisable to record proof if you're going to dice with bulk amounts.");
		if (TeleportHandler.checkReqs(player, new Position(2848, 2585), true, false, player.getSpellbook().getTeleportType())) {
			TeleportHandler.teleport(player, new Position(2848 + Misc.getRandomInclusive(5), 2585 + Misc.getRandomInclusive(3), 0), TeleportType.NORMAL, false, true);
		}
		//TeleportHandler.teleport(player, new Position(2972 + Misc.getRandomInclusive(5), 9735 + Misc.getRandomInclusive(3), 0), TeleportType.NORMAL, false, true);
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
