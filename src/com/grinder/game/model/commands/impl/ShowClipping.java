package com.grinder.game.model.commands.impl;

import com.grinder.game.collision.CollisionManager;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.object.ClippedMapObjects;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.commands.Command;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-10
 */
public class ShowClipping implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Shows the clippings by a gfx.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {

        Direction direction = Direction.NORTH;
        int length = 6;

        Position start = player.getPosition().copy();

        for (int tile = 0; tile < length; tile++) {
            start = start.move(direction);
            int clipping = CollisionManager.getClipping(start.getX(), start.getY(), start.getZ());
            if(clipping != 0)
                player.getPacketSender().sendGraphic(new Graphic(187), start);

            player.sendMessage("tile["+tile+"] is "+Integer.toHexString(clipping));
        }

        final Position front = player.getPosition().clone().move(Direction.NORTH);
        ClippedMapObjects.getObjectsAt(front).forEach(obj -> {
            player.sendMessage(obj.getId()+" ["+obj.getObjectType()+"] -> "+obj.getDefinition().getName()+", solid = "+obj.getDefinition().solid);
        });

    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }
}
