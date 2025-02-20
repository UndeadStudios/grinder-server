package com.grinder.game.task.impl;

import java.util.ArrayList;
import java.util.List;

import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.shop.Shop;
import com.grinder.game.model.item.container.shop.ShopManager;
import com.grinder.game.task.Task;
import com.grinder.util.Misc;
import com.grinder.util.ShopIdentifiers;

public class ShopRestockTask extends Task {

    private final Shop shop;

    public ShopRestockTask(Shop shop) {
        super(shop.getId() == ShopIdentifiers.GENERAL_STORE ? 500 : 10);
        this.shop = shop;
    }

    private static int restockCalc(int overflow, int curr) {
    //    int missing = overflow - curr;
        //int amount = (int) (missing + 1);
/*        if (amount < 1) {
            amount = 1;
        }*/
        return 1;
    }

    @Override
    protected void execute() {

        if (shop.isLimitedShop()) {
            return;
        }

        List<Integer> items = new ArrayList<Integer>();
        for (Item item : Misc.concat(shop.getCurrentStock(), shop.getOriginalStock())) {
            if (item == null)
                continue;
            int itemId = item.getId();
            if (!items.contains(itemId)) {
                items.add(itemId);
            }
        }

        boolean performedUpdate = false;

        for (int itemId : items) {
            int originalAmount = shop.getAmount(itemId, true);
            int currentAmount = shop.getAmount(itemId, false);

            // If we have too many in stock, delete some..
            if (currentAmount > originalAmount) {
                shop.removeItem(itemId, shop.getId() == ShopIdentifiers.GENERAL_STORE && shop.getAmount(itemId, true) == 0 ? 1 : restockCalc(currentAmount, originalAmount));
                performedUpdate = true;
            }

            // If we have too few in stock, add some..
            else if (currentAmount < originalAmount) {
                if (ShopManager.restocksItem(shop.getId())) {
                    shop.addItem(itemId, restockCalc(originalAmount, currentAmount));
                    performedUpdate = true;
                }
            }
        }

        if (performedUpdate) {
            ShopManager.refresh(shop);
        } else {
            stop();
        }
    }

    @Override
    public void stop() {
        setEventRunning(false);
        shop.setRestocking(false);
    }
}
