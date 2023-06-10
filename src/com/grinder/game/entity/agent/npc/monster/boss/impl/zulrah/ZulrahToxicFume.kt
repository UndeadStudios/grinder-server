package com.grinder.game.entity.agent.npc.monster.boss.impl.zulrah

import com.grinder.game.entity.`object`.DynamicGameObject
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.model.Direction
import com.grinder.game.model.Position
import com.grinder.game.model.areas.instanced.ZulrahShrine
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/12/2019
 * @version 1.0
 */
class ZulrahToxicFume(val zulrahBoss: ZulrahBoss, position: Position) {

    private val obj: DynamicGameObject = DynamicGameObject.createPublic(11700, position, 10, Direction.NORTH.toInteger())

    fun spawn() {
//        println("Spawning fume at ${position.compactString()}")
//        ObjectManager.add(obj, true)

        val area = zulrahBoss.area

        if (!zulrahBoss.isActive) return;

        if (area is ZulrahShrine) {
            area.add(obj)


            TaskManager.submit(object : Task(1, zulrahBoss, false) {

                var cycle = 0

                override fun execute() {

                    if (zulrahBoss.isDying || cycle >= 20) {
                        stop()
                        return
                    }

                    if (cycle % 2 == 0) {
                        val target = zulrahBoss.owner
                        if (getCenterPosition().isWithinDistance(target.position, 1)) {
                            target.combat.queue(Damage.create(0, 10))
                        }
                    }

                    cycle++
                }

                override fun stop() {
                    super.stop()
                    area.remove(obj)
                    zulrahBoss.fumePositions.remove(obj.position)
                }
            })

        }
    }
    fun getCenterPosition(): Position = obj.centerPosition!!

}