package com.grinder.game.content.skill.skillable.impl.hunter.birdhouse;

// unused imports
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

import java.util.*;

/**
 * @author Zach (zach@findzach.com)
 * @since 12/22/2020
 *
 * The list of Seeds we need for our BirdHouse
 */
public enum Seeds {

    // too long of a line, should not extend 112 characters.
    HOPS(10, ItemID.BARLEY_SEED, ItemID.HAMMERSTONE_SEED, ItemID.ASGARNIAN_SEED, ItemID.JUTE_SEED, ItemID.YANILLIAN_SEED, ItemID.KRANDORIAN_SEED, ItemID.WILDBLOOD_SEED),
    HERB(5, ItemID.RANARR_SEED, ItemID.TOADFLAX_SEED, ItemID.IRIT_SEED, ItemID.AVANTOE_SEED, ItemID.KWUARM_SEED, ItemID.SNAPDRAGON_SEED, ItemID.CADANTINE_SEED, ItemID.LANTADYME_SEED, ItemID.DWARF_WEED_SEED, ItemID.TORSTOL_SEED);

    // can be final
    private List<Integer> seedList = new LinkedList<>();

    private final int seedCost;

    Seeds(int seedCost, int... itemIds) {
        this.seedCost = seedCost;
        for (int itemid: itemIds) {
            seedList.add(itemid);
        }
    }

    public int getSeedCost() { return seedCost; }

    public List<Integer> getSeedList() {
        return seedList;
    }

    /**
     * Attempts to find the seed from the itemid
     *
     * // specify param or remove
     * @param itemId
     *
     * // specify return or remove
     * @return
     */
    public static Optional<Seeds> seedType(int itemId) {
        return Arrays.stream(Seeds.values()).filter(seeds -> seeds.seedList.contains(itemId)).findFirst();
    }
}
