package com.grinder.net.packet.impl;

import java.util.Optional;

import com.grinder.game.collision.CollisionManager;
import com.grinder.game.content.item.MorphItems;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.impl.Firemaking;
import com.grinder.game.content.skill.skillable.impl.Firemaking.LightableLog;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.grounditem.ItemOnGround;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.util.Executable;
import com.grinder.game.model.Position;
import com.grinder.game.entity.agent.movement.task.WalkToAction;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.util.ItemID;

/**
 * This packet is received when a player
 * clicks on the second option on a ground item.
 * An example being the "light" option on logs that
 * are on the ground.
 *
 * @author Professor Oak
 */

public class SecondItemOnGroundOptionPacketListener implements PacketListener {

    @Override
    public void handleMessage(final Player player, PacketReader packetReader, int packetOpcode) {
        final int y = packetReader.readLEShort();
        final int itemId = packetReader.readShort();
        final int x = packetReader.readLEShort();


        if (player == null || player.getHitpoints() <= 0) {
            return;
        }

        final Position position = new Position(x, y, player.getPosition().getZ());
        //if (!RegionManager.canReach(player.getPosition(), position, 1)) {
        if (!CollisionManager.canMove(player.getPosition(), position, 1, 1)) {
            player.setPositionToFace(position);
            player.setEntityInteraction(null);
            player.getPacketSender().sendMinimapFlagRemoval();
            player.getMotion().reset();
            player.sendMessage("I can't reach that!");
            return;
        }
        //Stop skilling..
        SkillUtil.stopSkillable(player);

        if (!player.getLastItemPickup().elapsed(300))
            return;
        if(player.isTeleporting() && player.getTeleportingType() == TeleportType.HOME) {
            player.stopTeleporting();
        }
        if (player.busy())
            return;
        if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
            return;
        }

        if (!MorphItems.INSTANCE.notTransformed(player, "", false, true))
            return;

        if (player.BLOCK_ALL_BUT_TALKING) {
            return;
        }
        if (player.isInTutorial()) {
            return;
        }

        player.setWalkToTask(new WalkToAction<>(player, position, 1, 1, () -> {
            //Make sure distance isn't way off..
            if (Math.abs(player.getPosition().getX() - x) > 20 || Math.abs(player.getPosition().getY() - y) > 20) {
                player.getMotion().clearSteps();
                return;
            }

            player.getLastItemPickup().reset();

            //Get ground item..
            Optional<ItemOnGround> item = ItemOnGroundManager.getItemOnGround(Optional.of(player.getUsername()), itemId, position);
            if (item.isPresent()) {
                //Handle it..
                /** FIREMAKING **/
                Optional<LightableLog> log = LightableLog.find(item.get().getItem().getId());
                if (!log.isPresent()) {
                    player.sendMessage("Nothing interesting happens.");
                    return;
                }
                if (Firemaking.initItemOnGround(player, ItemID.TINDERBOX, item.get().getItem().getId())) {
                    return;
                }
                if (log.isPresent()) {
                    SkillUtil.startSkillable(player, new Firemaking(log.get(), item.get()));
                    return;
                }
            }
        }));
    }
}
