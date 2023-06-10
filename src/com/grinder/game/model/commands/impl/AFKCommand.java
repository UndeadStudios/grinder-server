package com.grinder.game.model.commands.impl;

import com.grinder.game.content.item.MorphItems;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.areas.instanced.FightCaveArea;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.commands.Command;

public class AFKCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Set your account on the AFK chair mode.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {
		if (player.getHitpoints() <= 0)
			return;
		if (player.getStatus() == PlayerStatus.TRADING) {
			player.getPacketSender().sendMessage("You can't use this command while in a trade!", 1000);
			return;
		}
		if (player.getStatus() == PlayerStatus.BANKING) {
			player.getPacketSender().sendMessage("You can't use this command while banking!", 1000);
			return;
		}
		if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
			player.getPacketSender().sendMessage("You can't use this command while price checking!", 1000);
			return;
		}
		if (player.getMinigame() != null) {
			player.getPacketSender().sendMessage("You can't use this command while in a Minigame!", 1000);
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
    	if (player.getCombat().isInCombat() || player.getCombat().isUnderAttack()) {
			player.getPacketSender().sendMessage("You must wait 10 seconds after being out of combat before going AFK!", 1000);
			return;
		}
        if (AreaManager.inWilderness(player)) {
        	player.sendMessage("<img=794> @red@Err, it is not smart to go AFK while your in the Wilderness...");
        	return;
        }
        if (AreaManager.DUEL_ARENA.contains(player)) {
        	player.sendMessage("<img=794> @red@Err, it is not smart to go AFK in duel arena...");
        	return;
        }
        if (AreaManager.DuelFightArena.contains(player)) {
        	player.sendMessage("<img=794> @red@Err, it is not smart to go AFK in duel arena...");
        	return;
        }
		if (AreaManager.MINIGAME_LOBBY.contains(player)) {
			player.sendMessage("<img=794> @red@Err, it is not smart to go AFK here...");
			return;
		}
        if (AreaManager.FREE_PVP_ARENA.contains(player)) {
        	player.sendMessage("<img=794> It is not a good idea to go AFK in Fun PvP zone.");
        	return;
        }
    	if (player.busy()) {
    		player.getPacketSender().sendMessage("You can't do that when you're busy.", 1000);
    		return;
    	}
    	if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
    		player.getPacketSender().sendMessage("<img=779> You can't use afk command when it's already active.", 1000);
    		return;
    	}

		if (player.getArea() != null && player.getArea() instanceof FightCaveArea) {
			player.getPacketSender().sendMessage("You can't use this command in the Fight Caves!");
			return;
		}


    	if (!MorphItems.INSTANCE.notTransformed(player, "afk", true, false))
			return;

		if (player.getStatus() == PlayerStatus.TRADING) {
			player.getPacketSender().sendMessage("You can't use this command while trading!", 1000);
			return;
		}
		if (player.getStatus() == PlayerStatus.DUELING) {
			player.getPacketSender().sendMessage("You can't use this command while dueling!", 1000);
			return;
		}

		player.say("I'm AFK! Talk to the hand :troll:");
		player.performAnimation(new Animation(4117));
		player.getCombat().reset(false);
		player.getMotion().resetTargetFollowing();
		player.getMotion().clearSteps();
		player.setStatus(PlayerStatus.AWAY_FROM_KEYBOARD);
		player.getPacketSender().sendMessage("<img=779> @red@When you return type ::back, you can't move while afk!", 1000);
		player.updateAppearance();
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
