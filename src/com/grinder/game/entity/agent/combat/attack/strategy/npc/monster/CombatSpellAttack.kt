package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpell
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.Graphics
import com.grinder.game.model.projectile.ProjectileTemplateBuilder
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Priority

/**
 * CombatSpellAttack is a NPCAttackStrategy that utilizes the definitions of a CombatSpell.
 * @param spellType The CombatSpell to be cast.
 *
 * This could be rolled into MagicAttackStrategy but would require definition changes.
 */
class CombatSpellAttack(val spellType:CombatSpell) : MagicAttackStrategy() {
    override fun animate(actor: Agent) {
        actor.combat.castSpell = spellType
    }

    // this would reset combat
    override fun postHitAction(actor: Agent, target: Agent) {
        actor.combat.previousCast = actor.combat.castSpell
    }
}