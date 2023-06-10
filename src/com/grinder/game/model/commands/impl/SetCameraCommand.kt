package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.commands.DeveloperCommand
import com.grinder.game.task.TaskManager

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/12/2019
 * @version 1.0
 */
class SetCameraCommand : DeveloperCommand() {

    override fun execute(player: Player, command: String?, parts: Array<out String>) {

//        val x = parts[1].toIntOrNull()?:0;
//        val y = parts[2].toIntOrNull()?:0;
//        val level = parts[3].toIntOrNull()?:0;
//        val speed = parts[4].toIntOrNull()?:0;
//        val angle = parts[5].toIntOrNull()?:0;

//        player.packetSender.sendCameraAngle(localX + x, localY + y, level, speed, angle)
//        player.packetSender.sendCameraPos(4635, 7068, 0)
        player.packetSender.sendCameraNeutrality()

        TaskManager.submit(1) {
//            player.packetSender.sendCameraPos(6262, 6529, 0)
            player.packetSender.sendCameraAngle(55, 52, 422, 17, 224)
//            player.packetSender.sendCameraPos(6844, 5776, 0)
        }
        TaskManager.submit(9) {
            player.packetSender.sendCameraNeutrality()
//            player.packetSender.sendCameraPos(6844, 5776, 0)
        }
    }
}