package com.grinder.game.entity.agent.npc.slayer

import com.grinder.game.World
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerRewards
import com.grinder.game.entity.agent.combat.onIncomingHitApplied
import com.grinder.game.entity.agent.combat.onIncomingHitQueued
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.MonsterEvents
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplateBuilder
import com.grinder.game.model.sound.Sound
import com.grinder.game.task.TaskManager
import com.grinder.game.task.impl.NPCDeathTask
import com.grinder.util.Priority
import com.grinder.util.random.RandomUtil

/**
 * Gargoyle slayer class. Unique mechanic that a hammer needs to be used to smash the stone monster.
 */
class Gargoyle(id: Int, pos: Position) : SlayerMonster(id, pos) {

    companion object {
        // The maximum HP a gargoyle may have to shatter it.
        public const val HAMMER_HP_THRESHOLD = 8
        private val smashAnim = Animation(401)
        private val smashSound = Sound(2567)
        private val hammerProj = ProjectileTemplateBuilder(1452)
                .setHeights(11, 144)
                .setCurve(15)
                .build()
        /**
         * Attempts to shatter a gargoyle if they are within the HP limits, and have appropriate equipment.
         *
         * @param player The player attempting to shatter the gargoyle.
         * @param n The gargoyle.
         */
        fun breakGargoyle(
                player: Player
                , n: NPC,
                currHP:Int=n.hitpoints,
                autoSmash: Boolean=false,
                hammerEquip: Boolean
        ): Boolean {

            if (n.isDying)
                return false

            if (autoSmash && !hammerEquip && !player.slayer.unlocked[SlayerRewards.Rewards.GARGOYLE_SMASHER.rewardIndex])
                return false

            player.performAnimation(smashAnim)
            if (currHP > HAMMER_HP_THRESHOLD) {
                player.message("The gargoyle is not weak enough to shatter.")
                return false
            }

            // check if we have a hammer in inventory.
            if ((player.inventory.contains(4162) || hammerEquip || player.inventory.contains(21742)) &&
                    n.inBoundaries(player, 1)) { // rockhammer/granitehammer
                n.npcTransformationId = n.id + 1
                n.performAnimation(Animation(1520, Priority.HIGH))
                player.playSound(smashSound)
                n.appendDeath()
                return true
            } else if (player.inventory.contains(21754)) { // throwhammers
                player.inventory.delete(21754, 1)
                Projectile(player, n, hammerProj).onArrival {
                    n.performAnimation(Animation(1520))
                    n.npcTransformationId = n.id + 1
                    n.appendDeath()
                    player.playSound(smashSound)
                }.sendProjectile()
                return true
            }
            return false
        }
    }

    init {
        combat.onIncomingHitQueued {
            val newHP = hitpoints - totalDamage
            if (newHP <= 9) {
                if (newHP <= 0) {
                    totalDamage = hitpoints
                    hitpoints += RandomUtil.getRandomInclusive(2) + 1
                }
            }
        }
        combat.onIncomingHitApplied {
            val newHP = hitpoints - totalDamage
            if (newHP <= 9 && attacker.isPlayer) {
                val hammerEquip = context.used(21742)
                breakGargoyle(attacker.asPlayer, asNpc, newHP, true, hammerEquip)
            }
        }
        onEvent {
            if (it == MonsterEvents.ADDED){
                if (npcTransformationId != -1)
                    npcTransformationId = id - 1
            }
//            if(it == MonsterEvents.DYING)
//                World.npcRemoveQueue.add(this);
        }
    }

    override fun appendDeath() {
        if (!isDying) {
            TaskManager.submit(NPCDeathTask(this, 1, true))
            isDying = true;
        }
    }
}