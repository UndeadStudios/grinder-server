package com.grinder.game.content.minigame.castlewars;

import com.grinder.game.content.minigame.Party;
import com.grinder.game.entity.agent.player.Player;

import static com.grinder.game.content.minigame.castlewars.CastleWars.zamorakParty;
import static com.grinder.game.content.minigame.castlewars.CastleWars.saradominParty;

public class TeamManager {
    public static void joinSaradomin(Player player) {
        player.sendMessage("You joined saradomin team.");
        if (zamorakParty.inParty(player)) {
            zamorakParty.removePlayer(player);
        }
        saradominParty.addPlayer(player);
    }

    public static void joinZamorak(Player player) {
        if (saradominParty.inParty(player)) {
            saradominParty.removePlayer(player);
        }
        player.sendMessage("You joined zamorak team.");
        zamorakParty.addPlayer(player);
    }

    public static Party getOppositeTeam(Party party){
        if(party == saradominParty){
            return zamorakParty;
        }
        if(party == zamorakParty){
            return saradominParty;
        }
        return null;
    }
}
