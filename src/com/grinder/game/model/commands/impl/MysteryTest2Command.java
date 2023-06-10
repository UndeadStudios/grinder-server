package com.grinder.game.model.commands.impl;

import java.util.ArrayList;

import com.grinder.game.content.item.mysterybox.MysteryBoxRewardItem;
import com.grinder.game.content.item.mysterybox.MysteryBoxType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.item.container.bank.BankUtil;
import com.grinder.util.Misc;

public class MysteryTest2Command implements Command {

	@Override
	public String getSyntax() {
		return "[amount]";
	}

	@Override
	public String getDescription() {
		return "Adds x amount of mystery box opens.";
	}

	// Use ::mysterytest2 amountOfBoxesToOpen
	// Check bank for the items.
	@Override
	public void execute(Player player, String command, String[] parts) {
		player.getPacketSender().sendMessage("Adding " + Integer.parseInt(parts[1]) +" Mystery box rewards!");
		for (int amount2 = 0; amount2 < Integer.parseInt(parts[1]); amount2++) {
			ArrayList<MysteryBoxRewardItem> rewardable = new ArrayList<MysteryBoxRewardItem>();
			MysteryBoxType rewards = MysteryBoxType.forId(6199);
			for (MysteryBoxRewardItem items : rewards.getItemRewards()) {
				if (items == null) {
					continue;
				}
				if (Misc.getRandomDouble(100) <= items.getChance()) {
					rewardable.add(items);
				}
			}
			Item reward = rewardable.get(Misc.getRandomInclusive(rewardable.size() - 1)).getReward();
			BankUtil.addToBank(player, new Item(reward.getId(), reward.getAmount()));
		}
	}
	
	
    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }
}
