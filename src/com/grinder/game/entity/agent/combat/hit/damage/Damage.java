package com.grinder.game.entity.agent.combat.hit.damage;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.hit.Hit;
import com.grinder.util.Misc;

/**
 * Represents damage that can be applied to an {@link Agent}.
 *
 * Damage is a direct result of an incoming {@link Hit}.
 *
 * @author Gabriel Hannason
 * @author Stan van der Bend
 */
public class Damage {

	private Agent attacker;
	private Agent target;

	public static Damage create(int damageAmount) {
		return new Damage(damageAmount, DamageMask.REGULAR_HIT);
	}
	public static Damage create(int minDamage, int maxDamage) {
		return new Damage(Misc.random(minDamage, maxDamage), DamageMask.REGULAR_HIT);
	}

	public static Damage createPoisonHit(int maxAmount) { return new Damage(Misc.getRandomInclusive(maxAmount), DamageMask.POISON); }

	public static Damage createBlockedHit(){
		return new Damage(0, DamageMask.BLOCK);
	}

	public int damage;
	private DamageMask damageMask;
	private DamageListener damageListener;

	public Damage(int damage, DamageMask damageMask) {
		this.damage = damage;
		this.damageMask = damageMask;
		updateMask();
	}


    public void set(int damage) {
		this.damage = damage;
		updateMask();
	}

	public void incrementDamage(int damage) {
		this.damage += damage;
		updateMask();
	}

	public void multiplyDamage(double mod) {
		this.damage = Math.toIntExact(Math.round(((double) damage) * mod));
		updateMask();
	}

	private void updateMask() {
		if (this.damage <= 0) {
			this.damage = 0;
			this.damageMask = DamageMask.BLOCK;
		} else {
			if (this.damageMask == DamageMask.BLOCK) {
				this.damageMask = DamageMask.REGULAR_HIT;
			}
		}
	}

	public DamageMask getSegmentMask(Agent agent) {
		boolean involved = (target == null && attacker == null) || agent == target || agent == attacker;
		DamageMask damageMask = this.damageMask;
		if (!involved) {
			if (damageMask == DamageMask.BLOCK) {
				damageMask = DamageMask.BLOCK_OTHER;
			} else if (damageMask == DamageMask.REGULAR_HIT) {
				damageMask = DamageMask.REGULAR_HIT_OTHER;
			} else if (damageMask == DamageMask.SHIELD) {
				damageMask = DamageMask.SHIELD_OTHER;
			}
		}
		return damageMask;
	}

	public void flagAgents(Agent attacker, Agent target) {
		this.attacker = attacker;
		this.target = target;
	}

	public int getValue() {
		return damage;
	}

	public DamageMask getDamageMask() {
		return damageMask;
	}

	public DamageListener getDamageListener() {
		return damageListener;
	}

	public void setDamageListener(DamageListener damageListener) {
		this.damageListener = damageListener;
	}

	public Damage copy() {
		return new Damage(damage, damageMask);
	}
}
