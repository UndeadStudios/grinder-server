package com.grinder.game.content.minigame.fightcave;

import com.google.gson.annotations.Expose;
import com.grinder.game.content.minigame.fightcave.monsters.*;
import com.grinder.game.content.miscellaneous.Emotes;
import com.grinder.game.content.task_new.DailyTask;
import com.grinder.game.content.task_new.PlayerTaskManager;
import com.grinder.game.content.task_new.WeeklyTask;
import com.grinder.game.entity.agent.movement.pathfinding.PathFinder;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.LogoutPolicy;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.ObjectActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.areas.instanced.FightCaveArea;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.task.TaskManager;
import com.grinder.util.*;

import java.util.concurrent.TimeUnit;

/**
 * @author L E G E N D
 */
public final class FightCave {

    private static final Position[] SPAWNS = new Position[]{
            new Position(2414, 5083),
            new Position(2396, 5097),
            new Position(2385, 5101),
            new Position(2400, 5088),
            new Position(2385, 5071),
            new Position(2401, 5069),
            new Position(2414, 5084),
    };

    private static final Position ENTRANCE = new Position(2413, 5117);
    private static final Position EXIT = new Position(2438, 5168, 0);
    private static final int PRICE = 100_000_000;
    private static final int WAITING_TIME = 180;

    private boolean logoutRequested;
    private boolean paused;
    private FightCaveArea instance;
    @Expose
    private Wave current;
    private int monstersAlive;
    private Position jadSpawn;
    @Expose
    private long lastSession;

    static {
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.FIGHT_CAVE_ENTRANCE}, action -> {
            clickEntrance(action.getPlayer());
            return true;
        });
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.FIGHT_CAVE_EXIT}, action -> {
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
        instance.destroy();
        instance = null;
    }

    public static void reset(Player player) {
        player.setArea(null);
        player.getFightCave().setCurrentWave(null);
        player.getFightCave().setJadSpawn(null);
        player.getFightCave().setPaused(false);
        player.getFightCave().setLogoutRequested(false);
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

        var timeLeft = WAITING_TIME - TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - player.getFightCave().getLastSession());
        if (timeLeft > 0) {
            var minutes = 0;
            var seconds = 0;
            while (timeLeft >= 60) {
                timeLeft -= 60;
                minutes++;
            }
            seconds = (int) timeLeft;
            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(NpcID.TZHAARMEJJAL)
                    .setText("Hey, JalYt, you were in the cave only a moment ago.",
                            "You wait " + (minutes == 1 ? (minutes + " minute ") : minutes > 1 ? minutes + " minutes " : "") + (seconds == 1 ? (seconds + " second ") : seconds > 1 ? seconds + " seconds " : "") + "before going in again.")
                    .start(player);
            return;
        }

        new DialogueBuilder(DialogueType.OPTION)
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
                .start(player);
    }

    public static void clickExit(Player player) {
        new DialogueBuilder(DialogueType.OPTION)
                .setOptionTitle("Really leave?")
                .firstOption("Yes - really leave.", $ -> FightCave.exit(player, true))
                .addCancel("No, I'll Stay.").start(player);
    }

    private static void skipWaveOption(Player player, int wave) {
        if (player.getInventory().contains(new Item(ItemID.COINS, getWaveCost(player, wave)))) {
            player.getInventory().delete(new Item[]{new Item(ItemID.COINS, getWaveCost(player, wave))});
            startWave(player, wave);
        } else {
            new DialogueBuilder(DialogueType.NPC_STATEMENT).
                    setNpcChatHead(NpcID.TZHAARMEJJAL).
                    setText("You don't have enough coins to proceed.").
                    start(player);
        }
    }

    public static void sendWaveMessage(Player player) {
        player.sendMessage("<col=FF00000>" + "Wave " + player.getFightCave().getCurrentWave().asInt() + "!");
    }

    public static void sendWaveMusic(Player player) {
        player.getPacketSender().sendJinglebitMusic(76, 0);
    }

    public static void setWave(Player player, int wave) {
        if (wave <= 0) {
            wave = 1;
        }
        player.getFightCave().setCurrentWave(Wave.values()[wave - 1]);
    }

    public static void start(Player player, int wave) {
        var currentInstance = player.getFightCave().getCurrentInstance();
        if (currentInstance == null)
            player.getFightCave().setCurrentInstance(new FightCaveArea(player.getFightCave()));
        if (player.getArea() != player.getFightCave().getCurrentInstance()) {
            player.setArea(player.getFightCave().getCurrentInstance());
            player.getFightCave().getCurrentInstance().add(player);
        }
        player.getFightCave().setPaused(false);
        player.getFightCave().setLogoutRequested(false);
        setWave(player, wave);
        sendWaveMessage(player);
        if (wave == 63) {
            player.sendMessage("<col=FF00000>Final Challenge!");
        }
        spawnWaveNpcs(player);
        if (wave != 63)
            DialogueManager.start(player, 23);
    }

    private static void startWave(Player player, int wave) {
        DialogueManager.start(player, -1);
        teleport(player);

        // Auto delay by one tick to walk the player
        TaskManager.submit(1, ()-> {
            try {
                PathFinder.INSTANCE.find(player, SPAWNS[3].clone().setZ(player.getZ()), true);
                start(player, wave);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public static void resume(Player player) {
        start(player, player.getFightCave().getCurrentWave().asInt());
    }

    public static void pause(Player player) {
        setWave(player, player.getFightCave().getCurrentWave().getNextWave().asInt());
        player.sendMessage("<col=FF0000>The Fight Cave has been paused. You may now log out.");
        player.getFightCave().setPaused(true);
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
        final int wave = player.getFightCave().getCurrentWave().asInt();
        player.getFightCave().clear();
        player.resetAttributes();
        player.getCombat().reset(true);
        player.moveTo(EXIT);
        FightCave.reset(player);
        if (wave > 1) {
            ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.TOKKUL, (int) ((wave / 63.0) * 8032)));
        }
        setWave(player, 1);
        player.getFightCave().updateLastSession();
        if (showDialog) {
            if (wave == 1) {
                DialogueManager.start(player, 2814);
            } else {
                DialogueManager.start(player, 2813);
            }
        }
    }

    public static void onPlayerDeath(Player player) {
        exit(player);
        DialogueManager.start(player, 24);
    }

    public static void onNpcDeath(Player player, NPC npc) {
        player.getFightCave().monstersAlive--;
        if (npc.getId() == Constants.JAD) {
            Graphic.sendGlobal(new Graphic(453, GraphicHeight.HIGH, Priority.HIGHEST), npc.getPosition().transform(-2, -2, 0));
            player.performAnimation(Emotes.EmoteData.CHEER.animation);
            TaskManager.submit(5, () -> {
                exit(player);
                ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.FIRE_CAPE, 1));
                ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.TOKKUL, 8032));
                DialogueManager.start(player, 25);
                player.getPoints().increase(AttributeManager.Points.FIGHT_CAVES_COMPLETED, 1); // Increase points
                player.getCollectionLog().createOrUpdateEntry(player,  "Fight Cave", new Item(ItemID.FIRE_CAPE)); // Collection Log
                PlayerTaskManager.progressTask(player, DailyTask.FIGHT_CAVES);
                PlayerTaskManager.progressTask(player, WeeklyTask.FIGHT_CAVES);
            });
            return;
        }

        if (npc.getId() == Constants.RECOIL || npc.getId() == Constants.RECOIL_2) {
            spawnNpc(player, Constants.RECOIL_MINIONS, npc.getLastPosition());
            spawnNpc(player, Constants.RECOIL_MINIONS, npc.getLastPosition());
        }

        if (player.getFightCave().monstersAlive < 1) {
            if (player.getFightCave().isLogoutRequested()) {
                pause(player);
            } else {
                if (player.getFightCave().getCurrentWave() != Wave._JAD) {
                    advanceWave(player);
                }
            }
        }

    }

    private static void spawnWaveNpcs(Player player) {
        player.getFightCave().monstersAlive = 0;
        for (int npcId : player.getFightCave().getCurrentWave().getIds()) {
            FightCaveNpc npc = null;
            switch (npcId) {
                case Constants.JAD:
                    DialogueManager.start(player, 2812);
                    TaskManager.submit(10, () -> {
                        var jadSpawn = player.getFightCave().getJadSpawn();
                        var jad = new TzTokJad(jadSpawn == null ? getRandomSpawn(player) : jadSpawn);
                        jad.setOwner(player);
                        jad.setArea(player.getFightCave().getCurrentInstance());
                        jad.spawn();
                        player.getFightCave().getCurrentInstance().add(jad);
                    });
                    break;
                case Constants.MELEE:
                    npc = new YtMejKot(npcId, getRandomSpawn(player));
                    break;
                case Constants.MAGE:
                case Constants.MAGE_ORANGE:
                    npc = new KetZek(npcId, getRandomSpawn(player));
                    player.getFightCave().setJadSpawn((npcId == Constants.MAGE_ORANGE) ? npc.getSpawnPosition() : player.getFightCave().getJadSpawn());
                    break;
                case Constants.RANGER:
                case Constants.RANGER_2:
                    npc = new TokXil(npcId, getRandomSpawn(player));
                    break;
                case Constants.RECOIL:
                case Constants.RECOIL_2:
                    npc = new TzKek(npcId, getRandomSpawn(player));
                    break;
                case Constants.PRAYER:
                case Constants.PRAYER_2:
                    npc = new TzKih(npcId, getRandomSpawn(player));
                    break;
                default:
                    spawnNpc(player, npcId);
                    break;
            }
            if (npc != null) {
                npc.setOwner(player);
                npc.setArea(player.getFightCave().getCurrentInstance());
                npc.spawn();
                player.getFightCave().getCurrentInstance().add(npc);
                npc.getMotion().traceTo(player.getPosition());
                npc.getMotion().setCanReTrace(true);
                npc.setEntityInteraction(player);
            }

            player.getFightCave().monstersAlive++;
        }
    }

    private static void spawnNpc(Player player, int npcId) {
        spawnNpc(player, npcId, getRandomSpawn(player));
    }

    private static void spawnNpc(Player player, int npcId, Position pos) {
        var npc = new FightCaveNpc(npcId, pos);
        npc.setOwner(player);
        npc.setArea(player.getFightCave().getCurrentInstance());
        npc.spawn();
        player.getFightCave().getCurrentInstance().add(npc);
        player.getFightCave().monstersAlive++;
    }

    private static void advanceWave(Player player) {
        if (player.getFightCave().isLogoutRequested()) {
            forceLogout(player);
        } else {
            setWave(player, player.getFightCave().getCurrentWave().getNextWave().asInt());
            sendWaveMessage(player);
            sendWaveMusic(player);
            spawnWaveNpcs(player);
            if (player.getFightCave().getCurrentWave() == Wave._JAD) {
                player.sendMessage("<col=FF00000>Final Challenge!");
            }

        }
    }

    public static void requestLogout(Player player) {
        player.getFightCave().setLogoutRequested(true);
        if (player.getFightCave().getCurrentWave() == Wave._JAD) {
            player.sendMessage("<col=FF0000>If you logout right now you'll have to repeat the wave.");
            return;
        }

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

    private static int getWaveCost(Player player, int wave) {
        if (wave > 63)
            wave = 63;
        if (wave == 1)
            return 0;
        var ironPrice = PRICE;
        if (player.getGameMode().isAnyIronman())
            ironPrice *= 0.50;

        var finalPrice = ironPrice;
        var memberRights = PlayerUtil.getMemberRights(player);

        for (int i = PlayerRights.BRONZE_MEMBER.ordinal(); i <= PlayerRights.DIAMOND_MEMBER.ordinal(); i++) {
            if (memberRights.ordinal() >= PlayerRights.values()[i].ordinal())
                finalPrice -= 0.05 * (ironPrice);
        }

/*        if (memberRights == PlayerRights.PLATINUM_MEMBER) {
            finalPrice -= 0.10 * (ironPrice);
        }*/

        return (int) (wave / 63.0 * finalPrice);
    }


    public static boolean canLogout(Player player) {
        return player.getFightCave().isLogoutRequested();
    }

    public static boolean isActive(Player player) {
        var currentInstance = player.getFightCave().getCurrentInstance();
        if (currentInstance == null)
            return false;
        return player.getArea() == currentInstance && isInside(player);
    }

    public static boolean isInside(Player player) {
        return AreaManager.inside(player.getPosition(), new FightCaveArea(null));
    }

    public void setCurrentInstance(FightCaveArea instance) {
        this.instance = instance;
    }

    public FightCaveArea getCurrentInstance() {
        return instance;
    }

    public long getLastSession() {
        return lastSession;
    }

    public Wave getCurrentWave() {
        return current;
    }

    public Position getJadSpawn() {
        return jadSpawn;
    }

    public void setJadSpawn(Position jadSpawn) {
        this.jadSpawn = jadSpawn;
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


