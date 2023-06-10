package com.grinder.game.definition

/**
 * This enum represents all possible value (or estimation) types
 * that are declared in the [ItemValueDefinition] class.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   19/01/2020
 * @version 1.0
 */
enum class ItemValueType {

    /**
     * This was formerly retrieved from [PriceCheckerDefinition.getValue]
     * @see [ItemValueDefinition.price_checker_value]
     */
    PRICE_CHECKER,

    /**
     * This was formerly retrieved from [PriceDefinition.getValue]
     * @see [ItemValueDefinition.item_prices_value]
     */
    ITEM_PRICES,

    /**
     * This was formerly retrieved from [TokensPriceDefinition.getValue]
     * @see [ItemValueDefinition.osrs_store_value]
     */
    OSRS_STORE,

    /**
     * This was formerly retrieved from [ItemDefinition.getValue]
     * @see [ItemValueDefinition.items_value]
     */
    ITEMS_VALUE,

    /**
     * This was formerly retrieved from [ItemDefinition.getHighAlchValue]
     *
     * @see [ItemValueDefinition.high_alch_value]
     */
    HIGH_ALCHEMY,

    /**
     * This was formerly retrieved from [ItemDefinition.getLowAlchValue]
     * @see [ItemValueDefinition.low_alch_value]
     */
    LOW_ALCHEMY
}