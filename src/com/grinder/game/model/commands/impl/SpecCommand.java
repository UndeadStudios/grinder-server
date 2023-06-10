package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

public class SpecCommand implements Command {

    @Override
    public String getSyntax() {
        return "[amount]";
    }

    @Override
    public String getDescription() {
        return "Sets your special charge to x amount.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        int amt = 100;
        if (parts.length > 1)
            amt = Integer.parseInt(parts[1]);
        player.setSpecialPercentage(amt);
        SpecialAttackType.updateBar(player, true);
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }

}
