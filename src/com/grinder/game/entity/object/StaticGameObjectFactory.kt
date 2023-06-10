package com.grinder.game.entity.`object`

import com.grinder.game.model.Position

/**
 * Use this class to produce new [static game objects][StaticGameObject]
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-20
 */
object StaticGameObjectFactory {

    @JvmStatic
    fun produce(objectId: Int, position: Position, type: ObjectType, direction: Int): StaticGameObject {
        return StaticGameObject(objectId, position, type.value, direction)
    }

    @JvmStatic
    fun produce(objectId: Int, position: Position, type: Int, direction: Int): StaticGameObject {
        return StaticGameObject(objectId, position, type, direction)
    }

    @JvmStatic
    fun produce(objectId: Int, position: Position, type: ObjectType): GameObject {
        return produce(objectId, position, type.value, 0)
    }

    @JvmStatic
    fun produce(objectId: Int, position: Position, type: Int): GameObject {
        return produce(objectId, position, type, 0)
    }

    @JvmStatic
    fun produce(objectId: Int, position: Position): GameObject {
        return produce(objectId, position, 10, 0)
    }
}