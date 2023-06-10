package com.grinder.game.entity.agent.combat.attack

import com.grinder.game.model.Skill

/**
 * Represents different styles of an attack.
 *
 * The [style][AttackStyle] determines the speed of an attack,
 * and in what [skill][Skill] experience is gained.
 *
 * @author lare96
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 */
enum class AttackStyle {

    ACCURATE {
        override fun skill(type: AttackType) = if (type == AttackType.RANGED)
            intArrayOf(Skill.RANGED.ordinal)
        else
            intArrayOf(Skill.ATTACK.ordinal)
    },
    AGGRESSIVE {
        override fun skill(type: AttackType) = if (type == AttackType.RANGED)
            intArrayOf(Skill.RANGED.ordinal)
        else
            intArrayOf(Skill.STRENGTH.ordinal)
    },
    DEFENSIVE {
        override fun skill(type: AttackType) = if (type == AttackType.RANGED)
            intArrayOf(Skill.RANGED.ordinal, Skill.DEFENCE.ordinal)
        else
            intArrayOf(Skill.DEFENCE.ordinal)
    },
    CONTROLLED {
        override fun skill(type: AttackType) =
                intArrayOf(Skill.ATTACK.ordinal, Skill.STRENGTH.ordinal, Skill.DEFENCE.ordinal)
    };

    /**
     * Determines the Skill trained by this fighting style based on the
     * [AttackType].
     *
     * @param type the combat type to determine the Skill trained with.
     * @return the Skill trained by this fighting style.
     */
    abstract fun skill(type: AttackType): IntArray
    fun getStanceStrengthBonus(type: AttackType): Int {
        return when (type) {
            AttackType.MELEE -> when {
                this === AGGRESSIVE -> +3
                this === CONTROLLED -> +1
                else -> 0
            }
            AttackType.RANGED -> if (this === ACCURATE) +3 else 0
            else -> 0
        }
    }

    fun getStanceAccuracyBonus(type: AttackType): Int {
        when (type) {
            AttackType.MELEE -> {
                if (this === ACCURATE) return +3
                else if (this === CONTROLLED) return +1
            }
            AttackType.RANGED -> {
                if (this === ACCURATE) return +3
            }
            AttackType.MAGIC -> {
                if (this === ACCURATE) return +3 // (trident only)
                else if (this === CONTROLLED) return +1 // (trident only)
            }
            else -> return 0
        }
        return 0
    }

    val stanceDefenceBonus: Int
        get() {
            if (this === DEFENSIVE) return +3
            return if (this === CONTROLLED) +1 else 0
        }
}