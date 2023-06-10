package com.grinder.game.model.commands.impl;

import static com.grinder.game.entity.agent.player.PlayerRights.ADMINISTRATOR;
import static com.grinder.game.entity.agent.player.PlayerRights.CO_OWNER;
import static com.grinder.game.entity.agent.player.PlayerRights.DEVELOPER;
import static com.grinder.game.entity.agent.player.PlayerRights.GLOBAL_MODERATOR;
import static com.grinder.game.entity.agent.player.PlayerRights.MODERATOR;
import static com.grinder.game.entity.agent.player.PlayerRights.OWNER;
import static com.grinder.game.entity.agent.player.PlayerRights.SERVER_SUPPORTER;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.model.punishment.PunishmentType;

public class UnMutePlayer implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
        return "Removes the mute from the player's account.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        final String targetName = command.substring(parts[0].length() + 1);
        PunishmentManager.revoke(player, targetName, PunishmentType.MUTE);
    }

    @Override
    public boolean canUse(Player player) {
        return player.getRights().anyMatch(SERVER_SUPPORTER, MODERATOR, GLOBAL_MODERATOR, ADMINISTRATOR, DEVELOPER, CO_OWNER, OWNER);
    }

}
