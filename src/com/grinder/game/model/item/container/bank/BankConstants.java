package com.grinder.game.model.item.container.bank;

import com.grinder.game.GameConstants;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-23
 */
public class BankConstants {

    public static final int INTERFACE_ID = 5292;

    public static final int TOTAL_BANK_TABS = 10;

    public static final int CONTAINER_START = 50050;

    /**
     * The scroll bar in the bank - interface id
     */
    public static final int BANK_SCROLL_BAR_INTERFACE_ID = 5385;

    /**
     * The bank tabs interface id, used when switching an items' tab.
     */
    public static final int BANK_TAB_INTERFACE_ID = 5383;

    /**
     * The bank inventory interface id.
     */
    public static final int INVENTORY_INTERFACE_ID = 5064;
    
    /**
     * The deposit box interface id.
     */
    public static final int DEPOSIT_BOX_INTERFACE_ID = 4465;
    
    /**
     * The deposit box item container id.
     */
    public static final int DEPOSIT_BOX_ITEM_CONTAINER_ID = 7423;

    static final String BANK_TITLE = "The Bank of " + GameConstants.NAME;

    static final String[] BANK_PIN_INSTRUCTIONS = { "First click the FIRST digit.", "Now click the SECOND digit.", "Time for the THIRD digit.", "Finally, the FOURTH digit." };

    static final int[] TEXT_DIGIT_NUMBERS = { 14883, 14884, 14885, 14886, 14887, 14888, 14889, 14890, 14891, 14892 };

    static final int[] TEXT_PROGRESS_TRACKERS = { 14913, 14914, 14915, 14916 };

    static final long PIN_ENTER_DELAY = 600;

    static final int PIN_RESET_DELAY = 48;

    static final int BANK_PIN_INTERFACE = 7424;

    static final int TEXT_TITLE = 14923;

    static final int TEXT_REMOVE_PIN = 14921;

    static final int TEXT_CURRENT_STEP = 15313;

}
