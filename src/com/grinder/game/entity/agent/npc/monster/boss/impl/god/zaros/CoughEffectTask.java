package com.grinder.game.entity.agent.npc.monster.boss.impl.god.zaros;

import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Skill;
import com.grinder.game.model.areas.godwars.NexChamber;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

import java.util.List;

/**
 * @author Savions.
 */
public class CoughEffectTask extends Task {

	private final Player target;
	private int coughTicks = 30;

	public CoughEffectTask(final Player target) {
		super(2, target, true);
		this.target = target;
		EntityExtKt.setBoolean(target, Attribute.NEX_COUGH, true, false);
	}

	@Override protected void execute() {
		if (!target.isRegistered() || !(target.getArea() instanceof NexChamber)) {
			stop();
			EntityExtKt.setBoolean(target, Attribute.NEX_COUGH, false, false);
			return;
		}
		final int amount = 2;
		target.getSkillManager().setCurrentLevel(Skill.PRAYER, target.getSkillManager().getCurrentLevel(Skill.PRAYER) - amount, true);
		target.getSkillManager().setCurrentLevel(Skill.RANGED, target.getSkillManager().getCurrentLevel(Skill.RANGED) - amount, true);
		target.getSkillManager().setCurrentLevel(Skill.DEFENCE, target.getSkillManager().getCurrentLevel(Skill.DEFENCE) - amount, true);
		target.getSkillManager().setCurrentLevel(Skill.ATTACK, target.getSkillManager().getCurrentLevel(Skill.ATTACK) - amount, true);
		target.getSkillManager().setCurrentLevel(Skill.MAGIC, target.getSkillManager().getCurrentLevel(Skill.MAGIC) - amount, true);
		if ((coughTicks % 6) == 0) {
			target.say("*Cough*");
			target.getLocalPlayers().forEach(p -> {
				if (p != null && target != p && p.getPosition().isWithinDistance(target.getPosition(), 1) && p.getArea() == target.getArea()
						&& canApply(p)) {
					TaskManager.submit(new CoughEffectTask(p));
				}
			});
			if (coughTicks == 0) {
				stop();
				EntityExtKt.setBoolean(target, Attribute.NEX_COUGH, false, false);
				return;
			}
		}
		coughTicks--;
	}

	public static boolean canApply(final Player target) {
		return !EntityExtKt.getBoolean(target, Attribute.NEX_COUGH, false);
	}
}
