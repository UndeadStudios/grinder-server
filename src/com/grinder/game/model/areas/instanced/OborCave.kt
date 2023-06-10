@file:JvmName("OborCave")
package com.grinder.game.model.areas.instanced

import com.grinder.game.content.miscellaneous.TravelSystem
import com.grinder.game.content.skill.skillable.impl.agility.obstacle.impl.RockObstacle
import com.grinder.game.entity.`object`.DynamicGameObject
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.npc.monster.boss.impl.Obor
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Boundary
import com.grinder.game.model.Direction
import com.grinder.game.model.Position
import com.grinder.game.model.areas.InstancedBossArea
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import java.util.*

val entrance = Position(3091, 9815)

/**
 * Determines if the player has the correct requirements to enter the lair of Odor.
 * @param pl The player.
 */
fun enterLair(pl: Player) {
    if (!pl.inventory.contains(ItemID.GIANT_KEY)) {
        pl.sendMessage("It's locked.")
        pl.packetSender.sendSound(Sounds.USE_KEY_ON_LOCKED_DOOR)
        return
    }
    DialogueBuilder(DialogueType.OPTION).setOptionTitle("Enter Obor's Lair?")
            .firstOption("Yes.") {
                DialogueBuilder(DialogueType.OPTION).setOptionTitle("@red@You will lose all your items dropped if you die!")
                        .firstOption("I know I'm risking everything I have.") { p: Player ->
                            val boss = Obor(Obor.SPAWN_POSITION.transform(0, 0, p.index * 4))
                            val instance = CaveInstance(boss)
                            instance.enter(p)
                        }.addCancel("I need to prepare some more.").start(it)
            }.addCancel("No.").start(pl)
}

/**
 * Creates a new instance of the OborCave for a player.
 * @param player The player who entered the cave
 */
class CaveInstance(val obor: Obor):
        InstancedBossArea(Boundary(3081, 3103, 9796, 9815)) {

    val gate1: DynamicGameObject = DynamicGameObject.createLocal(29486, Position(3092, 9815, obor.position.z), 0, Direction.NORTH.id)
    val gate2: DynamicGameObject = DynamicGameObject.createLocal(29487, Position(3091, 9815, obor.position.z), 0, Direction.NORTH.id)

    override fun enter(agent: Agent?) {
        if (agent is Player) {
            //gate1.addTo(agent)
            //gate2.addTo(agent)
            super.enter(agent)
            agent.inventory.delete(ItemID.GIANT_KEY, 1)
            TravelSystem.fadeTravelAction(agent) {
                agent.moveTo(entrance.transform(0, 0, agent.index * 4))
                super.enter(agent)
                obor.spawn()
            }
            //addGameObject(gate1)
            //addGameObject(gate2)
            agent.packetSender.sendInterfaceRemoval();
            agent.sendMessage("You use your key to unlock the gate.")
            agent.packetSender.sendSound(Sounds.OPEN_METAL_GATE)
        }
    }

    override fun leave(agent: Agent?) {
        if (agent is Player && agent.isActive) {
            super.leave(agent)
            TravelSystem.fadeTravelAction(agent) {
                agent.moveTo(Position(3095, 9832, 0))
                agent.sendMessage("The gate locks shut behind you.")
                agent.packetSender.sendSound(70)
            }
            agent.packetSender.sendInterfaceRemoval();
        }
        obor.remove()
        super.leave(agent)
    }

    override fun handleObjectClick(pl: Player?, obj:GameObject, type: Int): Boolean {
        checkNotNull(pl)
        when(obj.id) {
            29491 -> TaskManager.submit(RockObstacle(pl, pl.y < obj.y))
            gate1.id, gate2.id -> DialogueBuilder(DialogueType.OPTION).setOptionTitle("Are you sure you want to leave Obor's Lair?")
                    .firstOption("Yes.") {
                        leave(it)
                    }.addCancel("Stay.").start(pl)
            29488, 29489 -> leave(pl)
        }
        return true
    }
}