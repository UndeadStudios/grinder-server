package com.grinder.game.entity.agent.npc;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.clan.GlobalClanChatManager;
import com.grinder.game.content.item.charging.impl.EtherBracelet;
import com.grinder.game.content.minigame.warriorsguild.WarriorsGuild;
import com.grinder.game.content.minigame.warriorsguild.drops.Defender;
import com.grinder.game.content.miscellaneous.PetHandler;
import com.grinder.game.content.pvm.MonsterKillTracker;
import com.grinder.game.content.pvm.MonsterKilling;
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerMaster;
import com.grinder.game.content.skill.skillable.impl.slayer.superior.SuperiorSlayerManager;
import com.grinder.game.content.task_new.DailyTask;
import com.grinder.game.content.task_new.PlayerTaskManager;
import com.grinder.game.content.task_new.WeeklyTask;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.definition.ItemValueType;
import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.definition.NpcDropDefinition;
import com.grinder.game.definition.NpcDropDefinition.DropTable;
import com.grinder.game.definition.NpcDropDefinition.NPCDrop;
import com.grinder.game.definition.NpcDropDefinition.RDT;
import com.grinder.game.entity.agent.combat.event.impl.DropItemLootEvent;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.model.Position;
import com.grinder.game.content.cluescroll.scroll.ScrollConstants;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.bank.Banking;
import com.grinder.net.codec.database.SQLManager;
import com.grinder.net.codec.database.impl.DatabaseRareDropLogs;
import com.grinder.util.*;
import com.grinder.util.random.RandomGen;

import java.util.*;

public class NPCDropGenerator {

    /**
     * The {@link Player} whose generating a drop.
     */
    private final Player player;
    /**
     * The {@link NpcDropDefinition} this drop is for.
     */
    private final NpcDropDefinition def;

    /**
     * Constructor
     *
     * @param player
     * @param def
     */
    public NPCDropGenerator(Player player, NpcDropDefinition def) {
        this.player = player;
        this.def = def;
    }

	public static int[] easyClueIds = new int[] { 2677, 3519, 2717, ItemID.REWARD_CASKET_EASY_};
    public static int[] mediumClueIds = new int[] { 2801, 3593, 2806, ItemID.REWARD_CASKET_MEDIUM_ };
    public static int[] hardClueIds = new int[] { 2722, 3531, 2728, ItemID.REWARD_CASKET_HARD_ };
    public static int[] eliteClueIds = new int[] { ScrollConstants.ITEM_ELITE_SCROLL, ScrollConstants.ITEM_ELITE_SCROLL_BOX, ScrollConstants.ITEM_ELITE_SCROLL_REWARD_CASKET, ScrollConstants.ITEM_ELITE_SCROLL_COMPLETION_CASKET };

	/**
	 * Cyclops roll generator for defenders
	 * @param player
	 * @param npc
	 */
	public static void roll(Player player, NPC npc) {
		var nextDefender = WarriorsGuild.getNextDefender(player.getAsPlayer());
		if (npc.fetchDefinition().getCombatLevel() == 106) {
			for (var item : com.grinder.game.content.minigame.warriorsguild.drops.Misc.values()) {
				var dropRate = item.getRoll();
				var rolled = Misc.randomInclusive(1, dropRate);
				if (rolled == dropRate) {
					if (Misc.random(2) == 1) {
						break;
					}
					ItemOnGroundManager.registerNonGlobal(player, new Item(item.getId()), npc.getPosition());
					new NPCDropGenerator(player, NpcDropDefinition.get(npc.getId()).get()).HandleDropMessages(new NpcDropDefinition.NPCDrop(item.getId(), 1, 1, dropRate), npc.fetchDefinition());
					player.sendMessage("<col=FF0000>You have received a " + ItemDefinition.forId(item.getId()).getName() + " as a bonus drop from the Cyclops.");
					player.getCollectionLog().createOrUpdateEntry(player,  "Warriors Guild", new Item(item.getId()));
					break;
				}
			}
		} else {
			if (nextDefender == Defender.AVERNIC_HILT || nextDefender == Defender.DRAGON) {
				nextDefender = Defender.RUNE;
			}
		}
		var dropRate = nextDefender.getRoll();
		var rolled = Misc.randomInclusive(1, dropRate);
		if (rolled == dropRate) {
			if (Misc.random(2) == 1) {
				if (nextDefender == Defender.AVERNIC_HILT || nextDefender == Defender.RUNE || nextDefender == Defender.DRAGON) {
					return;
				}
			}
			ItemOnGroundManager.registerNonGlobal(player, new Item(nextDefender.getId()), npc.getPosition());
			if (nextDefender == Defender.AVERNIC_HILT || nextDefender == Defender.RUNE || nextDefender == Defender.DRAGON) {
				new NPCDropGenerator(player, NpcDropDefinition.get(npc.getId()).get()).HandleDropMessages(new NpcDropDefinition.NPCDrop(nextDefender.getId(), 1, 1, dropRate), npc.fetchDefinition());
				player.getCollectionLog().createOrUpdateEntry(player,  "Warriors Guild", new Item(nextDefender.getId()));
				return;
			}
			player.sendMessage("<col=FF0000>You have received a drop: " + nextDefender.getName() + ".");
			player.getCollectionLog().createOrUpdateEntry(player,  "Warriors Guild", new Item(nextDefender.getId()));
		}
	}

    /**
     * Attempts to start a new generator using the given entities.
     *
     * @param killerPlayer
     * @param npc
     */
    public static void start(Player killerPlayer, NPC npc) {
			final NpcDefinition definition = npc.fetchDefinition();
			final Player player = GlobalClanChatManager.getLootRecipientOnKill(killerPlayer, npc);

			/*
			 * Superior Slayer
			 */
			SuperiorSlayerManager.handleSuperiorSlayerMonsterDrop(player, npc);

			Position pos = npc.getPosition();

			if (definition.getId() == 1101)
				pos = new Position(2463, 4781, pos.getZ());
			else if (definition.getId() == 4315) {
				if (pos.getY() < 3000) // Members boss zone
					pos = new Position(2287, 2572, 0);
					else
					pos = new Position(2505, 3898, pos.getZ());
			} else if (definition.getId() == NpcID.VORKATH_8061 || definition.getId() == NpcID.VORKATH_8059)
				pos = killerPlayer.getPosition().clone();
			else if (definition.getId() == 2042 || definition.getId() == 2043 || definition.getId() == 2044 || definition.getId() == 2045)
				pos = player.getPosition();

			final Optional<NpcDropDefinition> optionalNpcDropDefinition = NpcDropDefinition.get(npc.getId());

			if (optionalNpcDropDefinition.isPresent()) {

				final NpcDropDefinition dropDefinition = optionalNpcDropDefinition.get();
				final NPCDropGenerator dropGenerator = new NPCDropGenerator(player, dropDefinition);

				// Larran's key drop with our custom formula and its perfect
				if (AreaManager.inWilderness(npc)) {
					if (Misc.random(1_500) < npc.fetchDefinition().getCombatLevel() / 10) {
						PlayerUtil.broadcastMessage("<img=783> " + PlayerUtil.getImages(player) + "" + player.getUsername() +" received a @dre@" + ItemDefinition.forId(23490).getName() + "</col> drop from slaying monsters in the Wilderness.</col>");
						ItemOnGroundManager.register(player, new Item(23490, 1), pos);
					}
				}

				// Cyclops
				if (definition.getId() == NpcID.CYCLOPS_2137 || definition.getId() == NpcID.CYCLOPS_2138 || definition.getId() == NpcID.CYCLOPS_2139 || definition.getId() == NpcID.CYCLOPS_2140 || definition.getId() == NpcID.CYCLOPS_2141
						|| definition.getId() == NpcID.CYCLOPS_2142 || definition.getId() == NpcID.CYCLOPS_2463 || definition.getId() == NpcID.CYCLOPS_2464 || definition.getId() == NpcID.CYCLOPS_2465 || definition.getId() == NpcID.CYCLOPS_2466
						|| definition.getId() == NpcID.CYCLOPS_2467 || definition.getId() == NpcID.CYCLOPS_2468) {
					roll(player, npc);
				}

				for (final Item droppedItem : dropGenerator.getDropList()) {

					final ItemDefinition itemDefinition = droppedItem.getDefinition();
					final DropItemLootEvent dropItemLootEvent = new DropItemLootEvent(player, npc, droppedItem);

					player.getCombat().submit(dropItemLootEvent);

					if (handleLoot(player, droppedItem) || !dropItemLootEvent.getDropItemOnGround())
						continue;

					if (!itemDefinition.isStackable()) {
						for (int i = 0; i < droppedItem.getAmount(); i++) {

							Optional<PetHandler.Pet> pet = PetHandler.Pet.getPetForItem(droppedItem.getId());
							if (pet.isPresent()) {
								if (PetHandler.alreadyExists(player, droppedItem.getId())) {
									continue;
								}
							}
							if (itemDefinition.getName().contains(" scroll (easy)")) {
								if (player.getInventory().containsAny(easyClueIds) || player.getBank(Banking.getTabContainingItemOrDefault(player, droppedItem)).containsAnyNonePlaceHolders(easyClueIds)) {
									continue;
								}
							} else if (itemDefinition.getName().contains(" scroll (medium)")) {
								if (player.getInventory().containsAny(mediumClueIds) || player.getBank(Banking.getTabContainingItemOrDefault(player, droppedItem)).containsAnyNonePlaceHolders(mediumClueIds)) {
									continue;
								}
							} else if (itemDefinition.getName().contains(" scroll (hard)")) {
								if (player.getInventory().containsAny(hardClueIds) || player.getBank(Banking.getTabContainingItemOrDefault(player, droppedItem)).containsAnyNonePlaceHolders(hardClueIds)) {
									continue;
								}
							} else if (itemDefinition.getName().contains(" scroll (elite)")) {
								if (player.getInventory().containsAny(eliteClueIds) || player.getBank(Banking.getTabContainingItemOrDefault(player, droppedItem)).containsAnyNonePlaceHolders(eliteClueIds)) {
									continue;
								}
							}

							// New drops message handling
							if (MonsterKilling.INSTANCE.isBoss(npc) && !droppedItem.getDefinition().isTradeable()) {
								player.sendMessage("@red@Untradeable drop: " + droppedItem.getDefinition().getName() +"");
							}

							if (MonsterKilling.INSTANCE.isBoss(npc) && droppedItem.getValue(ItemValueType.PRICE_CHECKER) > 0) {
								player.sendMessage("@red@Valuable drop: " + droppedItem.getDefinition().getName() +" (" + Misc.formatWithAbbreviationCustomPrefix(droppedItem.getValue(ItemValueType.PRICE_CHECKER), null) +"@red@ coins)");
							}

							final Item item = new Item(droppedItem.getId(), 1);

							GlobalClanChatManager.sendLootShareMessage(player, npc, item);
							ItemOnGroundManager.register(player, item, pos);
						}
					} else {

						if (droppedItem.getId() == 995 && droppedItem.getAmount() < 30_000) {
							droppedItem.setAmount(droppedItem.getAmount() * 20);
						}
						if (droppedItem.getId() == 995) {
							AchievementManager.processFor(AchievementType.GOLDEN_MOUNTAIN, droppedItem.getAmount(), player);
							AchievementManager.processFor(AchievementType.MONEY_FOUNTAIN, droppedItem.getAmount(), player);
							AchievementManager.processFor(AchievementType.MONEY_CHEST, droppedItem.getAmount(), player);
							AchievementManager.processFor(AchievementType.MONEY_POUCH, droppedItem.getAmount(), player);
						}



						// New drops message handling

						if (MonsterKilling.INSTANCE.isBoss(npc) && !droppedItem.getDefinition().isTradeable()) {
							player.sendMessage("@red@Untradeable drop: " + Misc.format(droppedItem.getAmount()) +" x " + droppedItem.getDefinition().getName() +"");
						}

						if (MonsterKilling.INSTANCE.isBoss(npc) && droppedItem.getValue(ItemValueType.PRICE_CHECKER) > 0) {
							player.sendMessage("@red@Valuable drop: " + Misc.format(droppedItem.getAmount()) + " x " + droppedItem.getDefinition().getName() +" (" + Misc.formatWithAbbreviationCustomPrefix((droppedItem.getValue(ItemValueType.PRICE_CHECKER) * droppedItem.getAmount()), null) +"@red@ coins)");
						}

						GlobalClanChatManager.sendLootShareMessage(player, npc, droppedItem);

							if (player.isRingofWealthActivated()
								&& (droppedItem.getId() == 995 || droppedItem.getId() == 13307 || droppedItem.getId() == 6529)
								&& EquipmentUtil.isWearingAnyROW(player.getEquipment())) {

							if (droppedItem.getId() == 995) { // Coins
								player.getInventory().add(droppedItem);
							}

							if (droppedItem.getId() == 13307) { // Blood money
								player.getInventory().add(droppedItem);
							}

							if (droppedItem.getId() == 6529) { // Tokkul
								player.getInventory().add(droppedItem);
							}
						} else {
							ItemOnGroundManager.register(player, droppedItem, pos);
						}
					}

					if(itemDefinition.isCollectable()) {
						if (npc.fetchDefinition().getName().toLowerCase().contains(" reborn")) {
							player.getCollectionLog().createOrUpdateEntry(player,  "Vet'ion", droppedItem);
						}
						player.getCollectionLog().createOrUpdateEntry(player,  definition.getName(), droppedItem);
					}
				}
			}
		}

	/**
	 * Handles the loot.
	 *
	 * @param item
	 *            the item
	 */
	public static boolean handleLoot(Player player, Item item) {

		if(EtherBracelet.INSTANCE.isActive(player) && item.getId() == ItemID.REVENANT_ETHER)
			return EtherBracelet.INSTANCE.handleEtherDrop(player, item);

		return false;
	}

	/**
	 * Returns if the item name or ID is excluded from the drop
	 * announcements messages
	 * Contains list of item names and item IDs that are excluded
	 * @param drop
	 * @return
	 */
	public boolean excludeItemDropesage(NPCDrop drop) {
		if (drop.getItemId() != 987
				&& drop.getItemId() != 2912
				&& drop.getItemId() != 989 && !ItemDefinition.forId(drop.getItemId()).getName().contains("totem")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("clue scroll")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("seed")
				// too add later
				// ancient staff, muddy key, barrows gloves, shield left, iron sq, crystal bow, new crystal, black mask, ancient mace, dragon boot
				// slayer staff, gloves of silence, rock-shell
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("shard")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("uncut")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains(" pouch")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("ensouled")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("mysterious")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains(" rune")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains(" sand")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("banana")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("talisman")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("kebab")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("ecumenical")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("coconut")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("dragon dagger")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("dragon halberd")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("leaf-bladed-sword")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("rune ")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("cannonball ")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("dragon bolt ")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("bolts ")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains(" head")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("red gloves")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains(" seed")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("splitbark ")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("dragon mace")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("eggs")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("dragon scimitar")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("wine of")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("dragon glove")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("bucket of sand")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("bucket of sand")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("dragon plateskirt")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("purple gloves")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("yellow gloves")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("teal gloves")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains(" ore")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("mystic")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("long bone")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("teacher wand")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("curved bone")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("helm of neit")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("bandos boot")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("mystic mud")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("battlestaff")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("leaf-")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("dragon med")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("holy elixir")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains(" head")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("crawling")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("bronze boot")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("giant key")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("coins")
				&& !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains("(e)")) {
			return true;
		}

		return false;
	}

	/**
	 * Handles drop announcements messages on NPC death for NPCDropGenerator
	 * @param drop
	 * @param npc
	 */
	public void HandleDropMessages(NPCDrop drop, NpcDefinition npc) {

		Optional<PetHandler.Pet> pet = PetHandler.Pet.getPetForItem(drop.getItemId());
		if (pet.isPresent()) {
			if (PetHandler.alreadyExists(player, drop.getItemId())) {
				return;
			}
		}
		if (ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains(" scroll (easy)")) {
			if (player.getInventory().containsAny(easyClueIds) || player.getBank(Banking.getTabContainingItemOrDefault(player, drop.getItemId())).containsAnyNonePlaceHolders(easyClueIds)) {
				return;
			}
		} else if (ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains(" scroll (medium)")) {
			if (player.getInventory().containsAny(mediumClueIds) || player.getBank(Banking.getTabContainingItemOrDefault(player, drop.getItemId())).containsAnyNonePlaceHolders(mediumClueIds)) {
				return;
			}
		} else if (ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains(" scroll (hard)")) {
			if (player.getInventory().containsAny(hardClueIds) || player.getBank(Banking.getTabContainingItemOrDefault(player, drop.getItemId())).containsAnyNonePlaceHolders(hardClueIds)) {
				return;
			}
		} else if (ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().contains(" scroll (elite)")) {
			if (player.getInventory().containsAny(eliteClueIds) || player.getBank(Banking.getTabContainingItemOrDefault(player, drop.getItemId())).containsAnyNonePlaceHolders(eliteClueIds)) {
				return;
			}
		}
		//}

		// Increase points
		player.getPoints().increase(AttributeManager.Points.RARE_DROPS_RECEIVED, 1); // Increase points
		PlayerTaskManager.progressTask(player, DailyTask.RARE_DROPS);
		PlayerTaskManager.progressTask(player, WeeklyTask.RARE_DROPS);

		String name = npc.getName();
		final MonsterKillTracker.KillTrack tracked = MonsterKillTracker.forName(player, name);

		if (tracked != null) {
			if (tracked.getDropKC() == 0) {
				tracked.setDropKC(1);
			}
			if (drop.getMaxAmount() > 1 && !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().endsWith("s")) {
				PlayerUtil.broadcastMessage("<img=783> " + PlayerUtil.getImages(player) + "" + player.getUsername() + " has just received @dre@" + ItemDefinition.forId(drop.getItemId()).getName() + "s</col> as a rare drop from @dre@" + npc.getName() + "</col>! (Killcount: " + Misc.format(tracked.getDropKC()) + ") (Total Killcount: " + Misc.format(tracked.getAmount()) + ")");
				if (DiscordBot.ENABLED)
					DiscordBot.INSTANCE.sendDropsMessages("[DROPS]: " + player.getUsername() + " has just received " + ItemDefinition.forId(drop.getItemId()).getName() + "s as a rare drop from " + npc.getName() + "! Killcount: (" + Misc.format(tracked.getDropKC()) + ") Total Kill Count: (" + Misc.format(tracked.getAmount()) + ")");
				tracked.setDropKC(0);
			} else {
				PlayerUtil.broadcastMessage("<img=783> " + PlayerUtil.getImages(player) + "" + player.getUsername() + " has just received @dre@" + ItemDefinition.forId(drop.getItemId()).getName() + "</col> as a rare drop from @dre@" + npc.getName() + "</col>! (Killcount: " + Misc.format(tracked.getDropKC()) + ") (Total Killcount: " + Misc.format(tracked.getAmount()) + ")");
				if (DiscordBot.ENABLED)
					DiscordBot.INSTANCE.sendDropsMessages("[DROPS]: " + player.getUsername() + " has just received " + ItemDefinition.forId(drop.getItemId()).getName() + " as a rare drop from " + npc.getName() + "! Killcount: (" + Misc.format(tracked.getDropKC()) + ") Total Kill Count: (" + Misc.format(tracked.getAmount()) + ")");
				tracked.setDropKC(0);
			}
		}


		if (!MonsterKilling.INSTANCE.isBossesId(npc)) {
			if (drop.toItem().getValue(ItemValueType.PRICE_CHECKER) > 0) {
				if (drop.getMaxAmount() > 1 && !ItemDefinition.forId(drop.getItemId()).getName().toLowerCase().endsWith("s")) {
					player.sendMessage("@red@You have received a rare drop.");
					player.sendMessage("@red@Rare drop: " + ItemDefinition.forId(drop.getItemId()).getName() + "s (" + Misc.formatWithAbbreviationCustomPrefix(drop.toItem().getValue(ItemValueType.PRICE_CHECKER), null) + "@red@ coins)");
				} else {
					player.sendMessage("@red@You have received a rare drop.");
					player.sendMessage("@red@Rare drop: " + ItemDefinition.forId(drop.getItemId()).getName() + " (" + Misc.formatWithAbbreviationCustomPrefix(drop.toItem().getValue(ItemValueType.PRICE_CHECKER), null) + "@red@ coins)");
				}
			}
		}


		player.getPacketSender().sendJinglebitMusic(139, 0);
		//System.out.println("[NO RING] " + player.getUsername()
		//		+ ": just received a rare drop item id " + ItemDefinition.forId(drop.getItemId()).getName() + "!");
		if (drop.getChance() >= 32) {
			Logging.log("rareDrops", "" + player.getUsername() + ": got a rare drop of: " + ItemDefinition.forId(drop.getItemId()).getName() + " from: " + npc.getName() +"");

			// Logging
			new DatabaseRareDropLogs(
					SQLManager.Companion.getINSTANCE(),
					player.getUsername(),
					ItemDefinition.forId(drop.getItemId()).getName(),
					npc.getName(),
					drop.getChance()
			).schedule(player);
		}
	}

	/**
	 * Returns the droprate counter depending on npc level as a formula
	 * @return
	 */

	public int getFixedDropRate() {

		if (NpcDefinition.forId(def.getNpcIds()[0]).getName().toLowerCase().contains("avatar")

		) {
			return 0;

		}
		if (NpcDefinition.forId(def.getNpcIds()[0]).getCombatLevel() > 660) {
			return 1; // 2

		} else if (NpcDefinition.forId(def.getNpcIds()[0]).getCombatLevel() > 275) {
			return 1; // 3

		} else {
			return 1;
		}
	}

	/**
	 * Handles the Slayer's enchantment item drop
	 * param drop
	 * @param npc
	 */
	public boolean canDropSlayerEnchantment(NpcDefinition npc) {
		int dropChance = (1 / (320 - (npc.getHitpoints() * 8/10))) * 250;
		if (npc.getCombatLevel() > 300 || npc.getHitpoints() >= 320) { // Bosses
			dropChance = 5;
		}
		dropChance += 1;
		return Misc.randomChance(dropChance);
	}
    /**
     * Generates a list of items from the drop definition that will be dropped for a
     * player.
     *
     * @return
     */
    public List<Item> getDropList() {

		NpcDefinition npc = NpcDefinition.forId(def.getNpcIds()[0]);

        // The {@RandomGen} which will help us randomize drops..
        RandomGen random = new RandomGen();

        // The list containing the {@link Item} that will be dropped for the player.
        List<Item> items = new LinkedList<>();

        // The list containing the drop tables which we've gone through.
        List<DropTable> parsedTables = new ArrayList<DropTable>();

        // Drop "always" items..
        if (def.getAlwaysDrops() != null) {
            for (NPCDrop drop : def.getAlwaysDrops()) {
                items.add(drop.toItem(random));
            }
        }

        // Handle RDT.. If a drop is generated from RDT, no further items should be
        // given.
        // There are 128 slots in the rdt, many empty. When a player is wearing ring of
        // wealth, the empty slots aren't counted.
        if (def.getRdtChance() > 0 && random.get().nextInt(def.getRdtChance()) <= 32) {
            int rdtLength = NpcDropDefinition.RDT.values().length;
            int slots = wearingRingOfWealth() ? rdtLength : 128;
            int slot = random.get().nextInt(slots);
            if (slot < rdtLength) {
                RDT rdtDrop = RDT.values()[slot];

                if (random.get().nextInt(rdtDrop.getChance()) <= rdtDrop.getChance() / 8) {

					// Brimstone key RDT drop handling
					if (rdtDrop.getItemId() == 23083) {
						if (player.getSlayer().getTask() != null && player.getSlayer().getTask().getMaster() != null && player.getSlayer().getTask().getMaster().equals(SlayerMaster.KONAR_QUO_MATEN)
								&& npc.getName().toLowerCase().contains(player.getSlayer().getTask().getName().toLowerCase())) {
							items.add(new Item(rdtDrop.getItemId(), rdtDrop.getAmount()));
							AchievementManager.processFor(AchievementType.LUCKY_TRIP, player);
							player.getPacketSender().sendMessage("<img=783> You have just received an RDT drop!");
							player.getPoints().increase(AttributeManager.Points.RDT_DROPS_RECEIVED, 1); // Increase points
						}
						return items;
					}


                    items.add(new Item(rdtDrop.getItemId(), rdtDrop.getAmount()));
					AchievementManager.processFor(AchievementType.LUCKY_TRIP, player);
                    player.getPacketSender().sendMessage("<img=783> You have just received an RDT drop!");
					player.getPoints().increase(AttributeManager.Points.RDT_DROPS_RECEIVED, 1); // Increase points
                    PlayerTaskManager.progressTask(player, DailyTask.RDT_DROPS);
					PlayerTaskManager.progressTask(player, WeeklyTask.RDT_DROPS);
					return items;
                }
			}
		}

		// Handle unique drops..
		// The amount of items the player will receive from the unique drop
		// tables.
		// Note: A player can't receive multiple drops from the same drop
		// table.
		double rolls = 1 + random.get().nextDouble(2.0, this.wearingRingOfWealthI() ? 4.48 : this.wearingRingOfWealth() ? 3.32 : 3.0);
        //if (player.getGameMode().isClassic()) {
        //	rolls+=0.52;
		//}
		if (PlayerUtil.isDiamondMember(player)) {
			rolls += 1;
		}
		if (player.getGameMode().isOneLife()) {
			rolls += 1;
		}
		if (player.getGameMode().isAnyIronman()) {
			rolls += 1;
		}
		if (player.getGameMode().isUltimate()) {
			rolls += 1;
		}
		if (player.getGameMode().isPure() || player.getGameMode().isMaster()) {
			rolls /= 2;
		}

		for (int i = 0; i < rolls; i++) {
			Optional<DropTable> table = Optional.empty();

			// Check if we should access the special drop table..
			if (def.getSpecialDrops() != null && !parsedTables.contains(DropTable.SPECIAL)) {
				if (def.getSpecialDrops().length > 0) {
					NPCDrop drop = def.getSpecialDrops()[random.get().nextInt(def.getSpecialDrops().length)];

					if(drop == null)
						break;
					int dropRollChance = getFixedDropRate();

					if (player.getGameMode().isPure() || player.getGameMode().isMaster()) {
						dropRollChance = 1;
					}
					if (Misc.random(6) == 1) { // DROP CHANGES.  was removed
						if (player.getGameMode().isRealism()) {
							dropRollChance += 1;
						}
					}
					if (player.getGameMode().isOneLife()) {
							dropRollChance += 1;
						}
					if (Misc.random(3) == 1) { // DROP CHANGES.  was removed
						if (player.getGameMode().isAnyIronman()) {
							dropRollChance += 1;
						}
					}
					if (random.get().nextInt(drop.getChance()) <= dropRollChance) {

						if (Misc.random(5) == 1) { // DROP CHANGES. best value 10
						if ((drop.getChance() > 70 || drop.toItem().getValue(ItemValueType.PRICE_CHECKER) > 20_000_000)/* && player.getWildernessLevel() <= 0*/) { // DROP CHANGES. was 70
							break;
						}
						}

						if (Misc.random(9) == 1) { // DROP CHANGES. was 9
							if (!player.getGameMode().isClassic() && !player.getGameMode().isRealism() && !player.getGameMode().isOneLife()
									&& !player.getGameMode().isAnyIronman()) { // 10% chance~ to skip a rare drop. This makes our custom formulas work great after testing.
								break;
							}
						}

						if (Misc.random(2) == 1) { // DROP CHANGES. was 4
							if ((drop.getItemId() == ItemID.DRACONIC_VISAGE && player.getWildernessLevel() <= 0)) {
								break;
							}
						}

						if (npc.getId() == 11278 || npc.getId() == 11279 || npc.getId() == 11280 || npc.getId() == 11281 || npc.getId() == 11282) {
							if (drop.toItem().getValue(ItemValueType.PRICE_CHECKER) > 550_000_000 && Misc.random(2) == 1) {
								break;
							}
						}


						if (Misc.random(3) == 1) { // DROP CHANGES was 4
							if (drop.getItemId() == ItemID.TWISTED_BOW || drop.getItemId() == ItemID.TORVA_FULL_HELM_DAMAGED || drop.getItemId() == ItemID.TORVA_PLATEBODY_DAMAGED || drop.getItemId() == ItemID.TORVA_PLATELEGS_DAMAGED
									|| drop.getItemId() == ItemID.ZARYTE_CROSSBOW || drop.getItemId() == ItemID.ZARYTE_VAMBRACES || drop.getItemId() == ItemID.ZARYTE_BOW || drop.getItemId() == ItemID.BOW_OF_FAERDHINEN_INACTIVE
									|| drop.getItemId() == ItemID.AMULET_OF_BLOOD_FURY || drop.getItemId() == ItemID.RING_OF_ENDURANCE_UNCHARGED || drop.getItemId() == ItemID.RING_OF_THIRD_AGE
									|| drop.getItemId() == 15892 || drop.getItemId() == 15893 || drop.getItemId() == 15894 || drop.getItemId() == 15895 || drop.getItemId() == 15896 || drop.getItemId() == 15897
									|| drop.getItemId() == ItemID.ANCIENT_HILT || drop.getItemId() == ItemID.NIHIL_DUST || drop.getItemId() == ItemID.NIHIL_HORN || drop.getItemId() == ItemID.NIHIL_SHARD || drop.getItemId() == ItemID.DRAGONS_TAIL
									|| drop.getItemId() == ItemID.INFERNAL_CAPE) {
								break;
							}
						}

						if (Misc.random(3) == 1) { // DROP CHANGES. was 5
							if ((drop.getItemId() == ItemID.TWISTED_BOW || drop.getItemId() == ItemID.GHRAZI_RAPIER || drop.getItemId() == ItemID.DRAGON_CLAWS ||
									drop.getItemId() == ItemID.ABYSSAL_WHIP || drop.getItemId() == ItemID.DARK_BOW || drop.getItemId() == ItemID.BANDOS_CHESTPLATE || drop.getItemId() == ItemID.BANDOS_TASSETS
									|| drop.getItemId() == 15751
									|| drop.getItemId() == ItemID.ARMADYL_HILT || drop.getItemId() == ItemID.GRANITE_MAUL || drop.getItemId() == ItemID.CORRUPTED_HELM || drop.getItemId() == ItemID.CORRUPTED_KITESHIELD || drop.getItemId() == ItemID.CORRUPTED_PLATEBODY
									|| drop.getItemId() == ItemID.CORRUPTED_PLATELEGS || drop.getItemId() == ItemID.CORRUPTED_PLATESKIRT || drop.getItemId() == ItemID.INFERNAL_CAPE
									|| drop.getItemId() == ItemID.AMULET_OF_FURY || drop.getItemId() == ItemID.DRAGON_BOOTS || drop.getItemId() == ItemID.MUDDY_KEY || drop.getItemId() == ItemID.CRYSTAL_KEY
									|| drop.getItemId() == ItemID.LOOP_HALF_OF_KEY || drop.getItemId() == ItemID.TOOTH_HALF_OF_KEY || drop.getItemId() == ItemID.DRAGON_PLATEBODY || drop.getItemId() == ItemID.DRAGON_FULL_HELM
									|| drop.getItemId() == ItemID.BARROWS_GLOVES || drop.getItemId() == ItemID.ARMADYL_HELMET || drop.getItemId() == ItemID.ARMADYL_PLATEBODY || drop.getItemId() == ItemID.ARMADYL_PLATESKIRT
									|| drop.getItemId() == ItemID.KRAKEN_TENTACLE || drop.getItemId() == ItemID.SERPENTINE_VISAGE || drop.getItemId() == ItemID.MAGIC_FANG || drop.getItemId() == ItemID.TANZANITE_FANG
									|| drop.getItemId() == ItemID.ANCESTRAL_HAT || drop.getItemId() == ItemID.ANCESTRAL_ROBE_BOTTOM || drop.getItemId() == ItemID.ANCESTRAL_ROBE_TOP || drop.getItemId() == ItemID.INQUISITORS_ARMOUR_SET
									|| drop.getItemId() == ItemID.INQUISITORS_GREAT_HELM || drop.getItemId() == ItemID.INQUISITORS_HAUBERK || drop.getItemId() == ItemID.INQUISITORS_MACE || drop.getItemId() == ItemID.INQUISITORS_PLATESKIRT
/*								|| drop.getItemId() == ItemID.SPECTRAL_SPIRIT_SHIELD || drop.getItemId() == ItemID.SPECTRAL_SIGIL || drop.getItemId() == ItemID.ARCANE_SPIRIT_SHIELD || drop.getItemId() == ItemID.ARCANE_SIGIL
								|| drop.getItemId() == ItemID.ELYSIAN_SPIRIT_SHIELD || drop.getItemId() == ItemID.ELYSIAN_SIGIL || drop.getItemId() == ItemID.AMULET_OF_ETERNAL_GLORY*/ || drop.getItemId() == ItemID.DRAGON_HUNTER_CROSSBOW
									|| drop.getItemId() == ItemID.ARMADYL_CROSSBOW || drop.getItemId() == ItemID.DRAGON_WHIP || drop.getItemId() == ItemID.DRAGON_GODSWORD || drop.getItemId() == 22326 || drop.getItemId() == 22327
									|| drop.getItemId() == 22328 || drop.getItemId() == 22486 || drop.getItemId() == ItemID.PRIMORDIAL_CRYSTAL || drop.getItemId() == ItemID.PEGASIAN_CRYSTAL || drop.getItemId() == ItemID.ETERNAL_CRYSTAL
									|| drop.getItemId() == 21637 || drop.getItemId() == 15802 || drop.getItemId() == 22006 || drop.getItemId() == ItemID.KODAI_INSIGNIA || drop.getItemId() == ItemID.STAFF_OF_LIGHT || drop.getItemId() == 15824
									|| drop.getItemId() == ItemID.TORVA_FULL_HELM_DAMAGED || drop.getItemId() == ItemID.TORVA_PLATEBODY_DAMAGED || drop.getItemId() == ItemID.TORVA_PLATELEGS_DAMAGED
									|| drop.getItemId() == 15892 || drop.getItemId() == 15893 || drop.getItemId() == 15894 || drop.getItemId() == 15895 || drop.getItemId() == 15896 || drop.getItemId() == 15897
									|| drop.getItemId() == ItemID.ZARYTE_CROSSBOW || drop.getItemId() == ItemID.ZARYTE_VAMBRACES || drop.getItemId() == ItemID.ZARYTE_BOW || drop.getItemId() == ItemID.BOW_OF_FAERDHINEN_INACTIVE
									|| drop.getItemId() == ItemID.AMULET_OF_BLOOD_FURY || drop.getItemId() == ItemID.RING_OF_ENDURANCE_UNCHARGED || drop.getItemId() == ItemID.RING_OF_THIRD_AGE
									|| drop.getItemId() == ItemID.ANCIENT_HILT || drop.getItemId() == ItemID.NIHIL_DUST || drop.getItemId() == ItemID.NIHIL_HORN || drop.getItemId() == ItemID.NIHIL_SHARD || drop.getItemId() == ItemID.DRAGONS_TAIL
							) && player.getWildernessLevel() <= 0) { // 25% even less chance for those items specifically as per testing results
								break;
							}
						}

						Optional<PetHandler.Pet> pet = PetHandler.Pet.getPetForItem(drop.getItemId());
						 if (Misc.random(5) == 1) {
							 if ((pet != null && pet.isPresent())) {
								 break;
							 }
						 }

						// Handle SLAYERS_ENCHANTMENT drop
						if (drop.getItemId() == ItemID.SLAYERS_ENCHANTMENT && canDropSlayerEnchantment(npc) &&
								player.getWildernessLevel() > 0 &&
								player.getSlayer().getTask() != null &&
								player.getSlayer().getTask().getMaster() != null &&
								!player.getSlayer().getTask().getMaster().equals(SlayerMaster.KRYSTILIA)) {
							break;
						}

						//if (drop.toItem().getValue(ItemValueType.PRICE_CHECKER) > 550_000_000 && Misc.random(2) == 1) { // 50% chance to skip rare items
						//	continue;
						//}

						items.add(drop.toItem(random));
						parsedTables.add(DropTable.SPECIAL);

						// handle achievements
						AchievementManager.processFor(AchievementType.LUCKY_JOURNEY, player);
						if (npc.getId() == NpcID.GENERAL_GRAARDOR) {
							AchievementManager.processFor(AchievementType.BANDOS_TREASURE, player);
						} else if (npc.getId() == NpcID.CORPOREAL_BEAST) {
							AchievementManager.processFor(AchievementType.LUCKY_BEAST, player);
						}

						// announce drop
						if (drop.getItemId() == ItemID.GHRAZI_RAPIER || drop.getItemId() == ItemID.AMULET_OF_ETERNAL_GLORY || excludeItemDropesage(drop) && (drop.getChance() >= 8 && drop.toItem().getValue(ItemValueType.PRICE_CHECKER) > 50_000_000) || drop.toItem().getValue(ItemValueType.PRICE_CHECKER) > 250_000_000
								|| drop.getChance() >= 100) {
							// New drops message handling
							HandleDropMessages(drop, npc);
						}

						//continue; // this does not change anything
						break; // makes it where you can only get one RDT drop for each kill
					}
				}
			}

			// If we didn't get a special drop, attempt to find a different table..
            if (!table.isPresent()) {
                int chance = random.get().nextInt(100);
                if ((table = getDropTable(chance)).isPresent()) {
                    // Make sure we haven't already parsed this table.
                    if (parsedTables.contains(table.get())) {
                        continue;
                    }
                    // Get the items related to this drop table..
                    Optional<NPCDrop[]> dropTableItems = Optional.empty();
                    switch (table.get()) {
                        case COMMON:
                            if (def.getCommonDrops() != null) {
                                dropTableItems = Optional.of(def.getCommonDrops());
                            }
                            break;
                        case UNCOMMON:
                            if (def.getUncommonDrops() != null) {
                                dropTableItems = Optional.of(def.getUncommonDrops());
                            }
                            break;
                        case RARE:
                            if (def.getRareDrops() != null) {
                                dropTableItems = Optional.of(def.getRareDrops());
                            }
                            break;
                        case VERY_RARE:
                            if (def.getVeryRareDrops() != null) {
                                dropTableItems = Optional.of(def.getVeryRareDrops());
                            }
                            break;
                        default:
                            break;
                    }
                    if (!dropTableItems.isPresent()) {
                        continue;
                    }
                    // Get a random drop from the table..
                    NPCDrop npcDrop = dropTableItems.get()[random.get().nextInt(dropTableItems.get().length)];

					// Slayers enchantment drop (non-special drop handled here)
					if (npcDrop.getItemId() == ItemID.SLAYERS_ENCHANTMENT &&
							canDropSlayerEnchantment(npc) &&
							player.getWildernessLevel() > 0 &&
							player.getSlayer().getTask() != null &&
							player.getSlayer().getTask().getMaster() != null &&
							!player.getSlayer().getTask().getMaster().equals(SlayerMaster.KRYSTILIA)) {
						break;
					}

                    // Add the drop to the drop list.
                    items.add(npcDrop.toItem(random));

                    // Flag this table as visited..
                    parsedTables.add(table.get());
                }
            }
        }
        return items;
    }

    /**
	 * Checks if the player is wearing a ring of wealth which will increase the
	 * chances for getting a good drop.
	 *
	 * @return
	 */
	public boolean wearingRingOfWealth() {
		int[] ring_of_wealth = { 2572, 11988, 11986, 11984, 11982, 11980};
		return wearingRingOfWealthI() || Arrays.stream(ring_of_wealth)
				.anyMatch(ring -> player.getEquipment().getItems()[EquipmentConstants.RING_SLOT].getId() == ring);
	}
    /**
	 * Checks if the player is wearing a ring of wealth which will increase the
	 * chances for getting a good drop.
	 *
	 * @return
	 */
	public boolean wearingRingOfWealthI() {
		int[] ring_of_wealth_i = { 12785, 20790, 20789, 20788, 20787, 20786 };
		return Arrays.stream(ring_of_wealth_i)
				.anyMatch(ring -> player.getEquipment().getItems()[EquipmentConstants.RING_SLOT].getId() == ring);
	}

    /**
     * Attempts to fetch the drop table for the given chance.
     *
     * @param chance
     * @return
     */
    public Optional<DropTable> getDropTable(int chance) {
        Optional<DropTable> table = Optional.empty();
        // Fetch one of the ordinary drop tables
        // based on our chance.
        for (DropTable dropTable : DropTable.values()) {
            if (dropTable.getRandomRequired() >= 0) {
                if (chance <= dropTable.getRandomRequired()) {
                    table = Optional.of(dropTable);
                }
            }
        }
        return table;
    }
}
