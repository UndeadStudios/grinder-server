package com.grinder.game.entity.agent.player

import com.grinder.game.content.item.MorphItems.isCrab
import com.grinder.game.content.item.MorphItems.isDemon
import com.grinder.game.content.item.MorphItems.isDrunk
import com.grinder.game.content.miscellaneous.WelcomeManager
import com.grinder.game.content.miscellaneous.WelcomeManager.WelcomeStage
import com.grinder.game.entity.agent.player.death.ItemsKeptOnDeath
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.toggleBoolean
import com.grinder.game.model.ButtonActions.onClick
import com.grinder.game.model.ButtonActions.setIgnoreRestriction
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.DialogueOptions
import com.grinder.util.timing.TimerKey

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   28/11/2019
 * @version 1.0
 */
object PlayerSettings {

    private const val LOGOUT = 2458
    private const val TOGGLE_RUN_ENERGY_ORB = 1050
    private const val TOGGLE_RUN_ENERGY_SETTINGS = 19158
    private const val TOGGLE_EXP_LOCK = 476
    private const val OPEN_EXP_DROPS_SETUP = 257
    private const val OPEN_WORLD_MAP = 156
    private const val OPEN_EQUIPMENT_SCREEN = 27651

    private const val OPEN_PRICE_CHECKER = 27652
    private const val PRICE_CHECKER_WITHDRAW_ALL = 18370
    private const val PRICE_CHECKER_DEPOSIT_ALL = 18372
    private const val PRICE_CHECKER_SEARCH = 18360

    private const val OPEN_ITEMS_KEPT_ON_DEATH_SCREEN = 27653

    private const val OPEN_ADVANCED_OPTIONS = 42524
    private const val OPEN_ADVANCED_OPTIONS2 = 42508
    private const val OPEN_KEY_BINDINGS = 42552

    private const val TOGGLE_AUTO_RETALIATE_328 = 24115
    private const val TOGGLE_AUTO_RETALIATE_425 = 24041
    private const val TOGGLE_AUTO_RETALIATE_3796 = 24033
    private const val TOGGLE_AUTO_RETALIATE_776 = 24048
    private const val TOGGLE_AUTO_RETALIATE_1698 = 24017
    private const val TOGGLE_AUTO_RETALIATE_1764 = 24010
    private const val TOGGLE_AUTO_RETALIATE_2276 = 22845
    private const val TOGGLE_AUTO_RETALIATE_5570 = 24025

    private const val FIRST_DIALOGUE_OPTION_OF_FIVE = 2494
    private const val SECOND_DIALOGUE_OPTION_OF_FIVE = 2495
    private const val THIRD_DIALOGUE_OPTION_OF_FIVE = 2496
    private const val FOURTH_DIALOGUE_OPTION_OF_FIVE = 2497
    private const val FIFTH_DIALOGUE_OPTION_OF_FIVE = 2498
    private const val FIRST_DIALOGUE_OPTION_OF_FOUR = 2482
    private const val SECOND_DIALOGUE_OPTION_OF_FOUR = 2483
    private const val THIRD_DIALOGUE_OPTION_OF_FOUR = 2484
    private const val FOURTH_DIALOGUE_OPTION_OF_FOUR = 2485
    private const val FIRST_DIALOGUE_OPTION_OF_THREE = 2471
    private const val SECOND_DIALOGUE_OPTION_OF_THREE = 2472
    private const val THIRD_DIALOGUE_OPTION_OF_THREE = 2473
    private const val FIRST_DIALOGUE_OPTION_OF_TWO = 2461
    private const val SECOND_DIALOGUE_OPTION_OF_TWO = 2462

    private const val CLOSE_BUTTON_1 = 18247
    private const val CLOSE_BUTTON_2 = 38117
    private const val CANCEL_DESTROY_ITEM = 14176

    init {

        onClick(42544) {
            player.message("You no longer have the option to change your display name.")
        }

        onClick(349, 24111, 65031, 65028) {
            player.message("A spell can be autocast by simply right-clicking " +
                    "on it in your Magic spellbook and selecting the \"Autocast\" option.")
        }

        setIgnoreRestriction(3651, true)
        onClick(3651) {
            if (player.isInTutorial)
                WelcomeManager.welcome(player, WelcomeStage.STARTER)
            player.appearance.setCanChangeAppearance(false)
            player.updateAppearance()
        }

        onClick(CLOSE_BUTTON_1, CLOSE_BUTTON_2, CANCEL_DESTROY_ITEM) {
            player.removeInterfaces()
        }

        onClick(26702) {
            player.packetSender.sendTabs()
        }

        setIgnoreRestriction(LOGOUT, true)
        onClick(LOGOUT) {
            if (player.hasLogoutTimer()) {
                player.packetSender.sendInterfaceRemoval();
                player.sendMessage("@red@Your logout request has been interrupted.")
                player.setHasLogoutTimer(false);
                return@onClick
            }
            if(player.canLogout())
                player.requestClientLogout()
        }

        onClick(OPEN_EXP_DROPS_SETUP, OPEN_WORLD_MAP,
                OPEN_ADVANCED_OPTIONS, OPEN_ADVANCED_OPTIONS2,
                OPEN_KEY_BINDINGS)
        {
            val interfaceId = when(id){
                OPEN_EXP_DROPS_SETUP -> 55200
                OPEN_WORLD_MAP -> 54000
                OPEN_ADVANCED_OPTIONS,
                OPEN_ADVANCED_OPTIONS2 -> 23000
                OPEN_KEY_BINDINGS -> 53000
                else -> return@onClick
            }
            if (player.busy())
                player.removeInterfaces()
            player.openInterface(interfaceId)
        }
        onClick(OPEN_EQUIPMENT_SCREEN) {
            if (player.busy())
                player.removeInterfaces()
            EquipmentBonuses.open(player)
            EquipmentBonuses.update(player)
            player.equipment.refreshItems()
        }

        onClick(OPEN_ITEMS_KEPT_ON_DEATH_SCREEN) {
            if (player.busy())
                player.removeInterfaces()
            ItemsKeptOnDeath.open(player)
        }
        configureAutoRetaliateButtons()
        configureDialogueButtons()
        configurePriceCheckerButtons()
        configureToggleButtons()
    }

    private fun configureDialogueButtons(){
        onClick(FIRST_DIALOGUE_OPTION_OF_FIVE, FIRST_DIALOGUE_OPTION_OF_FOUR, FIRST_DIALOGUE_OPTION_OF_THREE, FIRST_DIALOGUE_OPTION_OF_TWO, ignoreRestriction = true) {
            if (player.dialogueOptions != null) {
                val opt: DialogueOptions = player.dialogueOptions

                player.dialogueOptions = null

                opt?.handleOption(player, 1)
            }
        }
        onClick(SECOND_DIALOGUE_OPTION_OF_FIVE, SECOND_DIALOGUE_OPTION_OF_FOUR, SECOND_DIALOGUE_OPTION_OF_THREE, SECOND_DIALOGUE_OPTION_OF_TWO, ignoreRestriction = true) {
            if (player.dialogueOptions != null) {
                val opt: DialogueOptions = player.dialogueOptions

                player.dialogueOptions = null

                opt?.handleOption(player, 2)
            }
        }
        onClick(THIRD_DIALOGUE_OPTION_OF_FIVE, THIRD_DIALOGUE_OPTION_OF_FOUR, THIRD_DIALOGUE_OPTION_OF_THREE, ignoreRestriction = true) {
            if (player.dialogueOptions != null) {
                val opt: DialogueOptions = player.dialogueOptions

                player.dialogueOptions = null

                opt?.handleOption(player, 3)
            }
        }
        onClick(FOURTH_DIALOGUE_OPTION_OF_FIVE, FOURTH_DIALOGUE_OPTION_OF_FOUR, ignoreRestriction = true) {
            if (player.dialogueOptions != null) {
                val opt: DialogueOptions = player.dialogueOptions

                player.dialogueOptions = null

                opt?.handleOption(player, 4)
            }
        }
        onClick(FIFTH_DIALOGUE_OPTION_OF_FIVE, ignoreRestriction = true) {
            if (player.dialogueOptions != null) {
                val opt: DialogueOptions = player.dialogueOptions

                player.dialogueOptions = null

                opt?.handleOption(player, 5)
            }
        }
    }

    private fun configureAutoRetaliateButtons() {
        onClick(
                TOGGLE_AUTO_RETALIATE_328,
                TOGGLE_AUTO_RETALIATE_425,
                TOGGLE_AUTO_RETALIATE_776,
                TOGGLE_AUTO_RETALIATE_1698,
                TOGGLE_AUTO_RETALIATE_1764,
                TOGGLE_AUTO_RETALIATE_2276,
                TOGGLE_AUTO_RETALIATE_3796,
                TOGGLE_AUTO_RETALIATE_5570,
                65035) {
            if (!player.isInTutorial) {
                player.combat.setAutoRetaliate(player.combat.retaliateAutomatically().not())
                player.packetSender.sendConfig(172, if (player.combat.retaliateAutomatically()) 1 else 0)
            }
        }
    }

    private fun configureToggleButtons() {

        onClick(TOGGLE_EXP_LOCK) {
            toggleExperience(player)
        }

        onClick(TOGGLE_RUN_ENERGY_ORB, TOGGLE_RUN_ENERGY_SETTINGS) {
            if (player.busy())
                player.removeInterfaces()
            val state = when {
                player.isDrunk() -> "you are drunk"
                player.isDemon() -> "you are in demon mode"
                player.isCrab() -> "you are a crab"
                player.equipment.items[EquipmentConstants.WEAPON_SLOT].id == 20056 -> "holding the Ale of the gods"
                player.equipment.items[EquipmentConstants.WEAPON_SLOT].id == 7671
                        || player.equipment.items[EquipmentConstants.WEAPON_SLOT].id == 7673
                        || player.equipment.items[EquipmentConstants.WEAPON_SLOT].id == 11705
                        || player.equipment.items[EquipmentConstants.WEAPON_SLOT].id == 11706 -> "holding boxing gloves"
                else -> ""
            }

            if (state.isNotEmpty()) {
                player.isRunning = false
                player.message("You can't toggle the run while $state!")
            } else
                player.isRunning = player.runEnergy > 0 && !player.isRunning

            player.packetSender.sendRunStatus()
        }
    }

    fun toggleExperience(player: Player) {
        val state = if (player.toggleBoolean(Attribute.EXPERIENCED_LOCKED)) "@red@Locked" else "@gre@Unlocked"
        player.packetSender.sendString(31658, state)
        player.message("Your experience is now $state@bla@.")
    }

    fun toggleMultiplyXPDropsState(player: Player) {
        player.toggleBoolean(Attribute.MULTIPLY_XP_DROPS)
        updateMultiplyXPDropsState(player)
    }

    fun updateMultiplyXPDropsState(player: Player) {
        val enabled = player.getBoolean(Attribute.MULTIPLY_XP_DROPS)
        player.packetSender.sendString(31660, if (enabled) "@gre@On" else "@red@Off")
        player.message("Your combat XP drops are now " + (if (!enabled) "not " else "") + "multiplied. Your XP gained won't be affected.")
    }

    fun toggleMultiplyXPDropsStateNoMessage(player: Player) {
        player.toggleBoolean(Attribute.MULTIPLY_XP_DROPS)
        val enabled = player.getBoolean(Attribute.MULTIPLY_XP_DROPS)
        player.packetSender.sendString(31660, if (enabled) "@gre@On" else "@red@Off")
    }


    private fun configurePriceCheckerButtons() {
        onClick(OPEN_PRICE_CHECKER) {
            if (player.busy())
                player.removeInterfaces()
            player.priceChecker.open()
            player.timerRepository.cancel(TimerKey.BUTTON_DELAY);
        }
        onClick(PRICE_CHECKER_WITHDRAW_ALL) {
            player.priceChecker.withdrawAll(true)
        }
        onClick(PRICE_CHECKER_DEPOSIT_ALL) {
            player.priceChecker.depositAll()
        }
        onClick(PRICE_CHECKER_SEARCH) {
            player.message("Coming soon.")
        }
    }
}