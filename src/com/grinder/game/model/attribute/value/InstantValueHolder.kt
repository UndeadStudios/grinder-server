package com.grinder.game.model.attribute.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.grinder.game.model.attribute.AttributeValueHolderTemplate
import java.time.Instant

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   13/05/2020
 * @version 1.0
 */
class InstantValueHolder(instant: Instant = Instant.now())
    : AttributeValueHolderTemplate<Instant>(instant) {

    override fun save() = true

    override fun reset() {
        value = Instant.now()
    }

    override fun serialize()
            = JsonPrimitive(value.toString())

    override fun deserialize(input: JsonElement){
        value = Instant.parse(input.asString)
    }
}