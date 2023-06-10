package com.grinder.game.model.item.container.player;

import java.text.NumberFormat;
import java.util.List;

import com.grinder.game.definition.ItemValueType;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.item.Item;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.item.ItemUtil;
import com.grinder.game.model.item.container.ItemContainer;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.model.item.container.StackType;
import com.grinder.util.Misc;
import com.grinder.util.timing.TimerKey;

import static com.grinder.util.ItemID.BLOOD_MONEY;
import static com.grinder.util.ItemID.COINS;

public class PriceChecker extends ItemContainer {

	public static final int INTERFACE_ID = 42000, CONTAINER_ID = 18500, SCROLL_ID = 18499;
	private static final int TEXT_START_ID = 18300;
	public static final int PRICE_MULTIPLIER_THRESHOLD = 50_000;

	public PriceChecker(Player player) {
		super(player);
	}

	public ItemContainer open() {
		getPlayer().setStatus(PlayerStatus.PRICE_CHECKING);
		getPlayer().getMotion().clearSteps();
		refreshItems();
		return this;
	}

	@Override
	public int capacity() {
		return 28;
	}

	@Override
	public StackType stackType() {
		return StackType.DEFAULT;
	}

	@Override
	public ItemContainer refreshItems() {
		List<Item> items_ = getValidItems();

		getPlayer().getPacketSender().sendInterfaceSet(INTERFACE_ID, 3321);

		getPlayer().getPacketSender().sendScrollbarHeight(SCROLL_ID, Misc.getContainerScrollMax(items_.size(), 5, 35, 0, 222, true));

		getPlayer().getPacketSender().sendItemContainer(this, CONTAINER_ID);
		getPlayer().getPacketSender().sendItemContainer(getPlayer().getInventory(), 3322);

		// Item values
		if (items_.size() > 0) {

			String valueString = ItemContainerUtil.readValueOfContents(this);

			if (!valueString.equals("Too high!")) {
				try {
					final long total = Long.parseLong(valueString);
					valueString = NumberFormat.getIntegerInstance().format(total); // TOTAL
					//System.out.println(total);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
			getPlayer().getPacketSender().sendString(18351, valueString); // TOTAL

			// Send item prices
			for (int i = 0; i < capacity(); i++) {
				String itemPrice = "";

				if (getItems()[i].getId() > 0 && getItems()[i].getAmount() > 0) {
					int itemAmt = getItems()[i].getAmount();
					long price = getItems()[i].getValue(ItemValueType.PRICE_CHECKER);
					if(!ItemUtil.bypassPriceMultiplier(getItems()[i].getId())) {
						if (price > 200_000 && price != 1) {
								price *= 0.935;
						}
					}
					long total_price = (long) itemAmt * price;

					if (total_price > Long.MAX_VALUE) {
						itemPrice = "Too high!";
					} else if (itemAmt > 1) {
						itemPrice = NumberFormat.getIntegerInstance().format(itemAmt)
								+ " x "
								+ NumberFormat.getIntegerInstance().format(price)
								+ "\\n= "
								+ NumberFormat.getIntegerInstance().format(total_price);
					} else {
						itemPrice = NumberFormat.getIntegerInstance().format(price);
					}
				}

				getPlayer().getPacketSender().sendString(TEXT_START_ID + i, itemPrice);
			}

		} else {
			getPlayer().getPacketSender().sendString(18351, "0"); // TOTAL VALUE

			// Reset item prices
			for (int i = 0; i < capacity(); i++) {
				getPlayer().getPacketSender().sendString(TEXT_START_ID + i, "");
			}
		}

		return this;
	}

	@Override
	public ItemContainer full() {
		getPlayer().getPacketSender().sendMessage("The price checker can't hold any more items.", 1000);
		return this;
	}

	public void withdrawAll(boolean message) {
		if (getValidItems() == null || getValidItems().size() == 0) {
			if (message) {
				getPlayer().getPacketSender().sendMessage("You have no items in the price checker to remove.", 1000);
			}
			return;
		}
		if (getPlayer().getStatus() == PlayerStatus.PRICE_CHECKING && getPlayer().getInterfaceId() == INTERFACE_ID) {
			for (Item item : getValidItems()) {
				//System.out.println(item);
				ItemContainerUtil.switchItem(this, getPlayer().getInventory(), item.clone(), false, false);
			}
			refreshItems();
			getPlayer().getInventory().refreshItems();
		}
	}

	public void depositAll() {
		if (getPlayer().getInventory().getValidItems() == null || getPlayer().getInventory().getValidItems().size() == 0) {
			getPlayer().getPacketSender().sendMessage("You have no items that can be checked.", 2500);
			return;
		}
		if (player.getCombat().isInCombat() || player.getCombat().isUnderAttack()) {
			player.getPacketSender().sendMessage("You must wait 10 seconds after being out of combat to use the price checker.", 1000);
			return;
		}
		if (player.getWildernessLevel() > 0) {
			player.getPacketSender().sendMessage("You cannot price check items inside in the Wilderness!");
			return;
		}
		if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
			return;
		}
		if (getPlayer().getMinigame() != null) {
			getPlayer().sendMessage("You cannot price check items inside a minigame.");
			return;
		}
		if (player.getStatus() == PlayerStatus.DUELING) {
			player.getPacketSender().sendMessage("You cannot price check while in a duel!", 1000);
			return;
		}
		if (player.getStatus() == PlayerStatus.TRADING) {
			player.getPacketSender().sendMessage("You cannot price check while in a trade!", 1000);
			return;
		}
		if (player.getStatus() == PlayerStatus.SHOPPING) {
			player.getPacketSender().sendMessage("You cannot price check while shopping!", 1000);
			return;
		}
		if (getPlayer().getStatus() == PlayerStatus.PRICE_CHECKING && getPlayer().getInterfaceId() == INTERFACE_ID) {
			for (Item item : getPlayer().getInventory().getValidItems()) {
				if (item.getValue(ItemValueType.PRICE_CHECKER) > 0) {
					ItemContainerUtil.switchItem(getPlayer().getInventory(), this, item.clone(), false, false);
				}
			}
			refreshItems();
			getPlayer().getInventory().refreshItems();
		}
	}

	public boolean deposit(int id, int amount, int slot) {
		if (getPlayer().getTimerRepository().has(TimerKey.BUTTON_DELAY)) {
			return false;
		}
		if (getPlayer().BLOCK_ALL_BUT_TALKING) {
			return false;
		}
		if (getPlayer().getMinigame() != null) {
			getPlayer().sendMessage("You cannot price check items inside a minigame.");
			return false;
		}
		getPlayer().getTimerRepository().replaceIfLongerOrRegister(TimerKey.BUTTON_DELAY, 1);
		if (getPlayer().getStatus() == PlayerStatus.PRICE_CHECKING && getPlayer().getInterfaceId() == INTERFACE_ID) {

			// Verify item
			if (getPlayer().getInventory().getItems()[slot].getId() == id) {
				
				
				// Perform switch
				final Item item = new Item(id, amount);
				if (!item.getDefinition().isTradeable() && id != BLOOD_MONEY && id != COINS) {
					getPlayer().getPacketSender()
							.sendMessage("That item can't be price checked because it isn't tradeable.", 1000);
					return true;
				}
				if (item.getValue(ItemValueType.PRICE_CHECKER) == 0) {
					getPlayer().getPacketSender()
							.sendMessage("There's no point price checking that item. It has no value.", 1000);
					return true;
				}

				if (item.getAmount() == 1) {
					getPlayer().getInventory().moveItemFromSlot(this, item, slot, false, true);
				} else {
					ItemContainerUtil.switchItem(getPlayer().getInventory(), this, item, false, true);
				}
			}
			return true;
		}
		return false;
	}

	public boolean withdraw(int id, int amount, int slot) {
		if (getPlayer().getStatus() == PlayerStatus.PRICE_CHECKING && getPlayer().getInterfaceId() == INTERFACE_ID) {

			// Verify item
			if (getItems()[slot].getId() == id) {
				// Perform switch
				final Item item = new Item(id, amount);
				if (item.getAmount() == 1) {
					moveItemFromSlot(getPlayer().getInventory(), item, slot, true, true);
				} else {
					ItemContainerUtil.switchItem(this, getPlayer().getInventory(), item, true, true);
				}
			}
			return true;
		}
		return false;
	}
}
