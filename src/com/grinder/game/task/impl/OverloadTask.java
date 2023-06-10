package com.grinder.game.task.impl;

import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Skill;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.timing.TimerKey;

/**
 * A {@link Task} implementation which handles the Overload effect
 *
 * @author Austin
 */
public class OverloadTask extends Task {

	private final Player player;

	public OverloadTask(Player player) {
		super(1);
		this.player = player;
		TaskManager.cancelTasks(this);
		player.getTimerRepository().register(TimerKey.OVERLOAD_POTION, 500);
	}

	public static void resetOverload(Player player) {
		player.sendMessage("The effects of your overload have worn off..");
		player.getSkillManager().setCurrentLevel(Skill.ATTACK, player.getSkillManager().getMaxLevel(0), true);
		player.getSkillManager().setCurrentLevel(Skill.DEFENCE, player.getSkillManager().getMaxLevel(1), true);
		player.getSkillManager().setCurrentLevel(Skill.STRENGTH, player.getSkillManager().getMaxLevel(2), true);
		player.getSkillManager().setCurrentLevel(Skill.MAGIC, player.getSkillManager().getMaxLevel(6), true);
		player.getSkillManager().setCurrentLevel(Skill.RANGED, player.getSkillManager().getMaxLevel(4), true);
		player.getTimerRepository().cancel(TimerKey.OVERLOAD_POTION);
	}
	
	int ticks = 0;

	@Override
	public void execute() {
		if (player == null) {
			stop();
			return;
		}
		if (player.isDying()) {
			player.sendMessage("The effects of your overload have worn off..");
			player.getTimerRepository().cancel(TimerKey.OVERLOAD_POTION);
			stop();
			return;
		}
		if (ticks == 500 || player.getWildernessLevel() > 0 ||  AreaManager.DuelFightArena.contains(player)) {
			if (ticks > 10)
				player.getSkillManager().increaseLevelTemporarily(Skill.HITPOINTS, 50, player.getSkillManager().getMaxLevel(3));
			resetOverload(player);
			stop();
			return;
		}
		if (ticks < 10 && ticks % 2 == 0) {
			player.performAnimation(new Animation(3171));
			player.getMotion().update(MovementStatus.NONE);
			player.getCombat().queue(new Damage(10, DamageMask.REGULAR_HIT));
		}
		if (ticks % 25 == 0) {
			player.getSkillManager().increaseLevelTemporarily(Skill.ATTACK,
					(int) Math.floor(5 + player.getSkillManager().getMaxLevel(0) * 0.16),
					(int) Math.floor(5 + player.getSkillManager().getMaxLevel(0) * 1.16));
			player.getSkillManager().increaseLevelTemporarily(Skill.DEFENCE,
					(int) Math.floor(5 + player.getSkillManager().getMaxLevel(1) * 0.16),
					(int) Math.floor(5 + player.getSkillManager().getMaxLevel(1) * 1.16));
			player.getSkillManager().increaseLevelTemporarily(Skill.STRENGTH,
					(int) Math.floor(5 + player.getSkillManager().getMaxLevel(2) * 0.16),
					(int) Math.floor(5 + player.getSkillManager().getMaxLevel(2) * 1.16));
			player.getSkillManager().increaseLevelTemporarily(Skill.MAGIC,
					(int) Math.floor(5 + player.getSkillManager().getMaxLevel(6) * 0.16),
					(int) Math.floor(5 + player.getSkillManager().getMaxLevel(6) * 1.16));
			player.getSkillManager().increaseLevelTemporarily(Skill.RANGED,
					(int) Math.floor(5 + player.getSkillManager().getMaxLevel(4) * 0.16),
					(int) Math.floor(5 + player.getSkillManager().getMaxLevel(4) * 1.16));
		}
		
		if (ticks == 450) {
			player.sendMessage("The effects of your overload will expire soon.");
		}
		ticks++;
	}
}
