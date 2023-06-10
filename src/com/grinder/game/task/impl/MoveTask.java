package com.grinder.game.task.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

/**
 * Player moving task
 * 
 * @author 2012 <http://www.rune-server.org/members/dexter+morgan/>
 *
 */
public class MoveTask extends Task {

	private Player player;

	private Position position;

	private Animation start;

	private Animation finish;

	private Position facePosition;

	public MoveTask(Player player, Position position, Animation start, Animation finish, Position facePosition) {
		this.player = player;
		this.setPosition(position);
		this.setStart(start);
		this.setFinish(finish);
		PlayerExtKt.resetInteractions(player, true, false);
		PlayerExtKt.block(player, false, false);
		if (facePosition != null)
			setFacePosition(facePosition);
	}

	public MoveTask(Player player, Position position, Animation start) {
		this.player = player;
		this.setPosition(position);
		this.setStart(start);
		this.setFinish(null);
	}

	@Override
	protected void execute() {
		if (getFacePosition() == null) {
			runMoveTask();
		} else {
			player.setPositionToFace(facePosition);
			TaskManager.submit(1, () -> {
				runMoveTask();
			});
		}
		stop();
	}

	private void runMoveTask() {
		player.performAnimation(getStart());
		TaskManager.submit(1, () -> {
			player.moveTo(getPosition());
			player.performAnimation(getFinish());
			player.getMotion().update(MovementStatus.NONE).clearSteps();
		});
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
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

	public Position getFacePosition() {
		return facePosition;
	}

	public void setFacePosition(Position facePosition) {
		this.facePosition = facePosition;
	}
}
