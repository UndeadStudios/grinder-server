package com.grinder.game.message.encoder;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.updating.UpdateBlockSet;
import com.grinder.game.entity.updating.block.*;
import com.grinder.game.entity.updating.seg.AddNpcSegment;
import com.grinder.game.entity.updating.seg.MovementSegment;
import com.grinder.game.entity.updating.SegmentType;
import com.grinder.game.entity.updating.UpdateSegment;
import com.grinder.game.message.MessageEncoder;
import com.grinder.game.message.impl.NpcSynchronizationMessage;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.net.packet.*;

/**
 * A {@link MessageEncoder} for the {@link NpcSynchronizationMessage}.
 *
 * @author Major
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 */
public final class NpcSynchronizationMessageEncoder extends MessageEncoder<NpcSynchronizationMessage> {

	@Override
	public Packet encode(NpcSynchronizationMessage message) {
		GamePacketBuilder builder = new GamePacketBuilder(message.isLargeScene() ? 66 : 65, PacketType.VARIABLE_SHORT);
		builder.switchToBitAccess();

		GamePacketBuilder blockBuilder = new GamePacketBuilder();
		builder.putBits(8, message.getLocalNpcCount());

		for (UpdateSegment segment : message.getSegments()) {
			SegmentType type = segment.getType();
			if (type == SegmentType.REMOVE_MOB) {
				putRemoveMobUpdate(builder);
			} else if (type == SegmentType.ADD_MOB) {
				putAddNpcUpdate((AddNpcSegment) segment, message, builder);
				putBlocks(segment, message, blockBuilder);
			} else {
				putMovementUpdate(segment, message, builder);
				putBlocks(segment, message, blockBuilder);
			}
		}

		if (blockBuilder.getLength() > 0) {
			builder.putBits(15, 0x7FFF);
			builder.switchToByteAccess();
			builder.putRawBuilder(blockBuilder);
		} else {
			builder.switchToByteAccess();
		}

		return builder.toGamePacket();
	}

	/**
	 * Puts an add npc update.
	 *
	 * @param seg The segment.
	 * @param message The message.
	 * @param builder The builder.
	 */
	private static void putAddNpcUpdate(AddNpcSegment seg, NpcSynchronizationMessage message, GamePacketBuilder builder) {
		final boolean updateRequired = seg.getBlockSet().size() > 0;
		final Position npc = message.getPosition();
		final Position other = seg.getPosition();
		final Direction facing = seg.getFacing();
		final boolean largeScene = seg.isLargeScene();

		int dx = other.getX() - npc.getX();
		int dy = other.getY() - npc.getY();
		builder.putBits(15, seg.getIndex());
		builder.putBits(largeScene ? 8 : 5, dx);
		builder.putBits(1, updateRequired ? 1 : 0);
		builder.putBits(3, facing.getNpcWalkValue());
		builder.putBits(1,  updateRequired ? 1 : 0);
		builder.putBits(largeScene ? 8 : 5, dy);
		builder.putBits(14, seg.getNpcId());
	}

	/**
	 * Puts an animation block into the specified builder.
	 *
	 * @param block The block.
	 * @param builder The builder.
	 */
	private static void putAnimationBlock(AnimationBlock block, GamePacketBuilder builder) {
		Animation animation = block.getAnimation();
		builder.put(DataType.SHORT, DataOrder.LITTLE, animation.getId());
		builder.put(DataType.BYTE, animation.getDelay());
	}

	/**
	 * Puts the blocks for the specified segment.
	 *
	 * @param segment The segment.
	 * @param builder The block builder.
	 */
	private static void putBlocks(UpdateSegment segment, NpcSynchronizationMessage message, GamePacketBuilder builder) {
		UpdateBlockSet blockSet = segment.getBlockSet();
		if (blockSet.size() > 0) {
			int mask = 0;

			if (blockSet.contains(AnimationBlock.class)) {
				mask |= 0x10;
			}

			if (blockSet.contains(HitFirstUpdateBlock.class)) {
				mask |= 0x8;
			}

			if (blockSet.contains(GraphicBlock.class)) {
				mask |= 0x80;
			}

			if (blockSet.contains(InteractingMobBlock.class)) {
				mask |= 0x20;
			}

			if (blockSet.contains(ForceChatBlock.class)) {
				mask |= 0x1;
			}

			if (blockSet.contains(HitSecondUpdateBlock.class)) {
				mask |= 0x40;
			}

			if (blockSet.contains(TransformBlock.class)) {
				mask |= 0x2;
			}

			if (blockSet.contains(TurnToPositionBlock.class)) {
				mask |= 0x4;
			}

			if (blockSet.contains(ForceMovementBlock.class)) {
				mask |= 0x400;
			}

			builder.put(DataType.SHORT, mask);

			if (blockSet.contains(AnimationBlock.class)) {
				putAnimationBlock(blockSet.get(AnimationBlock.class), builder);
			}

			if (blockSet.contains(GraphicBlock.class)) {
				putGraphicBlock(blockSet.get(GraphicBlock.class), builder);
			}

			if (blockSet.contains(HitFirstUpdateBlock.class)) {
				putHitUpdateBlock(message.getPlayer(), blockSet.get(HitFirstUpdateBlock.class), builder);
			}

			if (blockSet.contains(InteractingMobBlock.class)) {
				putInteractingMobBlock(blockSet.get(InteractingMobBlock.class), builder);
			}

			if (blockSet.contains(ForceChatBlock.class)) {
				putForceChatBlock(blockSet.get(ForceChatBlock.class), builder);
			}

			if (blockSet.contains(HitSecondUpdateBlock.class)) {
				putSecondHitUpdateBlock(message.getPlayer(), blockSet.get(HitSecondUpdateBlock.class), builder);
			}

			if (blockSet.contains(TransformBlock.class)) {
				putTransformBlock(blockSet.get(TransformBlock.class), builder);
			}

			if (blockSet.contains(TurnToPositionBlock.class)) {
				putTurnToPositionBlock(blockSet.get(TurnToPositionBlock.class), builder);
			}

			if (blockSet.contains(ForceMovementBlock.class)) {
				putForceMovementBlock(blockSet.get(ForceMovementBlock.class), message.getLastKnownRegion(), builder);
			}
		}
	}

	/**
	 * Puts a force chat block into the specified builder.
	 *
	 * @param block The block.
	 * @param builder The builder.
	 */
	private static void putForceChatBlock(ForceChatBlock block, GamePacketBuilder builder) {
		builder.putString(block.getString());
	}

	/**
	 * Puts a graphic block into the specified builder.
	 *
	 * @param block The block.
	 * @param builder The builder.
	 */
	private static void putGraphicBlock(GraphicBlock block, GamePacketBuilder builder) {
		Graphic graphic = block.getGraphic();
		builder.put(DataType.SHORT, graphic.getId());
		builder.put(DataType.INT, ((graphic.getHeight()) << 16) + (graphic.getDelay() & 0xffff));
	}

	/**
	 * Puts a hit update block into the specified builder.
	 *
	 * @param block The block.
	 * @param builder The builder.
	 */
	private static void putHitUpdateBlock(final Player player, HitFirstUpdateBlock block, GamePacketBuilder builder) {
		builder.put(DataType.SHORT, block.getDamage().getValue());
		builder.put(DataType.BYTE, block.getDamage().getSegmentMask(player).spriteId());
		builder.put(DataType.SHORT, block.getCurrentHealth());
		builder.put(DataType.SHORT, block.getMaximumHealth());
	}

	/**
	 * Puts an interacting mob block into the specified builder.
	 *
	 * @param block The block.
	 * @param builder The builder.
	 */
	private static void putInteractingMobBlock(InteractingMobBlock block, GamePacketBuilder builder) {
		builder.put(DataType.SHORT, block.getIndex());
	}

	/**
	 * Puts a movement update for the specified segment.
	 *
	 * @param segment The segment.
	 * @param message The message.
	 * @param builder The builder.
	 */
	private static void putMovementUpdate(UpdateSegment segment, NpcSynchronizationMessage message, GamePacketBuilder builder) {
		boolean updateRequired = segment.getBlockSet().size() > 0;
		if (segment.getType() == SegmentType.RUN) {
			final Direction[] directions = ((MovementSegment) segment).getDirections();
			final int walkMask = directions[0].getClientWalkMask();
			final int runMask = directions[1].getClientWalkMask();
			builder.putBits(1, 1);
			builder.putBits(2, 2);
			builder.putBits(3, walkMask);
			builder.putBits(3, runMask);
			builder.putBits(1, updateRequired ? 1 : 0);
		} else if (segment.getType() == SegmentType.WALK) {
			final Direction[] directions = ((MovementSegment) segment).getDirections();
			final int walkMask = directions[0].getClientWalkMask();
			builder.putBits(1, 1);
			builder.putBits(2, 1);
			builder.putBits(3, walkMask);
			builder.putBits(1, updateRequired ? 1 : 0);
		} else {
			if (updateRequired) {
				builder.putBits(1, 1);
				builder.putBits(2, 0);
			} else {
				builder.putBits(1, 0);
			}
		}
	}

	/**
	 * Puts a remove mob update.
	 *
	 * @param builder The builder.
	 */
	private static void putRemoveMobUpdate(GamePacketBuilder builder) {
		builder.putBits(1, 1);
		builder.putBits(2, 3);
	}

	/**
	 * Puts a second hit update block into the specified builder.
	 *
	 * @param block The block.
	 * @param builder The builder.
	 */
	private static void putSecondHitUpdateBlock(final Player player, HitSecondUpdateBlock block, GamePacketBuilder builder) {
		builder.put(DataType.SHORT, block.getDamage().getValue());
		builder.put(DataType.BYTE, block.getDamage().getSegmentMask(player).spriteId());
		builder.put(DataType.SHORT, block.getCurrentHealth());
		builder.put(DataType.SHORT, block.getMaximumHealth());

	}

	/**
	 * Puts a transform block into the specified builder.
	 *
	 * @param block The block.
	 * @param builder The builder.
	 */
	private static void putTransformBlock(TransformBlock block, GamePacketBuilder builder) {
		builder.put(DataType.BYTE, block.getHeadIconId());
		if(block.getId() != -1){
			builder.put(DataType.BYTE, 1);
			builder.put(DataType.SHORT, DataOrder.LITTLE, DataTransformation.ADD, block.getId());
		} else
			builder.put(DataType.BYTE, 0);
	}

	/**
	 * Puts a turn to position block into the specified builder.
	 *
	 * @param block The block.
	 * @param builder The builder.
	 */
	private static void putTurnToPositionBlock(TurnToPositionBlock block, GamePacketBuilder builder) {
		int x = block.getFaceDegrees() >> 16;
		int z = block.getFaceDegrees() & 0xffff;
		builder.put(DataType.SHORT, x);
		builder.put(DataType.SHORT, z);
	}

	private static void putForceMovementBlock(ForceMovementBlock block, Position relativeRegion, GamePacketBuilder builder) {
		final Position start = block.getStartPosition();
		final Position end = block.getEndPosition();
		final int localOriginX = start.getLocalX(relativeRegion);
		final int localOriginY = start.getLocalY(relativeRegion);
		final int destinationX = localOriginX + (end.getX() - start.getX());
		final int destinationY = localOriginY + (end.getY() - start.getY());
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, localOriginX);
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, localOriginY);
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, destinationX);
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, destinationY);
		builder.put(DataType.SHORT, DataOrder.LITTLE, DataTransformation.ADD, block.getStartTick());
		builder.put(DataType.SHORT, DataOrder.BIG, DataTransformation.ADD, block.getEndTick());
		builder.put(DataType.SHORT, DataOrder.LITTLE, DataTransformation.ADD, block.getAnimation());
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, block.getDirection());
	}

}