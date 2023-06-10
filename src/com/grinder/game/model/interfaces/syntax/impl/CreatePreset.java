package com.grinder.game.model.interfaces.syntax.impl;

import com.grinder.game.content.miscellaneous.presets.Presetable;
import com.grinder.game.content.miscellaneous.presets.Presetables;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;
import com.grinder.util.Misc;

public class CreatePreset implements EnterSyntax {

	private int presetIndex;

	public CreatePreset(final int presetIndex) {
		this.presetIndex = presetIndex;
	}

	@Override
	public void handleSyntax(Player player, String input) {

		player.getPacketSender().sendInterfaceRemoval();

		if (!player.getGameMode().isSpawn() && !PlayerUtil.isDeveloper(player)) {
			player.sendMessage("You can only use presets on spawn game mode.");
			return;
		}

		input = Misc.formatText(input);

		if (input.length() > 14) {
			player.sendMessage("The input name is too long for the preset. Please enter a shorter name.");
			player.setCurrentPreset(null);
			Presetables.INSTANCE.open(player);
			return;
		}

		if(!Misc.isValidName(input)) {
			player.getPacketSender().sendMessage("The input name is invalid for the preset. Please enter characters only.");
			player.setCurrentPreset(null);
			Presetables.INSTANCE.open(player);
			return;
		}

		if(player.getPresets()[presetIndex] == null) {

			//Get stats..
			int[] stats = new int[7];
			for(int i = 0; i < stats.length; i++) {
				stats[i] = player.getSkillManager().getMaxLevel(Skill.values()[i]);
			}
			
			Item[] inventory = player.getInventory().copyValidItemsArray();
			Item[] equipment = player.getEquipment().copyValidItemsArray();
			for(Item t : Misc.concat(inventory, equipment)) {
				if(t.getDefinition().isNoted()) {
					player.getPacketSender().sendMessage("You can't create presets which contain noted items.");
					return;
				}
			}
			player.getPresets()[presetIndex] = new Presetable(input, presetIndex, inventory, equipment, 
					stats, player.getSpellbook(), false);
			player.setCurrentPreset(player.getPresets()[presetIndex]);

			Presetables.INSTANCE.open(player);
		}
	}

	@Override
	public void handleSyntax(Player player, int input) {
	}

}
