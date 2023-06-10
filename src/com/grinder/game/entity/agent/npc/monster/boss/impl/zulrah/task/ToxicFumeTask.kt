package com.grinder.game.entity.agent.npc.monster.boss.impl.zulrah.task

import com.grinder.game.entity.agent.npc.monster.boss.impl.zulrah.ZulrahBoss
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.task.Task

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/12/2019
 * @version 1.0
 */
class ToxicFumeTask(val zulrahBoss: ZulrahBoss) : Task(1, false){

    private var cycle = 0

    override fun execute() {

        if(!zulrahBoss.isAlive){
            stop()
            return
        }

        when(cycle++) {
            0 -> {

            }
            30 -> {
                stop()
                return
            }
        }
        if(cycle > 3){
            zulrahBoss.combat.target?.let {
                if(it is Player){

                }
            }
        }
    }

    companion object {

        fun createPoisonProjectile() = ProjectileTemplate
                .builder(1045)
                .setSourceSize(4)
                .setDelay(40)
                .setSpeed(110)
                .setStartHeight(80)
                .setEndHeight(0)
                .setCurve(20)
    }
}