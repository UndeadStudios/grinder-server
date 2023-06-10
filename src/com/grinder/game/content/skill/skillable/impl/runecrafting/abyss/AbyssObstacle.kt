package com.grinder.game.content.skill.skillable.impl.runecrafting.abyss

import com.grinder.game.content.skill.skillable.impl.runecrafting.abyss.obstacle.*
import com.grinder.game.model.Position

enum class AbyssObstacle(
        val type : ObstacleType,
        val objectId: Int,
        val exitPos : Position
) {

    EAST_TENDRIL(ObstacleType.TENDRILS, 26253, Position(3051, 4835)),
    WEST_TENDRIL(ObstacleType.TENDRILS, 26189, Position(3025, 4830)),
    WEST_ROCKS(ObstacleType.ROCK, 26188, Position(3035, 4820)),
    EAST_ROCKS(ObstacleType.ROCK, 26574, Position(3047, 4821)),
    EAST_BOIL(ObstacleType.BOIL, 26252, Position(3052, 4829)),
    WEST_BOIL(ObstacleType.BOIL, 26190, Position(3025, 4833)),
    EAST_EYES(ObstacleType.EYES, 26251, Position(3051, 4837)),
    WEST_EYES(ObstacleType.EYES, 26191, Position(3028, 4840)),
    EAST_GAP(ObstacleType.GAP, 26250, Position(3050, 4840)),
    WEST_GAP(ObstacleType.GAP, 26192, Position(3030, 4841)),
    NORTH_PASSAGE(ObstacleType.PASSAGE, 26208, Position(3040, 4844))
    ;

    val function = when(type) {
        ObstacleType.TENDRILS -> Tendrils(exitPos, type)
        ObstacleType.ROCK -> Rocks(exitPos, type)
        ObstacleType.BOIL -> Boil(exitPos, type)
        ObstacleType.EYES -> Eyes(exitPos, type)
        ObstacleType.GAP -> Gap(exitPos, type)
        ObstacleType.PASSAGE -> Passage(exitPos, type)
    }

    companion object {

        /**
         * Finds the [AbyssObstacle] with the given object id or null.
         */
        fun getAbyssObstacleForObject(objectId : Int) = values().find { it.objectId == objectId }

        /**
         * Checks if the given object id corresponds to any [AbyssObstacle].
         */
        fun isAbyssObstacle(objectId: Int) = getAbyssObstacleForObject(objectId) != null
    }
}