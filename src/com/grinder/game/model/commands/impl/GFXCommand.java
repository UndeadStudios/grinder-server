package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

public class GFXCommand implements Command {

    @Override
    public String getSyntax() {
        return "[id]";
    }

    @Override
    public String getDescription() {
        return "Performs a graphic id function.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        int gfx = Integer.parseInt(parts[1]);
//        int delay = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
        int height = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
       	player.performGraphic(new Graphic(gfx, 0, height));
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }

}
