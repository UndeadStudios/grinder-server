package com.grinder.game.model.commands.impl

import com.grinder.game.World
import com.grinder.game.entity.agent.movement.task.impl.FollowAgentTask
import com.grinder.game.entity.agent.npc.monster.boss.impl.CallistoBoss
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.event.PlayerEvent
import com.grinder.game.entity.agent.player.event.PlayerEventListener
import com.grinder.game.entity.agent.player.event.PlayerEvents
import com.grinder.game.model.commands.DeveloperCommand

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   15/11/2019
 * @version 1.0
 */
class ControlNPCCommand : DeveloperCommand() {

    override fun getSyntax() = ""

    override fun getDescription() = "Controls NPC's by certain values."

    override fun execute(player: Player, command: String, parts: Array<out String>) {

        val npc = CallistoBoss(6503, player.position.clone())

        npc.spawn()
        npc.combat.disable()
        npc.say("disabled my combat!")

        player.subscribe(object : PlayerEventListener {
            override fun on(event: PlayerEvent): Boolean {
                if (event == PlayerEvents.TALKED) {
                    val text = player.currentChatMessage
                        ?.text
                        ?.trim()
                        ?.toLowerCase() ?: return false

                    if (text.equals("go", true)) {
                        if (parts.size == 1) {
                            // trace npc to player
                            npc.motion.start(FollowAgentTask(npc, player, false, false, 100), false)
                        } else {
                            val dx = parts[1].toIntOrNull() ?: 0
                            val dy = parts[2].toIntOrNull() ?: 0
                            npc.say("moving $dx, $dy")
                            npc.motion.traceTo(npc.position.clone().add(dx, dy))
                        }
                    }
                    if (text.equals("stop", true)) {
                        npc.motion.clearSteps()
                        npc.motion.resetTargetFollowing()
                        npc.motion.cancelTask()
                    }
                    if (text.equals("shoo", true)) {
                        World.npcRemoveQueue.add(npc)
                        return true
                    }
                }
                return false
            }
        })
    }

}