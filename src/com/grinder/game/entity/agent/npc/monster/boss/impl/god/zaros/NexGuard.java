package com.grinder.game.entity.agent.npc.monster.boss.impl.god.zaros;

import com.grinder.game.content.minigame.warriorsguild.drops.Misc;
import com.grinder.game.entity.agent.combat.attack.AttackProvider;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider;
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonEffect;
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonType;
import com.grinder.game.entity.agent.combat.event.impl.FreezeEvent;
import com.grinder.game.entity.agent.combat.event.impl.IncomingHitApplied;
import com.grinder.game.entity.agent.combat.event.impl.IncomingHitQueued;
import com.grinder.game.entity.agent.combat.event.impl.OutgoingHitApplied;
import com.grinder.game.entity.agent.combat.hit.Hit;
import com.grinder.game.entity.agent.combat.hit.HitTemplate;
import com.grinder.game.entity.agent.combat.hit.HitTemplateBuilder;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.npc.monster.Monster;
import com.grinder.game.entity.agent.npc.monster.boss.Boss;
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.projectile.ProjectileTemplate;
import com.grinder.game.model.projectile.ProjectileTemplateBuilder;
import com.grinder.util.NpcID;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * @author Savions.
 */
public class NexGuard extends Boss implements AttackProvider {

	private static final ProjectileTemplate SMOKE_PROJECTILE = new ProjectileTemplateBuilder(2004).setSourceSize(1).setStartHeight(20).setEndHeight(20).
			setDelay(40).setSpeed(30).setCurve(5).build();
	private static final ProjectileTemplate SHADOW_PROJECTILE = new ProjectileTemplateBuilder(1999).setSourceSize(1).setStartHeight(20).setEndHeight(20).
			setDelay(40).setSpeed(30).setCurve(5).build();
	private static final ProjectileTemplate BLOOD_PROJECTILE = new ProjectileTemplateBuilder(2002).setSourceSize(1).setStartHeight(20).setEndHeight(20).
			setDelay(40).setSpeed(30).setCurve(5).build();
	private static final ProjectileTemplate ICE_PROJECTILE = new ProjectileTemplateBuilder(2006).setSourceSize(1).setStartHeight(20).setEndHeight(20).
			setDelay(40).setSpeed(30).setCurve(5).build();

	private static final Graphic DEFENCE_GFX = new Graphic(2011);
	private static final Animation ATTACK_ANIM = new Animation(1979);
	private final Nex nex;
	private boolean vulnerable;

	public NexGuard(int id, @NotNull Position position, Nex nex) {
		super(id, position);
		this.nex = nex;
		getMotion().update(MovementStatus.DISABLED);
		fetchDefinition().setAggressive(false);
		getCombat().subscribe(event -> {
			if (!vulnerable) {
				if (event instanceof IncomingHitQueued) {
					((IncomingHitQueued) event).getHit().setNegateDamages(true);
				} else if (event instanceof IncomingHitApplied) {
					final Hit hit = ((IncomingHitApplied) event).getHit();
					performGraphic(DEFENCE_GFX);
					if (hit.getAttacker() != null && hit.getAttacker().isPlayer()) {
						hit.getAttacker().getAsPlayer().sendMessage("Nex is still too strong to attack this guard.");
					}
				} else if (event instanceof OutgoingHitApplied && ((OutgoingHitApplied) event).getHit().getTarget() != null) {
					if (Misc.random(4) == 0) {
						if (npcId() == NpcID.FUMUS) {
							PoisonEffect.applyPoisonTo(((OutgoingHitApplied) event).getHit().getTarget(), PoisonType.NEX);
						} else if (npcId() == NpcID.GLACIES) {
							((OutgoingHitApplied) event).getHit().getTarget().getCombat().submit(new FreezeEvent(5, false));
						}
					}
				}
			}
			return false;
		});
	}

	@Override public void appendDeath() {
		nex.incrementPhase();
		super.appendDeath();
	}

	public void makeVulnerable() {
		vulnerable = true;
		fetchDefinition().setAggressive(true);
	}

	@NotNull @Override public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
		if (npcId() == NpcID.FUMUS) {
			return Stream.of(SMOKE_PROJECTILE);
		} else if (npcId() == NpcID.UMBRA) {
			return Stream.of(SHADOW_PROJECTILE);
		} else if (npcId() == NpcID.CRUOR) {
			return Stream.of(BLOOD_PROJECTILE);
		} else {
			return Stream.of(ICE_PROJECTILE);
		}
	}

	@Override public Animation getAttackAnimation(AttackType type) {
		return ATTACK_ANIM;
	}

	@Override public Stream<HitTemplate> fetchHits(AttackType type) {
		return new HitTemplateBuilder(type).setDelay(2).buildAsStream();
	}

	@NotNull protected AttackTypeProvider attackTypes() {
		return AttackType.MAGIC;
	}

	@Override public int getMaxHit(AttackType type) {
		return 29;
	}

	@Override protected int maxTargetsHitPerAttack(@NotNull AttackType type) {
		return 1;
	}

	@Override public int fetchAttackDuration(AttackType type) {
		return 5;
	}

	@Override public double protectionPrayerReductionMultiplier(AttackType type) {
		return 0.5;
	}

	@Override public int attackRange(@NotNull AttackType type) {
		return 10;
	}

	@Override public boolean skipNextCombatSequence() {
		return !vulnerable || super.skipNextCombatSequence();
	}

	@NotNull @Override public BossAttack generateAttack() {
		return new BossAttack(this);
	}
}
