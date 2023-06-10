package com.grinder.game.entity.agent.combat.event.impl

import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpell
import com.grinder.game.entity.agent.combat.event.CombatEvent

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   10/09/2020
 */
class SpellCastEvent(val spell: CombatSpell) : CombatEvent