package com.grinder.game.entity.agent.npc.monster.boss.impl.zulrah

import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/12/2019
 * @version 1.0
 */
class ZulrahPattern(
        val location: ZulrahLocation,
        val state: ZulrahState,
        val cloudLocation: ZulrahCloudLocation? = null,
        val switchType: ZulrahAttackSwitchType? = null,
        private vararg val types: AttackType) {

    var count = 0

    fun attackTypeProvider(): AttackTypeProvider = when {
        types.isEmpty() -> state.type
        switchType == ZulrahAttackSwitchType.ALTERNATING -> {
            if(count == 2)
                count = 0
            types[count++]
        }
        else -> {
            AttackType.equalChances(*types)
        }
    }

    fun attackSpeed() = when(state) {
        ZulrahState.SERPENTINE,
        ZulrahState.TANZANITE -> 7
        ZulrahState.MAGMA -> 9
    }
}