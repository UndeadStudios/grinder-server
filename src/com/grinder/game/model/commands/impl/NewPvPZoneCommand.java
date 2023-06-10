package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Position;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.util.Misc;

public class NewPvPZoneCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Teleports you to the new PvP Minigame zone.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (TeleportHandler.checkReqs(player, new Position(3446, 4719), true, false, player.getSpellbook().getTeleportType())) {
            TeleportHandler.teleport(player, new Position(3446 + Misc.getRandomInclusive(3), 4719 + Misc.getRandomInclusive(20), 0), TeleportType.NORMAL, true, true);
        }
    }

    @Override
    public boolean canUse(Player player) {
        return PlayerUtil.isDeveloper(player);
    }
}
