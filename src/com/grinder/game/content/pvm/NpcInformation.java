package com.grinder.game.content.pvm;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.grinder.game.content.pvm.BossDropTables;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.definition.NpcDropDefinition;
import com.grinder.game.definition.NpcDropDefinition.NPCDrop;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.interfaces.syntax.impl.NPCDropFinderSyntax;

/**
 * Handles presenting information
 * 
 * @author 2012
 *
 */
public class NpcInformation {

	/**
	 * The drop definition
	 */
	private NpcDropDefinition dropDefinition;

	/**
	 * Sends enter input
	 * 
	 * @param player
	 *            the player
	 */
	public static void sendInput(Player player) {
		player.setEnterSyntax(new NPCDropFinderSyntax());
		player.getPacketSender().sendEnterInputPrompt("Enter NPC name to search for drops..");
	}
	
	public static boolean display(Player player, int npcId, int combatLevel) {

		PlayerExtKt.resetInteractions(player, true, true);

		SkillUtil.stopSkillable(player);
		/*
		 * The definition
		 */
		NpcDefinition def = NpcDefinition.forId(npcId);
		
		if(def == null) {
			return false;
		}
		/*
		 * The drops
		 */
		Optional<NpcDropDefinition> drops = NpcDropDefinition.get(npcId);
		
/*		if(!drops.isPresent()) {
			if (combatLevel <= 0) {
				return false;
			}
			player.getPacketSender().sendMessage("This NPC doesn't drop any items.", 1000);
			return false;
		}*/
		display(player, def, drops);
		return true;
	}

	/**
	 * Displaying npc information
	 * 
	 * @param player
	 *            the player
	 * @param def
	 *            the npc
	 */
	public static void display(Player player, NpcDefinition def, Optional<NpcDropDefinition> drops) {
		/*
		 * The npc definition
		 */
		if (def == null) {
			return;
		}
		/*
		 * No drops
		 */
		if (drops.isEmpty()) {
			player.getPacketSender().sendMessage("This NPC doesn't appear to have a drop table. Contact a moderator if this is a bug.");
			return;
		}
		player.getNpcInfo().setDropDefinition(drops.get());
		/*
		 * The information
		 */
		player.getPacketSender().sendString(43004, "@or2@" + def.getName());
		player.getPacketSender().sendString(43005, "@or2@Retreats: @or1@" + (def.doesRetreat() ? "Yes" : "No"));
		player.getPacketSender().sendString(43006, "@or2@Aggressive: @or1@" + (def.isAggressive() ? "Yes" : "No"));
		player.getPacketSender().sendString(43007, "@or2@Poisonous: @or1@" + (def.isPoisonous() ? "Yes" : "No"));
		player.getPacketSender().sendString(43008, "@or2@Max hit: @or1@" + def.getMaxHit());
		player.getPacketSender().sendString(43009, "@or2@Hitpoints: @or1@" + def.getHitpoints());
		player.getPacketSender().sendString(43010, "@or2@Slayer requirement: @or1@" + (def.getSlayerLevel() <= 1 ? "None" : def.getSlayerLevel()));
		player.getPacketSender().sendString(43011, "@or2@Respawn time: @or1@" + def.getRespawn());
		player.getPacketSender().sendString(43014, "");
		/*
		 * The always drop
		 */
		List<Item> always = new LinkedList<>();
		/*
		 * Lists the always
		 */
		if (drops.get().getAlwaysDrops() != null) {
			for (NPCDrop drop : drops.get().getAlwaysDrops()) {
				if (drop != null)
					always.add(drop.toItem());
			}
		}
		/*
		 * The other drops
		 */
		List<Item> other = new LinkedList<>();
		/*
		 * Common drops
		 */
		if (drops.get().getCommonDrops() != null) {
			for (NPCDrop drop : drops.get().getCommonDrops()) {
				if (drop != null)
					other.add(drop.toItem());
			}
		}
		/*
		 * Uncommon drops
		 */
		if (drops.get().getUncommonDrops() != null) {
			for (NPCDrop drop : drops.get().getUncommonDrops()) {
				if (drop != null)
					other.add(drop.toItem());
			}
		}
		/*
		 * Rare drops
		 */
		if (drops.get().getRareDrops() != null) {
			for (NPCDrop drop : drops.get().getRareDrops()) {
				if (drop != null)
					other.add(drop.toItem());
			}
		}
		/*
		 * Very rare drops
		 */
		if (drops.get().getVeryRareDrops() != null) {
			for (NPCDrop drop : drops.get().getVeryRareDrops()) {
				if (drop != null)
					other.add(drop.toItem());
			}
		}
		/*
		 * Special drops
		 */
		if (drops.get().getSpecialDrops() != null) {
			for (NPCDrop drop : drops.get().getSpecialDrops()) {
				if (drop != null)
					other.add(drop.toItem());
			}
		}

		for (int i = 0; i < 62; i++) {
			player.getPacketSender().sendItemOnInterface(43023 + i, -1, -1);
		}
		for (int i = 0; i < 6; i++) {
			player.getPacketSender().sendItemOnInterface(43016 + i, -1, -1);
		}

		for (int i = 0; i < other.size(); i++) {
			player.getPacketSender().sendItemOnInterface(43023 + i, other.get(i).getId(), other.get(i).getAmount());
		}
		player.getPacketSender().sendInterfaceScrollReset(43022);
		player.getPacketSender().sendScrollbarHeight(43022, Math.max(194, (int) Math.ceil(other.size() / 7.0) * 38 + 5));
		/*
		 * Display always
		 */
		for (int i = 0; i < always.size(); i++) {
			player.getPacketSender().sendItemOnInterface(43016 + i, always.get(i).getId(), always.get(i).getAmount());
		}
		//player.getPacketSender().sendMessage("displaynpc:" + def.getId());
		player.getPacketSender().sendInterface(43000);
	}

	/**
	 * Checking drop rate
	 * 
	 * @param player
	 *            the player
	 * @param item
	 *            the item
	 */
	public static void checkDropRate(Player player, int interfaceId, Item item) {
		int textId = interfaceId == BossDropTables.ITEM_CONTAINER_ID ? BossDropTables.CHECK_DROP_RATE_TEXT_ID : 43014;

		/*
		 * No definition
		 */
		if (player.getNpcInfo().getDropDefinition() == null) {
			return;
		}
		/*
		 * The drops
		 */
		NpcDropDefinition drops = player.getNpcInfo().getDropDefinition();

		/*
		 * Always drops (only for bosses interface)
		 */
		if (interfaceId == BossDropTables.ITEM_CONTAINER_ID && drops.getAlwaysDrops() != null) {
			for (NPCDrop drop : drops.getAlwaysDrops()) {
				if (drop.getItemId() == item.getId()) {
					player.getPacketSender().sendString(textId, item.getDefinition().getName() + " - @gre@Always");
					return;
				}
			}
		}
		/*
		 * Common drops
		 */
		if (drops.getCommonDrops() != null) {
			for (NPCDrop drop : drops.getCommonDrops()) {
				if (drop.getItemId() == item.getId()) {
					player.getPacketSender().sendString(textId, item.getDefinition().getName() + " - @gre@1/" + (drop.getChance() == 0 ? "5" : drop.getChance()));
					return;
				}
			}
		}
		/*
		 * Uncommon drops
		 */
		if (drops.getUncommonDrops() != null) {
			for (NPCDrop drop : drops.getUncommonDrops()) {
				if (drop.getItemId() == item.getId()) {
					player.getPacketSender().sendString(textId, item.getDefinition().getName() + " - @yel@1/" + (drop.getChance() == 0 ? "15" : drop.getChance()));
					return;
				}
			}
		}
		/*
		 * Rare drops
		 */
		if (drops.getRareDrops() != null) {
			for (NPCDrop drop : drops.getRareDrops()) {
				if (drop.getItemId() == item.getId()) {
					player.getPacketSender().sendString(textId, item.getDefinition().getName() + " - @or2@1/" + (drop.getChance() == 0 ? "30" : drop.getChance()));
					return;
				}
			}
		}
		/*
		 * Very rare drops
		 */
		if (drops.getVeryRareDrops() != null) {
			for (NPCDrop drop : drops.getVeryRareDrops()) {
				if (drop.getItemId() == item.getId()) {
					player.getPacketSender().sendString(textId, item.getDefinition().getName() + " - @or3@1/" + (drop.getChance() == 0 ? "50" : drop.getChance()));
					return;
				}
			}
		}
		/*
		 * Special drops
		 */
		if (drops.getSpecialDrops() != null) {
			for (NPCDrop drop : drops.getSpecialDrops()) {
				if (drop.getItemId() == item.getId()) {
					player.getPacketSender().sendString(textId, item.getDefinition().getName() + " - @red@1/" + (int) (drop.getChance() / 1.5));
					return;
				}
			}
		}
	}

	/**
	 * Sets the dropDefinition
	 *
	 * @return the dropDefinition
	 */
	public NpcDropDefinition getDropDefinition() {
		return dropDefinition;
	}

	/**
	 * Sets the dropDefinition
	 * 
	 * @param dropDefinition
	 *            the dropDefinition
	 */
	public void setDropDefinition(NpcDropDefinition dropDefinition) {
		this.dropDefinition = dropDefinition;
	}
}
