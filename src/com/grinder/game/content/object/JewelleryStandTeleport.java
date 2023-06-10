package com.grinder.game.content.object;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;

/**
 * Teleporting using the jewellery stand
 * 
 * @author 2012
 *
 */
public class JewelleryStandTeleport {

	/**
	 * Displaying the teleports
	 * 
	 * @param player
	 *            the player
	 */
	public static void send(Player player) {
		DialogueManager.start(player, 2548);
		player.setDialogueOptions(new DialogueOptions() {
			@Override
			public void handleOption(Player player, int option) {
				switch (option) {
				case 1:
					TeleportHandler.teleport(player, new Position(3184, 3946, 0), player.getSpellbook().getTeleportType(),
							true, true);
					break;
				case 2:
					player.getPacketSender().sendInterfaceRemoval();
					break;
				}
			}
		});
	}
}
