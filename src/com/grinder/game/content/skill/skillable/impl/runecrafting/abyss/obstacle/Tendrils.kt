package com.grinder.game.content.skill.skillable.impl.runecrafting.abyss.obstacle

import com.grinder.game.content.skill.skillable.impl.Woodcutting
import com.grinder.game.content.skill.skillable.impl.runecrafting.abyss.ObstacleType
import com.grinder.game.content.skill.skillable.impl.woodcutting.AxeType
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ObjectID

class Tendrils(exit: Position, type : ObstacleType) : PassableObstacle(exit, type) {

    override fun canDo(player: Player): Boolean {
        return Woodcutting.getAxe(player).isPresent
    }

    override fun onEnd(player: Player, gameObject: GameObject) {
        replaceObject(player, gameObject, ObjectID.CHOPPED_TENDRILS)
    }

    override fun getSound(): Sound {
        return Sound(Sounds.HIT_TREE)
    }

    override fun getAnim(player: Player): Animation {
        return Woodcutting.getAxe(player).orElseGet { AxeType.BRONZE_AXE }.animation
    }
}