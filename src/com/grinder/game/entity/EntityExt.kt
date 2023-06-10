package com.grinder.game.entity

import com.grinder.game.entity.agent.player.Color
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.removeInterfaces
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.attribute.value.InstantValueHolder
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   19/04/2020
 * @version 1.0
 */

fun Entity.hasAttribute(attributeKey: String)
        = attributes.containsKey(attributeKey)

fun Entity.removeAttribute(attributeKey: String)
        = attributes.remove(attributeKey)

fun Entity.getInt(attributeKey: String, defaultValue: Int = 0)
        = attributes.numAttr(attributeKey, defaultValue).value.toInt()

fun Entity.setInt(attributeKey: String, value: Int, defaultValue: Int = 0) {
    attributes.numAttr(attributeKey, defaultValue).value = value
}

fun Entity.incInt(attributeKey: String, amount: Int, cap: Int = Int.MAX_VALUE, default: Int = 0): Int{
    attributes.numAttr(attributeKey, default).inc(amount, cap)
    return getInt(attributeKey)
}

fun Entity.decInt(attributeKey: String, amount: Int, min: Int = 0, default: Int = 0): Int{
    attributes.numAttr(attributeKey, default).dec(amount, min)
    return getInt(attributeKey)
}

fun Entity.getDouble(attributeKey: String, defaultValue: Double = 0.0)
        = attributes.numAttr(attributeKey, defaultValue).value.toDouble()

fun Entity.setDouble(attributeKey: String, value: Double, defaultValue: Double = 0.0) {
    attributes.numAttr(attributeKey, defaultValue).value = value
}

fun Entity.setBoolean(attributeKey: String, value: Boolean, defaultValue: Boolean = false) {
    attributes.boolAttr(attributeKey, defaultValue).value = value
}

fun Entity.toggleBoolean(attributeKey: String, defaultValue: Boolean = false) : Boolean {
    setBoolean(attributeKey, !getBoolean(attributeKey, defaultValue), defaultValue)
    return getBoolean(attributeKey)
}

fun Entity.getBoolean(attributeKey: String, defaultValue: Boolean = false)
    = attributes.boolAttr(attributeKey, defaultValue).value

fun Entity.getLong(attributeKey: String, defaultValue: Long = 0L)
        = attributes.numAttr(attributeKey, defaultValue).value.toLong()

fun Entity.setLong(attributeKey: String, value: Long, defaultValue: Long = 0L) {
    attributes.numAttr(attributeKey, defaultValue).value = value
}

/**
 * Make sure to first check if the attribute exists!
 */
inline fun<reified E : Enum<E>> Entity.getEnum(attributeKey: String) : E {
    return enumValueOf(getString(attributeKey,"null"))
}

fun<E : Enum<E>> Entity.setEnum(attributeKey: String, enum: Enum<E>) {
    setString(attributeKey, enum.name, "null")
}

fun Entity.getString(attributeKey: String, defaultValue: String = "")
        = attributes.stringAttr(attributeKey, defaultValue).value

fun Entity.setString(attributeKey: String, value: String, defaultValue: String = ""){
    attributes.stringAttr(attributeKey, defaultValue).value = value
}

fun Entity.markTime(attributeKey: String) {
    if(!hasAttribute(attributeKey))
        attributes.set(attributeKey, Attribute(InstantValueHolder(Instant.now())))
    else
        attributes.reset(attributeKey)
}

fun Entity.markTimeFromNow(attributeKey: String, duration: Duration) {
    attributes.set(attributeKey, Attribute(InstantValueHolder(Instant.now().plus(duration))))
}

fun Entity.getTimePassed(
        attributeKey: String,
        unit: TimeUnit = TimeUnit.SECONDS
): Long {
    if(hasAttribute(attributeKey))
    {
        val valueHolderTemplate = attributes[attributeKey]?.valueHolder
        if(valueHolderTemplate is InstantValueHolder){
            val then = valueHolderTemplate.value
            val now = Instant.now()
            val duration = Duration.between(then, now)
            return unit.convert(duration)
        }
    }
   return 0L
}

fun Entity.passedTimeGenericAction(amount: Long = 1L, message: Boolean = false)  =
    passedTime(Attribute.GENERIC_ACTION, amount, TimeUnit.SECONDS, message, true)

fun Entity.passedTime(attributeKey: String, removeIfPassed: Boolean = true): Boolean {
    if (hasAttribute(attributeKey))
    {
        val valueHolderTemplate = attributes[attributeKey]?.valueHolder
        if(valueHolderTemplate is InstantValueHolder){
            val then = valueHolderTemplate.value
            val now = Instant.now()
            if (then.isAfter(now)) {
                if (removeIfPassed)
                    removeAttribute(attributeKey)
                return true
            }
        }
    }
    return false
}

fun Entity.passedTime(
        attributeKey: String,
        amount: Long,
        unit: TimeUnit = TimeUnit.SECONDS,
        message: Boolean = true,
        updateIfPassed: Boolean = true
): Boolean {

    val passed = !hasAttribute(attributeKey) || getTimePassed(attributeKey, unit) >= amount

    if(passed) {
        if(updateIfPassed)
            markTime(attributeKey)
        return true
    }

    if(message)
        asOptionalPlayer.ifPresent {
            val displayUnit = if (unit.ordinal < TimeUnit.SECONDS.ordinal) TimeUnit.SECONDS else unit
            val timeLeft = timeLeft(attributeKey, amount, displayUnit)
            val unitName = displayUnit.name.toLowerCase()
            it.removeInterfaces()
            it.message("You can only do this once every $amount $unitName, please wait $timeLeft more $unitName!", Color.RED)
        }
    return false
}

fun Entity.timeLeft(attributeKey: String, amount: Long, unit: TimeUnit = TimeUnit.SECONDS): Long {
    return amount - getTimePassed(attributeKey, unit)
}

fun Entity.timeLeftString(attributeKey: String, amount: Long, unit: TimeUnit = TimeUnit.SECONDS): String {
    val timeLeft = timeLeft(attributeKey, amount, unit)
    val unitName = unit.name.toLowerCase()
    return "$timeLeft more $unitName"
}