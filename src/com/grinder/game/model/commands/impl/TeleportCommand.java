package com.grinder.game.model.commands.impl;

import com.grinder.game.content.skill.skillable.impl.magic.Teleporting;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.commands.Command;

public class TeleportCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Opens the teleport interface.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {
		Teleporting.handleButton(player, 55555);
    }

    @Override
    public boolean canUse(Player player) {
        return PlayerUtil.isHighStaff(player);
    }
}
