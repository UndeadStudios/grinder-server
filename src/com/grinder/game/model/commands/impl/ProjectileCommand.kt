package com.grinder.game.model.commands.impl

import com.grinder.game.World
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.TileGraphic
import com.grinder.game.model.commands.DeveloperCommand
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate

class ProjectileCommand : DeveloperCommand() {

    override fun execute(player: Player, command: String, parts: Array<out String>) {
        val temp = ProjectileTemplate
                .builder(856)
                .setStartHeight(250)
                .setEndHeight(8)
                .setCurve(0)
                .setDelay(40)
                .setSourceSize(1)
                .setSourceOffset(160)
                .setSpeed(180)
                .build()
        val start = player.position.clone().add(3, 1)
        val end = player.position.clone().add(3, 0)
        val proj = Projectile(start, end, temp)
        proj.sendProjectile()
        proj.onArrival {
            World.spawn(TileGraphic(end, Graphic(305, 20, GraphicHeight.LOW)))
        }
    }
}