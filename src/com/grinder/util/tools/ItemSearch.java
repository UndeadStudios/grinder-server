package com.grinder.util.tools;

import java.io.File;
import java.io.FileReader;
import java.text.NumberFormat;
import java.util.Scanner;

import com.google.common.util.concurrent.AtomicLongMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grinder.game.GameConstants;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.bank.BankConstants;

/**
 * An utility used for checking the items in the economy.
 * 
 * @author Blake
 *
 */
public class ItemSearch {

	/**
	 * A string that represents the path of the character's files.
	 */
	private static String PATH = System.getProperty("user.home") + File.separator + "GrinderScapeCharacters" + File.separator;
	
	/**
	 * A hash collection of the results.
	 */
	private static AtomicLongMap<String> results = AtomicLongMap.create();
	
	public static void main(String args[]) {
		if (args.length > 0) {
			PATH = args[0];
			System.out.println("Setting the path to: " + PATH);
		}
		
		loadDefinitions();
		
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("Enter the id of the item to search for: ");
		
		do {
			int checkId = scanner.nextInt();
			
			System.out.println("Enter the minimum amount of an item: ");
			
			int minAmount = scanner.nextInt();
			
			check(checkId, minAmount);
			
		} while (scanner.hasNext());
		
		scanner.close();
	}
	
	/**
	 * Attempts to check for an item.
	 * 
	 * @param itemId
	 *            the item id
	 * @param itemAmount
	 *            the amount
	 */
	private static void check(int itemId, int itemAmount) {
		System.out.println("\n---");
		System.out.println("\nShowing results for: " + ItemDefinition.forId(itemId).getName() + " (" + itemId + ") x " + NumberFormat.getIntegerInstance().format(itemAmount) + "\n");
		
		File charFolder = new File(PATH);
		
		String files[] = charFolder.list();
		
		for (String s : files) {
			try {
				File charFile = new File(PATH + s);
				try (FileReader fileReader = new FileReader(charFile)) {
					JsonParser fileParser = new JsonParser();
					Gson builder = new GsonBuilder().create();
					JsonObject reader = (JsonObject) fileParser.parse(fileReader);
					
					String username = reader.get("username").getAsString();
					
					String[] strings = new String[] { "inventory", "looting-bag", "equipment", "bank-" };
					
					for (String string : strings) {
						if (string.equals("bank-")) {
							for (int i = 0; i < BankConstants.TOTAL_BANK_TABS; i++) {
								Item[] items = builder.fromJson(reader.get(string + i).getAsJsonArray(), Item[].class);
								
								for (Item item : items) {
									if (item == null || item.getId() != itemId) {
										continue;
									}
									
									if (item.getAmount() >= itemAmount) {
										results.addAndGet(username, item.getAmount());
									}
								}
							}
						} else {
							if (reader.has(string)) {
								long amount = getAmount(itemId, builder.fromJson(reader.get(string).getAsJsonArray(), Item[].class));

								if (amount >= itemAmount) {
									results.addAndGet(username, amount);
								}
							}
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			} catch (Exception Ioe) {
				Ioe.printStackTrace();
			}
		}
		
		long total = 0;
		
		for (String name : results.asMap().keySet()) {
			System.out.println(name + " - " + NumberFormat.getIntegerInstance().format(results.get(name)));
			total += results.get(name);
		}
		
		System.out.println("\nTotal: " + NumberFormat.getIntegerInstance().format(total) + "\n");
		System.out.println("---");
		System.out.println("\nEnter the id of the item to search for: ");
	}
	
	/**
	 * Gets the total amount of an item from the specified items array.
	 * 
	 * @param itemId
	 *            the item id
	 * @param items
	 *            the items
	 * @return the amount
	 */
	private static long getAmount(int itemId, Item[] items) {
		long total = 0;
		
		for (Item item : items) {
			if (item == null || item.getId() != itemId) {
				continue;
			}
			
			total += item.getAmount();
		}
		
		return total;
	}
	
	/**
	 * Loads the item definitions.
	 */
	private static void loadDefinitions() {
		try {
			FileReader reader = new FileReader(GameConstants.DEFINITIONS_DIRECTORY + "items.json");
			
			ItemDefinition[] defs = new Gson().fromJson(reader, ItemDefinition[].class);
			
			for (ItemDefinition def : defs) {
				ItemDefinition.definitions.put(def.getId(), def);
			}
			
			reader.close();
		} catch (Exception e) {
			System.out.println("Error loading item definitions.");
		}
	}
	
}
