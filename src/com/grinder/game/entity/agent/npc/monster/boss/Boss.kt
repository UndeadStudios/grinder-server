package com.grinder.game.entity.agent.npc.monster.boss

import com.grinder.game.collision.CollisionPolicy
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.AgentUtil
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.npc.monster.Monster
import com.grinder.game.entity.agent.npc.monster.MonsterEvents
import com.grinder.game.entity.agent.npc.monster.aggression.MonsterAggressionTolerancePolicy
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Boundary
import com.grinder.game.model.Position
import java.security.SecureRandom
import java.util.*
import java.util.function.Predicate
import java.util.stream.Stream

/**
 * Represents a [Monster] with more elaborate mechanics, to create epic battles.
 *
 * @see BossCombat for the overarching combat implementation
 * @see BossAttack for specifying elaborate attack strategies
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-10
 *
 * @param id            the [id] of this boss
 * @param position      the [position] of this boss
 * @param bossPolicies  specify any [BossPolicy] that should hold for this boss
 */
abstract class Boss(
        id: Int,
        position: Position,
        vararg bossPolicies: BossPolicy
) : Monster(id, position) {

    init {
        onEvent {
            if(it == MonsterEvents.ADDED){
                bossAttack = generateAttack()
            }/*
            if(it == MonsterEvents.DYING) {
                World.npcRemoveQueue.add(this);
            }*/
        }
    }
    private val bossCombat = BossCombat(this)
    private val bossPolicies = arrayOf(*bossPolicies)

    open val circumscribedBoundaries = emptyList<Boundary>()

    lateinit var bossAttack  : BossAttack

    abstract fun generateAttack() : BossAttack

    override fun getAggressionTolerancePolicy() = MonsterAggressionTolerancePolicy.NEVER

    override fun getCombat() = bossCombat

    override fun getAttackStrategy() = bossAttack as AttackStrategy<*>

    /**
     * Get the [AttackTypeProvider] that is used  to provide
     * all probable [AttackType]s that can be rolled in [randomizeAttack].
     */
    protected abstract fun attackTypes(): AttackTypeProvider

    /**
     * Get the maximum number of targets that can be hit using the provided [AttackType].
     *
     * @param type the [AttackType] to get the maximum number of targets for the attack.
     */
    protected abstract fun maxTargetsHitPerAttack(type: AttackType): Int

    /**
     * Should the [attackRangePolicy] be used to verify the provided [AttackType]
     * in the [excludeType] predicate tested for in [randomizeAttack].
     *
     * @param type the [AttackType] to check or not.
     */
    protected open fun checkAttackRangeForTypeExclusion(type: AttackType) = false

    /**
     * Gets the [OutOfRangePolicy] that should be applied whenever
     * a selected for target is not in range of the provided [AttackType].
     *
     * @param type the [AttackType] to get the policy for.
     */
    open fun attackRangePolicy(type: AttackType) = OutOfRangePolicy.EXCLUDE_TYPE

    /**
     * Check whether this boss can be attacked by the provided [AttackType].
     *
     * @param type the [AttackType] that is being used to prepare an attack
     * @return a [Pair] consisting of a [Boolean] representing whether the boss is immune,
     *  and a [String] containing the message to send to an attacking [Player] if the boss is immune.
     */
    open fun immuneToAttack(type: AttackType) : Pair<Boolean, String?> = Pair(false, null)

    /**
     * Should all damage received by this boss be negated (ignored)?
     *
     * @param details the [AttackContext] in which the damage is created.
     */
    open fun negateAllIncomingDamage(details: AttackContext) = false

    /**
     * Get the maximum number of targets that can be hit by the [bossAttack].
     * @see maxTargetsHitPerAttack
     * @return [maxTargetsHitPerAttack] if the [BossAttack.hasPreferredType]
     *          and zero otherwise
     */
    fun maxTargets() = if(bossAttack.hasPreferredType())
        maxTargetsHitPerAttack(bossAttack.type())
    else
        0

    /**
     * Get a stream of all players within distance of this [position].
     *
     * @param maxDistance the maximum distance from this [position]
     */
    fun playerStream(maxDistance: Int): Stream<Player> = AgentUtil.getPlayersInProximity(this, maxDistance, CollisionPolicy.NONE)

    /**
     * Check whether this boss has the provided [BossPolicy] in its [bossPolicies].
     */
    private fun hasPolicy(policy: BossPolicy) = bossPolicies.contains(policy)

    /**
     * Randomize the [BossAttack.preferredType] of this [bossAttack], and possibly
     * switch targets when the rolled type can only be applied to a target that
     * the boss is not currently fighting.
     *
     * TODO: this is a bit overcomplicated :3, could be simplified
     * TODO: also this method is highly inefficient!
     */
    open fun randomizeAttack() {

        val hasPreviousAttack = bossAttack.hasPreferredType()

        // get all possible attack types chances that may be selected for
        val attackTypeChances = attackTypes().provide()

        if (attackTypeChances.isEmpty()) {
            warn(this, hasPreviousAttack, "no attack types were provided.")
            return
        }

        // contains all possible types, used if no priority type was eligible
        val availableTypes = HashSet<AttackType>()
        // contains all priority (rolled) types
        val priorityTypes = ArrayList<AttackType>()

        val excludeTypePredicate = excludeType()
        val excludedTypes = AttackType.values().filter { excludeTypePredicate.test(it) }

        // add all attack types associated with the chance
        for(chance in attackTypeChances) {
            for (type in chance.attackTypes) {
                if (!excludedTypes.contains(type))
                    availableTypes.add(type)
            }
        }

        if(availableTypes.isEmpty()) {
            debug("no available attack types")
            return
        }

        if(availableTypes.size > 1) {
            var attempts = 0
            while (priorityTypes.isEmpty()) {

                // cheap check, should not be reached
                if (attempts > 1000) {
                    warn(this, true, "Could not generate an attack type for the npc!")
                    return
                }

                // iterate over each possible chance to roll an attack type
                for (attackTypeChance in attackTypeChances) {

                    // add the rolled attack type (if present) to the priority types
                    attackTypeChance.roll().ifPresent {
                        if (availableTypes.contains(it))
                            priorityTypes.add(it)
                    }
                }
                attempts++
            }
        } else if(availableTypes.size == 1)
            priorityTypes.add(availableTypes.first())

        var type: AttackType?

        // if there is just one priority type present
        if (priorityTypes.size == 1) {

            type = priorityTypes[0]

            if (availableTypes.size > 1) {
                combat.findAttackableTarget(type).ifPresent {
                    if (combat.isInReachForAttack(it, false)) {
                        switchToTarget(it)
                    }
                }
            }

        } else {

            // shuffle the priority types
            priorityTypes.shuffle(SecureRandom())

            type = null

            for (next in priorityTypes) {

                val agentOptional = combat.findAttackableTarget(next)


                if (agentOptional.isPresent) {
                    val agent = agentOptional.get()
                    if (combat.isInReachForAttack(agent, false)) {
                        switchToTarget(agent)
                    }
                    type = next
                    break
                }
            }
        }

        // if we did not manage to find an eligible type
        if (type == null) {

            // just get any type from allTypes that wasn't tested for in priority types.
            if (availableTypes.size > priorityTypes.size) {
                availableTypes.removeAll(priorityTypes.toSet())
                type = availableTypes.stream().findAny().orElse(null)
            } else {
                // get a random type from priority types
                if (priorityTypes.size > 0) {
                    type = priorityTypes.random()
                }
            }
        }

        // if we still didn't get an attack type, something is wrong with the code
        if (type == null) {
            warn(this, hasPreviousAttack, "failed to generate an attack type.")
            return
        }

        // update the preferred type in the boss attack
        setPreferredAttackType(type)
    }

    fun setPreferredAttackType(type: AttackType) {
        bossAttack.setType(type)
    }

    /**
     * Switch focus to the provided [Agent] given that
     * this boss is not already in combat with the provided [Agent].
     *
     * @param agent the [Agent] to switch focus to
     */
    private fun switchToTarget(agent: Agent) {
        if (!bossCombat.isInCombatWith(agent)) {
            if (bossCombat.isUnderAttack)
                bossCombat.resetTarget()
            bossCombat.target(agent)
        }
    }

    /**
     * Get a predicate that when true excludes a tested [AttackType].
     *
     * The predicate only returns true when [checkAttackRangeForTypeExclusion] returns true,
     * a [BossCombat.findCurrentOrClosestTarget] was not found and the [attackRangePolicy]
     * is equal to [OutOfRangePolicy.EXCLUDE_TYPE].
     *
     * @return a [Predicate] that tests [AttackType]s
     */
    private fun excludeType(): Predicate<AttackType> {
        return Predicate { type ->
            try {
                if (checkAttackRangeForTypeExclusion(type)) {
                    val optionalTarget = bossCombat.findCurrentOrClosestTarget(attackRange(type))
                    val policy = attackRangePolicy(type)
                    return@Predicate optionalTarget.isEmpty && policy == OutOfRangePolicy.EXCLUDE_TYPE
                }
            } catch (e: Exception){
                e.printStackTrace()
                return@Predicate false
            }
            return@Predicate false
        }
    }

    private fun warn(boss: Boss, resetCombat: Boolean, exceptionMessage: String) {
        if (resetCombat) {
            boss.combat.reset(false)
            System.err.println("[BossNPC]: " + exceptionMessage + " for " + boss.fetchDefinition().name + "! -> resetting combat.")
        } else
            System.err.println("[BossNPC]: " + exceptionMessage + " for " + boss.fetchDefinition().name + "! -> continue combat with " + boss.bossAttack.type() + ".")
    }
}
