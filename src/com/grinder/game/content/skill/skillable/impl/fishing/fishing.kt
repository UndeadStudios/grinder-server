@file:JvmName("Fishing")

package com.grinder.game.content.skill.skillable.impl.fishing

import com.grinder.game.content.item.degrading.DegradingType
import com.grinder.game.content.miscellaneous.PetHandler
import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.content.skill.skillable.DefaultSkillable
import com.grinder.game.content.skill.task.SkillMasterType
import com.grinder.game.content.skill.task.SkillTaskManager
import com.grinder.game.content.task_new.DailyTask
import com.grinder.game.content.task_new.PlayerTaskManager
import com.grinder.game.content.task_new.WeeklyTask
import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces.assign
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.grounditem.ItemOnGroundManager
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.Skill
import com.grinder.game.model.attribute.AttributeManager
import com.grinder.game.model.interfaces.dialogue.DialogueManager
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.sound.Sound
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.net.packet.impl.EquipPacketListener
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.Priority
import com.grinder.util.TaskFunctions
import com.grinder.util.oldgrinder.EquipSlot
import kotlin.random.Random

/**
 * This is the base catch multiplayer to be used for determining catch rates.
 */
private var CATCH_MULTIPLIER = 1.2

/**
 * The projectile which will be fired towards the player from the
 * fishing spot.
 */
private const val BIG_FISH_PROJECTILE = 94

/**
 * The defence animation the player will perform when attacked.
 */
private val DEFENCE_ANIM = Animation(404)

/*
* Messages that are sent to the player while training Fishing skill
 */
private val FISHING_MESSAGES = arrayOf(
    arrayOf("@whi@You can train in the Fishing guild after reaching level 68 Fishing!"),
    arrayOf("@whi@Every equipped Angler gear piece increases your experience gain in Fishing skill!"),
    arrayOf("@whi@You can take a Fishing skill task from your master for bonus rewards."),
    arrayOf("@whi@Check out Rada's blessing features to get a chance of double fish."),
    arrayOf("@whi@Fishing with Spirit flakes gives you a chance of getting double fish."),
    arrayOf("@whi@There's a chance to get Big shark when fishing Sharks in the Fishing guild."),
    arrayOf("@whi@You can fish Lava eels in the Lava pool areas such as in Taverly Dungeon."),
    arrayOf("@whi@Fishing in the Wilderness Resource Area provides 20% bonus experience gain!"),
    arrayOf("@whi@Fishing with the skillcape equipped will give you 20% bonus experience gain!"),
    arrayOf("@whi@Players with Members rank can train in unique skilling islands with a lot of spots.")
)

var currentMessage: String? = null

fun sendSkillRandomMessages(player: Player) {
    currentMessage = FISHING_MESSAGES[Misc.getRandomInclusive(FISHING_MESSAGES.size - 1)][0]
    player.packetSender.sendMessage("<img=779> $currentMessage")
}


/**
 * Determines if the player is within the fishing guild.
 */
fun catchBonuses(player: Player): Double {
    var bonus = CATCH_MULTIPLIER
    // fishing guild location 5% bonus fish chance
    if (player.x in 2595..2611 && player.y in 3406..3425)
        bonus += 0.05
    // 5% boost if fishing pet is out
    if (player.currentPet?.id == 6715)
        bonus += 0.05
    // fishing cape
    if (player.equipment.contains(ItemID.FISHING_CAPE)
            || player.equipment.contains(ItemID.FISHING_CAPE_T_))
        bonus += 0.05
    // angler fishing outfit bonus 2.5% each
    if (player.equipment.contains(ItemID.ANGLER_HAT)) {
        bonus += 0.025
    }
    if (player.equipment.contains(ItemID.ANGLER_TOP)) {
        bonus += 0.025
    }
    if (player.equipment.contains(ItemID.ANGLER_WADERS)) {
        bonus += 0.025
    }
    if (player.equipment.contains(ItemID.ANGLER_BOOTS)) {
        bonus += 0.025
    }
    return bonus
}

/**
 * Fills the Karambwan vessel with Karambwanji
 * @param player Player associated with action.
 * @param slot1 Slot of the first item Used.
 * @param slot2 The second slot of the item used.
 */
fun fillKarambwanVessel(player: Player?, slot1: Int, slot2: Int): Boolean {
    val item1 = player?.inventory?.get(slot1)!!
    val item2 = player?.inventory?.get(slot2)!!
    if (item1.id != 3157 && item2.id != 3157)
        return false
    val itemUsed = if (item1.id == 3157) item2.id else item1.id
    if (itemUsed != ItemID.RAW_KARAMBWANJI)
        return false
    player.removeInventoryItem(Item(itemUsed))
    player.inventory.replaceFirst(3157, 3159)
    player.sendMessage("You place the Karambwanji in the vessel.")
    return true
}

/**
 * ItemOnItem action better a fish
 *
 * @param player The player performing the action.
 * @param slot1 The item slot being used.
 * @param slot2 The item slot being used on.
 */
fun gutFish(player: Player?, slot1: Int, slot2: Int): Boolean {
    val item1 = player?.inventory?.get(slot1)!!
    val item2 = player.inventory?.get(slot2)!!
    if (item1.id != ItemID.KNIFE && item2.id != ItemID.KNIFE)
        return false
    when (val itemUsed = if (item1.id == ItemID.KNIFE) item2.id else item1.id) {
        ItemID.LEAPING_TROUT, ItemID.LEAPING_SALMON -> {
            if (player.inventory.isFull) {
                player.sendMessage("You need at least 2 free inventory spaces.")
                return true
            }
            player.performAnimation(Animation(1248))
            player.removeInventoryItem(Item(itemUsed))
            if (Random.nextInt(4) == 0) {
                player.sendMessage("You fail to gain anything useful and reduce the fish to fragments, not even usable as bait.")
                return true
            }
            var msg = "You cut open the fish and extract some fish cuts and roe."
            if (Random.nextInt(3) == 0) {
                msg = "You cut open the fish and extract some roe, but the rest of the fish is reduced to useless fragments, which you discard."
            } else {
                player.addInventoryItem(Item(ItemID.FISH_OFFCUTS))
            }
            player.addInventoryItem(Item(ItemID.ROE))
            player.addExperience(Skill.COOKING, 10)
            player.sendMessage(msg)
        }
        ItemID.LEAPING_STURGEON -> {
            if (player.inventory.isFull) {
                player.sendMessage("You don't have enough space in your pack to attempt cutting open the fish.")
                return true
            }
            player.performAnimation(Animation(1248))
            player.removeInventoryItem(Item(itemUsed))
            if (Random.nextInt(4) == 0) {
                player.sendMessage("You fail to gain anything useful and reduce the fish to fragments, not even usable as bait.")
                return true
            }
            var msg = "You cut open the fish and extract some fish cuts and caviar."
            if (Random.nextInt(3) == 0) {
                msg = "You cut open the fish and extract some roe, but the rest of the fish is reduced to useless fragments, which you discard."
            } else {
                player.addInventoryItem(Item(ItemID.FISH_OFFCUTS))
            }
            player.addInventoryItem(Item(ItemID.CAVIAR))
            player.addExperience(Skill.COOKING, 15)
            player.sendMessage(msg)
        }
        else -> return false
    }
    return true
}

/**
 * Fishing action that kicks off when a 'fishing spot' is clicked.
 *
 * @param fishingSpot The fishingOption that is clicked. ex: 'harpoon'
 * @param npc NPC that has been clicked.
 */
class FishingAction(val fishingSpot: FishOption, val npc: NPC) : DefaultSkillable() {

    // Why would I create another 'task' inside a 'task', Should be part of 'onCycle'
    override fun startAnimationLoop(player: Player?) {
        val tool = fishingSpot.tool.findTool(player!!) ?: return
        val animLoop = if (tool.id != -1) DefaultToolAnimTask(player, tool) else BarehandToolAnimTask(player, tool)
        TaskManager.submit(animLoop)
        tasks.add(animLoop)
        player.positionToFace = npc.position
        player.sendMessage(tool.useMessage)
    }

    override fun startGraphicsLoop(player: Player?) {
    }

    // Why would I create another 'task' inside a 'task', Should be part of 'onCycle'
    override fun startSoundLoop(player: Player?) {} // sound is in animation

    override fun allowFullInventory(): Boolean {
        return false
    }

    override fun onCycle(player: Player?) {
        val tool = fishingSpot.tool.findTool(player!!)
        // Handle random event..
        if (tool != null && tool.id != -1 && Misc.getRandomInclusive(1000) == 1) {
            TaskManager.submit(BigFishRandom(player, npc, tool.id))
            cancel(player)
        }
    }

    override fun finishedCycle(player: Player?) {
        checkNotNull(player)
        val tool = fishingSpot.tool.findTool(player)
        val addReq = (fishingSpot.tool == FishingTool.BARBARIAN_ROD || tool?.id == -1)
        val pool = if (addReq) fishingSpot.rewardsByReq(player) else fishingSpot.rewards(player)
        assert(pool.isNotEmpty()) // This should not be false, as it occurs the same cycle as hasRequirements.
        checkNotNull(tool)
        val reward = pool.reversed().filter {
            val modifier = tool.modifier + catchBonuses(player)
            it.fish.catch(player, modifier)
        }
        if (reward.isEmpty())
            return
        val roll = reward[Random.nextInt(reward.size)]
        val itm = Item(roll.fish.itemID, roll.amt(player))
        val bait = roll.fish.firstBait(player)
        if (bait > 0)
            player.removeInventoryItem(Item(bait))

        // Golden tench
        if (Misc.random(200) == Misc.random(200)) {
            player.sendMessage("@red@The cormorant has brought you a very strange tench.");
            ItemContainerUtil.addOrDrop(player.inventory, player, Item(22840))
        }

         // Infernal harpoon
        val infernalHarpoon =
            (player.equipment.contains(21031) || player.inventory.contains(21031)) && Misc.getRandomInclusive(4) == 1

        if (infernalHarpoon && fishingSpot.tool == FishingTool.HARPOON) {
            when (roll.fish.itemID) {
                /*ItemID.RAW_SHRIMPS -> {
                    player.inventory.add(ItemID.SHRIMPS, 1)
                    player.skillManager.addExperience(Skill.COOKING, 30)
                }
                ItemID.RAW_ANCHOVIES -> {
                    player.inventory.add(ItemID.ANCHOVIES, 1)
                    player.skillManager.addExperience(Skill.COOKING, 30)
                }
                ItemID.RAW_SARDINE -> {
                    player.inventory.add(ItemID.SARDINE, 1)
                    player.skillManager.addExperience(Skill.COOKING, 40)
                }
                ItemID.RAW_HERRING -> {
                    player.inventory.add(ItemID.HERRING, 1)
                    player.skillManager.addExperience(Skill.COOKING, 50)
                }
                ItemID.RAW_MACKEREL -> {
                    player.inventory.add(ItemID.MACKEREL, 1)
                    player.skillManager.addExperience(Skill.COOKING, 60)
                }
                ItemID.RAW_TROUT -> {
                    player.inventory.add(ItemID.TROUT, 1)
                    player.skillManager.addExperience(Skill.COOKING, 70)
                }
                ItemID.LEAN_SNAIL -> {
                    player.inventory.add(ItemID.LEAN_SNAIL_MEAT, 1)
                    player.skillManager.addExperience(Skill.COOKING, 80)
                }
                ItemID.RAW_COD -> {
                    player.inventory.add(ItemID.COD, 1)
                    player.skillManager.addExperience(Skill.COOKING, 75)
                }
                ItemID.RAW_PIKE -> {
                    player.inventory.add(ItemID.PIKE, 1)
                    player.skillManager.addExperience(Skill.COOKING, 80)
                }
                ItemID.CRAB_MEAT -> {
                    player.inventory.add(ItemID.COOKED_CRAB_MEAT, 1)
                    player.skillManager.addExperience(Skill.COOKING, 100)
                }
                ItemID.FAT_SNAIL -> {
                    player.inventory.add(ItemID.FAT_SNAIL_MEAT, 1)
                    player.skillManager.addExperience(Skill.COOKING, 95)
                }
                ItemID.RAW_SALMON -> {
                    player.inventory.add(ItemID.SALMON, 1)
                    player.skillManager.addExperience(Skill.COOKING, 90)
                }*/
                ItemID.RAW_TUNA -> {
                    ItemContainerUtil.addOrDrop(player.inventory, player, Item(ItemID.TUNA, 1))
                    player.skillManager.addExperience(Skill.COOKING, 50)
                }
                ItemID.RAW_SWORDFISH -> {
                    ItemContainerUtil.addOrDrop(player.inventory, player, Item(ItemID.SWORDFISH, 1))
                    player.skillManager.addExperience(Skill.COOKING, 70)
                }
                ItemID.RAW_SHARK -> {
                    ItemContainerUtil.addOrDrop(player.inventory, player, Item(ItemID.SHARK, 1))
                    player.skillManager.addExperience(Skill.COOKING, 105)
                }
            }
            if (player.equipment.contains(21031)) {
                player.itemDegradationManager.degrade(DegradingType.SKILLING, -1)
            } else {
                player.itemDegradationManager.degradeInventoryItems(DegradingType.SKILLING, -1, 21031)
            }

            // Send sound
            player.playSound(Sound(2577))

            // Send graphic
            player.performGraphic(Graphic(86))
        } else {
            // Add item if its not Infernal harpoon cooked
            if (itm.id == ItemID.SWORDFISH && Misc.random(500) == 1) {
                ItemContainerUtil.addOrDrop(player.inventory, player, Item(ItemID.BIG_SWORDFISH))
                player.sendMessage("You catch an enormous swordfish!")
            } else if (itm.id == ItemID.BASS && Misc.random(500) == 1) {
                ItemContainerUtil.addOrDrop(player.inventory, player, Item(ItemID.BIG_BASS))
                player.sendMessage("You catch an enormous swordfish!")
            } else {
                ItemContainerUtil.addOrDrop(player.inventory, player, itm)
            }
        }

        // Add exp..
        player.addExperience(Skill.FISHING, roll.fish.fishReq.exp)

        // Random events for anti botting
        player.tryRandomEventTrigger(1.3f);


        // Barbarian fishing
        if (addReq && roll.fish.addReq != null) {
            for (req in roll.fish.addReq) {
                player.addExperience(req.skill, req.exp)
            }
            // barbarian has special animations for when you succesfully catch them.
            if (roll.fish == Fish.TUNA)
                player.performAnimation(Animation(6710, 0, 3, Priority.HIGH))
            else if (roll.fish == Fish.SWORDFISH)
                player.performAnimation(Animation(6707, 0, 3, Priority.HIGH))
            else if (roll.fish == Fish.SHARK)
                player.performAnimation(Animation(6705, 0, 3, Priority.HIGH))
        }

        // Send message
        player.sendMessage("You manage to catch a ${itm.definition.name}.")

        // Roll pet
        PetHandler.onSkill(player, Skill.FISHING)

        // Add points
        player.points.increase(AttributeManager.Points.FISHES_CAUGHT, 1) // Increase points
        // Process skilling task
        SkillTaskManager.perform(player, itm.id, 1, SkillMasterType.FISHING);

        handleTasks(player, itm);
        // Send random skill messages
        if (Misc.getRandomInclusive(8) == Misc.getRandomInclusive(8) && player.skillManager.getMaxLevel(Skill.FISHING) < SkillUtil.maximumAchievableLevel()) {
            TaskFunctions.delayBy(3) {
                sendSkillRandomMessages(player)
            }
        }

    }

    // This is bulky and not required, not sure why it's here. 'hasRequirements' should be overrided
    override fun loopRequirements(): Boolean {
        return true
    }

    fun handleTasks(player: Player, it: Item) {
        PlayerTaskManager.progressTask(player, DailyTask.FISHES_CAUGHT)
        PlayerTaskManager.progressTask(player, WeeklyTask.FISHES_CAUGHT)
        if(it.id == ItemID.RAW_LOBSTER) {
            PlayerTaskManager.progressTask(player, DailyTask.FISH_RAW_LOBSTER)
        }
        if(it.id == ItemID.RAW_SHARK) {
            PlayerTaskManager.progressTask(player, DailyTask.FISH_RAW_SHARK)
            PlayerTaskManager.progressTask(player, WeeklyTask.FISH_RAW_SHARK)
        }
        if(it.id == ItemID.RAW_MONKFISH) {
            PlayerTaskManager.progressTask(player, WeeklyTask.RAW_MONKFISH)
        }

    }

    override fun hasRequirements(player: Player?): Boolean {
        checkNotNull(player)
        val tool = fishingSpot.tool.findTool(player)
        player.positionToFace = npc.position
        if (tool == null) {
            DialogueManager.sendStatement(player, "You need a ${fishingSpot.tool.toolName()} to catch these fish.")
            return false
        }
        // build reward pool from possible fish
        val pool = fishingSpot.rewards(player)
        if (pool.isEmpty()) {
            DialogueManager.sendStatement(player, "You need to be at least level ${fishingSpot.lowestLevel()} Fishing to catch these fish.")
            return false
        }
        // determine if we have the required bait.
        if (fishingSpot.requiresBait()) {
            val fish = pool.first().fish
            val reqBait = fish.firstBait(player)
            if (reqBait == 0) {
                val bait = fish.baits?.first()!!
                DialogueManager.sendStatement(player, "You do not have any ${ItemDefinition.forId(bait).name} left.")
                return false
            }
        }
        // check additional skill requirements
        if ((fishingSpot.tool == FishingTool.BARBARIAN_ROD || tool.id == -1)) {
            val newPool = fishingSpot.rewardsByReq(player)
            if (newPool.isNullOrEmpty()) {
                val fish = pool.first().fish
                var statement = "You need at least" // TODO: Find out the proper messages
                fish.addReq?.forEachIndexed() { i, req ->
                    statement += " ${req.lv} ${req.skill.getName()}" + if (i != fish.addReq.size - 1) "," else "."
                }
                DialogueManager.sendStatement(player, statement)
                return false
            }
        }
        return super.hasRequirements(player)
    }

    override fun cyclesRequired(player: Player?): Int {
        // There are ways to do 2/3 tick fishing but requires tick manipulation. Default is 5ticks
        return 5
    }

}

/**
 * Represents a random event which attacks a player's tool, forcing it to
 * drop onto the ground.
 *
 * This is a custom version of the OSRS "Big fish random event", which was
 * deleted in a update. The NPC "big fish" was deleted along with it, so we
 * simply shoot the projectile from the fish spot.
 *
 * @author Professor Oak
 * @author Lare96
 * @author Kevlon
 *
 * @param pl The player being attacked by a fish.
 * @param npc The fishing spot's position. The attacking npc.
 * @param tool The tool the player is using when being attacked by a fish
 */
class BigFishRandom(val player: Player, val npc: NPC, val tool: Int) : Task(1, player, true) {

    /**
     * This {@link Task}'s current cycle.
     */
    var cycle = 0

    override fun execute() {
        when (cycle) {
            0 ->
                Projectile(npc, player, BIG_FISH_PROJECTILE, 40, 70, 31, 33, 0).sendProjectile()
            2 -> {
                player.performAnimation(DEFENCE_ANIM)
                player.packetSender.sendSound(1032)
            }
            3 ->
                if (player.inventory.contains(tool)) {
                    player.inventory.delete(tool, 1)
                } else if (player.equipment.containsAtSlot(EquipSlot.WEAPON, tool)) {
                    player.equipment.delete(Item(tool))
                }
            4 -> {
                player.packetSender.sendSound(1035)
                ItemOnGroundManager.registerNonGlobal(player, Item(tool))
                DialogueManager.sendStatement(player, "A big fish attacked and you were forced to drop your "
                        + ItemDefinition.forId(tool).name.toLowerCase() + ".")
                // Update equipment (incase of dragon or infernal harpoon)
                player.equipment.refreshItems()
                EquipPacketListener.resetWeapon(player)
                EquipmentBonuses.update(player)
                assign(player)
                player.updateAppearance()
                stop()
            }
        }
        cycle++
    }

}