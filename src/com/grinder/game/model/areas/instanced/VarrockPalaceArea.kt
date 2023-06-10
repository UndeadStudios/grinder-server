package com.grinder.game.model.areas.instanced

import com.grinder.game.World
import com.grinder.game.content.miscellaneous.TravelSystem
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022.setState
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022State
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.Monster
import com.grinder.game.entity.agent.npc.monster.MonsterEvents
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Boundary
import com.grinder.game.model.areas.InstancedArea
import com.grinder.game.task.TaskManager
import com.grinder.util.Executable
import com.grinder.util.Misc
import java.util.*
import kotlin.random.Random

/**
 * This area was used for the christmas event in 2019.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   20/12/2019
 * @version 1.0
 */
class VarrockPalaceArea(player: Player, val guards: List<Monster>, completeAction: Executable)
    : InstancedArea(Boundary(3185, 3235, 3457, 3509)) {

    private var aliveGuardsCount = guards.size

    init {
        guards.forEach {
            it.onEvent {
                if(it == MonsterEvents.DYING){
                    if(--aliveGuardsCount <= 0){
                        TravelSystem.fadeTravelAction(player,
                                checkBusy = false,
                                checkSpam = false,
                                state = 2,
                                duration = 3,
                                tickDelay = 2) {
                            destroy()
                            player.area = null
                            player.moveTo(player.position.clone().setZ(0))
                            completeAction.execute()
                        }
                    }
                }
            }
            World.npcAddQueue.add(it)
            add(it)
        }
        TaskManager.submit(1) {
            guards.forEach { guardMonster ->
                guardMonster.combat.initiateCombat(player)
            }
        }
        TaskManager.submit(Random.nextInt(2, 4)) {
            if(Random.nextBoolean()) {
                guards.forEach { guardMonster ->
                    guardMonster.say(Misc.randomString("Charge!!", "Attack!", "For the queen!"))
                }
            }
        }
    }

    override fun enter(agent: Agent?) {
        if(agent is Player) {
            super.enter(agent)
        }
    }

    override fun leave(agent: Agent?) {
        if(agent is Player) {
            super.leave(agent)
            if(agent.isLoggedIn){
                val destination = agent.position.clone().setZ(0)
                TravelSystem.fadeTravel(agent,
                        checkBusy = false,
                        checkSpam = false,
                        state = 2,
                        duration = 3,
                        tickDelay = 2,
                        destination = destination)
            } else
                agent.moveTo(agent.position.clone().setZ(0))
            agent.setState(Christmas2022State.TALK_TO_SANTA_EX_WIFE)
        }
    }

    override fun isMulti(agent: Agent?) = true
    override fun handleDeath(player: Player?, killer: Optional<Player>?) = false

    override fun handleDeath(npc: NPC?) = false

    override fun canAttack(attacker: Agent?, target: Agent?) = true
    override fun canEat(player: Player?, itemId: Int) = false

    override fun process(agent: Agent?) {

    }

    override fun canTeleport(player: Player?) = false

    override fun canTrade(player: Player?, target: Player?) = false

    override fun canDrink(player: Player?, itemId: Int) = false

    override fun handleObjectClick(player: Player?, obj:GameObject, type: Int) = false

    override fun onPlayerRightClick(player: Player?, rightClicked: Player?, option: Int) {

    }

    override fun dropItemsOnDeath(player: Player?, killer: Optional<Player>?) = false
    override fun defeated(player: Player?, agent: Agent?) {
    }
}