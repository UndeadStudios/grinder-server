package com.grinder.game.content.pvm;

import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeNpc;
import com.grinder.game.content.minigame.aquaisneige.monsters.TheInadequacy;
import com.grinder.game.content.minigame.fightcave.FightCaveNpc;
import com.grinder.game.content.minigame.fightcave.monsters.TzTokJad;
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerMonsterType;
import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;
import com.grinder.util.Misc;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Handles the MosterKillTracker functionality.
 * 
 * @author Awakening - https://www.rune-server.ee/members/awakening/
 * @date 28/05/2019
 * 
 * TODO: Re-work so if names are too long for the interface, the size of the button
 * is changed server sided.
 *
 */
public class MonsterKillTracker {

	/**
	 * The kills tracked
	 */
	private ArrayList<KillTrack> kills = new ArrayList<KillTrack>();

	/**
	 * The monsters killed
	 */
	private int monstersKilled;

	/**
	 * The bosses killed
	 */
	private int bossesKilled;

	/**
	 * Whom we fighting
	 */
	private NPC fighting;

	/**
	 * The time it took
	 */
	private long time;

	public static final int INTERFACE_ID = 59250;
	public static final int SCROLL_BAR_ID = INTERFACE_ID + 3;

	/**
	 * Tracking npc kill
	 * 
	 * @param player
	 *            the player
	 * @param npc
	 *            the npc
	 */
	public static long track(Player player, NPC npc) {
		/*
		 * The npc tracking
		 */
		KillTrack tracked = forName(player, npc.fetchDefinition().getName());
		/*
		 * The time difference
		 */
		long difference = System.currentTimeMillis() - player.getKillTracker().getTime();
		/*
		 * The time elapsed
		 */
		String elapsed = Misc.getTimeElapsed(player.getKillTracker().getTime());
		/*
		 * Not tracked yet
		 */
		if (tracked == null) {
			player.getPacketSender().sendMessage("<img=769> " + npc.fetchDefinition().getName() + " has been added to your kill tracker.");
			player.getKillTracker().getKills()
					.add(new KillTrack(npc.getId(), npc.fetchDefinition().getName(), 1, 1, 1, difference, null));
			return difference;
		}
		/*
		 * Increase kills
		 */
		tracked.setAmount(tracked.getAmount() + 1);
		tracked.setStreakAmount(tracked.getStreakAmount() + 1);
		tracked.setDropKC(tracked.getDropKC() + 1);

		if (MonsterKilling.INSTANCE.isBoss(npc)) {
			if (npc instanceof FightCaveNpc) {
				if (npc instanceof TzTokJad) {
					player.sendMessage("Your " + npc.fetchDefinition().getName() + " kill count is: @red@" + getKillsCount(player, npc.fetchDefinition().getId()) + "</col>.");
				}
			} else if (npc instanceof AquaisNeigeNpc) {
				if (npc instanceof TheInadequacy) {
					player.sendMessage("Your " + npc.fetchDefinition().getName() + " kill count is: @red@" + getKillsCount(player, npc.fetchDefinition().getId()) + "</col>.");
				}
			} else {
				player.sendMessage("Your " + npc.fetchDefinition().getName() + " kill count is: @red@" + getKillsCount(player, npc.fetchDefinition().getId()) + "</col>.");
			}
		}

		if (MonsterKilling.INSTANCE.isBoss(npc)) {
				if (npc instanceof FightCaveNpc) {
					if (npc instanceof TzTokJad) {
						if (tracked.getRecord() > difference) { // New personal best
							tracked.setRecord(difference);
							player.sendMessage("Fight duration: @red@" + elapsed.replace("sec", "seconds") + "</col> (new personal best)");
						} else {
							player.sendMessage("Fight duration: @red@" + elapsed.replace("sec", "seconds") +"</col>. Personal best: " + Misc.sendTimeFormat(tracked.getRecord()).replace("sec", "seconds") + "");
						}
					}
				} else if (npc instanceof AquaisNeigeNpc) {
					if (npc instanceof TheInadequacy) {
						if (tracked.getRecord() > difference) { // New personal best
							tracked.setRecord(difference);
							player.sendMessage("Fight duration: @red@" + elapsed.replace("sec", "seconds") + "</col> (new personal best)");
						} else {
							player.sendMessage("Fight duration: @red@" + elapsed.replace("sec", "seconds") +"</col>. Personal best: " + Misc.sendTimeFormat(tracked.getRecord()).replace("sec", "seconds") + "");
						}
					}
				} else {
					if (MonsterKilling.INSTANCE.isBoss(npc)) {
						if (tracked.getRecord() > difference) { // New personal best
							tracked.setRecord(difference);
							player.sendMessage("Fight duration: @red@" + elapsed.replace("sec", "seconds") + "</col> (new personal best)");
						} else {
							player.sendMessage("Fight duration: @red@" + elapsed.replace("sec", "seconds") + "</col>. Personal best: " + Misc.sendTimeFormat(tracked.getRecord()).replace("sec", "seconds") + "");
						}
					}
				}
				}
		/*
		 * New record
		 */
		if (tracked.getRecord() > difference && !MonsterKilling.INSTANCE.isBoss(npc)) {
			tracked.setRecord(difference);
			player.getPacketSender().sendMessage("<img=769>@whi@ New personal record for killing " + npc.fetchDefinition().getName() + " with a record of " + elapsed.replace("sec", "seconds") + "!");
			/*
			 * Sends tracking message
			 
			player.getPacketSender().sendMessage("<img=769>Total execution time: " + elapsed);*/
		}
		return difference;
	}

    public static boolean handleButton2(Player player, int buttonId) {
        if (buttonId >= 48211 && buttonId <= 49108) {
            sendSelectedNPC(player, buttonId);
            return true;
        }
        return false;
    }

    /**
	 * Returns the amount of kills for the NPC Name.
	 */
	public static int getKillsCount(Player player, int npcId) {

		final NpcDefinition npcDefinition = NpcDefinition.forId(npcId);
		final KillTrack tracked = forName(player, npcDefinition.getName());

		if(tracked == null)
			return -1;

		return tracked.getAmount();
	}


	/**
	 * Displays the side
	 * 
	 * @param player
	 *            the player
	 */
	public static void displayNPCList(Player player) {
		
		/*
		 * Clears interface
		 */
		for (int i = 0; i < 300 * 3; i += 3) {
			player.getPacketSender().sendString(48211 + i, "");
			player.getPacketSender().sendTooltip(48211 + i, "");
			player.getPacketSender().sendString(48212 + i, "");
		}
		
		player.getPacketSender().sendString(48202, "@or1@Grinderscape NPC Kill Log");
		player.getPacketSender().sendString(48203, "@or1@View your Boss and Monster killing statsistics below");
		
		/*
		 * The string id
		 */
		int id = 48211;
		String[] splitNPCName;

		/*
		 * Sends the kills
		 */
		for (KillTrack track : getSortedList(player)) {
			if (track == null) {
				continue;
			}
			
			/**
			 * If NPC Name Less Than or Equal to 18.
			 */
			if(track.getName().length() <= 18) {
				player.getPacketSender().sendString(id, track.getName());
				player.getPacketSender().sendTooltip(id, "View " + track.getName() + " tracking");

				/**
				 * Shortens NPC Names which are greater than 18.
				 * 
				 * NPCName is split into each corresponding word, the last word is used 
				 * to represent the NPC is the interface, e.g. Al-kharid warrior > warrior
				 * The given word is then capitalised. (warrior > Warrior)
				 */
			} else if (track.getName().length() > 18) {
				if(StringUtils.contains(track.getName(), " ")) {
					splitNPCName = track.getName().split("\\s");
					player.getPacketSender().sendString(id, WordUtils.capitalize(splitNPCName[splitNPCName.length - 1].toString()));
					player.getPacketSender().sendTooltip(id, "View " + WordUtils.capitalize(splitNPCName[splitNPCName.length - 1].toString()) + " tracking");
				} else {
					player.getPacketSender().sendString(id, track.getName());
					player.getPacketSender().sendTooltip(id, "View " + track.getName() + " tracking");
				}
			}
			
			player.getPacketSender().sendString(id + 1, track.getAmount() + "");

			id += 3;
		}

		/*
		 * Scroll height
		 */
		int scroll = Math.max(getSortedList(player).size() * 16, 131);

		player.getPacketSender().sendScrollbarHeight(48209, scroll);
		/*
		 * Sends overall kills
		 */
		player.getPacketSender().sendString(48205,
				"@or2@Total NPC killcount: " + player.getKillTracker().getMonstersKilled());
		player.getPacketSender().sendString(48206,
				"@or2@Total Boss Killcount: " + player.getKillTracker().getBossesKilled());

		player.getPacketSender().sendString(48204, "@or1@Select an NPC!");
		player.getPacketSender().sendString(48207, "");
		player.getPacketSender().sendString(48208, "");

		player.getPacketSender().sendInterface(48200);
	}

	public static void sendSelectedNPC(Player player, int button) {
		int index = (button - 48211) / 3;

		if (index >= getSortedList(player).size())
			return;

		KillTrack track = getSortedList(player).get(index);

		if (track == null)
			return;

		String npcName = track.getName();
		int npcAmount = track.getAmount();
		long npcRecord = TimeUnit.MILLISECONDS.toSeconds(track.getRecord());

		if (button != 0 && npcName != null && npcAmount != 0) {
			player.getPacketSender().sendString(48204, "@or1@" + npcName);
			player.getPacketSender().sendString(48207, "@or2@Amount Killed: " + NumberFormat.getIntegerInstance().format(npcAmount));
			player.getPacketSender().sendString(48208, "@or2@Fastest Kill: " + npcRecord + " seconds.");
			if (npcAmount == 1) {
				player.say("I have killed " + npcName +" once with a time record of " + npcRecord +" seconds.");
			} else {
			player.say("I have killed " + npcName +" a total of " + NumberFormat.getIntegerInstance().format(npcAmount) +" times with the fastest record of " + npcRecord + " seconds.");
			}
		}
	}

	/**
	 * Sending the slayer log
	 * 
	 * @param player
	 *            the player
	 */
	public static void sendSlayerLog(Player player) {
		int startId = INTERFACE_ID + 8;
		int id = startId;
		for (KillTrack track : getSlayerList(player)) {
			if (track == null) {
				continue;
			}
			if ((id - startId) % 2 == 0)
				player.getPacketSender().sendInterfaceDisplayState(id, false);
			player.getPacketSender().sendString(id + 1, track.getName());
			player.getPacketSender().sendString(id + 2, NumberFormat.getInstance().format(track.getAmount()));
			player.getPacketSender().sendString(id + 3, NumberFormat.getInstance().format(track.getStreakAmount()));
			player.getPacketSender().sendInterfaceDisplayState(id + 4, false);
			id += 5;
		}

		for (; id < startId + 500; id += 5) {
			if ((id - startId) % 2 == 0)
				player.getPacketSender().sendInterfaceDisplayState(id, true);
			player.getPacketSender().sendString(id + 1, "");
			player.getPacketSender().sendString(id + 2, "");
			player.getPacketSender().sendString(id + 3, "");
			player.getPacketSender().sendInterfaceDisplayState(id + 4, true);
		}

		int scroll = Math.max(getSlayerList(player).size() * 18, 244);

		player.getPacketSender().sendScrollbarHeight(SCROLL_BAR_ID, scroll);
		player.getPacketSender().sendInterface(INTERFACE_ID);
	}

	/*
	 * Finds existing
	 */
	public static KillTrack forName(Player player, String name) {
		for (KillTrack track : player.getKillTracker().getKills()) {
			if (track == null) {
				continue;
			}
			if (track.getName().equalsIgnoreCase(name)) {
				return track;
			}
		}
		return null;
	}

	/**
	 * Sorts the list in alpha order
	 * 
	 * @param player
	 *            the player
	 * @return the sorted list
	 */
	private static ArrayList<KillTrack> getSortedList(Player player) {
		/*
		 * The existing list
		 */
		ArrayList<KillTrack> existing = player.getKillTracker().getKills();
		/*
		 * The new list
		 */
		ArrayList<KillTrack> list = new ArrayList<KillTrack>();
		/*
		 * The names
		 */
		String[] names = new String[existing.size()];
		/*
		 * The slot
		 */
		int slot = 0;
		/*
		 * Loop the existing
		 */
		for (KillTrack track : existing) {
			/*
			 * Invalid track
			 */
			if (track == null) {
				continue;
			}
			/*
			 * Adds to name list
			 */
			names[slot] = track.getName();
			slot++;
		}
		/*
		 * Sorts to alpha
		 */
		Arrays.sort(names);
		/*
		 * Adds to new list
		 */
		for (String s : names) {
			list.add(forName(player, s));
		}
		return list;
	}

	/**
	 * Sorts the list in alpha order
	 * 
	 * @param player
	 *            the player
	 * @return the sorted list
	 */
	private static ArrayList<KillTrack> getSlayerList(Player player) {
		/*
		 * The existing list
		 */
		ArrayList<KillTrack> existing = player.getKillTracker().getKills();
		/*
		 * The new list
		 */
		ArrayList<KillTrack> list = new ArrayList<KillTrack>();
		/*
		 * The names
		 */
		String[] names = new String[existing.size()];
		/*
		 * The slot
		 */
		int slot = 0;
		/*
		 * The slayer monsters
		 */
		for (SlayerMonsterType monster : SlayerMonsterType.values()) {
			/*
			 * Invalid monster
			 */
			if (monster == null) {
				continue;
			}
			/*
			 * Check tracking
			 */
			for (KillTrack track : existing) {
				/*
				 * Invalid track
				 */
				if (track == null) {
					continue;
				}
				/*
				 * Is slayable
				 */
				if (track.getName().toLowerCase().contains(monster.getName().toLowerCase())) {
					names[slot] = monster.getName().toLowerCase();
					slot++;
				}
			}
		}
		/*
		 * Fix list
		 */
		for (int i = 0; i < names.length; i++) {
			if (names[i] == null) {
				names[i] = "";
			}
		}
		/*
		 * Sorts to alpha
		 */
		Arrays.sort(names);
		/*
		 * Adds to new list
		 */
		for (String s : names) {
			if (!list.contains(forName(player, s))) {
				list.add(forName(player, s));
			}
		}
		return list;
	}

	/**
	 * Handles reset streak button
	 * @param player
	 * @param button
	 * @return
	 */
	public static boolean handleButton(Player player, int button) {
		int startId = INTERFACE_ID + 12;
		if (button >= startId && button <= startId + 500) {
			int index = (button - startId) / 5;
			player.getKillTracker().getKills().get(index).setStreakAmount(0);
			if (player.getInterfaceId() == INTERFACE_ID) {
				player.getPacketSender().sendString(button - 1, "0");
			}
			return true;
		}
		return false;
	}

	/**
	 * Sets the kills
	 *
	 * @return the kills
	 */
	public ArrayList<KillTrack> getKills() {
		return kills;
	}

	/**
	 * Sets the kills
	 * 
	 * @param kills
	 *            the kills
	 */
	public void setKills(ArrayList<KillTrack> kills) {
		this.kills = kills;
	}

	/**
	 * Sets the monstersKilled
	 *
	 * @return the monstersKilled
	 */
	public int getMonstersKilled() {
		return monstersKilled;
	}

	/**
	 * Sets the monstersKilled
	 * 
	 * @param monstersKilled
	 *            the monstersKilled
	 */
	public void incrementMonsterKillCount(int monstersKilled) {
		this.monstersKilled += monstersKilled;
	}

	/**
	 * Sets the bossesKilled
	 *
	 * @return the bossesKilled
	 */
	public int getBossesKilled() {
		return bossesKilled;
	}

	/**
	 * Increments the bossesKilled variable.
	 * 
	 * @param amount the number of bosses killed
	 */
	public void incrementBossKillCount(int amount) {
		this.bossesKilled += amount;
	}

	/**
	 * Sets the fighting
	 *
	 * @return the fighting
	 */
	public NPC getFighting() {
		return fighting;
	}

	/**
	 * Sets the fighting
	 * 
	 * @param fighting
	 *            the fighting
	 */
	public void setFighting(NPC fighting) {
		this.fighting = fighting;
	}

	/**
	 * Sets the time
	 *
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Sets the time
	 * 
	 * @param time
	 *            the time
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * Represents a kill tracker
	 */
	public static class KillTrack {

		/**
		 * The npc id
		 */
		private int id;

		/**
		 * The npc id
		 */
		private String name;

		/**
		 * The amount killed
		 */
		private int amount;

		/**
		 * The amount killed in the current streak
		 */
		private int streakAmount;

		/**
		 * The fastest kill
		 */
		private long record;

		/**
		 * The drop KC
		 */
		private int dropRecord;

		/**
		 * The rare drops
		 */
		private Item[] rareDrops;

		/**
		 * Represents a single npc tracking
		 * 
		 * @param id
		 *            the npc id
		 * @param name
		 *            the name
		 * @param amount
		 *            the amount
		 * @param record
		 *            the record
		 * @param rareDrops
		 *            the rare drops
		 */
		KillTrack(int id, String name, int amount, int streakAmount, int dropRecord, long record, Item[] rareDrops) {
			this.setId(id);
			this.setName(name);
			this.setAmount(amount);
			this.setStreakAmount(streakAmount);
			this.setDropKC(dropRecord);
			this.setRecord(record);
			this.setRareDrops(new Item[] {});
		}

		/**
		 * Gets the id
		 *
		 * @return the id
		 */
		public int getId() {
			return id;
		}

		/**
		 * Sets the id
		 * 
		 * @param id
		 *            the id
		 */
		public void setId(int id) {
			this.id = id;
		}

		/**
		 * Sets the name
		 *
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Sets the name
		 * 
		 * @param name
		 *            the name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Gets the amount
		 *
		 * @return the amount
		 */
		public int getAmount() {
			return amount;
		}

		/**
		 * Sets the amount
		 * 
		 * @param amount
		 *            the amount
		 */
		public void setAmount(int amount) {
			this.amount = amount;
		}

		/**
		 * Gets the streakAmount
		 *
		 * @return the streakAmount
		 */
		public int getStreakAmount() {
			return streakAmount;
		}

		/**
		 * Sets the streakAmount
		 *
		 * @param streakAmount
		 *            the amount
		 */
		public void setStreakAmount(int streakAmount) {
			this.streakAmount = streakAmount;
		}

		/**
		 * Sets the drop record
		 *
		 * @return the drop record
		 */
		public int getDropKC() {
			return dropRecord;
		}

		/**
		 * Sets the drop record
		 *
		 * @param dropRecord
		 *            the record
		 */
		public void setDropKC(int dropRecord) {
			this.dropRecord = dropRecord;
		}

		/**
		 * Sets the record
		 *
		 * @return the record
		 */
		public long getRecord() {
			return record;
		}

		/**
		 * Sets the record
		 * 
		 * @param record
		 *            the record
		 */
		public void setRecord(long record) {
			this.record = record;
		}

		/**
		 * Sets the rareDrops
		 *
		 * @return the rareDrops
		 */
		public Item[] getRareDrops() {
			return rareDrops;
		}

		/**
		 * Sets the rareDrops
		 * 
		 * @param rareDrops
		 *            the rareDrops
		 */
		public void setRareDrops(Item[] rareDrops) {
			this.rareDrops = rareDrops;
		}
	}
}