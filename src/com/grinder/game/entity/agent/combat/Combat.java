package com.grinder.game.entity.agent.combat;

import com.grinder.game.GameConstants;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.minigame.pestcontrol.PestControl;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.AgentUtil;
import com.grinder.game.entity.agent.combat.attack.AttackContext;
import com.grinder.game.entity.agent.combat.attack.AttackStrategy;
import com.grinder.game.entity.agent.combat.attack.AttackStyle;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.special.melee.GraniteMaulSpecialAttack;
import com.grinder.game.entity.agent.combat.attack.special.melee.VestaSpearSpecialAttack;
import com.grinder.game.entity.agent.combat.attack.weapon.Weapon;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponFightType;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface;
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpell;
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Ammunition;
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeapon;
import com.grinder.game.entity.agent.combat.event.ApplicableCombatEvent;
import com.grinder.game.entity.agent.combat.event.CombatEvent;
import com.grinder.game.entity.agent.combat.event.CombatEventListener;
import com.grinder.game.entity.agent.combat.event.CombatState;
import com.grinder.game.entity.agent.combat.event.impl.*;
import com.grinder.game.entity.agent.combat.hit.Hit;
import com.grinder.game.entity.agent.combat.hit.HitController;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageTransform;
import com.grinder.game.entity.agent.combat.misc.CombatPrayer;
import com.grinder.game.entity.agent.movement.Motion;
import com.grinder.game.entity.agent.movement.NPCMovementCoordinator;
import com.grinder.game.entity.agent.movement.task.WalkToAction;
import com.grinder.game.entity.agent.movement.task.impl.FollowAgentTask;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.Monster;
import com.grinder.game.entity.agent.npc.monster.boss.Boss;
import com.grinder.game.entity.agent.npc.monster.boss.impl.vorkath.VorkathBoss;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.CombatActions;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.task.TaskManager;
import com.grinder.util.*;
import com.grinder.util.debug.DebugListener;
import com.grinder.util.time.SecondsTimer;
import com.grinder.util.timing.Timer;
import com.grinder.util.timing.TimerKey;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface.HALBERD;

/**
 * Represents a manager and controller for combat actions
 * of an {@link T agent}.
 *
 * @author Swiffy
 * @author Stan van der Bend
 */
public abstract class Combat<T extends Agent> extends HitController<T> implements DebugListener {

    private static final int CYCLES_TILL_ATTACK_COOLDOWN = Math.toIntExact(TimeUnit.SECONDS.toMillis(10) / GameConstants.WORLD_CYCLE_PERIOD);
    private static final int CYCLES_TILL_COMBAT_COOLDOWN = Math.toIntExact(TimeUnit.SECONDS.toMillis(15) / GameConstants.WORLD_CYCLE_PERIOD);
    private static final int MAX_DISTANCE_TO_POTENTIAL_TARGET = 40;
    private static final Predicate<Integer> IS_IN_TARGET_ABLE_DISTANCE = distance -> distance < MAX_DISTANCE_TO_POTENTIAL_TARGET;

    protected final T actor;
    private final CombatValidator<T> combatEvaluator = new CombatValidator<>();
    private final List<CombatEventListener> listeners = new ArrayList<>();
    protected Map<Agent, TreeMap<Long, Integer>> trackedDamage = new HashMap<>();

    Agent target;
    Agent opponent;
    Agent lastAttacked;

    public boolean negateAggression = false;

    private boolean trackIncomingDamages = false;
    private boolean retaliatedAttack = false;
    protected AttackStrategy attackStrategy;

    SecondsTimer poisonImmunityTimer = new SecondsTimer();
    SecondsTimer venomImmunityTimer = new SecondsTimer();
    SecondsTimer aggressivityTimer = new SecondsTimer();
    AtomicInteger successAttackCounter = new AtomicInteger();
    AtomicInteger failedAttackCounter = new AtomicInteger();

    private CombatSpell castSpell, autoCastSpell, previousCast;
    private RangedWeapon rangedWeapon;
    private Ammunition ammunition;

    private Weapon weapon;
    private WeaponFightType fightType = WeaponFightType.UNARMED_KICK;

    private long lastAttackNanos = -1;

    private boolean autoRetaliate;

    private long lastAttackTime = 0;

    /**
     * May be set to true to cancel the next attack,
     * must be set to true before {@link CombatState#STARTING_ATTACK} in order to have an effect.
     */
    private boolean cancelNextAttack;

    /**
     * Create a new {@link Combat} controller for the argued {@link Agent}.
     *
     * @param actor the {@link Agent} to control combat for.
     */
    public Combat(final T actor) {
        this.actor = actor;
        subscribe(event -> {
            if (event instanceof ApplicableCombatEvent) {
                final ApplicableCombatEvent combatEvent = (ApplicableCombatEvent) event;
                if (combatEvent.isApplicableTo(actor)) {
                    combatEvent.applyTo(actor);
                }
            }
            if (event.getClass().isEnum())
                actor.onStateChange(event.toString());
            CombatActions.INSTANCE.handleEvent(this, event);
            return false;
        });
    }

    /**
     * Check if this {@link #actor} is in range of the argued target for combat.
     *
     * @param target         the {@link Agent} that may or may not be in range.
     * @param triggerActions {@code true} if this method may change the state of combat.
     * @return {@code true} if the {@link #actor} is in reach of the target.
     */
    public abstract boolean isInReachForAttack(final Agent target, final boolean triggerActions);

    /**
     * Subscribe a {@link CombatEventListener} to this combat controller.
     *
     * @param eventListener the {@link CombatEventListener} to add to {@link #listeners}.
     */
    public void subscribe(@NotNull CombatEventListener eventListener) {
        listeners.add(eventListener);
    }

    /**
     * Notify all {@link CombatEventListener combat listeners}
     * of the argued {@link CombatEvent}.
     *
     * @param event the {@link CombatEvent} to notify the listeners of.
     */
    public void submit(CombatEvent event) {
        notify(event);
    }

    /**
     * Notify all {@link CombatEventListener combat listeners}
     * of the argued {@link CombatEvent}.
     *
     * @param event the {@link CombatEvent} to notify the listeners of.
     */
    void notify(CombatEvent event) {
        listeners.removeIf(combatEventListener -> combatEventListener.on(event));
    }

    /**
     * Attempts to engage in combat with the argued {@link Agent}
     * given that the agent is a valid target.
     *
     * @param target the {@link Agent} targeted by the {@link #actor}.
     */
    public void initiateCombat(final Agent target) {
        if (this.target != target) {
            failedAttackCounter.set(0);
            successAttackCounter.set(0);
            lastAttackNanos = -1;
        }

        determineAndSetStrategy();
        //if (canAttack(target)) {
            target(target);
       // }/* else {
       //     failedAttackCounter.incrementAndGet();
       // }*/
    }

    /**
     * Sets the argued {@link Agent} as the {@link #target}
     * of this combat instance. This also starts a {@link FollowAgentTask}
     * that focuses upon the target.
     *
     * @param agent the {@link Agent} to be targeted.
     */
    public void target(final Agent agent) {
        target = agent;
        actor.setEntityInteraction(target);
        actor.getMotion().start(new FollowAgentTask(actor, target, false, true), false);
        notify(CombatState.LOCKED_TARGET);
    }

    /**
     * Attempts to engage in combat with the argued {@link Agent}
     * given that the agent is a valid target.
     *
     * @param target        the {@link Agent} targeted by the {@link #actor}.
     * @param movement      should the {@link #actor} trace to the the target
     *                      given that the target is a valid opponent.
     * @param finalizedTask a task to execute if the {@link #actor} has
     *                      reached the target given that the target was
     *                      a valid opponent. Or {@code null} if nothing special
     *                      should happen if the target is reached.
     */
    public void initiateCombat(final Agent target, final boolean movement, final Executable finalizedTask) {
        if (hasTarget())
            return;

        if (movement) {
            traceTo(target, finalizedTask);
        } else {
            initiateCombat(target);
        }
    }

    /**
     * Attempts to engage in combat with the argued {@link Agent}.
     *
     * @param target   the {@link Agent} targeted by the {@link #actor}.
     * @param movement should the {@link #actor} trace to the the target
     *                 given that the target is a valid opponent.
     */
    public void initiateCombat(final Agent target, final boolean movement) {
        initiateCombat(target, movement, null);
    }

    private void traceTo(final Agent target, final Executable finalizedTask) {
        int distance = requiredAttackDistance();
        actor.setEntityInteraction(target);
        ArrayList<WalkToAction.Policy> polices = new ArrayList<>();
        polices.add(WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES);
        polices.add(WalkToAction.Policy.NO_RESET_ENTITY_INTERACTION_ON_EXECUTION);
        polices.add(WalkToAction.Policy.EXECUTE_ON_LINE_OF_SIGHT);
        if (distance > 1) {
            polices.add(WalkToAction.Policy.EXECUTE_WHEN_IN_DISTANCE);
        }
        if (actor instanceof Boss || distance > 1) {
            // This is a quick fix; it prevents npcs from not being able to attack players that stand under spawn and don't move
            // The "real" solution would be to store polices per-npc and update them when attack type changes
            polices.add(WalkToAction.Policy.ALLOW_UNDER);
        }
        actor.getMotion().start(new WalkToAction(actor, target, distance, 0, () -> {
            //actor.setPositionToFace(target.getPosition());
            initiateCombat(target);
            if (finalizedTask != null) {
                finalizedTask.execute();
            }
        }, polices.toArray(new WalkToAction.Policy[polices.size()])), false);
    }

    /**
     * Determine the {@link AttackStrategy} used by this {@link #actor}.
     *
     * @return an {@link AttackStrategy} used during combat.
     */
    public abstract AttackStrategy<? extends Agent> determineStrategy();

    public void sequenceHitsAndDamages(){
        sequenceHits(actor);
        sequenceDamages(actor);
    }

    /**
     * Sequences this {@link Combat} instance for the {@link #actor} every game tick.
     * <p>
     * First sequence pending hits and damages from {@link HitController}.
     * Then check whether the next combat turn should be skipped.
     * Then finally sequence the actual combat mechanics.
     * </p>
     */
    public void sequence() {

        if (target != null) {

            if (!skipNextCombatTurn())
                sequenceCombatTurn(false);

        } else if (opponent != null) {

            if (cooledDownFromAttack()) {
                String message = "cooled down from attack -> ";
                boolean isAttackingOpponent = !isAttacking(opponent);
                if (isAttackingOpponent)
                    message += "has opponent ";
                boolean isBeingAttackedByOpponent = opponent.getCombat().isAttacking(actor);
                if (isBeingAttackedByOpponent) {
                    if (isAttackingOpponent)
                        message += "and ";
                    message += "is opponent";
                }
                message += "> set opponent to null";
                opponent = null;
                actor.debug(message);
            } else
                actor.debug("I still have an opponent but no target and not yet cooled down!");
        }

        notify(CombatState.SEQUENCED_COMBAT);
    }

    /**
     * Prepares an attack and launches it if the {@link #target} is not null.
     * <p>
     * The attack used is described by the {@link #attackStrategy}.
     * If the {@link #target} is out of reach of the {@link #actor},
     * the {@link #actor} will attempt to force move towards the {@link #target}.
     * </p>
     *
     * @param ignoreAttackDelay if true the attack will be instantly sequenced.
     */
    public void sequenceCombatTurn(final boolean ignoreAttackDelay) {
        actor.debug("sequenceCombatTurn -> "+target);
        actor.setEntityInteraction(target);
        determineAndSetStrategy();
        updateTrackedDamages();

        if (combatEvaluator.evaluateTarget(this))
            sequenceAttack(ignoreAttackDelay);

        notify(CombatState.SEQUENCED_ATTACK);
    }

    /**
     * Clears the {@link #opponent} given that the argued {@link Agent}
     * is currently the {@link #opponent}.
     *
     * @param other the {@link Agent} to potentially reset as opponent.
     */
    public void clearOpponent(Agent other) {
        if (other == opponent)
            clearOpponent();
    }

    /**
     * Sets the {@link #opponent} to {@code null}.
     */
    public void clearOpponent() {
        opponent = null;
    }

    /**
     * Re-starts the {@link TimerKey#COMBAT_COOLDOWN} timer to end in {@link Combat#CYCLES_TILL_COMBAT_COOLDOWN}.
     */
    public void resetCombatCoolDown() {
        actor.getTimerRepository().register(TimerKey.COMBAT_COOLDOWN, CYCLES_TILL_COMBAT_COOLDOWN);
        actor.debug("Reset combat cooldown");
    }

    /**
     * Re-starts the {@link TimerKey#ATTACK_COOLDOWN} timer to end in {@link Combat#CYCLES_TILL_ATTACK_COOLDOWN}.
     */
    public void resetAttackCoolDown() {
        actor.getTimerRepository().register(TimerKey.ATTACK_COOLDOWN, CYCLES_TILL_ATTACK_COOLDOWN);
        actor.debug("Reset attack cooldown");
    }

    /**
     * Extends or starts the {@link TimerKey#COMBAT_NEXT_ATTACK} timer with the specified delay.
     *
     * @param extraDelay the amount of seconds the timer will be extended by.
     */
    public void extendNextAttackDelay(final int extraDelay) {
        actor.getTimerRepository().replaceIfLongerOrRegister(TimerKey.COMBAT_NEXT_ATTACK, extraDelay);
        actor.debug("Extend next attack delay by " + extraDelay + " ticks.");
    }

    public Timer getNextAttackTimer(boolean resetIfPresent) {

        if (resetIfPresent && actor.getTimerRepository().has(TimerKey.COMBAT_NEXT_ATTACK))
            resetNextAttackTimer();

        return actor.getTimerRepository().timers().get(TimerKey.COMBAT_NEXT_ATTACK);
    }

    /**
     * Resets the {@link TimerKey#COMBAT_NEXT_ATTACK} to the duration defined in the {@link #attackStrategy}.
     */
    public void resetNextAttackTimer() {
        final int attackDuration = attackStrategy.duration(actor);
        actor.debug("reset next attack delay -> strategy = " + attackStrategy.getClass().getSimpleName());
        setNextAttackDelay(attackDuration);
    }

    /**
     * Re-starts the {@link TimerKey#COMBAT_NEXT_ATTACK} timer to end in the specified delay.
     *
     * @param delay the amount of seconds the timer will take.
     */
    public void setNextAttackDelay(final int delay) {
        actor.getTimerRepository().register(TimerKey.COMBAT_NEXT_ATTACK, delay);
    }

    @Override
    public String[] lines() {
        return new String[]{
                "Name: " + ((actor instanceof NPC) ? actor.getAsNpc().fetchDefinition().getName() : actor.getAsPlayer().getUsername()),
                "Target: " + ((target == null) ? "NONE" : ((target instanceof NPC) ? target.getAsNpc().fetchDefinition().getName() : target.getAsPlayer().getUsername())),
                "Opponent: " + ((opponent == null) ? "NONE" : ((opponent instanceof NPC) ? opponent.getAsNpc().fetchDefinition().getName() : opponent.getAsPlayer().getUsername())),
                "Type: " + ((attackStrategy == null) ? "NONE" : attackStrategy.type()),
                "Timer: A[" + actor.getTimerRepository().left(TimerKey.COMBAT_NEXT_ATTACK) + "] " + ((this instanceof NPCCombat) ? "R[" + ((NPCCombat) this).getFailedToReachCount().get() + "]" : ""),
                "Reset: A[" + actor.getTimerRepository().left(TimerKey.ATTACK_COOLDOWN) + "], C[" + actor.getTimerRepository().left(TimerKey.COMBAT_COOLDOWN) + "]",
                "Hits: " + hits.size() + " D[" + secondsPastLastHitDealt() + "], R[" + secondsPastLastHitReceived() + "]",
                "Damages: " + damages.size(),
        };
    }

    /**
     * Resets this {@link Combat} instance for the {@link #actor}.
     *
     * @param resetOpponent this flag determines whether the {@link #opponent} should be set to null.
     */
    public void reset(final boolean resetOpponent) {
        reset(resetOpponent, false);
    }

    public void reset(final boolean resetOpponent, final boolean movement) {

        actor.debug("reset combat");

        if (resetOpponent) {
            clearOpponent();
            actor.getCombat().clearDamageCache();
        }

        resetTarget();

        if (actor instanceof NPC){
            NPCMovementCoordinator movementCoordinator = ((NPC) actor).getMovementCoordinator();
            if (movementCoordinator.getGoalState() == NPCMovementCoordinator.GoalState.IN_COMBAT)
                movementCoordinator.reset();
        }

        if (movement) {
            actor.getMotion().clearSteps();
            actor.getMotion().cancelTask();
            actor.ifPlayer(player -> player.getPacketSender().sendMinimapFlagRemoval());
        }
    }

    public void resetTarget() {
        resetCombatWith(target);
        target = null;
    }

    public boolean resetCombatWith(final Agent agent) {

        final Optional<Agent> optionalTarget = Optional.ofNullable(agent);

        optionalTarget
                .map(Agent::getCombat)
                .filter(combat -> combat.isBeingAttackedBy(actor))
                .ifPresent(Combat::clearOpponent);

        if (agent == target)
            target = null;
        if (agent == lastAttacked)
            lastAttacked = null;

        final Motion<?> motion = actor.getMotion();
        if (optionalTarget.filter(motion::isFollowing).isPresent()) {
            motion.resetTargetFollowing();
            motion.movementTask().ifPresent(task -> {
                if(task instanceof FollowAgentTask){
                    FollowAgentTask<?> followAgentTask = (FollowAgentTask<?>) task;
                    followAgentTask.stop();
                }
            });
        }

        failedAttackCounter.set(0);

        return optionalTarget.isPresent();
    }

    /**
     * Check whether the specified {@link Agent} is set as a {@link #opponent}.
     *
     * @param agent the {@link Agent} to check.
     * @return {@code true} if the agent is an opponent;
     * {@code false} otherwise
     */
    public boolean isBeingAttackedBy(final Agent agent) {
        return opponent == agent;
    }

    public void onHitApplied(Hit hit) {


        final Agent attacker = hit.getAttacker();

        opponent = attacker;

        tryPerformingBlockAnimation(actor, hit, actor.getBlockAnim());

        if (!hit.negateDamages())
            queue(hit.getDamages());

        applyHitEffects(actor, hit);

        boolean shouldRetaliate = true;
        if (actor.isPlayer()) {
            if (actor.getAsPlayer().BLOCK_ALL_BUT_TALKING || actor.getAsPlayer().isShouldNoClip()) {
                shouldRetaliate = false;
            }
            if (EntityExtKt.getBoolean(actor, Attribute.STALL_HITS, false)) {// Agility stall hits
                shouldRetaliate = false;
            }
        }

        if (shouldRetaliate) {
            retaliate(attacker);
        }

        cacheDamage(attacker, hit.getTotalDamage());

        final long currentTime = System.currentTimeMillis();
        lastReceivedHitTimer = currentTime;
        attacker.getCombat().lastDealtHitTimer = currentTime;

        final int damage = hit.getTotalDamage();
        if (trackIncomingDamages) {
            trackedDamage.putIfAbsent(attacker, new TreeMap<>());
            trackedDamage.get(attacker).compute(currentTime, (key, value) -> value == null ? damage : value + damage);
        }


        attacker.ifPlayer(player -> {
            if (damage > 0) {
                player.getCombat().handleItemEffectAttack(attacker, hit);
            }
        });

        actor.ifPlayer(player -> {

            if (player.isTeleporting() && player.getTeleportingType() == TeleportType.HOME)
                player.stopTeleporting();

            if (player.getRights() != PlayerRights.DEVELOPER && !PlayerUtil.isDeveloper(player))
                player.getPacketSender().sendInterfaceRemoval();

            actor.getTimerRepository().register(TimerKey.COMBAT_COOLDOWN, 6);

            if (hit.isAccurate())
                player.getCombat().handlePrayerEffect(damage);

            if (damage > 0) {
                player.getCombat().handleItemEffectDefend(attacker, hit);
            }
        });

        final int recoilDamage = hit.getTotalRecoilDamage();
        if (recoilDamage > 0) {
            final Agent recoilTarget;
            if (AreaManager.allInMulti(actor, attacker)) {
                recoilTarget = findRecoilTarget().orElse(attacker);
            } else
                recoilTarget = attacker;

            Damage d = Damage.create(recoilDamage);
            d.flagAgents(hit.getAttacker(), hit.getTarget());
            recoilTarget.getCombat().queue(d);
        }
    }

    @Override
    public void queue(Hit hit) {

        super.queue(hit);

        notify(new IncomingHitQueued(hit));
        hit.getAttacker().getCombat().submit(new OutgoingHitQueued(hit));
    }

    @Override
    protected void apply(Hit hit){

        final Agent actor = hit.getAttacker();
        final Agent target = hit.getTarget();

        if(!target.isAlive() || !actor.isAlive())
            return;

        if(target.isUntargetable() || target.hasPendingTeleportUpdate())
            return;

        notify(new IncomingHitApplied(hit));
        actor.getCombat().submit(new OutgoingHitApplied(hit));

        onHitApplied(hit);
        actor.ifPlayer(player -> {
            if (PestControl.playerIsInPestControl(player)) {
                player.pestControlTotalHit += hit.getTotalDamage();
                player.pestControlZeal += (hit.getTotalDamage() * 2);

                if (player.pestControlZeal > 1000) {
                    player.pestControlZeal = 1000;
                }
            }
        });
    }

    private void updateTrackedDamages() {

        if (!trackIncomingDamages)
            return;

        final HashSet<Agent> invalidOpponents = new HashSet<>();

        for (final Map.Entry<Agent, TreeMap<Long, Integer>> entry : trackedDamage.entrySet()) {

            final Agent opponent = entry.getKey();

            if (!opponent.isActive() || !isWithinDistance(opponent, Player.NORMAL_VIEW_DISTANCE))
                invalidOpponents.add(opponent);
        }

        invalidOpponents.forEach(trackedDamage::remove);
    }

    /**
     * Handles auto-retaliation of the {@link #actor} when attacked.
     *
     * @param attacker the {@link Agent} attacking the {@link #actor}.
     */
    public void retaliate(final Agent attacker) {
        if (EntityExtKt.getBoolean(actor, Attribute.STALL_HITS, false))
            return;

        if (actor.isPlayer()) {
            if (actor.getAsPlayer().BLOCK_ALL_BUT_TALKING || actor.getAsPlayer().isShouldNoClip()) {
                return;
            }

            if(actor.getAsPlayer().getAgility().getObstacle() == null) {
                SkillUtil.stopSkillable(actor.getAsPlayer());
            }
        }

        if (!hasTarget()) {
            if (retaliateAutomatically()) {
                retaliatedAttack = true;
                TaskManager.submit(1, () -> initiateCombat(attacker));
            }
        }
    }

    /**
     * Check whether the {@link #target} is set.
     *
     * @return {@code true} if the target is not null,
     * {@code false} otherwise
     */
    public boolean hasTarget() {
        return target != null;
    }

    public boolean retaliateAutomatically() {
        return autoRetaliate;
    }

    public boolean isRetaliatedAttack() {
        return retaliatedAttack;
    }

    /**
     * Given that the {@link #actor} can reach the {@link #target} and is ready to attack,
     * <p>
     * the attack is performed based on the {@link #attackStrategy} used.
     *
     * @param ignoreAttackDelay should the attack delay be ignored?
     */
    private void sequenceAttack(final boolean ignoreAttackDelay) {

        actor.debug("sequenceAttack");
        if (readyToAttack(ignoreAttackDelay)) {

            if (canAttack(target)) {
                preAttack();
                if (!cancelNextAttack)
                    attack(target);
                postAttack();
                cancelNextAttack = false;
                failedAttackCounter.set(0);
                successAttackCounter.incrementAndGet();
                lastAttackNanos = System.nanoTime();
            } else {
                failedAttackCounter.incrementAndGet();
            }
        }
    }

    /**
     * Sequence the {@link #attackStrategy} and queues any outgoing {@link Hit}.
     */
    public void attack(Agent target) {

        actor.debug("attack!");

        if (target != null && target instanceof NPC) { // Handle combat dummies
            if (target.getAsNpc().getId() == NpcID.COMBAT_DUMMY || target.getAsNpc().getId() == NpcID.UNDEAD_COMBAT_DUMMY) {
                attackStrategy.sequence(actor, target);
                Optional.ofNullable(attackStrategy.createHits(actor, target))
                        .map(Stream::of)
                        .ifPresent(pendingHitStream -> pendingHitStream.forEach(this::queueOutgoingHit));
                attackStrategy.postHitAction(actor, target);
                return;
            }
        }

        target.getCombat().setOpponent(actor);
        lastAttacked = target;

        attackStrategy.sequence(actor, target);

        Optional.ofNullable(attackStrategy.createHits(actor, target))
                .map(Stream::of)
                .ifPresent(pendingHitStream -> pendingHitStream.forEach(this::queueOutgoingHit));

        if (retaliatedAttack)
            retaliatedAttack = false;

        attackStrategy.postHitAction(actor, target);

        if (target instanceof Player) {
            boolean shouldRetaliate = true;
            if (target.getAsPlayer().BLOCK_ALL_BUT_TALKING || target.getAsPlayer().isShouldNoClip()) {
                shouldRetaliate = false;
            }
            if (EntityExtKt.getBoolean(target, Attribute.STALL_HITS, false)) {// Agility stall hits
                shouldRetaliate = false;
            }

            if (shouldRetaliate) {
                target.getCombat().retaliate(actor);
            }
        }

        target.getCombat().resetAttackCoolDown();
    }

    /**
     * Faces the {@link #target} and resets the {@link TimerKey#COMBAT_COOLDOWN} timer.
     */
    void preAttack() {

        /*
         * TODO: the reason why this has to be done is because actor.resetEntityInteraction is invoked in
         *       NPCMOvementCoordinator because my (Stan) code was removed that handle movement coordination
         *       when a NPC is in combat.
         */
        actor.setEntityInteraction(target);

        attackStrategy.animate(actor);

        notify(CombatState.STARTING_ATTACK);
    }

    /**
     * Resets the {@link TimerKey#ATTACK_COOLDOWN} and handles retaliation.
     */
    public void postAttack() {

        resetAttackCoolDown();

        if (!disregardAttackDelay())
            resetNextAttackTimer();

        notify(CombatState.FINISHED_ATTACK);
    }

    /**
     * Check whether the {@link #actor} is still actively engaged in combat
     * or is cooling down from a recent combat session.
     *
     * @return {@code true} if the actor is in active combat or is cooling down from combat;
     * {@code false} otherwise
     */
    public boolean isInCombat() {
        return !cooledDownFromCombat();
    }

    /**
     * Check whether the {@link #actor} is still actively engaged in combat.
     *
     * @return {@code true} if the actor is being attacked or is attacking a target;
     * {@code false} otherwise
     */
    public boolean isUnderAttack() {
        return !cooledDownFromCombat();
    }

    /**
     * Check whether the specified {@link Agent} is cooled down from the last combat turn.
     *
     * @return {@code true} if the agent has no active {@link TimerKey#COMBAT_COOLDOWN} timer.
     * {@code false} otherwise
     */
    public boolean cooledDownFromCombat() {
        return !actor.getTimerRepository().has(TimerKey.COMBAT_COOLDOWN);
    }

    /**
     * Check whether the specified {@link Agent} is set as a {@link #target} or {@link #opponent}.
     *
     * @return {@code true} if the agent is a target or opponent;
     * {@code false} otherwise
     */
    public boolean isBeingAttacked() {
        return opponent != null;
    }

    public boolean isAttacking(final Agent agent) {
        return agent.getCombat().isBeingAttackedBy(actor);
    }

    public boolean notInCombatWith(final Agent agent) {
        return !isInCombatWith(agent);
    }

    /**
     * Check whether the specified {@link Agent} is set as a {@link #target} or {@link #opponent}.
     *
     * @param agent the {@link Agent} to check.
     * @return {@code true} if the agent is a target or opponent;
     * {@code false} otherwise
     */
    public boolean isInCombatWith(final Agent agent) {
        return hasTargeted(agent) || isBeingAttackedBy(agent);
    }

    /**
     * Check whether the specified {@link Agent} is set as {@link #target}.
     *
     * @param agent the {@link Agent} to check.
     * @return {@code true} if the target equals the specified agent;
     * {@code false} otherwise
     */
    public boolean hasTargeted(final Agent agent) {
        return target == agent;
    }

    public boolean isAttacking() {
        return target != null;
    }

    /**
     * Check whether the specified {@link Agent} is cooled down from the last performed attack.
     *
     * @return {@code true} if the agent has no active {@link TimerKey#ATTACK_COOLDOWN} timer.
     * {@code false} otherwise
     */
    public boolean cooledDownFromAttack() {
        return !actor.getTimerRepository().has(TimerKey.ATTACK_COOLDOWN);
    }

    public int requiredAttackDistance() {
        return determineStrategy().requiredDistance(actor);
    }

    public boolean isMeleeAttack() {
        return determineStrategy().type() == AttackType.MELEE;
    }

    public boolean isRangeAttack() {
        return determineStrategy().type() == AttackType.RANGED;
    }

    public boolean isMagicAttack() {
        return determineStrategy().type() == AttackType.MAGIC;
    }

    private void determineAndSetStrategy() {
        attackStrategy = determineStrategy();
    }

    private boolean disregardAttackDelay() {
        return (attackStrategy instanceof GraniteMaulSpecialAttack);
    }

    private boolean canEngageInCombat() {
        return Optional.ofNullable(actor)
                .filter(T::isActive)
                .filter(T::isAlive)
                .filter(T::canBeTargeted)
                .isPresent();
    }

    private boolean isAlreadyUnderAttackButNotBy(Agent agent) {
        return isBeingAttacked() && !isBeingAttackedBy(agent);
    }

    private boolean opponentIsAliveAndActive() {
        return opponent.isActive() && (opponent.isAlive() || hasNoPendingHitsButFrom(opponent));
    }

    public boolean readyToAttack(final boolean skipAttackDelay) {

        final boolean disregardDelayAndDontResetTimer = disregardAttackDelay();
        final boolean attackTimerCompleted = !actor.getTimerRepository().has(TimerKey.COMBAT_NEXT_ATTACK);

        return skipAttackDelay || disregardDelayAndDontResetTimer || attackTimerCompleted;
    }

    public boolean isWithinDistance(final Agent target, final int requiredDistance) {
        final int distance = DistanceUtil.calculateDistance(target, actor);
        return distance <= requiredDistance;
    }

    public boolean isWithinAttackDistance(final Agent target) {
        if (actor.isUnder(target)) {
            actor.debug(target+" is under me!");
            return false;
        }
        int distance = requiredAttackDistance();
        if (distance == 1 && isMeleeAttack()) {
            return target.interactTarget.reached(actor);
        }
        final boolean lineInSightCheck = LineOfSight.withinSight(actor, target, distance);
        if (!lineInSightCheck)
            actor.debug("Line in sight check failed for "+target+", req distance = "+distance);
        return lineInSightCheck;
    }

    /**
     * Check if this {@link #actor} can attack the provided target {@link Agent}.
     *
     * @param target the {@link Agent} that is targeted
     * @return {@code true} if the actor can attack the target
     * {@code false} if not
     */
    protected boolean canAttack(final Agent target) {
        return canAttack(target, false);
    }

    /**
     * Check if this {@link #actor} can attack the provided target {@link Agent}.
     *
     * @param target        the {@link Agent} that is targeted.
     * @param ignoreActions if {@code true} no states are changed
     *                      if {@code false} states may be changed
     * @return {@code true} if the actor can attack the target
     * {@code false} if not
     */
    protected boolean canAttack(final Agent target, boolean ignoreActions) {
        return canAttackWith(target, determineStrategy(), ignoreActions);
    }

    /**
     * Check if this {@link #actor} can attack the provided target {@link Agent}
     * with the provided {@link AttackStrategy}.
     *
     * @param target         the {@link Agent} that is targeted
     * @param attackStrategy the {@link AttackStrategy} used
     * @param ignoreActions  if {@code true} no states are changed
     *                       if {@code false} states may be changed
     * @return {@code true} if the actor can attack the target
     * {@code false} if not
     * @apiNote actions should only be ignored during combat sequencing,
     * if not then states may be changed,
     * potentially causing unexpected behaviour.
     */
    public boolean canAttackWith(final Agent target, final AttackStrategy attackStrategy, final boolean ignoreActions) {

        final Combat<?> targetCombat = target.getCombat();
        if (!targetCombat.canBeAttackedBy(actor, ignoreActions)) {
            if (!ignoreActions)
                reset(false);
            return false;
        }

        final boolean actorInMultiArea = AreaManager.inMulti(actor);
        final boolean targetInMultiArea = AreaManager.inMulti(target);
        final boolean notInMultiArea = !actorInMultiArea && !targetInMultiArea;

        if (notInMultiArea) {

            if (isAlreadyUnderAttackButNotBy(target)) {
                if (target != null && target instanceof NPC) { // Handle combat dummies
                    if (target.getAsNpc().getId() == NpcID.COMBAT_DUMMY || target.getAsNpc().getId() == NpcID.UNDEAD_COMBAT_DUMMY) {
                        return true;
                    }
                }
                if (opponentIsAliveAndActive()) {
                    if (!ignoreActions) {
                        notify(new ActorAlreadyUnderAttack(target));
                        reset(false);
                    }
                    return false;
                }
            }

            if (targetCombat.isAlreadyUnderAttackButNotBy(actor)) {
                if (targetCombat.opponentIsAliveAndActive()) {
                    if (!ignoreActions) {
                        if (target != null && target instanceof NPC) { // Handle combat dummies
                            if (target.getAsNpc().getId() == NpcID.COMBAT_DUMMY || target.getAsNpc().getId() == NpcID.UNDEAD_COMBAT_DUMMY) {
                                return true;
                            }
                        }
                        notify(new TargetAlreadyUnderAttack(target));
                        reset(false);
                    }
                    return false;
                }
            }
        }

        if (!AreaManager.canAttack(actor, target)) {
            if (!ignoreActions) {
                if (target instanceof Player)
                    actor.messageIfPlayer("You cannot attack players here.");
                reset(false);
            }
            return false;
        }

        if (target.getTimerRepository().has(TimerKey.ATTACK_IMMUNITY)) {
            if (!ignoreActions) {
                notify(new TargetIsImmuneToAttacks());
                reset(false);
            }
            return false;
        }

        if (!attackStrategy.canAttack(actor, target)) {
            if (!ignoreActions) {
                reset(false);
            }
            return false;
        }

        return true;
    }

    /**
     * Check whether this {@link #actor} can be attacked by the provided {@link Agent}.
     *
     * @param attacker the {@link Agent} attacking this actor
     * @param ignoreActions
     * @return {@code true} if the attacker can attack the {@link #actor}
     * {@code false} if not
     */
    public boolean canBeAttackedBy(final Agent attacker, boolean ignoreActions) {

        final Combat<? extends Agent> attackerCombat = attacker.getCombat();
        final boolean actorCanEngageInCombat = canEngageInCombat();
        final boolean targetCanEngageInCombat = attackerCombat.canEngageInCombat();

        if (!actorCanEngageInCombat || !targetCanEngageInCombat) {
            if (!ignoreActions)
                attackerCombat.setOpponent(null);
            if (actorCanEngageInCombat)
                attacker.debug("Can not engage in combat");
            if (targetCanEngageInCombat)
                attacker.debug("Target can not engage in combat");
            return false;
        }

        if (!VestaSpearSpecialAttack.Companion.canBeAttackedBy(actor, attackerCombat))
            return false;

        final Optional<Agent> optionalTargetOfAttacker = Optional.ofNullable(attackerCombat.getTarget());
        final Position attackerPosition = attacker.getPosition();
        final Position actorPosition = actor.getPosition();

        if (optionalTargetOfAttacker.isPresent()) {
            return optionalTargetOfAttacker
                    .map(Agent::getPosition)
                    .filter(targetPosition -> targetPosition.getZ() == actorPosition.getZ())
                    .map(attackerPosition::getDistance)
                    .filter(IS_IN_TARGET_ABLE_DISTANCE)
                    .isPresent();
        }

        return true;
    }

    public boolean canBeReachedInLongRange(final Agent attacker, int requiredDistance, final boolean triggerActions) {
        boolean skipClipping = false;

        if (attacker instanceof Monster) {
            if (((Monster) attacker).skipProjectileClipping()) {
                skipClipping = true;
            }
        }
        if (actor instanceof Monster) {
            if (((Monster) actor).skipProjectileClipping()) {
                skipClipping = true;
            }
        }

        final boolean largeEntity = actor.getSize() > 1;

        if (attacker.getMotion().isRunning()) {
            int extra = actor.getMotion().isRunning() ? 3 : actor.getMotion().isMoving() ? 2 : largeEntity ? 1 : 0;
            requiredDistance += extra;
        } else if (attacker.getMotion().isMoving())
            requiredDistance += largeEntity ? 1 : 0;

        if (!skipClipping) {
            if (!LineOfSight.withinSight(attacker, actor, requiredDistance)) {
                attacker.debug("Could not reach attack location! dis = " + requiredDistance);
                return false;
            }
        }

        return true;
    }

    boolean canBeReachedInCloseQuarter(final Agent attacker, int requiredDistance, final boolean triggerActions) {
        // This is really hacky, should be using the movement queue to check where the player will be instead.
        boolean largeEntity = actor.getSize() > 1;
        if (attacker.getMotion().isRunning()) {
            int extra = actor.getMotion().isRunning() ? 3 : actor.getMotion().isMoving() ? 2 : 1;
            requiredDistance += extra;
        } else if (attacker.getMotion().isMoving())
            requiredDistance += actor.getMotion().isMoving() || largeEntity ? 2 : 1;

        if (!LineOfSight.withinSight(attacker, actor, requiredDistance, checkWalls(attacker))) {
            attacker.debug("Could not reach attack location! req = " + requiredDistance);
            return false;
        }

        Direction attackerDirection = Direction.getDirectionFacingPosition(attacker.getPosition(), actor);
        int targetDistance = DistanceUtil.calculateDistance(attacker, actor);

        if (targetDistance > 1 && requiredDistance > 1 && AgentUtil.isFrozen(attacker)) {
            if (triggerActions)
                attacker.getCombat().resetNextAttackTimer();
            return false;
        }

        if (targetDistance == 1) {
            if (actor.getSize() <= 1) {
                boolean diagonalAttack = Direction.isDiagonalDirection(attackerDirection);

                if (diagonalAttack) {
                    if (AgentUtil.isFrozen(attacker)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines whether the walls should be accounted for the attacker's line of sight.
     *
     * @param attacker The attacker.
     * @return <code>true</code> if walls should be accounted for
     */
    boolean checkWalls(final Agent attacker) {
        if (attacker.getCombat().isMeleeAttack()) {
            if (attacker.getCombat().uses(HALBERD) || actor instanceof VorkathBoss) {
                return false;
            }
            return true;
        }
        return false;
    }

    Damage transformOutgoingDamage(final Agent target, final AttackContext context, final int originalDamage, final Damage initial) {

        if (!context.isIgnorePrayer())
            CombatPrayer.applyProtectivePrayerDamageReduction(actor, target, context.getTypeUsed(), initial);

        // Staff of the dead effect
        if (/*actor.isNpc() && */context.getTypeUsed() == AttackType.MELEE) {
            if (!EntityExtKt.passedTime(target, Attribute.SOTD_SPEC_EFFECT, 60, TimeUnit.SECONDS, false, false)) {
                if (target.isPlayer() && target.getAsPlayer().getEquipment().containsAny(ItemID.STAFF_OF_THE_DEAD, ItemID.STAFF_OF_BALANCE, ItemID.STAFF_OF_LIGHT, ItemID.TOXIC_STAFF_OF_THE_DEAD)) {
                    initial.multiplyDamage(0.50); // 50% reduction
                }
            }
        }
        if (actor instanceof Player) {
            final Player player = (Player) actor;
            processDamageAchievements(initial, player);
        }

        DamageTransform.transformPlayerIncomingHitDamage(actor, target, originalDamage, initial);
        return initial;
    }

    private void processDamageAchievements(Damage damage, Player player) {
        if (player.getCombat().getTarget() != null) {
            if (player.getCombat().getTarget().isNpc()) {
                if (player.getCombat().getTarget().getAsNpc().getId() == NpcID.COMBAT_DUMMY || player.getCombat().getTarget().getAsNpc().getId() == NpcID.UNDEAD_COMBAT_DUMMY) {
                    return;
                }
            }
        }
        AchievementManager.processFor(AchievementType.A_WORLD_OF_PAIN, player);
        if (damage.getValue() > 75) {
            AchievementManager.processFor(AchievementType.DEVASTATING_DAMAGE, player);
            AchievementManager.processFor(AchievementType.EXTREME_DAMAGE, player);
            AchievementManager.processFor(AchievementType.HARD_DAMAGE, player);
        } else if (damage.getValue() > 50) {
            AchievementManager.processFor(AchievementType.EXTREME_DAMAGE, player);
            AchievementManager.processFor(AchievementType.HARD_DAMAGE, player);
        } else if (damage.getValue() > 30)
            AchievementManager.processFor(AchievementType.HARD_DAMAGE, player);
    }

    public boolean uses(WeaponInterface weaponInterface) {
        return weapon != null ? weapon.uses(weaponInterface) : weaponInterface == WeaponInterface.UNARMED;
    }

    boolean skipNextCombatTurn() {
        return actor instanceof Player
                && hasTarget()
                && isWithinAttackDistance(target)
                && (actor.getMotion().isMoving() || actor.getMotion().didSequenceStep());
    }

    public Optional<Agent> findCurrentTarget() {
        return Optional.ofNullable(getTarget());
    }

    public Optional<Agent> findPreferredOpponent() {

        final long timeStamp = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(10L);

        int mostDamage = 0;
        Agent strongestOpponent = null;

        for (final Map.Entry<Agent, TreeMap<Long, Integer>> entry : trackedDamage.entrySet()) {

            final Agent opponent = entry.getKey();

            if (!isInReachAndEligibleForAttack(opponent))
                continue;

            final NavigableMap<Long, Integer> relevantDamages = entry.getValue().tailMap(timeStamp, true);
            final int totalDamage = relevantDamages.values().stream().mapToInt(Integer::intValue).sum();

            if (totalDamage > mostDamage || strongestOpponent == null) {
                mostDamage = totalDamage;
                strongestOpponent = opponent;
            } else if (totalDamage == mostDamage) {
                strongestOpponent = Misc.random(strongestOpponent, opponent);
            }
        }

        return Optional.ofNullable(strongestOpponent);
    }

    protected boolean isInReachAndEligibleForAttack(final Agent target) {
        return canReach(target, false)
                && canAttackWith(target, determineStrategy(), true);
    }

    public boolean canReach(Agent agent, boolean triggerActions) {
        return isWithinAttackDistance(agent);
    }

    public boolean hasSuccessfullyAttackedTarget() {
        return hasTarget() && lastAttackNanos > 0;
    }

    public boolean hasElapsedSinceLastAttack(TimeUnit timeUnit, long duration) {
        return getTimeSinceLastAttack(timeUnit) >= duration;
    }

    public long getTimeSinceLastAttack(TimeUnit timeUnit) {
        return timeUnit.convert(System.nanoTime() - lastAttackNanos, TimeUnit.NANOSECONDS);
    }

    public AttackStyle styleUsed() {
        return fightType.getStyle();
    }

    public T getActor() {
        return actor;
    }

    public Agent getTarget() {
        return target;
    }

    public Agent getOpponent() {
        return opponent;
    }

    public Agent getLastAttacked() {
        return lastAttacked;
    }

    public CombatSpell getSpell() {
        return castSpell == null ? autoCastSpell : castSpell;
    }

    public CombatSpell getCastSpell() {
        return castSpell;
    }

    public CombatSpell getPreviousCast() {
        return previousCast;
    }

    public CombatSpell getAutocastSpell() {
        return autoCastSpell;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public WeaponFightType getFightType() {
        return fightType;
    }

    public RangedWeapon getRangedWeapon() {
        return rangedWeapon;
    }

    public Ammunition getAmmunition() {
        return ammunition;
    }

    public SecondsTimer getPoisonImmunityTimer() {
        return poisonImmunityTimer;
    }

    public SecondsTimer getVenomImmunityTimer() {
        return venomImmunityTimer;
    }

    public SecondsTimer getAggressivityTimer() {
        return aggressivityTimer;
    }

    public AtomicInteger getFailedAttackCounter() {
        return failedAttackCounter;
    }

    public void setTarget(Agent target) {
        this.target = target;
    }

    public void setOpponent(Agent attacker) {
        this.opponent = attacker;
        actor.lastAgentHitBy = attacker;

        long currentTime = System.currentTimeMillis();

        lastAttackTime = currentTime;
    }

    public boolean checkPJTimer()
    {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastAttackTime) <= 11000)
        {
            return true;
        }
        return false;
    }

    public void setCastSpell(CombatSpell castSpell) {
        this.castSpell = castSpell;
    }

    public void setPreviousCast(CombatSpell previousCast) {
        this.previousCast = previousCast;
    }

    public void setAutocastSpell(CombatSpell autoCastSpell) {
        this.autoCastSpell = autoCastSpell;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public void setFightType(WeaponFightType fightType) {
        this.fightType = fightType;
    }

    public void setRangedWeapon(RangedWeapon rangedWeapon) {
        this.rangedWeapon = rangedWeapon;
    }

    public void setAmmunition(Ammunition ammunition) {
        this.ammunition = ammunition;
    }

    public void setAutoRetaliate(boolean autoRetaliate) {
        this.autoRetaliate = autoRetaliate;
    }

    public void setTrackIncomingDamages(boolean trackIncomingDamages) {
        this.trackIncomingDamages = trackIncomingDamages;
    }
    public boolean negateAggression() {
        return negateAggression;
    }

    public void setNegateAggression(boolean negateAggression) {
        this.negateAggression = negateAggression;
    }

    public void setCancelNextAttack(boolean cancelNextAttack) {
        this.cancelNextAttack = cancelNextAttack;
    }
}
