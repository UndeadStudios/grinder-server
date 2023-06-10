package com.grinder.game.entity.agent.player;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.grinder.Config;
import com.grinder.game.GameConstants;
import com.grinder.game.content.GameMode;
import com.grinder.game.content.collectionLog.CollectionLog;
import com.grinder.game.content.item.coloring.ItemColorCustomizer.ColorfulItem;
import com.grinder.game.content.item.mysterybox.MysteryBoxRewardItem;
import com.grinder.game.content.item.mysterybox.SpinMysteryBoxType;
import com.grinder.game.content.minigame.barrows.BarrowsBrother;
import com.grinder.game.content.miscellaneous.WelcomeManager.Welcome;
import com.grinder.game.content.miscellaneous.presets.Presetable;
import com.grinder.game.content.pvm.MonsterKillTracker.KillTrack;
import com.grinder.game.content.skill.Skills;
import com.grinder.game.content.skill.skillable.impl.agility.Agility.Obstacles;
import com.grinder.game.content.skill.skillable.impl.cons.House;
import com.grinder.game.content.skill.skillable.impl.magic.Teleporting.PreviousTeleport;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler.PrayerType;
import com.grinder.game.content.skill.skillable.impl.runecrafting.pouch.EssencePouch;
import com.grinder.game.content.skill.skillable.impl.runecrafting.pouch.PouchType;
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerTask;
import com.grinder.game.content.skill.task.SkillMasterType;
import com.grinder.game.content.skill.task.SkillTask;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponFightType;
import com.grinder.game.model.MagicSpellbook;
import com.grinder.game.model.Position;
import com.grinder.game.model.SkullType;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager.Points;
import com.grinder.game.model.attribute.AttributeMap;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.ItemDeserialiser;
import com.grinder.game.model.item.ItemSerializer;
import com.grinder.game.model.item.container.bank.Bank;
import com.grinder.game.model.item.container.bank.BankConstants;
import com.grinder.game.model.item.container.player.SafeDeposit;
import com.grinder.net.codec.login.LoginResponses;
import com.grinder.util.DiscordBot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;

public class PlayerLoading {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final TypeReference<Map<String, Integer>> MAP_STRING_INTEGER_TYPE = new TypeReference<Map<String, Integer>>() {

    };

    private final static Logger LOGGER = LogManager.getLogger(PlayerLoading.class);

    public static int getResult(Player player, boolean dontCheckPassword) {
        return getResult(player, dontCheckPassword, false);
    }
    
    public static int getResult(Player player, boolean dontCheckPassword, boolean offline) {

        // Create the path and file objects.
        final Path path = Paths.get(GameConstants.PLAYER_DIRECTORY, player.getUsername() + ".json");
        final File file = path.toFile();

        // If the file doesn't exist, we're logging in for the first
        // time and can skip all of this.
        if (!file.exists())
            return LoginResponses.NEW_ACCOUNT;

        // Now read the properties from the json parser.
        try (final FileReader fileReader = new FileReader(file)) {

            final JsonParser fileParser = new JsonParser();
            final Gson builder = new GsonBuilder()
                    .registerTypeHierarchyAdapter(Item.class, ItemDeserialiser.INSTANCE)
                    .registerTypeHierarchyAdapter(Item.class, ItemSerializer.INSTANCE)
                    .create();

            final JsonElement e = fileParser.parse(fileReader);

            if(e.isJsonNull()) {
                System.err.println(file.getName() + " is null buddy.");
                // Discord logging
                if (DiscordBot.ENABLED) DiscordBot.INSTANCE.sendServerLogs("[ACCOUNT NULL ON LOGIN]: " + player.getUsername() + " files is corrupted and has been reset.");
                return 0;
            }
            final JsonObject reader = (JsonObject) e;
            if (reader.has("username")) {
                player.setUsername(reader.get("username").getAsString());
            }

            if (reader.has("password")) {
                String password = reader.get("password").getAsString();
                if (!dontCheckPassword) {
                    if (!offline && !player.getPassword().equals(password)) {
                        return LoginResponses.LOGIN_INVALID_CREDENTIALS;
                    }
                    player.setPassword(password);
                }
            }
            if (reader.has("mac-address")) {
                player.setMacAddress(reader.get("mac-address").getAsString());
            }
            if (reader.has("sn-address")) {
                player.setSnAddress(reader.get("sn-address").getAsString());
            }
            if (reader.has("quest-track")) {
                player.getQuest().tracker.progress = (HashMap<String, Integer>) MAPPER
                        .readValue(reader.get("quest-track").toString(), MAP_STRING_INTEGER_TYPE);
            }
            if (reader.has("hd-serial-number")) {
                player.setHdSerialNumber(reader.get("hd-serial-number").getAsString());
            }
            if (reader.has("title")) {
                String title = reader.get("title").getAsString();
                if (!title.equals("empty")) {
                    player.setTitle(title);
                }
            }

            if (reader.has("player-rights")) {
                String rights = reader.get("player-rights").getAsString();
                player.setRights(PlayerRights.valueOf(rights));
                if (Config.DEVELOPER_MACHINE)
                    player.setRights(PlayerRights.DEVELOPER);
            }

            if (reader.has("game-mode")) {
                player.setGameMode(GameMode.valueOf(reader.get("game-mode").getAsString()));
            }

            if (reader.has("creation-date")) {
                player.setCreationDate(builder.fromJson(reader.get("creation-date"), Timestamp.class));
                //if (player.getCreationDate() == null)
                //    player.setCreationDate(new Timestamp(new Date().getTime()));
            }

            if (reader.has("player-crown")) {
                int rights = reader.get("player-crown").getAsInt();
                player.setCrown(rights);
            }

            if (reader.has("position")) {
                player.setTeleportPosition(builder.fromJson(reader.get("position"), Position.class));
                if (player.getPosition().getZ() < 0) {
                    LOGGER.warn("Player {" + player + "} has an invalid height value, setting to default pos!");
                    player.setTeleportPosition(GameConstants.DEFAULT_POSITION.clone());
                }
            }
            if (reader.has("designer")) {
                EntityExtKt.setBoolean(player, Attribute.DESIGNER, reader.get("designer").getAsBoolean(), false);
            }

            if (reader.has("middleman")) {
                EntityExtKt.setBoolean(player, Attribute.MIDDLEMAN, reader.get("middleman").getAsBoolean(), false);
            }

            if (reader.has("eventhost")) {
                EntityExtKt.setBoolean(player, Attribute.EVENT_HOST, reader.get("eventhost").getAsBoolean(), false);
            }

            if (reader.has("veteran")) {
                EntityExtKt.setBoolean(player, Attribute.VETERAN, reader.get("veteran").getAsBoolean(),false);
            }

            if (reader.has("exstaff")) {
                EntityExtKt.setBoolean(player, Attribute.EX_STAFF, reader.get("exstaff").getAsBoolean(), false);
            }

            if (reader.has("respected")) {
                EntityExtKt.setBoolean(player, Attribute.RESPECTED, reader.get("respected").getAsBoolean(), false);
            }

            if (reader.has("contributor")) {
                EntityExtKt.setBoolean(player, Attribute.CONTRIBUTOR, reader.get("contributor").getAsBoolean(), false);
            }

            if (reader.has("dicer")) {
                player.setDicer(reader.get("dicer").getAsBoolean());
            }

            if (reader.has("time-played")) {
                player.setTimePlayed((reader.get("time-played").getAsLong()));
            }

            if (reader.has("youtuber")) {
                EntityExtKt.setBoolean(player, Attribute.YOUTUBER, reader.get("youtuber").getAsBoolean(), false);
            }

            if (reader.has("wiki-editor")) {
                EntityExtKt.setBoolean(player, Attribute.WIKI_EDITOR, reader.get("wiki-editor").getAsBoolean(), false);
            }

            if (reader.has("last-bounty-teleport")) {
                player.getBountyTeleportTimer().start(reader.get("last-bounty-teleport").getAsInt());
            }

            if (reader.has("last_dfs")) {
                player.getDragonFireShieldTimer().start(reader.get("last_dfs").getAsInt());
            }
            if (reader.has("spell-book")) {
                player.setSpellbook(MagicSpellbook.valueOf(reader.get("spell-book").getAsString()));
            }

            if (reader.has("fight-type")) {
                player.getCombat().setFightType(WeaponFightType.valueOf(reader.get("fight-type").getAsString()));
            }

            if (reader.has("auto-retaliate")) {
                player.getCombat().setAutoRetaliate(reader.get("auto-retaliate").getAsBoolean());
            }

            if (reader.has("xp-locked")) {
                player.getAttributes()
                        .boolAttr(Attribute.EXPERIENCED_LOCKED, false)
                        .setValue(reader.get("xp-locked").getAsBoolean());
            }

            if (reader.has("multiply-xp-drops")) {
                player.getAttributes()
                        .boolAttr(Attribute.MULTIPLY_XP_DROPS, false)
                        .setValue(reader.get("multiply-xp-drops").getAsBoolean());
            }

            if (reader.has("changed-pass")) {
                EntityExtKt.setBoolean(player, Attribute.CHANGED_PASS, reader.get("changed-pass").getAsBoolean(), false);
            }

            if (reader.has("isJailed")) {
                player.setJailed(reader.get("isJailed").getAsBoolean());
            }

            if (reader.has("pendingRandomEvent")) {
                EntityExtKt.setBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, reader.get("pendingRandomEvent").getAsBoolean(), false);
            }

            if (reader.has("pendingRandomEvent2")) {
                EntityExtKt.setBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, reader.get("pendingRandomEvent2").getAsBoolean(), false);
            }

            if (reader.has("hasTriggerRandomEvent")) {
                EntityExtKt.setBoolean(player, Attribute.HAS_TRIGGER_RANDOM_EVENT, reader.get("hasTriggerRandomEvent").getAsBoolean(), false);
            }

            if (reader.has("canToggleVialCrushing")) {
                player.setUnlockedVialCrushing(reader.get("canToggleVialCrushing").getAsBoolean());
            }

            if (reader.has("vialCrushingToggled")) {
                player.setVialCrushingToggled(reader.get("vialCrushingToggled").getAsBoolean());
            }

            if (reader.has("clanchat")) {
                player.setClanChatName(reader.get("clanchat").getAsString());
            }

            if (reader.has("target-teleport")) {
                player.setTargetTeleportUnlocked(reader.get("target-teleport").getAsBoolean());
            }

            if (reader.has("preserve")) {
                player.setPreserveUnlocked(reader.get("preserve").getAsBoolean());
            }

            if (reader.has("rigour")) {
                player.setRigourUnlocked(reader.get("rigour").getAsBoolean());
            }

            if (reader.has("augury")) {
                player.setAuguryUnlocked(reader.get("augury").getAsBoolean());
            }

            if (reader.has("usedSaradominLight")) {
                player.getAttributes()
                        .boolAttr(Attribute.CONSUMED_SARADOMIN_LIGHT, false)
                        .setValue(reader.get("usedSaradominLight").getAsBoolean());
            }

            if (reader.has("last-veng")) {
                player.getVengeanceDelayTimer().start(reader.get("last-veng").getAsInt());
            }

            if (reader.has("last-bossZoneTeleport")) {
                player.getTheCursedVaultDelayTimer().start(reader.get("last-bossZoneTeleport").getAsInt());
            }

            if (reader.has("last-death")) {
                player.setLastDeath(builder.fromJson(reader.get("last-death"), Date.class));
            }

            if (reader.has("monster-kills")) {
                player.getKillTracker().incrementMonsterKillCount(reader.get("monster-kills").getAsInt());
            }
            if (reader.has("bosses-kills")) {
                player.getKillTracker().incrementBossKillCount(reader.get("bosses-kills").getAsInt());
            }
            if (reader.has("spec-percentage")) {
                player.setSpecialPercentage(reader.get("spec-percentage").getAsInt());
            }
            if (reader.has("recoil-damage")) {
                player.getAttributes()
                        .numAttr(Attribute.RING_OF_RECOIL_CHARGES, 0)
                        .setValue(reader.get("recoil-damage").getAsInt());
            }
            if (reader.has("poison-damage")) {
                player.setPoisonDamage(reader.get("poison-damage").getAsInt());
            }
            if (reader.has("venom-damage")) {
                player.setVenomDamage(reader.get("venom-damage").getAsInt());
            }
            if (reader.has("blowpipe-scales")) {
                player.setBlowpipeScales(reader.get("blowpipe-scales").getAsInt());
            }
            if (reader.has("bank-pin")) {
                player.setPin(reader.get("bank-pin").getAsInt());
            }
            if (reader.has("failed-pin-tries")) {
                player.setFailedBankPinTries(reader.get("failed-pin-tries").getAsInt());
            }
            if (reader.has("account-flagged")) {
                player.setAccountFlagged(reader.get("account-flagged").getAsBoolean());
            }
            if (reader.has("presetsOnDeath")) {
                player.setOpenPresetsOnDeath(reader.get("presetsOnDeath").getAsBoolean());
            }

            if (reader.has("receivedStarter")) {
                player.setReceivedStarter(reader.get("receivedStarter").getAsBoolean());
            }

            if (reader.has("onTutorial")) {
                player.setOnTutorialMode(reader.get("onTutorial").getAsBoolean());
            }

            if (reader.has("map-instance")) {
                player.instancedMapTick = reader.get("map-instance").getAsInt();
            }

            if (reader.has("bonecrusher-active")) {
                player.getAttributes()
                        .boolAttr(Attribute.BONECRUSHER_ACTIVE, false)
                        .setValue(reader.get("bonecrusher-active").getAsBoolean());
            }

            if (reader.has("bonecrusher-charges")) {
                player.getAttributes()
                        .numAttr(Attribute.BONECRUSHER_CHARGES, 0)
                        .setValue(reader.get("bonecrusher-charges").getAsInt());
            }

            if (reader.has("ROW-activated")) {
                player.setRingofWealthActivated(reader.get("ROW-activated").getAsBoolean());
            }

            if (reader.has("item-degradation")) {
                player.getItemDegradationManager().read(reader.get("item-degradation"));
            }

            if (reader.has("clue-scroll-stats")) {
                player.getClueScrollManager().read(reader.get("clue-scroll-stats"));
            }

            if (reader.has("poison-immunity")) {
                player.getCombat().getPoisonImmunityTimer().start(reader.get("poison-immunity").getAsInt());
            }

            if (reader.has("venom-immunity")) {
                player.getCombat().getVenomImmunityTimer().start(reader.get("venom-immunity").getAsInt());
            }

            if (reader.has("divine-attack")) {
                player.divineAttack.start(reader.get("divine-attack").getAsInt());
            }

            if (reader.has("divine-strength")) {
                player.divineStrength.start(reader.get("divine-strength").getAsInt());
            }

            if (reader.has("divine-defence")) {
                player.divineDefence.start(reader.get("divine-defence").getAsInt());
            }

            if (reader.has("divine-range")) {
                player.divineRange.start(reader.get("divine-range").getAsInt());
            }

            if (reader.has("divine-magic")) {
                player.divineMagic.start(reader.get("divine-magic").getAsInt());
            }

            if (reader.has("aggressivity-timer")) {
                player.getCombat().getAggressivityTimer().start(reader.get("aggressivity-timer").getAsInt());
            }

            if (reader.has("fire-immunity")) {
                player.getCombat().getFireImmunityTimer().start(reader.get("fire-immunity").getAsInt());
            }

            if (reader.has("super-fire-immunity")) {
                player.getCombat().getSuperFireImmunityTimer().start(reader.get("super-fire-immunity").getAsInt());
            }

            if (reader.has("teleblock-timer")) {
                player.getCombat().getTeleBlockTimer().start(reader.get("teleblock-timer").getAsInt());
            }

            if (reader.has("voting-bonus-timer")) {
                player.getVotingBonusTimer().start(reader.get("voting-bonus-timer").getAsInt());
            }

            if (reader.has("antipoison-timer")) {
                player.getCombat().getPoisonImmunityTimer().start(reader.get("antipoison-timer").getAsInt());
            }

            if (reader.has("antivenom-timer")) {
                player.getCombat().getVenomImmunityTimer().start(reader.get("antivenom-timer").getAsInt());
            }

            if (reader.has("prayerblock-timer")) {
                player.getCombat().getPrayerBlockTimer().start(reader.get("prayerblock-timer").getAsInt());
            }

            if (reader.has("protectPrayerblock-timer")) {
                player.getCombat().getProtectBlockTimer().start(reader.get("protectPrayerblock-timer").getAsInt());
            }

            if (reader.has("target-search-timer")) {
                player.getCombat().getBountyHuntController().startSearchTimer(reader.get("target-search-timer").getAsInt());
            }

            if (reader.has("special-attack-restore-timer")) {
                player.getSpecialAttackRestore().start(reader.get("special-attack-restore-timer").getAsInt());
            }

            if (reader.has("skull-timer")) {
                player.setSkullTimer(reader.get("skull-timer").getAsInt());
            }

            if (reader.has("skull-type")) {
                player.setSkullType(SkullType.valueOf(reader.get("skull-type").getAsString()));
            }

            if (reader.has("running")) {
                player.setRunning(reader.get("running").getAsBoolean());
            }

            if (reader.has("run-energy")) {
                player.setRunEnergy(reader.get("run-energy").getAsInt());
            }
            if (reader.has("recent-kills")) {
                String[] recentKills = builder.fromJson(reader.get("recent-kills").getAsJsonArray(), String[].class);
                for (String hostAddress : recentKills) {
                    player.getCombat().getBountyHuntController().addRecentKill(hostAddress);
                }
            }
            if (reader.has("recent-IPS")) {
                String[] recentIPS = builder.fromJson(reader.get("recent-IPS").getAsJsonArray(), String[].class);
                for (String l : recentIPS) {
                    player.getRecentIPS().add(l);
                }
            }

            if (reader.has("blocked-monsters")) {
                String[] monsters = builder.fromJson(reader.get("blocked-monsters").getAsJsonArray(), String[].class);
                for (String l : monsters) {
                    player.getSlayer().getBlockedMonsters().add(l);
                }
            }
            if (reader.has("yell-credits")) {
                player.getAttributes()
                        .numAttr(Attribute.YELL_CREDITS, 0)
                        .setValue(reader.get("yell-credits").getAsInt());
            }
            if (reader.has("amount-donated")) {
                player.getAttributes().numAttr(Attribute.AMOUNT_PAID, 0).setValue(reader.get("amount-donated").getAsInt());
            }

            if (EntityExtKt.hasAttribute(player, Attribute.LAST_GODZILLA_ENTRY)) {
                EntityExtKt.setLong(player, Attribute.LAST_STUCK, EntityExtKt.getLong(player, Attribute.LAST_GODZILLA_ENTRY, System.currentTimeMillis() - 1800000), System.currentTimeMillis() - 1800000);
            }
            if (reader.has("streakHoursElapsed")) {
                EntityExtKt.setLong(player, Attribute.STREAK_HOURS_ELAPSED, EntityExtKt.getLong(player, Attribute.STREAK_HOURS_ELAPSED, 0), 0);
            }
            if (reader.has("killed-barrows")) {
                BarrowsBrother[] brothers = builder.fromJson(reader.get("killed-barrows").getAsJsonArray(), BarrowsBrother[].class);
                Arrays.stream(brothers).forEach(player.getKilledBarrows()::add);
            }
            if (reader.has("final-barrow")) {
                String barrow = reader.get("final-barrow").getAsString();
                player.setFinalBarrow(BarrowsBrother.valueOf(barrow));
            }
            if (reader.has("agility-obstacle")) {
                player.getAgility().setCompleted(builder.fromJson(reader.get("agility-obstacle").getAsJsonArray(), boolean[].class));

                if (player.getAgility().getCompleted().length < Obstacles.values().length) {
                    player.getAgility().setCompleted(Arrays.copyOf(player.getAgility().getCompleted(), Obstacles.values().length));
                }
            }
            if (reader.has("slayer-unlocks")) {
                player.getSlayer()
                        .setUnlocked(builder.fromJson(reader.get("slayer-unlocks").getAsJsonArray(), boolean[].class));
            }
            if (reader.has("slayer-extends")) {
                player.getSlayer()
                        .setExtended(builder.fromJson(reader.get("slayer-extends").getAsJsonArray(), boolean[].class));
            }
            if (reader.has("custom-presets")) {
                player.setPresets(builder.fromJson(reader.get("custom-presets").getAsJsonArray(), Presetable[].class));
            }
            if (reader.has("inventory")) {
                player.getInventory()
                        .setItems(builder.fromJson(reader.get("inventory").getAsJsonArray(), Item[].class));
            }

            if (reader.has("looting-bag")) {
                player.getLootingBag().getContainer()
                        .setItems(builder.fromJson(reader.get("looting-bag").getAsJsonArray(), Item[].class));
            }

            if (reader.has("equipment")) {
                player.getEquipment()
                        .setItems(builder.fromJson(reader.get("equipment").getAsJsonArray(), Item[].class));
            }

            if (reader.has("essence-pouches")) {
                Type type = new TypeToken<Map<PouchType, EssencePouch>>() {
                }.getType();
                player.pouches.putAll(builder.fromJson(reader.get("essence-pouches").getAsJsonObject(), type));
            }

            if (reader.has("appearance")) {
                /*
                 * Temporary fix for recoloring indexes out of bounds for the new fixed client side color arrays
                 * Retains color if in bounds, tries to set color to proper color if out of bounds
                 */
                int[] look = builder.fromJson(reader.get("appearance").getAsJsonArray(), int[].class);
                for (int i = 0; i < look.length; i++) {
                    int length = -1;
                    switch (i) {
                        case Appearance.HAIR_COLOUR:
                            length = 24;
                            break;
                        case Appearance.TORSO_COLOUR:
                        case Appearance.LEG_COLOUR:
                        case Appearance.SKIN_COLOUR:
                            length = 16;
                            break;
                        case Appearance.FEET_COLOUR:
                            length = 6;
                            break;
                    }
                    if (length != -1 && look[i] >= length)
                        look[i] = look[i] % length;
                }
                player.getAppearance().set(look);
            }

            if (reader.has("achievements_new")) {
                player.getAchievements()
                        .copyProgress(builder.fromJson(reader.get("achievements_new").getAsJsonArray(), int[].class));
            }
            if (reader.has("all-skill-task-new")) {
                try {
                    Type typeOfHashMap = new TypeToken<Map<SkillMasterType, SkillTask>>() {
                    }.getType();
                    Map<SkillMasterType, SkillTask> tasks = builder.fromJson(reader.get("all-skill-task-new").getAsJsonObject(), typeOfHashMap);

                    Arrays.stream(SkillMasterType.values()).forEach(it ->
                            tasks.putIfAbsent(it, null)
                    );

                    player.getSkillTaskManager().setTasks(tasks);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.err.println("Failed to load skilling tasks - resetting tasks.");
                }
            }

            if (reader.has("last-daily-flax")) {
                player.setLastDailyFlax(reader.get("last-daily-flax").getAsLong());
            }

            if (reader.has("skill-points")) {
                player.getSkillTaskManager()
                        .setPoints(builder.fromJson(reader.get("skill-points").getAsJsonArray(), int[].class));
            }
            if (reader.has("welcome")) {
                player.getWelcome().setWelcome(builder.fromJson(reader.get("welcome"), Welcome.class));
            }
            if (reader.has("skills")) {
                player.getSkillManager().setSkills(builder.fromJson(reader.get("skills"), Skills.class));
            }

            if (reader.has("quick-prayers")) {
                player.getQuickPrayers().setPrayers(builder.fromJson(reader.get("quick-prayers"), PrayerType[].class));
            }

            if (reader.has("slayer-task")) {
                player.getSlayer().setTask(builder.fromJson(reader.get("slayer-task"), SlayerTask.class));
            }

            if (reader.has("friends")) {
                long[] friends = builder.fromJson(reader.get("friends").getAsJsonArray(), long[].class);

                for (long l : friends) {
                    player.getRelations().getFriendList().add(l);
                }
            }

            if (reader.has("ignores")) {
                long[] ignores = builder.fromJson(reader.get("ignores").getAsJsonArray(), long[].class);

                for (long l : ignores) {
                    player.getRelations().getIgnoreList().add(l);
                }
            }

            if (reader.has("chat-modes")) {
                player.getChatSettings().setModes(builder.fromJson(reader.get("chat-modes").getAsJsonArray(), int[].class));
            }

            if (reader.has("notes")) {
                String[] string = builder.fromJson(reader.get("notes").getAsJsonArray(), String[].class);

                for (String l : string) {
                    player.getNotes().getNotes().add(l);
                }
            }

            if (reader.has("npc-tracker")) {
                KillTrack[] string = builder.fromJson(reader.get("npc-tracker").getAsJsonArray(), KillTrack[].class);


                for (KillTrack l : string) {
                    player.getKillTracker().getKills().add(l);
                }
            }

            if (reader.has("previous-teleports")) {
                PreviousTeleport[] string = builder.fromJson(reader.get("previous-teleports").getAsJsonArray(),
                        PreviousTeleport[].class);

                for (PreviousTeleport l : string) {
                    player.getTeleport().getPreviousTeleports().add(l);
                }
            }

            for (Points points : Points.values()) {
                if (reader.has(points.name())) {
                    player.getPoints().set(points, reader.get(points.name()).getAsInt());
                }
            }

            if (reader.has("insert-mode")) {
                player.setInsertMode(reader.get("insert-mode").getAsBoolean());
            }

            if (reader.has("modifiable-x-value")) {
                player.setModifiableXValue(reader.get("modifiable-x-value").getAsInt());
            }

            if (reader.has("bank-quantity-config")) {
                player.setBankQuantityConfig(reader.get("bank-quantity-config").getAsInt());
            }

            if (reader.has("tab-display-config")) {
                player.setTabDisplayConfig(reader.get("tab-display-config").getAsInt());
            }

            if (reader.has("fixed-bank-width")) {
                player.setFixedBankWidth(reader.get("fixed-bank-width").getAsBoolean());
            }

            if (reader.has("show-deposit-worn")) {
                player.setShowDepositWornItems(reader.get("show-deposit-worn").getAsBoolean());
            }

            /* BANKS */
            for (int i = 0; i < player.getBanks().length; i++) {
                if (reader.has("bank-" + i)) {
                    player.setBank(i, new Bank(player)).getBank(i).addItems(builder.fromJson(reader.get("bank-" + i).getAsJsonArray(), Item[].class), false);
                }
            }

            if (reader.has("yell-colors")) {
                player.setYellColors(builder.fromJson(reader.get("yell-colors").getAsJsonArray(), int[].class));
            }

            if (reader.has("yell-title")) {
                player.setYellTitle(reader.get("yell-title").getAsString());
            }

            /*
             * Legacy color loading, remove this code after new colorful item saving/loading ("colorful-item-map" below) goes live and some time has passed
             */
            if (reader.has("ween-colors")) {
                player.getColorfulItemMap().put(ColorfulItem.HWEEN_MASK, builder.fromJson(reader.get("ween-colors").getAsJsonArray(), Integer[].class));
            }
            if (reader.has("phat-colors")) {
                player.getColorfulItemMap().put(ColorfulItem.PARTYHAT, builder.fromJson(reader.get("phat-colors").getAsJsonArray(), Integer[].class));
            }
            if (reader.has("santa-colors")) {
                player.getColorfulItemMap().put(ColorfulItem.SANTA_HAT, builder.fromJson(reader.get("santa-colors").getAsJsonArray(), Integer[].class));
            }
            if (reader.has("max-colors")) {
                player.getColorfulItemMap().put(ColorfulItem.MAX_CAPE, builder.fromJson(reader.get("max-colors").getAsJsonArray(), Integer[].class));
            }
            if (reader.has("scarf-colors")) {
                player.getColorfulItemMap().put(ColorfulItem.SCARF, builder.fromJson(reader.get("scarf-colors").getAsJsonArray(), Integer[].class));
            }
            if (reader.has("angelic-colors")) {
                player.getColorfulItemMap().put(ColorfulItem.ANGELIC_CAPE, builder.fromJson(reader.get("angelic-colors").getAsJsonArray(), Integer[].class));
            }

            if (reader.has("colorful-item-map")) {
                Type typeOfHashMap = new TypeToken<Map<ColorfulItem, Integer[]>>() {
                }.getType();
                Map<ColorfulItem, Integer[]> map = builder.fromJson(reader.get("colorful-item-map").getAsJsonObject(), typeOfHashMap);
                // Instead of setting player's colorful item map to this map, put each key pair from the map into the player's map, because there may have been additional colorful items added since the last save
                map.forEach((colorfulItem, integers) -> player.getColorfulItemMap().put(colorfulItem, integers));
            }

            if (reader.has("rune-pouch")) {
                player.getRunePouch().setItems(builder.fromJson(reader.get("rune-pouch").getAsJsonArray(), Item[].class));
            }

            if (reader.has("safe-deposit")) {
                player.getSafeDeposit().setItems(builder.fromJson(reader.get("safe-deposit").getAsJsonArray(), Item[].class));
            }

            if (reader.has("mystery-box-items-list")) {

                final List[] lists = builder.fromJson(reader.get("mystery-box-items-list").getAsJsonArray(), List[].class);

                for (int i = 0; i < lists.length; i++) {

                    final List<LinkedTreeMap<String, Double>> list = lists[i];

                    if (list == null)
                        continue;

                    final List<Item> loadedItems = new ArrayList<>();

                    for (final Map<String, Double> entry : list) {
                        if (entry == null)
                            continue;
                        final int itemId = entry.get("id").intValue();
                        final int itemAmount = entry.get("amount").intValue();
                        final Item item = new Item(itemId, itemAmount);
                        loadedItems.add(item);
                    }
                    /*
                     * Ensure the mystery box's rewards haven't changed since last save
                     */
                    MysteryBoxRewardItem[] rewards = SpinMysteryBoxType.values()[i].getRewards().getItemRewards();
                    List<Item> rewardItems = new ArrayList<>(rewards.length);
                    Arrays.stream(rewards).forEach(r -> rewardItems.add(r.getReward()));
                    if (loadedItems.size() == rewardItems.size() && loadedItems.containsAll(rewardItems)) {
                        player.getMysteryBoxItemsLists()[i] = loadedItems;
                    }
                }
            }

            if (reader.has("purchased-titles")) {
                String[] titles = builder.fromJson(reader.get("purchased-titles").getAsJsonArray(), String[].class);
                Arrays.stream(titles).forEach(player.getPurchasedTitles()::add);
            }

            if (reader.has("house")) {
                player.setHouse(builder.fromJson(reader.get("house"), House.class));
            }

            if (reader.has("attributeMap")) {
                final AttributeMap attributeMap = AttributeMap.Companion.deserialize(reader.get("attributeMap").getAsJsonObject());
                player.getAttributes().putAll(attributeMap);
            }

            if (reader.has("collection-log")) {
                Type type = new TypeToken<Map<String, List<CollectionLog.CLEntry>>>() {
                }.getType();
                Map<String, List<CollectionLog.CLEntry>> entries = builder.fromJson(reader.get("collection-log").getAsJsonObject(), type);
                player.getCollectionLog().getLogEntries().putAll(entries);
            }

            if (reader.has("task-type-one")) {
                EntityExtKt.setInt(player, Attribute.TASK_TYPE_ONE, reader.get("task-type-one").getAsInt(), 0);
            }
            if (reader.has("task-type-two")) {
                EntityExtKt.setInt(player, Attribute.TASK_TYPE_TWO, reader.get("task-type-two").getAsInt(), 0);
            }
            if (reader.has("task-type-three")) {
                EntityExtKt.setInt(player, Attribute.TASK_TYPE_THREE, reader.get("task-type-three").getAsInt(), 0);
            }
            if (reader.has("task-type-four")) {
                EntityExtKt.setInt(player, Attribute.TASK_TYPE_FOUR, reader.get("task-type-four").getAsInt(), 0);
            }

            if (reader.has("task-id-one")) {
                EntityExtKt.setInt(player, Attribute.TASK_ID_ONE, reader.get("task-id-one").getAsInt(), 0);
            }
            if (reader.has("task-id-two")) {
                EntityExtKt.setInt(player, Attribute.TASK_ID_TWO, reader.get("task-id-two").getAsInt(), 0);
            }
            if (reader.has("task-id-three")) {
                EntityExtKt.setInt(player, Attribute.TASK_ID_THREE, reader.get("task-id-three").getAsInt(), 0);
            }
            if (reader.has("task-id-four")) {
                EntityExtKt.setInt(player, Attribute.TASK_ID_FOUR, reader.get("task-id-four").getAsInt(), 0);
            }

            if (reader.has("task-id-one-t")) {
                EntityExtKt.setString(player, Attribute.TASK_ID_ONE_T, reader.get("task-id-one-t").getAsString(), "");
            }
            if (reader.has("task-id-two-t")) {
                EntityExtKt.setString(player, Attribute.TASK_ID_TWO_T, reader.get("task-id-two-t").getAsString(), "");
            }
            if (reader.has("task-id-three-t")) {
                EntityExtKt.setString(player, Attribute.TASK_ID_THREE_T, reader.get("task-id-three-t").getAsString(), "");
            }
            if (reader.has("task-id-four-t")) {
                EntityExtKt.setString(player, Attribute.TASK_ID_FOUR_T, reader.get("task-id-four-t").getAsString(), "");
            }

            if (reader.has("task-amount-done-one")) {
                EntityExtKt.setInt(player, Attribute.TASK_AMOUNT_DONE_ONE, reader.get("task-amount-done-one").getAsInt(), 0);
            }
            if (reader.has("task-amount-done-two")) {
                EntityExtKt.setInt(player, Attribute.TASK_AMOUNT_DONE_TWO, reader.get("task-amount-done-two").getAsInt(), 0);
            }
            if (reader.has("task-amount-done-three")) {
                EntityExtKt.setInt(player, Attribute.TASK_AMOUNT_DONE_THREE, reader.get("task-amount-done-three").getAsInt(), 0);
            }
            if (reader.has("task-amount-done-four")) {
                EntityExtKt.setInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, reader.get("task-amount-done-four").getAsInt(), 0);
            }

            if (reader.has("task-reward-item-id-one")) {
                EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_ID_ONE, reader.get("task-reward-item-id-one").getAsInt(), 0);
            }
            if (reader.has("task-reward-item-id-two")) {
                EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_ID_TWO, reader.get("task-reward-item-id-two").getAsInt(), 0);
            }
            if (reader.has("task-reward-item-id-three")) {
                EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_ID_THREE, reader.get("task-reward-item-id-three").getAsInt(), 0);
            }
            if (reader.has("task-reward-item-id-four")) {
                EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_ID_FOUR, reader.get("task-reward-item-id-four").getAsInt(), 0);
            }

            if (reader.has("task-reward-item-amount-one")) {
                EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_ONE, reader.get("task-reward-item-amount-one").getAsInt(), 0);
            }
            if (reader.has("task-reward-item-amount-two")) {
                EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_TWO, reader.get("task-reward-item-amount-two").getAsInt(), 0);
            }
            if (reader.has("task-reward-item-amount-three")) {
                EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_THREE, reader.get("task-reward-item-amount-three").getAsInt(), 0);
            }
            if (reader.has("task-reward-item-amount-four")) {
                EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_FOUR, reader.get("task-reward-item-amount-four").getAsInt(), 0);
            }

            if (reader.has("task-claimed-one")) {
                EntityExtKt.setBoolean(player, Attribute.TASK_CLAIMED_ONE, reader.get("task-claimed-one").getAsBoolean(), false);
            }
            if (reader.has("task-claimed-two")) {
                EntityExtKt.setBoolean(player, Attribute.TASK_CLAIMED_TWO, reader.get("task-claimed-two").getAsBoolean(), false);
            }
            if (reader.has("task-claimed-three")) {
                EntityExtKt.setBoolean(player, Attribute.TASK_CLAIMED_THREE, reader.get("task-claimed-three").getAsBoolean(), false);
            }
            if (reader.has("task-claimed-four")) {
                EntityExtKt.setBoolean(player, Attribute.TASK_CLAIMED_FOUR, reader.get("task-claimed-four").getAsBoolean(), false);
            }
            if (reader.has("inventory-presets-0")) {
                player.presetInventory[0]
                        .setItems(builder.fromJson(reader.get("inventory-presets-0").getAsJsonArray(), Item[].class));
            }
            if (reader.has("inventory-presets-1")) {
                player.presetInventory[1]
                        .setItems(builder.fromJson(reader.get("inventory-presets-1").getAsJsonArray(), Item[].class));
            }
            if (reader.has("inventory-presets-2")) {
                player.presetInventory[2]
                        .setItems(builder.fromJson(reader.get("inventory-presets-2").getAsJsonArray(), Item[].class));
            }
            if (reader.has("inventory-presets-3")) {
                player.presetInventory[3]
                        .setItems(builder.fromJson(reader.get("inventory-presets-3").getAsJsonArray(), Item[].class));
            }
            if (reader.has("inventory-presets-4")) {
                player.presetInventory[4]
                        .setItems(builder.fromJson(reader.get("inventory-presets-4").getAsJsonArray(), Item[].class));
            }
            if (reader.has("inventory-presets-5")) {
                player.presetInventory[5]
                        .setItems(builder.fromJson(reader.get("inventory-presets-5").getAsJsonArray(), Item[].class));
            }
            if (reader.has("inventory-presets-6")) {
                player.presetInventory[6]
                        .setItems(builder.fromJson(reader.get("inventory-presets-6").getAsJsonArray(), Item[].class));
            }
            if (reader.has("inventory-presets-7")) {
                player.presetInventory[7]
                        .setItems(builder.fromJson(reader.get("inventory-presets-7").getAsJsonArray(), Item[].class));
            }
            if (reader.has("inventory-presets-8")) {
                player.presetInventory[8]
                        .setItems(builder.fromJson(reader.get("inventory-presets-8").getAsJsonArray(), Item[].class));
            }
            if (reader.has("inventory-presets-9")) {
                player.presetInventory[9]
                        .setItems(builder.fromJson(reader.get("inventory-presets-9").getAsJsonArray(), Item[].class));
            }

            if (reader.has("equipment-presets-0")) {
                player.presetEquipment[0]
                        .setItems(builder.fromJson(reader.get("equipment-presets-0").getAsJsonArray(), Item[].class));
            }
            if (reader.has("equipment-presets-1")) {
                player.presetEquipment[1]
                        .setItems(builder.fromJson(reader.get("equipment-presets-1").getAsJsonArray(), Item[].class));
            }
            if (reader.has("equipment-presets-2")) {
                player.presetEquipment[2]
                        .setItems(builder.fromJson(reader.get("equipment-presets-2").getAsJsonArray(), Item[].class));
            }
            if (reader.has("equipment-presets-3")) {
                player.presetEquipment[3]
                        .setItems(builder.fromJson(reader.get("equipment-presets-3").getAsJsonArray(), Item[].class));
            }
            if (reader.has("equipment-presets-4")) {
                player.presetEquipment[4]
                        .setItems(builder.fromJson(reader.get("equipment-presets-4").getAsJsonArray(), Item[].class));
            }
            if (reader.has("equipment-presets-5")) {
                player.presetEquipment[5]
                        .setItems(builder.fromJson(reader.get("equipment-presets-5").getAsJsonArray(), Item[].class));
            }
            if (reader.has("equipment-presets-6")) {
                player.presetEquipment[6]
                        .setItems(builder.fromJson(reader.get("equipment-presets-6").getAsJsonArray(), Item[].class));
            }
            if (reader.has("equipment-presets-7")) {
                player.presetEquipment[7]
                        .setItems(builder.fromJson(reader.get("equipment-presets-7").getAsJsonArray(), Item[].class));
            }
            if (reader.has("equipment-presets-8")) {
                player.presetEquipment[8]
                        .setItems(builder.fromJson(reader.get("equipment-presets-8").getAsJsonArray(), Item[].class));
            }
            if (reader.has("equipment-presets-9")) {
                player.presetEquipment[9]
                        .setItems(builder.fromJson(reader.get("equipment-presets-9").getAsJsonArray(), Item[].class));
            }

            if (reader.has("presets-amount")) {
                player.presetsAmount = reader.get("presets-amount").getAsInt();
            }

            if (reader.has("preset-names")) {
                String[] presetNames = builder.fromJson(reader.get("preset-names").getAsJsonArray(), String[].class);
                player.presetNames = presetNames;
            }

            if (reader.has("preset-books")) {
                MagicSpellbook[] presetBooks = builder.fromJson(reader.get("preset-books").getAsJsonArray(), MagicSpellbook[].class);
                player.presetMagicBooks = presetBooks;
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Failed to load account of " + player + "", e);
            return LoginResponses.LOGIN_ACCOUNT_LOCKED;
        }
        return LoginResponses.LOGIN_SUCCESSFUL;
    }

    public static Bank[] getBankContainers(String username) {
        Path path = Paths.get(GameConstants.PLAYER_DIRECTORY, username + ".json");
        File file = path.toFile();

        try (FileReader fileReader = new FileReader(file)) {
            JsonParser fileParser = new JsonParser();
            Gson builder = new GsonBuilder().create();
            JsonObject reader = (JsonObject) fileParser.parse(fileReader);

            Bank[] banks = new Bank[BankConstants.TOTAL_BANK_TABS];

            for (int i = 0; i < banks.length; i++) {
                if (reader.has("bank-" + i)) {
                    banks[i] = (Bank) new Bank(null).addItems(builder.fromJson(reader.get("bank-" + i).getAsJsonArray(), Item[].class), false);
                }
            }

            return banks;
        } catch (Exception e) {
            LOGGER.error("Failed to load bank containers of Player '" + username + "'", e);
        }

        return null;
    }

    public static SafeDeposit getDepositContainers(String username) {
        Path path = Paths.get(GameConstants.PLAYER_DIRECTORY, username + ".json");
        File file = path.toFile();

        try (FileReader fileReader = new FileReader(file)) {
            JsonParser fileParser = new JsonParser();
            Gson builder = new GsonBuilder().create();
            JsonObject reader = (JsonObject) fileParser.parse(fileReader);

            SafeDeposit safeDeposit = null;

            for (int i = 0; i < 28; i++) {
                if (reader.has("safe-deposit")) {
                    safeDeposit = (SafeDeposit) new SafeDeposit(null).addItems(builder.fromJson(reader.get("safe-deposit").getAsJsonArray(), Item[].class), false);
                }
            }

            return safeDeposit;
        } catch (Exception e) {
            LOGGER.error("Failed to load safe deposit container of Player '" + username + "'", e);
        }
        return null;
    }

    public static ArrayList<Item> getBankItems(String username) {
        final ArrayList<Item> items = new ArrayList<>();

        final Path path = Paths.get(GameConstants.PLAYER_DIRECTORY, username + ".json");
        final File file = path.toFile();

        try (final FileReader fileReader = new FileReader(file)) {
            final JsonParser fileParser = new JsonParser();
            final Gson builder = new GsonBuilder().create();
            final JsonObject reader = (JsonObject) fileParser.parse(fileReader);
            for (int i = 0; i < BankConstants.TOTAL_BANK_TABS; i++) {
                if (reader.has("bank-" + i))
                    items.addAll(Arrays.asList(builder.fromJson(reader.get("bank-" + i).getAsJsonArray(), Item[].class)));
            }
            return items;
        } catch (Exception e) {
            LOGGER.error("Failed to load bank items of Player '" + username + "'", e);
        }
        return items;
    }

    public static ArrayList<Item> getInventoryItems(String username) {
        final ArrayList<Item> items = new ArrayList<>();
        final Path path = Paths.get(GameConstants.PLAYER_DIRECTORY, username + ".json");
        final File file = path.toFile();

        try (final FileReader fileReader = new FileReader(file)) {
            final JsonParser fileParser = new JsonParser();
            final Gson builder = new GsonBuilder().create();
            final JsonObject reader = (JsonObject) fileParser.parse(fileReader);

            if (reader.has("inventory"))
                items.addAll(Arrays.asList(builder.fromJson(reader.get("inventory").getAsJsonArray(), Item[].class)));

            return items;
        } catch (Exception e) {
            LOGGER.error("Failed to load inventory items of Player '" + username + "'", e);
        }
        return items;
    }

    public static Optional<String> getString(final String username, final String key) {
        final Path path = Paths.get(GameConstants.PLAYER_DIRECTORY, username + ".json");
        final File file = path.toFile();

        try (final FileReader fileReader = new FileReader(file)) {
            final JsonParser fileParser = new JsonParser();
            final JsonObject reader = (JsonObject) fileParser.parse(fileReader);

            if (reader.has(key))
                return Optional.of(reader.get(key).getAsString());

        } catch (Exception e) {
            LOGGER.error("Failed to load string '" + key + "' of Player '" + username + "'", e);
        }

        return Optional.empty();
    }

    public static Optional<String[]> getStringArray(final String username, final String key) {
        final Path path = Paths.get(GameConstants.PLAYER_DIRECTORY, username + ".json");
        final File file = path.toFile();

        try (final FileReader fileReader = new FileReader(file)) {
            final JsonParser fileParser = new JsonParser();
            final Gson builder = new GsonBuilder().create();
            final JsonObject reader = (JsonObject) fileParser.parse(fileReader);

            if (reader.has(key))
                return Optional.of(builder.fromJson(reader.get(key).getAsJsonArray(), String[].class));

        } catch (Exception e) {
            LOGGER.error("Failed to load string array '" + key + "' of Player '" + username + "'", e);
        }
        return Optional.empty();
    }
}