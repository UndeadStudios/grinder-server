package com.grinder.game.content.skill.skillable.impl.cons;

import com.grinder.game.World;
import com.grinder.game.content.skill.skillable.impl.cons.actions.HouseBuildingActions;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.areas.Area;
import com.grinder.game.model.areas.constructed.Palette;
import com.grinder.game.model.areas.constructed.Palette.PaletteTile;
import com.grinder.game.model.areas.instanced.HouseInstance;
import com.grinder.game.model.item.Item;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

/**
 * The majority of this file was written in 2010 by blakeman8192, the entire
 * system is in the process of being rewritten.
 *
 * Much of this code is experimental.
 *
 * @author Simplex
 */
public class Construction {

	/**
	 * Find house room at position.
	 * @param player
	 * 				House owner
	 */
	public static Optional<HouseRoom> getHouseRoomAt(Player player, Position position) {
		int[] currentRoomChunk = getRoomChunkAt(position);

		HouseRoom room = player.getCurrentRoomSet()
				[player.getPosition().getZ()]
				[currentRoomChunk[0] - 1]
				[currentRoomChunk[1] - 1];

		return Optional.ofNullable(room);
	}

	/**
	 * Create a new HouseInstance.
	 *
	 * @param player
	 * 				House owner.
	 */
	public static void newHouse(Player player) {
		player.getHouse().setHasHouse(true);

		// set all house rooms to empty (rather than null)
		roomFill(player, player.getHouse().getHouseHouseRooms(), ConstructionUtils.EMPTY);

		// set first room in the center of map as Garden
		HouseRoom garden = new HouseRoom(0, ConstructionUtils.GARDEN, 0, 7, 7, 0);
		player.getHouse().getHouseHouseRooms()[0][7][7] = garden;

		// place an exit portal in central room
		HouseFurniture pf = new HouseFurniture(7, 7, 0, HotSpotType.CENTREPIECE.getHotSpotId(),
			HouseFurnitureType.EXIT_PORTAL.getFurnitureId(), HotSpotType.CENTREPIECE.getXOffset(),
			HotSpotType.CENTREPIECE.getYOffset());

		// add furniture to furniture list
		garden.addFurniture(pf);
		System.out.println("newHouse " + garden.getFurniture());
	}

	/**
	 * Creates a dungeon and subsequent dungeon stair room at the tile where
	 * the player built the dungeon entrance.
	 *
	 * @param player
	 * 			Home owner
	 * @param roomX
	 * 			Tile X to start building the dungeon
	 * @param roomY
	 * 			Tile Y to start building the dungeon
	 */
	public static void newDungeon(Player player, int roomX, int roomY) {
		// fill floor with empty tiles
		roomFill(player, player.getHouse().getDungeonRooms(), ConstructionUtils.DUNGEON_EMPTY);

		// add stair room in the room where the dungeon was instantiated
		player.getHouse().getDungeonRooms()[0][roomX][roomY] = new HouseRoom(0, ConstructionUtils.DUNGEON_STAIR_ROOM, 0, roomX, roomY, 0);

		// add stairs
		HouseFurniture pf = new HouseFurniture(roomX, roomY, 0, HotSpotType.DUNG_STAIRS.getHotSpotId(),
			HouseFurnitureType.SPIRAL_STAIRCASE.getFurnitureId(), HotSpotType.DUNG_STAIRS.getXOffset(),
			HotSpotType.DUNG_STAIRS.getYOffset());

		// add stairs to furniture list
		player.getHouse().getDungeonFurniture().add(pf);

		// reform the dungeon palette
		generateDungeonPalette(player);
	}

	/**
	 * Fill room set with given room type.
	 * @param player
	 * 				House owner
	 * @param houseRooms
	 *				HouseRoom set
	 * @param roomType
	 * 				HouseRoom to fill with
	 */
	public static void roomFill(Player player, HouseRoom[][][] houseRooms, int roomType) {
		for (int x = 0; x < 13; x++)
			for (int y = 0; y < 13; y++)
				if (houseRooms[0][x][y] == null)
					houseRooms[0][x][y] = new HouseRoom(0, roomType, 0, x, y, 0);
	}

	/**
	 * Simple counting utility, count rooms that are not buildable / empty.
	 * @param player
	 * @return
	 * 		  Number of built rooms.
	 */
	public static int countRooms(Player player) {
		HouseRoom[][][] houseRooms = player.getCurrentRoomSet();
		int i = 0;
		for(int z = 0; z < 2; z++) {
			for (int x = 0; x < 13; x++) {
				for (int y = 0; y < 13; y++) {
					HouseRoom room = houseRooms[z][x][y];
					if (room != null && room.getType() != ConstructionUtils.BUILDABLE
							&& room.getType() != ConstructionUtils.EMPTY
							&& room.getType() != ConstructionUtils.DUNGEON_EMPTY) {
						i++;
					}
				}
			}
		}

		return i;
	}

	/**
	 * Executes {@link UpdateHouseInstanceTask} with given HouseInstance.
	 * @param player
	 * 				Player entering house
	 * @param instance
	 * 				Instance to send
	 */
	public static void updateHouseInstance(Player player, HouseInstance instance, boolean buildMode) {
		if(player.BLOCK_ALL_BUT_TALKING) {
			player.sendMessage("You can't do that right now.");
			return;
		}

		Optional<Area> currentArea = Optional.ofNullable(player.getArea());

		// in the off chance someone is trying to join this instance
		// from another instance, destroy their current instance
		currentArea.filter(a -> a != instance)
				   .ifPresent(a -> {a.leave(player);});

		HouseInstance houseInstance;

		// either joining from the house portal or updating from inside
		if(instance == null) {
			houseInstance = getHouseInstance(player);
		} else {
			houseInstance = instance;
		}

		// set build mode
		houseInstance.setBuildMode(buildMode);
		player.getPacketSender().sendConfig(950, buildMode ? 1 : 0);

		// temporary, give house (TODO: buy from housing agent)
		if (houseInstance.isOwner(player) && !player.getHouse().hasHouse()) {
			player.getHouse().setHasHouse(true);
			Construction.newHouse(player);
		}

		// initiate house update task
		TaskManager.submit(new UpdateHouseInstanceTask(player, houseInstance));
	}

	/**
	 * Finds the first entrance portal instance in the furniture list.
	 * @param player
	 * 				Player in {@link HouseInstance}.
	 */
	public static HouseFurniture findFirstEntrancePortal(Player player) {
		Optional<HouseFurniture> entrancePortal = getHouseOwner(player)
				.getHouse().getSurfaceFurniture().stream().filter(furniture -> furniture.getFurnitureId() == 4525).findFirst();

		if(entrancePortal.isEmpty()) {
			System.err.println("Something went wrong! Could not find an entrance portal for " + player.getUsername());
			throw new IllegalStateException();
		}

		return entrancePortal.get();
	}

	/**
	 * Generate {@link PaletteTile} map for the Construction surface (house).
	 * @param player
	 */
	public static void generateSurfacePalette(Player player) {
		HouseInstance mapInstance = getHouseInstance(player);

		Palette palette = new Palette();
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < 13; x++) {
				for (int y = 0; y < 13; y++) {
					if (player.getHouse().getHouseHouseRooms()[z][x][y] == null)
						continue;
					if (player.getHouse().getHouseHouseRooms()[z][x][y].getX() == 0)
						continue;
					PaletteTile tile = new PaletteTile(player.getHouse().getHouseHouseRooms()[z][x][y].getX(),
						player.getHouse().getHouseHouseRooms()[z][x][y].getY(), player.getHouse().getHouseHouseRooms()[z][x][y].getZ(),
						player.getHouse().getHouseHouseRooms()[z][x][y].getRotation());
					palette.setTile(x, y, z, tile);
				}
			}
		}

		mapInstance.setSurfacePalette(palette);
	}

	/**
	 * Generate {@link PaletteTile} map for the Construction dungeon.
	 * @param player
	 */
	public static void generateDungeonPalette(Player player) {
		HouseInstance mapInstance = getHouseInstance(player);

		Palette palette = new Palette();
		HouseRoom[][][] houseRooms = mapInstance.getHouseOwner().getHouse().getDungeonRooms();

		for (int x = 0; x < 13; x++) {
			for (int y = 0; y < 13; y++) {
				PaletteTile tile = null;
				HouseRoom r = houseRooms[0][x][y];
				if (r == null || r.getType() == ConstructionUtils.EMPTY) {
					tile = new PaletteTile(HouseRoomType.DUNGEON_EMPTY.getX(),
							HouseRoomType.DUNGEON_EMPTY.getY(), 0, 0);
				} else {
					tile = new PaletteTile(houseRooms[0][x][y].getX(), houseRooms[0][x][y].getY(),
							houseRooms[0][x][y].getZ(), houseRooms[0][x][y].getRotation());
				}
				palette.setTile(x, y, 0, tile);
			}
		}

		mapInstance.setDungeonPalette(palette);
	}


	/**
	 * If exists, returns the current HouseInstance. If not, creates one.
	 * @param player
	 * 				Player entering HouseInstance.
	 */
	private static HouseInstance getHouseInstance(Player player) {
		HouseInstance mapInstance;

		if (player.getArea() != null && player.getArea() instanceof HouseInstance) {
			mapInstance = (HouseInstance) player.getArea();
		} else {
			mapInstance = new HouseInstance(player);
		}

		return mapInstance;
	}

	/**
	 * TODO rewrite
	 */
	public static void placeNPCs(Player p) {

		/*if (p.getHouseServant() > 0) {
			HouseFurniture portal = findNearestPortal(p);
			int toX = ConstructionUtils.BASE_X + ((portal.getRoomX() + 1) * 8);
			int toY = ConstructionUtils.BASE_Y + ((portal.getRoomY() + 1) * 8);
			// TODO Servants
			Servant npc = new Servant(p.getHouseServant(),
				new Position(toX + 3, toY + 1, p.getPosition().getZ()));
			((HouseInstance) p.getArea()).addCharacter(npc);
			World.getNpcAddQueue().add(npc);
		}*/
		if (p.inBuildingMode()) {
			return;
		}
		for (HouseFurniture pf : p.getHouse().getSurfaceFurniture()) {
			HouseFurnitureType f = HouseFurnitureType.forFurnitureId(pf.getFurnitureId());
			int npcId = ConstructionUtils.getGuardId(f.getFurnitureId());
			if (npcId == -1)
				continue;
			HouseRoom houseRoom = p.getHouse().getHouseHouseRooms()[pf.getRoomZ()][pf.getRoomX()][pf.getRoomY()];
			HotSpotType hs = HotSpotType.forHotSpotIdAndCoords(pf.getHotSpotId(), pf.getStandardXOff(), pf.getStandardYOff(),
					houseRoom);
			int actualX = ConstructionUtils.BASE_X + (pf.getRoomX() + 1) * 8;
			actualX += ConstructionUtils.getXOffsetForObjectId(pf.getFurnitureId(), hs, houseRoom.getRotation());
			int actualY = ConstructionUtils.BASE_Y + (pf.getRoomY() + 1) * 8;
			actualY += ConstructionUtils.getYOffsetForObjectId(pf.getFurnitureId(), hs, houseRoom.getRotation());

			// TODO: Add servants
			//Servant npc = new Servant(npcId, new Position(actualX, actualY));
			//((HouseInstance) p.getArea()).addCharacter(npc);
			//World.getNpcAddQueue().add(npc);
		}
	}

	/**
	 * @return Player's area as HouseInstance
	 */
	public static HouseInstance getInstance(Player player) {
		return (HouseInstance) player.getArea();
	}

	/**
	 * @return House owner (May not be Player joining instance)
	 */
	public static Player getHouseOwner(Player player) {
		return getInstance(player).getHouseOwner();
	}

	/**
	 * Place all furniture in a given room
	 * TODO: Rewrite
	 */
	public static void placeAllFurniture(Player p, int x, int y, int z) {
		for (HouseFurniture pf : getHouseOwner(p).getHouse().getSurfaceFurniture()) {
			if (pf.getRoomZ() != z)
				continue;
			if (pf.getRoomX() != x || pf.getRoomY() != y)
				continue;
			HouseRoom houseRoom = getHouseOwner(p).getHouse().getHouseHouseRooms()[pf.getRoomZ()][pf.getRoomX()][pf.getRoomY()];
			HotSpotType hs = HotSpotType.forHotSpotIdAndCoords(pf.getHotSpotId(), pf.getStandardXOff(), pf.getStandardYOff(),
					houseRoom);
			if (hs == null)
				return;
			int actualX = ConstructionUtils.BASE_X + (pf.getRoomX() + 1) * 8;
			actualX += ConstructionUtils.getXOffsetForObjectId(pf.getFurnitureId(), hs, houseRoom.getRotation());
			int actualY = ConstructionUtils.BASE_Y + (pf.getRoomY() + 1) * 8;
			actualY += ConstructionUtils.getYOffsetForObjectId(pf.getFurnitureId(), hs, houseRoom.getRotation());
			HouseFurnitureType f = HouseFurnitureType.forFurnitureId(pf.getFurnitureId());
			ArrayList<HotSpotType> hsses = HotSpotType.forObjectId_3(f.getHotSpotId());
			doFurniturePlace(hs, f, hsses, getMyChunkFor(actualX, actualY), actualX, actualY, houseRoom.getRotation(), p,
				false, z);
		}
	}

	/**
	 * Finds and validates that a hotspot exists within the room the player is standing in.
	 *
	 * @param player
	 * 				House owner
	 * @param objectId
	 * 				Hotspot (or partial hotspot) Object Id
	 */
	public static Optional<HotSpotType> verifyHotspot(Player player, int objectId) {
		// all possible objects that may refer to this specific hotspot
		ArrayList<HotSpotType> hotspotPool = HotSpotType.forObjectId_2(objectId);

		if (hotspotPool.isEmpty())
			return Optional.empty();

		// room the player is standing in
		int[] chunk = getCurrentChunk(player);

		int xInChunk = chunk[0] - 1;
		int yInChunk = chunk[1] - 1;

		int toHeight = player.getPosition().getZ();
		int roomRot = player.getCurrentRoomSet()[toHeight][xInChunk][yInChunk].getRotation();
		int myRoomType = player.getCurrentRoomSet()[toHeight][xInChunk][yInChunk]
				.getType();

		HotSpotType hotspot = null;

		if (hotspotPool.size() == 1) {
			hotspot = hotspotPool.get(0);
		} else {
			// look for the primary hotspot object
			for (HotSpotType find : hotspotPool) {
				int actualX = ConstructionUtils.BASE_X + (chunk[0] * 8);
				actualX += ConstructionUtils.getXOffsetForObjectId(find.getObjectId(), find, roomRot);
				int actualY = ConstructionUtils.BASE_Y + (chunk[1] * 8);
				actualY += ConstructionUtils.getYOffsetForObjectId(find.getObjectId(), find, roomRot);
				if (player.getHouse().getBuildFurnitureX() == actualX && player.getHouse().getBuildFurnitureY() == actualY
						&& myRoomType == find.getRoomType()
						|| find.getCarpetDimensions() != null && myRoomType == find.getRoomType()) {
					hotspot = find;
					break;
				}
			}
		}

		return Optional.ofNullable(hotspot);
	}

	/**
	 * Place all furniture for all rooms on a given height level.
	 * TODO: Rewrite
	 */
	public static void placeAllFurniture(Player p, int heightLevel) {
		for (HouseFurniture pf : p.getCurrentFurnitureSet()) {
			if (pf.getRoomZ() != heightLevel) {
				continue;
			}
			HouseRoom houseRoom = getHouseOwner(p).getHouse().getHouseHouseRooms()[pf.getRoomZ()][pf.getRoomX()][pf.getRoomY()];
			if (houseRoom == null) {
				System.out.println("[Con] Error " + pf.getFurnitureId() + " - HouseRoom not found [" + pf.getRoomZ() + "][" + pf.getRoomX() + "][" + pf.getRoomY() + "]");
				return;
			}
			HotSpotType hs = HotSpotType.forHotSpotIdAndCoords
				(pf.getHotSpotId(), pf.getStandardXOff(), pf.getStandardYOff(),
						houseRoom);
			if (hs == null) {
				System.out.println("[Con] Error " + pf.getFurnitureId() + " - No hotspot found");
				return;
			}
			// int rotation = hs.getRotation(houseRoom.getRotation());

			int actualX = ConstructionUtils.BASE_X + (pf.getRoomX() + 1) * 8;
			actualX += ConstructionUtils.getXOffsetForObjectId(pf.getFurnitureId(), hs, houseRoom.getRotation());
			int actualY = ConstructionUtils.BASE_Y + (pf.getRoomY() + 1) * 8;
			actualY += ConstructionUtils.getYOffsetForObjectId(pf.getFurnitureId(), hs, houseRoom.getRotation());

			HouseFurnitureType f = HouseFurnitureType.forFurnitureId(pf.getFurnitureId());

			ArrayList<HotSpotType> hsses = HotSpotType.forObjectId_3(f.getHotSpotId());

			doFurniturePlace(hs, f, hsses, getMyChunkFor(actualX, actualY), actualX, actualY, houseRoom.getRotation(), p,
				false, heightLevel);
		}
	}

	/**
	 * Place single furniture piece. (Replaces a hotspot)
	 * TODO: Rewrite
	 */
	public static void doFurniturePlace(HotSpotType s, HouseFurnitureType f, ArrayList<HotSpotType> hsses, int[] myTiles, int actualX,
										int actualY, int roomRot, Player p, boolean placeHotspot, int height) {
		int portalId = -1;

		System.out.println("Do furniture placement: " + actualX + " " + actualY);

		int offsetX = ConstructionUtils.BASE_X + (myTiles[0] * 8);
		int offsetY = ConstructionUtils.BASE_Y + (myTiles[1] * 8);

		if (s.getHotSpotId() == 72) {
			if (s.getXOffset() == 0) {
				for (HousePortal portal : getHouseOwner(p).getHouse().getHousePortals()) {
					if (portal.getRoomX() == myTiles[0] - 1 && portal.getRoomY() == myTiles[1] - 1
						&& portal.getRoomZ() == height && portal.getId() == 0) {
						if (HousePortalType.forType(portal.getType()).getObjects() != null)
							portalId = HousePortalType.forType(portal.getType()).getObjects()[f.getFurnitureId() - 13636];

					}
				}
			}
			if (s.getXOffset() == 3) {
				for (HousePortal portal : getHouseOwner(p).getHouse().getHousePortals()) {
					if (portal.getRoomX() == myTiles[0] - 1 && portal.getRoomY() == myTiles[1] - 1
						&& portal.getRoomZ() == height && portal.getId() == 1) {
						if (HousePortalType.forType(portal.getType()).getObjects() != null)
							portalId = HousePortalType.forType(portal.getType()).getObjects()[f.getFurnitureId() - 13636];

					}
				}

			}
			if (s.getXOffset() == 7) {
				for (HousePortal portal : getHouseOwner(p).getHouse().getHousePortals()) {
					if (portal.getRoomX() == myTiles[0] - 1 && portal.getRoomY() == myTiles[1] - 1
						&& portal.getRoomZ() == height && portal.getId() == 2) {
						if (HousePortalType.forType(portal.getType()).getObjects() != null)
							portalId = HousePortalType.forType(portal.getType()).getObjects()[f.getFurnitureId() - 13636];

					}
				}
			}
		}
		if (height == 4)
			height = 0;

		if (s.getHotSpotId() == 92) {
			if (s.getObjectId() == 15329 || s.getObjectId() == 15328) {
				p.getPacketSender()
					.sendObject_cons(actualX, actualY,
						s.getObjectId() == 15328 ? (placeHotspot ? 15328 : f.getFurnitureId())
							: (placeHotspot ? 15329 : f.getFurnitureId() + 1),
						s.getRotation(roomRot), 0, height);
				offsetX += ConstructionUtils.getXOffsetForObjectId(f.getFurnitureId(),
					s.getXOffset() + (s.getObjectId() == 15329 ? 1 : -1), s.getYOffset(), roomRot,
					s.getRotation(0));
				offsetY += ConstructionUtils.getYOffsetForObjectId(f.getFurnitureId(),
					s.getXOffset() + (s.getObjectId() == 15329 ? 1 : -1), s.getYOffset(), roomRot,
					s.getRotation(0));
				p.getPacketSender()
					.sendObject_cons(offsetX, offsetY,
						s.getObjectId() == 15329 ? (placeHotspot ? 15328 : f.getFurnitureId())
							: (placeHotspot ? 15329 : f.getFurnitureId() + 1),
						s.getRotation(roomRot), 0, height);

			}
			if (s.getObjectId() == 15326 || s.getObjectId() == 15327) {
				p.getPacketSender().sendObject_cons(actualX, actualY, s.getObjectId() == 15327
						? (placeHotspot ? 15327 : f.getFurnitureId() + 1) : (placeHotspot ? 15326 : f.getFurnitureId()),
					s.getRotation(roomRot), 0, height);
				offsetX += ConstructionUtils.getXOffsetForObjectId(f.getFurnitureId(),
					s.getXOffset() + (s.getObjectId() == 15326 ? 1 : -1), s.getYOffset(), roomRot,
					s.getRotation(0));
				offsetY += ConstructionUtils.getYOffsetForObjectId(f.getFurnitureId(),
					s.getXOffset() + (s.getObjectId() == 15326 ? 1 : -1), s.getYOffset(), roomRot,
					s.getRotation(0));
				p.getPacketSender().sendObject_cons(offsetX, offsetY, s.getObjectId() == 15326
						? (placeHotspot ? 15327 : f.getFurnitureId() + 1) : (placeHotspot ? 15326 : f.getFurnitureId()),
					s.getRotation(roomRot), 0, height);

			}
		} else if (s.getHotSpotId() == 85) {
			actualX = ConstructionUtils.BASE_X + (myTiles[0] * 8) + 2;
			actualY = ConstructionUtils.BASE_Y + (myTiles[1] * 8) + 2;
			int type = 22, leftObject = 0, rightObject = 0, upperObject = 0, downObject = 0, middleObject = 0,
				veryMiddleObject = 0, cornerObject = 0;
			if (f.getFurnitureId() == 13331) {
				leftObject = rightObject = upperObject = downObject = 13332;
				middleObject = 13331;
				cornerObject = 13333;
			}
			if (f.getFurnitureId() == 13334) {
				leftObject = rightObject = upperObject = downObject = 13335;
				middleObject = 13334;
				cornerObject = 13336;
			}
			if (f.getFurnitureId() == 13337) {
				leftObject = rightObject = upperObject = downObject = middleObject = cornerObject = 13337;
				type = 10;
			}
			if (f.getFurnitureId() == 13373) {
				veryMiddleObject = 13373;
				leftObject = rightObject = upperObject = downObject = middleObject = 6951;
			}
			if (placeHotspot || f.getFurnitureId() == 13337) {
				for (int x = 0; x < 4; x++) {
					for (int y = 0; y < 4; y++) {
						p.getPacketSender().sendObject_cons(actualX + x, actualY + y, 6951, 0, 10, height);
						p.getPacketSender().sendObject_cons(actualX + x, actualY + y, 6951, 0, 22, height);
					}
				}

			}
			p.getPacketSender().sendObject_cons(actualX, actualY, placeHotspot ? 15348 : cornerObject, 1, type, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 1, placeHotspot ? 15348 : leftObject, 1, type, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 2, placeHotspot ? 15348 : leftObject, 1, type, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 3, placeHotspot ? 15348 : cornerObject, 2, type,
				height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 3, placeHotspot ? 15348 : upperObject, 2, type,
				height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 3, placeHotspot ? 15348 : upperObject, 2, type,
				height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 3, placeHotspot ? 15348 : cornerObject, 3, type,
				height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 2, placeHotspot ? 15348 : rightObject, 3, type,
				height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 1, placeHotspot ? 15348 : rightObject, 3, type,
				height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY, placeHotspot ? 15348 : cornerObject, 0, type,
				height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY, placeHotspot ? 15348 : downObject, 0, type, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY, placeHotspot ? 15348 : downObject, 0, type, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 1, placeHotspot ? 15348 : middleObject, 0, type,
				height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 1, placeHotspot ? 15348 : middleObject, 0, type,
				height);
			if (veryMiddleObject != 0)
				p.getPacketSender().sendObject_cons(actualX + 1, actualY + 2, veryMiddleObject, 0, 10, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 2, placeHotspot ? 15348 : middleObject, 0, type,
				height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 2, placeHotspot ? 15348 : middleObject, 0, type,
				height);

		} else if (s.getHotSpotId() == 86) {
			actualX = ConstructionUtils.BASE_X + (myTiles[0] * 8) + 2;
			actualY = ConstructionUtils.BASE_Y + (myTiles[1] * 8) + 2;

			p.getPacketSender().sendObject_cons(actualX + 1, actualY, placeHotspot ? 15352 : f.getFurnitureId(), 3, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY, placeHotspot ? 15352 : f.getFurnitureId(), 3, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY, placeHotspot ? 15352 : f.getFurnitureId(), 2, 2,
				height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 1, placeHotspot ? 15352 : f.getFurnitureId(), 2, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 2, placeHotspot ? 15352 : f.getFurnitureId() + 1, 2,
				0, height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 3, placeHotspot ? 15352 : f.getFurnitureId(), 1, 2,
				height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 3, placeHotspot ? 15352 : f.getFurnitureId(), 1, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 3, placeHotspot ? 15352 : f.getFurnitureId(), 1, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 3, placeHotspot ? 15352 : f.getFurnitureId(), 0, 2,
				height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 2, placeHotspot ? 15352 : f.getFurnitureId(), 0, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 1, placeHotspot ? 15352 : f.getFurnitureId(), 0, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX, actualY, placeHotspot ? 15352 : f.getFurnitureId(), 3, 2, height);

		} else if (s.getHotSpotId() == 78) {
			actualX = ConstructionUtils.BASE_X + (myTiles[0] * 8);
			actualY = ConstructionUtils.BASE_Y + (myTiles[1] * 8);
			// south walls
			p.getPacketSender().sendObject_cons(actualX, actualY, placeHotspot ? 15369 : f.getFurnitureId(), 3, 2, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY, placeHotspot ? 15369 : f.getFurnitureId(), 3, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY, placeHotspot ? 15369 : f.getFurnitureId(), 3, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY, placeHotspot ? 15369 : f.getFurnitureId(), 3, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 6, actualY, placeHotspot ? 15369 : f.getFurnitureId(), 3, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY, placeHotspot ? 15369 : f.getFurnitureId(), 2, 2,
				height);
			// north walls
			p.getPacketSender().sendObject_cons(actualX, actualY + 7, placeHotspot ? 15369 : f.getFurnitureId(), 0, 2,
				height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 7, placeHotspot ? 15369 : f.getFurnitureId(), 1, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 7, placeHotspot ? 15369 : f.getFurnitureId(), 1, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY + 7, placeHotspot ? 15369 : f.getFurnitureId(), 1, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 6, actualY + 7, placeHotspot ? 15369 : f.getFurnitureId(), 1, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 7, placeHotspot ? 15369 : f.getFurnitureId(), 1, 2,
				height);
			// left walls
			p.getPacketSender().sendObject_cons(actualX, actualY + 1, placeHotspot ? 15369 : f.getFurnitureId(), 0, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 2, placeHotspot ? 15369 : f.getFurnitureId(), 0, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 5, placeHotspot ? 15369 : f.getFurnitureId(), 0, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 6, placeHotspot ? 15369 : f.getFurnitureId(), 0, 0,
				height);
			// right walls
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 1, placeHotspot ? 15369 : f.getFurnitureId(), 2, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 2, placeHotspot ? 15369 : f.getFurnitureId(), 2, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 5, placeHotspot ? 15369 : f.getFurnitureId(), 2, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 6, placeHotspot ? 15369 : f.getFurnitureId(), 2, 0,
				height);
		} else if (s.getHotSpotId() == 77) {
			actualX = ConstructionUtils.BASE_X + (myTiles[0] * 8);
			actualY = ConstructionUtils.BASE_Y + (myTiles[1] * 8);
			// left down corner
			p.getPacketSender().sendObject_cons(actualX, actualY, placeHotspot ? 15372 : f.getFurnitureId() + 1, 3, 10,
				height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY, placeHotspot ? 15371 : f.getFurnitureId() + 2, 0, 10,
				height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY, placeHotspot ? 15370 : f.getFurnitureId(), 0, 10,
				height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 1, placeHotspot ? 15371 : f.getFurnitureId() + 2, 1, 10,
				height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 2, placeHotspot ? 15370 : f.getFurnitureId(), 3, 10,
				height);
			// right down corner
			p.getPacketSender().sendObject_cons(actualX + 7, actualY, placeHotspot ? 15372 : f.getFurnitureId() + 1, 2, 10,
				height);
			p.getPacketSender().sendObject_cons(actualX + 6, actualY, placeHotspot ? 15371 : f.getFurnitureId() + 2, 0, 10,
				height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY, placeHotspot ? 15370 : f.getFurnitureId(), 2, 10,
				height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 1, placeHotspot ? 15371 : f.getFurnitureId() + 2, 3,
				10, height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 2, placeHotspot ? 15370 : f.getFurnitureId(), 3, 10,
				height);
			// upper left corner
			p.getPacketSender().sendObject_cons(actualX, actualY + 7, placeHotspot ? 15372 : f.getFurnitureId() + 1, 0, 10,
				height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 7, placeHotspot ? 15371 : f.getFurnitureId() + 2, 0,
				10, height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 7, placeHotspot ? 15370 : f.getFurnitureId(), 0, 10,
				height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 6, placeHotspot ? 15371 : f.getFurnitureId() + 2, 1, 10,
				height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 5, placeHotspot ? 15370 : f.getFurnitureId(), 1, 10,
				height);
			// upper right corner
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 7, placeHotspot ? 15372 : f.getFurnitureId() + 1, 1,
				10, height);
			p.getPacketSender().sendObject_cons(actualX + 6, actualY + 7, placeHotspot ? 15371 : f.getFurnitureId() + 2, 0,
				10, height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY + 7, placeHotspot ? 15370 : f.getFurnitureId(), 2, 10,
				height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 6, placeHotspot ? 15371 : f.getFurnitureId() + 2, 3,
				10, height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 5, placeHotspot ? 15370 : f.getFurnitureId(), 1, 10,
				height);
		} else if (s.getHotSpotId() == 44) {
			int combatringStrings = 6951;
			int combatringFloorsCorner = 6951;
			int combatringFloorsOuter = 6951;
			int combatringFloorsInner = 6951;
			actualX = ConstructionUtils.BASE_X + (myTiles[0] * 8) + 1;
			actualY = ConstructionUtils.BASE_Y + (myTiles[1] * 8) + 1;
			if (!placeHotspot) {
				if (f.getFurnitureId() == 13126) {
					combatringStrings = 13132;
					combatringFloorsCorner = 13126;
					combatringFloorsOuter = 13128;
					combatringFloorsInner = 13127;
				}
				if (f.getFurnitureId() == 13133) {
					combatringStrings = 13133;
					combatringFloorsCorner = 13135;
					combatringFloorsOuter = 13134;
					combatringFloorsInner = 13136;
				}
				if (f.getFurnitureId() == 13137) {
					combatringStrings = 13137;
					combatringFloorsCorner = 13138;
					combatringFloorsOuter = 13139;
					combatringFloorsInner = 13140;
				}
			}

			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 3, placeHotspot ? 15292 : combatringFloorsInner, 0,
				22, height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 3, placeHotspot ? 15292 : combatringFloorsInner, 0,
				22, height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 2, placeHotspot ? 15292 : combatringFloorsInner, 0,
				22, height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 2, placeHotspot ? 15292 : combatringFloorsInner, 0,
				22, height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 1, placeHotspot ? 15291 : combatringFloorsOuter, 3,
				22, height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 1, placeHotspot ? 15291 : combatringFloorsOuter, 3,
				22, height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 4, placeHotspot ? 15291 : combatringFloorsOuter, 1,
				22, height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 4, placeHotspot ? 15291 : combatringFloorsOuter, 1,
				22, height);
			p.getPacketSender().sendObject_cons(actualX + 4, actualY + 3, placeHotspot ? 15291 : combatringFloorsOuter, 2,
				22, height);
			p.getPacketSender().sendObject_cons(actualX + 4, actualY + 2, placeHotspot ? 15291 : combatringFloorsOuter, 2,
				22, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 3, placeHotspot ? 15291 : combatringFloorsOuter, 0,
				22, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 2, placeHotspot ? 15291 : combatringFloorsOuter, 0,
				22, height);
			p.getPacketSender().sendObject_cons(actualX + 4, actualY + 1, placeHotspot ? 15289 : combatringFloorsCorner, 3,
				22, height);
			p.getPacketSender().sendObject_cons(actualX + 4, actualY + 4, placeHotspot ? 15289 : combatringFloorsCorner, 2,
				22, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 4, placeHotspot ? 15289 : combatringFloorsCorner, 1,
				22, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 1, placeHotspot ? 15289 : combatringFloorsCorner, 0,
				22, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 4, placeHotspot ? 15277 : combatringStrings, 3, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 1, placeHotspot ? 15277 : combatringStrings, 3, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY + 4, placeHotspot ? 15277 : combatringStrings, 3, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY + 1, placeHotspot ? 15277 : combatringStrings, 0, 3,
				height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY, placeHotspot ? 15277 : combatringStrings, 1, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY, placeHotspot ? 15277 : combatringStrings, 1, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY, placeHotspot ? 15277 : combatringStrings, 1, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 4, actualY, placeHotspot ? 15277 : combatringStrings, 1, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY, placeHotspot ? 15277 : combatringStrings, 0, 3,
				height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 5, placeHotspot ? 15277 : combatringStrings, 3, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 5, placeHotspot ? 15277 : combatringStrings, 3, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 3, actualY + 5, placeHotspot ? 15277 : combatringStrings, 3, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 4, actualY + 5, placeHotspot ? 15277 : combatringStrings, 3, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY + 5, placeHotspot ? 15277 : combatringStrings, 3, 3,
				height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 5, placeHotspot ? 15277 : combatringStrings, 2, 3,
				height);
			p.getPacketSender().sendObject_cons(actualX, actualY, placeHotspot ? 15277 : combatringStrings, 1, 3, height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 4, placeHotspot ? 15277 : combatringStrings, 2, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 3, placeHotspot ? 15277 : combatringStrings, 2, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 2, placeHotspot ? 15277 : combatringStrings, 2, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 1, placeHotspot ? 15277 : combatringStrings, 2, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY + 4, placeHotspot ? 15277 : combatringStrings, 0, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY + 3, placeHotspot ? 15277 : combatringStrings, 0, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY + 2, placeHotspot ? 15277 : combatringStrings, 0, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY + 1, placeHotspot ? 15277 : combatringStrings, 0, 0,
				height);

			if (f.getFurnitureId() == 13145) {
				p.getPacketSender().sendObject_cons(actualX + 1, actualY + 1, placeHotspot ? 6951 : 13145, 0, 0, height);
				p.getPacketSender().sendObject_cons(actualX + 2, actualY + 1, placeHotspot ? 6951 : 13145, 0, 0, height);
				p.getPacketSender().sendObject_cons(actualX + 1, actualY, placeHotspot ? 6951 : 13145, 1, 0, height);
				p.getPacketSender().sendObject_cons(actualX + 1, actualY + 2, placeHotspot ? 6951 : 13145, 3, 0, height);
				if (!placeHotspot)
					p.getPacketSender().sendObject_cons(actualX + 1, actualY + 1, 13147, 0, 22, height);

				p.getPacketSender().sendObject_cons(actualX + 3, actualY + 3, placeHotspot ? 6951 : 13145, 0, 0, height);
				p.getPacketSender().sendObject_cons(actualX + 4, actualY + 3, placeHotspot ? 6951 : 13145, 0, 0, height);
				p.getPacketSender().sendObject_cons(actualX + 3, actualY + 2, placeHotspot ? 6951 : 13145, 1, 0, height);
				p.getPacketSender().sendObject_cons(actualX + 3, actualY + 4, placeHotspot ? 6951 : 13145, 3, 0, height);
				if (!placeHotspot)
					p.getPacketSender().sendObject_cons(actualX + 3, actualY + 3, 13147, 0, 22, height);
			}
			if (f.getFurnitureId() == 13142 && !placeHotspot) {
				p.getPacketSender().sendObject_cons(actualX + 2, actualY + 2, 13142, 0, 22, height);
				p.getPacketSender().sendObject_cons(actualX + 2, actualY + 1, 13143, 0, 22, height);
				p.getPacketSender().sendObject_cons(actualX + 2, actualY + 3, 13144, 1, 22, height);

			}
		} else if (s.getCarpetDimensions() != null) {
			for (int x = 0; x < s.getCarpetDimensions().getWidth() + 1; x++) {
				for (int y = 0; y < s.getCarpetDimensions().getHeight() + 1; y++) {
					boolean isEdge = (x == 0 && y == 0 || x == 0 && y == s.getCarpetDimensions().getHeight()
						|| y == 0 && x == s.getCarpetDimensions().getWidth()
						|| x == s.getCarpetDimensions().getWidth() && y == s.getCarpetDimensions().getHeight());
					boolean isWall = ((x == 0 || x == s.getCarpetDimensions().getWidth())
						&& (y != 0 && y != s.getCarpetDimensions().getHeight())
						|| (y == 0 || y == s.getCarpetDimensions().getHeight())
						&& (x != 0 && x != s.getCarpetDimensions().getWidth()));
					int rot = 0;
					if (x == 0 && y == s.getCarpetDimensions().getHeight() && isEdge)
						rot = 0;
					if (x == s.getCarpetDimensions().getWidth() && y == s.getCarpetDimensions().getHeight() && isEdge)
						rot = 1;
					if (x == s.getCarpetDimensions().getWidth() && y == 0 && isEdge)
						rot = 2;
					if (x == 0 && y == 0 && isEdge)
						rot = 3;
					if (y == 0 && isWall)
						rot = 2;
					if (y == s.getCarpetDimensions().getHeight() && isWall)
						rot = 0;
					if (x == 0 && isWall)
						rot = 3;
					if (x == s.getCarpetDimensions().getWidth() && isWall)
						rot = 1;
					offsetX = ConstructionUtils.BASE_X + (myTiles[0] * 8);
					offsetY = ConstructionUtils.BASE_Y + (myTiles[1] * 8);
					offsetX += ConstructionUtils.getXOffsetForObjectId(f.getFurnitureId(), s.getXOffset() + x - 1,
						s.getYOffset() + y - 1, roomRot, s.getRotation(roomRot));
					offsetY += ConstructionUtils.getYOffsetForObjectId(f.getFurnitureId(), s.getXOffset() + x - 1,
						s.getYOffset() + y - 1, roomRot, s.getRotation(roomRot));
					if (isEdge)
						p.getPacketSender().sendObject_cons(offsetX, offsetY,
							placeHotspot ? s.getObjectId() + 2 : f.getFurnitureId(),
							HotSpotType.getRotation_2(rot, roomRot), 22, height);
					else if (isWall)
						p.getPacketSender().sendObject_cons(offsetX, offsetY,
							placeHotspot ? s.getObjectId() + 1 : f.getFurnitureId() + 1,
							HotSpotType.getRotation_2(rot, roomRot), s.getObjectType(), height);
					else
						p.getPacketSender().sendObject_cons(offsetX, offsetY,
							placeHotspot ? s.getObjectId() : f.getFurnitureId() + 2,
							HotSpotType.getRotation_2(rot, roomRot), s.getObjectType(), height);
				}
			}
		} else if (s.isMultiple()) {

			HouseRoom houseRoom = p.getCurrentRoomSet()[p.getPosition().getZ()][myTiles[0] - 1][myTiles[1] - 1];

			for (HotSpotType find : hsses) {

				if (find.getObjectId() != s.getObjectId()) {
					continue;
				}
				if (houseRoom != null) {
					if (houseRoom.getType() != find.getRoomType()) {
						continue;
					}
				}

				int actualX1 = ConstructionUtils.BASE_X + (myTiles[0] * 8);
				actualX1 += ConstructionUtils.getXOffsetForObjectId(find.getObjectId(), find, roomRot);
				int actualY1 = ConstructionUtils.BASE_Y + (myTiles[1] * 8);
				actualY1 += ConstructionUtils.getYOffsetForObjectId(find.getObjectId(), find, roomRot);

				p.getPacketSender().sendObject_cons(actualX1, actualY1,
					placeHotspot ? s.getObjectId() : f.getFurnitureId(), find.getRotation(roomRot),
					find.getObjectType(), height);
			}
		} else if (s.getHotSpotId() == 104) {
			p.getPacketSender().sendObject_cons(
				offsetX + ConstructionUtils.getXOffsetForObjectId(f.getFurnitureId(), HotSpotType.SEATING_SPACE1, roomRot),
				offsetY + ConstructionUtils.getYOffsetForObjectId(f.getFurnitureId() + 1, HotSpotType.SEATING_SPACE1, roomRot),
				placeHotspot ? HotSpotType.SEATING_SPACE2.getObjectId() : f.getFurnitureId() + 1,
				HotSpotType.SEATING_SPACE1.getRotation(roomRot),
				10,
				height);
			p.getPacketSender().sendObject_cons(
				offsetX + ConstructionUtils.getXOffsetForObjectId(f.getFurnitureId(), HotSpotType.SEATING_SPACE2, roomRot),
				offsetY + ConstructionUtils.getYOffsetForObjectId(f.getFurnitureId(), HotSpotType.SEATING_SPACE2, roomRot),
				placeHotspot ? HotSpotType.SEATING_SPACE1.getObjectId() : f.getFurnitureId(),
				HotSpotType.SEATING_SPACE2.getRotation(roomRot),
				10,
				height);
			p.getPacketSender().sendObject_cons(
				offsetX + ConstructionUtils.getXOffsetForObjectId(f.getFurnitureId(), HotSpotType.SEATING_SPACE3, roomRot),
				offsetY + ConstructionUtils.getYOffsetForObjectId(f.getFurnitureId() + 1, HotSpotType.SEATING_SPACE3, roomRot),
				placeHotspot ? HotSpotType.SEATING_SPACE3.getObjectId() : f.getFurnitureId(),
				HotSpotType.SEATING_SPACE3.getRotation(roomRot),
				10,
				height);
			p.getPacketSender().sendObject_cons(
				offsetX + ConstructionUtils.getXOffsetForObjectId(f.getFurnitureId(), HotSpotType.SEATING_SPACE4, roomRot),
				offsetY + ConstructionUtils.getYOffsetForObjectId(f.getFurnitureId() + 1, HotSpotType.SEATING_SPACE4, roomRot),
				placeHotspot ? HotSpotType.SEATING_SPACE4.getObjectId() : f.getFurnitureId() + 1,
				HotSpotType.SEATING_SPACE4.getRotation(roomRot),
				10,
				height);
		} else if (s.getHotSpotId() == 108) {

			p.BLOCK_ALL_BUT_TALKING = true;

			p.getPacketSender().sendMapState(2);
			p.getPacketSender().sendInterface(28640);

			p.getPacketSender().sendObject_cons(actualX, actualY,
				(portalId != -1 ? portalId : placeHotspot ? s.getObjectId() : f.getFurnitureId()),
				s.getRotation(roomRot), s.getObjectType(), height);

			final int[][][] floorScheme = new int[][][]{
				{{0, 2, 1}, {0, 2, 0}, {0, 2, 0}, {0, 2, 0}, {0, 2, 0}, {0, 2, 0}, {0, 2, 0}, {0, 1, 1}},
				{{0, 3, 0}, {1, 0, 1}, {1, 0, 0}, {1, 0, 0}, {1, 0, 0}, {1, 0, 0}, {1, 3, 1}, {0, 1, 0}},
				{{0, 3, 0}, {1, 1, 0}, {2, 0, 1}, {2, 0, 0}, {2, 0, 0}, {2, 3, 1}, {1, 3, 0}, {0, 1, 0}},
				{{0, 3, 0}, {1, 1, 0}, {2, 1, 0}, {3, 0, 0}, {3, 0, 0}, {2, 3, 0}, {1, 3, 0}, {0, 1, 0}},
				{{0, 3, 0}, {1, 1, 0}, {2, 1, 0}, {3, 0, 0}, {3, 0, 0}, {2, 3, 0}, {1, 3, 0}, {0, 1, 0}},
				{{0, 3, 0}, {1, 1, 0}, {2, 1, 1}, {2, 2, 0}, {2, 2, 0}, {2, 2, 1}, {1, 3, 0}, {0, 1, 0}},
				{{0, 3, 0}, {1, 1, 1}, {1, 2, 0}, {1, 2, 0}, {1, 2, 0}, {1, 2, 0}, {1, 2, 1}, {0, 1, 0}},
				{{0, 3, 1}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 1}},
			};

			for (int y = 0; y < 8; y++) {
				for (int x = 0; x < 8; x++) {
					if (floorScheme[y][x][0] != 3)
						p.getPacketSender().sendObject_cons(
							offsetX + x,
							offsetY + y,
							placeHotspot ? 29241 :
								getSuperiorFloorTile(floorScheme[y][x][0], floorScheme[y][x][2], f),
							floorScheme[y][x][1],
							22,
							height);
				}
			}

			TaskManager.submit(new Task(1, p, false) {

				@Override
				protected void execute() {
					p.getPacketSender().sendInterfaceRemoval();
					p.getPacketSender().sendMapState(0);
					p.BLOCK_ALL_BUT_TALKING = false;
					stop();
				}
			});

		} else if (s.getHotSpotId() == 109) {
			actualX = ConstructionUtils.BASE_X + (myTiles[0] * 8);
			actualY = ConstructionUtils.BASE_Y + (myTiles[1] * 8);

			p.getPacketSender().sendObject_cons(actualX, actualY, placeHotspot ? 29132 : f.getFurnitureId(), 3, 2, height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY, placeHotspot ? 29133 : f.getFurnitureId(), 3, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY, placeHotspot ? 29133 : f.getFurnitureId() + 1, 3, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY, placeHotspot ? 29133 : f.getFurnitureId() + 2, 3, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 6, actualY, placeHotspot ? 29133 : f.getFurnitureId(), 3, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY, placeHotspot ? 29133 : f.getFurnitureId(), 2, 2,
				height);
			// north walls
			p.getPacketSender().sendObject_cons(actualX, actualY + 7, placeHotspot ? 29133 : f.getFurnitureId(), 0, 2,
				height);
			p.getPacketSender().sendObject_cons(actualX + 1, actualY + 7, placeHotspot ? 29133 : f.getFurnitureId(), 1, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 2, actualY + 7, placeHotspot ? 29133 : f.getFurnitureId() + 2, 1, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 5, actualY + 7, placeHotspot ? 29133 : f.getFurnitureId() + 1, 1, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 6, actualY + 7, placeHotspot ? 29133 : f.getFurnitureId(), 1, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 7, placeHotspot ? 29133 : f.getFurnitureId(), 1, 2,
				height);
			// left walls
			p.getPacketSender().sendObject_cons(actualX, actualY + 1, placeHotspot ? 29133 : f.getFurnitureId(), 0, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 2, placeHotspot ? 29133 : f.getFurnitureId() + 2, 0, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 5, placeHotspot ? 29133 : f.getFurnitureId() + 1, 0, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX, actualY + 6, placeHotspot ? 29133 : f.getFurnitureId(), 0, 0,
				height);
			// right walls
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 1, placeHotspot ? 29133 : f.getFurnitureId(), 2, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 2, placeHotspot ? 29133 : f.getFurnitureId() + 1, 2, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 5, placeHotspot ? 29133 : f.getFurnitureId() + 2, 2, 0,
				height);
			p.getPacketSender().sendObject_cons(actualX + 7, actualY + 6, placeHotspot ? 29133 : f.getFurnitureId(), 2, 0,
				height);
		} else {
			p.getPacketSender().sendObject_cons(actualX, actualY,
				(portalId != -1 ? portalId : placeHotspot ? s.getObjectId() : f.getFurnitureId()),
				s.getRotation(roomRot), s.getObjectType(), height);
		}
	}

	/**
	 * Returns the superior floor object for given tile in superior garden floor scheme.
	 * @param sqId
	 * 			  	Current square in the superior garden tile map.
	 * @param cornerTile
	 * 				Corner tile data.
	 * @param theme
	 * 				Theme ID (Volcanic, zen, otherworldly)
	 * @return
	 */
	public static int getSuperiorFloorTile(int sqId, int cornerTile, HouseFurnitureType theme) {
		int themeId = theme == HouseFurnitureType.ZEN_THEME ? 0
			: theme == HouseFurnitureType.OTHERWORLDY_THEME ? 1 : 2;
		return ConstructionUtils.SUP_TILE_DATA[themeId][sqId][cornerTile];
	}

	/**
	 * Switch RoomSets, palettes, etc. to dungeon variant.
	 * TODO: rewrite
	 */
	public static void enterDungeon(Player player) {
		// flag objects in all chunks for removal
		for (int i = 0; i < 13; i++)
			for (int i2 = 0; i2 < 13; i2++)
				player.getPacketSender().sendObjectsRemoval(i, i2, 0);

		player.setInHouseDungeon(true);
		player.getHouse().setConstructionBuildPosition(player.getPosition().clone());
		updateHouseInstance(player, ((HouseInstance) player.getArea()), player.inBuildingMode());
	}

	/**
	 * Return to surface from dungeon.
	 * TODO: rewrite
	 */
	public static void exitDungeon(Player p) {
		for (int i = 0; i < 13; i++)
			for (int i2 = 0; i2 < 13; i2++)
				p.getPacketSender().sendObjectsRemoval(i, i2, 0);
		p.setInHouseDungeon(false);
		p.getHouse().setConstructionBuildPosition(p.getPosition().clone());
		updateHouseInstance(p, ((HouseInstance) p.getArea()), p.inBuildingMode());
	}


	/**
	 * Has required items to build/upgrade furniture
	 * TODO: Rewrite
	 */
	public static String hasReqs(Player p, HouseFurnitureType f, HotSpotType hs) {
		if (p.getRights().isAdvancedStaff())
			return null;

		if (p.getSkillManager().getCurrentLevel(Skill.CONSTRUCTION) < f.getLevel()) {
			return "You need a Construction level of " + f.getLevel() + " to build this.";
		}
		for (int i1 = 0; i1 < f.getRequiredItems().length; i1++) {
			if (p.getInventory().getAmount(f.getRequiredItems()[i1][0]) < f.getRequiredItems()[i1][1]) {
				String s = ItemDefinition.forId(f.getRequiredItems()[i1][0]).getName();
				if (!s.endsWith("s") && f.getRequiredItems()[i1][1] > 1)
					s = s + "s";
				return "You need " + f.getRequiredItems()[i1][1] + "x " + s + " to build this.";
			}
		}
		if (f.getAdditionalSkillRequirements() != null) {
			for (int ii = 0; ii < f.getAdditionalSkillRequirements().length; ii++) {
				if (p.getSkillManager().getCurrentLevel(Skill.values()[
					f.getAdditionalSkillRequirements()[ii][0]]) < f.getAdditionalSkillRequirements()[ii][1]) {
					return "You need a " + Skill.values()[(f.getAdditionalSkillRequirements()[ii][0])].getName()
						+ " of at least " + f.getAdditionalSkillRequirements()[ii][1] + "" + " to build this.";
				}
			}
		}
		if (f.getFurnitureRequired() != -1) {
			HouseFurnitureType fur = HouseFurnitureType.forFurnitureId(f.getFurnitureRequired());
			int[] myTiles = getCurrentChunk(p);
			for (HouseFurniture pf : p.getHouse().getSurfaceFurniture()) {
				if (pf.getRoomX() == myTiles[0] - 1 && pf.getRoomY() == myTiles[1] - 1) {
					if (pf.getHotSpot(
						p.getCurrentRoomSet()[p.getPosition().getZ()][myTiles[0]
							- 1][myTiles[1] - 1].getRotation()) == hs) {
						if (pf.getFurnitureId() != fur.getFurnitureId()) {
							return "This is an upgradeable piece of furniture. (build the furniture before this first)";
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * Furniture build/upgrade action.
	 * TODO: rewrite
	 */
	public static boolean buildActions(Player p, HouseFurnitureType f, HotSpotType hs) {
		String s = hasReqs(p, f, hs);
		if (s != null) {
			p.sendMessage(s);
			return false;
		}

		for (int i = 0; i < f.getRequiredItems().length; i++) {
			ItemDefinition item = ItemDefinition.forId(f.getRequiredItems()[i][0]);
			if (item.isStackable())
				p.getInventory().delete(f.getRequiredItems()[i][0], f.getRequiredItems()[i][1]);
			else {
				for (int a = 0; a < f.getRequiredItems()[i][1]; a++) {
					p.getInventory().delete(f.getRequiredItems()[i][0], 1);
				}
			}
		}

		p.getSkillManager().addExperience(Skill.CONSTRUCTION, f.getXP());
		return true;
	}

	/**
	 * Checks for room from current tile. Player should be standing in a
	 * doorway, facing into the empty / existing room.
	 *
	 * @param player
	 * 				House owner.
	 */
	public static Optional<HouseRoom> checkForRoom(Player player) {
		int[] myTiles = getCurrentChunk(player);
		int xOnTile = getPlayerChunkX(myTiles, player);
		int yOnTile = getPlayerChunkY(myTiles, player);
		int direction = 0;
		final int LEFT = 0, DOWN = 1, RIGHT = 2, UP = 3;
		if (xOnTile == 0)
			direction = LEFT;
		if (yOnTile == 0)
			direction = DOWN;
		if (xOnTile == 7)
			direction = RIGHT;
		if (yOnTile == 7)
			direction = UP;
		int xOff = 0, yOff = 0;
		if (direction == LEFT) {
			xOff = -1;
		}
		if (direction == DOWN) {
			yOff = -1;
		}
		if (direction == RIGHT) {
			xOff = 1;
		}
		if (direction == UP) {
			yOff = 1;
		}

		Optional<HouseRoom> possibleRoom =
				 Optional.ofNullable(player.getCurrentRoomSet()[player.getPosition().getZ()%4][myTiles[0] - 1 + xOff][myTiles[1] - 1 + yOff]);

		if(possibleRoom.isEmpty())
			return possibleRoom;

		HouseRoom room = possibleRoom.get();

		if (room.getType() == ConstructionUtils.BUILDABLE || room.getType() == ConstructionUtils.EMPTY
			|| room.getType() == ConstructionUtils.DUNGEON_EMPTY)
			return Optional.empty();

		return possibleRoom;
	}

	/**
	 * Handle furniture remove action, find what hotspot this object belongs to.
	 * TODO: rewrite
	 */
	public static boolean handleRemoveClick(Player player, int objectX, int objectY, int objectId) {

		System.out.println("HandleRemoveClick: " + objectX + " " + objectY + " " + objectId);
		if (!HouseBuildingActions.INSTANCE.buildingChecks(player)) {
			return false;
		}
		if (objectId == 13126 || objectId == 13127 || objectId == 13128 || objectId == 13132)
			objectId = 13126;
		if (objectId == 13133 || objectId == 13134 || objectId == 13135 || objectId == 13136)
			objectId = 13133;
		if (objectId == 13137 || objectId == 13138 || objectId == 13139 || objectId == 13140)
			objectId = 13137;
		if (objectId == 13145 || objectId == 13147)
			objectId = 13145;
		if (objectId == 13142 || objectId == 13143 || objectId == 13144)
			objectId = 13142;
		if (objectId == 13588 || objectId == 13589 || objectId == 13590)
			objectId = 13588;
		if (objectId == 13591 || objectId == 13592 || objectId == 13593)
			objectId = 13591;
		if (objectId == 13594 || objectId == 13595 || objectId == 13596)
			objectId = 13594;
		if (objectId > 13456 && objectId <= 13476)
			objectId = 13456;
		if (objectId > 13449 && objectId <= 13455)
			objectId = 13449;
		if (objectId > 13331 && objectId <= 13337 || objectId == 13373)
			objectId = 13331;
		if (objectId > 13313 && objectId <= 13327)
			objectId = 13313;

		HouseFurnitureType furniture = HouseFurnitureType.forFurnitureId(objectId);

		if (furniture == null)
			return false;

		if (furniture == HouseFurnitureType.EXIT_PORTAL || furniture == HouseFurnitureType.EXIT_PORTAL_) {
			int portalAmt = 0;
			for (HouseFurniture pf : player.getHouse().getSurfaceFurniture()) {
				HouseFurnitureType ff = HouseFurnitureType.forFurnitureId(pf.getFurnitureId());
				if (ff == HouseFurnitureType.EXIT_PORTAL || ff == HouseFurnitureType.EXIT_PORTAL_)
					portalAmt++;
			}
			if (portalAmt < 2) {
				player.getPacketSender().sendMessage("You need at least 1 exit portal in your house");
				return true;
			}
		}

		int[] myTiles = getCurrentChunk(player);
		int roomRot = player.getCurrentRoomSet()[player.getPosition().getZ()][myTiles[0] - 1][myTiles[1] - 1].getRotation();
		HouseRoom houseRoom = player.getCurrentRoomSet()[player.getPosition().getZ()][myTiles[0] - 1][myTiles[1] - 1];

		ArrayList<HotSpotType> hotspots = HotSpotType.forObjectId_3(furniture.getHotSpotId());

		if (hotspots.isEmpty())
			return false;

		HotSpotType hotspot = null;
		if (hotspots.size() == 1)
			hotspot = hotspots.get(0);
		else {
			for (HotSpotType find : hotspots) {
				int actualX = ConstructionUtils.BASE_X + (myTiles[0] * 8);
				actualX += ConstructionUtils.getXOffsetForObjectId(find.getObjectId(), find, roomRot);
				int actualY = ConstructionUtils.BASE_Y + (myTiles[1] * 8);
				actualY += ConstructionUtils.getYOffsetForObjectId(find.getObjectId(), find, roomRot);
				if (objectX == actualX && objectY == actualY) {
					hotspot = find;
					break;
				}
			}
		}
		if (objectId == 13331) {
			hotspot = HotSpotType.OUBLIETTE_FLOOR_1;
		}
		if (objectId == 13313) {
			hotspot = HotSpotType.OUBLIETTE_CAGE_1;
		}
		if (objectId == 13126 || objectId == 13127 || objectId == 13128 || objectId == 13132 || objectId == 13133
			|| objectId == 13134 || objectId == 13135 || objectId == 13136 || objectId == 13137 || objectId == 13138
			|| objectId == 13139 || objectId == 13140 || objectId == 13145 || objectId == 13147 || objectId == 13142
			|| objectId == 13143 || objectId == 13144) {
			hotspot = HotSpotType.COMBAT_RING_1;
		}
		if (objectId == 13456)
			if (houseRoom.getType() == ConstructionUtils.FORMAL_GARDEN)
				hotspot = HotSpotType.FORMAL_HEDGE_1;
		if (objectId == 13449)
			if (houseRoom.getType() == ConstructionUtils.FORMAL_GARDEN)
				hotspot = HotSpotType.FORMAL_FENCE;
		if (objectId == 15270 || objectId == 15273 || objectId == 15274 || objectId >= 13588 && objectId <= 13597) {
			if (houseRoom.getType() == ConstructionUtils.CHAPEL)
				hotspot = HotSpotType.CHAPEL_RUG_1;
			if (houseRoom.getType() == ConstructionUtils.PARLOUR)
				hotspot = HotSpotType.PARLOUR_RUG_3;
			if (houseRoom.getType() == ConstructionUtils.SKILL_ROOM || houseRoom.getType() == ConstructionUtils.SKILL_HALL_DOWN
				|| houseRoom.getType() == ConstructionUtils.QUEST_ROOM
				|| houseRoom.getType() == ConstructionUtils.QUEST_HALL_DOWN
				|| houseRoom.getType() == ConstructionUtils.DUNGEON_STAIR_ROOM
				|| houseRoom.getType() == ConstructionUtils.SKILL_HALL_DOWN)
				hotspot = HotSpotType.SKILL_HALL_RUG_3;
			if (houseRoom.getType() == ConstructionUtils.BEDROOM)
				hotspot = HotSpotType.BEDROOM_RUG_3;
		}
		if (objectId == 29262 || objectId == 5907 || objectId == 29267) {
			hotspot = HotSpotType.SUPERIOR_GARDEN_FENCE2;
		}
		doFurniturePlace(hotspot, furniture, hotspots, myTiles, objectX, objectY, roomRot, player, true, player.getPosition().getZ());
		player.performAnimation(new Animation(3685));

		removeHouseFurniture(player, myTiles, hotspot);
		return true;
	}

	/**
	 * Remove this object from furniture list.
	 * TODO: Rewrite
	 */
	public static void removeHouseFurniture(Player p, int[] myTiles, HotSpotType hs) {
		Iterator<HouseFurniture> iterator = p.getCurrentFurnitureSet().iterator();

		while (iterator.hasNext()) {
			HouseFurniture pf = iterator.next();
			if (pf.getRoomX() != myTiles[0] - 1 || pf.getRoomY() != myTiles[1] - 1
				|| pf.getRoomZ() != (p.getPosition().getZ()))
				continue;
			if (pf.getStandardXOff() == hs.getXOffset() && pf.getStandardYOff() == hs.getYOffset())
				iterator.remove();
		}
	}

	/**
	 * Create a new room from interface. Should be facing into new room.
	 * TODO: rewrite
	 */
	public static void createRoom(int roomType, Player p, int toHeight) {

		HouseRoomType rd = HouseRoomType.forID(roomType);

		if (rd == null) {
			System.out.println("No HouseRoomType for type: " + roomType);
			return;
		}

		if (p.getInventory().getAmount(995) < rd.getCost()) {
			p.sendMessage("You need " + rd.getCost() + " coins to build this");
			return;
		}

		boolean isDungeon = ConstructionUtils.isDungeonRoom(roomType);

		if (!p.isInHouseDungeon()) {
			if (isDungeon && toHeight != 102 && toHeight != 103) {
				p.sendMessage("You can only build this houseRoom in your dungeon.");
				return;
			}
		} else {
			if (!isDungeon) {
				p.sendMessage("You can only build this houseRoom on the surface");
				return;
			}
		}

		int[] myTiles = getCurrentChunk(p);

		if (myTiles == null) {
			return;
		}

		int xOnTile = getPlayerChunkX(myTiles, p);
		int yOnTile = getPlayerChunkY(myTiles, p);

		int direction = 0;

		final int LEFT = 0, DOWN = 1, RIGHT = 2, UP = 3, SAME = 4;

		if (xOnTile == 0)
			direction = LEFT;
		if (yOnTile == 0)
			direction = DOWN;
		if (xOnTile == 7)
			direction = RIGHT;
		if (yOnTile == 7)
			direction = UP;

		int rotation = HouseRoomType.getFirstElegibleRotation(rd, direction);

		HouseRoom houseRoom = new HouseRoom(rotation, roomType, p.getHouse().getStyle().ordinal(), myTiles[0] - 1, myTiles[1] - 1, p.getZ());
		PaletteTile tile = new PaletteTile(houseRoom.getX(), houseRoom.getY(), houseRoom.getZ(), houseRoom.getRotation());

		p.sendMessage("Tile: " + tile.getZ() + " style: " + p.getHouse().getStyle().ordinal());

		int xOff = 0, yOff = 0;
		if (direction == LEFT) {
			xOff = -1;
		}
		if (direction == DOWN) {
			yOff = -1;
		}
		if (direction == RIGHT) {
			xOff = 1;
		}
		if (direction == UP) {
			yOff = 1;
		}

		int buildChunkX = (myTiles[0] - 1) + xOff;
		int buildChunkY = (myTiles[1] - 1) + yOff;


		if (toHeight == 1) {
			HouseRoom r = p.getHouse().getHouseHouseRooms()[0][buildChunkX][buildChunkY];
			if (r.getType() == ConstructionUtils.EMPTY || r.getType() == ConstructionUtils.BUILDABLE
				|| r.getType() == ConstructionUtils.GARDEN || r.getType() == ConstructionUtils.FORMAL_GARDEN) {
				p.sendMessage("You need a foundation to build there");
				return;
			}
		}

		if(buildChunkX <3 || buildChunkY <3
			|| buildChunkX >= 10 || buildChunkY >= 10) {
			p.sendMessage("You may not build a houseRoom next to the ambivalent void.");
			return;
		}

		HouseInstance house = (HouseInstance) p.getArea();

		Palette palette = isDungeon ? house.getDungeonPalette() : house.getSurfacePalette();
		palette.setTile(buildChunkX, buildChunkY, toHeight, tile);

		p.getCurrentRoomSet()[toHeight][buildChunkX][buildChunkY]
			= new HouseRoom(rotation, roomType, 0, buildChunkX, buildChunkY, toHeight);

		p.getHouse().setConstructionBuildPosition(p.getPosition().clone());

		// sendPOH(p, ((HouseInstance) p.getArea()).getHouseOwner(), true, false);
		// p.getPacketSender().constructSingleMapChunk(tile, buildChunkX, buildChunkY, toHeight);
		// p.getPacketSender().constructMapRegion(((HouseInstance) p.getArea()).getSurfacePalette());
		System.out.println("Building room at " + toHeight + " " + buildChunkX + " " + buildChunkY);
		p.getPacketSender().sendInterfaceRemoval();
		updateHouseInstance(p, ((HouseInstance) p.getArea()), p.inBuildingMode());
	}


	/**
	 * Rotate a room given direction.
	 *
	 * 0 = counterclock wise
	 * 1 = clockwise
	 *
	 * TODO: rewrite
	 */
	public static void rotateRoom(Player p, boolean clockwise) {
		if (p.getArea() == null || ((HouseInstance) p.getArea()).getHouseOwner() != p)
			return;
		int[] myTiles = getCurrentChunk(p);
		int xOnTile = getPlayerChunkX(myTiles, p);
		int yOnTile = getPlayerChunkY(myTiles, p);
		int direction = 0;
		final int LEFT = 0, DOWN = 1, RIGHT = 2, UP = 3;
		if (xOnTile == 0)
			direction = LEFT;
		if (yOnTile == 0)
			direction = DOWN;
		if (xOnTile == 7)
			direction = RIGHT;
		if (yOnTile == 7)
			direction = UP;
		int xOff = 0, yOff = 0;
		if (direction == LEFT) {
			xOff = -1;
		}
		if (direction == DOWN) {
			yOff = -1;
		}
		if (direction == RIGHT) {
			xOff = 1;
		}
		if (direction == UP) {
			yOff = 1;
		}
		int chunkX = (myTiles[0] - 1) + xOff;
		int chunkY = (myTiles[1] - 1) + yOff;
		HouseRoom r = p.getCurrentRoomSet()[p.getPosition().getZ()][chunkX][chunkY];
		HouseRoomType rd = HouseRoomType.forID(r.getType());
		int toRot = (clockwise ?
			HouseRoomType.getNextEligibleRotationClockWise(rd, direction, r.getRotation())
			: HouseRoomType.getNextEligibleRotationCounterClockWise(rd, direction, r.getRotation()));

		PaletteTile tile = new PaletteTile(rd.getX(), rd.getY(), 0, toRot);
		p.getPacketSender().sendObjectsRemoval(chunkX, chunkY, p.getPosition().getZ());
		HouseInstance house = (HouseInstance) p.getArea();

		if (p.isInHouseDungeon()) {
			house.getDungeonPalette().setTile(chunkX, chunkY, 0, tile);
		} else {
			house.getSurfacePalette().setTile(chunkX, chunkY, p.getPosition().getZ(), tile);
		}
		p.getCurrentRoomSet()[p.getPosition().getZ()][chunkX][chunkY].setRotation(toRot);
		p.getHouse().setConstructionBuildPosition(p.getPosition().clone());

		// TODO: Destory instance
		// getInstance(p).destruct();
		generateSurfacePalette(p);
		updateHouseInstance(p, ((HouseInstance) p.getArea()), p.inBuildingMode());

		// TODO LOOP CONVERSATION
	}

	/**
	 * Handle room delete from dialogue, should be in doorway looking into
	 * room to be delete.
	 *
	 * TODO: rewrite
	 *
	 * @param player
	 * 			House owner.
	 */
	public static void deleteRoom(Player player) {
		int[] myTiles = getCurrentChunk(player);
		int xOnTile = getPlayerChunkX(myTiles, player);
		int yOnTile = getPlayerChunkY(myTiles, player);
		int direction = 0;
		int z = player.getZ();

		final int LEFT = 0, DOWN = 1, RIGHT = 2, UP = 3;

		if (xOnTile == 0)
			direction = LEFT;
		if (yOnTile == 0)
			direction = DOWN;
		if (xOnTile == 7)
			direction = RIGHT;
		if (yOnTile == 7)
			direction = UP;

		int roomType = player.isInHouseDungeon() ? ConstructionUtils.DUNGEON_EMPTY : ConstructionUtils.EMPTY;
		HouseRoom houseRoom = new HouseRoom(0, roomType, 0, myTiles[0] - 1, myTiles[1] - 1, player.getZ());

		int xOff = 0, yOff = 0;

		if (direction == LEFT) {
			xOff = -1;
		}

		if (direction == DOWN) {
			yOff = -1;
		}

		if (direction == RIGHT) {
			xOff = 1;
		}

		if (direction == UP) {
			yOff = 1;
		}

		int chunkX = (myTiles[0] - 1) + xOff;
		int chunkY = (myTiles[1] - 1) + yOff;
		HouseRoom room = player.getCurrentRoomSet()[z][chunkX][chunkY];

		if (room.getType() == ConstructionUtils.GARDEN || room.getType() == ConstructionUtils.FORMAL_GARDEN) {
			int gardenAmt = 0;
			for (int h = 0; h < player.getHouse().getHouseHouseRooms().length; h++) {
				for (int x = 0; x < player.getHouse().getHouseHouseRooms()[h].length; x++) {
					for (int y = 0; y < player.getHouse().getHouseHouseRooms()[h][x].length; y++) {
						HouseRoom r1 = player.getHouse().getHouseHouseRooms()[h][x][y];
						if (r1 == null)
							continue;
						if (r1.getType() == ConstructionUtils.GARDEN || r1.getType() == ConstructionUtils.FORMAL_GARDEN) {
							gardenAmt++;
						}
					}
				}
			}
			if (gardenAmt < 2) {
				player.getPacketSender().sendMessage("You need atleast 1 garden or formal garden");
				player.getPacketSender().sendInterfaceRemoval();
				return;
			}
		}

		//player.getPacketSender().sendObjectsRemoval(chunkX, chunkY, player.getPosition().getZ());
		room.removeFurniture(player);
		HouseInstance house = (HouseInstance) player.getArea();

		if (player.isInHouseDungeon()) {
			// empty dungeon
			house.getDungeonPalette().setTile(chunkX, chunkY, z, new PaletteTile(chunkX, chunkY, z));
			getHouseOwner(player).getHouse().getDungeonRooms()[z][chunkX][chunkY] = new HouseRoom(0, ConstructionUtils.DUNGEON_EMPTY, 0, chunkX, chunkY, 0);
		} else if(player.getZ() > 0) {
			// thin air
			house.getSurfacePalette().setTile(chunkX, chunkY, z, null);
			player.getCurrentRoomSet()[z][chunkX][chunkY] = null;
		} else {
			// empty grass
			house.getSurfacePalette().setTile(chunkX, chunkY, z, new PaletteTile(chunkX, chunkY, z));
			player.getCurrentRoomSet()[z][chunkX][chunkY] = new HouseRoom(0, ConstructionUtils.EMPTY, 0, chunkX, chunkY, 0);
		}

		player.getHouse().setConstructionBuildPosition(player.getPosition().clone());

		// TODO: Destruct instance
		// getInstance(p).destruct();

		generateSurfacePalette(player);
		updateHouseInstance(player, ((HouseInstance) player.getArea()), player.inBuildingMode());
		Iterator<HouseFurniture> iterator = player.getCurrentFurnitureSet().iterator();
		while (iterator.hasNext()) {
			HouseFurniture pf = iterator.next();
			if (pf.getRoomX() == chunkX && pf.getRoomY() == chunkY && pf.getRoomZ() == z)
				iterator.remove();
		}
		Iterator<HousePortal> portals = player.getHouse().getHousePortals().iterator();
		while (portals.hasNext()) {
			HousePortal port = portals.next();
			if (port.getRoomX() == chunkX && port.getRoomY() == chunkY && port.getRoomZ() == z)
				iterator.remove();
		}
	}

	/**
	 * Generates an item set for the clicked {@link HotSpotType} and writes it.
	 */
	public static void sendFurnitureBuilderItemSet(ArrayList<HouseFurnitureType> items, Player c) {
		ArrayList<Item> itemArray = new ArrayList<Item>();
		for (HouseFurnitureType item : items)
			itemArray.add(new Item(item.getItemId(), 1));

		c.getPacketSender().sendInterfaceItems(138274, itemArray);
	}

	/**
	 * Sends furniture builder crosses and sends them. Red "X" that appears on
	 * furniture that cannot be built.
	 *
	 * TODO: rewrite
	 */
	public static void sendFurnitureBuilderCrosses(ArrayList<HouseFurnitureType> items, Player c, HotSpotType hs) {
		int i = 1000;

		for (HouseFurnitureType f : items) {
			c.getPacketSender().sendString(138275 + (i - 1000) * 6, f.toString());
			c.getPacketSender().sendString(138280 + (i - 1000) * 6, "Level: " + f.getLevel());
			int i2 = 0;
			boolean canMake = true;
			for (int i1 = 0; i1 < f.getRequiredItems().length; i1++) {
				c.getPacketSender().sendString((138276 + i2) + (i - 1000) * 6, f.getRequiredItems()[i1][1] + " x "
					+ ItemDefinition.forId(f.getRequiredItems()[i1][0]).getName());
				if (c.getInventory().getAmount(f.getRequiredItems()[i1][0]) < f.getRequiredItems()[i1][1]) {
					i2++;
					canMake = false;
					continue;
				}
				i2++;
			}
			if (f.getAdditionalSkillRequirements() != null) {
				for (int ii = 0; ii < f.getAdditionalSkillRequirements().length; ii++) {
					c.getPacketSender().sendString((138276 + (i2++)) + (i - 1000) * 6,
						Skill.values()[(f.getAdditionalSkillRequirements()[ii][0])].getName() + " "
							+ f.getAdditionalSkillRequirements()[ii][1]);
					if (c.getSkillManager().getCurrentLevel(Skill.values()[(
						f.getAdditionalSkillRequirements()[ii][0])]) < f.getAdditionalSkillRequirements()[ii][1]) {
						canMake = false;
					}
				}
			}
			if (f.getFurnitureRequired() != -1) {
				HouseFurnitureType fur = HouseFurnitureType.forFurnitureId(f.getFurnitureRequired());
				if (fur == null) {
					c.getPacketSender().sendMessage("Error on this interface: req" + f.getFurnitureRequired());
					continue;
				}
				c.getPacketSender().sendString((138276 + (i2++)) + (i - 1000) * 6, fur.toString());
				if (canMake) {
					canMake = false;
					int[] myTiles = getCurrentChunk(c);
					for (HouseFurniture pf : c.getHouse().getSurfaceFurniture()) {
						if (pf.getRoomX() == myTiles[0] - 1 && pf.getRoomY() == myTiles[1] - 1) {
							if (pf.getHotSpot(
								c.getCurrentRoomSet()[c.getPosition().getZ()][myTiles[0]
									- 1][myTiles[1] - 1].getRotation()) == hs) {
								if (pf.getFurnitureId() != fur.getFurnitureId()) {
									canMake = false;
								} else {
									canMake = true;
								}
							}
						}
					}
				}
			}
			if (canMake)
				c.getPacketSender().sendConfig(i, 0);
			else
				c.getPacketSender().sendConfig(i, 1);
			for (; i2 < 4; i2++) {
				c.getPacketSender().sendString((138276 + i2) + (i - 1000) * 6, "");

			}
			i++;
		}
		for (; i < 1008; i++) {
			c.getPacketSender().sendString(138275 + (i - 1000) * 6, "");
			c.getPacketSender().sendString(138276 + (i - 1000) * 6, "");
			c.getPacketSender().sendString(138277 + (i - 1000) * 6, "");
			c.getPacketSender().sendString(138278 + (i - 1000) * 6, "");
			c.getPacketSender().sendString(138279 + (i - 1000) * 6, "");
			c.getPacketSender().sendString(138280 + (i - 1000) * 6, "");
			c.getPacketSender().sendConfig(i, 0);
		}
	}

	/**
	 * Handle furniture item container click in furniture builder.
	 * TODO: rewrite
	 */
	public static void buildFurnitureFromInterface(Player player, int itemID, int slot) {
		if (player.getArea() == null || getHouseOwner(player) != player) {
			System.out.println("[Con] error: no instance building from furniture interface [p: " + player.getUsername() + "]");
			return;
		}

		if (!player.inBuildingMode()) {
			System.out.println("[Con] error: no building mode building from furniture interface [p: " + player.getUsername() + "]");
			return;
		}

		HouseFurnitureType f = HouseFurnitureType.forItemId(itemID, slot);

		player.getPacketSender().sendInterfaceRemoval();

		Optional<HotSpotType> hs = verifyHotspot(player, player.getHouse().getBuildFurnitureId());

		if(hs.isEmpty()) {
			return;
		}

		HotSpotType s = hs.get();

		if (!buildActions(player, f, s))
			return;

		int[] chunk = getCurrentChunk(player);
		HouseRoom room = getHouseRoomAt(player, player.getPosition()).get();

		int actualX = ConstructionUtils.BASE_X + (chunk[0] * 8);
		actualX += ConstructionUtils.getXOffsetForObjectId(f.getFurnitureId(), s, room.getRotation());

		int actualY = ConstructionUtils.BASE_Y + (chunk[1] * 8);
		actualY += ConstructionUtils.getYOffsetForObjectId(f.getFurnitureId(), s, room.getRotation());

		if (s.getRoomType() != room.getType() && s.getCarpetDimensions() == null) {
			player.getPacketSender().sendMessage("You can't build this furniture in this room.");
			return;
		}

		if (f.getFurnitureId() == HouseFurnitureType.DUNGEON_ENTRANCE.getFurnitureId()) {
			HouseRoom r = player.getHouse().getDungeonRooms()[0][(chunk[0])][(chunk[1])];

			if (r == null || r.getType() == ConstructionUtils.EMPTY) {
				// no dungeon yet
				newDungeon(player, chunk[0] - 1, chunk[1] - 1);

				if(PlayerUtil.isDeveloper(player)) {
					player.sendMessage("No dungeon detected. Creating at chunk " + (chunk[0] - 1) + ", " + (chunk[1] - 1));
				}
			} else if (r.getType() != ConstructionUtils.DUNGEON_EMPTY) {
				String e = HouseRoomType.forID(r.getType()).toString();
				player.sendMessage("You have " + ConstructionUtils.anOrA(e) + " " + e + " built below, you must remove it to build a dungeon entrance here.");
				return;
			}
		}

		doFurniturePlace(s, f,
				HotSpotType.forObjectId_2(player.getHouse().getBuildFurnitureId()),
				chunk,
				actualX,
				actualY,
				room.getRotation(),
				player,
				false,
				player.getPosition().getZ());

		HouseFurniture pf = new HouseFurniture(chunk[0] - 1, chunk[1] - 1, player.getZ(), s.getHotSpotId(),
			f.getFurnitureId(), s.getXOffset(), s.getYOffset());
		room.addFurniture(pf);
		player.getCurrentFurnitureSet().add(pf);
		player.setPositionToFace(new Position(actualX, actualY));
		player.updateAppearance();
		player.performAnimation(new Animation(3684));
	}

	/**
	 * Calculates room chunk boundaries.
	 * TODO: convert to Position
	 *
	 * @return int array containing Position values.
	 */
	public static int[] getRoomChunkAt(Position pos) {
		for (int x = 0; x < 13; x++) {
			for (int y = 0; y < 13; y++) {
				int minX = ((ConstructionUtils.BASE_X) + (x * 8));
				int maxX = ((ConstructionUtils.BASE_X + 7) + (x * 8));
				int minY = ((ConstructionUtils.BASE_Y) + (y * 8));
				int maxY = ((ConstructionUtils.BASE_Y + 7) + (y * 8));
				if (pos.getX() >= minX && pos.getX() <= maxX && pos.getY() >= minY
						&& pos.getY() <= maxY) {
					return new int[]{x, y};
				}
			}
		}

		System.out.println("Critical error: Could not find player's room chunk at " + pos.toString());
		try {
			throw new Exception();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Calculates room chunk position.
	 * TODO: convert to Position
	 *
	 * @return int array containing Position values.
	 */
	public static int[] getCurrentChunk(Player p) {
		return getRoomChunkAt(p.getPosition());
	}

	/**
	 * Calculates room chunk boundaries for given coordinates.
	 * TODO: rewrite
	 *
	 * @return int array containing Position values.
	 */
	public static int[] getMyChunkFor(int findX, int findY) {
		for (int x = 0; x < 13; x++) {
			for (int y = 0; y < 13; y++) {
				int minX = ((ConstructionUtils.BASE_X) + (x * 8));
				int maxX = ((ConstructionUtils.BASE_X + 7) + (x * 8));
				int minY = ((ConstructionUtils.BASE_Y) + (y * 8));
				int maxY = ((ConstructionUtils.BASE_Y + 7) + (y * 8));
				if (findX >= minX && findX <= maxX && findY >= minY && findY <= maxY) {
					return new int[]{x, y};
				}
			}
		}
		return null;
	}

	/**
	 * Finds the player's X coordinate in this chunk. (1 - 8)
	 * @param tile
	 * 			Contains position values for this chunk.
	 * @param player
	 * @return
	 */
	public static int getPlayerChunkX(int[] tile, Player player) {
		return getChunkX(tile, player.getPosition());
	}
	/**
	 * Finds the player's Y coordinate in this chunk. (1 - 8)
	 * @param tile
	 * 			Contains position values for this chunk.
	 * @param player
	 * @return
	 */
	public static int getPlayerChunkY(int[] tile, Player player) {
		return getChunkY(tile, player.getPosition());
	}

	public static int getChunkX(int[] tile, Position pos) {
		int baseX = ConstructionUtils.BASE_X + (tile[0] * 8);
		return pos.getX() - baseX;
	}

	public static int getChunkY(int[] tile, Position pos) {
		int baseY = ConstructionUtils.BASE_Y + (tile[0] * 8);
		return pos.getY() - baseY;
	}
	/**
	 * Enter a friend's house at house portal.
	 * @param player
	 * 				Player entering house.
	 * @param friendUsername
	 * 				Friend name entered in dialog.
	 */
	public static void enterFriendsHouse(Player player, String friendUsername) {
		Optional<Player> possibleFriend = World.findPlayerByName(friendUsername);

		player.sendMessage("Entering friend " + friendUsername);
		if (!possibleFriend.isPresent()) {
			player.getPacketSender().sendMessage("That player is currently offline.");
			return;
		}

		Player friend = possibleFriend.get();
		HouseInstance friendsHouse = HouseInstance.get(friend);

		if (!friend.getHouse().hasHouse()) {
			player.getPacketSender().sendMessage("That player does not own a house.");
			return;
		}

		if (friendsHouse == null) {
			player.getPacketSender().sendMessage("That player is not in their house right now.");
			return;
		}

		if (friend.inBuildingMode()) {
			player.getPacketSender().sendMessage("That player is currently in building mode.");
			return;
		}
		if(friendsHouse.isLocked() && !PlayerUtil.isStaff(player)) {
			player.getPacketSender().sendMessage("That player has locked their portal. Nobody may enter.");
			return;
		}
		updateHouseInstance(player, friendsHouse, false);
	}

}
