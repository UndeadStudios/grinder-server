package com.grinder.game.content.minigame.blastfurnace;

import com.google.common.base.CaseFormat;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

import java.util.ArrayList;

/**
 * @author L E G E N D
 * @date 2/17/2021
 * @time 5:50 AM
 * @discord L E G E N D#4380
 */
public enum BlastFurnaceBar {

    BRONZE(BlastFurnaceOre.COPPER, BlastFurnaceOre.TIN, ItemID.BRONZE_BAR, 1, 6.2),
    IRON(BlastFurnaceOre.IRON, ItemID.IRON_BAR, 15, 12.5),
    SILVER(BlastFurnaceOre.SILVER, ItemID.SILVER_BAR, 20, 13.6),
    STEEL(BlastFurnaceOre.IRON, BlastFurnaceOre.COAL, ItemID.STEEL_BAR, 30, 17.5),
    GOLD(BlastFurnaceOre.GOLD, ItemID.GOLD_BAR, 40, 22.5),
    MITHRIL(BlastFurnaceOre.MITHRIL, BlastFurnaceOre.COAL, 2, ItemID.MITHRIL_BAR, 50, 30),
    ADAMANTITE(BlastFurnaceOre.ADAMANTITE, BlastFurnaceOre.COAL, 3, ItemID.ADAMANTITE_BAR, 70, 37.5),
    RUNITE(BlastFurnaceOre.RUNITE, BlastFurnaceOre.COAL, 4, ItemID.RUNITE_BAR, 85, 50);

    private final BlastFurnaceBarRequirement[] requirements;
    private final int barId;
    private final int levelRequired;
    private final double xp;

    BlastFurnaceBar(BlastFurnaceOre ore, int barId, int levelRequired, double xp) {
        this(new BlastFurnaceBarRequirement[]{BlastFurnaceBarRequirement.create(ore)}, barId, levelRequired, xp);
    }

    BlastFurnaceBar(BlastFurnaceOre ore, BlastFurnaceOre ore2, int barId, int levelRequired, double xp) {
        this(new BlastFurnaceOre[]{ore, ore2}, barId, levelRequired, xp);
    }

    BlastFurnaceBar(BlastFurnaceOre requirement, BlastFurnaceOre requirement2, int amountNeeded2, int barId, int levelRequired, double xp) {
        this(requirement, BlastFurnaceBarRequirement.create(requirement2, amountNeeded2), barId, levelRequired, xp);
    }

    BlastFurnaceBar(BlastFurnaceOre requirement, BlastFurnaceBarRequirement requirement2, int barId, int levelRequired, double xp) {
        this(new BlastFurnaceBarRequirement[]{BlastFurnaceBarRequirement.create(requirement), requirement2}, barId, levelRequired, xp);
    }

    BlastFurnaceBar(BlastFurnaceOre[] requirements, int barId, int levelRequired, double xp) {
        this(BlastFurnaceBarRequirement.create(requirements), barId, levelRequired, xp);
    }

    BlastFurnaceBar(BlastFurnaceBarRequirement[] requirements, int barId, int levelRequired, double xp) {
        this.requirements = requirements;
        this.barId = barId;
        this.levelRequired = levelRequired;
        this.xp = xp;
    }

    public Item[] getRequirementsAsItems() {
        var items = new ArrayList<Item>();
        for (var requirement : requirements) {
            items.add(new Item(requirement.getOre().getOreId(), requirement.getAmountRequired()));
        }
        return items.toArray(Item[]::new);
    }

    public int getBarId() {
        return barId;
    }

    public int getLevelRequired() {
        return levelRequired;
    }

    public double getXp() {
        return xp;
    }

    public String getName(){
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
    }

    public BlastFurnaceBarRequirement[] getRequirements() {
        return requirements;
    }
}
