package com.grinder.game.content.minigame.blastfurnace.npcs;

import com.grinder.game.World;
import com.grinder.game.content.minigame.blastfurnace.BlastFurnace;
import com.grinder.game.content.minigame.blastfurnace.BlastFurnaceOre;
import com.grinder.game.entity.agent.movement.pathfinding.PathFinder;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.model.Position;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;

import java.util.Arrays;
import java.util.function.IntPredicate;

import static com.grinder.util.NpcID.*;

/**
 * @author L E G E N D
 * @date 2/28/2021
 * @time 1:49 AM
 * @discord L E G E N D#4380
 */
public class DwarvenMiner extends NPC {

    public static final int[] CART_IDS = new int[]{
            DWARVEN_MINER, DWARVEN_MINER_2435,
            DWARVEN_MINER_2439, DWARVEN_MINER_2440,
            DWARVEN_MINER_2444, DWARVEN_MINER_2445};

    public static final int[] IDS = new int[]{
            DWARVEN_MINER_2436,
            DWARVEN_MINER_2437, DWARVEN_MINER_2438,
            DWARVEN_MINER_2441, DWARVEN_MINER_2442,
            DWARVEN_MINER_2443, DWARVEN_MINER_2446};

    public static final Position SPAWN_POSITION = new Position(1939, 4958);
    public static final Position END_POSITION = new Position(1940, 4958);
    private static boolean done = true;
    public final Position ORDAN_POSITION = BlastFurnace.getOrdan().getPosition().transform(0, -1, 0);

    public DwarvenMiner(int id, Position position) {
        super(id, position);
    }

    public static void process() {
        if (done) {
            TaskManager.submit(Misc.random(10, 15), () -> create().start());
            done = false;
        }
    }

    public void start() {
        World.getNpcAddQueue().add(this);
        TaskManager.submit(2, () -> PathFinder.INSTANCE.find(this, ORDAN_POSITION, false));
        TaskManager.submit(new Task(1, false) {
            @Override
            protected void execute() {
                if (!isActive()){
                    stop();
                    return;
                }
                if (getPosition().equals(ORDAN_POSITION)) {
                    giveOre();
                    stop();
                }
            }
        });
    }

    public void leave() {
        TaskManager.submit(2, () -> PathFinder.INSTANCE.find(this, END_POSITION, false));
        TaskManager.submit(new Task(1) {
            @Override
            protected void execute() {
                if (!isActive()){
                    stop();
                    return;
                }
                if (getPosition().equals(END_POSITION)) {
                    done = true;
                    World.getNpcRemoveQueue().add(DwarvenMiner.this);
                    stop();
                }
            }
        });

    }

    public void giveOre() {
        TaskManager.submit(3, () -> {
            var randomOre = getRandomOre();
            say("Here, " + Misc.random(20, 40) + (randomOre == BlastFurnaceOre.COAL ? " pieces of " : " ores of ") + randomOre.getName().toLowerCase() + ".");
            TaskManager.submit(4, () -> {
                BlastFurnace.getOrdan().say("Thanks, I'll send them to storage.");
                leave();
            });
        });
    }

    public BlastFurnaceOre getRandomOre() {
        return Misc.random(BlastFurnaceOre.values());
    }

    public static DwarvenMiner create() {
        return new DwarvenMiner(IDS[Misc.random(0, IDS.length - 1)], SPAWN_POSITION);
    }

    public static boolean isDwarvenMiner(int id) {
        return Arrays.stream(IDS).anyMatch(value -> value == id);
    }
}
