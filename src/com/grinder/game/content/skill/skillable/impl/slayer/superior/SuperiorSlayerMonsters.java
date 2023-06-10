package com.grinder.game.content.skill.skillable.impl.slayer.superior;

import com.grinder.game.content.skill.skillable.impl.slayer.SlayerMonsterType;
import com.grinder.util.ItemID;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Luka Furlan, luka.furlan9@gmail.com
 * @since 14/7/2020
 */
public enum SuperiorSlayerMonsters {

    MONSTROUS_BASILISK(7395, 1700, SlayerMonsterType.BASILISK),
    COCKATHRICE(7393, 950, SlayerMonsterType.COCKATRICE),
    SCREAMING_BANSHEE(7390, 610, SlayerMonsterType.BANSHEE),
    SCREAMING_TWISTED_BANSHEE(7391, 2200, SlayerMonsterType.BANSHEE),

    ABHORRENT_SPECTRE(7402, 2500, SlayerMonsterType.ABERRANT_SPECTRE),
    REPUGNANT_SPECTRE(7403, 4085, SlayerMonsterType.DEVIANT_SPECRTRE),
    GREATER_ABYSSAL_DEMON(7410, 4200, SlayerMonsterType.ABYSSAL_DEMON, ItemID.ABYSSAL_HEAD),
    INSATIABLE_BLOODVELD(7397, 2900, SlayerMonsterType.BLOODVELD, ItemID.ENSOULED_BLOODVELD_HEAD),
    INSATIABLE_MUTATED_BLOODVELD(7398, 4100, SlayerMonsterType.MUTATED_BLOODVELD, ItemID.ENSOULED_BLOODVELD_HEAD),
    //SPIKED_TUROTH(10397, 1998, SlayerMonsterType.TUROTH),
    CHASM_CRAWLER(7389, 600, SlayerMonsterType.CAVE_CRAWLER),
    CRUSHING_HAND(7388, 550, SlayerMonsterType.CRAWLING_HAND),
    CHOKE_DEVIL(7404, 3000, SlayerMonsterType.SMOKE_DEVIL),
    MARBLE_GARGOYLE(7407, 3044, SlayerMonsterType.GARGOYLE),
    VITREOUS_JELLY(7399, 1900, SlayerMonsterType.JELLY),
    VITREOUS_WARPED_JELLY(7400, 2200, SlayerMonsterType.WARPED_JELLY),
    KING_KURASK(7405, 2767, SlayerMonsterType.KURASK),
    NECHRYARCH(7411, 3068, SlayerMonsterType.NECHRYAEL),
    GIANT_ROCKSLUG(7392, 770, SlayerMonsterType.ROCKSLUG),
    NIGHT_BEAST(7409, 6462, SlayerMonsterType.DARK_BEAST),
    FLAMING_PYRELORD(7394, 1250, SlayerMonsterType.PYREFIEND),
    MALEVOLENT_MAGE(7396, 1750, SlayerMonsterType.INFERNAL_MAGE),
    // SHADOW_WYRM
    // GUARDIAN_DRAKE
    // COLOSSAL_HYDRA
    CAVE_ABOMINATION(7399, 1900, SlayerMonsterType.CAVE_HORROR),
    NUCLEAR_SMOKE_DEVIL(7406, 2400, SlayerMonsterType.SMOKE_DEVIL);

    private final int npcId;
    private final int experienceReward;
    private final SlayerMonsterType slayerMonster;
    private final Optional<Integer> ensouledHeadId;

    SuperiorSlayerMonsters(int npcId, int experienceReward, SlayerMonsterType slayerMonster) {
        this.npcId = npcId;
        this.experienceReward = experienceReward;
        this.slayerMonster = slayerMonster;
        this.ensouledHeadId = Optional.empty();
    }

    SuperiorSlayerMonsters(int npcId, int experienceReward, SlayerMonsterType slayerMonster, int ensouledHeadId) {
        this.npcId = npcId;
        this.experienceReward = experienceReward;
        this.slayerMonster = slayerMonster;
        this.ensouledHeadId = Optional.of(ensouledHeadId);
    }

    public static Optional<SuperiorSlayerMonsters> forId(int npcId) {
        return Arrays.stream(SuperiorSlayerMonsters.values())
                .filter(superiorSlayerMonster -> superiorSlayerMonster.npcId == npcId)
                .findFirst();
    }

    public static Optional<SuperiorSlayerMonsters> forSlayerMonster(SlayerMonsterType slayerMonster) {
        return Arrays.stream(SuperiorSlayerMonsters.values())
                .filter(superiorSlayerMonster -> superiorSlayerMonster.slayerMonster == slayerMonster)
                .findFirst();
    }

    public int getNpcId() {
        return npcId;
    }

    public SlayerMonsterType getSlayerMonster() {
        return slayerMonster;
    }

    public Optional<Integer> getEnsouledHeadId() {
        return ensouledHeadId;
    }

    public int getExperienceReward() {
        return experienceReward;
    }
}
