package com.grinder.game.model.item

import com.google.gson.GsonBuilder
import com.grinder.game.model.item.container.ItemContainer
import com.grinder.game.model.item.container.StackType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Test for [AttributableItem] system.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   09/09/2020
 */
internal class AttributableItemTest {

    @Test
    fun testSaving(){

        val gson = GsonBuilder()
                .registerTypeHierarchyAdapter(Item::class.java, ItemDeserialiser)
                .registerTypeHierarchyAdapter(Item::class.java, ItemSerializer)
                .create()
        val item = AttributableItem(995, 1)

        item.setAttribute(AttributeKey("charges"), 1)

        val serialisedItem = gson.toJson(item)

        assertEquals("{\"id\":995,\"amount\":1,\"attributes\":{\"charges\":1}}", serialisedItem)

        val deserialisedItem = gson.fromJson<AttributableItem>(serialisedItem, Item::class.java)

        assertEquals(item, deserialisedItem)

        val container = object : ItemContainer() {
            override fun full() = this
            override fun capacity() = 1
            override fun stackType() = StackType.DEFAULT
            override fun refreshItems() = this
        }

        container.add(item)

        val serialisedContainer = gson.toJsonTree(container.validItems)
        val deserialisedContainer = gson.fromJson(serialisedContainer, Array<Item>::class.java)
        val deserialisedContainerItem = deserialisedContainer[0]

        assertEquals(item, deserialisedContainerItem)

        println(deserialisedContainerItem)
    }
}