package com.grinder.game.content.minigame.pestcontrol;

import com.grinder.game.World;
import com.grinder.game.content.points.ParticipationPoints;
import com.grinder.game.content.task_new.DailyTask;
import com.grinder.game.content.task_new.PlayerTaskManager;
import com.grinder.game.content.task_new.WeeklyTask;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.npc.monster.pestcontrol.*;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.FacingDirection;
import com.grinder.game.model.Position;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.grinder.game.content.minigame.pestcontrol.PestControl.spawnedNpcs;
import static com.grinder.util.ObjectID.*;
import static com.grinder.game.content.minigame.pestcontrol.PestControlConstants.*;

/**
 * To keep track of each Pest Control Instance.
 *
 * @author  Minoroin / TealWool#0873 (https://www.rune-server.ee/members/minoroin/)
 * @since   20/11/2021
 * @version 1.0
 */
public class PestControlInstance {

    public boolean finished = true;

    public boolean finishing = false;

    public ArrayList<Player> playersInInstance = new ArrayList<>();
    public ArrayList<NPC> npcsInInstance = new ArrayList<>();
    public ArrayList<Player> playersToRemove = new ArrayList<>();

    private int gameTimer;

    private int minutesLeft = GAME_TIME;

    private final PestControlDoorsManager doorsManager;

    private PestControlPortalManager portalManager;

    private final PestControlBarricadesManager barricadesManager;

    private NPC voidKnight;

    private ArrayList<Shifter> shifters = new ArrayList<>();
    private ArrayList<Brawler> brawlers = new ArrayList<>();
    private ArrayList<Defiler> defilers = new ArrayList<>();
    private ArrayList<Ravager> ravagers = new ArrayList<>();
    private ArrayList<Torcher> torchers = new ArrayList<>();
    private ArrayList<Splatter> splatters = new ArrayList<>();
    private ArrayList<Spinner> spinners = new ArrayList<>();

    private ArrayList<Shifter> shiftersToRemove = new ArrayList<>();
    private ArrayList<Brawler> brawlersToRemove = new ArrayList<>();
    private ArrayList<Defiler> defilersToRemove = new ArrayList<>();
    private ArrayList<Ravager> ravagersToRemove = new ArrayList<>();
    private ArrayList<Torcher> torchersToRemove = new ArrayList<>();
    private ArrayList<Splatter> splattersToRemove = new ArrayList<>();
    private ArrayList<Spinner> spinnersToRemove = new ArrayList<>();

    private PestControlBarricade[] barricades;

    private Position base = new Position(2624, 2560);

    private boolean running = false;

    public PestControlInstance() {
        doorsManager = new PestControlDoorsManager();

        barricadesManager = new PestControlBarricadesManager(this);
        barricades = barricadesManager.getBarricades();
    }

    public void startPestControl() {
        portalManager = new PestControlPortalManager(this);


        voidKnight = addNPC(NpcID.VOID_KNIGHT_2953, 32, 32);
        voidKnight.getMotion().update(MovementStatus.DISABLED);

        NPC squire = addNPC(NpcID.SQUIRE_2949, 31, 48);
        squire.getMotion().update(MovementStatus.DISABLED);
        squire.setFace(FacingDirection.SOUTH);
        squire.setPositionToFace(new Position(squire.getPosition().getX(), squire.getPosition().getY() -1, squire.getPosition().getZ()));

        gameTimer = (GAME_TIME*100);
        finished = false;
        finishing = false;

        TaskManager.submit(3, () -> {
            running = true;
        });
    }

    public void addPlayer(Player player)
    {
        playersInInstance.add(player);
    }

    protected NPC addNPC(int npcId, int localX, int localY) {
        NPC npc = NPCFactory.INSTANCE.create(npcId, base.transform(localX, localY, 0));
        World.getNpcAddQueue().add(npc);
        npc.fetchDefinition().setRespawn(-1);
        spawnedNpcs.add(npc);
        return npc;
    }

    protected void addRavager(Ravager ravager) {
        ravagers.add(ravager);
    }
    protected void addSplatter(Splatter splatter) { splatters.add(splatter); }
    protected void addSpinner(Spinner spinner) { spinners.add(spinner); }
    protected void addShifter(Shifter shifter) { shifters.add(shifter); }
    protected void addBrawler(Brawler brawler) { brawlers.add(brawler); }
    protected void addDefiler(Defiler defiler) { defilers.add(defiler); }
    protected void addTorcher(Torcher torcher) { torchers.add(torcher); }

    private void removeNPCsFromInstance() {
        spawnedNpcs.forEach((npc) -> {
            if (npc != null) {
                if (npc.isAlive() && npc.getHitpoints() > 0  && !npc.isDying()) {
                    npc.appendDeath();
                }
            }
        });
        spawnedNpcs.clear();
    }

    private void lostGame(boolean voidKnightDeath) {
        removeNPCsFromInstance();

        playersInInstance.forEach((player -> {
            //Show message lost game
            player.resetAttributes();
            player.BLOCK_ALL_BUT_TALKING = true;
            player.getCombat().reset(true);
            player.moveTo(new Position(2657, 2639, 0));

            // Increase points
            player.getPoints().increase(AttributeManager.Points.PEST_CONTROL_LOSSES, 1);

            player.getPacketSender().sendWalkableInterface(-1);

            // Play jinglebit
            player.getPacketSender().sendJinglebitMusic(144, 0); // lost jingle

            if (voidKnightDeath) {
                new DialogueBuilder(DialogueType.NPC_STATEMENT)
                        .setNpcChatHead(NpcID.SQUIRE)
                        .setText("The Void Knight was killed, another of our Order has", "fallen and that Island is lost.")
                        .setExpression(DialogueExpression.CRYING_ALMOST)
                        .start(player);
            } else {
                new DialogueBuilder(DialogueType.NPC_STATEMENT)
                        .setNpcChatHead(NpcID.SQUIRE)
                        .setText("There aren't enough players for the game to continue.")
                        .setExpression(DialogueExpression.CRYING_ALMOST)
                        .start(player);
                player.sendMessage("@red@Not enough players to continue the game!");
            }
            TaskManager.submit(1, () -> {
                player.BLOCK_ALL_BUT_TALKING = false;
            });
        }));
        cleanUp();
    }

    private void winGame(boolean portals) {
        removeNPCsFromInstance();

        playersInInstance.forEach((player -> {
            //Show message lost game
            resetPlayer(player);
            player.BLOCK_ALL_BUT_TALKING = true;
            if (player.pestControlZeal >= 400) {

                // OLD REWARD SYSTEM
                //player.getAttributes().numAttr(Attribute.PEST_CONTROL_POINTS, 0).setValue(5 + player.getAttributes().numInt(Attribute.PEST_CONTROL_POINTS));

                // Add commendation points
                int pointsReward = 10 + Misc.random(10);
                player.getPoints().increase(AttributeManager.Points.COMMENDATION, pointsReward);

                // Participation points
                ParticipationPoints.addPoints(player, Misc.random(5), "@dre@from Minigames</col>.");

                // Extra cash reward
                player.getInventory().add(new Item(995, (player.getSkillManager().calculateCombatLevel() * COINS_GIVEN)));

                // Play jinglebit
                player.getPacketSender().sendJinglebitMusic(145, 0); // Win jingle

                // Increase points
                player.getPoints().increase(AttributeManager.Points.PEST_CONTROL_WINS, 1);

                PlayerTaskManager.progressTask(player, DailyTask.PEST_CONTROL);
                PlayerTaskManager.progressTask(player, WeeklyTask.PEST_CONTROL);

                if (portals) {
                    new DialogueBuilder(DialogueType.NPC_STATEMENT)
                            .setNpcChatHead(NpcID.SQUIRE)
                            .setText("Congratulations! You managed to destroy all the portals!", "We've awarded you " + pointsReward +" Void Knight Commendation", "points. Please also accept these coins as a reward.")
                            .setExpression(DialogueExpression.HAPPY)
                            .add(DialogueType.STATEMENT)
                            .setText("@blu@You now have @dre@"+player.getPoints().get(AttributeManager.Points.COMMENDATION)+" @blu@Void Knight Commendation points!", "You can speak to a Void Knight to exchange your points for", "rewards.")
                            .start(player);
                } else {
                    new DialogueBuilder(DialogueType.NPC_STATEMENT)
                            .setNpcChatHead(NpcID.SQUIRE)
                            .setText("Congratulations! You managed to destroy all the portals!", "fallen and that Island is lost.")
                            .setExpression(DialogueExpression.HAPPY)
                            .add(DialogueType.STATEMENT)
                            .setText("@blu@You now have @red@"+player.getPoints().get(AttributeManager.Points.COMMENDATION)+" @blu@Void Knight Commendation points!", "You can speak to a Void Knight to exchange your points for", "rewards.")
                            .start(player);
                }
            } else {
                new DialogueBuilder(DialogueType.STATEMENT)
                        .setText("The knights noticed your lack of zeal in that battle and have not", "presented you with any points.")
                        .start(player);
            }
            TaskManager.submit(1, () -> {
                player.BLOCK_ALL_BUT_TALKING = false;
            });
        }));
        cleanUp();
    }

    private void resetPlayer(Player player) {
        player.resetAttributes();
        player.getCombat().reset(true);
        player.moveTo(new Position(2657, 2639, 0));

        player.getPacketSender().sendWalkableInterface(-1);
    }

    private void cleanUp() {
        ravagers.clear();
        splatters.clear();
        spinners.clear();
        shifters.clear();
        brawlers.clear();
        defilers.clear();
        torchers.clear();

        TaskManager.submit(3, () -> {
            playersInInstance.clear();
            finished = true;
        });
        finishing = true;
        running = false;

        PestControl.LOBBY_TIMER = 50;
    }

    public void process() {
        if (finished) {
            return;
        }
        if (finishing) {
            return;
        }
        if (running) {

            gameTimer--;

            playersToRemove.forEach(playerToRemove -> {
                playersInInstance.remove(playerToRemove);
            });
            playersToRemove.clear();

            playersInInstance.forEach((player -> {
                if (player.pestControlZeal>0) {
                    player.pestControlZeal-=2;
                }
                player.getPacketSender().sendWalkableInterface(65100);

                int secondsLeft = (int) (gameTimer * 0.6f);
                int minutesLeft = 0;

                while(secondsLeft >= 60) {
                    minutesLeft++;
                    secondsLeft -= 60;
                }

                player.getPacketSender().sendString(65101, (minutesLeft+1) + " minutes left!"); // Time

                if (player.pestControlZeal < 600) {
                    player.getPacketSender().sendString(65102, "@dre@" + (player.pestControlZeal / 10) + "%"); // Zeal
                } else {
                    player.getPacketSender().sendString(65102, (player.pestControlZeal / 10) + "%"); // Zeal
                }

                player.getPacketSender().sendString(65104, Integer.toString(player.pestControlTotalHit)); //Total hit

                player.getPacketSender().sendString(65106, Integer.toString(portalManager.getWestPortalNPC().getHitpoints())); //WEST portal HP
                player.getPacketSender().sendString(65107, Integer.toString(portalManager.getEastPortalNPC().getHitpoints())); //EAST portal HP
                player.getPacketSender().sendString(65108, Integer.toString(portalManager.getSouthEastPortalNPC().getHitpoints())); //SE Portal HP
                player.getPacketSender().sendString(65109, Integer.toString(portalManager.getSouthWestPortalNPC().getHitpoints())); //SW Portal HP

                if (outsideMapInstance(player)) {
                    resetPlayer(player);
                    playersToRemove.add(player);
                }
            }));


            if (voidKnight == null) {
                lostGame(false);
                playersInInstance.forEach(player -> {
                    player.sendMessage("An error occurred during Pest Control.");
                });
                return;
            }
            if (voidKnight.isDying() || voidKnight.getHitpoints() <= 0 || playersInInstance.size() < PLAYERS_NEEDED) {
                lostGame(voidKnight.isDying() || voidKnight.getHitpoints() <= 0);
            }
            if (gameTimer <= 0 || portalManager.allPortalsDead()) {
                winGame(portalManager.allPortalsDead());
            }
            if (gameTimer % 100 == 0) {
                minutesLeft--;
            }
            portalManager.process();

            if (shifters.size() >= 1) {
                shifters.forEach(shifter -> {
                    shifter.process(base, doorsManager, voidKnight);
                    //remove is shifter is dead..
                    if (shifter.isDying()) {
                        shiftersToRemove.add(shifter);
                    }
                });
            }
            if (defilers.size() >= 1) {
                defilers.forEach(defiler -> {
                    defiler.process(base, doorsManager, voidKnight);
                    //remove is defiler is dead..
                    if (defiler.isDying()) {
                        defilersToRemove.add(defiler);
                    }
                });
            }
            if (torchers.size() >= 1) {
                torchers.forEach(torcher -> {
                    torcher.process(base, doorsManager, voidKnight);
                    //remove is torcher is dead..
                    if (torcher.isDying()) {
                        torchersToRemove.add(torcher);
                    }
                });
            }
            if (shifters.size() >= 1) {
                shifters.forEach(shifter -> {
                    shifter.process(base, doorsManager, voidKnight);
                    //remove is shifter is dead..
                    if (shifter.isDying()) {
                        shiftersToRemove.add(shifter);
                    }
                });
            }
            if (ravagers.size() >= 1) {
                ravagers.forEach(ravager -> {
                    ravager.attackObject(base, barricades, doorsManager);
                    //remove is ravager is dead..
                    if (ravager.isDying()) {
                        ravagersToRemove.add(ravager);
                    }
                });
            }
            if (splatters.size() >= 1) {
                splatters.forEach(splatter -> {
                    splatter.attackObject(base, barricades, doorsManager);
                    //remove is splatter is dead..
                    if (splatter.isDying()) {
                        splattersToRemove.add(splatter);
                    }
                });
            }
            shiftersToRemove.forEach(shifter -> {
                shifters.remove(shifter);
            });
            shiftersToRemove.clear();
            defilersToRemove.forEach(defiler -> {
                defilers.remove(defiler);
            });
            defilersToRemove.clear();
            torchersToRemove.forEach(torcher -> {
                torchers.remove(torcher);
            });
            torchersToRemove.clear();
            shiftersToRemove.forEach(shifter -> {
                shifters.remove(shifter);
            });
            shiftersToRemove.clear();
            ravagersToRemove.forEach(ravager -> {
                ravagers.remove(ravager);
            });
            ravagersToRemove.clear();
            splattersToRemove.forEach(splatter -> {
                splatters.remove(splatter);
            });
            splattersToRemove.clear();
        }
    }

    public boolean outsideMapInstance(Player player) {
        Position playerPosition = player.getPosition();
        Position mapBasePosition = base;
        if (!playerPosition.inside(mapBasePosition.getX(), mapBasePosition.getY(), mapBasePosition.getX()+64, mapBasePosition.getY()+64)) {
            return true;
        }
        return false;
    }

    public boolean handlePlayerDeath(Player player) {
        if (hasPlayer(player)) {
            player.moveTo(base.transform(32 + Misc.random(3), 49 + Misc.random(5), 0));
            return true;
        }
        return false;
    }

    public boolean handleObject(Player player, GameObject object, int clickNumber) {
        if (barricadesManager.repairBarricade(player, object) && clickNumber == 3)
            return true;

        Position playersPosition = player.getPosition();
        Position southDoor = base.transform(32, 25, 0);
        Position westDoor = base.transform(19, 32, 0);
        Position eastDoor = base.transform(46, 32, 0);
        if (clickNumber == 1) {
            switch (object.getId()) {
                case GATE_122:
                case GATE_120:
                    if (playersPosition.isWithinDistance(southDoor, 5)) {
                        doorsManager.openSouthDoor(base);
                        player.getPacketSender().sendAreaPlayerSound(67, 5, 1, 0);
                        return true;
                    }
                    if (playersPosition.isWithinDistance(westDoor, 5)) {
                        doorsManager.openWestDoor(base);
                        player.getPacketSender().sendAreaPlayerSound(67, 5, 1, 0);
                        return true;
                    }
                    if (playersPosition.isWithinDistance(eastDoor, 5)) {
                        doorsManager.openEastDoor(base);
                        player.getPacketSender().sendAreaPlayerSound(67, 5, 1, 0);
                        return true;
                    }
                    break;
                case GATE_123:
                case GATE_121:
                    if (playersPosition.isWithinDistance(southDoor, 5)) {
                        player.getPacketSender().sendAreaPlayerSound(67, 5, 1, 0);
                        doorsManager.closeSouthDoor(base);
                        return true;
                    }
                    if (playersPosition.isWithinDistance(westDoor, 5)) {
                        player.getPacketSender().sendAreaPlayerSound(67, 5, 1, 0);
                        doorsManager.closeWestDoor(base);
                        return true;
                    }
                    if (playersPosition.isWithinDistance(eastDoor, 5)) {
                        player.getPacketSender().sendAreaPlayerSound(67, 5, 1, 0);
                        doorsManager.closeEastDoor(base);
                        return true;
                    }
                    break;
                case LADDER_174:
                    if (!EntityExtKt.passedTime(player, Attribute.GENERIC_ACTION, 3, TimeUnit.SECONDS, true, true))
                        return true;
                    //West Ladder
                    if (playersPosition.isWithinDistance(base.transform(23, 27, 0), 1)) {
                        player.moveTo(base.transform(23, 25, 0));
                    } else if (playersPosition.sameAs(base.transform(23, 25, 0))) {
                        player.moveTo(base.transform(23, 27, 0));
                    } else if (playersPosition.isWithinDistance(base.transform(42, 27, 0), 1)) {
                        player.moveTo(base.transform(42, 25, 0));
                    } else if (playersPosition.sameAs(base.transform(42, 25, 0))) {
                        player.moveTo(base.transform(42, 27, 0));
                    } else if (playersPosition.isWithinDistance(base.transform(44, 41, 0), 1)) {
                        player.moveTo(base.transform(46, 41, 0));
                    } else if (playersPosition.sameAs(base.transform(46, 41, 0))) {
                        player.moveTo(base.transform(44, 41, 0));
                    } else if (playersPosition.isWithinDistance(base.transform(21, 41, 0), 1)) {
                        player.moveTo(base.transform(19, 41, 0));
                    } else if (playersPosition.sameAs(base.transform(19, 41, 0))) {
                        player.moveTo(base.transform(21, 41, 0));
                    }
                    return true;
            }
        }
        if (clickNumber == 3) {
            switch (object.getId()) {
                case GATE_129:
                case GATE_130:
                case GATE_132:
                case GATE_133:
                    if (playersPosition.isWithinDistance(southDoor, 5)) {
                        doorsManager.repairSouthDoor(player, base);
                        return true;
                    }
                    if (playersPosition.isWithinDistance(westDoor, 5)) {
                        doorsManager.repairWestDoor(player, base);
                        return true;
                    }
                    if (playersPosition.isWithinDistance(eastDoor, 5)) {
                        doorsManager.repairEastDoor(player, base);
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    public boolean hasPlayer(Player player) {
        return playersInInstance.contains(player);
    }
}
