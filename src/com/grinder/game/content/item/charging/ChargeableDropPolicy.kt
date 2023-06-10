package com.grinder.game.content.item.charging

/**
 * Some [Chargeable] items have no "drop" option, but an "uncharge" option instead.
 * As this option is currently being handled by the ill-named "drop packet handler",
 * this enum provides a work around solution to provide custom upon handling of the "uncharge" packet.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since 2020-09-17
 */
enum class ChargeableDropPolicy(val confirmText: String){

    /**
     * Drop the uncharged item on the floor.
     */
    DROP_ON_FLOOR("Really drop it?"),

    /**
     * Keep the uncharged item in the inventory.
     */
    KEEP_IN_INVENTORY("Really uncharge it?")

}