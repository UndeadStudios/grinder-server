package com.grinder.game.model.commands.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.grinder.game.GameConstants;
import com.grinder.game.World;
import com.grinder.game.entity.agent.player.bot.BotPlayer;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.commands.Command;
import io.netty.channel.Channel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-04
 */
public class DebugHome implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Debugs home area to data dumps.";
    }

    private final static Path DUMP_LOCATION = Paths.get("data", "dump_info.json");
    private final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void execute(Player player, String command, String[] parts) {

        File file = DUMP_LOCATION.toFile();

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Iterator<Player> iterator = World.getPlayers().iterator();

        int playerCount = 0;
        int nulledPlayerCount = 0;
        int nulledChannelCount = 0;
        int activeChannelCount = 0;
        int playersInHomeCount = 0;

        int botCount = 0;
        int botsInHomeCount = 0;

        JsonObject debug = new JsonObject();
        JsonArray playersInHome = new JsonArray();
        JsonArray playersNotInHome = new JsonArray();

        while (iterator.hasNext()){

            final Player other = iterator.next();

            if(other != null){

                if(other instanceof BotPlayer)
                    botCount++;
                else
                    playerCount++;

                final boolean inHomeRadius = other.getPosition().isWithinDistance(GameConstants.DEFAULT_POSITION, 100);

                if(inHomeRadius){
                    if(other instanceof BotPlayer)
                        botsInHomeCount++;
                    else
                        playersInHomeCount++;
                }

                final Channel otherChannel = other.getSession().getChannel();

                if(otherChannel == null)
                    nulledChannelCount++;
                else if(otherChannel.isActive())
                    activeChannelCount++;

                int queuedPacketsCount = other.getSession().packetsInQueue();

                if(!(player instanceof BotPlayer)) {
                    final JsonObject object = new JsonObject();
                    final JsonObject lastPacketReceived = new JsonObject();
                    final JsonObject lastPacketSend = new JsonObject();
//
//                    lastPacketReceived.addProperty("info", other.getSession().getLastPacketReceivedName());
//                    lastPacketReceived.addProperty("time", System.currentTimeMillis() - other.getSession().getLastPacketReceivedTime());
//
//                    lastPacketSend.addProperty("info", other.getSession().getLastPacketSendName());
//                    lastPacketSend.addProperty("time", System.currentTimeMillis() - other.getSession().getLastPacketSendTime());

                    object.addProperty("name", other.getUsername());
                    object.addProperty("position", other.getPosition().toString());
                    object.addProperty("queuedPacketsCount", queuedPacketsCount);
                    object.add("lastPacketReceived", lastPacketReceived);
                    object.add("lastPacketSend", lastPacketSend);

                    if(inHomeRadius)
                        playersInHome.add(object);
                    else
                        playersNotInHome.add(object);
                }
            } else nulledPlayerCount++;
        }
        debug.addProperty("nulledPlayerCount", nulledPlayerCount);
        debug.addProperty("nulledChannelCount", nulledChannelCount);
        debug.addProperty("activeChannelCount", activeChannelCount);
        debug.addProperty("playerCount", playerCount);
        debug.addProperty("playersInHomeCount", playersInHomeCount);
        debug.addProperty("botCount", botCount);
        debug.addProperty("botsInHomeCount", botsInHomeCount);
        debug.add("playersInHome", playersInHome);
        debug.add("playersNotInHome", playersNotInHome);

        final FileWriter writer;
        try {
            writer = new FileWriter(file);
            GSON.toJson(debug, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.getPacketSender().sendMessage("Home debug complete.");
    }

    @Override
    public boolean canUse(Player player) {
        return player.getRights().isStaff(PlayerRights.DEVELOPER);
    }
}
