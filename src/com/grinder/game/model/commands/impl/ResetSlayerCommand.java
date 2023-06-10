package com.grinder.game.model.commands.impl;

import java.util.Optional;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.util.Misc;

public class ResetSlayerCommand implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
        return "Resets Slayer task for a player.";
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
        plr.get().getSlayer().setTask(null);
        plr.get().getPacketSender().sendMessage("<img=742> @dre@" + PlayerUtil.getImages(player) + "" + player.getUsername() +"</col> has reset your Slayer task.");
        player.sendMessage("<img=742> @dre@You have reset the Slayer task for " + plr.get().getUsername() + "'s</col>!");
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || rights == PlayerRights.CO_OWNER || rights == PlayerRights.GLOBAL_MODERATOR
        || rights == PlayerRights.ADMINISTRATOR || rights == PlayerRights.MODERATOR);
    }

}
