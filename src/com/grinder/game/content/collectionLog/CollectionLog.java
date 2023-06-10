package com.grinder.game.content.collectionLog;

import com.grinder.game.content.cluescroll.ClueScrollManager;
import com.grinder.game.content.pvm.MonsterKillTracker;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.item.Item;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;

import java.util.*;

public class CollectionLog {

    private static final int INTERFACE_ID = 64600;
    private static final int NAME_STRING_ID = 64624;
    private static final int ITEM_CONTAINER_SIZE = 210;
    private static final int AMOUNT_OBTAINED_CHILD_ID = 64626;
    private static final int BOSS_TAB_BUTTON_ID = 64604;
    private static final int RAIDS_TAB_BUTTON_ID = 64605;
    private static final int CLUES_TAB_BUTTON_ID = 64606;
    private static final int MINIGAMES_TAB_BUTTON_ID = 64607;
    private static final int OTHER_TAB_BUTTON_ID = 64608;

    private static final int LOG_NAMES_SCROLL_ID = 64846;

    private static final int ITEM_CONTAINER_SCROLL_ID = 64627;

    private static final int COMPLETED_STRING_ID = 64971;

    private CLTabType currentTab = null;

    private CLData currentLog = null;

    private final HashMap<String, List<CLEntry>> logEntries = new HashMap<>();

    private final Player p;

    public CollectionLog(Player p) {
        this.p = p;
    }

    public void createOrUpdateEntry(Player player, String name, Item item) {
        if (!item.getDefinition().isCollectable()) return;
        if (CLData.COLLECTION_LOG_DATA.stream().noneMatch(clData -> clData.getName().equalsIgnoreCase(name) && Arrays.stream(clData.getItemIds()).anyMatch(i -> i == item.getId())))
            return;

        List<CLEntry> entries = logEntries.computeIfAbsent(name, e -> new ArrayList<>());

        Optional<CLEntry> possibleEntry = entries.stream().filter(clEntry -> clEntry.itemId == item.getId()).findAny();

        player.sendMessage("@red@Your collection log entry has been updated.");
        if (possibleEntry.isPresent()) {
            possibleEntry.get().incrementBy(item.getAmount());
        } else {
            CLEntry newEntry = new CLEntry(item.getId());
            newEntry.incrementBy(item.getAmount());
            entries.add(newEntry);
        }

    }

    public void showInterface() {
        currentTab = CLTabType.BOSSES;
        currentLog = CLData.LOG_DATA_BOSSES.get(0);
        sendLogNames();
        sendLog();
        p.getPacketSender().sendScrollbarHeight(LOG_NAMES_SCROLL_ID, Math.max(247, CLData.LOG_DATA_BOSSES.size() * 15))
                .sendScrollbarHeight(ITEM_CONTAINER_SCROLL_ID, Math.max(204, ((currentLog.getItemIds().length / 6) + 1) * 38))
                .sendInterface(INTERFACE_ID);
    }

    public void sendLogNames() {
        List<CLData> logData = CLTabType.getTabData(currentTab);

        int startingStringChildId = 64908;

        p.getPacketSender().clearInterfaceText(startingStringChildId, startingStringChildId + 46)
                .sendString(NAME_STRING_ID, currentLog.getName());

        for (CLData data : logData) {

            p.getPacketSender().sendString(startingStringChildId, getObtainedAmount(data.getName()) == data.getItemIds().length ? "@gre@" + data.getName() : data.getName());
            startingStringChildId++;
        }
    }

    public List<CLEntry> getLogByName(String name) {
        return logEntries.get(name);
    }

    public int getObtainedAmount(String name) {
        List<CLEntry> entries = getLogByName(name);
        if (entries == null) {
            return 0;
        }
//        System.out.println("Returning: + " + entries.size());
//        for(int i = 0; i < entries.size(); i ++) {
//            System.out.println(entries.get(i).itemId);
//        }
        return entries.size();
    }

    public void sendLog() {
        clearItems();

        int itemContainerId = 64628;

        List<CLEntry> log = getLogByName(currentLog.getName());
        //System.out.println();
        p.getPacketSender().sendScrollbarHeight(ITEM_CONTAINER_SCROLL_ID, Math.max(204, ((currentLog.getItemIds().length / 6) + 1) * 38));
        List<Item> temp = new ArrayList<>();
        for (int i = 0; i < currentLog.getItemIds().length; i++) {
            int amount = 0;
            if (log != null) {
                for (CLEntry clEntry : log) {
                    if (currentLog.getItemIds()[i] == clEntry.itemId) {
                        amount = clEntry.itemAmount;
                        //  System.out.println("Have : " + currentLog.getItemIds()[i]);
                        break;
                    }
                }
            }
            temp.add(new Item(currentLog.getItemIds()[i], amount));
        }
        p.getPacketSender().sendInterfaceItems(itemContainerId, temp);
        int obtained = getObtainedAmount(currentLog.getName());
        String color;
        if (obtained == currentLog.getItemIds().length) {
            color = "@gre@";
        } else if (obtained > 0) {
            color = "@yel@";
        } else {
            color = "@red@";
        }

        String obtainedText = " " + color + obtained + "/" + currentLog.getItemIds().length;
        String clueType = currentLog.getName().split("\\s")[0];
        if (currentLog.getTab().equals(CLTabType.CLUES)) {

            int count = 0;
            ClueScrollManager mg = p.getClueScrollManager();
            if (clueType.contains("Easy")) {
                count = mg.easyScrollCount;
            } else if (clueType.contains("Medium")) {
                count = mg.mediumScrollCount;
            } else if (clueType.contains("Hard")) {
                count = mg.hardScrollCount;
            } else if (clueType.contains("Elite")) {
                count = mg.eliteScrollCount;
            } else if (clueType.contains("Global")) {
                count = p.getPoints().get(AttributeManager.Points.FINISHED_CLUE_SCROLLS);
            }
            p.getPacketSender().sendString(COMPLETED_STRING_ID, clueType + " Clues Completed: @whi@" + count);
        } else if (currentLog.getTab().equals(CLTabType.BOSSES)) {
            String name = currentLog.getName();
            //NpcDefinition def = NpcDefinition.forName(name);
            final MonsterKillTracker.KillTrack tracked = MonsterKillTracker.forName(p, name);
            if (name.equals("Vet'ion")) {
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "                       Kill Count: @whi@ " + Misc.format(MonsterKillTracker.forName(p, "Vet'ion Reborn").getAmount()));
            } else if (tracked != null) {
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "                       Kill Count: @whi@ " + Misc.format(tracked.getAmount()));

            } else {
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "                       Kill Count: @whi@ 0");
            }
        } else if (currentLog.getTab().equals(CLTabType.MINIGAMES)) {
            if (clueType.contains("Aquais")) {
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "    Challenges Completed: @whi@ " + Misc.format(p.getPoints().get(AttributeManager.Points.AQUAIS_NEIGE_GAMES_COMPLETED)));
            } else if (clueType.contains("Barrows")) {
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "               Chests Opened: @whi@ " + Misc.format(p.getPoints().get(AttributeManager.Points.BARROWS_CHEST)));
            } else if (clueType.contains("Castle")) {
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "                      Games Won: @whi@ " + Misc.format(p.getPoints().get(AttributeManager.Points.CASTLEWARS_WON_GAMES)));
            } else if (clueType.contains("Fight")) {
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "    Challenges Completed: @whi@ " + Misc.format(p.getPoints().get(AttributeManager.Points.FIGHT_CAVES_COMPLETED)));
            } else if (clueType.contains("Inferno")) {
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "    Challenges Completed: @whi@ " + Misc.format(p.getPoints().get(AttributeManager.Points.FIGHT_CAVES_COMPLETED)));
            } else if (clueType.contains("Pest")) {
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "                      Games Won: @whi@ " + Misc.format(p.getPoints().get(AttributeManager.Points.PEST_CONTROL_WINS)));
            } else if (clueType.contains("Warriors")) {
                int cyclopsKills = MonsterKillTracker.getKillsCount(p, NpcID.CYCLOPS_2137)
                        + MonsterKillTracker.getKillsCount(p, NpcID.CYCLOPS_2138)
                        + MonsterKillTracker.getKillsCount(p, NpcID.CYCLOPS_2139)
                        + MonsterKillTracker.getKillsCount(p, NpcID.CYCLOPS_2140)
                        + MonsterKillTracker.getKillsCount(p, NpcID.CYCLOPS_2141)
                        + MonsterKillTracker.getKillsCount(p, NpcID.CYCLOPS_2142)
                        + MonsterKillTracker.getKillsCount(p, NpcID.CYCLOPS_2463)
                        + MonsterKillTracker.getKillsCount(p, NpcID.CYCLOPS_2464)
                        + MonsterKillTracker.getKillsCount(p, NpcID.CYCLOPS_2465)
                        + MonsterKillTracker.getKillsCount(p, NpcID.CYCLOPS_2466)
                        + MonsterKillTracker.getKillsCount(p, NpcID.CYCLOPS_2467)
                        + MonsterKillTracker.getKillsCount(p, NpcID.CYCLOPS_2468);
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "                       Kill Count: @whi@ " + Misc.format(cyclopsKills));
            } else {
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "");
            }
        } else if (currentLog.getTab().equals(CLTabType.OTHER)) {
            //System.out.println(clueType + " "  + clueType.toString());
            if (clueType.contains("Crystal")) {
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "                Chests Opened: @whi@ " + Misc.format(p.getPoints().get(AttributeManager.Points.CRYSTAL_CHESTS_OPENED)));
            } else if (clueType.contains("Muddy")) {
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "                Chests Opened: @whi@ " + Misc.format(p.getPoints().get(AttributeManager.Points.MUDDY_CHESTS_OPENED)));
            } else if (clueType.contains("Slayer")) {
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "                Slayer Streak: @whi@ " + Misc.format(p.getPoints().get(AttributeManager.Points.SLAYER_STREAK)));
            } else if (clueType.contains("PvP")) {
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "                Boxes Opened: @whi@ " + Misc.format(p.getPoints().get(AttributeManager.Points.PVP_BOXES_COUNT)));
            } else if (clueType.contains("Super")) {
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "                Boxes Opened: @whi@ " + Misc.format(p.getPoints().get(AttributeManager.Points.SUPER_BOXES_COUNT)));
            } else if (clueType.contains("Extreme")) {
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "                Boxes Opened: @whi@ " + Misc.format(p.getPoints().get(AttributeManager.Points.EXTREME_BOXES_COUNT)));
            } else if (clueType.contains("Legendary")) {
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "                Boxes Opened: @whi@ " + Misc.format(p.getPoints().get(AttributeManager.Points.LEGENDARY_BOXES_COUNT)));
            } else if (clueType.contains("Gilded")) {
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "                Boxes Opened: @whi@ " + Misc.format(p.getPoints().get(AttributeManager.Points.GILDED_BOXES_COUNT)));
            } else if (clueType.contains("Sacred")) {
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "                Boxes Opened: @whi@ " + Misc.format(p.getPoints().get(AttributeManager.Points.QUESTION_BOXES_COUNT)));
            } else if (clueType.contains("VIP")) {
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "                Boxes Opened: @whi@ " + Misc.format(p.getPoints().get(AttributeManager.Points.FIFTY_DOLLARS_BOXES_COUNT)));
            } else {
                p.getPacketSender().sendString(COMPLETED_STRING_ID, "");
            }
        } else {
            p.getPacketSender().sendString(COMPLETED_STRING_ID, "");
        }
        p.getPacketSender().sendString(AMOUNT_OBTAINED_CHILD_ID, obtainedText);
    }


    public void clearItems() {
        int startingItemContainerId = 64628;

        for (int i = 0; i < ITEM_CONTAINER_SIZE; i++) {
            if (startingItemContainerId != 64700 && startingItemContainerId != 64773) {
                p.getPacketSender().clearItemOnInterface(startingItemContainerId);
            }
            startingItemContainerId++;
        }
    }

    public boolean handleButton(int btnId) {
        if (btnId >= 64847 && btnId <= 64907) {
            int index = btnId - 64847;

            List<CLData> logs = CLTabType.getTabData(currentTab);

            if (logs.size() > index) {
                Optional<CLData> log = Optional.of(logs.get(index));
                CLData log_ = log.get();
                if (!log_.equals(currentLog)) {
                    displayLog(log_);
                }
            }
            return true;
        } else if (btnId >= BOSS_TAB_BUTTON_ID && btnId <= OTHER_TAB_BUTTON_ID) {
            CLTabType newTab = CLTabType.COLLECTION_LOG_TABS.stream().filter(clTabType -> btnId == clTabType.getButtonId()).findAny().orElse(CLTabType.BOSSES);

            if (newTab.equals(currentTab)) return true;

            currentTab = newTab;

            int scrollSize = 0;

            if (btnId == BOSS_TAB_BUTTON_ID) {
                currentLog = CLData.LOG_DATA_BOSSES.get(0);
                scrollSize = CLData.LOG_DATA_BOSSES.size();
            } else if (btnId == RAIDS_TAB_BUTTON_ID) {
                currentLog = CLData.LOG_DATA_RAIDS.get(0);
                scrollSize = CLData.LOG_DATA_RAIDS.size();
            } else if (btnId == CLUES_TAB_BUTTON_ID) {
                currentLog = CLData.LOG_DATA_CLUES.get(0);
                scrollSize = CLData.LOG_DATA_CLUES.size();
            } else if (btnId == MINIGAMES_TAB_BUTTON_ID) {
                currentLog = CLData.LOG_DATA_MINIGAMES.get(0);
                scrollSize = CLData.LOG_DATA_MINIGAMES.size();
            } else {
                currentLog = CLData.LOG_DATA_OTHER.get(0);
                scrollSize = CLData.LOG_DATA_OTHER.size();
            }

            sendLogNames();
            sendLog();
            p.getPacketSender().sendScrollbarHeight(LOG_NAMES_SCROLL_ID, Math.max(247, scrollSize * 15));

            return true;
        }
        return false;
    }

    public void displayLog(CLData logData) {
        currentLog = logData;
        sendLogNames();
        sendLog();
    }

    public HashMap<String, List<CLEntry>> getLogEntries() {
        return logEntries;
    }

    public static class CLEntry {
        private final int itemId;
        private int itemAmount;

        public int getItemId() {
            return itemId;
        }

        public int getItemAmount() {
            return itemAmount;
        }

        public void incrementBy(int amount) {
            itemAmount += amount;
        }

        CLEntry(int itemId) {
            this.itemId = itemId;
            itemAmount = 0;
        }
    }
}
