package com.grinder.game.content.cluescroll.task.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.content.cluescroll.ClueScroll;
import com.grinder.game.content.cluescroll.task.ClueTask;
import com.grinder.util.Misc;
import com.grinder.util.oldgrinder.StreamHandler;

/**
 * This task represents a {@link ClueTask} that scans for the {@link Position} relevant to solving a {@link SearchDigSpotClueTask}.
 *
 * @author Pb600
 * @author Stan van der Bend
 */
public class ScanDigSpotClueTask extends SearchDigSpotClueTask {

    private static final boolean DEBUG = true;
    private static final int ARROW_POINTING_AT_SPOT_HINT = 2;

    private final Position[] positions;
    private final int scanRange;

    private Position taskPosition;

    public ScanDigSpotClueTask(int taskID, int scanRange, ClueScroll clueScroll, Position... positions) {
        super(taskID, clueScroll, positions[0]);
        this.positions = positions;
        this.scanRange = scanRange;
    }

    public void scanLocation(Player player) {

        final int distanceBetweenSpotAndPlayer = Misc.distanceBetween(player.getPosition(), taskPosition);

        if (DEBUG)
            player.sendMessage("Your distance is " + distanceBetweenSpotAndPlayer + " steps far, and the minimum range must be " + scanRange + " steps.");

        if (distanceBetweenSpotAndPlayer <= scanRange) {

            StreamHandler.createObjectHints(player, taskPosition.getX(), taskPosition.getY(), taskPosition.getZ(), ARROW_POINTING_AT_SPOT_HINT);

            player.sendMessage("The orb glows as you scan. You are in range of the coordinate!");
            player.sendMessage("The coordinate is " + distanceBetweenSpotAndPlayer + " paces away.");

        } else
            player.sendMessage("You are too far away and nothing scans.");

    }

    @Override
    public boolean isPerformingTask(Player player, Object... args) {
        return player.getPosition().getZ() == height && player.getPosition().is(taskPosition, true);
    }

    @Override
    public boolean completeTask(Player player) {
        StreamHandler.removePlayerHint(player);
        return super.completeTask(player);
    }

    @Override
    public ClueTask randomize() {
        taskPosition = Misc.random(positions);
        return this;
    }

    @Override
    public ClueTask clone() {
        return new ScanDigSpotClueTask(taskID, scanRange, clueScroll, positions).setAgent(clueTaskAgent).randomize();
    }

    @Override
    public String toString() {
        return "ScanTaskType [position=" + taskPosition + " -> " + super.toString() + "]";
    }
}
