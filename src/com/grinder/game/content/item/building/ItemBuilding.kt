package com.grinder.game.content.item.building

import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.item.charging.Chargeables
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerManager
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerRewards
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerRewards.Rewards.*
import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.Animation
import com.grinder.game.model.ItemActions
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.item.AttributableItem
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.name
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID

/**
 * Building items
 *
 * TODO: resolve duplicate BLACK_SLAYER_HELM condition
 *
 * @author 2012
 * @author Stan van der Bend
 */
object ItemBuilding {

    const val CHISEL = 1755
    const val TINDERBOX = 590

    init {
        for (buildable in Buildable.values()) {
            for (first in buildable.components) {
                for (second in buildable.components) {
                    if (first != second) {
                        ItemActions.onItemOnItem(first to second) {
                            return@onItemOnItem startBuild(player, first, second, buildable)
                        }
                    }
                }
            }
        }
    }

    /**
     * Builds the item
     */
    private fun startBuild(player: Player, first: Int, second: Int, buildable: Buildable): Boolean {

        if (player.wildernessLevel > 0) {
            player.statement("You can't combine items while in the Wilderness.")
            return false
        }

        if (buildable.skillRequirement != null) {
            if (player.getLevel(buildable.skillRequirement.skill) < buildable
                            .skillRequirement.levelRequired) {
                player.message("You don't have the required level to do this.")
                player.message("You need a level of at least " + buildable.skillRequirement.levelRequired
                        + " " + buildable.skillRequirement.skill.name.toLowerCase() + " to do this.")
                return true
            }
        }
        if (buildable.isConfirmation) {
            DialogueBuilder(DialogueType.STATEMENT)
                    .setText("Are you use you want to combine these items?",
                            "@red@Once you do, you wont be able to undo this."
                    ).add(DialogueType.OPTION)
                    .firstOption("Yes, proceed.") {
                        build(it, first, second, buildable)
                    }
                    .addCancel("No, I've changed my mind.")
                    .start(player)
            return true
        }
        build(player, first, second, buildable)
        return true
    }

    private fun SlayerManager.unlockedAny(vararg rewards: SlayerRewards.Rewards): Boolean {
        return rewards.any { unlocked[it.ordinal] }
    }

    /**
     * Building items
     */
    private fun build(player: Player, first: Int, second: Int, build: Buildable) {

        if (!player.inventory.contains(first) || !player.inventory.contains(second))
            return

        if (build.skillRequirement != null) {
            if (player.getMaxLevel(build.skillRequirement.skill) < build.skillRequirement.levelRequired) {
                player.message("You don't have the required " + build.skillRequirement.skill.getName().toLowerCase()
                        + " to do this. You need a level of at least " + build.skillRequirement.levelRequired + "!", 1000)
                player.removeInterfaces()
                return
            }
        }

        if (build == Buildable.SLED)
            player.performAnimation(Animation(1470))

        when (build) {
            Buildable.SLAYER_HELMET, Buildable.SLAYER_HELMET_1, Buildable.SLAYER_HELMET_2, Buildable.SLAYER_HELMET_3,
            Buildable.SLAYER_HELMET_4, Buildable.SLAYER_HELMET_5, Buildable.SLAYER_HELMET_6, Buildable.SLAYER_HELMET_7, Buildable.SLAYER_HELMET_8,
            Buildable.SLAYER_HELMET_9, Buildable.SLAYER_HELMET_10, Buildable.SLAYER_HELMET_IMBUED, Buildable.SLAYER_HELMET_IMBUED_1,
            Buildable.SLAYER_HELMET_IMBUED_2, Buildable.SLAYER_HELMET_IMBUED_3, Buildable.SLAYER_HELMET_IMBUED_4,
            Buildable.SLAYER_HELMET_IMBUED_5, Buildable.SLAYER_HELMET_IMBUED_6, Buildable.SLAYER_HELMET_IMBUED_7,
            Buildable.SLAYER_HELMET_IMBUED_8, Buildable.SLAYER_HELMET_IMBUED_9, Buildable.SLAYER_HELMET_IMBUED_10 -> {
                if (!player.slayer.unlockedAny(MALEVOLENT_MASQUERADE)) {
                    DialogueBuilder(DialogueType.STATEMENT)
                            .setText("You must learn the ability @dre@'Malevolent Masquerade'</col>",
                                    "from a Slayer master to do this."
                            ).start(player)
                    return
                }
            }
            Buildable.PURPLE_SLAYER_HELM, Buildable.PURPLE_SLAYER_HELM_I -> {
                if (!player.slayer.unlockedAny(MALEVOLENT_MASQUERADE, KING_BLACK_BONNET, UNHOLY_HELMET, KALPHITE_KAT)) {
                    DialogueBuilder(DialogueType.STATEMENT)
                            .setText("You must unlock all Slayer helm abilities",
                                    "from a Slayer master to do this."
                            ).start(player)
                    return
                }
            }
            Buildable.BLACK_SLAYER_HELM, Buildable.BLACK_SLAYER_HELM_I -> {
                if (!player.slayer.unlockedAny(KING_BLACK_BONNET)) {
                    DialogueBuilder(DialogueType.STATEMENT)
                            .setText("You must learn the ability @dre@'King Black Bonnet'</col>",
                                    "from a Slayer master to do this."
                            ).start(player)
                    return
                }
            }
            Buildable.GREEN_SLAYER_HELM, Buildable.GREEN_SLAYER_HELM_I -> {
                if (!player.slayer.unlockedAny(KALPHITE_KAT)) {
                    DialogueBuilder(DialogueType.STATEMENT)
                            .setText("You must learn the ability @dre@'Kalphite Kat'</col>",
                                    "from a Slayer master to do this."
                            ).start(player)
                    return
                }
            }
            Buildable.RED_SLAYER_HELM, Buildable.RED_SLAYER_HELM_I -> {
                if (!player.slayer.unlockedAny(UNHOLY_HELMET)) {
                    DialogueBuilder(DialogueType.STATEMENT)
                            .setText("You must learn the ability @dre@'Unholy helmet'</col>",
                                    "from a Slayer master to do this."
                            ).start(player)
                    return
                }
            }
            Buildable.TURQOISE_SLAYER_HELM, Buildable.TURQOISE_SLAYER_HELM_I -> {
                if (!player.slayer.unlockedAny(UNDEAD_HEAD)) {
                    DialogueBuilder(DialogueType.STATEMENT)
                        .setText("You must learn the ability @dre@'Undead Helmet'</col>",
                            "from a Slayer master to do this."
                        ).start(player)
                    return
                }
            }
            Buildable.HYDRA_SLAYER_HELM, Buildable.HYDRA_SLAYER_HELM_I -> {
                if (!player.slayer.unlockedAny(USE_MORE_HEAD)) {
                    DialogueBuilder(DialogueType.STATEMENT)
                        .setText("You must learn the ability @dre@'Use More Head'</col>",
                            "from a Slayer master to do this."
                        ).start(player)
                    return
                }
            }
            Buildable.TWISTED_SLAYER_HELMET, Buildable.TWISTED_SLAYER_HELMET_I -> {
                if (!player.slayer.unlockedAny(TWISTED_VISION)) {
                    DialogueBuilder(DialogueType.STATEMENT)
                        .setText("You must learn the ability @dre@'Twisted Vision'</col>",
                            "from a Slayer master to do this."
                        ).start(player)
                    return
                }
            }
/*            Buildable.TZTOK_SLAYER_HELMET, Buildable.TZTOK_SLAYER_HELMET_I -> {
                if (!player.slayer.unlockedAny(TZTOK_JAD)) {
                    DialogueBuilder(DialogueType.STATEMENT)
                        .setText("You must learn the ability @dre@'TzTok-Jad'</col>",
                            "from a Slayer master to do this."
                        ).start(player)
                    return
                }
            }
            Buildable.VAMPYRIC_SLAYER_HELMET, Buildable.VAMPYRIC_SLAYER_HELMET_I -> {
                if (!player.slayer.unlockedAny(VERZIK_VITUR)) {
                    DialogueBuilder(DialogueType.STATEMENT)
                        .setText("You must learn the ability @dre@'Verzik Vitur'</col>",
                            "from a Slayer master to do this."
                        ).start(player)
                    return
                }
            }
            Buildable.TZKAL_SLAYER_HELMET, Buildable.TZKAL_SLAYER_HELMET_I -> {
                if (!player.slayer.unlockedAny(TZKAL_ZUK)) {
                    DialogueBuilder(DialogueType.STATEMENT)
                        .setText("You must learn the ability @dre@'TzKal-Zuk'</col>",
                            "from a Slayer master to do this."
                        ).start(player)
                    return
                }
            }*/
            Buildable.BANDOS_WHIP, Buildable.ZAMORAK_WHIP, Buildable.SARADOMIN_WHIP, Buildable.GUTHIX_WHIP, -> {
                if (player.inventory.getAmount(ItemID.BANDOSIAN_COMPONENTS) < 2) {
                    DialogueBuilder(DialogueType.STATEMENT)
                        .setText("You need to have two Bandosian components to be able to",
                            "repair this whip."
                        ).start(player)
                    return
                }
            }
            Buildable.TORVA_WHIP, -> {
                if (player.inventory.getAmount(ItemID.BANDOSIAN_COMPONENTS) < 2) {
                    DialogueBuilder(DialogueType.STATEMENT)
                        .setText("You need to have 2 Bandosian components",
                            "and a Dragon whip to repair this whip."
                        ).start(player)
                    return
                }
            }
            else -> {
            }
        }

        val firstFound = build.components.first { it == first }
        val secondFound = build.components.first { it == second }

        if (firstFound == secondFound)
            return

        if (firstFound == 0 || secondFound == 0)
            return

        if (canCombine(first, second, firstFound, secondFound)) {

            if (build.components.size == 2) {

                val component1ItemId = build.components[0]
                val component1Item = player.inventory.getById(component1ItemId)
                val component1Name = component1Item.name()

                val component2ItemId = build.components[1]
                val component2Item = player.inventory.getById(component2ItemId)
                val component2Name = component2Item.name()

                if (component1ItemId != CHISEL && component1ItemId != TINDERBOX)
                    player.removeInventoryItem(component1Item, -1)

                if (component2ItemId != CHISEL && component2ItemId != TINDERBOX)
                    player.removeInventoryItem(component2Item, -1)

                val resultItems = createResultItems(build)

                transferCharges(resultItems, component1Item, component2Item)

                for (resultItem in resultItems)
                    player.addInventoryItem(resultItem, -1)

                player.inventory.refreshItems()

                val displayedResultItem = resultItems.firstOrNull() ?: return
                val displayedResultItemId = displayedResultItem.id
                val displayedResultName = displayedResultItem.name()

                if (component1ItemId == ItemID.CLEANING_CLOTH || component2ItemId == ItemID.CLEANING_CLOTH) {
                    player.itemStatement(displayedResultItemId, 200,
                            "You have used the @dre@$component1Name</col> with @dre@$component2Name</col>",
                            "to get a cleaned @dre@$displayedResultName</col>.")
                } else if (build == Buildable.GUARDIAN_BOOTS) {
                    player.message("The second you hold your tourmaline core by the Bandos boots," +
                            " the core is absorbed rapidly. " +
                            "Perhaps there is some link between the metal in Bandos and tourmaline...")
                } else if (build == Buildable.KODAI_WAND) {
                    player.message("Power shoots down the length of the wand, " +
                            "empowering it with the darkness of Xeric's Kodai wizards.")
                } else if (build == Buildable.SLED) {
                    player.itemStatement(displayedResultItemId, 200,
                            "You carefully wax your @dre@$component1Name</col> and now it's ready to use.",
                            "to get a cleaned @dre@$displayedResultName</col>.")
                } else {
                    player.itemStatement(displayedResultItemId, 200,
                            "You have combined the @dre@$component1Name</col> with @dre@$component2Name</col>",
                            "to turn it to @dre@${displayedResultItem.name(true)}</col>.")
                }

                player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
                player.progressAchievement(AchievementType.COMBINATION)

            } else {

                val missingComponents = ArrayList<Int>()
                for (componentItemId in build.components) {
                    if (!player.inventory.contains(componentItemId)) {
                        missingComponents.add(componentItemId)
                    }
                }


                if (missingComponents.isNotEmpty() && build.components.contains(ItemID.DRAGON_WHIP)) {
                    player.itemStatement(missingComponents.first(), 200, "You need 2 Bandosian components, and", "a Dragon whip to repair this whip!")
                    return
                }

                if (missingComponents.isNotEmpty()) {
                    player.itemStatement(missingComponents.first(), 200, "You seem to be missing a component!")
                    return
                }
                // Zaryte crossbow
                if (build.components[0] == ItemID.NIHIL_SHARD || build.components[1] == ItemID.NIHIL_SHARD || build.components[2] == ItemID.NIHIL_SHARD) {
                    if (player.inventory.getAmount(Item(ItemID.NIHIL_SHARD)) < 250) {
                        player.itemStatement(ItemID.NIHIL_SHARD, 200, "You need at least 250 Nihil shards to combine these items!")
                        return;
                    }
                }
                val shardAmount = player.inventory.getAmount(ItemID.NIHIL_SHARD)

                val required = build.components
                    .map { player.inventory.getById(it) }
                    .filterNotNull()
                    .toTypedArray()


                for (component in required)
                    player.inventory.delete(component, false)

                val resultItems = createResultItems(build)

                transferCharges(resultItems, *required)

                for (resultItem in resultItems)
                    player.addInventoryItem(resultItem, -1)

                player.inventory.refreshItems()

                val firstResultItem = resultItems.first() ?: return
                val firstResultItemId = firstResultItem.id
                val firstResultName = firstResultItem.name(true)


                // Zaryte crossbow
                if (firstResultItemId == ItemID.ZARYTE_CROSSBOW) {
                    player.inventory.add(ItemID.NIHIL_SHARD, shardAmount - 250)
                }

                player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
                player.itemStatement(firstResultItemId, 200,
                        "You have put all the pieces together to make ",
                        "@dre@$firstResultName</col>.")
            }

            if (build.skillRequirement != null)
                player.addExperience(build.skillRequirement.skill, build.skillRequirement.experienceReceived.toInt())
        }
    }

    private fun createResultItems(build: Buildable) = Array(build.result.size) {
        val resultItemId = build.result[it]
        if (Chargeables.isChargeable(resultItemId))
            AttributableItem(resultItemId, 1)
        else
            Item(resultItemId)
    }

    private fun transferCharges(result: Array<Item>, vararg required: Item) {
        for (resultItem in result) {
            if (resultItem is AttributableItem) {
                for (requiredItem in required) {
                    if (requiredItem is AttributableItem) {
                        if (Chargeables.isSameChargeable(resultItem, requiredItem)) {
                            for ((attribute, value) in requiredItem.attributes)
                                resultItem.attributes[attribute] = value
                        }
                    }
                }
            }
        }
    }

    /**
     * Combing items
     */
    private fun canCombine(first: Int, second: Int, item1: Int, item2: Int): Boolean {
        return first == item1 && second == item2
                || second == item1 && first == item2
    }
}