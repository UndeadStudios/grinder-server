package com.grinder.util.debug;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.net.packet.interaction.PacketInteraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
/**
 *
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 *
 */
public class DebugManager extends PacketInteraction {

    private static final ArrayList<String> DEVELOPERS = new ArrayList<>(Arrays.asList("Dexter"));

    private static final HashMap<String, Boolean> TOGGLES = new HashMap<>();

    private static final Position COORDS = new Position(2197, 3810);

    public static final int DEV_CRATE = 354;

    static {
        toggle("packet", false);
        toggle("button", true);
        toggle("item-container", true);
        toggle("npc-option", true);
        toggle("item-option", true);
        toggle("object-option", true);
        toggle("item-on-object", true);
        toggle("item-on-item", true);
        toggle("equip", true);
    }

    public static void debug(Player player, String type, String s) {
        if (!isDev(player)) {
            return;
        }

        if (TOGGLES.get(type) == null) {
            player.getPacketSender().sendMessage("Invalid debug type: " + type);
            return;
        }

        if (!TOGGLES.get(type)) {
            return;
        }

        String output = player.getUsername() + ": packet: " + type + ": " + s;

        System.out.println(output);
        player.getPacketSender().sendMessage(output);
    }

    private static void toggle(String s, boolean state) {
        TOGGLES.put(s, state);
    }

    private static boolean isDev(Player player) {
        return DEVELOPERS.contains(player.getUsername());
    }

    @Override
    public boolean handleCommand(Player player, String command, String[] args) {
        if (!isDev(player)) {
            return false;
        }
        if (command.startsWith("debug")) {
            if (args.length < 2) {
                return true;
            }

            String type = args[1];
            boolean state = Boolean.parseBoolean(args[2]);

            if (TOGGLES.get(type) == null) {
                return true;
            }

            TOGGLES.put(type, state);
            player.getPacketSender().sendMessage(type + ": " + state);
            return true;
        } else if (command.startsWith("devzone")) {
            player.moveTo(COORDS);
        }
        return false;
    }
}
