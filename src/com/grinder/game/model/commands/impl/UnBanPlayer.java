package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.model.punishment.PunishmentType;

import static com.grinder.game.entity.agent.player.PlayerRights.*;
import static com.grinder.game.entity.agent.player.PlayerRights.OWNER;

public class UnBanPlayer implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
        return "Removes the ban from the player's account.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {

        final String targetName = command.substring(parts[0].length() + 1);

        PunishmentManager.revoke(player, targetName, PunishmentType.BAN);
    }

    @Override
    public boolean canUse(Player player) {
        return player.getRights().anyMatch(MODERATOR, GLOBAL_MODERATOR, ADMINISTRATOR, DEVELOPER, CO_OWNER, OWNER);
    }

}
