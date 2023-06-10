package com.grinder.game.content.minigame;

import com.grinder.game.content.skill.SkillConstants;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.area.Region;
import com.grinder.game.model.item.Item;
import com.grinder.util.Misc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.grinder.game.content.minigame.castlewars.CastleWars.findAvailableSpot;

public class Party {
    private CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<Player>();

    private Map<Integer, Long> inactivePlayers = new HashMap<Integer, Long>();

    private final String name;
    private final int teamID;

    public Party(String name, int teamID) {
        this.name = name;
        this.teamID = teamID;
    }

    /**
     * Request player removal from the party.
     *
     * @param player: Player to be removed.
     */
    public void leaveParty(Player player) {
        removePlayer(player);
    }

    /**
     * Add a player client instance to the party.
     *
     * @param player: Player to be inserted.
     */
    public void addPlayer(Player player) {
        if (player != null) {
            if (!players.contains(player)) {
                players.add(player);
            }
            player.setCurrentParty(this);
        }
    }

    /**
     * Remove a player client instance from the party.
     *
     * @param player: Player to be removed.
     */
    public void removePlayer(Player player) {
        if (player != null) {
            players.remove(player);
            player.setCurrentParty(null);
        }
    }

    /**
     * Verify if a player is in this party.
     *
     * @param player: Player to be searched.
     * @return: True in case player is in party.
     */
    public boolean inParty(Player player) {
        if (player != null) {
            return players.contains(player);
        }
        return false;
    }

    /**
     * Check the amount of players in this party.
     *
     * @return: Amount of player in this party.
     */
    public int memberCount() {
        return players.size();
    }

    /**
     * Teleport every player in this party to a random spot in a range of coordinates.
     *
     * @param startX: Initial X coordinate.
     * @param startY: Initial Y coordinate.
     * @param finalX: Final X coordinate.
     * @param finalY: Final Y coordinate.
     * @param height: Height position.
     */
    public void teleportAll(int startX, int startY, int finalX, int finalY, int height) {
        for (Player player : players) {
            player.moveTo(findAvailableSpot(new Boundary(startX, finalX, startY, finalY), height));
        }
    }

    /**
     * Send a message to all players in the party.
     *
     * @param message: Message to be sent to all players.
     */
    public void messageAll(String message) {
        for (Player player : players) {
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

    /**
     * Give an item to every player in the party.
     *
     * @param itemID: Item to be given.
     * @param itemAmount: Amount of item to be given.
     */
    public void giveItem(int itemID, int itemAmount) {
        for (Player player : players) {
            if (player != null) {
                player.getInventory().add(new Item(itemID, itemAmount));
            }
        }
    }

    /**
     * Get total level of the party.
     *
     * @return: Party's total level.
     */
    public int getTotalLevel() {
        int totalLevel = 0;
        for (Player player : players) {
            if (player != null) {
                for (int i = 0; i < player.getSkills().getLevels().length; i++) {
                    totalLevel += player.getSkills().getLevels()[i];
                }
            }
        }

        return totalLevel;
    }

    /**
     * Get total combat level of the party.
     *
     * @return: Party's total combat level.
     */
    public int getTotalCombatLevel() {
        int totalLevel = 0;
        for (Player player : players) {
            if (player != null) {
                totalLevel += player.getSkills().getLevel(Skill.ATTACK);
                totalLevel += player.getSkills().getLevel(Skill.STRENGTH);
                totalLevel += player.getSkills().getLevel(Skill.RANGED);
                totalLevel += player.getSkills().getLevel(Skill.MAGIC);
                totalLevel += player.getSkills().getLevel(Skill.HITPOINTS);
                totalLevel += player.getSkills().getLevel(Skill.DEFENCE);
                totalLevel += player.getSkills().getLevel(Skill.PRAYER);
            }
        }

        return totalLevel;
    }

    public String toString() {
        return name + ":" + memberCount() + " : " + players.toString();
    }

    public CopyOnWriteArrayList<Player> getPlayers() {
        return players;
    }

    public void addParty(Party party) {
        for (Player partyPlayer : party.getPlayers()) {
            addPlayer(partyPlayer);
        }
    }

    public void clear(boolean changeParty) {
        if (changeParty) {
            for (Player client : getPlayers()) {
                client.getAsPlayer().setCurrentParty(null);
            }
        }
        players.clear();
        inactivePlayers.clear();
    }

    public boolean isInactive(Player player) {
        if (player != null) {
            return inactivePlayers.containsKey(new Integer(player.accountID));
        }
        return false;
    }

    public long getPlayerInactivity(Player player) {
        if (player != null) {
            Long time = inactivePlayers.get(new Integer(player.accountID));
            if (time != null) {
                return time.longValue();
            }
        }
        return -1;
    }

    public void putInactivePlayer(Player player) {
        if (player != null) {
            inactivePlayers.put(player.accountID, System.currentTimeMillis());
        }
    }

    public void putInactivePlayer(Player player, long time) {
        if (player != null) {
            inactivePlayers.put(player.accountID, time);
        }
    }

    public void removeInactivePlayer(Player player) {
        if (player != null) {
            removeInactivePlayer(player.accountID);
        }
    }

    public void removeInactivePlayer(int playerID) {
        inactivePlayers.remove(playerID);
    }

    public Map<Integer, Long> getInactivePlayers() {
        return inactivePlayers;
    }

    public int getTeamID() {
        return teamID;
    }

    public String getName() {
        return name;
    }
}
