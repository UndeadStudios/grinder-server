package com.grinder.game.content.minigame;

import com.grinder.game.content.minigame.castlewars.CastleWars;
import com.grinder.game.content.minigame.impl.BattleRoyaleMinigame;
import com.grinder.game.content.minigame.impl.WeaponMinigame;
import com.grinder.game.content.minigame.pestcontrol.PestControl;
import com.grinder.game.content.miscellaneous.Broadcast;
import com.grinder.game.content.points.ParticipationPoints;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.areas.impl.PublicMinigameLobby;
import com.grinder.game.model.attribute.AttributeManager.Points;
import com.grinder.game.model.item.container.bank.Banking;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.net.packet.impl.EquipPacketListener;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Handles all minigames
 *
 * @author 2012
 */
public final class MinigameManager {

    private final static Logger LOGGER = LogManager.getLogger(MinigameManager.class.getSimpleName());

    /**
     * if {@code true} the {@link #init()} method will be returned during startup.
     */
    public final static boolean DISABLE = false;

    /**
     * The {@link BattleRoyaleMinigame battle royale game} instance.
     */
    public static BattleRoyaleMinigame BATTLE_ROYALE = new BattleRoyaleMinigame(
                    new Boundary(1722, 1794, 3390, 3453),
                    new Boundary(1795, 1800, 3403, 3419));

    /**
     * The {@link WeaponMinigame weapons game} instance.
     */
    public static WeaponMinigame WEAPON_GAME = new WeaponMinigame(new Boundary(2947, 3006, 4224, 4285));

    /**
     * The {@link Position} to move players to when they exit the minigame,
     * be it through losing said minigame, personal decision, or disconnect.
     */
    public static final Position EXIT_MINIGAME = new Position(3109, 3502, 0);

    /**
     * Represents the minimum amount of players that need to
     * be present in the lobby before the game can start.
     */
    private static final int MINIMUM_PLAYERS_IN_LOBBY = 3;

    /**
     * The amount of {@link ItemID#BLOOD_MONEY blood money}
     * awarded to a player upon winning a game.
     */
    public static final int BASE_BLOOD_MONEY_REWARD = 10_000; // Should reward base 100k bm, 1 OSRS token, and random mystery boxes
    // For every hour the game is not active the rewards should increase and announce
    // All participants should get rewarded with BM, participation points, and minigame points.

    /**
     * The extra amount of {@link ItemID#BLOOD_MONEY blood money}
     * awarded for each player that participated to the player that won the game.
     *
     * (so this amount times number of players that were present at the start of the game)
     */
    public static final int EXTRA_BLOOD_MONEY_REWARD_PER_PLAYER = 12_500;

    /**
     * The minimum amount of minutes between each {@link #publicMinigame}.
     */
    public static final int MINUTES_TILL_NEXT_PUBLIC_GAME = 3;

    /**
     * The possible {@link #publicMinigame public games}.
     */
    private static final Minigame[] PUBLIC_GAMES = {WEAPON_GAME, BATTLE_ROYALE};

    /**
     * The amount of minutes since the last {@link #publicMinigame} ended.
     */
    public static int minutesSinceLastPublicGameEnded;

    /**
     * The amount of minutes for which a {@link #publicMinigame} has been active.
     */
    public static int minuteInGame;

    /**
     * The amount of cycles till the participants can attack each other.
     */
    public static int dangerousTimer;

    /**
     * The {@link Minigame public game} currently selected.
     */
    public static Minigame publicMinigame = WEAPON_GAME;

    /**
     * Is {@code true} if the {@link #publicMinigame} has started.
     */
    public static boolean publicMinigameStarted = false;

    /**
     * The total participants presents in the {@link #publicMinigame}
     * at the start of the game.
     */
    public static int initialParticipantsCount;

    /**
     * Starts the minigame manager by instantiating a task that cycles every 100 ticks (1 minutes),
     * which handles the creation of new mini-games and can announce this to the world.
     */
    public static void init() {

        if (DISABLE)
            return;

        TaskManager.submit(new Task(100) {

            @Override
            protected void execute() {

                if (!publicMinigameStarted)
                    minutesSinceLastPublicGameEnded++;
                else {
                    minuteInGame++;
                }

                if (minutesSinceLastPublicGameEnded >= MINUTES_TILL_NEXT_PUBLIC_GAME) {
                    minutesSinceLastPublicGameEnded = 0;

                    if (PublicMinigameLobby.lobby.size() < MINIMUM_PLAYERS_IN_LOBBY) {
                        PlayerUtil.broadcastMessage("<img=792> " + publicMinigame.getName() + " has not started due to lack of players.");
                        publicMinigameStarted = false;
                    } else {

                        startPublicMinigame();

                        PlayerUtil.broadcastMessage("<img=792> " + publicMinigame.getName() + " has started!");
                        Broadcast.broadcast(null, 60, publicMinigame.getName() + " has started!", "");
                    }
                } else {
                    if (minutesSinceLastPublicGameEnded == MINUTES_TILL_NEXT_PUBLIC_GAME / 2 && PublicMinigameLobby.lobby.size() >= MINIMUM_PLAYERS_IN_LOBBY) {
                        publicMinigame = PUBLIC_GAMES[Misc.getRandomInclusive(PUBLIC_GAMES.length - 1)];
                        PlayerUtil.broadcastMessage("<img=792> " + publicMinigame.getName() + " is starting in " + minutesSinceLastPublicGameEnded + " minutes! ");
                    } else {
                        final int minutesTillStart = MINUTES_TILL_NEXT_PUBLIC_GAME - minutesSinceLastPublicGameEnded;
                        if (minutesTillStart <= 3 && minutesTillStart % 2 != 0 && PublicMinigameLobby.lobby.size() >= MINIMUM_PLAYERS_IN_LOBBY) {
                            Broadcast.broadcast(null, 60, publicMinigame.getName() + " is starting in " + minutesTillStart + " minutes! ::tourny", "");
                            PlayerUtil.broadcastMessage("<img=792> " + publicMinigame.getName() + " is starting in " + minutesTillStart + " minutes! ");
                        }
                    }
                }


            }
        });

        /*
         * updates  the "Time left" variable in the tab interface
         */
        TaskManager.submit(new Task(1) {
            @Override
            protected void execute() {
                if (publicMinigameStarted) {
                    if (dangerousTimer > 0) {
                        if (--dangerousTimer == 0){
                            for (Player player : publicMinigame.getPlayers()){
                                player.getPacketSender().sendInteractionOption("Attack", 2, true);
                            }
                        }
                    }
                }
                CastleWars.process();
                PestControl.process();
            }
        });
    }

    /**
     * Starting the public minigame
     */
    public static void startPublicMinigame() {

        initialParticipantsCount = 0;
        publicMinigameStarted = true;
        dangerousTimer = publicMinigame.getTimeUntilDangerous();

        for (Player player : PublicMinigameLobby.lobby) {

            if (player == null || !player.isActive() || !AreaManager.MINIGAME_LOBBY.contains(player))
                continue;

            enterGame(player, publicMinigame);
            initialParticipantsCount++;
        }
    }

    /**
     * Enter the argued {@link Minigame} given that the {@link Player}
     * passes the requirements.
     *
     * @param player   the {@link Player} wanting to enter the game.
     * @param minigame the {@link Minigame} the player wants to enter.
     */
    public static void enterGame(Player player, Minigame minigame) {

        if (!minigame.hasRequirements(player)) {
            player.sendDevelopersMessage("[debug]: you do not match the requirements for the game.");
            return;
        }
        if (player.getGameMode().isSpawn()) {
            return;
        }

        if (!minigame.getPlayers().contains(player))
            minigame.getPlayers().add(player);
        else {
            LOGGER.error("Attempting to add duplicate player {" + player + "} in game {" + minigame + "}");
            return;
        }

        prepare(player, minigame);

        minigame.start(player);
    }

    /**
     * Winning the minigame
     *
     * @param winner   the player
     * @param minigame the minigame
     */
    public static void wonMinigame(Player winner, Minigame minigame) {
        ItemOnGroundManager.removeAllInArea(minigame.boundaries());
        MinigameManager.rewardOtherPlayers(minigame, winner);

        publicMinigameStarted = false;
        minutesSinceLastPublicGameEnded = 0;

        final int participationPoints = 15 + Misc.random(15);
        final int minigamePoints = 150 + Misc.random(150);
        if (!winner.getGameMode().isAnyIronman()) {
            //PlayerUtil.broadcastMessage("<img=792>@red@ " + winner.getUsername() + " has won the " + minigame.getName() + "! and has won "
           //         + (NumberFormat.getIntegerInstance().format(getBloodMoneyReward())) + " Blood money, " + participationPoints + " Participation Points, and " + minigamePoints + " Minigame Points!");
            PlayerUtil.broadcastMessage("<img=792>@red@ " + PlayerUtil.getImages(winner) + "" + winner.getUsername() +" has won the " + minigame.getName() + "! and has won " + participationPoints + " Participation Points, and " + minigamePoints + " Minigame Points!");
           // winner.getPacketSender().sendMessage("<img=766> You have received @dre@" + NumberFormat.getIntegerInstance().format(getBloodMoneyReward()) + " Blood money</col> for your activity and it was " + (winner.getGameMode().isUltimate() ? "dropped under you" : "sent to your bank") + ".");
            //BankUtil.addToBank(winner, new Item(BLOOD_MONEY, getBloodMoneyReward()));
        } else {
            winner.sendMessage("Iron Man is not currently eligible for a minigame reward. However, you still get the minigame points.");
        }
        winner.getPacketSender().sendJinglebitMusic(94, 0);

        // Increase points & send message
        winner.getPoints().increase(Points.MINIGAMES_WON, 1); // Increase points
        winner.getPoints().increase(Points.MINIGAMES_WON_STREAK, 1); // Increase points
        if (winner.getPoints().get(Points.MINIGAMES_WON_STREAK) > winner.getPoints().get(Points.MINIGAMES_HIGHEST_WON_STREAK)) {
            winner.getPoints().set(Points.MINIGAMES_HIGHEST_WON_STREAK, winner.getPoints().get(Points.MINIGAMES_WON_STREAK));
        }

        winner.sendMessage("<img=792>@red@ You have won the minigame " + winner.getPoints().get(Points.MINIGAMES_WON) +" times. Highest streak: " + winner.getPoints().get(Points.MINIGAMES_HIGHEST_WON_STREAK) + ".");



        resetMinigameState(winner);


        ParticipationPoints.addPoints(winner, participationPoints, "@dre@from Minigames</col>.");
        winner.getPoints().increase(Points.MINIGAME_POINTS, minigamePoints);


        publicMinigame = PUBLIC_GAMES[Misc.getRandomInclusive(PUBLIC_GAMES.length - 1)];
        minuteInGame = 0;
        minigame.getPlayers().clear();
    }

    /**
     * Rewards other players excluding the winner
     */
    public static void rewardOtherPlayers(Minigame minigame, Player winner) {

        for (Player other : minigame.getPlayers()) {

            if (other == null || other == winner)
                continue;

            resetMinigameState(other);

            rewardForParticipation(minigame, other);
        }
    }

    public static void rewardForParticipation(Minigame minigame, Player player) {
        ParticipationPoints.addPoints(player, 10, "@dre@from Minigames</col>.");

        final int minigamePoints = 25 + Misc.random(10);
        player.getPoints().increase(Points.MINIGAME_POINTS, minigamePoints);
/*        int bmReward = 1500 + Misc.random(2500);
        if (Misc.random(5) == 1) {
            bmReward *= 2;
        }*/
        player.getPacketSender().sendJinglebitMusic(108, 0);
        //player.sendMessage("<img=766> You have received @dre@" + NumberFormat.getIntegerInstance().format(bmReward) + " Blood money</col> for your activity and it was " + (player.getGameMode().isUltimate() ? "dropped under you" : "sent to your bank") + ".");
        final String reasoning = minigame.getPlayers().size() <= 3 ? "performance" : "participation";
        player.sendMessage("<img=766> You have been awarded @dre@"+minigamePoints+" Minigame Points</col> for your "+reasoning+"!");

        // Increase points & reset streak for not winning
        player.getPoints().increase(Points.MINIGAMES_LOST, 1); // Increase points
        player.getPoints().set(Points.MINIGAMES_WON_STREAK, 0); // Increase points

        //BankUtil.addToBank(player, new Item(BLOOD_MONEY, bmReward));
    }

    /**
     * Leaving minigame
     *
     * @param player the player
     */
    public static void leaveMinigame(Player player, Minigame minigame) {

        minigame.getPlayers().remove(player);

        displayWeapon(player);

        resetMinigameState(player);

        player.getPoints().set(Points.MINIGAME_DEATH_STREAK, 0);
        //player.sendMessage("You have been moved outside of the minigame.");
        player.moveTo(EXIT_MINIGAME);
    }

    static void resetMinigameState(Player player) {
        player.BLOCK_ALL_BUT_TALKING = true;
        player.getInventory().resetItems().refreshItems();
        player.getEquipment().resetItems().refreshItems();
        player.setMinigame(null);
        player.getPacketSender().sendWalkableInterface(-1);
        player.getPacketSender().sendInteractionOption("null", 2, true);
        TaskManager.submit(new Task(1) {
            @Override
            public void execute() {
                player.moveTo(EXIT_MINIGAME);
                player.BLOCK_ALL_BUT_TALKING = false;
                player.sendMessage("You have been moved outside of the minigame.");
                stop();
            }
        });
        player.resetAttributes();
    }

    /**
     * Preparing for minigame
     *
     * @param player   the player
     * @param minigame the minigame
     */
    public static void prepare(Player player, Minigame minigame) {

        if (minigame.removeItems()) {
            Banking.depositItems(player, player.getInventory(), true);
            Banking.depositItems(player, player.getEquipment(), true);
        }

        player.setMinigame(minigame);
        player.resetAttributes();
        player.getMinigame().getPlayers().add(player);

        player.getPoints().set(Points.MINIGAME_DEATH_STREAK, 0);
        player.getPoints().set(Points.WEAPON_MINIGAME, 0);
    }

    /**
     * Displays the weapon
     *
     * @param player the player
     */
    public static void displayWeapon(Player player) {
        EquipPacketListener.resetWeapon(player);
        player.getCombat().reset(false);
        EquipmentBonuses.update(player);
        player.getEquipment().refreshItems();
        player.setUpdateInventory(true);
        player.updateAppearance();
    }

    /**
     * Sending the interface
     *
     * @param player the player
     */
    public static void sendInterface(Player player) {

        player.getPacketSender().sendString(29379, "@or1@" + publicMinigame.getName());

        StringBuilder stringBuilder = new StringBuilder("@gre@Play Time: " + minuteInGame + " min");

        if (publicMinigameStarted) {

            if (dangerousTimer > 0)
                stringBuilder = new StringBuilder("@gre@Time left: " + dangerousTimer + " ticks\\n");

            if (MinigameManager.publicMinigame instanceof BattleRoyaleMinigame)
                stringBuilder.append("\\nStatus: ").append(dangerousTimer > 0 ? "@gre@SAFE" : "@red@DANGEROUS").append("\\n");
        }

        stringBuilder.append("\\n@gre@Player(s): ").append(MinigameManager.publicMinigame.getPlayers().size()).append("\\n");

        int top = 1;

        for (Player players : getTopPlayers(publicMinigame, getMinigamePoints())) {

            if (players == null)
                continue;

            stringBuilder.append("\\n").append(top).append(". ").append(players.getUsername());

            if (MinigameManager.publicMinigame.getName().equalsIgnoreCase("Weapon Game")) {
                stringBuilder.append(" (").append(players.getPoints().get(Points.WEAPON_MINIGAME)).append("/15)"); // WINNING_SCORE
            }
            top++;

            if (top == 7)
                break;
        }
        player.getPacketSender().sendString(29380, stringBuilder.toString());
    }

    @Nullable
    private static Points getMinigamePoints() {
        return (publicMinigame instanceof WeaponMinigame) ? Points.WEAPON_MINIGAME : null;
    }

    /**
     * Gets the players in the argued game
     * (which may be sorted based on {@link Points points}).
     */
    public static ArrayList<Player> getTopPlayers(Minigame minigame, Points points) {
        final ArrayList<Player> existing = minigame.getPlayers();
        final Set<Player> hs = new HashSet<>(existing);
        existing.clear();
        existing.addAll(hs);
        if (points != null) {
            existing.sort((o1, o2) -> Integer.compare(
                    o2.getPoints().get(points),
                    o1.getPoints().get(points)));
        }
        return existing;
    }

    /**
     * The amount of {@link ItemID#BLOOD_MONEY blood money}
     * to be awarded to the winner of the {@link #publicMinigame}.
     *
     * @return an integer representing the rewarded amount.
     */
    public static int getBloodMoneyReward() {
        int reward = BASE_BLOOD_MONEY_REWARD;
        reward += (PublicMinigameLobby.lobby.size() * EXTRA_BLOOD_MONEY_REWARD_PER_PLAYER);
        return reward;
    }

    /**
     * Will check the players position to see if we're inside of either Game
     * @param player
     * @return - True if a user is inside of ANY minigame {@link #PUBLIC_GAMES}
     */
    public static boolean isInPublicMinigameArea(Player player) {
        for (Minigame game: PUBLIC_GAMES) {
            if (game.contains(player.getPosition())) {
                return true;
            }
        }
        return false;
    }
}