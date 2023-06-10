package com.grinder.game.content.minigame.barrows;

import com.google.common.collect.ImmutableList;
import com.grinder.game.World;
import com.grinder.game.content.task_new.DailyTask;
import com.grinder.game.content.task_new.PlayerTaskManager;
import com.grinder.game.content.task_new.WeeklyTask;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.model.Position;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;

import java.util.Optional;

public final class BarrowsManager {

    private final Player player;

    private static final ImmutableList<Integer> BARROW_ITEMS = ImmutableList.of(4708, 4710, 4712, 4714, 4716, 4718, 4720, 4722, 4724, 4726, 4728, 4730,
            4732, 4734, 4736, 4738, 4745, 4747, 4749, 4751, 4753, 4755, 4757, 4759, 7462, 15370);

    private static final ImmutableList<Integer> RUNES = ImmutableList.of(558, 560, 562, 565, 556, 555, 557, 563, 561, 564, 566, 9075);

    public BarrowsManager(Player player) {
        this.player = player;
    }

    public static void load(){
//        final GameObject someObject = StaticGameObjectFactory.produce(ObjectID.CHEST_68, new Position(3551, 9695), 10, 0);
//        ObjectManager.add(someObject, true);
    }

    public boolean dig() {
        final Optional<BarrowsBrother> check = BarrowsBrother.getBarrowsBrothers().stream().filter(brother -> brother.getDig().inDig(player)).findFirst();
        check.ifPresent(brother -> {
            player.getPacketSender().sendMessage("You've found a crypt!");
            player.moveTo(brother.getCavePosition());
        });
        return check.isPresent();
    }

    public void spawn(BarrowsBrother brother, boolean chest) {
        if (player.isSpawnedBarrows()) {
            return;
        }
        if (brother == player.getFinalBarrow() && !chest) {
            player.setDialogueOptions(new DialogueOptions() {
                @Override
                public void handleOption(Player player, int option) {
                    if (option == 1) {
                        if (player.getKilledBarrows().size() >= 5) {
                            player.moveTo(new Position(3551, 9691).clone().add(Misc.getRandomInclusive(2), Misc.getRandomInclusive(1)));
                        } else {
                            player.sendMessage("You need a killcount of at least 5 to enter this tunnel.", 1000);
                        }
                    }
                    player.getPacketSender().sendInterfaceRemoval();
                }
            });
            DialogueManager.start(player, 26);
            return;
        }

        if (player.getKilledBarrows().stream().anyMatch(brother::equals)) {
            player.sendMessage("The sarcophagus appears to be empty.", 1000);
            return;
        }

        final Position spawnPosition = chest
                ? new Position(3553, 9691, 0)
                : brother.getSpawnPosition().clone();

        final NPC barrow = NPCFactory.INSTANCE.create(brother.getNpcId(), spawnPosition);

        World.getNpcAddQueue().add(barrow);

        barrow.setOwner(player);
        barrow.say(brother.getSpawnMessage());

        TaskManager.submit(2, () -> barrow.getCombat().initiateCombat(player));

        TaskManager.submit(new BarrowsTask(barrow, player));
        player.getPacketSender().sendEntityHint(barrow);
        player.setSpawnedBarrows(true);
    }

    public void handleChest(BarrowsBrother brother) {
        if (player.getKilledBarrows().size() < 5) {
            return;
        }
        if (player.getKilledBarrows().size() == 6) {
            //varbit for open chest
            player.getPacketSender().sendVarbit(1394, 1);
            player.getKilledBarrows().clear();
            player.setFinalBarrow(Misc.randomTypeOfList(BarrowsBrother.getBrothersList()));
            updateInterface();
            player.getPacketSender().sendCameraShake(3, 10, 3, 10);
            TaskManager.submit(4, () -> {
                player.moveTo(new Position(3564, 3288));
                player.getPacketSender().sendCameraNeutrality();
                handleReward();
                player.getPacketSender().sendVarbit(1394, 0);
            });
        } else if (player.getKilledBarrows().size() == 5) {
            spawn(brother, true);
        }
    }

    public void handleStairs(BarrowsBrother brother) {
        player.moveTo(brother.getHillPosition());
    }

    public void handleReward() {

        if (Misc.random(2) == 1) // 50% chance
        addItem(Misc.randomTypeOfList(BARROW_ITEMS), 1);

        if (Misc.getRandomInclusive(3) == 1) {
            addItem(Misc.randomTypeOfList(BARROW_ITEMS), 1);
            player.sendMessage("@red@Lucky! You have received a bonus barrows item!");
        }

        for (int i = 0; i < 3; i++) {
            addItem(Misc.randomTypeOfList(RUNES), Misc.randomInclusive(150, 1000));
        }

        addItem(ItemID.BOLT_RACK, Misc.randomInclusive(50, 400));
        player.getPoints().increase(AttributeManager.Points.BARROWS_CHEST);
        player.sendMessage("@or3@You've looted a total of " + player.getPoints().get(AttributeManager.Points.BARROWS_CHEST) + " chests.");
        //player.sendMessage("Your Barrows chest count is: @red@" + player.getPoints().get(AttributeManager.Points.BARROWS_CHEST) + ".");
        PlayerTaskManager.progressTask(player, DailyTask.BARROWS_CHESTS);
        PlayerTaskManager.progressTask(player, WeeklyTask.BARROWS_CHESTS);

    }

    private void addItem(int id, int amount) {
        final Inventory inventory = player.getInventory();
        if (inventory.countFreeSlots() <= 0 && !(inventory.contains(id) && ItemDefinition.forId(id).isStackable())) {
            ItemOnGroundManager.registerNonGlobal(player, new Item(id, amount));
        } else {
            inventory.add(id, amount);
        }
        player.getCollectionLog().createOrUpdateEntry(player,  "Barrows", new Item(id));
    }

    public void updateInterface() {
        player.getPacketSender().sendString(4536, "Killcount: " + player.getKilledBarrows().size());
    }
}