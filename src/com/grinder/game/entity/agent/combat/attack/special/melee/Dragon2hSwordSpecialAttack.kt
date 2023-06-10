package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.World
import com.grinder.game.entity.EntityType
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.sound.Sound
import com.grinder.util.Priority
import java.util.*

/**
 * @author _jordan <https://www.rune-server.ee/members/_jordan/>
 */
class Dragon2hSwordSpecialAttack : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.POWERSTAB

    override fun sequence(actor: Agent, target: Agent) {
        val center = actor.position.clone()
        val region = World.regions.fromPosition(center)

        //The wiki doesn't exactly explain this part.
        //From what I understand, its either 14 npcs or 3 players.
        //Im depending on the target type to do this.
        if (target is Player) {
            region.getEntities<Player>(EntityType.PLAYER).filter { player ->
                player != actor && player.isWithinDistance(center, 1)
            }.limit(3)
        } else {
            region.getEntities<NPC>(EntityType.NPC).filter { npc ->
                npc != actor && npc.isWithinDistance(center, 1)
            }.limit(14)
        }.forEach {
            if (it == target || (AreaManager.inMulti(it) && AreaManager.canAttack(actor, it))) {
                actor.combat.queueOutgoingHit(
                        Hit(actor, it, this, HitTemplate
                                .builder(AttackType.MELEE)
                                .setDelay(1)
                                .build(), true))
            }
        }
    }

    class Provider : SpecialAttackProvider {

        override fun getAttackAnimation(type: AttackType?) =
                Animation(3157, Priority.HIGH)

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(559, Priority.HIGH))

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(2530))

        override fun fetchAttackDuration(type: AttackType?) = 7
    }

}