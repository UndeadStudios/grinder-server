package com.grinder.game.model.commands.impl;

import com.grinder.game.collision.CollisionManager;
import com.grinder.game.collision.TileFlags;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.commands.Command;

/**
 * Displays all the solid objects within an distance from the player
 */
public class ShowCollisionCommand implements Command {

    @Override
    public String getSyntax() {
        return "[distance]";
    }

    @Override
    public String getDescription() {
        return "Displays tile collision of a nearby area.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        int distance = 5;
        if(parts.length > 1) {
            distance = Integer.parseInt(parts[1]);
        }

        for (int x = -distance; x < distance; x++) {
            for (int y = -distance; y < distance; y++) {
                Position position = player.getPosition().clone().add(x, y);
                if((CollisionManager.getClipping(position.getX(), position.getY(), position.getZ()) & TileFlags.BLOCKED) != 0) {
                    player.getPacketSender().sendGraphic(new Graphic(187), position.clone());
                }
            }
        }
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER;
    }
}
