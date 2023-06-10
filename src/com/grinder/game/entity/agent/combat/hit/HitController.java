package com.grinder.game.entity.agent.combat.hit;

import com.grinder.game.content.skill.SkillManager;
import com.grinder.game.content.skill.skillable.impl.magic.SpellCasting;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.Combat;
import com.grinder.game.entity.agent.combat.attack.AttackContext;
import com.grinder.game.entity.agent.combat.attack.AttackStrategy;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.strategy.MeleeAttackStrategy;
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonEffect;
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonType;
import com.grinder.game.entity.agent.combat.event.impl.BindEvent;
import com.grinder.game.entity.agent.combat.event.impl.FreezeEvent;
import com.grinder.game.entity.agent.combat.event.impl.PoisonEvent;
import com.grinder.game.entity.agent.combat.event.impl.VegeanceEvent;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.TotalTrackedDamage;
import com.grinder.game.entity.agent.combat.misc.CombatEquipment;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.game.entity.updating.UpdateBlock;
import com.grinder.game.entity.updating.UpdateBlockSet;
import com.grinder.game.entity.updating.block.HitFirstUpdateBlock;
import com.grinder.game.entity.updating.block.HitSecondUpdateBlock;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Skill;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.player.Equipment;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;
import com.grinder.util.timing.TimerKey;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import static com.grinder.util.ItemID.RING_OF_LIFE;

/**
 * This class represents a custom queue implementation to which one can add {@link Hit}s.
 * @see Combat#queueOutgoingHit(Hit)
 *
 * These hits are then sequenced and translated into {@link Damage}.
 *
 * @author Professor Oak
 * @author Stan van der Bend
 */
public abstract class HitController<T extends Agent> {

	/**
	 * The amount of time it takes for cached damage to timeout.
	 */
	private static final long DAMAGE_CACHE_TIMEOUT = 60_000;

	public final Map<Agent, TotalTrackedDamage> damageMap = new HashMap<>();

	// Our list containing all our incoming hits waiting to be processed.
	protected List<Hit> hits = new ArrayList<>();

	// Our queue of current damage waiting to be dealt.
	protected Queue<Damage> damages = new ConcurrentLinkedQueue<>();

	private Agent firstDamageDealer = null;

	protected long lastReceivedHitTimer = System.currentTimeMillis();
	protected long lastDealtHitTimer = System.currentTimeMillis();

	public abstract Damage modifyHitDamage(AttackContext context, Agent target, int baseDamage);

	public void queue(Hit hit) {
		hits.add(hit);
	}

	public void queue(Damage... damages) {
		Arrays.stream(damages)
				.filter(Objects::nonNull)
				.forEach(this.damages::add);
	}

	public void queueOutgoingHit(Hit outgoingHit){
		Optional.ofNullable(outgoingHit)
				.filter(Hit::canBeQueued)
				.ifPresent(hit -> hit.getTarget().getCombat().queue(hit));
	}

	protected abstract void apply(Hit hit);

	protected void sequenceHits(T actor) {

		if (!actor.isAlive()) {
			hits.clear();
			damages.clear();
			return;
		}



		final Iterator<Hit> iterator = hits.iterator();

		while (iterator.hasNext()) {

			final Hit nextHit = iterator.next();

			if (nextHit == null || !nextHit.isValid()) {
				iterator.remove();
				continue;
			}

			final int damageApplicationDelay = nextHit.getAndDecrementDelay();

			if (damageApplicationDelay <= 0) {
				if (!EntityExtKt.getBoolean(actor, Attribute.STALL_HITS, false)) {
					apply(nextHit);
					iterator.remove();
				}
			}
		}
		actor.onStateChange("sequenced hits");
	}

	protected void sequenceDamages(T actor){
		if (!damages.isEmpty()) {
			//if (actor.isStallingDamage()) {
			//	return;
			//}
			final UpdateBlockSet blockSet = actor.getBlockSet();

			if(!blockSet.contains(HitFirstUpdateBlock.class)){
				Optional.ofNullable(damages.poll())
						.map(actor::decrementHealth)
						.ifPresent(firstHit -> {
							actor.setPrimaryHit(firstHit);
							blockSet.add(UpdateBlock.Companion.createUpdateFirstHitBlock(actor, firstHit));
						});
			}

			if(!blockSet.contains(HitSecondUpdateBlock.class)){
				Optional.ofNullable(damages.poll())
						.map(actor::decrementHealth)
						.ifPresent(secondHit -> {
							actor.setSecondaryHit(secondHit);
							blockSet.add(UpdateBlock.Companion.createUpdateSecondHitBlock(actor, secondHit));
						});
			}

			if(actor instanceof Player){

				final Player actorPlayer = ((Player) actor);
				final Equipment equipment = actorPlayer.getEquipment();
				final int currentHitPoints = actorPlayer.getHitpoints();
				final int maxHitPoints = actorPlayer.getSkillManager().getMaxLevel(Skill.HITPOINTS);

				final Item ringItem = equipment.atSlot(EquipmentConstants.RING_SLOT);

				if(ringItem.getId() == RING_OF_LIFE)
					CombatEquipment.handleRingOfLife(actorPlayer, equipment, currentHitPoints, maxHitPoints, ringItem);

			}
		}
		actor.onStateChange("sequenced damages");
	}

	protected void cacheDamage(Agent agent, int amount) {

		if (amount <= 0) {
			return;
		}

		if (damageMap.containsKey(agent)) {
			damageMap.get(agent).incrementDamage(amount);
			return;
		}

		if(damageMap.isEmpty())
			firstDamageDealer = agent;

		damageMap.put(agent, new TotalTrackedDamage(amount));
	}

	protected long secondsPastLastHitReceived(){
		return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastReceivedHitTimer);
	}

	protected long secondsPastLastHitDealt(){
		return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastDealtHitTimer);
	}

	public boolean hasDoneDamage(Player player) {
		return damageMap.containsKey(player);
	}

	public boolean hasDoneDamage(Player player, int minDamage) {
		return damageMap.containsKey(player) && damageMap.get(player).getDamage() >= minDamage;
	}

	/***
	 * Checks if the pending hit queue is empty, except for the specified
	 * {@link Agent}.
	 *
	 * Used for anti-pjing.
	 *
	 * @param exception the {@link Agent} to check for in the {@link #hits}.
	 *
	 * @return true if {@link #hits} is empty except for the exception.
	 */
	protected boolean hasNoPendingHitsButFrom(Agent exception) {

		for (final Hit hit : hits) {

			if (hit == null || hit.getAttacker() == null)
				continue;

			if (!hit.getAttacker().equals(exception))
				return false;
		}
		return true;
	}

	protected void tryPerformingBlockAnimation(T actor, Hit hit, int blockAnimationId) {
		// Logout timer in the Wilderness reset upon getting hit
		if (actor instanceof Player) {
			if (actor != null && actor.getAsPlayer().hasLogoutTimer()) {
				actor.getAsPlayer().getPacketSender().sendInterfaceRemoval();
				actor.getAsPlayer().sendMessage("@red@Your logout request has been interrupted.");
				actor.getAsPlayer().setHasLogoutTimer(false);
			}
		}
		// Agility
		if (EntityExtKt.getBoolean(actor, Attribute.STALL_HITS, false))
			return;

		actor.ifPlayer(player -> {

			if (player.isTeleporting() && player.getTeleportingType() == TeleportType.HOME)
				player.stopTeleporting();

			if (player.getRights() != PlayerRights.DEVELOPER)
				player.getPacketSender().sendInterfaceRemoval();

/*			if (player.getCombat().getTarget() != null) {
				if (player.getCombat().getTarget().isNpc()) {
					if (player.getCombat().getTarget().getAsNpc().getId() == NpcID.COMBAT_DUMMY || player.getCombat().getTarget().getAsNpc().getId() == NpcID.UNDEAD_COMBAT_DUMMY) {
						return;
					}
				}
			}*/
		});

		actor.getTimerRepository().register(TimerKey.COMBAT_COOLDOWN, 15);

		if(blockAnimationId <= 0 && !(actor.isNpc() && actor.getAsNpc().isNoRetaliateNPC(actor.getAsNpc().getId()))) {
			return;
		}
		final Agent attacker = hit.getAttacker();
		final int damage = hit.getTotalDamage();

		// delay is handled in client cycles for melee
		if(!(hit.getContext().getStrategy() instanceof MeleeAttackStrategy)){
			final Animation blockAnimation = new Animation(blockAnimationId);
			actor.performAnimation(blockAnimation);
		}

		if (actor instanceof Player) {

			final Player actorPlayer = ((Player) actor);
			final int blockSoundId = Sounds.getBlockSound(actorPlayer, damage);

			actorPlayer.getPacketSender().sendSound(blockSoundId);

		} else if (attacker instanceof Player) {
			final Player attackerPlayer = ((Player) attacker);
			final NPC actorNPC = ((NPC) actor);

			actorNPC.sendBlockSound(attackerPlayer);
		}
	}

	protected void rewardHitExperience(T actor, Hit hit){

		if(!(actor instanceof Player))
			return;

		final Agent target = hit.getTarget();

//		if(target instanceof Player){
//
//			final Player targetPlayer = ((Player) target);
//			final GameMode targetGameMode = targetPlayer.getGameMode();
//
//			if(targetGameMode.isIronman() || targetGameMode.isHardcore() || targetGameMode.isUltimate() || targetGameMode.isSpawn())
//				return;
//		}FIXME if its broken not sure why this is here, it's blocking ironmans from getting frozen.

		if (target != null && target instanceof NPC) {
			if (target.getAsNpc().getId() == NpcID.COMBAT_DUMMY || target.getAsNpc().getId() == NpcID.UNDEAD_COMBAT_DUMMY) {
				return;
			}
		}

		final Player player = ((Player) actor);
		final SkillManager skillManager = player.getSkillManager();
		final AttackType hitType = hit.getAttackType();
		int damage = hit.getTotalDamage();
		if (target.getHitpoints() < damage) // do not award more than you are hitting for.
			damage = target.getHitpoints();

		// Add magic exp, even if total damage is 0.
		// Since spells have a base exp reward
		if (hitType == AttackType.MAGIC) {
			if (player.getCombat().getPreviousCast() != null) {
				if (hit.getTotalDamage() == 0) {
					skillManager.addExperience(Skill.MAGIC, SpellCasting.MAGIC_SPLASH_HIT_EXPERIENCE, true);
				} else if (hit.isAccurate() && hit.getTotalDamage() > 0)
					skillManager.addExperience(Skill.MAGIC, damage * 4, true);
					//skillManager.addExperience(Skill.MAGIC, damage);
				else
					skillManager.addExperience(Skill.MAGIC, SpellCasting.MAGIC_SPLASH_HIT_EXPERIENCE, true);
			} else {
				//Blaze	- Magic - MagicExp = 2 * Damage
				skillManager.addExperience(Skill.MAGIC, SpellCasting.MAGIC_SPLASH_HIT_EXPERIENCE, true);
				//skillManager.addExperience(Skill.MAGIC, damage * 2);
			}
		}

		if(target.isNpc()) {
			if(player.instance != null) {
				player.getCOX().points += damage * 2;
			}
		}

		// Don't add any exp to other skills if total damage is 0.
		if (damage <= 0)
			return;

		if (hitType == AttackType.MAGIC) {
			skillManager.addExperience(Skill.HITPOINTS, damage * 1.33);
			if (target != null && !target.isNpc() && player.getCombat().getCastSpell() != null) {
				if (player.getCombat().getCastSpell().spellId() != -1 && (hit.isAccurate() || hit.getTotalDamage() > 0)) {
					final int spellId = player.getCombat().getCastSpell().spellId();
					//Check if should freeze player.. //target
					TaskManager.submit(new Task(1) {

						@Override
						protected void execute() {

							switch (spellId) {
								case 1572:
									target.getCombat().submit(new BindEvent(player, target.hasActivePrayer(PrayerHandler.PROTECT_FROM_MAGIC) ? 8 : 10, 5));
									break;
								case 1582:
									target.getCombat().submit(new BindEvent(player, target.hasActivePrayer(PrayerHandler.PROTECT_FROM_MAGIC) ? 8 : 16, 5));
									break;
								case 1592:
									target.getCombat().submit(new BindEvent(player, target.hasActivePrayer(PrayerHandler.PROTECT_FROM_MAGIC) ? 12 : 24, 5));
									break;
								case 12861:
									target.getCombat().submit(new FreezeEvent(target.hasActivePrayer(PrayerHandler.PROTECT_FROM_MAGIC) ? 8 : 10, false));
									break;
								case 12881:
									target.getCombat().submit(new FreezeEvent(target.hasActivePrayer(PrayerHandler.PROTECT_FROM_MAGIC) ? 8 : 16, false));
									break;
								case 12871:
									target.getCombat().submit(new FreezeEvent(target.hasActivePrayer(PrayerHandler.PROTECT_FROM_MAGIC) ? 9 : 18, false));
									break;
								case 12891:
									target.getCombat().submit(new FreezeEvent(target.hasActivePrayer(PrayerHandler.PROTECT_FROM_MAGIC) ? 10 : 20, false));
									break;
							}

							stop();
						}
					});
				}
			}
				return;
		}

		for (final int skillId : hit.getSkills()) {
			final Skill skill = Skill.values()[skillId];
			skillManager.addExperience(skill, (double) (damage * 4) / hit.getSkills().length);
		}

		skillManager.addExperience(Skill.HITPOINTS, damage * 1.33);
	}

	protected void applyHitEffects(T actor, Hit hit) {

		final Agent attacker = hit.getAttacker();
		final AttackType attackType = hit.getAttackType();
		final AttackStrategy<?> attackStrategy = hit.getCombatMethod();
		final int damage = hit.getTotalDamage();

		if(hit.handleAfterHitEffects()) {
			Optional.ofNullable(attackStrategy)
					.ifPresent(method -> method.postHitEffect(hit));
		}

		final Agent target = hit.getTarget();
		if(target.isActive() && target.isAlive()){
			Optional.ofNullable(target.getCombat().determineStrategy()).ifPresent(method -> method.postIncomingHitEffect(hit));
			hit.findGraphic().ifPresent(target::performGraphic);
			hit.findSound().ifPresent(sound -> target.ifPlayer(player -> player.playSound(sound)));
			hit.findConsumer().ifPresent(agentConsumer -> agentConsumer.accept(target));
		}

		if (!hit.isIgnorePoisonEffects()) {
			if (attacker instanceof Player) {

				final Player attackerPlayer = ((Player) attacker);
				final float odds = EquipmentUtil.isWieldingAbyssalTentacle(attackerPlayer)
						? 25.0F
						: 50.0F;

				if (Misc.randomChance(odds)) {
					CombatEquipment
							.findPoisonousWeapon(attackType, attackerPlayer)
							.map(PoisonEvent::new)
							.ifPresent(event -> actor.getCombat().submit(event));
				}

			} else if (attacker instanceof NPC) {

				final NPC npcAttacker = ((NPC) attacker);
				final NpcDefinition definition = npcAttacker.fetchDefinition();

				if (definition.isPoisonous()) {
					if (Misc.randomChance(PoisonEffect.APPLY_POISON_CHANCE)) {
						actor.getCombat().submit(new PoisonEvent(PoisonType.SUPER));
					}
				}
			}
		}

		if(damage > 0){
			if(!actor.getVengeanceEffect().finished()) {
				actor.getVengeanceEffect().stop();
				actor.getCombat().submit(new VegeanceEvent(attacker, hit));
			}
		}
	}

	/**
	 * Performs a search on the <code>damageMap</code> to find which
	 * {@link Player} dealt the most damage on this controller.
	 *
	 * @param clearMap
	 *            <code>true</code> if the map should be discarded once the
	 *            killer is found, <code>false</code> if no data in the map
	 *            should be modified.
	 * @return the player who killed this entity, or <code>null</code> if an npc
	 *         or something else killed this entity.
	 */
	public Optional<Agent> findKiller(boolean clearMap) {

		// Return null if no players killed this entity.
		if (damageMap.size() == 0) {
			return Optional.empty();
		}

		// The damage and killer placeholders.
		int damage = 0;
		Optional<Agent> killer = Optional.empty();

		for (Map.Entry<Agent, TotalTrackedDamage> entry : damageMap.entrySet()) {

			// Check if this entry is valid.
			if (entry == null) {
				continue;
			}

			// Check if the cached time is valid.
			long timeout = entry.getValue().getStopwatch().elapsed();

			if (timeout > DAMAGE_CACHE_TIMEOUT)
				continue;

			// Check if the key for this entry has logged out.
			final Agent agent = entry.getKey();

			if (!agent.isActive())
				continue;

			/*if(agent instanceof Player && agent.getAsPlayer().getGameMode().isAnyIronman()){
				// Ironman players won't be taken into account when there are multiple damage dealers.
				if (damageMap.size() > 1 )
					continue;
			}*/
			if(agent instanceof Player && agent.getAsPlayer().getGameMode().isSpawn()){
				// Spawn game mode players won't be taken into account when there are multiple damage dealers.
				if (damageMap.size() > 1 )
					continue;
			}

			// If their damage is above the placeholder value, they become the
			// new 'placeholder'.
			if (entry.getValue().getDamage() > damage) {
				damage = entry.getValue().getDamage();
				killer = Optional.of(entry.getKey());
			}
		}

		// Clear the damage map if needed.
		if (clearMap)
			clearDamageCache();

		// Return the killer placeholder.
		return killer;
	}

	public Map<Agent, TotalTrackedDamage> getDamageMap() {
		return damageMap;
	}

	public void clearDamages(T actor) {
		damages.clear();
		hits.clear();
		if(actor.isAlive()) {
			clearDamageCache();
		}
	}

	public void clearDamageCache() {
		damageMap.clear();
		firstDamageDealer = null;
	}

	/**
	 * Aldus wiki (https://oldschool.runescape.wiki/w/Ring_of_recoil),
	 *
	 * "Recoil damage will now always target the NPC that caused the initial damage.
	 * Previously, when under attack from multiple opponents,
	 * the ring would recoil any damage received to a random attacker,
	 * often the attacker that most recently damaged the player."
	 *
	 * @return an optional agent that will receive any recoil damage.
	 */
	public Optional<Agent> findRecoilTarget(){
		if(damageMap.keySet().stream().anyMatch(Agent::isPlayer)){
			return Optional.empty();
		}
		return Optional.ofNullable(firstDamageDealer);
	}
}
