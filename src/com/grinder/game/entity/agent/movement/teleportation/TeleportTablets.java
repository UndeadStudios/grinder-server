package com.grinder.game.entity.agent.movement.teleportation;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import com.grinder.game.content.skill.skillable.impl.magic.Teleporting;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.Position;

public class TeleportTablets {

    /**
     * The amount of milliseconds a player must wait between
     * each teletab-use.
     */
    private static final long TELE_TAB_DELAY = 1800;
    public static boolean init(Player player, int itemId) {
        Optional<TeleportTablet> tab = TeleportTablet.getTab(itemId);

        // Checks if the tab isn't present, if not perform nothing
        if (!tab.isPresent()) {
            return false;
        }

        if (tab.get() == TeleportTablet.MOSLE_HARMLESS) {
            if (!PlayerUtil.isMember(player)) {
                player.sendMessage("You must be a Ruby member to be able to use this teleport.");
                return false;
            }
        }

        if (player.getWildernessLevel() > 20) {
            player.getPacketSender().sendMessage("You can't teleport above level 20 Wilderness", 1000);
            return false;
        }

        //Handle present tab..
		if (player.getInventory().contains(tab.get().getTab())) {
			if (player.getClickDelay().elapsed(TELE_TAB_DELAY)) {

                if (tab.get() == TeleportTablet.ZUL_ANDRA) {
                    TeleportHandler.teleport(player, Teleporting.TeleportLocation.ZULRAH.getPosition(), TeleportType.SCROLL, false, true);
                } else if (!tab.get().getNewTeleportType()) {
                        TeleportHandler.teleport(player, tab.get().getPosition(), TeleportType.TELE_TAB, false, true);
                } else {
                        TeleportHandler.teleport(player, tab.get().getPosition(), TeleportType.TELE_TAB_NEW, false, true);
                    }
					player.getInventory().delete(tab.get().getTab(), 1);
                player.getPoints().increase(AttributeManager.Points.TELETABS_USED, 1); // Increase points
					player.getClickDelay().reset();
				}
			}

        return true;
    }

    /**
     * Teleport Tablet data storage.
     *
     * @author Dennis
     */
    public enum TeleportTablet {

        VARROCK(8007, new Position(3210, 3424, 0), false),
        LUMBRIDGE(8008, new Position(3222, 3218, 0), false),
        FALADOR(8009, new Position(2964, 3378, 0), false),
        CAMELOT(8010, new Position(2757, 3477, 0), false),
        ARDOUGNE(8011, new Position(2662, 3305, 0), false),
        WATCH_TOWER(8012, new Position(2549, 3112, 0), false),
        HOME(8013, new Position(3099, 3503, 0), false),
        RIMMINGTON(11741, new Position(2956, 3223, 0), false),
        TAVERLY(11742, new Position(2896, 3456, 0), false),
        POLLNIVNEACH(11743, new Position(3356, 2966, 0), false),
        RELLEKA(11744, new Position(2669, 3636, 0), false),
        BRIMHAVEN(11745, new Position(2760, 3178, 0), false),
        YANILLE(11746, new Position(2544, 3092, 0), false),
        TROLLHEIM(11747, new Position(2890, 3676, 0), false),
        ANNAKARL(12775, new Position(3288, 3886, 0), false),
        CARRALLANGER(12776, new Position(3156, 3666, 0), false),
        DAREEYAK(12777, new Position(2966, 3695, 0), false),
        GHORROCK(12778, new Position(2977, 3873, 0), false),
        KHARYRLL(12779, new Position(3492, 3471, 0), false),
        LASSAR(12780, new Position(3006, 3471, 0), false),
        PADDEWWA(12781, new Position(3098, 9884, 0), false),
        SENNTISTEN(12782, new Position(3322, 3336, 0), false),
        NARDAH(12402, new Position(3423, 2914, 0), true),
        DIGSITE(12403, new Position(3350, 3344, 0), true),
        FELDIP_HILLS(12404, new Position(2556, 2983, 0), true),
        LUNAR_ISLE(12405, new Position(2107, 3914, 0), true),
        MORTON(12406, new Position(3487, 3284, 0), true),
        PEST_CONTROL(12407, new Position(2662, 2647, 0), true),
        PISCATORIS(12408, new Position(2342, 3692, 0), true),
        TAI_BWO_WANNAI(12409, new Position(2790, 3065, 0), true),
        ELF_CAMP(12410, new Position(2195, 3253, 0), false),
        MOSLE_HARMLESS(12411, new Position(3677, 2976, 0), true),
        LUMBERYARD(12642, new Position(3306, 3483, 0), true),
        ZUL_ANDRA(12938, new Position(2200, 3055, 0), true),
        //KEY_MASTER(13249, new Position()),
        LUMBRIDGE_GRAVEYARD(19613, new Position(3244, 3199, 0), false),
        DRAYNOR_MANOR(19615, new Position(3111, 3326, 0), false),
        MIND_ALTAR(19617, new Position(2784, 4843, 0), false),
        //SALVE_GRAVEYARD(19619, new Position()),
        FENKENSTRAINS_CASTLE(19621, new Position(3549, 3528, 0), false),
        WEST_ARDOUGNE(19623, new Position(2524, 3306, 0), false),
        HARMONY_ISLAND(19625, new Position(3801, 2857, 0), false),
        CEMETARY(19627, new Position(2976, 3750, 0), false),
        BARROWS(19629, new Position(3565, 3306, 0), false),
        APE_ATOLL(19631, new Position(2778, 2786, 0), false),
        PRIFDINNAS(23771, new Position(2239, 3275, 0), false),
        REVENANT_CAVE(21802, new Position(3133, 3833, 0), true),
        KOUREND(19651, new Position(1645, 3667, 0), false);


        /**
         * The {@value #tab_set} storing
         */
        private static Set<TeleportTablet> tab_set = Collections.unmodifiableSet(EnumSet.allOf(TeleportTablet.class));
        /**
         * The {@link Item} id of the teleport tablet.
         */
        private final int tabId;
        /**
         * The specified {@link Position} that the teleport tablet will send the {@link Player} upon interaction.
         */
        private final Position position;
        
        private boolean newTeleportType;

        /**
         * TabData constructor
         *
         * @param tabId
         * @param position
         */
        private TeleportTablet(int tabId, Position position, boolean newTeleportType) {
            this.tabId = tabId;
            this.position = position;
            this.newTeleportType = newTeleportType;
        }

        /**
         * Gets the teleport tablet from the {@value #tab_set} stream.
         *
         * @param tabId
         * @return tabId
         */
        public static Optional<TeleportTablet> getTab(int tabId) {
            return tab_set.stream().filter(tabs -> tabs.getTab() == tabId).findFirst();
        }

        /**
         * Gets the {@link #tabId} and returns as its initial value.
         *
         * @return tabId
         */
        public int getTab() {
            return tabId;
        }

        /**
         * Gets the {@link #tabId} and returns as its initial value.
         *
         * @return position
         */
        public Position getPosition() {
            return position;
        }


        /**
         * Gets the {@link #newTeleportType} and returns as its boolean.
         *
         * @return newTeleportType
         */
        public boolean getNewTeleportType() {
            return newTeleportType;
        }
    }
}
