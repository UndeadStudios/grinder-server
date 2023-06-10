package com.grinder.game.content.skill.skillable.impl.fletching;

import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

import java.util.HashMap;
import java.util.Map;

import static com.grinder.game.content.skill.skillable.impl.fletching.FletchingConstants.*;

/**
 * An enumerated type listing all of the items that can be made from a
 * specific log.
 *
 * @author Professor Oak
 */
public enum FletchableLog {

    REGULAR(ItemID.LOGS,
            new FletchableItem(new Item(ItemID.ARROW_SHAFT, 15), 1, 5, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(ItemID.JAVELIN_SHAFT, 15), 3, 5, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(ItemID.WOODEN_STOCK), 9, 6, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(ItemID.SHORTBOW_U_), 5, 10, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(ItemID.LONGBOW_U_), 10, 20, CUTTING_LOGS_ANIM)),
    OAK(ItemID.OAK_LOGS,
            new FletchableItem(new Item(ItemID.ARROW_SHAFT, 30), 15, 10, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(ItemID.OAK_STOCK), 24, 16, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(ItemID.OAK_SHORTBOW_U_), 20, 33, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(ItemID.OAK_LONGBOW_U_), 25, 50, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(22251), 25, 50, CUTTING_LOGS_ANIM)),
    WILLOW(ItemID.WILLOW_LOGS,
            new FletchableItem(new Item(ItemID.ARROW_SHAFT, 45), 30, 15, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(ItemID.WILLOW_STOCK), 39, 22, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(ItemID.WILLOW_SHORTBOW_U_), 35, 66, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(ItemID.WILLOW_LONGBOW_U_), 40, 83, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(22254), 42, 83, CUTTING_LOGS_ANIM)),
    MAPLE(ItemID.MAPLE_LOGS,
            new FletchableItem(new Item(ItemID.ARROW_SHAFT, 60), 45, 20, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(ItemID.MAPLE_STOCK), 54, 32, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(ItemID.MAPLE_SHORTBOW_U_), 50, 100, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(ItemID.MAPLE_LONGBOW_U_), 55, 116, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(22257), 57, 117, CUTTING_LOGS_ANIM)),
    TEAK(ItemID.TEAK_LOGS,
            new FletchableItem(new Item(ItemID.ARROW_SHAFT, 65), 46, 20, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(ItemID.TEAK_STOCK), 46, 27, CUTTING_LOGS_ANIM)),
    YEW(ItemID.YEW_LOGS,
            new FletchableItem(new Item(ItemID.ARROW_SHAFT, 75), 60, 25, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(ItemID.YEW_STOCK), 69, 50, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(ItemID.YEW_SHORTBOW_U_), 65, 135, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(ItemID.YEW_LONGBOW_U_), 70, 150, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(22260), 72, 150, CUTTING_LOGS_ANIM)),
    MAGIC(ItemID.MAGIC_LOGS,
            new FletchableItem(new Item(ItemID.ARROW_SHAFT, 90), 75, 30, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(21952, 1), 78, 70, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(ItemID.MAGIC_SHORTBOW_U_), 80, 166, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(ItemID.MAGIC_LONGBOW_U_), 85, 183, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(22263), 87, 183, CUTTING_LOGS_ANIM)),
    REDWOOD(ItemID.REDWOOD_LOGS,
            new FletchableItem(new Item(ItemID.ARROW_SHAFT, 105), 90, 35, CUTTING_LOGS_ANIM),
            new FletchableItem(new Item(22266, 1), 90, 216, CUTTING_LOGS_ANIM)),
    ACHEY(ItemID.ACHEY_TREE_LOGS,
            new FletchableItem(new Item(ItemID.OGRE_ARROW_SHAFT, 4), 5, 10, CUTTING_LOGS_ANIM)),

    CELASTRUS_BARK(ItemID.CELASTRUS_BARK,
            new FletchableItem(new Item(ItemID.BATTLESTAFF, 1), 40, 80, CUTTING_LOGS_ANIM))

    ;

    static Map<Integer, FletchableLog> logs = new HashMap<>();

    static {
        for (FletchableLog l : FletchableLog.values()) {
            logs.put(l.getLogId(), l);
        }
    }

    private final int logId;
    private final FletchableItem[] fletchable;

    FletchableLog(int logId, FletchableItem... fletchable) {
        this.logId = logId;
        this.fletchable = fletchable;
    }

    public int getLogId() {
        return logId;
    }

    public FletchableItem[] getFletchable() {
        return fletchable;
    }
}
