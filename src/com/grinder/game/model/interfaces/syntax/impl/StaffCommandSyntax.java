package com.grinder.game.model.interfaces.syntax.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.commands.CommandManager;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;

/**
 * Handles the staff commands
 * 
 * @author 2012
 *
 */
public class StaffCommandSyntax implements EnterSyntax {

	/**
	 * The command
	 */
	private String command;

	/**
	 * The staff command
	 * 
	 * @param command
	 *            the command
	 */
	public StaffCommandSyntax(String command) {
		this.setCommand(command);
	}

	@Override
	public void handleSyntax(Player player, String input) {
		
		Command c = CommandManager.commands.get(getCommand());
		if (c != null) {

			if (c.canUse(player)) {
				c.execute(player, getCommand()+ " "+input, new String[] {getCommand(), input });
			}
		} else {
			player.getPacketSender().sendMessage("This command does not exist.");
		}
	}

	@Override
	public void handleSyntax(Player player, int input) {

	}

	/**
	 * Sets the command
	 *
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Sets the command
	 * 
	 * @param command
	 *            the command
	 */
	public void setCommand(String command) {
		this.command = command;
	}
}
