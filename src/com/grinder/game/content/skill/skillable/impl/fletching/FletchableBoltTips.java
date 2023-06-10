package com.grinder.game.content.skill.skillable.impl.fletching;

import com.grinder.game.model.item.Item;

/**
 * Represents bolt tips which can be made using the Fletching skill.
 *
 * @author Austin
 */
public enum FletchableBoltTips {

    OPAL(new Item(1609), new Item(45, 12), 1.5, 1),
    JADE(new Item(1611), new Item(9187, 12), 2.4, 26),
    PEARL(new Item(411), new Item(46, 6), 3.2, 41),
    TOPAZ(new Item(1613), new Item(9188, 12), 4, 48),
    SAPPHIRE(new Item(1607), new Item(9189, 12), 4, 56),
    EMERALD(new Item(1605), new Item(9190, 12), 5.5, 58),
    RUBY(new Item(1603), new Item(9191, 12), 6, 63),
    DIAMOND(new Item(1601), new Item(9192, 12), 6, 65),
    DRAGONSTONE(new Item(1615), new Item(9193, 12), 8.2, 71),
    ONYX(new Item(6573), new Item(9194, 24), 9.4, 73);

    private final Item item, outcome;

    private final double xp;

    private final int levelReq;

    FletchableBoltTips(Item item, Item outcome, double xp, int levelReq) {
        this.item = item;
        this.outcome = outcome;
        this.xp = xp;
        this.levelReq = levelReq;
    }

    public Item getItem() {
        return item;
    }

    public Item getOutcome() {
        return outcome;
    }

    public double getXp() {
        return xp;
    }

    public int getLevelReq() {
        return levelReq;
    }
}
