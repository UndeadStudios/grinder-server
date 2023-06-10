package com.grinder.game.entity.agent.npc.monster.boss.impl.vorkath.task

import com.grinder.game.collision.CollisionManager
import com.grinder.game.entity.agent.npc.monster.boss.impl.vorkath.VorkathBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.vorkath.ZombifiedSpawn
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.*
import com.grinder.game.model.areas.instanced.VorkathArea
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.task.Task
import com.grinder.util.Misc
import java.util.*

/**
 * Zombified Ice Dragonfire: Vorkath freezes the player in place with damageless ice dragonfire, summons a zombified spawn, and is immune to all damage while the spawn is alive.
 * The spawn moves toward the player and, if not killed first, explodes on impact dealing up to 60 damage scaling off of its remaining Hitpoints.
 * Players should queue the Crumble Undead spell and cast on the spawn as soon as it lands to prevent damage during this special attack.
 * Once the spawn is killed, the special attack ends which frees the player and removes Vorkath's immunity.
 *
 * @author  Leviticus
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   19/10/2019
 * @version 1.0
 */
class ZombifiedTask(private val vorkathBoss: VorkathBoss, val target: Player) : Task(1) {

    override fun execute() {
        stop()
        vorkathBoss.performAnimation(Animation(7960))
        val spawnPosition = createPreferredSpawnPosition(target.position)
        val spawnProjectile = Projectile(vorkathBoss.centerPosition, spawnPosition, PROJECTILE_TEMPLATE)
        spawnProjectile.sendProjectile()
        spawnProjectile.onArrival {
            ZombifiedSpawn(vorkathBoss, spawnPosition, target).spawn()
        }
    }

    companion object {

        fun createPreferredSpawnPosition(referencePosition: Position) : Position{

            val x = referencePosition.x
            val y = referencePosition.y

            val baseX: Int = x shr 8 shl 8
            val baseY: Int = y shr 8 shl 8

            val dirs = Direction.VALID_DIRECTIONS.asList();
            Collections.shuffle(dirs)

            for (i in 1..3) {
                for (dir: Direction in dirs) {
                    var xRandom = 1
                    var yRandom = 1
                    if (dir.x != 0 || (dir.x != 0 && dir.y != 0 && Misc.randomBoolean())) {
                        xRandom = 8
                        yRandom = 1 + Misc.random(7)
                    } else {
                        xRandom = 1 + Misc.random(7)
                        yRandom = 8
                    }
                    val spawnPos = Position(x + dir.x * xRandom, y + dir.y * yRandom)

                    val offsetX = spawnPos.x - baseX
                    val offsetY = spawnPos.y - baseY

                    if (!CollisionManager.blocked(spawnPos) && VorkathArea.fightBounds.contains(Position(offsetX, offsetY))) {
                        return spawnPos
                    }
                }
            }

            return referencePosition.clone()
        }

        private val PROJECTILE_TEMPLATE = ProjectileTemplate
                .builder(1484)
                .setSourceSize(5)
                .setSourceOffset(0)
                .setHeights(60, 0)
                .setCurve(45)
                .setSpeed(45)
                .setDelay(40)
                .build()
    }
}