package com.grinder.game.content.skill.skillable.impl.runecrafting.abyss

import com.grinder.game.model.ObjectActions

object AbyssEvents {

    init {
        ObjectActions.onClick(AbyssObstacle.values().map { it.objectId }) {
            val obstacle = AbyssObstacle.getAbyssObstacleForObject(it.objectActionMessage.objectId)
            obstacle ?: return@onClick

            obstacle.function.pass(it.player, it.objectActionMessage.gameObject)
        }

    }

}