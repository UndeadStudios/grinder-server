package com.grinder.game.content.skill.skillable.impl.magic

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface
import com.grinder.game.model.Animation

enum class CastSpellAnimation(private val animation: Animation, private val animationWithStaff: Animation? = null) {

    OFFENSIVE(Animation(711), Animation(1162)),
    OFFENSIVE_2(Animation(710), Animation(1161)),
    OFFENSIVE_3(Animation(727), Animation(1167)),
    OFFENSIVE_4(Animation(7855), Animation(7855)),
    EFFECTIVE(Animation(716), Animation(1163)),
    EFFECTIVE_2(Animation(729), Animation(1169)),
    CRUMBLE_UNDEAD(Animation(724), Animation(1166)),
    EMPOWERING(Animation(811));

    fun getAnimation(caster: Agent): Animation {
        return if (animationWithStaff != null && (caster.combat.uses(WeaponInterface.STAFF) || caster.combat.uses(WeaponInterface.ANCIENT_STAFF) || caster.combat.uses(WeaponInterface.BLADED_STAFF)))
            animationWithStaff
        else
            animation
    }
}