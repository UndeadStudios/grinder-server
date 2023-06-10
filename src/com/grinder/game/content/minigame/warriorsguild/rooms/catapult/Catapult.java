package com.grinder.game.content.minigame.warriorsguild.rooms.catapult;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.StaticGameObject;
import com.grinder.game.model.*;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.model.projectile.ProjectileTemplateBuilder;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.net.packet.impl.EquipPacketListener;
import com.grinder.util.Misc;
import com.grinder.util.ObjectID;
import com.grinder.util.oldgrinder.EquipSlot;

import java.util.Optional;

import static com.grinder.util.NpcID.GAMFRED;
import static com.grinder.util.ObjectID.DEFENSIVE_SHIELD;

/**
 * @author L E G E N D
 */
public class Catapult {

    private static final Position OBJECT_POSITION = new Position(2840, 3552, 1);
    private static final Position PROJECTILE_START_POSITION = new Position(2842, 3554, 1);
    private static final Position PROJECTILE_END_POSITION = new Position(2842, 3545, 1);

    private static CatapultAttackStyle currentAttack = CatapultAttackStyle.DEFAULT;
    private static CatapultAttackStyle lastAttack = currentAttack;
    private static boolean started;

    static {
        //Spawn Dynamic Game Object for the catapult
        ObjectManager.add(DynamicGameObject.createPublic(ObjectID.CATAPULT_DEFAULT, OBJECT_POSITION));

        ItemActionExtKt.onEquipAction(new int[]{ObjectID.DEFENSIVE_SHIELD}, action -> {
            var player = action.getPlayer();
            if (player.getPosition().equals(PROJECTILE_END_POSITION)) {
                if (!player.getEquipment().isSlotOccupied(EquipSlot.WEAPON)) {
                    EquipPacketListener.equip(action);
                    Catapult.sendAttackStylesInterface(action.getPlayer());
                } else {
                    new DialogueBuilder(DialogueType.STATEMENT).
                            setText("You will need to make sure your sword hard is free to equip this", "shield.").
                            start(player);
                }
            } else {
                player.sendMessage("You may not equip this shield outside the target area in the Warrior's Guild.");
            }
            return null;
        });
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.INFORMATION_SCROLL_3}, action -> {
            action.getPlayer().getPacketSender().sendInterface(58500);
            return true;
        });
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.INFORMATION_SCROLL_2}, action -> {
            action.getPlayer().getPacketSender().sendInterface(58530);
            return true;
        });
        ButtonActions.INSTANCE.onClick(new int[]{58524, 58525, 58526, 58527}, true, action ->
        {
            selectCatapultStyle(action.getPlayer(), CatapultAttackStyle.values()[action.getId() - 58524 + 1]);
            return null;
        });
        TaskManager.submit(1, Catapult::start);
    }

    public static void start() {
        if (started) {
            return;
        }
        started = true;
        sendAttack();
    }

    public static void selectCatapultStyle(Player player, CatapultAttackStyle style) {
        player.getWarriorsGuild().setSelectedCatapultAttackStyle(style);
        player.getPacketSender().sendConfig(751, style.ordinal() - 1);
    }

    public static void sendAttack() {
        var randomIndex = 0;
        lastAttack = currentAttack;
        while (CatapultAttackStyle.values()[1 + randomIndex] == lastAttack) {
            randomIndex = Misc.random(CatapultAttackStyle.values().length - 2);
        }
        currentAttack = CatapultAttackStyle.values()[++randomIndex];
        replaceCatapult(currentAttack);
        sendProjectile(currentAttack);
    }

    public static void sendProjectile(CatapultAttackStyle style) {
        TaskManager.submit(new Task(3) {
            @Override
            protected void execute() {
                var builder = new ProjectileTemplateBuilder(style.projectileId);
                builder.setCurve(20);
                builder.setStartHeight(100);
                builder.setEndHeight(30);
                builder.setSpeed(188);
                builder.build();

                var template = builder.build();
                var projectile = new Projectile(PROJECTILE_START_POSITION, PROJECTILE_END_POSITION, template);

                projectile.sendProjectile();
                replaceCatapult(CatapultAttackStyle.DEFAULT);
                projectile.onArrival(() -> onArrival(style));
                stop();
            }
        });
    }

    private static void onArrival(CatapultAttackStyle style) {
        Boundary boundary = new Boundary(2837, 2847, 3542, 3556);
        var players = AreaManager.getPlayers(boundary);

        for (var player : players) {
            if (player.getPosition().equals(PROJECTILE_END_POSITION)) {
                if (!isShieldEquipped(player)) {
                    sendWarning(player);
                } else {
                    if (player.getWarriorsGuild().getSelectedCatapultAttackStyle() == style) {
                        player.performAnimation(style.defenceAnimation);
                        onSuccess(player);
                    } else {
                        player.performAnimation(style.attackAnimation);
                    }
                }
            }
            if (player.getZ() == 1) {
                player.playSound(style.sound);
            }
        }
        lastAttack = style;
        sendAttack();
    }

    public static void unEquipShield(Player player) {
        player.getEquipment().delete(DEFENSIVE_SHIELD, 1, true);
        ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(DEFENSIVE_SHIELD));
        player.getPacketSender().sendTabs();
        player.updateAppearance();
    }

    public static void onSuccess(Player player) {
        player.getSkillManager().addExperience(Skill.DEFENCE, 10);
        player.getWarriorsGuild().addTokens(2);
    }

    public static void onLogout(Player player) {
        if (isShieldEquipped(player)) {
            unEquipShield(player);
        }
    }

    public static void sendWarning(Player player) {
        var randomDirection = Direction.getDirection(Misc.random(1, 7));
        var randomPosition = new Position(randomDirection.getX(), randomDirection.getY());
        player.setForceMovement(new ForceMovement(player.getPosition(), randomPosition, 4, 4, randomDirection.getForceMovementMask(), 819));
       TaskManager.submit(3 ,()->{
           player.moveTo(player.getPosition().transform(randomPosition.getX(), randomDirection.getY(), 0));
           player.setForceMovement(null);
       });

        new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(GAMFRED).setText(
                "Watch out! You'll need to equip the shield as soon as",
                "you're on the target spot else you could get hit! Speak",
                "to me to get one, and make sure both your hands are",
                "free to equip it.").start(player);
    }

    public static void sendAttackStylesInterface(Player player) {
        player.getPacketSender().sendTabInterface(4, 58514); // tab interface
        player.getPacketSender().sendTab(4);
    }

    public static void replaceCatapult(CatapultAttackStyle style) {
        getCurrentCatapultObject().ifPresent(object -> {
            ObjectManager.remove(object, false);
            ObjectManager.add(DynamicGameObject.createPublic(style.objectId, object.getPosition(), object.getObjectType(), object.getFace()), true);
        });
    }

    public static boolean inside(Player player) {
        Boundary boundary = new Boundary(2837, 2847, 3542, 3556);
        var players = AreaManager.getPlayers(boundary);
        for (var p : players) {
            if (p == player) {
                if (p.getZ() == 1)
                    return true;
            }
        }
        return false;
    }

    public static Optional<GameObject> getCurrentCatapultObject() {
        return ObjectManager.findDynamicObjectAt(OBJECT_POSITION);
    }

    public static boolean isShieldEquipped(Player player) {
        return player.getEquipment().contains(DEFENSIVE_SHIELD);
    }
}
