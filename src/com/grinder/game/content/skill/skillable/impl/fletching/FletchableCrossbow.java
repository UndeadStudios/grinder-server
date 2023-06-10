package com.grinder.game.content.skill.skillable.impl.fletching;

import com.grinder.game.model.Animation;

import static com.grinder.util.ItemID.*;

/**
 * Represents crossbows which can be made via the Fletching skill.
 *
 * @author Professor Oak
 */
public enum FletchableCrossbow {

    BRONZE_CROSSBOW(WOODEN_STOCK, BRONZE_LIMBS, BRONZE_CROSSBOW_U_, 9, 12, new Animation(4436)),
    IRON_CROSSBOW(WILLOW_STOCK, IRON_LIMBS, IRON_CROSSBOW_U_, 39, 44, new Animation(4438)),
    STEEL_CROSSBOW(TEAK_STOCK, STEEL_LIMBS, STEEL_CROSSBOW_U_, 46, 54, new Animation(4439)),
    MITHRIL_CROSSBOW(MAPLE_STOCK, MITHRIL_LIMBS, MITHRIL_CROSSBOW_U_, 54, 64, new Animation(4440)),
    ADAMANT_CROSSBOW(MAHOGANY_STOCK, ADAMANTITE_LIMBS, ADAMANT_CROSSBOW_U_, 61, 82, new Animation(4441)),
    RUNE_CROSSBOW(YEW_STOCK, RUNITE_LIMBS, RUNITE_CROSSBOW_U_, 69, 100, new Animation(4442)),
    DRAGON_CROSSBOW(21952, 21918, 21921, 78, 135, new Animation(7860));

    private final int stock, limbs, unstrung, level, limbsExp;
    private final Animation animation;

    FletchableCrossbow(int stock, int limbs, int unstrung, int level, int limbsExp, Animation animation) {
        this.stock = stock;
        this.limbs = limbs;
        this.unstrung = unstrung;
        this.level = level;
        this.limbsExp = limbsExp;
        this.animation = animation;
    }

    public int getStock() {
        return stock;
    }

    public int getUnstrung() {
        return unstrung;
    }

    public int getLimbs() {
        return limbs;
    }

    public int getLevel() {
        return level;
    }

    public int getLimbsExp() {
        return limbsExp;
    }

    public Animation getAnimation() {
        return animation;
    }
}
