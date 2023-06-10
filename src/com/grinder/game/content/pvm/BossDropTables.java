package com.grinder.game.content.pvm;

import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.definition.NpcDropDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;
import org.apache.commons.lang.WordUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BossDropTables {

    private static final int INTERFACE_ID = 81200;
    private static final int SELECT_A_BOSS_TEXT_ID = INTERFACE_ID + 8;
    private static final int NAME_ID = INTERFACE_ID + 10;
    private static final int COMBAT_ID = INTERFACE_ID + 19;
    private static final int HITPOINTS_ID = INTERFACE_ID + 20;
    private static final int MAX_HIT_ID = INTERFACE_ID + 21;
    private static final int SLAYER_LEVEL_ID = INTERFACE_ID + 22;
    private static final int KILL_COUNT_ID = INTERFACE_ID + 23;
    private static final int FASTEST_TELEPORT_ID = INTERFACE_ID + 24;
    public static final int CHECK_DROP_RATE_TEXT_ID = INTERFACE_ID + 25;
    public static final int ITEM_CONTAINER_ID = INTERFACE_ID + 30;
    private static final int LIST_SCROLL_ID = INTERFACE_ID + 31;
    private static final int LIST_LINE_START_ID = INTERFACE_ID + 32;

    public static void openInterface(Player player) {
        for (Boss boss : Boss.values()) {
            player.getPacketSender().sendString(LIST_LINE_START_ID + boss.ordinal() * 2, boss.toString());
        }
        player.getPacketSender().sendScrollbarHeight(LIST_SCROLL_ID, Boss.values().length * 20);
        player.getPacketSender().sendInterfaceScrollReset(LIST_SCROLL_ID);

        showComponents(player, false);
        player.getPacketSender().sendInterface(INTERFACE_ID);
    }

    private static void sendBossData(Player player, NpcDefinition boss) {
        player.getPacketSender().sendString(NAME_ID, WordUtils.capitalizeFully(boss.getName()));
        player.getPacketSender().sendString(COMBAT_ID, String.valueOf(boss.getCombatLevel()));
        player.getPacketSender().sendString(HITPOINTS_ID, String.valueOf(boss.getHitpoints()));
        player.getPacketSender().sendString(MAX_HIT_ID, String.valueOf(boss.getMaxHit()));
        int slayerLvl = boss.getSlayerLevel();
        player.getPacketSender().sendString(SLAYER_LEVEL_ID, slayerLvl > 0 ? String.valueOf(slayerLvl) : "None");
        int killCount = player.getKillTracker().getKillsCount(player, boss.getId());
        player.getPacketSender().sendString(KILL_COUNT_ID, killCount > 0 ? String.valueOf(killCount) : "0");
        player.getPacketSender().sendString(FASTEST_TELEPORT_ID, Boss.getBossTeleports().get(boss.getId()));
        player.getPacketSender().sendString(CHECK_DROP_RATE_TEXT_ID, "Click on an item to check its drop rate.");

        List<Item> items = new ArrayList<>();
        Optional<NpcDropDefinition> drops = NpcDropDefinition.get(boss.getId());
        if (!drops.isPresent()) return;
        for (NpcDropDefinition.NPCDrop drop : drops.get().getAllDrops()) {
            items.add(drop.toItem());
        }
        player.getPacketSender().sendInterfaceItems(ITEM_CONTAINER_ID, items);
        player.getPacketSender().sendScrollbarHeight(ITEM_CONTAINER_ID - 1, Misc.getContainerScrollMax(items.size(), 7, 3, 5, 144, true));
        player.getPacketSender().sendInterfaceScrollReset(ITEM_CONTAINER_ID - 1);

        player.getNpcInfo().setDropDefinition(drops.get());

        showComponents(player, true);
    }

    private static void showComponents(Player player, boolean bossSelected) {
        player.getPacketSender().sendInterfaceDisplayState(ITEM_CONTAINER_ID - 4, !bossSelected); // box
        player.getPacketSender().sendInterfaceDisplayState(ITEM_CONTAINER_ID - 3, !bossSelected); // box
        player.getPacketSender().sendInterfaceDisplayState(ITEM_CONTAINER_ID - 2, !bossSelected); // box
        player.getPacketSender().sendInterfaceDisplayState(ITEM_CONTAINER_ID - 1, !bossSelected); // scroll
        player.getPacketSender().sendInterfaceDisplayState(SELECT_A_BOSS_TEXT_ID, bossSelected);
        player.getPacketSender().sendInterfaceDisplayState(COMBAT_ID - 6 - 2, !bossSelected); // vertical line
        player.getPacketSender().sendInterfaceDisplayState(COMBAT_ID - 6 - 1, !bossSelected); // vertical line
        player.getPacketSender().sendInterfaceDisplayState(COMBAT_ID - 6, !bossSelected);
        player.getPacketSender().sendInterfaceDisplayState(HITPOINTS_ID - 6, !bossSelected);
        player.getPacketSender().sendInterfaceDisplayState(MAX_HIT_ID - 6, !bossSelected);
        player.getPacketSender().sendInterfaceDisplayState(SLAYER_LEVEL_ID - 6, !bossSelected);
        player.getPacketSender().sendInterfaceDisplayState(KILL_COUNT_ID - 6, !bossSelected);
        player.getPacketSender().sendInterfaceDisplayState(FASTEST_TELEPORT_ID - 6, !bossSelected);
        player.getPacketSender().sendInterfaceDisplayState(NAME_ID, !bossSelected);
        player.getPacketSender().sendInterfaceDisplayState(COMBAT_ID, !bossSelected);
        player.getPacketSender().sendInterfaceDisplayState(HITPOINTS_ID, !bossSelected);
        player.getPacketSender().sendInterfaceDisplayState(MAX_HIT_ID, !bossSelected);
        player.getPacketSender().sendInterfaceDisplayState(SLAYER_LEVEL_ID, !bossSelected);
        player.getPacketSender().sendInterfaceDisplayState(KILL_COUNT_ID, !bossSelected);
        player.getPacketSender().sendInterfaceDisplayState(FASTEST_TELEPORT_ID, !bossSelected);
        player.getPacketSender().sendInterfaceDisplayState(CHECK_DROP_RATE_TEXT_ID, !bossSelected);
    }

    public static boolean handleButton(Player player, int button) {
        if (player.getInterfaceId() == INTERFACE_ID && button >= LIST_LINE_START_ID && button <= LIST_LINE_START_ID + 100) {
            if ((button - LIST_LINE_START_ID) / 2 >= Boss.values().length) {
                return false;
            }
            sendBossData(player, NpcDefinition.forId(Boss.values()[(button - LIST_LINE_START_ID) / 2].getNpcId()));
            return true;
        }
        return false;
    }

    enum Boss {
        /**
         * Keep bosses in alphabetical order
         */


        // Abyssal sire
        ALCHEMICAL_HYDRA(NpcID.ALCHEMICAL_HYDRA, "Spellbook"),
        BARRELCHEST(NpcID.BARRELCHEST, "Spellbook"),
        BLACK_KNIGHT_TITAN(NpcID.BLACK_KNIGHT_TITAN, "Max cape / Sceptre"),
        BRYOPHYTA(NpcID.BRYOPHYTA, "Mossy key"),
        CALLISTO(NpcID.CALLISTO, "Spellbook"),
        CERBERUS(NpcID.CERBERUS, "Spellbook"),
        CHAOS_ELEMENTAL(NpcID.CHAOS_ELEMENTAL, "Spellbook"),
        CHAOS_FANATIC(NpcID.CHAOS_FANATIC, "Spellbook"),
        COMMANDER_ZILYANA(NpcID.COMMANDER_ZILYANA, "Spellbook"),
        CORPOREAL_BEAST(NpcID.CORPOREAL_BEAST, "Spellbook"),
        CRAZY_ARCHAEOLOGIST(NpcID.CRAZY_ARCHAEOLOGIST, "Spellbook"),
        CHRONOZON(NpcID.CHRONOZON, "Spellbook"),
        DAGANNOTH_PRIME(NpcID.DAGANNOTH_PRIME, "Max cape"),
        DAGANNOTH_REX(NpcID.DAGANNOTH_REX, "Max cape"),
        DAGANNOTH_SUPREME(NpcID.DAGANNOTH_SUPREME, "Max cape"),
        // Fragment of seren
        GALVEK(NpcID.GALVEK, "Spellbook"),
        GENERAL_GRAARDOR(NpcID.GENERAL_GRAARDOR, "Spellbook"),
        GIANT_MOLE(NpcID.GIANT_MOLE, "Spellbook"),
        GIANT_SEA_SNAKE(NpcID.GIANT_SEA_SNAKE, "Spellbook"),
        GLOD(NpcID.GLOD, "Spellbook"),
        ICE_QUEEN(NpcID.ICE_QUEEN, "Skull sceptre"),
        ICE_TROLL_KING(NpcID.ICE_TROLL_KING, "Spellbook"),
        JUNGLE_DEMON(NpcID.JUNGLE_DEMON, "Spellbook"),
        KALPHITE_QUEEN(NpcID.KALPHITE_QUEEN_6500, "Spellbook"),
        KAMIL(NpcID.KAMIL, "Max cape / Sceptre"),
        KING_BLACK_DRAGON(NpcID.KING_BLACK_DRAGON, "Spellbook"),
        // Kraken
        KREE_ARRA(NpcID.KREEARRA, "Spellbook"),
        KRIL_TSUTSAROTH(NpcID.KRIL_TSUTSAROTH, "Spellbook"),
        MERODACH(NpcID.MERODACH, "Spellbook"),
        MUTANT_TARN(NpcID.MUTANT_TARN, "Max cape"),
        NEX(NpcID.NEX, "Spellbook"),
        // Sarachnis
        SCORPIA(NpcID.SCORPIA, "Spellbook"),
        SEA_TROLL_QUEEN(NpcID.SEA_TROLL_QUEEN, "Edge sailor"),
        GREATER_SKELETON_HELLHOUND(NpcID.GREATER_SKELETON_HELLHOUND, "Spellbook"),
        SLASH_BASH(NpcID.SLASH_BASH, "Spellbook"),
        THE_MIMIC(NpcID.THE_MIMIC_8633, "Halloween Event"),
        //THE_NIGHTMARE(NpcID.THE_NIGHTMARE_9425, "Spellbook"),
        THE_UNTOUCHABLE(NpcID.THE_UNTOUCHABLE, "Skull sceptre"),
        THERMONUCLEAR_DEVIL(NpcID.THERMONUCLEAR_SMOKE_DEVIL, "Spellbook"),
        VENENATIS(NpcID.VENENATIS, "Spellbook"),
        VETION(NpcID.VETION, "Spellbook"),
        VORKATH(NpcID.VORKATH_8061, "Spellbook"),
        OBOR(NpcID.OBOR, "Giant key"),
        PORAZDIR(NpcID.PORAZDIR, "Spellbook"),
        ZULRAH(NpcID.ZULRAH, "Spellbook / Teletab");

        private int npcId;
        private String teleport;

        Boss(int npcId, String teleport) {
            this.npcId = npcId;
            this.teleport = teleport;
        }

        @Override
        public String toString() {
            return WordUtils.capitalizeFully(name().replace("_", " "));
        }

        public int getNpcId() {
            return npcId;
        }

        public String getTeleport() {
            return teleport;
        }

        public static Map<Integer, String> bossTeleports = new HashMap<>();

        public static Map<Integer, String> getBossTeleports() {
            return bossTeleports;
        }

        static {
            for (Boss boss : Boss.values()) bossTeleports.put(boss.getNpcId(), boss.getTeleport());
        }
    }

}
