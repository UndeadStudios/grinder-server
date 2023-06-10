package com.grinder.net.packet.impl;

import com.grinder.game.World;
import com.grinder.game.content.miscellaneous.CommandEvent;
import com.grinder.game.content.clan.GlobalClanChatManager;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.message.decoder.CommandMessageDecoder;
import com.grinder.game.message.impl.CommandMessage;
import com.grinder.game.model.CommandActions;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.commands.CommandManager;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.model.punishment.PunishmentType;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.net.packet.interaction.PacketInteractionManager;
import com.grinder.util.Logging;
import com.grinder.util.Misc;
import kotlin.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * This packet listener manages commands a player uses by using the command
 * console prompted by using the "`" char.
 *
 * @author Gabriel Hannason
 */
public class CommandPacketListener implements PacketListener {

    public static final int OP_CODE = 103;

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {

//		final SimpleBenchMarker simpleBenchMarker = new SimpleBenchMarker();

//		simpleBenchMarker.reset();

        final CommandMessageDecoder decoder = new CommandMessageDecoder();
        final CommandMessage message = decoder.decode(packetReader.getPacket());

        if (player == null)
            return;

        if (player.getGameMode().isOneLife() && player.fallenOneLifeGameMode() && !message.getCommand().startsWith("/") && !message.getCommand().toLowerCase().startsWith("yell")) {
            player.sendMessage("Your account has fallen as a One life game mode and can no longer do any actions.");
            return;
        }

        if (player.BLOCK_ALL_BUT_TALKING && !message.getCommand().startsWith("/") && !message.getCommand().toLowerCase().startsWith("yell"))
            return;

        if (player.isTeleporting() && player.getTeleportingType() == TeleportType.HOME)
            player.stopTeleporting();

        if (player.isInTutorial() && !message.getCommand().startsWith("/") && !message.getCommand().toLowerCase().startsWith("yell"))
            return;

        if (player.isDying() && !message.getCommand().startsWith("/") && !message.getCommand().toLowerCase().startsWith("yell"))
            return;

        if (player.getHitpoints() <= 0)
            return;

        if (player.isAccountFlagged())
            return;

        if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false))
            return;

        if (CommandActions.INSTANCE.handleCommand(player, message))
            return;

        if (PacketInteractionManager.handleCommand(player, message.getCommand(), message.getCommand().split(" "))) {
            return;
        }

        handleCommand(player, message.getCommand());
    }

    public static void handleCommand(Player player, String command) {


/*		if (command.toLowerCase().equals("offsnow")) {
			return;
		}*/
        String[] parts = command.split(" ");
        parts[0] = parts[0].toLowerCase();
        if (CommandEvent.check(player, parts[0])) {
            return;
        }
//		simpleBenchMarker.mark("CommandEvent");
        if (command.contains("\r") || command.contains("\n")) {
            return;
        }
        //System.out.println(""+ player.getUsername() + " used the command " + command +"");
        if (!command.startsWith("/") && PlayerUtil.isStaff(player) && !command.toLowerCase().contains("autotype")) {
            PlayerUtil.broadcastPlayerDeveloperMessage(PlayerUtil.getImages(player) + "" + player.getUsername() + " has used the command ::" + command);
        }
//		simpleBenchMarker.mark("broadcastPlayerDeveloperMessage");

        if (!command.toLowerCase().contains("autotype"))
            Logging.log("commands", player.getUsername() + " used the command ::" + command);

//		simpleBenchMarker.mark("Logging.log");

        if (player.getMinigame() != null && !PlayerUtil.isDeveloper(player)) {
            player.sendMessage("You cannot use commands while inside a minigame.");
            return;
        }
        if (command.startsWith("/") && command.length() >= 1) {
            GlobalClanChatManager.sendMessage(player, command.substring(1, command.length()));
            return;
        }
        if (player.isTeleporting() && player.getTeleportingType() == TeleportType.HOME)
            player.stopTeleporting();

        if (parts[0].equals("autotype") || parts[0].equals("repeat")) {
            if (player.hasAutoTalkerMessageActive()) {
                DialogueManager.sendStatement(player, "You already have an active running message by the auto-talker.");
                return;
            }
        }
        if (player.hasAutoTalkerMessageActive()) {
            player.sendMessage("@red@The message auto-typer has been interrupted. Type ::repeat to repeat the last message.");
            player.setHasAutoTalkerMessageActive(false);
            player.getAutoChatBreakTimer().start(10);
            if (!Objects.equals(player.getMessageToAutoTalk(), "")) {
                player.setMessageToAutoTalk("");
            }
        }
        // Logout timer in the Wilderness
        if (player.hasLogoutTimer()) {
            //player.getPacketSender().sendInterfaceRemoval();
            player.sendMessage("@red@Your logout request has been interrupted.");
            player.setHasLogoutTimer(false);
        }

        if (parts[0].equals("punish")) {
            if (player.getRights().isStaff()) {
                final int targetPlayerId = Integer.parseInt(command.substring(parts[0].length()).trim());
                final Player targetPlayer = World.getPlayers().get(targetPlayerId);

                if (targetPlayer == null) {
                    player.sendMessage("Something went wrong in punishing player with index " + targetPlayerId);
                    return;
                }

                final DialogueBuilder builder = new DialogueBuilder(DialogueType.OPTION);
                final List<Pair<String, Consumer<Player>>> optionHandler = new ArrayList<>();

                for (PunishmentType type : PunishmentType.values()) {
                    if (type.hasSufficientRights(player.getRights())) {
                        optionHandler.add(new Pair<>(Misc.capitalize(type.name().toLowerCase().replace("_", " ")), (staff) -> {
                            PunishmentManager.submit(staff, targetPlayer.getUsername(), type);
                        }));
                    }
                }

                builder.addOptions(optionHandler);
                builder.start(player);
                return;
            }
        }


        player.getPoints().increase(AttributeManager.Points.COMMANDS_USED_COUNT, 1); // Increase points

        Command c = CommandManager.commands.get(parts[0]);

//		simpleBenchMarker.mark("get command");

//		simpleBenchMarker.println("BOOO", 5, Server.getLogger());
        if (c != null) {


            if (c.canUse(player)) {
                c.execute(player, command, parts);
            } else {
                player.getPacketSender().sendMessage("You don't have enough previleges to execute this command.", 1000);
                return;
            }

        } else {
            player.getPacketSender().sendMessage("This command does not exist.", 1000);
            return;

        }
    }
}
