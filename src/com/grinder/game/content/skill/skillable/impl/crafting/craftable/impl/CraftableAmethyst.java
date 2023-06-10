package com.grinder.game.content.skill.skillable.impl.crafting.craftable.impl;

import com.grinder.game.model.Animation;
import com.grinder.game.model.item.Item;

/**
 * Represents a fletchable item.
 *
 * @author Professor Oak
 */
public final class CraftableAmethyst {

    private final Item product;
    private final int levelRequired;
    private final int experience;
    private final Animation animation;

    CraftableAmethyst(Item product, int levelRequired, int experience, Animation animation) {
        this.product = product;
        this.levelRequired = levelRequired;
        this.experience = experience;
        this.animation = animation;
    }

    public Item getProduct() {
        return product;
    }

    public int getLevelRequired() {
        return levelRequired;
    }

    public int getExperience() {
        return experience;
    }

    public Animation getAnimation() {
        return animation;
    }
}
