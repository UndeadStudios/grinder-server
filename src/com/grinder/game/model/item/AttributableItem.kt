package com.grinder.game.model.item

import com.google.gson.annotations.Expose
import com.grinder.util.Logging

class AttributableItem(id: Int, amount: Int = 1) : Item(id, amount) {

    init {
        if(definition.isTradeable) {
            Logging.log("itemAttributes", "Created a tradable attributable item of id $id.")
        }
    }

    /**
     * Attributes associated with this item.
     */
    @Expose var attributes = emptyMap<AttributeKey, Int>().toMutableMap()

    /**
     * Gets the value of the attribute corresponding to the given [AttributeKey] or else null.
     */
    fun getAttribute(key: AttributeKey): Int? {
        return if(attributes.containsKey(key)) attributes[key]  else null
    }

    /**
     * Gets the value of the attribute corresponding to the given [AttributeKey] or else null.
     */
    fun setAttribute(key: AttributeKey, value : Int) {
        if(attributes.containsKey(key)) attributes.replace(key, value)
        else attributes.put(key, value)
    }

    /**
     * Gets the value of the attribute corresponding to the given [AttributeKey] or else null.
     */
    fun increase(key: AttributeKey, amount : Int) {
        if(attributes.containsKey(key)) attributes.replace(key, getAttribute(key)!! + amount)
        else attributes.put(key, getAttribute(key) ?: 0 +amount)
    }

    /**
     * Increment an integer attribute
     */
    fun increment(key : AttributeKey) : Int? {
        if(!attributes.containsKey(key)) return null
        val value = (attributes[key] as Int)
        attributes.replace(key, value+1)
        return value+1
    }

    /**
     * Decrement an integer attribute
     */
    fun decrement(key : AttributeKey) : Int? {
        if(!attributes.containsKey(key)) return null
        val value = (attributes[key] as Int)
        attributes.replace(key, value-1)
        return value-1
    }

    override fun clone(): Item {
        val item = AttributableItem(id, amount)
        item.attributes = attributes
        return item
    }
}

data class AttributeKey(val name: String)
