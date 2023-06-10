package com.grinder.game.content.dueling;

import com.grinder.Server;
import com.grinder.game.GameConstants;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.gambling.GambleTax;
import com.grinder.game.content.item.PlatinumToken;
import com.grinder.game.content.miscellaneous.npcs.AnnouncerNPC;
import com.grinder.game.content.trading.TradeConstants;
import com.grinder.game.definition.ItemValueType;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.model.ButtonActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainer;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.net.packet.PacketSender;
import com.grinder.util.Misc;
import com.grinder.util.time.SecondsTimer;
import com.grinder.util.tools.DupeDetector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static com.grinder.util.ItemID.COINS;
import static com.grinder.util.ItemID.PLATINUM_TOKEN;

/**
 * Represents a controller for the dueling system.
 *
 * @author Professor Oak
 * @author Stan van der Bend
 */
public final class DuelController {

	private final Player player;
	private final ItemContainer container;
	private final SecondsTimer request_delay = new SecondsTimer();
	private final boolean[] rules = new boolean[DuelRule.values().length];

	private DuelState state = DuelState.NONE;
	private Player interact;
	private int configValue;

	static {
		ButtonActions.INSTANCE.onClick(6674, 6520,
				(action) -> {
					final Player player = action.getPlayer();
					player.getDueling().acceptDuel();
				});

		for (DuelRule duelRule : DuelRule.values()){
			ButtonActions.INSTANCE.onClick(duelRule.getButtonId(),
					(action) -> {
						final Player player = action.getPlayer();
						player.getDueling().checkRule(duelRule);
					});
		}
	}

	/**
	 * Create a new {@link DuelController} instance.
	 *
	 * @param player the {@link Player} to control the dueling mechanics of.
	 */
	public DuelController(Player player) {
		this.player = player;
		this.container = new DuelStakeContainer(player);
	}

	/**
	 * Requests a duel with or accepts a request from {@link Player other}.
	 *
	 * @param other the {@link Player} requested to duel with.
	 */
	public void requestDuel(Player other) {

		if (DuelUtil.cannotRequestDuel(player, other))
			return;

		if (Server.isUpdating()) {
			player.sendMessage("You cannot duel with other players while the server is being updated.");
			return;
		}

		player.setPositionToFace(other.getPosition());

		if (state == DuelState.NONE || state == DuelState.REQUESTED_DUEL) {

			if (!request_delay.finished()) {
				final int seconds = request_delay.secondsRemaining();
				player.sendMessage("You must wait another " + (seconds == 1 ? "second" : "" + seconds + " seconds") + " before sending more duel challenges.");
				return;
			}

			setInteract(other);
			state = DuelState.REQUESTED_DUEL;

			if (DuelUtil.isBusy(other, player))
				return;

			final DuelController otherController = other.getDueling();
			final boolean acceptedRequest =
					otherController.getState() == DuelState.REQUESTED_DUEL
					&& otherController.getInteract() == player;

			if (acceptedRequest) {
				openDuelSettingsInterface();
				otherController.openDuelSettingsInterface();
				other.setPositionToFace(player.getPosition());
			} else {
				player.sendMessage("You've sent a duel challenge to " + other.getUsername() + "...");
				player.setPositionToFace(player.getMotion().getLastPositionToFace());
				other.sendMessage(player.getUsername() + ":duelreq:");
			}
			request_delay.start(2);
		} else
			player.sendMessage("You can't do that right now.");
	}

	public void openDuelSettingsInterface() {

		if (DuelUtil.notInDuelArena(player) || DuelUtil.notInDuelArena(interact))
			return;

		state = DuelState.SELECT_DUEL_SETTINGS;

		player.setStatus(PlayerStatus.DUELING);
		player.setPositionToFace(interact.getPosition());

		final PacketSender packetSender = player.getPacketSender();
		packetSender.sendInteractionOption("null", 2, true);
		packetSender.sendInteractionOption("null", 1, false);
		packetSender.sendConfig(DuelConstants.RULES_CONFIG_ID, 0);
		packetSender.sendString(DuelConstants.DUELING_WITH_FRAME, "@or1@Dueling with: @whi@" + interact.getUsername() + "@or1@          Combat level: @whi@" + interact.getSkillManager().calculateCombatLevel());
		packetSender.sendString(DuelConstants.STATUS_FRAME_1, "");
		packetSender.sendString(669, "Lock Weapon");
		packetSender.sendString(8278, "Neither player is allowed to change weapon.");

		final Item[] equippedItems = player.getEquipment().getItems();
		for (int i = 0; i < equippedItems.length; i++) {
			final Item item = equippedItems[i];
			packetSender.sendItemOnInterface(13824, item.getId(), i, item.getAmount());
		}

		container.resetItems();
		container.refreshItems();
	}

	public void onCloseDuelInterface() {

		if (state != DuelState.NONE) {

			final Player other = interact;

			for (Item item : container.getValidItems())
				ItemContainerUtil.switchItem(container, player.getInventory(), item.clone(), false, false);

			player.getInventory().refreshItems();

			resetAttributes();

			player.resetAttributes();
			player.sendMessage("Duel stake and options were declined.");
			player.getPacketSender().sendInterfaceRemoval();

			if (other != null) {
				if (other.getStatus() == PlayerStatus.DUELING)
					if (other.getDueling().getInteract() == player)
						other.getPacketSender().sendInterfaceRemoval();
			}
		}
	}

	public void resetAttributes() {

		player.resetAttributes();

		interact = null;
		state = DuelState.NONE;

		if (player.getStatus() == PlayerStatus.DUELING)
			player.setStatus(PlayerStatus.NONE);

		container.resetItems();

		Arrays.fill(rules, false);

		configValue = 0;

		final PacketSender packetSender = player.getPacketSender();
		packetSender.sendConfig(DuelConstants.RULES_CONFIG_ID, 0);
		packetSender.sendInteractionOption("Challenge", 1, false);
		packetSender.sendInteractionOption("null", 2, true);
		packetSender.sendEntityHintRemoval(true);
		packetSender.clearItemOnInterface(DuelConstants.MAIN_INTERFACE_CONTAINER);
		packetSender.clearItemOnInterface(DuelConstants.SECOND_INTERFACE_CONTAINER);
	}

	/**
	 * Handles depositing and withdrawal of items to or from the {@link #container}.
	 */
	public void switchItem(int id, int amount, int slot, ItemContainer from, ItemContainer to) {

		if (DuelUtil.cannotStakeItems(interact, player))
			return;

		final Item item = new Item(id, amount);

		if (player.getInterfaceId() == DuelConstants.INTERFACE_ID) {

			if (!DuelUtil.validate(player, interact, PlayerStatus.DUELING, DuelState.SELECT_DUEL_SETTINGS, DuelState.ACCEPTED_DUEL_SCREEN))
				return;

			if (DuelUtil.cannotStakeItem(item, player))
				return;

			if (state == DuelState.ACCEPTED_DUEL_SCREEN)
				state = DuelState.SELECT_DUEL_SETTINGS;

			if (interact.getDueling().getState() == DuelState.ACCEPTED_DUEL_SCREEN)
				interact.getDueling().setState(DuelState.SELECT_DUEL_SETTINGS);

			player.getPacketSender().sendString(DuelConstants.STATUS_FRAME_1, "@red@DUEL MODIFIED!");
			interact.getPacketSender().sendString(DuelConstants.STATUS_FRAME_1, "@red@DUEL MODIFIED!");

			if (state == DuelState.SELECT_DUEL_SETTINGS && interact.getDueling().getState() == DuelState.SELECT_DUEL_SETTINGS) {

				final Item itemAtSlot = from.atSlot(slot);

				if (itemAtSlot.getId() == id) {

					if (from instanceof Inventory)
						if (!itemAtSlot.getDefinition().isStackable())
							amount = Math.min(amount, container.countFreeSlots());

					if (amount <= 0)
						return;

					final boolean sort = from == container;

					if (item.getAmount() == 1)
						from.moveItemFromSlot(to, item, slot, sort, true);
					else
						ItemContainerUtil.switchItem(from, to, item, sort, true);
				}
			} else
				player.getPacketSender().sendInterfaceRemoval();
		}
	}

	public void acceptDuel() {

		if (!DuelUtil.validate(player, interact, PlayerStatus.DUELING, DuelState.SELECT_DUEL_SETTINGS, DuelState.ACCEPTED_DUEL_SCREEN, DuelState.CONFIRM_SCREEN, DuelState.ACCEPTED_CONFIRM_SCREEN)) {
			return;
		}

		if (Server.isUpdating()) { // Extra just in case
			player.sendMessage("You cannot duel with other players while the server is being updated.");
			return;
		}

		if (DuelUtil.cannotAcceptDuelSettings(player)) {
			return;
		}

		final Player other = interact;
		final DuelState otherState = other.getDueling().getState();

		if (state == DuelState.SELECT_DUEL_SETTINGS) {

			final int freeSlotsRequired = DuelUtil.getFreeSlotsRequired(container, interact, player);
			if (player.getInventory().countFreeSlots() < freeSlotsRequired) {
				player.sendMessage("You need at least " + freeSlotsRequired + " free inventory slots for this duel.", 2000);
				return;
			}

			if (selectedAllRules(DuelRule.NO_MELEE, DuelRule.NO_RANGED, DuelRule.NO_MAGIC)) {
				player.sendMessage("You must enable at least one of the three combat styles.", 1500);
				return;
			}

			state = DuelState.ACCEPTED_DUEL_SCREEN;

			player.getPacketSender().sendString(DuelConstants.STATUS_FRAME_1, "Waiting for other player..");
			other.getPacketSender().sendString(DuelConstants.STATUS_FRAME_1, player.getUsername() + " has accepted.");

			if (state == DuelState.ACCEPTED_DUEL_SCREEN && otherState == DuelState.ACCEPTED_DUEL_SCREEN) {
				player.getDueling().openDuelConfirmationInterface();
				other.getDueling().openDuelConfirmationInterface();
			}
		} else if (state == DuelState.CONFIRM_SCREEN) {

			if (!DuelUtil.validate(player, interact, PlayerStatus.DUELING, DuelState.CONFIRM_SCREEN, DuelState.ACCEPTED_CONFIRM_SCREEN)) {
				return;
			}

			state = DuelState.ACCEPTED_CONFIRM_SCREEN;

			player.getPacketSender().sendString(DuelConstants.STATUS_FRAME_2, "Waiting for " + other.getUsername() + "'s confirmation..");
			other.getPacketSender().sendString(DuelConstants.STATUS_FRAME_2, player.getUsername() + " has accepted.");

			if (state == DuelState.ACCEPTED_CONFIRM_SCREEN && otherState == DuelState.ACCEPTED_CONFIRM_SCREEN) {

				final boolean obstacle = selectedRule(DuelRule.OBSTACLES);
				final boolean movementDisabled = selectedRule(DuelRule.NO_MOVEMENT);
				final Position playerPosition = DuelUtil.getRandomSpawn(obstacle);
				final Position otherPosition = movementDisabled
						? playerPosition.clone().add(-1, 0)
						: DuelUtil.getRandomSpawn(obstacle);

				player.getDueling().startDuel(playerPosition);
				other.getDueling().startDuel(otherPosition);
			}
		}
	}

	private void openDuelConfirmationInterface() {

		state = DuelState.CONFIRM_SCREEN;

		final String playerItems = ItemContainerUtil.listItems(container);
		final String otherItems = ItemContainerUtil.listItems(interact.getDueling().getContainer());

		final PacketSender packetSender = player.getPacketSender();
		packetSender.sendString(DuelConstants.ITEM_LIST_1_FRAME, playerItems);
		packetSender.sendString(DuelConstants.ITEM_LIST_2_FRAME, otherItems);

		for (int i = 8238; i <= 8253; i++)
			packetSender.sendString(i, "");

		packetSender.sendString(8250, "Hitpoints will be restored.");
		packetSender.sendString(8238, "Boosted stats will be restored.");

		if (selectedRule(DuelRule.OBSTACLES))
			packetSender.sendString(8239, "@red@There will be obstacles in the arena.");

		packetSender.sendString(8240, "");
		packetSender.sendString(8241, "");

		for(DuelRule duelRule : DuelRule.values()) {
			if (duelRule != DuelRule.OBSTACLES && selectedRule(duelRule))
				packetSender.sendString(DuelConstants.RULES_FRAME_START + duelRule.ordinal(), "" + duelRule.toString());
		}

		packetSender.sendString(DuelConstants.STATUS_FRAME_2, "");
		packetSender.sendInterfaceSet(DuelConstants.CONFIRM_INTERFACE_ID, Inventory.INTERFACE_ID);
		packetSender.sendItemContainer(player.getInventory(), TradeConstants.INVENTORY_CONTAINER_INTERFACE);
	}

	public boolean handleButton(int button) {
		final DuelRule rule = DuelRule.forButtonId(button);
		if (rule != null) {
			checkRule(rule);
			return true;
		}
		return false;
	}

	private void checkRule(DuelRule rule) {

		if (player.getStatus() != PlayerStatus.DUELING)
			return;

		if (!DuelUtil.validate(player, interact, PlayerStatus.DUELING, DuelState.SELECT_DUEL_SETTINGS, DuelState.ACCEPTED_DUEL_SCREEN))
			return;

		if (state == DuelState.SELECT_DUEL_SETTINGS || state == DuelState.ACCEPTED_DUEL_SCREEN) {

			if (!selectedRule(rule)) {
				rules[rule.ordinal()] = true;
				configValue += rule.getConfigId();
			} else {
				rules[rule.ordinal()] = false;
				configValue -= rule.getConfigId();
			}

			final DuelController otherController = interact.getDueling();
			otherController.setConfigValue(configValue);
			otherController.getRules()[rule.ordinal()] = rules[rule.ordinal()];

			player.getPacketSender().sendToggle(DuelConstants.RULES_CONFIG_ID, configValue);
			interact.getPacketSender().sendToggle(DuelConstants.RULES_CONFIG_ID, configValue);

			if (state == DuelState.ACCEPTED_DUEL_SCREEN)
				state = DuelState.SELECT_DUEL_SETTINGS;

			if (otherController.getState() == DuelState.ACCEPTED_DUEL_SCREEN)
				otherController.setState(DuelState.SELECT_DUEL_SETTINGS);

			player.getPacketSender().sendString(DuelConstants.STATUS_FRAME_1, "@red@DUEL MODIFIED!");
			interact.getPacketSender().sendString(DuelConstants.STATUS_FRAME_1, "@red@DUEL MODIFIED!");

			if (selectedRule(rule))
				DuelUtil.notifyRuleChange(rule, player, interact);
		}
	}

	private void startDuel(Position telePos) {

		state = DuelState.STARTING_DUEL;

		player.resetAttributes();

		for (DuelRule rule: DuelRule.values()){
			if(selectedRule(rule)){
				final int targetedSlot = rule.getEquipmentSlot();
				if (targetedSlot >= 0) {
					final Item itemAtSlot = player.getEquipment().atSlot(targetedSlot).clone();
					if (itemAtSlot.getId() > 0) {
						player.getEquipment().delete(itemAtSlot);
						player.getInventory().add(itemAtSlot);
					}
				}
			}
		}

		if(selectedAnyRule(DuelRule.NO_WEAPON, DuelRule.NO_SHIELD)){
			final Item weapon = player.getEquipment().atSlot(EquipmentConstants.WEAPON_SLOT);
			if (weapon.getId() > 0 && weapon.getDefinition().isDoubleHanded()) {
				player.getEquipment().delete(weapon);
				player.getInventory().add(weapon);
			}
		}

		final PacketSender packetSender = player.getPacketSender();
		packetSender.sendInterfaceRemoval();
		packetSender.clearItemOnInterface(DuelConstants.MAIN_INTERFACE_CONTAINER);
		packetSender.clearItemOnInterface(DuelConstants.SECOND_INTERFACE_CONTAINER);

		packetSender.sendInteractionOption("Attack", 2, true);
		packetSender.sendInteractionOption("null", 1, false);

		player.resetAttributes();

		if (selectedRule(DuelRule.NO_MOVEMENT)) {
			player.getMotion().update(MovementStatus.DISABLED);
			PlayerExtKt.resetInteractions(player, true, false);
		}

		packetSender.sendPositionalHint(interact.getPosition().clone(), 10);
		packetSender.sendEntityHint(interact);

		player.moveTo(telePos);
		player.getPacketSender().sendJinglebitMusic(97, 25);
		player.setEntityInteraction(interact);

		TaskManager.submit(new Task(2, player, false) {
			int timer = 3;
			@Override
			public void execute() {
				if (state != DuelState.STARTING_DUEL) {
					stop();
					return;
				}
				if (timer == 3 || timer == 2 || timer == 1) {
					player.say("" + timer + "..");
				} else {
					state = DuelState.IN_DUEL;
					player.say("FIGHT!!");
					stop();
				}
				timer--;
			}
		});
	}

	public void forfeit() {
		if (!player.getArea().equals(AreaManager.DUEL_ARENA)) {
			return;
		}
		if (player.getDueling().getRules()[DuelRule.NO_FORFEIT.ordinal()]) {
			player.getPacketSender().sendMessage("You can't forfeit from this duel!", 1000);
			return;
		}
		if (player.busy()) {
			player.sendMessage("You can't forfeit at this moment!");
			return;
		}
		if (player.getCombat().getOpponent() != null || player.getDueling().getInteract() != null) {

			if (player.getDueling().getInteract().getHitpoints() <= 0 || player.getDueling().getInteract().isDying()
					|| player.getHitpoints() <= 0 || player.isDying()) {
				player.getPacketSender().sendMessage("You can't forfeit at this moment!", 1000);
				return;
			}
		}
		if (player.getStatus() == PlayerStatus.DUELING) {
			player.setStatus(PlayerStatus.NONE);
		}
		if (player.getDueling().inDuel()) {
			player.getDueling().loseDuel();
		}
		player.moveTo(new Position(3361 + Misc.getRandomInclusive(10), 3275, 0));
		player.getPacketSender().sendMessage("You have forfeit from the duel!");
	}

	public void loseDuel() {

		if (DuelUtil.validate(player, interact, null, DuelState.STARTING_DUEL, DuelState.IN_DUEL)) {

			long totalValue = 0;
			final List<Item> winnings = new ArrayList<>();

			for (Item item : interact.getDueling().getContainer().getValidItems()) {
				ItemContainerUtil.addOrDrop(interact.getInventory(), interact, item);
				winnings.add(item);
				totalValue += item.getValue(ItemValueType.ITEMS_VALUE);
			}

			if (!GameConstants.BONUS_DOUBLE_EXP_WEEKEND) {
				player.sendMessage("@red@Enjoy a full tax rate reduction every weekend while gambling and staking!");
				GambleTax.applyTaxIfApplicable(player, interact, Misc.concat(winnings, container.getValidItems()));
			}

			for (Item item : container.getValidItems()) {

				if(item.getId() == COINS) {
					final int amountOfItem = interact.getInventory().getAmount(item.getId());
					final long newAmount = (long) amountOfItem + item.getAmount();
					if (newAmount > Integer.MAX_VALUE) {
						final PlatinumToken.ConversionResults conversionResults = PlatinumToken.convertCoins(item.getAmount());
						final int coinsToAdd = conversionResults.getLeftOverCoins();
						final int tokensToAdd = conversionResults.getTokensFromCoins();
						if(coinsToAdd > 0)
							ItemContainerUtil.addOrDrop(interact.getInventory(), interact, new Item(COINS, coinsToAdd));
						if(tokensToAdd > 0)
							ItemContainerUtil.addOrDrop(interact.getInventory(), interact, new Item(PLATINUM_TOKEN, tokensToAdd));
					} else
						ItemContainerUtil.addOrDrop(interact.getInventory(), interact, item);
				} else
					ItemContainerUtil.addOrDrop(interact.getInventory(), interact, item);

				winnings.add(item);
				totalValue += item.getValue(ItemValueType.ITEMS_VALUE);
			}


			DuelUtil.logStakeWinnings(interact, player);

			final PacketSender otherPacketSender = interact.getPacketSender();

			otherPacketSender.sendString(DuelConstants.SCOREBOARD_USERNAME_FRAME, player.getUsername());
			otherPacketSender.sendString(DuelConstants.SCOREBOARD_COMBAT_LEVEL_FRAME, "" + player.getSkillManager().calculateCombatLevel());
			otherPacketSender.sendString(DuelConstants.TOTAL_WORTH_FRAME, "@yel@Total: @or1@" + Misc.insertCommasToNumber("" + totalValue + "") + " value!");

			otherPacketSender.sendInterfaceItems(DuelConstants.SCOREBOARD_CONTAINER, winnings);
			otherPacketSender.sendInterface(DuelConstants.SCOREBOARD_INTERFACE_ID);

			interact.resetAttributes();
			interact.setEntityInteraction(null);
			interact.getCombat().reset(false);
			interact.getMotion().reset();

			interact.moveTo(DuelConstants.BASE_END_DUEL_TELEPORT_LOCATION.clone().add(Misc.getRandomInclusive(4), Misc.getRandomInclusive(2)));
			player.moveTo(DuelConstants.BASE_END_DUEL_TELEPORT_LOCATION.clone().add(Misc.getRandomInclusive(4), Misc.getRandomInclusive(2)));

			AnnouncerNPC.announceWinner(interact, player, "duel_arena");

			interact.resetAttributes();

			AchievementManager.processFor(AchievementType.DUEL_ICIOUS, interact);
			interact.sendMessage("You have won the duel! You have won " + interact.getPoints().get(AttributeManager.Points.DUEL_WINS) + " duels.");
			interact.getPacketSender().sendJinglebitMusic(98, 25);
			interact.getPoints().increase(AttributeManager.Points.DUEL_WINS); // Increase points


			interact.getPoints().increase(AttributeManager.Points.DUEL_WIN_STREAK, 1); // Increase points
			if (interact.getPoints().get(AttributeManager.Points.DUEL_WIN_STREAK) > interact.getPoints().get(AttributeManager.Points.DUEL_HIGHEST_WIN_STREAK)) {
				interact.getPoints().set(AttributeManager.Points.DUEL_HIGHEST_WIN_STREAK, interact.getPoints().get(AttributeManager.Points.DUEL_WIN_STREAK));
			}

			//if ((interact.getPoints().get(AttributeManager.Points.DUEL_WINS) % 5 == 0)) {
			//	interact.sendMessage("You have won " + interact.getPoints().get(AttributeManager.Points.DUEL_WINS) + " duels.");
			//}


			player.resetAttributes();
			player.setEntityInteraction(null);
			player.getMotion().reset();
			player.sendMessage("You were defeated! You have won " + player.getPoints().get(AttributeManager.Points.DUEL_WINS) + " duels.");
			player.sendMessage("You have now lost " + player.getPoints().get(AttributeManager.Points.DUEL_LOSES) + " duels.");
			player.getPoints().increase(AttributeManager.Points.DUEL_LOSES); // Increase points
			player.getPoints().set(AttributeManager.Points.DUEL_WIN_STREAK, 0);

			Scoreboard.INSTANCE.addEntry(interact, player, false, "duel_arena", "Duel Arena");

			interact.getDueling().resetAttributes();
			resetAttributes();

			DupeDetector.INSTANCE.check(player);
			DupeDetector.INSTANCE.check(interact);
		} else {

			resetAttributes();
			player.getPacketSender().sendInterfaceRemoval();

			if (interact != null) {
				interact.getDueling().resetAttributes();
				interact.getPacketSender().sendInterfaceRemoval();
			}
		}
	}

	public boolean inDuel() {
		return state == DuelState.STARTING_DUEL || state == DuelState.IN_DUEL;
	}

	public boolean selectedAllRules(DuelRule... rules){
		return Stream.of(rules).allMatch(this::selectedRule);
	}

	public boolean selectedAnyRule(DuelRule... rules) {
		return Stream.of(rules).anyMatch(this::selectedRule);
	}

	public boolean selectedRule(DuelRule rule){
		return this.rules[rule.ordinal()];
	}

	public DuelState getState() {
		return state;
	}

	public void setState(DuelState state) {
		this.state = state;
	}

	public ItemContainer getContainer() {
		return container;
	}

	public Player getInteract() {
		return interact;
	}

	public void setInteract(Player interact) {
		this.interact = interact;
	}

	public boolean[] getRules() {
		return rules;
	}

	public void setConfigValue(int configValue) {
		this.configValue = configValue;
	}
}
