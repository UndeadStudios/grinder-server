package com.grinder.game.model.item.container.player;

import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.game.model.Animation;
import com.grinder.game.model.ButtonActions;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainer;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.model.item.container.StackType;
import com.grinder.game.model.item.container.bank.BankConstants;
import com.grinder.game.model.sound.Sounds;
import com.grinder.util.Logging;

/**
 * Represents a player's Safe Deposit Box.
 *
 * @author Blake
 */
public class SafeDeposit extends ItemContainer {

	public static final int INTERFACE_ID = 38_000;
	public static final int SLOTS_STRING_ID = INTERFACE_ID + 14;
	public static final int ITEM_CONTAINER_ID = INTERFACE_ID + 15;

	static {
		ButtonActions.INSTANCE.onClick(INTERFACE_ID + 9, clickAction ->
				clickAction.getPlayer().getSafeDeposit().depositInventory());
		ButtonActions.INSTANCE.onClick(INTERFACE_ID + 12, clickAction ->
				clickAction.getPlayer().getSafeDeposit().depositEquipment());
	}

	/**
	 * Constructs a new {@link SafeDeposit}.
	 *
	 * @param player The player.
	 */
	public SafeDeposit(Player player) {
		super(player);
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
	public SafeDeposit refreshItems() {
		player.getPacketSender().sendItemContainer(this, ITEM_CONTAINER_ID);
		player.getPacketSender().sendItemContainer(player.getInventory(), BankConstants.INVENTORY_INTERFACE_ID);
		player.getPacketSender().sendString(SLOTS_STRING_ID, getValidItems().size() + " / " + capacity());
		return this;
	}

	@Override
	public SafeDeposit full() {
		player.getPacketSender().sendMessage("Not enough space in the deposit box.", 1000);
		player.getPacketSender().sendSound(Sounds.INVENTORY_FULL_SOUND);
		return this;
	}

	@Override
	public boolean validateSpace(ItemContainer target) {
		return target instanceof Inventory;
	}

	/**
	 * Opens the {@link SafeDeposit} interface.
	 */
	public void open() {
		player.getPacketSender().sendSound(Sounds.OPEN_BANK_BOOTH_2);
		refreshItems();
		player.getPacketSender().sendInterfaceSet(INTERFACE_ID, 5063);
		player.getInventory().refreshItems();
	}

	/**
	 * Deposits the entire inventory to the deposit box.
	 */
	public void depositInventory() {
		if (player.getInterfaceId() != INTERFACE_ID)
			return;

		if (dupeDetected())
			return;

		final Inventory inventory = player.getInventory();

		depositContainer(inventory);
	}

	/**
	 * Deposits all of the equipment to the deposit box.
	 */
	public void depositEquipment() {

		if (player.getInterfaceId() != INTERFACE_ID)
			return;

		if (dupeDetected())
			return;

		final Equipment equipment = player.getEquipment();

		if (!depositContainer(equipment))
			return;

		EquipmentBonuses.update(player);
		WeaponInterfaces.INSTANCE.assign(player);
		player.updateAppearance();
	}

	private boolean depositContainer(ItemContainer container) {

		if (container.getValidItems() == null || container.getValidItems().size() == 0) {
			player.sendMessage("You have no items to deposit.", 2500);
			return false;
		}

		for (Item item : container.getValidItems())
			ItemContainerUtil.switchItem(container, this, item.clone(), false, false);

		player.performAnimation(new Animation(834)); // 2697
		refreshItems();
		container.refreshItems();
		return true;
	}

	/**
	 * Deposits an item to the deposit box.
	 *
	 * @param id     The id.
	 * @param slot   The slot.
	 * @param amount The amount.
	 */
	public void depositFromInventory(int id, int slot, final int amount) {

		final Inventory inventory = player.getInventory();

		inventory.findAtSlot(slot)
				.filter(item -> item.getId() == id)
				.map(Item::clone).map(item -> item.setAmount(amount)).ifPresent(item ->
				{
					final int amountInInventory = inventory.getAmount(item);

					if (amount == -1 || amount > amountInInventory)
						item.setAmount(amountInInventory);

					if (item.getAmount() > 0) {

						if (dupeDetected())
							return;

						player.performAnimation(new Animation(2697));

						inventory.moveItemFromSlot(this, item, slot, false, false);
						refreshItems();
						inventory.refreshItems();
					}
				});
	}

	public void withdrawToInventory(int id, int slot, int amount) {

		findAtSlot(slot)
				.filter(item -> item.getId() == id)
				.map(Item::clone).map(item -> item.setAmount(amount)).ifPresent(item ->
				{
					final int amountInDeposit = getAmount(item);

					if (amount == -1 || amount > amountInDeposit)
						item.setAmount(amountInDeposit);

					if (item.getAmount() > 0) {

						if (dupeDetected())
							return;

						player.performAnimation(new Animation(2697));

						final Inventory inventory = player.getInventory();
						moveItemFromSlot(inventory, item, slot, false, false);
						inventory.refreshItems();
						refreshItems();
					}
				});
	}

	private boolean dupeDetected() {
		if (player.getMinigame() != null) {
			Logging.log("depositboxdupe", "" + player.getUsername() + " tried to deposit item while inside a Minigame " + player.getMinigame() + " " + player.getPosition() + "");
			PlayerUtil.broadcastPlayerDeveloperMessage("" + player.getUsername() + " tried to deposit item while inside a Minigame " + player.getMinigame() + " " + player.getPosition() + "");
			return true;
		}

		return false;
	}
}

