package com.grinder.game.model.commands.impl;

import java.util.Optional;

import com.grinder.game.World;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

public class ForceTeleTomeCommand implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
		return "Forces the player to be teleported to you.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (command.length() <= 9) {
            player.sendMessage("Wrong usage of the command!");
            return;
        }
        Optional<Player> plr = World.findPlayerByName(command.substring(parts[0].length() + 1));
        if (plr.isPresent()) {
        	if (PlayerUtil.isDeveloper(plr.get()) && EntityExtKt.getBoolean(plr.get(), Attribute.INVISIBLE, false)) {
            	player.getPacketSender().sendMessage("<img=742> The player that you're trying to teleport to you is not currently online!");
            	return;
        	}
            if (plr.get().getStatus() == PlayerStatus.TRADING) {
                player.sendMessage("<img=742> The player that you're trying to teleport is in a busy state.");
                return;
            }
            if (plr.get().getStatus() == PlayerStatus.BANKING) {
                player.sendMessage("<img=742> The player that you're trying to teleport is in a busy state.");
                return;
            }
            if (plr.get().getStatus() == PlayerStatus.DICING) {
                player.sendMessage("<img=742> The player that you're trying to teleport is in a busy state.");
                return;
            }
            if (plr.get().getStatus() == PlayerStatus.SHOPPING) {
                player.sendMessage("<img=742> The player that you're trying to teleport is in a busy state.");
                return;
            }
            if (plr.get().getStatus() == PlayerStatus.DUELING) {
                player.sendMessage("<img=742> The player that you're trying to teleport is in a busy state.");
                return;
            }
            if (!PlayerUtil.isDeveloper(player) && plr.get().busy()) {
                player.sendMessage("<img=742> The player that you're trying to teleport is in a busy state.");
                return;
            }
            plr.get().moveTo(player.getPosition().clone());
            plr.get().getPacketSender().sendMessage("<img=742> You have been teleported to: @dre@" + PlayerUtil.getImages(player) + "" + player.getUsername() +"</col>!");
            player.getPacketSender().sendMessage("<img=742> You have teleported @dre@" + plr.get().getUsername() + "</col> to you!");
        } else {
        	player.getPacketSender().sendMessage("<img=742> The player that you're trying to teleport to you is not currently online!");
        	return;
        }
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }

}
