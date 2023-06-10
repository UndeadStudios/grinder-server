package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.Position;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.util.Misc;

public class GECommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Teleports you to the market trade area.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {
		if (player.getHitpoints() <= 0)
			return;
		if (player.getStatus() == PlayerStatus.TRADING) {
			player.getPacketSender().sendMessage("You can't use this command while in a trade!");
			return;
		}
        if (player.BLOCK_ALL_BUT_TALKING) {
        	return;
        }
        if (player.isInTutorial()) {
        	return;
        }
		if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
			return;
		}
		if (player.getCombat().isInCombat()) {
			player.getPacketSender().sendMessage("You must wait 10 seconds after being out of combat before using the command!");
			return;
		}
        /*if (AreaManager.inside(player.getPosition(), AreaManager.WILD)) {
			player.getPacketSender().sendMessage("You can't use this command in the Wilderness!");
        	return;
        }*/
    	if (player.busy()) {
    		player.getPacketSender().sendMessage("You can't do that when you're busy.");
    		return;
    	}
    	if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
			player.getPacketSender().sendMessage("<img=779> You can't use ge command when you're AFK.");
			return;
		}
		if (player.getStatus() == PlayerStatus.TRADING) {
			player.getPacketSender().sendMessage("You can't use this command while trading!");
			return;
		}
		if (player.getStatus() == PlayerStatus.DUELING) {
			player.getPacketSender().sendMessage("You can't use this command while dueling!");
			return;
		}
		TeleportHandler.teleport(player, new Position(3091 + Misc.getRandomInclusive(6), 3480 + Misc.getRandomInclusive(5), 0), TeleportType.NORMAL, false, true);
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
