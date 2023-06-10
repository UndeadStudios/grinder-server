package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;

public class InfernoCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Teleports you to the inferno Minigame.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
    	TeleportHandler.teleport(player, new Position(2496, 5116, 0), TeleportType.NORMAL, false, false);
    }

    @Override
    public boolean canUse(Player player) {
        return player.getRights() == PlayerRights.OWNER || player.getRights() == PlayerRights.DEVELOPER;
    }

}
