package com.grinder.game.model.commands.impl;

import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.model.punishment.PunishmentType;
import com.grinder.util.ItemID;

public class Empty implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Empties your inventory items.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {
    	if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
    		player.getPacketSender().sendMessage("You can't use empty command when you're AFK!");
    		return;
    	}

        if (player.isAccountFlagged()) {
			PunishmentManager.submit(player.getUsername(), PunishmentType.LOCK);
    		return;
        }
		if (player.getGameMode().isOneLife() && player.fallenOneLifeGameMode()) {
			player.sendMessage("Your account has fallen as a One life game mode and can no longer do any actions.");
			return;
		}

        if (player.BLOCK_ALL_BUT_TALKING) {
        	return;
        }
		/*if (player.getGameMode().isIronman()) {
			player.getPacketSender().sendMessage("You can't use this command as an Iron Man.");
			return;
		}
		if (player.getGameMode().isHardcore()) {
			player.getPacketSender().sendMessage("You can't use this command as an Hardcore Iron Man.");
			return;
		}*/
    	if (player.busy()) {
			player.sendMessage("You can't do that right now.");
    		return;
    	}
    	if (player.isJailed()) {
    		player.getPacketSender().sendMessage("You can't empty your items while being jailed.");
    		return;
    	}
    	if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
    		return;
		}
    	if (player.getWildernessLevel() > 0) {
    		player.getPacketSender().sendMessage("You can't empty your items in the Wilderness!");
    		return;
    	}
		if (player.getInventory().contains(ItemID.ROTTEN_POTATO)) {
			player.sendMessage("You cannot empty your inventory when you have rotten potato present.");
			return;
		}
    	if (player.isShowEmptyWarning()) {
			new DialogueBuilder(DialogueType.STATEMENT)
					.setText("The following command will clear all of the items in your inventory.", "Are you absolutely sure you want to do this?")
					.add(DialogueType.OPTION)
					.firstOption("Empty Inventory.", player2 -> {
						SkillUtil.stopSkillable(player);
						ItemContainerUtil.emptyItems(player.getInventory()).refreshItems();
						player.getPacketSender().sendInterfaceRemoval();
						player.sendMessage("@dre@You've emptied your inventory</col>.");
					})
					.addCancel("Don't Empty.")
					.thirdOption("Empty Inventory. Disable Warnings for Current Session.", player2 -> {
						player.setShowEmptyWarning(false);
						SkillUtil.stopSkillable(player);
						ItemContainerUtil.emptyItems(player.getInventory()).refreshItems();
						player.getPacketSender().sendInterfaceRemoval();
						player.sendMessage("@dre@You've emptied your inventory</col>.");
					}).start(player);
			return;
		} else {
			SkillUtil.stopSkillable(player);
			ItemContainerUtil.emptyItems(player.getInventory()).refreshItems();
			player.sendMessage("@dre@You've emptied your inventory</col>.");
		}
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
