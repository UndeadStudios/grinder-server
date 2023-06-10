package com.grinder.game.content.item

import com.grinder.game.collision.CollisionManager
import com.grinder.game.content.`object`.DwarfCannon
import com.grinder.game.content.minigame.castlewars.CastleWars
import com.grinder.game.content.quest.QuestManager
import com.grinder.game.entity.`object`.DynamicGameObject
import com.grinder.game.entity.`object`.ObjectManager
import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.*
import com.grinder.game.model.areas.Area
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.areas.InstancedBossArea
import com.grinder.game.model.areas.UntypedInstancedBossArea
import com.grinder.game.model.areas.godwars.GodChamberArea
import com.grinder.game.model.areas.godwars.GodwarsArea
import com.grinder.game.model.areas.impl.*
import com.grinder.game.model.areas.impl.slayer.LizardmanShamanArea
import com.grinder.game.model.areas.impl.slayer.SlayerTowerArea
import com.grinder.game.model.areas.impl.slayer.SmokeDevilDungeonArea
import com.grinder.game.model.areas.instanced.*
import com.grinder.game.model.item.Item
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import com.grinder.util.ObjectID

/**
 * Created by Kyle Fricilone on May 26, 2020.
 */
object CannonBase {

    private const val RADIUS = 85
    private val BLACK_DWARF_GUARD = Position(3008, 3452, 0)

    private val CANNON_ITEM_IDS = intArrayOf(
        ItemID.CANNON_BASE,
        ItemID.CANNON_STAND,
        ItemID.CANNON_BARRELS,
        ItemID.CANNON_FURNACE)

    private val CANNON_OBJ_IDS = intArrayOf(
        ObjectID.CANNON_BASE,
        ObjectID.CANNON_STAND,
        ObjectID.CANNON_BARRELS,
        ObjectID.DWARF_MULTICANNON)

    private val SETUP_MSGS = arrayOf(
        "You place the cannon base on the ground.",
        "You add the stand.",
        "You add the barrels.",
        "You add the furnace.")

    init {
        onFirstInventoryAction(ItemID.DWARF_CANNON_SET) {
            if (player.checkFreeInventorySlots(4)){
                val setItem = getItem()?:return@onFirstInventoryAction
                if (player.removeInventoryItem(setItem, -1)){
                    player.addInventoryItem(Item(ItemID.CANNON_BASE), -1)
                    player.addInventoryItem(Item(ItemID.CANNON_STAND), -1)
                    player.addInventoryItem(Item(ItemID.CANNON_BARRELS), -1)
                    player.addInventoryItem(Item(ItemID.CANNON_FURNACE), -1)
                    player.inventory.refreshItems()
                }
            }
        }
        ItemActions.onClick(ItemID.CANNON_BASE) {
            if (isInInventory()) {

                if (!QuestManager.hasCompletedQuest(player, "Dwarf Cannon")) {
                    player.sendMessage("You need to complete " +
                            "the quest 'Dwarf Cannon' quest to be able to use this cannon.")
                    return@onClick false
                }

                if (CANNON_ITEM_IDS.any { player.inventory.getAmount(it) < 1 }) {
                    player.message("You do not have all the required parts.")
                    return@onClick false
                }

                if (player.cannon != null) {
                    player.message("You already have a cannon assembled.")
                    return@onClick false
                }

                if (player.area != null && player.area is UntypedInstancedBossArea || player.area is InstancedBossArea) {
                    player.message("You cannot setup a cannon here.")
                    return@onClick false
                }

                if (isProhibited(player)) {
                    return@onClick false
                }

                val pos = player.position.copy()

                val positions = Direction.VALUES
                    .filterNot { it == Direction.NONE }
                    .map { pos.copy().move(it) }

                if (positions.any { CollisionManager.blocked(it) }) {
                    player.sendMessage("Not enough room to setup your cannon.")
                    return@onClick false
                }

                for (x in -5..5) {
                    for (y in -5..5) {
                        if (ObjectManager.existsAt(
                                ObjectID.DWARF_MULTICANNON,
                                pos.copy().move(x, y)
                            )
                        ) {
                            player.message("Your cannon is too close to another cannon.")
                            return@onClick false
                        }
                    }
                }

                setup(player, pos)

                return@onClick true
            }
            return@onClick false
        }
    }

    fun setup(player: Player, pos: Position) {
        val objPos = pos.copy().move(Direction.SOUTH_WEST)

        move(player)

        player.BLOCK_ALL_BUT_TALKING = true
        TaskManager.submit(object : Task(3, false) {
            var cycle = 0

            override fun execute() {
                if (cycle < 4) {
                    player.cannon?.let { ObjectManager.remove(it.obj, true) }
                    val obj = DynamicGameObject.createPublic(CANNON_OBJ_IDS[cycle], objPos, 10, 0)
                    obj.setSpawnedFor(player)
                    ObjectManager.add(obj, true)

                    player.cannon = DwarfCannon(obj)
                    player.inventory.delete(CANNON_ITEM_IDS[cycle], 1)
                    player.setPositionToFace(pos, true)
                    player.performAnimation(Animation(827))
                    player.sendMessage(SETUP_MSGS[cycle])
                    player.packetSender.sendAreaEntitySound(obj, 2876, 10, 1, 0)
                } else {
                    player.cannon.load()
                    player.cannon.setupDegradeTask()
                    player.BLOCK_ALL_BUT_TALKING = false

                    stop()
                }

                cycle++
            }

        })
    }

    private fun move(player: Player) {
        Direction.VALUES.filterNot { it == Direction.NONE }
            .map { player.position.copy().move(it.directionVector.mul(2)) }
            .find { !CollisionManager.blocked(it) }
            ?.let { player.motion.clearAndEnqueue(it) }
    }

    private fun isProhibited(player: Player): Boolean {
        if (player.area != null) {
            if (isAreaProhibited(player, player.area) || player.area.isCannonProhibited) {
                player.sendMessage(getAreaMessage(player, player.area))
                return true
            }
        }

        val pos = player.position
        if (pos.isWithinDistance(BLACK_DWARF_GUARD, RADIUS)) {
            player.statement(   "It is not permitted to set up a cannon this close to the Dwarf Black", "Guard.")
            return true
        }

        return false
    }

    private fun isAreaProhibited(player: Player, area: Area): Boolean {

        // KQ Area
        // Wintertodt area
        // Fremmennik Slayer dungeon
/*        if (AreaManager.inside(player.position, Boundary(1728, 1791, 5312, 5375))) // Ancient cavern
            return true*/
        if (AreaManager.inside(player.position, Boundary(3281, 3304, 3158, 3178))) // alkarid palace
            return true
        if (AreaManager.inside(player.position, Boundary(2880, 2945, 5185, 5230))) // nex
            return true
        else if (AreaManager.inside(player.position, Boundary(2950, 3071, 9800, 9855))) // dwarven mine
            return true
        else if (AreaManager.inside(player.position, Boundary(2994, 3071, 9698, 9799))) // dwarven mine
            return true
        else if (AreaManager.inside(player.position, Boundary(3008, 6016, 3071, 6079))) // zalcano
            return true
        else if (AreaManager.inside(player.position, Boundary(2432, 2559, 10112, 10175))) // waterbirth dungeon
            return true
        else if (AreaManager.inside(player.position, Boundary(2240, 2303, 9984, 10047))) // kraken cove
            return true
        else if (AreaManager.inside(player.position, Boundary(3200, 3263, 10304, 10367))) // scorpia
            return true
        else if (AreaManager.inside(player.position, Boundary(3520, 3583, 9964, 9727))) // barrows crypt
            return true
        else if (AreaManager.inside(player.position, Boundary(1337, 1382, 10233, 10290))) // alchemical hydra
            return true
        else if (AreaManager.inside(player.position, Boundary(1243, 1289, 10143, 10199))) // wyrm karuulm dungeon
            return true
        else if (AreaManager.inside(player.position, Boundary(2125, 2164, 4678, 4717))) // wyrm karuulm dungeon
            return true
        else if (AreaManager.inside(player.position, Boundary(3400, 3455, 3530, 3580))) // slayer tower
            return true
        else if (AreaManager.inside(player.position, Boundary(3400, 3450, 9926, 9981))) // slayer basement
            return true
        else if (AreaManager.inside(player.position, Boundary(2253, 2290, 4676, 4715))) // King black dragon
            return true
/*        else if (AreaManager.inside(player.position, Boundary(3326, 3392, 3202, 3266))) // Duel arena
            return true
        else if (AreaManager.inside(player.position, Boundary(3349, 3392, 3267, 3325))) // Duel arena
            return true*/
        else if (AreaManager.inside(player.position, Boundary(3642, 3683, 3204, 3234))) // Ver sinhaza
            return true

        return when (area) {
            is CerberusArea -> true
            is FightCaveArea -> true
            is AquaisNeigeArea -> true
            is GodwarsArea -> true
            is DuelFightArena -> true
            is GodChamberArea<*> -> true
            is KalphiteLair -> true
            is VorkathArea -> true
            is BankAreas -> true
            is LizardmanShamanArea -> true
            is RevenantsCave -> true
            is ZulrahShrine -> true
            is HouseInstance -> true
            is SlayerTowerArea -> true
            is AncientDungeonArea -> true
            is CatacombsOfKourendArea -> true
            is SmokeDungeonArea -> true
            is SmokeDevilDungeonArea -> true
            is WarriorsGuildArea -> true
            is BarrowsArea -> true
            is PestControlArea -> true
            is CastleWars -> true
            else -> false
        }
    }

    private fun getAreaMessage(player: Player, area: Area): String {
        if (AreaManager.inside(player.position, Boundary(1243, 1289, 10143, 10199))) {
           return "The former states that the wyrms electricity would render the cannon useless."
        } else if (AreaManager.inside(player.position, Boundary(1337, 1382, 10233, 10290))) {
            return "The former states that the cannon would destroy the chemical containers."
        }
        return when (area) {
            is SlayerTowerArea -> "Dark forces are preventing the dwarven construction from working."
            is CerberusArea -> "Your cannon would melt if you set it up here."
            is SmokeDevilDungeonArea -> "Your cannon would melt if you set it up here."
            is SmokeDungeonArea -> "Your cannon would melt if you set it up here."
            is AquaisNeigeArea -> "The intense cold threatens to crack the metal of your cannon. You probably shouldn't put it down here."
            is LizardmanShamanArea -> "The ground is too marshy here."
            is AncientDungeonArea -> "The ground is too damp to support a cannon."
            is CatacombsOfKourendArea -> "The influence of the dark altar prevents your cannon being set up here."
            is FightCaveArea -> "Your cannon would melt if you set it up here."
            is VorkathArea -> "The intense cold threatens to crack the metal of your cannon. You probably shouldn't put it down here."
            is GodwarsArea -> "The intense cold threatens to crack the metal of your cannon. You probably shouldn't put it down here."
            is ZulrahShrine -> "The ground is too damp to support a cannon."
            else -> "You're not allowed to place a cannon in this area."
        }
    }
}