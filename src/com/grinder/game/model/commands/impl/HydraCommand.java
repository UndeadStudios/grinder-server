package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.npc.monster.boss.impl.hydra.AlchemicalHydraInstance;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.commands.Command;

public class HydraCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Start's the Alchemical Hydra boss instance.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
		AlchemicalHydraInstance.climbRocks(player);
    }

    @Override
    public boolean canUse(Player player) {
        return player.getRights() == PlayerRights.OWNER || player.getRights() == PlayerRights.DEVELOPER;
    }

}
