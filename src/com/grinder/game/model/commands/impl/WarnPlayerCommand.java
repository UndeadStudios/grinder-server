package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.model.punishment.PunishmentType;

import static com.grinder.game.entity.agent.player.PlayerRights.*;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-03
 */
public class WarnPlayerCommand implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
        return "Gives the player's account a warning.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {

        final String targetName = command.substring(parts[0].length() + 1);

        PunishmentManager.submit(player, targetName, PunishmentType.WARN);
    }

    @Override
    public boolean canUse(Player player) {
        return player.getRights().anyMatch(SERVER_SUPPORTER, MODERATOR, GLOBAL_MODERATOR, ADMINISTRATOR, DEVELOPER, CO_OWNER, OWNER);
    }

}
