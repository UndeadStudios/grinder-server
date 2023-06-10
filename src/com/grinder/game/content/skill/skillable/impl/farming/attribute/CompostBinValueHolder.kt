package com.grinder.game.content.skill.skillable.impl.farming.attribute

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.grinder.game.content.skill.skillable.impl.farming.CompostBin
import com.grinder.game.model.attribute.AttributeValueHolderTemplate

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/09/2020
 */
class CompostBinValueHolder(bins: Array<CompostBin> = emptyArray())
    : AttributeValueHolderTemplate<Array<CompostBin>>(bins) {

    companion object {
        private val gson = GsonBuilder().setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create()
        private val type = object : TypeToken<Array<CompostBin>>() {}.type
    }

    override fun reset() {
        value = emptyArray()
    }

    override fun save() = value.isNotEmpty()

    override fun serialize(): JsonElement {
        return gson.toJsonTree(value)
    }

    override fun deserialize(input: JsonElement) {
        value = gson.fromJson(input, type)
    }
}