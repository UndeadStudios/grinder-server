package com.grinder.game.content.`object`

import com.grinder.game.model.ObjectActions
import com.grinder.game.model.item.container.bank.presets.PresetsManager
import com.grinder.util.ObjectID

/**
 * Handles all bank objects in the game.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   20/01/2021
 */
object BankObjectActions {

    private val bankObjectIDs = intArrayOf(

            ObjectID.BANK,

            ObjectID.BANK_BOOTH, ObjectID.BANK_BOOTH_2,
            ObjectID.BANK_BOOTH_3, ObjectID.BANK_BOOTH_4,
            ObjectID.BANK_BOOTH_5, ObjectID.BANK_BOOTH_6,
            ObjectID.BANK_BOOTH_7, ObjectID.BANK_BOOTH_8,
            ObjectID.BANK_BOOTH_9, ObjectID.BANK_BOOTH_10,
            ObjectID.BANK_BOOTH_11, ObjectID.BANK_BOOTH_12,
            ObjectID.BANK_BOOTH_13, ObjectID.BANK_BOOTH_14,
            ObjectID.BANK_BOOTH_15, ObjectID.BANK_BOOTH_16,
            ObjectID.BANK_BOOTH_17, ObjectID.BANK_BOOTH_18,
            ObjectID.BANK_BOOTH_19, ObjectID.BANK_BOOTH_20,
            ObjectID.BANK_BOOTH_21, ObjectID.BANK_BOOTH_22,
            ObjectID.BANK_BOOTH_23, ObjectID.BANK_BOOTH_24,
            ObjectID.BANK_BOOTH_25, ObjectID.BANK_BOOTH_26,
            ObjectID.BANK_BOOTH_27, ObjectID.BANK_BOOTH_28,
            ObjectID.BANK_BOOTH_29, ObjectID.BANK_BOOTH_30,
            ObjectID.BANK_BOOTH_33, ObjectID.BANK_BOOTH_34,
            ObjectID.BANK_BOOTH_35, ObjectID.BANK_BOOTH_36,
            ObjectID.BANK_BOOTH_37, ObjectID.BANK_BOOTH_38,
            ObjectID.BANK_BOOTH_39, ObjectID.BANK_BOOTH_40,
            ObjectID.BANK_BOOTH_41, ObjectID.BANK_BOOTH_42,
            ObjectID.BANK_BOOTH_43, ObjectID.BANK_BOOTH_44,
            ObjectID.BANK_BOOTH_10355,
            ObjectID.TIGHTROPE_4, // Varrock bank booths (W big bank)
            ObjectID.CHEST_89, 34343,
            27291, 32666, 36559,
            ObjectID.BANK_CHEST, ObjectID.BANK_CHEST_2,
            ObjectID.BANK_CHEST_3, ObjectID.BANK_CHEST_4,
            ObjectID.BANK_CHEST_5, ObjectID.BANK_CHEST_6,
            ObjectID.BANK_CHEST_7, ObjectID.BANK_CHEST_8,
            ObjectID.BANK_CHEST_9, ObjectID.BANK_CHEST_10,
            ObjectID.BANK_CHEST_11, ObjectID.BANK_CHEST_12,
            ObjectID.BANK_CHEST_13, ObjectID.BANK_CHEST_14,
            ObjectID.BANK_CHEST_15, ObjectID.BANK_CHEST_16,
            ObjectID.BANK_CHEST_17, ObjectID.BANK_CHEST_18,
            ObjectID.OPEN_CHEST, ObjectID.OPEN_CHEST_2,
            ObjectID.OPEN_CHEST_3,
            ObjectID.OPEN_CHEST_5, ObjectID.OPEN_CHEST_6,
            ObjectID.OPEN_CHEST_7, ObjectID.OPEN_CHEST_8,
            7478,
            27253,
            32666
    )

    init {
        ObjectActions.onClick(*bankObjectIDs) {
            val player = it.player
            if (it.isFirstOption() || it.isSecondOption())
                player.bankpin.openBank()
            else if (it.actionNameContains("Collect")){
                player.sendMessage("You have nothing to available to collect from the ${it.objectName()}.")
            }
            else if (it.isFourthOption()){
                player.bankpin.openBankPresets()
            } else {
                player.sendMessage("Nothing interesting happens.")
            }
            return@onClick true
        }
    }


}