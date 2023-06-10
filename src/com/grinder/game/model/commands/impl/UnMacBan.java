package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.model.punishment.PunishmentType;

public class UnMacBan implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
        return "Removes the MAC ban from the player's account.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        final String targetName = command.substring(parts[0].length() + 1);
        PunishmentManager.revoke(player, targetName, PunishmentType.MAC_BAN);
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || rights == PlayerRights.GLOBAL_MODERATOR || rights == PlayerRights.ADMINISTRATOR
        		|| rights == PlayerRights.CO_OWNER);
    }

}
