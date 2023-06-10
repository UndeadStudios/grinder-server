package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

import static com.grinder.util.ItemID.BLOOD_MONEY;

public class IODCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Adds a PK set to your account.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        int[] items = new int[]{6570, 667, 13072, 13073, 6585, 4151, 20997, 391, 11840, BLOOD_MONEY};
        for (int rune : items) {
            player.getInventory().add(rune, 1);
        }
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || player.getUsername().equals("Mod Grinder"));
    }

}
