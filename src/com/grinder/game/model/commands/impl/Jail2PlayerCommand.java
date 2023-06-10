package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.model.punishment.PunishmentType;
import com.grinder.util.Logging;

public class Jail2PlayerCommand implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
        return "Jails the player's account to the 2nd jail.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {

        final String targetName = command.substring(parts[0].length() + 1);

        Logging.log("jails", player.getUsername() + " has jailed the account: " + targetName);
        PunishmentManager.submit(player, targetName, PunishmentType.JAIL);
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || rights == PlayerRights.MODERATOR || rights == PlayerRights.GLOBAL_MODERATOR
                || rights == PlayerRights.ADMINISTRATOR || rights == PlayerRights.CO_OWNER || rights == PlayerRights.SERVER_SUPPORTER);
    }
}
