package com.grinder.net.packet.impl;

import com.grinder.game.World;
import com.grinder.game.content.dueling.DuelRule;
import com.grinder.game.content.item.MorphItems;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.movement.task.WalkToAction;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.util.timing.TimerKey;

/**
 * Handles the follow player packet listener Sets the player to follow when the
 * packet is executed
 *
 * @author Gabriel Hannason
 */
public class FollowPlayerPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {

        int otherPlayersIndex = packetReader.readLEShort();

        if (otherPlayersIndex < 0 || otherPlayersIndex > World.getPlayers().capacity())
            return;
        if (player.isTeleporting() && player.getTeleportingType() == TeleportType.HOME) {
            player.stopTeleporting();
        }
        if (player.busy()) {
            return;
        }
        if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
            return;
        }
        if (player.BLOCK_ALL_BUT_TALKING) {
            return;
        }
        if (player.isInTutorial()) {
            return;
        }
        if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
            return;
        }

        if (!MorphItems.INSTANCE.notTransformed(player, "do this", true, true))
            return;

        if (player.getTimerRepository().has(TimerKey.FREEZE)) {
            player.getPacketSender().sendMessage("A magical spell has made you unable to move.", 1000);
            return;
        }

        // Duel, disabled movement?
        if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_MOVEMENT.ordinal()]) {
            DialogueManager.sendStatement(player, "Movement has been disabled in this duel!");
            return;
        }

        // Stun
        if (player.getTimerRepository().has(TimerKey.STUN)) {
            player.getPacketSender().sendMessage("You're stunned!", 1000);
            return;
        }

        if (player.hasPendingTeleportUpdate() || !player.getMotion().canMove() || !player.getMotion().canMove()) {
            player.getPacketSender().sendMinimapFlagRemoval();
            return;
        }

        Player leader = World.getPlayers().get(otherPlayersIndex);
        if (leader == null)
            return;
        SkillUtil.stopSkillable(player);
        player.setEntityInteraction(leader);
        player.setWalkToTask(new WalkToAction<>(player, leader.followTarget, 0, () -> {
            player.setEntityInteraction(leader);
            player.getMotion().followTarget(leader, true, false);
        }, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES, WalkToAction.Policy.ALLOW_UNDER, WalkToAction.Policy.EXECUTE_WHEN_IN_DISTANCE));
    }

}
