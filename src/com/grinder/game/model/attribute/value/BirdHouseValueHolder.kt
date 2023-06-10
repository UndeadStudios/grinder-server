package com.grinder.game.model.attribute.value

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.grinder.game.content.skill.skillable.impl.hunter.birdhouse.BirdHouse
import com.grinder.game.content.skill.skillable.impl.hunter.birdhouse.BirdHouseSpot
import com.grinder.game.model.attribute.AttributeValueHolderTemplate

/**
 * @author Zach (zach@findzach.com)
 * @since 12/21/2020
 *
 * Leveraging the Attribute System and GSON to simply save and load our BirdHouse Data
 */
class BirdHouseValueHolder :  AttributeValueHolderTemplate<HashMap<BirdHouseSpot, BirdHouse>>(HashMap()) {

    companion object {
        private val gson = GsonBuilder().setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create()
        private val type = object : TypeToken<Map<BirdHouseSpot, BirdHouse>>() {}.type
    }

    override fun reset() {
        value = HashMap()
    }

    override fun save(): Boolean {
        return value.isNotEmpty()
    }

    override fun serialize(): JsonElement {
        return gson.toJsonTree(value)
    }

    override fun deserialize(input: JsonElement) {
        value = gson.fromJson(input, type)
    }
}