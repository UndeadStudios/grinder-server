package com.grinder.game.model.item;

import java.util.HashMap;

import com.grinder.game.definition.ItemValueType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.util.ItemID;

public enum BloodItemPrices {

    BLOOD_MONEY(ItemID.BLOOD_MONEY, 1),
    DRAGON_DEFENDER(12954, 12000),
    BARROWS_GLOVES(7462, 3000),
    FIRE_CAPE(6570, 15000),
    HEALER_HAT(10547, 1000),
    FIGHTER_HAT(10548, 1000),
    RUNNER_HAT(10549, 1000),
    RANGER_HAT(10550, 1000),
    FIGHTER_TORSO(10551, 14000),
    VOID_MELEE_HELM(11665, 15000),
    VOID_RANGER_HELM(11664, 15000),
    VOID_MAGE_HELM(11663, 15000),
    VOID_TOP(8839, 30000),
    VOID_ROBE(8840, 30000),
    VOID_GLOVES(8842, 1000),
    ELITE_VOID_TOP(13072, 65000),
    ELITE_VOID_ROBE(13073, 65000),
    SLAYER_HELMET(11864, 15000),
    RUNE_POUCH(12791, 25000),
    LOOTING_BAG(11941, 25000),
    BOOK_OF_DARKNESS(12610, 3000),
    BOOK_OF_LAW(12612, 2000),
    LIGHT_BALLISTA(19478, 3000),
    HEAVY_BALLISTA(19481, 3000),
    GRANITE_CLAMP(12849, 25000),
    GRANITE_MAUL_OR(12848, 25000),
    ARDOUGNE_CAPE_1(13121, 5000),
    ARDOUGNE_CAPE_2(13122, 5000),
    ARODUGNE_CAPE_3(13123, 5000),
    ARDOUGNE_CAPE_4(13124, 5000);

    public static HashMap<Integer, BloodItemPrices> blooditem = new HashMap<>();

    static {
        for (final BloodItemPrices f : BloodItemPrices.values())
            blooditem.put(f.getId(), f);
    }

    private final int id;
    private final int price;

    BloodItemPrices(final int id, final int price) {
        this.id = id;
        this.price = price;
    }

    /**
     * Gets the total cost of blood money carried in player's equipment and
     * inventory.
     */
    public static long getItemsWorthInBloodMoney(Player player) {
        long cost = 0;
        for (Item item : player.getInventory().getItems()) {
            cost += item.getValue(ItemValueType.PRICE_CHECKER) * item.getAmount();
        }
        for (Item item : player.getEquipment().getItems()) {
            cost += item.getValue(ItemValueType.PRICE_CHECKER) * item.getAmount();
        }
        cost /= 1000;
        if (cost >= Integer.MAX_VALUE || cost < 0) {
            cost = Integer.MAX_VALUE;
        }
        return cost;
    }

    public int getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }
}
