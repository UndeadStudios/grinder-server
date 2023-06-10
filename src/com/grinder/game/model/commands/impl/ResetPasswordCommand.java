package com.grinder.game.model.commands.impl;

import java.util.Optional;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.*;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.model.punishment.PunishmentType;
import com.grinder.util.DiscordBot;
import com.grinder.util.Logging;
import com.grinder.util.Misc;

public class ResetPasswordCommand implements Command {

	int resetCount = 0;
	@Override
	public String getSyntax() {
		return "[playerName]";
	}

	@Override
	public String getDescription() {
		return "Reset's the player's account password.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {

    	if (parts.length < 2 || parts[1].isEmpty())
    		return;

		final String targetName = Misc.formatName(command.substring(parts[0].length()).trim());

		if (targetName.toLowerCase().equals("mod grinder") || targetName.toLowerCase().equals("3lou 55") || targetName.toLowerCase().equals("help")) {
			player.getPacketSender().sendMessage("You can't reset a staff's password. Please contact an administrator.");
			return;
		}

		if(!PlayerSaving.playerExists(targetName)){
			new DialogueBuilder(DialogueType.STATEMENT)
					.setText("Could not find a player by the name of:", targetName)
					.start(player);
			return;
		}

		final Optional<Player> online = World.findPlayerByName(targetName);
		int number = Misc.getRandomInclusive(9999);
		String input = Integer.toString(number);

			
			String currentPassword = "N/A";
			//player.getPacketSender().sendEnterInputPrompt("Enter a new password");
			//player.setEnterSyntax(new EnterSyntax() {

			//@Override
			//public void handleSyntax(Player player, String input) {
				if (online.isPresent()) {
					if (PlayerUtil.isStaff(online.get())) {
						player.getPacketSender().sendMessage("You can't reset a staff's password. Please contact an administrator.");
						return;
					}
					currentPassword = online.get().getPassword();
					online.get().setPassword(input);
				} else {
					if (PlayerSaving.playerExists(targetName)) {
						Player offline = new Player();
						offline.setUsername(targetName);
						PlayerLoading.getResult(offline, false, true);
						if (PlayerUtil.isStaff(offline)) {
						player.getPacketSender().sendMessage("You can't reset a staff's password. Please contact an administrator.");
						return;
						}
						currentPassword = offline.getPassword();
						offline.setPassword(input);
						PlayerSaving.save(offline, true);
					} else {
						player.sendMessage("Player save not found for username: \"" + targetName + "\".");
					}
				}
    	//}

				player.sendMessage("<img=750><shad>@gre@ The password for " + Misc.capitalize(targetName) + " has been set to: @red@\"" + input + "\"@gre@.");
	    		Logging.log("passreset", player.getUsername() + " has reset the password for the account: " + Misc.capitalize(targetName) + " from " + currentPassword + " to: " + input +"");
	    		++resetCount;

				if(DiscordBot.ENABLED)
				DiscordBot.INSTANCE.sendModMessage(Misc.capitalize(targetName) + " password has been reset by " + player.getUsername() +".");


	    		if (resetCount >= 5 && !PlayerUtil.isDeveloper(player)) { // Safety check just in case
					PunishmentManager.submitForcedPunishment(player, player.getUsername(), PunishmentType.IP_BAN);
				}
		//		@Override
		//		public void handleSyntax(Player player, int input) {

		//		}
		//	});

    }

	@Override
	public boolean canUse(Player player) {
		PlayerRights rights = player.getRights();
		return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || rights == PlayerRights.ADMINISTRATOR || rights == PlayerRights.CO_OWNER || player.getUsername().equals("3lou 55"));
	}
}
