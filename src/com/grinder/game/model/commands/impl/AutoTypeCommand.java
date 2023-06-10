package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;

import java.util.Objects;

public class AutoTypeCommand implements Command {

	@Override
	public String getSyntax() {
		return "[message]";
	}

	@Override
	public String getDescription() {
		return "Autotypes your message every 5 seconds.";
	}

	@Override
	public void execute(Player player, String command, String[] parts) {
		if (player.isAccountFlagged()) {
			return;
		}
		if (player.BLOCK_ALL_BUT_TALKING) {
			return;
		}

		if (command.length() <= 8) {
			player.sendMessage("Wrong usage of the command!");
			return;
		}
		if(player.isMuted()){
			player.sendMessage("You're not allowed to use this feature when your account is muted.");
			return;
		}

		final String autoTypeMessage = command.substring(9);
		/*if (Misc.blockedWord(autoTypeMessage)) {
			DialogueManager.sendStatement(player, "A word was blocked in your sentence. Please do not repeat it!");
			return;
		}*/

		if (autoTypeMessage.isEmpty()) {
			return;
		}

		if (player.busy()) {
			player.sendMessage("You cannot do that when busy.");
			return;
		}

		if (player.hasAutoTalkerMessageActive()) {
			DialogueManager.sendStatement(player, "You already have an active running message by the auto-talker.");
			return;
		}

		if (!player.getAutoChatBreakTimer().finished()) {
			player.sendMessage("@red@You can use this command again after waiting for " + (player.getAutoChatBreakTimer().secondsRemaining() > 1 ? "" + player.getAutoChatBreakTimer().secondsRemaining() + " seconds" : "one more second") +".");
			return;
		}

		// Enable autotyping
		player.setHasAutoTalkerMessageActive(true);
		player.setMessageToAutoTalk(autoTypeMessage);
		player.setTempMessageToAutoTalk(autoTypeMessage);
		player.getAutoChatBreakTimer().start(10);

		// Send message and chat
		player.say(Misc.capitalize(player.getMessageToAutoTalk()));
		player.sendMessage("@red@Your message has been set to enabled by the auto-talker.");

		// Autotype loop
		TaskManager.submit(new Task(8) {
			@Override
			public void execute() {

				// Handlers
				if (!player.hasAutoTalkerMessageActive()) {
					stop();
				}
				if (Objects.equals(player.getMessageToAutoTalk(), "")) {
					stop();
				}

				if(player.isMuted()){
					player.sendMessage("You're not allowed to use this feature when your account is muted.");
					stop();
					return;
				}

				// Autotype the message
				if (player.hasAutoTalkerMessageActive() && !Objects.equals(player.getMessageToAutoTalk(), "")) {
					player.say(Misc.capitalize(player.getMessageToAutoTalk()));
				}

			}
		});


	}

	@Override
	public boolean canUse(Player player) {
		return true;
	}
}
