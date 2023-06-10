package com.grinder.game.task.impl;

import com.grinder.game.entity.grounditem.ItemOnGround;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.task.Task;

/**
 * A {@link Task} implementation which handles the respawn of an
 * {@link ItemOnGround}.
 *
 * @author Professor Oak
 */
public class ItemOnGroundRespawnTask extends Task {

    /**
     * The {@link ItemOnGround} which is going to respawn.
     */
    private final ItemOnGround item;

    public ItemOnGroundRespawnTask(ItemOnGround item, int ticks) {
        super(ticks);
        this.item = item;
    }

    @Override
    public void execute() {
        final ItemOnGround itemOnGround = new ItemOnGround(item.getState(),
                item.findOwner(),
                item.getPosition(),
                item.getItem().clone().setAmount(item.getInitialAmount()),
                item.goesGlobal(),
                item.getRespawnTimer(),
                ItemOnGroundManager.STATE_UPDATE_DELAY);
        ItemOnGroundManager.register(itemOnGround);
        stop();
    }
}
