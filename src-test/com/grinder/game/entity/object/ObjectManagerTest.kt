package com.grinder.game.entity.`object`

import com.grinder.GrinderTest
import com.grinder.game.collision.Collisions
import com.grinder.game.collision.TileFlags
import com.grinder.game.model.Position
import com.grinder.util.ObjectID
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ObjectManagerTest : GrinderTest() {

    @Test
    fun addAndRemoveObject(){

        val id = ObjectID.CRATE
        val position =  Position(10, 10, 4)

        val obj = StaticGameObjectFactory.produce(id, position, ObjectType.INTERACTABLE.value)

        ObjectManager.add(obj, false)

        Assertions.assertTrue(ObjectManager.existsAt(position))
        Assertions.assertTrue(ObjectManager.existsAt(id, position))
        Assertions.assertTrue(ObjectManager.findDynamicObjectAt(position).get() == obj)
//        TODO: add instance support
//        Assertions.assertTrue(Collisions.check(position.x, position.y, 4, TileFlags.BLOCKED))
        Assertions.assertFalse(Collisions.check(position.x, position.y, 0, TileFlags.BLOCKED))

        ObjectManager.remove(obj, false)

        Assertions.assertFalse(ObjectManager.existsAt(position))
        Assertions.assertFalse(ObjectManager.existsAt(id, position))
        Assertions.assertTrue(ObjectManager.findDynamicObjectAt(position).isEmpty)
        Assertions.assertFalse(Collisions.check(position.x, position.y, 4, TileFlags.BLOCKED))
        Assertions.assertFalse(Collisions.check(position.x, position.y, 0, TileFlags.BLOCKED))
    }

}