package com.grinder.game.content.minigame.chamberoxeric;

import com.grinder.game.content.minigame.chamberoxeric.party.COXParty;
import com.grinder.game.content.minigame.chamberoxeric.party.syntax.SetPartyCombatLevelEnterSyntax;
import com.grinder.game.content.minigame.chamberoxeric.party.syntax.SetPartySizeEnterSyntax;
import com.grinder.game.content.minigame.chamberoxeric.party.syntax.SetPartySkillTotalEnterSyntax;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Skill;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;
import com.grinder.game.model.ui.UserContainerInterface;
import com.grinder.net.packet.interaction.PacketInteraction;
import com.grinder.util.Misc;

import java.text.NumberFormat;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class COXInterface extends PacketInteraction {

    private static final int MY_PARTY_ID = 65_671;

    private static final int RECRUITING_PARTIES_ID = 65_400;

    private static final int PARTY_MINIMAL_SCROLL_MAX = 256;

    public static final int PARTY_PREFER_SIZE_STRING = 65691;

    public static final int PARTY_PREFER_COMBAT_LEVEL_STRING = 65692;

    public static final int PARTY_PREFER_SKILL_TOTAL_STRING = 65693;

    private static final int PARTY_USERNAME_STRING = 65721;

    private static final int PARTY_COMBAT_STRING = 65722;

    private static final int PARTY_TOTAL_LEVEL_STRING = 65723;

    private static final int PARTY_TOTAL_KILLS_STRING = 65724;

    private static final int INGAME_WIDGET = 23364;

    public static final int REWARD_WIDGET = 23368;

    public static final int REWARD_WIDGET_CONTAINER = REWARD_WIDGET + 5;

    private static final Skill[] SKILL_ORDER = {Skill.ATTACK, Skill.STRENGTH, Skill.RANGED,
            Skill.MAGIC, Skill.DEFENCE, Skill.HITPOINTS, Skill.PRAYER, Skill.AGILITY, Skill.HERBLORE,
            Skill.THIEVING, Skill.CRAFTING, Skill.RUNECRAFTING, Skill.MINING, Skill.SMITHING,
            Skill.FISHING, Skill.COOKING, Skill.FIREMAKING, Skill.WOODCUTTING, Skill.FLETCHING,
            Skill.SLAYER, Skill.FARMING, Skill.CONSTRUCTION, Skill.HUNTER};

    static {
        new UserContainerInterface(COXInterface.REWARD_WIDGET_CONTAINER) {

            @Override
            public boolean handleOption(Player player, int id, int slot, int option) {
                if(player.getInterfaceId() != COXInterface.REWARD_WIDGET) {
                    return false;
                }

                int amount = getAmount(option);
                if(amount == 4) {
                    player.setEnterSyntax(new EnterSyntax() {
                        @Override
                        public void handleSyntax(Player player, String input) {

                        }

                        @Override
                        public void handleSyntax(Player player, int input) {
                            if(input < 0) {
                                return;
                            }
                            player.getCOX().getParty().withdraw(player, player.getCOX().reward, id, input, slot);
                            player.getCOX().reward.refreshItems();
                        }
                    });
                    player.getPacketSender().sendEnterAmountPrompt("How many would you like to withdraw?");
                    return true;
                }

                player.getCOX().getParty().withdraw(player, player.getCOX().reward, id, amount, slot);
                player.getCOX().reward.refreshItems();
                return false;
            }
        };
    }

    public static void sendIngameWidget(Player p) {
        p.getPacketSender().sendString(23366, p.getUsername() + ": @whi@" + Misc.format(p.getCOX().points));
        p.getPacketSender().sendString(23367, "Time: @whi@" + p.getCOX().getParty().time.getTimeElapsed());
        p.getPacketSender().sendWalkableInterface(INGAME_WIDGET);
    }

    public static void openRecruitingBoard(final Player p) {
        p.getPacketSender().clearInterfaceText(65414, 65513);

        int line = 65_414;

        for (COXParty party : COXManager.RAID_PARTIES.values()) {
            p.getPacketSender().sendString(line, party.clanChat.players().size() + " members. " + party.preferCombatlevel + " preferred combat level with " + party.preferSkillTotal + " total level.");
            line++;
        }

        p.getPacketSender().sendInterface(RECRUITING_PARTIES_ID);
    }

    public static void openMyParty(final Player player) {
        if (player.getCOX().getParty() == null) {
            COXManager.makeParty(player);
        }

        if (player.getCOX().getParty() == null) {
            return;
        }

        final COXParty party = player.getCOX().getParty();

        player.getPacketSender().sendString(PARTY_PREFER_SIZE_STRING,
                "Preferred party size: @whi@" + party.preferSize);
        player.getPacketSender().sendString(PARTY_PREFER_COMBAT_LEVEL_STRING,
                "Preferred combat level: @whi@" + party.preferCombatlevel);
        player.getPacketSender().sendString(PARTY_PREFER_SKILL_TOTAL_STRING,
                "Preferred skill total: @whi@" + party.preferSkillTotal);

        sendPlayerList(player);

        int line = 65694;

        for (int i = 0; i < SKILL_ORDER.length; i++) {
            player.getPacketSender().sendString(line + i, "");
        }

        player.getPacketSender().sendInterface(MY_PARTY_ID);
    }

    private static void sendPlayerList(final Player player) {
        if (player.getCurrentClanChat() == null) {
            return;
        }
        int index = 0;
        for (final Player p : player.getCurrentClanChat().players()) {
            if (p == null) {
                continue;
            }
            player.getPacketSender().sendString(PARTY_USERNAME_STRING, "@whi@" + p.getUsername());
            player.getPacketSender().sendString(PARTY_COMBAT_STRING + index,
                    "@or1@" + p.getSkillManager().calculateCombatLevel() + "");
            player.getPacketSender().sendString(PARTY_TOTAL_LEVEL_STRING + index, "@or1@"
                    + NumberFormat.getInstance().format(p.getSkillManager().countTotalLevel()) + "");
            player.getPacketSender().sendString(PARTY_TOTAL_KILLS_STRING + index,
                    "@or1@" + p.getPoints().get(AttributeManager.Points.RAID_TOTAL_KILLS) + "");

            index += 7;
        }
        for (int i = PARTY_USERNAME_STRING + index; i < PARTY_USERNAME_STRING + 137; i++) {
            player.getPacketSender().sendString(i, "");
        }
        player.getPacketSender().sendScrollbarHeight(PARTY_MINIMAL_SCROLL_MAX,
                255 + 16 * player.getCurrentClanChat().players().size());
    }

    private static void sendDisplayStats(Player player, int index) {
        if (index >= player.getCurrentClanChat().players().size()) {
            return;
        }

        final Player p = player.getCurrentClanChat().players().get(index);

        if (p == null) {
            return;
        }

        int line = 65694;

        for (int i = 0; i < SKILL_ORDER.length; i++) {
            player.getPacketSender().sendString(line + i,
                    "@or1@" + p.getSkillManager().getCurrentLevel(SKILL_ORDER[i]));
        }
    }

    @Override
    public boolean handleButtonInteraction(Player player, int button) {
        if (button >= PARTY_USERNAME_STRING - 3 && button <= PARTY_USERNAME_STRING + 133) {
            if (player.getCurrentClanChat() == null) {
                return false;
            }
            int index = button - (PARTY_USERNAME_STRING - 3);
            if (index > 0) {
                index /= 7;
            }
            sendDisplayStats(player, index);
            return true;
        }
        switch (button) {
            case 65410:
            case 65687:
                openMyParty(player);
                return true;
            case 65406:
            case 65683:
                openRecruitingBoard(player);
                return true;
            case 65679:
                COXManager.disbandParty(player);
                return true;
            case PARTY_PREFER_SIZE_STRING:
                player.setEnterSyntax(new SetPartySizeEnterSyntax());
                player.getPacketSender().sendEnterAmountPrompt("Set a party preferred size:");
                return true;
            case PARTY_PREFER_COMBAT_LEVEL_STRING:
                player.setEnterSyntax(new SetPartyCombatLevelEnterSyntax());
                player.getPacketSender().sendEnterAmountPrompt("Set a party preferred combat level:");
                return true;
            case PARTY_PREFER_SKILL_TOTAL_STRING:
                player.setEnterSyntax(new SetPartySkillTotalEnterSyntax());
                player.getPacketSender()
                        .sendEnterAmountPrompt("Set a party preferred total skill level:");
                return true;
        }
        return false;
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (object.getId()) {
            case 29776:
                openRecruitingBoard(player);
                return true;
        }
        return false;
    }
}
