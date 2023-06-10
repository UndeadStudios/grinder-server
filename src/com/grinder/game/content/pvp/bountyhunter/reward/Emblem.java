package com.grinder.game.content.pvp.bountyhunter.reward;

import com.grinder.game.model.item.Item;

public enum Emblem {
    MYSTERIOUS_EMBLEM_1(12746, 1000),
    MYSTERIOUS_EMBLEM_2(12748, 2000),
    MYSTERIOUS_EMBLEM_3(12749, 4000),
    MYSTERIOUS_EMBLEM_4(12750, 8000),
    MYSTERIOUS_EMBLEM_5(12751, 11000),
    MYSTERIOUS_EMBLEM_6(12752, 17000),
    MYSTERIOUS_EMBLEM_7(12753, 25000),
    MYSTERIOUS_EMBLEM_8(12754, 33500),
    MYSTERIOUS_EMBLEM_9(12755, 41000),
    MYSTERIOUS_EMBLEM_10(12756, 50000)
    ;

    private int id;
    
    private int value;
    
    Emblem(int id, int value) {
        this.id = id;
        this.value = value;
    }

    public static boolean isMysteriousEmblem(int id) {
        return id == MYSTERIOUS_EMBLEM_1.getId() ||
                id == MYSTERIOUS_EMBLEM_2.getId() ||
                id == MYSTERIOUS_EMBLEM_3.getId() ||
                id == MYSTERIOUS_EMBLEM_4.getId() ||
                id == MYSTERIOUS_EMBLEM_5.getId() ||
                id == MYSTERIOUS_EMBLEM_6.getId() ||
                id == MYSTERIOUS_EMBLEM_7.getId() ||
                id == MYSTERIOUS_EMBLEM_8.getId() ||
                id == MYSTERIOUS_EMBLEM_9.getId() ||
                id == MYSTERIOUS_EMBLEM_10.getId();
    }

    public static boolean isDropped(Item item){
        return isMysteriousEmblem(item.getId()) && item.getId() != MYSTERIOUS_EMBLEM_1.id;
    }

    public static Item getLesserChargedEmblem(Item item){
        return new Item(item.getId() == Emblem.MYSTERIOUS_EMBLEM_2.getId()
                ? item.getId() - 2
                : item.getId() - 1);
    }

    public static Item getHigherChargedEmblem(Item item){
        if (item.getId() == Emblem.MYSTERIOUS_EMBLEM_10.getId()) {
            return new Item(Emblem.MYSTERIOUS_EMBLEM_10.getId());
        }
        return new Item(item.getId() == Emblem.MYSTERIOUS_EMBLEM_2.getId()
                ? item.getId() + 2
                : item.getId() + 1);
    }

    public int getId() {
    	return id;
    }
    
    public int getValue() {
    	return value;
    }
}
