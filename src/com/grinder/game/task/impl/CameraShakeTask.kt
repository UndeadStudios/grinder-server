package com.grinder.game.task.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.task.Task

/**
 * Shakes the camera then resets it after a small amount of time.
 * @param player The player that has it's camera shook.
 * @param shakeDir The direction to shake the camera.
 * @param mag Magnitude of the shake. (how strong the shake is)
 * @param apl Amplitude of the shake. (how frequent the shake is)
 * @param pi 4pi over periods.
 */
class CameraShakeTask(
        val player: Player,
        val shakeDir: Int, val mag: Int, val apl: Int, val pi: Int,
        resetDelay:Int) : Task(resetDelay) {
    init {
        player.packetSender.sendCameraShake(shakeDir, mag, apl, pi)
    }

    override fun execute() {
        player.packetSender.sendCameraNeutrality()
        stop()
    }

}