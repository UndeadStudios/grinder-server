package com.grinder.game.content.item.charging

/**
 * Annotate [Chargeable] objects that should be loaded through reflection.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   09/09/2020
 *
 * @param chargedId     the item id of the charged item variant.
 * @param unchargedId   the item id of the uncharged item variant.
 */
internal annotation class Charge(val chargedId: Int, val unchargedId: Int)