package com.grinder.game.content.minigame.blastfurnace;

/**
 * @author L E G E N D
 * @date 2/17/2021
 * @time 6:43 AM
 * @discord L E G E N D#4380
 */
public class BlastFurnaceBarRequirement {

    private final BlastFurnaceOre ore;
    private final int amountRequired;

    public BlastFurnaceBarRequirement(BlastFurnaceOre ore, int amountRequired) {
        this.ore = ore;
        this.amountRequired = amountRequired;
    }


    public static BlastFurnaceBarRequirement create(int oreId) {
        return create(BlastFurnaceOre.forId(oreId));
    }

    public static BlastFurnaceBarRequirement create(int oreId, int amountRequired) {
        return create(BlastFurnaceOre.forId(oreId), amountRequired);
    }

    public static BlastFurnaceBarRequirement create(BlastFurnaceOre ore) {
        return create(ore, 1);
    }

    public static BlastFurnaceBarRequirement create(BlastFurnaceOre ore, int amountRequired) {
        return new BlastFurnaceBarRequirement(ore, amountRequired);
    }

    public static BlastFurnaceBarRequirement[] create(BlastFurnaceOre[] ores) {
        var values = new BlastFurnaceBarRequirement[ores.length];
        for (int index = 0; index < ores.length; index++) {
            values[index] = create(ores[index]);
        }
        return values;
    }

    public BlastFurnaceOre getOre() {
        return ore;
    }

    public int getAmountRequired() {
        return amountRequired;
    }
}
