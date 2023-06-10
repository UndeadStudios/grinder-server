package com.grinder.game.content.skill.skillable.impl.agility.obstacle.impl;

import com.grinder.game.content.skill.skillable.impl.agility.obstacle.AgilityObstacle;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.Task;

/**
 * Jumping over an agitlity obstacle
 *
 * @author 2012 <http://www.rune-server.org/members/dexter+morgan/>
 */
public class JumpAgilityObstacle implements AgilityObstacle {

    private Position startPosition;

    private Position middlePosition;

    private Position endPosition;

    private Animation animation;

    private Sound sound;

    private String message = "";

    public JumpAgilityObstacle(Position startPosition, Position middlePosition, Position endPosition,
                               Animation animation) {
        this.setStartPosition(startPosition);
        this.setMiddlePosition(middlePosition);
        this.setEndPosition(endPosition);
        this.setAnimation(animation);
    }

    public JumpAgilityObstacle(Position startPosition, Position middlePosition, Position endPosition,
                               Animation animation, String message) {
        this.setStartPosition(startPosition);
        this.setMiddlePosition(middlePosition);
        this.setEndPosition(endPosition);
        this.setAnimation(animation);
        this.setMessage(message);
    }

    public JumpAgilityObstacle(Position startPosition, Position middlePosition, Position endPosition,
                               Animation animation, Sound sound) {
        this.setStartPosition(startPosition);
        this.setMiddlePosition(middlePosition);
        this.setEndPosition(endPosition);
        this.setAnimation(animation);
        this.setSound(sound);
    }

    public JumpAgilityObstacle(Position startPosition, Position middlePosition, Position endPosition,
                               Animation animation, Sound sound, String message) {
        this.setStartPosition(startPosition);
        this.setMiddlePosition(middlePosition);
        this.setEndPosition(endPosition);
        this.setAnimation(animation);
        this.setSound(sound);
        this.setMessage(message);
    }

    public JumpAgilityObstacle() {

    }

    @Override
    public void execute(Player player) {
        player.moveTo(getStartPosition());
        player.performAnimation(getAnimation());
        PlayerExtKt.block(player, false, true);
        PlayerExtKt.resetInteractions(player, true, false);

        if (getSound() != null)
            player.playSound(getSound());

        if (getMessage() != "")
            player.sendMessage(getMessage());

        Task task = new Task(1) {
            int delay = 0;

            @Override
            protected void execute() {
                if (delay == 1) {
                    player.moveTo(getMiddlePosition());
                    if (getSound() != null)
                        player.playSound(getSound());
                } else if (delay == 2) {
                    player.performAnimation(getAnimation());
                } else if (delay == 3) {
                    player.moveTo(getEndPosition());
                    if (getSound() != null)
                        player.playSound(getSound());
                    PlayerExtKt.unblock(player, false, true);
                    PlayerExtKt.resetInteractions(player, true, false);
                    player.getAgility().setObstacle(null);
                    stop();
                }
                delay++;
            }
        };
        player.getAgility().setObstacle(task);
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Position startPosition) {
        this.startPosition = startPosition;
    }

    public Position getMiddlePosition() {
        return middlePosition;
    }

    public void setMiddlePosition(Position middlePosition) {
        this.middlePosition = middlePosition;
    }

    public Position getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(Position endPosition) {
        this.endPosition = endPosition;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }
}
