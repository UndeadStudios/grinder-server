package com.grinder.game.model.commands.impl;

import java.util.Optional;

import com.grinder.game.World;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.*;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.commands.Command;
import com.grinder.util.Logging;
import com.grinder.util.Misc;

public class PromoteAdministrator implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
        return "Promotes the player to the Administrator rank.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (command.length() <= 4) {
            return;
        }
        String player2 = command.substring(parts[0].length() + 1);
        Optional<Player> plr = World.findPlayerByName(player2);
        player2 = Misc.capitalize(player2);
        if (!PlayerSaving.playerExists(player2) && !plr.isPresent()) {
            player.getPacketSender().sendMessage(player2 + " is not a valid online player.");
            return;
        }
        /*if (plr.get().busy()) {
        	player.sendMessage("<img=779> " + player2 + " is currently busy and can't be promoted.");
        	return;
        }*/
        if (plr.get().getRights().equals(PlayerRights.DEVELOPER) || plr.get().getRights().equals(PlayerRights.CO_OWNER) || plr.get().getRights().equals(PlayerRights.OWNER)) {
            return;
        }
        if (plr.get().getStatus() == PlayerStatus.TRADING
                || plr.get().getStatus() == PlayerStatus.DUELING
                || plr.get().getStatus() == PlayerStatus.DICING) {
            player.getPacketSender().sendMessage("<img=779> You can't use this command because " + player2 +" is in a busy state!");
            return;
        }
/*        if (plr.get().getGameMode().isIronman() || plr.get().getGameMode().isHardcore() || plr.get().getGameMode().isUltimate()) {
            player.getPacketSender().sendMessage("You can't promote a player with an Iron Man rank.");
            return;
        }*/
        if (plr.get().BLOCK_ALL_BUT_TALKING) {
            return;
        }
        if (plr.get().isInTutorial()) {
            return;
        }
        if (EntityExtKt.getBoolean(plr.get(), Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(plr.get(), Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
            return;
        }
        if (player.busy()) {
            player.getPacketSender().sendMessage("<img=779> You can't do that when you're busy.");
            return;
        }
        if (plr.get().getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
            player.getPacketSender().sendMessage("<img=779> " + player2 +" can't be promoted while AFK!");
            return;
        }
        plr.get().setRights(PlayerRights.ADMINISTRATOR);
        plr.get().setCrown(PlayerRights.ADMINISTRATOR.ordinal());
        plr.get().getPacketSender().sendRights();
        //plr.get().requestLogout();
        player.getPacketSender().sendMessage("" + player2 + " has been successfully promoted to Administrator's rank!");
        plr.get().getPacketSender().sendMessage("You have been promoted to Administrator's rank by " + PlayerUtil.getImages(player) + "" + player.getUsername() +"! Please relog for your status to show up.");
        PlayerUtil.broadcastMessage("<img=742> " + player2 + " has been promoted to Administrator's rank by " + PlayerUtil.getImages(player) + "" + player.getUsername() +"!");
        Logging.log("promotions", "" + player.getUsername() + " gave the administrator's rank to: " + plr.get().getUsername() + "");
    }

    @Override
    public boolean canUse(Player player) {
        return player.getUsername().equals("3lou 55") || player.getRights().equals(PlayerRights.DEVELOPER) || player.getRights().equals(PlayerRights.OWNER) || player.getRights().equals(PlayerRights.CO_OWNER);
    }

}
