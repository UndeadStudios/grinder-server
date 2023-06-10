package com.grinder.game.model.commands.impl;

import com.grinder.game.content.skill.skillable.impl.Thieving;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.item.container.bank.BankUtil;
import com.grinder.util.Misc;

public class MuddyTest2 implements Command {

    @Override
    public String getSyntax() {
        return "[amount]";
    }

    @Override
    public String getDescription() {
        return "Adds x amount of muddy chest opens.";
    }

	// Use ::muddytest2 amountOfChestsToOpen
	// Check bank for the items.
	// Bank gets cleared upon every usage.
	@Override
	public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendMessage("Adding " + Integer.parseInt(parts[1]) +" Muddy chest rewards!");
        for (int amount2 = 0; amount2 < Integer.parseInt(parts[1]); amount2++) {
            int luckyItem = -1;
            int pkResources = Thieving.PVP_RESOURCES_ITEMS[Misc.getRandomInclusive(Thieving.PVP_RESOURCES_ITEMS.length - 1)];
            int resourcesRandom = 25 + Misc.getRandomInclusive(35);
            if (Misc.getRandomInclusive(2) == 1) {
                luckyItem = Thieving.MUDDY_CHEST_ITEMS[Misc.getRandomInclusive(Thieving.MUDDY_CHEST_ITEMS.length - 1)];
               // World.sendMessage("<img=754> @whi@"+ player.getUsername()+" has just unlocked the muddy chest and received a bonus PK item.");
            }
            int bmRandom = 2500 + Misc.getRandomInclusive(2500); // 3rd item is always cash bonus reward
            BankUtil.addToBank(player, new Item(13307, bmRandom));
            BankUtil.addToBank(player, new Item(pkResources, resourcesRandom));
            BankUtil.addToBank(player, new Item(luckyItem, 1));
        }
	}
	
	
    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }
}
