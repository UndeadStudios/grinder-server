package com.grinder.game.definition.loader.impl;

import com.grinder.game.content.miscellaneous.npcs.AnnouncerNPC;
import com.grinder.game.content.miscellaneous.npcs.CleverBot;
import com.grinder.game.model.FacingDirection;
import com.grinder.game.model.Position;

public class NpcCustomSpawns {

    /**
     * Custom-Class Assigned NPC Spawns
     */
    public static void load() {
        // Gambling-Announcer NPC
        AnnouncerNPC gamblingBot = AnnouncerNPC.generateAnnouncerNPC(1895, new Position(2848, 2594, 0), "gambling");
        //gamblingBot.fetchDefinition().setName("Gambling Announcer");
        gamblingBot.setFace(FacingDirection.NORTH);

        // Duel-Arena Announcer NPC
        AnnouncerNPC duelArenaBot = AnnouncerNPC.generateAnnouncerNPC(3390, new Position(3369, 3271, 0), "duel_arena");
        duelArenaBot.fetchDefinition().setName("Granny Mike");
        duelArenaBot.setFace(FacingDirection.SOUTH);

        // Clever-bot NPC
        CleverBot cleverBot = CleverBot.generateBot(3835, new Position(2845, 2583, 0));
        cleverBot.setFace(FacingDirection.NORTH);
    }
}
