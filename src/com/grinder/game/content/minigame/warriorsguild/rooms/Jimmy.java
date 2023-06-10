package com.grinder.game.content.minigame.warriorsguild.rooms;

import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.updating.block.BasicAnimationSet;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.ObjectActions;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.ObjectID;
import com.grinder.util.oldgrinder.EquipSlot;

import java.util.HashMap;
import java.util.Map;

import static com.grinder.util.ItemID.ONE_BARREL;

/**
 * @author L E G E N D
 */
public class Jimmy {



    private static final BasicAnimationSet BAS = new BasicAnimationSet(4179, 4178, 4178, 4178, 4178, 4178, 4178);
    private static final Animation KEG_FALL_ANIMATION = new Animation(4188);
    private static final Animation PICKING_UP_KEG_ANIMATION = new Animation(4180);
    private static final int[] OBJECTS = new int[]{ObjectID.JIMMY_BARREL_1, ObjectID.JIMMY_BARREL_2, ObjectID.JIMMY_BARREL_3, ObjectID.JIMMY_BARREL_4, ObjectID.JIMMY_BARREL_5};
    private static final int[] ITEMS = new int[]{ONE_BARREL, ItemID.TWO_BARRELS, ItemID.THREE_BARRELS, ItemID.FOUR_BARRELS, ItemID.FIVE_BARRELS};
    private static final Map<Integer, GameObject> kegs = new HashMap<>();
    static {
        ObjectActions.INSTANCE.onClick(OBJECTS, action -> {
            if (action.getType() == ObjectActions.ClickAction.Type.FIRST_OPTION) {
                pickupKeg(action.getPlayer(), action.getObject());
            }
            return true;
        });
    }

    private static void pickupKeg(Player player, GameObject object) {


        if (player.getEquipment().isSlotOccupied(EquipSlot.HAT) ||
                player.getEquipment().isSlotOccupied(EquipSlot.SHIELD) ||
                player.getEquipment().isSlotOccupied(EquipSlot.WEAPON)) {
            
            if (!player.getEquipment().atSlot(EquipSlot.HAT).getDefinition().getName().contains(" barrel")) {
                DialogueManager.sendStatement(player, "To balance kegs you will need your head and hands free!");
                return;
            }
        }
        if (player.getRunEnergy() <= 5) {
            DialogueManager.sendStatement(player, "You're too exhausted to continue. Take a break.");
            return;
        }
        if (!kegs.containsKey(object.getId())) {
            kegs.put(object.getId(), object);
        }
        player.performAnimation(PICKING_UP_KEG_ANIMATION);
        player.playSound(new Sound(1929));
        player.getWarriorsGuild().addTokens(2);
        ObjectManager.remove(object, true);
        player.getWarriorsGuild().getKegs().put(object.getId(), object);
        player.getEquipment().set(EquipSlot.HAT, new Item(ONE_BARREL - 1 + player.getWarriorsGuild().getKegs().size(), 1));
        player.getEquipment().refreshItems();
        player.getAppearance().setBas(BAS);
        player.updateAppearance();
        if (player.getWarriorsGuild().getKegsTask() != null) {
            player.getWarriorsGuild().getKegsTask().stop();
            player.getWarriorsGuild().setKegsTask(null);
        }

        player.getWarriorsGuild().setKegsTask(new Task(8) {
            @Override
            protected void execute() {
                var random = Misc.randomInclusive(0, 2) != 0;
                if (!random) {
                    sendKegFall(player);
                    stop();
                } else {
                    addExperience(player);
                    reduceRunEnergy(player);
                }
            }
        }.bind(player));

        TaskManager.submit(player.getWarriorsGuild().getKegsTask());
    }

    public static void reduceRunEnergy(Player player) {
        player.setRunEnergy((int) (player.getRunEnergy() * 0.92));
    }

    public static void addExperience(Player player) {
        player.getSkillManager().addExperience(Skill.STRENGTH, Misc.random(80, 100));
    }

    public static void sendKegFall(Player player) {
        player.performAnimation(KEG_FALL_ANIMATION);
        player.performGraphic(new Graphic(689 - player.getWarriorsGuild().getKegs().size()));
        player.getCombat().queue(Damage.create(2));
        player.playSound(new Sound(1920));
        player.say("Ouch!");
        reduceRunEnergy(player);
        reset(player);
    }

    public static void onLogout(Player player) {
        reset(player);
    }

    public static void reset(Player player) {
        if (player.getWarriorsGuild().getKegsTask() != null) {
            player.getWarriorsGuild().getKegsTask().stop();
            player.getWarriorsGuild().setKegsTask(null);
        }

        if (isHoldingKeg(player)) {
            if (player.getEquipment().isSlotOccupied(EquipSlot.HAT)) {
                player.getEquipment().delete(player.getEquipment().atSlot(EquipSlot.HAT), true);
            }

            player.resetBas();
            player.updateAppearance();
            TaskManager.submit(player, 1, () -> {
                for (var keg : player.getWarriorsGuild().getKegs().values()) {
                    ObjectManager.add(keg, true);
                }
                player.getWarriorsGuild().getKegs().clear();
            });
        }
    }

    public static boolean isHoldingKeg(Player player) {
        return player.getWarriorsGuild().getKegs().size() > 0;
    }

    public static boolean isKeg(int itemId) {
        for (var barrelId : ITEMS) {
            if (barrelId == itemId)
                return true;
        }
        return false;
    }
}
