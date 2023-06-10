package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

public class BarrageCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Adds 1,000 barrage spell runes.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
    	player.getInventory().add(560, 1000); // Death rune
    	player.getInventory().add(565, 1000); // Blood rune
    	player.getInventory().add(555, 1000); // Water rune
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }

}
