package com.grinder.game.entity.agent.combat;

import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackContext;
import com.grinder.game.entity.agent.combat.attack.AttackStrategy;
import com.grinder.game.entity.agent.combat.attack.strategy.MeleeAttackStrategy;
import com.grinder.game.entity.agent.combat.event.CombatState;
import com.grinder.game.entity.agent.combat.event.impl.ActorAlreadyUnderAttack;
import com.grinder.game.entity.agent.combat.event.impl.TargetAlreadyUnderAttack;
import com.grinder.game.entity.agent.combat.event.impl.TargetIsOutOfReach;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.entity.agent.movement.NPCMotion;
import com.grinder.game.entity.agent.movement.NPCMovementCoordinator;
import com.grinder.game.entity.agent.movement.pathfinding.PathFinder;
import com.grinder.game.entity.agent.movement.task.impl.FollowAgentTask;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.bot.NPCBotHandler;
import com.grinder.game.entity.agent.npc.monster.aggression.MonsterAggressionProcess;
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy;
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatProcess;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;
import com.grinder.util.time.SecondsTimer;
import com.grinder.util.timing.Timer;
import kotlin.random.Random;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.grinder.game.entity.agent.movement.NPCMovementCoordinator.GoalState;

/**
 * This class forms a {@link Combat} implementation for {@link NPC} typed entities.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-03-21
 */
public class NPCCombat extends Combat<NPC> {

    private final AtomicInteger failedToReachCount = new AtomicInteger(0);
    private final AtomicInteger combatFailureCount = new AtomicInteger(0);
    private final AtomicInteger hitFromDiffOpponent = new AtomicInteger(0);
    private final AtomicInteger outOfCombatCount = new AtomicInteger(0);

    private final SecondsTimer switchTimer = new SecondsTimer();
    private final MonsterAggressionProcess aggressionProcess = new MonsterAggressionProcess();
    private final MonsterRetreatProcess retreatProcess = new MonsterRetreatProcess();

    private boolean enabled = true;
    private boolean canIgnorePlayers = true;

    private boolean processAggression = true;
    private boolean processRetreating = true;

    public NPCCombat(NPC actor) {
        super(actor);

        subscribe(event -> {
            if(event == CombatState.LOCKED_TARGET){

                failedToReachCount.set(0);
                lastReceivedHitTimer = System.currentTimeMillis();
                lastDealtHitTimer = System.currentTimeMillis();
                final NPCMotion motion = actor.getMotion();
                if(target != null && !motion.hasTask()) {
                    motion.start(new FollowAgentTask(actor, target, false, false, 16), false);
                }

            }
            if (event instanceof TargetIsOutOfReach) {

                if(actor.getRetreatPolicy() == MonsterRetreatPolicy.RETREAT_IF_OUT_OF_COMBAT)
                    outOfCombatCount.incrementAndGet();

                if(actor.getRetreatPolicy() != MonsterRetreatPolicy.NEVER)
                    failedToReachCount.incrementAndGet();
                else {
                    resetAttackCoolDown();
                }

            } else if(event == CombatState.FINISHED_ATTACK){

                final Optional<Agent> optionalTarget = Optional.ofNullable(target);

                optionalTarget
                        .filter(character -> character instanceof Player)
                        .map(Agent::getAsPlayer)
                        .ifPresent(player -> {
                            final NpcDefinition definition = actor.fetchDefinition();
                            final int attackSoundId = definition.getAttackSound();
                            if (attackSoundId > 0)
                                player.getPacketSender().sendSound(attackSoundId, 2);
                        });

                combatFailureCount.set(0);
                failedToReachCount.set(0);
                outOfCombatCount.set(0);

            } else if(event instanceof TargetAlreadyUnderAttack){

                final TargetAlreadyUnderAttack alreadyUnderAttack = (TargetAlreadyUnderAttack) event;
                final Agent failedTarget = alreadyUnderAttack.getFailedTarget();
                actor.debug("can't attack -> "+failedTarget+" is already under attack");

            } else if(event instanceof ActorAlreadyUnderAttack){

                actor.debug("can't attack -> I'm already under attack");
            }/* else if(event instanceof IncomingHitApplied) {
                final IncomingHitApplied incomingHitApplied = (IncomingHitApplied) event;
                final Agent attacker = incomingHitApplied.getHit().getAttacker();
                if (!hasTargeted(attacker)) {
                    if (hitFromDiffOpponent.incrementAndGet() == 4) {
                        actor.debug("on hit received -> switching to new target "+attacker+"");
                        reset(false);
                        initiateCombat(attacker);
                    }
                } else
                    hitFromDiffOpponent.set(0);
            }*/
            return false;
        });
    }

    public boolean canSwitch() {
        return switchTimer.finished();
    }

    @Override
    public void initiateCombat(Agent target) {

        // Combat dummies should not retaliate
        if (actor.isNoRetaliateNPC(actor.getId())) {
            return;
        }
        // Void knight middle
        if (actor.getId() == NpcID.VOID_KNIGHT_2953) {
            return;
        }

        // Voidknight spinner system to not retaliate and move randomly when being hit
        if (actor.getId() == NpcID.SPINNER || actor.getId() == NpcID.SPINNER_1710 || actor.getId() == NpcID.SPINNER_1711 || actor.getId() == NpcID.SPINNER_1712 || actor.getId() == NpcID.SPINNER_1713) {
            int randomMovement = Misc.random(6);
            if (randomMovement <= 2) {
                return;
            }
            if (randomMovement == 3)
            PathFinder.INSTANCE.find(actor, new Position(actor.getPosition().getX() + Random.Default.nextInt(-2, 2), actor.getPosition().getY() + Random.Default.nextInt(-2, 2), actor.getPosition().getZ()), true);
            else if (randomMovement == 4)
                PathFinder.INSTANCE.find(actor, new Position(actor.getPosition().getX() + Random.Default.nextInt(-2, 2), actor.getPosition().getY(), actor.getPosition().getZ()), true);
            else if (randomMovement == 5)
                PathFinder.INSTANCE.find(actor, new Position(actor.getPosition().getX(), actor.getPosition().getY() + Random.Default.nextInt(-2, 2), actor.getPosition().getZ()), true);
            else if (randomMovement == 6)
                PathFinder.INSTANCE.find(actor, new Position(actor.getPosition().getX() + Random.Default.nextInt(-2, 2), actor.getPosition().getY() + Random.Default.nextInt(-1, 1), actor.getPosition().getZ()), true);
            return;
        }


        if(!enabled) {
            actor.debug("initiateCombat -> combat is disabled, could not initiate!");
            return;
        }
    	actor.debug("initiate combat -> with target "+target);
        actor.setEntityInteraction(target);
    	super.initiateCombat(target);
    }

    @Override
    public void target(Agent agent) {
        if (agent instanceof Player) {
            if (agent.getAsPlayer().isInTutorial())
                return;
            if (EntityExtKt.getBoolean(agent.getAsPlayer(), Attribute.INVISIBLE, false))
                return;
        }
        super.target(agent);
    }

    @Override
    public void sequence() {

        final Timer timer = getNextAttackTimer(false);

        if(timer != null)
            actor.debug("next attack in "+timer.ticks()+" ticks");
        else
            actor.debug("no attack has been selected yet");
        if(actor.sequenceProperty().get()) {
            if(processAggression)
                aggressionProcess.sequence(actor);
            if(processRetreating)
                retreatProcess.sequence(actor);
        }
        super.sequence();
    }

    @Override
    public boolean canBeAttackedBy(Agent attacker, boolean ignoreActions) {

        if (actor.getMovementCoordinator().getGoalState() == GoalState.RETREAT_HOME) {
            if (attacker instanceof Player) {
                final Player attackerPlayer = ((Player) attacker);
                attackerPlayer.sendMessage("This npc is currently retreating and can't be targeted!");
            }
            return false;
        }

        if (super.canBeAttackedBy(attacker, ignoreActions)) {

            if (attacker instanceof Player) {

                final Player attackerPlayer = ((Player) attacker);
                final Player owner = actor.getOwner();

                if (owner != null && attackerPlayer != owner) {
                    attackerPlayer.sendMessage("This npc was not spawned for you!");
                    return false;
                }
                if (attackerPlayer.getGameMode().isSpawn() && (actor.getId() == NpcID.KING_BLACK_DRAGON_6502
                || actor.getId() == NpcID.KING_BLACK_DRAGON_6502
                || actor.getId() == NpcID.GENERAL_GRAARDOR_6494
                || actor.getId() == NpcID.JUNGLE_DEMON_6382
                        || actor.getId() == NpcID.MUTANT_TARN_9346
                        || actor.getId() == NpcID.CORPOREAL_BEAST_9347
                        || actor.getId() == NpcID.BKT_9350
                || actor.getId() == NpcID.KAMIL_6345)) {
                    attackerPlayer.sendMessage("You cannot attack Wilderness spirit bosses in spawn game mode.");
                    return false;
                }
                if (actor.getCombat().isUnderAttack() || actor.getCombat().isInCombat()) {
                    if (attackerPlayer.getCombat().target != null && attackerPlayer.getCombat().target.isNpc() && actor.getCombat().getTarget() != null && actor.getCombat().getTarget() != attackerPlayer && attackerPlayer.getGameMode().isSpawn()) {
                        attackerPlayer.sendMessage("You cannot attack NPC's that are in combat with other players in spawn game mode.");
                        return false;
                    }
                }

                if (!AreaManager.inMulti(attacker) && !AreaManager.inMulti(actor)) {

                    if (isBeingAttacked() && !isBeingAttackedBy(attacker)) {
                        int secs = (int) secondsPastLastHitReceived();

                        if (secs < 8) {
                            attackerPlayer.getCombat().notify(new TargetAlreadyUnderAttack(actor));
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean canAttack(Agent target) {

        final NPCBotHandler botHandler = actor.getBotHandler();

        actor.debug("canAttack ? "+target);
        /*if (botHandler != null) {
            if(!AreaManager.inWilderness(target)){
                reset(true);
                return false;
            }
        }*/
        return super.canAttack(target);
    }

    @Override
    public boolean retaliateAutomatically() {
        return actor.getMovementCoordinator().getGoalState() == GoalState.WALK_IN_RADIUS;
    }

    @Override
    public AttackStrategy<? extends Agent> determineStrategy() {
        final AttackStrategy<? extends Agent> strategy = actor.getAttackStrategy();
        if(strategy != null)
            return strategy;
        return MeleeAttackStrategy.INSTANCE;
    }

    @Override
    public boolean isInReachForAttack(Agent target, boolean triggerActions) {
        return canReach(target, triggerActions);
    }

    @Override
    public Damage modifyHitDamage(AttackContext context, Agent target, int baseDamage) {

        final Damage damage = new Damage(baseDamage, DamageMask.REGULAR_HIT);

        return transformOutgoingDamage(target, context, baseDamage, damage);
    }

    @Override
    public boolean resetCombatWith(Agent agent) {
        failedToReachCount.set(0);
        outOfCombatCount.set(0);
        return super.resetCombatWith(agent);
    }

    @Override
    boolean skipNextCombatTurn() {

        if(!enabled)
            return true;

        if(actor.getRetreatPolicy() != MonsterRetreatPolicy.NEVER) {
            final NpcDefinition definition = actor.fetchDefinition();
            final boolean retreats = definition != null && definition.doesRetreat();

            if (retreats) {

                final NPCMovementCoordinator coordinator = actor.getMovementCoordinator();
                final Combat<?> combat = actor.getCombat();

                if (coordinator.getGoalState() == GoalState.RETREAT_HOME || coordinator.getGoalState() == GoalState.WALK_HOME) {
                    actor.debug("skipping next combat turn and retreating, reset combat");
                    combat.reset(true);
                    return true;
                }
/*                if (actor.getPosition().getDistance(actor.getSpawnPosition()) >= definition.getCombatFollowDistance()) {
                    actor.debug("skipping next combat turn and start retreating, outside follow range!");
                    combat.reset(true);
                    coordinator.retreatHome();
                    return true;
                }*/
            }
        }
        return false;
    }

    @Override
    public boolean canReach(Agent agent, boolean triggerActions) {

        final boolean inReach = super.canReach(agent, triggerActions);
        final boolean inside = actor.isInside(agent) && actor.getSize() < 2;

        if (inside) {
            if(canIgnorePlayers)
                agent.ifPlayer(aggressionProcess::temporarilyIgnore);
            actor.debug("can't reach -> "+agent+" is inside of me");
            return false;
        }
        if (inReach) {

            int requiredDistance = actor.getCombat().requiredAttackDistance();

            if (actor.getMotion().isRunning()) {
                int extra = actor.getMotion().isRunning() ? 3 : actor.getMotion().isMoving() ? 3 : 1;
                requiredDistance += extra;
            } else if (actor.getMotion().isMoving())
                requiredDistance += actor.getMotion().isMoving() ? 3 : 1;

            if (requiredDistance > 1) {
                final boolean canReachInLongRange = agent.getCombat().canBeReachedInLongRange(actor, requiredDistance, triggerActions);
                if(!canReachInLongRange)
                    actor.debug("can't reach -> long range check has failed");
                return canReachInLongRange;
            }
            final Direction direction = actor.getDirection(agent.getPosition());
            final boolean blocked = !agent.interactTarget.reached(actor);

            if (blocked){
                if (canIgnorePlayers)
                    agent.ifPlayer(aggressionProcess::temporarilyIgnore);
                actor.debug("can't reach -> "+direction+" is obstructed");
                return false;
            }
            return true;
        }
        //actor.setEntityInteraction(agent);
        actor.debug("can't reach -> "+agent+" is not in reach of me");
        return false;
    }

    public AtomicInteger getFailedToReachCount() {
        return failedToReachCount;
    }

    public AtomicInteger getOutOfCombatCount() {
        return outOfCombatCount;
    }

    public void disable() {
        enabled = false;
    }

    public void enable(){
        enabled = true;
    }

    public void setCanIgnorePlayers(boolean canIgnorePlayers) {
        this.canIgnorePlayers = canIgnorePlayers;
    }

    public boolean isProcessAggression() {
        return processAggression;
    }

    public void setProcessAggression(boolean processAggression) {
        this.processAggression = processAggression;
    }

    public boolean isProcessRetreating() {
        return processRetreating;
    }

    public void setProcessRetreating(boolean processRetreating) {
        this.processRetreating = processRetreating;
    }
}
