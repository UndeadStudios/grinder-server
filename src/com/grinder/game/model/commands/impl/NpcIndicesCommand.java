package com.grinder.game.model.commands.impl;

import com.grinder.game.World;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.commands.Command;

/**
 * @author Savions.
 */
public class NpcIndicesCommand implements Command {

	@Override public String getSyntax() {
		return "Gets all indices of npcs around me";
	}

	@Override public String getDescription() {
		return "Gets all indices of npcs around me";
	}

	@Override public void execute(Player player, String command, String[] parts) {
		World.getNpcs().forEach(n -> {
			if (n != null && n.getPosition().getDistance(player.getPosition()) < 16) {
				player.getPacketSender().sendMessage("pos=" + n.getPosition() + ", index=" + n.getIndex());
			}
		});
	}

	@Override public boolean canUse(Player player) {
		PlayerRights rights = player.getRights();
		return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
	}
}
