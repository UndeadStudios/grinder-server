package com.grinder.game.entity.`object`

import com.grinder.GrinderTest
import com.grinder.game.GameConstants
import com.grinder.game.collision.Collisions
import com.grinder.game.collision.TileFlags
import com.grinder.util.ObjectID
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ClippedMapObjectsTest : GrinderTest() {

    @Test
    fun addAndRemoveObject(){

        val id = ObjectID.CRATE
        val position = GameConstants.DEFAULT_POSITION.clone()

        val obj = StaticGameObjectFactory.produce(id, position, ObjectType.INTERACTABLE.value)

        ClippedMapObjects.add(obj)

        Assertions.assertTrue(ClippedMapObjects.exists(obj))
        Assertions.assertTrue(ClippedMapObjects.getObjectsAt(position).size == 1)
        Assertions.assertTrue(Collisions.check(position.x, position.y, position.z, TileFlags.BLOCKED))
        // TODO: add support for instance based collision detection (height >= 4)
//        Assertions.assertFalse(Collisions.check(position.x, position.y, 4, TileFlags.BLOCKED))
        
        ClippedMapObjects.removeAll(position)

        Assertions.assertFalse(ClippedMapObjects.exists(obj))
        Assertions.assertTrue(ClippedMapObjects.getObjectsAt(position).size == 0)
        Assertions.assertFalse(Collisions.check(position.x, position.y, position.z, TileFlags.BLOCKED))
        Assertions.assertFalse(Collisions.check(position.x, position.y, 4, TileFlags.BLOCKED))
    }
}