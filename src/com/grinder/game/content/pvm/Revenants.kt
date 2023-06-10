package com.grinder.game.content.pvm

import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.getInt
import com.grinder.game.entity.incInt
import com.grinder.game.model.Graphic
import com.grinder.game.model.attribute.Attribute
import com.grinder.util.Misc
import kotlin.random.Random

/**
 * Handles revenant health restoration.
 *
 * see https://oldschool.runescape.wiki/w/Revenants
 * see https://www.reddit.com/r/2007scape/comments/7gezst/revenants_healing/
 *
 * TODO: maybe create a monster implementation of revenants to handle these mechanics.
 *
 * @author  Unknown
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/08/2020
 */
object Revenants {

    /**
     * A hash collection of the Revenant [NPC] ids.
     */
    private val revenantNpcIds = hashSetOf(7881, 7931, 7932, 7933, 7934, 7935, 7936, 7937, 7938, 7939, 7940)

    /**
     * Maximum number of times a revenant can heal itself in a combat session.
     */
    private const val MAX_HEALING_IN_COMBAT = 6

    /**
     * Sequence the heal mechanic.
     */
    @JvmStatic
    fun onSequence(npc: NPC) {
        if(npc.isActive && npc.isAlive) {
            if (npc.hitpoints < npc.maximumHitpoints / 2) {
                if (Misc.randomChance(3F)) {
                    if (revenantNpcIds.contains(npc.id)) {
                        if (npc.getInt(Attribute.REVENANT_HEAL_COUNT, 0) < MAX_HEALING_IN_COMBAT) {
                            val healModifier = Random.nextInt(10, 20) / 100.0
                            val maxAmountToHeal = npc.maximumHitpoints - npc.hitpoints
                            val amountToHeal = npc.maximumHitpoints.times(healModifier).coerceAtMost(maxAmountToHeal.toDouble()).toInt()
                            npc.hitpoints += amountToHeal
                            npc.performGraphic(Graphic(1221))
                            npc.incInt(Attribute.REVENANT_HEAL_COUNT, 1)
                        }
                    }
                }
            }
        }
    }
}