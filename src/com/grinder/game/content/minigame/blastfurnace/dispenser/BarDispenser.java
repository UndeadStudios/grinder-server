package com.grinder.game.content.minigame.blastfurnace.dispenser;

import com.google.gson.annotations.Expose;
import com.grinder.game.content.minigame.blastfurnace.BlastFurnaceBar;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.ClippedMapObjects;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.*;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.ObjectID;
import kotlin.Pair;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author L E G E N D
 * @date 2/17/2021
 * @time 6:09 AM
 * @discord L E G E N D#4380
 */
public class BarDispenser {

    @Expose
    private DispenserState state = DispenserState.EMPTY;

    @Expose
    private final Map<BlastFurnaceBar, Integer> bars = new HashMap<>();

    static {
        ItemActions.INSTANCE.onItemOnObjectByItemId(ItemID.BUCKET_OF_WATER, ObjectID.BAR_DISPENSER_DEFAULT, action -> {
            useBucket(action.getPlayer());
            return true;
        });
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.BAR_DISPENSER_DEFAULT}, action -> {
            if (action.isFirstOption()) {
                take(action.getPlayer());
            } else {
                check(action.getPlayer());
            }
            return true;
        });
    }

    public void onEnter(Player player) {
        updateVisual(player);
    }

    public void add(BlastFurnaceBar bar, int amount) {
        var newAmount = getAmount(bar) + amount;
        bars.put(bar, newAmount);
    }

    public void remove(BlastFurnaceBar bar, int amount) {
        var newAmount = getAmount(bar) - amount;
        if (newAmount == 0) {
            bars.remove(bar);
            return;
        }
        bars.put(bar, newAmount);
    }

    public void setState(DispenserState state) {
        this.state = state;
    }

    public boolean isFull() {
        return getSpace() == 0;
    }

    public boolean isHot() {
        return state == DispenserState.HOT;
    }

    public boolean isCold() {
        return state == DispenserState.COLD;
    }

    public boolean isMelting() {
        return state == DispenserState.MELTING;
    }

    public boolean isEmpty() {
        return getAmount() == 0;
    }

    public DispenserState getState() {
        return state;
    }

    public Map<BlastFurnaceBar, Integer> getBars() {
        return bars;
    }

    public int getSpace() {
        return 28 - getAmount();
    }

    public int getAmount() {
        var amount = 0;
        for (var type : bars.keySet()) {
            amount += getAmount(type);
        }
        return amount;
    }

    public int getAmount(BlastFurnaceBar bar) {
        return bars.getOrDefault(bar, 0);
    }

    private static void switchState(Player player, DispenserState state) {
        player.getBlastFurnace().getBarDispenser().setState(state);
        updateVisual(player);
    }

    public static void flash() {
        getObject().ifPresent(object -> object.performAnimation(new Animation(2440)));
    }

    public static void melt(Player player) {
        switchState(player, DispenserState.MELTING);
        TaskManager.submit(2, BarDispenser::flash);
        TaskManager.submit(3, () -> switchState(player, DispenserState.HOT));
    }

    public static void sendInterface(Player player) {
        var options = new ArrayList<Pair<String, Consumer<Player>>>();
        for (var type : player.getBlastFurnace().getBarDispenser().getBars().keySet()) {
            options.add(getOption(type));
        }
        options.add(new Pair<>("Cancel.", $ -> DialogueManager.start(player, -1)));
        new DialogueBuilder(DialogueType.OPTION).addOptions(options).start(player);
    }

    public static void check(Player player) {
        var state = player.getBlastFurnace().getBarDispenser().getState();
        var text = "";
        switch (state) {
            case EMPTY:
                text = "There is nothing inside the Dispenser.";
                break;
            case HOT:
                text = "The Dispenser is hot.";
                break;
            case MELTING:
                text = "Bars are still being melted.";
                break;
            case COLD:
                text = "The Dispenser is cold.";
                break;
        }
        new DialogueBuilder(DialogueType.STATEMENT)
                .setText(text)
                .start(player);
    }

    public static void take(Player player) {
        var dispenser = player.getBlastFurnace().getBarDispenser();
        if (dispenser.isEmpty()) {
            new DialogueBuilder(DialogueType.STATEMENT)
                    .setText("The dispenser doesn't contain any bars.")
                    .start(player);
            return;
        }

        if (dispenser.isHot() && !isWearingIceGloves(player)) {
            new DialogueBuilder(DialogueType.STATEMENT).setText("Too hot!").start(player);
            return;
        }
        if (dispenser.isMelting()) {
            new DialogueBuilder(DialogueType.STATEMENT).setText("Your ores are being melted ... please wait!").start(player);
            return;
        }
        if (player.getInventory().countFreeSlots() == 0) {
            new DialogueBuilder(DialogueType.STATEMENT)
                    .setText("You don't have any free inventory space.")
                    .start(player);
            return;
        }

        if (isWearingIceGloves(player) || player.getBlastFurnace().getBarDispenser().isCold()) {
            sendInterface(player);
            player.getBlastFurnace().update();
        } else {
            new DialogueBuilder(DialogueType.STATEMENT)
                    .setText("The bars are still molten! You need to cool them down.")
                    .start(player);
        }
    }

    public static void collect(Player player, BlastFurnaceBar bar) {
        var dispenser = player.getBlastFurnace().getBarDispenser();
        final var originalAmount = dispenser.getAmount(bar);
        while (dispenser.getAmount(bar) > 0) {
            if (player.getInventory().countFreeSlots() == 0) {
                break;
            }
            player.getInventory().add(bar.getBarId(), 1);
            if (bar == BlastFurnaceBar.GOLD && isWearingGoldsmithGauntlets(player)) {
                player.getSkillManager().addExperience(Skill.SMITHING, 56.2);
            } else {
                player.getSkillManager().addExperience(Skill.SMITHING, bar.getXp());
            }
            dispenser.remove(bar, 1);
        }
        new DialogueBuilder(DialogueType.STATEMENT)
                .setText("You take " + NumberFormat.getIntegerInstance().format(originalAmount - dispenser.getAmount(bar)) + "x " + bar.getName() + " from the dispenser.").start(player);
        player.getInventory().refreshItems();

        if (dispenser.isEmpty()) {
            switchState(player, DispenserState.EMPTY);
        }
    }

    public static void updateVisual(Player player) {
        player.getPacketSender().sendConfig(936, player.getBlastFurnace().getBarDispenser().getState().ordinal());
    }

    public static void useBucket(Player player) {
        if (player.getBlastFurnace().getBarDispenser().isHot()) {
            switchState(player, DispenserState.COLD);
        }
        player.playSound(new Sound(1051));
    }

    public static Optional<GameObject> getObject() {
        return ClippedMapObjects.findObject(9092, new Position(1940, 4963, 0));
    }

    public static Pair<String, Consumer<Player>> getOption(BlastFurnaceBar bar) {
        return new Pair<>(bar.getName() + " Bar.", player -> collect(player, bar));
    }

    public static boolean isWearingIceGloves(Player player) {
        return player.getEquipment().contains(ItemID.ICE_GLOVES);
    }

    public static boolean isWearingGoldsmithGauntlets(Player player) {
        return player.getEquipment().contains(ItemID.GOLDSMITH_GAUNTLETS)
                || player.getEquipment().contains(ItemID.SMITHING_CAPE)
                || player.getEquipment().contains(ItemID.SMITHING_CAPE_T_)
                || player.getEquipment().contains(ItemID.MAX_CAPE)
                || player.getEquipment().contains(ItemID.AVAS_MAX_CAPE)
                || player.getEquipment().contains(ItemID.ARDOUGNE_MAX_CAPE)
                || player.getEquipment().contains(ItemID.FIRE_MAX_CAPE)
                || player.getEquipment().contains(ItemID.FIRE_MAX_CAPE_2)
                || player.getEquipment().contains(ItemID.GUTHIX_MAX_CAPE)
                || player.getEquipment().contains(ItemID.INFERNAL_MAX_CAPE)
                || player.getEquipment().contains(ItemID.SARADOMIN_MAX_CAPE)
                || player.getEquipment().contains(ItemID.ZAMORAK_MAX_CAPE)
                || player.getEquipment().contains(ItemID.MAX_CAPE_2)
                || player.getEquipment().contains(ItemID.MYTHICAL_MAX_CAPE)
                || player.getEquipment().contains(ItemID.MAX_CAPE_3);
    }
}
