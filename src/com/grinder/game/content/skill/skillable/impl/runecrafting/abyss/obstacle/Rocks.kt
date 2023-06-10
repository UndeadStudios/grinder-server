package com.grinder.game.content.skill.skillable.impl.runecrafting.abyss.obstacle

import com.grinder.game.content.skill.skillable.impl.Mining
import com.grinder.game.content.skill.skillable.impl.mining.PickaxeType
import com.grinder.game.content.skill.skillable.impl.runecrafting.abyss.ObstacleType
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ObjectID

class Rocks(exit: Position, type : ObstacleType) : PassableObstacle(exit, type) {

    override fun canDo(player: Player): Boolean {
        return Mining.findPickaxe(player).isPresent
    }

    override fun onEnd(player: Player, gameObject: GameObject) {
        replaceObject(player, gameObject, ObjectID.BROKEN_ROCK)
    }

    override fun getSound(): Sound {
        return Sound(Sounds.ROCK_MINED_SOUND)
    }

    override fun getAnim(player: Player): Animation {
        return Mining.findPickaxe(player).orElseGet { PickaxeType.BRONZE }.animaion
    }
}