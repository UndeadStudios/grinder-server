package com.grinder.game.entity.agent.combat.attack.weapon.poison;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.task.Task;
import com.grinder.util.NpcID;

/**
 * @author Savions.
 */
public class VenomEffectTask extends Task {

	private final Agent target;
	
	public VenomEffectTask(Agent target) {
		super(30, target, true);
		this.target = target;
		bind(target);
	}

	@Override protected void execute() {
		if(!target.isRegistered() || stopCondition()) {
			stop();
			return;
		}

		sequence();
	}

	private boolean stopCondition() {
		return !target.isVenomed() || !target.getCombat().getVenomImmunityTimer().finished() || target.getVenomDamage() > 10000;
	}

	private void sequence() {
		if(target instanceof Player){

			final Player targetPlayer = ((Player) target);

			final int lifePoints = target.getHitpoints();
			int venomDamage = target.getVenomDamage();

			if(targetPlayer.getDueling().inDuel() && lifePoints <= 0)
				target.setVenomDamage(0);
			if (venomDamage > lifePoints)
				venomDamage = lifePoints;

		}

		if (target.getPoisonDamage() != 0) {
			target.setPoisonDamage(0);
		}

		if (target instanceof NPC) {
			if (target.getAsNpc().fetchDefinition().getId() == NpcID.COMBAT_DUMMY || target.getAsNpc().fetchDefinition().getId() == NpcID.UNDEAD_COMBAT_DUMMY) {
				if (target.getPoisonDamage() >= 20) {
					target.setPoisonDamage(0);
				}
			}
		}

		final int newVenomDamage = target.increaseVenomDamage();

		target.getCombat().queue(new Damage(newVenomDamage, DamageMask.VENOM));
	}

	@Override
	public void stop() {
		target.setVenomDamage(0);
		if (target.isPlayer()) {
			target.getAsPlayer().getPacketSender().sendOrbConfig();
		}
		super.stop();
	}
}
