package com.grinder.game.content.skill.skillable.impl.hunter.birdhouse;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Skill;
import com.grinder.util.ItemID;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Zach (zach@findzach.com)
 * @since 12/21/2020
 */
public enum BirdHouseTier {

    WOOD(21512, ItemID.LOGS, 1, 30553, 5, 180),
    OAK(21515, ItemID.OAK_LOGS, 4, 30556, 14, 280),
    WILLOW(21518, ItemID.WILLOW_LOGS, 7, 30559, 24, 560),
    TEAK(21521, ItemID.TEAK_LOGS, 10, 30562, 34, 700),
    MAPLE(22192, ItemID.MAPLE_LOGS, 13, 31827, 44, 820),
    MAHOGANY(22195, ItemID.MAHOGANY_LOGS, 16, 31830, 49, 960),
    YEW(22198, ItemID.YEW_LOGS, 19, 31833, 59, 1020),
    MAGIC(22201, ItemID.MAGIC_LOGS, 22, 31836, 74, 1140),
    REDWOOD(22204, ItemID.REDWOOD_LOGS, 25, 31839, 89, 1200);


    private final int birdhouseId;
    private final int requiredId;
    private final int varpValueIndexId;
    private final int objectId;
    private final int levReq;
    private final int expGained;

    /**
     * Birdhouse Tier Data
     *
     * @param birdhouseId - The Bird House Item ID
     * @param requiredId  - The Log required to make the birdhouseId
     * @param objectId    - The empty birdhouse ID. + 1 is birdhouse with seeds +2 is full birdhouse
     * @param levReq      - The minimum hunter level
     * @param expGained   - the OSRS rate of xp gained
     */
    BirdHouseTier(int birdhouseId, int requiredId, int varpValueIndexId, int objectId, int levReq, int expGained) {
        this.birdhouseId = birdhouseId;
        this.requiredId = requiredId;
        this.varpValueIndexId = varpValueIndexId;
        this.objectId = objectId;
        this.levReq = levReq;
        this.expGained = expGained;
    }

    /**
     * Will find the best birdhouse the user has and can setup
     * @param player The user in question
     * @return Will return the highest tier of Birdhouse the player has that they're capable of setting up
     */
    public static BirdHouseTier highestTierInInventory(Player player) {
        int playerHuntLvl = player.getSkillManager().getCurrentLevel(Skill.HUNTER);
        Stream<BirdHouseTier> highestTier = Arrays.stream(BirdHouseTier.values()).
                filter(birdHouseTier -> playerHuntLvl >= birdHouseTier.levReq).
                filter(birdHouseTier -> player.getInventory().contains(birdHouseTier.birdhouseId)).
                sorted(Comparator.comparingInt(BirdHouseTier::getLevReq).reversed());
        Optional<BirdHouseTier> levelRequiredAndInInventory = highestTier.findFirst();

        // can use .orElse instead
        return levelRequiredAndInInventory.isPresent() ? levelRequiredAndInInventory.get() : null;
    }

    public int getRequiredId() {
        return requiredId;
    }

    public int getObjectId() {
        return objectId;
    }

    public int getLevReq() {
        return levReq;
    }

    public int getExpGained() {
        return expGained;
    }

    public int getBirdhouseId() {
        return birdhouseId;
    }

    public int getVarpValueIndexId() {
        return varpValueIndexId;
    }
}
