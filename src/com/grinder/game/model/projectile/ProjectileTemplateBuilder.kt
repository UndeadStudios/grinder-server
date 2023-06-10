package com.grinder.game.model.projectile

import com.grinder.game.definition.NpcHeights.getNpcHeight
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.model.Graphic
import com.grinder.game.model.sound.Sound
import java.util.*
import java.util.stream.Stream

/**
 * Represents a builder interface for building [projectile templates][ProjectileTemplate].
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 06/11/2019
 *
 * @param graphicId the [ProjectileTemplate.projectileId]
 */
class ProjectileTemplateBuilder(val graphicId: Int) {

    private var sourceSize = 1
    private var sourceOffset = 64
    private var startHeight = 30
    private var endHeight = 30
    private var curve = 0
    private var speed = 40
    private var delay = 30
    private var arrivalSound: Sound? = null
    private var departureSound: Sound? = null
    private var arrivalGraphic: Graphic? = null

    fun setSourceSize(sourceSize: Int)  = also { it.sourceSize = sourceSize }
    fun setSourceOffset(sourceOffset: Int) = also { it.sourceOffset = sourceOffset }
    fun setHeights(startHeight: Int, endHeight: Int) = also {
        setStartHeight(startHeight)
        setEndHeight(endHeight)
    }
    fun setStartHeight(startHeight: Int) = also { it.startHeight = startHeight }
    fun setEndHeight(endHeight: Int) = also { it.endHeight = endHeight }
    fun setEndHeight(target: Agent, multiplier: Double = 1.0) : ProjectileTemplateBuilder {
        val endHeight: Int = if (target is NPC) {
            getNpcHeight((target as NPC?)!!)
        } else {
            196
        }
        val heightMultiplier = multiplier * if (endHeight > 500) 0.5 else 0.8
        return setEndHeight((endHeight / 4 * heightMultiplier).toInt())
    }
    fun setCurve(curve: Int) = also { it.curve = curve }
    fun setDelay(delay: Int) = also { it.delay = delay }
    fun setSpeed(speed: Int) = also { it.speed = speed }
    fun setArrivalSound(sound: Sound) = also { it.arrivalSound = sound }
    fun setDepartureSound(sound: Sound) = also { it.departureSound = sound }
    fun setArrivalGraphic(graphic: Graphic) = also { it.arrivalGraphic = graphic }

    fun build() = object : ProjectileTemplate {
        override fun sourceSize() = sourceSize
        override fun sourceOffset() = sourceOffset
        override fun projectileId() = graphicId
        override fun startHeight() = startHeight
        override fun endHeight() = endHeight
        override fun curve() = curve
        override fun lifetime() = speed
        override fun delay() = delay
        override fun arrivalSound() = Optional.ofNullable(arrivalSound)
        override fun departureSound() = Optional.ofNullable(departureSound)
        override fun arrivalGraphic() = Optional.ofNullable(arrivalGraphic)
    }
    fun buildAsStream() = Stream.of(build())
}
