package com.grinder.game.content.skill.skillable.impl.cons;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.instanced.HouseInstance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

/**
 * @author Simplex
 * @since Mar 27, 2020
 */
public class HouseRoom {

	private int rotation, type, theme;
	private int x, y, z;
	private transient boolean[] doors;
	private ArrayList<HouseFurniture> furniture;

	public HouseRoom(int rotation, int type, int theme, int x, int y, int z) {
		this.rotation = rotation;
		this.type = type;
		this.theme = theme;
		this.xSlot = x;
		this.ySlot = y;
		this.zSlot = z;
		this.furniture = new ArrayList<>();
		init();
	}

	private void init()
	{
		HouseRoomType rd = HouseRoomType.forID(type);
		x = rd.getX();
		y = rd.getY();
		doors = rd.getRotatedDoors(rotation);
	}
	public boolean[] getDoors()
	{
		return doors;
	}
	public int getX()
	{
		return x;
	}
	public int getY()
	{
		return y;
	}
	public int getZ()
	{
		return z;
	}
	public int getType()
	{
		return type;
	}
	public int getRotation()
	{
		return rotation;
	}
	public void setRotation(int rotation)
	{
		this.rotation = rotation;
	}
	public int getTheme() {
		return this.theme;
	}

	public ArrayList<HouseFurniture> getFurniture() {
		if (furniture == null) {
			furniture = new ArrayList<>();
		}
		return furniture;
	}

	public void addFurniture(HouseFurniture furnitureToAdd) {
		furniture.add(furnitureToAdd);
	}

	public void removeFurniture(Player player) {
		Iterator<HouseFurniture> roomFurnitureIterator = furniture.iterator();
		while (roomFurnitureIterator.hasNext()) {
			HouseFurniture pf = roomFurnitureIterator.next();
			System.out.println(pf.toString());
			if (pf.getRoomZ() != getZ()) {
				System.out.println("hier 1");
				continue;
			}
			System.out.println(getX() + " " + getY() + " " + getZ() + " " + pf.getRoomX() + " " + pf.getRoomY() + " " + pf.getRoomZ());
			if (!new Position(xSlot, ySlot, zSlot).equals(new Position(pf.getRoomX(), pf.getRoomY(), pf.getRoomZ()))) {
				System.out.println("hier 2");
				continue;
			}
			if(HouseInstance.get(player) == null || HouseInstance.get(player).getHouseOwner() != player) {
				System.out.println("hier 3");
				continue;
			}
			HotSpotType hs = HotSpotType.forHotSpotIdAndCoords
					(pf.getHotSpotId(), pf.getStandardXOff(), pf.getStandardYOff(),
							this);
			if (hs == null) {
				System.out.println("[Con] Error " + pf.getFurnitureId() + " - No hotspot found");
				return;
			}
			//int rotation = hs.getRotation(getRotation());
			System.out.println("Removing " + pf.toString());
			int actualX = ConstructionUtils.BASE_X + (pf.getRoomX() + 1) * 8;
			actualX += ConstructionUtils.getXOffsetForObjectId(pf.getFurnitureId(), hs, getRotation());
			int actualY = ConstructionUtils.BASE_Y + (pf.getRoomY() + 1) * 8;
			actualY += ConstructionUtils.getYOffsetForObjectId(pf.getFurnitureId(), hs, getRotation());

			Optional<GameObject> furnitureOpt = ObjectManager.findDynamicObjectAt(pf.getFurnitureId(), new Position(actualX, actualY, player.getZ()));
			furnitureOpt.ifPresent(gameObject -> ObjectManager.remove(gameObject, true));
			roomFurnitureIterator.remove();
		}
	}

	@Override
	public String toString() {
		return String.format("[HouseRoom X=%d Y=%d Z=%d]", x, y, z);
	}

	private int xSlot = -1,  ySlot = -1,  zSlot = -1;
}
