package com.grinder.game.content.skill.skillable.impl.runecrafting.abyss.obstacle

import com.grinder.game.content.skill.skillable.impl.runecrafting.abyss.ObstacleType
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds

class Eyes(exit: Position, type : ObstacleType) : PassableObstacle(exit, type) {

    override fun canDo(player: Player): Boolean {
        return true
    }

    override fun onEnd(player: Player, gameObject: GameObject) {}

    override fun getSound(): Sound {
        return Sound(Sounds.PICKPOCKET_SOUND)
    }

    override fun getAnim(player: Player): Animation {
        return Animation(881)
    }
}