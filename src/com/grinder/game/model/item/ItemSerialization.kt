package com.grinder.game.model.item

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.grinder.game.content.item.charging.impl.MagmaHelmet
import com.grinder.game.content.item.charging.impl.SerpentineHelmet
import com.grinder.game.content.item.charging.impl.TanzaniteHelmet
import java.lang.reflect.Type

object ItemDeserialiser : JsonDeserializer<Item> {

    private val gson = Gson()

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): Item? {
        val obj = json.asJsonObject

        return if (json.toString().contains("attributes")) {
            val id = obj.get("id").asInt
            val amount = obj.get("amount").asInt
            val attrsObj = obj.getAsJsonObject("attributes")

            val type = object : TypeToken<Map<String, Int>>() {}.type
            val attrs : Map<String, Int> = gson.fromJson(attrsObj, type)

            val at = AttributableItem(id, amount)

            // TEMP Cheapfix to rename old chargeable keys, TODO: remove
            SerpentineHelmet.renameChargesKey(at)
            TanzaniteHelmet.renameChargesKey(at)
            MagmaHelmet.renameChargesKey(at)

            at.attributes = attrs.mapKeys { AttributeKey(it.key) }.toMutableMap()

            at
        } else {
            gson.fromJson(json.toString(), Item::class.java)
        }
    }
}

object ItemSerializer : JsonSerializer<Item> {

    override fun serialize(src: Item, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObj = JsonObject()

        jsonObj.addProperty("id", src.id)
        jsonObj.addProperty("amount", src.amount)
        if(src.hasAttributes()) {
            val attributes = src.asAttributable.attributes
            jsonObj.add("attributes", context.serialize(attributes.mapKeys { it.key.name }))
        }
        return jsonObj
    }

}