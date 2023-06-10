package com.grinder.game.model.commands.impl;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.model.punishment.PunishmentType;
import com.grinder.util.Logging;

public class MacBanCommand implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
        return "MAC bans the player's account from the server.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {

        final String targetName = command.substring(parts[0].length() + 1);
        World.findPlayerByName(command.substring(parts[0].length() + 1)).ifPresent(target -> {
            Logging.log("hostbans", player.getUsername() + " has host banned the account: " + target.getUsername() + " from MAC address: " + target.getMacAddress());
        });
        PunishmentManager.submit(player, targetName, PunishmentType.MAC_BAN);
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || rights == PlayerRights.GLOBAL_MODERATOR || rights == PlayerRights.ADMINISTRATOR || rights == PlayerRights.CO_OWNER);
    }

}
