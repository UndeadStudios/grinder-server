package com.grinder.game.content.clan;

import com.grinder.game.entity.agent.player.Player;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-15
 */
public enum ClanMemberAction {

    KICK(-1, 0){
        @Override
        public void execute(final Player initiator, ClanChat clan, ClanMember clanMember) {
            clanMember.ifOnline(player -> GlobalClanChatManager.kick(initiator, clan, player));
        }
    },
    DEMOTE(0, 1){
        @Override
        public void execute(final Player initiator, ClanChat clan, ClanMember clanMember) {
            GlobalClanChatManager.demote(initiator, clan, clanMember);
        }
    },
    BAN(1, 2) {
        @Override
        public void execute(Player initiator, ClanChat clan, ClanMember clanMember) {
            GlobalClanChatManager.ban(initiator, clan, clanMember);
        }
    },
    PROMOTE_TO_RECRUIT(2,3) {
        @Override
        public void execute(Player initiator, ClanChat clan, ClanMember clanMember) {
            promote(initiator, clan, clanMember, ClanChatRank.RECRUIT);
        }
    },
    PROMOTE_TO_CORPORAL(3, 4) {
        @Override
        public void execute(Player initiator, ClanChat clan, ClanMember clanMember) {
            promote(initiator, clan, clanMember, ClanChatRank.CORPORAL);
        }
    },
    PROMOTE_TO_SERGEANT(4, 5) {
        @Override
        public void execute(Player initiator, ClanChat clan, ClanMember clanMember) {
            promote(initiator, clan, clanMember, ClanChatRank.SERGEANT);
        }
    },
    PROMOTE_TO_LIEUTENANT(5, 6) {
        @Override
        public void execute(Player initiator, ClanChat clan, ClanMember clanMember) {
            promote(initiator, clan, clanMember, ClanChatRank.LIEUTENANT);
        }
    },
    PROMOTE_TO_CAPTAIN(6, 7) {
        @Override
        public void execute(Player initiator, ClanChat clan, ClanMember clanMember) {
            promote(initiator, clan, clanMember, ClanChatRank.CAPTAIN);
        }
    },
    PROMOTE_TO_GENERAL(7, 8) {
        @Override
        public void execute(Player initiator, ClanChat clan, ClanMember clanMember) {
            promote(initiator, clan, clanMember, ClanChatRank.GENERAL);
        }
    };

    private final int setupMenuId;
    private final int tabMenuId;

    ClanMemberAction(int setupMenuId, int tabMenuId) {
        this.setupMenuId = setupMenuId;
        this.tabMenuId = tabMenuId;
    }

    public abstract void execute(final Player initiator, final ClanChat clan, final ClanMember clanMember);

    public static ClanMemberAction forSetupMenu(int menuId){
        for(ClanMemberAction action : values()){
            if(action.setupMenuId == menuId)
                return action;
        }
        return null;
    }

    public static ClanMemberAction forTabMenu(int menuId){
        for(ClanMemberAction action : values()){
            if(action.tabMenuId == menuId)
                return action;
        }
        return null;
    }

    public static void promote(final Player initiator, final ClanChat clanChat, final ClanMember target, final ClanChatRank rank){

        if(target.getRank() == rank){
            initiator.sendMessage("That player already has that rank.");
            return;
        }

        if(!target.getRank().canBeChanged()){
            initiator.sendMessage("That player can't be promoted or demoted.");
            return;
        }

        target.setRank(rank);
        target.ifOnline(targetPlayer -> GlobalClanChatManager.updateRank(clanChat, targetPlayer));

        clanChat.kickIfNoPermissions(target);
        clanChat.setRequiredUpdate(true);

        if (initiator.getInterfaceId() == ClanChatConstants.CLAN_SETUP_INTERFACE_ID)
            ClanChatSetup.open(initiator, clanChat);
    }

    public boolean requiresOwnerRights(){
        return this != BAN;
    }

}
