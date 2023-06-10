package com.grinder.game.content.minigame.castlewars;

import com.grinder.game.collision.CollisionManager;
import com.grinder.game.content.minigame.Party;
import com.grinder.game.content.task_new.DailyTask;
import com.grinder.game.content.task_new.PlayerTaskManager;
import com.grinder.game.content.task_new.WeeklyTask;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.movement.pathfinding.PathFinder;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.impl.BarricadeEntity;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.model.*;
import com.grinder.game.model.areas.Area;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.consumable.edible.Edible;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.bank.BankUtil;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.oldgrinder.EquipSlot;
import com.grinder.util.oldgrinder.StreamHandler;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static com.grinder.game.content.minigame.castlewars.CastleWarsConstants.*;
import static com.grinder.game.content.minigame.castlewars.CastleWarsDoorsManager.*;
import static com.grinder.game.content.minigame.castlewars.FlagManager.*;
import static com.grinder.game.content.minigame.castlewars.LobbyManager.*;
import static com.grinder.util.ObjectID.*;

public class CastleWars extends Area {
    public static CastleWarsParty saradominParty = new CastleWarsParty("Saradomin Team", 0);
    public static CastleWarsParty saradominLobby = new CastleWarsParty("Saradomin Lobby", 0);

    public static CastleWarsParty zamorakParty = new CastleWarsParty("Zamorak Team", 1);
    public static CastleWarsParty zamorakLobby = new CastleWarsParty("Zamorak Lobby", 1);

    public enum DoorState {
        OPENED, CLOSED, LOCKED, BROKEN;
    }
    public enum CatapultState {
        OPERATIONAL, BURNING, DESTROYED;
    }
    protected static DoorState smallDoorState[] = {DoorState.CLOSED, DoorState.CLOSED};
    protected static DoorState bigDoorState[] = {DoorState.CLOSED, DoorState.CLOSED};
    protected static FlagState flagState[] = {FlagState.SAFE, FlagState.SAFE};
    protected static CatapultState catapultState[] = new CatapultState[]{CatapultState.OPERATIONAL, CatapultState.OPERATIONAL};
    protected static Coordinate[] droppedFlag = new Coordinate[2];

    public CastleWars() {
        super(new Boundary(2368, 2431, 3072, 3135), new Boundary(2368, 2431, 9480, 9529));
    }

    @Override
    public void process(Agent agent) {
        if (agent.isPlayer()) {
            Player player = agent.getAsPlayer();
            if (!zamorakParty.getPlayers().contains(agent) && !saradominParty.getPlayers().contains(agent) && !isInCastleWarsLobby(player)) {
                if (saradominLobby.getPlayers().contains(player)) {
                    LobbyManager.leaveLobbyRoom(player, saradominLobby);
                }
                if (zamorakLobby.getPlayers().contains(player)) {
                    LobbyManager.leaveLobbyRoom(player, zamorakLobby);
                }
                removeGameItems(agent.getAsPlayer());
                moveToCastleWars(agent.getAsPlayer());
            }
        }

    }

    @Override
    public void defeated(Player player, Agent agent) {

    }

    @Override
    public void enter(Agent agent) {
        super.enter(agent);
    }

    @Override
    public void leave(Agent agent) {
        super.leave(agent);
    }

    @Override
    public void onPlayerRightClick(Player player, Player rightClicked, int option) {

    }

    @Override
    public boolean isMulti(Agent agent) {
        return true;
    }

    @Override
    public boolean canTeleport(Player player) {
        return false;
    }

    @Override
    public boolean canAttack(Agent attacker, Agent target) {
        if (attacker.isPlayer()) {
            if (target.isPlayer()) {
                if (attacker.getAsPlayer().getCurrentParty() == target.getAsPlayer().getCurrentParty()) {
                    attacker.getAsPlayer().sendMessage("You can't attack your own teammates!");
                    return false;
                }
            }
            return !isInCastleWarsLobby(attacker.getAsPlayer());
        } else {
            return false;
        }
    }

    @Override
    public boolean canTrade(Player player, Player target) {
        return false;
    }

    @Override
    public boolean canDrink(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean canEat(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean dropItemsOnDeath(Player player, Optional<Player> killer) {
        return false;
    }

    @Override
    public boolean handleObjectClick(Player player, GameObject obj, int actionType) {
        return false;
    }

    @Override
    public boolean handleDeath(Player player, Optional<Player> killer) {
        if (killer.isPresent()) {
            giveLife(player, killer.get());
        } else {
            giveLife(player, null);
        }
        return true;
    }

    @Override
    public boolean isSafeForHardcore() {
        return true;
    }

    @Override
    public boolean handleDeath(NPC npc) {
        return false;
    }

    enum RockState {COLLAPSED, CLEARED;}
    protected static RockState[][] rockState = new RockState[][]{{RockState.COLLAPSED, RockState.COLLAPSED}, {RockState.COLLAPSED, RockState.COLLAPSED}};
    protected static int doorHealth[] = {MAX_DOOR_HEALTH, MAX_DOOR_HEALTH};
    protected static int teamPoints[] = new int[2];
    private static int[] teamReward = new int[2];

    public static CopyOnWriteArrayList<NPC> zammyBarricades = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<NPC> saraBarricades = new CopyOnWriteArrayList<>();

    public enum GameState{AWAITING, STARTING, RUNNING;};
    public static GameState gameState = GameState.AWAITING;
    public static long updateTime;
    public static int minutesLeft;
    private static long lastSpotOffer;
    private static boolean updateInterface;
    private static int processTick;

    public static GameState getGameState() {
        return gameState;
    }

    public static void process() {
        processTick++;
        if (gameState == GameState.AWAITING) {
            if (zamorakLobby.memberCount() >= MIN_TEAM_MEMBERS && saradominLobby.memberCount() >= MIN_TEAM_MEMBERS) {
                gameState = GameState.STARTING;
                updateTime = System.currentTimeMillis();
            }
        } else if (gameState == GameState.STARTING) {
            // When there's no enough players in the waiting room, change back
            // to awaiting state.

            int currentTimeLeft = getTimeLeft();
            if (minutesLeft != currentTimeLeft) {
                minutesLeft = currentTimeLeft;
                if (minutesLeft == 3) {
                    PlayerUtil.broadcastMessage("<img=792> Castle Wars minigame is starting in " + minutesLeft + " minutes!");
                }
                Stream<Player> stream = Stream.concat(zamorakLobby.getPlayers().stream(), saradominLobby.getPlayers().stream());

                stream.forEach((player) -> {
                    if (player != null) {
                        int add = 0;
                        if (gameState == GameState.RUNNING) {
                            add += 2;
                        }
                        player.getPacketSender().sendString(11480, "Time until next game starts: " + (getTimeLeft()+add));
                    }
                });
            }

            if (currentTimeLeft <= 0) {
                if (zamorakLobby.memberCount() < MIN_TEAM_MEMBERS || saradominLobby.memberCount() < MIN_TEAM_MEMBERS) {
                    gameState = GameState.AWAITING;
                } else {
                    // Start the game
                    startGame();
                }
            }

        } else if (gameState == GameState.RUNNING) {
            int currentTimeLeft = getTimeLeft();

            if (zamorakParty.memberCount() < 1 || saradominParty.memberCount() < 1) {
                Stream<Player> stream = Stream.concat(zamorakParty.getPlayers().stream(), saradominParty.getPlayers().stream());

                stream.forEach((player) -> {
                    if (player != null) {
                        player.sendMessage("The game was ended because there was not enough players.");

                        CastleWarsParty party = (CastleWarsParty) player.getCurrentParty();
                        player.getTrading().closeTrade();
                        player.getPacketSender().sendWalkableInterface(-1);
                        removeGameItems(player);
                        player.getPacketSender().sendInteractionOption("null", 2, true);
                        StreamHandler.createObjectHints(player, 0, 0, 0, 0);
                        player.resetAttributes();

                    }
                });
                gameState = GameState.AWAITING;
                updateTime = System.currentTimeMillis();
                saradominParty.teleportAll(2439, 3082, 2445, 3097, 0);
                zamorakParty.teleportAll(2439, 3082, 2445, 3097, 0);

                saradominParty.clear(true);
                zamorakParty.clear(true);
            }

            if (minutesLeft != currentTimeLeft) {
                minutesLeft = currentTimeLeft;

                Stream<Player> stream = Stream.concat(zamorakLobby.getPlayers().stream(), saradominLobby.getPlayers().stream());

                stream.forEach((player) -> {
                    if (player != null) {
                        int add = 0;
                        if (gameState == GameState.RUNNING) {
                            add += 2;
                        }
                        player.getPacketSender().sendString(11480, "Time until next game starts: " + (getTimeLeft()+add));
                    }
                });
                setUpdateInterface(true);
            }

            if (minutesLeft > 2) {
                if (System.currentTimeMillis() - lastSpotOffer > 10000) {
                    lastSpotOffer = System.currentTimeMillis();
                    if (zamorakParty.memberCount() > saradominParty.memberCount() && zamorakParty.memberCount() >= 1 && saradominParty.memberCount() >= 1) {
                        LobbyManager.offerSpot(saradominLobby);
                    } else if (saradominParty.memberCount() > zamorakParty.memberCount() && zamorakParty.memberCount() >= 1 && saradominParty.memberCount() >= 1) {
                        LobbyManager.offerSpot(zamorakLobby);
                    }
                }
            }

            for (Player player : zamorakParty.getPlayers()) {
                processPlayer(player);
            }

            for (Player player : saradominParty.getPlayers()) {
                processPlayer(player);
            }

            boolean interruptGame = zamorakParty.memberCount() <= 0 && saradominParty.memberCount() <= 0;

            if (updateInterface) {
                setUpdateInterface(false);
            }

            if (minutesLeft <= 0 || interruptGame) {
                gameState = GameState.AWAITING;
                updateTime = System.currentTimeMillis();
                finishGame();
            }
        }
    }

    private static void processPlayer(Player player) {
        if (player == null) {
            return;
        }
        Party playerParty = player.getCurrentParty();

        if (playerParty != zamorakParty && playerParty != saradominParty) {
            return;
        }

        if (isHoldingFlag(player)) {
            if (isHoldingFlag(player, playerParty)) {
                if (getCastleTeam(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ()) == playerParty) {
                    removeHoldingFlag(player);
                    player.getEquipment().refreshItems();
                    returnFlagToStand(playerParty);
                    player.sendMessage("The flag returned to stand.");

                    if (playerParty.getTeamID() == ZAMORAK_TEAM) {
                        saradominParty.getPlayers().stream().forEach((p) -> {
                            StreamHandler.createObjectHints(p, 0, 0, 0, 0);
                        });
                    } else {
                        zamorakParty.getPlayers().stream().forEach((p) -> {
                            StreamHandler.createObjectHints(p, 0, 0, 0, 0);
                        });
                    }
                }
            }
        }
        if (!player.inCastleWarsRegion()) {
            if (isHoldingFlag(player)) {
                if (isHoldingFlag(player, zamorakParty)) {
                    returnFlagToStand(zamorakParty);
                } else if (isHoldingFlag(player, saradominParty)) {
                    returnFlagToStand(saradominParty);
                }
                removeHoldingFlag(player);
            }
            removeFromGameToLobby(player.getCurrentParty(), player);
        }
        /*
        if (inTeamBase(player)) {
            if (player.castleWarsIddleTimer > 0) {
                long timeSpent = System.currentTimeMillis() - player.castleWarsIddleTimer;
                int secondsLeft = 120 - (int) (timeSpent / 1000);

                if (secondsLeft < 0) {// Player is for too long on the base;
                    player.sendMessage("You was idle for too long and removed from the game.");
                    removeFromGameToLobby(playerParty, player);
                }
            }
        }*/

        if (updateInterface) {
            updateInterface(player);
        }
    }

    protected static void resetPlayerProgress(Player player) {
        if (player != null) {
            player.totalShotCatapultDamage = 0;
            player.cwGameCaptures = 0;
            player.cwGameCatapultDamage = 0;
            player.cwGameKills = 0;
        }
    }

    protected static void setUpdateInterface(boolean updateInterface) {
        CastleWars.updateInterface = updateInterface;
    }

    private static int getDoorHealth(Party party) {
        int teamID = getTeamID(party);
        if (teamID == -1) {
            return 0;
        }
        return (int) ((doorHealth[teamID] * 100f) / 300f);
    }

    public static void removeGameItems(Player c) {

        for (int gameItem : GAME_ITEMS) {

            Item[] equipmentItems = c.getEquipment().getItems();
            for (int slot = 0; slot < equipmentItems.length; slot++) {
                Item item = equipmentItems[slot];
                if (item != null && item.getId() == gameItem) {
                    c.getEquipment().delete(item, slot);
                }
            }

            for (int slot = 0; slot < c.getInventory().getItems().length; slot++) {
                int itemID = c.getInventory().getItems()[slot].getId();
                if (itemID == gameItem) {
                    int amount = c.getInventory().getItems()[slot].getAmount();
                    c.getInventory().delete(new Item(itemID, amount), slot);
                }

            }

        }
        c.getInventory().refreshItems();
        c.updateAppearance();
    }

    protected static int getTeamID(Party party) {
        if (party == saradominParty) {
            return SARADOMIN_TEAM;
        } else if (party == zamorakParty) {
            return ZAMORAK_TEAM;
        }
        return -1;
    }

    public static void startGame() {
        gameState = GameState.RUNNING;
        updateTime = System.currentTimeMillis();
        minutesLeft = GAME_DURATION;

        resetGame();

        saradominLobby.teleportAll(2423, 3072, 2430, 3080, 1);
        zamorakLobby.teleportAll(2369, 3127, 2376, 3135, 1);

        saradominParty.clear(true);
        zamorakParty.clear(true);

        saradominParty.resetEffects();
        zamorakParty.resetEffects();

        saradominParty.addParty(saradominLobby);
        zamorakParty.addParty(zamorakLobby);

        saradominLobby.clear(false);
        zamorakLobby.clear(false);

        Stream<Player> stream = Stream.concat(zamorakParty.getPlayers().stream(), saradominParty.getPlayers().stream());

        stream.forEach((player) -> {
            if (player != null) {
                player.castleWarsIddleTimer = System.currentTimeMillis();
                resetPlayerProgress(player);
                player.getPacketSender().sendWalkableInterface(11146);
                player.getPacketSender().sendInteractionOption("Attack", 2, true);
                player.getPacketSender().sendString(11155, getTimeLeft() + " Min");

                if (player.getNpcTransformationId() != -1) {
                    player.setNpcTransformationId(-1);
                }
            }
        });

        updateInterface = true;
    }

    public static void giveLife(Player player, Player killer) {
        Party playerParty = player.getCurrentParty();
        Position freeSpot = null;

        boolean isHoldingFlag = isHoldingFlag(player);
        if (isHoldingFlag) {
            dropFlag(player);
        }

        if (killer != null) {
            killer.cwGameKills++;
            killer.castleWarsKills++;
            //killer.sendMessage("You have killed " + player.getFormattedName() + ", you now have " + killer.cwGameKills + (killer.cwGameKills > 1 ? " kills." : " kill"));
        }

        if (playerParty == saradominParty) {
            freeSpot = findAvailableSpot(new Boundary(2423, 2430, 3072, 3080), 1);
        } else if (playerParty == zamorakParty) {
            freeSpot = new Position(2369+Misc.random(7), 3127+Misc.random(8), 1);
            freeSpot = findAvailableSpot(new Boundary(2369, 2376, 3127, 3135), 1);
        }

        if (freeSpot != null) {
            player.moveTo(freeSpot);
            playerEnteredBase(player);
        }
    }

    public static void finishGame() {

        updateGameReward();

        boolean elegibleForReward = saradominParty.memberCount() >= 10 && zamorakParty.memberCount() >= 10;

        Stream<Player> stream = Stream.concat(zamorakParty.getPlayers().stream(), saradominParty.getPlayers().stream());

        stream.forEach((player) -> {
            if (player != null) {
                PlayerTaskManager.progressTask(player, DailyTask.CASTLE_WARS);
                PlayerTaskManager.progressTask(player, WeeklyTask.CASTLE_WARS);
                CastleWarsParty party = (CastleWarsParty) player.getCurrentParty();
                player.getTrading().closeTrade();
                player.getPacketSender().sendWalkableInterface(-1);
                removeGameItems(player);
                player.getPacketSender().sendInteractionOption("null", 2, true);
                StreamHandler.createObjectHints(player, 0, 0, 0, 0);
                player.resetAttributes();
                player.castleWarsGamesPlayed++;

                int teamScorePoints = teamReward[party.getTeamID()];

                if (elegibleForReward) {
                    /*
                    if (player.accountID == CastleWarsScoreBoard.playerWithMostCapturesID) {
                        teamScorePoints += 2;
                        player.sendMessage("@or2@You received 2 extra scores for being the player with most captures.");
                    }
                    if (player.accountID == CastleWarsScoreBoard.playerWithMostKillsID) {
                        teamScorePoints += 2;
                        player.sendMessage("@or2@You received 2 extra scores for being the player with most kills.");
                    }
                    if (player.accountID == CastleWarsScoreBoard.playerWithMostCatapultDamageID) {
                        teamScorePoints += 2;
                        player.sendMessage("@or2@You received 2 extra scores for being the player with most catapult damage.");
                    }
                     */
                }
                if (teamPoints[SARADOMIN_TEAM] > teamPoints[ZAMORAK_TEAM]) {
                    if (party == zamorakParty) {
                        //lost
                        player.getPoints().increase(AttributeManager.Points.CASTLEWARS_LOST_GAMES, 1);
                        player.getPacketSender().sendJinglebitMusic(83, 0); // lost jingle
                    } else {
                        //won
                        player.getPoints().increase(AttributeManager.Points.CASTLEWARS_WON_GAMES, 1);
                        player.getPacketSender().sendJinglebitMusic(82, 0); // Win jingle
                    }

                } else if (teamPoints[ZAMORAK_TEAM] > teamPoints[SARADOMIN_TEAM]) {
                    if (party == zamorakParty) {
                        //won
                        player.getPoints().increase(AttributeManager.Points.CASTLEWARS_WON_GAMES, 1);
                        player.getPacketSender().sendJinglebitMusic(82, 0); // Win jingle
                    } else {
                        //lost
                        player.getPoints().increase(AttributeManager.Points.CASTLEWARS_LOST_GAMES, 1);
                        player.getPacketSender().sendJinglebitMusic(83, 0); // lost jingle
                    }
                }


                if (teamScorePoints > 0) {
                    player.castleWarsScore += teamScorePoints;
                    int pointsReward = 2 + Misc.random(5) + teamScorePoints;

                    new DialogueBuilder(DialogueType.ITEM_STATEMENT).setItem(ITEM_TICKETS, 200)
                            .setText("You gained "  + pointsReward + " tickets for your work!")
                            .start(player);
                    if (player.getInventory().countFreeSlots() > 0) {
                        player.getInventory().add(new Item(ITEM_TICKETS, pointsReward));
                    } else {
                        BankUtil.addToBank(player, new Item(ITEM_TICKETS, pointsReward));
                        player.sendMessage("The reward was placed in your bank.");
                    }
                } else {
                    new DialogueBuilder(DialogueType.ITEM_STATEMENT).setItem(ITEM_TICKETS, 200)
                            .setText("You did not gain any tickets for not scoring anything!")
                            .start(player);
                }
            }
        });
        if (teamPoints[SARADOMIN_TEAM] > teamPoints[ZAMORAK_TEAM]) {
            //Saradomin won
            PlayerUtil.broadcastMessage("<img=792>@red@ Saradomin's Team has beat Zamorak's Team at Castle Wars at with a score of " + teamPoints[SARADOMIN_TEAM] + "-" + teamPoints[ZAMORAK_TEAM] + "!");

        } else if (teamPoints[ZAMORAK_TEAM] > teamPoints[SARADOMIN_TEAM]) {
            //Zamorak Won
            PlayerUtil.broadcastMessage("<img=792>@red@ Zamorak's Team has beat Saradomin's Team at Castle Wars with a score of " + teamPoints[ZAMORAK_TEAM] + "-" + teamPoints[SARADOMIN_TEAM] + "!");

        } else {
            //draw
            PlayerUtil.broadcastMessage("<img=792>@red@ Castle Wars ended with a draw with a score of " + teamPoints[SARADOMIN_TEAM] + "-" + teamPoints[ZAMORAK_TEAM] + "!");

        }
        saradominParty.teleportAll(2439, 3082, 2445, 3097, 0);
        zamorakParty.teleportAll(2439, 3082, 2445, 3097, 0);

        saradominParty.clear(true);
        zamorakParty.clear(true);

        Stream<Player> streamLobby = Stream.concat(zamorakLobby.getPlayers().stream(), saradominLobby.getPlayers().stream());
        streamLobby.forEach((p) -> {
            if (p != null) {
                if (zamorakLobby.memberCount() >= 1 && saradominLobby.memberCount() >= 1) {
                    p.getPacketSender().sendString(11480, "Time until next game starts: " + getTimeLeft());
                } else {
                    p.getPacketSender().sendString(11480, "Waiting for players to join the other team.");
                }
            }
        });

    }

    private static void updateGameReward() {
        teamReward[SARADOMIN_TEAM] = 0;
        teamReward[ZAMORAK_TEAM] = 0;

        if (teamPoints[SARADOMIN_TEAM] > teamPoints[ZAMORAK_TEAM]) {
            teamReward[SARADOMIN_TEAM] = 3;
            if (teamPoints[ZAMORAK_TEAM] > 0) {
                teamReward[ZAMORAK_TEAM] = 1;
            }
        } else if (teamPoints[SARADOMIN_TEAM] == teamPoints[ZAMORAK_TEAM]) {
            int points = teamPoints[SARADOMIN_TEAM] > 0 ? 2 : 1;
            teamReward[SARADOMIN_TEAM] = points;
            teamReward[ZAMORAK_TEAM] = points;
        } else if (teamPoints[ZAMORAK_TEAM] > teamPoints[SARADOMIN_TEAM]) {
            teamReward[ZAMORAK_TEAM] = 3;
            if (teamPoints[SARADOMIN_TEAM] > 0) {
                teamReward[SARADOMIN_TEAM] = 1;
            }
        }
    }

    public static int getTotalTimeLeft() {
        if (gameState != GameState.RUNNING) {
            return (int) (GAME_AWAIT_DURATION - TimeUnit.MILLISECONDS.toMinutes((System.currentTimeMillis() - updateTime)));
        }
        return (int) ((GAME_DURATION + GAME_AWAIT_DURATION) - TimeUnit.MILLISECONDS.toMinutes((System.currentTimeMillis() - updateTime)));
    }

    public static int getTimeLeft() {
        if (gameState != GameState.RUNNING) {
            int timeLeft = (int) (GAME_AWAIT_DURATION - TimeUnit.MILLISECONDS.toMinutes((System.currentTimeMillis() - updateTime)));
            if (timeLeft < 0) {
                timeLeft = GAME_AWAIT_DURATION;
            }
            return timeLeft;
        }
        int timeLeft = (int) (GAME_DURATION - TimeUnit.MILLISECONDS.toMinutes((System.currentTimeMillis() - updateTime)));
        if (timeLeft < 0) {
            timeLeft = 0;
        }
        return timeLeft;
    }

    public static void resetGame() {

        // Collapse all rocks
        RockManager.collapseAllRocks();

        // Close doors
        closeDoubleDoor(saradominParty);
        closeDoubleDoor(zamorakParty);

        closeSmallDoor(saradominParty);
        closeSmallDoor(zamorakParty);

        // Reset current arrow pointers
        ArrowPointerManager.resetPointers();

        // Remove dropped flags
        removeDroppedFlag(saradominParty);
        removeDroppedFlag(zamorakParty);

        // Return flags to stand
        returnFlagToStand(saradominParty);
        returnFlagToStand(zamorakParty);

        // Reset catapult states
        //setCatapultRepaired(saradominParty);
        //setCatapultRepaired(zamorakParty);

        // Restart door health
        doorHealth[ZAMORAK_TEAM] = MAX_DOOR_HEALTH;
        doorHealth[SARADOMIN_TEAM] = MAX_DOOR_HEALTH;

        // Reset points
        teamPoints[ZAMORAK_TEAM] = 0;
        teamPoints[SARADOMIN_TEAM] = 0;

        // Reset flag state
        flagState[ZAMORAK_TEAM] = FlagState.SAFE;
        flagState[SARADOMIN_TEAM] = FlagState.SAFE;

        // Reset dropped flag coordinates.
        droppedFlag[ZAMORAK_TEAM] = null;
        droppedFlag[SARADOMIN_TEAM] = null;

        // Reset bosskill progress
        saradominParty.reset();
        zamorakParty.reset();

        // Reset all ropes on walls
        RopeManager.removeAllRopes();

        // Reset game barricades.
        resetBarricades();

        saraBarricades.clear();
        zammyBarricades.clear();

        // Reset invite counter.
        lastSpotOffer = System.currentTimeMillis();

        setUpdateInterface(true);

    }

    public static void resetBarricades() {
        Stream<NPC> stream = Stream.concat(saraBarricades.stream(), zammyBarricades.stream());
        stream.forEach((barricade) -> {
            barricade.appendDeath();
        });
    }

    public static boolean processClick(final Player player, final int objectID, final int objectX, final int objectY, final int height, final int direction, final int type, int clickType) {

        if (player.getCurrentParty() == null || player.getCurrentParty() == saradominLobby || player.getCurrentParty() == zamorakLobby) {
            switch (objectID) {
                case OBJECT_SARADOMIN_JOIN_PORTAL :
                    joinLobbyTeam(player, saradominParty);
                    return true;
                case OBJECT_SARADOMIN_WAITING_LEAVE_PORTAL :
                    if (player.inSaradominLobby()) {
                        LobbyManager.leaveLobbyRoom(player, saradominLobby);
                    }
                    return true;
                case OBJECT_ZAMORAK_JOIN_PORTAL :
                    joinLobbyTeam(player, zamorakParty);
                    return true;
                case OBJECT_ZAMORAK_WAITING_LEAVE_PORTAL :
                    if (player.inZamorakLobby()) {
                        LobbyManager.leaveLobbyRoom(player, zamorakLobby);
                    }
                    return true;
                case OBJECT_GUTHIX_JOIN_PORTAL :
                    joinLobbyTeam(player, null);
                    return true;
            }
            return false;
        }

        Party playerParty = player.getCurrentParty();

        switch (objectID) {

            case OBJECT_ZAMORAK_CATAPULT :
                CatapultManager.operateCatapult(zamorakParty, player);
                return true;

            case OBJECT_SARADOMIN_CATAPULT :
                CatapultManager.operateCatapult(saradominParty, player);
                return true;

            case OBJECT_ZAMORAK_BROKEN_CATAPULT :
            case OBJECT_SARADOMIN_BROKEN_CATAPULT :
                CatapultManager.repairCatapult(player, objectID);
                return true;

            case OBJECT_ZAMORAK_ALTAR :
            case OBJECT_SARADOMIN_ALTAR :
                if (inSaraSafeRoom(player) || inZammySafeRoom(player)) {
                    if (EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 2, TimeUnit.SECONDS, false, true)) {
                        if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager()
                                .getMaxLevel(Skill.PRAYER)) {
                            player.performAnimation(new Animation(645));
                            player.getSkillManager().setCurrentLevel(Skill.PRAYER,
                                    player.getSkillManager().getMaxLevel(Skill.PRAYER), true);
                            player.getPacketSender().sendMessage("You recharge your Prayer points.");
                            player.getPoints().increase(AttributeManager.Points.RECHARGED_PRAYER_TIMES, 1); // Increase points
                            player.getPacketSender().sendSound(Sounds.PRAY_ALTAR);
                            EntityExtKt.markTime(player, Attribute.LAST_PRAY);
                        } else {
                            EntityExtKt.markTime(player, Attribute.LAST_PRAY);
                            player.getPacketSender().sendMessage("You already have full prayer points.");
                            player.getPacketSender().sendSound(Sounds.PRAYER_UNAVAILABLE_SOUND);
                        }
                    }
                }
                return true;

            case OBJECT_WALL_ROPE :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                RopeManager.climbWall(player, objectX, objectY);
                return true;

            case OBJECT_ZAMORAK_BASE_LEAVE_PORTAL :
                if (playerParty == zamorakParty) {
                    if (inZammySafeRoom(player)) {
                        removeFromGameToLobby(zamorakParty, player);
                    }
                }
                return true;
            case OBJECT_SARADOMIN_BASE_LEAVE_PORTAL :
                if (playerParty == saradominParty) {
                    if (inSaraSafeRoom(player)) {
                        removeFromGameToLobby(saradominParty, player);
                    }
                }
                return true;

            case OBJECT_SARADOMIN_FLAG_STAND :
            case OBJECT_SARADOMIN_STAND :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (objectX == 2429 && objectY == 3074) {
                    captureFlag(player, objectID, saradominParty);
                }
                return true;

            case OBJECT_ZAMORAK_FLAG_STAND :
            case OBJECT_ZAMORAK_STAND :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (objectX == 2370 && objectY == 3133) {
                    captureFlag(player, objectID, zamorakParty);
                }
                return true;

            case BANDAGES_TABLE :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (clickType == 1) {
                    takeDeskItem(player, ITEM_BANDAGE, 1);
                } else if (clickType == 2) {
                    takeDeskItem(player, ITEM_BANDAGE, 5);
                }
                return true;
            case SARADOMIN_ENERGY_BARRIER :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (playerParty == saradominParty) {
                    if (!isHoldingFlag(player)) {
                        passBarrier(player, objectX, objectY);
                    } else {
                        player.sendMessage("You can't pass while holding the flag.", 2);
                    }
                } else {
                    player.sendMessage("You can't enter here.");
                }
                return true;
            case ZAMORAK_ENERGY_BARRIER :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (playerParty == zamorakParty) {
                    if (!isHoldingFlag(player)) {
                        passBarrier(player, objectX, objectY);
                    } else {
                        player.sendMessage("You can't pass while holding the flag.", 2);
                    }
                } else {
                    player.sendMessage("You can't enter here.");
                }
                return true;
            case LADDER_63 :// Saradomin safe room ladder.
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (playerParty == saradominParty) {
                    playerLeftBase(player);
                }
                return false;

            case LADDER_64 :// Zamorak safe room ladder
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (playerParty == zamorakParty) {
                    playerLeftBase(player);
                }
                return false;
            case TRAPDOOR_16 :// Saradomin safe trapdoor.
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (playerParty == saradominParty) {
                    if (!isHoldingFlag(player)) {
                        player.moveTo(new Position(player.getPosition().getX(), player.getPosition().getY(), 1));
                        playerEnteredBase(player);
                    } else {
                        player.sendMessage("You can't climb-down while holding the flag.", 2);
                    }
                }
                return true;
            case TRAPDOOR_17 :// Zamorak safe trapdoor.
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (playerParty == zamorakParty) {
                    if (!isHoldingFlag(player)) {
                        player.moveTo(new Position(player.getPosition().getX(), player.getPosition().getY(), 1));
                        playerEnteredBase(player);
                    } else {
                        player.sendMessage("You can't climb-down while holding the flag.", 2);
                    }
                }
                return true;

            case STAIRCASE_13 :
            case STAIRCASE_14 :
            case STAIRCASE_15 :
            case STAIRCASE_16 :
            case STAIRCASE_17 :
            case STAIRCASE_18 :

                if (!EntityExtKt.passedTime(player, Attribute.GENERIC_ACTION, 3, TimeUnit.SECONDS, true, true))
                    return true;

                PlayerExtKt.resetInteractions(player,true, true);
                PlayerExtKt.block(player, false, true);

                TaskManager.submit((1), () -> {
                    PlayerExtKt.unblock(player, false, true);
                    // Saradomin stairs.
                    if (objectX == 2425 && objectY == 3074) {
                        if (player.getPosition().sameAs(new Position(2426, 3074, 3))) {
                            player.moveTo(new Position(2425, 3077, 2));
                        } else {
                            player.moveTo(new Position(2426, 3074, 3));
                        }
                    } else if (objectX == 2430 && objectY == 3081) {
                        player.moveTo(new Position(2427, 3081, 1));
                    } else if (objectX == 2428 && objectY == 3081) {
                        player.moveTo(new Position(2430, 3080, 2));
                    } else if (objectX == 2419 && objectY == 3080) {
                        player.moveTo(new Position(2419, 3077, 0));
                    } else if (objectX == 2419 && objectY == 3078) {
                        player.moveTo(new Position(2420, 3080, 1));
                    } else if (objectX == 2417 && objectY == 3074) {
                        if (player.getPosition().sameAs(new Position(2416, 3074, 0))) {
                            player.moveTo(new Position(2417, 3077, 0));
                        } else if (player.getPosition().sameAs(new Position(2417, 3077, 0))) {
                            player.moveTo(new Position(2416, 3074, 0));
                        }
                    }
                    // Zamorak stairs.
                    else if (objectX == 2382 && objectY == 3131) {
                        if (player.getPosition().sameAs(new Position(2383, 3133, 0))) {
                            player.moveTo(new Position(2382, 3130, 0));
                        } else if (player.getPosition().sameAs(new Position(2382, 3130, 0))) {
                            player.moveTo(new Position(2383, 3133, 0));
                        }
                    } else if (objectX == 2380 && objectY == 3127) {
                        if (player.getPosition().sameAs(new Position(2379, 3127, 1))) {
                            player.moveTo(new Position(2380, 3130, 0));
                        } else if (player.getPosition().sameAs(new Position(2380, 3130, 0))) {
                            player.moveTo(new Position(2379, 3127, 1));
                        }
                    } else if (objectX == 2369 && objectY == 3126) {
                        if (player.getPosition().sameAs(new Position(2369, 3127, 2))) {
                            player.moveTo(new Position(2372, 3126, 1));
                        } else if (player.getPosition().sameAs(new Position(2372, 3126, 1))) {
                            player.moveTo(new Position(2369, 3127, 2));
                        }
                    } else if (objectX == 2374 && (objectY == 3133 || objectY == 3131)) {
                        if (player.getPosition().sameAs(new Position(2373, 3133, 3))) {
                            player.moveTo(new Position(2374, 3130, 2));
                        } else if (player.getPosition().sameAs(new Position(2374, 3130, 2))) {
                            player.moveTo(new Position(2373, 3133, 3));
                        }
                    }
                });
                return true;

            case LARGE_DOOR_24 :
            case LARGE_DOOR_25 :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return false;
                }
                if (bigDoorState[SARADOMIN_TEAM] == DoorState.CLOSED) {
                    if (clickType == 1) {
                        if (playerParty == zamorakParty) {
                            player.sendMessage("It's shut, you'll have to break it down.");
                            return true;
                        } else if (playerParty == saradominParty) {
                            openDoubleDoor(saradominParty);
                            player.getPacketSender().sendSound(62);
                        }
                    } else if (clickType == 2) {
                        if (playerParty == saradominParty) {
                            player.sendMessage("You don't want to damage your own doors.");
                        } else if (playerParty == zamorakParty) {
                            attackDoubleDoor(player, saradominParty);
                        }
                    }
                }
                return true;

            case LARGE_DOOR_26 :
            case LARGE_DOOR_27 :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (clickType == 1) {
                    if (bigDoorState[SARADOMIN_TEAM] == DoorState.OPENED) {
                        closeDoubleDoor(saradominParty);
                        player.getPacketSender().sendSound(60);
                    }
                }
                return true;

            case BROKEN_DOOR :
            case BROKEN_DOOR_2 :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (bigDoorState[SARADOMIN_TEAM] == DoorState.BROKEN) {
                    if (playerParty == saradominParty) {// Repair door
                        if (player.getInventory().contains(ITEM_TOOLKIT)) {
                            repairDoor(saradominParty);
                        } else {
                            player.sendMessage("You need a toolkit to repair the door.");
                        }
                    } else {
                        player.sendMessage("You don't want to repair your opponents door.");
                    }
                }
                return true;

            case LARGE_DOOR_28 :
            case LARGE_DOOR_29 :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (bigDoorState[ZAMORAK_TEAM] == DoorState.CLOSED) {
                    if (clickType == 1) {
                        if (playerParty == saradominParty) {
                            player.sendMessage("It's shut, you'll have to break it down.");
                            return true;
                        } else if (playerParty == zamorakParty) {
                            openDoubleDoor(zamorakParty);
                            player.getPacketSender().sendSound(62);
                        }
                    } else if (clickType == 2) {
                        if (playerParty == zamorakParty) {
                            player.sendMessage("You don't want to damage your own doors.");
                        } else if (playerParty == saradominParty) {
                            attackDoubleDoor(player, zamorakParty);
                        }
                    }
                }
                return true;

            case LARGE_DOOR_30 :
            case LARGE_DOOR_31 :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (clickType == 1) {
                    if (bigDoorState[ZAMORAK_TEAM] == DoorState.OPENED) {
                        closeDoubleDoor(zamorakParty);
                        player.getPacketSender().sendSound(60);
                    }
                }
                return true;

            case BROKEN_DOOR_3 :
            case BROKEN_DOOR_4 :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (bigDoorState[ZAMORAK_TEAM] == DoorState.BROKEN) {
                    if (playerParty == zamorakParty) {// Repair door
                        if (player.getInventory().contains(ITEM_TOOLKIT)) {
                            repairDoor(zamorakParty);
                        } else {
                            player.sendMessage("You need a toolkit to repair the door.");
                        }
                    } else {
                        player.sendMessage("You don't want to repair your opponents door.");
                    }
                }
                return true;

            case DOOR_125 :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (smallDoorState[SARADOMIN_TEAM] == DoorState.CLOSED) {
                    if (playerParty == saradominParty) {
                        openSmallDoor(saradominParty);
                        player.getPacketSender().sendSound(62);
                    } else if (playerParty == zamorakParty) {
                        boolean success = Misc.random(100) <= (PICK_LOCK_CHANCE + (player.getInventory().contains(ITEM_LOCKPICK) ? LOCK_PICK_BOOST : 0));
                        if (success) {
                            openSmallDoor(saradominParty);
                            player.getPacketSender().sendSound(62);
                            player.sendMessage("You manage to pick the lock!");
                        } else {
                            player.sendMessage("You fail to pick the lock!");
                        }
                    }
                }
                return true;
            case DOOR_126 :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (smallDoorState[SARADOMIN_TEAM] == DoorState.OPENED) {
                    closeSmallDoor(saradominParty);
                    player.getPacketSender().sendSound(60);
                }
                return true;

            case DOOR_127 :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (smallDoorState[ZAMORAK_TEAM] == DoorState.CLOSED) {
                    if (playerParty == zamorakParty) {
                        openSmallDoor(zamorakParty);
                        player.getPacketSender().sendSound(62);
                    } else if (playerParty == saradominParty) {
                        boolean success = Misc.random(100) <= (PICK_LOCK_CHANCE + (player.getInventory().contains(ITEM_LOCKPICK) ? LOCK_PICK_BOOST : 0));
                        if (success) {
                            openSmallDoor(zamorakParty);
                            player.getPacketSender().sendSound(62);
                            player.sendMessage("You manage to pick the lock!");
                        } else {
                            player.sendMessage("You fail to pick the lock!");
                        }
                    }
                }

                return true;

            case DOOR_128 :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (smallDoorState[ZAMORAK_TEAM] == DoorState.OPENED) {
                    closeSmallDoor(zamorakParty);
                    player.getPacketSender().sendSound(60);
                }
                return true;

            case TABLE_46 :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (clickType == 1) {
                    takeDeskItem(player, 4045, 1);
                } else if (clickType == 2) {
                    takeDeskItem(player, 4045, 5);
                }
                return true;
            case 40432 :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (clickType == 1) {
                    takeDeskItem(player, ItemID.CASTLEWARS_BREW4, 1);
                } else if (clickType == 2) {
                    takeDeskItem(player, ItemID.CASTLEWARS_BREW4, 5);
                }
                return true;
            case TABLE_45 :// Rope table(saradomin)
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (clickType == 1) {
                    takeDeskItem(player, 954, 1);
                } else if (clickType == 2) {
                    takeDeskItem(player, 954, 5);
                }
                return true;
            case TABLE_43 :// Rocks table(saradomin)
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (clickType == 1) {
                    takeDeskItem(player, 4043, 1);
                } else if (clickType == 2) {
                    takeDeskItem(player, 4043, 5);
                }
                return true;

            case TABLE_42 :// Toolkit table(saradomin)
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                if (clickType == 1) {
                    takeDeskItem(player, 4051, 1);
                } else if (clickType == 2) {
                    takeDeskItem(player, 4051, 5);
                }
                return true;

            case TABLE_47 :// PickAxe table
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                takeDeskItem(player, 1265, 1);
                return true;

            case TABLE_44 :
                if (clickType == 1) {
                    takeDeskItem(player, 4053, 1);
                } else if (clickType == 2) {
                    takeDeskItem(player, 4053, 5);
                }
                return true;

            case ROCKS_21 :
            case ROCKS_22 :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                RockManager.mineRock(player, objectID, objectX, objectY, height, direction, type);
                return true;

            case CAVE_WALL :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                int obX = objectX;
                int obY = objectY;
                if (player.isWithinDistance(new Position(2400, 9512, 0), 3)) {
                    obX = 2400;
                    obY = 9512;
                } else if (player.isWithinDistance(new Position(2391, 9501, 0), 3)) {
                    obX = 2391;
                    obY = 9501;
                } else if (player.isWithinDistance(new Position(2401, 9494, 0), 3)) {
                    obX = 2401;
                    obY = 9494;
                } else if (player.isWithinDistance(new Position(2409, 9503, 0), 3)) {
                    obX = 2409;
                    obY = 9503;
                }
                RockState tunnelRocks = RockManager.getRockClearedState(obX, obY);
                if (tunnelRocks == RockState.CLEARED) {
                    RockManager.mineWall(player, obX, obY);
                } else {
                    player.sendMessage("The tunnel has already collapsed.");
                }
                return true;

            case OBJECT_ZAMORAK_FLAG :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                GameObject zammyFlagObject = ObjectManager.findDynamicObjectAt(OBJECT_ZAMORAK_FLAG, new Position(objectX, objectY, height)).orElse(null);
                if (zammyFlagObject != null) {
                    pickupFlag(player, OBJECT_ZAMORAK_FLAG, objectX, objectY, height, playerParty);
                }
                return true;

            case OBJECT_SARADOMIN_FLAG :
                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                    return true;
                }
                GameObject saraFlagObject = ObjectManager.findDynamicObjectAt(OBJECT_SARADOMIN_FLAG, new Position(objectX, objectY, height)).orElse(null);
                if (saraFlagObject != null) {
                    pickupFlag(player, OBJECT_SARADOMIN_FLAG, objectX, objectY, height, playerParty);
                }
                return true;

        }

        return false;
    }

    private static void takeDeskItem(Player player, int itemID, int amount) {
        int freeSpaces = player.getInventory().countFreeSlots();

        if (amount > freeSpaces) {
            amount = freeSpaces;
        }

        if (amount <= 0) {
            new DialogueBuilder(DialogueType.STATEMENT)
                    .setText("Your inventory is too full to hold any more " + ItemDefinition.forId(itemID).getName() + ".")
                    .start(player);
            return;
        }

        player.getPacketSender().sendSound(Sounds.PICKUP_ITEM);
        player.getInventory().add(new Item(itemID, amount));

    }

    public static boolean processItemOnObject(final Player player, GameObject mapObject, int itemId) {
        if (player.getCurrentParty() == null) {
            return false;
        }
        // Party playerParty = player.getCurrentParty();
        int objectID = mapObject.getId();
        switch (objectID) {
            case CAVE_WALL :

                if (itemId == ITEM_EXPLOSIVE) {
                    int obX = mapObject.getX();
                    int obY = mapObject.getY();
                    if (player.isWithinDistance(new Position(2400, 9512, 0), 3)) {
                        obX = 2400;
                        obY = 9512;
                    } else if (player.isWithinDistance(new Position(2391, 9501, 0), 3)) {
                        obX = 2391;
                        obY = 9501;
                    } else if (player.isWithinDistance(new Position(2401, 9494, 0), 3)) {
                        obX = 2401;
                        obY = 9494;
                    } else if (player.isWithinDistance(new Position(2409, 9503, 0), 3)) {
                        obX = 2409;
                        obY = 9503;
                    }
                    player.getPacketSender().sendSound(1384);
                    if (obX == mapObject.getX() && obY == mapObject.getY()) {
                        //too far away
                        return false;
                    }
                    RockState tunnelRocks = RockManager.getRockClearedState(obX, obY);

                    if (tunnelRocks == RockState.CLEARED) {
                        //World.spawn(new TileGraphic(new Position(mapObject.getX(), mapObject.getY(), mapObject.getHeight()), new Graphic(2739)));
                        player.getPacketSender().sendSound(Sounds.ROCK_MINED_SOUND);
                        player.getInventory().delete(new Item(ITEM_EXPLOSIVE, 1));
                        RockManager.collapseRock(obX, obY);
                        player.sendMessage("You've collapsed the tunnel!");
                        RockManager.setRockCollapsedState(obX, obY);
                    } else {
                        player.sendMessage("The tunnel has already collapsed.");
                    }
                }
                return true;
            case ROCKS_21 :
            case ROCKS_22 :
                if (itemId == ITEM_EXPLOSIVE) {
                    int obX = mapObject.getX();
                    int obY = mapObject.getY();
                    if (player.isWithinDistance(new Position(2400, 9512, 0), 3)) {
                        obX = 2400;
                        obY = 9512;
                    } else if (player.isWithinDistance(new Position(2391, 9501, 0), 3)) {
                        obX = 2391;
                        obY = 9501;
                    } else if (player.isWithinDistance(new Position(2401, 9494, 0), 3)) {
                        obX = 2401;
                        obY = 9494;
                    } else if (player.isWithinDistance(new Position(2409, 9503, 0), 3)) {
                        obX = 2409;
                        obY = 9503;
                    }
                    int currentObject = RockManager.explodeRock(player, objectID, obX, obY, 0);
                    if (currentObject != -1) {
                        player.getInventory().delete(new Item(ITEM_EXPLOSIVE, 1));
                        //World.spawn(new TileGraphic(new Position(mapObject.getX(), mapObject.getY(), mapObject.getHeight()), new Graphic(2739)));
                        player.getPacketSender().sendSound(Sounds.FIRE_EXPLODING_SOUND);
                        if (currentObject == 4437) {
                            player.sendMessage("You manage to clear the some of the rocks.");
                        } else if (currentObject == 4438) {
                            player.sendMessage("You manage to clear the rest of the rocks.");
                        }
                    }
                    player.getPacketSender().sendSound(2348);
                }
                return true;
        }

        return false;
    }

    public static Coordinate getCastleWarsTeleport() {
        return new Coordinate(2440 + Misc.random(4), 3083 + Misc.random(14), 0);
    }

    public static void moveToCastleWars(Player client) {
        client.moveTo(findAvailableSpot(new Boundary(2439, 2444, 3082, 3097), 0));
    }

    public static void passBarrier(Player player, int objectX, int objectY) {

        int playerX = player.getPosition().getX();
        int playerY = player.getPosition().getY();

        player.setShouldNoClip(true);
        player.BLOCK_ALL_BUT_TALKING = true;
        if (objectX == 2422 && objectY == 3076) {
            PathFinder.INSTANCE.find(player, new Position(player.getX() <= 2422 ? 2423 : 2422, player.getY()), false);

            if (playerX <= 2422) {
                playerEnteredBase(player);
            } else {
                playerLeftBase(player);
            }
        } else if (objectX == 2426 && objectY == 3081) {
            PathFinder.INSTANCE.find(player, new Position(player.getX(), player.getY() >= 3081 ? 3080 : 3081), false);
            if (playerY >= 3081) {
                playerEnteredBase(player);
            } else {
                playerLeftBase(player);
            }
        } else if (objectX == 2377 && objectY == 3131) {
            PathFinder.INSTANCE.find(player, new Position(player.getX() >= 2377 ? 2376 : 2377, player.getY()), false);
            if (playerX >= 2377) {
                playerEnteredBase(player);
            } else {
                playerLeftBase(player);
            }
        } else if (objectX == 2373 && objectY == 3126) {
            PathFinder.INSTANCE.find(player, new Position(player.getX(), player.getY() < 3127 ? 3127 : 3126), false);
            if (playerY < 3127) {
                playerEnteredBase(player);
            } else {
                playerLeftBase(player);
            }
        }

        TaskManager.submit(2, () -> {
            player.setShouldNoClip(false);
            player.BLOCK_ALL_BUT_TALKING = false;
        });
    }

    protected static void playerEnteredBase(Player player) {
        player.castleWarsIddleTimer = System.currentTimeMillis();
    }

    protected static void playerLeftBase(Player player) {
        player.castleWarsIddleTimer = -1;
    }

    /**
     * Check player is inside safe room.
     *
     * @param player
     *            : Player to check if is inside safe room
     * @return: true in case player is inside safe room.
     */
    protected static boolean inSaraSafeRoom(Player player) {
        return inSaraSafeRoom(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
    }

    protected static boolean inSaraSafeRoom(int absX, int absY, int height) {
        return (absX >= 2423 && absX <= 2431 && absY >= 3072 && absY <= 3080) && height == 1;
    }

    /**
     * Check player is inside safe room.
     *
     * @param player
     *            : Player to check if is inside safe room
     * @return: true in case player is inside safe room.
     */
    protected static boolean inZammySafeRoom(Player player) {
        return inZammySafeRoom(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
    }

    protected static boolean inZammySafeRoom(int absX, int absY, int height) {
        return (absX >= 2368 && absX <= 2376 && absY >= 3127 && absY <= 3135) && height == 1;
    }

    public static boolean useBandage(Player playerUsing, Player playerReceiving, int slot) {
        /*
        if (System.currentTimeMillis() - playerUsing.bandageDelay < BANDAGE_DELAY)
            return false;

         */

        if (playerUsing.getCurrentParty() == null) {
            return false;
        }

        if (!playerUsing.getInventory().containsAnyAtSlot(slot, ITEM_BANDAGE)) {
            return false;
        }

        if (playerUsing != playerReceiving) {
            /*if (!playerUsing.isCloseEnough(playerReceiving, 5)) {
                return true;
            }*/

            if (isHoldingFlag(playerReceiving)) {
                playerUsing.sendMessage("This player cannot be healed.");
                return true;
            }
        }

        if (playerUsing.getCurrentParty() != playerReceiving.getCurrentParty()) {
            playerUsing.sendMessage("You don't want to be healing your enemies!");
            return true;
        }

        //float healPercent = BraceletManager.isUsingBracelet(playerReceiving) ? 0.20F : 0.10F;
        float healPercent = 0.10f;
        float restorePercent = 0.30F;

        if (playerReceiving != null) {
            if (playerUsing.getCurrentParty() != playerReceiving.getCurrentParty()) {
                playerUsing.sendMessage("This player is not in your team.");
                return false;
            }
            int currentHp = playerUsing.getSkillManager().getCurrentLevel(Skill.HITPOINTS);
            int maxHp = playerUsing.getSkillManager().getMaxLevel(Skill.HITPOINTS);

            int healAmount = (int) ((float) playerReceiving.getSkills().getLevel(Skill.HITPOINTS) * healPercent);
            healAmount = (int) ((float) playerUsing.getSkills().getLevel(Skill.HITPOINTS) * (playerUsing.getEquipment().containsAny(ItemID.CASTLE_WARS_BRACELET_1_, ItemID.CASTLE_WARS_BRACELET_2_, ItemID.CASTLE_WARS_BRACELET_3_) ? 0.50f : 0.10f));
            int runRestore = (int) ((float) playerUsing.getSkills().getLevel(Skill.AGILITY) * 0.30F);
            playerUsing.setRunEnergy(playerUsing.getRunEnergy() + runRestore * 100);

            if (healAmount + currentHp > maxHp)
                healAmount = maxHp - currentHp;

            if (healAmount < 0)
                healAmount = 0;

            playerUsing.setHitpoints(playerUsing.getHitpoints() + healAmount);
            int agilityPercent = (int) ((float) playerReceiving.getSkills().getLevel(Skill.AGILITY) * restorePercent);
            playerReceiving.setRunEnergy(playerReceiving.getRunEnergy()+agilityPercent * 100);
        }
        if (playerUsing != playerReceiving) {
            //playerUsing.getTask().finishTask(Tasks.MEDIC);
        }
        //playerUsing.bandageDelay = System.currentTimeMillis();
        playerUsing.getInventory().delete(new Item(ITEM_BANDAGE, 1), slot);
        return true;
    }

    public static void logoutPlayer(Player player) {
        if (isHoldingFlag(player)) {
            dropFlag(player);
        }
        Party playerParty = player.getCurrentParty();
        removeGameItems(player);
        if (playerParty == saradominParty) {
            saradominParty.putInactivePlayer(player);
            saradominParty.removePlayer(player);
        } else if (playerParty == zamorakParty) {
            zamorakParty.putInactivePlayer(player);
            zamorakParty.removePlayer(player);
        }

    }

    public static void removeFromParty(Player player) {
        removeFromParty(player, player.getCurrentParty());
    }

    public static void removeFromParty(Player player, Party party) {
        if (party != null)
            party.removePlayer(player);
    }

    private static Party inactiveOnParty(Player player) {
        if (saradominParty.isInactive(player)) {
            return saradominParty;
        } else if (zamorakParty.isInactive(player)) {
            return zamorakParty;
        }
        return null;
    }

    public static void loginPlayer(Player player) {
        if (isHoldingFlag(player)) {
            removeHoldingFlag(player);
        }
        Party playerParty = inactiveOnParty(player);
        long inactiveTime = -1;

        if (playerParty != null) {
            inactiveTime = playerParty.getPlayerInactivity(player);
        }

        if (playerParty != null) {// IF player was registered as inactive
            boolean removePlayer = true;
            if (inactiveTime >= 0) {
                // Add player back to game if wasn't inactive for too long to account for lag
                if ((System.currentTimeMillis() - inactiveTime) < TimeUnit.SECONDS.toMillis(1)) {
                    playerParty.addPlayer(player);
                    player.getPacketSender().sendWalkableInterface(11146);
                    player.getPacketSender().sendInteractionOption("Attack", 2, true);
                    if (ArrowPointerManager.hasActivePointers(TeamManager.getOppositeTeam(playerParty))) {
                        ArrowPointerManager.showPointer(player);
                    }
                    removePlayer = false;
                }
            }
            // Remove player from castle to its party lobby.
            if (removePlayer) {
                removeFromGameToLobby(playerParty, player);
            }
            // Remove player from inactive list
            playerParty.removeInactivePlayer(player);
        } else {
            // Record of this player being inactive is not found,
            // so remove game items and move to castle wars lobby.
            removeGameItems(player);
            moveToCastleWars(player);
        }

    }

    public static void removeFromGameToLobby(Party party, Player player) {
        party.removePlayer(player);
        if (party != null) {
            if (ArrowPointerManager.hasActivePointers(TeamManager.getOppositeTeam(party))) {
                ArrowPointerManager.hidePointer(player);
            }
        }
        removeGameItems(player);
        player.getPacketSender().sendInteractionOption("null", 2, true);
        joinLobbyRoom(player, party, party.getTeamID());
    }

    public static boolean isOpositeTeam(Party teamOne, Party teamTwo) {
        return (teamOne == saradominParty && teamTwo == zamorakParty) || (teamTwo == saradominParty && teamOne == zamorakParty);
    }

    protected static void updateInterface(Player player) {
        Party playerParty = player.getCurrentParty();
        if (playerParty != zamorakParty && playerParty != saradominParty) {
            return;
        }
        int playerTeam = playerParty.getTeamID();
        int opositeTeam = playerTeam == ZAMORAK_TEAM ? SARADOMIN_TEAM : ZAMORAK_TEAM;
        if (playerTeam >= 0) {
            int healthPercent = getDoorHealth(playerParty);

            int config = 0;
            int initialConfig = healthPercent;

            if (smallDoorState[playerTeam] == DoorState.OPENED)
                initialConfig += 128;// Door state
            if (rockState[playerTeam][0] == RockState.CLEARED)
                initialConfig += 256;// Rock 1 cleared
            if (rockState[playerTeam][1] == RockState.CLEARED)
                initialConfig += 512;// Rock 2 collapsed
            if (catapultState[playerTeam] == CatapultState.DESTROYED)
                initialConfig += 1024;// Catapult destroyed

            config += initialConfig;
            config += flagState[playerTeam].getStateID() * 2097152;
            config += teamPoints[playerTeam] * 16777216;
            player.getPacketSender().sendToggle(playerParty == zamorakParty ? 377 : 378, config);
            config = initialConfig;
            config += flagState[opositeTeam].getStateID() * 2097152;
            config += teamPoints[opositeTeam] * 16777216;
            player.getPacketSender().sendToggle(playerParty == zamorakParty ? 378 : 377, config);


            player.getPacketSender().sendString(11155, getTimeLeft() + " Min");
        }
    }

    public static boolean inTeamBase(Player player) {
        Party team = player.getCurrentParty();
        if (team == saradominParty) {
            return inSaraSafeRoom(player);
        } else if (team == zamorakParty) {
            return inZammySafeRoom(player);
        }
        return false;
    }

    public static void broadcast(String message) {
        Stream.of(saradominParty, zamorakParty).parallel().forEach(party -> {
            for (Player c : party.getPlayers()) {
                if (c != null) {
                    c.sendMessage(message);
                }
            }
        });
    }

    public static boolean canEquip(Player player, int slotID) {
        if (player.inCastleWarsRegion()) {
            if (slotID == EquipSlot.HAT) {
                player.sendMessage("You can't remove your team's colours.");
                return false;
            }
            if (slotID == EquipSlot.CAPE) {
                player.sendMessage("You can't remove your team's colours.");
                return false;
            }
        }
        return true;
    }

    public static int getPlayerCount() {
        return (zamorakParty.memberCount() + saradominParty.memberCount()) + (zamorakLobby.memberCount() + saradominLobby.memberCount());
    }

    public static boolean hasEffect(Player c, CastleWarsEffect effect) {
        Party currentParty = c.getCurrentParty();
        if (currentParty == saradominParty || currentParty == zamorakParty) {
            CastleWarsParty gameParty = (CastleWarsParty) currentParty;
            return gameParty.hasEffect(effect);
        }
        return false;
    }

    public static Position findAvailableSpot(Boundary boundary, int height) {
        //try 15 times to find random position
        for (int i=0; i<15; i++) {
            Position pos = boundary.getRandomPosition();
            pos.setZ(height);
            if (CollisionManager.open(pos)) {
                return pos;
            }
        }

        return null;
    }

    public static boolean isInCastleWars(Player player) {
        Boundary castleWarsTop = new Boundary(2368, 2431, 3072, 3135);
        Boundary castleWarsBottom = new Boundary(2368, 2431, 9480, 9529);
        return castleWarsTop.contains(player.getPosition()) || castleWarsBottom.contains(player.getPosition()) || saradominParty.getPlayers().contains(player) || zamorakParty.getPlayers().contains(player);
    }

    public static boolean isInCastleWarsLobby(Player player) {
        Boundary saraLobby = new Boundary(2369, 2393,9482, 9498);
        Boundary zammyLobby = new Boundary(2409, 2430,9514, 9536);
        return saraLobby.contains(player.getPosition()) || zammyLobby.contains(player.getPosition());// || saradominLobby.getPlayers().contains(player) || zamorakLobby.getPlayers().contains(player);
    }
}
