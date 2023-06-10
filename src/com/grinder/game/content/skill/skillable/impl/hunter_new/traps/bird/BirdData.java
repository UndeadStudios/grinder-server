package com.grinder.game.content.skill.skillable.impl.hunter_new.traps.bird;

import com.google.common.collect.ImmutableSet;
import com.grinder.game.model.item.Item;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;

/**
 * The enumerated type whose elements represent a set of constants
 * used for bird snaring.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public enum BirdData {
    CRIMSON_SWIFT(NpcID.CRIMSON_SWIFT, 9373, 1, 24, 526, 10088, 9978),
    GOLDEN_WARBLER(NpcID.GOLDEN_WARBLER, 9377, 5, 37, 526, 10090, 9978),
    COPPER_LONGTAIL(NpcID.COPPER_LONGTAIL, 9379, 9, 51, 526, 10091, 9978),
    CERULEAN_TWITCH(NpcID.CERULEAN_TWITCH, 9375, 11, 74.5, 526, 10089, 9978),
    TROPICAL_WAGTAIL(NpcID.TROPICAL_WAGTAIL, 9348, 19, 95, 526, 10087, 9978);

    public static final ImmutableSet<BirdData> VALUES = ImmutableSet.copyOf(values());

    /**
     * The npc id for this bird.
     */
    public final int npcId;

    /**
     * The object id for the catched bird.
     */
    public final int objectId;

    /**
     * The requirement for this bird.
     */
    final int requirement;

    /**
     * The experience gained for this bird.
     */
    final double experience;

    /**
     * The reward obtained for this bird.
     */
    final Item[] reward;

    /**
     * Constructs a new {@link BirdData}.
     * @param npcId {@link #npcId}.
     * @param objectId {@link #objectId}
     * @param requirement {@link #requirement}.
     * @param experience {@link #experience}.
     * @param reward {@link #reward}.
     */
    BirdData(int npcId, int objectId, int requirement, double experience, int... reward) {
        this.npcId = npcId;
        this.objectId = objectId;
        this.requirement = requirement;
        this.experience = experience;
        this.reward = Item.convert(reward);
    }

    public static BirdData getByID(int id) {
        return VALUES.stream().filter(b -> b.getNpcId() == id).findFirst().orElse(null);
    }

    /**
     * @return the npc id.
     */
    public int getNpcId() {
        return npcId;
    }

    @Override
    public String toString() {
        return Misc.capitalize(name().replaceAll("_", " ").toLowerCase());
    }
}
