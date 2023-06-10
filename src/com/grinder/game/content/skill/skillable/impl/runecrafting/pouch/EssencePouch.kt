package com.grinder.game.content.skill.skillable.impl.runecrafting.pouch

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.Skill
import com.grinder.util.ItemID.PURE_ESSENCE
import com.grinder.util.ItemID.RUNE_ESSENCE
import java.util.concurrent.ThreadLocalRandom

class EssencePouch(var capacity : Int, private val type : PouchType) {

    /**
     *  Amount of rune essence in the pouch
     */
    private var runeEssence: Int = 0

    /**
     *  Amount of pure essence in the pouch
     */
    private var pureEssence: Int = 0

    fun totalEssence() = pureEssence + runeEssence

    fun space() = capacity - totalEssence()

    fun isFull() = totalEssence() == capacity

    /**
     * Add pure and rune essence from the player's inventory to the pouch
     */
    fun addEssence(player: Player) {

        if(player.skills.getLevel(Skill.RUNECRAFTING) < type.levelReq) {
            player.message("You need a Runecrafting level of ${type.levelReq} to use this pouch.")
            return
        }

        if(!player.inventory.contains(PURE_ESSENCE) && !player.inventory.contains(RUNE_ESSENCE)) {
            player.message("You do not have any rune essence.")
            return;
        }

        if(isFull()) {
            player.message("Your ${type.name.toLowerCase()} pouch is full.")
            return
        }

        val initialSpace = space()
        var left = space()

        // Remove pure essence first, then rune essence until the pouch is full
        while(left > 0) {
            if(player.inventory.contains(PURE_ESSENCE)) {
                player.inventory.delete(PURE_ESSENCE, 1)
                pureEssence++
                left--
            } else if(player.inventory.contains(RUNE_ESSENCE)) {
                player.inventory.delete(RUNE_ESSENCE, 1)
                runeEssence++
                left--
            } else {
                break
            }
        }

        // Chance to degrade the pouch
        degrade(player, initialSpace - space())

        player.message("Your ${type.name.toLowerCase()} pouch now contains $runeEssence rune essence and $pureEssence pure essence.")
    }

    /**
     * Withdraws essence from the pouch and adds it to the player's inventory.
     */
    fun withdraw(player: Player) {

        if(player.inventory.isFull) {
            player.message("Your inventory is full.")
            return
        }

        // Calculate how much essence to withdraw
        val pureWithdraw = player.inventory.countFreeSlots().coerceAtMost(pureEssence)
        val runeWithdaw = player.inventory.countFreeSlots()
                .coerceAtMost(runeEssence)
                .coerceAtMost(capacity - pureWithdraw)

        player.inventory.add(PURE_ESSENCE, pureWithdraw)
        player.inventory.add(RUNE_ESSENCE, runeWithdaw)
        
        pureEssence -= pureWithdraw
        runeEssence -= runeWithdaw

        if(space() == capacity) {
            player.message("Your ${type.name.toLowerCase()} pouch is now empty.")
        } else {
            player.message("Your ${type.name.toLowerCase()} pouch now contains $runeEssence rune essence and $pureEssence pure essence.")
        }
    }

    /**
     * Tell the player how much essence is currently stored.
     */
    fun check(player: Player) {
        player.message("Your ${type.name.toLowerCase()} pouch contains $runeEssence rune essence and $pureEssence pure essence.")
    }

    /**
     * Degrade the pouch.
     */
    fun degrade(player : Player, amount : Int) {
        if(ThreadLocalRandom.current().nextInt(0, 100) < (amount * type.degradeChance)) {
            capacity -= 3

            if(capacity > 0) {
                player.message("Your ${type.name.toLowerCase()} pouch degrades slightly.")
            } else {
                player.message("Your ${type.name.toLowerCase()} pouch degrades completely.")
                player.inventory.delete(type.itemId, 1)
            }
        }
    }
}