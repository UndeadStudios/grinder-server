package com.grinder.game.content.skill.skillable.impl.crafting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.player.Player;

public enum HideTanData {
	
	LEATHER(14793, "Soft leather", 1739, 2, 1741, 1, new int[0]),
	HARD_LEATHER(14794, "Hard leather", 1739, 5, 1743, 28, new int[0]),
	SNAKEHIDE(14795, "Snakeskin", 6287, 25, 6289, 45, new int[0]),
	SNAKEHIDE2(14796, "Snakeskin",7801, 45, 6289, 45, new int[0]),
	GREEN_LEATHER(14797, "Green d'hide", 1753, 25, 1745, 57, new int[] { 1135, 1065, 1099 }),
	BLUE_LEATHER(14798, "Blue d'hide", 1751, 45, 2505, 66, new int[] { 2499, 2487, 2493 }),
	RED_LEATHER(14799, "Red d'hide", 1749, 45, 2507, 73, new int[] { 2501, 2489, 2495 }),
	BLACK_LEATHER(14800, "Black d'hide", 1747, 45, 2509, 79, new int[] { 2503, 2491, 2497 });

	private int baseButtonId;
	private String text;
	private int itemId;
	private int coins;
	private int outcome;
	private int requiredLevel;
	private int[] craftableOutcomes;
	
	private static Map<Integer, HideTanData> hideData = new HashMap<>();

	private static final int INTERFACE_ID = 14670;

	static {
		Arrays.stream(values()).forEach(v -> {
			for (int i = 0; i < 4; i++) {
				hideData.put(v.getBaseButtonId() + i * 8, v);
			}
		});
	}

	public static HideTanData forId(int id) {
		return hideData.get(id);
	}

	HideTanData(int baseButtonId, String text, int itemId, int coins, int outcome, int requiredLevel, int[] craftableOutcomes) {
		this.baseButtonId = baseButtonId;
		this.text = text;
		this.itemId = ((short) itemId);
		this.coins = coins;
		this.outcome = ((short) outcome);
		this.requiredLevel = ((short) requiredLevel);
		this.craftableOutcomes = craftableOutcomes;
	}
	
	public int getBaseButtonId() {
		return baseButtonId;
	}
	
	public String getText() {
		return text;
	}

	public int getCoins() {
		return coins;
	}

	public int[] getCraftableOutcomes() {
		return craftableOutcomes;
	}

	public int getItemId() {
		return itemId;
	}

	public int getOutcome() {
		return outcome;
	}

	public int getRequiredLevel() {
		return requiredLevel;
	}
	
	public static final boolean clickButton(Player player, int id) {
		if (player.getInterfaceId() != INTERFACE_ID) {
			return false;
		}
		
		HideTanData hdt = forId(id);
		
		if (hdt == null) {
			return false;
		}
		
		int amount = 1;
		
		if (id >= 14809 && id <= 14816) {
			amount = 5;
		}
		
		if (id >= 14801 && id <= 14808) {
			//Tan X
		}
		
		if (id >= 14793 && id <= 14800) {
			amount = player.getInventory().getAmount(hdt.getItemId());
		}
		
		hdt.tan(player, amount);
		return false;
	}

	public static void sendTanningInterface(Player player) {
		for (int i = 0; i < values().length; i++) {
			player.getPacketSender().sendInterfaceModel(14769 + i, values()[i].getItemId(), 250);
			player.getPacketSender().sendString(14777 + i, values()[i].getText());
			player.getPacketSender().sendString(14785 + i, values()[i].getCoins() + " gp");
		}
		
		player.getPacketSender().sendInterface(INTERFACE_ID);
	}

	public void tan(Player player, int amount) {
		int price = getCoins();
		int coins = player.getInventory().getAmount(995);

		if (coins < price) {
			player.sendMessage("You do not have enough coins to purchase this.");
			return;
		}

		int invAm = player.getInventory().getAmount(getItemId());

		if (invAm == 0) {
			player.sendMessage("You do not have any of this hide.");
			return;
		}

		if (invAm < amount) {
			amount = invAm;
		}

		int total = amount * price;

		if (total > coins) {
			amount = coins / price;
			total = amount * price;
		}

		player.getInventory().delete(995, total, true);
		player.getInventory().delete(getItemId(), amount, false);
		player.getInventory().add(getOutcome(), amount);

		player.sendMessage("The tanner tans " + amount + " " + ItemDefinition.forId(getOutcome()).getName().toLowerCase() +"" + (amount > 1 ? "s" : "") +" for you.");
	}
	
}
