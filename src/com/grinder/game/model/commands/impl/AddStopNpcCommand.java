package com.grinder.game.model.commands.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.commands.Command;

public class AddStopNpcCommand implements Command {

	@Override
	public String getSyntax() {
		return "[npcId] [face]";
	}

	@Override
	public String getDescription() {
		return "Adds a non moving NPC to npc_spawns file.";
	}

	@Override
	public void execute(Player player, String command, String[] parts) {

		int id = Integer.parseInt(parts[1]);
		int x = player.getPosition().getX();
		int y = player.getPosition().getY();
		int z = player.getPosition().getZ();
		
		//int face = Integer.parseInt(parts[2]);
		String face = command.substring(parts[2].length() + 1);
		//String face = "NORTH";
		
		if(parts.length > 2) {
			face = parts[2].toUpperCase();
		}

		String line = System.getProperty("line.separator");

		String data = "" + line + "\t{" + line + "";

		data += "\t\t\"id\": " + id + "," + line + "";
		data += "\t\t\"position\": {" + line + "";
		data += "\t\t\t\"x\": " + x + "," + line + "";
		data += "\t\t\t\"y\": " + y + "," + line + "";
		data += "\t\t\t\"z\": " + z + "" + line + "";
		data += "\t\t}," + line + "";
		data += "\t\t\"facing\": \"" + face + "\"," + line + "";
		data += "\t\t\"radius\": 0" + line + "";
		data += "\t}," + line + "";

		
		
		try {
			Files.write(Paths.get("./data/npc_spawns.cfg"), data.getBytes(), StandardOpenOption.APPEND);
			player.getPacketSender().sendMessage("Added non moving npc " + id + " to your spawns config!");
		} catch (IOException e) {
		}
	}

	@Override
	public boolean canUse(Player player) {
		PlayerRights rights = player.getRights();
		return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
	}

}
