package com.grinder.game.entity.agent.combat.attack.weapon.magic

import com.grinder.game.collision.CollisionManager
import com.grinder.game.collision.CollisionPolicy
import com.grinder.game.entity.agent.*
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Graphic
import com.grinder.game.model.Graphics
import com.grinder.game.model.MagicSpellbook
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.item.Item
import com.grinder.game.model.sound.Sounds
import java.util.*
import kotlin.math.abs

/**
 * A [CombatSpell] implementation that is primarily used for spells that
 * are a part of the ancients spellbook.
 *
 * @author lare96
 */
abstract class CombatAncientSpell : CombatSpell() {
    override fun getSpellbook(): MagicSpellbook {
        return MagicSpellbook.ANCIENT
    }

    override fun finishCast(cast: Agent, castOn: Agent, accurate: Boolean, damage: Int) {

        // The spell wasn't accurate, so do nothing.
        if (!accurate || damage <= 0) {
            return
        }

        // Do the spell effect here.
        spellEffect(cast, castOn, damage)

        // The spell doesn't support multiple targets or we aren't in a
        // multicombat zone, so do nothing.
        if (spellRadius() == 0 || !AreaManager.inMulti(castOn)) return


        // We passed the checks, so now we do multiple target stuff.
        var it: Iterator<Agent>? = null
        if (cast.isPlayer && castOn.isPlayer) it =
            (cast as Player).localPlayers.iterator() else if (cast.isPlayer && castOn.isNpc) it =
            (cast as Player).localNpcs.iterator() else if (cast.isNpc && castOn.isNpc) it =
            AgentUtil.getNPCsInProximity(cast, spellRadius(), CollisionPolicy.NONE)
                .iterator() else if (cast.isNpc && castOn.isPlayer) it =
            AgentUtil.getPlayersInProximity(cast, spellRadius(), CollisionPolicy.NONE).iterator()


        if (it == null) return
        val iterator: Iterator<Agent> = it
        while (iterator.hasNext()) {
            val next = iterator.next()

            if (next.isNpc) {
                val n = next as NPC
                if (!n.fetchDefinition().isAttackable) {
                    continue
                }
                if (!AreaManager.canAttack(cast, n) || !AreaManager.inMulti(n)) {
                    continue
                }
                if (CollisionManager.blocked(n.position)) {
                        continue
                }
                if (!CollisionManager.canMove3(n.position, cast.position, 1, 1)) {
                    continue
                }
                if (!CollisionManager.canMove(n.position, 3)) {
                    continue
                }
                if (!CollisionManager.canMove(n.position, 6)) {
                    continue
                }
                if (!CollisionManager.canMove(n.position, 4)) {
                    continue
                }
                if (!CollisionManager.canMove(n.position, 1)) {
                    continue
                }
            } else {
                val p = next as Player
                if (!AreaManager.canAttack(cast, p) || !AreaManager.inMulti(p) || p.isInTutorial) {
                    continue
                }
                if (p.inWilderness() && cast.inWilderness()) {
                    val combatDifference: Int =
                        abs(cast.combatLevelCapped_126() - p.combatLevelCapped_126())
                    if (combatDifference > cast.getWildernessLevel() || combatDifference > p.wildernessLevel) {
                        continue
                    }
                }
                if (CollisionManager.blocked(p.position)) {
                    continue
                }
                if (!CollisionManager.canMove3(p.position, cast.position, 1, 1)) {
                    continue
                }

            }
            if (next.position.isWithinDistance(castOn.position, spellRadius()) && next != cast
                && next != castOn && next.hitpoints > 0 && next.hitpoints > 0
            ) {
/*                if (next.inWilderness() && cast.inWilderness()) {
                    val combatDifference: Int =
                        abs(cast.combatLevelCapped_126() - next.combatLevelCapped_126())
                    if (combatDifference > cast.getWildernessLevel()) {
                        continue
                    }
                }*/
                val qH = Hit(cast, next, MagicAttackStrategy.INSTANCE, true, 0)
                    .setHandleAfterHitEffects(false)
                if (qH.isAccurate) {

                    // Successful hit, send graphics and do spell effects.
                    endGraphic(cast, next).ifPresent { graphic: Graphic? -> next.performGraphic(graphic) }
                    spellEffect(cast, next, qH.totalDamage)
                } else {

                    // Unsuccessful hit. Send splash graphics for the spell
                    // because it wasn't accurate
                    next.performGraphic(Graphics.SPLASH_GRAPHIC)
                    next.asOptionalPlayer.ifPresent { player: Player -> player.packetSender.sendAreaPlayerSound(Sounds.MAGIC_SPLASH) }
                }
                cast.combat.queueOutgoingHit(qH)
            }
        }
    }

    override fun equipmentRequired(player: Player): Optional<Array<Item>> {

        // Ancient spells never require any equipment, although the method can
        // still be overridden if by some chance a spell does.
        return Optional.empty()
    }

    /**
     * The effect this spell has on the target.
     *
     * @param cast
     * the entity casting this spell.
     * @param castOn
     * the person being hit by this spell.
     * @param damage
     * the damage inflicted.
     */
    abstract fun spellEffect(cast: Agent?, castOn: Agent?, damage: Int)

    /**
     * The radius of this spell, only comes in effect when the victim is hit in
     * a multicombat area.
     *
     * @return how far from the target this spell can hit when targeting
     * multiple entities.
     */
    abstract fun spellRadius(): Int
}