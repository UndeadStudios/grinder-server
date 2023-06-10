package com.grinder.game.model.commands.impl;

import com.grinder.game.content.miscellaneous.presets.Presetables;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.commands.Command;

public class PresetsCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Opens the presets system.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
		if (!player.busy()) {
			Presetables.INSTANCE.open(player, Presetables.GLOBAL_PRESETS[0]);
		} else {
			player.getPacketSender().sendMessage("Please finish what you're doing before using that command.");
			return;
		}
    }

    @Override
    public boolean canUse(Player player) {
        return player.getGameMode().isSpawn() || PlayerUtil.isDeveloper(player);
    }

}
