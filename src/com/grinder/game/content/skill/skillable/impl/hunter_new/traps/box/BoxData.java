package com.grinder.game.content.skill.skillable.impl.hunter_new.traps.box;


import com.google.common.collect.ImmutableSet;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;
import com.grinder.util.NpcID;

/**
 * The enumerated type whose elements represent a set of constants
 * used for box trapping.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public enum BoxData {
    FERRET(NpcID.FERRET, 27, 115, 10092),
    CHINCHOMPA(NpcID.CHINCHOMPA, 53, 198.25, 10033),
    CARNIVOROUS_CHINCHOMPA(NpcID.CARNIVOROUS_CHINCHOMPA, 63, 265, 10034),
    BLACK_CHINCHOMPA(NpcID.BLACK_CHINCHOMPA, 73, 315, ItemID.BLACK_CHINCHOMPA);

    public static final ImmutableSet<BoxData> VALUES = ImmutableSet.copyOf(values());

    /**
     * The npc id for this box trap.
     */
    final int npcId;

    /**
     * The requirement for this box trap.
     */
    final int requirement;

    /**
     * The experience gained for this box trap.
     */
    final double experience;

    /**
     * The reward obtained for this box trap.
     */
    final Item[] reward;

    /**
     * Constructs a new {@link BoxData}.
     * @param npcId {@link #npcId}.
     * @param requirement {@link #requirement}.
     * @param experience {@link #experience}.
     * @param reward {@link #reward}.
     */
    BoxData(int npcId, int requirement, double experience, int... reward) {
        this.npcId = npcId;
        this.requirement = requirement;
        this.experience = experience;
        this.reward = Item.convert(reward);
    }

    public static BoxData getByID(int id) {
        return VALUES.stream().filter(b -> b.npcId == id).findFirst().orElse(null);
    }
}
