package com.grinder.game.model;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.content.skill.skillable.impl.magic.SpellCasting;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.model.sound.Sounds;

/**
 * Represents a player's magic spellbook.
 *
 * @author relex lawl
 */

public enum MagicSpellbook {

    NORMAL(1151, TeleportType.NORMAL),
    ANCIENT(12855, TeleportType.ANCIENT),
    LUNAR(29999, TeleportType.LUNAR),
    ARCEEUS(39999, TeleportType.ARCEEUS);

    /**
     * The spellbook's interface id
     */
    private final int interfaceId;
    /**
     * The spellbook's teleport type
     */
    private TeleportType teleportType;

    /**
     * The MagicSpellBook constructor.
     *
     * @param interfaceId The spellbook's interface id.
     * @param message     The message received upon switching to said spellbook.
     */
    private MagicSpellbook(int interfaceId, TeleportType teleportType) {
        this.interfaceId = interfaceId;
        this.teleportType = teleportType;
    }

    /**
     * Gets the MagicSpellBook for said id.
     *
     * @param id The ordinal of the SpellBook to fetch.
     * @return The MagicSpellBook who's ordinal is equal to id.
     */
    public static MagicSpellbook forId(int id) {
        for (MagicSpellbook book : MagicSpellbook.values()) {
            if (book.ordinal() == id) {
                return book;
            }
        }
        return NORMAL;
    }

    /**
     * Changes the magic spellbook for a player.
     *
     * @param player The player changing spellbook.
     * @param book   The new spellbook.
     */
    public static void changeSpellbook(Player player, MagicSpellbook book) {

        if(book == ANCIENT) {
            if (!QuestManager.hasCompletedQuest(player, "Desert Treasure")) {
                player.sendMessage("You must complete the quest 'Desert Treasure' to be able to use Ancient Magicks.");
                return;
            }
        }
        if (book == LUNAR) {
            if (player.getSkillManager().getMaxLevel(Skill.DEFENCE) < 40) {
            	DialogueManager.sendStatement(player, "You need at least level 40 Defence to use the Lunar spellbook.");
                return;
            }
        }

        //Update spellbook
        player.setSpellbook(book);

        //Reset autocast
        SpellCasting.setSpellToCastAutomatically(player, null);
        
        // Play sound
        player.getPacketSender().sendSound(Sounds.SWITCH_SPELLBOOK);
        
        // Finish Task
        AchievementManager.processFor(AchievementType.MYSTERY_MIND, player);

      //Send the new spellbook interface to the client side tabs
        if (book == LUNAR) {
        	DialogueManager.sendStatement(player, "You have switched to lunar magic spell book.");
        player.getPacketSender().sendTabInterface(6, player.getSpellbook().getInterfaceId());
    } else if (book == ANCIENT) {
        //Send the new spellbook interface to the client side tabs
    	DialogueManager.sendStatement(player, "You have switched to ancient magic spell book.");
    	player.getPacketSender().sendTabInterface(6, player.getSpellbook().getInterfaceId());
    } else {
        //Send the new spellbook interface to the client side tabs
    	DialogueManager.sendStatement(player, "You have switched to modern magic spell book.");
    	player.getPacketSender().sendTabInterface(6, player.getSpellbook().getInterfaceId());
    }
    }

    /**
     * Gets the interface to switch tab interface to.
     *
     * @return The interface id of said spellbook.
     */
    public int getInterfaceId() {
        return interfaceId;
    }

    /**
     * Gets the spellbook's teleport type
     *
     * @return The teleport type of said spellbook.
     */
    public TeleportType getTeleportType() {
        return teleportType;
    }
}
