package com.grinder.game.model.commands.impl;

import java.util.concurrent.TimeUnit;

import com.grinder.game.GameConstants;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.timing.TimerKey;

public class StuckCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Unstucks your account if stuck.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {
		if (player.getCombat().isInCombat()) {
    		player.getPacketSender().sendMessage("You can't use this command while in combat.");
    		return;
    	}
    	if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
    		player.getPacketSender().sendMessage("You can't use this command while being away from keyboard.", 1000);
    		return;
    	}
		if (player.getStatus() == PlayerStatus.TRADING) {
			player.getPacketSender().sendMessage("You can't use this command while trading!", 1000);
			return;
		}
		if (player.getStatus() == PlayerStatus.DUELING) {
			player.getPacketSender().sendMessage("You can't use this command while dueling!", 1000);
			return;
		}
		if (AreaManager.inWilderness(player)) {
        	player.sendMessage("You can't use this command in the Wilderness.");
        	return;
        }
		if (AreaManager.DUEL_ARENA.contains(player)) {
        	player.sendMessage("You can't use this command in the duel arena.");
        	return;
        }
		if (AreaManager.DuelFightArena.contains(player)) {
        	player.sendMessage("You can't use this command in the duel arena.");
        	return;
        }
		if (AreaManager.FREE_PVP_ARENA.contains(player)) {
        	player.sendMessage("You can't use this command in the Fun PvP zone.");
        	return;
        }
    	if (player.getTimerRepository().has(TimerKey.FREEZE)) {
    		player.getPacketSender().sendMessage("You can't use this command while frozen.");
    		return;
    	}
    	if (player.getTimerRepository().has(TimerKey.COMBAT_COOLDOWN)) {
    		player.getPacketSender().sendMessage("You can't use this command while in combat.");
    		return;
    	}
    	if (player.getCombat().isInCombat()) {
			player.getPacketSender().sendMessage("You must wait 10 seconds after being out of combat to use this command.", 1000);
			return;
		}
    	if (player.getTimerRepository().has(TimerKey.STUN)) {
    		player.getPacketSender().sendMessage("You can't use this command while stunned.");
    		return;
    	}
    	if (!player.getCombat().getTeleBlockTimer().finished()) {
    		player.getPacketSender().sendMessage("You can't use this command while tele blocked.");
    		return;
    	}
    	if (!player.getCombat().isInCombat() && player.getHitpoints() <= 0) {
    		player.setHitpoints(1);
    		player.resetAttributes();
			player.performGraphic(new Graphic(436));
    		player.getPacketSender().sendMessage("Your hitpoints were restored!");
    		return;
    	}
		if (player.getHitpoints() <= 0)
			return;
		if (EntityExtKt.getBoolean(player, Attribute.HAS_TRIGGER_RANDOM_EVENT, false)) {
			player.moveTo(new Position(2640 + Misc.random(2), 10024 + Misc.random(2), 0));
			return;
		}
		if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
			return;
		}
		if (player.BLOCK_ALL_BUT_TALKING) {
			return;
		}
        if (player.isInTutorial()) {
    		player.getPacketSender().sendMessage("You can't use this command while on tutorial.");
        	return;
        }
		if (!EntityExtKt.passedTime(player, Attribute.LAST_STUCK, 30, TimeUnit.MINUTES, false, true)) {
			player.getPacketSender().sendMessage("<img=793> You can only use the stuck command once every 30 minutes!");
			return;
		}
		if (player.isJailed()) {
			player.getPacketSender().sendMessage("You're not stuck at the moment.");
			return;
		}
        DialogueManager.sendStatement(player, "You will be teleported out of here in 15 seconds!");
        player.BLOCK_ALL_BUT_TALKING = true;
        player.getCombat().reset(false);
		//player.performAnimation(new Animation(12575));
			TaskManager.submit(new Task(25) {
				@Override
				public void execute() {
					stop();
			        player.BLOCK_ALL_BUT_TALKING = false;
			       	player.moveTo(GameConstants.DEFAULT_DEATH_POSITION);
			        DialogueManager.sendStatement(player, "You have been moved back to home area!");
			        SkillUtil.stopSkillable(player);
			       	//player.performAnimation(new Animation(-1));
				}
		});
        }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
