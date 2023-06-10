package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

public class UnlockPrayersCommands implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Unlocks all locked prayers.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        int type = Integer.parseInt(parts[1]);
        if (type == 0) {
            player.setPreserveUnlocked(true);
        } else if (type == 1) {
            player.setRigourUnlocked(true);
        } else if (type == 2) {
            player.setAuguryUnlocked(true);
        }
        player.getPacketSender().sendConfig(709, player.isPreserveUnlocked() ? 1 : 0);
        player.getPacketSender().sendConfig(711, player.isRigourUnlocked() ? 1 : 0);
        player.getPacketSender().sendConfig(713, player.isAuguryUnlocked() ? 1 : 0);
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || rights == PlayerRights.ADMINISTRATOR);
    }

}
