package com.grinder.game.content.skill.skillable.impl.agility.obstacle.impl;

import com.grinder.game.content.skill.skillable.impl.agility.obstacle.AgilityObstacle;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.movement.pathfinding.PathFinder;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.updating.block.BasicAnimationSet;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

public class CrossAgilityObstacle implements AgilityObstacle {

    private Position startPosition;

    private Animation prepareAnimation;

    private Position moveToPosition;

    private Animation actionAnimation;

    private Animation finishAnimation;

    private int speed;

    private int delay;

    private int direction;

    private boolean ignoreStartPosition;

    private int amount;

    private boolean shouldRun;

    private Sound sound;

    private boolean shouldRepeatSound;

    private int tickDelay;

    private String message = "";

    private String finishMessage = "";

    private boolean ignoreFail = false;

    public CrossAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
                                Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction) {
        this.setStartPosition(startPosition);
        this.setPrepareAnimation(prepareAnimation);
        this.setMoveToPosition(moveToPosition);
        this.setActionAnimation(actionAnimation);
        this.setFinishAnimation(finishAnimation);
        this.setSpeed(speed);
        this.setDelay(delay);
        this.setDirection(direction);
        this.setIgnoreStartPosition(false);
        this.setAmount(1);
        this.setShouldRun(false);
        this.setTickDelay(0);
    }

    public CrossAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
                                 Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction, String message, String finishMessage) {
        this.setStartPosition(startPosition);
        this.setPrepareAnimation(prepareAnimation);
        this.setMoveToPosition(moveToPosition);
        this.setActionAnimation(actionAnimation);
        this.setFinishAnimation(finishAnimation);
        this.setSpeed(speed);
        this.setDelay(delay);
        this.setDirection(direction);
        this.setIgnoreStartPosition(false);
        this.setAmount(1);
        this.setShouldRun(false);
        this.setTickDelay(0);
        this.setMessage(message);
        this.setFinishMessage(finishMessage);
    }

    public CrossAgilityObstacle(Position startPosition, int tickDelay, Animation prepareAnimation, Position moveToPosition,
                                Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction) {
        this.setStartPosition(startPosition);
        this.setPrepareAnimation(prepareAnimation);
        this.setMoveToPosition(moveToPosition);
        this.setActionAnimation(actionAnimation);
        this.setFinishAnimation(finishAnimation);
        this.setSpeed(speed);
        this.setDelay(delay);
        this.setDirection(direction);
        this.setIgnoreStartPosition(false);
        this.setAmount(1);
        this.setShouldRun(false);
        this.setTickDelay(tickDelay);
    }

    public CrossAgilityObstacle(Position startPosition, int tickDelay, Animation prepareAnimation, Position moveToPosition,
                                Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction, Sound sound, boolean shouldRepeatSound) {
        this.setStartPosition(startPosition);
        this.setPrepareAnimation(prepareAnimation);
        this.setMoveToPosition(moveToPosition);
        this.setActionAnimation(actionAnimation);
        this.setFinishAnimation(finishAnimation);
        this.setSpeed(speed);
        this.setDelay(delay);
        this.setDirection(direction);
        this.setIgnoreStartPosition(false);
        this.setAmount(1);
        this.setShouldRun(false);
        this.setTickDelay(tickDelay);
        this.setSound(sound);
        this.setShouldRepeatSound(shouldRepeatSound);
    }

    public CrossAgilityObstacle(Position startPosition, int tickDelay, Animation prepareAnimation, Position moveToPosition,
                                Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction, String message, String finishMessage) {
        this.setStartPosition(startPosition);
        this.setPrepareAnimation(prepareAnimation);
        this.setMoveToPosition(moveToPosition);
        this.setActionAnimation(actionAnimation);
        this.setFinishAnimation(finishAnimation);
        this.setSpeed(speed);
        this.setDelay(delay);
        this.setDirection(direction);
        this.setIgnoreStartPosition(false);
        this.setAmount(1);
        this.setShouldRun(false);
        this.setTickDelay(tickDelay);
        this.setMessage(message);
        this.setFinishMessage(finishMessage);
    }

    public CrossAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
                                Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction, Sound sound, boolean shouldRepeatSound) {
        this.setStartPosition(startPosition);
        this.setPrepareAnimation(prepareAnimation);
        this.setMoveToPosition(moveToPosition);
        this.setActionAnimation(actionAnimation);
        this.setFinishAnimation(finishAnimation);
        this.setSpeed(speed);
        this.setDelay(delay);
        this.setDirection(direction);
        this.setIgnoreStartPosition(false);
        this.setAmount(1);
        this.setShouldRun(false);
        this.setSound(sound);
        this.setShouldRepeatSound(shouldRepeatSound);
        this.setTickDelay(0);
    }

    public CrossAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
                                Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction, Sound sound, boolean shouldRepeatSound, boolean ignoreFail) {
        this.setStartPosition(startPosition);
        this.setPrepareAnimation(prepareAnimation);
        this.setMoveToPosition(moveToPosition);
        this.setActionAnimation(actionAnimation);
        this.setFinishAnimation(finishAnimation);
        this.setSpeed(speed);
        this.setDelay(delay);
        this.setDirection(direction);
        this.setIgnoreStartPosition(false);
        this.setAmount(1);
        this.setShouldRun(false);
        this.setSound(sound);
        this.setShouldRepeatSound(shouldRepeatSound);
        this.setTickDelay(0);
        this.setIgnoreFail(ignoreFail);
    }

    public CrossAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
                                Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction, Sound sound, boolean shouldRepeatSound, String message, String finishMessage) {
        this.setStartPosition(startPosition);
        this.setPrepareAnimation(prepareAnimation);
        this.setMoveToPosition(moveToPosition);
        this.setActionAnimation(actionAnimation);
        this.setFinishAnimation(finishAnimation);
        this.setSpeed(speed);
        this.setDelay(delay);
        this.setDirection(direction);
        this.setIgnoreStartPosition(false);
        this.setAmount(1);
        this.setShouldRun(false);
        this.setSound(sound);
        this.setShouldRepeatSound(shouldRepeatSound);
        this.setTickDelay(0);
        this.setMessage(message);
        this.setFinishMessage(finishMessage);
    }

    public CrossAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
                                Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction, boolean ignoreStartPosition, boolean shouldRun) {
        this.setStartPosition(startPosition);
        this.setPrepareAnimation(prepareAnimation);
        this.setMoveToPosition(moveToPosition);
        this.setActionAnimation(actionAnimation);
        this.setFinishAnimation(finishAnimation);
        this.setSpeed(speed);
        this.setDelay(delay);
        this.setDirection(direction);
        this.setIgnoreStartPosition(ignoreStartPosition);
        this.setAmount(1);
        this.setShouldRun(shouldRun);
        this.setTickDelay(0);
    }

    public CrossAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
                                Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction, boolean ignoreStartPosition, boolean shouldRun, Sound sound, boolean shouldRepeatSound) {
        this.setStartPosition(startPosition);
        this.setPrepareAnimation(prepareAnimation);
        this.setMoveToPosition(moveToPosition);
        this.setActionAnimation(actionAnimation);
        this.setFinishAnimation(finishAnimation);
        this.setSpeed(speed);
        this.setDelay(delay);
        this.setDirection(direction);
        this.setIgnoreStartPosition(ignoreStartPosition);
        this.setAmount(1);
        this.setShouldRun(shouldRun);
        this.setSound(sound);
        this.setShouldRepeatSound(shouldRepeatSound);
        this.setTickDelay(0);
    }

    public CrossAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
                                Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction, boolean ignoreStartPosition, boolean shouldRun, Sound sound, boolean shouldRepeatSound, String message, String finishMessage) {
        this.setStartPosition(startPosition);
        this.setPrepareAnimation(prepareAnimation);
        this.setMoveToPosition(moveToPosition);
        this.setActionAnimation(actionAnimation);
        this.setFinishAnimation(finishAnimation);
        this.setSpeed(speed);
        this.setDelay(delay);
        this.setDirection(direction);
        this.setIgnoreStartPosition(ignoreStartPosition);
        this.setAmount(1);
        this.setShouldRun(shouldRun);
        this.setSound(sound);
        this.setShouldRepeatSound(shouldRepeatSound);
        this.setTickDelay(0);
        this.setMessage(message);
        this.setFinishMessage(finishMessage);
    }

    public CrossAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
                                Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction,
                                boolean ignoreStartPosition) {
        this.setStartPosition(startPosition);
        this.setPrepareAnimation(prepareAnimation);
        this.setMoveToPosition(moveToPosition);
        this.setActionAnimation(actionAnimation);
        this.setFinishAnimation(finishAnimation);
        this.setSpeed(speed);
        this.setDelay(delay);
        this.setDirection(direction);
        this.setIgnoreStartPosition(ignoreStartPosition);
        this.setAmount(1);
        this.setShouldRun(false);
        this.setTickDelay(0);
    }

    public CrossAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
                                Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction, int amount) {
        this.setStartPosition(startPosition);
        this.setPrepareAnimation(prepareAnimation);
        this.setMoveToPosition(moveToPosition);
        this.setActionAnimation(actionAnimation);
        this.setFinishAnimation(finishAnimation);
        this.setSpeed(speed);
        this.setDelay(delay);
        this.setDirection(direction);
        this.setIgnoreStartPosition(false);
        this.setAmount(amount);
        this.setTickDelay(0);
    }

    @Override
    public void execute(Player player) {
        if (getTickDelay() == 0) {
            doObstacle(player);
        } else {
            TaskManager.submit(getTickDelay(), () -> {
                doObstacle(player);
            });
        }
    }

    private void doObstacle(Player player) {
        if (EntityExtKt.getBoolean(player, Attribute.DID_FAIL_AGILITY_OBSTACLE, false) && !getIgnoreFail()) {
            return;
        }

        player.getMotion().clearSteps();

        PlayerExtKt.block(player, false, false);
        PlayerExtKt.resetInteractions(player, true, false);

        int moveX = getMoveToPosition().getX();
        int moveY = getMoveToPosition().getY();

        Position movingTo = new Position(player.getX() + moveX, player.getY() + moveY, getStartPosition().getZ() + getMoveToPosition().getZ());
        player.setShouldNoClip(true);

        if (!player.shouldSetRunningBack) {
            player.shouldSetRunningBack = true;
            player.wasRunningAgility = player.isRunning();
        }
        player.setRunning(shouldRun);

		/*
		Assume if height changes, is a teeth grip, so move player down level at start and move more towards actually start..
		 */
        if (getMoveToPosition().getZ() != 0) {
            int addX = 0;
            int addY = 0;
            if (getMoveToPosition().getX() > 0)
                addX = 1;
            if (getMoveToPosition().getX() < 0)
                addX = -1;
            if (getMoveToPosition().getY() > 0)
                addY = 1;
            if (getMoveToPosition().getY() < 0)
                addY = -1;

            Position moveTo = new Position(getStartPosition().getX() + addX, getStartPosition().getY() + addY, getStartPosition().getZ() + getMoveToPosition().getZ());
            player.moveTo(moveTo);
        }

        BasicAnimationSet agilityAnimationSet = new BasicAnimationSet(808, 823, getActionAnimation().getId(), 820, 821, 822, getActionAnimation().getId());
        player.getAppearance().setBas(agilityAnimationSet);
        player.updateAppearance();
        if (getPrepareAnimation() != null)
            player.forceAnimation(getPrepareAnimation());

        if (getSound() != null && !getShouldRepeatSound())
            player.playSound(getSound());

        if (getMessage() != "")
            player.sendMessage(getMessage());

        Task obstacle = new Task(1, true) {
            int amount = 0;

            @Override
            protected void execute() {
                if (EntityExtKt.getBoolean(player, Attribute.DID_FAIL_AGILITY_OBSTACLE, false) && !getIgnoreFail()) {
                    stop();
                    return;
                }
                /*
                 * Check if finished
                 */
                if ((player.getPosition().getX() == movingTo.getX() && player.getPosition().getY() == movingTo.getY()) ||
                        (player.isRunning() && player.isWithinDistance(movingTo, 1))) {
                    //player at end
                    player.getMotion().clearSteps();
                    //PlayerExtKt.unblock(player, false, true);
                    //PlayerExtKt.resetInteractions(player, true, false);
                    player.getAgility().setObstacle(null);
                    player.getClickDelay().reset();
                    player.setShouldNoClip(false);
                    player.resetBas();
                    player.updateAppearance();

                    if (getFinishMessage() != "")
                        player.sendMessage(getFinishMessage());

                    if (getFinishAnimation() != null)
                        player.forceAnimation(getFinishAnimation());
                    stop();
                } else if (getSound() != null && getShouldRepeatSound())
                    player.playSound(getSound());
                if (amount == 0 || (amount == 1 && !player.getMotion().isMoving()) && (!EntityExtKt.getBoolean(player, Attribute.DID_FAIL_AGILITY_OBSTACLE, false) || getIgnoreFail())) {
                    PathFinder.INSTANCE.find(player, new Position(player.getX() + moveX, player.getY() + moveY), false);
                    player.setPositionToFace(new Position(player.getX() + moveX, player.getY() + moveY), true);
                }


                amount++;
            }
        };
        player.getAgility().setObstacle(obstacle);
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Position startPosition) {
        this.startPosition = startPosition;
    }

    public Animation getPrepareAnimation() {
        return prepareAnimation;
    }

    public void setPrepareAnimation(Animation prepareAnimation) {
        this.prepareAnimation = prepareAnimation;
    }

    public Position getMoveToPosition() {
        return moveToPosition;
    }

    public void setMoveToPosition(Position moveToPosition) {
        this.moveToPosition = moveToPosition;
    }

    public Animation getActionAnimation() {
        return actionAnimation;
    }

    public void setActionAnimation(Animation actionAnimation) {
        this.actionAnimation = actionAnimation;
    }

    public Animation getFinishAnimation() {
        return finishAnimation;
    }

    public void setFinishAnimation(Animation finishAnimation) {
        this.finishAnimation = finishAnimation;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getActionDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public boolean isIgnoreStartPosition() {
        return ignoreStartPosition;
    }

    public void setIgnoreStartPosition(boolean ignoreStartPosition) {
        this.ignoreStartPosition = ignoreStartPosition;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean getShouldRun() {
        return shouldRun;
    }

    public void setShouldRun(boolean shouldRun) {
        this.shouldRun = shouldRun;
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public boolean getShouldRepeatSound() {
        return shouldRepeatSound;
    }

    public void setShouldRepeatSound(boolean shouldRepeatSound) {
        this.shouldRepeatSound = shouldRepeatSound;
    }

    public int getTickDelay() {
        return tickDelay;
    }

    public void setTickDelay(int tickDelay) {
        this.tickDelay = tickDelay;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getFinishMessage() { return finishMessage; }

    public void setFinishMessage(String finishMessage) { this.finishMessage = finishMessage; }

    public boolean getIgnoreFail() {
        return ignoreFail;
    }

    public void setIgnoreFail(boolean ignoreFail) { this.ignoreFail = ignoreFail; }
}
