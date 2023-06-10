package com.grinder.game.model.commands.impl;

import com.grinder.game.content.skill.skillable.impl.Thieving;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.item.container.bank.BankUtil;
import com.grinder.game.model.item.container.bank.Banking;
import com.grinder.util.Misc;

public class CrystalTestCommand implements Command {

    @Override
    public String getSyntax() {
        return "[amount]";
    }

    @Override
    public String getDescription() {
        return "Wipes bank and opens x amount of crystal chests.";
    }

	// Use ::crystaltest amountOfChestsToOpen
	// Check bank for the items.
	// Bank gets cleared upon every usage.
	@Override
	public void execute(Player player, String command, String[] parts) {
    Banking.wipe(player);
    player.getPacketSender().sendMessage("Checking " + Integer.parseInt(parts[1]) +" Crystal chest rewards!");
    for (int amount2 = 0; amount2 < Integer.parseInt(parts[1]); amount2++) {
    	int item1 = Thieving.CRYSTAL_CHEST_LOOT[Misc.getRandomInclusive(Thieving.CRYSTAL_CHEST_LOOT.length - 1)];
    	int item2 = -1;
        if (Misc.getRandomInclusive(4) == 1) { // You only have 1/4 chance of getting a second good item from the chest
            item2 = Thieving.CRYSTAL_CHEST_LOOT[Misc.getRandomInclusive(Thieving.CRYSTAL_CHEST_LOOT.length - 1)];
        } else {
            item2 = Thieving.NOOBISH_ITEMS[Misc.getRandomInclusive(Thieving.NOOBISH_ITEMS.length - 1)];
            ;
        }
        int moneyRandom = 150_000 + Misc.getRandomInclusive(500000); // 3rd item is always cash bonus reward
        BankUtil.addToBank(player, new Item(item1, 1));
        BankUtil.addToBank(player, new Item(item2, 1));
        BankUtil.addToBank(player, new Item(995, moneyRandom));
	}
	}
	
	
    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }
}
