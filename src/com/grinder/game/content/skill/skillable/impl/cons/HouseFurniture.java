package com.grinder.game.content.skill.skillable.impl.cons;

/**
 *
 * @author Owner Blade
 *
 */
public class HouseFurniture {

	private int roomX, roomY, roomZ, hotSpotId, furnitureId, standardXOff, standardYOff;
	public HouseFurniture(int roomX, int roomY, int roomZ, int hotSpotId, int furnitureId, int standardXOff, int standardYOff) {
		this.roomX = roomX;
		this.roomY = roomY;
		this.roomZ = roomZ;
		this.hotSpotId = hotSpotId;
		this.furnitureId = furnitureId;
		this.standardXOff = standardXOff;
		this.standardYOff = standardYOff;
	}
	public HotSpotType getHotSpot(int roomRot) {
		return HotSpotType.forObjectIdAndCoords(furnitureId, standardXOff, standardYOff);
	}
	public int getRoomY() {
		return roomY;
	}
	public void setRoomY(int roomY) {
		this.roomY = roomY;
	}
	public int getHotSpotId() {
		return hotSpotId;
	}
	public void setHotSpotId(int hotSpotId) {
		this.hotSpotId = hotSpotId;
	}
	public int getFurnitureId() {
		return furnitureId;
	}
	public void setFurnitureId(int furnitureId) {
		this.furnitureId = furnitureId;
	}
	public int getRoomX() {
		return roomX;
	}
	public void setRoomX(int roomX) {
		this.roomX = roomX;
	}
	public int getStandardXOff() {
		return standardXOff;
	}
	public void setStandardXOff(int standardXOff) {
		this.standardXOff = standardXOff;
	}
	public int getStandardYOff() {
		return standardYOff;
	}
	public void setStandardYOff(int standardYOff) {
		this.standardYOff = standardYOff;
	}
	public int getRoomZ() {
		return roomZ;
	}
	public void setRoomZ(int roomZ) {
		this.roomZ = roomZ;
	}

	@Override
	public String toString() {
		return "[ID=" + getFurnitureId() + "][ROOM X=" + getRoomX() + " Y=" + getRoomY() + " Z=" + getRoomZ() + "]";
	}

}
