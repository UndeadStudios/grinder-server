package com.grinder.game.content.skill.skillable.impl.agility.obstacle.impl;

import com.grinder.game.content.skill.skillable.impl.agility.obstacle.AgilityObstacle;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;

public class FailAgilityObstacle implements AgilityObstacle {

    private Position startPosition;

    private Animation stoppedAnimation;

    private Animation failedAnimation;

    private Animation finishAnimation;

    private int failChance;

    private int delay;

    private int failDamage;

    private Position failPosition;

    private Sound sound;

    private Position secondPosition;

    private Position faceDirection;

    private String failMessage = "";

    private Sound failSound;

    private MultipleAgilityObstacles multiFail;

    private Graphic failGFX;

    public FailAgilityObstacle(Position startPosition, Animation stoppedAnimation,
                               Animation failedAnimation, Animation finishAnimation, int failChance, int delay, int failDamage, Position failPosition) {
        this.setStartPosition(startPosition);
        this.setStoppedAnimation(stoppedAnimation);
        this.setFailedAnimation(failedAnimation);
        this.setFinishAnimation(finishAnimation);
        this.setFailChance(failChance);
        this.setDelay(delay);
        this.setFailDamage(failDamage);
        this.setFailPosition(failPosition);
    }

    public FailAgilityObstacle(Position startPosition, Animation stoppedAnimation,
                               Animation failedAnimation, Animation finishAnimation, int failChance, int delay, int failDamage, Position failPosition, String failMessage, Sound failSound) {
        this.setStartPosition(startPosition);
        this.setStoppedAnimation(stoppedAnimation);
        this.setFailedAnimation(failedAnimation);
        this.setFinishAnimation(finishAnimation);
        this.setFailChance(failChance);
        this.setDelay(delay);
        this.setFailDamage(failDamage);
        this.setFailPosition(failPosition);
        this.setFailMessage(failMessage);
        this.setFailSound(failSound);
    }

    public FailAgilityObstacle(Position startPosition, Animation stoppedAnimation,
                               Animation failedAnimation, Animation finishAnimation, int failChance, int delay, int failDamage, Position failPosition, Position secondPosition) {
        this.setStartPosition(startPosition);
        this.setStoppedAnimation(stoppedAnimation);
        this.setFailedAnimation(failedAnimation);
        this.setFinishAnimation(finishAnimation);
        this.setFailChance(failChance);
        this.setDelay(delay);
        this.setFailDamage(failDamage);
        this.setFailPosition(failPosition);
        this.setSecondPosition(secondPosition);
    }

    public FailAgilityObstacle(Position startPosition, Animation stoppedAnimation,
                               Animation failedAnimation, Animation finishAnimation, int failChance, int delay, int failDamage, Position failPosition, Position secondPosition, String failMessage) {
        this.setStartPosition(startPosition);
        this.setStoppedAnimation(stoppedAnimation);
        this.setFailedAnimation(failedAnimation);
        this.setFinishAnimation(finishAnimation);
        this.setFailChance(failChance);
        this.setDelay(delay);
        this.setFailDamage(failDamage);
        this.setFailPosition(failPosition);
        this.setSecondPosition(secondPosition);
        this.setFailMessage(failMessage);
    }

    public FailAgilityObstacle(Position startPosition, Animation stoppedAnimation,
                               Animation failedAnimation, Animation finishAnimation, int failChance, int delay, int failDamage, Position failPosition, Position secondPosition, Position faceDirection) {
        this.setStartPosition(startPosition);
        this.setStoppedAnimation(stoppedAnimation);
        this.setFailedAnimation(failedAnimation);
        this.setFinishAnimation(finishAnimation);
        this.setFailChance(failChance);
        this.setDelay(delay);
        this.setFailDamage(failDamage);
        this.setFailPosition(failPosition);
        this.setSecondPosition(secondPosition);
        this.setFaceDirection(faceDirection);
    }

    public FailAgilityObstacle(Position startPosition, Animation stoppedAnimation,
                               Animation failedAnimation, Animation finishAnimation, int failChance, int delay, int failDamage, Position failPosition, Position secondPosition, Position faceDirection, Graphic failGFX) {
        this.setStartPosition(startPosition);
        this.setStoppedAnimation(stoppedAnimation);
        this.setFailedAnimation(failedAnimation);
        this.setFinishAnimation(finishAnimation);
        this.setFailChance(failChance);
        this.setDelay(delay);
        this.setFailDamage(failDamage);
        this.setFailPosition(failPosition);
        this.setSecondPosition(secondPosition);
        this.setFaceDirection(faceDirection);
        this.setFailGFX(failGFX);
    }

    public FailAgilityObstacle(Position startPosition, Animation stoppedAnimation,
                               Animation failedAnimation, Animation finishAnimation, int failChance, int delay, int failDamage, Position failPosition, Position secondPosition, Position faceDirection, String failMessage) {
        this.setStartPosition(startPosition);
        this.setStoppedAnimation(stoppedAnimation);
        this.setFailedAnimation(failedAnimation);
        this.setFinishAnimation(finishAnimation);
        this.setFailChance(failChance);
        this.setDelay(delay);
        this.setFailDamage(failDamage);
        this.setFailPosition(failPosition);
        this.setSecondPosition(secondPosition);
        this.setFaceDirection(faceDirection);
        this.setFailMessage(failMessage);
    }

    public FailAgilityObstacle(Position startPosition, Animation stoppedAnimation, int failChance, int delay, int failDamage, MultipleAgilityObstacles multiFail, Graphic GFX) {
        this.setStartPosition(startPosition);
        this.setStoppedAnimation(stoppedAnimation);
        this.setFailChance(failChance);
        this.setDelay(delay);
        this.setFailDamage(failDamage);
        this.setMultiFail(multiFail);
        this.setFailGFX(GFX);
    }

    @Override
    public void execute(Player player) {
        boolean failed = player.getSkillManager().getCurrentLevel(Skill.AGILITY) <= Misc.random(getFailChance());

        if (getFaceDirection() != null)
            player.setPositionToFace(new Position(player.getPosition().getX() + getFaceDirection().getX(), player.getPosition().getY() + getFaceDirection().getY()), true);

        TaskManager.submit(1, () -> {
            if (getFaceDirection() != null)
                player.setPositionToFace(new Position(player.getPosition().getX() + getFaceDirection().getX(), player.getPosition().getY() + getFaceDirection().getY()), true);
        });

        if (getStoppedAnimation() != null)
            player.forceAnimation(getStoppedAnimation());

        if (failed) {
            EntityExtKt.setBoolean(player, Attribute.DID_FAIL_AGILITY_OBSTACLE, true, false);
            PlayerExtKt.block(player, false, true);
            PlayerExtKt.resetInteractions(player, true, false);
            player.getMotion().clearSteps();

            Task obstacle = new Task(1, true) {

                int delay = 0;

                @Override
                protected void execute() {
                    if (delay == 0) {
                        if (getMultiFail() != null) {
                            getMultiFail().execute(player);

                            int time = 0;
                            for (int i=0; i<getMultiFail().getObstacles().length; i++) {
                                time += getMultiFail().getDelay()[i];
                            }
                            TaskManager.submit(3, () -> {
                                if (getFailGFX() != null)
                                    player.performGraphic(getFailGFX());
                            });
                            TaskManager.submit(time, () -> {
                                doFail(player);
                            });
                            stop();
                        } else {
                            player.forceAnimation(getFailedAnimation());
                            if (getFailSound() != null)
                                player.getPacketSender().sendSound(getFailSound());
                        }
                    }

                    if (delay == 2) {
                        player.moveTo(getFailPosition());
                        if (getFinishAnimation() != null)
                            player.forceAnimation(getFinishAnimation());
                    }
                    if (delay == 2) {
                        if (getSecondPosition() == null) {
                            doFail(player);
                            stop();
                        }
                    }
                    if (delay == 3) {
                        player.moveTo(getSecondPosition());
                        doFail(player);
                        stop();
                    }

                    delay++;
                }
            };
            player.getAgility().setObstacle(obstacle);
        }
    }

    private void doFail(Player player) {
        player.getCombat().queue(new Damage(getFailDamage(), DamageMask.REGULAR_HIT));
        PlayerExtKt.unblock(player, false, true);
        PlayerExtKt.resetInteractions(player, true, false);
        player.getAgility().setObstacle(null);
        player.getClickDelay().reset();

        player.crossAgilityReady = false;
        EntityExtKt.setBoolean(player, Attribute.STALL_HITS, false, false);
        player.performAnimation(Animation.DEFAULT_RESET_ANIMATION);
        player.resetBas();
        player.updateAppearance();
        player.setShouldNoClip(false);
        if (getFailMessage() != "")
            player.sendMessage(getFailMessage());

        if (player.shouldSetRunningBack)
            player.setRunning(player.wasRunningAgility);

        player.shouldSetRunningBack = false;

        if (player.getAppearance().isMale()) {
            player.playSound(new Sound(Misc.random(2) % 2 == 0 ? 521 : 519));
        } else {
            player.playSound(new Sound(Misc.random(2) % 2 == 0 ? 509 : 510));
        }
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Position startPosition) {
        this.startPosition = startPosition;
    }

    public Animation getStoppedAnimation() {
        return stoppedAnimation;
    }

    public void setStoppedAnimation(Animation stoppedAnimation) {
        this.stoppedAnimation = stoppedAnimation;
    }

    public Animation getFailedAnimation() {
        return failedAnimation;
    }

    public void setFailedAnimation(Animation failedAnimation) {
        this.failedAnimation = failedAnimation;
    }

    public Animation getFinishAnimation() {
        return finishAnimation;
    }

    public void setFinishAnimation(Animation finishAnimation) {
        this.finishAnimation = finishAnimation;
    }

    public int getFailChance() {
        return failChance;
    }

    public void setFailChance(int failChance) {
        this.failChance = failChance;
    }

    public int getActionDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getFailDamage() {
        return failDamage;
    }

    public void setFailDamage(int failDamage) {
        this.failDamage = failDamage;
    }

    public Position getFailPosition() {
        return failPosition;
    }

    public void setFailPosition(Position failPosition) {
        this.failPosition = failPosition;
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public Position getSecondPosition() {
        return secondPosition;
    }

    public void setSecondPosition(Position secondPosition) {
        this.secondPosition = secondPosition;
    }

    public Position getFaceDirection() {
        return faceDirection;
    }

    public void setFaceDirection(Position faceDirection) {
        this.faceDirection = faceDirection;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String failMessage) {
        this.failMessage = failMessage;
    }

    public Sound getFailSound() {
        return failSound;
    }

    public void setFailSound(Sound failSound) {
        this.failSound = failSound;
    }

    public MultipleAgilityObstacles getMultiFail() {
        return multiFail;
    }

    public void setMultiFail(MultipleAgilityObstacles multiFail) {
        this.multiFail = multiFail;
    }

    public Graphic getFailGFX() {
        return failGFX;
    }

    public void setFailGFX(Graphic failGFX) {
        this.failGFX = failGFX;
    }

}