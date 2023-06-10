package com.grinder.game.content.clan;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-15
 */
public enum ClanChatAction {
    ENTER,
    PROMOTE,
    KICK,
    TALK,
    BAN;

    public static ClanChatAction forButton(final int buttonId){
        switch (buttonId){
            case 43707: return ENTER;
            case 43710: return TALK;
            case 43713: return KICK;
            case 43716: return BAN;
        }
        return null;
    }

    public static boolean canChangeSetting(ClanChatAction action, ClanChatRank rank){
        switch (action){
            case ENTER:
            case BAN:
            case TALK:
            case KICK:
            case PROMOTE:
                return rank == ClanChatRank.STAFF || rank == ClanChatRank.OWNER;
        }
        return true;
    }

    public static boolean canApply(ClanChatAction action, ClanChatRank rank){
        switch (action){
            case BAN:
            case KICK:
            case PROMOTE:
                return rank == ClanChatRank.STAFF || rank == ClanChatRank.OWNER;
        }
        return true;
    }
}
