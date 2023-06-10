package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;

/**
 * @author L E G E N D
 * @date 2/24/2021
 * @time 4:20 AM
 * @discord L E G E N D#4380
 */
public class VarbitCommand implements com.grinder.game.model.commands.Command {
    @Override
    public String getSyntax() {
        return "[id] [value]";
    }

    @Override
    public String getDescription() {
        return "Updates the value of a specific varbit.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        int id = Integer.parseInt(parts[1]);
        int value = Integer.parseInt(parts[2]);
        player.getPacketSender().sendVarbit(id, value);
        player.getPacketSender().sendConsoleMessage("Varbit " + id +" was updated to: " + value);
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER;
    }
}
