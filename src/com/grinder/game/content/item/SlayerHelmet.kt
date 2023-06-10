package com.grinder.game.content.item

import com.grinder.game.content.item.jewerly.SlayerRing
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.passedTime
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.*
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.name
import com.grinder.game.model.onSecondContainerEquipmentAction
import com.grinder.game.model.onSecondInventoryAction
import com.grinder.game.model.onThirdInventoryAction
import com.grinder.util.ItemID

/**
 * Handles slayer helmet disassembly.
 *
 * @author  ? (Stan converted this to Kotlin)
 * @since   15/01/2021
 */
object SlayerHelmet {

    init {
        
        onThirdInventoryAction(
                ItemID.SLAYER_HELMET,
                ItemID.SLAYER_HELMET_I_,
                ItemID.BLACK_SLAYER_HELMET,
                ItemID.BLACK_SLAYER_HELMET_I_,
                ItemID.GREEN_SLAYER_HELMET,
                ItemID.GREEN_SLAYER_HELMET_I_,
                ItemID.RED_SLAYER_HELMET,
                ItemID.RED_SLAYER_HELMET_I_,
                ItemID.PURPLE_SLAYER_HELMET,
                ItemID.PURPLE_SLAYER_HELMET_I_,
                21888,
                21890,
                23073,
                23075,
                15910,
                16109,
                ItemID.TWISTED_SLAYER_HELMET,
                ItemID.TWISTED_SLAYER_HELMET_I,
                ItemID.TZTOK_SLAYER_HELMET,
                ItemID.TZTOK_SLAYER_HELMET_I,
                ItemID.VAMPYRIC_SLAYER_HELMET,
                ItemID.VAMPYRIC_SLAYER_HELMET_I,
                ItemID.TZKAL_SLAYER_HELMET,
                ItemID.TZKAL_SLAYER_HELMET_I
        ) {
            SlayerRing.check(player)
        }
        onSecondContainerEquipmentAction(
            ItemID.SLAYER_HELMET,
            ItemID.SLAYER_HELMET_I_,
            ItemID.BLACK_SLAYER_HELMET,
            ItemID.BLACK_SLAYER_HELMET_I_,
            ItemID.GREEN_SLAYER_HELMET,
            ItemID.GREEN_SLAYER_HELMET_I_,
            ItemID.RED_SLAYER_HELMET,
            ItemID.RED_SLAYER_HELMET_I_,
            ItemID.PURPLE_SLAYER_HELMET,
            ItemID.PURPLE_SLAYER_HELMET_I_,
            21888,
            21890,
            23073,
            23075,
            15910,
            16109,
            ItemID.TWISTED_SLAYER_HELMET,
            ItemID.TWISTED_SLAYER_HELMET_I,
            ItemID.TZTOK_SLAYER_HELMET,
            ItemID.TZTOK_SLAYER_HELMET_I,
            ItemID.VAMPYRIC_SLAYER_HELMET,
            ItemID.VAMPYRIC_SLAYER_HELMET_I,
            ItemID.TZKAL_SLAYER_HELMET,
            ItemID.TZKAL_SLAYER_HELMET_I)
        {
            operate(player, 1)
        }

        onSecondInventoryAction(
                ItemID.SLAYER_HELMET,
                ItemID.SLAYER_HELMET_I_,
                ItemID.BLACK_SLAYER_HELMET,
                ItemID.BLACK_SLAYER_HELMET_I_,
                ItemID.GREEN_SLAYER_HELMET,
                ItemID.GREEN_SLAYER_HELMET_I_,
                ItemID.RED_SLAYER_HELMET,
                ItemID.RED_SLAYER_HELMET_I_,
                ItemID.PURPLE_SLAYER_HELMET,
                ItemID.PURPLE_SLAYER_HELMET_I_,
                21888,
                21890,
                23073,
                23075,
                15910,
                16109,
                ItemID.TWISTED_SLAYER_HELMET,
                ItemID.TWISTED_SLAYER_HELMET_I,
                ItemID.TZTOK_SLAYER_HELMET,
                ItemID.TZTOK_SLAYER_HELMET_I,
                ItemID.VAMPYRIC_SLAYER_HELMET,
                ItemID.VAMPYRIC_SLAYER_HELMET_I,
                ItemID.TZKAL_SLAYER_HELMET,
                ItemID.TZKAL_SLAYER_HELMET_I
        ) {
            if (player.passedTime(Attribute.GENERIC_ACTION, 1, message = false)){
                if (player.busy()){
                    player.message("You can't do that when you're busy.")
                    return@onSecondInventoryAction
                }

                // Disable dis-assemble for those items
                if (/*getItemId() == 21888 || getItemId() == 21890 || getItemId() == 23073 || getItemId() == 23075
                    || getItemId() == ItemID.TWISTED_SLAYER_HELMET
                    || getItemId() == ItemID.TWISTED_SLAYER_HELMET_I*/
                    getItemId() == ItemID.TZTOK_SLAYER_HELMET
                    || getItemId() == ItemID.TZTOK_SLAYER_HELMET_I
                    || getItemId() == ItemID.VAMPYRIC_SLAYER_HELMET
                    || getItemId() == ItemID.VAMPYRIC_SLAYER_HELMET_I
                    || getItemId() == ItemID.TZKAL_SLAYER_HELMET
                    || getItemId() == ItemID.TZKAL_SLAYER_HELMET_I) {
                    player.sendMessage("This item cannot be dis-assembled anymore.")
                    return@onSecondInventoryAction
                }
                val helmet = getItem()?:return@onSecondInventoryAction
                val coloured = isColoured(getItemId())
                val iVersion = isIVersion(getItemId())

                DialogueManager.start(player, 2703)
                player.dialogueOptions = object : DialogueOptions() {
                    override fun handleOption(player: Player, option: Int) {
                        when (option) {
                            1 -> {
                                if (player.checkFreeInventorySlots(if (coloured) 6 else 5)) {
                                    if (player.removeInventoryItem(helmet, -1)) {
                                        player.message("You have disassembled your @dre@" + helmet.name() + "</col>.")
                                        player.addInventoryItems(listOf(
                                                Item(if (iVersion)
                                                    ItemID.BLACK_MASK_I_
                                                else
                                                    ItemID.BLACK_MASK, 1),
                                                Item(ItemID.ENCHANTED_GEM, 1),
                                                Item(ItemID.FACEMASK, 1),
                                                Item(ItemID.EARMUFFS, 1),
                                                Item(ItemID.NOSE_PEG, 1),
                                                Item(ItemID.SPINY_HELMET, 1)
                                        ))
                                        val extraIngredient = when (getItemId()) {
                                            ItemID.BLACK_SLAYER_HELMET,
                                            ItemID.BLACK_SLAYER_HELMET_I_ -> ItemID.KBD_HEADS
                                            ItemID.GREEN_SLAYER_HELMET,
                                            ItemID.GREEN_SLAYER_HELMET_I_ -> ItemID.KQ_HEAD
                                            ItemID.RED_SLAYER_HELMET,
                                            ItemID.RED_SLAYER_HELMET_I_ -> ItemID.ABYSSAL_HEAD
                                            ItemID.PURPLE_SLAYER_HELMET,
                                            ItemID.PURPLE_SLAYER_HELMET_I_ -> ItemID.DARK_CLAW

                                            21888, // Turqouise Slayer Helmet
                                            21890 -> 21907

                                            23073, // Hydra Slayer Helmet
                                            23075 -> 23077

                                            ItemID.TWISTED_SLAYER_HELMET,
                                            ItemID.TWISTED_SLAYER_HELMET_I -> ItemID.TWISTED_HORNS

                                            else -> -1
                                        }
                                        if (extraIngredient != -1)
                                            player.addInventoryItem(Item(extraIngredient, 1))
                                    }
                                }
                            }
                        }
                        player.removeInterfaces()
                    }
                }
            }
        }
    }

    private fun isColoured(helmetId: Int) : Boolean {
        return helmetId == ItemID.BLACK_SLAYER_HELMET
                || helmetId == ItemID.BLACK_SLAYER_HELMET_I_
                || helmetId == ItemID.GREEN_SLAYER_HELMET
                || helmetId == ItemID.GREEN_SLAYER_HELMET_I_
                || helmetId == ItemID.RED_SLAYER_HELMET
                || helmetId == ItemID.RED_SLAYER_HELMET_I_
                || helmetId == ItemID.PURPLE_SLAYER_HELMET
                || helmetId == ItemID.PURPLE_SLAYER_HELMET_I_
                || helmetId == 21888
                || helmetId == 21890
                || helmetId == 23073
                || helmetId == 23075
                || helmetId == ItemID.TWISTED_SLAYER_HELMET
                || helmetId == ItemID.TWISTED_SLAYER_HELMET_I
                || helmetId == ItemID.TZTOK_SLAYER_HELMET
                || helmetId == ItemID.TZTOK_SLAYER_HELMET_I
                || helmetId == ItemID.VAMPYRIC_SLAYER_HELMET
                || helmetId == ItemID.VAMPYRIC_SLAYER_HELMET_I
                || helmetId == ItemID.TZKAL_SLAYER_HELMET
                || helmetId == ItemID.TZKAL_SLAYER_HELMET_I
    }

    private fun operate(player: Player, slot: Int) {

        if (player.slayer.task == null || player.slayer.task.monster.locations[0] == null) {
            SlayerRing.sendNoTaskOption(player)
            return
        }

        DialogueBuilder(DialogueType.STATEMENT)
            .setText("Do you wish to teleport to your Slayer task", "monster location?")
            .add(DialogueType.OPTION)
            .firstOption("Yes please.") {
                val slayerHelm = player.equipment.findAtSlot(slot)
                slayerHelm.ifPresent { helmet ->
                    TeleportHandler.teleport(
                        it,
                        it.slayer.task.monster.locations[0],
                        it.spellbook.teleportType,
                        true,
                        true)
                }

            }
            .secondOption("Teleport me to my Slayer Master.") {
                SlayerRing.teleportToSlayerMaster(player)
            }
            .thirdOption("Show me how many left.") {
                SlayerRing.check(player)
            }
            .addCancel("Maybe later!")
            .start(player)

    }

    private fun isIVersion(helmetId: Int) : Boolean {
        return helmetId == ItemID.SLAYER_HELMET_I_
                || helmetId == ItemID.BLACK_SLAYER_HELMET_I_
                || helmetId == ItemID.GREEN_SLAYER_HELMET_I_
                || helmetId == ItemID.RED_SLAYER_HELMET_I_
                || helmetId == ItemID.PURPLE_SLAYER_HELMET_I_
                || helmetId == 21890
                || helmetId == 23075
                || helmetId == ItemID.TWISTED_SLAYER_HELMET_I
                || helmetId == ItemID.TZTOK_SLAYER_HELMET_I
                || helmetId == ItemID.VAMPYRIC_SLAYER_HELMET_I
                || helmetId == ItemID.TZKAL_SLAYER_HELMET_I
    }
}