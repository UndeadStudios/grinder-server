package com.grinder.game.entity.agent.npc.monster.boss.impl;

import com.grinder.game.GameConstants;
import com.grinder.game.World;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.pvm.MonsterKillTracker;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackProvider;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider;
import com.grinder.game.entity.agent.combat.event.impl.FreezeEvent;
import com.grinder.game.entity.agent.combat.hit.HitTemplate;
import com.grinder.game.entity.agent.combat.hit.HitTemplateBuilder;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.entity.agent.npc.NPCDropGenerator;
import com.grinder.game.entity.agent.npc.monster.boss.Boss;
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack;
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.updating.UpdateBlock;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.TileGraphic;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.model.projectile.ProjectileTemplate;
import com.grinder.game.model.projectile.ProjectileTemplateBuilder;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.DistanceUtil;
import com.grinder.util.Misc;
import com.grinder.util.time.TimeUnits;
import com.grinder.util.timing.TimerKey;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Savions.
 */
public class MimicBoss extends Boss implements AttackProvider {

	private static final String[] RANDOM_SHOUTS = { "Have some candy!", "HAHAHAHAHAHA!", "The sweet death", "Halloween? I am ALWAYS HERE!",
			"Welcome to my home!", "WHO WANTS SOME CANDY??", "I promise this one won't hurt!"
	};

	private static final Position SPAWN_POS = new Position(2716, 4318, 1);

	private MimicAttackType mimicAttackType = MimicAttackType.MELEE_ATTACK;

	public MimicBoss() {
		super(8633, SPAWN_POS);
	}

	@Override public void appendDeath() {
		super.appendDeath();
		say("NOOOOOOO!!!!");
		TaskManager.submit(new Task(2,  false) {

			@Override protected void execute() {
				final Optional<Agent> killer = getCombat().findKiller(false);
				playerStream(32).filter(p -> p != null && p.getArea() == getArea() && (killer.isEmpty() || p != killer.get())
						&& getCombat().hasDoneDamage(p, 20)).forEach(p -> {
					NPCDropGenerator.start(p, getAsNpc());
					MonsterKillTracker.track(p, getAsNpc());
					PlayerUtil.broadcastMessage("<img=789> @whi@" + PlayerUtil.getImages(p) + "" + p.getUsername() + " has just defeated The Mimic!");

					if (p.getAchievements().getProgress()[AchievementType.HALLOWEEN_2022.ordinal()] == 0 && p.getTimePlayed(TimeUnits.HOUR) >= 1) {
						AchievementManager.processFor(AchievementType.HALLOWEEN_2022, p);
						p.sendMessage("You have defeated the evil mimic boss and completed the 2022 Halloween event!");
						p.sendMessage("You can still fight this boss again for halloween drops!");
					}
					//p.moveTo(GameConstants.DEFAULT_POSITION);
				});
				stop();
			}
		});
	}

	@Override
	public void randomizeAttack() {
		if (Misc.getDistance(getCombat().getTarget().getPosition(), this.getCenterPosition()) <= 5 && Misc.random(2) == 0) {
			mimicAttackType = MimicAttackType.MELEE_ATTACK;
		} else {
			final int random = Misc.random(8);
			switch(random) {
				case 0:
				case 1:
					performAnimation(new Animation(8309));
					say(random == 1 ? "Don't move! HAHAHA!" : "Healing time! HAHAHA!!");
					getCombat().extendNextAttackDelay(7);
					final ArrayList<Position> positions = new ArrayList<Position>();
					playerStream(16).filter(p -> p != null && p.isActive()).forEach(p -> {
						final Position targetPos = p.getPosition().clone();
						p.sendMessage("The mimic has sent a " + (random == 1 ? "<col=ff0000>damaging</col>" : "<col=00FF00>healing</col>") + " candy towards you, move!");
						positions.add(targetPos);
						var builder = new ProjectileTemplateBuilder(1671 + random);
						builder.setCurve(10);
						builder.setStartHeight(120);
						builder.setEndHeight(0);
						builder.setSpeed(25 + Math.max(DistanceUtil.getChebyshevDistance(getCenterPosition(), p.getPosition().clone()) * 3, 35));
						builder.build();
						new Projectile(getCenterPosition(), targetPos, builder.build()).sendProjectile();
					});
					if (positions.size() > 0) {
						TaskManager.submit(new Task(4, this, false) {
							@Override protected void execute() {
								if (isActive() && !isDying()) {
									positions.forEach(pos -> {
										//new Projectile(cast, castOn, 1252, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 30, 10, 280));

										World.spawn(new TileGraphic(pos, new Graphic(1675 + random)));
										playerStream(16).filter(p -> p != null && p.isActive() && pos.equals(p.getPosition())).forEach(p -> {
											if (random == 0) {
												healWithHitsplat(20);
												p.getCombat().queue(new Damage(20 + Misc.random(10), DamageMask.REGULAR_HIT));
											} else {
												p.getCombat().queue(new Damage(35 + Misc.random(10), DamageMask.REGULAR_HIT));
											}
										});
									});
								}
								stop();
							}
						});
					}
					break;
				default:
					if (Misc.random(3) == 0) {
						say(Misc.random(RANDOM_SHOUTS));
					}
					mimicAttackType = MimicAttackType.MAGE_ATTACK;
					break;
			}
		}
		setBossAttack(generateAttack());
	}

	@Override public int fetchAttackDuration(AttackType type) {
		return 3;
	}

	@Override public Animation getAttackAnimation(AttackType type) {
		return mimicAttackType.getAttackAnimation(type);
	}

	@Override public Stream<HitTemplate> fetchHits(AttackType type) {
		return mimicAttackType.fetchHits(type);
	}

	@Override public int getMaxHit(AttackType type) {
		return mimicAttackType.getMaxHit();
	}

	@Override
	public int attackRange(@NotNull AttackType type) {
		return AttackType.MELEE.equals(type) || Misc.random(4) == 0 ? 1 : 10;
	}

	@NotNull @Override public BossAttack generateAttack() {
		final BossAttack attack = new BossAttack(mimicAttackType);
		attack.setType(mimicAttackType.type());
		return attack;
	}

	@NotNull @Override protected AttackTypeProvider attackTypes() {
		return mimicAttackType.type();
	}

	@Override protected int maxTargetsHitPerAttack(@NotNull AttackType type) {
		return 64;
	}

	@NotNull @Override public OutOfRangePolicy attackRangePolicy(@NotNull AttackType type) {
		return OutOfRangePolicy.TRACE_TO_TARGET;
	}

	@Override public double protectionPrayerReductionMultiplier(AttackType type) {
		return 0.5;
	}

	private enum MimicAttackType implements AttackProvider {
		MAGE_ATTACK(AttackType.MAGIC) {
			@Override public Animation getAttackAnimation(AttackType type) {
				return new Animation(8309);
			}

			@Override public Stream<HitTemplate> fetchHits(AttackType type) {
				return new HitTemplateBuilder(type).setDelay(2).buildAsStream();
			}

			@NotNull @Override public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
				return Stream.of(CANDY_PROJECTILE);
			}

			@Override public int getMaxHit() {
				return 14;
			}
		},
		MELEE_ATTACK(AttackType.MELEE) {

			@Override public Animation getAttackAnimation(AttackType type) {
				return new Animation(8308);
			}

			@Override public Stream<HitTemplate> fetchHits(AttackType type) {
				return new HitTemplateBuilder(type).setDelay(0).buildAsStream();
			}

			@Override public int getMaxHit() {
				return 28;
			}
		};

		private static final ProjectileTemplate CANDY_PROJECTILE = new ProjectileTemplateBuilder(1670).setSourceSize(1).setStartHeight(50).setEndHeight(30).
				setDelay(40).setSpeed(30).setCurve(5).build();

		private final AttackType type;

		private MimicAttackType(final AttackType type) {
			this.type = type;
		}

		public final AttackType type() { return type; }

		@Override public int fetchAttackDuration(com.grinder.game.entity.agent.combat.attack.AttackType type) {
			return 3;
		}

		@Override public Animation getAttackAnimation(com.grinder.game.entity.agent.combat.attack.AttackType type) {
			return null;
		}

		@Override public Stream<HitTemplate> fetchHits(com.grinder.game.entity.agent.combat.attack.AttackType type) {
			return null;
		}

		public int getMaxHit() {
			return 33;
		}
	}
}
