package com.grinder.game.content.skill.skillable.impl.runecrafting.abyss.obstacle

import com.grinder.game.content.skill.skillable.impl.runecrafting.abyss.ObstacleType
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.game.model.sound.Sound
import com.grinder.util.TaskFunctions.delayBy

class Passage(exit: Position, type : ObstacleType) : PassableObstacle(exit, type) {

    override fun pass(player: Player, gameObject: GameObject) {
        player.message(type.start)
        player.performAnimation(getAnim(player))

        delayBy(3) {
            player.moveTo(exit)
        }
    }

    override fun canDo(player: Player): Boolean {
        return true
    }

    override fun onEnd(player: Player, gameObject: GameObject) {
    }

    override fun getSound(): Sound {
        return Sound(0)
    }

    override fun getAnim(player: Player): Animation {
        return Animation(746)
    }
}