package com.grinder.game.entity.agent.npc.monster.boss.impl.god.zaros;

import com.grinder.game.World;
import com.grinder.game.collision.CollisionManager;
import com.grinder.game.content.pvm.MonsterKillTracker;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackProvider;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider;
import com.grinder.game.entity.agent.combat.event.CombatState;
import com.grinder.game.entity.agent.combat.event.impl.*;
import com.grinder.game.entity.agent.combat.hit.Hit;
import com.grinder.game.entity.agent.combat.hit.HitTemplate;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.entity.agent.npc.NPCDropGenerator;
import com.grinder.game.entity.agent.npc.monster.boss.Boss;
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack;
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.model.*;
import com.grinder.game.model.areas.godwars.NexChamber;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.model.projectile.ProjectileTemplate;
import com.grinder.game.model.projectile.ProjectileTemplateBuilder;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.TimedObjectSpawnTask;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;
import com.grinder.util.ObjectID;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Savions.
 */
public class Nex extends Boss implements AttackProvider {

	private static final int ATTACK_DURATION = 4;
	private static final int TIME_OUT_DURATION = ATTACK_DURATION * 3;
	private static final Animation SPAWN_ANIM = new Animation(9182);
	private static final Animation PHASE_SWITCH_ANIM = new Animation(9186);
	private static final Animation TURMOIL_ANIM = new Animation(9179);
	private static final Graphic TURMOIL_GFX = new Graphic(2016);
	private static final Graphic SHADOW_GFX = new Graphic(383);
	private static final Graphic WRATH_GFX = new Graphic(2013);
	private static final ProjectileTemplate WRATH_PROJECTILE = new ProjectileTemplateBuilder(2014).setSourceSize(1).setStartHeight(50).setEndHeight(0).
			setDelay(20).setSpeed(60).setCurve(5).build();
	private static final Animation SUMMON_ANIM = new Animation(9188);
	private static final Position CENTRE_POS = new Position(2924, 5202);
	private static final int[][] WRATH_DELTAS = { {1, -2}, {-1, -1}, {3, -1}, {-2, 1}, {4, 1}, {1, 4}, {-1, 3}, {3, 3}};

	private enum GuardType {
		FUMUS("Fumus", new Position(2913, 5215), 2720, "Fill my soul with smoke!"),
		UMBRA("Umbra", new Position(2937, 5215), 2040, "Darken my shadow!"),
		CRUOR("Cruor", new Position(2937, 5191), 1360, "Flood my lungs with blood!"),
		GLACIAS("Glacies", new Position(2913, 5191), 680, "Infuse me with the power of ice!");

		private final String name, phaseStartScream;
		private final Position pos;
		private final int lifePointThreshold;

		private GuardType(String name, Position pos, int lifePointThreshold, String phaseStartScream) {
			this.name = name;
			this.pos = pos;
			this.lifePointThreshold = lifePointThreshold;
			this.phaseStartScream = phaseStartScream;
		}

		public final String guardName() { return name; }
		public final Position pos() { return pos; }
		public final int lifePointThreshold() { return lifePointThreshold; }
		public final String phaseStartScream() { return phaseStartScream; }
	}

	private NexGuard[] nexGuards = new NexGuard[GuardType.values().length];
	private boolean locked = true, flying;
	private boolean immune = false;
	private int flyTimer = 15;
	private int phaseIndex = -1;
	private int attackIndex = 0;
	private int attackCycles = 0;
	private int allowNonCombat = TIME_OUT_DURATION;
	private NexAttackType attackType = NexAttackType.SMOKE_RUSH;

	@Override public void appendDeath() {
		super.appendDeath();
		setHeadIcon(16);
		if (getArea() instanceof NexChamber) {
			((NexChamber) getArea()).resetNex();
		}
		say("Taste my wrath!");
		TaskManager.submit(new Task(1,  false) {
			int ticks = 0;
			@Override protected void execute() {
				if (++ticks == 3) {
					for (int[] deltas : WRATH_DELTAS) {
						new Projectile(getCenterPosition(), getCenterPosition().transform(deltas[0], deltas[1], 0), WRATH_PROJECTILE).sendProjectile();
					}
				} else if (ticks >= 4) {
					stop();
					for (int[] deltas : WRATH_DELTAS) {
						World.spawn(new TileGraphic(getCenterPosition().transform(deltas[0], deltas[1], 0), WRATH_GFX));
					}
					playerStream(32).filter(p -> p != null && p.getArea() == getArea()).forEach(p -> {
						for (int[] deltas : WRATH_DELTAS) {
							if (p.getPosition().equals(getCenterPosition().transform(deltas[0], deltas[1], 0))) {
								p.getCombat().queue(new Damage(15 + Misc.random(30), DamageMask.REGULAR_HIT));
								break;
							}
						}
					});
					final Optional<Agent> killer = getCombat().findKiller(false);
					playerStream(32).filter(p -> p != null && p.getArea() == getArea() && (killer.isEmpty() || p != killer.get())
							&& getCombat().hasDoneDamage(p, 50)).forEach(p -> {
						NPCDropGenerator.start(p, getAsNpc());
						MonsterKillTracker.track(p, getAsNpc());
						PlayerUtil.broadcastMessage("<img=789> @whi@" + PlayerUtil.getImages(p) + "" + p.getUsername() + " has just defeated Nex!");
					});
					stop();
				}
			}
		});
	}

	public Nex(int id) {
		super(id, CENTRE_POS);
		fetchDefinition().setAggressive(false);
		getMotion().setRunning(true);
		getCombat().subscribe(event -> {
			if (event instanceof OutgoingHitApplied) {
				attackType.postHit(((OutgoingHitApplied) event).getHit().getTarget().getAsPlayer());
			} else if (CombatState.FINISHED_ATTACK.equals(event)) {
				attackIndex++;
				allowNonCombat = TIME_OUT_DURATION;
			} else if (event instanceof TargetIsNotReachable || event instanceof TargetIsOutOfReach) {
				flyToTarget();
			} else if (event instanceof IncomingHitQueued) {
				final Hit hit = ((IncomingHitQueued) event).getHit();
				if (hit.getTotalDamage() > 100) {
					hit.setTotalDamage(100);
				}
				if (immune) {
					hit.setNegateDamages(true);
					if (hit.getAttacker() != null && hit.getAttacker().isPlayer()) {
						hit.getAttacker().getAsPlayer().sendMessage("Nex is currently immune to your attacks.");
					}
				} else if (AttackType.MELEE.equals(hit.getAttackType()) && getHeadIcon() == 9) {
					hit.setNegateDamages(true);
					if (hit.getAttacker() != null) {
						hit.getAttacker().getCombat().queue(new Damage(hit.getTotalDamage(), DamageMask.BLOCK));
					}
				}
			} else if (!immune && phaseIndex < GuardType.values().length && event instanceof IncomingHitApplied) {
				final Hit hit = ((IncomingHitApplied) event).getHit();
				if (getHitpoints() - hit.getTotalDamage() <= GuardType.values()[phaseIndex].lifePointThreshold()) {
					hit.setTotalDamage(getHitpoints() - GuardType.values()[phaseIndex].lifePointThreshold());
					say(GuardType.values()[phaseIndex].guardName() + ", don't fail me!");
					immune = true;
					nexGuards[phaseIndex].makeVulnerable();
				}
			}
			return false;
		});
	}

	public void start() {
		say("AT LAST!");
		performAnimation(SPAWN_ANIM);
		TaskManager.submit(new Task(1, this, false) {
			int tickCount = -1;
			int bodyGuardCount;
			@Override protected void execute() {
				if (++tickCount < 4) {
					return;
				}
				if (bodyGuardCount >= 4) {
					if (tickCount == 4) {
						incrementPhase();
					} else if (tickCount == 7) {
						locked = false;
						fetchDefinition().setAggressive(true);
						stop();
					}
				} else {
					final GuardType guardType = GuardType.values()[bodyGuardCount];
					say(guardType.guardName() + "!");
					performAnimation(SUMMON_ANIM);
					setPositionToFace(guardType.pos(), true);
					nexGuards[bodyGuardCount] = new NexGuard(NpcID.FUMUS + bodyGuardCount, guardType.pos(), Nex.this);
					nexGuards[bodyGuardCount].setPositionToFace(getCenterPosition());
					nexGuards[bodyGuardCount].spawn();
					var builder = new ProjectileTemplateBuilder(2010);
					builder.setCurve(10);
					builder.setStartHeight(100);
					builder.setEndHeight(20);
					builder.setSpeed(70);
					builder.build();
					new Projectile(getCenterPosition(), guardType.pos, builder.build()).sendProjectile();
					bodyGuardCount++;
					tickCount = 0;
				}
			}
		});
	}

	public void incrementPhase() {
		phaseIndex++;
		immune = false;
		getCombat().extendNextAttackDelay(5);
		if (phaseIndex >= GuardType.values().length) {
			say("NOW, THE POWER OF ZAROS!");
			performAnimation(TURMOIL_ANIM);
			performGraphic(TURMOIL_GFX);
			heal(500);
			locked = true;
			TaskManager.submit(new Task(2, this, false) {

				@Override protected void execute() {
					locked = false;
				}
			});
			attackType = NexAttackType.ZAROS_ATTACK;
		} else {
			final GuardType guardType = GuardType.values()[phaseIndex];
			say(guardType.phaseStartScream);
			var builder = new ProjectileTemplateBuilder(2010);
			builder.setCurve(10);
			builder.setStartHeight(20);
			builder.setEndHeight(100);
			builder.setSpeed(70);
			builder.build();
			new Projectile(guardType.pos, this, builder.build()).sendProjectile();
			performAnimation(PHASE_SWITCH_ANIM);
			attackType = NexAttackType.MELEE_ATTACK;
		}
		setBossAttack(generateAttack());
	}

	private void flyToTarget() {
		if (!isActive() || isDying() || flyTimer > 0) {
			return;
		}
		final Player player = findNextTarget();
		if (player == null || flying || locked) {
			return;
		}
		Position targetPosition = null;
		final int[][] deltas = Misc.getCoordOffsetsNear(3);
		coordsLoop: for (int i = 0; i < deltas [0].length; i++) {
			final Position basePos = player.getPosition().transform(deltas[0][i], deltas[1][i], 0);
			if (getPosition().equals(basePos)) {
				continue;
			}
			for (int xDelta = 0; xDelta <= 2; xDelta++) {
				for (int yDelta = 0; yDelta <=2; yDelta++) {
					if (CollisionManager.blocked(basePos.transform(xDelta, yDelta, 0))) {
						continue coordsLoop;
					}
				}
			}
			targetPosition = basePos;
			break;
		}
		if (targetPosition == null) {
			return;
		}
		flying = true;
		flyTimer = 15;
		fetchDefinition().setAggressive(false);
		setLastPosition(getPosition().clone());
		getMotion().clearSteps();
		setForceMovement(new ForceMovement(getPosition().clone(), targetPosition, 4, 30,
				Direction.getDirection(Misc.getDirection(getCenterPosition(), targetPosition)).getForceMovementMask(), 9187));
		final Position finalizedPos = targetPosition;
		allowNonCombat = TIME_OUT_DURATION;
		TaskManager.submit(new Task(1, this, false) {
			@Override protected void execute() {
				moveTo(finalizedPos.clone());
				if (!player.isActive() || player.getArea() != getArea()) {
					flyToTarget();
				} else {
					flying = false;
					getCombat().target(player);
					fetchDefinition().setAggressive(true);
				}
				stop();
			}
		});
	}

	private Player findNextTarget() {
		int currentDistance = 0;
		Player player = null;
		for (Player p : playerStream(32).filter(p -> p != null && p.getArea() == getArea()).toArray(Player[]::new)) {
			final int distance = Misc.getDistance(p.getPosition(), this.getCenterPosition());
			if (distance > currentDistance) {
				currentDistance = distance;
				player = p;
			}
		}
		return player;
	}

	@NotNull @Override public OutOfRangePolicy attackRangePolicy(@NotNull AttackType type) {
		return OutOfRangePolicy.TRACE_TO_TARGET;
	}

	@Override public boolean useSmartPathfinding() {
		return true;
	}

	@Override
	public void randomizeAttack() {
		if (phaseIndex == 0) {
			attackIndex %= 8;
			if (attackIndex == 0) {
				getCombat().extendNextAttackDelay(4);
				final Player target = findNextTarget();
				if (target != null && CoughEffectTask.canApply(target)) {
					setPositionToFace(target.getPosition());
					say("Let the virus flow through you!");
					TaskManager.submit(new CoughEffectTask(target));
				}
				attackIndex++;
			} else {
				if (attackIndex == 2) {
					flyToTarget();
				}
				if (Misc.getDistance(getCombat().getTarget().getPosition(), this.getCenterPosition()) <= 3) {
					attackType = Misc.random(2) != 0 ? NexAttackType.SMOKE_RUSH : NexAttackType.MELEE_ATTACK;
				} else {
					attackType = NexAttackType.SMOKE_RUSH;
				}
			}
		} else if (phaseIndex == 1) {
			attackIndex %= 7;
			if (attackIndex == 0) {
				getCombat().extendNextAttackDelay(4);
				say("Fear the shadow!");
				performAnimation(PHASE_SWITCH_ANIM);
				for (final Player p : playerStream(32).filter(p -> p != null && p.getArea() == getArea()).toArray(Player[]::new)) {
					final Position position = p.getPosition().clone();
					TaskManager.submit(new TimedObjectSpawnTask(DynamicGameObject.createPublic(ObjectID.SHADOW, position), 3, Optional.of(() -> {
						World.spawn(new TileGraphic(position, SHADOW_GFX));
						if (p.isActive() && p.getPosition().equals(position)) {
							p.getCombat().queue(new Damage(35 + Misc.random(25), DamageMask.REGULAR_HIT));
						}
					})));
				}
				attackIndex++;
			} else {
				if (attackIndex == 2) {
					flyToTarget();
				}
				if (Misc.getDistance(getCombat().getTarget().getPosition(), this.getCenterPosition()) <= 3) {
					attackType = Misc.random(2) != 0 ? NexAttackType.SHADOW_RUSH : NexAttackType.MELEE_ATTACK;
				} else {
					attackType = NexAttackType.SHADOW_RUSH;
				}
			}
		} else if (phaseIndex == 2) {
			//TODO blood reaver attack when heal hitsplat mask is added
			if (Misc.getDistance(getCombat().getTarget().getPosition(), this.getCenterPosition()) <= 3) {
				attackType = Misc.random(2) != 0 ? NexAttackType.BLOOD_RUSH : NexAttackType.MELEE_ATTACK;
			} else {
				attackType = NexAttackType.BLOOD_RUSH;
			}
		} else if (phaseIndex == 3) {
			attackIndex %= 7;
			if (attackIndex == 0) {
				getCombat().extendNextAttackDelay(6);
				say("Contain this!!");
				final Position position = getPosition().clone();
				performAnimation(PHASE_SWITCH_ANIM);
				TaskManager.submit(new Task(4, this, false) {
					@Override protected void execute() {
						for (int x = -1; x <= 3; x += 1) {
							for (int y = -1; y <= 3; y += 1) {
								if (y >= 0 && y <= 2 && x >= 0 && x <= 2) {
									continue;
								}
								if (!CollisionManager.blocked(position.transform(x, y, 0))) {
									TaskManager.submit(new TimedObjectSpawnTask(DynamicGameObject.createPublic(ObjectID.STALAGMITE_42943, position.transform(x, y, 0)), 6,
											Optional.empty()));
								}
							}
						}
						playerStream(32).filter(p -> p != null && p.getArea() == getArea() && p.getPosition().getDistance(position.transform(1, 1, 0)) == 3).forEach(p -> {
							p.getCombat().queue(new Damage(20 + Misc.random(40), DamageMask.REGULAR_HIT));
							p.say("Ouch!");
							PrayerHandler.deactivatePrayer(p, PrayerHandler.getProtectingPrayer(AttackType.MELEE));
							PrayerHandler.deactivatePrayer(p, PrayerHandler.getProtectingPrayer(AttackType.MAGIC));
							PrayerHandler.deactivatePrayer(p, PrayerHandler.getProtectingPrayer(AttackType.RANGED));
						});
						stop();
					}
				});
				attackIndex++;
			} else {
				if (Misc.getDistance(getCombat().getTarget().getPosition(), this.getCenterPosition()) <= 3) {
					attackType = Misc.random(2) != 0 ? NexAttackType.ICE_RUSH : NexAttackType.MELEE_ATTACK;
				} else {
					attackType = NexAttackType.ICE_RUSH;
				}
			}
		} else {
			attackIndex %= 10;
			if (attackIndex == 5) {
				setHeadIcon(9);
			} else if (attackIndex == 0) {
				setHeadIcon(-1);
			}
			if (Misc.getDistance(getCombat().getTarget().getPosition(), this.getCenterPosition()) <= 3) {
				attackType = Misc.random(2) != 0 ? NexAttackType.ZAROS_RUSH : NexAttackType.ZAROS_ATTACK;
			} else {
				attackType = NexAttackType.ZAROS_ATTACK;
			}
		}
		setBossAttack(generateAttack());
	}

	@Override
	public int attackRange(@NotNull AttackType type) {
		return AttackType.MELEE.equals(type) || Misc.random(4) == 0 ? 1 : 10;
	}

	@Override public boolean skipNextCombatSequence() {
		return locked || flying || super.skipNextCombatSequence();
	}

	@Override public boolean skipNextRetreatSequence() {
		return locked || flying || super.skipNextRetreatSequence();
	}

	@NotNull @Override public BossAttack generateAttack() {
		final BossAttack attack = new BossAttack(attackType);
		attack.setType(attackType.type());
		return attack;
	}

	@Override public void sequence() {
		super.sequence();
		if (flyTimer > 0) {
			flyTimer--;
		}
		if (!skipNextCombatSequence()) {
			if (allowNonCombat > 0) {
				allowNonCombat--;
			} else {
				flyToTarget();
			}
		}
	}

	@NotNull @Override protected AttackTypeProvider attackTypes() {
		return attackType.type();
	}

	@Override public int getMaxHit(AttackType type) {
		return attackType.getMaxHit();
	}

	@Override protected int maxTargetsHitPerAttack(@NotNull AttackType type) {
		return AttackType.MELEE.equals(type) && phaseIndex != GuardType.values().length ? 1 : 64;
	}

	@Override
	public int getBlockAnim() {
		return 9185;
	}

	@Override public int fetchAttackDuration(AttackType type) {
		return 4;
	}

	@Override public double protectionPrayerReductionMultiplier(AttackType type) {
		return 0.5;
	}

	@Override
	public Animation getAttackAnimation(AttackType type) {
		return attackType.getAttackAnimation(type);
	}

	@NotNull
	@Override
	public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
		return attackType.fetchProjectiles(type);
	}

	@Override public Optional<Graphic> fetchAttackGraphic(AttackType type) {
		return Optional.empty();
	}

	@Override public Optional<String> fetchTextAboveHead(AttackType type) {
		return Optional.empty();
	}

	@Override public Optional<Sound> fetchAttackSound(AttackType type) {
		return Optional.empty();
	}

	@Override
	public Stream<HitTemplate> fetchHits(AttackType type) {
		return attackType.fetchHits(type);
	}

	public void destroy() {
		TaskManager.cancelTasks(this);
		if (isActive())
			World.getNpcRemoveQueue().add(this);
		for (NexGuard nexGuard : nexGuards) {
			if (nexGuard != null && nexGuard.isActive()) {
				TaskManager.cancelTasks(nexGuard);
				World.getNpcRemoveQueue().add(nexGuard);
			}
		}
	}

	public boolean locked() { return locked; }
}
