package com.grinder.game.content.skill.skillable.impl.hunter.birdhouse

import com.grinder.game.collision.CollisionManager
import com.grinder.game.content.skill.skillable.impl.hunter.birdhouse.task.EditBirdHouseTask
import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.`object`.DynamicGameObject
import com.grinder.game.entity.`object`.ObjectManager
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.block
import com.grinder.game.entity.agent.player.event.impl.PlayerLoginEvent
import com.grinder.game.entity.agent.player.itemStatement
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.*
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.attribute.value.BirdHouseValueHolder
import com.grinder.game.model.interfaces.dialogue.DialogueManager
import com.grinder.game.model.item.Item
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.ObjectID
import java.util.*

/**
 * Will create and setup our BirdHouses for the beautiful GrinderScape
 */
object BirdHouseActions {

    /**
     * Communicates with our Attributes
     * @see BirdHouseValueHolder
     */
    fun getBirdHouseData(player: Player): HashMap<BirdHouseSpot, BirdHouse> {
        return player.attributes.getValue(Attribute.BIRDHOUSE_DATA) {BirdHouseValueHolder()}
    }

    /**
     * Only updates if we are close to the area
     */
    @JvmStatic
    fun refreshRegional(player: Player) {
        if (getBirdHouseData(player).size > 0) {
            for (birds in getBirdHouseData(player).entries) {
                if (Misc.distanceBetween(player.position, birds.key.hotSpotPos) < 100) {
                    birds.value.owner = player
                    birds.value.refreshBirdHouse()
                }
            }
        }
    }

    /**
     * Updates for our player if we're online & have birdhouses collecting
     */
    @JvmStatic
    fun pulseBirdHouses(player: Player) {
        if (getBirdHouseData(player).size > 0) {
            for (birds in getBirdHouseData(player).entries) {
                if (birds.value.state == BirdHouseState.BUILT_COLLECTING) {
                    birds.value.owner = player;
                    birds.value.verifyState()
                }
            }
        }
    }

    init {

        PlayerActions.onEvent(PlayerLoginEvent::class) {
            if (getBirdHouseData(player).size > 0) {
                for (birds in getBirdHouseData(player).entries) {
                    if (birds.value.state == BirdHouseState.BUILT_FULL) {
                        player.sendMessage("@blu@The birdhouse located at @red@${birds.value.spot.cleanName}@blu@ is ready for harvest!")
                    }
                }
            }
        }

        BirdHouseTier.values().forEach { it ->
            ItemActions.onItemOnItem(ItemID.CLOCKWORK to it.requiredId) {
                when {
                    !player.inventory.contains(ItemID.HAMMER) -> DialogueManager.sendStatement(player, "You need a Hammer in order to build a birdhouse!")
                    player.busy() -> player.sendMessage("Please finish what you are doing!")
                    (player.skillManager.getCurrentLevel(Skill.CRAFTING) < it.levReq + 1) -> player.sendMessage("You need ${it.levReq + 1} Crafting in order to create this bird house!")
                    else -> {
                        player.performAnimation(Animation(898))
                        player.inventory.delete(ItemID.CLOCKWORK, 1)
                        player.inventory.delete(it.requiredId, 1)
                        player.inventory.add(it.birdhouseId, 1)
                        player.skillManager.addExperience(Skill.CRAFTING, 15);
                        player.sendMessage("Combining the Clockwork and ${ItemDefinition.forId(it.requiredId).name} you create a bird house!")
                    }
                }
                return@onItemOnItem true
            }

            ItemActions.onItemOnObjectByItemId(it.birdhouseId to ObjectID.SPACE) {
                handleSpaceClick(player, gameObject.position)
                return@onItemOnObjectByItemId true
            }

            BirdHouseTier.values().forEach {
                ObjectActions.onClick(it.objectId) { it2 ->
                    val spot = BirdHouseSpot.getSpotFromPos(it2.getObject().position) ?: return@onClick true
                    val playerBirdData = getBirdHouseData(it2.player)
                    if (playerBirdData.containsKey(spot)) {
                        playerBirdData[spot]!!.handleClick(it2.objectActionMessage.opcode)
                    }
                    return@onClick true
                }
            }

            BirdHouseTier.values().forEach {
                ObjectActions.onClick(it.objectId + 1) { it2 ->
                    val spot = BirdHouseSpot.getSpotFromPos(it2.getObject().position) ?: return@onClick true
                    val playerBirdData = getBirdHouseData(it2.player)
                    if (playerBirdData.containsKey(spot)) {
                        playerBirdData[spot]!!.handleClick(it2.objectActionMessage.opcode)
                    }
                    return@onClick true
                }
            }

            BirdHouseTier.values().forEach {
                ObjectActions.onClick(it.objectId + 2) { it2 ->
                    val spot = BirdHouseSpot.getSpotFromPos(it2.getObject().position) ?: return@onClick true
                    val playerBirdData = getBirdHouseData(it2.player)
                    if (playerBirdData.containsKey(spot)) {
                        playerBirdData[spot]!!.handleClick(it2.objectActionMessage.opcode)
                    }
                    return@onClick true
                }
            }

            ObjectActions.onClick(ObjectID.SPACE, 30567, 30568) {
                val spot = BirdHouseSpot.getSpotFromPos(it.getObject().position) ?: return@onClick true
                val playerBirdData = getBirdHouseData(it.player)
                if (playerBirdData.containsKey(spot)) {
                    playerBirdData[spot]!!.handleClick(it.objectActionMessage.opcode)
                } else {
                    handleSpaceClick(it.player, it.getObject().position)
                }
                return@onClick true
            }

            /**
             * Handles the 'Space' AKA Patch of the BirdHouse
             */
            ObjectActions.onClick(ObjectID.SPACE) {
                handleSpaceClick(it.player, it.getObject().position)
                return@onClick true
            }
        }

        //Initial Spawn for Birdhouses, what all users see by default. We want to only load data if they're using this feature
        BirdHouseSpot.values().forEach {
            CollisionManager.addClipping(it.hotSpotPos.x, it.hotSpotPos.y, 0, CollisionManager.BLOCKED_TILE);
            ObjectManager.add(DynamicGameObject.createPublic(ObjectID.SPACE, it.hotSpotPos, 10, it.faceDirection.id), true)
        }
    }

    fun handleSpaceClick(player: Player, pos: Position): Boolean {

        val spot = BirdHouseSpot.getSpotFromPos(pos) ?: return false
        val data = getBirdHouseData(player)

        val bestBirdHouse = BirdHouseTier.highestTierInInventory(player);
        if (bestBirdHouse != null) {

            player.performAnimation(Animation(827))

            val birdHouse = BirdHouse(player, spot, BirdHouseState.BUILT_EMPTY, bestBirdHouse)
            player.itemStatement(birdHouse.tier.birdhouseId, 250, "You begin setting up the trap...")

            val buildBirdHouseTask = EditBirdHouseTask(player, birdHouse)
            player.block()
            //We have already verified the user has this item
            player.inventory.delete(Item(birdHouse.tier.birdhouseId, 1))
            TaskManager.submit(buildBirdHouseTask)
            data[spot] = birdHouse
        } else {
            player.message("You do not have a bird house that you have the Hunter level to setup!")
            player.message("You can get a Clockwork from thieving Miscellaneous stalls or trading other players!")
        }
        return true
    }

   // fun handleBirdHouseOnSpace(player: Player, birdHouseTier: BirdHouseTier, )

    /**
     * Since our Birdhouses are private objs i've had to use the Packet class to call this method
     */
     fun useItemOnBirdHouse(player: Player, seedId: Int, pos: Position, obj: Int): Boolean {
        val birdHouseSpot = BirdHouseSpot.getSpotFromPos(pos) ?: return false
        val birdHouseData = getBirdHouseData(player)
        val birdHouseObject = birdHouseData[birdHouseSpot]?.tier?.objectId
        if (birdHouseObject == obj) {
            val playerBirdHouseData = getBirdHouseData(player)
            if (playerBirdHouseData.containsKey(birdHouseSpot)) {
                playerBirdHouseData[birdHouseSpot]!!.handleItemOnObject(seedId)
            }
            return true
        }
        return false
    }
}