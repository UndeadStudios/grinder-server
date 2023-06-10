package com.grinder.game.model.item.container;

import com.grinder.game.model.item.Item;

import static com.grinder.util.ItemID.BLOOD_MONEY;
import static com.grinder.util.ItemID.COINS;

/**
 * I hate this
 */
public final class ItemTransformer {

    public static void transformItemIdAndAmount(Item item) {
        int amount = item.getAmount();
        if (item.getId() == 8322) {
            item.setId(BLOOD_MONEY);
            amount = item.getAmount() * 10000;
        } else if (item.getId() == 8465) {
            item.setId(COINS);
            amount = item.getAmount() * 1000000;
        } else if (item.getId() == 15198) {
            item.setId(COINS);
            amount = item.getAmount() * 10000000;
        } else if (item.getId() == 15199) {
            item.setId(4835);
            amount = item.getAmount() * 25;
        } else if (item.getId() == 14158) {
            item.setId(22125);
            amount = item.getAmount() * 50;
        } else if (item.getId() == 14159) {
            item.setId(13442);
            amount = item.getAmount() * 250;
        } else if (item.getId() == 14160) {
            item.setId(21326);
            amount = item.getAmount() * 500;
        } else if (item.getId() == 14161) {
            item.setId(21905);
            amount = item.getAmount() * 500;
        } else if (item.getId() == 14162) {
            item.setId(386);
            amount = item.getAmount() * 1000;
        }
        if(amount != item.getAmount())
            item.setAmount(amount);
    }
}
