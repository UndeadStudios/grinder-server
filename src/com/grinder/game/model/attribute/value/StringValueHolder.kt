package com.grinder.game.model.attribute.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.grinder.game.model.attribute.AttributeValueHolderTemplate

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   2018-12-19
 * @version 1.0
 */
class StringValueHolder(string : String = "")
    : AttributeValueHolderTemplate<String>(string) {

    private val initialValue = value

    private fun notInitialValue() : Boolean {
        return value != initialValue
    }
    override fun save(): Boolean {
        return notInitialValue()
    }
    override fun reset() {
        value = initialValue
    }
    override fun serialize(): JsonElement {
        return JsonPrimitive(value)
    }
    override fun deserialize(input: JsonElement) {
        value = input.asString
    }
}