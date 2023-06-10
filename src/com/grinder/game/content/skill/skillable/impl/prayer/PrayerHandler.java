package com.grinder.game.content.skill.skillable.impl.prayer;

import com.grinder.game.content.dueling.DuelRule;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.special.melee.DragonScimitarSpecialAttack;
import com.grinder.game.entity.agent.combat.attack.special.melee.RoseWhipSpecialAttack;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.death.ItemsKeptOnDeath;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.game.model.ButtonActions;
import com.grinder.game.model.Skill;
import com.grinder.game.model.SkullType;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.sound.Sounds;
import com.grinder.util.Misc;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * All of the prayers that can be activated and deactivated. This currently only
 * has support for prayers present in the <b>317 protocol</b>.
 *
 * @author Swiffy
 */
public class PrayerHandler {

	static {
		for (PrayerType prayerType: PrayerType.values()){
			ButtonActions.INSTANCE.onClick(prayerType.buttonId, clickAction -> {
				final Player player = clickAction.getPlayer();
				if (!player.hasActivePrayer(prayerType.ordinal())){
					activatePrayer(player, prayerType.ordinal());
				} else {
					deactivatePrayer(player, prayerType.ordinal());
					player.getPacketSender().sendSound(Sounds.PRAYER_TURNED_OFF);
				}
			});
		}
	}

	/**
	 * The items kept on death interface id
	 */
	public static final int ITEMS_KEPT_ONDEATH_SCREEN_INTERFACE_ID = 17100;

	public static final int THICK_SKIN = 0, BURST_OF_STRENGTH = 1, CLARITY_OF_THOUGHT = 2, SHARP_EYE = 3,
			MYSTIC_WILL = 4, ROCK_SKIN = 5, SUPERHUMAN_STRENGTH = 6, IMPROVED_REFLEXES = 7, RAPID_RESTORE = 8,
			RAPID_HEAL = 9, PROTECT_ITEM = 10, HAWK_EYE = 11, MYSTIC_LORE = 12, STEEL_SKIN = 13, ULTIMATE_STRENGTH = 14,
			INCREDIBLE_REFLEXES = 15, PROTECT_FROM_MAGIC = 16, PROTECT_FROM_MISSILES = 17, PROTECT_FROM_MELEE = 18,
			EAGLE_EYE = 19, MYSTIC_MIGHT = 20, RETRIBUTION = 21, REDEMPTION = 22, SMITE = 23, PRESERVE = 24,
			CHIVALRY = 25, PIETY = 26, RIGOUR = 27, AUGURY = 28;
	/**
	 * Contains every prayer that counts as a defense prayer.
	 */
	public static final int[] DEFENCE_PRAYERS = { THICK_SKIN, ROCK_SKIN, STEEL_SKIN, CHIVALRY, PIETY, RIGOUR, AUGURY };
	/**
	 * Contains every prayer that counts as a strength prayer.
	 */
	public static final int[] STRENGTH_PRAYERS = { BURST_OF_STRENGTH, SUPERHUMAN_STRENGTH, ULTIMATE_STRENGTH, CHIVALRY,
			PIETY };
	/**
	 * Contains every prayer that counts as an attack prayer.
	 */
	public static final int[] ATTACK_PRAYERS = { CLARITY_OF_THOUGHT, IMPROVED_REFLEXES, INCREDIBLE_REFLEXES, CHIVALRY,
			PIETY };
	/**
	 * Contains every prayer that counts as a ranged prayer.
	 */
	public static final int[] RANGED_PRAYERS = { SHARP_EYE, HAWK_EYE, EAGLE_EYE, RIGOUR };
	/**
	 * Contains every prayer that counts as a magic prayer.
	 */
	public static final int[] MAGIC_PRAYERS = { MYSTIC_WILL, MYSTIC_LORE, MYSTIC_MIGHT, AUGURY };
	/**
	 * Contains every prayer that counts as an overhead prayer, excluding
	 * protect from summoning.
	 */
	public static final int[] OVERHEAD_PRAYERS = { PROTECT_FROM_MAGIC, PROTECT_FROM_MISSILES, PROTECT_FROM_MELEE,
			RETRIBUTION, REDEMPTION, SMITE };
	/**
	 * Contains every protection prayer
	 */
	public static final int[] PROTECTION_PRAYERS = { PROTECT_FROM_MAGIC, PROTECT_FROM_MISSILES, PROTECT_FROM_MELEE };

	/**
	 * Gets the protecting prayer based on the argued combat type.
	 *
	 * @param type
	 *            the combat type.
	 * @return the protecting prayer.
	 */
	public static int getProtectingPrayer(AttackType type) {
		switch (type) {
		case MELEE:
			return PROTECT_FROM_MELEE;
		case MAGIC:
			return PROTECT_FROM_MAGIC;
		case RANGED:
			return PROTECT_FROM_MISSILES;
		case SPECIAL:
			return PROTECT_FROM_MAGIC;
		default:
			throw new IllegalArgumentException("Invalid combat type: " + type);
		}
	}

	public static boolean hasProtectionPrayer(Player p, AttackType type) {
		int prayer = getProtectingPrayer(type);
		return PrayerHandler.isActivated(p, prayer);
	}

	public static boolean isActivated(Agent c, int prayer) {
		return c.getPrayerActive()[prayer];
	}

	/**
	 * Activates said prayer with specified <code>prayerId</code> and
	 * de-activates all non-stackable prayers.
	 *
	 * @param agent
	 *            The player activating prayer.
	 * @param prayerId
	 *            The id of the prayer being turned on, also known as the
	 *            ordinal in the respective enum.
	 */
	public static void activatePrayer(Agent agent, final int prayerId) {

		// Get the prayer data.
		PrayerType pd = PrayerType.prayerData.get(prayerId);

		// Check if it's available
		if (pd == null) {
			return;
		}

		if(pd.isProtectionPrayer()){
			if(!DragonScimitarSpecialAttack.Companion.canUseProtectionPrayer(agent)){
				return;
			}
		}

		if(pd == PrayerType.PROTECT_ITEM){
			if(!RoseWhipSpecialAttack.Companion.canUseProtectItemPrayer(agent)){
				return;
			}
		}

		if (agent.isPlayer()) {
			Player p = agent.getAsPlayer();
			if (p.getMinigame() != null) {
				if (p.getMinigame().getUnuseablePrayer() != null) {
					for (int i = 0; i < p.getMinigame().getUnuseablePrayer().length; i++) {
						if (p.getMinigame().getUnuseablePrayer()[i] == prayerId) {
							deactivatePrayer(p, prayerId);
							return;
						}
					}
				}
			}
		}

		// Check if we're already praying this prayer.
		if (agent.getPrayerActive()[prayerId]) {

			// If we are an npc, make sure our headicon
			// is up to speed.
			if (agent.isNpc()) {
				NPC npc = agent.getAsNpc();
				if (pd.hint != -1) {
					int hintId = getHeadHint(agent);
					if (npc.getHeadIcon() != hintId) {
						npc.setHeadIcon(hintId);
					}
				}
			}

			return;
		}

		// If we're a player, make sure we can use this prayer.
		if (agent.isPlayer()) {
			Player player = agent.getAsPlayer();
			if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) <= 0) {
				player.getPacketSender().sendConfig(pd.configId, 0);
				player.getPacketSender().sendMessage("You do not have enough Prayer points.", 1000);
				player.getPacketSender().sendSound(Sounds.PRAYER_UNAVAILABLE_SOUND);
				return;
			}
			if (!canUse(player, pd, true)) {
				return;
			}
		}

		switch (prayerId) {
		case THICK_SKIN:
		case ROCK_SKIN:
		case STEEL_SKIN:
			resetPrayers(agent, DEFENCE_PRAYERS, prayerId);
			break;
		case BURST_OF_STRENGTH:
		case SUPERHUMAN_STRENGTH:
		case ULTIMATE_STRENGTH:
			resetPrayers(agent, STRENGTH_PRAYERS, prayerId);
			resetPrayers(agent, RANGED_PRAYERS, prayerId);
			resetPrayers(agent, MAGIC_PRAYERS, prayerId);
			break;
		case CLARITY_OF_THOUGHT:
		case IMPROVED_REFLEXES:
		case INCREDIBLE_REFLEXES:
			resetPrayers(agent, ATTACK_PRAYERS, prayerId);
			resetPrayers(agent, RANGED_PRAYERS, prayerId);
			resetPrayers(agent, MAGIC_PRAYERS, prayerId);
			break;
		case SHARP_EYE:
		case HAWK_EYE:
		case EAGLE_EYE:
		case MYSTIC_WILL:
		case MYSTIC_LORE:
		case MYSTIC_MIGHT:
			resetPrayers(agent, STRENGTH_PRAYERS, prayerId);
			resetPrayers(agent, ATTACK_PRAYERS, prayerId);
			resetPrayers(agent, RANGED_PRAYERS, prayerId);
			resetPrayers(agent, MAGIC_PRAYERS, prayerId);
			break;
		case CHIVALRY:
		case PIETY:
		case RIGOUR:
		case AUGURY:
			resetPrayers(agent, DEFENCE_PRAYERS, prayerId);
			resetPrayers(agent, STRENGTH_PRAYERS, prayerId);
			resetPrayers(agent, ATTACK_PRAYERS, prayerId);
			resetPrayers(agent, RANGED_PRAYERS, prayerId);
			resetPrayers(agent, MAGIC_PRAYERS, prayerId);
			break;
		case PROTECT_FROM_MAGIC:
		case PROTECT_FROM_MISSILES:
		case PROTECT_FROM_MELEE:
			resetPrayers(agent, OVERHEAD_PRAYERS, prayerId);
			break;
		case RETRIBUTION:
		case REDEMPTION:
		case SMITE:
			resetPrayers(agent, OVERHEAD_PRAYERS, prayerId);
			break;
		}
		agent.setPrayerActive(prayerId, true);
		
		if (agent.isPlayer()) {
			Player p = agent.getAsPlayer();
			p.getPacketSender().sendConfig(pd.configId, 1);

			if (pd.hint != -1) {
				int hintId = getHeadHint(agent);
				p.getAppearance().setHeadHint(hintId);
			}
			
			if (pd.soundId != -1) {
				p.getPacketSender().sendSound(pd.soundId);
			}

			if (p.getInterfaceId() == EquipmentBonuses.INTERFACE_ID) {
				EquipmentBonuses.update(p);
			}

			// Close interfaces..
			if (p.getStatus() == PlayerStatus.NONE) {
				if (prayerId != PROTECT_ITEM)
					p.getPacketSender().sendInterfaceRemoval();
				if (prayerId == PROTECT_ITEM && p.getInterfaceId() != ITEMS_KEPT_ONDEATH_SCREEN_INTERFACE_ID)
					p.getPacketSender().sendInterfaceRemoval();
			}
			// Update items on death interface when you have protect item on.
			if (prayerId == PROTECT_ITEM && p.getInterfaceId() > 0 && p.getInterfaceId() == ITEMS_KEPT_ONDEATH_SCREEN_INTERFACE_ID) {
				ItemsKeptOnDeath.updateInterface(p);
			}
		} else if (agent.isNpc()) {

			NPC npc = agent.getAsNpc();
			if (pd.hint != -1) {
				int hintId = getHeadHint(agent);
				if (npc.getHeadIcon() != hintId) {
					npc.setHeadIcon(hintId);
				}
			}
		}
	}

	/**
	 * Checks if the player can use the specified prayer.
	 *
	 * @param player
	 * @param prayer
	 * @return
	 */
	public static boolean canUse(Player player, PrayerType prayer, boolean msg) {
		if (player.BLOCK_ALL_BUT_TALKING) {
			return false;
		}
		if (player.getSkillManager().getMaxLevel(Skill.PRAYER) < (prayer.requirement)) {
			if (msg) {
				player.getPacketSender().sendConfig(prayer.configId, 0);
				player.getPacketSender().sendMessage("You need a Prayer level of at least " + prayer.requirement + " to use " + prayer.getPrayerName() + ".", 600);
				player.getPacketSender().sendSound(Sounds.PRAYER_UNAVAILABLE_SOUND);
			}
			return false;
		}
		if (prayer == PrayerType.CHIVALRY && player.getSkillManager().getMaxLevel(Skill.DEFENCE) < 60) {
			if (msg) {
				player.getPacketSender().sendConfig(prayer.configId, 0);
				player.getPacketSender().sendMessage("You need a Defence level of at least 60 to use Chivalry.", 1000);
				player.getPacketSender().sendSound(Sounds.PRAYER_UNAVAILABLE_SOUND);
			}
			return false;
		}
		if (prayer == PrayerType.PIETY && player.getSkillManager().getMaxLevel(Skill.DEFENCE) < 70) {
			if (msg) {
				player.getPacketSender().sendConfig(prayer.configId, 0);
				player.getPacketSender().sendMessage("You need a Defence level of at least 70 to use Piety.", 1000);
				player.getPacketSender().sendSound(Sounds.PRAYER_UNAVAILABLE_SOUND);
			}
			return false;
		}
		if ((prayer == PrayerType.RIGOUR || prayer == PrayerType.AUGURY)
				&& player.getSkillManager().getMaxLevel(Skill.DEFENCE) < 70) {
			if (msg) {
				player.getPacketSender().sendConfig(prayer.configId, 0);
				player.getPacketSender().sendMessage("You need a Defence level of at least 70 to use that prayer.", 1000);
				player.getPacketSender().sendSound(Sounds.PRAYER_UNAVAILABLE_SOUND);
			}
			return false;
		}
		if (prayer == PrayerType.PROTECT_ITEM) {
			if (player.getGameMode().isUltimate()) {
				DialogueManager.sendStatement(player, "You can't use the Protect Item prayer as an Ultimate Iron Man!");
				player.getPacketSender().sendConfig(prayer.configId, 0);
				player.getPacketSender().sendSound(Sounds.PRAYER_UNAVAILABLE_SOUND);
				return false;
			}
			if ((player.isSkulled() && player.getSkullType() == SkullType.RED_SKULL) || !player.getCombat().getProtectBlockTimer().finished()) {
				if (msg) {
					player.getPacketSender().sendConfig(prayer.configId, 0);
					if (!player.getCombat().getProtectBlockTimer().finished()) {
						player.getPacketSender().sendMessage("You have been disabled and can no longer use protect item prayer.", 1000);
					} else {
					DialogueManager.sendStatement(player, "You can't use the Protect Item prayer with a red skull!");
					}
					player.getPacketSender().sendSound(Sounds.PRAYER_UNAVAILABLE_SOUND);
				}
				return false;
			}
		}
		if (!player.getCombat().getPrayerBlockTimer().finished()) {
			if (prayer == PrayerType.PROTECT_FROM_MELEE || prayer == PrayerType.PROTECT_FROM_MISSILES
					|| prayer == PrayerType.PROTECT_FROM_MAGIC) {
				if (msg) {
					player.getPacketSender().sendConfig(prayer.configId, 0);
					player.getPacketSender().sendMessage("You have been disabled and can no longer use protection prayers.", 1000);
					player.getPacketSender().sendSound(Sounds.PRAYER_UNAVAILABLE_SOUND);
				}
				return false;
			}
		}

		// Prayer locks
		boolean locked = false;

		if (prayer == PrayerType.PRESERVE && !player.isPreserveUnlocked()
				|| prayer == PrayerType.RIGOUR && !player.isRigourUnlocked()
				|| prayer == PrayerType.AUGURY && !player.isAuguryUnlocked()) {
			if (player.getRights() != PlayerRights.OWNER && player.getRights() != PlayerRights.DEVELOPER) {
				locked = true;
			}
		}

		if (locked) {
			if (msg) {
				player.getPacketSender().sendMessage("You have not unlocked that Prayer yet.", 1000);
				player.getPacketSender().sendSound(Sounds.PRAYER_UNAVAILABLE_SOUND);
			}
			return false;
		}

		// Duel, disabled prayer?
		if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_PRAYER.ordinal()]) {
			if (msg) {
				DialogueManager.sendStatement(player, "Prayer has been disabled in this duel!");
				player.getPacketSender().sendConfig(prayer.configId, 0);
				player.getPacketSender().sendSound(Sounds.PRAYER_UNAVAILABLE_SOUND);
			}
			return false;
		}

		return true;
	}

	/**
	 * Deactivates said prayer with specified <code>prayerId</code>.
	 *
	 * @param c
	 *            The player deactivating prayer.
	 * @param prayerId
	 *            The id of the prayer being deactivated.
	 */
	public static void deactivatePrayer(Agent c, int prayerId) {
		PrayerType pd = PrayerType.prayerData.get(prayerId);

		if (pd == null) {
			return;
		}
		/*if (c.isPlayer()) {
			if (c.getAsPlayer().BLOCK_ALL_BUT_TALKING)
				return;
		}*/
		c.setPrayerActive(prayerId, false);

		if (c.isPlayer()) {
			Player p = c.getAsPlayer();
			p.getPacketSender().sendConfig(pd.configId, 0);
			if (pd.hint != -1) {
				int hintId = getHeadHint(c);
				p.getAppearance().setHeadHint(hintId);
			}

			p.getQuickPrayers().checkActive();
			EquipmentBonuses.update(p);
			//if (prayerId == PROTECT_ITEM) {
			//	p.sendMessage("@red@Protection prayer: "+p.getPrayerActive()[prayerId]);
			//}

			// Close interfaces..
			if (p.getStatus() == PlayerStatus.NONE) {
				if (prayerId != PROTECT_ITEM)
					p.getPacketSender().sendInterfaceRemoval();
				if (prayerId == PROTECT_ITEM && p.getInterfaceId() != ITEMS_KEPT_ONDEATH_SCREEN_INTERFACE_ID)
					p.getPacketSender().sendInterfaceRemoval();
			}
			// Update items on death interface when you have protect item on.
			if (prayerId == PROTECT_ITEM && p.getInterfaceId() > 0 && p.getInterfaceId() == ITEMS_KEPT_ONDEATH_SCREEN_INTERFACE_ID) {
				ItemsKeptOnDeath.updateInterface(p);
			}
		} else if (c.isNpc()) {
			if (pd.hint != -1) {
				int hintId = getHeadHint(c);
				if (c.getAsNpc().getHeadIcon() != hintId) {
					c.getAsNpc().setHeadIcon(hintId);
				}
			}
		}
		// Sounds.sendSound(player, Sound.DEACTIVATE_PRAYER_OR_CURSE);
	}

	/**
	 * Deactivates every prayer in the player's prayer book.
	 *
	 * @param player
	 *            The player to deactivate prayers for.
	 */
	public static void deactivatePrayers(Agent agent) {
		for (int i = 0; i < agent.getPrayerActive().length; i++) {
			deactivatePrayer(agent, i);
		}
		if (agent.isPlayer()) {
			agent.getAsPlayer().getQuickPrayers().setEnabled(false);
			agent.getAsPlayer().getPacketSender().sendQuickPrayersState(false);
		} else if (agent.isNpc()) {
			if (agent.getAsNpc().getHeadIcon() != -1) {
				agent.getAsNpc().setHeadIcon(-1);
			}
		}
	}

	public static void resetAll(Player player) {
		for (int i = 0; i < player.getPrayerActive().length; i++) {
			PrayerType pd = PrayerType.prayerData.get(i);
			if (pd == null)
				continue;
			player.setPrayerActive(i, false);
			player.getPacketSender().sendConfig(pd.configId, 0);
			if (pd.hint != -1) {
				int hintId = getHeadHint(player);
				player.getAppearance().setHeadHint(hintId);
			}
		}
		player.getQuickPrayers().setEnabled(false);
		player.getPacketSender().sendQuickPrayersState(false);
	}

	/**
	 * Gets the player's current head hint if they activate or deactivate a head
	 * prayer.
	 *
	 * @param player
	 *            The player to fetch head hint index for.
	 * @return The player's current head hint index.
	 */
	private static int getHeadHint(Agent agent) {
		boolean[] prayers = agent.getPrayerActive();
		if (prayers[PROTECT_FROM_MELEE])
			return 0;
		if (prayers[PROTECT_FROM_MISSILES])
			return 1;
		if (prayers[PROTECT_FROM_MAGIC])
			return 2;
		if (prayers[RETRIBUTION])
			return 3;
		if (prayers[SMITE])
			return 4;
		if (prayers[REDEMPTION])
			return 5;
		return -1;
	}

	@NotNull
	public static Set<PrayerType> getActivePrayers(Agent agent){
		final HashSet<PrayerType> prayerTypes = new HashSet<>();
		final boolean[] activePrayers = agent.getPrayerActive();
		for(int i = 0; i < activePrayers.length; i++){
			if(activePrayers[i]){
				final PrayerType prayerType = PrayerType.prayerData.get(i);
				if(prayerType != null)
					prayerTypes.add(prayerType);
			}
		}
		return prayerTypes;
	}

	public static PrayerType getPrayer(int prayerId){
		return PrayerType.prayerData.get(prayerId);
	}

	public static double getEquipmentBonus(Player player){
		double bonus = player.getBonusManager().getPrayerBonus();
		if(EquipmentUtil.hasAnyAmuletOfTheDamned(player)){
			if(EquipmentUtil.isWearingVeracSet(player)){
				bonus += 7;
			}
		}
		return bonus;
	}

	/**
	 * Checks if a player has no prayer on.
	 *
	 * @param player
	 *            The player to check prayer status for.
	 * @param exceptionId
	 *            The prayer id currently being turned on/activated.
	 * @return if <code>true</code>, it means player has no prayer on besides
	 *         <code>exceptionId</code>.
	 */
	private final static boolean hasNoPrayerOn(Player player, int exceptionId) {
		int prayersOn = 0;
		for (int i = 0; i < player.getPrayerActive().length; i++) {
			if (player.getPrayerActive()[i] && i != exceptionId)
				prayersOn++;
		}
		return prayersOn == 0;
	}

	/**
	 * Resets <code> prayers </code> with an exception for
	 * <code> prayerID </code>
	 *
	 * @param prayers
	 *            The array of prayers to reset
	 * @param prayerID
	 *            The prayer ID to not turn off (exception)
	 */
	public static void resetPrayers(Agent c, int[] prayers, int prayerID) {
		for (int i = 0; i < prayers.length; i++) {
			deactivatePrayer(c, prayers[i]);
		}
	}

	/**
	 * Resets prayers in the array
	 *
	 * @param player
	 * @param prayers
	 */
	public static void resetPrayers(Player player, int[] prayers) {
		for (int i = 0; i < prayers.length; i++) {
			deactivatePrayer(player, prayers[i]);
		}
	}

	/**
	 * Checks if action button ID is a prayer button.
	 *
	 * @param buttonId
	 *            action button being hit.
	 */
	public static final boolean negateButton(final int actionButtonID) {
		return PrayerType.actionButton.containsKey(actionButtonID);
	}

	/**
	 * Represents a prayer's configurations, such as their level requirement,
	 * buttonId, configId and drain rate.
	 *
	 * @author relex lawl
	 */
	public enum PrayerType {
		THICK_SKIN(1, 1, 5609, 83, 2690),

		BURST_OF_STRENGTH(4, 1, 5610, 84, 2688),

		CLARITY_OF_THOUGHT(7, 1, 5611, 85, 2664),

		SHARP_EYE(8, 1, 19812, 700, 2685),

		MYSTIC_WILL(9, 1, 19814, 701, 2670),

		ROCK_SKIN(10, 2, 5612, 86, 2684),

		SUPERHUMAN_STRENGTH(13, 1.5, 5613, 87, 2689),

		IMPROVED_REFLEXES(16, 1.5, 5614, 88, 2662),

		RAPID_RESTORE(19, .4, 5615, 89, 2679),

		RAPID_HEAL(22, .6, 5616, 90, 2678),

		PROTECT_ITEM(25, .6, 5617, 91, 1982),

		HAWK_EYE(26, 1.5, 19816, 702, 2666),

		MYSTIC_LORE(27, 1.5, 19818, 703, 2668),

		STEEL_SKIN(28, 2.5, 5618, 92, 2687),

		ULTIMATE_STRENGTH(31, 2.8, 5619, 93, 2691),

		INCREDIBLE_REFLEXES(34, 2.8, 5620, 94, 2667),

		PROTECT_FROM_MAGIC(37, 2.8, 5621, 95, 2675, 2),

		PROTECT_FROM_MISSILES(40, 2.8, 5622, 96, 2677, 1),

		PROTECT_FROM_MELEE(43, 2.8, 5623, 97, 2676, 0),

		EAGLE_EYE(44, 3, 19821, 704, 2665),

		MYSTIC_MIGHT(45, 3, 19823, 705, 2669),

		RETRIBUTION(46, 1, 683, 98, 2682, 4),

		REDEMPTION(49, 2, 684, 99, 2680, 5),

		SMITE(52, 5, 685, 100, 2686, 100, 6),

		PRESERVE(55, 1, 28001, 708, 3817),

		CHIVALRY(60, 5, 19825, 706, 3826),

		PIETY(70, 6, 19827, 707, 3825),

		RIGOUR(74, 6.4, 28004, 710, 560),

		AUGURY(77, 6.1, 28007, 712, 3987);

		/**
		 * Contains the PrayerData with their corresponding prayerId.
		 */
		private static HashMap<Integer, PrayerType> prayerData = new HashMap<Integer, PrayerType>();
		/**
		 * Contains the PrayerData with their corresponding buttonId.
		 */
		private static HashMap<Integer, PrayerType> actionButton = new HashMap<Integer, PrayerType>();

		/**
		 * Populates the prayerId and buttonId maps.
		 */
		static {
			for (PrayerType pd : PrayerType.values()) {
				prayerData.put(pd.ordinal(), pd);
				actionButton.put(pd.buttonId, pd);
			}
		}

		/**
		 * The prayer's level requirement for player to be able to activate it.
		 */
		private int requirement;
		/**
		 * The prayer's action button id in prayer tab.
		 */
		private int buttonId;
		/**
		 * The prayer's config id to switch their glow on/off by sending the
		 * sendConfig packet.
		 */
		private int configId;
		/**
		 * The prayer's drain rate as which it will drain the associated
		 * player's prayer points.
		 */
		private double drainRate;
		/**
		 * The prayer's head icon hint index.
		 */
		private int hint = -1;
		/**
		 * The sound id.
		 */
		private int soundId;
		/**
		 * The prayer's formatted name.
		 */
		private String name;

		private PrayerType(int requirement, double drainRate, int buttonId, int configId, int soundId, int... hint) {
			this.requirement = requirement;
			this.drainRate = drainRate;
			this.buttonId = buttonId;
			this.configId = configId;
			this.soundId = soundId;
			if (hint.length > 0)
				this.hint = hint[0];
		}

		/**
		 * Gets the prayer's formatted name.
		 *
		 * @return The prayer's name
		 */
		private final String getPrayerName() {
			if (name == null)
				return Misc.capitalizeWords(toString().toLowerCase().replaceAll("_", " "));
			return name;
		}


		public boolean isProtectionPrayer(){
			switch (this){
				case PROTECT_FROM_MAGIC:
				case PROTECT_FROM_MELEE:
				case PROTECT_FROM_MISSILES:
					return true;
			}
			return false;
		}
		/**
		 * https://oldschool.runescape.wiki/w/Prayer#Prayers
		 *
		 * @return the drain rate of prayer points per tick.
		 */
		public double getDrainInterval(){
			switch (this){
				case CHIVALRY:
				case PIETY:
				case RIGOUR:
				case AUGURY:
					// 1 point per 1.5 seconds, 1/(1500/600) = 0.3
					return 1.5;
				case SMITE:
					// 1 point per 2 seconds, 1/(2000/600) = 0.3
					return 2;
				case STEEL_SKIN:
				case ULTIMATE_STRENGTH:
				case INCREDIBLE_REFLEXES:
				case PROTECT_FROM_MAGIC:
				case PROTECT_FROM_MISSILES:
				case PROTECT_FROM_MELEE:
				case EAGLE_EYE:
				case MYSTIC_MIGHT:
					// 1 point per 3 seconds, 1/(3000/600) = 0.2
					return 3;
				case ROCK_SKIN:
				case SUPERHUMAN_STRENGTH:
				case IMPROVED_REFLEXES:
				case HAWK_EYE:
				case MYSTIC_LORE:
				case REDEMPTION:
					// 1 point per 6 seconds, 1/(6000/600) = 0.1
					return 6;
				case THICK_SKIN:
				case BURST_OF_STRENGTH:
				case CLARITY_OF_THOUGHT:
				case SHARP_EYE:
				case MYSTIC_WILL:
				case RETRIBUTION:
					// 1 point per 12 seconds, 1/(12000/600) = 0.05
					return 12;
				case RAPID_HEAL:
				case PROTECT_ITEM:
				case PRESERVE:
					// 1 point per 18 seconds, 1/(18000/600) = 0.03333333333
					return 18;
				case RAPID_RESTORE:
					// 1 point per 36 seconds, 1/(36000/600) = 0.01666666667
					return 36;
				default:
					System.err.println("Drain rate was not specified for prayer "+name());
					return 0.0;
			}
		}

		public int getDrainEffect(){
			switch (this){
				case CHIVALRY:
				case PIETY:
				case RIGOUR:
				case AUGURY:
					return 24;
				case SMITE:
					return 18;
				case STEEL_SKIN:
				case ULTIMATE_STRENGTH:
				case INCREDIBLE_REFLEXES:
				case PROTECT_FROM_MAGIC:
				case PROTECT_FROM_MISSILES:
				case PROTECT_FROM_MELEE:
				case EAGLE_EYE:
				case MYSTIC_MIGHT:
					return 12;
				case ROCK_SKIN:
				case SUPERHUMAN_STRENGTH:
				case IMPROVED_REFLEXES:
				case HAWK_EYE:
				case MYSTIC_LORE:
				case REDEMPTION:
					return 6;
				case THICK_SKIN:
				case BURST_OF_STRENGTH:
				case CLARITY_OF_THOUGHT:
				case SHARP_EYE:
				case MYSTIC_WILL:
				case RETRIBUTION:
					return 3;
				case RAPID_HEAL:
				case PROTECT_ITEM:
				case PRESERVE:
					return 2;
				case RAPID_RESTORE:
					return 1;
				default:
					System.err.println("Drain rate was not specified for prayer "+name());
					return 0;
			}
		}
	}
}
