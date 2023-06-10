package com.grinder.game.content.item.charging

/**
 * Represents a policy that details what should happen
 * with a [Chargeable] upon death.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   09/09/2020
 */
enum class ChargeableDeathPolicy {

    /**
     * The [Chargeable] is kept on death, with its charges intact.
     */
    KEEP,

    /**
     * The [Chargeable] is dropped on death, with its charges intact.
     */
    DROP,

    /**
     * The [Chargeable] is dropped on death, without charges.
     */
    DROP_UNCHARGED,

    // TODO: DROP UNCHARGED AND DROP THE CHARGES FOR EXAMPLE SCALES/DARTS/ETHER/RUNES (BASICALLY UNCHARGE + DROP)

    /**
     * The [Chargeable] is destroyed on death.
     */
    DESTROY
}