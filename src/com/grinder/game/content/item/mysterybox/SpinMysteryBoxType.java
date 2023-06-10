package com.grinder.game.content.item.mysterybox;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.item.Item;
import org.apache.commons.lang.WordUtils;

import java.util.*;
import java.util.List;

public enum SpinMysteryBoxType {
    BARROWS_MYSTERY_BOX(15200, MysteryBoxType.BARROWS_MYSTERY_BOX, AttributeManager.Points.BARROWS_BOXES_COUNT),
    LEGENDARY_MYSTERY_BOX(15201, MysteryBoxType.LEGENDARY_MYSTERY_BOX, AttributeManager.Points.LEGENDARY_BOXES_COUNT),
    PVP_MYSTERY_BOX(15202, MysteryBoxType.PVP_MYSTERY_BOX, AttributeManager.Points.PVP_BOXES_COUNT),
    GILDED_MYSTERY_BOX(15203, MysteryBoxType.GILDED_MYSTERY_BOX, AttributeManager.Points.GILDED_BOXES_COUNT),
    SACRED_MYSTERY_BOX(15204, MysteryBoxType.SACRED_MYSTERY_BOX, AttributeManager.Points.QUESTION_BOXES_COUNT),
    VIP_MYSTERY_BOX(15724, MysteryBoxType.VIP_MYSTERY_BOX, AttributeManager.Points.FIFTY_DOLLARS_BOXES_COUNT),
    HUNDRED_DOLLAR_MYSTERY_BOX(15725, MysteryBoxType.HUNDRED_DOLLAR_MYSTERY_BOX, AttributeManager.Points.HUNDRED_DOLLARS_BOXES_COUNT),
    SUPER_MYSTERY_BOX(15205, MysteryBoxType.SUPER_MYSTERY_BOX, AttributeManager.Points.SUPER_BOXES_COUNT),
    EXTREME_MYSTERY_BOX(15206, MysteryBoxType.EXTREME_MYSTERY_BOX, AttributeManager.Points.EXTREME_BOXES_COUNT),
    STAFF_PRESENT(15215, MysteryBoxType.STAFF_PRESENT, null),
    DAILY_LUCK_PRESENT(15267, MysteryBoxType.DAILY_LUCK_PRESENT, null),
    VOTING_MYSTERY_BOX(15207, MysteryBoxType.VOTING_MYSTERY_BOX, AttributeManager.Points.VOTING_BOXES_COUNT),
    MYSTERY_BOX(6199, MysteryBoxType.MYSTERY_BOX, AttributeManager.Points.REGULAR_BOXES_COUNT);

    private final int itemId;
    private final MysteryBoxType rewards;
    private final AttributeManager.Points points;

    SpinMysteryBoxType(int itemId, MysteryBoxType rewards, AttributeManager.Points points) {
        this.itemId = itemId;
        this.rewards = rewards;
        this.points = points;
    }

    public int getSpinButtonId() {
        return MysteryBoxConstants.SPIN_BUTTON_START_ID + ordinal();
    }

    public int getRewardsButtonId() {
        return MysteryBoxConstants.VIEW_REWARDS_BUTTON_START_ID + ordinal();
    }


    public AttributeManager.Points getPoints() {
        return points;
    }

    public int getItemId() {
        return itemId;
    }

    public MysteryBoxType getRewards() {
        return rewards;
    }

    public String getFormattedName() {
        String name = toString();
//        if (this == EPIC_RARE_MYSTERY_BOX) {
//            name = name.replace("QUESTION_MARK", "?");
//        }
        return WordUtils.capitalizeFully(name.replace("_", " "));
    }

    public String getPluralFormattedName() {
        String name = getFormattedName();
        if (name.endsWith("x")) {
            return name + "es";
        }
        return name + "s";
    }

    public List<Item> getItems(Player player) {
        if (player.getMysteryBoxItemsLists()[ordinal()] == null) {
            refreshItems(player);
        }
        return player.getMysteryBoxItemsLists()[ordinal()];
    }

    public void refreshItems(Player player) {
        MysteryBoxRewardItem[] rewards = getRewards().getItemRewards();
        List<Item> items = new ArrayList<>(rewards.length);
        Arrays.stream(rewards).forEach(r -> items.add(r.getReward()));
        Collections.shuffle(items);
        player.getMysteryBoxItemsLists()[ordinal()] = items;
    }

    private static Map<Integer, SpinMysteryBoxType> itemIds = new HashMap<>();

    public static Map<Integer, SpinMysteryBoxType> getItemIds() {
        return itemIds;
    }

    static {
        Arrays.stream(values()).forEach(b -> {
            itemIds.put(b.getItemId(), b);
        });
    }
}
