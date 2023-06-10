package com.grinder.game.content.miscellaneous.pickables;

import com.grinder.util.ItemID;
import com.grinder.util.ObjectID;

/**
 * @author L E G E N D
 * @date 2/17/2021
 * @time 5:08 AM
 * @discord L E G E N D#4380
 */
public enum PickableType {
    FLAX(ItemID.FLAX, 100, 5, "You pick some flax.", "You can't carry any more flax.", ObjectID.FLAX, ObjectID.FLAX_2, ObjectID.FLAX_3, ObjectID.FLAX_4, ObjectID.FLAX_5, 14909),
    ONION(ItemID.ONION, 20, 0, "You pick an onion", "You don't have room for this onion.", ObjectID.ONION),
    WHEAT(ItemID.GRAIN, 50, 0, "You pick some grain.", "You can't carry any more grain.", ObjectID.WHEAT_5,ObjectID.WHEAT_6,ObjectID.WHEAT_7),
    POTATO(ItemID.POTATO, 50, 0, "You pick a potato.", "You don't have room for this potato.", ObjectID.POTATO),
    CABBAGE(ItemID.CABBAGE, 50, 0, "You pick a cabbage.", "You don't have room for this cabbage.", ObjectID.CABBAGE);

    private final int[] objectIds;
    private final int itemId;
    private final int respawnTime;
    private final int chanceOfDeSpawn;
    private final String message;
    private final String fullInventoryMessage;

    PickableType(int itemId, int respawnTime, int chanceOfDeSpawn, String message, String fullInventoryMessage, int... objectIds) {
        this.objectIds = objectIds;
        this.itemId = itemId;
        this.respawnTime = respawnTime;
        this.chanceOfDeSpawn = chanceOfDeSpawn;
        this.message = message;
        this.fullInventoryMessage = fullInventoryMessage;
    }

    public int[] getObjectIds() {
        return objectIds;
    }

    public int getItemId() {
        return itemId;
    }

    public int getRespawnTime() {
        return respawnTime;
    }

    public int getChanceOfDeSpawn() {
        return chanceOfDeSpawn;
    }

    public String getFullInventoryMessage() {
        return fullInventoryMessage;
    }

    public String getMessage() {
        return message;
    }
}
