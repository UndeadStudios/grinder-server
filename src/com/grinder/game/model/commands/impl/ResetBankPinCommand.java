
package com.grinder.game.model.commands.impl;

import java.util.Optional;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.commands.Command;
import com.grinder.util.DiscordBot;
import com.grinder.util.Logging;
import com.grinder.util.Misc;

import static com.grinder.game.entity.agent.player.PlayerRights.*;
import static com.grinder.game.entity.agent.player.PlayerRights.OWNER;

public class ResetBankPinCommand implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
        return "Resets the player's bank PIN.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        String player2 = command.substring(parts[0].length() + 1);
        Optional<Player> plr = World.findPlayerByName(player2);
        player2 = Misc.capitalize(player2);
        if (!plr.isPresent()) {
            player.getPacketSender().sendMessage(player2 + " is not currently online.");
            return;
        }
        if (PlayerUtil.isHighStaff(plr.get())) {
            player.getPacketSender().sendMessage("You can't reset the bank PIN of this account.");
            return;
        }
        plr.get().setPin(-1);
        plr.get().setAccountFlagged(false);
        plr.get().getRecentIPS().clear();
		player.sendMessage("<img=788> The player @dre@" + plr.get().getUsername() + "</col> bank PIN has been successfully removed!");
		plr.get().sendMessage("<img=788> Your bank PIN has been reset by @dre@" + PlayerUtil.getImages(player) + "" + player.getUsername() +"</col>!");
        plr.get().sendMessage("Please logout and login again for this to take effect.");
		Logging.log("pinreset", player.getUsername() + " has reset Bank PIN of the account: " + plr.get().getUsername() +"");
        if(DiscordBot.ENABLED)
            DiscordBot.INSTANCE.sendModMessage(plr.get().getUsername() + " bank PIN has been reset by " + player.getUsername() +".");
    }

    @Override
    public boolean canUse(Player player) {
        return player.getRights().anyMatch(MODERATOR, GLOBAL_MODERATOR, ADMINISTRATOR, DEVELOPER, CO_OWNER, OWNER);
    }

}
