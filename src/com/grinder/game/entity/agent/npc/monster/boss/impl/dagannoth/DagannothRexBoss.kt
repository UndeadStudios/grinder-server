package com.grinder.game.entity.agent.npc.monster.boss.impl.dagannoth

import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.Animation
import com.grinder.game.model.Position

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   30/09/2019
 * @version 1.0
 */
class DagannothRexBoss(npcId: Int, position: Position = Position(2913, 4448, 0), customVariant: Boolean = false)
    : DagannothBoss(npcId, position, customVariant), AttackProvider {
    init {
        race = MonsterRace.DAGANNOTH
        movementCoordinator.radius = 20
    }
    override fun generateAttack(): BossAttack {
        val attack = BossAttack(this)
        attack.setType(AttackType.MELEE)
        return attack
    }
    override fun getRetreatPolicy() = MonsterRetreatPolicy.NEVER
    override fun attackTypes() = AttackType.MELEE
    override fun attackRange(type: AttackType) = 1
    override fun maxTargetsHitPerAttack(type: AttackType) = 1
    override fun fetchAttackDuration(type: AttackType?) = 4
    override fun getAttackAnimation(type: AttackType?) = Animation(2853)
    override fun fetchHits(type: AttackType?) = HitTemplate
            .builder(AttackType.MELEE)
            .setDelay(0)
            .setDefenceStat(EquipmentBonuses.DEFENCE_SLASH)
            .buildAsStream()
}