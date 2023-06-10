package com.grinder.game.entity.agent.npc.monster.boss.impl.hydra.task

import com.grinder.game.World
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonEffect
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonType
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask
import com.grinder.game.entity.agent.npc.monster.boss.BossTask
import com.grinder.game.entity.agent.npc.monster.boss.impl.hydra.AlchemicalHydraBoss
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Graphic
import com.grinder.game.model.Position
import com.grinder.game.model.TileGraphic
import com.grinder.util.Misc
import java.util.function.Consumer

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-31
 */
class PoisonedTileTask(bossNPC: AlchemicalHydraBoss, private val position: Position)
    : BossTask<AlchemicalHydraBoss>(bossNPC, 20, 10, 1, true) {

    override fun onCycle(cycle: Int) {
        playerList.forEach(Consumer { player: Player ->
            if (player.position.sameAs(position)) {
                PoisonEffect.applyPoisonTo(player, PoisonType.MILD)
                player.combat.queue(Damage(6, DamageMask.POISON))
            }
            when (cycle) {
                1 -> World.spawn(TileGraphic(position, Graphic(1645)))
                2 -> World.spawn(TileGraphic(position, Graphic(1654 + Misc.randomInt(7))))
            }
        })
    }
}