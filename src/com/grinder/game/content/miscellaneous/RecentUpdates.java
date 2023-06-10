package com.grinder.game.content.miscellaneous;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.grinder.game.entity.Entity;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.ButtonActions;
import com.grinder.game.model.attribute.Attribute;

/**
 * Handles news and updates
 * 
 * @author 2012
 *
 */
public class RecentUpdates {

	/**
	 * The format
	 */
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy H:m");

	/**
	 * The start news id
	 */
	private static final int NEWS_ID = 49606;

	/**
	 * The updates
	 */
	private static final ArrayList<Update> updates = new ArrayList<>();

	static {

		ButtonActions.INSTANCE.onClick(697, 42543, clickAction -> RecentUpdates.display(clickAction.getPlayer()));

		//Date format: DAY/MONTH/YEAR

		updates.add(new Update("Grinderscape Updates",
				"Please check our Discord page for the latest server updates.\\n",
				"https://discord.gg/b46xx5u",
				"6/9/2022 7:49"));

		updates.add(new Update("Daily Tasks | Bank Presets | PJing & Bug Fixes",
				"Full Bank Presets System | Daily & Weekly Tasks in Quest Tab\\n"
						+ "Weapon Game Optimization | Wilderness Bug Fixes | Barrage Fixes\\n"
						+ "Updated Netty Framework | Charge Orb Spells | Bug Fixes",
				"https://forum.grinderscape.org/topic/6237-daily-weekly-tasks-bank-presets-pjing-timer-bug-fixes/",
				"11/8/2022 8:12"));


		updates.add(new Update("Collection Log | Bow of Faerdhinen | PvP Presets",
				"Ensouled Prayer Heads | New Random Event Rewards | Crystal Armour\\n"
						+ "Game Mode Fixes | Pet Drop Rates Revamp | Pest Control Overhaul\\n"
						+ "Fullscreen Setting Optimization | KC on Rare Drops | Bug Fixes",
				"https://forum.grinderscape.org/topic/6099-collection-log-bow-of-faerdhinen-pvp-presets-ensouled-prayer-heads-more/",
				"14/7/2022 7:07"));

		updates.add(new Update("MASSIVE UPDATE | 5 New Game Modes | NEW Content!",
				"726 New Items | 206# Cache Revision | Nightmare staffs | Torva Gear \\n"
						+ "$1500.00 Event | Triple Ranks System | Zaryte Crossbow | Ancient godsword\\n"
						+ "Superior Void | New Slayer Helmets | Shops Overhaul | Bug Fixes",
				"https://forum.grinderscape.org/topic/6000-biggest-update-ever-realism-spawn-one-life-pvp-master-5-new-game-modes-torva-set-726-new-items-cache-update-206-1500-event-rewards-tons-of-content-bug-fixes/",
				"23/6/2022 4:27"));

		updates.add(new Update("Amethyst Mining, Crafting, Fletching | Bug Fixes!",
				"Basilisk Jaw | Coins to Repair | Redwood Tasks \\n"
						+ "Burnt Bones Spawn | Studded armor support | Zulrah Fixes \\n"
						+ "Slayer Helm Teleport | Faster Home Tele | Bug Fixes",
				"https://forum.grinderscape.org/topic/5871-amethyst-mining-crafting-fletching-qol-changes-bug-fixes/",
				"19/5/2022 10:55"));

		updates.add(new Update("Skilling Pets | Farming Cape | Bug Fixes!",
				"5 New Skilling Pets | Farming Cape Teleports \\n"
						+ "Castle Wars Tickets | Bug Fixes",
				"https://forum.grinderscape.org/topic/5863-skilling-pets-farming-cape-bug-fixes/",
				"9/5/2022 01:02"));

		updates.add(new Update("Merodach Boss | Conversion Progress | Bug Fixes!",
				"Home Bank Redone | New Merodach Boss | Construction Base\\n"
						+ "Economy Major Changes | Over 15 New Items | Game Modes Base\\n"
						+ "La Isla Ebana Maps Rework & Other Bug Fixes & Tweaks!",
				"https://forum.grinderscape.org/topic/5632-new-merodach-boss-conversion-progress-lots-of-bug-fixes/",
				"27/2/2022 12:04"));

		updates.add(new Update("Economy Redone | Void (t) | Graceful | Bug fixes!",
				"Complete Economy Overhaul | Fletching Master | Xmas Removed\\n"
						+ "Galvek Drops | Items Reduction | Mystery Boxes Revamp\\n"
						+ "Trimmed Void Sets | Graceful Outfits Variants | Bug Fixes!",
				"https://forum.grinderscape.org/topic/5590-economy-redone-void-t-graceful-outfit-fletching-master-bug-fixes/",
				"19/1/2022 11:37"));

		updates.add(new Update("Pest Control | Major Combat Rework | Skills Rework!",
				"Pest Control Minigame | Crafting Rework | Xmas Event 201\\n"
						+ "Chronozon Boss | Combat Rework | Pathing Fixes\\n"
						+ "ACS Improvements | Projectiles Redone | Bug Fixes!",
				"https://forum.grinderscape.org/topic/5570-pest-control-major-combat-rework-skills-rework-xmas-201-big-content/",
				"28/12/2021 4:45"));

		updates.add(new Update("Galvek Boss | Platinum island | Herblore revamp!",
				"Herblore Skill Revamp | Platinum member island | CW Fixes\\n"
						+ "Galvek Boss | Combat Dummies | NPC Movement Fixes\\n"
						+ "Text Filter Changes | Combat Fixes | Bug Fixes!",
				"https://forum.grinderscape.org/topic/5511-galvek-boss-platinum-island-herblore-revamp-more/",
				"10/11/2021 4:45"));

		updates.add(new Update("Castle Wars Minigame | New Home | Skill Tips & MORE!",
				"Full Castle Wars Minigame | Completely New Home Area\\n"
						+ "Agility Bug Fixes | Skilling Tips | Weapon Game Revamp\\n"
						+ "Smoke Tunnels | Wilderness Logout Timer | Bug Fixes!",
				"https://forum.grinderscape.org/topic/5398-castle-wars-minigame-new-home-skill-tips-more/",
				"10/10/2021 5:05"));

		updates.add(new Update("Full Agility Skill | Brimhaven Agility | Skill Masters",
				"Agility Revamp | Brimhaven Agility | Agility Obstacles\\n"
						+ "All Rooftop Courses | New Skill Masters | Graceful Gears\\n"
						+ "3 New Weapon Specs | Contributor's Rank | Minor Bug Fixes!",
				"https://forum.grinderscape.org/topic/5285-full-agility-skill-brimhaven-agility-new-skill-masters-more/",
				"30/8/2021 9:01"));

		updates.add(new Update("Wilderness Slayer | Larran's Chest | QB & More!",
				"Wilderness Slayer | All Slayer Masters | Larrans' chest\\n"
						+ "Skill Teleports Revamp | Quest Tab Overhaul | Telekinetic Spell\\n"
						+ "Combat Delay Fixes | Blackjack Fix | Major Bug Fixes!",
				"https://forum.grinderscape.org/topic/5084-wilderness-slayer-larrans-chest-quest-tab-revamp-more/",
				"27/7/2021 9:25"));

		updates.add(new Update("Aquais Neige | Army fighter's torso | Bug fixes!",
				"Completely New Region with a New Minigame Aquais Neige\\n"
						+ "Newly Fighter's Torso gear | Ogre Gate Clue Fix\\n"
						+ "Lots of Back-end and Performance Improvements | Major Bug Fixes!",
				"https://forum.grinderscape.org/topic/4835-aquais-neige-minigame-army-fighters-torso-bug-fixes/",
				"17/6/2021 7:59"));

		updates.add(new Update("170+ New Items | New Member Benefits | Bug Fixes",
				"Over 170 Unique New Items | Members Prayer Drain Reduction Rates\\n"
						+ "New Exotic Limited Items Store | Dialogue Head Fixes for Customs\\n"
						+ "Lots of Back-end and Performance Improvements | Massive Bug Fixes!",
				"https://forum.grinderscape.org/topic/4701-170-new-items-new-member-benefits-bug-fixes/",
				"20/5/2021 6:21"));

		updates.add(new Update("Motherlode mine | New Skill Zone | Member ranks!",
				"Full Motherlode Minigame 1:1 OSRS, Bronze member Skilling Zone\\n"
						+ "New Member's Ranks | Game Economy Related Changes | Shops Revamp\\n"
						+ "Lots of Back-end and Performance Improvements | Massive Bug Fixes!",
				"https://forum.grinderscape.org/topic/4659-motherlode-mine-11-osrs-new-skilling-zone-new-member-ranks-more/",
				"3/5/2021 5:23"));

		updates.add(new Update("Blast Furnace | Region System Revamp | Bug Fixes!",
				"Full Blast Furnace Minigame Implemented, Corp Retreat Mechanics\\n"
						+ "Region System Core Revamp | Door Fixes | Frog Random Event\\n"
						+ "Item on Ground Core Improvements | Repeat AutoTalker | Bug Fixes!",
				"https://forum.grinderscape.org/topic/4597-blast-furnace-region-system-revamp-community-fixes/",
				"4/4/2021 3:24"));


		updates.add(new Update("Dungeon Mechanics | Rug Traveling | Massive QOL!",
				"All Dungeons Proper Mechanics Implemented, Gwd Fixes\\n"
						+ "Legendary Member Bossing Zone | Rug Travel | Al-Kharid Mechanics\\n"
						+ "Scoreboard Improvements | Items on Ground Dump | Massive Bug Fixes!",
				"https://forum.grinderscape.org/topic/4465-dungeon-mechanics-rug-traveling-massive-qol-bug-fixes/",
				"5/3/2021 9:55"));

		updates.add(new Update("Doors | Gates | Curtains | Spirit Tree | QOL & More!",
				"Complete Doors, Gates, and Trapdoors System 1:1 OSRS\\n"
						+ "Curtains System | Spirit Tree Travel | Cow Dairy | Lock-picking\\n"
						+ "Medallion Casino Revamp | Amulet of Chemistry | Massive Bug Fixes!",
				"https://forum.grinderscape.org/topic/4395-doors-gates-curtains-spirit-tree-qol-more/",
				"21/2/2021 9:44"));

		updates.add(new Update("Warriors Guild | Woodcutting Guild | PID Fix",
				"1:1 Warriors Guild Implemented | PID Randomization | Wildy Fix\\n"
						+ "Birdhouses | Obelisk Fixes | New Special Attacks | Boss Fixes\\n"
						+ "Woodcutting Guild | Gem Mining | Massive Bug Fixes!",
				"https://forum.grinderscape.org/topic/4313-warriors-guild-woodcutting-guild-pid-randomization/",
				"14/1/2021 8:32"));


		updates.add(new Update("Xmas-Event | Bonecrusher | Fight Caves | & MORE!!!",
				"1:1 Fight Caves Implemented | 2020 Christmas Event | Bonecrusher\\n"
						+ "Map Tweaks | Store Optmization | New Special Attacks | Boss Fixes\\n"
						+ "Items on Death Mechanics | Obelisks | Massive Bug Fixes!",
				"https://forum.grinderscape.org/topic/4223-christmas-event-2020-bonecrusher-fight-caves-more/",
				"27/12/2020 5:45"));

		updates.add(new Update("Slayer Revamp | Pathing | Auto-Typer | & MORE!",
				"Slayer Skill Revamp | Combat Improvements | Wiki Design\\n"
						+ "Auto-typer Implemented | New Shops | Pathing Re-Engineered\\n"
						+ "Stall Guards | Imbued Max Capes | Lots of QOL | Massive Bug Fixes!",
				"https://forum.grinderscape.org/topic/4109-slayer-revamp-pathing-auto-typer-more/",
				"5/12/2020 7:05"));

		updates.add(new Update("Hill Giant Boss | New Spells | BIG CONTENT & MORE!",
				"Obor Boss | Lizardman Shaman | Demonic Gorillas\\n"
						+ "Chasm of Fire | Catacombs of Kourend | Demon Agony | Teleporting\\n"
						+ "Superior Slayer | Easier Drops | Crushing Vials | Bug Fixes!",
				"https://forum.grinderscape.org/topic/3831-hill-giant-boss-new-spells-big-content-more/",
				"22/9/2020 2:33"));

		updates.add(new Update("Fishing Guild | Big Shark | Bug Fixes & More!",
				"Complete Fishing Skill Redone | Fishing Guild | Big Sharks\\n"
						+ "Hunter Trap Fixes | Home Tweaks | Shop Changes | KQ Fixes\\n"
						+ "Lumbridge Swamp Caves | Item Container Fixes | Bug Fixes!",
				"https://forum.grinderscape.org/index.php?/topic/3574-fishing-guild-big-sharks-bug-fixes-more/",
				"9/8/2020 7:54"));

		updates.add(new Update("Cannon | Salamanders | Chins | Bug Fixes",
				"Item Attributes System | Perfect Cannon | Chins\\n"
						+ "Salamanders | Home Extension | Scythe of Vitur | Wildy Scores\\n"
						+ "Boss Stats | Achievement Widget | Store Changes | Bug Fixes!",
				"https://forum.grinderscape.org/index.php?/topic/3476-cannon-salamanders-chins-item-attributes-bug-fixes/",
				"18/7/2020 10:44"));


		updates.add(new Update("Combat System | Instanced Bosses | QOL Updates!",
				"Complete Rework on Combat System | Instanced Bosses\\n"
						+ "Runecrafting Rework | Ourania Altar | The Abyss Support\\n"
						+ "Construction Progress | Farming Progress | Dialogues & QOL Updates!",
				"https://forum.grinderscape.org/index.php?/topic/3280-osrs-combat-system-instanced-bosses-voting-streaks-qol-updates/",
				"15/6/2020 11:04"));

		updates.add(new Update("Surge Spells | Minigames Overhaul | Bug Fixing!",
				"Fully Working Surge Spells | Vorkath & Zulrah Fixes\\n"
						+ "Boss Contracts Revamp | Minigames Overhaul | Cerberus Fix\\n"
						+ "Hunter Rework | Massive Bug Fixing!",
				"https://forum.grinderscape.org/index.php?/topic/2822-surge-spells-minigames-overhaul-massive-bug-fixing/",
				"11/4/2020 1:11"));


		updates.add(new Update("New OSRS Cache | Ambient Sounds | Vast Skills Rework",
				"Maps Rework | New OSRS Cache & Items | Networking Redone\\n"
						+ "New Dragon Whip Spec | Vines Cutting | Slayer Rework\\n"
						+ "Big Content Update | Gigantic Amount of Bug Fixes!",
				"https://forum.grinderscape.org/index.php?/topic/2660-new-osrs-cache-ambient-sounds-vast-skills-rework/",
				"9/3/2020 5:38"));

		updates.add(new Update("Map Updates & Game Changes Update!",
				"Home Area Expansion | Gamble Area Revamp | Price Guide IG\\n"
						+ "25% Bonus XP Voting | Tax System | New Gambling\\n"
						+ "Huge Economy and Price Changes | Minor Bug Fixes!",
				"https://forum.grinderscape.org/index.php?/topic/2534-map-updates-game-changes-update/",
				"26/1/2020 3:37"));

		updates.add(new Update("2020 Update | XMAS Event | Huge Server Rework!",
				"Unique Xmas Event | New Item Rewards | Commands Interface\\n"
						+ "Blowpipe emptying | Morphing System Rework | Clanchat Fixes\\n"
						+ "Huge Client Stability Rework | Over 40 Reported Bug Fixes!",
				"https://forum.grinderscape.org/index.php?/topic/2452-2020-update-xmas-event-huge-server-rework/",
				"6/1/2020 7:23"));


		updates.add(new Update("Item Drop Finder | Aggressivity Potion | Hunter Fixes",
				"Fixed All Hunter Bugs | Vorkath's Drop Fix | QOL Changes\\n"
						+ "Item Drop Finder | Boss Drop Table | Redone Quest Tab\\n"
						+ "Aggressivity Potion | And Tons Of Other Minor Fixes!",
				"https://forum.grinderscape.org/index.php?/topic/2284-item-drop-finder-aggressivity-potion-hunter-fixes/",
				"2/12/2019 4:49"));

		updates.add(new Update("Hunter Tomes | Bosses Core Rework | HUGE UPDATE!",
				"Hunter Tomes Added | Member's Bond Stacking | Bosses Redone\\n"
						+ "General Store | New Ranks | Faster Switching\\n"
						+ "Randomised PID | Christmas Cracker | TONS MORE!",
				"https://forum.grinderscape.org/index.php?/topic/2204-hunter-tomes-bosses-core-rework-huge-update/",
				"21/11/2019 2:03"));

		updates.add(new Update("136 Unique Titles | Bosses Changes | Bug Fixes",
				"Fully Dynamic Titles System | NPC Kill Tracker Extended\\n"
						+ "Colorful Max Hood | New BKT Zone | Bosses Reworked\\n"
						+ "Tons of Reported Bugs has Been Fixed!",
				"https://forum.grinderscape.org/index.php?/topic/2075-136-unique-titles-bosses-changes-bug-fixes/",
				"14/10/2019 1:36"));
		
		updates.add(new Update("Bug Fixes & QOL Fixes 3#",
				"Fixed Task Rewards Lost on Completion | ::tasks Updated\\n"
				+ "Fixed Several Clue Scroll Bugs | PvM Improvements\\n"
				+ "Over 20 Minor Reported Bugs has Been Fixed!",
				"https://forum.grinderscape.org/index.php?/topic/1979-bug-fixes-qol-fixes-3/",
				"18/9/2019 10:47"));
		
		updates.add(new Update("236 New Tasks | New Gilded Items | Tons of Bug Fixes!",
				"236 New Tasks | Ava's Assembler Max Cape | PVP Fixes\\n"
				+ "40+ New Items | 4 New Potions | 5 New Spells.\\n"
				+ "Major Combat Fixes | Godwars Fixes | & MUCH MORE!!!",
				"https://forum.grinderscape.org/index.php?/topic/1944-236-new-tasks-new-gilded-items-tons-of-bug-fixes/",
				"11/9/2019 4:01"));
		
		updates.add(new Update("Summer Sale & Hotfixes!!!",
				"Summer Sale: Up To 35% Discount on Premium stores\\n"
				+ "Mystery Box & Task Interface Listing Fixes\\n"
				+ "Client Resizeable Support, Trade Fix, and Other Hotfixes!",
				"https://forum.grinderscape.org/index.php?/topic/1863-summer-sale-hotfixes/",
				"18/8/2019 3:46"));
			
		updates.add(new Update("Biggest Update Ever! Massive Content & More!",
					"Seers Rooftop, OSRS Spell Book, Colorful Max Cape\\n"
					+ "New Home, New Member Zone, Avatar Bosses\\n"
					+ "UIM Deposit Box, Skill Guides, AND TONS MORE!",
					"https://forum.grinderscape.org/index.php?/topic/1788-biggest-update-ever-massive-content-more/",
					"29/7/2019 6:27"));
		
		updates.add(new Update("Achievement Toggle | Legendary Items | And Lots Of..",
				"Dragon Pickaxe Special | Resize Chatbox | Login Hover..\\n"
				+ "Legendary Colorful Items | Achievements Toggle Overlay..\\n"
				+ "Major Bug Fixes | Freezing Fix | AND TONS MORE!",
				"https://forum.grinderscape.org/index.php?/topic/1621-achievement-toggle-legendary-items-and-lots-of-content/",
				"26/06/2019 5:54"));
		
		updates.add(new Update("LootShare | NPC Kill Tracker | Boss Mechanics & MORE!",
				"Dynamic Password Changer | NPC Kill Tracker | Lootshare..\\n"
				+ "Clan Chat Redone, Boss Mechanics Nourished, Dragonstone Bolts\\n"
				+ "Boss Contract Shop, Fullscreen Centering & Tons MORE!",
				"https://forum.grinderscape.org/index.php?/topic/1553-lootshare-npc-kill-tracker-boss-mechanics-more/",
				"11/06/2019 12:19"));
		
		updates.add(new Update("Sanction System | New Pet | Shop Changes & MORE!",
				"New Sanction System To Manage, New Hell Cat Pet, Lot's of Shops.. \\n"
				+ "Changes, Hunter Traps Fixes, Daily Login Rewards Fix, Void..\\n"
				+ "Nerfed, Items on Bank Feature, Bosses Hits Fixes & MORE!",
				"https://forum.grinderscape.org/index.php?/topic/1339-sanction-system-new-pet-shop-changes-more/",
				"10/05/2019 1:32"));
		
		updates.add(new Update("Bug Fixes & QOL Fixes 2",
				"Protection Prayers Optimization, Cerberus Souls Bug Fixed \\n"
				+ "NPC Combat Core Modifications, Roald Clue Fixed, Silver Ore Fix\\n"
				+ "Charge Spell, Wildy Course Fixed, Smithing XP Boost & MORE!",
				"https://forum.grinderscape.org/index.php?/topic/1192-bug-fixes-qol-fixes-2/",
				"22/04/2019 4:14"));
		
		updates.add(new Update("Massive Combat Changes | Brimstone Chest | Tons of..",
				"Completely rewritten the combat pathing system with combat changes \\n"
				+ "OSRS movement system,  Brimstone Chest, Barrows Repairing\\n"
				+ "Items on Death Fixes, Redone Drop Rates, Clue Fixes & MORE!",
				"https://forum.grinderscape.org/index.php?/topic/1121-massive-combat-changes-brimstone-chest-tons-of-fixes/",
				"15/04/2019 4:02"));
		
		updates.add(new Update("Iron Man | Clue Scrolls | Hydra | Vorkath & MORE!",
				"5 Different Game Modes, Clue Scrolls System | DFS Charging\\n"
				+ "Items Degrading, Hydra Boss, Vorkath Boss, Rune Dragons\\n"
				+ "Over 70 Bug Fixes, Bank PIN, Voting Tickets, & MUCH MORE!",
				"https://forum.grinderscape.org/index.php?/topic/877-ironman-clue-scrolls-hydra-vorkath-more/",
				"20/03/2019 12:35"));
		
		updates.add(new Update("Minor bug fixes & UI enhancements!",
				"A bug w﻿ith dicing caused players to lose items will no longer happen.﻿\\n"
				+ "Buffed d claws special accuracy by 5%; Fixed hunter's trap issue\\n"
				+ "Buffed max hit for Wind wave - Fire wave by 50%, and a few other fixes.",
				"https://forum.grinderscape.org/index.php?/topic/763-mini-update-minor-fixes-ui-enhancements/",
				"5/02/2019 4:37"));
		
		updates.add(new Update("Automated Dicing | New Ranks | Massive Bug Fixes!",
				"Automated Dicing System with Flower Gambling | 5 New In-game Ranks\\n"
				+ "Combat Changes to Perfection, Stackable Mystery Boxes\\n"
				+ "Fixed Trade Bug, New Bones Shop, Dicer's Token, And More!",
				"https://forum.grinderscape.org/index.php?/topic/748-automated-dicing-new-ranks-massive-bug-fixes/",
				"31/01/2019 12:03"));
		
		updates.add(new Update("Quality of Life Improvements | Corp Pet...",
				"Corp Pet Addition, Mass Items Equipment Fixes, Rules Updated\\n"
				+ "Removed Christmas Theme, Yell Titles With Spaces Support, and\\n"
				+ "Amulet of Damned Support, Iron Man Modes Coming Soon & Much More!",
				"https://forum.grinderscape.org/index.php?/topic/713-quality-of-life-improvements-corp-pet-amulet-of-damned/",
				"14/01/2019 2:46"));
		
		updates.add(new Update("Yell Customizer | Xmas Theme | Ghrazi Rapier!",
				"Complete New Yell Customizer Added, Monkey Guards Combat Fix\\n"
				+ "Long Message Line Splitting Overhaul, New Gameframe, Xmas Theme\\n"
				+ "Xmas Theme, Ghrazi Rapier, and Tons of Bug Fixes!",
				"https://forum.grinderscape.org/index.php?/topic/668-yell-customizer-xmas-theme-ghrazi-rapier/",
				"22/12/2018 3:07"));

		updates.add(new Update("High scores | Client Emojis | Shop Interface!",
				"High scores Added on Website, New Shops Interface, Decanter System\\n"
				+ "Items Kept On Death Redone, Clan Chat Overhaul, Buy 50 Added\\n"
				+ "Complete New Custom Emojis, Price Checker Redone, Tons of Bug Fixes!",
				"https://forum.grinderscape.org/index.php?/topic/663-high-scores-client-emojis-shop-interface-much-more/",
				"11/12/2018 4:45"));

		updates.add(new Update("Custom Items | Daily Login Rewards | XP Customization",
				"Tons of Shop Changes, New Custom Items, Drops Nerfs, 34+ Bug Fixes.\\n"
				+ "Yell Message Colors, XP Customization, Magic Skill Interface.\\n"
				+ "Daily Login Rewards, Clan Chat Interface Hovers, Obelisks GFX!",
				"https://forum.grinderscape.org/index.php?/topic/657-custom-items-daily-login-rewards-xp-customization/",
				"02/12/2018 3:29"));

		updates.add(new Update("Mini Bug Fixes V2!",
				"Barrage's Bugs Fixed, Tabs issue no longer happens.\\n"
				+ "Agility Skillcape Proper Requirements, Kree'arra Spawn Fix.\\n"
				+ "Obstacles XP Bug Fixed, New Emblem Trader in Revenant's Cave!",
				"https://forum.grinderscape.org/index.php?/topic/644-mini-bug-fixes-v2/",
				"16/11/2018 2:00"));
		
		updates.add(new Update("Bank System | Shop Searching | Quest Tab Revamp!",
				"Shop Searching Feature, Barrage Bug Fix, Quest tab Revamp.\\n"
				+ "Redone Achievements System, Weapon Game Bug Fixed.\\n"
				+ "Complete New Bank System with Resizeable Support, Wilderness Fix!",
				"https://forum.grinderscape.org/index.php?/topic/592-bank-system-shop-searching-quest-tab-revamp-much-more/",
				"5/11/2018 4:46"));
		
		updates.add(new Update("Revenant's Caves | Rune Pouch | 30+ New Items!",
				"Tons Of Bug Fixes, Full Sounds Support, New Mystery Box\\n"
				+ "3 New Stores, PVP Items, Shop Prices Optimized, Ore Prospecting\\n"
				+ "Skeleton Hellhounds, Dicing System, Combat Balancing & Much More!",
				"https://forum.grinderscape.org/index.php?/topic/567-revenants-caves-rune-pouch-30-new-items-much-more/",
				"24/10/2018 7:00"));
		
		updates.add(new Update("Game sounds, Resource Area Gate, and Fairy Ring(+)..",
				"Over 40 Bugs Fixed, Duel Poison Bug No Longer Happens.\\n"
				+ "More Than A Hundred Sounds & Music Added! Fairy Transformation Ring(+).\\n"
				+ "Instanced Cerberus, Wilderness Resource Gate Added, Bolts Special...",
				"https://forum.grinderscape.org/index.php?/topic/549-game-sounds-resource-area-gate-and-fairy-ring/",
				"6/10/2018 5:40"));
		
		updates.add(new Update("Mini Bug Fixes & Server Optimization!",
				"Shop Names Are Now equal To Their Store Content. Minor Bug Fixes.\\n"
				+ "Zulrah Teleport Tab Fixed, Participation Announcements Reduced.\\n"
				+ "Stalls Item Rewards Modifications, Boss Contract Timers Optimized...",
				"https://forum.grinderscape.org/index.php?/topic/534-mini-bug-fixes-server-optimization/",
				"19/09/2018 3:58"));
		
		updates.add(new Update("Farming skill, New home area, and Zulrah Boss!",
				"Muddy Chest added, Emojis Now Show In Private Chat, RDT Upgraded\\n"
				+ "Duel Poison Bug Fixed, Barrows Will Be Broken When Dropped, Trade Fix\\n"
				+ "Hourly Bonus Skill, Withdraw All But One Added, And Bug Fixes...",
				"https://forum.grinderscape.org/index.php?/topic/503-farming-skill-new-home-area-and-zulrah-boss/",
				"5/09/2018 7:23"));
		
		updates.add(new Update("Retaliation Fix, Blood Altar, Blowpipe Fixes..",
				"Void repair cost reduction, PJING fix, k'riltsaruth fix\\n"
				+ "Cleaning cloth support, Blood altar added, Toxic blowpipe redone\\n"
				+ "PM fixes, shop buy limit, and a lot of other bug fixes you can find...",
				"https://forum.grinderscape.org/index.php?/topic/432-retaliation-fix-blood-altar-blowpipe-fixes-part-2/",
				"19/08/2018 4:07"));
		
		updates.add(new Update("Jewellry enchanting, BM Rewards, Bolt Enchanting..",
				"Broken items fixing cost reduced, lottery limit, bosses optimized\\n"
				+ "Participation points rewards blood money, exp halve removed\\n"
				+ "Godwars fix, potion decanting fix, combat styles saving, and more...",
				"https://forum.grinderscape.org/index.php?/topic/368-jewellry-enchanting-bm-rewards-bolt-enchantingpart-1/",
				"10/08/2018 6:56"));
		
		updates.add(new Update("Notifications Tab, Member Zone, and Gilded Altars..",
				"Member tab, Member zone teleport, Unique shop, Gilded Altar\\n"
				+ "Notifications tab added with flashing, Major shop rework..\\n"
				+ "Slayer stronghold teleport added, Over 12 new commands and bug fixes.",
				"https://forum.grinderscape.org/index.php?/topic/350-notifications-tab-donator-zone-and-gilded-altars/",
				"4/08/2018 9:25"));
		
		updates.add(new Update("Void Repairing, Shop Bug, Price checker IG!",
				"You can now repair your broken items, barrows degrading on death\\n"
				+ "Wilderness boosting fixed, Reset Lamp fixed, Overload added\\n"
				+ "Crafting rework, skill interfaces added, shards making and much more...",
				"https://forum.grinderscape.org/index.php?/topic/315-void-repairing-shop-bug-price-checker-ig/",
				"29/07/2018 00:00"));
		
		updates.add(new Update("Tons of Bug Fixes, Wilderness Thieving, Clan Setup",
				"Npc pathing reworked, new trade interface, clan chat setup interface\\n"
				+ "High alch items now works, slayer masters added, cooking gloves work.\\n"
				+ "Wilderness skilling added using the skilling teleports.",
				"https://forum.grinderscape.org/index.php?/topic/234-tons-of-bug-fixes-wilderness-thieving-clan-chat-setup/",
				"10/07/2018 00:00"));
		
		updates.add(new Update("Bug Fixes, Clan Chat Rework, and New Interfaces",
				"XP bug fixed, medium caskets can now be opened, private messages fixed.\\n"
				+ "Blood money boosting tweaked, pets rates nerfed by 200%, tourny minigame fixed.\\n"
				+ "Clan chat complete rework, and future updates preview.",
				"https://forum.grinderscape.org/index.php?/topic/183-bug-fixes-clan-chat-rework-and-drop-rates-changes/",
				"4/07/2018 00:00"));
		
		updates.add(new Update("Tons of Bug Fixes, New Content, and Member perks",
				"Working sled, dwarven rock cake, beer drinking system implemented.\\n"
				+ "Lots of item stat requirement fixes, and core system improvements\\n"
				+ "Item on bank to note for members, New items in Premium Store!",
				"https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=465286",
				"3/04/2018 00:00"));
		
		updates.add(new Update("Duel Fixes, Client Tweaks, NPC Combat Changes",
				"Client launcher and cache downloader should be permanently fixed.\\n"
				+ "The hunter traps bug is now fixed. Angler fish hitpoints heals properly now.\\n"
				+ "Npcs size and path finding system has been implemented.",
				"https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=465279",
				"28/03/2018 00:00"));
		
		updates.add(new Update("Content, changes, and fixes",
				"You can now play the @gre@Battle Royale@whi@ and @gre@Weapon Game @whi@minigames at ::tourny\\n"
				+ "You can now pickup your @gre@hunter traps. Anglerfish @whi@been added to skilling tasks.\\n"
				+ "New additions include @gre@wilderness boss, @gre@lottery, @whi@and @gre@wilderness agility",
				"https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=465232",
				"19/03/2018 00:00"));
		
		updates.add(new Update("New Commands, Member features, Bug Fixes",
				"Ifyou're now stuck at zero hit points, or ifyou're stuck in wilderness agility \\n"
				+ "You can now use @red@::stuck @whi@command to get unstuck! Many useful commands for staff\\n"
				+ "Have also been added, @gre@shop prices fixed,@whi@ and member benefits!",
				"https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=465252",
				"12/03/2018 00:00"));

		updates.add(new Update("Skills Updated, Vannaka Slayer Master, Misc things.",
				"@gre@Vannaka@whi@ the Slayer master has been relased. You also get more @gre@Slayer points\\n"
				+ "You can now fish @gre@Anglerfish@whi@ in the Wilderness resource area. @gre@Elite void@whi@ got a bonus.\\n"
				+ "Shops changes include @gre@Premium@whi@ and @gre@Blood@whi@ store, including prices!",
				"https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=465229",
				"10/03/2018 00:00"));
		
		updates.add(new Update("Combat Additons and Changes, Tweaks, Misc things.",
				"@gre@Tome of fire@whi@ is released with its effects, @gre@mage's book@whi@ increase acuracy\\n"
				+ "You can now be @red@jailed@whi@ by a staff member! @gre@Jad@whi@ has had his defence nerfed\\n"
				+ "and @gre@Tzhaar mage@whi@ monsters have their correct combat method.",
				"https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=465228",
				"08/03/2018 00:00"));
		
		updates.add(new Update("Bosses, skills, combat, weapons, and Launcher",
				"Weapons such as @gre@Armadyl Godsword @whi@and @gre@Dragon claws@whi@ now more accurate and do more\\ndamage. The combat mechanics have been improved for move smooth experience.\\n@gre@Hunter @whi@and @gre@Slayer@whi@ skills have been released!",
				"https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=465217",
				"03/03/2018 00:00"));

		updates.add(new Update("Many many spawns, experience cap, ladders stairs",
				"GrinderScape has now over @gre@500+ @whi@spawns added!\\nThe experience cap has been lifted to 350M from 250M\\nMany ladders and stairs now work!",
				"https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=465214",
				"02/03/2018 00:00"));

		updates.add(new Update("Wilderness Resource, looting bag, presets",
				"There is now a @gre@Wilderness resource@whi@ teleport with @gre@chaos altar for prayer\\nThe @gre@looting bag@whi@ now works. Custom @gre@presets@whi@ now save\\nRing of nature, ring of coins now transform you!",
				"https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=465205",
				"25/02/2018 00:00"));

		updates.add(new Update("Weapon Minigame! Smithing Skill",
				"Participate in the server activity minigame called @gre@'Weapon Game'@whi@ great fun!\\n@gre@Smithing@whi@ skill is now available at Varrock!\\nPets drop rate has increased to 14000:1",
				"https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=465202",
				"25/02/2018 00:00"));

		updates.add(new Update("PKing bots! Untradeables",
				"You can now fight the bots in the Wilderness. The smithing skill is a lot harder with\\nthe experience nerf. You can @red@no longer trade pets, skillcapes, plus more.",
				"https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=465199",
				"24/02/2018 00:00"));

		updates.add(new Update("Fixes, changes, additions",
				"Mining master with tasks released! Pets save on logout, and can't duel with them.\\nWoodcutting gives more logs.\\nNew potions added: combat potion, antidote, sanfew serum.",
				"https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=465197",
				"23/02/2018 00:00"));

		updates.add(new Update("Fixes, changes, additions",
				"Woodcutting master with tasks released! Many fixes such as dragon scimitar,\\nitem textures, dragons combat",
				"https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=465192",
				"21/02/2018 00:00"));

		updates.add(new Update("Small Updates",
				"You can now gamble your firecape for Jad pet! Bosses Teleport have been released!",
				"https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=465186",
				"20/02/2018 00:00"));

		updates.add(new Update("Small Updates",
				"PvP basic kill increase from 1k to 7k, get PKing!\\nDying in PvP, duel arena, and using Box of health is now fixed\\nBarrows now fully works and brothers death are saved.",
				"https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=465180",
				"19/02/2018 00:00"));

		updates.add(new Update("Achievements & PvP - Part 2",
				"25 achievements have been released!\\nFew items had fixing like Tanzanite helm.\\nPrayers now switch flawlessly and finally barrows can be completed.",
				"https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=465178",
				"18/02/2018 00:00"));

		updates.add(new Update("Small Updates - Part 1",
				"Thieving has had changes and additions. Rogue Clothing available!\\nSmall PvP Updates such as killstreak buff.",
				"https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=465174",
				"18/02/2018 00:00"));

		updates.add(new Update("Duel Arena Fixes",
				"Special bar now fully regenerates as well as the random DCing bug been fixed.",
				"https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=465171",
				"17/02/2018 00:00"));

		updates.add(new Update("Fixes and Changes",
				"Many small fixes that maximise gameplay mechanics and enjoyment\\nMain fixes are: Staking Blood Money, Special and health regenerate after duel,\\nnow able to pickup Blood Money from PvP.",
				"https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=465171",
				"17/02/2018 00:00"));

		updates.add(new Update("GrinderScape Release!",
				"GrinderScape has been relased. The first update consisted of small bug fixes",
				"https://forum.grinderscape.org/index.php?app=forums&module=forums&controller=topic&id=465167",
				"16/02/2018 00:00"));
	}

	/**
	 * Displaying the updates
	 * 
	 * @param player
	 *            the palyer
	 */
	public static void display(Player player) {

		int id = NEWS_ID;
		for (Update update : updates) {

			player.getPacketSender().sendString(id, "@or1@" + update.getTitle());
			player.getPacketSender().sendString(id + 1, update.getDescription());
			player.getPacketSender().sendString(id + 2, "View Thread");
			try {
				Date date1 = DATE_FORMAT.parse(update.getDate().toString());
				Date today = DATE_FORMAT.parse(DATE_FORMAT.format(new Date()));
				long diff = today.getTime() - date1.getTime();
				String daysAgo = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + " days ago";
				if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) == 0) {
					daysAgo = "Today";
				}
				if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) == 1) {
					daysAgo = "Yesterday";
				}
				player.getPacketSender().sendString(id + 3, daysAgo);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			id += 5;
		}

		int scroll = Math.max(updates.size() * 70, 215);

		player.getPacketSender().sendScrollbarHeight(49604, scroll);
		player.getPacketSender().sendInterface(49600);
		EntityExtKt.markTime(player, Attribute.LAST_NEWS);
	}
	
	/**
	 * Opens a thread
	 * 
	 * @param player
	 *            the player
	 * @param button
	 *            the button
	 */
	public static void viewThread(Player player, int button) {
		int id = button - 49608;
		id += 5;
		id /= 5;
		id--;

		if (id > updates.size()) {
			return;
		}
		if (updates.get(id).getThreadLink().length() < 1) {
			player.getPacketSender().sendMessage("The thread link is unavailable. Contact a moderator for more.");
			return;
		}
		player.getPacketSender().sendMessage("Opening: @dre@" + updates.get(id).getThreadLink());
		player.getPacketSender().sendURL(updates.get(id).getThreadLink());
	}
	
	public static void onLogin(Player player) {
		try {
			long time = DATE_FORMAT.parse(updates.get(0).getDate().toString()).getTime();
			if (player.isNewPlayer() || EntityExtKt.getLong(player, Attribute.LAST_NEWS, time-1) < time) {
				player.getPacketSender().sendMessage(":newsflash:");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public static boolean handleButton(Player player, int buttonId) {
        if (buttonId >= 49608 && buttonId <= 49653) {
            if (!player.BLOCK_ALL_BUT_TALKING)
            viewThread(player, buttonId);
            return true;
        }
        return false;
    }

    /**
	 * Represents an update
	 * 
	 * @author 2012
	 *
	 */
	public static class Update {

		/**
		 * The title
		 */
		private String title;

		/**
		 * The description
		 */
		private String description;

		/**
		 * The thread link
		 */
		private String threadLink;

		/**
		 * The date
		 */
		private String date;

		/**
		 * Represents an update
		 * 
		 * @param title
		 *            the title
		 * @param description
		 *            the description
		 * @param threadLink
		 *            the thread link
		 * @param date
		 *            the date
		 */
		public Update(String title, String description, String threadLink, String date) {
			this.setTitle(title);
			this.setDescription(description);
			this.setThreadLink(threadLink);
			this.setDate(date);
		}

		/**
		 * Sets the title
		 *
		 * @return the title
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * Sets the title
		 * 
		 * @param title
		 *            the title
		 */
		public void setTitle(String title) {
			this.title = title;
		}

		/**
		 * Sets the description
		 *
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * Sets the description
		 * 
		 * @param description
		 *            the description
		 */
		public void setDescription(String description) {
			this.description = description;
		}

		/**
		 * Sets the threadLink
		 *
		 * @return the threadLink
		 */
		public String getThreadLink() {
			return threadLink;
		}

		/**
		 * Sets the threadLink
		 * 
		 * @param threadLink
		 *            the threadLink
		 */
		public void setThreadLink(String threadLink) {
			this.threadLink = threadLink;
		}

		/**
		 * Sets the date
		 *
		 * @return the date
		 */
		public String getDate() {
			return date;
		}

		/**
		 * Sets the date
		 * 
		 * @param date
		 *            the date
		 */
		public void setDate(String date) {
			this.date = date;
		}
	}
}
