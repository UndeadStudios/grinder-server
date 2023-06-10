package com.grinder.game.content.pvm;

import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.definition.NpcDropDefinition;
import com.grinder.game.definition.NpcDropDefinition.NPCDrop;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;
import com.grinder.game.service.search.droptable.SearchDropTableWorker;
import com.grinder.net.packet.PacketSender;
import com.grinder.util.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;

public class ItemDropFinderInterface {

    private static final int INTERFACE_ID = 81600;
    public static final int INPUT_ID = INTERFACE_ID + 7;
    private static final int SEARCH_NPC_BUTTON_ID = INTERFACE_ID + 8;
    private static final int SEARCH_ITEM_BUTTON_ID = INTERFACE_ID + 9;
    private static final int SELECT_NPC_TEXT_ID = INTERFACE_ID + 10;
    public static final int ENTER_INPUT_TEXT_ID = INTERFACE_ID + 11;
    public static final int LIST_SCROLL_ID = INTERFACE_ID + 12;
    public static final int LIST_START_ID = INTERFACE_ID + 13;
    private static final int NPC_LAYER_ID = INTERFACE_ID + 213;
    private static final int NPC_NAME_ID = INTERFACE_ID + 214;
    private static final int ITEM_CONTAINER_ID = INTERFACE_ID + 219;
    private static final int ITEM_RECTANGLE_START_ID = INTERFACE_ID + 220;
    private static final int ITEM_CHANCE_TEXT_START_ID = INTERFACE_ID + 221;

    private static void clearLeftSide(Player player) {
        player.setLastDropTableInputText("");
        PacketSender packetSender = player.getPacketSender();
        packetSender.sendString(INPUT_ID, "");
        packetSender.sendString(ENTER_INPUT_TEXT_ID, "Enter a search input.");
        packetSender.sendInterfaceDisplayState(ENTER_INPUT_TEXT_ID, false);
        packetSender.sendInterfaceDisplayState(LIST_SCROLL_ID, true);
        packetSender.clearInterfaceText(LIST_START_ID, LIST_START_ID + 200);
        packetSender.sendInterfaceScrollReset(LIST_SCROLL_ID);
    }

    private static void clearRightSide(Player player) {
        PacketSender packetSender = player.getPacketSender();
        packetSender.sendInterfaceDisplayState(SELECT_NPC_TEXT_ID, false);
        packetSender.sendInterfaceDisplayState(NPC_LAYER_ID, true);
        packetSender.sendString(NPC_NAME_ID, "");
        packetSender.clearInterfaceText(ITEM_CHANCE_TEXT_START_ID, ITEM_CHANCE_TEXT_START_ID + 200);
    }

    public static void openInterface(Player player) {

        final PacketSender packetSender = player.getPacketSender();

        /*
         * Reset left panel
         */
        clearLeftSide(player);

        /*
         * Reset right panel
         */
        clearRightSide(player);

        packetSender.sendInterface(INTERFACE_ID);
    }

    public static void sendList(Player player, String input) {
        if (player.getInterfaceId() != INTERFACE_ID)
            return;

        if (input == null) {
            return;
        }

        final PacketSender packetSender = player.getPacketSender();

        List<NpcDefinition> defs = generateList(input, player.isDropTablesItemSearch());

        /*
         * Shows or hides "Enter a search input." and the scroll bar list
         */
        boolean hideText = !input.isEmpty() && !defs.isEmpty();
        if (!hideText)
            packetSender.sendString(ENTER_INPUT_TEXT_ID, input.isEmpty() ? "Enter a search input." : "No results found.");
        packetSender.sendInterfaceDisplayState(ENTER_INPUT_TEXT_ID, hideText);
        packetSender.sendInterfaceDisplayState(LIST_SCROLL_ID, !hideText);

        /*
         * The list of npc names
         */
        packetSender.clearInterfaceText(LIST_START_ID, LIST_START_ID + 200);
        int index = 0;
        for (NpcDefinition def : defs) {
            packetSender.sendString(LIST_START_ID + index * 2, Misc.formatText(def.getName()));
            packetSender.sendInterfaceDisplayState(LIST_START_ID + index++ * 2 + 1, false);
        }

        packetSender.sendInterfaceScrollReset(LIST_SCROLL_ID);
    }

    public static List<NpcDefinition> generateList(String input, boolean item) {
        List<NpcDefinition> defs = new ArrayList<>();

        if (input == null || input.isEmpty())
            return defs;

        input = input.toLowerCase();

        /*
         * Search npc definitions
         */
        for (SortedMap.Entry<String, NpcDefinition> entry : NpcDropDefinition.nameDefinitionAlphabetical.entrySet()) {
            String name = entry.getKey();
            NpcDefinition def = entry.getValue();


            if (item) {
                /*
                 * Search drop table for item
                 */
                Optional<NpcDropDefinition> dropDef = NpcDropDefinition.get(def.getId());
                if (dropDef.isPresent()) {
                    for (NpcDropDefinition.NPCDrop drop : dropDef.get().getAllDrops()) {
                        if (ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains(input)) {
                            defs.add(def);
                            break;
                        }
                    }
                }
            } else {
                if (name.contains(input)) {
                    defs.add(def);
                }
            }

            if (defs.size() == 100) {
                break;
            }
        }
        return defs;
    }

    public static boolean handleButton(Player player, int button) {
        if (player.getInterfaceId() == INTERFACE_ID) {
            if (button >= LIST_START_ID && button <= LIST_START_ID + 200) {
                final List<SearchDropTableWorker.NpcDropTable> searchResults = player.getDropTableSearchResults();

                int index = (button - LIST_START_ID) / 2;
                if (index >= searchResults.size())
                    return true;
                sendNpc(player, searchResults.get(index));
                return true;
            } else if (button == SEARCH_NPC_BUTTON_ID) {
                player.setDropTablesItemSearch(false);
                clearLeftSide(player);
                clearRightSide(player);
                sendList(player, player.getLastDropTableInputText());
            } else if (button == SEARCH_ITEM_BUTTON_ID) {
                player.setDropTablesItemSearch(true);
                clearLeftSide(player);
                clearRightSide(player);
                sendList(player, player.getLastDropTableInputText());
            }
        }
        return false;
    }
    private static void sendNpc(Player player, SearchDropTableWorker.NpcDropTable def) {

        final PacketSender packetSender = player.getPacketSender();

        packetSender.sendInterfaceDisplayState(SELECT_NPC_TEXT_ID, true);
        packetSender.sendInterfaceDisplayState(NPC_LAYER_ID, false);
        packetSender.sendString(NPC_NAME_ID, Misc.formatText(def.getNpcName()));
        packetSender.clearInterfaceText(ITEM_CHANCE_TEXT_START_ID, ITEM_CHANCE_TEXT_START_ID + 200);

        final List<Item> items = new ArrayList<>();
        final NpcDropDefinition dropsDefinition = def.getDropDefinition();

        int index = 0;

        final NPCDrop[][] dropArrays = new NPCDrop[][] {
                dropsDefinition.getAlwaysDrops(),
                dropsDefinition.getCommonDrops(),
                dropsDefinition.getUncommonDrops(),
                dropsDefinition.getRareDrops(),
                dropsDefinition.getVeryRareDrops(),
                dropsDefinition.getSpecialDrops()
        };

        for (NPCDrop[] dropArray : dropArrays) {
            if (dropArray != null) {

                for (NPCDrop drop : dropArray) {

                    items.add(drop.toItem());

                    if (player.isDropTablesItemSearch() && ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains(player.getLastDropTableInputText().toLowerCase()))
                        packetSender.sendInterfaceDisplayState(ITEM_RECTANGLE_START_ID + index * 2, false);

                    final String text;

                    if (dropArray == dropsDefinition.getCommonDrops())
                        text = "@gre@Common\\n@gre@1/" + (drop.getChance() == 0 ? "5" : drop.getChance());
                    else if (dropArray == dropsDefinition.getUncommonDrops())
                        text = "@yel@Uncommon\\n@yel@1/" + (drop.getChance() == 0 ? "15" : drop.getChance());
                    else if (dropArray == dropsDefinition.getRareDrops())
                        text = "@or2@Rare\\n@or2@1/" + (drop.getChance() == 0 ? "30" : drop.getChance());
                    else if (dropArray == dropsDefinition.getVeryRareDrops())
                        text = "@or3@Very Rare\\n@or3@1/" + (drop.getChance() == 0 ? "50" : drop.getChance());
                    else if (dropArray == dropsDefinition.getSpecialDrops())
                        text = "@red@Special\\n@red@1/" + (int) (drop.getChance() / 1.5);
                    else
                        text = "@gre@Always\\n@gre@1/1";

                    packetSender.sendString(ITEM_CHANCE_TEXT_START_ID + index * 2, text);
                    index++;
                }
            }
        }

        packetSender.sendInterfaceItems(ITEM_CONTAINER_ID, items);
        packetSender.sendInterfaceScrollReset(ITEM_CONTAINER_ID - 1);
    }

}
