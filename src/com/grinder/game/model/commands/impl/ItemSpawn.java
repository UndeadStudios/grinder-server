package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.commands.Command;
import com.grinder.util.Logging;
import com.grinder.game.entity.agent.player.PlayerRights;

public class ItemSpawn implements Command {

    @Override
    public String getSyntax() {
        return "[id] [x]";
    }

    @Override
    public String getDescription() {
        return "Spawns x amount of the item id.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        int amount = 1;
        if (parts.length > 2) {
            amount = Integer.parseInt(parts[2]);
        }
        if (Integer.parseInt(parts[1]) == 20527) {
        	return;
        }
        System.out.println("Player " + player.getUsername() +" spawned " + Integer.parseInt(parts[1]) +" with the amount of " + amount + "!");
        player.getInventory().add(new Item(Integer.parseInt(parts[1]), amount));
        /*int itemId = Integer.parseInt(parts[1]);
        for (int i = 0; i < 27; i++) {
        	player.getInventory().add(new Item(itemId, amount));
        	itemId++;
        }*/
        if (!player.getUsername().equals("Help") && !player.getUsername().equals("Mod Grinder")) {
        	Logging.log("ItemSpawns", "Player " + player.getUsername() +" spawned " + Integer.parseInt(parts[1]) +" with the amount of " + amount + "");
        }
        player.getInventory().refreshItems();
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER/* || player.getUsername().equals("Mod Hellmage")*/);
    }

}
