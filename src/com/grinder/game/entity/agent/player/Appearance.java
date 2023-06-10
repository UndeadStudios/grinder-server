package com.grinder.game.entity.agent.player;

import com.google.gson.annotations.Expose;
import com.grinder.game.entity.updating.block.BasicAnimationSet;
import com.grinder.util.Misc;

/**
 * This file manages a player's appearance and properties, such as head hints, gender, prayer head hints, etc.
 *
 * @author relex lawl
 */

public class Appearance {

    /**
     * The index of said body part color in the look array.
     */
    public static final int HAIR_COLOUR = 8, TORSO_COLOUR = 9, LEG_COLOUR = 10, FEET_COLOUR = 11, SKIN_COLOUR = 12,
            HEAD = 1, CHEST = 2, ARMS = 3, HANDS = 4, LEGS = 5, FEET = 6, BEARD = 7, GENDER = 0;
    /**
     * Can the player change appearance right now?
     */
    @Expose
    private boolean canChangeAppearance = false;
    /**
     * The player's head icon hint.
     */
    @Expose
    private int headHint = -1;
    /**
     * The player's bounty hunter skull.
     */
    @Expose
    private int bountyHunterSkull = -1;
    /**
     * The player's current character clothing.
     */
    @Expose
    private int[] look = new int[13];

    /**
     * The associated player.
     */
    private Player player;

    /**
     * The current basic animation set of the player, falls back to the default one
     * if none is provided.
     */
    private BasicAnimationSet bas;

    /**
     * The Appearance constructor, also sets
     * the player's default clothing.
     *
     * @param player The associated player.
     */
    public Appearance(Player player) {
        this.player = player;
        set();
    }

    /**
     * Gets the player's current head hint index.
     *
     * @return The player's head hint.
     */
    public int getHeadHint() {
        return headHint;
    }

    /**
     * Sets the player's head icon hint.
     *
     * @param headHint The hint index to use.
     * @return The Appearance instance.
     */
    public Appearance setHeadHint(int headHint) {
        this.headHint = headHint;
        player.updateAppearance();
        return this;
    }

    /**
     * Gets the player's current bounty hunter skull.
     *
     * @return The player's skull hint.
     */
    public int getBountyHunterSkull() {
        return bountyHunterSkull;
    }

    /**
     * Sets the player's bounty hunter skull.
     *
     * @param skullHint The skull hint index to use.
     * @return The Appearance instance.
     */
    public Appearance setBountyHunterSkull(int skullHint) {
        this.bountyHunterSkull = skullHint;
        player.updateAppearance();
        return this;
    }

    /**
     * Checks if a player can change appearance right now
     *
     * @return the canChangeAppearance value
     */
    public boolean canChangeAppearance() {
        return canChangeAppearance;
    }

    /**
     * Sets if a player can change appearance right now
     *
     * @param l The value to set
     */
    public void setCanChangeAppearance(boolean l) {
        this.canChangeAppearance = l;
    }

    /**
     * Gets the look array, which is an array with 13 elements describing the
     * look of a player.
     *
     * @return The look array.
     */
    public int[] getLook() {
        return look;
    }

    /**
     * Sets the look array.
     *
     * @param look The look array.
     * @throws IllegalArgumentException if the array length is not 12.
     */
    public void set(int[] look) {
        if (look.length < 12) {
            throw new IllegalArgumentException("Array length must be 12.");
        }
        this.look = look;
    }

    /**
     * Sets a specific look.
     *
     * @param index Array index to set.
     * @param look  Value to change look[index] to.
     */
    public void set(int index, int look) {
        this.look[index] = look;
        player.updateAppearance();
    }

    /**
     * Sets the player's default clothing.
     */
    public void set() {
        if (isMale()) {
            look[HEAD] = Misc.random(8);
            look[CHEST] = 18 + Misc.random(6);
            look[ARMS] = 26 + Misc.random(5);
            look[HANDS] = 33 + Misc.random(1);
            look[LEGS] = 36 + Misc.random(4);
            look[FEET] = 42 + Misc.random(1);
            look[BEARD] = 10 + Misc.random(7);
        } else {
            look[HEAD] = 45 + Misc.random(8);
            look[CHEST] = 56 + Misc.random(3);
            look[ARMS] = 61 + Misc.random(3);
            look[HANDS] = 67 + Misc.random(1);
            look[LEGS] = 70 + Misc.random(7);
            look[FEET] = 79 + Misc.random(1);
            look[BEARD] = -1;
        }
        look[HAIR_COLOUR] = Misc.random(11);
        look[TORSO_COLOUR] = Misc.random(15);
        look[LEG_COLOUR] = Misc.random(15);
        look[FEET_COLOUR] = Misc.random(5);
        look[SKIN_COLOUR] = Misc.random(7);
    }

    public boolean isMale() {
        return look[GENDER] == 0;
    }

    public String getGenderName(){
        return isMale() ? "boy" : "girl";
    }

    public BasicAnimationSet getBas() {
        return bas;
    }

    public void setBas(BasicAnimationSet bas) {
        this.bas = bas;
    }
}
