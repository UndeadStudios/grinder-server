package com.grinder.game.content.minigame.impl.inferno;

import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.ForceMovement;
import com.grinder.game.model.Position;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.ForceMovementTask;
import com.grinder.util.ObjectID;

/**
 * A class that handles the Inferno minigame.
 * 
 * @author Blake
 *
 */
public class InfernoManager {
	

	/**
	 * Handles the object clicking.
	 * 
	 * @param player
	 *            The player.
	 * @param object
	 *            The object
	 * @return <code>true</code> if handled
	 */
	public static boolean clickObject(Player player, GameObject object) {
		if (object.getId() == ObjectID.THE_INFERNO_2) {
			if (player.getPosition().getY() != 5116) {
				return false;
			}
			player.BLOCK_ALL_BUT_TALKING = true;
			
			TaskManager.submit(new Task(1) {
				
				int count = 1;

				@Override
				protected void execute() {
					if (count == 2) {
						TaskManager.submit(new ForceMovementTask(player, 8, new ForceMovement(player.getPosition().clone(), new Position(0, 8), 0, 200, 0, 6723)));
					} else if (count == 6) {
						DialogueManager.sendStatement(player, "You jump into the fiery cauldron of The Inferno; your heart is pulsating.");
					} else if (count == 9) {
						player.getPacketSender().sendWalkableInterface(18679);
					} else if (count == 12) {
						DialogueManager.sendStatement(player, "You fall and fall and feel the temperature rising.");
					} else if (count == 15) {
						DialogueManager.sendStatement(player, "Your heart is in your throat...");
						player.setPositionToFace(new Position(2271, 5343 + 2));
					} else if (count == 18) {
						DialogueManager.sendStatement(player, "You hit the ground in the centre of The Inferno.");
						player.moveTo(new Position(2271, 5343));
						player.performAnimation(new Animation(4367));
					}
					
					if (count++ == 19) {
						player.getPacketSender().sendWalkableInterface(-1);
						player.BLOCK_ALL_BUT_TALKING = false;
						stop();
					}
				}
				
			}.bind(player));
			return true;
		}

		return false;
	}

}
