package com.grinder.game.task.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.ForceMovement;
import com.grinder.game.model.Position;
import com.grinder.game.task.Task;

/**
 * A {@link Task} implementation that handles forced movement.
 * An example of forced movement is the Wilderness ditch.
 *
 * @author Professor Oak
 */
public class ForceMovementTask extends Task {

    private Player player;
    private Position end;
    private Position start;

    public ForceMovementTask(Player player, int delay, ForceMovement forceM) {
        super(delay, player, (delay == 0));
        this.player = player;
        this.start = forceM.getStart().clone();
        this.end = forceM.getEnd().clone();

        //Reset combat
        player.getCombat().reset(false);

        player.setLastPosition(player.getPosition().clone());
        //Reset movement queue
        player.getMotion().clearSteps();

        //Playerupdating
        player.setForceMovement(forceM);
    }

    @Override
    protected void execute() {
        int x = start.getX() + end.getX();
        int y = start.getY() + end.getY();
        int z = start.getZ() + end.getZ();
        player.moveTo(new Position(x, y, z));
        player.setForceMovement(null);
        stop();
    }
}
