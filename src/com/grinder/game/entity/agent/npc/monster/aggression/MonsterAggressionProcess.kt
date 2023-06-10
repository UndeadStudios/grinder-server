package com.grinder.game.entity.agent.npc.monster.aggression

import com.grinder.game.collision.CollisionManager
import com.grinder.game.collision.CollisionPolicy
import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeNpc
import com.grinder.game.content.minigame.fightcave.FightCaveNpc
import com.grinder.game.content.minigame.fightcave.monsters.YtHurKot
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
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.Monster
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.aggression.MonsterAggressionProcess.Companion.targetMap
import com.grinder.game.entity.agent.npc.monster.aggression.MonsterAggressionTolerancePolicy.IN_VICINITY
import com.grinder.game.entity.agent.npc.monster.aggression.MonsterAggressionType.ALWAYS
import com.grinder.game.entity.agent.npc.monster.aggression.MonsterAggressionType.COMBAT_LEVEL_BASED
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.impl.KingBlackDragonBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.MerodachBoss
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
import com.grinder.game.entity.getBoolean
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.attribute.Attribute
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

        when {
            npc.movementCoordinator.goalState == NPCMovementCoordinator.GoalState.IN_COMBAT -> {
                return;
            }
            npc.movementCoordinator.goalState == NPCMovementCoordinator.GoalState.RETREAT_HOME -> {
                return;
            }
            npc.movementCoordinator.goalState == NPCMovementCoordinator.GoalState.WALK_HOME -> {
                return;
            }
            npc is Monster && npc.skipNextCombatSequence() -> {
                return
            }
            npc is Boss && npc.skipNextCombatSequence() -> {
                return
            }
            npc.movementCoordinator.isRetreating -> return
            npc.combat.hasSuccessfullyAttackedTarget() -> {
                if (!npc.combat.hasElapsedSinceLastAttack(TimeUnit.SECONDS, 8)) {
                    return
                }
            }
        }

        if (MonsterRace.isRace(npc, MonsterRace.COMBAT_DUMMY) || MonsterRace.isRace(npc, MonsterRace.PEST_PORTAL)) {
            return
        }

        val definition = npc.fetchDefinition() ?: return

        if (!definition.isAttackable || definition.combatLevel <= 0 || definition.combatFollowDistance <= 0)
            return

        val combatLevel = definition.combatLevel
        val attackStrategy = npc.combat.determineStrategy()

        val aggressiveType = if (isAlwaysAggressive(npc, attackStrategy, combatLevel))
            ALWAYS else COMBAT_LEVEL_BASED

        val tolerancePolicy = npc.aggressionTolerancePolicy



        val candidates = AgentUtil
                .getPlayersInProximity(npc, if ((npc is FightCaveNpc || npc is AquaisNeigeNpc)) 64 else getAggressionDistance(npc), CollisionPolicy.NONE)
                .collect(Collectors.toSet())

        npc.debug("sequence aggression -> found ${candidates.size} candidates")

        for (player in candidates) {

            val distance = DistanceUtil.calculateDistance(player, npc)
            val aggressionDistance = getAggressionDistance(npc)

            if (distance <= aggressionDistance) {


                val aggressivityPotionTimerNotFinished = player.combat.aggressivityTimer.finished().not()

                // Heights not the same
                if (player.position.z != npc.position.z) {
                    break
                }

                // 5 minutes aggressivity timer per area if not negated aggression
                if (negateAggression(npc, player) && !aggressivityPotionTimerNotFinished) {
                    npc.debug("aggression -> timer finishes for $player, is not longer aggro")
                    break;
                }

                // the npc should not sequence aggression if it is not marked as aggressive in its definition.
                if (!isBandit(npc) && !isGodWarsMinion(npc) && !isAlwaysAggressive(npc, attackStrategy, combatLevel) && npc !is RockCrab) {
                    if (!definition.isAggressive && !aggressivityPotionTimerNotFinished)
                        break
                }

                // if the aggressive type of the npc is COMBAT_LEVEL_BASED,
                // the npc will not attempt to target a player with twice its combat level.
                if (!isBandit(npc) && !isGodWarsMinion(npc)) {
                    if (aggressiveType == COMBAT_LEVEL_BASED
                        && !aggressivityPotionTimerNotFinished
                        && !isAlwaysAggressive(npc, attackStrategy, combatLevel)
                        && npc !is RockCrab
                        && isTooStrong(player.skillManager.calculateCombatLevel(), combatLevel)) {
                        npc.debug("aggression -> ${player.username} is too strong")
                        break
                    }
                }

                // If the NPC has no movement reach
                if (!CollisionManager.canMove3(npc.position, player.position, npc.size / 2, npc.size / 2) && npc.id != NpcID.SEA_TROLL_QUEEN && npc.id != NpcID.GIANT_SEA_SNAKE) {
                    npc.debug("collision -> is blocked to reach to $player")
                    break
                }

                // If the tile you are on is blocked
/*                if (CollisionManager.blocked(player.position)) {
                    npc.debug("collision -> tile is blocked at $player")
                    if (candidates.contains(player))
                        candidates.remove(player);
                    if (npc.interactingEntity != null) {
                        npc.resetEntityInteraction()
                        npc.motion.clearSteps();
                    }
                    break
                }*/


                if ((player.combat.isInCombat || player.combat.isUnderAttack) && !AreaManager.inMulti(player)) {
                    npc.debug("is underattack -> is blocked to reach to $player")
                    break
                }

                // Region toloreance
                if (player.aggressionTolerance.finished() && !isAlwaysAggressive(npc, attackStrategy, combatLevel)) {
                    if (npc.isMorphed && npc is RockCrab || npc.isHide) {
                        break
                    }
                }

                // Riding a carpet aka travel system
                if (player.appearance.bas?.idle == RugMerchant.BAS_ID) {
                    break
                }

                // Player in tutorial or invisible mode
                if (player.isInTutorial || player.getBoolean(Attribute.INVISIBLE)) {
                    break
                }



                if (npc.combat.negateAggression()) {
                    npc.debug("negateAgression method")
                    break
                }


                // In some circumstances you ignore aggression such as when under npc or when leaving the player alone #NPCMovementCoordinater
                if (timeoutMap.containsKey(player)) {
                    val timePassed = System.nanoTime() - timeoutMap[player]!!
                    if (TimeUnit.MILLISECONDS.toSeconds(timePassed) >= 5)
                        timeoutMap.remove(player)
                    else {
                        npc.debug("aggression -> ignoring ${player.username} for ${5 - TimeUnit.NANOSECONDS.toSeconds(timePassed)} seconds")
                        break
                    }
                }

                if (targetMap.containsKey(player) && !AreaManager.inMulti(player)) { // Don't target player that is already in the target list of another npc
                    break
                }


                // if the tolerance policy of the npc is IN_VICINITY,
                // the npc is no longer aggressive after 5 minutes
                if (tolerancePolicy == IN_VICINITY
                        && !aggressivityPotionTimerNotFinished
                        && player.aggressionTolerance.finished()
                        && !isAlwaysAggressive(npc, attackStrategy, combatLevel)) {
                            npc.debug("aggression -> $player is tolerant")
//                    npc.motion.clearSteps();
//                    npc.resetEntityInteraction()
                    break
                }

                if (!npc.isInside(player) && attackStrategy.type() != AttackType.MELEE && !canSee(npc, player)) {
                    npc.debug("sequence aggression -> projectile path to ${player.username} not possible")
                    npc.combat.submit(TargetIsNotReachable(player))
                    break
                }

/*               if (!npc.combat.canAttackWith(player, attackStrategy, false)) {
                    npc.debug("combat -> can't attack ${player.username} with current attack strategy")
                   npc.motion.clearSteps();
                   npc.resetEntityInteraction()
                   candidates.clear();
                    break
                }*/
/*
                if(!npc.combat.isInReachForAttack(player, true)){
                    npc.debug("combat -> not isInReachForAttack ${player.username} with current attack strategy")
                    break
                }*/

                    targetMap.getOrPut(player) { HashMap() }[npc] = distance
                    npc.debug("sequence aggression -> added ${player.username} to target map")
                    //npc.setEntityInteraction(player)


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
                        else hostileNpcPaths.minByOrNull { it.value }?.let {
                            initiateCombat(nearbyNpc = it.key, player = player)
                        }
                    }
                }

                targetMap.clear()
            }
        }

        npc.onStateChange("sequenced aggression")
    }

    private fun isAlwaysAggressive(
        npc: NPC,
        attackStrategy: AttackStrategy<out Agent>?,
        combatLevel: Int
    ) = ((npc.inWilderness() && npc.id != NpcID.RUNITE_GOLEM && npc.id != 526 && npc.id != 4096 && npc.id != 1158)
            || (npc is FightCaveNpc && npc !is YtHurKot)
            || npc is AquaisNeigeNpc
            || npc.id in 9001..9019
            || npc.id == NpcID.ELVARG_6349
            || npc.id == NpcID.AGRITHNANA
            || npc.id == NpcID.FLAMBEED
            || npc.id == NpcID.KARAMEL
            || npc.id == NpcID.DESSOURT
            || npc.id == NpcID.TREE_SPIRIT
            || npc.id == NpcID.KAMIL_HARD
            || npc.id == NpcID.DESSOUS
            || npc.id == NpcID.DESSOUS_6344
            || npc.id == NpcID.DAMIS
            || npc.id == NpcID.FAREED
            || npc.id == NpcID.JUNGLE_DEMON_HARD
            || npc.id == NpcID.GELATINNOTH_MOTHER
            || npc.id in 4885..4889
            || npc.id == NpcID.CULINAROMANCER_6368
            || npc.id in 1689..1738
            || isBandit(npc)
            || npc.id in 2450..2456
            || attackStrategy is SpinolypAttack
            || (combatLevel > 150 && npc.id != NpcID.RUNITE_GOLEM && npc.id != 526 && npc.id != 4096 && npc.id != 1158)
            || AreaManager.GOD_WARS_AREA.contains(npc)/*
            || npc is RockCrab*/)

    private fun canSee(entity: Entity, target: Entity) : Boolean {
        if (entity is Monster && entity.skipProjectileClipping())
            return true
        else if (entity is Boss && entity.skipProjectileClipping())
            return true
        return withinSight(entity, target)
    }

    fun temporarilyIgnore(player: Player) {
        timeoutMap[player] = System.nanoTime()
    }

    companion object {

        val secondsUntilTolerant = Math.toIntExact(TimeUnit.MINUTES.toSeconds(5))

        private val targetMap = HashMap<Player, HashMap<NPC, Int>>()

        fun evaluateTargetMap() {
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

        fun isBandit(npc: NPC) = npc.id == NpcID.BANDIT || npc.id == NpcID.BANDIT_695

        fun isGodWarsMinion(npc: NPC) = npc.id == NpcID.HOBGOBLIN_2241 || npc.id == NpcID.ORK_2240 ||
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
                is KingBlackDragonBoss -> 40
                is MerodachBoss -> 40
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
                else -> (if (npc.fetchDefinition().combatFollowDistance <= 3) 3 else npc.fetchDefinition().combatFollowDistance / 1.5).toInt()
            }
        }

        private fun isTooStrong(playerCombat: Int, npcCombat: Int) = playerCombat > npcCombat * 2
    }

}