package com.grinder.game.content.item

import com.grinder.game.content.quest.QuestManager
import com.grinder.game.entity.agent.player.notInDangerOrAfkOrBusyOrInteracting
import com.grinder.game.model.Skill
import com.grinder.game.model.interfaces.dialogue.promptRedeemDialogue
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.game.model.onFirstInventoryAction
import com.grinder.util.ItemID

object QuestScrolls {

    init {
        onFirstInventoryAction(16073) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                if (QuestManager.hasCompletedQuest(player, "Clock Tower")) {
                    player.sendMessage("You already have this quest completed.")
                    return@onFirstInventoryAction
                }
                player.promptRedeemDialogue(Item(16073, 1)) {
                    QuestManager.complete(
                        player,
                        QuestManager.QUESTS["Clock Tower"],
                        arrayOf<String>("6,000,000 coins."),
                        995
                    )
                    player.inventory.add(Item(995, 6000000))

                    QuestManager.QUESTS["Clock Tower"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Clock Tower"]?.name,
                            it1.finalStage
                        )
                    }
                    player.quest.tracker.update()
                }
            }
        }

        onFirstInventoryAction(16074) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                if (QuestManager.hasCompletedQuest(player, "Cook's Assistant")) {
                    player.sendMessage("You already have this quest completed.")
                    return@onFirstInventoryAction
                }
                player.promptRedeemDialogue(Item(16074, 1)) {
                    QuestManager.complete(
                        player,
                        QuestManager.QUESTS["Cook's Assistant"],
                        arrayOf<String>("15,000 Cooking XP", "Access to Cook's range!"),
                        1891
                    )
                    player.skillManager.addFixedDelayedExperience(Skill.COOKING, 15000)

                    QuestManager.QUESTS["Cook's Assistant"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Cook's Assistant"]?.name,
                            it1.finalStage
                        )
                    }
                    player.quest.tracker.update()
                }
            }
        }

        onFirstInventoryAction(16075) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                if (QuestManager.hasCompletedQuest(player, "Doric's Quest")) {
                    player.sendMessage("You already have this quest completed.")
                    return@onFirstInventoryAction
                }
                player.promptRedeemDialogue(Item(16075, 1)) {
                    QuestManager.complete(
                        player,
                        QuestManager.QUESTS["Doric's Quest"],
                        arrayOf<String>("5,000 Mining XP", "Access to Doric's anvil!"),
                        1269
                    )
                    player.skillManager.addFixedDelayedExperience(Skill.MINING, 5000)

                    QuestManager.QUESTS["Doric's Quest"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Doric's Quest"]?.name,
                            it1.finalStage
                        )
                    }
                    player.quest.tracker.update()
                }
            }
        }

        onFirstInventoryAction(16076) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                if (QuestManager.hasCompletedQuest(player, "Imp Catcher")) {
                    player.sendMessage("You already have this quest completed.")
                    return@onFirstInventoryAction
                }
                player.promptRedeemDialogue(Item(16076, 1)) {
                    QuestManager.complete(
                        player,
                        QuestManager.QUESTS["Imp Catcher"],
                        arrayOf<String>("15,000 Magic XP", "Amulet of accuracy."),
                        1478
                    )
                    player.skillManager.addFixedDelayedExperience(Skill.MAGIC, 15000)
                    ItemContainerUtil.addOrDrop(player.inventory, player, Item(ItemID.AMULET_OF_ACCURACY, 1))
                    QuestManager.QUESTS["Imp Catcher"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Imp Catcher"]?.name,
                            it1.finalStage
                        )
                    }
                    player.quest.tracker.update()
                }
            }
        }

        onFirstInventoryAction(16077) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                if (QuestManager.hasCompletedQuest(player, "Sheep Shearer")) {
                    player.sendMessage("You already have this quest completed.")
                    return@onFirstInventoryAction
                }
                player.promptRedeemDialogue(Item(16077, 1)) {
                    QuestManager.complete(
                        player,
                        QuestManager.QUESTS["Sheep Shearer"],
                        arrayOf<String>("15,000 Crafting XP", "5,000,000 coins."),
                        1735
                    )
                    player.inventory.add(Item(995, 5000000))
                    player.skillManager.addFixedDelayedExperience(Skill.CRAFTING, 15000)

                    QuestManager.QUESTS["Sheep Shearer"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Sheep Shearer"]?.name,
                            it1.finalStage
                        )
                    }
                    player.quest.tracker.update()
                }
            }
        }

        onFirstInventoryAction(16078) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                if (QuestManager.hasCompletedQuest(player, "Witch's Potion")) {
                    player.sendMessage("You already have this quest completed.")
                    return@onFirstInventoryAction
                }
                player.promptRedeemDialogue(Item(16078, 1)) {
                    QuestManager.complete(
                        player,
                        QuestManager.WITCHS_POTION,
                        arrayOf("50,000 Magic XP", ""),
                        ItemID.EYE_OF_NEWT
                    )
                    player.skillManager.addFixedDelayedExperience(Skill.MAGIC, 50000)
                    QuestManager.QUESTS["Witch's Potion"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Witch's Potion"]?.name,
                            it1.finalStage
                        )
                    }
                    player.quest.tracker.update()
                }
            }
        }

        onFirstInventoryAction(16079) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                if (QuestManager.hasCompletedQuest(player, "Druidic Ritual")) {
                    player.sendMessage("You already have this quest completed.")
                    return@onFirstInventoryAction
                }
                player.promptRedeemDialogue(Item(16079, 1)) {
                    QuestManager.complete(
                        player,
                        QuestManager.DRUIDIC_RITUAL,
                        arrayOf<String>("1,000 Herblore XP", "Access to the Herblore skill."),
                        525
                    )
                    player.skillManager.addFixedDelayedExperience(Skill.HERBLORE, 1000)

                    QuestManager.QUESTS["Druidic Ritual"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Druidic Ritual"]?.name,
                            it1.finalStage
                        )
                    }
                    player.quest.tracker.update()
                }
            }
        }

        onFirstInventoryAction(16080) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                if (QuestManager.hasCompletedQuest(player, "Ernest The Chicken")) {
                    player.sendMessage("You already have this quest completed.")
                    return@onFirstInventoryAction
                }
                player.promptRedeemDialogue(Item(16080, 1)) {
                    QuestManager.complete(
                        player,
                        QuestManager.QUESTS["Ernest The Chicken"],
                        arrayOf("5,000,000 coins.", "Ability to equip Ava's equipment."),
                        314
                    )
                    player.inventory.add(Item(995, 5000000))

                    QuestManager.QUESTS["Ernest The Chicken"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Ernest The Chicken"]?.name,
                            it1.finalStage
                        )
                    }
                    player.quest.tracker.update()
                }
            }
        }

        onFirstInventoryAction(16081) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                if (QuestManager.hasCompletedQuest(player, "Gertrude's Cat")) {
                    player.sendMessage("You already have this quest completed.")
                    return@onFirstInventoryAction
                }
                player.promptRedeemDialogue(Item(16081, 1)) {
                    QuestManager.complete(
                        player,
                        QuestManager.QUESTS["Gertrude's Cat"],
                        arrayOf<String>("A pet Kitten."),
                        1555
                    )
                    player.inventory.add(1555, 1)

                    QuestManager.QUESTS["Gertrude's Cat"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Gertrude's Cat"]?.name,
                            it1.finalStage
                        )
                    }
                    player.quest.tracker.update()
                }
            }
        }

        onFirstInventoryAction(16082) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                if (QuestManager.hasCompletedQuest(player, "Monk's Friend")) {
                    player.sendMessage("You already have this quest completed.")
                    return@onFirstInventoryAction
                }
                player.promptRedeemDialogue(Item(16082, 1)) {
                    QuestManager.complete(
                        player,
                        QuestManager.QUESTS["Monk's Friend"],
                        arrayOf<String>("1,000 Law runes."),
                        563
                    )
                    player.inventory.add(Item(563, 1000))

                    QuestManager.QUESTS["Monk's Friend"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Monk's Friend"]?.name,
                            it1.finalStage
                        )
                    }
                    player.quest.tracker.update()
                }
            }
        }

        onFirstInventoryAction(16083) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                if (QuestManager.hasCompletedQuest(player, "Rune Mysteries")) {
                    player.sendMessage("You already have this quest completed.")
                    return@onFirstInventoryAction
                }
                player.promptRedeemDialogue(Item(16083, 1)) {
                    QuestManager.complete(
                        player, QuestManager.QUESTS["Rune Mysteries"], arrayOf(
                            "Access to the Runecrafting skill.", "Air talisman.",
                            "35,000 Runecrafting experience"
                        ), ItemID.AIR_TALISMAN
                    )
                    player.skillManager.addFixedDelayedExperience(Skill.RUNECRAFTING, 35000)

                    QuestManager.QUESTS["Rune Mysteries"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Rune Mysteries"]?.name,
                            it1.finalStage
                        )
                    }
                    player.quest.tracker.update()
                }
            }
        }

        onFirstInventoryAction(16084) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                if (QuestManager.hasCompletedQuest(player, "Goblin Diplomacy")) {
                    player.sendMessage("You already have this quest completed.")
                    return@onFirstInventoryAction
                }
                player.promptRedeemDialogue(Item(16084, 1)) {
                    QuestManager.complete(
                        player,
                        QuestManager.QUESTS["Goblin Diplomacy"],
                        arrayOf("150,000 Attack XP"),
                        2357
                    )
                    player.skillManager.addFixedDelayedExperience(Skill.ATTACK, 150000)

                    QuestManager.QUESTS["Goblin Diplomacy"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Goblin Diplomacy"]?.name,
                            it1.finalStage
                        )
                    }
                    player.quest.tracker.update()
                }
            }
        }

        onFirstInventoryAction(16085) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                if (QuestManager.hasCompletedQuest(player, "The Restless Ghost")) {
                    player.sendMessage("You already have this quest completed.")
                    return@onFirstInventoryAction
                }
                player.promptRedeemDialogue(Item(16085, 1)) {
                    QuestManager.complete(
                        player,
                        QuestManager.QUESTS["The Restless Ghost"],
                        arrayOf<String>("75,000 Prayer XP"),
                        553
                    )
                    player.skillManager.addFixedDelayedExperience(Skill.PRAYER, 75000)

                    QuestManager.QUESTS["The Restless Ghost"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["The Restless Ghost"]?.name,
                            it1.finalStage
                        )
                    }
                    player.quest.tracker.update()
                }
            }
        }

        onFirstInventoryAction(16086) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                if (QuestManager.hasCompletedQuest(player, "Dwarf Cannon")) {
                    player.sendMessage("You already have this quest completed.")
                    return@onFirstInventoryAction
                }
                player.promptRedeemDialogue(Item(16086, 1)) {
                    QuestManager.complete(
                        player,
                        QuestManager.QUESTS["Dwarf Cannon"],
                        arrayOf<String>("Ability to use the Dwarf multi-cannon.", "50 Cannon balls."),
                        4
                    )

                    QuestManager.QUESTS["Dwarf Cannon"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Dwarf Cannon"]?.name,
                            it1.finalStage
                        )
                    }
                    player.quest.tracker.update()
                }
            }
        }

        onFirstInventoryAction(16087) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                if (QuestManager.hasCompletedQuest(player, "Lost City")) {
                    player.sendMessage("You already have this quest completed.")
                    return@onFirstInventoryAction
                }
                player.promptRedeemDialogue(Item(16087, 1)) {
                    QuestManager.complete(
                        player,
                        QuestManager.QUESTS["Lost City"],
                        arrayOf("Access to Zanaris.", "Ability to equip the Dragon longsword."),
                        772
                    )

                    QuestManager.QUESTS["Lost City"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Lost City"]?.name,
                            it1.finalStage
                        )
                    }
                    player.quest.tracker.update()
                }
            }
        }

        onFirstInventoryAction(16089) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                if (QuestManager.hasCompletedQuest(player, "Pirate's Treasure")) {
                    player.sendMessage("You already have this quest completed.")
                    return@onFirstInventoryAction
                }
                player.promptRedeemDialogue(Item(16089, 1)) {
                    QuestManager.complete(
                        player,
                        QuestManager.PIRATES_TREASURE,
                        arrayOf<String>("10,000,000 coins."),
                        995
                    )

                    QuestManager.QUESTS["Pirate's Treasure"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Pirate's Treasure"]?.name,
                            it1.finalStage
                        )
                    }
                    player.quest.tracker.update()
                }
            }
        }

        onFirstInventoryAction(16090) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                if (QuestManager.hasCompletedQuest(player, "Waterfall Quest")) {
                    player.sendMessage("You already have this quest completed.")
                    return@onFirstInventoryAction
                }
                player.promptRedeemDialogue(Item(16090, 1)) {
                    QuestManager.complete(
                        player,
                        QuestManager.QUESTS["Waterfall Quest"],
                        arrayOf<String>(
                            "237,500 Strength XP",
                            "237,500 Attack XP",
                            "Cache of runes",
                            "40 Mithril seeds",
                            "a Mystery box."
                        ), ItemID.GLARIALS_URN
                    )

                    player.skillManager.addFixedDelayedExperience(Skill.STRENGTH, 237500)
                    player.skillManager.addFixedDelayedExperience(Skill.ATTACK, 237500)
                    player.inventory.add(Item(ItemID.CACHE_OF_RUNES, 1))
                    player.inventory.add(Item(ItemID.MITHRIL_SEEDS, 40))
                    player.inventory.add(Item(ItemID.MYSTERY_BOX, 1))

                    QuestManager.QUESTS["Waterfall Quest"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Waterfall Quest"]?.name,
                            it1.finalStage
                        )
                    }
                    player.quest.tracker.update()
                }
            }
        }

        onFirstInventoryAction(16091) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                if (QuestManager.hasCompletedQuest(player, "Dragon Slayer")) {
                    player.sendMessage("You already have this quest completed.")
                    return@onFirstInventoryAction
                }
                player.promptRedeemDialogue(Item(16091, 1)) {
                    QuestManager.complete(
                        player, QuestManager.QUESTS["Dragon Slayer"], arrayOf<String>(
                            "300,000 Attack XP",
                            "300,000 Strength XP",
                            "300,000 Defence XP",
                            "Ability to equip Rune and",
                            "Dragon Platebodies."
                        ), 1127
                    )
                    player.skillManager.addFixedDelayedExperience(Skill.ATTACK, 300000)
                    player.skillManager.addFixedDelayedExperience(Skill.STRENGTH, 300000)
                    player.skillManager.addFixedDelayedExperience(Skill.DEFENCE, 300000)

                    QuestManager.QUESTS["Dragon Slayer"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Dragon Slayer"]?.name,
                            it1.finalStage
                        )
                    }
                    player.quest.tracker.update()
                }
            }
        }

        onFirstInventoryAction(16092) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                if (QuestManager.hasCompletedQuest(player, "Monkey Madness")) {
                    player.sendMessage("You already have this quest completed.")
                    return@onFirstInventoryAction
                }
                player.promptRedeemDialogue(Item(16092, 1)) {
                    QuestManager.complete(
                        player, QuestManager.QUESTS["Monkey Madness"], arrayOf(
                            "150,000 Attack XP",
                            "75,000 Agility XP",
                            "Ability to equip the",
                            "Dragon scimitar/Lava blade",
                            "Access to Jungle demon lair"
                        ), ItemID.DRAGON_SCIMITAR
                    )
                    player.skillManager.addFixedDelayedExperience(Skill.ATTACK, 150000)
                    player.skillManager.addFixedDelayedExperience(Skill.AGILITY, 75000)

                    QuestManager.QUESTS["Monkey Madness"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Monkey Madness"]?.name,
                            it1.finalStage
                        )
                    }
                    player.quest.tracker.update()
                }
            }
        }

        onFirstInventoryAction(16093) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                if (QuestManager.hasCompletedQuest(player, "Desert Treasure")) {
                    player.sendMessage("You already have this quest completed.")
                    return@onFirstInventoryAction
                }
                player.promptRedeemDialogue(Item(16093, 1)) {
                    QuestManager.complete(
                        player,
                        QuestManager.QUESTS["Desert Treasure"],
                        arrayOf<String>("Ability to use Ancient Magicks"),
                        4675
                    )


                    QuestManager.QUESTS["Desert Treasure"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Desert Treasure"]?.name,
                            it1.finalStage
                        )
                    }
                    player.quest.tracker.update()
                }
            }
        }

        onFirstInventoryAction(16094) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                if (QuestManager.hasCompletedQuest(player, "Recipe for Disaster")) {
                    player.sendMessage("You already have this quest completed.")
                    return@onFirstInventoryAction
                }
                player.promptRedeemDialogue(Item(16094, 1)) {

                    QuestManager.complete(
                        player, QuestManager.QUESTS["Awowogei Quest"], arrayOf(
                            "150,000 Cooking XP",
                            "75,000 Agility XP",
                            "Increased access to the",
                            "Culinaromancer's Chest"
                        ), 7462
                    )


                    QuestManager.QUESTS["Awowogei Quest"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Awowogei Quest"]?.name,
                            it1.finalStage
                        )
                    }

                    player.skillManager.addFixedDelayedExperience(Skill.COOKING, 150000)
                    player.skillManager.addFixedDelayedExperience(Skill.AGILITY, 75000)
                    QuestManager.complete(
                        player, QuestManager.QUESTS["Dwarf Quest"], arrayOf(
                            "Culinaromancer's Chest"
                        ), 7462
                    )

                    QuestManager.QUESTS["Dwarf Quest"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Dwarf Quest"]?.name,
                            it1.finalStage
                        )
                    }
                    player.skillManager.addFixedDelayedExperience(Skill.COOKING, 225000)
                    player.skillManager.addFixedDelayedExperience(Skill.SLAYER, 200000)
                    QuestManager.complete(
                        player, QuestManager.QUESTS["Evil Dave"], arrayOf(
                            "Culinaromancer's Chest"
                        ), 7462
                    )

                    QuestManager.QUESTS["Evil Dave"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Evil Dave"]?.name,
                            it1.finalStage
                        )
                    }
                    player.skillManager.addFixedDelayedExperience(Skill.COOKING, 70000)
                    QuestManager.complete(
                        player, QuestManager.QUESTS["Goblin Generals"], arrayOf(
                            "Culinaromancer's Chest"
                        ), 7462
                    )
                    QuestManager.QUESTS["Goblin Generals"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Goblin Generals"]?.name,
                            it1.finalStage
                        )
                    }
                    player.skillManager.addFixedDelayedExperience(Skill.COOKING, 300000)
                    player.skillManager.addFixedDelayedExperience(Skill.CRAFTING, 100000)
                    player.skillManager.addFixedDelayedExperience(Skill.FARMING, 200000)

                    QuestManager.complete(
                        player, QuestManager.QUESTS["Pirate Pete Quest"], arrayOf(
                            "Culinaromancer's Chest"
                        ), 7462
                    )

                    QuestManager.QUESTS["Pirate Pete Quest"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Pirate Pete Quest"]?.name,
                            it1.finalStage
                        )
                    }
                    player.skillManager.addFixedDelayedExperience(Skill.COOKING, 100000)
                    player.skillManager.addFixedDelayedExperience(Skill.FISHING, 100000)
                    player.skillManager.addFixedDelayedExperience(Skill.MINING, 100000)
                    player.skillManager.addFixedDelayedExperience(Skill.CRAFTING, 100000)

                    QuestManager.complete(
                        player, QuestManager.QUESTS["Lumbridge Sage"], arrayOf(
                            "Culinaromancer's Chest"
                        ), 7462
                    )

                    QuestManager.QUESTS["Lumbridge Sage"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Lumbridge Sage"]?.name,
                            it1.finalStage
                        )
                    }
                    player.skillManager.addFixedDelayedExperience(Skill.COOKING, 200500)
                    player.skillManager.addFixedDelayedExperience(Skill.MAGIC, 375500)
                    QuestManager.complete(
                        player, QuestManager.QUESTS["Sir Amik"], arrayOf(
                            "Culinaromancer's Chest"
                        ), 7462
                    )

                    QuestManager.QUESTS["Sir Amik"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Sir Amik"]?.name,
                            it1.finalStage
                        )
                    }
                    player.skillManager.addFixedDelayedExperience(Skill.COOKING, 75000)
                    player.skillManager.addFixedDelayedExperience(Skill.HITPOINTS, 75000)

                    QuestManager.complete(
                        player, QuestManager.QUESTS["Skrach Quest"], arrayOf(
                            "Culinaromancer's Chest"
                        ), 7462
                    )
                    QuestManager.QUESTS["Skrach Quest"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Skrach Quest"]?.name,
                            it1.finalStage
                        )
                    }
                    player.skillManager.addFixedDelayedExperience(Skill.COOKING, 150000)
                    player.skillManager.addFixedDelayedExperience(Skill.WOODCUTTING, 150000)
                    player.skillManager.addFixedDelayedExperience(Skill.RANGED, 150000)
                    player.skillManager.addFixedDelayedExperience(Skill.CRAFTING, 150000)

                    QuestManager.complete(
                        player,
                        QuestManager.QUESTS["Recipe for Disaster"],
                        arrayOf<String>("Full Access to", "Culinaromancer's Chest"),
                        7462
                    )
                    QuestManager.QUESTS["Recipe for Disaster"]?.let { it1 ->
                        player.quest.tracker.setProgress(
                            QuestManager.QUESTS["Recipe for Disaster"]?.name,
                            it1.finalStage
                        )
                    }
                    player.quest.tracker.update()
                }
            }
        }

    }


    /*    for (q in QuestManager.QUESTS.values) {
            QuestManager.complete(player, q, arrayOfNulls(0), 4151)
            player.quest.tracker.setProgress(q.name, q.finalStage)
            player.getQuest().tracker.update();
        }*/
}