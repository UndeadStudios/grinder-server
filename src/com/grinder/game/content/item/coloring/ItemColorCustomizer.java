package com.grinder.game.content.item.coloring;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.util.Misc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ItemColorCustomizer {

    public static final int INTERFACE_ID = 51300;
    public static final int TITLE_TEXT_ID = INTERFACE_ID + 3;
    public static final int COLOR_PANEL_TITLE_TEXT_ID = INTERFACE_ID + 6;
    public static final int SAVE_COLOR_BUTTON_ID = INTERFACE_ID + 12;
    public static final int DISCARD_COLOR_BUTTON_ID = INTERFACE_ID + 13;

    public static final int ONE_COLOR_PICKER_LAYER_ID = INTERFACE_ID + 23;
    public static final int TWO_COLOR_PICKERS_LAYER_ID = INTERFACE_ID + 30;
    public static final int THREE_COLOR_PICKERS_LAYER_ID = INTERFACE_ID + 40;
    public static final int FOUR_COLOR_PICKERS_LAYER_ID = INTERFACE_ID + 52;
    public static final int FIVE_COLOR_PICKERS_LAYER_ID = INTERFACE_ID + 66;
    public static final int SIX_COLOR_PICKERS_LAYER_ID = INTERFACE_ID + 82;

    /**
     * This method ensure the {@link Player#updateColorfulItem} flag is set upon a player logging in.
     *
     * @param player the {@link Player} logging in.
     */
    public static void onLogin(Player player){
        player.updateEquippedItemColors();
    }

    public static void openInterface(Player player, int itemId, boolean message) {
        ColorfulItem colorfulItem = ColorfulItem.forId(itemId);

        if (colorfulItem == ColorfulItem.MAX_HOOD) // fail-safe, client-side customize button is disabled
            return;

        Integer[] colors = colorfulItem.getColorsFor(player);

        player.getPacketSender().sendString(TITLE_TEXT_ID, Misc.formatText(colorfulItem.getName()) + " Color Customizer");
        player.getPacketSender().sendString(COLOR_PANEL_TITLE_TEXT_ID, colors.length > 1 ? "Colors" : "Color");
        for (int i = 0; i < colors.length; i++) {
            int boxId = ((i + 1) * 2) + getColorPickerLayerId(colorfulItem);
            player.getPacketSender().sendStringColour(boxId, colors[i]);
        }

        player.getPacketSender().sendInterface(INTERFACE_ID);

        if (message && !colorfulItem.useItemModel())
            player.getPacketSender().sendMessage("@red@It is advisable equip and customize the item to see live preview.", 1000);
    }

    public static void saveColor(Player player, int itemId, Integer[] colors) {

        final ColorfulItem colorfulItem = ColorfulItem.forId(itemId);

        colorfulItem.updateColorsFor(player, colors);

        player.updateEquippedItemColors();

        final String successfullySavedMessage = "Your " +
                (colors.length > 1 ? "colors " : "color ") +
                "for the item " +
                "'colorful " + colorfulItem.getName() + "' " +
                (colors.length > 1 ? "have " : "has ") +
                "been saved.";
        player.getPacketSender().sendMessage(successfullySavedMessage);
    }

    public static int getColorPickerLayerId(ColorfulItem colorfulItem) {
        int id;
        int colorPickersAmt = colorfulItem.getColorNames().length;
        switch (colorPickersAmt) {
            // interfaces for 2-4 pickers have not been made yet
            /*case 2:
                id = TWO_COLOR_PICKERS_LAYER_ID;
                break;
            case 3:
                id = THREE_COLOR_PICKERS_LAYER_ID;
                break;
            case 4:
                id = FOUR_COLOR_PICKERS_LAYER_ID;
                break;*/
            case 5:
                id = FIVE_COLOR_PICKERS_LAYER_ID;
                break;
            case 6:
                id = SIX_COLOR_PICKERS_LAYER_ID;
                break;
            default:
                id = ONE_COLOR_PICKER_LAYER_ID;
                break;

        }
        return id;
    }

    public enum ColorfulItem {
        HWEEN_MASK("h'ween mask", 15196, 1053, new int[] { 926 }, true),
        PARTYHAT("partyhat", 15193, 1044, new int[] { 926 }, true),
        SANTA_HAT("santa hat", 15194, 1050, new int[] { 933 }, true),
        SCARF("gnome scarf", 15300, 9470, new int[] { 119, 103, 127 },true),
        MAX_CAPE("max cape", 15195, 13280, new int[] { 253, 254, 255, 5683, 784, 673, 675, 815, 5708, 668, 4300, 5458, 4316, 902, 5714, 945, 5706, 4820, 972, 5437, 522 },
                new String[] { "Primary", "Secondary", "Tertiary", "Trim", "Rubies" }, true),
        MAX_HOOD("max hood", 15271, 13281, new int[] { 784, 945, 914, 5458, 675, 4820, 972, 685, 815 },
                new String[0], true),
        ANGELIC_CAPE("angelic cape", 15901, 15900, new int[] { 10320, 32450, 10338, 52, 117, 119, 42, 126, 1087 },
                new String[] { "Cape color", "Cape trim", "Dollar sign", "Inner cape", "Wings color" }, true)
        ;

        public Integer[] getColorsFor(Player player) {
            return player.getColorfulItemMap().get(this);
        }

        public static Integer[][] getAllColorsFor(Player player){

            Integer[][] array = new Integer[values().length][];
            int i = 0;
            for(ColorfulItem item : values()){
                array[i++] = item.getColorsFor(player);
            }
            return array;
        }

        public void updateColorsFor(Player player, Integer[] colors) {
            player.getColorfulItemMap().put(this, colors);
        }

        private int itemId, copyId;
        private int[] colorsToReplace; // colors to find
        private String[] colorNames;
        private String name;
        private boolean useItemModel; // whether to display a character model (false) or item model (true) in the interface preview

        public int getItemId() {
            return itemId;
        }

        public int getCopyId() {
            return copyId;
        }

        public int[] getColorsToReplace() {
            return colorsToReplace;
        }

        public String[] getColorNames() {
            return colorNames;
        }

        public String getName() {
            return name;
        }

        public boolean useItemModel() { return useItemModel; }

        ColorfulItem(String name, int itemId, int copyId, int[] colorsToReplace, boolean useItemModel) {
            this.name = name;
            this.itemId = itemId;
            this.copyId = copyId;
            this.colorsToReplace = colorsToReplace;
            this.colorNames = new String[] { "" };
            this.useItemModel = useItemModel;
        }

        ColorfulItem(String name, int itemId, int copyId, int[] colorsToReplace, String[] colorNames, boolean useItemModel) {
            this.name = name;
            this.itemId = itemId;
            this.copyId = copyId;
            this.colorsToReplace = colorsToReplace;
            this.colorNames = colorNames;
            this.useItemModel = useItemModel;
        }

        private static Map<Integer, ColorfulItem> itemIds = new HashMap<>();

        public static Map<Integer, ColorfulItem> getItemIds() {
            return itemIds;
        }

        public static ColorfulItem forId(int itemId) {
            return getItemIds().get(itemId);
        }

        private static Map<String, ColorfulItem> names = new HashMap<>();

        public static Map<String, ColorfulItem> getNames() {
            return names;
        }

        public static ColorfulItem forName(String name) {
            return getNames().get(name);
        }

        private static Map<Integer, ColorfulItem> copyItemIds = new HashMap<>();

        public static Map<Integer, ColorfulItem> getCopyItemIds() {
            return copyItemIds;
        }

        public static ColorfulItem forCopyId(int itemId) {
            return getCopyItemIds().get(itemId);
        }

        /**
         * A map that fills all items' color values to white
         */
        public static Map<ColorfulItem, Integer[]> getDefaultColorfulItemMap() {
            Map<ColorfulItem, Integer[]> map = new HashMap<>();
            Arrays.stream(values()).forEach(colorfulItem -> {
                Integer[] colors = new Integer[colorfulItem.getColorNames().length];
                Arrays.fill(colors, 0xffffff);
                map.put(colorfulItem, colors);
            });
            return map;
        }

        static {
            Arrays.stream(values()).forEach(colorfulItem -> {
                itemIds.put(colorfulItem.getItemId(), colorfulItem);
                names.put(colorfulItem.getName(), colorfulItem);
                copyItemIds.put(colorfulItem.getCopyId(), colorfulItem);
            });
        }
    }

}
