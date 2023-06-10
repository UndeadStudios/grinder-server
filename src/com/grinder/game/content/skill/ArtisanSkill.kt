package com.grinder.game.content.skill

import com.grinder.game.model.item.Item

/**
 * Class represents data on converting a single item into another item during skilling.
 *
 * @param baseItem Item required to perform skill item transformation.
 * @param productItem Item transformed during a skill action.
 */
data class MaterialTrans(val baseItem:Item?, val productItem:Item)

/**
 * Represents data on converting a group of materials into a single product during skilling.
 *
 * @param materials Group of items required.
 * @param productItem Item produced while consuming the materials.
 */
data class MaterialCombineTrans(val materials:Array<Item>, val productItem: Item) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MaterialCombineTrans

        if (!materials.contentEquals(other.materials)) return false
        if (productItem != other.productItem) return false

        return true
    }

    override fun hashCode(): Int {
        var result = materials.contentHashCode()
        result = 31 * result + productItem.hashCode()
        return result
    }
}