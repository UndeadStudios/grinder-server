@file:JvmName("BryophytaCave")
package com.grinder.game.model.areas.instanced

import com.grinder.game.content.miscellaneous.TravelSystem
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.npc.monster.boss.impl.Bryophyta
import com.grinder.game.entity.agent.npc.monster.boss.impl.Bryophyta.Companion.SPAWN_POSITION
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Boundary
import com.grinder.game.model.Position
import com.grinder.game.model.areas.InstancedBossArea
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.sound.Sounds
import java.util.*

val bryophytaEntrance = Position(3214, 9937)


fun enterBryophytaCaveLair(pl: Player) {

    if (!pl.inventory.contains(22374)) {
        pl.sendMessage("It's locked.")
        pl.packetSender.sendSound(Sounds.USE_KEY_ON_LOCKED_DOOR)
        return
    }

    DialogueBuilder(DialogueType.OPTION).setOptionTitle("Enter Bryophyta's Lair?")
            .firstOption("Yes.") {
                DialogueBuilder(DialogueType.OPTION).setOptionTitle("@red@You will lose all your items dropped if you die!")
                        .firstOption("I know I'm risking everything I have.") { p: Player ->
                            val boss = Bryophyta(SPAWN_POSITION.transform(0, 0, p.index * 4))
                            val instance = BryophytaCaveInstance(boss)
                            instance.enter(p)
                        }.addCancel("I need to prepare some more.").start(it)
            }.addCancel("No.").start(pl)
}

/**
 * Creates a new instance of the BryophytaCaveInstance for a player.
 * @param player The player who entered the cave
 */
class BryophytaCaveInstance(val bryophyta: Bryophyta):
        InstancedBossArea(Boundary(3208, 3230, 9925, 9946)) {

    override fun enter(agent: Agent?) {
        if (agent is Player) {
            super.enter(agent)
            agent.inventory.delete(22374, 1)
            TravelSystem.fadeTravelAction(agent) {
                agent.moveTo(bryophytaEntrance.transform(0, 0, agent.index * 4))
                super.enter(agent)
                bryophyta.spawn()
            }

            agent.packetSender.sendInterfaceRemoval();
            agent.sendMessage("You use your key to unlock the gate.")
            agent.packetSender.sendSound(Sounds.OPEN_METAL_GATE)
        }
    }

    override fun leave(agent: Agent?) {
        if (agent is Player && agent.isActive) {
            super.leave(agent)
            TravelSystem.fadeTravelAction(agent) {
                agent.moveTo(Position(3174, 9900, 0))
                agent.sendMessage("The gate locks shut behind you.")
                agent.packetSender.sendSound(70)
            }
            agent.packetSender.sendInterfaceRemoval();
        }
        bryophyta.remove()
        super.leave(agent)
    }

    override fun handleObjectClick(pl: Player?, obj:GameObject, type: Int): Boolean {
        checkNotNull(pl)
        when(obj.id) {
            32535 -> DialogueBuilder(DialogueType.OPTION).setOptionTitle("Are you sure you want to leave Bryophyta's Lair?")
                    .firstOption("Yes.") {
                        leave(it)
                    }.addCancel("Stay.").start(pl)
        }
        return true
    }
}