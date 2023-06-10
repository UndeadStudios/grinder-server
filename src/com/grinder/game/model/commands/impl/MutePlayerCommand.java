package com.grinder.game.model.commands.impl;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.model.punishment.PunishmentType;
import com.grinder.util.Logging;
import com.grinder.util.Misc;

import static com.grinder.game.entity.agent.player.PlayerRights.*;

public class MutePlayerCommand implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
        return "Mute's the player's account.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {

        final String targetName = command.substring(parts[0].length() + 1);

        PunishmentManager.submit(player, targetName, PunishmentType.MUTE);
        Logging.log("mutes", player.getUsername() + " has muted the account: " + targetName);
        player.sendMessage("You have successfully muted " + targetName +"!");

        final String playerName = Misc.capitalize(targetName);

        World.findPlayerByName(targetName)
                .ifPresent(target -> target.sendMessage("You have been muted by " + player.getUsername() + "!"));
    }

    @Override
    public boolean canUse(Player player) {
        return player.getRights().anyMatch(SERVER_SUPPORTER, MODERATOR, GLOBAL_MODERATOR, ADMINISTRATOR, DEVELOPER, CO_OWNER, OWNER);
    }

}
