package com.grinder.net.packet.impl;

import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.model.punishment.PunishmentType;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.util.DiscordBot;
import com.grinder.util.Logging;
import com.grinder.util.Misc;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @author Awakening
 * @version 1.0
 * @since 2019-05-28
 */
public class ChangePasswordPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {

		final String enteredPassword = packetReader.readString();
		final String newPassword = packetReader.readString();
		final String confirmationPassword = packetReader.readString();

		if (player.isAccountFlagged()) {
			PunishmentManager.submit(player.getUsername(), PunishmentType.IP_BAN, PunishmentType.MAC_BAN);
			return;
		}
		if (player.BLOCK_ALL_BUT_TALKING) {
			return;
		}
		if (enteredPassword.equals("")) {
			player.getPacketSender().sendString(51013,
					"     Enter your current password!");
			return;
		}
		if (!enteredPassword.equals(player.getPassword())) {
			player.getPacketSender().sendString(51013,
					"     Invalid current password!");
			return;
		}

		if (!newPassword.equals(confirmationPassword)) {
			player.getPacketSender().sendString(51013,
					"     New Passwords don't match!");
			return;
		}

		if (newPassword.length() < 4) {
			player.getPacketSender().sendString(51013,
					"     @red@Your Password is too Short!");
			return;
		}

		if (newPassword.length() > 14) {
			player.getPacketSender().sendString(51013,
					"     @red@Your Password is too Long!");
			return;
		}
		if (enteredPassword.equals(player.getPassword())) {
			if (newPassword.equals(player.getPassword())) {
				player.getPacketSender().sendString(51013,
						"@red@Your new password must be different!");
			return;
		}
		for (String illegalCharacters : Misc.INVALID_PASS_CHARACTERS) {
			if (newPassword.contains(illegalCharacters)) {
				player.getPacketSender().sendString(51013,
						"     @red@Your password contains non-valid characters!");
			return;
			}
		}
			player.setPassword(newPassword);
			EntityExtKt.setBoolean(player, Attribute.CHANGED_PASS, true, true);
			player.sendMessage("<img=750>@gre@ You have successfully changed your password!");
			player.getPacketSender().sendString(51013, "     ");
			player.getPacketSender().sendInterfaceRemoval();
			Logging.log("passchanges", "'" + player.getUsername() + "' changed password from " + player.getPassword() +" to " + newPassword + " from IP '" + player.getHostAddress() + " and MAC " + player.getMacAddress());
			if(DiscordBot.ENABLED)
				DiscordBot.INSTANCE.sendServerLogs(player.getUsername() + " password has been changed from IP " + player.getHostAddress() + " and MAC " + player.getMacAddress());
		}

		// send result of validation back to client
		player.getPacketSender().sendChangePasswordResponse(true);
	}
}
