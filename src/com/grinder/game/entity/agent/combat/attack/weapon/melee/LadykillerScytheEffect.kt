package com.grinder.game.entity.agent.combat.attack.weapon.melee


import com.grinder.game.content.item.charging.impl.LadykillerScythe
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.strategy.MeleeAttackStrategy
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.inWilderness
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.Direction
import com.grinder.game.model.Graphic
import com.grinder.game.model.areas.AreaManager
import com.grinder.util.Priority
import com.grinder.util.collection.addMany
import com.grinder.util.oldgrinder.EquipSlot


object LadykillerScytheEffect {

    /**
     * Check if the player has the scythe equipped and is in a multicombat area.
     */
    fun canUse(player: Player): Boolean {
        return player.equipment.containsAtSlot(EquipSlot.WEAPON, 15933)
    }

    /**
     *
     */
    fun postHit(player: Player, target: Agent) {

        val weapon = player.equipment[EquipSlot.WEAPON]

        if(LadykillerScythe.getCharges(weapon) > 0) {
            LadykillerScythe.decrementCharges(player, weapon)
        } else {
            player.message("Your Scythe of Vitur has run out of charges.")
            return
        }
        //delayBy(1) {
            performGraphic(player)
        //}

        // Find NPCs and players in front of the attacker that can be attacked
        val attackable = ArrayList<Agent?>()
                .addMany(player.localPlayers, player.localNpcs)
                .asSequence()
                .filterNotNull()
                .filter { it != target }
                .filter { if(it.isNpc) it.asNpc.fetchDefinition().isAttackable else it.combat.canBeAttackedBy(player, true) }
                .filter { if(it is Player) it.inWilderness() else true}
                .filter { AreaManager.inMulti(it) }
                .filter { player.lastFacingDirection.inFront( player.position, it.position, 1) }
                .take(2)
                .toMutableList()

        // If there are less than 3 possible targets, apply multiple hits to the same if it is large
        if(attackable.size < 3) {
            if(target.isNpc && target.asNpc.fetchDefinition().size >= 2) {
                val diff = (3 - attackable.size).coerceAtMost(2)
                repeat(diff) { attackable.add(target) }
            }
        }

        // Apply hits, dealing 50% and 25% damage for the 2nd and 3rd hits respectively
        attackable.forEachIndexed {idx, it ->

            val qH = Hit(player, it, MeleeAttackStrategy.INSTANCE, true, 1, 1.0/ (idx*2.0 + 2.0))
                                .setHandleAfterHitEffects(false)

            player.combat.queueOutgoingHit(qH)
        }
    }

    fun performGraphic(player: Player) {

        val gfx = when(player.lastFacingDirection) {
            /*Direction.SOUTH, Direction.SOUTH_EAST -> 506
            Direction.NORTH, Direction.NORTH_WEST -> 478
            Direction.EAST, Direction.NORTH_EAST -> 1231
            else -> 1172*/
            Direction.SOUTH, Direction.SOUTH_EAST -> 478
            Direction.NORTH, Direction.NORTH_WEST -> 506
            Direction.EAST, Direction.NORTH_EAST -> 1172
            else -> 1231
        }

        //player.performGraphic(Graphic(gfx, 96, 20))
        Graphic.sendGlobal(Graphic(gfx,10, 20, Priority.HIGH), player.combat.target.centerPosition)
    }
}
