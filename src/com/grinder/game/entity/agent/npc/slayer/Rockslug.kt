package com.grinder.game.entity.agent.npc.slayer

import com.grinder.game.content.skill.skillable.impl.slayer.SlayerRewards
import com.grinder.game.entity.agent.combat.onIncomingHitApplied
import com.grinder.game.entity.agent.combat.onIncomingHitQueued
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.game.model.sound.Sound
import com.grinder.game.task.TaskManager
import com.grinder.game.task.impl.NPCDeathTask
import com.grinder.util.ItemID
import com.grinder.util.random.RandomUtil

/**
 * Rockslug slayer class. Monster must be finished with salt in order to kill.
 */
class Rockslug(id: Int, pos: Position) : SlayerMonster(id, pos) {

    companion object {

        // The maximum HP a gargoyle may have to shatter it.
        private val SALT_HP_THRESHOLD = 5
        private val saltAnim = Animation(1574)
        private val saltSound = Sound(2567)

        /**
         * Attempts to salt slug if the player has the required materials.
         *
         * @param player The player attempting to salt the slug.
         * @param n The Rockslug.
         */
        fun saltSlug(player: Player,
                     n: NPC,
                     newHp: Int = n.hitpoints,
                     autoSalt: Boolean = false,
                     sabreEquip: Boolean
        ): Boolean {

            if (n.isDying)
                return false

            if (autoSalt && !sabreEquip && !player.slayer.unlocked[SlayerRewards.Rewards.SLUG_SALTER.rewardIndex])
                return false

            if (newHp > SALT_HP_THRESHOLD) {
                if (!autoSalt) // delete a bag of salt if they did it manually
                    player.inventory.delete(ItemID.BAG_OF_SALT, 1)
                player.performAnimation(saltAnim)
                player.message("The rockslug is not weak enough to enough to be affected by the salt.")
                return false
            }

            if (!sabreEquip) {
                if (!player.inventory.contains(ItemID.BAG_OF_SALT))
                    return false
                player.performAnimation(saltAnim)
                player.playSound(saltSound)
                player.inventory.delete(ItemID.BAG_OF_SALT, 1)
            }
            n.appendDeath()
            player.message("The rockslug shrivels up and dies.")
            return true
        }
    }

    init {
        combat.onIncomingHitApplied {
            val newHP = hitpoints - totalDamage
            if (newHP <= SALT_HP_THRESHOLD && attacker.isPlayer) {
                val sabreEquip = context.used(ItemID.BRINE_SABRE)
                saltSlug(attacker.asPlayer, this@Rockslug, newHP, true, sabreEquip)
            }
        }
        combat.onIncomingHitQueued {
            val newHP = hitpoints - totalDamage
            if (newHP <= 0) {
                totalDamage = hitpoints
                hitpoints += RandomUtil.getRandomInclusive(2) + 1
            }
        }

    }

    override fun appendDeath() {
        if (!isDying) {
            TaskManager.submit(NPCDeathTask(this, 1, true))
            isDying = true;
        }
    }
}