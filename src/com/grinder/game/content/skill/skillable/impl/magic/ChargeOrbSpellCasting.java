package com.grinder.game.content.skill.skillable.impl.magic;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.impl.Woodcutting;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.Skill;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.util.ItemID;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class ChargeOrbSpellCasting {

	/**
	 * A hash collection of the {@link ChargeOrbSpellType}'s.
	 */
	private static final Map<Integer, ChargeOrbSpellType> enchantSpells = new HashMap<>();

	/**
	 * A hash collection of the {@link ChargeOrb}'s.
	 */
	static final Map<Integer, ChargeOrb> enchantItems = new HashMap<>();

	static {
		Arrays.stream(ChargeOrb.values()).forEach(enc -> enchantItems.put(enc.getSpellId(), enc));
	}

	/**
	 * Handles the enchanting of an item.
	 * 
	 * @param player
	 *            the player
	 * @param spellId
	 *            the spell id
	 * @return <code>true</code> if handled
	 */
	public static boolean enchant(Player player, int spellId) {

		final Optional<ChargeOrb> optionalChargeOrb = ChargeOrb.forId(spellId);
		System.out.println(spellId);
		if (optionalChargeOrb.isEmpty()) {
			player.getPacketSender().sendMessage("Nothing interesting happens.", 1000);
			return false;
		}

		final ChargeOrb chargeOrb = optionalChargeOrb.get();
		final ChargeOrbSpellType spellType = enchantSpells.get(spellId);

		SkillUtil.startSkillable(player, new ChargeOrbSkillable(chargeOrb.getRunes(), chargeOrb.getExperience(), chargeOrb.getEnchantedItem(), new Graphic(chargeOrb.getGFX()), chargeOrb.getEnchantLevel()));

		return true;
	}
	

}
