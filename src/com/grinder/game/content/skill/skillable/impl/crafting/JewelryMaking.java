package com.grinder.game.content.skill.skillable.impl.crafting;

import java.util.*;

import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.ItemCreationSkillable;
import com.grinder.game.content.skill.skillable.impl.fletching.FletchableItem;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.AnimationLoop;
import com.grinder.game.model.interfaces.menu.CreationMenu;
import com.grinder.game.model.interfaces.menu.impl.QuardrupleItemCreationMenu;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.Skill;
import com.grinder.game.model.item.RequiredItem;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.model.sound.SoundLoop;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;

import static com.grinder.util.ItemID.KNIFE;

/**
 * A class that handles jewelry making.
 * 
 * @author Blake
 *
 */
public class JewelryMaking {
	

	/**
	 * The jewelry making interface id.
	 */
	private static final int INTERFACE_ID = 58000;
	
	/**
	 * The animation of the action.
	 */
	private static final Animation animation = new Animation(899);

	/**
	 * The sound of the action.
	 */
	private static Sound sound = new Sound(2725);
	
	/**
	 * Opens the jewelry making interface.
	 * 
	 * @param player
	 *            the player
	 */
	public static void openInterface(Player player) {
		player.getPacketSender().sendInterfaceItems(58018, getItems(player, ItemID.RING_MOULD));
		player.getPacketSender().sendInterfaceItems(58019, getItems(player, ItemID.NECKLACE_MOULD));
		player.getPacketSender().sendInterfaceItems(58020, getItems(player, ItemID.AMULET_MOULD));
		player.getPacketSender().sendInterfaceItems(58021, getItems(player, ItemID.BRACELET_MOULD));
		player.getPacketSender().sendInterface(INTERFACE_ID);
	}

	public static void openSilverInterface(Player player) {
		Optional<CreationMenu> menu = Optional.empty();
		List<RequiredItem> requiredItemList = new LinkedList<>();

		CreationMenu.CreationMenuAction action = (index, item, amount) -> {
			Jewelry jewelry = Jewelry.forReward(item);
			if (jewelry != null) {
				SkillUtil.startSkillable(
						player, new ItemCreationSkillable(
								Arrays.asList(jewelry.getRequiredItems()),
								jewelry.getReward(),
								amount,
								new AnimationLoop(animation, 5),
								new SoundLoop(sound, 4),
								jewelry.getRequiredLevel(),
								jewelry.getExperience(),
								Skill.CRAFTING, null, 3));
				player.getPacketSender().sendInterfaceRemoval();
			}
		};

		menu = Optional.of(new QuardrupleItemCreationMenu(player,
				ItemID.UNSTRUNG_SYMBOL, ItemID.UNSTRUNG_EMBLEM,
				ItemID.SILVER_SICKLE, ItemID.TIARA,
				"What would you like to make?", action));

		if (menu.isPresent()) {
			player.setCreationMenu(menu);
			menu.get().open();
		}
	}
	
	/**
	 * Starts the jewelry making action.
	 * 
	 * @param player
	 *            the player
	 * @param item
	 *            the item
	 * @param amount
	 *            the amount
	 */
	public static void start(Player player, int item, int amount) {
		if (player.getInterfaceId() != INTERFACE_ID) {
			return;
		}
		
		Jewelry jewelry = Jewelry.forReward(item);
		
		if (jewelry != null) {
			SkillUtil.startSkillable(
                    player, new ItemCreationSkillable(
							Arrays.asList(jewelry.getRequiredItems()),
							jewelry.getReward(),
							amount,
							new AnimationLoop(animation, 5),
							new SoundLoop(sound , 4),
                            jewelry.getRequiredLevel(),
							jewelry.getExperience(),
							Skill.CRAFTING, null, 3));
			player.getPacketSender().sendInterfaceRemoval();
		}
	}
	
	/**
	 * Gets the item data for the specified mould.
	 * 
	 * @param player
	 *            the player
	 * @param mouldId
	 *            the mould id
	 * @return the items
	 */
	private static List<Item> getItems(Player player, int mouldId) {
		List<Item> items = new ArrayList<>(8);
		
		if (!player.getInventory().contains(mouldId)) {
			return items;
		}
		
		for (Jewelry j : Jewelry.values()) {
			if (j.getMould() != mouldId) {
				continue;
			}
			
			if (j.getRequiredLevel() > player.getSkillManager().getCurrentLevel(Skill.CRAFTING)) {
				continue;
			}

			boolean hasItems = true;
			
			for (int i = 0; i < j.getRequiredItems().length; i++) {
				if (!player.getInventory().contains(j.getRequiredItems()[i].getItem())) {
					hasItems = false;
					break;
				}
			}
			
			items.add(hasItems ? j.getReward() : new Item(-1));
		}
		
		return items;
	}
	
}
