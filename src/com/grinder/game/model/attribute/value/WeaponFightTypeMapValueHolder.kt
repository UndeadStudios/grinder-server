package com.grinder.game.model.attribute.value

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponFightType
import com.grinder.game.model.attribute.AttributeValueHolderTemplate

/**
 * Represents a map, where the keys are item ids,
 * and the values [fight types][WeaponFightType].
 *
 * This is used to cache a selected fight style of a weapon.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   3/11/2020
 */
class WeaponFightTypeMapValueHolder : AttributeValueHolderTemplate<HashMap<Int, WeaponFightType>>(HashMap()) {

    companion object {
        private val gson = GsonBuilder().setPrettyPrinting().create()
        private val type = object : TypeToken<Map<Int, WeaponFightType>>() {}.type
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