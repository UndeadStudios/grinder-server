package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Position;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.util.Misc;

public class SEdgeCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Teleports you to Old Edgeville.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        TeleportHandler.teleport(player, new Position(3278 + Misc.getRandomInclusive(3), 9828 + Misc.getRandomInclusive(3), 0), TeleportType.NORMAL, false, false);
    }

    @Override
    public boolean canUse(Player player) {
        return PlayerUtil.isStaff(player);
    }
}
