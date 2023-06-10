package com.grinder.game.content.minigame.aquaisneige.monsters

import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeNpc
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.Graphic
import com.grinder.game.model.Position
import com.grinder.game.model.Skill
import com.grinder.util.Misc
import java.util.stream.Stream

class WaterElemental(npcId: Int, position: Position?) : AquaisNeigeNpc(npcId, position!!), AttackProvider {
    override fun generateAttack(): BossAttack {
        val attack: BossAttack = object : BossAttack(this) {
            override fun postHitAction(actor: Boss, target: Agent) {
                super.postHitAction(actor, target)
            }

            override fun createHits(actor: Boss, target: Agent): Array<Hit> {

                val hits = super.createHits(actor, target)
                val defenceLevel = owner.skillManager.getCurrentLevel(Skill.DEFENCE)
                if (defenceLevel > 0) {
                    for (hit in hits) {
                        if (hit.isAccurate) {
                            val skillReduction = defenceLevel * 0.05;
                            owner.skillManager.setCurrentLevel(
                                Skill.DEFENCE,
                                (defenceLevel - skillReduction).toInt(),
                                true
                            )
                            owner.performGraphic(Graphic(398))
                        }
                    }
                }
                return arrayOf(Hit(actor, target, this, false, 0))
            }
        }
        attack.setType(AttackType.MELEE)
        return attack
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        return HitTemplate.builder(type)
            .setAttackStat(EquipmentBonuses.ATTACK_CRUSH)
            .setDefenceStat(EquipmentBonuses.DEFENCE_CRUSH).
            buildAsStream()
    }

    init {
        race = MonsterRace.ELEMENTAL
    }
}