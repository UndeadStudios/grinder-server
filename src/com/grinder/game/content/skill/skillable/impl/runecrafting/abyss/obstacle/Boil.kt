package com.grinder.game.content.skill.skillable.impl.runecrafting.abyss.obstacle

import com.grinder.game.content.skill.skillable.impl.runecrafting.abyss.ObstacleType
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID
import com.grinder.util.ObjectID

class Boil(exit: Position, type : ObstacleType) : PassableObstacle(exit, type) {

    override fun canDo(player: Player): Boolean {
        return player.inventory.contains(ItemID.TINDERBOX)
    }

    override fun onEnd(player: Player, gameObject: GameObject) {
        replaceObject(player, gameObject, ObjectID.BURST_BOIL)
    }

    override fun getSound(): Sound {
        return Sound(Sounds.BURN_LOGS_SOUND)
    }

    override fun getAnim(player: Player): Animation {
        return Animation(733)
    }
}