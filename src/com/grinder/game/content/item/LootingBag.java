package com.grinder.game.content.item;

import java.text.NumberFormat;

import com.grinder.game.definition.ItemValueType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.item.container.ItemContainer;
import com.grinder.game.model.item.container.StackType;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.game.model.item.container.bank.Banking;

/**
 * Handles the looting bag
 * 
 * @author 2012
 *
 */
public class LootingBag {

	/**
	 * The looting bag
	 */
	public static final int LOOTING_BAG = 11941;

	/**
	 * The looting bag interface id
	 */
	private static final int LOOTING_BAG_INTERFACE = 26700;

	/**
	 * The container interface id
	 */
	private static final int CONTAINER_INTERFACE = 26706;

	/**
	 * The container
	 */
	private LootingBagContainer container;

	/**
	 * Represents the looting bag
	 * 
	 * @param player
	 *            the player
	 */
	public LootingBag(Player player) {
		container = new LootingBagContainer(player);
	}

	/**
	 * Adding an item to the looting bag
	 * 
	 * @param player
	 *            the player
	 * @param item
	 *            the item
	 */
	public static void add(Player player, Item item) {
		/*
		 * Not in wild
		 */
		if (!AreaManager.inWilderness(player)) {
			player.getPacketSender().sendMessage("You can only use the looting bag inside the Wilderness.", 1000);
			return;
		}
		/*
		 * Doesnt own 
		 */
		if(!player.getInventory().contains(LOOTING_BAG)) {
			return;
		}
		/*
		 * Checks size
		 */
		if (!player.getLootingBag().getContainer().canHold(item)) {
			player.getPacketSender().sendMessage("There is no more space in the looting bag.", 1000);
			return;
		}
		/*
		 * Invalid
		 */
		if (item.getId() == LootingBag.LOOTING_BAG) {
			player.getPacketSender().sendMessage("You can't add a looting bag to a looting bag.", 1000);
			return;
		}
		/*
		 * Cant add
		 */
		if (!item.getDefinition().isTradeable()) {
			player.getPacketSender().sendMessage("This type of item can't be added to the looting bag.", 1000);
			return;
		}
		/*
		 * Adds item
		 */
		if (player.getInventory().contains(item)) {
			/*
			 * The existing amount
			 */
			int existing = player.getInventory().getAmount(item);
			/*
			 * 1 - 5
			 */
			if (existing > 1 && existing <= 5) {
				DialogueManager.start(player, 2512);

				player.setDialogueOptions(new DialogueOptions() {
					@Override
					public void handleOption(Player player, int option) {
						switch (option) {
						case 1:
							addToBag(player, item.clone().setAmount(1));
							break;
						case 2:
							addToBag(player, item.clone().setAmount(5));
							break;
						case 3:
							addToBag(player, item.clone().setAmount(item.getAmount()));
							break;
						}
						player.getPacketSender().sendInterfaceRemoval();
					}
				});
			} else if (existing >= 5) {
				DialogueManager.start(player, 2513);
				player.setDialogueOptions(new DialogueOptions() {
					@Override
					public void handleOption(Player player, int option) {
						switch (option) {
						case 1:
							addToBag(player, item.clone().setAmount(1));
							break;
						case 2:
							addToBag(player, item.clone().setAmount(5));
							break;
						case 3:
							addToBag(player, item.clone().setAmount(10));
							break;
						case 4:
							addToBag(player, item.clone().setAmount(player.getInventory().getAmount(item)));
							break;
						}
						player.getPacketSender().sendInterfaceRemoval();
					}
				});
			} else {
				addToBag(player, item.clone());
			}
		}
	}

	/**
	 * Adds to bag
	 * 
	 * @param player
	 *            the player
	 * @param item
	 *            the item
	 */
	private static void addToBag(Player player, Item item) {
		/*
		 * Doesnt own 
		 */
		if(!player.getInventory().contains(LOOTING_BAG)) {
			return;
		}
		/*
		 * The existing amount
		 */
		int existing = player.getInventory().getAmount(item);
		/*
		 * Fixes amount free slots
		 */
		if (item.getAmount() > player.getLootingBag().getContainer().countFreeSlots()) {
			if(item.getDefinition().isStackable()) {
				if(!player.getLootingBag().getContainer().canHold(item)) {
					return;
				}
			} else {
				item.setAmount(player.getLootingBag().getContainer().countFreeSlots());
			}
		}
		/*
		 * Fixes amount more than
		 */
		if (item.getAmount() > existing) {
			item.setAmount(existing);
		}
		player.getInventory().delete(item);
		player.getLootingBag().getContainer().add(item);
	}

	/**
	 * Checking the container looting bag
	 * 
	 * @param player
	 *            the player
	 */
	public static void check(Player player) {
		player.getLootingBag().getContainer().refreshItems();
		player.getPacketSender().sendTabInterface(3, LOOTING_BAG_INTERFACE);
	}

	/**
	 * Deposits container
	 * 
	 * @param player
	 *            the player
	 */
	public static void deposit(Player player) {
		/*
		 * Doesnt own 
		 */
		if(!player.getInventory().contains(LOOTING_BAG)) {
			return;
		}
		/*
		 * Not in wild
		 */
		if (AreaManager.inWilderness(player)) {
			player.getPacketSender().sendMessage("You can't deposit your looting bag items in the Wilderness.", 1000);
			return;
		}
		/*
		 * Empty
		 */
		if (player.getLootingBag().getContainer().getValidItems().size() == 0) {
			player.getPacketSender().sendMessage("Your looting bag is empty.", 1000);
			return;
		}
		/*
		 * Deposit
		 */
		final LootingBagContainer container = player.getLootingBag().getContainer();

		if(player.getGameMode().isUltimate()){
			for (Item item : container.getValidItems()) {
				ItemOnGroundManager.register(player, item, player.getPosition().copy());
			}
		} else {
			Banking.depositItems(player, container, true);
		}
		container.resetItems();

		player.getPacketSender().sendMessage("Your looting bag content has been " + (player.getGameMode().isUltimate() ? "dropped under you" : "sent to your bank") +".", 1000);
	}

	/**
	 * Represents the looting bag container
	 */
	public static class LootingBagContainer extends ItemContainer {

		/**
		 * Sets the player
		 * 
		 * @param player
		 *            the player
		 */
		public LootingBagContainer(Player player) {
			setPlayer(player);
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
			/*
			 * Clear
			 */
			for (int i = 0; i < capacity(); i++) {
				getPlayer().getPacketSender().sendItemOnInterface(CONTAINER_INTERFACE, -1, i, 0);
			}
			/*
			 * Total value
			 */
			long totalValue = 0;
			/*
			 * Send
			 */
			for (int i = 0; i < getValidItems().size(); i++) {
				Item item = getValidItems().get(i);
				if (item == null) {
					continue;
				}
				getPlayer().getPacketSender().sendItemOnInterface(CONTAINER_INTERFACE, item.getId(), i,
						item.getAmount());
				totalValue += item.getValue(ItemValueType.PRICE_CHECKER);
			}
			/*
			 * Display
			 */
			getPlayer().getPacketSender().sendString(26707,
					"Value: " + NumberFormat.getInstance().format(totalValue) + " GP");
			return this;
		}

		@Override
		public ItemContainer full() {
			return this;
		}
	}

	/**
	 * Sets the container
	 *
	 * @return the container
	 */
	public LootingBagContainer getContainer() {
		return container;
	}

	/**
	 * Sets the container
	 * 
	 * @param container
	 *            the container
	 */
	public void setContainer(LootingBagContainer container) {
		this.container = container;
	}
}
