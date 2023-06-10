package com.grinder.game.task.impl;

import com.grinder.game.entity.grounditem.ItemOnGround;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.task.Task;

/**
 * A {@link Task} implementation that handles the
 * processing of all active {@link ItemOnGround}.
 * 
 * @author Professor Oak
 */
public class ItemOnGroundSequenceTask extends Task {

	public ItemOnGroundSequenceTask() {
		super(1);
	}

	@Override
	protected void execute() {
		ItemOnGroundManager.process();
	}
}

