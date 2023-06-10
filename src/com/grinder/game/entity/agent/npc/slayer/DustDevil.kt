package com.grinder.game.entity.agent.npc.slayer

import com.grinder.game.entity.agent.combat.*
import com.grinder.game.entity.agent.npc.monster.MonsterEvents
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil
import com.grinder.game.model.Position
import com.grinder.game.model.Skill
import com.grinder.util.DistanceUtil

/**
 * Handles the Dust devil's effect.
 *
 * @author Blake
 */
class DustDevil(id: Int, pos: Position) : SlayerMonster(id, pos) {

    companion object {
        private val affectedSkills = arrayListOf(Skill.ATTACK, Skill.DEFENCE, Skill.STRENGTH, Skill.RANGED,
                Skill.MAGIC, Skill.AGILITY, Skill.PRAYER)

        fun decreaseLevels(player: Player, skill: Skill) {
            var reduction = player.skillManager.getCurrentLevel(skill)

            if (skill == Skill.AGILITY || skill == Skill.DEFENCE || skill == Skill.PRAYER) {
                reduction = player.skillManager.getCurrentLevel(skill) / 2
            }

            player.skillManager.decreaseLevelTemporarily(skill, reduction, 0)
        }
    }

     init {
         onEvent {
/*             if (it == MonsterEvents.POST_SEQUENCE)
                 if (combat.target != null && !DistanceUtil.isWithinDistance(this@DustDevil, combat.target, 2) && !motion.isMoving)
                     motion.traceTo(combat.target.position)*/
         }
        combat.onIncomingHitApplied {
            attacker.ifPlayer {
                if (!EquipmentUtil.isSmokeProtect(it.equipment))
                    totalDamage = 0
            }
        }
        combat.onOutgoingHitApplied {
            target.ifPlayer {
                if (!EquipmentUtil.isSmokeProtect(it.equipment)) {
                    totalDamage = 16
                    affectedSkills.forEach { skill -> decreaseLevels(it, skill) }
                }
            }
        }
    }

}