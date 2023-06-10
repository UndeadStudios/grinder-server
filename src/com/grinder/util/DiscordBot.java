package com.grinder.util;

import com.grinder.Config;
import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.message.MessageFilter;
import com.grinder.game.model.message.MessageFilterManager;
import com.grinder.game.model.message.MessageFilterScope;
import com.grinder.game.model.message.MessageFilterType;
import com.grinder.game.model.punishment.Punishment;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.util.discord.DiscordChannelType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.*;

/**
 * TODO: add documentation
 *
 * TODO: clean all this stuff up (this is merely an experimental implementation atm!)
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-02
 */
public class DiscordBot extends ListenerAdapter {

    private static final long GUILD_ID = 358664434324865024L;
    public static boolean ENABLED = true;
    public static DiscordBot INSTANCE;

    private final JDA jda;

    public static void init(){

        if(!ENABLED || Config.DEVELOPER_MACHINE)
            return;

        try {
            INSTANCE = new DiscordBot();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    private DiscordBot() throws LoginException {
        jda = JDABuilder
                .createDefault(Config.DISCORD_TOKEN)
                .addEventListeners(this)
                .build();
    }

    public void sendModMessage(final String message) {
        sendMessage(DiscordChannelType.PUNISHMENT_BOT, message);
    }


    public void sendDropsMessages(final String message) {
        sendMessage(DiscordChannelType.DROPS_CHANNEL, message);
    }

    public void sendServerLogs(final String message) {
        sendMessage(DiscordChannelType.SERVER_LOGS_CHANNEL, message);
    }

    public void sendVoteLogs(final String message) {
        sendMessage(DiscordChannelType.VOTE_LOGS_CHANNEL, message);
    }

    public void sendMessage(final DiscordChannelType type, final String message){

        if(jda.getStatus() == JDA.Status.CONNECTED){
            final MessageBuilder builder = new MessageBuilder();

            builder.append(message);

            final MessageAction action = jda.getTextChannelById(type.getChannelId()).sendMessage(builder.build());
            action.queue();
        }
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) return;
        // We don't want to respond to other bot accounts, including ourself
        Message message = event.getMessage();
        String content = message.getContentRaw();
        MessageChannel channel = event.getChannel();
        // getContentRaw() is an atomic getter
        // getContentDisplay() is a lazy getter which modifies the content for e.g. console view (strip discord formatting)

        if(content.startsWith("!commands")){

            final MessageBuilder builder = new MessageBuilder();

            builder.append("List of commands: \n")
                    .append("\t !list <name> \n")
                    .append("\t !request <hash> \n")
                    .append("\t !reason <hash> \n")
                    .append("\t !set_reason <hash> <reason> \n")
                    .append("\t !help");

            channel.sendMessage(builder.build()).queue();

        } else if(content.startsWith("!help")){

            final MessageBuilder builder = new MessageBuilder();

            builder.append("You can view commands by executing !commands \n")
                    .append("Parameters for the commands are denoted between diamond operators (<>) \n")
                    .append("For a list of parameters and their explanation execute !params \n");

            channel.sendMessage(builder.build()).queue();

        } else if(content.startsWith("!params")){

            final MessageBuilder builder = new MessageBuilder();

            builder.append("List of params: \n")
                    .append("\t <name> \t : this the user name of a player. (e.g. 'stan')\n")
                    .append("\t <hash> \t : this is the hash of a specific punishment. (e.g. '-1234567')\n")
                    .append("\t <reason> \t : this can be any amount of text, stating the reason of a punishment. (e.g. 'stan was not nice to me')\n");

            channel.sendMessage(builder.build()).queue();

        } else if (content.startsWith("!list")) {

            if(content.length() < 6){
                channel.sendMessage("Also specify the name of the target! (e.g. '!list stan')").queue();
                return;
            }

            final String name = content.substring(6).trim();

            World.submitGameThreadJob(() -> {
                final List<Punishment> punishments = PunishmentManager.findPunishmentsForName(name);

                if (punishments.isEmpty()) {

                    channel.sendMessage("Did not find any punishments for " + name).queue();

                } else {
                    channel.sendMessage("Listing punishments of " + name + ":").queue();
                    for (final Punishment punishment : punishments) {
                        channel.sendMessage(punishment.toMessage()).queue();
                    }
                }
                return null;
            });
        } else if(content.startsWith("!players")){

            channel.sendMessage("There are currently "+ PlayerUtil.transformPlayerCount() +" players online!").queue();

        } else if (content.startsWith("!request")) {

            String id = content.substring(9).trim();

            if(TextUtil.isInteger(id)){

                long hash = Long.parseLong(id);

                World.submitGameThreadJob(() -> {
                    final Optional<Punishment> optionalPunishment = PunishmentManager.findByHash(hash);

                    if(optionalPunishment.isPresent()){

                        channel.sendMessage(optionalPunishment.get().toMessage()).queue();

                    } else
                        channel.sendMessage("Could not find a punishment of hash '"+hash+"' ").queue();
                    return null;
                });
            }
        } else if(content.startsWith("!reason")){

            String id = content.substring(8).trim();

            if(TextUtil.isInteger(id)){

                long hash = Long.parseLong(id);
                World.submitGameThreadJob(() -> {
                    final Optional<Punishment> optionalPunishment = PunishmentManager.findByHash(hash);

                    if (optionalPunishment.isPresent()) {

                        final MessageBuilder builder = new MessageBuilder();

                        final String reason = optionalPunishment.get().getReason();

                        if (reason == null) {

                            builder.append("Punishment '")
                                    .append(String.valueOf(hash))
                                    .append("' has no reason set yet. \n")
                                    .append("You can do so by the command !set_reason <hash> <reason>");


                        } else builder.append("The reason for punishment '")
                                .append(String.valueOf(hash))
                                .append("' is: \n").append(reason);

                        channel.sendMessage(builder.build()).queue();
                    } else
                        channel.sendMessage("Could not find a punishment of hash '" + hash + "' ").queue();
                    return null;
                });
            }

        } else if(content.startsWith("!set_reason")){

            String[] split = content.split(" ");
            String id = split[1];

            if(TextUtil.isInteger(id)){

                long hash = Long.parseLong(id);

                World.submitGameThreadJob(() -> {
                    final Optional<Punishment> optionalPunishment = PunishmentManager.findByHash(hash);

                    if (optionalPunishment.isPresent()) {

                        final String reason = content.substring(split[0].length() + split[1].length() + 1);

                        if (!reason.isEmpty()) {
                            optionalPunishment.get().setReason(reason);

                            channel.sendMessage("You set the reason for punishment '" + hash + "'.").queue();

                            PunishmentManager.save();
                        } else
                            channel.sendMessage("Ooohwee, it seems you forgot to enter a reason my friend!").queue();

                    } else
                        channel.sendMessage("Could not find a punishment of hash '" + hash + "' ").queue();
                    return null;
                });
            }
        } else if(content.startsWith("!get_price")){
            String[] split = content.split(" ");
            String id = split[1];

            if(TextUtil.isInteger(id)) {

                int itemId = Integer.parseInt(id);


            } else
                channel.sendMessage("Please enter only integer values!").queue();
        } else if(content.startsWith("!print_player_activity")){

            final MessageBuilder messageBuilder = new MessageBuilder();

            for(Player player : World.getPlayers()){
                if (player != null && player.isActive()){
                    if (player.getCombat().isUnderAttack()){
                        messageBuilder.append(player).append(" is under attack combat:").append('\n');
                        for (String debugLine: player.getCombat().lines()){
                            messageBuilder.append("\t").append(debugLine).append('\n');
                        }
                    }
                }
            }

            channel.sendMessage(messageBuilder.build()).queue();

        } else if(content.startsWith("!add_filter")){

            final String[] split = content.split(" ");

            if(split.length < 4){
                invalidFilter(channel);
                return;
            }

            try {

                final MessageFilterType type = MessageFilterType.valueOf(split[1]);
                final MessageFilterScope scope = MessageFilterScope.valueOf(split[2]);
                final ArrayList<String> triggerWords = new ArrayList<>();

                for (int i = 3; i < split.length; i++)
                    triggerWords.add(split[i].trim());

                World.submitGameThreadJob(() -> {
                    if (MessageFilterManager.INSTANCE.addFilter(new MessageFilter(event.getAuthor().getName(), triggerWords, type, scope))) {
                        channel.sendMessage("Added filter to list, new list size = " + MessageFilterManager.INSTANCE.getFilters().size() + " :)").queue();
                    } else {
                        channel.sendMessage("Could not add filter to list, filters_size = " + MessageFilterManager.INSTANCE.getFilters().size() + "!").queue();
                    }
                    return null;
                });

            } catch (Exception e){
                invalidFilter(channel);
            }

        } else if(content.startsWith("!show_filters")){
            final MessageBuilder messageBuilder = new MessageBuilder();
            World.submitGameThreadJob(() -> {
                MessageFilterManager.INSTANCE.filtersGroupedByUserName().forEach((user, filters) -> {
                    messageBuilder.append("By ").append("**").append(user).append("**").append(":\n");
                    filters.forEach(filter -> {
                        messageBuilder.append("\t Type:  ").append(filter.getFilterType()).append('\n');
                        messageBuilder.append("\t Scope: ").append(filter.getScope()).append('\n');
                        messageBuilder.append("\t Words: ").append(filter.toFormattedString()).append('\n');
                    });
                });
                channel.sendMessage(messageBuilder.build()).queue();
                return null;
            });
        } else if(content.startsWith("!del_filter")){
            final String[] split = content.split(" ");

            if(split.length == 1){
                channel.sendMessage("Please also provide the trigger word u'd like to remove :)").queue();
                return;
            }

            for(int i = 1; i < split.length; i++) {
                final String word = split[i];
                final MessageBuilder messageBuilder = new MessageBuilder();
                World.submitGameThreadJob(() -> {
                    MessageFilterManager.INSTANCE.getFilters()
                            .stream()
                            .filter(filter -> filter.getTriggers().removeIf(trigger -> trigger.equalsIgnoreCase(word)))
                            .forEach(messageFilter -> messageBuilder.append("Removed trigger word from filter by ")
                                    .append(messageFilter.getCreator()));
                    MessageFilterManager.INSTANCE.getFilters().removeIf(messageFilter -> messageFilter.getTriggers().isEmpty());
                    MessageFilterManager.INSTANCE.serialise();
                    channel.sendMessage(messageBuilder.build()).queue();
                    return null;
                });
            }
        } else if(content.startsWith("!logs")) {

            boolean force = content.contains("-f");

            String address = "127.0.0.1";
            try {
                address = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            if (force || address.equalsIgnoreCase("145.239.205.160")) {
                final File consoleLogs = Paths.get("logs/console.log").toFile();
                if (consoleLogs.exists())
                    channel.sendFile(consoleLogs, consoleLogs.getName()).queue();
            }
        }
    }

    private void invalidFilter(MessageChannel channel) {
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder
                .append("Please use syntax as so: `!add_filter <type> <scope> <words>`").append('\n')
                .append("\tWhere `<type>` can be any of the following: ").append(Arrays.toString(MessageFilterType.values())).append('\n')
                .append("\tWhere `<scope>` can be any of the following: ").append(Arrays.toString(MessageFilterScope.values())).append('\n')
                .append("\tWhere `<words>` can be a string of words separated by a blank\n")
                .append("e.g. `!add_filter SPLIT_CONTAINS ALL rsps dupe`\n")
                .append("\ti.e. add a filter that will only trigger if any word of the sentence by the player contains a trigger word,\n")
                .append("\twhich is applied to all types of chat for the trigger words 'rsps' and 'dupe'");
        channel.sendMessage(messageBuilder.build()).queue();
    }
}
