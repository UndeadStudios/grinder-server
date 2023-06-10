package com.grinder.game.content.minigame.aquaisneige;

import com.google.gson.annotations.Expose;
import com.grinder.game.World;
import com.grinder.game.content.minigame.aquaisneige.monsters.*;
import com.grinder.game.content.miscellaneous.Emotes;
import com.grinder.game.content.miscellaneous.TravelSystem;
import com.grinder.game.content.task_new.DailyTask;
import com.grinder.game.content.task_new.PlayerTaskManager;
import com.grinder.game.content.task_new.WeeklyTask;
import com.grinder.game.entity.agent.movement.pathfinding.PathFinder;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.boss.impl.Krampus;
import com.grinder.game.entity.agent.player.LogoutPolicy;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.*;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.areas.instanced.AquaisNeigeArea;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.task.TaskManager;
import com.grinder.util.*;

import java.util.concurrent.TimeUnit;


public final class AquaisNeige {

    private static final Position[] SPAWNS = new Position[]{
            new Position(1330, 3112),
            new Position(1331, 3103),
            new Position(1328, 3095),
            new Position(1314, 3096),
            new Position(1300, 3099),
            new Position(1295, 3110),
            new Position(1309, 3113),
    };

    private static final Position ENTRANCE = new Position(1314, 3091);
    private static final Position EXIT = new Position(1310, 3041, 0);
    private static final int PRICE = 100_000_000;
    private static final int WAITING_TIME = 180;

    private boolean logoutRequested;
    private boolean paused;
    private AquaisNeigeArea instance;
    @Expose
    private Wave current;
    private int monstersAlive;
    private Position inadequacySpawn;
    @Expose
    private long lastSession;

    static {
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.CAVE_ENTRANCE_32}, action -> {
            clickEntrance(action.getPlayer());
            return true;
        });
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.CAVE_18}, action -> {
            var player = action.getPlayer();
            switch (action.getType()) {
                case FIRST_OPTION:
                    clickExit(player);
                    break;
                case SECOND_OPTION:
                    //escape
                    exit(player, true);
                    break;

            }
            return true;
        });
    }

    public void clear() {
        if (instance != null) {
            instance.destroy();
            instance = null;
        }
    }

    public static void reset(Player player) {
        player.setArea(null);
        player.getAquaisNeige().setCurrentWave(null);
        player.getAquaisNeige().setInadequacySpawn(null);
        player.getAquaisNeige().setPaused(false);
        player.getAquaisNeige().setLogoutRequested(false);
    }

    public void updateLastSession() {
        this.lastSession = System.currentTimeMillis();
    }

    public void setLastSession(long session) {
        this.lastSession = session;
    }

    public static void clickEntrance(Player player) {

        if (player.busy())
            return;

        if (player.getUsername().equals("Legends") || player.getMacAddress().equals("00-1A-73-DA-E3-BC") || player.getHostAddress().equals("84.15.182.83")
                || player.getHdSerialNumber().equals("-1533891927")) {
            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(NpcID.SKRACH_UGLOGWEE_4853).setExpression(DialogueExpression.ANGRY)
                    .setText("Hey, Uglogwee, this Minigame is boring for you to play.",
                            "You shall never pass!")
                    .start(player);
            return;
        }

        var timeLeft = WAITING_TIME - TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - player.getAquaisNeige().getLastSession());
        if (timeLeft > 0) {
            var minutes = 0;
            var seconds = 0;
            while (timeLeft >= 60) {
                timeLeft -= 60;
                minutes++;
            }
            seconds = (int) timeLeft;
            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(NpcID.SKRACH_UGLOGWEE_4853)
                    .setText("Hey, Uglogwee, you were in the cave only a moment ago.",
                            "You wait " + (minutes == 1 ? (minutes + " minute ") : minutes > 1 ? minutes + " minutes " : "") + (seconds == 1 ? (seconds + " second ") : seconds > 1 ? seconds + " seconds " : "") + "before going in again.")
                    .start(player);
            return;
        }
        startWave(player, 1);

        /*new DialogueBuilder(DialogueType.OPTION)
                .firstOption("TzTok-Jad.", pIgnore -> new DialogueBuilder(DialogueType.STATEMENT)
                        .setText("In order to skip straight to TzTok-Jad you have to pay " + Misc.formatWithAbbreviation2(getWaveCost(player, 63)) + " coins", "are you sure you want to do that?")
                        .add(DialogueType.OPTION).setOptionTitle("Pay " + Misc.format(getWaveCost(player, 63)) + "?")
                        .firstOption("Yes.", pIgnore2 -> skipWaveOption(player, 63))
                        .addCancel("No.")
                        .start(pIgnore))
                .secondOption("Specific wave.", pIgnore3 -> player.requestInput(Integer.class, "Enter wave", wave -> new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.TZHAARMEJJAL)
                        .setText("Skipping to wave " + (wave > 63 ? 63 : wave) + " is going to cost you " + Misc.formatWithAbbreviation2(getWaveCost(player, wave)), "are you sure you want to do that?")
                        .add(DialogueType.OPTION).setOptionTitle("Pay " + Misc.format(getWaveCost(player, wave)) + "?")
                        .firstOption("Yes.", pIgnore4 -> skipWaveOption(player, (wave > 63 ? 63 : wave)))
                        .addCancel("No.")
                        .start(player)))
                .thirdOption("All waves.", pIgnore5 -> startWave(player, 1))
                .addCancel("Cancel.")
                .start(player);*/
    }

    public static void clickExit(Player player) {
        new DialogueBuilder(DialogueType.OPTION)
                .setOptionTitle("Really leave?")
                .firstOption("Yes - really leave.", $ -> AquaisNeige.exit(player, true))
                .addCancel("No, I'll Stay.").start(player);
    }

    /*private static void skipWaveOption(Player player, int wave) {
        if (player.getInventory().contains(new Item(ItemID.COINS, getWaveCost(player, wave)))) {
            player.getInventory().deleteItemSet(new Item[]{new Item(ItemID.COINS, getWaveCost(player, wave))});
            startWave(player, wave);
        } else {
            new DialogueBuilder(DialogueType.NPC_STATEMENT).
                    setNpcChatHead(NpcID.TZHAARMEJJAL).
                    setText("You don't have enough coins to proceed.").
                    start(player);
        }
    }*/

    public static void sendWaveMessage(Player player) {
        player.sendMessage("<col=FF00000>" + "Wave " + player.getAquaisNeige().getCurrentWave().asInt() + "!");
    }

    public static void sendWaveMusic(Player player) {
        player.getPacketSender().sendJinglebitMusic(75, 0);
    }

    public static void setWave(Player player, int wave) {
        if (wave <= 0) {
            wave = 1;
        }
        player.getAquaisNeige().setCurrentWave(Wave.values()[wave - 1]);
    }

    public static void start(Player player, int wave) {
        var currentInstance = player.getAquaisNeige().getCurrentInstance();
        if (currentInstance == null)
            player.getAquaisNeige().setCurrentInstance(new AquaisNeigeArea(player.getAquaisNeige()));
        if (player.getArea() != player.getAquaisNeige().getCurrentInstance()) {
            player.setArea(player.getAquaisNeige().getCurrentInstance());
            player.getAquaisNeige().getCurrentInstance().add(player);
        }
        player.getAquaisNeige().setPaused(false);
        player.getAquaisNeige().setLogoutRequested(false);
        setWave(player, wave);
        sendWaveMessage(player);
        if (wave == 30) {
            player.sendMessage("<col=FF00000>Final Challenge!");
        }
        spawnWaveNpcs(player);
        if (wave != 30)
            DialogueManager.start(player, 2820);
    }

    private static void startWave(Player player, int wave) {
        player.performAnimation(new Animation(844, 25));
        DialogueManager.start(player, -1);
        player.getPacketSender().sendSound(65);
        TravelSystem.INSTANCE.fadeTravelAction(player, true, true, "", 2, 3, 2, () -> {
            teleport(player);

            TaskManager.submit(1, ()-> {
                try {
                    PathFinder.INSTANCE.find(player, SPAWNS[2].clone().setZ(player.getZ()), true);
                    start(player, wave);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            });
            return null;
        });
    }

    public static void resume(Player player) {
        start(player, player.getAquaisNeige().getCurrentWave().asInt());
    }

    public static void pause(Player player) {
        setWave(player, player.getAquaisNeige().getCurrentWave().getNextWave().asInt());
        player.sendMessage("<col=FF0000>The Aquais Neige has been paused. You may now log out.");
        player.getAquaisNeige().setPaused(true);
    }

    public static void teleport(Player player) {
        player.moveTo(ENTRANCE.clone().setZ(player.getIndex() * 4));
    }

    public static void exit(Player player) {
        exit(player, false);
    }

    public static void exit(Player player, boolean showDialog) {
        if (player.busy())
            return;
        final int wave = player.getAquaisNeige().getCurrentWave().asInt();
        player.getAquaisNeige().clear();
        player.resetAttributes();
        player.getCombat().reset(true);
        player.moveTo(EXIT);
        AquaisNeige.reset(player);
        if (wave > 1) {
            int rewardAmount = (int) ((wave / 30.0) * 8032);
            if (wave <= 15) { // To prevent players from playing the first few rounds to boost coins
                rewardAmount *= 0.30;
            }
            ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(23497, rewardAmount));
        }
        setWave(player, 1);
        player.getAquaisNeige().updateLastSession();
        if (showDialog) {
            if (wave == 1) {
                DialogueManager.start(player, 2823);
            } else {
                DialogueManager.start(player, 2822);
            }
        }
    }

    public static void onPlayerDeath(Player player) {
        exit(player);
        DialogueManager.start(player, 2824);
        player.getPacketSender().sendCameraNeutrality();
    }

    public static void onNpcDeath(Player player, NPC npc) {
        player.getAquaisNeige().monstersAlive--;
        if (npc.getId() == Constants.THE_INADEQUACY) {
            // Removing any minions.
            var inadequacy = (TheInadequacy) npc;
            var idanequacyMinionList = inadequacy.getMinions();

            // Removing Lists.
            for (AquaisNeigeNpc minion : idanequacyMinionList) {
                if (minion != null && !minion.isAlive()) {
                    World.remove(minion);
                    idanequacyMinionList.remove(minion);
                }
            }

            Graphic.sendGlobal(new Graphic(456, GraphicHeight.HIGH, Priority.HIGHEST), npc.getPosition().transform(-2, -2, 0));
            player.getPacketSender().sendJinglebitMusic(78, 0);
            player.performAnimation(Emotes.EmoteData.CHEER.animation);
            TaskManager.submit(5, () -> {
                exit(player);
                ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.HYDRO_CAPE, 1));
                ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(23497, 8032));
                DialogueManager.start(player, 2825);
                player.getCollectionLog().createOrUpdateEntry(player,  "Aquais Neige", new Item(ItemID.HYDRO_CAPE)); // Collection Log

                // Global announcement
                PlayerUtil.broadcastMessage("<img=792> Congratulations to " + PlayerUtil.getImages(player) + "" + player.getUsername() +" for completing the Aquais Neige minigame challenge.");

                // Aquais Neige pet
                if (Misc.random(5) == Misc.random(5)) {
                    PlayerUtil.broadcastMessage("<img=792> Congratulations to " + PlayerUtil.getImages(player) + "" + player.getUsername() + " for winning The Inadequacy pet as a bonus!");

                    if (DiscordBot.ENABLED)
                        DiscordBot.INSTANCE.sendServerLogs("[MINIGAME]: " + player.getUsername() + " has just won the Aquais Neige pet 'The Inadequacy'.");

                    player.getCollectionLog().createOrUpdateEntry(player, "Aquais Neige", new Item(15916)); // Collection Log
                    ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(15916, 1));
                }

                // Increase points
                player.getPoints().increase(AttributeManager.Points.AQUAIS_NEIGE_GAMES_COMPLETED, 1); // Increase points
                PlayerTaskManager.progressTask(player, DailyTask.AQUAIS_NEIGE);
                PlayerTaskManager.progressTask(player, WeeklyTask.AQUAIS_NEIGE);

            });
            return;
        }

        if (player.getAquaisNeige().monstersAlive < 1) {
            if (player.getAquaisNeige().isLogoutRequested()) {
                pause(player);
            } else {
                if (player.getAquaisNeige().getCurrentWave() != Wave.INADEQUACY) {
                    advanceWave(player);
                }
            }
        }

    }

    private static void spawnWaveNpcs(Player player) {
        player.getAquaisNeige().monstersAlive = 0;
        for (int npcId : player.getAquaisNeige().getCurrentWave().getIds()) {
            AquaisNeigeNpc npc = null;
            switch (npcId) {
                case Constants.THE_INADEQUACY:
                    DialogueManager.start(player, 2821);
                    TaskManager.submit(10, () -> {
                        var inadequacySpawn = player.getAquaisNeige().getInadequacySpawn();
                        var inadequacy = new TheInadequacy(inadequacySpawn == null ? getRandomSpawn(player) : inadequacySpawn);
                        inadequacy.setOwner(player);
                        inadequacy.setArea(player.getAquaisNeige().getCurrentInstance());
                        inadequacy.spawn();
                        player.getAquaisNeige().getCurrentInstance().add(inadequacy);
                    });
                    break;
                case Constants.KRAMPUS:
                case Constants.KRAMPUS_2:
                    npc = new Krampus(npcId, getRandomSpawn(player));
                    break;
                case Constants.NYKUR:
                case Constants.NYKUR_2:
                    npc = new Nykur(npcId, getRandomSpawn(player));
                    player.getAquaisNeige().setInadequacySpawn((npcId == Constants.NYKUR_2) ? npc.getSpawnPosition() : player.getAquaisNeige().getInadequacySpawn());
                    break;
                case Constants.ARMAROS:
                case Constants.ARMAROS_2:
                    npc = new Armaros(npcId, getRandomSpawn(player));
                    break;
                case Constants.ICE_GIANT:
                case Constants.ICE_GIANT_2:
                    npc = new IceGiant(npcId, getRandomSpawn(player));
                    break;
                case Constants.WATER_ELEMENTAL:
                case Constants.WATER_ELEMENTAL_2:
                    npc = new WaterElemental(npcId, getRandomSpawn(player));
                    break;
                case Constants.WATER_WIZARD:
                case Constants.WATER_WIZARD_2:
                    npc = new WaterWizard(npcId, getRandomSpawn(player));
                    break;
                case Constants.HYDRO_TROLL:
                case Constants.HYDRO_TROLL_2:
                    npc = new HydroTroll(npcId, getRandomSpawn(player));
                    break;
                case Constants.HYDRO_WARRIOR:
                case Constants.HYDRO_WARRIOR_2:
                    npc = new HydroWarrior(npcId, getRandomSpawn(player));
                    break;
                case Constants.ICE_SPIDER:
                case Constants.ICE_SPIDER_2:
                    npc = new IceSpider(npcId, getRandomSpawn(player));
                    break;
                case Constants.ICEFIENDS:
                case Constants.ICEFIENDS_2:
                    npc = new IceFiend(npcId, getRandomSpawn(player));
                    break;
                default:
                    spawnNpc(player, npcId);
                    break;
            }
            if (npc != null) {
                npc.setOwner(player);
                npc.setArea(player.getAquaisNeige().getCurrentInstance());
                npc.spawn();
                player.getAquaisNeige().getCurrentInstance().add(npc);
                npc.getMotion().traceTo(player.getPosition());
                npc.getMotion().setCanReTrace(true);
                npc.setEntityInteraction(player);
            }

            player.getAquaisNeige().monstersAlive++;
        }
    }

    private static void spawnNpc(Player player, int npcId) {
        spawnNpc(player, npcId, getRandomSpawn(player));
    }

    private static void spawnNpc(Player player, int npcId, Position pos) {
        var npc = new AquaisNeigeNpc(npcId, pos);
        npc.setOwner(player);
        npc.setArea(player.getAquaisNeige().getCurrentInstance());
        npc.spawn();
        player.getAquaisNeige().getCurrentInstance().add(npc);
        player.getAquaisNeige().monstersAlive++;
    }

    private static void advanceWave(Player player) {
        if (player.getAquaisNeige().isLogoutRequested()) {
            forceLogout(player);
        } else {
            setWave(player, player.getAquaisNeige().getCurrentWave().getNextWave().asInt());
            sendWaveMessage(player);
            sendWaveMusic(player);
            spawnWaveNpcs(player);
            if (player.getAquaisNeige().getCurrentWave() == Wave.INADEQUACY) {
                player.getPacketSender().sendJinglebitMusic(92, 0);
                player.sendMessage("<col=FF00000>Final Challenge!");
            }
        }
    }

    public static void requestLogout(Player player) {
        player.getAquaisNeige().setLogoutRequested(true);

        if (player.getAquaisNeige().getCurrentWave() == Wave.INADEQUACY) {
            player.sendMessage("<col=FF0000>If you logout right now you'll have to repeat the wave.");
            return;
        }
        player.getPacketSender().sendCameraNeutrality();
        player.sendMessage("<col=FF0000> Your logout request has been received, the minigame will be paused at the end of this wave.");
    }

    public static void forceLogout(Player player) {
        player.logout(LogoutPolicy.IMMEDIATE);
    }

    public static void onLogin(Player player) {
        TaskManager.submit(2, () -> {
            if (isActive(player) || isInside(player)) {
                resume(player);
            } else {
                //player teleport or left the instance and the cave
                setWave(player, 1);
            }
        });
    }

    public static boolean onLogout(Player player) {
        if (isActive(player)) {
            if (!canLogout(player)) {
                requestLogout(player);
                return false;
            }
        }
        return true;
    }


    private static Position getRandomSpawn(Player player) {
        return SPAWNS[Misc.getRandomExclusive(SPAWNS.length)].transform(0, 0, player.getZ());
    }

    /*private static int getWaveCost(Player player, int wave) {
        if (wave > 30)
            wave = 30;
        if (wave == 1)
            return 0;
        var ironPrice = PRICE;
        if (player.getGameMode().isAnyIronman())
            ironPrice *= 0.50;

        var finalPrice = ironPrice;
        var memberRights = PlayerUtil.getMemberRights(player);

        for (int i = PlayerRights.BRONZE_MEMBER.ordinal(); i <= PlayerRights.LEGENDARY_MEMBER.ordinal(); i++) {
            if (memberRights.ordinal() >= PlayerRights.values()[i].ordinal())
                finalPrice -= 0.05 * (ironPrice);
        }

        if (memberRights == PlayerRights.PLATINUM_MEMBER) {
            finalPrice -= 0.10 * (ironPrice);
        }

        return (int) (wave / 30.0 * finalPrice);
    }*/


    public static boolean canLogout(Player player) {
        return player.getAquaisNeige().isLogoutRequested();
    }

    public static boolean isActive(Player player) {
        var currentInstance = player.getAquaisNeige().getCurrentInstance();
        if (currentInstance == null)
            return false;
        return player.getArea() == currentInstance && isInside(player);
    }

    public static boolean isInside(Player player) {
        return AreaManager.inside(player.getPosition(), new AquaisNeigeArea(null));
    }

    public void setCurrentInstance(AquaisNeigeArea instance) {
        this.instance = instance;
    }

    public AquaisNeigeArea getCurrentInstance() {
        return instance;
    }

    public long getLastSession() {
        return lastSession;
    }

    public Wave getCurrentWave() {
        return current;
    }

    public Position getInadequacySpawn() {
        return inadequacySpawn;
    }

    public void setInadequacySpawn(Position inadequacySpawn) {
        this.inadequacySpawn = inadequacySpawn;
    }

    public void setCurrentWave(Wave wave) {
        current = wave;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isLogoutRequested() {
        return logoutRequested;
    }

    public void setLogoutRequested(boolean logoutRequested) {
        this.logoutRequested = logoutRequested;
    }

    public boolean isPaused() {
        return paused;
    }

}


