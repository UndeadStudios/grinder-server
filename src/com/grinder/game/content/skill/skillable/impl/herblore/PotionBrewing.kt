package com.grinder.game.content.skill.skillable.impl.herblore

import com.grinder.game.content.item.jewerly.AmuletOfChemistry
import com.grinder.game.content.miscellaneous.PetHandler
import com.grinder.game.content.quest.QuestManager
import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.content.skill.skillable.ItemCreationSkillable
import com.grinder.game.content.skill.task.SkillMasterType
import com.grinder.game.content.skill.task.SkillTaskManager
import com.grinder.game.content.task_new.DailyTask
import com.grinder.game.content.task_new.PlayerTaskManager
import com.grinder.game.content.task_new.WeeklyTask
import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.passedTimeGenericAction
import com.grinder.game.model.*
import com.grinder.game.model.attribute.AttributeManager
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueManager
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.menu.impl.SingleItemCreationMenu
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.RequiredItem
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.SoundLoop
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.TaskFunctions
import java.util.*
import java.util.function.Consumer

/**
 *  Handles potion brewing skill actions.
 *
 * @author  Professor Oak
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/02/2021
 */
object PotionBrewing {

    /**
     * Potion brewing animation.
     */
    private val ANIMATION = Animation(363)

    /*
    * Messages that are sent to the player while training Herblore skill
     */
    private val HERBLORE_MESSAGES = arrayOf(
        arrayOf("@whi@Did you know Zahur can help you clean herbs or create potions?"),
        arrayOf("@whi@You can take a Herblore skill task from your master for bonus rewards."),
        arrayOf("@whi@Check out the skill guides or Wiki for the fastest XP methods."),
        arrayOf("@whi@Herbloring with the skillcape equipped will give you 20% bonus experience gain!")
    )
    var currentMessage: String? = null

    fun sendSkillRandomMessages(player: Player) {
        currentMessage = HERBLORE_MESSAGES[Misc.getRandomInclusive(HERBLORE_MESSAGES.size - 1)][0]
        player.packetSender.sendMessage("<img=779> $currentMessage")
    }

    init {

        /**
         * Handle grimy herb cleaning.
         */
        for (type in PotionIngredientHerbType.values()){
            val cleanHerbName = ItemDefinition.forId(type.cleanHerb).name
            val grimyHerbName = ItemDefinition.forId(type.grimyHerb).name
            onFirstInventoryAction(type.grimyHerb) {
                if(!QuestManager.hasCompletedQuest(player, "Druidic Ritual")) {
                    player.sendMessage("You need to complete the quest 'Druidic Ritual' to be able to do this.")
                    return@onFirstInventoryAction
                }
                if (player.hasSkillLevel(Skill.HERBLORE, type.levelReq, action = "to clean $grimyHerbName")) {
                  //  if (player.clickDelay.elapsed(50L)){
                        player.clickDelay.reset()
                        player.inventory.set(getSlot(), Item(type.cleanHerb))
                        player.inventory.refreshItems()

                        // Add xp
                        player.addExperience(Skill.HERBLORE, type.exp)

                        // Process skill task
                        SkillTaskManager.perform(player, type.cleanHerb, 1, SkillMasterType.HERBLORE)

                        // Send message
                        player.message("You clean the dirt off the $cleanHerbName.")

                        // Send random skill messages
                        if (Misc.getRandomInclusive(15) == Misc.getRandomInclusive(15) && player.skillManager.getMaxLevel(Skill.HERBLORE) < SkillUtil.maximumAchievableLevel()) {
                            TaskFunctions.delayBy(3) {
                                sendSkillRandomMessages(player)
                            }
                        }


                   // }
                }
            }
        }

        /**
         * Handle potion ingredient grinding.
         */
        for (type in PotionIngredientGrindType.values()){
            ItemActions.onItemOnItem(ItemID.PESTLE_AND_MORTAR to type.id) {
                if(!QuestManager.hasCompletedQuest(player, "Druidic Ritual")) {
                    player.sendMessage("You need to complete the quest 'Druidic Ritual' to be able to do this.")
                    return@onItemOnItem true
                }
                if (type == PotionIngredientGrindType.SEAWEED){
                    if (player.passedTimeGenericAction()){
                        player.motion.update(MovementStatus.DISABLED)
                        player.resetInteractions(true, false)
                        player.removeInterfaces()
                        player.playSound(2608, 0, 1)
                        player.removeInventoryItem(Item(type.id), 0)
                        player.addInventoryItem(Item(type.result), 1)
                        TaskManager.submit(1) {
                            player.motion.update(MovementStatus.NONE)
                            player.message("You grind down the ${ItemDefinition.forId(type.id).name.toLowerCase()}.")
                        }

                    }
                } else {
                    val menu = SingleItemCreationMenu(
                        player, type.result, "How many ${ItemDefinition.forId(type.result).name.toLowerCase()} you like to grind?"
                    ) { _, _, amount ->
                        val toGrind = ItemCreationSkillable(
                            listOf(
                                RequiredItem(Item(type.id), true),
                                RequiredItem(Item(ItemID.PESTLE_AND_MORTAR), false)
                            ),
                            Item(type.result),
                            amount,
                            AnimationLoop(ANIMATION, 4),
                            SoundLoop(Sound(2608, 0), 4),
                            type.requiredLevel,
                            type.xp,
                            Skill.HERBLORE,
                            "You grind the ${ItemDefinition.forId(type.id).name.toLowerCase()} down.",
                            2
                        )
                        SkillUtil.startSkillable(player, toGrind)
                    }.open()
                    player.creationMenu = Optional.of(menu)
                }
                return@onItemOnItem true
            }
            if (type.swampTarRequired) {
                ItemActions.onItemOnItem(ItemID.SWAMP_TAR to type.id) {
                    if(!QuestManager.hasCompletedQuest(player, "Druidic Ritual")) {
                        player.sendMessage("You need to complete the quest 'Druidic Ritual' to be able to do this.")
                        return@onItemOnItem true
                    }
                    if (type.swampTarRequired) {
                        if (player.inventory.getAmount(ItemID.SWAMP_TAR) < 15) {
                            DialogueBuilder(DialogueType.ITEM_STATEMENT)
                                .setItem(ItemID.SWAMP_TAR, 200)
                                .setText(
                                    "You don't have enough swamp tar to mix with this herb.",
                                    "You need at least 15 swamp tar."
                                )
                                .start(player)
                        } else {
                            val menu = SingleItemCreationMenu(
                                player,
                                type.result,
                                "How many ${ItemDefinition.forId(type.result).name.toLowerCase()} you like to grind?"
                            ) { _, _, amount ->
                                val toGrind = ItemCreationSkillable(
                                    listOf(
                                        RequiredItem(Item(type.id), true),
                                        RequiredItem(Item(ItemID.SWAMP_TAR, 15), true),
                                        RequiredItem(Item(ItemID.PESTLE_AND_MORTAR), false)
                                    ),
                                    Item(type.result),
                                    amount,
                                    AnimationLoop(ANIMATION, 4),
                                    SoundLoop(Sound(2608, 0), 4),
                                    type.requiredLevel,
                                    type.xp,
                                    Skill.HERBLORE,
                                    "You mix the ${ItemDefinition.forId(type.id).name.toLowerCase()} into the swamp tar.",
                                    2
                                )
                                SkillUtil.startSkillable(player, toGrind)
                            }.open()
                            player.creationMenu = Optional.of(menu)
                        }
                    }
                    return@onItemOnItem true
                }
            }
        }

        /**
         * Handle potion brewing from vial of water to (unf) type.
         */
        for (type in PotionUnfinishedType.values()){
            ItemActions.onItemOnItem(type.requiredVialId to type.requiredHerbId) {
                if(!QuestManager.hasCompletedQuest(player, "Druidic Ritual")) {
                    player.sendMessage("You need to complete the quest 'Druidic Ritual' to be able to do this.")
                    return@onItemOnItem true
                }
                if (player.tryRandomEventTrigger(1.3f)) return@onItemOnItem true
                player.creationMenu = Optional.of(SingleItemCreationMenu(
                    player,
                    type.potionUnfId,
                    "How many potions would you like to make?"
                ) { _, _, amount ->
                    val skillable = ItemCreationSkillable(
                        listOf(
                            RequiredItem(Item(type.requiredVialId), true),
                            RequiredItem(Item(type.requiredHerbId), true)
                        ),
                        Item(type.potionUnfId),
                        amount,
                        AnimationLoop(ANIMATION, 4),
                        SoundLoop(Sound(2608, 0), 4),
                        type.requiredHerbloreLevel,
                        10,
                        Skill.HERBLORE,
                        "You put the ${ItemDefinition.forId(type.requiredHerbId).name.toLowerCase()} into the vial of of water.",
                        2)
                    SkillUtil.startSkillable(player, skillable)
                }.open())
                return@onItemOnItem true
            }
        }

        /**
         * Handle potion brewing from potion to (unf) type.
         */
        for (type in PotionAddToPotionType.values()){
            ItemActions.onItemOnItem(type.requiredPotionId to type.requiredIngredientId) {
                if(!QuestManager.hasCompletedQuest(player, "Druidic Ritual")) {
                    player.sendMessage("You need to complete the quest 'Druidic Ritual' to be able to do this.")
                    return@onItemOnItem true
                }
                if (player.tryRandomEventTrigger(1.3f)) return@onItemOnItem true
                player.creationMenu = Optional.of(SingleItemCreationMenu(
                    player,
                    type.potionUnfId,
                    "How many potions would you like to make?"
                ) { _, _, amount ->
                    val skillable = ItemCreationSkillable(
                        listOf(
                            RequiredItem(Item(type.requiredPotionId), true),
                            RequiredItem(Item(type.requiredIngredientId), true)
                        ),
                        Item(type.potionUnfId),
                        amount,
                        AnimationLoop(ANIMATION, 4),
                        SoundLoop(Sound(2608, 0), 4),
                        type.requiredHerbloreLevel,
                        type.experience,
                        Skill.HERBLORE,
                        "You put the ${ItemDefinition.forId(type.requiredIngredientId).name.toLowerCase()} into the ${ItemDefinition.forId(type.requiredPotionId).name.toLowerCase()}.",
                        2)
                    SkillUtil.startSkillable(player, skillable)
                }.open())
                return@onItemOnItem true
            }
        }



        /**
         * Handle potion brewing from (unf) to finished type.
         */
        for (type in PotionFinishedType.values()){


            ItemActions.onItemOnItem(type.unfinishedPotion to type.itemNeeded) {
                if(!QuestManager.hasCompletedQuest(player, "Druidic Ritual")) {
                    player.sendMessage("You need to complete the quest 'Druidic Ritual' to be able to do this.")
                    return@onItemOnItem true
                }
                if (type.finishedPotion == ItemID.STAMINA_POTION_4_) {
                    if (player.tryRandomEventTrigger(1.3f)) return@onItemOnItem true

                    openStaminaUpgradeDialogue(player, type);

                } else if (type.finishedPotion == ItemID.ANTIDOTE_PLUS_PLUS_4_) {
                    if ((type.itemNeeded == ItemID.IRIT_LEAF && !player.inventory.contains(ItemID.MAGIC_ROOTS)) ||
                        (type.itemNeeded == ItemID.MAGIC_ROOTS && !player.inventory.contains(ItemID.IRIT_LEAF))) {
                        player.sendMessage("You need an Irit Leaf and Magic Roots to do that!");
                        return@onItemOnItem true
                    } else {
                        openAntidotePlusPlusUpgradeDialogue(player, type);
                    }
                } else {
                    if (player.tryRandomEventTrigger(1.3f)) return@onItemOnItem true
                    player.creationMenu = Optional.of(SingleItemCreationMenu(
                        player,
                        type.finishedPotion,
                        "How many potions would you like to make?"
                    ) { _, _, amount ->
                        val skillable = object : ItemCreationSkillable(
                            listOf(
                                RequiredItem(Item(type.itemNeeded), true),
                                RequiredItem(Item(type.unfinishedPotion), true)
                            ),
                            Item(type.finishedPotion),
                            amount,
                            AnimationLoop(ANIMATION, 4),
                            SoundLoop(Sound(2608, 0), 4),
                            type.levelReq,
                            type.expGained,
                            Skill.HERBLORE,
                            "You mix the ${ItemDefinition.forId(type.itemNeeded).name.toLowerCase()} into your potion.",
                            2
                        ) {
                            override fun finishedCycle(player: Player) {
                                if (Misc.randomChance(5F) && AmuletOfChemistry.brewExtraDoseForPotion(player))
                                    product.id = if (AmuletOfChemistry.hasEquippedAndHasCharge(player))
                                        PotionDosageType.forId(type.finishedPotion)
                                            .map { it.fourDosePotionID }
                                            .orElse(type.finishedPotion)
                                    else
                                        type.finishedPotion
                                else if (product.id != type.finishedPotion)
                                    product.id = type.finishedPotion
                                super.finishedCycle(player)
                                PetHandler.onSkill(player, Skill.HERBLORE);
                                player.points.increase(AttributeManager.Points.POTIONS_CREATED) // Increase points
                                PlayerTaskManager.progressTask(player, DailyTask.POTIONS_CREATOR)
                                PlayerTaskManager.progressTask(player, WeeklyTask.POTIONS_CREATOR)
                            }
                        }
                        SkillUtil.startSkillable(player, skillable)
                    }.open()
                    )
                }
                return@onItemOnItem true
            }
        }

        ItemActions.onItemOnItem(ItemID.ANTIDOTE_PLUS_PLUS_4_ to ItemID.ZULRAHS_SCALES) {
            if (player.inventory.contains(ItemID.ANTIDOTE_PLUS_PLUS_4_) && player.inventory.getAmount(ItemID.ZULRAHS_SCALES) >= 20) {
                player.performAnimation(ANIMATION)
                player.inventory.delete(ItemID.ZULRAHS_SCALES, 20)
                player.inventory.delete(ItemID.ANTIDOTE_PLUS_PLUS_4_, 1)
                player.inventory.add(Item(ItemID.ANTI_VENOM_4_, 1))
                player.addExperience(Skill.HERBLORE, 120)
            } else {
                player.sendMessage("You need at least 20 Zulrah Scales to do that!")
            }
            return@onItemOnItem true
        }
        ItemActions.onItemOnItem(ItemID.ANTIDOTE_PLUS_PLUS_3_ to ItemID.ZULRAHS_SCALES) {
            if(!QuestManager.hasCompletedQuest(player, "Druidic Ritual")) {
                player.sendMessage("You need to complete the quest 'Druidic Ritual' to be able to do this.")
                return@onItemOnItem true
            }
            if (player.inventory.contains(ItemID.ANTIDOTE_PLUS_PLUS_3_) && player.inventory.getAmount(ItemID.ZULRAHS_SCALES) >= 20) {
                player.performAnimation(ANIMATION)
                player.inventory.delete(ItemID.ZULRAHS_SCALES, 20)
                player.inventory.delete(ItemID.ANTIDOTE_PLUS_PLUS_3_, 1)
                player.inventory.add(Item(ItemID.ANTI_VENOM_3_, 1))
                player.addExperience(Skill.HERBLORE, 120)
            } else {
                player.sendMessage("You need at least 20 Zulrah Scales to do that!")
            }
            return@onItemOnItem true
        }
        ItemActions.onItemOnItem(ItemID.ANTIDOTE_PLUS_PLUS_2_ to ItemID.ZULRAHS_SCALES) {
            if(!QuestManager.hasCompletedQuest(player, "Druidic Ritual")) {
                player.sendMessage("You need to complete the quest 'Druidic Ritual' to be able to do this.")
                return@onItemOnItem true
            }
            if (player.inventory.contains(ItemID.ANTIDOTE_PLUS_PLUS_2_) && player.inventory.getAmount(ItemID.ZULRAHS_SCALES) >= 20) {
                player.performAnimation(ANIMATION)
                player.inventory.delete(ItemID.ZULRAHS_SCALES, 20)
                player.inventory.delete(ItemID.ANTIDOTE_PLUS_PLUS_2_, 1)
                player.inventory.add(Item(ItemID.ANTI_VENOM_2_, 1))
                player.addExperience(Skill.HERBLORE, 120)
            } else {
                player.sendMessage("You need at least 20 Zulrah Scales to do that!")
            }
            return@onItemOnItem true
        }
        ItemActions.onItemOnItem(ItemID.ANTIDOTE_PLUS_PLUS_1_ to ItemID.ZULRAHS_SCALES) {
            if(!QuestManager.hasCompletedQuest(player, "Druidic Ritual")) {
                player.sendMessage("You need to complete the quest 'Druidic Ritual' to be able to do this.")
                return@onItemOnItem true
            }
            if (player.inventory.contains(ItemID.ANTIDOTE_PLUS_PLUS_1_) && player.inventory.getAmount(ItemID.ZULRAHS_SCALES) >= 20) {
                player.performAnimation(ANIMATION)
                player.inventory.delete(ItemID.ZULRAHS_SCALES, 20)
                player.inventory.delete(ItemID.ANTIDOTE_PLUS_PLUS_1_, 1)
                player.inventory.add(Item(ItemID.ANTI_VENOM_1_, 1))
                player.addExperience(Skill.HERBLORE, 120)
            } else {
                player.sendMessage("You need at least 20 Zulrah Scales to do that!")
            }
            return@onItemOnItem true
        }

        val antiVenomPotions = mapOf(
            ItemID.ANTI_VENOM_1_ to ItemID.ANTI_VENOM_PLUS_1_,
            ItemID.ANTI_VENOM_2_ to ItemID.ANTI_VENOM_PLUS_2_,
            ItemID.ANTI_VENOM_3_ to ItemID.ANTI_VENOM_PLUS_3_,
            ItemID.ANTI_VENOM_4_ to ItemID.ANTI_VENOM_PLUS_4_
        )
        for ((baseId, plusId) in antiVenomPotions) {
            ItemActions.onItemOnItem(baseId to ItemID.TORSTOL) {
                if(!QuestManager.hasCompletedQuest(player, "Druidic Ritual")) {
                    player.sendMessage("You need to complete the quest 'Druidic Ritual' to be able to do this.")
                    return@onItemOnItem true
                }
                player.performAnimation(ANIMATION)
                val inventory = player.inventory
                inventory.delete(ItemID.TORSTOL, 1)
                inventory.delete(baseId, 1)
                inventory.add(Item(plusId, 1))
                player.addExperience(Skill.HERBLORE, 125)
                return@onItemOnItem true
            }
        }
    }

    private fun openStaminaUpgradeDialogue(player: Player, type: PotionFinishedType) {
        DialogueBuilder(DialogueType.OPTION).setOptionTitle("How many would you like to upgrade?")
            .firstOption("Just the one.", Consumer { player2: Player? ->
                val skillable = object : ItemCreationSkillable(
                    listOf(
                        RequiredItem(Item(type.itemNeeded), true),
                        RequiredItem(Item(type.unfinishedPotion), true)
                    ),
                    Item(type.finishedPotion),
                    1,
                    AnimationLoop(ANIMATION, 4),
                    SoundLoop(Sound(2608, 0), 4),
                    type.levelReq,
                    type.expGained,
                    Skill.HERBLORE,
                    "You mix the ${ItemDefinition.forId(type.itemNeeded).name.toLowerCase()} into your potion.",
                    2
                ) {
                    override fun finishedCycle(player: Player) {
                        if (Misc.randomChance(5F) && AmuletOfChemistry.brewExtraDoseForPotion(player))
                            product.id = if (AmuletOfChemistry.hasEquippedAndHasCharge(player))
                                PotionDosageType.forId(type.finishedPotion)
                                    .map { it.fourDosePotionID }
                                    .orElse(type.finishedPotion)
                            else
                                type.finishedPotion
                        else if (product.id != type.finishedPotion)
                            product.id = type.finishedPotion
                        super.finishedCycle(player)
                    }
                }
                SkillUtil.startSkillable(player, skillable)
                DialogueManager.start(player, -1)
            })
            .secondOption("All of them.", Consumer { player2: Player? ->
                var all = player.inventory.getAmount(type.unfinishedPotion);

                if (player.inventory.getAmount(type.itemNeeded) > all) {
                    all = player.inventory.getAmount(type.itemNeeded);
                }
                val skillable = object : ItemCreationSkillable(
                    listOf(
                        RequiredItem(Item(type.itemNeeded), true),
                        RequiredItem(Item(type.unfinishedPotion), true)
                    ),
                    Item(type.finishedPotion),
                    all,
                    AnimationLoop(ANIMATION, 4),
                    SoundLoop(Sound(2608, 0), 4),
                    type.levelReq,
                    type.expGained,
                    Skill.HERBLORE,
                    "You mix the ${ItemDefinition.forId(type.itemNeeded).name.toLowerCase()} into your potion.",
                    2
                ) {
                    override fun finishedCycle(player: Player) {
                        if (Misc.randomChance(5F) && AmuletOfChemistry.brewExtraDoseForPotion(player))
                            product.id = if (AmuletOfChemistry.hasEquippedAndHasCharge(player))
                                PotionDosageType.forId(type.finishedPotion)
                                    .map { it.fourDosePotionID }
                                    .orElse(type.finishedPotion)
                            else
                                type.finishedPotion
                        else if (product.id != type.finishedPotion)
                            product.id = type.finishedPotion
                        super.finishedCycle(player)
                    }
                }
                SkillUtil.startSkillable(player, skillable)
                DialogueManager.start(player, -1)
            })
            .start(player)
    }

    private fun openAntidotePlusPlusUpgradeDialogue(player: Player, type: PotionFinishedType) {
        player.creationMenu = Optional.of(SingleItemCreationMenu(
            player,
            type.finishedPotion,
            "How many potions would you like to make?"
        ) { _, _, amount ->
            val skillable = object : ItemCreationSkillable(
                listOf(
                    RequiredItem(Item(ItemID.IRIT_LEAF), true),
                    RequiredItem(Item(ItemID.MAGIC_ROOTS), true),
                    RequiredItem(Item(type.unfinishedPotion), true)
                ),
                Item(type.finishedPotion),
                amount,
                AnimationLoop(ANIMATION, 4),
                SoundLoop(Sound(2608, 0), 4),
                type.levelReq,
                type.expGained,
                Skill.HERBLORE,
                "You mix the ${ItemDefinition.forId(type.itemNeeded).name.toLowerCase()} into your potion.",
                2)
            {
                override fun finishedCycle(player: Player) {
                    if (Misc.randomChance(5F) && AmuletOfChemistry.brewExtraDoseForPotion(player))
                        product.id = if (AmuletOfChemistry.hasEquippedAndHasCharge(player))
                            PotionDosageType.forId(type.finishedPotion)
                                .map { it.fourDosePotionID }
                                .orElse(type.finishedPotion)
                        else
                            type.finishedPotion
                    else if (product.id != type.finishedPotion)
                        product.id = type.finishedPotion
                    super.finishedCycle(player)
                }
            }
            SkillUtil.startSkillable(player, skillable)
        }.open())

    }
}