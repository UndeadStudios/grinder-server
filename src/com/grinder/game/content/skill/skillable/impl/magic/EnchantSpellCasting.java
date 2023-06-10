package com.grinder.game.content.skill.skillable.impl.magic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.grinder.game.content.item.charging.impl.TomeOfFire;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.Skill;
import com.grinder.game.model.item.container.player.Inventory;

/**
 * A class that handles jewelry enchanting.
 * 
 * @author Blake
 *
 */
public class EnchantSpellCasting {

	/**
	 * A hash collection of the {@link EnchantItemSpellType}'s.
	 */
	private static final Map<Integer, EnchantItemSpellType> enchantSpells = new HashMap<>();

	/**
	 * A hash collection of the {@link EnchantableJewelry}'s.
	 */
	static final Map<Integer, EnchantableJewelry> enchantItems = new HashMap<>();

	static {
		Arrays.stream(EnchantItemSpellType.values()).forEach(enc -> enchantSpells.put(enc.getSpellId(), enc));
		Arrays.stream(EnchantableJewelry.values()).forEach(enc -> enchantItems.put(enc.getItemToEnchant().getId(), enc));
	}

	/**
	 * Handles the enchanting of an item.
	 * 
	 * @param player
	 *            the player
	 * @param spellId
	 *            the spell id
	 * @param itemId
	 *            the item id
	 * @return <code>true</code> if handled
	 */
	public static boolean enchant(Player player, int spellId, int itemId) {

		final Optional<EnchantableJewelry> optionalEnchantableJewelry = EnchantableJewelry.forId(itemId);

		if (optionalEnchantableJewelry.isEmpty()) {
        	player.getPacketSender().sendMessage("Nothing interesting happens.", 1000);
			return false;
		}

		final EnchantableJewelry enchantableJewelry = optionalEnchantableJewelry.get();
		final EnchantItemSpellType spellType = enchantSpells.get(spellId);
		final Spell spell = spellType.getSpell();

		if (spellType.getLevel() != enchantableJewelry.getEnchantLevel()) {
			player.getPacketSender().sendMessage("You can only enchant this jewelry using a level-" + spellType + " enchantment spell.", 1000);
			return true;
		}

		if (spell.canCast(player, null, true)) {
			SkillUtil.stopSkillable(player);
			PlayerExtKt.resetInteractions(player, true, false);
			final Inventory inventory = player.getInventory();
			inventory.delete(enchantableJewelry.getItemToEnchant(), false);
			inventory.add(enchantableJewelry.getEnchantedItem(), false);
			inventory.refreshItems();
			player.getPacketSender().sendTab(6);
			player.getSkillManager().addExperience(Skill.MAGIC, spellType.getExperience());
			player.performAnimation(new Animation(enchantableJewelry.getEnchantAnimationId()));
			player.performGraphic(new Graphic(enchantableJewelry.getGFX(), GraphicHeight.HIGH));
			AchievementManager.processFor(AchievementType.JEWELLERY_ENCHANTER, player);
		}
		return true;
	}
	

}
