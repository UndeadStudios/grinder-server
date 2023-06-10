package com.grinder.game.message.encoder;

import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.player.Appearance;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.entity.agent.player.equipment.EquipmentType;
import com.grinder.game.entity.updating.SegmentType;
import com.grinder.game.entity.updating.UpdateBlockSet;
import com.grinder.game.entity.updating.UpdateSegment;
import com.grinder.game.entity.updating.block.*;
import com.grinder.game.entity.updating.seg.*;
import com.grinder.game.message.MessageEncoder;
import com.grinder.game.message.impl.PlayerSynchronizationMessage;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.item.container.player.Equipment;
import com.grinder.net.packet.*;
import com.grinder.net.packet.impl.MessagePublicListener;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link MessageEncoder} for the {@link PlayerSynchronizationMessage}.
 *
 * @author Graham
 * @author Major
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 */
public final class PlayerSynchronizationMessageEncoder extends MessageEncoder<PlayerSynchronizationMessage> {

	private static final int DEFAULT_HINT_ICON = 0;

	@Override
	public Packet encode(@NotNull PlayerSynchronizationMessage message) {
		GamePacketBuilder builder = new GamePacketBuilder(81, PacketType.VARIABLE_SHORT);
		builder.switchToBitAccess();

		GamePacketBuilder blockBuilder = new GamePacketBuilder();

		putMovementUpdate(message.getSegment(), message, builder);
		putBlocks(message.getSegment(), message, blockBuilder);

		builder.putBits(8, message.getLocalPlayers());

		for (UpdateSegment segment : message.getSegments()) {
			SegmentType type = segment.getType();
			if (type == SegmentType.REMOVE_MOB) {
				putRemovePlayerUpdate(builder);
			} else if (type == SegmentType.ADD_MOB) {
				putAddPlayerUpdate((AddPlayerSegment) segment, message, builder);
				putBlocks(segment, message, blockBuilder);
			} else {
				putMovementUpdate(segment, message, builder);
				putBlocks(segment, message, blockBuilder);
			}
		}

		if (blockBuilder.getLength() > 0) {
			builder.putBits(11, 2047);
			builder.switchToByteAccess();
			builder.putRawBuilder(blockBuilder);
		} else {
			builder.switchToByteAccess();
		}

		return builder.toGamePacket();
	}

	/**
	 * Puts an add player update.
	 *
	 * @param seg The segment.
	 * @param message The message.
	 * @param builder The builder.
	 */
	private static void putAddPlayerUpdate(AddPlayerSegment seg, PlayerSynchronizationMessage message, GamePacketBuilder builder) {
		boolean updateRequired = seg.getBlockSet().size() > 0;
		Position player = message.getPosition();
		Position other = seg.getPosition();
		builder.putBits(11, seg.getIndex());
		builder.putBits(1, updateRequired ? 1 : 0);
		builder.putBits(1, 1); // discard walking queue?
		builder.putBits(5, other.getY() - player.getY());
		builder.putBits(5, other.getX() - player.getX());
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
		builder.put(DataType.BYTE, DataTransformation.NEGATE, animation.getDelay());
	}

	/**
	 * Puts an appearance block into the specified builder.
	 *
	 * @param block The block.
	 * @param builder The builder.
	 */
	private static void putAppearanceBlock(AppearanceBlock block, GamePacketBuilder builder) {
		Appearance appearance = block.getAppearance();
		GamePacketBuilder playerProperties = new GamePacketBuilder();

		playerProperties.put(DataType.BYTE, block.getSkullIcon());
		playerProperties.put(DataType.BYTE, DEFAULT_HINT_ICON);
		playerProperties.put(DataType.BYTE, appearance.getHeadHint());
		playerProperties.put(DataType.BYTE, appearance.isMale() ? 0 : 1);
		playerProperties.put(DataType.BYTE, block.getHide() ? 1 : 0);

		if(block.updateColors()) {
			playerProperties.put(DataType.BYTE, 1);
			for (Integer[] colors : block.getColors()) {
				for (Integer color : colors) {
					playerProperties.put(DataType.INT, color);
				}
			}
		} else
			playerProperties.put(DataType.BYTE, 0);

		if (block.appearingAsNpc()) {
			playerProperties.put(DataType.BYTE, 255);
			playerProperties.put(DataType.BYTE, 255);
			playerProperties.put(DataType.SHORT, block.getNpcTransformId());
		} else {
			final Equipment equipment = block.getEquipment();
			int[] equip = new int[equipment.capacity()];

			for (int i = 0; i < equipment.capacity(); i++) {
				equip[i] = equipment.getItems()[i].getId();
			}
			if (equip[EquipmentConstants.HEAD_SLOT] > -1) {
				playerProperties.put(DataType.SHORT, 0x200 + equip[EquipmentConstants.HEAD_SLOT]);
			} else {
				playerProperties.put(DataType.BYTE, 0);
			}
			if (equip[EquipmentConstants.CAPE_SLOT] > -1) {
				playerProperties.put(DataType.SHORT, 0x200 + equip[EquipmentConstants.CAPE_SLOT]);
			} else {
				playerProperties.put(DataType.BYTE, 0);
			}
			if (equip[EquipmentConstants.AMULET_SLOT] > -1) {
				playerProperties.put(DataType.SHORT, 0x200 + equip[EquipmentConstants.AMULET_SLOT]);
			} else {
				playerProperties.put(DataType.BYTE, 0);
			}
			if (equip[EquipmentConstants.WEAPON_SLOT] > -1) {
				playerProperties.put(DataType.SHORT, 0x200 + equip[EquipmentConstants.WEAPON_SLOT]);
			} else {
				playerProperties.put(DataType.BYTE, 0);
			}
			if (equip[EquipmentConstants.BODY_SLOT] > -1) {
				playerProperties.put(DataType.SHORT, 0x200 + equip[EquipmentConstants.BODY_SLOT]);
			} else {
				playerProperties.put(DataType.SHORT, 0x100 + appearance.getLook()[Appearance.CHEST]);
			}
			if (equip[EquipmentConstants.SHIELD_SLOT] > -1) {
				playerProperties.put(DataType.SHORT, 0x200 + equip[EquipmentConstants.SHIELD_SLOT]);
			} else {
				playerProperties.put(DataType.BYTE, 0);
			}

			if (ItemDefinition.forId(equip[EquipmentConstants.BODY_SLOT]).getEquipmentType() == EquipmentType.PLATEBODY) {
				playerProperties.put(DataType.BYTE, 0);
			} else {
				playerProperties.put(DataType.SHORT, 0x100 + appearance.getLook()[Appearance.ARMS]);
			}

			if (equip[EquipmentConstants.LEG_SLOT] > -1) {
				playerProperties.put(DataType.SHORT, 0x200 + equip[EquipmentConstants.LEG_SLOT]);
			} else {
				playerProperties.put(DataType.SHORT, 0x100 + appearance.getLook()[Appearance.LEGS]);
			}

			if (ItemDefinition.forId(equip[EquipmentConstants.HEAD_SLOT]).getEquipmentType() == EquipmentType.FULL_HELMET
					|| ItemDefinition.forId(equip[EquipmentConstants.CAPE_SLOT]).getEquipmentType() == EquipmentType.HOODED_CAPE
					|| ItemDefinition.forId(equip[EquipmentConstants.HEAD_SLOT]).getEquipmentType() == EquipmentType.COIF) {
				playerProperties.put(DataType.BYTE, 0);
			} else {
				playerProperties.put(DataType.SHORT, 0x100 + appearance.getLook()[Appearance.HEAD]);
			}

			if (equip[EquipmentConstants.HANDS_SLOT] > -1) {
				playerProperties.put(DataType.SHORT, 0x200 + equip[EquipmentConstants.HANDS_SLOT]);
			} else {
				playerProperties.put(DataType.SHORT, 0x100 + appearance.getLook()[Appearance.HANDS]);
			}
			if (equip[EquipmentConstants.FEET_SLOT] > -1) {
				playerProperties.put(DataType.SHORT, 0x200 + equip[EquipmentConstants.FEET_SLOT]);
			} else {
				playerProperties.put(DataType.SHORT, 0x100 + appearance.getLook()[Appearance.FEET]);
			}
			if (appearance.getLook()[Appearance.BEARD] <= 0
					|| !appearance.isMale()
					|| ItemDefinition.forId(equip[EquipmentConstants.HEAD_SLOT]).getEquipmentType() == EquipmentType.FULL_HELMET) {
				playerProperties.put(DataType.BYTE, 0);
			} else {
				playerProperties.put(DataType.SHORT, 0x100 + appearance.getLook()[Appearance.BEARD]);
			}
		}

		int[] colors = appearance.getLook();
		playerProperties.put(DataType.BYTE, colors[Appearance.HAIR_COLOUR]);
		playerProperties.put(DataType.BYTE, colors[Appearance.TORSO_COLOUR]);
		playerProperties.put(DataType.BYTE, colors[Appearance.LEG_COLOUR]);
		playerProperties.put(DataType.BYTE, colors[Appearance.FEET_COLOUR]);
		playerProperties.put(DataType.BYTE, colors[Appearance.SKIN_COLOUR]);

		playerProperties.put(DataType.SHORT, block.getBas().getIdle());
		playerProperties.put(DataType.SHORT, block.getBas().getTurn());
		playerProperties.put(DataType.SHORT, block.getBas().getWalk());
		playerProperties.put(DataType.SHORT, block.getBas().getTurnBack());
		playerProperties.put(DataType.SHORT, block.getBas().getTurnLeft());
		playerProperties.put(DataType.SHORT, block.getBas().getTurnRight());
		playerProperties.put(DataType.SHORT, block.getBas().getRun());

		playerProperties.put(DataType.LONG, block.getName());
		playerProperties.put(DataType.BYTE, block.getCombat());
		playerProperties.put(DataType.INT, block.getRights());
		playerProperties.put(DataType.BYTE, block.getCrown());
		playerProperties.putString(block.getTitle());

		builder.put(DataType.BYTE, DataTransformation.NEGATE, playerProperties.getLength());
		builder.putRawBuilder(playerProperties);
	}

	/**
	 * Puts the blocks for the specified segment.
	 *
	 * @param segment The {@link UpdateSegment update}
	 * @param message The {@link PlayerSynchronizationMessage message}.
	 * @param builder The {@link GamePacketBuilder block builder}.
	 */
	private static void putBlocks(UpdateSegment segment, PlayerSynchronizationMessage message, GamePacketBuilder builder) {
		UpdateBlockSet blockSet = segment.getBlockSet();
		if (blockSet.size() > 0) {
			int mask = 0;

			if (blockSet.contains(ForceMovementBlock.class)) {
				mask |= 0x400;
			}
			if (blockSet.contains(GraphicBlock.class)) {
				mask |= 0x100;
			}
			if (blockSet.contains(AnimationBlock.class)) {
				mask |= 0x8;
			}
			if (blockSet.contains(ForceChatBlock.class)) {
				mask |= 0x4;
			}
			if (blockSet.contains(ChatBlock.class)) {
				mask |= 0x80;
			}
			if (blockSet.contains(InteractingMobBlock.class)) {
				mask |= 0x1;
			}
			if (blockSet.contains(AppearanceBlock.class)) {
				mask |= 0x10;
			}
			if (blockSet.contains(TurnToPositionBlock.class)) {
				mask |= 0x2;
			}
			if (blockSet.contains(HitFirstUpdateBlock.class)) {
				mask |= 0x20;
			}
			if (blockSet.contains(HitSecondUpdateBlock.class)) {
				mask |= 0x200;
			}

			if (mask >= 0x100) {
				mask |= 0x40;
				builder.put(DataType.SHORT, DataOrder.LITTLE, mask);
			} else {
				builder.put(DataType.BYTE, mask);
			}

			if (blockSet.contains(ForceMovementBlock.class)) {
				putForceMovementBlock(blockSet.get(ForceMovementBlock.class), message.getLastKnownRegion(), builder);
			}
			if (blockSet.contains(GraphicBlock.class)) {
				putGraphicBlock(blockSet.get(GraphicBlock.class), builder);
			}
			if (blockSet.contains(AnimationBlock.class)) {
				putAnimationBlock(blockSet.get(AnimationBlock.class), builder);
			}
			if (blockSet.contains(ForceChatBlock.class)) {
				putForceChatBlock(blockSet.get(ForceChatBlock.class), builder);
			}
			if (blockSet.contains(ChatBlock.class)) {
				putChatBlock(blockSet.get(ChatBlock.class), builder);
			}
			if (blockSet.contains(InteractingMobBlock.class)) {
				putInteractingMobBlock(blockSet.get(InteractingMobBlock.class), builder);
			}
			if (blockSet.contains(AppearanceBlock.class)) {
				putAppearanceBlock(blockSet.get(AppearanceBlock.class), builder);
			}
			if (blockSet.contains(TurnToPositionBlock.class)) {
				putTurnToPositionBlock(blockSet.get(TurnToPositionBlock.class), builder);
			}
			if (blockSet.contains(HitFirstUpdateBlock.class)) {
				putHitUpdateBlock(message.getPlayer(), blockSet.get(HitFirstUpdateBlock.class), builder);
			}
			if (blockSet.contains(HitSecondUpdateBlock.class)) {
				putSecondHitUpdateBlock(message.getPlayer(), blockSet.get(HitSecondUpdateBlock.class), builder);
			}
		}
	}

	/**
	 * Puts a chat block into the specified builder.
	 *
	 * @param block The block.
	 * @param builder The builder.
	 */
	private static void putChatBlock(ChatBlock block, GamePacketBuilder builder) {
		byte[] compressed = new byte[256];
		int length = MessagePublicListener.Companion.getHuffman().compress(block.getString(), compressed);
		builder.put(DataType.SHORT,(block.getColor() << 8) | (block.getEffect()));
		builder.put(DataType.INT, block.getRights());
		builder.put(DataType.BYTE, block.getCrown());
		builder.put(DataType.BYTE, length + 1);
		builder.putSmart(block.getString().length());
		builder.putBytes(compressed, length);
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
	 * Puts a force movement block in the specified builder.
	 *
	 * @param block The block.
	 * @param relativeRegion The {@link Position last known region} of the player receiving the update.
	 * @param builder The builder.
	 */
	private static void putForceMovementBlock(ForceMovementBlock block, Position relativeRegion, GamePacketBuilder builder) {
		final Position start = block.getStartPosition();
		final Position end = block.getEndPosition();
		final int localOriginX = start.getLocalX(relativeRegion);
		final int localOriginY = start.getLocalY(relativeRegion);
		final int destinationX = localOriginX + end.getX();
		final int destinationY = localOriginY + end.getY();
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, localOriginX);
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, localOriginY);
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, destinationX);
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, destinationY);
		builder.put(DataType.SHORT, DataOrder.LITTLE, DataTransformation.ADD, block.getStartTick());
		builder.put(DataType.SHORT, DataOrder.BIG, DataTransformation.ADD, block.getEndTick());
		builder.put(DataType.SHORT, DataOrder.LITTLE, DataTransformation.ADD, block.getAnimation());
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, block.getDirection());
	}

	/**
	 * Puts a graphic block into the specified builder.
	 *
	 * @param block The block.
	 * @param builder The builder.
	 */
	private static void putGraphicBlock(GraphicBlock block, GamePacketBuilder builder) {
		Graphic graphic = block.getGraphic();
		builder.put(DataType.SHORT, DataOrder.LITTLE, graphic.getId());
		builder.put(DataType.INT, graphic.getHeight() << 16 | graphic.getDelay() & 0xFFFF);
	}

	/**
	 * Puts a hit update block into the specified builder.
	 *
	 * @param block The block.
	 * @param builder The builder.
	 */
	private static void putHitUpdateBlock(Player player, HitFirstUpdateBlock block, GamePacketBuilder builder) {
		builder.put(DataType.SHORT, block.getDamage().getValue());
		builder.put(DataType.BYTE, block.getDamage().getSegmentMask(player).spriteId());
		builder.put(DataType.SHORT, block.getCurrentHealth());
		builder.put(DataType.SHORT,block.getMaximumHealth());
	}

	/**
	 * Puts an interacting mob block into the specified builder.
	 *
	 * @param block The block.
	 * @param builder The builder.
	 */
	private static void putInteractingMobBlock(InteractingMobBlock block, GamePacketBuilder builder) {
		builder.put(DataType.SHORT, DataOrder.LITTLE, block.getIndex());
	}

	/**
	 * Puts a movement update for the specified segment.
	 *
	 * @param seg The segment.
	 * @param message The message.
	 * @param builder The builder.
	 */
	private static void putMovementUpdate(UpdateSegment seg, PlayerSynchronizationMessage message, GamePacketBuilder builder) {

		final boolean updateRequired = seg.getBlockSet().size() > 0;

		if (seg.getType() == SegmentType.TELEPORT) {
			final Position position = ((TeleportSegment) seg).getDestination();
			builder.putBits(1, 1);
			builder.putBits(2, 3);
			builder.putBits(2, position.getZ());
			builder.putBits(1, message.hasRegionChanged() ? 0 : 1);
			builder.putBits(1, updateRequired ? 1 : 0);
			builder.putBits(7, position.getLocalY(message.getLastKnownRegion()));
			builder.putBits(7, position.getLocalX(message.getLastKnownRegion()));
		} else if (seg.getType() == SegmentType.RUN) {
			final Direction[] directions = ((MovementSegment) seg).getDirections();
//			final int runMask = directions[0].getClientRunMask(directions[1]);
			builder.putBits(1, 1);
			builder.putBits(2, 2);
//			builder.putBits(4, runMask);
			builder.putBits(3, directions[0].getClientWalkMask());
			builder.putBits(3, directions[1].getClientWalkMask());
			builder.putBits(1, updateRequired ? 1 : 0);
		} else if (seg.getType() == SegmentType.WALK) {
			final Direction[] directions = ((MovementSegment) seg).getDirections();
			final int walkMask = directions[0].getClientWalkMask();
//			System.out.println(directions[0]);
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
	 * Puts a remove player update.
	 *
	 * @param builder The builder.
	 */
	private static void putRemovePlayerUpdate(GamePacketBuilder builder) {
		builder.putBits(1, 1);
		builder.putBits(2, 3);
	}

	/**
	 * Puts a Second Hit Update block into the specified builder.
	 *
	 * @param block The block.
	 * @param builder The builder.
	 */
	private static void putSecondHitUpdateBlock(Player player, HitSecondUpdateBlock block, GamePacketBuilder builder) {
		builder.put(DataType.SHORT, block.getDamage().getValue());
		builder.put(DataType.BYTE, block.getDamage().getSegmentMask(player).spriteId());
		builder.put(DataType.SHORT, block.getCurrentHealth());
		builder.put(DataType.SHORT,block.getMaximumHealth());
	}

	/**
	 * Puts a Turn To Position block into the specified builder.
	 *
	 * @param block The block.
	 * @param builder The builder.
	 */
	private static void putTurnToPositionBlock(TurnToPositionBlock block, GamePacketBuilder builder) {
		int faceDegrees = block.getFaceDegrees();
		builder.put(DataType.SHORT, faceDegrees);
	}

}