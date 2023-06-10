package com.grinder.game.model

import java.util.function.Predicate

/**
 * TODO: turn action system into a generic system, e.g. fun <K : Event> onEvent(eventType : KClass<K>, (K) -> Unit) { ... }
 */

private val equipActionPredicate = Predicate<ItemActions.ItemClickAction> { it.isEquipAction() }
private val dropActionPredicate = Predicate<ItemActions.ItemClickAction> { it.isDropAction() }
private val firstActionPredicate = Predicate<ItemActions.ItemClickAction> { it.isFirstAction() }
private val secondActionPredicate = Predicate<ItemActions.ItemClickAction> { it.isSecondAction() }
private val thirdActionPredicate = Predicate<ItemActions.ItemClickAction> { it.isThirdAction() }
private val inventoryPredicate = Predicate<ItemActions.ItemClickAction> { it.isInInventory() }

fun onEquipAction(vararg itemIds: Int, function: ItemActions.ItemClickAction.() -> Unit) {
    onItemClick(*itemIds, predicate = equipActionPredicate.and(inventoryPredicate), function = function)
}
fun onDropAction(vararg itemIds: Int, function: ItemActions.ItemClickAction.() -> Unit) {
    onItemClick(*itemIds, predicate = dropActionPredicate.and(inventoryPredicate), function = function)
}
fun onFirstInventoryAction(vararg itemIds: Int, function: ItemActions.ItemClickAction.() -> Unit) {
    onItemClick(*itemIds, predicate = firstActionPredicate.and(inventoryPredicate), function = function)
}
fun onSecondInventoryAction(vararg itemIds: Int, function: ItemActions.ItemClickAction.() -> Unit) {
    onItemClick(*itemIds, predicate = secondActionPredicate.and(inventoryPredicate), function = function)
}
fun onThirdInventoryAction(vararg itemIds: Int, function: ItemActions.ItemClickAction.() -> Unit) {
    onItemClick(*itemIds, predicate = thirdActionPredicate.and(inventoryPredicate), function = function)
}

/**
 * Configures a [function] for the [itemIds] that is invoked
 * whenever a [ItemActions.ItemClickAction] is fired that passes the [predicate].
 *
 * @param itemIds the ids to configure the function for
 * @param predicate the predicate that is used to test the action
 * @param function the function to fire upon the action
 */
private fun onItemClick(vararg itemIds: Int,
                        predicate: Predicate<ItemActions.ItemClickAction>,
                        function: (ItemActions.ItemClickAction) -> Unit){
    ItemActions.onClick(*itemIds){
        if(predicate.test(this)){
            function.invoke(this)
            return@onClick true
        }
        return@onClick false
    }
}

private val firstContainerActionPredicate = Predicate<ItemActions.ItemContainerClickAction> { it.isFirstAction() }
private val secondContainerActionPredicate = Predicate<ItemActions.ItemContainerClickAction> { it.isSecondAction() }
private val thirdContainerActionPredicate = Predicate<ItemActions.ItemContainerClickAction> { it.isThirdAction() }
private val equipmentContainerPredicate = Predicate<ItemActions.ItemContainerClickAction> { it.isInEquipment() }

fun onFirstContainerEquipmentAction(vararg itemIds: Int, function: ItemActions.ItemContainerClickAction.() -> Unit) {
    onItemContainerClick(*itemIds, predicate = firstContainerActionPredicate.and(equipmentContainerPredicate), function = function)
}
fun onSecondContainerEquipmentAction(vararg itemIds: Int, function: ItemActions.ItemContainerClickAction.() -> Unit) {
    onItemContainerClick(*itemIds, predicate = secondContainerActionPredicate.and(equipmentContainerPredicate), function = function)
}
fun onThirdContainerEquipmentAction(vararg itemIds: Int, function: ItemActions.ItemContainerClickAction.() -> Unit) {
    onItemContainerClick(*itemIds, predicate = thirdContainerActionPredicate.and(equipmentContainerPredicate), function = function)
}

/**
 * Configures a [function] for the [itemIds] that is invoked
 * whenever a [ItemActions.ItemContainerClickAction] is fired that passes the [predicate].
 *
 * @param itemIds the ids to configure the function for
 * @param predicate the predicate that is used to test the action
 * @param function the function to fire upon the action
 */
private fun onItemContainerClick(vararg itemIds: Int,
                                 predicate: Predicate<ItemActions.ItemContainerClickAction>,
                                 function: (ItemActions.ItemContainerClickAction) -> Unit){
    ItemActions.onContainerClick(*itemIds){
        if(predicate.test(this)){
            function.invoke(this)
            return@onContainerClick true
        }
        return@onContainerClick false
    }
}