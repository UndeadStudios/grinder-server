package com.grinder.game.model.commands.impl;

import java.util.Optional;

import com.grinder.game.definition.NpcDropDefinition;
import com.grinder.game.entity.agent.npc.NPCDropGenerator;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.item.container.bank.BankUtil;
import com.grinder.game.model.item.container.bank.Banking;

public class DropTestCommand implements Command {

    @Override
    public String getSyntax() {
        return "[npcId] [x]";
    }

    @Override
    public String getDescription() {
        return "Simulate x amount of NPC kills drops.";
    }

	// Use ::Droptest npcid amountOfKills
	

    @Override
	public void execute(Player player, String command, String[] parts) {
    Banking.wipe(player);
    Optional<NpcDropDefinition> def = NpcDropDefinition.get(Integer.parseInt(parts[1]));
    NPCDropGenerator gen = new NPCDropGenerator(player, def.get());
    player.getPacketSender().sendMessage("Checking drop table for " + Integer.parseInt(parts[2]) +" NPC kills for ID: " + Integer.parseInt(parts[1]));
    for (int amount = 0; amount < Integer.parseInt(parts[2]); amount++) {
        for (Item item : gen.getDropList()) {
    	if (!item.getDefinition().isStackable()) {
    		for (int i = 0; i < item.getAmount(); i++) {
    			BankUtil.addToBank(player, new Item(item.getId(), item.getAmount()));
    		}
    	} else {
    		if (item.getId() == 995 && item.getAmount() < 75_000) {
        	item.setAmount((int) (item.getAmount() * 2.8));
    		}

            BankUtil.addToBank(player, new Item(item.getId(), item.getAmount()));
        	//item.switchItem(player.getBank(Bank.getTabForItem(player, item.getId())), item.clone(),
            //        item.getSlot(item.getId()), false, false);
    	}
    }
	}
	}
	
	
    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }
}
