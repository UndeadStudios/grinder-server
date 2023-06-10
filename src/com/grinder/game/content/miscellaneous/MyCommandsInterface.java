package com.grinder.game.content.miscellaneous;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.commands.CommandManager;
import com.grinder.net.packet.impl.CommandPacketListener;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class MyCommandsInterface {

    private static final int INTERFACE_ID = 82100;
    private static final int SCROLL_ID = INTERFACE_ID + 4;
    private static final int TEXT_START_ID = INTERFACE_ID + 6;

    public static void sendInterface(Player player) {
        SortedMap<String, Command> usableCommands = getUsableCmds(player);

        int index = 0;
        for (SortedMap.Entry<String, Command> entry : usableCommands.entrySet()) {
            final String name = entry.getKey();
            final Command command = entry.getValue();
            player.getPacketSender().sendString(TEXT_START_ID + index * 3, "::" + name + " " + command.getSyntax());
            player.getPacketSender().sendString(TEXT_START_ID + index * 3 + 1, command.getDescription());
            index++;
        }

        player.getPacketSender().sendInterfaceScrollReset(SCROLL_ID);
        player.getPacketSender().sendScrollbarHeight(SCROLL_ID, usableCommands.size() * 17 + 1);
        player.getPacketSender().sendInterface(INTERFACE_ID);

    }

    public static boolean handleButton(Player player, int button) {
        if (player.getInterfaceId() == INTERFACE_ID && button >= TEXT_START_ID && button <= TEXT_START_ID + 900) {
            int index = (button - TEXT_START_ID) / 3;
            SortedMap<String, Command> usableCommands = getUsableCmds(player);
            if (index >= usableCommands.size())
                return false;
            SortedMap.Entry<String, Command> entry = (SortedMap.Entry<String, Command>) usableCommands.entrySet().toArray()[index];
            String name = entry.getKey();
            Command command = entry.getValue();
            if (!command.getSyntax().isEmpty()) {
                player.getPacketSender().sendMessage("Enter the following syntax to use this command: \"::" + name + " " + command.getSyntax() + "\"");
            } else {
                player.getPacketSender().sendInterfaceRemoval();
                CommandPacketListener.handleCommand(player, name);
            }
            return true;
        }
        return false;
    }

    private static SortedMap<String, Command> getUsableCmds(Player player) {
        SortedMap<String, Command> usableCommands = new TreeMap<>();
        for (Map.Entry<String, Command> entry : CommandManager.commands.entrySet()) {
            final String name = entry.getKey();
            final Command command = entry.getValue();
            if (command.canUse(player)) {
                usableCommands.put(name, command);
            }
        }
        return usableCommands;
    }

}
