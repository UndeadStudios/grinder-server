package com.grinder.game.model.commands.impl;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.model.punishment.PunishmentType;
import com.grinder.util.Logging;

import static com.grinder.game.entity.agent.player.PlayerRights.*;

public class IpBanPlayerCommand implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
        return "IP ban's the account from the server.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {

        final String targetName = command.substring(parts[0].length() + 1);
        World.findPlayerByName(command.substring(parts[0].length() + 1)).ifPresent(target -> {
            Logging.log("IPbans", player.getUsername() + " has IP banned the account: " + target.getUsername() + " from IP address: " + target.getHostAddress());
        });
        PunishmentManager.submit(player, targetName, PunishmentType.IP_BAN);
    }

    @Override
    public boolean canUse(Player player) {
        return player.getRights().anyMatch(MODERATOR, GLOBAL_MODERATOR, ADMINISTRATOR, DEVELOPER, CO_OWNER, OWNER);
    }

}
