package com.grinder.game.entity.agent.npc.monster.boss.impl.hydra.task

import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.npc.monster.boss.BossTask
import com.grinder.game.entity.agent.npc.monster.boss.impl.hydra.AlchemicalHydraBoss
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Graphic
import com.grinder.game.model.Position
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import java.util.function.Consumer

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-31
 */
class LightningTileTask(bossNPC: AlchemicalHydraBoss, private val position: Position)
    : BossTask<AlchemicalHydraBoss>(bossNPC, 20, 5, 1, false) {

    override fun onCycle(cycle: Int) {
        playerList.forEach(Consumer { player: Player -> player.packetSender.sendGraphic(Graphic(1664), position) })
        if (cycle == 4) {
            val base = boss.localReferencePoint
            val tiles = arrayOf(
                    base.transform(44, 15, 0),
                    base.transform(34, 15, 0),
                    base.transform(44, 24, 0),
                    base.transform(33, 24, 0)
            )
            for (tile in tiles) {
                val projectile = Projectile(position, tile, LIGHTNING_PROJECTILE)
                projectile.sendProjectile()
                projectile.onArrival {
                    TaskManager.submit(createProjectileTask(tile))
                }
            }
        }
    }

    private fun createProjectileTask(tile: Position): Task {
        return object : Task(1) {

            private var x = 0
            private var y = 0

            override fun execute() {

                val moved = tile.transform(x, y, 0)

                playerList.removeIf { !it.isActive }

                if (playerList.isEmpty()) {
                    stop()
                } else {
                    playerList.forEach(Consumer { player: Player ->
                        player.packetSender.sendGraphic(Graphic(1666), moved)
                        if (player.position.sameAs(moved)) {
                            player.motion.impairMovement(5)
                            player.combat.queue(Damage.create(10, 30))
                            player.sendMessage("<col=ff1a1a>The electricity temporarily paralyzes you!")
                        }
                    })
                    for (player in playerList) {
                        if (player.position.getDistance(moved) < 64) {
                            if (player.x < moved.x) x-- else if (player.x > moved.x) x++
                            break
                        }
                    }
                    if (y++ == 36) stop()
                }
            }
        }
    }

    companion object {
        private val LIGHTNING_PROJECTILE = ProjectileTemplate.builder(1665)
                .setSourceSize(5)
                .setStartHeight(70)
                .setEndHeight(20)
                .setSpeed(44)
                .setDelay(3)
                .build()
    }
}