package com.grinder.game.model.commands.impl;

import java.util.Arrays;

import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

public class GetIdCommand implements Command {

	@Override
	public String getSyntax() {
		return "[itemName]";
	}

	@Override
	public String getDescription() {
		return "Shows a list of item's ids.";
	}

	@Override
	public void execute(Player player, String command, String[] parts) {
		String itemName = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

		int i = 0;
		
		for (ItemDefinition definition : ItemDefinition.definitions.values()) {
			if (definition == null || definition.isNoted()) {
				continue;
			}
			
			if (definition.getName().toLowerCase().contains(itemName)) {
				player.sendMessage(definition.getName() + " @dre@- @bla@" + definition.getId());
				
				if (i++ == 100) {
					player.sendMessage("Over @dre@100</col> results have been found!");
					break;
				}
			}
		}
		
		if (i == 0) {
			player.sendMessage("No results for `@dre@" + itemName + "</col>`.");
		}
	}
	
	@Override
	public boolean canUse(Player player) {
		PlayerRights rights = player.getRights();
		return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER/* || player.getUsername().equals("Mod Hellmage")*/);
	}

}
