package com.grinder.game.entity.agent.combat.attack.special

import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import java.util.stream.Stream

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   19/04/2020
 * @version 1.0
 */
interface SpecialAttackProvider : AttackProvider {

    override fun fetchHits(type: AttackType?) = Stream.empty<HitTemplate>()

}