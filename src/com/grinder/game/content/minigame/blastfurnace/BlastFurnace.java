package com.grinder.game.content.minigame.blastfurnace;

import com.google.gson.annotations.Expose;
import com.grinder.game.World;
import com.grinder.game.content.minigame.blastfurnace.conveyor.BlastFurnaceState;
import com.grinder.game.content.minigame.blastfurnace.conveyor.ConveyorBelt;
import com.grinder.game.content.minigame.blastfurnace.dispenser.BarDispenser;
import com.grinder.game.content.minigame.blastfurnace.npcs.*;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.ClippedMapObjects;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.ItemActions;
import com.grinder.game.model.ObjectActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;
import com.grinder.util.ObjectID;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.grinder.game.content.minigame.blastfurnace.conveyor.BlastFurnaceState.BROKEN;
import static com.grinder.game.content.minigame.blastfurnace.conveyor.BlastFurnaceState.RUNNING;

/**
 * @author L E G E N D
 * @date 2/15/2021
 * @time 6:23 AM
 * @discord L E G E N D#4380
 */
@SuppressWarnings("unused")
public class BlastFurnace {

    private static final Map<Player, Set<BlastFurnaceOreNpc>> NPCS = new HashMap<>();
    private static final BlastFurnaceStove STOVE = new BlastFurnaceStove();
    private static BlastFurnaceState state = RUNNING;
    public static final int COST = 2_520_000;

    @Expose
    private final Map<BlastFurnaceOre, Integer> inMachine;
    @Expose
    private long lastUsed;
    @Expose
    private final BlastFurnaceCoffer coffer ;
    @Expose
    private final ConveyorBelt conveyorBelt;
    @Expose
    private final BarDispenser barDispenser;


    public BlastFurnace() {
        this.inMachine = new HashMap<>();
        this.coffer = new BlastFurnaceCoffer();
        this.conveyorBelt = new ConveyorBelt();
        this.barDispenser = new BarDispenser();
    }

    static {
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.MELTING_POT}, action -> {
            checkPot(action.getPlayer());
            return true;
        });
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.CONVEYOR_BELT}, action -> {
            addToMachine(action.getPlayer());
            return true;
        });
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.SINK_8}, action -> {
            var player = action.getPlayer();
            if (!player.getInventory().contains(ItemID.BUCKET)) {
                player.sendMessage("You don't have anything to fill with water.");
                return true;
            }
            player.getInventory().replaceFirst(ItemID.BUCKET, ItemID.BUCKET_OF_WATER);
            player.getInventory().refreshItems();
            player.sendMessage("You fill your bucket with water.");
            player.playSound(new Sound(Sounds.FILLING_BUCKET_WITH_WATER));
            return true;
        });
        ItemActions.INSTANCE.onItemOnObjectByItemId(ItemID.BUCKET, ObjectID.SINK_8, action -> {
            var player = action.getPlayer();
            if (!player.getInventory().contains(ItemID.BUCKET)) {
                player.sendMessage("You don't have anything to fill with water.");
                return true;
            }
            player.getInventory().replace(action.getSlot(), new Item(ItemID.BUCKET_OF_WATER));
            player.getInventory().refreshItems();
            player.sendMessage("You fill your bucket with water.");
            player.playSound(new Sound(Sounds.FILLING_BUCKET_WITH_WATER));
            return true;
        });

        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.COKE}, action -> {
            STOVE.collect(action.getPlayer());
            return true;
        });
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.STOVE, ObjectID.STOVE_2, ObjectID.STOVE_3}, action -> {
            STOVE.refuel(action.getPlayer());
            return true;
        });
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.TEMPERATURE_GAUGE}, action -> {
            new DialogueBuilder(DialogueType.STATEMENT)
                    .setText("The gauage shows the temperature is : " + STOVE.getTemperature())
                    .start(action.getPlayer());

            return true;
        });
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.PUMP, ObjectID.PEDALS}, action -> {
            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(NpcID.BLAST_FURNACE_FOREMAN)
                    .setText("Hey .. Please don't disturb the workers.").start(action.getPlayer());
            return true;
        });
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.STAIRS_41}, action -> {
            //Disabled cause Keldagrim has no content.
            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(NpcID.BLAST_FURNACE_FOREMAN)
                    .setText("You're not allowed up there human.")
                    .start(action.getPlayer());
            return true;
        });
        startTask();
    }

    public static void check(Player player) {
        var blastFurnace = player.getBlastFurnace();
        if (blastFurnace.getLastUsed() >= 59) {
            return;
        }
        if (blastFurnace.canUse()) {
            blastFurnace.getCoffer().withdrawFromCoffer((int) Math.round(COST / 6000.0));
            if (blastFurnace.getCoffer().getCoinsInCoffer() < 12) {
                new DialogueBuilder(DialogueType.STATEMENT)
                        .setText("Your coffer has ran out of coins...")
                        .start(player);
            }
        }
    }

    public void update() {
        lastUsed = System.currentTimeMillis();
    }

    public void sendToBelt(BlastFurnaceOre ore) {
        getConveyorBelt().add(ore, getAmountInMachine(ore));
        inMachine.remove(ore);
        sendToDispenser();
    }

    public void sendToDispenser() {
        for (var barIndex = BlastFurnaceBar.values().length - 1; barIndex >= 0; barIndex--) {
            var bar = BlastFurnaceBar.values()[barIndex];
            while (hasRequirements(bar) && !getBarDispenser().isFull()) {
                for (var requirement : bar.getRequirements()) {
                    getConveyorBelt().remove(requirement.getOre(), requirement.getAmountRequired());
                }
                getBarDispenser().add(bar, 1);
            }
        }
    }

    public boolean isWorking() {
        return getLastUsed() <= 60;
    }

    public boolean canUse() {
        return coffer.getCoinsInCoffer() > 12 && isRunning();
    }

    public BlastFurnaceCoffer getCoffer() {
        return coffer;
    }

    public int getLastUsed() {
        return (int) (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastUsed));
    }

    public ConveyorBelt getConveyorBelt() {
        return conveyorBelt;
    }

    public BarDispenser getBarDispenser() {
        return barDispenser;
    }

    public Map<BlastFurnaceOre, Integer> getOresInMachine() {
        return inMachine;
    }

    public int getAmountInMachine(BlastFurnaceOre ore) {
        return inMachine.getOrDefault(ore, 0);
    }

    public int getAmountInMachine() {
        var amount = 0;
        for (var ore : inMachine.values()) {
            amount += ore;
        }
        return amount;
    }

    public boolean hasRequirements(BlastFurnaceBar bar) {
        for (var requirement : bar.getRequirements()) {
            if (getConveyorBelt().getAmount(requirement.getOre()) < requirement.getAmountRequired()) {
                return false;
            }
        }
        return true;
    }

    public static void process() {
        if (!AreaManager.BLAST_FURNACE_AREA.hasPlayers()) {
            return;
        }
        DwarvenMiner.process();
        updateConveyorBelt();
        updateDriveBelt();
        updateGearBox();
        STOVE.update();
    }

    public static void addToMachine(Player player) {

        if (player.getBlastFurnace().getCoffer().getCoinsInCoffer() == 0) {
            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                    .setItem(ItemID.COINS_4, 200)
                    .setText("You must put money in the coffer to pay the workers.")
                    .start(player);
            return;
        }
        if (player.getBlastFurnace().getBarDispenser().isFull()) {
            new DialogueBuilder(DialogueType.STATEMENT)
                    .setText("You should collect your bars before making any more.")
                    .start(player);
            return;
        }
        /*if (player.getBlastFurnace().getAmountInMachine() > 0) {
            //very very very slim chance of this occurring
            player.sendMessage("Please wait for ores to process before adding more...");
            return;
        }*/
        var oresInInventory = getOresInInventory(player);
        if (oresInInventory.size() == 0) {
            new DialogueBuilder(DialogueType.STATEMENT)
                    .setText("You don't have anything suitable for putting into the blase furnace.")
                    .start(player);
            return;
        }

        var noSpace = false;
        for (var ore : oresInInventory.keySet()) {
            var amount = getAmountInInventory(player, ore);
            var beltSpace = player.getBlastFurnace().getConveyorBelt().getSpace(ore);

            if (beltSpace <= 0) {
                noSpace = true;
                continue;
            }
            if (amount > beltSpace) {
                amount = beltSpace;
            }
            player.getInventory().delete(ore.getOreId(), amount);
            player.getBlastFurnace().getOresInMachine().put(ore, amount);
            player.sendMessage("All your ores goes onto the conveyor belt.");
            addNpc(player, ore);
            player.getBlastFurnace().update();
        }
        if (noSpace) {
            player.sendMessage("Your belt is getting full...");
        }
    }

    public static void startTask() {
        TaskManager.submit(Misc.random(100, 1000), () -> switchState(BROKEN));
    }

    public static void repair() {
        getThumpy().repair();
        getNumpty().repair();
        TaskManager.submit(10, () -> switchState(RUNNING));
    }

    public static void onEnter(Player player) {
        if (player.getBlastFurnace().getConveyorBelt().getAmount() > 0) {
            for (var ore : player.getBlastFurnace().getOresInMachine().keySet()) {
                addNpc(player, ore);
            }
        }
        player.getBlastFurnace().getBarDispenser().onEnter(player);
    }

    public static void onLeave(Player player) {
        player.getPacketSender().sendWalkableInterface(-1);
        if (NPCS.containsKey(player)) {
            removeAllNpcs(player);
        }
    }

    public static void addNpc(Player player, BlastFurnaceOre ore) {
        NPCS.putIfAbsent(player, new HashSet<>());
        NPCS.get(player).add(BlastFurnaceOreNpc.create(ore, player));
        if (player.getBlastFurnace().canUse()) {
            getOreNPC(player, ore).ifPresent(BlastFurnaceOreNpc::start);
        }
    }

    private static void startOreNpcs() {
        for (Player player : AreaManager.BLAST_FURNACE_AREA.players) {
            startOreNpcs(player);
        }
    }

    public static void startOreNpcs(Player player) {
        if (player.getBlastFurnace().getConveyorBelt().getAmount() > 0 && player.getBlastFurnace().canUse()) {
            for (var ore : player.getBlastFurnace().getOresInMachine().keySet()) {
                getOreNPC(player, ore).ifPresent(BlastFurnaceOreNpc::start);
            }
        }
    }

    public static void removeNpc(Player player, BlastFurnaceOre ore) {
        getOreNPC(player, ore).ifPresent(npc -> World.getNpcRemoveQueue().add(npc));
        NPCS.get(player).removeIf(npc -> npc.getOre() == ore);
    }

    public static void removeAllNpcs(Player player) {
        NPCS.remove(player);
    }

    public static void checkPot(Player player) {
        var bf = player.getBlastFurnace().getConveyorBelt();
        new DialogueBuilder(DialogueType.STATEMENT)
                .setText("Coal: " + bf.getAmount(BlastFurnaceOre.COAL),
                        "Tin Ore: " + bf.getAmount(BlastFurnaceOre.TIN),
                        "Copper Ore: " + bf.getAmount(BlastFurnaceOre.COPPER),
                        "Iron Ore: " + bf.getAmount(BlastFurnaceOre.IRON))
                .setNext(new DialogueBuilder(DialogueType.STATEMENT)
                        .setText("Silver Ore: " + bf.getAmount(BlastFurnaceOre.SILVER),
                                "Gold Ore: " + bf.getAmount(BlastFurnaceOre.GOLD),
                                "Mithril Ore: " + bf.getAmount(BlastFurnaceOre.MITHRIL),
                                "Adamantite Ore: " + bf.getAmount(BlastFurnaceOre.ADAMANTITE))
                        .setNext(new DialogueBuilder(DialogueType.STATEMENT)
                                .setText("Runite Ore: " + bf.getAmount(BlastFurnaceOre.RUNITE))
                        )).start(player);
    }


    public static void switchState(BlastFurnaceState state) {
        if (!AreaManager.BLAST_FURNACE_AREA.hasPlayers()) {
            return;
        }
        if (state == BROKEN) {
            BlastFurnace.state = state;
            repair();
        } else {
            if (getStove().canRun()) {
                BlastFurnace.state = state;
                startTask();
                startOreNpcs();
            }
        }
    }

    public static BlastFurnaceOre getMostAmount(Player player) {
        BlastFurnaceOre result = BlastFurnaceOre.COPPER;
        var amount = 0;
        for (var ore : BlastFurnaceOre.values()) {
            if (player.getInventory().getAmount(ore.getOreId()) > amount) {
                result = ore;
            }
        }
        return result;
    }

    public static Map<BlastFurnaceOre, Integer> getOresInInventory(Player player) {
        var map = new HashMap<BlastFurnaceOre, Integer>();
        for (var ore : BlastFurnaceOre.values()) {
            var amount = getAmountInInventory(player, ore);
            if (amount > 0) {
                map.put(ore, amount);
            }
        }
        return map;
    }

    public static int getAmountInInventory(Player player, BlastFurnaceOre ore) {
        return player.getInventory().getAmount(ore.getOreId());
    }

    public static Optional<BlastFurnaceOreNpc> getOreNPC(Player player, BlastFurnaceOre ore) {
        for (var npc : NPCS.get(player)) {
            if (npc.getOre() == ore) {
                return Optional.of(npc);
            }
        }
        return Optional.empty();
    }

    public static boolean isRunning() {
        return state == RUNNING;
    }

    public static boolean isBroken() {
        return state == BROKEN;
    }

    public static BlastFurnaceState getState() {
        return state;
    }

    public static Ordan getOrdan() {
        return (Ordan) AreaManager.BLAST_FURNACE_AREA.searchAnyNpc(npc -> npc instanceof Ordan).orElse(null);
    }

    public static Thumpy getThumpy() {
        return (Thumpy) AreaManager.BLAST_FURNACE_AREA.searchAnyNpc(npc -> npc instanceof Thumpy).orElse(null);
    }

    public static Numpty getNumpty() {
        return (Numpty) AreaManager.BLAST_FURNACE_AREA.searchAnyNpc(npc -> npc instanceof Numpty).orElse(null);
    }

    public static Dumpy getDumpy() {
        return (Dumpy) AreaManager.BLAST_FURNACE_AREA.searchAnyNpc(npc -> npc instanceof Dumpy).orElse(null);
    }

    public static BlastFurnaceStove getStove() {
        return STOVE;
    }

    private static GameObject GEAR_BOX_OBJ; //cache the objects!

    public static void updateGearBox() {
        if (GEAR_BOX_OBJ == null)
            GEAR_BOX_OBJ = ClippedMapObjects.findObject(ObjectID.GEAR_BOX, new Position(1945, 4966)).get();
        if (isRunning()) {
            GEAR_BOX_OBJ.performAnimation(new Animation(2436));
        }
    }

    public static void updateConveyorBelt() {
        if (isBroken()) {
            return;
        }
        var animationId = 2437;
        final var animation = new Animation(animationId);

        for (int i = 0; i < 3; i++) {
            ConveyorBelt.getBelt(i).performAnimation(animation);
        }
    }

    private static GameObject driveBelt1, driveBelt2, cog1, cog2; //cache the objects!

    public static void updateDriveBelt() {
        var animationId = 2438;
        if (isBroken()) {
            animationId = 2451;
        }
        final var animation = new Animation(animationId);
        if (driveBelt1 == null)
            driveBelt1 =  ClippedMapObjects.findObject( ObjectID.DRIVE_BELT, new Position(1944, 4967, 0)).get();
        driveBelt1.performAnimation(animation);

        if (driveBelt2 == null)
            driveBelt2 = ClippedMapObjects.findObject(ObjectID.DRIVE_BELT_3, new Position(1944, 4965, 0)).get();
        driveBelt2.performAnimation(animation);

        //Cogs (BottomDrive Belts)
        if (cog1 == null)
            cog1 = ClippedMapObjects.findObject(ObjectID.COGS, new Position(1945, 4967, 0)).get();
        cog1.performAnimation(animation);

        if (cog2 == null)
            cog2 = ClippedMapObjects.findObject(ObjectID.COGS_3, new Position(1945, 4965, 0)).get();
        cog2.performAnimation(animation);
    }
}
