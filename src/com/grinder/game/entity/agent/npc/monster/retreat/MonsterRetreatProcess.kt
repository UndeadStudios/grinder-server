package com.grinder.game.entity.agent.npc.monster.retreat

import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.Monster
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import java.util.concurrent.TimeUnit

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   07/09/2019
 * @version 1.0
 */
class MonsterRetreatProcess {

    fun sequence(npc: NPC){

        if(npc is Monster && npc.skipNextRetreatSequence())
            return

        if(npc is Boss && npc.skipNextRetreatSequence())
            return

        val retreatPolicy = npc.retreatPolicy

        if(retreatPolicy != MonsterRetreatPolicy.NEVER || npc.fetchDefinition().doesRetreat()){
            val combat = npc.combat
            val failedToReachCount = combat.failedToReachCount.get()
            val failedToAttackCount = combat.failedAttackCounter.get()
            val outOfCombatCount = combat.outOfCombatCount.get()
            val successfulAttackCount = combat.getTimeSinceLastAttack(TimeUnit.SECONDS).toInt()
            //npc.say("sequence retreating -> not retreating: $failedToReachCount, $failedToAttackCount, $successfulAttackCount")
            when {
                combat.isBeingAttacked -> {
                    if (failedToReachCount > 12 || failedToAttackCount > 5) {
                        npc.debug("sequence retreating -> retreating from combat: $failedToReachCount > 20 || $failedToAttackCount > 5")
                        retreat(npc)
                    }
                }
                combat.hasTarget() -> {

                    val target = combat.target!!
                    if (!target.position!!.isWithinDistance(npc.position, 20)) {
                        npc.debug("sequence retreating -> retreating as target is out of reach")
                        retreat(npc)
                    } else if (!target.position!!.isWithinDistance(npc.spawnPosition, 20) && npc !is Boss) {
                        npc.debug("sequence retreating -> retreating as target is far from spawn position")
                        retreat(npc)
                    } else if (target.combat.isBeingAttacked) {
                        //if(npc.combat.canSwitch()){
                            if(successfulAttackCount > 5 && failedToReachCount > 20){
                                npc.debug("sequence retreating -> retreating from combat: $successfulAttackCount == 0 && $failedToReachCount > 20")
                                retreat(npc)
                            }
                        //}
                    } else if (target.combat.isBeingAttacked.not()) {
                        if (retreatPolicy == MonsterRetreatPolicy.RETREAT_IF_OUT_OF_COMBAT) {


                            if (outOfCombatCount >= 100) { // 60 seconds
                                retreat(npc)
                                combat.outOfCombatCount.set(0)
                            }
                        }
                    }
                }
            }
        }
        npc.onStateChange("sequenced retreating")
    }

    private fun retreat(npc: NPC){
        npc.resetEntityInteraction()
        npc.combat.reset(true)
        npc.movementCoordinator.retreatHome()
        npc.regenerateFullHealth()
    }
}