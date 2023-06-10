package com.grinder.game.model.item;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.item.container.ItemContainer;

public enum ItemSetType {

    GUTHANS_SET(12873, 4724, 4726, 4728, 4730),
    VERACS_SET(12875, 4753, 4755, 4757, 4759),
    TORAGS_SET(12879, 4745, 4747, 4749, 4751),
    AHRIMS_SET(12881, 4708, 4710, 4712, 4714),
    KARILS_SET(12883, 4732, 4734, 4736, 4738),
    DHAROKS_SET(12877, 4716, 4718, 4720, 4722),
    GILDED_SET(13036, 3486, 3481, 3483, 3488),
    GILDED_SET_SK(13038, 3486, 3481, 3485, 3488),
    OBSIDIAN_ARMOUR(21279, 21298, 21301, 21304),
    PARTYHAT_SET(13173, 1038, 1040, 1042, 1044, 1046, 1048),
    HWEEN_SET(13175, 1053, 1055, 1057)
    ;

    private static final Map<Integer, ItemSetType> sets = new HashMap<Integer, ItemSetType>();

    static {
        for (ItemSetType set : ItemSetType.values()) {
            for (int i : set.items) {
                sets.put(i, set);
            }
            sets.put(set.getSetId(), set);
        }
    }

    private final int setId;
    private final int[] items;
    ItemSetType(int setId, int... items) {
        this.setId = setId;
        this.items = items;
    }

    public static Optional<ItemSetType> findInContainer(ItemContainer container){
        for(ItemSetType type: values()) {
            if(container.containsAll(type.items))
                return Optional.of(type);
        }
        return Optional.empty();
    }

    public static boolean pack(Player player, int itemId) {
        ItemSetType set = sets.get(itemId);
        if (set == null) {
            return false;
        }

        if (player.busy()) {
            player.getPacketSender().sendMessage("You can't do that right now.");
            return true;
        }

        for (int i : set.items) {
            if (!player.getInventory().contains(i)) {
                DialogueManager.sendStatement(player, "You do not have enough components to make a set out of this armor.");
                return true;
            }
        }

        for (int i : set.items) {
            player.getInventory().delete(i, 1);
        }

        player.getInventory().add(set.setId, 1);

        DialogueManager.sendStatement(player, "You've made a set out of your armor.");
        return true;
    }

    public static ItemSetType get(int item) {
        return sets.get(item);
    }

    public int getSetId() {
        return setId;
    }

    public int[] getItems() {
        return items;
    }
}
