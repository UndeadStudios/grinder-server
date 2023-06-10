package com.grinder.game.content.minigame.blastfurnace.conveyor;

import com.google.gson.annotations.Expose;
import com.grinder.game.content.minigame.blastfurnace.BlastFurnaceOre;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.model.Position;
import com.grinder.util.ObjectID;

import java.util.HashMap;
import java.util.Map;

/**
 * @author L E G E N D
 * @date 2/17/2021
 * @time 6:09 AM
 * @discord L E G E N D#4380
 */
public class ConveyorBelt {

    @Expose
    private final Map<BlastFurnaceOre, Integer> content = new HashMap<>();

    private static final GameObject[] objects = new GameObject[3];

    public static GameObject getBelt(int index) {
        if (objects[index] == null) {
            objects[0] = ObjectManager.findStaticObjectAt(ObjectID.CONVEYOR_BELT, new Position(1943, 4967, 0)).get();
            objects[1] = ObjectManager.findStaticObjectAt(ObjectID.CONVEYOR_BELT_2, new Position(1943, 4966, 0)).get();
            objects[2] = ObjectManager.findStaticObjectAt(ObjectID.CONVEYOR_BELT_2, new Position(1943, 4965, 0)).get();
        }
        return objects[index];
    }

    public void add(BlastFurnaceOre ore, int amount) {
        var newAmount = getAmount(ore) + amount;
        content.put(ore, newAmount);
    }

    public void remove(BlastFurnaceOre ore, int amount) {
        var newAmount = getAmount(ore) - amount;
        if (newAmount == 0) {
            content.remove(ore);
            return;
        }
        content.put(ore, newAmount);
    }

    public int getSpace(BlastFurnaceOre ore) {
        return ore.getCapacity() - getAmount(ore);
    }

    public int getAmount() {
        var amount = 0;
        for (var type : content.keySet()) {
            amount += getAmount(type);
        }
        return amount;
    }

    public int getAmount(BlastFurnaceOre ore) {
        return content.getOrDefault(ore, 0);
    }

    public Map<BlastFurnaceOre, Integer> getContent() {
        return content;
    }
}
