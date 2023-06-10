package com.grinder.game.content.skill.skillable.impl.farming;

import com.google.gson.annotations.Expose;
import com.grinder.game.World;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.ClippedMapObjects;
import com.grinder.game.model.Animation;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.Position;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;

public class CompostBin {

	private Player player;


	private Task task;

	@Expose
	private BinState binState;

	@Expose
	private int binWeeds;

	@Expose
	private Long closingTime = System.currentTimeMillis();

	@Expose
	private boolean canOpen;

	@Expose
	BinType binType;

	public CompostBin(BinType binType) {
		this.binType = binType;
		this.setBinState(BinState.EMPTY);
	}

	public void init(Player player){
		this.player = player;
//		binObject = DynamicGameObject.createLocal(getBinState().getObjectId(), binType.getPosition(), 10, binType.getFace());
//		binObject.addTo(player);
		transformBinObject(getBinState());
	}

	public void process() {
		if (Misc.elapsed(getClosingTime(), 600_000)) {
			if (!isCanOpen() && (getBinState().equals(BinState.CLOSED) || getBinState().equals(BinState.CLOSED_SUPER))) {
				player.sendMessage("Your "+ binType.name().toLowerCase().replace("_", " ") + " has finished composting.");
				setCanOpen(true);
			}
		}
	}

	void reloadObject() {
		if (ClippedMapObjects.USE_NEW_OBJECT_UPDATES) {
//			World.deSpawn(binObject);
//			World.addObject(binObject);
		} else {
//			player.getPacketSender().sendObjectRemoval(binObject);
//			player.getPacketSender().sendObject(binObject);
		}
	}

	void compostPotion(Item item) {
		if (binState.equals(BinState.COMPOSTED)) {
			transformBinObject(BinState.COMPOSTED_SUPER);
			
			switch (item.getId()) {
				case ItemID.COMPOST_POTION_4_:
					player.getInventory().delete(item);
					player.getInventory().add(ItemID.COMPOST_POTION_3_, 1);
					break;
				case ItemID.COMPOST_POTION_3_:
					player.getInventory().delete(item);
					player.getInventory().add(ItemID.COMPOST_POTION_2_, 1);
					break;
				case ItemID.COMPOST_POTION_2_:
					player.getInventory().delete(item);
					player.getInventory().add(ItemID.COMPOST_POTION_1_, 1);
					break;
				case ItemID.COMPOST_POTION_1_:
					player.getInventory().delete(item);
					player.getInventory().add(ItemID.EMPTY_VIAL, 1);
					break;
			}
			player.sendMessage("You turn the compost into super compost.");
		} else
			player.sendMessage("The compost bin is already super compost, or is not yet composted.");
	}
	
	void openBin() {
		if (binState == BinState.CLOSED_SUPER)
			transformBinObject(BinState.COMPOSTED_SUPER);
		else
			transformBinObject(BinState.COMPOSTED);
		setCanOpen(false);
		player.sendMessage("You open the Composting bin.");
	}
	
	void closeBin() {
		if (binState == BinState.FILLED_SUPER)
			transformBinObject(BinState.CLOSED_SUPER);
		else
			transformBinObject(BinState.CLOSED);
		setClosingTime(System.currentTimeMillis());
		player.sendMessage("You close the bin, and wait for the weeds to compost.");
	}

	void takeCompost() {
		if (task != null && task.isRunning())
			return;
		Position position = player.getPosition().copy();
		task = new Task(3, player, true) {
			@Override
			protected void execute() {
				if (!player.getPosition().equals(position)) {
					stop();
					return;
				}
				setBinWeeds(getBinWeeds()-1);
				if (getBinWeeds() > 0) {
					if (player.getInventory().contains(FarmingConstants.BUCKET)) {
						player.getInventory().delete(FarmingConstants.BUCKET, 1);
						if (getBinState().equals(BinState.COMPOSTED_SUPER)) { 
							player.getInventory().add(new Item(FarmingConstants.SUPER_COMPOST));
							player.sendMessage("You fill a bucket with super compost.");
						} else {
							player.getInventory().add(new Item(FarmingConstants.COMPOST));
							player.sendMessage("You fill a bucket with compost.");
						}
						player.performAnimation(new Animation(FarmingConstants.GETTING_COMPOST));
						if (!player.getInventory().contains(FarmingConstants.BUCKET)) {
							stop();
						}
					} else {
						stop();
						player.sendMessage("You must have a bucket to collect the compost.");
					}
				} else {
					setBinWeeds(0);
					transformBinObject(BinState.EMPTY);
					stop();
				}

			}
		};
		TaskManager.submit(task);
	}


	private Item findFirstSuper() {
		for (Item item : player.getInventory().getItems()) {
			for (Integer herb : FarmingConstants.HERBS) {
				if (item.getId() == herb)
					return item;
			}
		}
		return null;
	}

	void addSuperToBin() {

		Item superProduct = findFirstSuper();

		if(superProduct == null)
			return;

		superProduct = new Item(superProduct.getId(), player.getInventory().getAmount(superProduct.getId()));
		if (!getBinState().equals(BinState.PARTIALLY_FILLED_SUPER) && !getBinState().equals(BinState.EMPTY)) {
			if (getBinState().equals(BinState.PARTIALLY_FILLED) || getBinState().equals(BinState.FILLED)) {
				player.sendMessage("The compost bin already has non-super compost.");
				return;
			}
			player.sendMessage("The compost bin can't hold anymore.");
			return;
		}
		if (superProduct.getAmount() > 15 - getBinWeeds())
			superProduct.setAmount(15 - getBinWeeds());
		if (superProduct.getAmount() <= 0)
			player.sendMessage("The compost bin is full, close the lid and wait for the weeds to compost.");
		else {
			player.getInventory().delete(superProduct);
			setBinWeeds(getBinWeeds() + superProduct.getAmount());
			player.sendMessage("You put "+superProduct.getAmount()+" weeds into the compost bin.");
			player.sendMessage("The compost bin now has "+getBinWeeds()+"/15.");
			if (getBinWeeds() == 15) {
				transformBinObject(BinState.FILLED_SUPER);
				player.sendMessage("The (super) compost bin is now full, close the lid and wait for the weeds to compost.");
			} else if (getBinState().equals(BinState.EMPTY))
				transformBinObject(BinState.PARTIALLY_FILLED_SUPER);
		}
	}

	void addToBin() {
		Item weeds = new Item(FarmingConstants.WEEDS, player.getInventory().getAmount(FarmingConstants.WEEDS));
		if (!getBinState().equals(BinState.PARTIALLY_FILLED) && !getBinState().equals(BinState.EMPTY)) {
			if (getBinState().name().endsWith("SUPER"))
				player.sendMessage("The compost bin already has super compost.");
			else
				player.sendMessage("The compost bin can't hold any weeds.");
			return;
		}
		if (weeds.getAmount() > 15 - getBinWeeds())
			weeds.setAmount(15-getBinWeeds());
		if (weeds.getAmount() <= 0)
			player.sendMessage("The compost bin is full, close the lid and wait for the weeds to compost.");
		else {
			player.getInventory().delete(weeds);
			setBinWeeds(getBinWeeds() + weeds.getAmount());
			player.sendMessage("You put "+weeds.getAmount()+" weeds into the compost bin.");
			player.sendMessage("The compost bin now has "+getBinWeeds()+"/15 weeds.");
			if (getBinWeeds() == 15) {
				transformBinObject(BinState.FILLED);
				player.sendMessage("The compost bin is now full, close the lid and wait for the weeds to compost.");
			} else if (getBinState().equals(BinState.EMPTY))
				transformBinObject(BinState.PARTIALLY_FILLED);
		}
	}

	public void transformBinObject(BinState binState) {
//		final DynamicGameObject newObject = DynamicGameObject.createLocal(binState.getObjectId(), binObject.getPosition(), binObject.getObjectType(), binObject.getFace());
//		newObject.addTo(player);

//		if(ClippedMapObjects.USE_NEW_OBJECT_UPDATES){
//			World.deSpawn(binObject);
//			World.addObject(newObject);
//		} else {
//			player.getPacketSender().sendObjectRemoval(binObject);
//			player.getPacketSender().sendObject(newObject);
//		}
		this.setBinState(binState);

		reloadConfig();
//		this.binObject = newObject;
	}

	public static final int VARBIT_ID = 4775;

	public void reloadConfig() {
		player.getPacketSender().sendVarbit(VARBIT_ID, binState.getChildIndex());
	}

//	public DynamicGameObject getBinObject() {
//		return binObject;
//	}

	public BinState getBinState() {
		return binState;
	}

	private void setBinState(BinState binState) {
		this.binState = binState;
	}

	public int getBinWeeds() {
		return binWeeds;
	}

	public void setBinWeeds(int binWeeds) {
		this.binWeeds = binWeeds;
	}

	public Long getClosingTime() {
		return closingTime;
	}

	public void setClosingTime(Long closingTime) {
		this.closingTime = closingTime;
	}

	public boolean isCanOpen() {
		return canOpen;
	}

	public void setCanOpen(boolean canOpen) {
		this.canOpen = canOpen;
	}

}