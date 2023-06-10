package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.LogoutPolicy;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.commands.Command;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-04
 */
public class ForceChannelInactive implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Forces your channel to logout in two seconds.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.logout(LogoutPolicy.IDLE);
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER;
    }
}
