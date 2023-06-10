package com.grinder.game.collision

/**
 * Delegate of [CollisionManager] mainly to easily mock path finding unit tests
 */
object Collisions {

    /**
     * TODO: implement the 'height trick' at a deeper level,
     *       or preferably implement instancing properly.
     */
    @JvmStatic
    operator fun Collisions.get(x: Int, y: Int, plane: Int) =
        CollisionManager.getClipping(x, y, plane % 4)

    @JvmStatic
    fun check(x: Int, y: Int, plane: Int, flag: Int) =
        this[x, y, plane] and flag != 0

    @JvmStatic
    fun add(x: Int, y: Int, plane: Int, flag: Int) {
        CollisionManager.addClipping(x, y, plane, flag)
    }

    @JvmStatic
    fun set(x: Int, y: Int, plane: Int, flag: Int) {
        CollisionManager.setMask(x, y, plane, flag)
    }

    @JvmStatic
    fun remove(x: Int, y: Int, plane: Int, flag: Int) {
        CollisionManager.removeClipping(x, y, plane, flag)
    }

}