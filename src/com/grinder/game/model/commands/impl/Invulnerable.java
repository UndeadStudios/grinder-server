package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

public class Invulnerable implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Sets your account on/off invulnerable mode.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.setInvulnerable(!player.isInvulnerable());
        player.getPacketSender().sendMessage("Invulnerable: " + String.valueOf(player.isInvulnerable()));
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || player.getUsername().equals("Stan");
    }
}
