package com.grinder.game.model.commands.impl;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.model.punishment.PunishmentType;
import com.grinder.util.Logging;

public class IpMutePlayerCommand implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
        return "IP mute's the account from the server.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {

        final String targetName = command.substring(parts[0].length() + 1);

        World.findPlayerByName(targetName).ifPresent(target -> {
            PunishmentManager.submit(player, targetName, PunishmentType.IP_MUTE);
            Logging.log("IPmutes", player.getUsername() + " has IP muted the account: " + target.getUsername() + " from IP address: " + target.getHostAddress());
            target.sendMessage("You have been IP muted by " + player.getUsername() + "!");
            player.sendMessage(targetName+" was successfully IP muted!");
        });
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || rights == PlayerRights.MODERATOR || rights == PlayerRights.GLOBAL_MODERATOR || rights == PlayerRights.ADMINISTRATOR
        		|| rights == PlayerRights.CO_OWNER);
    }

}
