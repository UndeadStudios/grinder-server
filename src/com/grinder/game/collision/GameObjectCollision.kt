package com.grinder.game.collision

import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.model.Direction

object GameObjectCollision {

    const val ADD_MASK = 0
    const val REMOVE_MASK = 1
    const val SET_MASK = 2

    @JvmStatic
    fun modifyCollision(gameObject: GameObject, changeType: Int) {
        if(gameObject.definition == null)
            return
        if (!gameObject.definition.solid) {
            return
        }

        when (gameObject.objectType) {
            in 0..3 -> modifyWall(gameObject, changeType)
            in 9..21 -> modifyObject(gameObject, changeType)
            22 -> {
                if (gameObject.definition.isSolid) {
                    modifyMask(gameObject.position.x, gameObject.position.y, gameObject.position.z, TileFlags.FLOOR_DECO, changeType)
                }
            }
        }
    }

    fun modifyObject(gameObject: GameObject, changeType: Int) {
        var mask = TileFlags.LAND

        if (gameObject.definition.impenetrable) {//solid
            mask = mask or TileFlags.SKY
        }

        if (!gameObject.definition.isSolid) {//not alt
            mask = mask or TileFlags.IGNORED
        }

        var width = gameObject.definition.sizeX
        var height = gameObject.definition.sizeY

        if (gameObject.face and 0x1 == 1) {
            width = gameObject.definition.sizeY
            height = gameObject.definition.sizeX
        }

        for (offsetX in 0 until width) {
            for (offsetY in 0 until height) {
                modifyMask(gameObject.position.x + offsetX, gameObject.position.y + offsetY, gameObject.position.z, mask, changeType)
            }
        }
    }


    fun modifyWall(gameObject: GameObject, changeType: Int) {
        modifyWall(gameObject, 0, changeType)
        if (gameObject.definition.impenetrable) {
            modifyWall(gameObject, 1, changeType)
        }
        if (!gameObject.definition.isSolid) {
            modifyWall(gameObject, 2, changeType)
        }
    }

    /**
     * Wall types:
     * 0 - ║ External wall (vertical or horizontal)
     * 1 - ╔ External corner (flat/missing)
     * 2 - ╝ Internal corner
     * 3 - ╔ External corner (regular)
     */
    fun modifyWall(gameObject: GameObject, motion: Int, changeType: Int) {
        val rotation = gameObject.face
        val type = gameObject.objectType
        var tile = gameObject.position.clone()

        // Internal corners
        if (type == 2) {
            // Mask both cardinal directions
            val or = when (Direction.ORDINAL[rotation and 0x3]) {
                Direction.NORTH_WEST -> TileFlags.NORTH_OR_WEST
                Direction.NORTH_EAST -> TileFlags.NORTH_OR_EAST
                Direction.SOUTH_EAST -> TileFlags.SOUTH_OR_EAST
                Direction.SOUTH_WEST -> TileFlags.SOUTH_OR_WEST
                else -> 0
            }
            modifyMask(gameObject.position.x, gameObject.position.y, gameObject.position.z, applyMotion(or, motion), changeType)
            val dir = Direction.CARDINAL[(rotation + 3) and 0x3]
            tile.add(dir.x, dir.y)
        }

        // Mask one wall side
        var direction = when (type) {
            0 -> Direction.CARDINAL[(rotation + 3) and 0x3]
            2 -> Direction.CARDINAL[(rotation + 1) and 0x3]
            else -> Direction.ORDINAL[rotation and 0x3]
        }

        modifyMask(tile.x, tile.y, tile.z, direction.flag(motion), changeType)

        // Mask other wall side
        val dir = if (type == 2) {
            Direction.CARDINAL[rotation and 0x3]
        } else {
            direction
        }
        tile = gameObject.position.clone().add(dir.x, dir.y)

        direction = when (type) {
            2 -> Direction.CARDINAL[(rotation + 2) and 0x3]
            else -> direction.opposite
        }
        modifyMask(tile.x, tile.y, tile.z, direction.flag(motion), changeType)
    }

    fun modifyMask(x: Int, y: Int, plane: Int, mask: Int, changeType: Any) {
        when (changeType) {
            ADD_MASK -> Collisions.add(x, y, plane, mask)
            REMOVE_MASK -> Collisions.remove(x, y, plane, mask)
            SET_MASK -> Collisions.set(x, y, plane, mask)
        }
    }

    fun applyMotion(mask: Int, motion: Int): Int {
        return when (motion) {
            1 -> mask shl 9
            2 -> mask shl 22
            else -> mask
        }
    }

    fun Direction.flag(motion: Int) = applyMotion(flag(), motion)
}