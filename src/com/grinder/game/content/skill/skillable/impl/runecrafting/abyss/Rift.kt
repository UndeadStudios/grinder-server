package com.grinder.game.content.skill.skillable.impl.runecrafting.abyss

import com.fasterxml.jackson.databind.node.BooleanNode
import com.grinder.game.content.skill.skillable.impl.runecrafting.Talisman
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.playSound
import com.grinder.game.model.sound.Sounds

enum class Rift(val objectId : Int, val talisman : Talisman) {

    AIR_RIFT(25378, Talisman.AIR),
    NATURE(24975, Talisman.NATURE),
    COSMIC(24974, Talisman.COSMIC),
    BLOOD(25380, Talisman.BLOOD),
    FIRE(24971, Talisman.FIRE),
    EARTH(24972, Talisman.EARTH),
    BODY(24973, Talisman.BODY),
    MIND(25379, Talisman.MIND),
    AIR(25378, Talisman.AIR),
    WATER(25376, Talisman.WATER),
    DEATH(25035, Talisman.DEATH),
    LAW(25034, Talisman.LAW),
    CHAOS(24976, Talisman.CHAOS),
    ;

    companion object {

        /**
         *  Finds the [Rift] with the same object id as that given or null.
         */
        fun getRiftForObjectId(objectId: Int) = values().find { it.objectId == objectId}

        fun handleClick(objectId: Int, player: Player)  : Boolean {
            val rift = Rift.getRiftForObjectId(objectId)
            rift ?: return false

            player.playSound(Sounds.ENTER_RIFT)
            player.moveTo(rift.talisman.altarPos)
            return true
        }
    }

}