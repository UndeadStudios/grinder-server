package com.grinder.game.content.skill.skillable.impl.hunter

import com.grinder.game.content.miscellaneous.PetHandler
import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.content.skill.skillable.impl.hunter.HunterActions.configureTrap
import com.grinder.game.entity.`object`.ObjectManager
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.tryRandomEventTrigger
import com.grinder.game.entity.grounditem.ItemOnGround
import com.grinder.game.entity.grounditem.ItemOnGroundManager
import com.grinder.game.model.*
import com.grinder.game.model.item.Item
import com.grinder.net.packet.PacketConstants
import com.grinder.util.Misc
import org.apache.logging.log4j.LogManager
import java.util.*
import java.util.function.Predicate

/**
 * This object's sole purpose is to configure packet handlers for Hunter actions.
 *
 * TODO: remove duplicated code in [configureTrap]
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   30/11/2019
 * @version 1.0
 */
object HunterActions {

    private val LOGGER = LogManager.getLogger(HunterActions::class.java)!!

    init {
//        HunterCatchType.values().forEach { type ->
//            if (type.technique != HunterTechniqueType.TRAP) {
//                NPCActions.onClick(type.npcId) {
//
//                    val player = it.player
//                    val npc = it.npc
//
//                    if (type.technique.precondition.test(player, type)) {
//                        val action = HunterCatchAction(type, npc)
//                        SkillUtil.startActionTask(player, action)
//                    }
//                    return@onClick true
//                }
//            }
//        }
//        configureTrap(HunterToolType.BIRD_SNARE)
//        configureTrap(HunterToolType.BOX_TRAP)

    }

    /**
     * Drops the trap onto the ground for the world to see; used for logouts
     * @param logout If this method is being called during logout stages or not
     * @param player Player in question
     */
    fun dropTrap(trap: HunterTrap) {
        val droppedTrap = ItemOnGround(
            ItemOnGround.State.SEEN_BY_EVERYONE,
            Optional.empty(),
            trap.obj.position,
            trap.toolType.requiredItem,
            true,
            -1,
            ItemOnGroundManager.STATE_UPDATE_DELAY
        )
        ItemOnGroundManager.register(droppedTrap)
    }


    /**
     * Used to Clear Traps on user logout
     */
    public fun clearTraps(player: Player) {
        HunterTraps.PLAYER_TRAPS.forEach { user, playerTraps ->
            if (user == player.username) {
                var removalCount = 0
                val iterator = playerTraps.iterator()
                while (iterator.hasNext()) {
                    val trap = iterator.next()

                    dropTrap(trap)
                    iterator.remove()
                    trap.deSpawn(true)
                    removalCount++;
                }
                println("Removed $removalCount total Hunter Traps for $user")
            }
        }
    }

    private fun inHunterArea(position: Position): Boolean {
        return if (position.x in 3119..3172 || position.y in 3754..3792) true
        else position.x in 2688..2751 && position.y >= 3741 && position.y <= 3810
    }

    private fun configureTrap(toolType: HunterToolType) {
        val requiredItem = toolType.requiredItem
        val waitingObjectId = toolType.waitingObjectId
        val successObjectId = toolType.successObjectId
        val failedObjectId = toolType.failedObjectId
        if (requiredItem == null || waitingObjectId == -1 || successObjectId == -1 || failedObjectId == -1) {
            LOGGER.error("Could not configure actions for trap $toolType")
            return
        }
        ItemActions.onClick(requiredItem.id) {

            if (isInInventory() && !isDropAction()) {

                val position = player.position

                if (ObjectManager.findDynamicObjectAt(player.position, Predicate { gameObject -> gameObject.position.equals(position) && Misc.objectTypesCollide(gameObject.objectType, 10) }).isPresent) {
                    player.sendMessage("You can't setup a trap here. Try moving around a bit.", 1000)
                    return@onClick true
                }
                if (!inHunterArea(position)) {
                    player.sendMessage("You can only do this at the Hunter area.");
                    return@onClick true
                }

                val trapCount = HunterTraps.countTrapsOf(player)
                val maxTraps = HunterTraps.maxTrapsFor(player)

                if (trapCount >= maxTraps) {
                    player.sendMessage("You can't setup any more traps.", 1000)
                    return@onClick true
                }
                val trap = HunterTrap(player, toolType)

                /*
            * TODO:Rabbit snare requirement
            */
                /*if (trap.get().equals(TrapType.RABBIT_SNARE)) {
                if (!player.getInventory().contains(ItemIdentifiers.FERRET)) {
                    player.getPacketSender().sendMessage("You need to use a ferret as bait to lay this trap.", 1000);
                    return true;
                }
                player.getInventory().delete(new Item(ItemIdentifiers.FERRET));
            }*/
                player.performAnimation(Animation(827))
                player.inventory.delete(requiredItem.id, 1)

                trap.place()
                return@onClick true
            }
            return@onClick false
        }


        ObjectActions.onClick(failedObjectId) {
            val player = it.player
            val message = it.objectActionMessage
            val position = Position(message.x, message.y, player.plane)
            val trap = HunterTraps.findTrap(it.player, position)

            if (trap == null) {
                player.sendMessage("This is not your trap to loot!")
                return@onClick true
            }
            player.positionToFace = trap.obj.position
            if (message.opcode == PacketConstants.OBJECT_SECOND_CLICK_OPCODE) {
                player.performAnimation(Animation(827))
                trap.reset()
                return@onClick true
            }

            val inventory = player.inventory
            if (inventory.countFreeSlots() < 1) {
                player.sendMessage("You do not have enough free inventory slots to do this!")
                return@onClick true
            }

            player.performAnimation(Animation(827))
            inventory.add(requiredItem.clone())
            trap.remove()
            return@onClick true
        }
        ObjectActions.onClick(waitingObjectId) {
            val player = it.player
            val message = it.objectActionMessage
            val position = Position(message.x, message.y, player.plane)
            val trap = HunterTraps.findTrap(it.player, position)

            if (trap == null) {
                player.sendMessage("This is not your trap to loot!")
                return@onClick true
            }

            val inventory = player.inventory
            if (inventory.countFreeSlots() < 1) {
                player.sendMessage("You do not have enough free inventory slots to do this!")
                return@onClick true
            }

            player.positionToFace = trap.obj.position
            player.performAnimation(Animation(827))
            inventory.add(requiredItem.clone())
            trap.remove()
            return@onClick true
        }

        ObjectActions.onClick(successObjectId) {
            val player = it.player
            val message = it.objectActionMessage
            val position = Position(message.x, message.y, player.plane)
            val trap = HunterTraps.findTrap(it.player, position)

            if (trap == null) {
                player.sendMessage("This is not your trap to loot!")
                return@onClick true
            }
            player.positionToFace = trap.obj.position
            if (message.opcode == PacketConstants.OBJECT_SECOND_CLICK_OPCODE) {
                player.performAnimation(Animation(827))
                trap.reset()
                return@onClick true
            }

            // make sure no one can spam click while the trap is pending removal
            if (trap.state == HunterTrapState.LOOTED)
                return@onClick true

            val inventory = player.inventory
            val requiredSlots = if (trap.caught != null) 3 else 1
            if (inventory.countFreeSlots() < requiredSlots) {
                player.sendMessage("You do not have enough free inventory slots to do this!")
                return@onClick true
            }

            if (trap.state == HunterTrapState.SUCCESS) {
                val catchType = trap.caught!!
                val reward = catchType.loot
                val coinReward = Item(995, catchType.requiredLevel * 55)
                player.skillManager.addExperience(Skill.HUNTER, catchType.experienceGain)
                reward?.let { loot -> inventory.add(loot) }
                inventory.add(coinReward)
                player.tryRandomEventTrigger()
                trap.state = HunterTrapState.LOOTED
            }

            player.performAnimation(Animation(827))
            inventory.add(requiredItem.clone())
            trap.remove()
            PetHandler.onSkill(player, Skill.HUNTER)
            return@onClick true
        }
    }
}