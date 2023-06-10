package com.grinder.game.model.attribute.value

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.grinder.game.content.gambling.blackjack.card.Card
import com.grinder.game.model.attribute.AttributeValueHolderTemplate

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/09/2020
 */
class CardsValueHolder : AttributeValueHolderTemplate<MutableList<Card>>(ArrayList()) {

    companion object {
        private val gson = GsonBuilder().setPrettyPrinting().create()
        private val type = object : TypeToken<MutableList<Card>>() {}.type
    }

    override fun reset() {
        value = ArrayList()
    }

    override fun save() = true

    override fun serialize(): JsonElement {
        return gson.toJsonTree(value)
    }

    override fun deserialize(input: JsonElement) {
        value = gson.fromJson(input, type)
    }
}