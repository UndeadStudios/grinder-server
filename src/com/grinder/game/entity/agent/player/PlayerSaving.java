
package com.grinder.game.entity.agent.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.grinder.game.GameConstants;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager.Points;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.ItemDeserialiser;
import com.grinder.game.model.item.ItemSerializer;
import com.grinder.util.DiscordBot;
import com.grinder.util.Misc;
import com.grinder.util.time.TimeUnits;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PlayerSaving {

	private static final Logger LOGGER = LogManager.getLogger(PlayerSaving.class);

	public static void save(Player player) {
    	save(player, false);
	}

	public static void save(Player player, boolean offline) {

		if (player.isBotPlayer() || player.isNewPlayer())
			return;
		
		// Create the path and file objects.
		final Path path = Paths.get(GameConstants.PLAYER_DIRECTORY, player.getUsername() + ".json");
		
		final File file = path.toFile();

		if(!file.getParentFile().exists()) {
			if(file.getParentFile().mkdirs()) {
				LOGGER.info("Created non-existent character directory at " + file.getPath() + ".");
				if(!file.getParentFile().setWritable(true))
					LOGGER.error("Did not have access to make file writable, "+file.getParentFile());
			} else
				LOGGER.error("Could not create character directory at " + file.getPath() + ".");
		}

		if(!offline && !player.isActive()) {
			LOGGER.error("Did not save "+player+" due to the player not being registered.");
			return;
		}

		if (!file.getParentFile().exists()) {

			if (offline) {
				LOGGER.warn("Player save not found for username: \"" + player.getUsername() + "\".");
				return;
			}

			try {

				if(file.getParentFile().mkdirs())
					LOGGER.info("Created new directory at "+file.getParentFile());

			} catch (SecurityException e) {
				LOGGER.error("Unable to create directory for player data!", e);
			}
		}

		try (FileWriter writer = new FileWriter(file)) {

			final Gson gson = new GsonBuilder()
					.setPrettyPrinting()
					.registerTypeHierarchyAdapter(Item.class, ItemDeserialiser.INSTANCE)
					.registerTypeHierarchyAdapter(Item.class, ItemSerializer.INSTANCE)
					.create();

			final JsonObject object = new JsonObject();
			saveString(object, "username", player.getUsername().trim());
			saveString(object, "password", player.getPassword().trim());
			saveString(object, "title", player.getTitle());
			saveString(object, "player-rights", player.getRights().name());
			saveString(object, "game-mode", player.getGameMode().name());
			saveString(object, "mac-address", player.getMacAddress());
			saveString(object, "sn-address", player.getSnAddress());
			saveString(object, "connected-from", player.getHostAddress());
			saveString(object, "hd-serial-number", player.getHdSerialNumber());
			saveInt(object, "player-crown", player.getCrown());

			saveObject(gson, object, "position", player.getPosition());

			saveInt(object, "last-bounty-teleport", player.getBountyTeleportTimer().secondsRemaining());
			saveInt(object, "last_dfs", player.getDragonFireShieldTimer().secondsRemaining());
			saveString(object, "spell-book", player.getSpellbook().name());
			saveBoolean(object, "auto-retaliate", player.getCombat().retaliateAutomatically());
			saveBoolean(object, "isJailed", player.isJailed());
			saveBoolean(object, "canToggleVialCrushing", player.unlockedVialCrushing());
			saveBoolean(object, "vialCrushingToggled", player.isVialCrushingToggled());
			saveString(object, "clanchat", player.getClanChatName());
			saveBoolean(object, "target-teleport", player.isTargetTeleportUnlocked());
			saveInt(object, "last-veng", player.getVengeanceDelayTimer().secondsRemaining());
			saveInt(object, "last-bossZoneTeleport", player.getTheCursedVaultDelayTimer().secondsRemaining());
			saveObject(gson, object, "last-death", player.getLastDeath());
			saveInt(object, "run-energy", player.getRunEnergy());
			saveInt(object, "spec-percentage", player.getSpecialPercentage());
			saveInt(object, "poison-damage", player.getPoisonDamage());
			saveInt(object, "venom-damage", player.getVenomDamage());
			saveInt(object, "poison-immunity", player.getCombat().getPoisonImmunityTimer().secondsRemaining());
			saveInt(object, "venom_immunity", player.getCombat().getPoisonImmunityTimer().secondsRemaining());
			saveInt(object, "aggressivity-timer", player.getCombat().getAggressivityTimer().secondsRemaining());
			saveInt(object, "fire-immunity", player.getCombat().getFireImmunityTimer().secondsRemaining());
			saveInt(object, "super-fire-immunity", player.getCombat().getSuperFireImmunityTimer().secondsRemaining());
			saveInt(object, "teleblock-timer", player.getCombat().getTeleBlockTimer().secondsRemaining());
			saveInt(object, "antipoison-timer", player.getCombat().getPoisonImmunityTimer().secondsRemaining());
			saveInt(object, "antivenom-timer", player.getCombat().getVenomImmunityTimer().secondsRemaining());
			saveInt(object, "divine-attack", player.divineAttack.secondsRemaining());
			saveInt(object, "divine-strength", player.divineStrength.secondsRemaining());
			saveInt(object, "divine-defence", player.divineDefence.secondsRemaining());
			saveInt(object, "divine-range", player.divineRange.secondsRemaining());
			saveInt(object, "divine-magic", player.divineMagic.secondsRemaining());
			saveInt(object, "voting-bonus-timer", player.getVotingBonusTimer().secondsRemaining());
			saveInt(object, "prayerblock-timer", player.getCombat().getPrayerBlockTimer().secondsRemaining());
			saveInt(object, "protectPrayerblock-timer", player.getCombat().getProtectBlockTimer().secondsRemaining());
			saveInt(object, "target-search-timer", player.getCombat().getBountyHuntController().getRemainingSearchTimerSeconds());
			saveInt(object, "special-attack-restore-timer", player.getSpecialAttackRestore().secondsRemaining());
			saveString(object, "skull-type", player.getSkullType().name());

			saveObject(gson, object, "recent-kills", player.getCombat().getBountyHuntController().getRecentKills());
			saveObject(gson, object, "recent-IPS", player.getRecentIPS());

			saveInt(object, "amount-donated", player.getAttributes().numInt(Attribute.AMOUNT_PAID));
			saveInt(object, "poison-damage", player.getPoisonDamage());
			saveInt(object, "blowpipe-scales", player.getBlowpipeScales());
			saveInt(object, "bank-pin", player.pin());
			saveInt(object, "failed-pin-tries", player.getFailedBankPinTries());
			saveInt(object, "map-instance", player.instancedMapTick);

			saveJsonElement(object, "item-degradation", player.getItemDegradationManager().serialize());
			saveJsonElement(object, "clue-scroll-stats", player.getClueScrollManager().serialize());

			saveObject(gson, object, "killed-barrows", player.getKilledBarrows().toArray());
			saveString(object, "final-barrow", player.getFinalBarrow().name());
			saveObject(gson, object, "inventory", player.getInventory().getItems());
			saveObject(gson, object, "equipment", player.getEquipment().getItems());
			saveObject(gson, object, "appearance", player.getAppearance().getLook());
			saveObject(gson, object, "skills", player.getSkillManager().getSkills());

			saveObject(gson, object, "quick-prayers", player.getQuickPrayers().getPrayers());

			saveObject(gson, object, "friends", player.getRelations().getFriendList().toArray());
			saveObject(gson, object, "ignores", player.getRelations().getIgnoreList().toArray());
			saveObject(gson, object, "chat-modes", player.getChatSettings().getModes());

			saveObject(gson, object, "notes", player.getNotes().getNotes());
			saveObject(gson, object, "achievements_new", player.getAchievements().getProgress());

			saveInt(object, "monster-kills", player.getKillTracker().getMonstersKilled());
			saveInt(object, "bosses-kills", player.getKillTracker().getBossesKilled());
			saveObject(gson, object,"npc-tracker", player.getKillTracker().getKills());

			saveObject(gson, object,"looting-bag", player.getLootingBag().getContainer().getItems());

			saveObject(gson, object,"skill-points", player.getSkillTaskManager().getPoints());
			saveObject(gson, object,"previous-teleports", player.getTeleport().getPreviousTeleports());

			saveObject(gson, object, "custom-presets", player.getPresets());

			saveObject(gson, object, "welcome", player.getWelcome().getWelcome());
			saveObject(gson, object, "slayer-task", player.getSlayer().getTask());
			saveObject(gson, object, "blocked-monsters", player.getSlayer().getBlockedMonsters());
			saveObject(gson, object, "slayer-unlocks", player.getSlayer().getUnlocked());
			saveObject(gson, object, "slayer-extends", player.getSlayer().getExtended());
			saveObject(gson, object, "agility-obstacle", player.getAgility().getCompleted());
			//System.out.println(player.getCombat().getFightType().toString());

			saveString(object, "fight-type", player.getCombat().getFightType().toString());
			saveObject(gson, object, "creation-date", player.getCreationDate());
			saveObject(gson, object, "time-played", player.getTimePlayed(TimeUnits.MILLISECOND));
			saveObject(gson, object, "essence-pouches", player.pouches);

			saveObject(gson, object, "all-skill-task-new", player.getSkillTaskManager().getAllTasks());
			object.add("quest-track", gson.toJsonTree(player.getQuest().tracker.progress));
			for(Points points : Points.values()) { 
				if(!points.isSave()) {
					continue;
				}
				saveInt(object, points.name(), player.getPoints().get(points));
			}

			/* BANK */
			for (int i = 0; i < player.getBanks().length; i++) {
				if (player.getBank(i) != null) {
					saveObject(gson, object,"bank-" + i, player.getBank(i).getValidItems());
				}
			}

			saveObject(gson, object, "yell-colors", player.getYellColors());
			saveString(object, "yell-title", player.getYellTitle());

			saveObject(gson, object, "colorful-item-map", player.getColorfulItemMap());
			saveObject(gson, object,"rune-pouch", player.getRunePouch().getItems());
			saveObject(gson, object,"safe-deposit", player.getSafeDeposit().getItems());

			saveObject(gson, object, "mystery-box-items-list", player.getMysteryBoxItemsLists());

			saveObject(gson, object, "purchased-titles", player.getPurchasedTitles().toArray());

			saveObject(gson, object, "house", player.getHouse());

			saveJsonElement(object, "attributeMap", player.getAttributes().serialize());

			saveObject(gson, object, "collection-log", player.getCollectionLog().getLogEntries());

			saveInt(object, "task-type-one", EntityExtKt.getInt(player, Attribute.TASK_TYPE_ONE, 0));
			saveInt(object, "task-type-two", EntityExtKt.getInt(player, Attribute.TASK_TYPE_TWO, 0));
			saveInt(object, "task-type-three", EntityExtKt.getInt(player, Attribute.TASK_TYPE_THREE, 0));
			saveInt(object, "task-type-four", EntityExtKt.getInt(player, Attribute.TASK_TYPE_FOUR, 0));

			saveInt(object, "task-id-one", EntityExtKt.getInt(player, Attribute.TASK_ID_ONE, 0));
			saveInt(object, "task-id-two", EntityExtKt.getInt(player, Attribute.TASK_ID_TWO, 0));
			saveInt(object, "task-id-three", EntityExtKt.getInt(player, Attribute.TASK_ID_THREE, 0));
			saveInt(object, "task-id-four", EntityExtKt.getInt(player, Attribute.TASK_ID_FOUR, 0));

			saveString(object, "task-id-one-t", EntityExtKt.getString(player, Attribute.TASK_ID_ONE_T, ""));
			saveString(object, "task-id-two-t", EntityExtKt.getString(player, Attribute.TASK_ID_TWO_T, ""));
			saveString(object, "task-id-three-t", EntityExtKt.getString(player, Attribute.TASK_ID_THREE_T, ""));
			saveString(object, "task-id-four-t", EntityExtKt.getString(player, Attribute.TASK_ID_FOUR_T, ""));

			saveInt(object, "task-amount-done-one", EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 0));
			saveInt(object, "task-amount-done-two", EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 0));
			saveInt(object, "task-amount-done-three", EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 0));
			saveInt(object, "task-amount-done-four", EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 0));

			saveInt(object, "task-reward-item-id-one", EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_ID_ONE, 0));
			saveInt(object, "task-reward-item-id-two", EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_ID_TWO, 0));
			saveInt(object, "task-reward-item-id-three", EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_ID_THREE, 0));
			saveInt(object, "task-reward-item-id-four", EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_ID_FOUR, 0));

			saveInt(object, "task-reward-item-amount-one", EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_ONE, 0));
			saveInt(object, "task-reward-item-amount-two", EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_TWO, 0));
			saveInt(object, "task-reward-item-amount-three", EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_THREE, 0));
			saveInt(object, "task-reward-item-amount-four", EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_FOUR, 0));

			saveBoolean(object, "task-claimed-one", EntityExtKt.getBoolean(player, Attribute.TASK_CLAIMED_ONE, false));
			saveBoolean(object, "task-claimed-two", EntityExtKt.getBoolean(player, Attribute.TASK_CLAIMED_TWO, false));
			saveBoolean(object, "task-claimed-three", EntityExtKt.getBoolean(player, Attribute.TASK_CLAIMED_THREE, false));
			saveBoolean(object, "task-claimed-four", EntityExtKt.getBoolean(player, Attribute.TASK_CLAIMED_FOUR, false));

			saveObject(gson, object, "inventory-presets-0", player.presetInventory[0].getItems());
			saveObject(gson, object, "inventory-presets-1", player.presetInventory[1].getItems());
			saveObject(gson, object, "inventory-presets-2", player.presetInventory[2].getItems());
			saveObject(gson, object, "inventory-presets-3", player.presetInventory[3].getItems());
			saveObject(gson, object, "inventory-presets-4", player.presetInventory[4].getItems());
			saveObject(gson, object, "inventory-presets-5", player.presetInventory[5].getItems());
			saveObject(gson, object, "inventory-presets-6", player.presetInventory[6].getItems());
			saveObject(gson, object, "inventory-presets-7", player.presetInventory[7].getItems());
			saveObject(gson, object, "inventory-presets-8", player.presetInventory[8].getItems());
			saveObject(gson, object, "inventory-presets-9", player.presetInventory[9].getItems());

			saveObject(gson, object, "equipment-presets-0", player.presetEquipment[0].getItems());
			saveObject(gson, object, "equipment-presets-1", player.presetEquipment[1].getItems());
			saveObject(gson, object, "equipment-presets-2", player.presetEquipment[2].getItems());
			saveObject(gson, object, "equipment-presets-3", player.presetEquipment[3].getItems());
			saveObject(gson, object, "equipment-presets-4", player.presetEquipment[4].getItems());
			saveObject(gson, object, "equipment-presets-5", player.presetEquipment[5].getItems());
			saveObject(gson, object, "equipment-presets-6", player.presetEquipment[6].getItems());
			saveObject(gson, object, "equipment-presets-7", player.presetEquipment[7].getItems());
			saveObject(gson, object, "equipment-presets-8", player.presetEquipment[8].getItems());
			saveObject(gson, object, "equipment-presets-9", player.presetEquipment[9].getItems());

			saveInt(object, "presets-amount", player.presetsAmount);

			saveObject(gson, object, "preset-names", player.presetNames);

			saveObject(gson, object, "preset-books", player.presetMagicBooks);

			final String serialisedAccount = gson.toJson(object);
			if (serialisedAccount.isBlank()){
				DiscordBot.INSTANCE.sendModMessage("Attempted to save empty json string for {"+player+"} in {"+file+"}. @everyone (MESSAGE STAN!!!!)");
				LOGGER.error("Attempted to save empty json string for {"+player+"} in {"+file+"}.");
				return;
			}
			writer.write(serialisedAccount);
			writer.flush();
			writer.close();

			LOGGER.info("Successfully saved "+player+"");
		} catch (Exception e) {
			LOGGER.error("An exception occurred during saving "+player+"!", e);
		}
	}

	public static void saveJsonElement(JsonObject object, String name, JsonElement value) {
		try {
			object.add(name, value);
		}  catch (Exception e){
			LOGGER.error("Failed to save {"+name+", "+value+"}", e);
		}
	}

	public static void saveString(JsonObject jsonObject, String name, String value) {
    	try {
			jsonObject.addProperty(name, value);
		} catch (Exception e){
			LOGGER.error("Failed to save {"+name+", "+value+"}", e);
		}
    }

	public static void saveInt(JsonObject jsonObject, String name, int value) {
		try {
			jsonObject.addProperty(name, value);
		} catch (Exception e){
			LOGGER.error("Failed to save {"+name+", "+value+"}", e);
		}
    }

	public static void saveLong(JsonObject jsonObject, String name, long value) {
		try {
			jsonObject.addProperty(name, value);
		} catch (Exception e){
			LOGGER.error("Failed to save {"+name+", "+value+"}", e);
		}
	}

	public static void saveBoolean(JsonObject jsonObject, String name, boolean value) {
    	try {
			jsonObject.addProperty(name, value);
		} catch (Exception e){
			LOGGER.error("Failed to save {"+name+", "+value+"}", e);
		}
	}

	public static void saveObject(Gson gson, JsonObject jsonObject, String name, Object value){
    	try {
			saveJsonElement(jsonObject, name, gson.toJsonTree(value));
		} catch (Exception e){
    		LOGGER.error("Failed to save {"+name+", "+value+"}", e);
		}
	}

	public static boolean playerExists(String name) {
		name = Misc.formatPlayerName(name.toLowerCase());
		return new File(GameConstants.PLAYER_DIRECTORY+"" + name + ".json").exists();
	}
}
