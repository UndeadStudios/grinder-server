package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.commands.Command;
import com.grinder.game.content.skill.skillable.impl.magic.Teleporting;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;

public class MemberZoneCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Teleports you to the member's zone.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {
		if (!player.getRights().isHighStaff() && !PlayerUtil.isMember(player)) {
			player.getPacketSender()
					.sendMessage("<img=745>@red@ This teleport is only available to ranked members.", 1000);
			return;
		}
		if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.LA_ISLA_EBANA.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
			TeleportHandler.teleport(player, Teleporting.TeleportLocation.LA_ISLA_EBANA.getPosition(),
					player.getSpellbook().getTeleportType(), false, true);
		}
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
