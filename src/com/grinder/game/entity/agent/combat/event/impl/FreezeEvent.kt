package com.grinder.game.entity.agent.combat.event.impl

import com.grinder.util.Misc

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/08/2020
 */
open class FreezeEvent(duration: Int, forced: Boolean) :
    ImmobilizeEvent(effectDuration = Misc.getTicks(duration), forced = forced)