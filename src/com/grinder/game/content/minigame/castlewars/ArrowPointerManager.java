package com.grinder.game.content.minigame.castlewars;

import com.grinder.game.content.minigame.Party;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Coordinate;
import com.grinder.util.oldgrinder.StreamHandler;
import static com.grinder.game.content.minigame.castlewars.CastleWars.zamorakParty;
import static com.grinder.game.content.minigame.castlewars.CastleWars.saradominParty;

public class ArrowPointerManager {
    private static int[] pointedEntity = { -1, -1 };
    private static Coordinate[] pointedFlag = new Coordinate[2];

    public static void resetPointers() {
        pointedEntity[CastleWarsConstants.SARADOMIN_TEAM] = -1;
        pointedEntity[CastleWarsConstants.ZAMORAK_TEAM] = -1;
        pointedFlag[CastleWarsConstants.SARADOMIN_TEAM] = null;
        pointedFlag[CastleWarsConstants.ZAMORAK_TEAM] = null;
    }

    public static boolean hasActivePointers(Party party) {
        if (party != null) {
            int teamID = party.getTeamID();
            if (teamID >= 0 && teamID <= 1) {
                return pointedEntity[teamID] != -1 || pointedFlag[teamID] != null;
            }
        }
        return false;
    }

    public static void removePointers(Party party) {
        if (party != null) {
            int teamID = party.getTeamID();
            pointedFlag[teamID] = null;
            pointedEntity[teamID] = -1;
        }
    }

    public static void setFlagPointer(Party party, Coordinate coordinate) {
        if (party != null) {
            int teamID = party.getTeamID();
            pointedFlag[teamID] = coordinate;
            pointedEntity[teamID] = -1;
        }
    }

    public static void setEntityPointer(Party party, int entityID) {
        if (party != null) {
            int teamID = party.getTeamID();
            pointedFlag[teamID] = null;
            pointedEntity[teamID] = entityID;
        }
    }

    public static void hidePointer(Player player) {
        //player.getPA().removeHeadArrow();
    }

    public static void hidePointer(Party party) {
        for (Player player : party.getPlayers()) {
            if (player != null) {
                hidePointer(player);
            }
        }
    }

    public static void showPointer(Party party) {
        Party oppositeParty = TeamManager.getOppositeTeam(party);
        int partyID = oppositeParty.getTeamID();

        for (Player player : party.getPlayers()) {
            if (player != null) {
                if (pointedFlag[partyID] != null) {
                    int x = pointedFlag[partyID].getX();
                    int y = pointedFlag[partyID].getY();
                    int height = pointedFlag[partyID].getH();
                    StreamHandler.createObjectHints(player, x, y, height, 2);
                } else if (pointedEntity[partyID] != -1) {
                    /*
                    if (player.playerId == pointedEntity[partyID]) {
                        continue;
                    }
                    StreamHandler.createPlayerHints(player, 10, 2, pointedEntity[partyID]);
                    */

                }

            }
        }
    }

    public static void showPointer(Player player) {
        if (player == null) {
            return;
        }
        Party party = player.getCurrentParty();
        if (party != saradominParty && party != zamorakParty) {
            return;
        }
        Party oppositeParty = TeamManager.getOppositeTeam(party);
        int partyID = oppositeParty.getTeamID();

        if (pointedFlag[partyID] != null) {
            int x = pointedFlag[partyID].getX();
            int y = pointedFlag[partyID].getY();
            int height = pointedFlag[partyID].getH();
            StreamHandler.createObjectHints(player, x, y, height, 2);
        } else if (pointedEntity[partyID] != -1) {
            /*
            if (player.getAsPlayer().getUID() != pointedEntity[partyID]) {
                StreamHandler.createPlayerHints(player, 10, 2, pointedEntity[partyID]);
            }
             */
        }
    }
}
