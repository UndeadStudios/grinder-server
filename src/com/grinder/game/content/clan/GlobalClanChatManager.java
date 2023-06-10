package com.grinder.game.content.clan;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.hit.damage.TotalTrackedDamage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerSaving;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.interfaces.syntax.impl.ChangeClanChatName;
import com.grinder.game.model.interfaces.syntax.impl.EnterClanBan;
import com.grinder.game.model.interfaces.syntax.impl.EnterClanPromote;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.message.MessageFilterManager;
import com.grinder.game.model.message.MessageType;
import com.grinder.game.service.ServiceManager;
import com.grinder.game.service.tasks.TaskRequest;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static com.grinder.game.content.clan.ClanChatAction.*;
import static com.grinder.game.content.clan.ClanChatConstants.*;
import static com.grinder.game.content.clan.ClanChatRank.*;

public class GlobalClanChatManager {

    private static final Logger LOGGER = LogManager.getLogger(GlobalClanChatManager.class.getSimpleName());
    private static final ClanChat[] clans = new ClanChat[3000];

    public static void init() {
        try {
            final File dir = (new File(FILE_DIRECTORY));
            if (!dir.exists()) {
                if(dir.mkdir())
                    LOGGER.info("Created save directory {"+FILE_DIRECTORY+"}");
            }
            final File[] clanFiles = dir.listFiles();
            if(clanFiles == null)
                return;
            for (File file : clanFiles) {
                if (!file.exists())
                    continue;

                if (file.getName().endsWith(".json"))
                    continue;

                DataInputStream input = new DataInputStream(new FileInputStream(file));
                String name = input.readUTF();
                String owner = input.readUTF();
                int index = input.readShort();

                final ClanChat clan = new ClanChat(owner, name, index);

                clan.setRequirement(ENTER, ClanChatRank.forId(input.read()));
                clan.setRequirement(KICK, ClanChatRank.forId(input.read()));
                clan.setRequirement(TALK, ClanChatRank.forId(input.read()));
                clan.setRequirement(BAN, ClanChatRank.forId(input.read()));
                clan.setRequirement(PROMOTE, ClanChatRank.forId(input.read()));

                int totalRanks = input.readShort();

                for (int i = 0; i < totalRanks; i++) {
                    final String memberName = input.readUTF();
                    final ClanChatRank memberRank = ClanChatRank.forId(input.read());
                    final ClanMember clanMember = new ClanMember(memberName, memberRank);
                    clan.addMember(clanMember);
                }

                int totalBans = input.readShort();
                for (int i = 0; i < totalBans; i++) {
                    clan.ban(input.readUTF());
                }
                clans[index] = clan;

                input.close();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static void writeFile(final ClanChat clan) {

        if (clan.getIsSaving().get())
            return;

        clan.getIsSaving().set(true);

        ServiceManager.INSTANCE
                .getTaskService()
                .addTaskRequest(new SaveClanChatTask(clan));
    }

    static class SaveClanChatTask extends TaskRequest {

        public SaveClanChatTask(@NotNull ClanChat clan) {
            super(() -> {
                try {

//                    ClanChatSerializer.save(clan);

                    File file = new File(FILE_DIRECTORY + clan.getName());

                    if (!file.exists()) {
                        if (!file.createNewFile()){
                            LOGGER.error("Failed to create save file at {"+file+"} for clan chat!");
                            return;
                        }
                    }

                    DataOutputStream output = new DataOutputStream(new FileOutputStream(file));
                    output.writeUTF(clan.getName());
                    output.writeUTF(clan.getOwnerName());
                    output.writeShort(clan.getIndex());
                    output.write(clan.findRequirement(ENTER).map(ClanChatRank::ordinal).orElse(-1));
                    output.write(clan.findRequirement(KICK).map(ClanChatRank::ordinal).orElse(-1));
                    output.write(clan.findRequirement(TALK).map(ClanChatRank::ordinal).orElse(-1));
                    output.write(clan.findRequirement(BAN).map(ClanChatRank::ordinal).orElse(-1));
                    output.write(clan.findRequirement(PROMOTE).map(ClanChatRank::ordinal).orElse(-1));
                    output.writeShort(clan.members().size());

                    for (final ClanMember clanMember : clan.members()) {
                        output.writeUTF(clanMember.getName());
                        output.write(clanMember.getRank().ordinal());
                    }

                    output.writeShort(clan.bannedMembers().size());
                    for (ClanChatBan ban : clan.bannedMembers()) {
                        output.writeUTF(ban.getName());
                    }
                    output.close();
                } catch (IOException e) {
                    LOGGER.error("Failed to save clan chat!", e);
                } finally {
                    clan.getIsSaving().set(false);
                }
            }, true);
        }
    }

    public static void save() {
        for (ClanChat clan : clans) {
            if (clan != null) {
                writeFile(clan);
            }
        }
    }

    public static void save(final ClanChat clan) {
        writeFile(clan);
    }

    public static ClanChat create(final Player player, final String name) {

        final int clanIndex = freeIndex();

        if (clanIndex == -1) {
            player.sendMessage("An error occured! Please contact an administrator and report this.");
            return null;
        }

        clans[clanIndex] = new ClanChat(player, name, clanIndex);
        clans[clanIndex].init();
        return clans[clanIndex];
    }

    public static void joinDefaultClan(final Player player) {
        join(player, DEFAULT_CLAN_NAME);
    }

    public static void join(final Player player, final String channelName) {

        if (!ClanChatUtil.isValidChannelName(channelName))
            return;

        if (player.getCurrentClanChat() != null) {
            player.sendMessage("You're already in a clan channel.");
            return;
        }

        if (getClanChatOf(player) == null && channelName.equalsIgnoreCase(player.getUsername())) {
            player.sendMessage("Note: You can start a clan by setting its name via the clan setup interface.");
            return;
        }

        long start = System.nanoTime();
        for (final ClanChat clan : clans) {

            if (clan == null)
                continue;

            if (clan.getName().equalsIgnoreCase(channelName)) {
                join(player, clan);
                return;
            }
        }

        player.sendMessage("That channel does not exist.");
    }

    public static void sendMessage(final Player player, final String message) {

        final String username = player.getUsername();

        if (player.isMuted()) {
            player.sendMessage("You're muted and therefore can't chat.");
            return;
        }

        final ClanChat clanChat = player.getCurrentClanChat();

        if (clanChat == null) {
            player.sendMessage("You're not in a clan chat channel.");
            return;
        }
        if (message.toLowerCase().contains("<img")) {
            return;
        }

        /*if (!PlayerUtil.isStaff(player)) {

            if (clanChat.getName().toLowerCase().equals("help")) {
                if (Misc.blockedWord(message)) {
                    DialogueManager.sendStatement(player, "A word was blocked in your sentence. Please do not repeat it!");
                    return;
                }

                if (message.toLowerCase().contains("selling") || message.toLowerCase().contains("buying") || message.toLowerCase().contains("sale") || message.toLowerCase().contains("b sale") || message.toLowerCase().contains("buy ")) {
                    DialogueManager.sendStatement(player, "Buying and Selling over the 'Help' clan chat is not allowed. Please consider using a different clan/yell for that!");
                    player.sendMessage("<img=779> @red@You can buy Yell-tokens from the General store at home area. Member's can yell for free.");
                    //return;
                }

                if (message.toLowerCase().contains("how much is") || message.toLowerCase().contains("how much does") || message.toLowerCase().contains("howmuch ")) {
                    DialogueManager.sendStatement(player, "@whi@Just a quick tip:</col> You can check all the prices in fraction of a second by using the ::prices guide!");
                    // return;
                }

                if (((message.toLowerCase().contains("teleport") || message.toLowerCase().contains("tele")) && (message.toLowerCase().contains("slayer") || message.toLowerCase().contains(" task")))
                        || (message.toLowerCase().contains("where") && message.toLowerCase().contains("find"))) {
                    DialogueManager.sendStatement(player, "@whi@Tip:</col> You can buy an enchanted gem from any Slayer Master and use it to teleport to your Slayer task.");
                    //return;
                }

                if ((message.toLowerCase().contains("setup") || message.toLowerCase().contains("create") || message.contains("make")) && (message.toLowerCase().contains("bankpin") || message.toLowerCase().contains("bank pin"))) {
                    DialogueManager.sendStatement(player, "You can setup a bank PIN by speaking to Security Guard north of Home area.");
                    // return;
                }

                if (message.toLowerCase().contains("how ") && (message.toLowerCase().contains("slay") || message.toLowerCase().contains("kill"))) {
                    DialogueManager.sendStatement(player, "Make sure to equip right gear for Slayer monsters, and for bossing guides check out the Wiki for quick tips.");
                    //return;
                }

                if ((message.toLowerCase().contains("how ") || message.toLowerCase().contains("where ")) && (message.toLowerCase().contains("password") || message.toLowerCase().contains("pass"))) {
                    DialogueManager.sendStatement(player, "@whi@Quick Tip:</col> You can change your password from your quest tab. Make sure to unique one to be safe from leaked databases.");
                    //return;
                }
            }
        }*/

        player.getPoints().increase(AttributeManager.Points.CLAN_CHAT_MESSAGES, 1); // Increase points

        MessageFilterManager.INSTANCE.reportMessage(player, message, MessageType.CLAN_CHAT, clanChat.getName());

        final ClanChatRank rank = clanChat.getRank(player);

        if (!clanChat.hasPermission(TALK, rank)) {
            player.sendMessage("You do not have the required rank to speak in this channel.");
            return;
        }

        final String clanName = Misc.capitalizeWords(clanChat.getName());
        final String rankImage = rank.getSpriteId() != -1 ? " <clan=" + rank.getSpriteId() + ">" : "";

        for (final Player memberPlayer : clanChat.players()) {

            if (memberPlayer != null) {

                if (memberPlayer.getRelations().getIgnoreList().contains(player.getLongUsername()))
                    continue;

                final String rankIcon = PlayerUtil.getImages(player);

                memberPlayer.getPacketSender().sendSpecialMessage(username, 9, BRACKET_COLOR + "[" + CLAN_NAME_COLOR + clanName + rankImage + BRACKET_COLOR + "]" + " " + rankIcon + NAME_COLOR + Misc.capitalizeWords(username) + ": " + CHAT_COLOR + Misc.capitalize(message));
            }
        }
    }

    public static void leave(final Player player, final boolean kicked) {

        final ClanChat clanChat = player.getCurrentClanChat();

        if (clanChat == null)
            return;

        clanChat.removeActiveMember(player);

        player.setCurrentClanChat(null);
        player.getPacketSender().sendShowClanChatOptions(false);

        clanChat.setRequiredUpdate(true);

        ClanChatUtil.uponLeave(player, clanChat);
        ClanChatUtil.resetInterface(player);

        if (kicked)
            player.setClanChatName("");

        player.sendMessage(kicked ? "You have been kicked from the channel." : "You have left the channel.");

    }

    private static void join(final Player player, final ClanChat clan) {

        if (clan.isOwner(player)) {

            if (clan.getOwner() == null)
                clan.setOwnerPlayerInstance(player);

            clan.find(player).ifPresent(clanMember -> clanMember.setRank(OWNER));
        }

        player.sendMessage("Attempting to join channel...");

        if (clan.players().size() >= MAX_MEMBERS) {
            player.sendMessage("This clan channel is currently full.");
            return;
        }

        if (clan.isBanned(player.getUsername())) {
            player.sendMessage("You're currently banned from using this channel. Bans expire after 60 minutes.");
            return;
        }

        updateRank(clan, player);

        final String clanName = Misc.capitalizeWords(clan.getName());
        final ClanChatRank rank = clan.getRank(player);

        if (!clan.getName().toLowerCase().equals(DEFAULT_CLAN_NAME)) {
            if (!clan.hasPermission(ENTER, rank)) {
                player.sendMessage("Your rank is not high enough to enter this channel.");
                return;
            }
        }

        player.setCurrentClanChat(clan);
        player.setClanChatName(clan.getName());

        clan.addActiveMember(player);

        player.getPacketSender().sendString(CLAN_NAME_ID, "Talking in: <col=ffff64>" + clanName);
        player.getPacketSender().sendString(CLAN_OWNER_ID, "Owner: @whi@" + Misc.capitalizeWords(clan.getOwnerName()));
        player.getPacketSender().sendString(CLAN_JOIN_ID, "Leave Chat");
        player.getPacketSender().sendConfig(LOOT_SHARING_CONFIG_ID, clan.isLootSharingEnabled() ? 1 : 0);

        player.sendMessage("Now talking in " + clan.getOwnerName() + "'s channel.");
        player.sendMessage("To talk start each line of chat with the / symbol.");

        ClanChatUtil.uponJoin(player, clan);
        clan.setRequiredUpdate(true);
    }

    public static void delete(final Player player) {

        final ClanChat clan = getClanChatOf(player);

        if (clan == null) {
            player.sendMessage("You do not currently have a clan chat.");
            return;
        }

        final File file = new File(FILE_DIRECTORY + clan.getName());

        for (Player member : clan.players()) {
            if (member != null)
                leave(member, false);
        }

        if (player.getClanChatName() != null && player.getClanChatName().equalsIgnoreCase(clan.getName()))
            player.setClanChatName("");

        clans[clan.getIndex()] = null;

        file.delete();

        if (player.getInterfaceId() == CLAN_SETUP_INTERFACE_ID)
            ClanChatSetup.open(player, clan);
    }

    static void updateRank(final ClanChat clan, Player targetPlayer) {
        if (!clan.find(targetPlayer).isPresent())
            clan.addMember(new ClanMember(targetPlayer.getUsername(), ANYONE));

        clan.find(targetPlayer).ifPresent(clanMember -> {

            final ClanChatRank rank = clanMember.getRank();
            final Player owner = clan.getOwner();

            if (owner != null) {
                if (owner.getRelations().isFriendWith(targetPlayer.getUsername())) {
                    if (rank == ANYONE) {
                        clanMember.setRank(FRIEND);
                        //ClanChatUtil.updateInterfacesForMembers(clan);
                    }
                } else {
                    if (rank == ClanChatRank.FRIEND) {
                        clanMember.setRank(ANYONE);
                        //ClanChatUtil.updateInterfacesForMembers(clan);
                    }
                }
            }
            if (clanMember.getRank() == RECRUIT) {
            	AchievementManager.processFor(AchievementType.IMPORTANT_PERSON, targetPlayer);
            }
            if (PlayerUtil.isStaff(targetPlayer)) {
                    clanMember.setRank(STAFF);
            }
            if (clanMember.getRank() == ANYONE) {
                if (PlayerUtil.isStaff(targetPlayer)) {
                    clanMember.setRank(STAFF);
                    //ClanChatUtil.updateInterfacesForMembers(clan);
                } else {
                    clanMember.setRank(ANYONE);
                   // ClanChatUtil.updateInterfacesForMembers(clan);
                }
            }
        });
    }

    public static void setName(final Player player, String newName) {
        if (PlayerSaving.playerExists(newName)) {
            player.sendMessage("That clan chat name is already taken.");
            return;
        }

        newName = newName.toLowerCase();

        for (ClanChat c : clans) {
            if (c == null)
                continue;
            if (c.getName().toLowerCase().equals(newName)) {
                player.sendMessage("That clan chat name is already taken.");
                return;
            }
        }

        ClanChat clan = getClanChatOf(player);

        if (clan == null) {

            clan = create(player, newName);

            if(clan == null)
                return;

            if (player.getCurrentClanChat() == null)
                join(player, clan);

            if (player.getInterfaceId() == CLAN_SETUP_INTERFACE_ID)
                ClanChatSetup.open(player, clan);
        }

        if (clan.getName().toLowerCase().equals(newName))
            return;

        // Not sure when the method ever gets past previous return to get to here..?
        new File(FILE_DIRECTORY + clan.getName()).delete();
        clan.setName(Misc.capitalizeWords(newName));
        for (Player member : clan.players()) {
            if (member == null)
                continue;
            member.setClanChatName(clan.getName());
            member.getPacketSender().sendString(CLAN_NAME_ID, "Talking in: @whi@" + clan.getName());
        }

        writeFile(clan);

        if (player.getCurrentClanChat() == null) {
            join(player, clan);
        }
        if (player.getInterfaceId() == CLAN_SETUP_INTERFACE_ID) {
            ClanChatSetup.open(player, clan);
        }
    }

    public static void kick(final Player initiator, final ClanChat clan, final Player target) {

        if (clan == null) {
            initiator.sendMessage("You're not in a clan channel.");
            return;
        }

        final ClanChatRank initiatorRank = clan.getRank(initiator);

        if (!clan.hasPermission(KICK, initiatorRank)) {
            initiator.sendMessage("You do not have the required rank to kick this player.");
            return;
        }

        for (final Player member : clan.players()) {

            if (member != null && member.equals(target)) {

                final ClanChatRank memberRank = clan.getRank(member);

                if (memberRank != null) {

                    /*if (memberRank == ClanChatRank.STAFF) {
                        initiator.getPacketSender().sendMessage("That player can't be kicked.");
                        break;
                    }*/

                    if (initiatorRank.ordinal() < memberRank.ordinal()) {
                        initiator.sendMessage("You can't kick a player who has a higher rank than you!");
                        break;
                    }
                }
                leave(member, true);
                sendMessage(initiator, "Attempting to kick " + member.getUsername() + " from clan chat...");
                break;
            }
        }
    }

    public static void promote(final Player player, String targetName) {
        targetName = Misc.formatName(targetName);

        final ClanChat clan = player.getCurrentClanChat();

        if (clan == null) {
            player.sendMessage("You're not in a clan channel.");
            return;
        }

        final Optional<ClanMember> optionalClanMember = clan.find(player);

        if (!optionalClanMember.isPresent()) {
            player.sendMessage("Could not find you in the clan!");
            return;
        }

        final ClanMember member = optionalClanMember.get();

        if (!clan.hasPermission(PROMOTE, member.getRank())) {
            player.sendMessage("You do not have the required rank to promote players.");
            return;
        } /*else if (!clan.isOwner(member)) {
            player.sendMessage("Only the clan chat owner can do that.");
            return;
        }*/

        if (player.getUsername().equalsIgnoreCase(targetName)) {
            player.sendMessage("You can't promote yourself.");
            return;
        }

        final Optional<ClanMember> optionalTarget = clan.findByName(targetName);

        if (!optionalTarget.isPresent()) {
            player.sendMessage("Could not find a clan member by the name of '" + targetName + "'.");
            return;
        }

        final ClanMember target = optionalTarget.get();

        if (target.getRank() != ANYONE) {
            player.sendMessage("That player is already ranked.");
            return;
        }
        if (target.getRank() == ClanChatRank.RECRUIT) {
        	AchievementManager.processFor(AchievementType.IMPORTANT_PERSON, player);
        }
        target.setRank(RECRUIT);
        clan.setRequiredUpdate(true);
        ClanChatSetup.updateMembers(player, clan);
    }

    public static void ban(final Player player, final ClanChat clan, final ClanMember target) {

        if (clan == null) {
            player.getPacketSender().sendMessage("You're not in a clan channel.", 1000);
            return;
        }
        //System.out.println("HERE");
        final ClanChatRank rank = clan.getRank(player);

        if (!clan.hasPermission(BAN, rank)) {
            player.getPacketSender().sendMessage("You do not have the required rank to ban this player.", 1000);
            return;
        } else if (rank != OWNER && rank != STAFF && rank != GENERAL) {
            player.getPacketSender().sendMessage("Only the clan chat owner can do that.", 1000);
            return;
        }

        if (clan.isOwner(target)) {
            player.sendMessage("That player can not be banned.");
            return;
        }

        if (clan.isBanned(target)) {
            player.sendMessage("That player is already banned.");
            return;
        }

        clan.removeMember(target);
        clan.ban(target);

        ClanChatSetup.updateMembers(player, clan);

        saveTask(clan);

        target.ifOnline(targetPlayer -> leave(targetPlayer, true));
    }

    private static void revokeBan(final Player player, final String targetName) {
        final ClanChat clan = player.getCurrentClanChat();

        if (clan == null) {
            player.sendMessage("You're not in a clan channel.");
            return;
        }

        final ClanChatRank rank = clan.getRank(player);

        if (!clan.hasPermission(BAN, rank)) {
            player.sendMessage("You do not have the required rank to unban this player.");
            return;
        } else if (clan.getRank(player) != OWNER && clan.getRank(player) != STAFF && clan.getRank(player) != GENERAL) {
            player.sendMessage("Only the clan chat owner can do that.");
            return;
        }

        player.getPacketSender().clearInterfaceText(SETUP_BANNED_MEMBER_ID_START, SETUP_BANNED_MEMBER_ID_END);
        clan.revokeBan(targetName);
        ClanChatSetup.updateMembers(player, clan);
        saveTask(clan);
    }

    public static boolean handleButton(final Player player, final int button, final int menuId) {

        if (player.BLOCK_ALL_BUT_TALKING)
            return false;

        ClanChat clan;

        if (player.getInterfaceId() == CLAN_SETUP_INTERFACE_ID) {

            clan = Optional.ofNullable(player.getCurrentClanChat()).orElse(getClanChatOf(player));

            final Optional<ClanMember> optionalMember = Optional.ofNullable(clan).flatMap(clanChat -> clanChat.find(player));

            switch (button) {
                case CLAN_PROMOTE_MEMBER_BUTTON_ID:
                    if(canApply(PROMOTE, optionalMember.map(ClanMember::getRank).orElse(ANYONE))) {
                        player.setEnterSyntax(new EnterClanPromote());
                        player.getPacketSender().sendEnterInputPrompt("Enter the username of the player to promote:");
                    } else
                        player.getPacketSender().sendMessage("You do not have permissions to promote members.", 1000);
                    break;
                case CLAN_BAN_MEMBER_BUTTON_ID:
                    if(canApply(BAN, optionalMember.map(ClanMember::getRank).orElse(ANYONE))) {
                        player.setEnterSyntax(new EnterClanBan());
                        player.getPacketSender().sendEnterInputPrompt("Enter the username of the player to ban:");
                    } else
                        player.getPacketSender().sendMessage("You do not have permissions to ban members.", 1000);
                    break;
                case CLAN_DELETE_OR_CHANGE_NAME_BUTTON_ID:

                    if(clan != null && !clan.isOwner(player)){
                        player.getPacketSender().sendMessage("Only the clan owner can delete the clan.", 1000);
                        return true;
                    }
                    if (menuId == 0) {
                        player.setEnterSyntax(new ChangeClanChatName());
                        player.getPacketSender().sendEnterInputPrompt("What should your clan chat channel's name be?");
                    } else if (menuId == 1) {
                        final Consumer<Player> deleteAction = GlobalClanChatManager::delete;
                        new DialogueBuilder(DialogueType.OPTION).setOptionTitle("Are you sure?")
                                .firstOption("Yes delete the clan", deleteAction
                                        .andThen(futurePlayer -> ClanChatSetup.open(futurePlayer, null)))
                                .addCancel()
                                .start(player);
                    }
                    return true;
                case 43707:
                case 43710:
                case 43713:
                case 43716:

                    if(clan == null)
                        return true;

                    final ClanChatAction selectedAction = forButton(button);

                    if(optionalMember.isPresent()){
                        final ClanMember member = optionalMember.get();

                        if (selectedAction != null && !canChangeSetting(selectedAction, member.getRank())) {
                            player.getPacketSender().sendMessage("You do not have permissions to change this setting.", 1000);
                            return true;
                        }
                    }

                    final ClanChatRank selectedRank = ClanChatRank.forMenu(menuId);

                    if (clan.isRequirementSetAs(selectedAction, selectedRank))
                        return true;

                    clan.setRequirement(selectedAction, selectedRank);

                    if (selectedAction == ENTER)
                        clan.validateAllMembersAccess(player);

                    ClanChatSetup.open(player, clan);
                    clan.setRequiredUpdate(true);
                    player.sendMessage("You have changed your clan chat channel's settings.");
                    saveTask(clan);
                    return true;
            }
        }

        // Selecting a player in the setup interface banned members list
        if (button >= SETUP_BANNED_MEMBER_ID_START && button <= SETUP_BANNED_MEMBER_ID_END) {
            final int bannedMemberIndex = button - SETUP_BANNED_MEMBER_ID_START;
            // TODO: Doesnt work to unban players
            Optional.ofNullable(getClanChatOf(player))
                    .map(clanChat -> clanChat.bannedMembers().get(bannedMemberIndex))
                    .ifPresent(clanChatBan -> revokeBan(player, clanChatBan.getName()));
            return true;
        }

        int memberIndex = -1;
        ClanMemberAction memberAction = null;

        boolean inSetupScreen = false;

        if (button >= TAB_MEMBER_ID_START && button < TAB_MEMBER_ID_END) {
            memberIndex = button - TAB_MEMBER_ID_START;
            memberAction = ClanMemberAction.forTabMenu(menuId);
        } else if (button >= SETUP_MEMBER_ID_START && button < SETUP_MEMBER_ID_END) {
            inSetupScreen = true;
            memberIndex = button - SETUP_MEMBER_ID_START;
            memberAction = ClanMemberAction.forSetupMenu(menuId);
        }

        if (memberAction != null) {
            clan = player.getCurrentClanChat();

            if (clan != null) {

                // only owners can perform promoting/demoting actions
                if (memberAction.requiresOwnerRights() && !clan.isOwner(player) && clan.getRank(player) != STAFF) {
                    player.sendMessage("Only the clan chat owner or a staff member can do that.");
                    return true;
                }

                final Optional<ClanMember> optionalTarget = inSetupScreen
                        ? clan.findRankedAtIndex(memberIndex)
                        : clan.findOnlineAtIndex(memberIndex);

                if (optionalTarget.isPresent()) {
                    final ClanMember target = optionalTarget.get();
                    if (!target.is(player)) {
                        memberAction.execute(player, clan, target);
                        return true;
                    }
                }
            }
        }

        // Other buttons..
        switch (button) {
            case CLAN_LOOT_SHARE_BUTTON_ID:

                final ClanChat clanChat = player.getCurrentClanChat();

                if(clanChat != null){

                    if(!clanChat.isOwner(player) && clanChat.getRank(player) != STAFF){
                        player.getPacketSender().sendConfig(LOOT_SHARING_CONFIG_ID, clanChat.isLootSharingEnabled() ? 1 : 0);
                        player.sendMessage("Only the clan owner or a staff member can toggle loot-sharing.");
                        return true;
                    }

                    clanChat.toggleLootSharing();
                    final String playerName = Misc.capitalizeWords(player.getUsername());
//                    System.out.println(playerName);
                    clanChat.messageAll("<img=760> " + playerName + " has "+(clanChat.isLootSharingEnabled() ? "enabled" : "disabled")+" the loot-share.");
                } else {
                	player.getPacketSender().sendConfig(LOOT_SHARING_CONFIG_ID, 0);
                    player.sendMessage("You're not in a clan chat channel.");
                	return true;
                }

                break;
            case 37132: // CC Setup
                if (player.busy())
                    player.getPacketSender().sendInterfaceRemoval();

                final ClanChat inClan = player.getCurrentClanChat();
                final ClanChat myClan = getClanChatOf(player);

                if(inClan == null && myClan != null)
                    join(player, myClan);

                ClanChatSetup.open(player, Optional.ofNullable(inClan).orElse(myClan));
                return true;
            case 37129: // Join / Leave clan
                if (player.getCurrentClanChat() == null) {
                    player.requestInput(String.class,
                            "Enter the name of the clan chat channel you wish to join:",
                            (channelNameInput) -> join(player, channelNameInput));
                } else {
                    leave(player, false);
                    player.setClanChatName("");
                }
                return true;
        }
        return false;
    }

    static void demote(final Player player, final ClanChat clan, final ClanMember target) {

        ClanChatRank targetRank = target.getRank();

        if (clan.getRank(player) != OWNER && clan.getRank(player) != STAFF && clan.getRank(player) != GENERAL) {
            player.getPacketSender().sendMessage("Only the clan chat owner can do that.");
            return;
        }

        if (targetRank == null) {
            player.getPacketSender().sendMessage("That player has no rank.");
            return;
        }

        if (targetRank == OWNER) {
            player.getPacketSender().sendMessage("That player can't be promoted or demoted.");
            return;
        }

        target.setRank(ANYONE);
        target.ifOnline(targetPlayer -> updateRank(clan, targetPlayer));

        clan.kickIfNoPermissions(target);
        clan.setRequiredUpdate(true);

        if (player.getInterfaceId() == CLAN_SETUP_INTERFACE_ID)
            ClanChatSetup.open(player, clan);

        saveTask(clan);
    }

    /**
     * Saves the specified {@link ClanChat} clan after 60 seconds.
     *
     * @param clan the {@link ClanChat} to save
     */
    private static void saveTask(final ClanChat clan) {

        if (!clan.isQueuedForSaving()) {

            clan.setQueuedForSaving(true);

            TaskManager.submit(new Task(CLAN_SAVE_INTERVAL) {

                @Override
                protected void execute() {
                    stop();
                }

                @Override
                public void stop() {
                    clan.setQueuedForSaving(false);
                    save(clan);
                }
            });
        }
    }

    public static void onLogin(final Player player) {

        ClanChatUtil.resetInterface(player);

        if (player.getClanChatName() != null && !player.getClanChatName().isEmpty())
            GlobalClanChatManager.join(player, player.getClanChatName());
        else
            GlobalClanChatManager.join(player, DEFAULT_CLAN_NAME);
    }

    public static Player getLootRecipientOnKill(final Player playerKiller, final NPC npcKilled){

        final ClanChat clanChat = playerKiller.getCurrentClanChat();

        if(clanChat != null){

            if(clanChat.isLootSharingEnabled()){

                final Map<Agent, TotalTrackedDamage> damageMap = npcKilled.getCombat().getDamageMap();
                final List<Player> possibleRewardRecipients = new ArrayList<>();


                for(Map.Entry<Agent, TotalTrackedDamage> entry : damageMap.entrySet()) {

                    final Agent agent = entry.getKey();

                    if(agent instanceof Player) {

                        final Player player = (Player) agent;

                        if (player.getGameMode().isAnyIronman())
                            continue;
                        if (player.getGameMode().isSpawn())
                            continue;

                        if (clanChat.contains(player)) {
                            if (agent.getPosition().getZ() == playerKiller.getPosition().getZ() && agent.getPosition().getDistance(npcKilled.getPosition()) <= 15) {
                                possibleRewardRecipients.add(player);
                            }
                        }
                    }
                }

                return possibleRewardRecipients.isEmpty() ? playerKiller : Misc.randomElement(possibleRewardRecipients);
            }
        }

        return playerKiller;
    }

    public static void sendLootShareMessage(final Player recipient, final NPC npcKilled, final Item... items){

        final ClanChat clanChat = recipient.getCurrentClanChat();

        if(clanChat != null) {
            if(clanChat.isLootSharingEnabled()) {
                for (final Item item : items) {
                    clanChat.onlineMembers()
                            .filter(player -> player.getPosition().isWithinDistance(recipient.getPosition(), 12))
                            .forEach(player -> clanChat.message(player, "<img=760> @blu@" + recipient.getUsername() + "</col> received: " + item.getAmount() + "x " + item.getDefinition().getName() + " from " + npcKilled.fetchDefinition().getName() + "!"));
                }
            }
        }
    }

    private static int freeIndex() {
        for (int i = 0; i < clans.length; i++) {
            if (clans[i] == null) {
                return i;
            }
        }
        return -1;
    }

    static ClanChat getClanChatOf(final Player player) {
        for (final ClanChat clan : clans) {
            if (clan == null || clan.getOwnerName() == null)
                continue;
            if (clan.getOwnerName().equals(player.getUsername())) {
                return clan;
            }
        }
        return null;
    }

    public static void sequence() {
        for(final ClanChat clan : clans){
            if (clan == null)
                continue;
            if(clan.isRequiredUpdate()){
                ClanChatUtil.updateInterfacesForMembers(clan);
                clan.setRequiredUpdate(false);
            }
        }
    }
}