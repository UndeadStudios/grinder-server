package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.World
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.*
import com.grinder.game.model.Direction.*
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.item.name
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Priority
import com.grinder.util.oldgrinder.EquipSlot
import java.util.*
import java.util.stream.Stream
import kotlin.collections.HashSet


/**
 * https://oldschool.runescape.wiki/w/Dragon_halberd
 * https://oldschool.runescape.wiki/w/Crystal_halberd
 *
 * "The dragon halberd has a special attack called Sweep,
 * consuming 30% of the player's special attack energy.
 * In addition to a 10% damage boost, if used against "large" monsters
 * (anything that is larger than 1x1, such as General Graardor),
 * the special attack will deal an additional second hit onto them,
 * although with 25% reduced accuracy."
 *
 * TODO: Check if player and npc stream in multi area can be combined
 *
 * credits to jire for this https://www.rune-server.ee/runescape-development/rs2-server/snippets/677859-perfect-scythe-vitur-dragon-halberd-special-graphics.html
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   12/05/2020
 * @version 1.0
 */
class SweepSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.SWEEP

    override fun secondaryDamageModifier(context: AttackContext) = 1.10

    override fun sequence(actor: Agent, target: Agent) {

        val pair = getDirectionAndGraphicId(actor, target)
        val gfx: Int = pair.first
        val direction = pair.second
        val position = actor.centerLocation.clone().move(direction)

        Graphic.sendGlobal(Graphic(gfx, 0, 96, Priority.HIGH), position)

        val cx = position.x
        val cy = position.y

        val xRange = when (direction) {
            NORTH, SOUTH -> cx - 1..cx + 1
            else -> cx..cx
        }
        val yRange = when (direction) {
            EAST, WEST -> cy - 1..cy + 1
            else -> cy..cy
        }

        val npcs = HashSet<NPC>()
        val players = HashSet<Player>()
        for (x in xRange) {
            for (y in yRange) {
                val pos = Position(x, y, target.position.z)
                val region = World.regions.fromPosition(pos)
                val entities = region.getEntities(pos)
                for (entity in entities) {
                    if (entity != actor && entity != target) {
                        if (entity is Player)
                            players.add(entity)
                        if (entity is NPC)
                            npcs.add(entity)
                    }
                }
            }
        }

        var playerStream = players.stream()
        var npcStream = npcs.stream()

        if (target is Player) {
            playerStream = Stream.concat(Stream.of(target), playerStream.limit(2))
            npcStream = npcStream.limit(10)
        }

        if (target is NPC) {
            npcStream = Stream.concat(Stream.of(target), npcStream.limit(9))
            playerStream = playerStream.limit(3)
        }

        Stream.concat(playerStream, npcStream).forEach {

            val primaryTarget = it == target

            if (primaryTarget || (AreaManager.inMulti(it) && AreaManager.canAttack(actor, it))) {

                val hit1 = Hit(actor, it, this, HitTemplate
                        .builder(AttackType.MELEE)
                        .setDelay(1)
                        .build(), true)

                // Send sound
                if (actor is Player)
                actor.asPlayer.packetSender.sendSound(Sounds.DRAGON_HALLY_SPECIAL_SOUND);

                actor.combat.queueOutgoingHit(hit1)

                if (it is NPC) {
                    if (it.size > 1) {

                        val hit2 = Hit(actor, it, this, HitTemplate
                                .builder(AttackType.MELEE)
                                .setDelay(1)
                                .build(), false)

                        hit2.multiplyAccuracy(0.75)
                        hit2.createHits(1)

                        actor.combat.queueOutgoingHit(hit2)
                    }
                }
            }
        }
    }

    private fun getDirectionAndGraphicId(actor: Agent, target: Agent): Pair<Int, Direction> {
        val crystalHalberd = usesCrystalHalberd(actor)

        val gfx: Int
        var direction = getDirection(actor.centerPosition, target.centerPosition)

        when (direction) {
            SOUTH, SOUTH_EAST -> {
                gfx = if(crystalHalberd) 1232 else 478
                direction = SOUTH
            }
            NORTH, NORTH_WEST -> {
                gfx = if(crystalHalberd) 1233 else 506
                direction = NORTH
            }
            EAST, NORTH_EAST -> {
                gfx = if(crystalHalberd) 1234 else 1172
                direction = EAST
            }
            else -> {
                gfx = if(crystalHalberd) 1235 else 1231
                direction = WEST
            }
        }
        return Pair(gfx, direction)
    }

    private fun usesCrystalHalberd(actor: Agent) =
            actor is Player && actor.equipment.atSlot(EquipSlot.WEAPON)?.name()?.toLowerCase()?.contains("crystal") == true

    class Provider : SpecialAttackProvider {

        override fun getAttackAnimation(type: AttackType?) =
                Animation(1203, Priority.HIGH)

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.DRAGON_HALLY_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 7
    }
}