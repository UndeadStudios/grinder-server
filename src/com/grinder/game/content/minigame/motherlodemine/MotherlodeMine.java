package com.grinder.game.content.minigame.motherlodemine;

import com.google.gson.annotations.Expose;
import com.grinder.game.World;
import com.grinder.game.content.minigame.motherlodemine.sack.Sack;
import com.grinder.game.content.minigame.motherlodemine.sack.SackType;
import com.grinder.game.content.minigame.motherlodemine.wheel.Wheel;
import com.grinder.game.content.minigame.motherlodemine.wheel.WheelState;
import com.grinder.game.content.skill.skillable.impl.Mining;
import com.grinder.game.content.skill.skillable.impl.hunter_new.Hunter;
import com.grinder.game.content.skill.skillable.impl.hunter_new.traps.HunterTrap;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.object.ClippedMapObjects;
import com.grinder.game.model.*;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.*;

import java.util.*;

import static com.grinder.util.NpcID.PROSPECTOR_PERCY;
import static com.grinder.util.ObjectID.DARK_TUNNEL_2;

/**
 * @author L E G E N D
 * @date 2/9/2021
 * @time 2:53 AM
 * @discord L E G E N D#4380
 */
public final class MotherlodeMine {

    private static final Set<Position> PICKAXE_CRATES = new HashSet<>(Arrays.asList(
            new Position(3753, 5659, 0), new Position(3755, 5665, 0), new Position(3758, 5671, 0)));
    private static final Set<Position> HAMMER_CRATES = new HashSet<>(Arrays.asList(
            new Position(3752, 5664, 0), new Position(3755, 5660, 0),
            new Position(3756, 5668, 0), new Position(3752, 5674, 0)));

    private static final Map<Player, PayDirtNpc> NPCS = new HashMap<>();

    public MotherlodeMine() {
        this.sack = new Sack();
    }

    @Expose
    private boolean restrictedAreaUnlocked;
    @Expose
    private boolean upperFloor;
    @Expose
    private int oresInMachine;
    @Expose
    private Sack sack;

    static {
        Hunter.configure();
        // ENTRANCE FROM DWARVES MINE
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.CAVE_49}, action -> {
            var player = action.getPlayer();
            player.performAnimation(new Animation(2796, 25));
            player.playSound(new Sound(Sounds.CRAWL_THROUGH_TUNNEL));
            player.BLOCK_ALL_BUT_TALKING = true;
            TaskManager.submit(3, () -> {
                    player.moveTo(new Position(3728, 5692, 0));
                    player.resetAnimation();
                    player.BLOCK_ALL_BUT_TALKING = false;
            });
            return true;
        });
        // EXIT TO DWARVES MINE
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.TUNNEL_41}, action -> {
            var player = action.getPlayer();
            player.performAnimation(new Animation(2796, 25));
            player.playSound(new Sound(Sounds.CRAWL_THROUGH_TUNNEL));
            player.BLOCK_ALL_BUT_TALKING = true;
            TaskManager.submit(3, () -> {
                    player.moveTo(new Position(3060, 9766, 0));
                    player.resetAnimation();
                    player.BLOCK_ALL_BUT_TALKING = false;
            });
            return true;
        });

        // ENTRANCE FROM MINING GUILD
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.CAVE_54}, action -> {
            var player = action.getPlayer();
            player.performAnimation(new Animation(2796, 25));
            player.playSound(new Sound(Sounds.CRAWL_THROUGH_TUNNEL));
            player.BLOCK_ALL_BUT_TALKING = true;
            TaskManager.submit(3, () -> {
                player.moveTo(new Position(3718, 5678, 0));
                player.resetAnimation();
                player.BLOCK_ALL_BUT_TALKING = false;
            });
            return true;
        });

        // EXIT TO MINING GUILD
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.TUNNEL_46}, action -> {
            var player = action.getPlayer();
            if (player.getSkillManager().getCurrentLevel(Skill.MINING) < 60) {
                new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.DWARF_7721)
                        .setText("Sorry, but you're not experienced enough to go", "through there.")
                        .setNext(new DialogueBuilder(DialogueType.STATEMENT)
                                .setText("You need a Mining level of 60 to access the Mining Guild."))
                        .start(player);

            } else {
                player.performAnimation(new Animation(2796, 25));
                player.playSound(new Sound(Sounds.CRAWL_THROUGH_TUNNEL));
                player.BLOCK_ALL_BUT_TALKING = true;
                TaskManager.submit(3, () -> {
                    player.moveTo(new Position(3054, 9744, 0));
                    player.resetAnimation();
                    player.BLOCK_ALL_BUT_TALKING = false;
                });
            }
            return true;
        });

        //TUNNELS (AGILITY)
        ObjectActions.INSTANCE.onClick(new int[]{DARK_TUNNEL_2}, action -> {

            var player = action.getPlayer();
            if (player.getSkillManager().getCurrentLevel(Skill.AGILITY) >= 54) {
                player.performAnimation(new Animation(2796, 25));
                player.playSound(new Sound(Sounds.CRAWL_THROUGH_TUNNEL));
                player.BLOCK_ALL_BUT_TALKING = true;
                TaskManager.submit(1, () ->
                {
                    player.moveTo((player.getX() >= 3764) ? new Position(3759, 5670, 0) : new Position(3765, 5671, 0));
                    player.resetAnimation();
                    player.BLOCK_ALL_BUT_TALKING = false;
                });

            } else {
                new DialogueBuilder(DialogueType.STATEMENT).setText("It gets quite narrow further inside that tunnel.",
                        "you'd better have an agility level of 54 before entering").start(player);
            }
            return true;
        });

        // FIXING WHEEL
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.BROKEN_STRUT}, action -> {
            var wheel = Wheel.get(action.getObject());
            if (wheel != null) {
                wheel.fix(action.getPlayer());
            }
            return true;
        });
        // ROCKFALL MINING
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.ROCKFALL, ObjectID.ROCKFALL_2, ObjectID.ROCKFALL_3}, action -> {
            Rockfall.get(action.getObject()).mine(action.getPlayer());
            return true;
        });
        //CRATES
        ObjectActions.INSTANCE.onClick(new int[]{357}, action -> {
            var position = action.getObject().getPosition();
            var player = action.getPlayer();
            if (HAMMER_CRATES.contains(position)) {
                takeHammer(player);
                return true;
            } else if (PICKAXE_CRATES.contains(position)) {
                takePickaxe(player);
                return true;
            }
            return false;
        });
        //ADD TO HOPPER
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.HOPPER_27}, action -> {
            addToHopper(action.getPlayer());
            return true;
        });
        //COLLECT FROM SACK
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.EMPTY_SACK_2}, action -> {
            Sack.collectOres(action.getPlayer());
            return true;
        });

        //GOING UP THE LADDER
        ObjectActions.INSTANCE.onClick(new int[]{19044}, action -> {
            var player = action.getPlayer();
            if (player.getSkills().getLevel(Skill.MINING) >= 72) {
                if (player.getMotherlodeMine().isRestrictedAreaUnlocked()) {
                    player.performAnimation(new Animation(828, 15));
                    player.BLOCK_ALL_BUT_TALKING = true;
                    TaskManager.submit(2, () -> {
                        player.moveTo(new Position(3755, 5675, 0));
                        player.resetAnimation();
                        player.BLOCK_ALL_BUT_TALKING = false;
                        player.getMotherlodeMine().setUpperFloor(true);
                    });

                } else {
                    new DialogueBuilder(DialogueType.STATEMENT)
                            .setText("You need to unlock the upper floor by paying 100",
                                    "nuggets before you're allowed there.")
                            .start(player);
                }

            } else {
                new DialogueBuilder(DialogueType.NPC_STATEMENT)
                        .setNpcChatHead(PROSPECTOR_PERCY)
                        .setText("Ye wants to go up there, eh? well, ye'll need level 72",
                                "Mining first. An' don't think ye can fool me with yer",
                                "potions and fancy stat-boosts. Get yer level up for real.")
                        .start(player);
            }

            return true;
        });

        //GOING DOWN THE LADDER
        ObjectActions.INSTANCE.onClick(new int[]{19049}, action -> {
            var player = action.getPlayer();
            player.performAnimation(new Animation(827, 15));
            player.BLOCK_ALL_BUT_TALKING = true;
            TaskManager.submit(2, () -> {
                player.moveTo(new Position(3755, 5672, 0));
                player.resetAnimation();
                player.BLOCK_ALL_BUT_TALKING = false;
                player.getMotherlodeMine().setUpperFloor(false);
            });


            return true;
        });

        //MINING THE VIENS
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.ORE_VEIN_29, ObjectID.ORE_VEIN_30, ObjectID.ORE_VEIN_31, ObjectID.ORE_VEIN_32}, action -> {
            mineVien(action.getPlayer(), PayDirtObject.get(action.getObject()));
            return true;
        });

        ItemActions.INSTANCE.onClick(new int[]{ItemID.SOFT_CLAY_PACK}, action -> {
            if (action.isFirstAction()) {
                var player = action.getPlayer();
                player.getInventory().replace(action.getItem(), new Item(ItemID.SOFT_CLAY_2, 100));
                player.getInventory().refreshItems();
            }
            return true;
        });

        ItemActions.INSTANCE.onClick(new int[]{ItemID.BAG_FULL_OF_GEMS}, action -> {
            if (action.isFirstAction() && action.getItem() != null) {
                var player = action.getPlayer();
                if (player.getInventory().countFreeSlots() < 6) {
                    player.sendMessage("No enough space in Inventory.");
                    return true;
                }
                player.getInventory().delete(action.getItem(), true);
                for (var index = 0; index < 40; index++) {
                    var item = BagFullOfGems.roll();
                    player.getInventory().add(new Item(item.getItemId()), true);
                }
                player.getInventory().refreshItems();
            }
            return true;
        });
    }

    public Sack getSack() {
        if (sack == null) {
            sack = new Sack();
        }
        return sack;
    }


    public int getOresInMachine() {
        return oresInMachine;
    }

    public boolean isRestrictedAreaUnlocked() {
        return restrictedAreaUnlocked;
    }

    public void unlockRestrictedArea() {
        restrictedAreaUnlocked = true;
    }

    public boolean isAtUpperFloor() {
        return upperFloor;
    }

    public void setUpperFloor(boolean value) {
        upperFloor = value;
    }

    public static void onEnter(Player player) {
        updateInterface(player);
        if (player.getMotherlodeMine().getOresInMachine() > 0) {
            addNpc(player);
        }
    }

    public static void onLeave(Player player) {
        player.getPacketSender().sendWalkableInterface(-1);
        if (NPCS.containsKey(player)) {
            removeNpc(player);
        }
    }

    public static void takePickaxe(Player player) {
        if (player.getInventory().contains(ItemID.BRONZE_PICKAXE)) {
            player.sendMessage("You search the crate and find nothing.");
            return;
        }

        if (player.getInventory().countFreeSlots() == 0) {
            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(ItemID.BRONZE_PICKAXE, 200)
                    .setText("The crate contains a bronze pickaxe, but you don't", "have space to take it.")
                    .start(player);

        } else {
            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(ItemID.BRONZE_PICKAXE, 200)
                    .setText("You've found a bronze pickaxe. How handy.")
                    .setPostAction($ -> player.getInventory().add(ItemID.BRONZE_PICKAXE, 1))
                    .start(player);
        }
    }

    private static void takeHammer(Player player) {
        if (player.getInventory().contains(ItemID.HAMMER)) {
            player.sendMessage("You search the crate and find nothing.");
            return;
        }
        if (player.getInventory().countFreeSlots() == 0) {
            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(ItemID.HAMMER, 200)
                    .setText("The crate contains a hammer, but you don't have space", "to take it.")
                    .start(player);
        } else {
            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(ItemID.HAMMER, 200)
                    .setText("You've found a hammer. How handy.")
                    .setPostAction($ -> player.getInventory().add(ItemID.HAMMER, 1))
                    .start(player);
        }
    }

    public static void updateWheels() {
        if (getFirstWheel().isBroken() && getSecondWheel().isBroken()) {
            stopMoving();
            return;
        }
        continueMoving();
    }

    public static void continueMoving() {
        for (var npc : NPCS.values()) {
            var player = npc.getOwner();
            if (MotherlodeMine.isMachineBusy(player) && player.getMotherlodeMine().getSack().isSackFull()) {
                player.sendMessage("The machine can't clean your pay-dirt.");
                continue;

            }
            npc.startMoving();
        }
    }

    public static void stopMoving() {
        for (var npc : NPCS.values()) {
            npc.stopMoving();
        }
    }

    public static void mineVien(Player player, PayDirtObject object) {
        if (Mining.findPickaxe(player).isEmpty()) {
            new DialogueBuilder(DialogueType.STATEMENT)
                    .setText("You need a pickaxe to mine this rock. You do not have a pickaxe",
                            "which you have the Mining level to use.")
                    .start(player);
            return;
        }
        if (player.getSkillManager().getSkills().getLevel(Skill.MINING) < 30) {
            new DialogueBuilder(DialogueType.STATEMENT).setText("You need a Mining level of 30 to mine this rock.")
                    .start(player);
            return;
        }
        if (player.getInventory().countFreeSlots() == 0) {
            new DialogueBuilder(DialogueType.STATEMENT).setText("Your inventory is too full to hold any more pay-dirt.")
                    .start(player);
            return;
        }
        final var positionWhenClicked = player.getPosition();
        if (player.getMotherlodeMine().isAtUpperFloor()) {
            object.setType(PayDirtObjectType.TIME);
        }
        var pickaxe = Mining.findPickaxe(player).get();
        player.performAnimation(pickaxe.getSecondAnimation());
        player.sendMessage("You swing your pick at the rock.");

        var attemptsTillMine = new AtomicInteger(Misc.random(-3, 3));
        if (attemptsTillMine.getValue() <= 0) {
            attemptsTillMine.setValue(1);
        }
        var level = player.getSkillManager().getCurrentLevel(Skill.MINING);
        var pickaxeSpeed = pickaxe.getSpeed();
        var factor = (int) (level / pickaxeSpeed / 25);
        var miningSpeed = Misc.random(2, factor);
        TaskManager.submit(new Task(miningSpeed) {
            @Override
            protected void execute() {
                if (!player.getPosition().equals(positionWhenClicked)) {
                    stop();
                    return;
                }
                if (attemptsTillMine.getValue() == 0) {
                    player.getPacketSender().sendAreaPlayerSound(Sounds.ROCK_MINED_SOUND);
                    player.sendMessage("You manage to mine some pay-dirt.");
                    var xp = 60 + (60 * getProspectorBonus(player));
                    player.getSkillManager().addExperience(Skill.MINING, xp);
                    player.getInventory().add(new PayDirtItem(player), true);
                    object.check();
                    player.resetAnimation();
                    if (!object.isDepleting()) {
                        mineVien(player, object);
                    }
                    stop();
                } else {
                    player.performAnimation(pickaxe.getSecondAnimation());
                    attemptsTillMine.remove(1);
                }
            }
        }.bind(player));
    }

    @SuppressWarnings("unused")
    public static void detectAllViens() {
        for (var region : ClippedMapObjects.mapObjects.values()) {
            if (region.isEmpty())
                return;
            for (var object : region) {
                var objectId = object.getId();
                var objectPosition = object.getPosition();
                var objectFace = object.getFace();
                if (AreaManager.inside(object.getPosition(), AreaManager.MOTHERLODE_MINE_AREA)) {
                    if (PayDirtObject.isPayDirt(objectId)) {
                        System.out.println("{");
                        System.out.println("\t\"face\": " + objectFace + ",");
                        System.out.println("\t\"type\": 0,");
                        System.out.println("\t\"id\": " + (objectId - 4) + ",");
                        System.out.println("\t\"position\": {");
                        System.out.println("\t\t\"x\": " + objectPosition.getX() + ",");
                        System.out.println("\t\t\"y\": " + objectPosition.getY() + ",");
                        System.out.println("\t\t\"z\": " + objectPosition.getZ() + "");
                        System.out.println("\t}");
                        System.out.println("},");
                    }
                }
            }
        }
    }

    public static void updateInterface(Player player) {
        if (player.getMotherlodeMine().getSack().isSackFull()) {
            player.getPacketSender().sendStringColour(58562, 0xFF0000);
        } else {
            player.getPacketSender().sendStringColour(58562, 0xFFFFFF);
        }
        player.getPacketSender().sendString(58562, player.getMotherlodeMine().getSack().getAmountInSack() + "");
        player.getPacketSender().sendWalkableInterface(58560);
    }

    public static void addToHopper(Player player) {
        if (NPCS.containsKey(player) || isMachineBusy(player)) {
            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(ItemID.PAY_DIRT, 200)
                    .setText("You've already got some pay-dirt in the machine.", "You can put more in once the last batch come out.")
                    .start(player);
            return;
        }
        if (player.getMotherlodeMine().getSack().isSackFull()) {
            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(ItemID.PAY_DIRT, 200)
                    .setText("The sack is getting full. You should empty it before", "putting any more pay-dirt into the machine.")
                    .start(player);
            return;
        }
        // Safe Check in case the player becomes a member after unlocking the bigger sack.
        if (PlayerUtil.isMember(player) && player.getMotherlodeMine().getSack().getSackType() == SackType.UPGRADED) {
            player.getMotherlodeMine().getSack().setSackType(SackType.MEMBER);
        }
        var amountInInventory = player.getInventory().getAmount(ItemID.PAY_DIRT);
        if (amountInInventory == 0) {
            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(ItemID.PAY_DIRT, 200)
                    .setText("You don't have any pay-dirt to put in the hopper.")
                    .start(player);
            return;
        }
        if (!MotherlodeMine.isActive()) {
            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(ItemID.PAY_DIRT, 200)
                    .setText("The machine will need to be repaired before your pay-", "dirt can be cleaned.")
                    .start(player);
        }
        addNpc(player);
        player.performAnimation(new Animation(832, 25));
        player.playSound(new Sound(2496));
        player.getInventory().delete(ItemID.PAY_DIRT, amountInInventory);
        addOresToMachine(player, amountInInventory);
    }

    public static void addOresToMachine(Player player, int amount) {
        if (player.getMotherlodeMine().oresInMachine + amount > 28) {
            return;
        }
        player.getMotherlodeMine().oresInMachine += amount;
    }

    public static void addNpc(Player player) {
        NPCS.put(player, PayDirtNpc.create(player));
        if (isActive()) {
            NPCS.get(player).startMoving();
        }
    }

    public static void removeNpc(Player player) {
        World.getNpcRemoveQueue().add(NPCS.get(player));
        NPCS.remove(player);
    }

    public static Wheel getFirstWheel() {
        return Wheel.get(new Position(3743, 5668, 0));
    }

    public static Wheel getSecondWheel() {
        return Wheel.get(new Position(3743, 5662, 0));
    }

    public static WheelState getWheelsState() {
        if (getFirstWheel().isRunning() || getSecondWheel().isRunning()) {
            return WheelState.RUNNING;
        }
        return WheelState.BROKEN;
    }

    public static boolean isActive() {
        return getFirstWheel().getCurrentState() == WheelState.RUNNING || getSecondWheel().getCurrentState() == WheelState.RUNNING;
    }

    public static boolean isMachineBusy(Player player) {
        return player.getMotherlodeMine().getOresInMachine() > 0;
    }

    public static void resetOresInMachine(Player player) {
        player.getMotherlodeMine().oresInMachine = 0;
    }

    public static double getProspectorBonus(Player player) {
        var value = 0.0;
        if (player.getEquipment().contains(ItemID.PROSPECTOR_HELMET)) {
            value += 0.4;
        }
        if (player.getEquipment().contains(ItemID.PROSPECTOR_JACKET) || player.getEquipment().contains(ItemID.VARROCK_ARMOUR_4)) {
            value += 0.8;
        }
        if (player.getEquipment().contains(ItemID.PROSPECTOR_LEGS)) {
            value += 0.6;
        }
        if (player.getEquipment().contains(ItemID.PROSPECTOR_BOOTS)) {
            value += 0.2;
        }
        return value;
    }
}
