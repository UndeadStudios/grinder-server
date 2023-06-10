package com.grinder.game.content.clan;

import com.grinder.util.Misc;

public enum ClanChatRank {

	ANYONE(-1, -1),
    FRIEND(-1, 197),
    RECRUIT(0, 198),
    CORPORAL(1, 199),
    SERGEANT(2, 200),
    LIEUTENANT(3, 201),
    CAPTAIN(4, 202),
    GENERAL(5, 203),
    OWNER(6, 204),
    STAFF(-1, 203);

    private final int actionMenuId;
    private final int spriteId;

    ClanChatRank(int actionMenuId, int spriteId) {
        this.actionMenuId = actionMenuId;
        this.spriteId = spriteId;
    }

    public static ClanChatRank forId(int id) {
        for (ClanChatRank rank : ClanChatRank.values()) {
            if (rank.ordinal() == id) {
                return rank;
            }
        }
        return ANYONE;
    }

    public static ClanChatRank forMenu(int menuId){
        ClanChatRank rank = null;

        switch (menuId) {
            case 0: rank = ClanChatRank.ANYONE;break;
            case 1: rank = ClanChatRank.FRIEND;break;
            case 2: rank = ClanChatRank.RECRUIT;break;
            case 3: rank = ClanChatRank.CORPORAL;break;
            case 4: rank = ClanChatRank.SERGEANT;break;
            case 5: rank = ClanChatRank.LIEUTENANT;break;
            case 6: rank = ClanChatRank.CAPTAIN;break;
            case 7: rank = ClanChatRank.GENERAL;break;
            case 8: rank = ClanChatRank.OWNER;break;
        }
        return rank;
    }

    public String format(){
        if(this == OWNER)
            return "Only me";
        if(this == ANYONE)
            return "Anyone";
        return Misc.ucFirst(this.toString().toLowerCase()) + "+";
    }

    public boolean canBeChanged(){
        return this != OWNER;
    }

    public int getSpriteId() {
        return spriteId;
    }
}
