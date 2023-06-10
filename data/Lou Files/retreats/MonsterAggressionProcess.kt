package com.grinder.game.entity.agent.npc.monster.aggression

import com.grinder.game.collision.CollisionManager
import com.grinder.game.collision.CollisionPolicy
import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeNpc
import com.grinder.game.content.minigame.fightcave.FightCaveNpc
import com.grinder.game.content.miscellaneous.rugmerchant.RugMerchant
import com.grinder.game.entity.Entity
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.AgentUtil
import com.grinder.game.entity.agent.combat.LineOfSight.withinSight
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.npc.monster.SpinolypAttack
import com.grinder.game.entity.agent.combat.event.impl.TargetIsNotReachable
import com.grinder.game.entity.agent.inWilderness
import com.grinder.game.entity.agent.movement.NPCMovementCoordinator
import com.grinder.game.entity.agent.movement.pathfinding.PathFinder.find
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.Monster
import com.grinder.game.entity.agent.npc.monster.aggression.MonsterAggressionProcess.Companion.targetMap
import com.grinder.game.entity.agent.npc.monster.aggression.MonsterAggressionTolerancePolicy.IN_VICINITY
import com.grinder.game.entity.agent.npc.monster.aggression.MonsterAggressionType.ALWAYS
import com.grinder.game.entity.agent.npc.monster.aggression.MonsterAggressionType.COMBAT_LEVEL_BASED
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.impl.KingBlackDragonBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.SeaTrollQueenBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.arzinian.AvatarOfMagicBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.arzinian.AvatarOfMeleeBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.arzinian.AvatarOfRangingBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.corporealbeast.CorporealBeastBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.dagannoth.DagannothPrimeBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.dagannoth.DagannothRexBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.dagannoth.DagannothSupremeBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.God
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.GodMinion
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.Religious
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinion
import com.grinder.game.entity.agent.npc.monster.impl.RockCrab
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.areas.impl.WildernessArea
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc
import com.grinder.util.NpcID
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

/**
 * This class represents the aggression process for [NPC] entities.
 *
 * Each combat-ready [NPC] entity in the game has its own [MonsterAggressionProcess],
 * which is (generally) sequenced every world cycle.
 *
 * The results of each sequence are stored in the [targetMap].
 *
 * When all [MonsterAggressionProcess] instances are sequenced,
 * the results are evaluated and finally the [targetMap] is cleared.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @author  Lou
 * @since   07/09/2019
 * @version 1.0
 */
class MonsterAggressionProcess {

    private val timeoutMap = HashMap<Player, Long>()

    /**
     * Sequences the aggression process for the argued [npc],
     * if the [npc] has found a potential new target, it is stored in the [targetMap].
     *
     * @param npc the [NPC] whose aggression mechanics must be sequenced.
     */
    fun sequence(npc: NPC) {

        // the npc should not sequence aggression if it is a BossNPC that is skipping combat sequencing.
        if (npc is Monster && npc.skipNextCombatSequence()) {
            return
        }

        if (npc is Boss && npc.skipNextCombatSequence()) {
            return
        }

        // the npc should not sequence aggression if it is retreating.
        if (npc.movementCoordinator.isRetreating)
            return

        // the npc should not sequence aggression if its last successful attack was less than 8 seconds ago.
        if (npc.combat.hasSuccessfullyAttackedTarget()) {
            if (!npc.combat.hasElapsedSinceLastAttack(TimeUnit.SECONDS, 8)) {
                return
            }
        }

        val definition = npc.fetchDefinition() ?: return

        if (!definition.isAttackable || definition.combatLevel <= 0)
            return

        val combatLevel = definition.combatLevel
        val attackStrategy = npc.combat.determineStrategy()

        val aggressiveType = if (isAlwaysAggressive(npc, attackStrategy, combatLevel))
            ALWAYS else COMBAT_LEVEL_BASED

        val tolerancePolicy = npc.aggressionTolerancePolicy

        val candidates = AgentUtil
                .getPlayersInProximity(npc, if ((npc is FightCaveNpc || npc is AquaisNeigeNpc)) 64 else getAggressionDistance(npc), CollisionPolicy.PROJECTILE)
                .collect(Collectors.toSet())

        npc.debug("sequence aggression -> found ${candidates.size} candidates")



        for (player in candidates) {

            val distance = DistanceUtil.calculateDistance(player, npc)
            val aggressionDistance = getAggressionDistance(npc)

            if (distance <= aggressionDistance) {

                if (player.appearance.bas?.idle == RugMerchant.BAS_ID) {
                    continue
                }

                if (player.isInTutorial || player.invisible) {
                    continue
                }

                if (player.position.z != npc.position.z) {
                    continue
                }

                if (npc.movementCoordinator.goalState == NPCMovementCoordinator.GoalState.WALK_HOME) {
                    continue;
                }

                if (npc.movementCoordinator.retreatPosition != null && !npc.position.isWithinDistance(npc.movementCoordinator.retreatPosition, npc.fetchDefinition().combatFollowDistance)) {
                    continue;
                }

                if (npc.combat.negateAggression())
                    continue;


                if (CollisionManager.blocked(player.position)) {
                    npc.debug("collision -> is blocked to reach to $player")
                    continue
                }

                val ignoreToleranceAndCombatLevel = player.combat.aggressivityTimer.finished().not()

                if (negateAggression(npc, player) && !ignoreToleranceAndCombatLevel) {
                    npc.debug("aggression -> timer finishes for $player, is not longer aggro")
                    continue
                }

                // the npc should not sequence aggression if it is not marked as aggressive in its definition.
                if (!isBandit(npc) && !isGodWarsMinion(npc))
                    if (!definition.isAggressive && !ignoreToleranceAndCombatLevel)
                        continue

                if (timeoutMap.containsKey(player)) {
                    val timePassed = System.nanoTime() - timeoutMap[player]!!
                    if (TimeUnit.NANOSECONDS.toSeconds(timePassed) >= 5)
                        timeoutMap.remove(player)
                    else {
                        npc.debug("aggression -> ignoring ${player.username} for ${5 - TimeUnit.NANOSECONDS.toSeconds(timePassed)} seconds")
                        npc.resetEntityInteraction()
                        continue
                    }
                }

                if (targetMap.containsKey(player) && !AreaManager.inMulti(player)) // Don't target player that is already in the target list of another npc
                    continue

                // if the tolerance policy of the npc is IN_VICINITY,
                // the npc is no longer aggressive after 5 minutes
                if (tolerancePolicy == IN_VICINITY
                        && !ignoreToleranceAndCombatLevel
                        && player.aggressionTolerance.finished()
                        && !isAlwaysAggressive(npc, attackStrategy, combatLevel)) {
                            npc.debug("aggression -> $player is tolerant")
                    continue
                }

                // if the aggressive type of the npc is COMBAT_LEVEL_BASED,
                // the npc will not attempt to target a player with twice its combat level.
                if (!isBandit(npc) && !isGodWarsMinion(npc)) {
                    if (aggressiveType == COMBAT_LEVEL_BASED
                        && !ignoreToleranceAndCombatLevel
                        && isTooStrong(player.skillManager.calculateCombatLevel(), combatLevel)) {
                            npc.debug("aggression -> ${player.username} is too strong")
                            continue
                    }
                }


                if (!npc.combat.canAttackWith(player, attackStrategy, false)) {
                    npc.debug("combat -> can't attack ${player.username} with current attack strategy")
                    continue
                }

                if (!npc.isInside(player) && attackStrategy.type() != AttackType.MELEE && !canSee(npc, player)) {
                    npc.debug("sequence aggression -> projectile path to ${player.username} not possible")
                    npc.setEntityInteraction(player)
                    npc.combat.submit(TargetIsNotReachable(player))
                    continue
                }

                    targetMap.getOrPut(player) { HashMap() }[npc] = distance
                    npc.debug("sequence aggression -> added ${player.username} to target map")
                    npc.setEntityInteraction(player)
            }
        }

        npc.onStateChange("sequenced aggression")
    }

    private fun isAlwaysAggressive(
        npc: NPC,
        attackStrategy: AttackStrategy<out Agent>?,
        combatLevel: Int
    ) = (npc.inWilderness()
            || npc is FightCaveNpc
            || npc is AquaisNeigeNpc
            || npc.id in 9001..9019
            || attackStrategy is SpinolypAttack
            || combatLevel > 150
            || AreaManager.GOD_WARS_AREA.contains(npc)
            || npc is RockCrab)

    private fun canSee(entity: Entity, target: Entity) : Boolean {
        if (entity is Monster && entity.skipProjectileClipping())
            return true
        else if (entity is Boss && entity.skipProjectileClipping())
            return true
        return withinSight(entity, target, walls = false)
    }

    fun temporarilyIgnore(player: Player) {
        timeoutMap[player] = System.nanoTime()
    }

    companion object {

        val secondsUntilTolerant = Math.toIntExact(TimeUnit.MINUTES.toSeconds(5))

        private val targetMap = HashMap<Player, HashMap<NPC, Int>>()

        fun evaluateTargetMap() {

            targetMap.forEach { entry ->

                val player = entry.key
                val hostileNpcPaths = entry.value
                        .filterKeys { npc ->
                            if (npc.position.z == player.position.z
                                    && npc.movementCoordinator.isRetreating.not()) {

                                val npcCombat = npc.combat
                                if (npcCombat.hasTarget()) {
                                    val attackStrategy = npc.combat.determineStrategy()
                                    val targeted = !npcCombat.hasTargeted(player)
                                    val targetCombat = npcCombat.target.combat
                                    val targetAttacking = targetCombat.isAttacking
                                    val targetCanBeAttackedByNPC = npcCombat.canAttackWith(npc, attackStrategy, true)
                                    npc.debug("${player.username} -> $targeted, $targetAttacking, $targetCanBeAttackedByNPC")
                                    return@filterKeys targeted && !targetAttacking && targetCanBeAttackedByNPC
                                } else {
                                    npc.debug("has no target for aggressions")
                                }
                                return@filterKeys true
                            } else {
                                npc.debug("target not on same height")
                            }
                            return@filterKeys false
                        }

                if (hostileNpcPaths.isNotEmpty()) {

                    // if the player is in multi, all eligible npc entities can attempt to target the player.
                    if (AreaManager.inMulti(player)) {
                        hostileNpcPaths.entries.forEach {
                            initiateCombat(it.key, player)
                        }
                    }

                    // else we find the nearest npc to the player if present and make it attempt to target the player.
                    else hostileNpcPaths.minBy { it.value }?.let {
                        initiateCombat(nearbyNpc = it.key, player = player)
                    }
                }
            }

            targetMap.clear()
        }

        private fun initiateCombat(nearbyNpc: NPC, player: Player) {
            val goal = nearbyNpc.movementCoordinator.goalState
            if (goal != NPCMovementCoordinator.GoalState.RETREAT_HOME && goal != NPCMovementCoordinator.GoalState.WALK_HOME) {
                nearbyNpc.debug("evaluate aggression -> tracing and targeting ${player.username}")
                nearbyNpc.combat?.initiateCombat(player, !nearbyNpc.motion.movementDisabled())
            }
        }

        private fun negateAggression(npc: NPC, player: Player): Boolean {

            if (npc is Religious && AreaManager.GOD_WARS_AREA.contains(npc)) {
                val sameArea = npc.chamber().area.contains(player)
                npc.debug("${if (sameArea) "in same area" else "not in same area"} ")
                return !sameArea
            }

            if (isBandit(npc)) {

                var zamorakItemsWornByPlayerCount = EquipmentUtil.getItemCount(player, "Zamorak", true)
                var saradominItemsWornByPlayerCount = EquipmentUtil.getItemCount(player, "Saradomin", true)

                zamorakItemsWornByPlayerCount += EquipmentUtil.getItemCount(player, "Unholy", true)
                saradominItemsWornByPlayerCount += EquipmentUtil.getItemCount(player, "Holy", true)

                if (zamorakItemsWornByPlayerCount + saradominItemsWornByPlayerCount == 0)
                    return true

                if (Misc.randomBoolean()) {

                    val religionName = if (zamorakItemsWornByPlayerCount > 0) "Zamorak" else "Saradomin"

                    if (Misc.randomBoolean())
                        npc.say("Filthy $religionName follower scum!")
                    else
                        npc.say("$religionName scum! You will regret coming here!")

                }
            } else if (isZamorakMinion(npc)) {

                var zamorakItemsWornByPlayerCount = EquipmentUtil.getItemCount(player, "Zamorak", false)

                zamorakItemsWornByPlayerCount += EquipmentUtil.getItemCount(player, "Unholy", false)
                zamorakItemsWornByPlayerCount += EquipmentUtil.getZamorakItems(player);

                if (zamorakItemsWornByPlayerCount > 0)
                    return true

            } else if (isSaradominMinion(npc)) {
                var saradominItemsWornByPlayerCount = EquipmentUtil.getItemCount(player, "Saradomin", false)
                saradominItemsWornByPlayerCount += EquipmentUtil.getItemCount(player, "Holy ", false)
                saradominItemsWornByPlayerCount += EquipmentUtil.getItemCount(player, "Monk's", false)
                saradominItemsWornByPlayerCount += EquipmentUtil.getSaradominItems(player);

                if (saradominItemsWornByPlayerCount > 0)
                    return true
            } else if (isBandosMinion(npc)) {
                var bandosItemsWornByPlayerCount = EquipmentUtil.getItemCount(player, "Bandos", false)
                bandosItemsWornByPlayerCount += EquipmentUtil.getBandosItems(player);

                if (bandosItemsWornByPlayerCount > 0)
                    return true
            } else if (isArmadylMinion(npc)) {
                var aramadylItemsWornByPlayerCount = EquipmentUtil.getItemCount(player, "Armadyl", false)
                aramadylItemsWornByPlayerCount += EquipmentUtil.getArmadylItems(player);

                if (aramadylItemsWornByPlayerCount > 0)
                    return true
            }

            return false
        }

        private fun isBandit(npc: NPC) = npc.id == NpcID.BANDIT || npc.id == NpcID.BANDIT_695

        private fun isGodWarsMinion(npc: NPC) = npc.id == NpcID.HOBGOBLIN_2241 || npc.id == NpcID.ORK_2240 ||
        npc.id == NpcID.OGRE_2096 || npc.id == NpcID.JOGRE_2234 || npc.id == NpcID.CYCLOPS_7271 || npc.id == NpcID.FERAL_VAMPYRE ||
        npc.id == NpcID.BLOODVELD_3138 || npc.id == NpcID.PYREFIEND_3139 || npc.id == NpcID.GORAK_3141 || npc.id == NpcID.SARADOMIN_PRIEST ||
        npc.id == NpcID.SPIRITUAL_WARRIOR || npc.id == NpcID.SPIRITUAL_RANGER || npc.id == NpcID.SPIRITUAL_MAGE || npc.id == NpcID.KNIGHT_OF_SARADOMIN ||
        npc.id == NpcID.KNIGHT_OF_SARADOMIN_2214 || npc.id == NpcID.SPIRITUAL_WARRIOR_3166 || npc.id == NpcID.SPIRITUAL_RANGER_3167 || npc.id == NpcID.SPIRITUAL_MAGE_3168 ||
        npc.id == NpcID.AVIANSIE || npc.id == NpcID.AVIANSIE_3170 || npc.id == NpcID.AVIANSIE_3171 || npc.id == NpcID.AVIANSIE_3172 ||
        npc.id == NpcID.AVIANSIE_3173 || npc.id == NpcID.AVIANSIE_3174 || npc.id == NpcID.AVIANSIE_3175 || npc.id == NpcID.AVIANSIE_3176

        private fun isZamorakMinion(npc: NPC) = npc.id == NpcID.FERAL_VAMPYRE ||
                npc.id == NpcID.BLOODVELD_3138 || npc.id == NpcID.PYREFIEND_3139 || npc.id == NpcID.GORAK_3141

        private fun isSaradominMinion(npc: NPC) = npc.id == NpcID.SARADOMIN_PRIEST ||
                npc.id == NpcID.SPIRITUAL_WARRIOR || npc.id == NpcID.SPIRITUAL_RANGER || npc.id == NpcID.SPIRITUAL_MAGE || npc.id == NpcID.KNIGHT_OF_SARADOMIN ||
                npc.id == NpcID.KNIGHT_OF_SARADOMIN_2214 || npc.id == NpcID.SPIRITUAL_WARRIOR_3166

        private fun isBandosMinion(npc: NPC) = npc.id == NpcID.HOBGOBLIN_2241 || npc.id == NpcID.ORK_2240 ||
                npc.id == NpcID.OGRE_2096 || npc.id == NpcID.JOGRE_2234 || npc.id == NpcID.CYCLOPS_7271

        private fun isArmadylMinion(npc: NPC) = npc.id == NpcID.SPIRITUAL_WARRIOR_3166 || npc.id == NpcID.SPIRITUAL_RANGER_3167 || npc.id == NpcID.SPIRITUAL_MAGE_3168 ||
                npc.id == NpcID.AVIANSIE || npc.id == NpcID.AVIANSIE_3170 || npc.id == NpcID.AVIANSIE_3171 || npc.id == NpcID.AVIANSIE_3172 ||
                npc.id == NpcID.AVIANSIE_3173 || npc.id == NpcID.AVIANSIE_3174 || npc.id == NpcID.AVIANSIE_3175 || npc.id == NpcID.AVIANSIE_3176

        /**
         * This function return the maximum distance between a [Player] and a [NPC],
         * in which the argued [npc] is still aggressive towards the [Player].
         *
         *
         * @param npc the subject [NPC]
         *
         * @return a [Int] value representing the maximum aggression distance (default value = 8).
         */
        fun getAggressionDistance(npc: NPC): Int {
            return when (npc) {
                is FightCaveNpc -> 64
                is AquaisNeigeNpc -> 64
                is RockCrab -> 1
                is KingBlackDragonBoss -> 50
                is SeaTrollQueenBoss -> 15
                is DagannothPrimeBoss -> 5
                is DagannothRexBoss -> 4
                is DagannothSupremeBoss -> 5
                is CorporealBeastBoss -> 20
                is AvatarOfMagicBoss -> 15
                is AvatarOfMeleeBoss -> 15
                is AvatarOfRangingBoss -> 15
                is God -> 15
                is GodMinion<*> -> 24
                is Boss -> 15
                is BossMinion<*> -> 12
                else ->
                    when(npc.id) {
                        NpcID.SCORPIAS_OFFSPRING_6616 -> return 1
                        else -> npc.fetchDefinition().combatFollowDistance

                    }
            }
        }

        private fun isTooStrong(playerCombat: Int, npcCombat: Int) = playerCombat > npcCombat * 2
    }

}