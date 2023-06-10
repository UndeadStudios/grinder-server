package com.grinder.game.entity.agent.npc.monster.boss.impl.god.zaros;

import com.grinder.game.content.minigame.warriorsguild.drops.Misc;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.entity.agent.combat.attack.Attack;
import com.grinder.game.entity.agent.combat.attack.AttackProvider;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonEffect;
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonType;
import com.grinder.game.entity.agent.combat.event.impl.FreezeEvent;
import com.grinder.game.entity.agent.combat.hit.HitTemplate;
import com.grinder.game.entity.agent.combat.hit.HitTemplateBuilder;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Skill;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.model.projectile.ProjectileTemplate;
import com.grinder.game.model.projectile.ProjectileTemplateBuilder;
import com.grinder.util.timing.TimerKey;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

import static com.grinder.game.entity.agent.combat.attack.AttackType.SPECIAL;

/**
 * @author Savions.
 */
public enum NexAttackType implements AttackProvider {

	MELEE_ATTACK(AttackType.MELEE) {
		@Override public Animation getAttackAnimation(AttackType type) {
			return MELEE_ANIM;
		}

		@Override public void postHit(Player target) {
		}

		@Override public Stream<HitTemplate> fetchHits(AttackType type) {
			return new HitTemplateBuilder(type).setDelay(0).buildAsStream();
		}

		@Override public int getMaxHit() {
			return 30;
		}
	},
	ZAROS_ATTACK(AttackType.MELEE) {
		@Override public Animation getAttackAnimation(AttackType type) {
			return ZAROS_MELEE_ANIM;
		}

		@Override public void postHit(Player target) {
			if (Misc.random(4) == 0) {
				final int amount = 3 + Misc.random(3);
				target.getSkillManager().setCurrentLevel(Skill.PRAYER, target.getSkillManager().getCurrentLevel(Skill.PRAYER) - amount, true);
			}
		}

		@Override public Stream<HitTemplate> fetchHits(AttackType type) {
			return new HitTemplateBuilder(type).setDelay(0).buildAsStream();
		}

		@Override public int getMaxHit() {
			return 45;
		}
	},
	SMOKE_RUSH(AttackType.MAGIC) {

		@Override public Animation getAttackAnimation(AttackType type) {
			return MAGE_ANIM;
		}

		@Override public void postHit(Player target) {
			if (Misc.random(4) == 0) {
				PoisonEffect.applyPoisonTo(target, PoisonType.NEX);
			}
		}

		@Override public Stream<HitTemplate> fetchHits(AttackType type) {
			return new HitTemplateBuilder(type).setDelay(2).buildAsStream();
		}

		@NotNull @Override public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
			return Stream.of(SMOKE_PROJECTILE);
		}

	},
	SHADOW_RUSH(AttackType.RANGED) {

		@Override public Animation getAttackAnimation(AttackType type) {
			return MAGE_ANIM;
		}

		@Override public int getMaxHit() {
			return 40;
		}

		@Override public void postHit(Player target) {
			if (Misc.random(4) == 0) {
				final int amount = 3 + Misc.random(3);
				target.getSkillManager().setCurrentLevel(Skill.PRAYER, target.getSkillManager().getCurrentLevel(Skill.PRAYER) - amount, true);
			}
		}

		@Override public Stream<HitTemplate> fetchHits(AttackType type) {
			return new HitTemplateBuilder(type).setDelay(2).buildAsStream();
		}

		@NotNull @Override public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
			return Stream.of(SHADOW_PROJECTILE);
		}
	},
	BLOOD_RUSH(AttackType.MAGIC) {

		@Override public Animation getAttackAnimation(AttackType type) {
			return MAGE_ANIM;
		}

		@Override public Stream<HitTemplate> fetchHits(AttackType type) {
			return new HitTemplateBuilder(type).setDelay(2).buildAsStream();
		}

		@NotNull @Override public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
			return Stream.of(BLOOD_PROJECTILE);
		}
	},
	ICE_RUSH(AttackType.MAGIC) {

		@Override public Animation getAttackAnimation(AttackType type) {
			return MAGE_ANIM;
		}

		@Override public void postHit(Player target) {
			if (Misc.random(4) == 0) {
				target.getCombat().submit(new FreezeEvent(5, false));
				if (!target.getTimerRepository().has(TimerKey.FREEZE) && !target.getTimerRepository().has(TimerKey.FREEZE_IMMUNITY)) {
					target.performGraphic(new Graphic(2005));
				}
			}
		}

		@Override public Stream<HitTemplate> fetchHits(AttackType type) {
			return new HitTemplateBuilder(type).setDelay(2).buildAsStream();
		}

		@NotNull @Override public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
			return Stream.of(ICE_PROJECTILE);
		}
	},
	ZAROS_RUSH(AttackType.MAGIC) {

		@Override public Animation getAttackAnimation(AttackType type) {
			return MAGE_ANIM;
		}

		@Override public void postHit(Player target) {
			if (Misc.random(4) == 0) {
				final int amount = 3 + Misc.random(3);
				target.getSkillManager().setCurrentLevel(Skill.PRAYER, target.getSkillManager().getCurrentLevel(Skill.PRAYER) - amount, true);
			}
		}

		@Override public Stream<HitTemplate> fetchHits(AttackType type) {
			return new HitTemplateBuilder(type).setDelay(2).buildAsStream();
		}

		@Override public int getMaxHit() {
			return 40;
		}

		@NotNull @Override public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
			return Stream.of(ZAROS_PROJECTILE);
		}
	};

	private static final Animation MAGE_ANIM = new Animation(9188);
	private static final Animation MELEE_ANIM = new Animation(9180);
	private static final Animation ZAROS_MELEE_ANIM = new Animation(9181);

	private static final ProjectileTemplate SMOKE_PROJECTILE = new ProjectileTemplateBuilder(2004).setSourceSize(1).setStartHeight(50).setEndHeight(30).
			setDelay(40).setSpeed(30).setCurve(5).build();
	private static final ProjectileTemplate SHADOW_PROJECTILE = new ProjectileTemplateBuilder(1999).setSourceSize(1).setStartHeight(50).setEndHeight(30).
			setDelay(40).setSpeed(30).setCurve(5).build();
	private static final ProjectileTemplate BLOOD_PROJECTILE = new ProjectileTemplateBuilder(2002).setSourceSize(1).setStartHeight(50).setEndHeight(30).
			setDelay(40).setSpeed(30).setCurve(5).build();
	private static final ProjectileTemplate ICE_PROJECTILE = new ProjectileTemplateBuilder(2006).setSourceSize(1).setStartHeight(50).setEndHeight(30).
			setDelay(40).setSpeed(30).setCurve(5).build();
	private static final ProjectileTemplate ZAROS_PROJECTILE = new ProjectileTemplateBuilder(2007).setSourceSize(1).setStartHeight(50).setEndHeight(30).
			setDelay(40).setSpeed(30).setCurve(5).build();


	@Override public int fetchAttackDuration(AttackType type) {
		return 4;
	}

	private final AttackType type;

	private NexAttackType(final AttackType type) {
		this.type = type;
	}

	public final AttackType type() { return type; }

	public int getMaxHit() {
		return 33;
	}

	public void postHit(final Player target) {

	}
}
