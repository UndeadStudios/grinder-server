package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.commands.Command;

public class ToggleStaffPvpCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Toggles you un-attackable in the Wilderness.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.setStaffPvPToggled(player.staffPvpToggled() ? false : true);
        player.sendMessage("<img=788> You have toggled Wilderness safety to " + (player.staffPvpToggled() ? "@gre@ON</col>" : "@red@OFF</col>") +"!");
    }

    @Override
    public boolean canUse(Player player) {
        return PlayerUtil.isMediumStaff(player);
    }
}
