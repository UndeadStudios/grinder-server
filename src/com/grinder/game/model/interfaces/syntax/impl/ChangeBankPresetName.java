package com.grinder.game.model.interfaces.syntax.impl;

import com.grinder.game.content.miscellaneous.presets.Presetables;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;
import com.grinder.game.model.item.container.bank.presets.PresetsManager;
import com.grinder.util.Misc;

public class ChangeBankPresetName implements EnterSyntax {

	private int presetIndex;

	public ChangeBankPresetName(final int presetIndex) {
		this.presetIndex = presetIndex;
	}

	@Override
	public void handleSyntax(Player player, String input) {
		
		player.getPacketSender().sendInterfaceRemoval();

		input = Misc.formatText(input);

		if (input.length() > 14) {
			player.sendMessage("The input name is too long for the preset. Please enter a shorter name.");
			PresetsManager.ShowInterface(player);
			return;
		}
		
		if(!Misc.isValidName(input)) {
			player.getPacketSender().sendMessage("The input name is invalid for the preset. Please enter characters only.");
			PresetsManager.ShowInterface(player);
			return;
		}
		
		player.presetNames[presetIndex] = input;
		PresetsManager.ShowInterface(player);
	}

	@Override
	public void handleSyntax(Player player, int input) {
	}

}
