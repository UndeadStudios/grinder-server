package com.grinder.game.content.skill.skillable.impl.agility.obstacle.impl;

import com.grinder.game.content.skill.skillable.impl.agility.Agility;
import com.grinder.game.content.skill.skillable.impl.agility.obstacle.AgilityObstacle;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Position;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.MoveTask;

public class ClimbAgilityObstacle implements AgilityObstacle {

    private Position position;

    private boolean up;

    private Animation start;

    private Animation finish;

    private ClimbAgilityObstacle next;

    private Sound sound;

    private Position faceDirection;

    private String message = "";

    private String finishMessage = "";

    public ClimbAgilityObstacle(Position position, boolean up) {
        this.setPosition(position);
        this.setUp(up);
        this.setStart(isUp() ? Agility.CLIMB_UP : Agility.CLIMB_DOWN);
        this.setFinish(isUp() ? Agility.PULL_UP : Agility.LAND_DOWN);
    }

    public ClimbAgilityObstacle(Position position, boolean up, Animation start, Animation finish) {
        this.setPosition(position);
        this.setUp(up);
        this.setStart(start);
        this.setFinish(finish);
    }

    public ClimbAgilityObstacle(Position position, boolean up, Animation start, Animation finish, String message, String finishMessage) {
        this.setPosition(position);
        this.setUp(up);
        this.setStart(start);
        this.setFinish(finish);
        this.setMessage(message);
        this.setFinishMessage(finishMessage);
    }

    public ClimbAgilityObstacle(Position position, boolean up, Animation start, Animation finish, Position faceDirection) {
        this.setPosition(position);
        this.setUp(up);
        this.setStart(start);
        this.setFinish(finish);
        this.setFaceDirection(faceDirection);
    }

    public ClimbAgilityObstacle(Position position, boolean up, Animation start, Animation finish, Position faceDirection, Sound sound) {
        this.setPosition(position);
        this.setUp(up);
        this.setStart(start);
        this.setFinish(finish);
        this.setFaceDirection(faceDirection);
        this.setSound(sound);
    }

    public ClimbAgilityObstacle(Position position, boolean up, Animation start, Animation finish, Position faceDirection, String message, String finishMessage) {
        this.setPosition(position);
        this.setUp(up);
        this.setStart(start);
        this.setFinish(finish);
        this.setFaceDirection(faceDirection);
        this.setMessage(message);
        this.setFinishMessage(finishMessage);
    }

    public ClimbAgilityObstacle(Position position, boolean up, Animation start, Animation finish, ClimbAgilityObstacle next) {
        this.setPosition(position);
        this.setUp(up);
        this.setStart(start);
        this.setFinish(finish);
        this.setNext(next);
        this.setFaceDirection(faceDirection);
    }

    public ClimbAgilityObstacle(Position position, boolean up, Animation start, Animation finish, ClimbAgilityObstacle next, String message, String finishMessage) {
        this.setPosition(position);
        this.setUp(up);
        this.setStart(start);
        this.setFinish(finish);
        this.setNext(next);
        this.setFaceDirection(faceDirection);
        this.setMessage(message);
        this.setFinishMessage(finishMessage);
    }

    public ClimbAgilityObstacle(Position position, boolean up, Animation start, Animation finish, Position faceDirection, ClimbAgilityObstacle next) {
        this.setPosition(position);
        this.setUp(up);
        this.setStart(start);
        this.setFinish(finish);
        this.setNext(next);
    }

    public ClimbAgilityObstacle(Position position, boolean up, Animation start, Animation finish, Position faceDirection, ClimbAgilityObstacle next, String message, String finishMessage) {
        this.setPosition(position);
        this.setUp(up);
        this.setStart(start);
        this.setFinish(finish);
        this.setNext(next);
        this.setMessage(message);
        this.setFinishMessage(finishMessage);
    }

    public ClimbAgilityObstacle(Position position, boolean up, Animation start, Animation finish, Sound sound) {
        this.setPosition(position);
        this.setUp(up);
        this.setStart(start);
        this.setFinish(finish);
        this.setSound(sound);
    }

    public ClimbAgilityObstacle(Position position, boolean up, Animation start, Animation finish, Sound sound, String message, String finishMessage) {
        this.setPosition(position);
        this.setUp(up);
        this.setStart(start);
        this.setFinish(finish);
        this.setSound(sound);
        this.setMessage(message);
        this.setFinishMessage(finishMessage);
    }

    public ClimbAgilityObstacle(Position position, boolean up, Animation start, Animation finish, Sound sound, Position faceDirection) {
        this.setPosition(position);
        this.setUp(up);
        this.setStart(start);
        this.setFinish(finish);
        this.setSound(sound);
        this.setFaceDirection(faceDirection);
    }

    public ClimbAgilityObstacle(Position position, boolean up, Animation start, Animation finish, Sound sound, Position faceDirection, String message, String finishMessage) {
        this.setPosition(position);
        this.setUp(up);
        this.setStart(start);
        this.setFinish(finish);
        this.setSound(sound);
        this.setFaceDirection(faceDirection);
        this.setMessage(message);
        this.setFinishMessage(finishMessage);
    }

    public ClimbAgilityObstacle(Position position, boolean up, Animation start, Animation finish, Sound sound, ClimbAgilityObstacle next) {
        this.setPosition(position);
        this.setUp(up);
        this.setStart(start);
        this.setFinish(finish);
        this.setSound(sound);
        this.setNext(next);
    }

    public ClimbAgilityObstacle(Position position, boolean up, Animation start, Animation finish, Sound sound, ClimbAgilityObstacle next, Position faceDirection) {
        this.setPosition(position);
        this.setUp(up);
        this.setStart(start);
        this.setFinish(finish);
        this.setSound(sound);
        this.setNext(next);
        this.setFaceDirection(faceDirection);
    }

    public ClimbAgilityObstacle(Position position, boolean up, Animation start, Animation finish, Sound sound, ClimbAgilityObstacle next, String message, String finishMessage) {
        this.setPosition(position);
        this.setUp(up);
        this.setStart(start);
        this.setFinish(finish);
        this.setSound(sound);
        this.setNext(next);
        this.setMessage(message);
        this.setFinishMessage(finishMessage);
    }

    @Override
    public void execute(Player player) {
        PlayerExtKt.block(player, false, true);
        PlayerExtKt.resetInteractions(player, true, false);

        if (getMessage() != "")
            player.sendMessage(getMessage());

        if (getSound() != null) {
            TaskManager.submit(1, () -> {
                player.playSound(getSound());
            });
        }
        if (getFinishMessage() != "") {
            TaskManager.submit(1, () -> {
                player.sendMessage(getFinishMessage());
            });
        }

        player.getAgility().setObstacle(null);
        TaskManager.submit(new MoveTask(player, getPosition(), getStart(), getFinish(), getFaceDirection()));
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public Animation getStart() {
        return start;
    }

    public void setStart(Animation start) {
        this.start = start;
    }

    public Animation getFinish() {
        return finish;
    }

    public void setFinish(Animation finish) {
        this.finish = finish;
    }

    public ClimbAgilityObstacle getNext() {
        return next;
    }

    public void setNext(ClimbAgilityObstacle next) {
        this.next = next;
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public Position getFaceDirection() {
        return faceDirection;
    }

    public void setFaceDirection(Position faceDirection) {
        this.faceDirection = faceDirection;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getFinishMessage() { return finishMessage; }

    public void setFinishMessage(String finishMessage) { this.finishMessage = finishMessage; }
}
