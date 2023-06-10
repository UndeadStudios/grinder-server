package com.grinder.game.entity.agent.npc.monster.boss.impl.hydra.task

import com.grinder.game.World
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.npc.monster.boss.BossTask
import com.grinder.game.entity.agent.npc.monster.boss.impl.hydra.AlchemicalHydraBoss
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.event.impl.PositionChangedEvent
import com.grinder.game.entity.agent.player.subscribe
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Position
import com.grinder.game.model.TileGraphic
import com.grinder.util.time.SecondsTimer
import java.util.function.Consumer

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-31
 */
class FireTileTask(boss: AlchemicalHydraBoss, private val position: Position)
    : BossTask<AlchemicalHydraBoss>(boss, 20, 50, 1, false) {

    private val damageTimer = SecondsTimer()

    override fun onCycle(cycle: Int) {}

    override fun stop() {
//        CollisionManager.clearClipping(position.x, position.y, position.z)
        super.stop()
    }

    init {
//        CollisionManager.addClipping(position.x, position.y, position.z, CollisionManager.BLOCKED_TILE)
        playerList.forEach(Consumer { player: Player ->
            player.subscribe {
                if (it is PositionChangedEvent) {
                    if (damageTimer.finished()) {
                        if (position.sameAs(it.currentPosition)) {
                            player.combat.queue(Damage.create(1, 15))
                            damageTimer.start(3)
                        }
                    }
                }
                isStopped
            }
            //player.packetSender.sendGraphic(Graphic(1668), position)
            World.spawn(TileGraphic(player.position, Graphic(1668)));
        })
    }
}