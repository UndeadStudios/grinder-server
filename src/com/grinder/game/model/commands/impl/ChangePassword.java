package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.model.punishment.PunishmentType;
import com.grinder.util.Logging;
import com.grinder.util.Misc;
public class ChangePassword implements Command {

    @Override
    public String getSyntax() {
        return "[newpass]";
    }

    @Override
    public String getDescription() {
        return "Changes your account's password.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {


        // Known exploit
        if (command.contains("\r") || command.contains("\n")) {
            return;
        }
        if (player.isAccountFlagged()) {
            PunishmentManager.submit(player.getUsername(), PunishmentType.IP_BAN, PunishmentType.MAC_BAN);
    		return;
        }
        if (player.BLOCK_ALL_BUT_TALKING) {
        	return;
        }
        if (command.length() == 14) {
        	player.sendMessage("You should use ::changepassword newPassGoesHere");
        	return;
        }
        String pass = command.substring(parts[0].length() + 1);
        if (pass.equals(player.getPassword())) {
        	player.sendMessage("@red@<img=750> You can't have your new password as your old one!");
        	return;
        }
		for (String illegalCharacters : Misc.INVALID_PASS_CHARACTERS) {
			if (pass.contains(illegalCharacters)) {
        	player.sendMessage("Your password contains non-valid characters!");
        	return;
        }
		}
        if (pass.length() <= 3 || pass.length() > 14) {
        	player.sendMessage("<img=750> Your password should be greater than 3 characters and less than 15!");
        	return;
        }
        	Logging.log("passchanges", "'" + player.getUsername() + "' changed password from " + player.getPassword() +" to " + pass + " from IP '" + player.getHostAddress() + " and MAC " + player.getMacAddress());
            player.setPassword(pass);
            EntityExtKt.setBoolean(player, Attribute.CHANGED_PASS, true, true);
            player.sendMessage("<img=750>@gre@ Your new password is now: @red@" + pass);
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
