package com.grinder.game.entity.agent.npc.monster.boss

import com.grinder.game.collision.CollisionManager
import com.grinder.game.collision.CollisionPolicy
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.AgentUtil
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.event.CombatState
import com.grinder.game.entity.agent.combat.event.impl.TargetIsNotReachable
import com.grinder.game.entity.agent.combat.event.impl.TargetIsOutOfReach
import com.grinder.game.entity.agent.combat.subscribe
import com.grinder.game.entity.agent.npc.monster.MonsterCombat
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.getBoolean
import com.grinder.game.model.attribute.Attribute
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.max

/**
 * Represents [MonsterCombat] tweaked for [Boss] monsters.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/08/2020
 *
 * Creates a new [BossCombat] instances for the argued boss.
 *
 * @param boss the [Boss] to create the combat for.
 */
class BossCombat(private val boss: Boss) : MonsterCombat(boss) {

    /**
     * Used to check if the boss is allowed to switch targets.
     */
    private var timeSinceLastSwitch = System.currentTimeMillis()

    init {
        setTrackIncomingDamages(true)
        setCanIgnorePlayers(false)
        subscribe { event ->
            when (event) {
                CombatState.LOCKED_TARGET -> {
                    /*
                     * When we switch targets, check if a new attack can be generated.
                     */
                    if (readyToAttack(false)) {
                        boss.randomizeAttack()
                        boss.debug("randomizeAttack -> Locked on target")
                    }
                }
                CombatState.STARTING_ATTACK -> {
                    /*
                     * Attempts to delegate an attack to multiple targets.
                     */
                    eligibleTargetStream()
                            .filter { it !== target }
                            .filter { isInReachAndEligibleForAttack(it) }
                            .filter { !CollisionManager.blocked(it.position) }
                            .filter { it.isAlive }
                            .filter { !it.isDying}
                            .limit(max(0, boss.maxTargets().toLong()-1))
                            .forEach { attack(it) }
                }
                CombatState.FINISHED_ATTACK -> {
                    /*
                     * Switch to a new attack.
                     */
                    boss.randomizeAttack()
                }
                CombatState.SEQUENCED_ATTACK -> {
                    if (isUnderAttack) {
                        /*
                         * Check if there is a more suitable target.
                         */
                        trySwitchTarget()
                    } else {
                        boss.debug("not in active combat -> Searching target")

                        /*
                         * Check if there is any suitable target.
                         */
                        findCurrentOrPreferredOrNearestTarget().ifPresent {
                            if (!hasTargeted(it))
                                target(it)
                            else if(!isAttacking(it))
                                initiateCombat(it, !boss.motion.movementDisabled())
                        }
                    }
                }
            }
            if(event is TargetIsOutOfReach){
                if (boss.retreatPolicy == MonsterRetreatPolicy.RETREAT_IF_OUT_OF_COMBAT) {
                    outOfCombatCount.incrementAndGet()
                }
                /*
                 * Randomize attack if no reachable target was found
                 */
                if (readyToAttack(false)) {
                    boss.randomizeAttack()
                    boss.debug("randomizeAttack -> Target is out of reach")
                }
            } else if(event is TargetIsNotReachable){
                /*
                 * Bosses that cannot retreat should try to switch attack styles.
                 */
                if (boss.retreatPolicy == MonsterRetreatPolicy.NEVER) {
                    if (readyToAttack(false)) {
                        boss.randomizeAttack()
                        boss.debug("randomizeAttack -> Target is not reachable")
                    }
                }
            }
            false
        }
    }

    override fun canAttack(target: Agent): Boolean {
        return isEligibleForAttack(target)
                && super.canAttack(target)
    }

    private fun trySwitchTarget() {

        val agentOptional = findPreferredOpponent()
                .filter { notInCombatWith(it) }!!

        val timeBetweenLastSwitch: Long = System.currentTimeMillis() - timeSinceLastSwitch
        val secondsBetweenLastSwitch = TimeUnit.MILLISECONDS.toSeconds(timeBetweenLastSwitch)

        /*
         Switch targets roughly every 5 seconds,
         assuming there is a stronger target.
         */
        if (agentOptional.isPresent && secondsBetweenLastSwitch > 5) {
            if (Misc.randomChance(50.0f)) {
                resetTarget()
                target(agentOptional.get())
                timeSinceLastSwitch = System.currentTimeMillis()
            }
        }
    }

    /**
     * Check whether the argued [Agent] is eligible to be attacked by this boss.
     *
     * @param agent the [Agent] being checked
     */
    private fun isEligibleForAttack(agent: Agent): Boolean {

        if (!agent.isActive)
            return false

        if (agent is Player)
            if (agent.isInTutorial || agent.getBoolean(Attribute.INVISIBLE))
                return false

        val boundaries = boss.circumscribedBoundaries
        return if (boundaries.isEmpty())
            true
        else
            boundaries.stream().anyMatch { boundary -> boundary.contains(agent.position) }
    }

    /**
     * Returns a stream of [Agent]s that are visible to the [boss]
     * and are eligible for attack.
     */
    fun eligibleTargetStream() = AgentUtil
            .getPlayersInProximity(actor, Player.NORMAL_VIEW_DISTANCE, CollisionPolicy.NONE)
            .filter { isEligibleForAttack(it) }

    fun targetStream() = eligibleTargetStream()

    /**
     * Returns a stream of [Agent]s that are in reach of and eligible for an attack.
     */
    fun targetStream(maxDistance: Int) = targetStream()
            .filter { isWithinDistance(it, maxDistance) }!!

    /**
     * Find an [Agent] that may be attacked by the provided [AttackType].
     *
     * @param type the [AttackType] to find an eligible target for.
     */
    fun findAttackableTarget(type: AttackType): Optional<Agent> {
        return if (boss.attackRangePolicy(type) == OutOfRangePolicy.EXCLUDE_TYPE) {
            /*
             * Target must be in reach of the attack range.
             */
            findCurrentOrClosestTarget(boss.attackRange(type))
        } else
            /*
             * We don't care whether the target is in range or not,
             * just find the current, preferred or nearest!
             */
            findCurrentOrPreferredOrNearestTarget()
    }

    /**
     * Finds an [Agent] that is closest to this [boss].
     */
    fun findCurrentOrClosestTarget(maxDistance: Int): Optional<Agent> {
        if (hasTarget()) {
            val distance = target.position.getDistance(actor.position)
            if (distance <= maxDistance)
                return Optional.ofNullable(target)
        }
        return findNearestReachableTarget(maxDistance).map { it as Agent }
    }

    /**
     * Finds an [Agent] that is closest to this [boss].
     */
    fun findCurrentEmptyOrClosestTarget(maxDistance: Int): Optional<Agent> {
        if (hasTarget()) {
            val distance = target.position.getDistance(actor.position)
            if (distance <= maxDistance)
                return Optional.empty()
        }
        return findNearestReachableTarget(maxDistance).map { it as Agent }
    }

    /**
     * Finds the closest [Agent] that is in reach of and eligible for an attack.
     *
     * Target must be closer than the [maxDistance] to the [boss].
     */
    fun findNearestReachableTarget(maxDistance: Int) =
            targetStream(maxDistance).min(getMinDistanceComparator())

    fun findCurrentOrPreferredOrNearestTarget(): Optional<Agent> {
        return Optional.ofNullable(findCurrentTarget()
                .orElse(findPreferredOpponent()
                        .orElse(eligibleTargetStream()
                                .min(getMinDistanceComparator())
                                .orElse(null))))
    }

    private fun getMinDistanceComparator() =
            Comparator.comparingInt<Agent> { DistanceUtil.calculateDistance(actor, it) }
}