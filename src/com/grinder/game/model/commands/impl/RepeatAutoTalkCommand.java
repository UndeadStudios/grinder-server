package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;

import java.util.Objects;

public class RepeatAutoTalkCommand implements Command {

	@Override
	public String getSyntax() {
		return "[message]";
	}

	@Override
	public String getDescription() {
		return "Repeats your last auto-typed message.";
	}

	@Override
	public void execute(Player player, String command, String[] parts) {
		if (player.isAccountFlagged()) {
			return;
		}
		if (player.getTempMessageToAutoTalk() == null) {
			return;
		}
		if (player.BLOCK_ALL_BUT_TALKING) {
			return;
		}
		if (!player.getAutoChatBreakTimer().finished()) {
			player.sendMessage("@red@You can use this command again after waiting for " + (player.getAutoChatBreakTimer().secondsRemaining() > 1 ? "" + player.getAutoChatBreakTimer().secondsRemaining() + " seconds" : "one more second") +".");
			return;
		}

		if(player.isMuted()){
			player.sendMessage("You're not allowed to use this feature when your account is muted.");
			return;
		}


		/*if (Misc.blockedWord(player.getTempMessageToAutoTalk())) {
			DialogueManager.sendStatement(player, "A word was blocked in your sentence. Please do not repeat it!");
			return;
		}*/

		if (player.getTempMessageToAutoTalk().isEmpty()) {
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

		// Enable autotyping
		player.setHasAutoTalkerMessageActive(true);
		player.setMessageToAutoTalk(player.getTempMessageToAutoTalk());

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
