package com.grinder.game.model.commands.impl;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.util.Logging;

import java.util.Optional;

public class ForceBanCommand implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
        return "Forces the ban of an account.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        new MacBanCommand().execute(player, command, parts);
        new IpBanPlayerCommand().execute(player, command, parts);
        World.findPlayerByName(command.substring(parts[0].length() + 1)).ifPresent(target -> {
            Logging.log("hostbans", player.getUsername() + " has host banned the account: " + target.getUsername() + " from MAC address: " + target.getMacAddress());
        });
    }

    @Override
    public boolean canUse(Player player) {
        return player.getRights().anyMatch(PlayerRights.OWNER, PlayerRights.DEVELOPER);
    }

}
