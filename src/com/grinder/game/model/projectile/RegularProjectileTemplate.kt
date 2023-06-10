package com.grinder.game.model.projectile

/**
 * @author Jack Barnett (https://www.rune-server.ee/members/raccas/)
 */
class RegularProjectileTemplate(
        val projectileId : Int,
        val sourceSize : Int,
        val startHeight : Int,
        val endHeight : Int,
        val curve : Int,
        val sourceOffset : Int,
        val delay : Int,
        val speed : Int
) : ProjectileTemplate {

    override fun startHeight() = startHeight

    override fun sourceSize() = sourceSize

    override fun endHeight() = endHeight

    override fun curve() = curve

    override fun projectileId() = projectileId

    override fun sourceOffset() = sourceOffset

    override fun delay() = delay

    override fun lifetime() = speed

}