package com.grinder.game.model.commands.impl;

import java.util.regex.Pattern;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;

public class OpenThread implements Command {

    @Override
    public String getSyntax() {
        return "[####]";
    }

    @Override
    public String getDescription() {
        return "Opens the specified forum thread in your browser.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {

        if (parts.length > 2 || parts.length < 2) {
            player.getPacketSender().sendMessage("Please enter a valid command.");
            return;
        }

        String id = parts[1];

        // TODO: VERIFY THREAD EXISTS
        if (Pattern.matches("\\d{0,10}$", id)) {
            player.getPacketSender().sendURL("https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=" + id);
        } else {
            player.getPacketSender().sendMessage("Please enter a valid number.");
        }

        /*int ID = Integer.valueOf(parts[1]);

        try {

            HttpURLConnection con = (HttpURLConnection) new URL(
                    "https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=" + ID).openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String link = in.readLine();

            if (link != null)
                player.getPacketSender().sendURL(link);

            in.close();

        } catch (IOException ex) {
        }*/

    }

    @Override
    public boolean canUse(Player player) {
        return false;
    }

}
