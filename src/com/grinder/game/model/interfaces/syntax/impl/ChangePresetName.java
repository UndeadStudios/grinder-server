package com.grinder.game.model.interfaces.syntax.impl;

import com.grinder.game.content.miscellaneous.presets.Presetables;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;
import com.grinder.util.Misc;

public class ChangePresetName implements EnterSyntax {
	
	private int presetIndex;
	
	public ChangePresetName(final int presetIndex) {
		this.presetIndex = presetIndex;
	}

	@Override
	public void handleSyntax(Player player, String input) {
		
		player.getPacketSender().sendInterfaceRemoval();

		input = Misc.formatText(input);

		if (!player.getGameMode().isSpawn() && !PlayerUtil.isDeveloper(player)) {
			player.sendMessage("You can only use presets on spawn game mode.");
			return;
		}

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
		
		if(player.getPresets()[presetIndex] != null) {
			
			player.getPresets()[presetIndex].setName(input);
			player.getPacketSender().sendMessage("The preset's name has been updated.");
			
			Presetables.INSTANCE.open(player);
		}
	}

	@Override
	public void handleSyntax(Player player, int input) {
	}

}
