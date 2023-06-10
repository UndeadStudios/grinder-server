package com.grinder.game.model.attribute.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.internal.LazilyParsedNumber
import com.grinder.game.model.attribute.AttributeValueHolderTemplate

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   2018-12-19
 * @version 1.0
 */
class NumericalValueHolder(number: Number = 0)
    : AttributeValueHolderTemplate<Number>(number) {

    private val initialValue = number

    fun incJ(value: Int) {
        incJ(value, Integer.MAX_VALUE)
    }
    fun incJ(value : Number ,maxValue : Number = Int.MAX_VALUE){
        inc(value, maxValue)
    }
    fun decJ(value : Number){
        decJ(value, 0)
    }
    fun decJ(value : Number ,minValue : Number = 0){
        dec(value, minValue)
    }

    /**
     * Convert [value] to the same type of [other].
     *
     * This is done as gson does not automatically convert numbers.
     */
    inline fun <reified N : Number> convertLazyValue(other: N) {
        if (this.value is LazilyParsedNumber) {
            when (other) {
                is Byte -> this.value = this.value.toByte()
                is Short -> this.value = this.value.toShort()
                is Int -> this.value = this.value.toInt()
                is Long -> this.value = this.value.toLong()
                is Double -> this.value = this.value.toDouble()
                is Float -> this.value = this.value.toFloat()
                else -> throw UnsupportedOperationException("Did not find number type for $other")
            }
        }
    }

    inline fun<reified N : Number> inc(value: N, maxValue : Number = Int.MAX_VALUE) : N{

        convertLazyValue(value)

        when (val current = this.value) {
            is Byte -> this.value = minOf(current.plus(value.toByte()), maxValue.toInt())
            is Short -> this.value = minOf(current.plus(value.toShort()), maxValue.toInt())
            is Int -> this.value = minOf(current.plus(value.toInt()), maxValue.toInt())
            is Long -> this.value = minOf(current.plus(value.toLong()), maxValue.toLong())
            is Double -> this.value = minOf(current.plus(value.toDouble()), maxValue.toDouble())
            is Float -> this.value = minOf(current.plus(value.toFloat()), maxValue.toFloat())
            else -> throw UnsupportedOperationException("Did not find number type for $current")
        }

        return this.value as N
    }

    inline fun<reified N : Number> dec(value: N, minValue : Number = 0) : N {

        convertLazyValue(value)

        when (val current = this.value) {
            is Byte -> this.value = maxOf(current.minus(value.toByte()), minValue.toInt())
            is Short -> this.value = maxOf(current.minus(value.toShort()), minValue.toInt())
            is Int -> this.value = maxOf(current.minus(value.toInt()), minValue.toInt())
            is Long -> this.value = maxOf(current.minus(value.toLong()), minValue.toLong())
            is Double -> this.value = maxOf(current.minus(value.toDouble()), minValue.toDouble())
            is Float -> this.value = maxOf(current.minus(value.toFloat()), minValue.toFloat())
            else -> throw UnsupportedOperationException("Did not find number type for $current")
        }
        return this.value as N
    }

    override fun save(): Boolean {
        return value != initialValue
    }

    override fun reset(){
        value = initialValue
    }

    override fun serialize(): JsonElement {
        return JsonPrimitive(value)
    }

    override fun deserialize(input: JsonElement) {
        value = input.asNumber
    }
}