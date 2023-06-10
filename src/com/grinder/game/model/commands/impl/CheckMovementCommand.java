package com.grinder.game.model.commands.impl;

import com.grinder.game.collision.CollisionManager;
import com.grinder.game.entity.agent.movement.pathfinding.traverse.SmallTraversal;
import com.grinder.game.entity.agent.movement.pathfinding.traverse.TraversalType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.Direction;
import com.grinder.game.model.commands.Command;

import java.util.Arrays;

/**
 * Checks if player can move in a set direction
 */
public class CheckMovementCommand implements Command {

    @Override
    public String getSyntax() {
        return "[direction]";
    }

    @Override
    public String getDescription() {
        return "Checks if player can move in a direction.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        String name = command.replace(parts[0] + " ", "").replaceAll(" ", "_");
        Direction direction = Arrays.stream(Direction.values()).filter(dir -> dir.name().equalsIgnoreCase(name)).findFirst().orElse(Direction.NONE);
        if (direction == Direction.NONE) {
            player.sendMessage("Unable to find direction '" + name + "'.");
            return;
        }
        boolean blocked = new SmallTraversal(TraversalType.Ignored, false).blocked(player.getPosition(), direction);
        player.sendMessage(direction.name().toLowerCase() + " is " + (blocked ? "blocked" : "free"));
        System.out.println("Clipping: " + CollisionManager.getClipping(player.getPosition().copy().add(direction.getX(), direction.getY())));
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER;
    }
}
