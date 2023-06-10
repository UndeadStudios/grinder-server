package com.grinder.game.task.impl;

import com.grinder.game.World;
import com.grinder.game.collision.CollisionManager;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ClippedMapObjects;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.task.Task;

/**
 * A {@link Task} implementation which deregisters an original
 * {@link GameObject}, temporarily registers a replacement and then re-registers
 * the original one again.
 * <p>
 * Useful for skills such as Woodcutting and Mining where resources are
 * temporarily unavailable.
 *
 * @author Professor Oak
 * @author Stan van der Bend
 */
public class TimedObjectReplacementTask extends Task {

	/**
	 * The original {@link GameObject}.
	 */
	private final GameObject original;

	/**
	 * The temporary {@link GameObject}.
	 * <p>
	 * This object will be deregistered once the task has finished execution and the
	 * {@code original} object will respawn.
	 */
	private final DynamicGameObject temp;

	/**
	 * The amount of ticks before the {@code original} {@link GameObject} respawns
	 * again.
	 */
	private final int ticks;

	/**
	 * The current tick counter.
	 */
	private int tick = 0;

	/**
	 * Are the objects on the same tile (position)?
	 */
	private boolean sameTile = false;
	
	private boolean isPrivateObject = false;
	
	private Player player;
	

	/**
	 * Constructs this task.
	 *
	 * @param original
	 * @param ticks
	 */
	public TimedObjectReplacementTask(GameObject original, DynamicGameObject temp, int ticks) {
		super(1, true);
		this.original = original;
		this.temp = temp;
		this.ticks = ticks;
		this.sameTile = original.getPosition().equals(temp.getPosition());
		this.temp.setOriginalObject(original);
	}
	
	public TimedObjectReplacementTask(boolean privateObject, Player p, GameObject original, DynamicGameObject temp, int ticks)
	{
		super(1, true);
		this.original = original;
		this.temp = temp;
		this.ticks = ticks;
		this.sameTile = original.getPosition().equals(temp.getPosition());		
		this.isPrivateObject = privateObject;
		this.player = p;
		this.temp.setOriginalObject(original);
		temp.addTo(player);
	}
	

	/**
	 * Executes this task.
	 */
	@Override
	public void execute() {
		if (!isPrivateObject) {
			if (tick == 0) {
				CollisionManager.removeObjectClipping(original); //remove clipping of original item
				ObjectManager.add(temp, true);
			} else if (tick >= ticks) {
				temp.despawn();
				CollisionManager.addObjectClipping(original); //add back clipping of original object
				stop();
			}
		} else { //TODO: fix private objects, they should not be added into world... At the moment i just send packet only to that player
			try { //in case he logegd
				if (tick == 0) {
//					if(ClippedMapObjects.USE_NEW_OBJECT_UPDATES){
//						World.deSpawn(original);
//						World.addObject(temp);
//					} else {
						player.getPacketSender().sendObjectRemoval(original);
						player.getPacketSender().sendObject(temp);
//					}
				} else if (tick >= ticks) {
//					if(ClippedMapObjects.USE_NEW_OBJECT_UPDATES){
//						World.deSpawn(temp);
//						World.addObject(original);
//					} else {
						player.getPacketSender().sendObjectRemoval(temp);
						player.getPacketSender().sendObject(original);
//					}
					stop();
				}
			} catch (Exception e) {
				stop();
				e.printStackTrace();
			}
		}
		tick++;
	}
}
