package com.grinder.game.content.skill.skillable.impl.agility.obstacle.impl;

import com.grinder.game.content.skill.SkillRequirement;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.SkillActionTask;
import com.grinder.game.content.skill.skillable.impl.agility.obstacle.AgilityObstacle;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.movement.pathfinding.PathFinder;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.updating.block.BasicAnimationSet;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

public class MultipleAgilityObstacles implements AgilityObstacle {

    private SkillRequirement settings;

    private AgilityObstacle[] obstacles;

    private int[] delay;

    private boolean ignoreFail = false;

    private Position facePosition;

    private boolean ignoreFaceX;

    private boolean ignoreFaceY;

    public MultipleAgilityObstacles(SkillRequirement settings, AgilityObstacle[] obstacles, int[] delay) {
        this.setSettings(settings);
        this.setObstacles(obstacles);
        this.setDelay(delay);
    }

    public MultipleAgilityObstacles(SkillRequirement settings, AgilityObstacle[] obstacles, int[] delay, Position facePosition, boolean ignoreFaceX, boolean ignoreFaceY) {
        this.setSettings(settings);
        this.setObstacles(obstacles);
        this.setDelay(delay);
        this.setFacePosition(facePosition);
        this.setIgnoreFaceX(ignoreFaceX);
        this.setIgnoreFaceY(ignoreFaceY);
    }

    public MultipleAgilityObstacles(SkillRequirement settings, AgilityObstacle[] obstacles, int[] delay, boolean ignoreFail) {
        this.setSettings(settings);
        this.setObstacles(obstacles);
        this.setDelay(delay);
        this.setIgnoreFail(ignoreFail);
    }

    @Override
    public void execute(Player player) {
        handleObstacle(player, getObstacles()[0], getObstacles().length, 0);
    }

    public boolean handleObstacle(Player player, AgilityObstacle obstacle, int obstaclesLeft, int onObstacle) {

        if (getFacePosition() != null) {
            if (ignoreFaceX) {
                player.setPositionToFace(new Position(player.getX(), getFacePosition().getY()), true);
            } else if (ignoreFaceY) {
                player.setPositionToFace(new Position(getFacePosition().getX(), player.getY()), true);
            } else {
                player.setPositionToFace(getFacePosition(), true);
            }
        }

        if (obstacle instanceof CrossAgilityObstacle) {
            Position start = ((CrossAgilityObstacle) obstacle).getStartPosition();
            if (!player.getPosition().sameAs(start)) {
                if (start != null)
                    player.getMotion().enqueuePathToWithoutCollisionChecks(start.getX(), start.getY());
                TaskManager.submit(1, () -> handleObstacle(player, obstacle, obstaclesLeft, onObstacle));
                return true;
            }
        }

        if (obstacle instanceof ForceMovementAgilityObstacle && onObstacle == 0) {
            Position start = ((ForceMovementAgilityObstacle) obstacle).getStartPosition();
            if (!player.getPosition().sameAs(start)) {
                if (start != null)
                    player.getMotion().enqueuePathToWithoutCollisionChecks(start.getX(), start.getY());
                TaskManager.submit(1, () -> handleObstacle(player, obstacle, obstaclesLeft, onObstacle));
                return true;
            }
        }
        /*
         * The skill action
         */
        SkillActionTask task = new SkillActionTask(getSettings(), 1, Skill.AGILITY, getDelay()[onObstacle], true) {
            @Override
            public void sendBeforeSkillAction(Player player) {
                obstacle.execute(player);

                if (obstacle instanceof ClimbAgilityObstacle) {

                    ClimbAgilityObstacle obs = (ClimbAgilityObstacle) obstacle;

                    if (obs.getNext() != null && (!EntityExtKt.getBoolean(player, Attribute.DID_FAIL_AGILITY_OBSTACLE, false) || getIgnoreFail())) {
                        TaskManager.submit(new Task(1) {

                            @Override
                            protected void execute() {
                                obs.getNext().execute(player);
                                stop();
                            }

                        });
                    }
                }
            }

            @Override
            public void sendEndAction(Player player) {
                if (obstaclesLeft - 1 > 0 && (!EntityExtKt.getBoolean(player, Attribute.DID_FAIL_AGILITY_OBSTACLE, false) || getIgnoreFail()))
                    handleObstacle(player, getObstacles()[onObstacle + 1], obstaclesLeft - 1, onObstacle + 1);

            }
        };
        SkillUtil.startActionTask(player, task);
        return true;
    }

    public void setObstacles(AgilityObstacle[] obstacles) {
        this.obstacles = obstacles;
    }

    public AgilityObstacle[] getObstacles() {
        return obstacles;
    }

    public void setSettings(SkillRequirement settings) {
        this.settings = settings;
    }

    public SkillRequirement getSettings() {
        return settings;
    }

    public void setDelay(int[] delay) {
        this.delay = delay;
    }

    public int[] getDelay() {
        return delay;
    }

    public boolean getIgnoreFail() {
        return ignoreFail;
    }

    public void setIgnoreFail(boolean ignoreFail) {
        this.ignoreFail = ignoreFail;
    }

    public Position getFacePosition() { return facePosition; }

    public void setFacePosition(Position facePosition) { this.facePosition = facePosition; }

    public boolean getIgnoreFaceX() { return ignoreFaceX; }

    public void setIgnoreFaceX(boolean ignoreFaceX) { this.ignoreFaceX = ignoreFaceX; }

    public boolean getIgnoreFaceY() { return ignoreFaceY; }

    public void setIgnoreFaceY(boolean ignoreFaceY) { this.ignoreFaceY = ignoreFaceY; }
}
