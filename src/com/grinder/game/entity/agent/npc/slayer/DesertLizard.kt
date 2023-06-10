package com.grinder.game.entity.agent.npc.slayer

import com.grinder.game.entity.agent.combat.onIncomingHitApplied
import com.grinder.game.entity.agent.combat.onIncomingHitQueued
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.Position
import com.grinder.util.ItemID
import com.grinder.util.random.RandomUtil

/**
 * Handles finishing off the Desert Lizard using an Ice cooler.
 * @author Blake
 */
class DesertLizard(id: Int, pos: Position) : SlayerMonster(id, pos) {

    companion object {
        private const val HP_THRESHOLD = 4

        fun finishOff(player: Player,
                      n: NPC,
                      hp: Int = n.hitpoints
        ): Boolean {
            if (n.isDying)
                return false

            if (hp > HP_THRESHOLD) {
                player.message("The lizard isn't weak enough to be affected by the icy water.")
                return false
            }

            if (!player.inventory.contains(ItemID.ICE_COOLER))
                return false

            player.inventory.delete(ItemID.ICE_COOLER, 1)
            player.message("The lizard shudders and collapses from the freezing water.")
            n.appendDeath()
            return true
        }
    }

    init {
        combat.onIncomingHitQueued {
            val newHP = hitpoints - totalDamage;
            if (newHP <= 3) {
                if (newHP <= 0) {
                    totalDamage = hitpoints
                    hitpoints += RandomUtil.getRandomInclusive(2) + 1
                }
            }
        }
        // Players requested so you automatically use ice coolers to finish off the npc
        combat.onIncomingHitApplied {
            val newHP = hitpoints - totalDamage
            if (newHP <= 3 && attacker.isPlayer) {
                finishOff(attacker.asPlayer, asNpc, newHP)
            }
        }

    }

}