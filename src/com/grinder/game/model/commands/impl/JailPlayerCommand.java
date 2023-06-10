package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.model.punishment.PunishmentType;
import com.grinder.util.Logging;

import static com.grinder.game.entity.agent.player.PlayerRights.*;

public class JailPlayerCommand implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
        return "Jails the player's account.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (command.length() <= 5) {
            player.sendMessage("Wrong usage of the command!");
            return;
        }
        final String targetName = command.substring(parts[0].length() + 1);

        Logging.log("jails", player.getUsername() + " has jailed the account: " + targetName);
        PunishmentManager.submit(player, targetName, PunishmentType.JAIL);
    }

    @Override
    public boolean canUse(Player player) {
        return player.getRights().anyMatch(SERVER_SUPPORTER, MODERATOR, GLOBAL_MODERATOR, ADMINISTRATOR, DEVELOPER, CO_OWNER, OWNER);
    }

}
