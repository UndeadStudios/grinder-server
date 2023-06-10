package com.grinder.game.model.item.container;

import com.grinder.game.model.item.Item;

public class SlotItem extends Item {

	private int itemSlot;
	
	public SlotItem() {
		super(0, 0);
	}
	
	public SlotItem(int itemID, int amount, int itemSlot) {
		super(itemID, amount);
		this.itemSlot = itemSlot;
	}

	public int getItemSlot() {
		return itemSlot;
	}

	public void setItemSlot(int itemSlot) {
		this.itemSlot = itemSlot;
	}

	@Override
	public String toString() {
		return "SlotItem [itemSlot=" + itemSlot + ", itemID=" + getId() + ", amount=" + getAmount() + "]";
	}

}
