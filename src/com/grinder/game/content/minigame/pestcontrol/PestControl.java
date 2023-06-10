package com.grinder.game.content.minigame.pestcontrol;

import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.InstanceManager;
import com.grinder.game.model.areas.MapBuilder;
import com.grinder.game.model.areas.MapInstance;
import com.grinder.game.model.areas.instanced.PestControlArea;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.grinder.util.ObjectID.*;
import static com.grinder.game.content.minigame.pestcontrol.PestControlConstants.*;

/**
 * @author  Minoroin / TealWool#0873 (https://www.rune-server.ee/members/minoroin/)
 * @since   20/11/2021
 * @version 1.0
 */

public class PestControl {

    public static ArrayList<Player> playersInWaitingRoom = new ArrayList<Player>();

    public static ArrayList<NPC> spawnedNpcs = new ArrayList<NPC>();

    public static int LOBBY_TIMER = (WAIT_TIMER*100);

    public static final PestControlInstance PEST_CONTROL_INSTANCE = new PestControlInstance();

    public static void process() {
        if (playersInWaitingRoom.size() >= PLAYERS_NEEDED) {
            if (LOBBY_TIMER == 200) {
                playersInWaitingRoom.forEach((player) -> {
                    player.sendMessage("There are 2 minutes remaining until the Pest Control minigame starts!");
                    player.getPacketSender().sendSound(3120);
                });
            }
            if (LOBBY_TIMER == 100) {
                PlayerUtil.broadcastMessage("<img=792> There is one minute remaining before the Pest Control minigame starts!");
            }
            LOBBY_TIMER--;

            if ((LOBBY_TIMER <= 0 || playersInWaitingRoom.size() >= PLAYER_LIMIT)) {
                startGame();
            }

            playersInWaitingRoom.forEach((player) -> {
                player.getPacketSender().sendWalkableInterface(64300);

                int secondsLeft = (int) (LOBBY_TIMER * 0.6f);
                int minutesLeft = 0;

                while(secondsLeft >= 59) {
                    minutesLeft++;
                    secondsLeft -= 60;
                }

                if (secondsLeft < 0) {
                    secondsLeft = 0;
                }

                player.getPacketSender().sendString(64301, "Next departure in " + minutesLeft + " minutes and " + secondsLeft + " seconds.");
                player.getPacketSender().sendString(64302, "Players Ready: " + playersInWaitingRoom.size());
                player.getPacketSender().sendString(64303, "(Need " + PLAYERS_NEEDED + " to " + PLAYER_LIMIT + " players)");
               // player.getPacketSender().sendString(64504, "Pest Points: " + player.getAttributes().numInt(Attribute.PEST_CONTROL_POINTS));
                player.getPacketSender().sendString(64304, "Pest Points: " + player.getPoints().get(AttributeManager.Points.COMMENDATION));
            });
        }
        else
        {
            playersInWaitingRoom.forEach((player) -> {
                player.getPacketSender().sendWalkableInterface(64300);

                int secondsLeft = (int) (LOBBY_TIMER * 0.6f);
                int minutesLeft = 0;

                while(secondsLeft >= 59) {
                    minutesLeft++;
                    secondsLeft -= 60;
                }

                if (secondsLeft < 0) {
                    secondsLeft = 0;
                }

                player.getPacketSender().sendString(64301, "Waiting for more players");
                player.getPacketSender().sendString(64302, "Players Ready: " + playersInWaitingRoom.size());
                player.getPacketSender().sendString(64303, "(Need " + PLAYERS_NEEDED + " to " + PLAYER_LIMIT + " players)");
                // player.getPacketSender().sendString(64504, "Pest Points: " + player.getAttributes().numInt(Attribute.PEST_CONTROL_POINTS));
                player.getPacketSender().sendString(64304, "Pest Points: " + player.getPoints().get(AttributeManager.Points.COMMENDATION));
            });
        }

        PEST_CONTROL_INSTANCE.process();
    }

    private static void startGame() {
        playersInWaitingRoom.forEach((player) -> {
            Position playerPosition = player.getPosition();
            /*
            //Check if in boat
            if (!playerPosition.inside(2660, 2638, 2663, 2643)) {
                leaveWaitingBoat(player);
                return;
            }
            */
            player.getPacketSender().sendMusic(588, 4, 0);
            player.moveTo(new Position(2656 + Misc.random(3), 2609 + Misc.random(5), 0));

            player.pestControlZeal = 600;
            player.pestControlTotalHit = 0;
            player.resetAttributes();
            player.getCombat().reset(true);

            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(NpcID.SQUIRE_2949)
                    .setText("You must defend the Void Knight while the portals are",
                            "unsummoned. The ritual takes twenty minutes though,",
                            "so you can help out by destroying them yourselves!",
                            "Now GO GO GO!").start(player);

            PEST_CONTROL_INSTANCE.addPlayer(player);
        });
        PEST_CONTROL_INSTANCE.startPestControl();

        playersInWaitingRoom.clear();

        LOBBY_TIMER = (WAIT_TIMER*100)*10+50;
    }

    public static boolean handleObject(Player player, GameObject object, int clickNumber) {
        switch(object.getId()) {
            case GANGPLANK_27:
                enterWaitingBoat(player);
                return true;

            case LADDER_175:
                leaveWaitingBoat(player);
                return true;
        }
        return PEST_CONTROL_INSTANCE.handleObject(player, object, clickNumber);
    }

    public static void enterWaitingBoat(Player player) {
        if (player.getSkillManager().calculateCombatLevel() < COMBAT_LEVEL_REQUIRED) {
            DialogueManager.sendStatement(player, "You need a combat level of " + COMBAT_LEVEL_REQUIRED + " to use this lander.");
            return;
        }

        if (playersInWaitingRoom.size() == 0 && PEST_CONTROL_INSTANCE.finished)
        {
            LOBBY_TIMER = (WAIT_TIMER*100);
        }

        player.moveTo(new Position(2661, 2639, 0));
        playersInWaitingRoom.add(player);
    }

    public static void leaveWaitingBoat(Player player) {
        player.moveTo(new Position(2657, 2639, 0));
        playersInWaitingRoom.remove(player);
        player.getPacketSender().sendWalkableInterface(-1);
    }

    public static void removePlayerFromGame(Player player) {
        PEST_CONTROL_INSTANCE.playersToRemove.add(player);
        player.getPacketSender().sendWalkableInterface(-1);
    }

    public static boolean playerIsInPestControl(Player player) {
        if (player.isPlayer()) {
            if (PEST_CONTROL_INSTANCE.hasPlayer(player.getAsPlayer())) {
                return true;
            }
        }
        return false;
    }

    public static void sequence(Player player) {
        if (player.inPestControl() && !playerIsInPestControl(player))
        {
            player.moveTo(new Position(2657, 2639, 0));
        }
    }
}
