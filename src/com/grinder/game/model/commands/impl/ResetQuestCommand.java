
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

public class ResetQuestCommand implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
        return "Resets the player's quest id.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        // TODO: USAGE: ::resetquest [player name] [quest name]
        String player2 = command.substring(parts[2].length() + 1);
        Optional<Player> plr = World.findPlayerByName(player2);
        player2 = Misc.capitalize(player2);
        if (!plr.isPresent()) {
            player.getPacketSender().sendMessage(player2 + " is not currently online.");
            return;
        }
        // Quest ordinal value
        final String questName = command.substring(parts[0].length() + 1);
        int setStageId = Integer.parseInt(parts[1]);

        plr.get().getQuest().tracker.setProgress(questName, setStageId);
        plr.get().getQuest().tracker.update();



        // Send messages to player and staff
		player.sendMessage("<img=741> The player @dre@" + plr.get().getUsername() + "</col> quest ["+questName+"] has been successfully removed!");
		plr.get().sendMessage("<img=741> Your quest ["+questName+"] has been reset by @dre@" + PlayerUtil.getImages(player) + "" + player.getUsername() +"</col>!");
        plr.get().sendMessage("Please logout and login again for this to take effect.");

        // Add server logging
		Logging.log("questreset", player.getUsername() + " has reset the quest ["+questName+"] of the account: " + plr.get().getUsername() +"");

        // Discord logging
        if(DiscordBot.ENABLED)
            DiscordBot.INSTANCE.sendModMessage(plr.get().getUsername() + " quest ["+questName+"] has been reset by " + player.getUsername() +".");
    }

    @Override
    public boolean canUse(Player player) {
        return player.getRights().anyMatch(GLOBAL_MODERATOR, ADMINISTRATOR, DEVELOPER, CO_OWNER, OWNER);
    }

}
