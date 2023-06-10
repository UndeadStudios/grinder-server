package com.grinder.game.content.skill.skillable.impl.agility.obstacle.impl;

import com.grinder.game.content.skill.skillable.impl.agility.Agility;
import com.grinder.game.content.skill.skillable.impl.agility.obstacle.AgilityObstacle;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.model.Animation;
import com.grinder.game.model.ForceMovement;
import com.grinder.game.model.Position;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.ForceMovementTask;

public class ForceMovementAgilityObstacle implements AgilityObstacle {

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

    private Sound sound;

    private Boolean shouldRepeatSoundEffect;

    private Animation objectAnimation;

    private Position objectPosition;

    private String message = "";

    private String finishMessage = "";

    private boolean ignoreFail = false;

    public ForceMovementAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
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
    }

    public ForceMovementAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
                                        Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction, String message) {
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
        this.setMessage(message);
    }

    public ForceMovementAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
                                        Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction, Animation objectAnimation, Position objectPosition) {
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
        this.setObjectAnimation(objectAnimation);
        this.setObjectPosition(objectPosition);
    }

    public ForceMovementAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
                                        Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction, Animation objectAnimation, Position objectPosition, String message, String finishMessage) {
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
        this.setObjectAnimation(objectAnimation);
        this.setObjectPosition(objectPosition);
        this.setMessage(message);
        this.setFinishMessage(finishMessage);
    }

    public ForceMovementAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
                                        Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction, Sound sound, boolean shouldRepeatSoundEffect, Animation objectAnimation, Position objectPosition) {
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
        this.setObjectAnimation(objectAnimation);
        this.setObjectPosition(objectPosition);
        this.setSound(sound);
        this.setShouldRepeatSoundEffect(shouldRepeatSoundEffect);
    }

    public ForceMovementAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
                                        Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction, Sound sound, boolean shouldRepeatSoundEffect, Animation objectAnimation, Position objectPosition, String message) {
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
        this.setObjectAnimation(objectAnimation);
        this.setObjectPosition(objectPosition);
        this.setSound(sound);
        this.setShouldRepeatSoundEffect(shouldRepeatSoundEffect);
        this.setMessage(message);
    }

    public ForceMovementAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
                                        Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction, Sound sound, boolean shouldRepeatSoundEffect) {
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
        this.setSound(sound);
        this.setShouldRepeatSoundEffect(shouldRepeatSoundEffect);
    }

    public ForceMovementAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
                                        Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction, Sound sound, boolean shouldRepeatSoundEffect, boolean ignoreFail, String message, String finishMessage) {
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
        this.setSound(sound);
        this.setShouldRepeatSoundEffect(shouldRepeatSoundEffect);
        this.setIgnoreFail(ignoreFail);
        this.setMessage(message);
        this.setFinishMessage(finishMessage);
    }

    public ForceMovementAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
                                        Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction, Sound sound, boolean shouldRepeatSoundEffect, String message) {
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
        this.setSound(sound);
        this.setShouldRepeatSoundEffect(shouldRepeatSoundEffect);
        this.setMessage(message);
    }

    public ForceMovementAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
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
    }

    public ForceMovementAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
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
    }

    public ForceMovementAgilityObstacle(Position startPosition, Animation prepareAnimation, Position moveToPosition,
                                        Animation actionAnimation, Animation finishAnimation, int speed, int delay, int direction, int amount, Sound sound, boolean shouldRepeatSoundEffect) {
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
        this.setSound(sound);
        this.setShouldRepeatSoundEffect(shouldRepeatSoundEffect);
    }

    @Override
    public void execute(Player player) {
        PlayerExtKt.block(player, false, true);
        PlayerExtKt.resetInteractions(player, true, false);

        Task obstacle = new Task(1, true) {

            int delay = 0;
            int amount = getAmount();

            @Override
            protected void execute() {
                /*
                 * To far
                 */
                if (!player.getPosition().isWithinDistance(getStartPosition(), 20) || (EntityExtKt.getBoolean(player, Attribute.DID_FAIL_AGILITY_OBSTACLE, false) && !getIgnoreFail())) {
                    stop();
                    return;
                }

                /*
                 * Play sound
                 */
                if (getSound() != null && delay > 1 && delay < getActionDelay() + 3 && getShouldRepeatSoundEffect())
                    player.playSound(getSound());
                /*
                 * Perform
                 */
                if (delay == 1) {
                    if (getStartPosition().getZ() != player.getPosition().getZ())
                        player.moveTo(getStartPosition());

                    if (getPrepareAnimation() != null)
                        player.performAnimation(getPrepareAnimation());

                    if (getMessage() != "")
                        player.sendMessage(getMessage());

                } else if (delay == 3 && (!EntityExtKt.getBoolean(player, Attribute.DID_FAIL_AGILITY_OBSTACLE, false) || getIgnoreFail())) {
                    if (getSound() != null && !getShouldRepeatSoundEffect())
                        player.playSound(getSound());

                    player.getMotion().clearSteps();
                    if (player.getForceMovement() == null && (!EntityExtKt.getBoolean(player, Attribute.DID_FAIL_AGILITY_OBSTACLE, false) || getIgnoreFail())) {
                        Position moveTo = new Position(getMoveToPosition().getX(), getMoveToPosition().getY(), getMoveToPosition().getZ());
                        TaskManager.submit(new ForceMovementTask(player, getActionDelay(),
                                new ForceMovement(player.getPosition().clone(), moveTo, getActionDelay(), getSpeed(),
                                        getDirection(), getActionAnimation().getId())));
                    }
                } else if (delay == getActionDelay() && (!EntityExtKt.getBoolean(player, Attribute.DID_FAIL_AGILITY_OBSTACLE, false) || getIgnoreFail())) {
                    if (getFinishAnimation() != null) {
                        player.performAnimation(getFinishAnimation());

                    }
                    if (amount > 0) {
                        amount--;
                    }
                    if (getObjectPosition() != null && getObjectAnimation() != null)
                        player.getPacketSender().sendObjectAnimation(getObjectPosition().getX(), getObjectPosition().getY(), 10, getObjectPosition().getZ(), getObjectAnimation());

                } else if (delay >= getActionDelay() + 3) {
                    if (amount == 0) {
                        if (getFinishMessage() != "")
                            player.sendMessage(getFinishMessage());

                        //PlayerExtKt.unblock(player, false, true);
                        PlayerExtKt.resetInteractions(player, true, false);
                        player.performAnimation(Animation.DEFAULT_RESET_ANIMATION);
                        player.getAgility().setObstacle(null);
                        player.getClickDelay().reset();
                        stop();
                    } else {
                        delay = 0;
                    }
                }
                /*
                 * Cross ledge
                 */
                if (delay >= 1 && (!EntityExtKt.getBoolean(player, Attribute.DID_FAIL_AGILITY_OBSTACLE, false) || getIgnoreFail())) {
                    if (getActionAnimation().getId() == Agility.CROSS_LEDGE.getId()) {
                        player.performAnimation(getActionAnimation());
                    }
                }
                /*
                 * Started
                 */
                delay++;
            }
        };
        player.getAgility().setObstacle(obstacle);
        player.setPositionToFace(new Position(getStartPosition().getX()+getMoveToPosition().getX(), getStartPosition().getY()+getMoveToPosition().getY(), getMoveToPosition().getZ()));
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

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public Boolean getShouldRepeatSoundEffect() {
        return shouldRepeatSoundEffect;
    }

    public void setShouldRepeatSoundEffect(Boolean shouldRepeatSoundEffect) { this.shouldRepeatSoundEffect = shouldRepeatSoundEffect; }

    public Animation getObjectAnimation() {
        return objectAnimation;
    }

    public void setObjectAnimation(Animation objectAnimation) {
        this.objectAnimation = objectAnimation;
    }

    public Position getObjectPosition() {
        return objectPosition;
    }

    public void setObjectPosition(Position objectPosition) {
        this.objectPosition = objectPosition;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getFinishMessage() { return finishMessage; }

    public void setFinishMessage(String finishMessage) { this.finishMessage = finishMessage; }

    public boolean getIgnoreFail() {
        return ignoreFail;
    }

    public void setIgnoreFail(boolean ignoreFail) {
        this.ignoreFail = ignoreFail;
    }

}