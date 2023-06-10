package com.grinder.game.entity.agent.combat.event.impl

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil

class BindEvent(caster: Agent, effectDuration: Int, immunityDuration: Int) :
    ImmobilizeEvent(
        effectDuration = effectDuration + EquipmentUtil.getExtraBindSpellDuration(caster),
        immunityDuration = immunityDuration,
        forced = false
    )