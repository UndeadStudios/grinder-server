package com.grinder.net.packet.impl;

import com.grinder.game.World;
import com.grinder.game.content.item.MorphItems;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.movement.task.WalkToAction;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.message.decoder.PlayerOptionMessageDecoder;
import com.grinder.game.message.impl.PlayerOptionMessage;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.net.packet.PacketConstants;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;

/**
 * This packet listener is called when a player has clicked on another player's
 * menu actions.
 *
 * @author relex lawl
 */

public class PlayerOptionPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {

        final PlayerOptionMessageDecoder playerOptionMessageDecoder = new PlayerOptionMessageDecoder();
        final PlayerOptionMessage playerOptionMessage = playerOptionMessageDecoder.decode(packetReader.getPacket());

        if (player == null || player.getHitpoints() <= 0)
            return;

        if (player.busy())
            return;

        if (player.BLOCK_ALL_BUT_TALKING)
            return;

        if (player.isTeleporting())
            return;

        if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD)
            return;

        if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false) )
            return;

        final int otherIndex = playerOptionMessage.getIndex();

        if (otherIndex > World.getPlayers().capacity() || otherIndex < 0)
            return;

        final Player other = World.getPlayers().get(otherIndex);

        if (other == null)
            return;

        if (player.isInTutorial() || other.isInTutorial())
            return;

        SkillUtil.stopSkillable(player);

        if (!player.getCombat().isAttacking(other))
            player.getCombat().reset(false);

        if (player.getMotion().hasFollowTarget())
            player.getMotion().resetTargetFollowing();

        try {
            switch (packetOpcode) {
                case PacketConstants.SECOND_PLAYER_ACTION:
                    attack(player, other);
                    break;
                case PacketConstants.FIRST_PLAYER_ACTION:
                    option1(player, other);
                    break;
                case PacketConstants.PLAYER_OPTION_2_OPCODE:
                    option2(player, other);
                    break;
                case PacketConstants.PLAYER_OPTION_3_OPCODE:
                    option3(player, other);
                    break;
            }
        } catch (Exception e){
            System.err.println("PlayerOptionPacketListener: Error "+e.getMessage()+" occurred for "+player.getUsername());
        }
    }
    private static void attack(Player player, Player other) {

        if (other == null || other.getHitpoints() <= 0 || other.equals(player)) {
            player.getMotion().clearSteps();
			player.getPacketSender().sendMinimapFlagRemoval();
            return;
        }
        
        if (other.getDueling().inDuel()) {
        	if (!player.getDueling().inDuel()) {
                player.setPositionToFace(other.getPosition());
                player.getMotion().clearSteps();
                player.getPacketSender().sendMinimapFlagRemoval();
                player.sendMessage("You don't have an active duel target.");
        		return;
        	}
        }
        
		if (player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == 4084) {
			player.getPacketSender().sendMessage("You can't attack while your on sled!", 1000);
            onInteractionFail(player, other);
            return;
		}
        if (!MorphItems.INSTANCE.notTransformed(player, "attack", false, true)) {
            onInteractionFail(player, other);
            return;
        }
		if (player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == 20056) {
			player.getPacketSender().sendMessage("You can't attack while holding Ale of the gods!", 1000);
            onInteractionFail(player, other);
            return;
		}

        if (player.getDueling().inDuel()) {
            if (!player.getDueling().getInteract().getUsername().equals(other.getUsername())) {
                player.setPositionToFace(other.getPosition());
                player.getMotion().clearSteps();
                player.getPacketSender().sendMinimapFlagRemoval();
                player.sendMessage("That isn't your opponent.");
                return;
            }
        }

        // getting out from home teleport
        if(other.isTeleporting() && other.getTeleportingType() == TeleportType.HOME)
            other.stopTeleporting();

        player.getCombat().initiateCombat(other, true);
    }

    private static void onInteractionFail(Player player, Player other) {
        player.setPositionToFace(other.getPosition());
        player.getMotion().clearSteps();
        player.getPacketSender().sendMinimapFlagRemoval();
        player.setEntityInteraction(null);
    }

    /**
     * Manages the first option click on a player option menu.
     *
     * @param player The player clicking the other entity.
     */
    private static void option1(Player player, Player other) {
        player.setEntityInteraction(other);
        player.setWalkToTask(new WalkToAction<>(player, other, () -> {
            if (player.getMotion().isFollowing(other)) {
                player.getMotion().followTarget(null);
                player.setEntityInteraction(null);
            }
            if (player.getArea() != null)
                player.getArea().onPlayerRightClick(player, other, 1);
        }, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES));
    }

    /**
     * Manages the second option click on a player option menu.
     *
     * @param player The player clicking the other entity.
     */
    private static void option2(Player player, Player other) {
        player.setEntityInteraction(other);
        player.setWalkToTask(new WalkToAction<>(player, other, () -> {
            if (player.getMotion().isFollowing(other)) {
                player.getMotion().followTarget(null);
                player.setEntityInteraction(null);
            }
            if (player.getArea() != null)
                player.getArea().onPlayerRightClick(player, other, 2);
        }, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES));
    }

    /**
     * Manages the third option click on a player option menu.
     *
     * @param player The player clicking the other entity.
     */
    private static void option3(Player player, Player other) {
        player.setEntityInteraction(other);
        player.setWalkToTask(new WalkToAction<>(player, other, () -> {
            if (player.getMotion().isFollowing(other)) {
                player.getMotion().followTarget(null);
                player.setEntityInteraction(null);
            }
            if (player.getArea() != null) {
                player.getArea().onPlayerRightClick(player, other, 3);
            }
        }, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES));
    }
}
