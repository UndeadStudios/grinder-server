package com.grinder.game.content.minigame.warriorsguild.rooms.shotput;

import com.grinder.game.content.minigame.warriorsguild.npcs.Ref;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.model.*;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.model.projectile.ProjectileTemplateBuilder;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.oldgrinder.EquipSlot;

import java.util.Objects;

import static com.grinder.game.content.minigame.warriorsguild.rooms.shotput.ShotType.*;
import static com.grinder.util.NpcID.REF;

/**
 * @author L E G E N D
 **/
public final class ShotPut {

    static {
        ObjectActions.INSTANCE.onClick(new int[]{_22.objectId, _18.objectId}, action -> {
            var player = action.getPlayer();
            if (action.getType() == ObjectActions.ClickAction.Type.FIRST_OPTION) {
                var shot = find(player);
                if (!player.getPosition().equals(shot.position)) {
                    DialogueManager.sendStatement(player, "Please stand at the center of the Range!");
                    return false;
                }
                if (hasRequirements(player)) {
                    player.playSound(new Sound(1908));
                    player.performAnimation(new Animation(827));
                    TaskManager.submit(3, () ->  sendStylesDialogue(player));
                }
            }
            return true;
        });
        ItemActions.INSTANCE.onClick(new int[]{ItemID.GROUND_ASHES}, action -> {
            var player = action.getPlayer();
            if (isPlayerInRange(player)) {
                if (player.getEquipment().isSlotOccupied(EquipSlot.WEAPON)) {
                    DialogueManager.sendStatement(player, "To dust your hands you need them free of equipment!");
                    return true;
                }
                player.setBas(4185);
                player.updateAppearance();
              //  player.performAnimation(new Animation(689, 0));
                player.getWarriorsGuild().setHandsDusted(true);
                player.getInventory().delete(Objects.requireNonNull(action.getItem()));
                player.sendMessage("You dust your hands with the finely ground ash.");
                player.resetBas();
                player.updateAppearance();

            } else {
                DialogueManager.sendStatement(player, "You may only dust you hands when in the shotput throwing areas.");
            }

            return true;
        });
    }

    public static boolean isPlayerInRange(Player player) {
        var range_22 = new Boundary(2860, 2862, 3546, 3548);
        var range_18 = new Boundary(2860, 2862, 3552, 3554);
        return range_22.contains(player.getPosition()) || range_18.contains(player.getPosition()) && player.getZ() == 1;
    }

    public static void sendStylesDialogue(Player player) {
        player.setPositionToFace(player.getPosition().transform(1, 0, 0));
        var builder = new DialogueBuilder(DialogueType.OPTION).setOptionTitle("Choose your style");
        builder.firstOption("Standing throw", $ -> {
            player.playSound(new Sound(1907));
            player.performAnimation(new Animation(4181));
            sendProjectile(player);
        });
        builder.secondOption("Step and throw", $ -> {
            player.playSound(new Sound(1906));
            player.performAnimation(new Animation(4182));
            sendProjectile(player);
        });
        builder.thirdOption("Spin and throw", $ -> {
            player.playSound(new Sound(1905));
            player.performAnimation(new Animation(4183));
            sendProjectile(player);
        });
        builder.start(player);
    }

    public static void reduceRunEnergy(Player player) {
        player.setRunEnergy((int) (player.getRunEnergy() * 0.80));
    }

    public static void sendProjectile(Player player) {
        DialogueManager.start(player, -1);
        var strengthLevel = player.getSkills().getLevel(Skill.STRENGTH);
        var runEnergy = player.getRunEnergy();
        if (strengthLevel > 99) {
            strengthLevel = 99;
        }
        var maximumDistance = (int) ((runEnergy / 100.0) * strengthLevel / 80.25) + 1;
        var calculatedDistance = Misc.random(1, maximumDistance);
        var hittingRefereeChance = Misc.random(1, 2, 2, 3, 4);
        if (runEnergy <= 10) {
            calculatedDistance = 1;
        }
        final int distance = calculatedDistance;
        final int speed = distance == 1 ? 5 : 25;
        reduceRunEnergy(player);
        TaskManager.submit(new Task(4, false) {
            @Override
            protected void execute() {
                player.BLOCK_ALL_BUT_TALKING = true;
                var builder = new ProjectileTemplateBuilder(690);
                builder.setArrivalSound(new Sound(1901));
                builder.setCurve(30);
                builder.setStartHeight(50);
                builder.setEndHeight(10);
                builder.setSpeed(speed);
                builder.build();
                var template = builder.build();
                var startPosition = player.getPosition();
                var wereDustEffective = Misc.random(3) % 3 == 0;
                var distanceToAdd = distance % 6 + 1;
                while (distance + distanceToAdd > 13) {
                    distanceToAdd--;
                }
                var endPosition = player.getPosition().transform(distance + (wereDustEffective ? distanceToAdd : 0), 0, 0);
                if (hittingRefereeChance == 2) {
                    endPosition = getNearbyReferee(player).getPosition();
                }
                var projectile = new Projectile(startPosition, endPosition, template);
                projectile.sendProjectile();
                final var pos = endPosition;
                projectile.onArrival(() -> onArrival(player, pos));
                stop();
            }
        });
    }

    public static void sendFail(Player player) {
        player.say("Ouch!");
        sendDamage(player);
    }

    public static void sendDamage(Player player) {
        player.getCombat().queue(Damage.create(1));
    }

    public static Ref getNearbyReferee(Player player) {
        var npcId = player.getY() == 3547 ? 6074 : 6073;
        for (var npc : player.getLocalNpcs()) {
            if (npc.getId() == npcId)
                return (Ref) npc;
        }
        //Fail-Safe
        return new Ref(6074, new Position(2868, 3544, 1));
    }

    public static void onArrival(Player player, Position endPosition) {
        player.BLOCK_ALL_BUT_TALKING = false;
        var shot = ShotType.find(player);
        var distanceX = endPosition.getX() - player.getX();
        var distanceY = endPosition.getY() - player.getY();
        player.playSound(new Sound(1901));
        if (distanceY == 0) {
            ItemOnGroundManager.registerNonGlobal(player, new Item(shot.itemId), player.getPosition().transform(distanceX, 0, 0));
            if (distanceX <= 1) {
                sendFail(player);
                return;
            }
            sendShotDialogue(player, distanceX);
            int experience = distanceX * 6;
            if (experience > 200)
                experience = 205;
            if (distanceX > 40)
                distanceX = 40;
            player.getSkillManager().addExperience(Skill.STRENGTH, experience);
            player.getWarriorsGuild().addTokens(shot == _22 ? 3 + distanceX : 1 + distanceX);
            if (player.getWarriorsGuild().isHandsDusted()) {
                if (Misc.random(3) % 3 == 0) {
                    player.getWarriorsGuild().addTokens(shot == _22 ? (distanceX % 4) + 1 : (distanceX % 3) + 1);
                }
            }
        } else {
            var referee = getNearbyReferee(player);
            referee.say("Oi! " + player.getUsername() + ", Don't throw the ball outside the range!");
        }
    }

    public static void sendShotDialogue(Player player, int distance) {
        new DialogueBuilder(DialogueType.NPC_STATEMENT).setText("Well done. You threw the shot " + distance + " yards!").
                setNpcChatHead(REF).
                setStatementTitle(player.getY() == 3547 ? "22" : "18" + "lb Shot Put Referee").
                start(player);
    }

    public static void sendPickupItemDialogue(Player player) {
        new DialogueBuilder(DialogueType.NPC_STATEMENT).setText("Hey! You can't take that, it's guild property. Take one", "from the pile.").
                setNpcChatHead(REF).
                setStatementTitle(find(player).toString().replace("_", "") + "lb Shot Put Referee").
                start(player);
    }

    public static boolean hasRequirements(Player player) {
        if (!player.getEquipment().isSlotOccupied(EquipSlot.SHIELD) &&
                !player.getEquipment().isSlotOccupied(EquipSlot.WEAPON) &&
                !player.getEquipment().isSlotOccupied(EquipSlot.HANDS)) {
            return true;
        } else {
            DialogueManager.sendStatement(player, "To throw the shot you need your hands free!");
            return false;
        }
    }

    public static boolean isBall(int itemId) {
        return itemId == _22.itemId || itemId == _18.itemId;
    }
}
