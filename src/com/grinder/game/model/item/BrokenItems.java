package com.grinder.game.model.item;

import java.util.HashMap;
import java.util.Map;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.util.ItemID;

/**
 * If you die in the Wilderness and you have an item that can turn into a broken item
 * then it will turn into a broken item and stay in your inventory, and as a reward
 * for the killer he will get half the repair cost of the items that broke as a drop.
 * So for example, if you kill someone with Elite void knight top, the player will keep
 * the void knight top as broken upon death, and since it costs 14000 blood money to repair
 * the killer will get 7,000 blood money instead of the elite void knight top that he should've
 * got.
 *
 * P.S: If you die with untradeable item that doesn't have a broken version, it will vanish forever.
 */
public enum BrokenItems {

	/*
	 * Miscellaneous item's
	 */
    BRONZE_DEFENDER_BROKEN(8844, 20449, 750),
    IRON_DEFENDER_BROKEN(8845, 20451, 1110),
    STEEL_DEFENDER_BROKEN(8846, 20453, 1450),
    BLACK_DEFENDER_BROKEN(8847, 20455, 1700),
    MITHRIL_DEFENDER_BROKEN(8848, 20457, 1900),
    ADAMANT_DEFENDER_BROKEN(8849, 20459, 2500),
    RUNE_DEFENDER_BROKEN(8850, 20461, 3500),
    DRAGON_DEFENDER_BROKEN(12954, 20463, 12000),
    AVERNIC_DEFENDER_BROKEN(22322, 22441, 35000),
    ELDER_MAUL(21003, 21205, 38000),
    AVAS_ASSEMBLER(22109, 21914, 9000),
    FIRE_CAPE_BROKEN(6570, 20445, 5000),
    WATER_CAPE_BROKEN(15377, 15541, 7500),
    FIGHTER_TORSO_BROKEN(10551, 20513, 7500),
    HARDENED_TORSO_BROKEN(15394, 15395, 25_000),
    ARMY_TORSO_BROKEN(15396, 15397, 32_500),
    INFERNAL_CAPE(21295, 21287, 25000),
    BARRONITE_MACE(ItemID.BARRONITE_MACE, ItemID.BARRONITE_MACE_BROKEN, 15000),

    IMBUED_SARADOMIN_CAPE(ItemID.IMBUED_SARADOMIN_CAPE, 24236, 25000),
    IMBUED_ZAMORAK_CAPE(ItemID.IMBUED_ZAMORAK_CAPE, 24244, 25000),
    IMBUED_GUTHIX_CAPE(ItemID.IMBUED_GUTHIX_CAPE, 24240, 25000),

    IMBUED_SARADOMIN_MAX_CAPE(21776, 24238, 75000),
    IMBUED_ZAMORAK_MAX_CAPE(21780, 24246, 75000),
    IMBUED_GUTHIX_MAX_CAPE(21784, 24242, 75000),


    /*
    * Superior void equipment
     */
    SUPERIOR_VOID_GLOVES(26467, 15710, 5000),
    SUPERIOR_VOID_MAGE_HELM(26473, 15713, 15000),
    SUPERIOR_VOID_RANGE_HELM(26475, 15714, 15000),
    SUPERIOR_VOID_MELEE_HELM(26477, 15715, 15000),
    SUPERIOR_VOID_KNIGHT_TOP(26469, 15711, 24500),
    SUPERIOR_VOID_KNIGHT_ROBE(26471, 15712, 24500),
      // hiding until cache is finished to replace brokenItem id
//    MAX_CAPE(ItemID.MAX_CAPE_3, ItemID.MAX_CAPE_2, 50000),
//    SARADOMIN_MAX_CAPE(13331, ItemID.MAX_CAPE_2, 52500),
//    ZAMORAK_MAX_CAPE(13333, ItemID.MAX_CAPE_2, 52500),
//    GUTHIX_MAX_CAPE(13335, ItemID.MAX_CAPE_2, 52500),
//    AVAS_MAX_CAPE(13337, ItemID.MAX_CAPE_2, 53500),
//    ARDOUGNE_MAX_CAPE(20760, ItemID.MAX_CAPE_2, 53500),
    INFERNAL_MAX_CAPE(21285, 21289, 75000),
    ASSEMBLER_MAX_CAPE(21898, 21916, 65000),
    FIRE_MAX_CAPE_BROKEN(13329, 20447, 55000),

    /*
    * Hoods
     */
    //MAX_HOOD(ItemID.MAX_HOOD, -1, 25_000),
    // COLORFUL_PARTY_HAT(15193, -1, 250_000),
    //COLORFUL_SANTA_HAT(15194, -1, 250_000),
    //COLORFUL_HWEEN_MASK(15196, -1, 250_000),
    // SARADOMIN_MAX_HOOD(13332, -1, 27500),
    // ASSEMBLER_MAX_HOOD(21900, -1, 30000),
    //FIRE_MAX_HOOD(ItemID.FIRE_MAX_HOOD, -1, 30_000),
    //ZAMORAK_MAX_HOOD(13334, -1, 27500),
    // GUTHIX_MAX_HOOD(13336, -1, 27500),
    // AVAS_MAX_HOOD(13338, -1, 28500),
    //ARDOUGNE_MAX_HOOD(20764, -1, 28500),
    //INFERNAL_MAX_HOOD(21282, -1, 28500),

    COLORFUL_MAX_CAPE(15195, 15197, 350_000),
    COLORFUL_MAX_HOOD(15271, 15439, 200000),
    
    FIGHTER_HAT(10548, 20507, 4500),
    RANGER_HAT(10550, 20509, 1500),
    HEALER_HAT(10547, 20511, 3000),
    PENANCE_SKIRT(10555, 20515, 2500),
    
    /* CASTLE WARS STUFF */
    
    SARADOMIN_HALO(12637, 20537, 3000),
    ZAMORAK_HALO(12638, 20539, 3000),
    GUTHIX_HALO(12639, 20541, 3000),

    ARMADYL_HALO(24192, 24147, 10_000),
    ANCIENT_HALO(24201, 24153, 10_000),
    BANDOS_HALO(24195, 24147, 10_000),
    SEREN_HALO(24198, 24151, 15_000),
    BRASSICA_HALO(24204, 24155, 15_000),
    
    DECORATIVE_SWORD(4508, 20483, 5000),
    DECORATIVE_HELM(4511, 20489, 5000),
    DECORATIVE_BODY(4509, 20485, 5000),
    DECORATIVE_LEGS(4510, 20487, 5000),
    DECORATIVE_SKIRT(11895, 20493, 5000),
    DECORATIVE_SHIELD(4512, 20491, 5000),
    
    DECORATIVE_MAGE_HELM(11898, 20499, 3500),
    DECORATIVE_MAGE_BODY(11896, 20495, 5000),
    DECORATIVE_MAGE_LEGS(11897, 20497, 5000),
    
    DECORATIVE_RANGE_BODY(11899, 20501, 5000),
    DECORATIVE_RANGE_LEGS(11900, 20503, 5000),
    DECORATIVE_RANGE_QUIVER(11901, 20505, 2500),
    /*
     * Void's set
     */
    VOID_KNIGHT_TOP(8839, 20465, 9000),
    VOID_KNIGHT_ROBE(8840, 20469, 8000),
    VOID_KNIGHT_GLOVES(8842, 20475, 5000),
    VOID_KNIGHT_MAGE_HELM(11663, 20477, 7500),
    VOID_KNIGHT_RANGER_HELM(11664, 20479, 7500),
    VOID_KNIGHT_MELEE_HELM(11665, 20481, 7500),
    ELITE_VOID_KNIGHT_TOP(13072, 20467, 14500),
    ELITE_VOID_KNIGHT_BOTTOM(13073, 20471, 12000)
    ;
    //Original item value * this multiplier is the repair cost of all items.
    //Currently 3%
    //private static final double REPAIR_COST_MULTIPLIER = 0.538;
    private static final Map<Integer, BrokenItems> brokenItems = new HashMap<>();

    static {
        for (BrokenItems brokenItem : BrokenItems.values()) {
            brokenItems.put(brokenItem.getOriginalItem(), brokenItem);
        }
    }

    private final int originalItem;
    private final int brokenItem;
    private final int repairCost;
    private final int repairCurrencyId;
    
    BrokenItems(int originalItem, int brokenItem, int repairCost, int repairCurrencyId) {
        this.originalItem = originalItem;
        this.brokenItem = brokenItem;
        this.repairCost = repairCost;
        this.repairCurrencyId = repairCurrencyId;
    }
    
    BrokenItems(int originalItem, int brokenItem, int repairCost) {
        this(originalItem, brokenItem, repairCost, ItemID.COINS);
    }

    BrokenItems(int originalItem, int brokenItem) {
        this(originalItem, brokenItem, -1);
    }

    /**
     * Gets the total cost of repairing a player's stuff.
     */
    public static int getRepairCost(Player player) {
        int cost = 0;
        for (BrokenItems type : BrokenItems.values()) {

            final int repairCost = type.getRepairCost() * 1000;
            final int brokenItemId = type.getBrokenItem();

            if (cannotBeRepaired(brokenItemId) || repairCost == -1)
                continue;

            cost += repairCost * player.getInventory().getAmount(brokenItemId);
        }
        return cost;
    }

    /**
     * Repairs all broken stuff for a player.
     */
    public static void repair(Player player) {

        boolean repaired = false;

        final Inventory inventory = player.getInventory();

        for (BrokenItems type : BrokenItems.values()) {

            final int repairCost = type.getRepairCost() * 1000;
            final int brokenItemId = type.getBrokenItem();

            if (cannotBeRepaired(brokenItemId) || repairCost == -1)
                continue;


            final int amountInInventory = inventory.getAmount(brokenItemId);

            if (amountInInventory > 0) {

                final int currencyId = type.repairCurrencyId;
                int cost = repairCost * amountInInventory;

                if (inventory.getAmount(currencyId) >= cost) {
                    inventory.delete(currencyId, cost);
                    inventory.delete(brokenItemId, amountInInventory);
                    inventory.add(type.getOriginalItem(), amountInInventory);
                    repaired = true;
                    AchievementManager.processFor(AchievementType.BRAND_NEW, player);
                } else {
                    player.sendMessage("You could not afford repairing all your items.");
                    break;
                }
            }
        }

        if (repaired) {
            DialogueManager.start(player, 21);
        } else {
            player.getPacketSender().sendInterfaceRemoval();
        }
    }

    public static BrokenItems get(int originalId) {
        return brokenItems.get(originalId);
    }

    public static boolean breaksOnDeath(int id){
        return brokenItems.containsKey(id);
    }

    public int getOriginalItem() {
        return originalItem;
    }

    public int getBrokenItem() {
        return brokenItem;
    }

    public int getBloodMoneyValue() {
        return repairCost;
    }

    public int getRepairCost(){
        return repairCost;
    }

    private static boolean cannotBeRepaired(int itemId) {
        return itemId <= 0;
    }
}
