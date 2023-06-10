package com.grinder.game.model.item;

import com.grinder.game.definition.ItemValueType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.util.ItemID;

public class ItemUtil {

    public static boolean bypassPriceMultiplier(int itemId){
        return itemId == ItemID.BLOOD_MONEY || itemId == ItemID.COINS || itemId == ItemID.PLATINUM_TOKEN;
    }

    public static String format(final Item item){
        return item != null
                ? item.getDefinition() != null
                ? item.getDefinition().getName()
                : "NO_ITEM_NAME_"+item.getId()
                : "NULL_ITEM";
    }

    public static Item createInvalidItem(){
        return new Item(-1, 0);
    }

    public static boolean isValidItem(final Item item){
        return item != null && item.getId() > 0 && item.getAmount() > 0;
    }

    public static void destroyItemInterface(Player player, Item item) {// Destroy item created by Remco
        if (item.getId() == ItemID.ROTTEN_POTATO) {
            DialogueManager.sendStatement(player, "This item cannot be destroyed.");
            return;
        }
        player.setDestroyItem(item.getId());
        String[][] info = { // The info the dialogue gives
                {"Are you sure you want to discard this item?", "14174"}, {"Yes.", "14175"}, {"No.", "14176"},
                {"", "14177"}, {"This item will vanish once it hits the floor.", "14182"},
                {"You can't get it back if discarded.", "14183"}, {item.getDefinition().getName(), "14184"}};
        player.getPacketSender().sendItemOnInterface(14171, item.getId(), 0, item.getAmount());
        for (String[] strings : info)
            player.getPacketSender().sendString(Integer.parseInt(strings[1]), strings[0]);
        player.getPacketSender().sendChatboxInterface(14170);
    }

    public static boolean isHighValuedItem(String itemName) {
        return itemName.toLowerCase().contains("partyhat")
                || itemName.toLowerCase().contains("h'ween")
                || itemName.toLowerCase().contains("halloween")
                || itemName.toLowerCase().contains("colorful")
                || itemName.toLowerCase().contains("twisted")
                || itemName.toLowerCase().contains("gilded")
                || itemName.toLowerCase().contains("ancestral")
                || itemName.toLowerCase().contains("ballista")
                || itemName.toLowerCase().contains("santa");
    }

    public static boolean logItemIfValuable(final Item item) {
        final long highAlchValue = item.getValue(ItemValueType.HIGH_ALCHEMY);
        final long priceEstValue = item.getValue(ItemValueType.PRICE_CHECKER);
        final long tokenValue = item.getValue(ItemValueType.OSRS_STORE);

        return (highAlchValue * item.getAmount() >= 50000) || (item.getAmount() * priceEstValue >= 5000000)
                || (item.getAmount() * tokenValue > 5000000) || BrokenItems.breaksOnDeath(item.getId())
                || (item.getId() >= 15200 && item.getId() <= 15350)
                || item.getAmount() >= 100000 || item.getAmount() > 1000
                || item.getValue(ItemValueType.ITEM_PRICES) > 100_000 || item.getAmount() > 1000
                || item.getValue(ItemValueType.PRICE_CHECKER) > 50_000_000
                || item.getValue(ItemValueType.OSRS_STORE) * item.getAmount() >= 5_000_000
                || isHighValuedItem(item.getDefinition().getName());
    }
}
