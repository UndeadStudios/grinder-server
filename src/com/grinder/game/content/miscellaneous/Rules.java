package com.grinder.game.content.miscellaneous;

import com.grinder.game.content.miscellaneous.WelcomeManager.WelcomeStage;
import com.grinder.game.entity.agent.player.Player;

/**
 * Handles the rules
 * 
 * @author 2012
 *
 */
public class Rules {
	/**
	 * The rules interface
	 */
	private static final int RULES_INTERFACE = 23344;

	/**
	 * Whether the rule was confirmed
	 */
	private boolean[] confirmed = new boolean[7];

	/**
	 * Displaying the rules
	 * 
	 * @param player
	 *            the player
	 */
	public static void open(Player player) {
		int frame = 23350;
		for (int i = 0; i < player.getRules().getConfirmed().length; i++) {
			player.getPacketSender().sendStringColour(frame, player.getRules().getConfirmed()[i] ? 0xffd100 : 0x1fd17c);
			frame += 2;
		}
		player.getPacketSender().sendInterface(RULES_INTERFACE);
	}

	/**
	 * Clicking the rules
	 * 
	 * @param player
	 *            the player
	 * @param button
	 *            the button
	 * @return selected
	 */
	public static boolean click(Player player, int button) {
		/*
		 * Clicking titles
		 */
		if (button >= 23350 && button <= 23362) {
			int id = button - 23350;

			id /= 2;

			player.getRules().getConfirmed()[id] = true;
			player.getPacketSender().sendStringColour(button, 0xffd100);
			return true;
		}
		/*
		 * Clicking accept
		 */
		if (button == 23348) {
			/*for (Boolean confirmed : player.getRules().getConfirmed()) {
				if (!confirmed) {
					player.getPacketSender().sendMessage(
							"You need to read through the rules then @red@click@bla@ on the rule title when understood.");
					return true;
				}
			}*/
			if (player.isJailed()) {
				player.getPacketSender().sendInterfaceRemoval();
				return false;
			}
			if (player.isNewPlayer()) {
			WelcomeManager.welcome(player, WelcomeStage.RULES);
			} else {
			player.getPacketSender().sendInterfaceRemoval();
			}
			return true;
		}
		return false;
	}

	/**
	 * Sets the confirmed
	 *
	 * @return the confirmed
	 */
	public boolean[] getConfirmed() {
		return confirmed;
	}

	/**
	 * Sets the confirmed
	 * 
	 * @param confirmed
	 *            the confirmed
	 */
	public void setConfirmed(boolean[] confirmed) {
		this.confirmed = confirmed;
	}
}
