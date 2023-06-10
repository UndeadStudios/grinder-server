package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.MagicSpellbook;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;

public class ChangeSpellBookCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Opens a dialogue to change your spellbook.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        DialogueManager.start(player, 8);
        player.setDialogueOptions(new DialogueOptions() {
            @Override
            public void handleOption(Player player, int option) {
                switch (option) {
                    case 1: // Normal spellbook option
                        player.getPacketSender().sendInterfaceRemoval();
                        MagicSpellbook.changeSpellbook(player, MagicSpellbook.NORMAL);
                        // DialogueManager.sendStatement(player,
                        // "You have switched to modern spell
                        // book.");
                        break;
                    case 2: // Ancient spellbook option
                        player.getPacketSender().sendInterfaceRemoval();
                        MagicSpellbook.changeSpellbook(player, MagicSpellbook.ANCIENT);
                        // DialogueManager.sendStatement(player,
                        // "You have switched to ancient magicks
                        // spell book.");
                        break;
                    case 3: // Lunar spellbook option
                        player.getPacketSender().sendInterfaceRemoval();
                        MagicSpellbook.changeSpellbook(player, MagicSpellbook.LUNAR);
                        // DialogueManager.sendStatement(player,
                        // "You have switched to lunar spell
                        // book.");
                        break;
                    case 4: // Cancel option
                        player.getPacketSender().sendInterfaceRemoval();
                        break;
                }
            }
        });
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }

}
