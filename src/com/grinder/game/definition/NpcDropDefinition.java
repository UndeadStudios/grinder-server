package com.grinder.game.definition;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import com.grinder.game.model.item.Item;
import com.grinder.util.random.RandomGen;

import static com.grinder.util.ItemID.BLOOD_MONEY;

/**
 * Handles drop definitions.
 *
 * @author Professor Oak
 */
public class NpcDropDefinition {

    /**
     * The map containing all our {@link NpcDropDefinition}s.
     */
    public static Map<Integer, NpcDropDefinition> definitions = new HashMap<Integer, NpcDropDefinition>();
    public static Map<String, NpcDropDefinition> names = new HashMap<String, NpcDropDefinition>();
    public static SortedMap<String, NpcDefinition> nameDefinitionAlphabetical = new TreeMap<>();
    /**
     * The npcs which share this {@link NpcDropDefinition}.
     */
    private int[] npcIds;
    /**
     * The chance for this {@link NpcDropDefinition} to hit the
     * rare drop table.
     */
    private int rdtChance;
    /**
     * The possible drop tables for this {@link NpcDropDefinition}.
     */
    private NPCDrop[] alwaysDrops;
    private NPCDrop[] commonDrops;
    private NPCDrop[] uncommonDrops;
    private NPCDrop[] rareDrops;
    private NPCDrop[] veryRareDrops;
    private NPCDrop[] specialDrops;

    /**
     * Gets the {@link NpcDropDefinition} for the specified npc id.
     *
     * @param npcId
     * @return
     */
    public static Optional<NpcDropDefinition> get(int npcId) {
        NpcDropDefinition drop = definitions.get(npcId);
        if (drop != null) {
            return Optional.of(drop);
        }
        return Optional.empty();
    }
    public static Optional<NpcDropDefinition> getName(String name) {
        NpcDropDefinition drop = names.get(name);
        if (drop != null) {
            return Optional.of(drop);
        }
        return Optional.empty();
    }
    
    public static Optional<NpcDropDefinition> getName(int npcId) {
        NpcDropDefinition drop = names.get(npcId);
        if (drop != null) {
            return Optional.of(drop);
        }
        return Optional.empty();
    }
    public int[] getNpcIds() {
        return npcIds;
    }

    public int getRdtChance() {
        return rdtChance;
    }

    public NPCDrop[] getAlwaysDrops() {
        return alwaysDrops;
    }

    public NPCDrop[] getCommonDrops() {
        return commonDrops;
    }

    public NPCDrop[] getUncommonDrops() {
        return uncommonDrops;
    }

    public NPCDrop[] getRareDrops() {
        return rareDrops;
    }

    public NPCDrop[] getVeryRareDrops() {
        return veryRareDrops;
    }

    public NPCDrop[] getSpecialDrops() {
        return specialDrops;
    }

    public NPCDrop[] getAllDrops() {
        int len = (alwaysDrops != null ? alwaysDrops.length : 0)
                + (commonDrops != null ? commonDrops.length : 0)
                + (uncommonDrops != null ? uncommonDrops.length : 0)
                + (rareDrops != null ? rareDrops.length : 0)
                + (veryRareDrops != null ? veryRareDrops.length : 0)
                + (specialDrops != null ? specialDrops.length : 0);

        int index = 0;
        NPCDrop[] drops = new NPCDrop[len];

        if (alwaysDrops != null) for (NPCDrop drop : alwaysDrops) drops[index++] = drop;
        if (commonDrops != null) for (NPCDrop drop : commonDrops) drops[index++] = drop;
        if (uncommonDrops != null) for (NPCDrop drop : uncommonDrops) drops[index++] = drop;
        if (rareDrops != null) for (NPCDrop drop : rareDrops) drops[index++] = drop;
        if (veryRareDrops != null) for (NPCDrop drop : veryRareDrops) drops[index++] = drop;
        if (specialDrops != null) for (NPCDrop drop : specialDrops) drops[index++] = drop;

        return drops;
    }

    /**
     * Represents a drop table and the random
     * required to hit it.
     */
    public enum DropTable {
        COMMON(90),
        UNCOMMON(40),
        RARE(6),
        VERY_RARE(0.6),
        SPECIAL(-1), //Separately handled
        ;

        private final double randomRequired;

        DropTable(double randomRequired) {
            this.randomRequired = randomRequired;
        }

        public double getRandomRequired() {
            return randomRequired;
        }
    }

    public static enum RDT {
        LAW_RUNE(563, 45, 64),
        DEATH_RUNE(560, 45, 64),
        NATURE_RUNE(561, 67, 43),
        //STEEL_ARROW(886, 150, 64),
        RUNE_ARROW(886, 42, 64),
        //UNCUT_SAPPHIRE(1623, 1, 1),
        //UNCUT_EMERALD(1621, 1, 20),
        //UNCUT_RUBY(1619, 1, 20),
        //UNCUT_DIAMOND(1617, 1, 64),
        //DRAGONSTONE(1631, 1, 64),
        RUNITE_BAR(2364, 35, 32),
        //SILVER_ORE(443, 100, 64),
        //COINS(995, 3000000, 32),
        //DRAGON_DART_PSS(11234, 100, 32),
        //DRAGON_KNIVE_PSS(22810, 50, 32),
        BRIMSTONE_KEY(23083, 1, 64),
        //CHAOS_TALISMAN(1452, 1, 64),
        //NATURE_TALISMAN(1462, 1, 20),
        LOOP_HALF_OF_KEY(987, 1, 1),
        //TOOTH_HALF_OF_KEY(985, 1, 1),
        //ADAMANT_JAVELIN(829, 20, 64),
        //RUNE_JAVELIN(830, 5, 33),
        //RUNE_2H_SWORD(1319, 1, 43),
        //RUNE_BATTLEAXE(1373, 1, 43),
        BOLTS_1(9236, 38, 43),
        BOLTS_2(9237, 34, 43),
        BOLTS_3(9238, 31, 43),
        BOLTS_4(9239, 29, 43),
        BOLTS_5(9240, 21, 43),
        BOLTS_6(9241, 22, 43),
        BOLTS_7(9242, 24, 43),
        BOLTS_8(9243, 25, 43),
        BOLTS_9(9244, 23, 43),
        //BM(BLOOD_MONEY, 5000, 32),
        RUNE_KNIFE(868, 30, 43),
        RUNE_DART(811, 32, 43),
        RUNE_SQUARE_SHIELD(1185, 1, 64),
        //ZERKER_HELM(3751, 1, 32),
        //ARCHER_HELM(3749, 1, 32),
        //DRAGON_SCIMITAR_P(15346, 1, 25),
        //DRAGON_SCIMITAR_P_PLUS(15347, 1, 25),
        //MUDDY_KEY(991, 1, 15),
        //MAGIC_SHORTBOW(861, 1, 64),
        //AVA_ACCUMULATOR(10499, 1, 25),
        //BLACK_VAMBS(2503, 1, 25),
        //BLACK_CHAPS(2497, 1, 25),
        //BLACK_D_BODY(2491, 1, 25),
        //D_SCIM(4587, 1, 32),
        RUNE_HELM(1163, 1, 32),
        RUNE_BODY(1127, 1, 32),
        RUNE_LEGS(1079, 1, 32),
        RUNE_KITE(1201, 1, 32),
        RUNE_KITE_SHIELD(1201, 1, 128),
        //DRAGON_MED_HELM(1149, 1, 64),
        RUNE_SPEAR(1247, 1, 137),
        //DDS(5698, 1, 22),
        //RUNE_CROSS_BOW(9185, 1, 24),
        //ANCIENT_STAFF(4675, 1, 24),
        SHIELD_LEFT_HALF(2366, 1, 64);

        private final int itemId;
        private final int amount;
        private final int chance;

        private RDT(int itemId, int amount, int chance) {
            this.itemId = itemId;
            this.amount = amount;
            this.chance = chance;
        }

        public int getItemId() {
            return itemId;
        }

        public int getAmount() {
            return amount;
        }

        public int getChance() {
            return chance;
        }
    }

    public static final class NPCDrop {
        /**
         * The in-game item id of this drop.
         */
        private final int itemId;

        /**
         * The minimum amount of the item that will be dropped.
         */
        private final int minAmount;

        /**
         * The maximum amount of the item that will be dropped.
         */
        private final int maxAmount;

        /**
         * The chance that this item will be dropped.
         */
        private int chance;

        public NPCDrop(int itemId, int minAmount, int maxAmount) {
            this.itemId = itemId;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.chance = -1;
        }

        public NPCDrop(int itemId, int minAmount, int maxAmount, int chance) {
            this.itemId = itemId;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.chance = chance;
        }

        public int getItemId() {
            return itemId;
        }

        public int getMinAmount() {
            return minAmount;
        }

        public int getMaxAmount() {
            return maxAmount;
        }

        public Item toItem(RandomGen random) {
            return new Item(itemId, random.inclusive(minAmount, maxAmount));
        }

        public Item toItem() {
            return new Item(itemId,  maxAmount);
        }
        public int getChance() {
            if (chance >= 512) {
                return chance = 255;
            }
            //} else if (chance >= 128) {
            //    return chance = (int) (chance / 3.25);
            //} else
            return (int) (chance * 1.15);
        }
    }
}
