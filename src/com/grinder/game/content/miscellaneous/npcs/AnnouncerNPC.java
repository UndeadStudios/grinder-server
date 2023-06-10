package com.grinder.game.content.miscellaneous.npcs;

import com.grinder.game.World;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * NPC that announces victories between ingame minigames.
 */
public class AnnouncerNPC extends NPC {

    // WINNER & LOSER strings are replaced automatically.
    private static String[] gamblingSentences = {
            "LOSER has just lost a bet to WINNER!",
            "Bad luck LOSER! You just lost to WINNER.",
            "Better luck next time LOSER..",
    };

    // WINNER & LOSER strings are replaced automatically.
    private static String[] duelArenaSentences = {
            "LOSER had no chance against WINNER.",
            "Better luck next time LOSER!",
            "Congratulations to WINNER for winning against LOSER",
    };

    private static List<AnnouncerNPC> announcerNPCList = new ArrayList<>();

    private String npcType = "";

    public AnnouncerNPC(int npcId, Position position, String npcType) {
        super(npcId, position);

        this.npcType = npcType;
    }

    public static void announceWinner(Player winner, Player loser, String minigameType) {
        announcerNPCList.forEach(announcerNPC -> {
            if (announcerNPC.npcType.toLowerCase() == minigameType.toLowerCase()) {
                announcerNPC.say(generateWinningSentence(winner, loser, minigameType));
            }
        });
    }

    public static String generateWinningSentence(Player winner, Player loser, String minigameType) {
        String finalSentence = "";

        switch (minigameType) {
            case "duel_arena":
                finalSentence = duelArenaSentences[new Random().nextInt(duelArenaSentences.length)].replaceAll("WINNER", winner.getUsername()).replaceAll("LOSER", loser.getUsername());
                break;
            case "gambling":
                finalSentence = gamblingSentences[new Random().nextInt(gamblingSentences.length)].replaceAll("WINNER", winner.getUsername()).replaceAll("LOSER", loser.getUsername());
                break;
            default:
                break;
        }

        return finalSentence;
    }

    public static AnnouncerNPC generateAnnouncerNPC(int npcId, Position position, String npcType) {
        AnnouncerNPC announcerNPC = new AnnouncerNPC(npcId, position, npcType);
        World.getNpcAddQueue().add(announcerNPC);
        announcerNPCList.add(announcerNPC);

        return announcerNPC;
    }
}
