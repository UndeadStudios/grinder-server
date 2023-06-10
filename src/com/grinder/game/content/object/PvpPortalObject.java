package com.grinder.game.content.object;

import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.ObjectActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.timing.TimerKey;

import java.util.concurrent.TimeUnit;

public class PvpPortalObject {

    static {

        ObjectActions.INSTANCE.onClick(new int[]{26739}, clickAction -> {

            final Player player = clickAction.getPlayer();
            final ObjectActions.ObjectActionDetails message = clickAction.getObjectActionMessage();
            final int objectX = message.getX();
            final int objectY = message.getY();

            if (!PlayerUtil.isDeveloper(player)) {
                player.sendMessage("This minigame event is still under construction and is currently unavailable.");
                return false;
            }
            if (!canUsePortal(player))
                return true;

            player.BLOCK_ALL_BUT_TALKING = true;
            EntityExtKt.markTime(player, Attribute.LAST_GODZILLA_ENTRY);

            player.getMotion().enqueuePathToWithoutCollisionChecks(objectX, objectY);

            TaskManager.submit(player, 3, () -> {
                player.BLOCK_ALL_BUT_TALKING = false;
                if (Misc.random(2) == 1) {
                    TeleportHandler.teleport(player, new Position(3431 + Misc.random(3), 4703), TeleportType.NORMAL, false, true);
                } else {
                    TeleportHandler.teleport(player, new Position(3412 + Misc.random(3), 4703), TeleportType.NORMAL, false, true);
                }
                SkillUtil.stopSkillable(player);
            });
            return true;
        });

        ObjectActions.INSTANCE.onClick(new int[]{26740}, clickAction -> {

            final Player player = clickAction.getPlayer();
            final ObjectActions.ObjectActionDetails message = clickAction.getObjectActionMessage();
            final int objectX = message.getX();
            final int objectY = message.getY();

            player.BLOCK_ALL_BUT_TALKING = true;

            player.getMotion().enqueueStepsTo(new Position(objectX, objectY, 0));

            TaskManager.submit(player, 3, () -> {
                player.BLOCK_ALL_BUT_TALKING = false;
                if (Misc.random(3) == 1) {
                    TeleportHandler.teleport(player, new Position(3424 + Misc.random(8), 4741 + Misc.random(6)), TeleportType.NORMAL, false, true);
                } else if (Misc.random(3) == 2) {
                    TeleportHandler.teleport(player, new Position(3411 + Misc.random(7), 4786 + Misc.random(4)), TeleportType.NORMAL, false, true);
                } else if (Misc.random(5) == 3) {
                    TeleportHandler.teleport(player, new Position(3440 + Misc.random(8), 4778 + Misc.random(6)), TeleportType.NORMAL, false, true);
                } else if (Misc.random(3) == 3) {
                    TeleportHandler.teleport(player, new Position(3430 + Misc.random(15), 4765 + Misc.random(4)), TeleportType.NORMAL, false, true);
                } else {
                    TeleportHandler.teleport(player, new Position(3400 + Misc.random(17), 4738 + Misc.random(10)), TeleportType.NORMAL, false, true);
                }
                SkillUtil.stopSkillable(player);
            });
            return true;
        });

    }
    
    public static boolean canUsePortal(Player player){
        // oh also u can do this for a while
        if (!EntityExtKt.passedTime(player, Attribute.LAST_GODZILLA_ENTRY, 5, TimeUnit.MINUTES, false, false)) {
            player.sendMessage("You can only use this portal once every five minutes!");
            return false;
        }
        if (player.getCombat().isInCombat() || player.getCombat().isUnderAttack()) {
            player.sendMessage("You can't use this portal while in combat.");
            return false;
        }
        if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
            player.sendMessage("You can't use this portal while being away from keyboard.", 1000);
            return false;
        }
        if (player.getStatus() == PlayerStatus.TRADING) {
            player.sendMessage("You can't use this portal while trading!", 1000);
            return false;
        }
        if (player.getStatus() == PlayerStatus.DUELING) {
            player.sendMessage("You can't use this portal while dueling!", 1000);
            return false;
        }
        if (AreaManager.inWilderness(player)) {
            player.sendMessage("You can't use this portal in the Wilderness");
            return false;
        }
        if (player.getTimerRepository().has(TimerKey.FREEZE)) {
            player.sendMessage("You can't use this portal while frozen.");
            return false;
        }
        if (player.getTimerRepository().has(TimerKey.COMBAT_COOLDOWN)) {
            player.sendMessage("You can't use this portal while in combat.");
            return false;
        }
        if (player.getCombat().isInCombat()) {
            player.sendMessage("You must wait 10 seconds after being out of combat to use this portal.", 1000);
            return false;
        }
        if (player.getTimerRepository().has(TimerKey.STUN)) {
            player.sendMessage("You can't use this portal while stunned.");
            return false;
        }
        if (!player.getCombat().getTeleBlockTimer().finished()) {
            player.sendMessage("You can't use this portal while tele blocked.");
            return false;
        }
        return true;
    }

}
