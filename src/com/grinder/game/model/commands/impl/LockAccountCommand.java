package com.grinder.game.model.commands.impl;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.model.punishment.PunishmentType;
import com.grinder.util.Logging;

import static com.grinder.game.entity.agent.player.PlayerRights.*;

public class LockAccountCommand implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
        return "Locks the player's account for safety reasons.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {

        final String targetName = command.substring(parts[0].length() + 1);

        World.findPlayerByName(targetName).filter(target -> {
            if(target.getRights().isStaff(PlayerRights.ADMINISTRATOR)){
                player.getPacketSender().sendMessage("<img=742> " + targetName +" can't be locked. You can only lock ranks below you.");
                return false;
            }
            return true;
        }).ifPresent(target -> {
            PunishmentManager.submit(player, targetName, PunishmentType.LOCK);
            player.getPacketSender().sendMessage(targetName + " has been sucessfully locked!");
            Logging.log("locks", player.getUsername() + " has locked the account: " + targetName);
        });
    }

    @Override
    public boolean canUse(Player player) {
         return player.getRights().anyMatch(MODERATOR, GLOBAL_MODERATOR, ADMINISTRATOR, DEVELOPER, CO_OWNER, OWNER);
    }

}
