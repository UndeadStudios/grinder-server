package com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.task

import com.grinder.game.World.npcAddQueue
import com.grinder.game.World.npcRemoveQueue
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.SpecialAttackStrategy
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.HitTemplateBuilder
import com.grinder.game.entity.agent.npc.NPCFactory
import com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.GalvekBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.GalvekConstants
import com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.GalvekPhase
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Position
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.Misc
import com.grinder.util.NpcID.TSUNAMI
import java.util.stream.Collectors


/**
 * Sends a wall of 11 waves with one gap that the player must walk through
 *
 * @author Pea2nuts
 */
class GalvekTsunamiWall(val boss: GalvekBoss) {

    val playerList: MutableList<Player> = boss.playerStream(50).collect(Collectors.toList())

    val hitTemp: HitTemplate = HitTemplateBuilder(AttackType.SPECIAL)
        .setIgnoreAmmunitionEffects(true)
        .setIgnorePrayer(true)
        .setIgnorePoisonEffects(true)
        .setIgnoreAttackStats(true)
        .setIgnoreStrengthStats(true)
        .setDamageRange(75..110)
        .build()

    fun start() {
        val numWaves = 11
        val direction = TsunamiDirection.random()
        var remaining = numWaves

        // Loop through 11 times to send each projectile in a line
        for(i in 0 until numWaves) {

            val projectilePos: Position = boss.position.copy().transform(direction.offsetX + i, direction.offsetY, 0)

            // Make the boss face toward the projectiles
            if(i == 0) {
                boss.positionToFace = projectilePos
            }
            val projectile = Projectile(boss.position, projectilePos, GalvekConstants.TSUNAMI_PROJECTILE.setSpeed(55 + 2 / (i + 1)).build())
            projectile.sendProjectile()
            projectile.onArrival {
                remaining--

                // Once all projectiles arrive we run this on the arrival of the final one
                // If we don't wait until they all arrive, some waves will move a tick faster
                // than others. Ugly way to do this but it works.
                if (remaining == 0) {
                    sendWaves(numWaves, direction)
                }
            }
        }
    }

    private fun sendWaves(numWaves: Int, direction: TsunamiDirection) {
        val hole = Misc.random(0, numWaves - 1)
        for (k in 0 until numWaves) {
            val pos: Position = boss.position.copy().transform(direction.offsetX + k, direction.offsetY, 0)

            if (k == hole) {
                continue
            }

            val tsunami = NPCFactory.create(TSUNAMI, pos)
            tsunami.face = direction.facingDirection

            npcAddQueue.add(tsunami)
            tsunami.motion.setCanReTrace(true)
            val y = if (direction === TsunamiDirection.NORTHERN) -10 else 10
            tsunami.motion.enqueuePathToWithoutCollisionChecks(tsunami.x,  tsunami.y + y)

            TaskManager.submit(object : Task(1, tsunami, false) {
                var amt = 0
                override fun execute() {

                    // Stop this wave
                    if(boss.isDying || boss.phase !== GalvekPhase.WATER || amt == 13) {
                        this@GalvekTsunamiWall.boss.tsunamiActive = false
                        npcRemoveQueue.add(tsunami)
                        stop()
                    }

                    if(amt < 11) {
                        for(target in playerList) {
                            if(target == null || target.isDying) {
                                continue
                            }

                            if (target != null && target.position.equals(tsunami.position)) {
                                val hit = Hit(boss, target, SpecialAttackStrategy.INSTANCE, hitTemp)
                                target.combat.queue(hit)
                                amt = 12// wave crashes if it hits
                                break
                            }
                        }
                    }

                    if(amt == 12) {
                        tsunami.performAnimation(GalvekConstants.TSUNAMI_DEATH_ANIM)
                    }
                    amt++

                }
            })
        }

    }
}