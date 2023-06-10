package com.grinder.game.model.attribute

import com.google.gson.JsonElement

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   2018-12-19
 * @version 1.0
 */
abstract class AttributeValueHolderTemplate<V>(var value: V) {

    abstract fun reset()

    abstract fun save() : Boolean

    abstract fun serialize() : JsonElement

    abstract fun deserialize(input : JsonElement)

}