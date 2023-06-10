package com.grinder.game.entity.updating

import com.grinder.game.World
import com.grinder.game.content.item.coloring.ItemColorCustomizer
import com.grinder.game.entity.Entity
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.updating.block.*
import com.grinder.game.model.*
import com.grinder.game.model.attribute.Attribute
import kotlin.math.atan2
import kotlin.math.floor

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   24/03/2020
 * @version 1.0
 */
open class UpdateBlock {

    companion object {

        val DEFAULT_ANIMATION_SET = BasicAnimationSet(808, 823, 819, 820, 821, 822, 824)

        fun createColorItemBlock(player: Player) = ColorItemBlock(ItemColorCustomizer.ColorfulItem.values().map { it.getColorsFor(player).toIntArray() }.toTypedArray())

        fun createGraphicBlock(player: Player) = GraphicBlock(player.graphic)
        fun createGraphicBlock(graphic: Graphic) = GraphicBlock(graphic)

        fun createAnimationBlock(player: Player) = AnimationBlock(player.animation)
        fun createAnimationBlock(animation: Animation) = AnimationBlock(animation)

        //fun createUpdateHitBlock(player: Player, damage: Damage) = HitFirstUpdateBlock(damage.value, damage.damageMask.spriteId(), player.hitpoints, player.skillManager.getMaxLevel(Skill.HITPOINTS))
        fun createUpdateFirstHitBlock(agent: Agent, damage: Damage) = HitFirstUpdateBlock(damage, agent.hitpoints, agent.maxHitpoints)
        fun createUpdateSecondHitBlock(agent: Agent, damage: Damage) = HitSecondUpdateBlock(damage, agent.hitpoints, agent.maxHitpoints)

        fun createTurnToPositionBlock(player: Player) = TurnToPositionBlock(-1)
        fun createTurnToPositionBlock(position: Position) = TurnToPositionBlock(position.let {
            val faceX: Int = (it.x shl 1) + 1
            val faceY: Int = (it.y shl 1) + 1
            faceX shl 16 or faceY
        })
        fun createTurnToPositionBlock(src: Position, dst: Position, width: Int =1, length: Int = 1) = TurnToPositionBlock(let {
            val srcX = src.x * 64
            val srcY = src.y * 64
            val dstX = dst.x * 64
            val dstY = dst.y * 64
            var degreesX = (srcX - dstX).toDouble()
            var degreesY = (srcY - dstY).toDouble()
            degreesX += (floor(width / 2.0)) * 32
            degreesY += (floor(length / 2.0)) * 32
            (atan2(degreesX, degreesY) * 325.949).toInt() and 0x7ff
        })

        fun createTurnToPositionBlock(faceDegrees: Int, facing: Direction = Direction.SOUTH) = TurnToPositionBlock(faceDegrees)

        fun createInteractingMobBlock(entity: Entity?) = InteractingMobBlock(entity?.interactionIndex
                ?: InteractingMobBlock.RESET_INDEX)

        fun createChatBlock(player: Player) = player.currentChatMessage.let {
            ChatBlock(
                    it.color.id,
                    it.effect.id,
                    player.rightsValue,
                    player.crown,
                    it.text
            )
        }

        fun createForceChatBlock(player: Player) = ForceChatBlock(player.forcedChat)
        fun createForceChatBlock(message: String) = ForceChatBlock(message)

        fun createTransformBlock(id: Int, headIconId: Int = -1) = TransformBlock(id, headIconId)

        fun createForceMovementBlock(agent: Agent) = agent.forceMovement.let {
            ForceMovementBlock(
                    it.start,
                    it.end,
                    it.startTick,
                    it.endTick,
                    it.animation,
                    it.direction
            )
        }


        fun createAppearanceBlock(player: Player, updateColorfulItems: Boolean): AppearanceBlock {
            val bas = when {
                player.npcTransformationId != -1 -> {
                    World.npcManager.get(player.npcTransformationId)?.let {
                        BasicAnimationSet(it.standingAnimation, it.idleRotateLeftAnimation, it.walkingAnimation, it.rotate180Animation, it.rotateLeftAnimation, it.rotateRightAnimation, it.walkingAnimation)
                    }
                }
                player.appearance.bas != null -> player.appearance.bas
                else -> {
                    player.equipment.items[EquipmentConstants.WEAPON_SLOT]?.definition?.let {
                        BasicAnimationSet(it.standAnim, it.standTurnAnim, it.walkAnim, it.turn180Anim, it.turn90CCWAnim, it.turn90CWAnim, it.runAnim)
                    }
                }
            } ?: DEFAULT_ANIMATION_SET
            return AppearanceBlock(
                    player.appearance,
                    if (player.isSkulled) player.skullType.iconId else -1,
                    player.npcTransformationId,
                    player.equipment,
                    bas,
                    player.longUsername,
                    player.skillManager.calculateCombatLevel(),
                    player.rightsValue,
                    player.crown,
                    player.title,
                    player.getBoolean(Attribute.INVISIBLE),
                    if (updateColorfulItems) {
                        ItemColorCustomizer.ColorfulItem.getAllColorsFor(player)
                    } else
                        null
            )
        }
    }

}