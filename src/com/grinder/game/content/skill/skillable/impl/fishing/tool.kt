package com.grinder.game.content.skill.skillable.impl.fishing

import com.grinder.game.content.skill.GatherTool
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.hasItemInInventory
import com.grinder.game.model.Animation
import com.grinder.game.model.item.Item
import com.grinder.game.task.Task
import com.grinder.util.ItemID
import com.grinder.util.oldgrinder.EquipSlot

/**
 * DataEntries of FishingTools.
 *
 * @param tools An array of possible ResourceTool for tools.
 */
enum class FishingTool(val tools: Array<GatherTool>) {
    BARBARIAN_FISHING(arrayOf(GatherTool(-1, Animation(6703), "You start to lure the fish."))),
    SMALL_FISHING_NET(arrayOf(GatherTool(ItemID.SMALL_FISHING_NET, Animation(621), "You cast out your net..."))),
    BIG_FISHING_NET(arrayOf(GatherTool(ItemID.BIG_FISHING_NET, Animation(620), "You cast out your net..."))),
    FISHING_ROD(arrayOf(GatherTool(ItemID.FISHING_ROD, Animation(622), "You cast out your line..."))),
    FLY_FISHING_ROD(arrayOf(GatherTool(ItemID.FLY_FISHING_ROD, Animation(622), "You cast out your line..."))),
    OILY_FISHING_ROD(arrayOf(GatherTool(ItemID.OILY_FISHING_ROD, Animation(622), "You cast out your line..."))),
    BARBARIAN_ROD(arrayOf(
            GatherTool(11323, Animation(623), "You cast out your line..."),
            GatherTool(22842, Animation(623), "You cast out your line...")
    )),
    HARPOON(arrayOf(
            GatherTool(ItemID.HARPOON, Animation(618), "You start harpooning fish."),
            GatherTool(ItemID.DRAGON_HARPOON, Animation(7401), "You start harpooning fish.", 0.2),
            GatherTool(ItemID.INFERNAL_HARPOON, Animation(7402), "You start harpooning fish.", 0.2),
            GatherTool(23762, Animation(8336), "You start harpooning fish.", 0.35),
        GatherTool(ItemID.TRAILBLAZER_HARPOON, Animation(8784), "You start harpooning fish.", 0.35)
    )),
    LOBSTER_POT(arrayOf(GatherTool(ItemID.LOBSTER_POT, Animation(619), "You attempt to catch a lobster."))),
    KARAMBWAN_VESSEL(arrayOf(GatherTool(3159, Animation(1193), "You attempt to catch a karambwan.")));

    /**
     * findTool searches the player's inventory for the appropriate tool that can be used.
     *
     * @param player Player associated with fishing task.
     */
    fun findTool(player: Player): GatherTool? {
        for (tool in tools)
            if (player.hasItemInInventory(Item(tool.id), message = false)
                    || player.equipment.containsAnyAtSlot(EquipSlot.WEAPON, tool.id))
                return tool
        // barbarian fishing; fishing with your hands.
        if (this == HARPOON)
            return BARBARIAN_FISHING.tools[0]

        return null
    }

    /**
     * toolName normalizes the enum name for messages.
     */
    fun toolName(): String {
        return this.name.replace("_", " ").toLowerCase()
    }
}

/**
 * Default animation task for fishing tool.
 *
 * @param player Player performing the animation.
 * @param ResourceTool Information about the tool being used.
 */
class DefaultToolAnimTask(val player: Player?, val ResourceTool: GatherTool) : Task(7, player, true) {
    override fun execute() {
        player?.performAnimation(ResourceTool.startAnim)
    }
}

/**
 * Special animation task for barehanded fishing.
 *
 * @param player Player performing the animation.
 * @param ResourceTool Information about the tool being used.
 */
class BarehandToolAnimTask(val player: Player?, val ResourceTool: GatherTool) : Task(1, player, true) {
    var cycleCount = 0
    override fun execute() {
        when(cycleCount) {
            0-> {
                player?.performAnimation(ResourceTool?.startAnim)
                cycleCount++
            }
            3-> {
                player?.performAnimation(Animation(6704))
                cycleCount++
            }
            4-> {
                player?.performAnimation(Animation(6709))
                delay = 5
            }
            else -> {
                cycleCount++
            }
        }

    }
}