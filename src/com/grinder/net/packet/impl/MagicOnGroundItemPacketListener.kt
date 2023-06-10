package com.grinder.net.packet.impl

import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.content.skill.skillable.impl.magic.InteractiveSpell
import com.grinder.game.definition.ItemValueType
import com.grinder.game.entity.`object`.ClippedMapObjects
import com.grinder.game.entity.`object`.ObjectType
import com.grinder.game.entity.agent.combat.LineOfSight.withinSight
import com.grinder.game.entity.agent.movement.task.WalkToAction
import com.grinder.game.entity.agent.movement.teleportation.TeleportType
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.grounditem.ItemOnGroundManager
import com.grinder.game.entity.passedTimeGenericAction
import com.grinder.game.message.decoder.MagicOnGroundItemMessageDecoder
import com.grinder.game.message.impl.MagicOnGroundItemMessage
import com.grinder.game.model.*
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.attribute.AttributeManager
import com.grinder.game.model.item.BrokenItems
import com.grinder.game.model.item.ItemUtil
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.net.packet.PacketListener
import com.grinder.net.packet.PacketReader
import com.grinder.util.Executable
import com.grinder.util.Logging
import java.util.*

/**
 * Handles [MagicOnGroundItemMessage] packets.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   21/07/2021
 * @version 1.0
 */
class MagicOnGroundItemPacketListener : PacketListener {

    override fun handleMessage(player: Player, packetReader: PacketReader, packetOpcode: Int) {
        val message: MagicOnGroundItemMessage = MagicOnGroundItemMessageDecoder().decode(packetReader.packet)

        val spellId = message.spellId
        val itemId = message.itemId
        val itemX = message.itemX
        val itemY = message.itemY
        val position = Position(itemX, itemY, player.z)

        if (player.isInTutorial)
            return

        if (player.busy())
            return;

        if (player.BLOCK_ALL_BUT_TALKING)
            return;

        if (player.isTeleporting && player.teleportingType == TeleportType.HOME)
            player.stopTeleporting()

        if (player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT) || player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2))
            return

        if (!player.notAfkOrBusyOrInteracting())
            return

        if (!player.passedTimeGenericAction(1, false)) {
            player.message("You must wait a few seconds before you can cast the next spell.", Color.NONE)
            return
        }

        if (player.gameMode.isSpawn) {
            player.sendMessage("You cannot telegrab this item on spawn game mode.");
            return;
        }
        val optionalItemOnGround = ItemOnGroundManager.getItemOnGround(Optional.of(player.username), itemId, position)

        if (optionalItemOnGround.isPresent) {

            val groundItem = optionalItemOnGround.get()
            val optionalOwner: Optional<String> = groundItem.findOwner()
            if (optionalOwner.isPresent) {
                val owner: String = optionalOwner.get()
                if (player.gameMode.isAnyIronman && !owner.equals(
                        player.username,
                        ignoreCase = true
                    ) && player.minigame == null
                ) {
                    player.packetSender.sendMessage("You can't telegrab items dropped by other players as an Iron Man.", 1000)
                    return
                }
            }
        }


        SkillUtil.stopSkillable(player)
        player.combat.reset(false)

        InteractiveSpell.forSpellId(spellId).ifPresent { spellType ->
            if (spellType == InteractiveSpell.TELEKINETIC_GRAB) {

                player.setWalkToTask(
                    WalkToAction(
                        player, position,
                        1,
                        Executable {

                            val spell = spellType.spell
                            if (!spell.canCast(player, null, false))
                                return@Executable

                            ItemOnGroundManager.findVisibleItemOnGround(player, itemId, position).ifPresent {

                                if (withinSight(player, position, 15, false)) {
                                    player.positionToFace = position
                                    player.motion.reset();
                                    player.performAnimation(Animation(711))
                                    player.performGraphic(Graphic(142, GraphicHeight.MIDDLE))

                                    spell.deleteItemsRequired(player)


                                    // Logging Start
                                    val highAlchValue: Long = it.item.getValue(ItemValueType.HIGH_ALCHEMY)
                                    val priceEstValue: Long = it.item.getValue(ItemValueType.PRICE_CHECKER)
                                    val tokenValue: Long = it.item.getValue(ItemValueType.OSRS_STORE)
                                    if ((highAlchValue * it.item.amount >= 5000000 || it.item.amount * priceEstValue >= 50000000
                                            || it.item.amount * tokenValue > 5000000 || BrokenItems.breaksOnDeath(it.item.id) || it.item.id in 15200..15350
                                            || it.item.getValue(ItemValueType.ITEM_PRICES) > 2000000 || ItemUtil.isHighValuedItem(it.item.definition.getName())
                                            || it.item.getValue(ItemValueType.PRICE_CHECKER) > 50000000 && it.item.id != 8851)) {
                                        Logging.log("Valueabletelegrabs", "" + player.username +" telegrabbed " + it.item.amount +" x " + it.item.definition.name + " position: " + position +"")
                                        PlayerUtil.broadcastPlayerHighStaffMessage("" + player.username +" telegrabbed value item of " + it.item.amount +"  " + it.item.definition.name + " position: " + position +"")
                                    }
                                    player.setTeleGrabCount(player.getTeleGrabCount() + 1)
                                    if (player.getTeleGrabCount() >= 30) {
                                        PlayerUtil.broadcastPlayerStaffMessage(""+ player.username +" has done over 30 telegrabs in the current session. Log file has been created.")
                                        player.setTeleGrabCount(0)
                                    }
                                    Logging.log("Telegrabs", "" + player.username +" telegrabbed " + it.item.amount +" x " + it.item.definition.name + " position: " + position +"")
                                    // End of Logging

                                    player.addExperience(Skill.MAGIC, spell.baseExperience()/ 100)
                                    player.points.increase(AttributeManager.Points.SPELLS_CASTED)
                                    player.points.increase(AttributeManager.Points.TELEKINITIC_CASTS)

                                    val endHeight = ClippedMapObjects
                                        .getObjectsAt(position)
                                        .firstOrNull { obj -> obj.objectType == ObjectType.INTERACTABLE.value }
                                        ?.definition?.modelHeight ?: 0

                                    val template = ProjectileTemplate.builder(143)
                                        .setDelay(50)
                                        .setSpeed(10)
                                        .setHeights(50, endHeight / 4)
                                        .setCurve(5)
                                        .setDepartureSound(Sound(Sounds.TELEGRAB_SPELL))
                                        .build()
                                    val projectile = Projectile(player.position, position, template)

                                    projectile.sendProjectile()
                                    projectile.onArrival {
                                        Graphic.sendGlobal(Graphic(144, 0, endHeight), position)
                                        if (!it!!.isPendingRemoval) {
                                            if (it.item.amount > 0){
                                            ItemOnGroundManager.deregister(it)
                                            player.inventory.add(it.item)
                                        }
                                        }
                                    }
                                } else
                                    player.message("I can't reach that!")
                            }
                        }, WalkToAction.Policy.EXECUTE_ON_LINE_OF_SIGHT
                    )
                )
            }
        }
    }
}