package com.grinder.game.content.miscellaneous;

import com.grinder.ServerIO;
import com.grinder.game.content.achievement.AchievementDifficulty;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.item.MorphItems;
import com.grinder.game.content.minigame.castlewars.CastleWars;
import com.grinder.game.content.minigame.motherlodemine.sack.SackType;
import com.grinder.game.content.miscellaneous.donating.Store;
import com.grinder.game.content.miscellaneous.voting.Voting;
import com.grinder.game.content.pvm.*;
import com.grinder.game.content.skill.skillable.impl.magic.Teleporting;
import com.grinder.game.content.task_new.PlayerTaskManager;
import com.grinder.game.definition.ItemValueType;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerSettings;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.areas.InstancedBossArea;
import com.grinder.game.model.areas.UntypedInstancedBossArea;
import com.grinder.game.model.areas.godwars.GodChamberArea;
import com.grinder.game.model.areas.godwars.NexChamber;
import com.grinder.game.model.areas.impl.DuelFightArena;
import com.grinder.game.model.areas.impl.KalphiteLair;
import com.grinder.game.model.areas.impl.PublicMinigameLobby;
import com.grinder.game.model.areas.instanced.*;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.commands.impl.OnlineMiddlemenCommand;
import com.grinder.game.model.commands.impl.StaffOnline;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.util.Misc;
import com.grinder.util.timing.TimerKey;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.grinder.util.NpcID.PROSPECTOR_PERCY;

/**
 * Handles quest tab interaction
 *
 * @author 2012
 *
 */
public class QuestTab {
	private static final int AREAS_LINE = 31316;

	private static final int HELP_DESK_BUTTON = 31001;
	private static final int ACHIEVEMENTS_BUTTON = 31002;
	private static final int WILDERNESS_BUTTON = 31003;
	private static final int WILDERNESS_REFRESH = 31318;
	private static final int MEMBER_BUTTON = 31004;
	private static final int STAFF_BUTTON = 31005;
	private static final int INFORMATION_BUTTON = 31006;
	private static final int MEMBER_REFRESH = 31475;
	private static final int INFO_REFRESH = 31806;

	public enum Tab {

		HELP_DESK(HELP_DESK_BUTTON, 31100),
		ACHIEVEMENTS(ACHIEVEMENTS_BUTTON, 80000),
		WILDERNESS(WILDERNESS_BUTTON, 31300),
		MEMBER(MEMBER_BUTTON, 31400),

		STAFF(STAFF_BUTTON, 31500),

		INFORMATION(INFORMATION_BUTTON, 31600),

		QUEST(31007, 65_200),


		;

		private int button;
		private int id;

		Tab(int button, int id) {
			this.setButton(button);
			this.setId(id);
		}

		public int getButton() {
			return button;
		}

		public void setButton(int button) {
			this.button = button;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public static Tab forButton(int button) {
			return Arrays.stream(values()).filter(c -> c.getButton() == button).findFirst().orElse(null);
		}
	}

	/**
	 * Gets the total cost of blood money carried in player's equipment and
	 * inventory.
	 */
	public static long getCarriedWealth(Player player) {
		long cost = 0;
		for (Item item : player.getInventory().getItems()) {
			cost += item.getValue(ItemValueType.PRICE_CHECKER) * item.getAmount();
		}
		for (Item item : player.getEquipment().getItems()) {
			cost += item.getValue(ItemValueType.PRICE_CHECKER) * item.getAmount();
		}
		return cost;
	}

	/**
	 * Refreshes every tab on login
	 *
	 * @param player
	 *            the player
	 */
	public static void onLogin(Player player) {
		for (Tab tab : Tab.values()) {
			refresh(player, tab);
		}
	}

	/**
	 * Refrehes the information in the tab
	 *
	 * @param player
	 *            the player
	 * @param tab
	 *            the tab
	 */
	public static void refresh(Player player, Tab tab) {
		/*
		 * Tab not ready
		 */
		if (tab.getId() == -1) {
			player.getPacketSender().sendMessage("This tab is current under construction.", 1000);
			return;
		}
		switch (tab) {
			case QUEST:
				player.getQuest().tracker.update();
				break;

			case WILDERNESS:

				WildernessBossSpirit.INSTANCE.updateQuestTab(player);
				PorazdirWildernessEvent.INSTANCE.updateQuestTab(player);

				int wildernessPlayerCount = AreaManager.getPlayersInArena(AreaManager.WILD);
				player.getPacketSender().sendString(31324, wildernessPlayerCount + " Player(s)", true);

				break;
			case MEMBER:
				player.getPacketSender().sendString(31412, player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 9 ? getMemberColor(player) : "N/A", true);
				player.getPacketSender().sendString(31414, "$" + player.getAttributes().numInt(Attribute.AMOUNT_PAID), true);
				// Coming soon
				player.getPacketSender().sendString(31421, "", true);
				player.getPacketSender().sendString(31423, "", true);
				player.getPacketSender().sendString(31425, "", true);
				player.getPacketSender().sendString(31427, "", true);
				player.getPacketSender().sendString(31418, "", true);

				player.getPacketSender().sendString(31416, "", true);
				//player.getPacketSender().sendString(31428, "", true);
				player.getPacketSender().sendString(31430, "", true);
				player.getPacketSender().sendString(31432, "", true);
				player.getPacketSender().sendString(31434, "", true);



				player.getPacketSender().sendString(31436, "", true);
				player.getPacketSender().sendString(31438, "", true);
				player.getPacketSender().sendString(31440, "", true);
				player.getPacketSender().sendString(31442, "", true);
				player.getPacketSender().sendString(31444, "", true);
				player.getPacketSender().sendString(31446, "", true);
				player.getPacketSender().sendString(31448, "", true);
				player.getPacketSender().sendString(31450, "", true);
				player.getPacketSender().sendString(31452, "", true);
				player.getPacketSender().sendString(31454, "", true);
				player.getPacketSender().sendString(31456, "", true);
				player.getPacketSender().sendString(31458, "", true);
				player.getPacketSender().sendString(31460, "", true);
				player.getPacketSender().sendString(31462, "", true);
				player.getPacketSender().sendString(31464, "", true);
				player.getPacketSender().sendString(31466, "", true);
				player.getPacketSender().sendString(31468, "", true);
				player.getPacketSender().sendString(31470, "", true);
				player.getPacketSender().sendString(31472, "", true);
				player.getPacketSender().sendString(31474, "", true);
				player.getPacketSender().sendString(31476, "", true);
				break;
			case STAFF:
				break;
			case INFORMATION:

				player.getPacketSender().sendString(31612, PlayerUtil.transformPlayerCount() + " Player(s)", true);
				player.getPacketSender().sendString(31614, ServerIO.getServerOnlineTime());
				player.getPacketSender().sendString(31616, Skill.getBonusSkill().getName());
				player.getPacketSender().sendString(31618, Skill.getBonusSkillTimeLeft(), true);
				player.getPacketSender().sendString(31625, player.getPoints().getKDR() + "");
				player.getPacketSender().sendString(31638, player.getAttributes().numInt(Attribute.STREAK_PENALTY) + "", true);
				player.getPacketSender().sendString(31640, (!EntityExtKt.passedTime(player, Attribute.LAST_VOTE, 11, TimeUnit.HOURS, false, false) ? "@gre@Eligible": "@red@Not Eligible"));
				player.getPacketSender().sendString(31658, player.getAttributes().bool(Attribute.EXPERIENCED_LOCKED) ? ("@red@Locked") : ("@gre@Unlocked"));
				player.getPacketSender().sendString(31660, player.getAttributes().bool(Attribute.MULTIPLY_XP_DROPS) ? ("@gre@On") : ("@red@Off"));
				player.getPacketSender().sendString(31664, player.getWelcome().getWelcome().getShortDate());
				player.getPacketSender().sendString(31666, "GMT +0", true);
				player.getPacketSender().sendString(31631, Misc.formatWithAbbreviation(getCarriedWealth(player)));
				player.getPacketSender().sendString(31662, player.unlockedVialCrushing() ? "@or2@Not Unlocked" :  player.isVialCrushingToggled() ? "@gre@Enabled" : "@red@Disabled");
				player.getPacketSender().sendString(31669, "" + player.getKillTracker().getMonstersKilled() +"", true);
				player.getPacketSender().sendString(31671, "" + player.getKillTracker().getBossesKilled() +"", true);
				AttributeManager.sendTab(player);
				break;
			default:
				break;
		}
	}

	/**
	 * Gets the member's rank color.
	 *
	 * @param player
	 *            the player
	 * @return the color
	 */
	private static String getMemberColor(Player player) {
		if (PlayerUtil.isDiamondMember(player)) return "<col=D3DADB>Diamond member";
		else if (PlayerUtil.isTitaniumMember(player)) return "<col=00FFFF>Titanium member";
		else if (PlayerUtil.isPlatinumMember(player)) return "@whi@Platinum member";
		else if (PlayerUtil.isLegendaryMember(player)) return "<col=F3D200>Legendary member";
		else if (PlayerUtil.isAmethystMember(player)) return "<col=ff00ff>Amethyst member";
		else if (PlayerUtil.isTopazMember(player)) return "@blu@Topaz member";
		else if (PlayerUtil.isMember(player)) return "@red@Ruby member";
		else if (PlayerUtil.isBronzeMember(player)) return "<col=873600>Bronze member";
		return "";
	}

	/**
	 * Handles clicking
	 *
	 * @param player
	 *            the player
	 * @param button
	 *            the button
	 * @return the clicking
	 */
	public static boolean click(Player player, int button) {
		// Achievements
		if (button >= AchievementManager.getLineId(AchievementDifficulty.EASY) && button < AchievementManager.getLineId(AchievementDifficulty.OTHER) + AchievementManager.getByDifficulty(AchievementDifficulty.OTHER).size()) {
			AchievementManager.displayFromTab(player, button);
			return true;
		}

		switch (button) {
			case 65_300:
				refresh(player, Tab.QUEST);
				break;
			case HELP_DESK_BUTTON:
				refresh(player, Tab.HELP_DESK);
				break;
			case WILDERNESS_BUTTON:
			case WILDERNESS_REFRESH:
				refresh(player, Tab.WILDERNESS);
				break;
			case MEMBER_BUTTON:
			case MEMBER_REFRESH:
				refresh(player, Tab.MEMBER);
				break;
			case INFORMATION_BUTTON:
			case INFO_REFRESH:
				refresh(player, Tab.INFORMATION);
				break;
			case 31111:
				player.getPacketSender().sendURL(Voting.VOTE_URL);
				return true;
			case 31417:
			case 31112:
				player.getPacketSender().sendURL(Store.STORE_URL);
				return true;
			case 31113:
				new DialogueBuilder(DialogueType.OPTION)
						.firstOption("Redeem Purchase.", Store.INSTANCE::requestPurchaseLookup)
						.secondOption("Redeem Votes.", Voting.INSTANCE::requestVoteLookup)
						.addCancel()
						.start(player);
				return true;
			case 31115:
				if (player.getWildernessLevel() > 0) {
					player.getPacketSender().sendMessage("You can't do that right now.", 1000);
					return false;
				}
				//player.getPacketSender().sendInterface(23344);
				player.getPacketSender().sendMessage("@dre@Opening the rules page..");
				player.getPacketSender().sendURL("https://forum.grinderscape.org/index.php?/topic/19-in-game-rules/");
				return true;
			case 31116:
				if (player.getSkillTaskManager().getFirstTask() == null) {
					player.getPacketSender().sendMessage("You do not have a skilling task at the moment! Speak to a skill master for one.", 1000);
					return true;
				}
				player.getPacketSender()
						.sendMessage("Your current skilling task is to do with "
								+ player.getSkillTaskManager().getFirstTask().getSkill().name().toLowerCase() + ", talk to the skill master for more details.", 1000);
				return true;
			case 31117:
				PlayerTaskManager.openInterface(player);
				return true;
			case 31118:
				//NpcInformation.sendInput(player); // Old system
				player.getPacketSender().sendMessage("@dre@Opening the Item Prices page..");
				player.getPacketSender().sendURL("https://wiki.grinderscape.org/Main_page/Prices");

				return true;
			case 31420: // Bronze island
				if (!player.getRights().isHighStaff() && !PlayerUtil.isBronzeMember(player)) {
					player.getPacketSender()
							.sendMessage("<col=0040ff><img=1025> This teleport is only available to Bronze members or higher.</col>", 1000);
					return false;
				}
				if (player.getCombat().isInCombat()) {
					player.getPacketSender().sendMessage("You must wait a few seconds after being out of combat to teleport!", 1000);
					return false;
				}
				if (TeleportHandler.checkReqs(player, new Position(2145, 2587, 0), true, false, player.getSpellbook().getTeleportType())) {
					TeleportHandler.teleport(player, new Position(2145 + Misc.random(2), 2587 + Misc.random(2)),
							player.getSpellbook().getTeleportType(), false, true);
					player.getPacketSender().sendJinglebitMusic(253, 25);
				}
				return true;
			case 31422:
				if (!player.getRights().isHighStaff() && !PlayerUtil.isMember(player)) {
					player.getPacketSender()
							.sendMessage("<col=0040ff><img=745> This teleport is only available to Ruby members or higher.</col>", 1000);
					return false;
				}
				if (player.getCombat().isInCombat()) {
					player.getPacketSender().sendMessage("You must wait a few seconds after being out of combat to teleport!", 1000);
					return false;
				}
				if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.LA_ISLA_EBANA.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
					TeleportHandler.teleport(player, Teleporting.TeleportLocation.LA_ISLA_EBANA.getPosition(),
							player.getSpellbook().getTeleportType(), false, true);
					player.getPacketSender().sendJinglebitMusic(253, 25);
				}
				return true;
			case 31424:
				// Prevent teleport for 15 minutes
//				if (!player.getTheCursedVaultDelayTimer().finished()) {
//					if (player.getTheCursedVaultDelayTimer().secondsRemaining() == 1 || player.getTheCursedVaultDelayTimer().secondsRemaining() / 60 <= 1) {
//						player.sendMessage("You can use this teleport again after waiting for " + (player.getTheCursedVaultDelayTimer().secondsRemaining() >= 59 ? "one more minute" : "one more second") +".");
//					} else {
//						player.sendMessage("You can use this teleport again after waiting for " + (player.getTheCursedVaultDelayTimer().secondsRemaining() >= 59 ? "" + player.getTheCursedVaultDelayTimer().secondsRemaining() / 60 + " more minutes" : "" + player.getTheCursedVaultDelayTimer().secondsRemaining() + " more seconds") + ".");
//					}
//					return false;
//				}
				if (!player.getRights().isHighStaff() && !PlayerUtil.isLegendaryMember(player)) {
					player.getPacketSender()
							.sendMessage("<col=0040ff><img=1026> This teleport is only available to Legendary members or higher.</col>", 1000);
					return false;
				}
				if (player.getCombat().isInCombat()) {
					player.getPacketSender().sendMessage("You must wait a few seconds after being out of combat to teleport!", 1000);
					return false;
				}
				if (TeleportHandler.checkReqs(player, new Position(2268, 2591, 0), true, false, player.getSpellbook().getTeleportType())) {
					TeleportHandler.teleport(player, new Position(2268 + Misc.random(4), 2589 + Misc.random(3), 0),
							player.getSpellbook().getTeleportType(), false, true);
					player.getTheCursedVaultDelayTimer().start(900);
					player.getPacketSender().sendJinglebitMusic(253, 25);
				}
				return true;
			case 31426:
				if (!player.getRights().isHighStaff() && !PlayerUtil.isPlatinumMember(player)) {
					player.getPacketSender()
							.sendMessage("<img=1027> This teleport is only available to Platinum members or higher.", 1000);
					return false;
				}
				if (TeleportHandler.checkReqs(player, new Position(2043, 3683, 0), true, false, player.getSpellbook().getTeleportType())) {
					TeleportHandler.teleport(player, new Position(2043 + Misc.getRandomInclusive(4), 3683 + Misc.getRandomInclusive(3), 0),
							player.getSpellbook().getTeleportType(), false, true);
					player.getPacketSender().sendJinglebitMusic(253, 25);
				}
				return true;
			case 31429: // ::Bank
				if (!PlayerUtil.isLegendaryMember(player)) {
					player.getPacketSender().sendMessage("<col=0040ff><img=1026> You must be a Legendary member to use this command feature.</col>");
					return false;
				}
				if (player.getArea() != null && AreaManager.inWilderness(player)) {
					player.getPacketSender().sendMessage("You can't use this command in the Wilderness!");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof FightCaveArea) {
					player.getPacketSender().sendMessage("You can't use this command in the Fight Caves!");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof AquaisNeigeArea) {
					player.getPacketSender().sendMessage("You can't use this command in the Aquais Neige!");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof CastleWars) {
					player.getPacketSender().sendMessage("You can't use this command in the Castle Wars Minigame!");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof ZulrahShrine) {
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof NexChamber) {
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof KalphiteLair && player.getPosition().getZ() == 0) {
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof VorkathArea) {
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(3135,3155,4640,4660))) { // Tarn
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2846,2868,9626,9649))) { // SLASH BASH
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2804,2897,9894,9983))) { // Ice queen
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2886,2935,4429,4472))) { // Dagannoth
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2804,2897,9894,9983))) { // Ice queen
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(1729,1804,5131,5235))) { // Giant mole
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2614,2688,3980,4022))) { // Kamil
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2690,2737,9157,9207))) { // Jungle demon
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2451,2478,4762,4796))) { // Giant sea snake
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2494,2511,3889,3905))) { // Sea troll queen
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2240,2301,2562,2622))) { // Legendary boss zone
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2550,2622,9476,9529))) { // BKT-Untouchable
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(3451,3518,9474,9527))) { //KQ
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof HydraArea) {
					player.getPacketSender().sendMessage("You can't use this command here!");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof GodChamberArea) {
					player.getPacketSender().sendMessage("You can't use this command here!");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof CerberusArea) {
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2240, 2302, 2563, 2622))) {
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2885, 2908, 5255, 5277))) { // Zilyana
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2963,3000,4368,4400))) { // Corp Area
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof PublicMinigameLobby) {
					player.getPacketSender().sendMessage("You can't use this command here!");
					return false;
				}
				if (player.getMinigame() != null) {
					player.getPacketSender().sendMessage("You can't use this command while playing Minigames!");
					return false;
				}
				if (AreaManager.inWilderness(player) || player.getWildernessLevel() > 0) {
					player.getPacketSender().sendMessage("You can't use this command in the Wilderness!");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof DuelFightArena || player.getDueling().inDuel()) {
					player.getPacketSender().sendMessage("You can't use this command in the Duel Arena!");
					return false;
				}
				if (player.getStatus() == PlayerStatus.TRADING) {
					player.getPacketSender().sendMessage("You can't use this command while in a trade!", 1000);
					return false;
				}
				if (player.getArea() instanceof UntypedInstancedBossArea || player.getArea() instanceof InstancedBossArea) {
					player.getPacketSender().sendMessage("You can't use this command within instances!", 1000);
					return false;
				}
				if (player.getStatus() == PlayerStatus.BANKING) {
					player.getPacketSender().sendMessage("You can't use this command while banking!", 1000);
					return false;
				}
				if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
					player.getPacketSender().sendMessage("You can't use this command while price checking!", 1000);
					return false;
				}
				if (player.getStatus() == PlayerStatus.DUELING) {
					player.getPacketSender().sendMessage("You can't use this command while in a duel!", 1000);
					return false;
				}
				if (player.getStatus() == PlayerStatus.DICING) {
					player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
					return false;
				}
				if (player.getStatus() == PlayerStatus.SHOPPING) {
					player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
					return false;
				}
				if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
					player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
					return false;
				}
				if (player.isInTutorial()) {
					return false;
				}
				if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
					return false;
				}
				if (player.getCombat().isInCombat() || player.getCombat().isUnderAttack()) {
					player.getPacketSender().sendMessage("You must wait 10 seconds after being out of combat to use this command.", 1000);
					return false;
				}
				if (player.busy()) {
					player.getPacketSender().sendMessage("You can't do that when you're busy.", 1000);
					return false;
				}
				if (player.getInterfaceId() > 0) {
					player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
					return false;
				}
				player.getBankpin().openBank();
				player.getPacketSender().sendMessage("@dre@Bank Opened!");
				return true;
			case 31431: // ::Hp
				if (!PlayerUtil.isAmethystMember(player)) {
					player.getPacketSender().sendMessage("<col=0040ff><img=747> You must be an Amethyst member to use this command feature!");
					return false;
				}
				if (player.getArea() != null && AreaManager.inWilderness(player)) {
					player.getPacketSender().sendMessage("You can't use this command in the Wilderness!");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof FightCaveArea) {
					player.getPacketSender().sendMessage("You can't use this command in the Fight Caves!");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof AquaisNeigeArea) {
					player.getPacketSender().sendMessage("You can't use this command in the Aquais Neige!");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof CastleWars) {
					player.getPacketSender().sendMessage("You can't use this command in the Castle Wars Minigame!");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof ZulrahShrine) {
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof NexChamber) {
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof KalphiteLair && player.getPosition().getZ() == 0) {
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof VorkathArea) {
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(3135,3155,4640,4660))) { // Tarn
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2846,2868,9626,9649))) { // SLASH BASH
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2804,2897,9894,9983))) { // Ice queen
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2886,2935,4429,4472))) { // Dagannoth
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2804,2897,9894,9983))) { // Ice queen
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(1729,1804,5131,5235))) { // Giant mole
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2614,2688,3980,4022))) { // Kamil
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2690,2737,9157,9207))) { // Jungle demon
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2451,2478,4762,4796))) { // Giant sea snake
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2494,2511,3889,3905))) { // Sea troll queen
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2240,2301,2562,2622))) { // Legendary boss zone
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2550,2622,9476,9529))) { // BKT-Untouchable
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(3451,3518,9474,9527))) { //KQ
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof HydraArea) {
					player.getPacketSender().sendMessage("You can't use this command here!");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof GodChamberArea) {
					player.getPacketSender().sendMessage("You can't use this command here!");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof CerberusArea) {
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2240, 2302, 2563, 2622))) {
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2885, 2908, 5255, 5277))) { // Zilyana
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2963,3000,4368,4400))) { // Corp Area
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof PublicMinigameLobby) {
					player.getPacketSender().sendMessage("You can't use this command here!");
					return false;
				}
				if (player.getMinigame() != null) {
					player.getPacketSender().sendMessage("You can't use this command while playing Minigames!");
					return false;
				}
				if (AreaManager.inWilderness(player) || player.getWildernessLevel() > 0) {
					player.getPacketSender().sendMessage("You can't use this command in the Wilderness!");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof DuelFightArena || player.getDueling().inDuel()) {
					player.getPacketSender().sendMessage("You can't use this command in the Duel Arena!");
					return false;
				}
				if (player.getTimerRepository().has(TimerKey.LAST_HEAL_COMMAND)) {
					player.getPacketSender().sendMessage("You can only use this command once every 60 seconds!");
					return false;
				}
				if (player.getStatus() == PlayerStatus.TRADING) {
					player.getPacketSender().sendMessage("You can't use this command while in a trade!", 1000);
					return false;
				}
				if (player.getArea() instanceof UntypedInstancedBossArea || player.getArea() instanceof InstancedBossArea) {
					player.getPacketSender().sendMessage("You can't use this command within instances!", 1000);
					return false;
				}
				if (player.getStatus() == PlayerStatus.BANKING) {
					player.getPacketSender().sendMessage("You can't use this command while banking!", 1000);
					return false;
				}
				if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
					player.getPacketSender().sendMessage("You can't use this command while price checking!", 1000);
					return false;
				}
				if (player.getStatus() == PlayerStatus.DUELING) {
					player.getPacketSender().sendMessage("You can't use this command while in a duel!", 1000);
					return false;
				}
				if (player.getStatus() == PlayerStatus.DICING) {
					player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
					return false;
				}
				if (player.getStatus() == PlayerStatus.SHOPPING) {
					player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
					return false;
				}
				if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
					player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
					return false;
				}
				if (player.isInTutorial()) {
					return false;
				}
				if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
					return false;
				}
				if (player.getCombat().isInCombat() || player.getCombat().isUnderAttack()) {
					player.getPacketSender().sendMessage("You must wait 10 seconds after being out of combat to use this command.", 1000);
					return false;
				}
				if (player.busy()) {
					player.getPacketSender().sendMessage("You can't do that when you're busy.", 1000);
					return false;
				}
				if (player.getInterfaceId() > 0) {
					player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
					return false;
				}
				if (!MorphItems.INSTANCE.notTransformed(player, "heal", true, false))
					return false;
				player.performGraphic(new Graphic(433));
				SpecialAttackType.updateBar(player, true);
				player.getSkillManager().setCurrentLevel(Skill.HITPOINTS, player.getSkillManager().getMaxLevel(Skill.HITPOINTS), true);
				player.getSkillManager().setCurrentLevel(Skill.PRAYER, player.getSkillManager().getMaxLevel(Skill.PRAYER), true);
				WeaponInterfaces.INSTANCE.assign(player);
				EquipmentBonuses.update(player);
				player.getPacketSender().sendQuickChat("I have just restored my Health & Prayer! (::hp)");
				player.getTimerRepository().register(TimerKey.LAST_HEAL_COMMAND, 100);
				return true;
			case 31433: // ::Spec
				if (!PlayerUtil.isTopazMember(player)) {
					player.getPacketSender().sendMessage("<img=746> You must be a Topaz member to use this command feature!");
					return false;
				}
				if (player.getArea() != null && AreaManager.inWilderness(player)) {
					player.getPacketSender().sendMessage("You can't use this command in the Wilderness!");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof FightCaveArea) {
					player.getPacketSender().sendMessage("You can't use this command in the Fight Caves!");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof AquaisNeigeArea) {
					player.getPacketSender().sendMessage("You can't use this command in the Aquais Neige!");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof CastleWars) {
					player.getPacketSender().sendMessage("You can't use this command in the Castle Wars Minigame!");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof ZulrahShrine) {
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof NexChamber) {
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof KalphiteLair && player.getPosition().getZ() == 0) {
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof VorkathArea) {
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(3135,3155,4640,4660))) { // Tarn
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2846,2868,9626,9649))) { // SLASH BASH
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2804,2897,9894,9983))) { // Ice queen
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2886,2935,4429,4472))) { // Dagannoth
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2804,2897,9894,9983))) { // Ice queen
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(1729,1804,5131,5235))) { // Giant mole
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2614,2688,3980,4022))) { // Kamil
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2690,2737,9157,9207))) { // Jungle demon
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2451,2478,4762,4796))) { // Giant sea snake
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2494,2511,3889,3905))) { // Sea troll queen
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2240,2301,2562,2622))) { // Legendary boss zone
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2550,2622,9476,9529))) { // BKT-Untouchable
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(3451,3518,9474,9527))) { //KQ
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof HydraArea) {
					player.getPacketSender().sendMessage("You can't use this command here!");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof GodChamberArea) {
					player.getPacketSender().sendMessage("You can't use this command here!");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof CerberusArea) {
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2240, 2302, 2563, 2622))) {
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2885, 2908, 5255, 5277))) { // Zilyana
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (AreaManager.inside(player.getPosition(), new Boundary(2963,3000,4368,4400))) { // Corp Area
					player.getPacketSender().sendMessage("You can't use this command in this area.");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof PublicMinigameLobby) {
					player.getPacketSender().sendMessage("You can't use this command here!");
					return false;
				}
				if (player.getMinigame() != null) {
					player.getPacketSender().sendMessage("You can't use this command while playing Minigames!");
					return false;
				}
				if (AreaManager.inWilderness(player) || player.getWildernessLevel() > 0) {
					player.getPacketSender().sendMessage("You can't use this command in the Wilderness!");
					return false;
				}
				if (player.getArea() != null && player.getArea() instanceof DuelFightArena || player.getDueling().inDuel()) {
					player.getPacketSender().sendMessage("You can't use this command in the Duel Arena!");
					return false;
				}
				if (player.getTimerRepository().has(TimerKey.LAST_SPEC_COMMAND)) {
					player.getPacketSender().sendMessage("You can only use this command once every 60 seconds!");
					return false;
				}
				if (player.getStatus() == PlayerStatus.TRADING) {
					player.getPacketSender().sendMessage("You can't use this command while in a trade!", 1000);
					return false;
				}
				if (player.getArea() instanceof UntypedInstancedBossArea || player.getArea() instanceof InstancedBossArea) {
					player.getPacketSender().sendMessage("You can't use this command within instances!", 1000);
					return false;
				}
				if (player.getStatus() == PlayerStatus.BANKING) {
					player.getPacketSender().sendMessage("You can't use this command while banking!", 1000);
					return false;
				}
				if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
					player.getPacketSender().sendMessage("You can't use this command while price checking!", 1000);
					return false;
				}
				if (player.getStatus() == PlayerStatus.DUELING) {
					player.getPacketSender().sendMessage("You can't use this command while in a duel!", 1000);
					return false;
				}
				if (player.getStatus() == PlayerStatus.DICING) {
					player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
					return false;
				}
				if (player.getStatus() == PlayerStatus.SHOPPING) {
					player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
					return false;
				}
				if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
					player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
					return false;
				}
				if (player.isInTutorial()) {
					return false;
				}
				if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
					return false;
				}
				if (player.getCombat().isInCombat() || player.getCombat().isUnderAttack()) {
					player.getPacketSender().sendMessage("You must wait 10 seconds after being out of combat to use this command.", 1000);
					return false;
				}
				if (player.busy()) {
					player.getPacketSender().sendMessage("You can't do that when you're busy.", 1000);
					return false;
				}
				if (player.getInterfaceId() > 0) {
					player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
					return false;
				}
				if (!MorphItems.INSTANCE.notTransformed(player, "regenerate spec", true, false))
					return false;
				player.performGraphic(new Graphic(332));
				SpecialAttackType.updateBar(player, true);
				player.setSpecialPercentage(100);
				WeaponInterfaces.INSTANCE.assign(player);
				EquipmentBonuses.update(player);
				player.getPacketSender().sendQuickChat("I have just restored my Special Attack! (::spec)");
				player.getTimerRepository().register(TimerKey.LAST_SPEC_COMMAND, 100);
				return true;
			case 31435:
				if (PlayerUtil.isAmethystMember(player) || PlayerUtil.isLegendaryMember(player) || PlayerUtil.isPlatinumMember(player)) {
					player.sendMessage("<col=0040ff>Your current members rank reduces the slayer KC by 25% rate.</col>");
				} else if (PlayerUtil.isTopazMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank reduces the slayer KC by 20% rate.</col>");
				} else if (PlayerUtil.isMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank reduces the slayer KC by 10% rate.</col>");
				} else {
					player.sendMessage("<col=0040ff>Your members rank is not eligible for any of this feature benefits.</col>");
				}
				player.sendMessage("<col=0040ff>Amethyst members and above get 25% reduction, Topaz 20%, and Ruby members get 10% respectively.</col>");
				return true;
			case 31437:
				if (PlayerUtil.isMember(player)) {
					player.sendMessage("<col=0040ff>The KC required to enter is reduced from 10 to 5 for your members rank.</col>");
				} else {
					player.sendMessage("<col=0040ff>Your members rank is not eligible for any of this feature benefits.</col>");
				}
				return true;
			case 31439:
				if (PlayerUtil.isMember(player)) {
					player.sendMessage("<col=0040ff>The shop buying limit is increased from 1,000 to 2,500 for your members rank.</col>");
				} else {
					player.sendMessage("<col=0040ff>Your members rank is not eligible for any of this feature benefits.</col>");
				}
				return true;
			case 31441:
				if (PlayerUtil.isMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks the unlimited yells feature benefit!</col>");
				} else {
					player.sendMessage("<col=0040ff>Your members rank is not eligible for any of this feature benefits.</col>");
				}
				return true;
			case 31443:
				if (PlayerUtil.isMember(player)) {
					player.sendMessage("<col=0040ff>Your account is eligible to use the Yell Customizer feature!</col>");
				} else {
					player.sendMessage("<col=0040ff>Your members rank is not eligible for any of this feature benefits.</col>");
				}
				return true;
			case 31445:
				if (PlayerUtil.isMember(player)) {
					player.sendMessage("<col=0040ff>The KC required to enter is reduced from 20 to 5 for your members rank.</col>");
				} else {
					player.sendMessage("<col=0040ff>Your members rank is not eligible for any of this feature benefits.</col>");
				}
				return true;
			case 31447:
				if (PlayerUtil.isTopazMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks 250 extra bank space slots!</col>");
				} else {
					player.sendMessage("<col=0040ff>Your members rank is not eligible for any of this feature benefits.</col>");
				}
				return true;
			case 31449:
				if (PlayerUtil.isMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks the shift drop on items feature.</col>");
				} else {
					player.sendMessage("<col=0040ff>Your members rank is not eligible for any of this feature benefits.</col>");
				}
				return true;
			case 31451:
				if (PlayerUtil.isPlatinumMember(player) || PlayerUtil.isDiamondMember(player) || PlayerUtil.isTitaniumMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks 50% faster run energy regeneration rate.</col>");
				} else if (PlayerUtil.isLegendaryMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks 40% faster run energy regeneration rate.</col>");
				} else if (PlayerUtil.isAmethystMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks 30% faster run energy regeneration rate.</col>");
				} else if (PlayerUtil.isTopazMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks 20% faster run energy regeneration rate.</col>");
				} else if (PlayerUtil.isRubyMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks 10% faster run energy regeneration rate.</col>");
				} else {
					player.sendMessage("<col=0040ff>Your members rank is not eligible for any of this feature benefits.</col>");
				}

				player.sendMessage("<col=0040ff>Platinum members and above get 50% faster run regeneration, Legendary 40%, Amethyst 30%, Topaz 20%, and Ruby members get 10% respectively.</col>");
				return true;
			case 31453:
				if (PlayerUtil.isPlatinumMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks 75% faster farming growth rate.</col>");
				} else if (PlayerUtil.isLegendaryMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks 55% faster farming growth rate.</col>");
				} else if (PlayerUtil.isAmethystMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks 35% faster farming growth rate.</col>");
				}else if (PlayerUtil.isTopazMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks 25% faster farming growth rate.</col>");
				} else if (PlayerUtil.isRubyMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks 15% faster farming growth rate.</col>");
				} else {
					player.sendMessage("<col=0040ff>Your members rank is not eligible for any of this feature benefits.</col>");
				}
				player.sendMessage("<col=0040ff>Platinum members and above get 75% faster farming growth rate, Legendary 55%, Amethyst 35%, Topaz 25%, and Ruby members get 15% respectively.</col>");
				return true;
			case 31455:
				if (PlayerUtil.isDiamondMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks 30% increased drop rate chance.</col>");
				} else {
					player.sendMessage("<col=0040ff>Your members rank is not eligible for any of this feature benefits.</col>");
				}
				player.sendMessage("<col=0040ff>Diamond members are eligible for 30% increased drop rate chance global modifier.</col>");
				return true;
			case 31457:
				if (PlayerUtil.isAmethystMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks 30% bonus chance to find a rare item from clue rewards.</col>");
				} else if (PlayerUtil.isTopazMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks 20% bonus chance to find a rare item from clue rewards.</col>");
				} else if (PlayerUtil.isRubyMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks 10% bonus chance to find a rare item from clue rewards.</col>");
				} else {
					player.sendMessage("<col=0040ff>Your members rank is not eligible for any of this feature benefits.</col>");
				}
				player.sendMessage("<col=0040ff>Amethyst members and above get 30% bonus chance to find a rare item from clue rewards, Topaz 20%, and Ruby members get 10% respectively.</col>");
				return true;
			case 31459:
				if (PlayerUtil.isRubyMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks 25% increased coins when thieving from stalls.</col>");
				} else {
					player.sendMessage("<col=0040ff>Your members rank is not eligible for any of this feature benefits.</col>");
				}
				player.sendMessage("<col=0040ff>Ruby members and above are eligible for 25% increased coins when thieving from stalls.</col>");
				return true;
			case 31461:
				if (PlayerUtil.isRubyMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks to view max hit in your equipments tab.</col>");
				} else {
					player.sendMessage("<col=0040ff>Your members rank is not eligible for any of this feature benefits.</col>");
				}
				player.sendMessage("<col=0040ff>Ruby members and above are eligible to view max hit from their equipments tab.</col>");
				return true;
			case 31463:
				if (PlayerUtil.isRubyMember(player)) {
					new DialogueBuilder(DialogueType.NPC_STATEMENT)
							.setNpcChatHead(PROSPECTOR_PERCY)
							.setText("Your sack can hold " + SackType.MEMBER.getSize() + " Ores instead of",
									SackType.UPGRADED.getSize() + " for being a member.").start(player);
				} else {
					player.sendMessage("<col=0040ff>Your members rank is not eligible for any of this feature benefits.</col>");
				}
				player.sendMessage("<col=0040ff>Ruby members and above are eligible to hold more ores in their sack.</col>");
				return true;
			case 31465:
				if (PlayerUtil.isRubyMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks to get 50% discount when using rug merchant travel feature.</col>");
				} else {
					player.sendMessage("<col=0040ff>Your members rank is not eligible for any of this feature benefits.</col>");
				}
				player.sendMessage("<col=0040ff>Ruby members and above are eligible to get 50% discount when using rug merchant travel feature.</col>");
				return true;
			case 31467:
				if (PlayerUtil.isTopazMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks Flaxkeeper to hold 400 Flax instead of 250 only.</col>");
				} else {
					player.sendMessage("<col=0040ff>Your members rank is not eligible for any of this feature benefits.</col>");
				}
				player.sendMessage("<col=0040ff>Topaz members and above unlocks Flaxkeeper to hold 400 Flax instead of 250 only.</col>");
				return true;
			case 31469:
				if (PlayerUtil.isRubyMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks the feature to Note/Unnote items when used on a bankbooth.</col>");
				} else {
					player.sendMessage("<col=0040ff>Your members rank is not eligible for any of this feature benefits.</col>");
				}
				player.sendMessage("<col=0040ff>Topaz members and above unlocks the feature to Note/Unnote items when used on a bankbooth.</col>");
				return true;
			case 31471:
				if (PlayerUtil.isDiamondMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks 20% bonus experience global modifier to all skills.</col>");
				} else if (PlayerUtil.isTitaniumMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks 15% bonus experience global modifier to all skills.</col>");
				} else if (PlayerUtil.isPlatinumMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks 10% bonus experience global modifier to all skills.</col>");
				} else {
					player.sendMessage("<col=0040ff>Your members rank is not eligible for any of this feature benefits.</col>");
				}
				player.sendMessage("<col=0040ff>Diamond members get 20% bonus experience global modifier to all skills, Titanium 15%, and Platinum members get 10% respectively.</col>");
				return true;
			case 31473:
				if (PlayerUtil.isRubyMember(player)) {
					player.sendMessage("<col=0040ff>Your members rank unlocks 20% bonus chance of getting jad pet when gambling a fire cape.</col>");
				} else {
					player.sendMessage("<col=0040ff>Your members rank is not eligible for any of this feature benefits.</col>");
				}
				player.sendMessage("<col=0040ff>Topaz members and above unlocks 20% bonus chance of getting jad pet when gambling a fire cape..</col>");
				return true;
			case 31120:
				BossDropTables.openInterface(player);
				return true;
			case 31316:
				player.sendMessage("There are currently " + AreaManager.getPlayersInArena(AreaManager.WILD) + " player(s) in the Wilderness!");
				return true;
/*			case 31318:
				player.sendMessage("There are currently " + AreaManager.getPlayersInArena(AreaManager.REVENANTS_CAVE) + " player(s) in the Revenants Cave!");
				return true;*/
		/*	case 31320:
				player.sendMessage("There are currently " + AreaManager.getPlayersInArena(AreaManager.RESOURCE_AREA) + " player(s) in the Resource Area!");
				return true;
			case 31322:
				player.sendMessage("There are currently " + AreaManager.getPlayersInArena(AreaManager.WILDERNESS_AGILITY) + " player(s) in the Wilderness Agility Arena!");
				return true;
			case 31324:
				player.sendMessage("There are currently " + AreaManager.getPlayersInBoundaries(new Boundary(3274, 3298, 3922, 3948)) + " player(s) in the Wilderness Thieving Zone!");
				return true;
			case 31326:
				player.sendMessage("There are currently " + AreaManager.getPlayersInBoundaries(new Boundary(2945, 2960, 3813, 3826)) + " player(s) in Chaos Altar Area!");
				return true;
			case 31328:
				player.sendMessage("There are currently " + AreaManager.getPlayersInBoundaries(new Boundary(3122, 3158, 3763, 3799)) + " player(s) in Chinchompa's Hill!");
				return true;
			case 31330:
				player.sendMessage("There are currently " + AreaManager.getPlayersInBoundaries(new Boundary(3017, 3071, 3648, 3712)) + " player(s) in the Bandits Camp!");
				return true;
			case 31332:
				player.sendMessage("There are currently " + AreaManager.getPlayersInBoundaries(new Boundary(3005, 3046, 3617, 3648)) + " player(s) in the Dark Fortress!");
				return true;
			case 31334:
				player.sendMessage("There are currently " +  AreaManager.getPlayersInArena(AreaManager.KING_BLACK_DRAGON) + " player(s) in the King Black Dragon Arena!");
				return true;
			case 31336:
				player.sendMessage("There are currently " + AreaManager.getPlayersInBoundaries(new Boundary(3247, 3316, 3904, 3921)) + " player(s) in the Chaos Elemental's Zone!");
				return true;
			case 31338:
				player.sendMessage("There are currently " + AreaManager.getPlayersInBoundaries(new Boundary(2964, 3002, 3667, 3707)) + " player(s) in the Crazy Archaeologist's Zone!");
				return true;
			case 31340:
				player.sendMessage("There are currently " + AreaManager.getPlayersInBoundaries(new Boundary(2968, 2998, 3828, 3860)) + " player(s) in the Chaos Fanatic's Zone!");
				return true;
			case 31342:
				player.sendMessage("There are currently " + AreaManager.getPlayersInBoundaries(new Boundary(3319, 3342, 3728, 3753)) + " player(s) in the Venenatis's Zone!");
				return true;
			case 31344:
				player.sendMessage("There are currently " + AreaManager.getPlayersInBoundaries(new Boundary(3158, 3193, 3780, 3798)) + " player(s) in the Vet'ion's Zone!");
				return true;
			case 31346:
				player.sendMessage("There are currently " + AreaManager.getPlayersInBoundaries(new Boundary(3291, 3333, 3826, 3852)) + " player(s) in the Callisto's Zone!");
				return true;
			case 31348:
				player.sendMessage("There are currently " + AreaManager.getPlayersInBoundaries(new Boundary(3219, 3248, 10330, 10355)) + " player(s) in the Scorpia's Zone!");
				return true;
			case 31350:
				player.sendMessage("There are currently " + AreaManager.getPlayersInBoundaries(new Boundary(3140, 3189, 3721, 3748)) + " player(s) in the Chronozon's Zone!");
				return true;*/
			case 31637:
				player.sendMessage("You do not have any voting streak penalties.");
				return true;
			case 31639:
				player.sendMessage("You are currently " + (!EntityExtKt.passedTime(player, Attribute.LAST_VOTE, 11, TimeUnit.HOURS, false, false) ? "@gre@Eligible": "@red@Not Eligible") +" to vote for a voting reward.");
				return true;
			case 31630:
				player.say("I am currently carrying " + Misc.formatWithAbbreviation(getCarriedWealth(player)) + " worth of items.");
				return true;
			case 31617:
				player.sendMessage("There " + (Skill.getBonusSkillTimeLeft().contains("Minutes") ? "are currently" : "is one") +" " + Skill.getBonusSkillTimeLeft() + " left for the " + Skill.getBonusSkill().getName() + " skill bonus XP event to end.");
				return true;
			case 31668:
				player.say("I have slain " + Misc.format(player.getKillTracker().getMonstersKilled()) + " monsters so far.");
				return true;
			case 31670:
				player.say("I have slain " + Misc.format(player.getKillTracker().getBossesKilled()) + " bosses so far.");
				return true;
			case 31672:
				player.say("I have slain " + Misc.format(player.getPoints().get(AttributeManager.Points.SLAYER_NPC_KILLS)) + " slayer monsters so far.");
				return true;
			case 31657:
				PlayerSettings.INSTANCE.toggleExperience(player);
				return true;
			case 31659:
				PlayerSettings.INSTANCE.toggleMultiplyXPDropsState(player);
				return true;
			case 31661:
				new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(7724).
						setText("Stop right there " + player.getUsername() + "! you better discuss that", "with me first.").setExpression(DialogueExpression.ANGRY)
						.add(DialogueType.PLAYER_STATEMENT).setText("Soo barbaric! I'll give you a visit..")
						.add(DialogueType.NPC_STATEMENT).setText("See you soon!").start(player);
				return true;
			case 31121:
				if (player.isAccountFlagged()) {
					return false;
				}
				if (player.getGameMode().isOneLife() && player.fallenOneLifeGameMode()) {
					player.sendMessage("Your account has fallen as a One life game mode and can no longer do any actions.");
					return false;
				}
				player.getPacketSender().sendInterface(51000);
				return true;
			case 31127:
				RankChooser.openInterface(player);
				return true;
			case 31128:
				PlayerTitles.display(player);
				return true;
			case 31129:
				YellCustomizer.openInterface(player);
				return true;
			case 31131:
				MyCommandsInterface.sendInterface(player);
				return true;
			case 31132:
				player.getPacketSender().sendMessage("You must talk to Security guard in Edgeville bank to reset your bank PIN.", 1000);
				return true;
			case 31133:
				player.getPacketSender().sendMessage("You can use your Discord's registered email to add 2FA into your account.", 1000);
				return true;
			case 31135:
				player.getPacketSender().sendMessage("@dre@Opening the discord's link..");
				player.getPacketSender().sendURL("https://discord.gg/b46xx5u");
				return true;
			case 31136:
				player.getPacketSender().sendMessage("@dre@Opening the Facebook's page link..");
				player.getPacketSender().sendURL("https://www.facebook.com/Grinderscape.org/");
				return true;
			case 31137:
				player.getPacketSender().sendMessage("@dre@Opening the Twitter's link..");
				player.getPacketSender().sendURL("https://twitter.com/Grinderscape");
				return true;
			case 31138:
				player.getPacketSender().sendMessage("@dre@Opening the Youtube's link..");
				player.getPacketSender().sendURL("https://www.youtube.com/user/dokenfilm");
				return true;
			case 31140:
				player.getPacketSender().sendMessage("@dre@Opening the PvM Guides directory link..");
				player.getPacketSender().sendURL("https://forum.grinderscape.org/index.php?/forum/76-pvm-guides/");
				return true;
			case 31141:
				player.getPacketSender().sendMessage("@dre@Opening the Skilling Guides directory link..");
				player.getPacketSender().sendURL("https://forum.grinderscape.org/index.php?/forum/77-skilling-guides/");
				return true;
			case 31142:
				player.getPacketSender().sendMessage("@dre@Opening the Money Making Guides directory link..");
				player.getPacketSender().sendURL("https://forum.grinderscape.org/index.php?/forum/78-money-making-guides/");
				return true;
			case 31143:
				player.getPacketSender().sendMessage("@dre@Opening the Miscelleneous Guides directory link..");
				player.getPacketSender().sendURL("https://forum.grinderscape.org/index.php?/forum/79-miscellaneous-guides/");
				return true;
			case 31613:
				player.say("The server has been online for " + ServerIO.getServerOnlineTime());
				player.getPacketSender().sendString(31614, ServerIO.getServerOnlineTime());
				return true;
			case 31611:
				player.say("There are currently " + PlayerUtil.transformPlayerCount() + " player(s) online!");
				player.getPacketSender().sendString(31612, PlayerUtil.transformPlayerCount() + " Player(s)");
				return true;
			case 31622:
				player.getPacketSender().sendQuickChat("My KDR is " + player.getPoints().getKDR());
				return true;
			case 31663:
				player.getPacketSender().sendQuickChat("I joined Grinderscape on: " + player.getWelcome().getWelcome().getFullDate());
				return true;
			case 31665:
				player.getPacketSender().sendQuickChat("The server's time is GMT +0");
				player.getPacketSender().sendString(31666, "GMT +0");
				return true;
			case 31125:
				OnlineMiddlemenCommand.sendMiddlemenList(player);
				return true;
			case 31130:
				player.getPacketSender().sendMessage("@dre@Opening the Highscores page..");
				player.getPacketSender().sendURL("https://www.grinderscape.org/highscores/");
				return true;
			case 31415:
				player.getPacketSender().sendInterface(8134);
				player.getPacketSender().sendString(8144, "@or2@<img=745> Members rank Features.");
				player.getPacketSender().sendString(8145, "- Infinite Crystal Bow ammunitions.");
				player.getPacketSender().sendString(8147, "- Slayer task's amount reduction.");
				player.getPacketSender().sendString(8148, "Regular: 40%, Super: 50%, Extreme: 60%");
				player.getPacketSender().sendString(8149, "- Bone crusher charge's multiplier:");
				player.getPacketSender().sendString(8150, "Regular: 5x, Super: 10x, Extreme: 25x");
				player.getPacketSender().sendString(8151, "- Higher chance of receiving Jad pet in exchange");
				player.getPacketSender().sendString(8152, "for firecape.");
				player.getPacketSender().sendString(8153, "- Member's rank icons.");
				player.getPacketSender().sendString(8154, "- Access to Yell channel with infinite yells.");
				player.getPacketSender().sendString(8155, "- Access to equipments tab max hit preview.");
				player.getPacketSender().sendString(8156, "- Access to the Member's zone.");
				player.getPacketSender().sendString(8157, "- Ability to use the Gilded Altar.");
				player.getPacketSender().sendString(8158, "- Enter godwars chambers without kill counts.");
				player.getPacketSender().sendString(8159, "- Ability to use Kalphite's queen boss shortcut.");
				player.getPacketSender().sendString(8160, "- 5 Killcount for entering Tarn's lair, Ice Queen's lair");
				player.getPacketSender().sendString(8161, "and Kamil's Ice Cave.");
				player.getPacketSender().sendString(8162, "- Ability to use Shift + Drop on inventory items.");
				player.getPacketSender().sendString(8163, "- Use items on bank to Note/Unnote.");
				player.getPacketSender().sendString(8164, "- Discord rank upon request.");
				player.getPacketSender().sendString(8165, "");
				player.getPacketSender().sendString(8165, "");
				player.getPacketSender().sendString(8165, "-");
				player.getPacketSender().sendString(8165, "");
				player.getPacketSender().sendString(8165, "");
				player.getPacketSender().sendString(8166, "");
				player.sendMessage("Check out the wiki for the complete features list.");
				player.getPacketSender().clearInterfaceText(8166, 8200);
				return true;
			case 31411:
				if (PlayerUtil.isMember(player)) {
					player.sendMessage("<col=0040ff>Your account is not a member</col>.");
					return true;
				}
				player.sendMessage("<col=0040ff>Your account's member rank is: " + getMemberColor(player) +"</col>");
				return true;
			case 31413:
				player.sendMessage("<col=0040ff>Your account's total spent amount is: $" + player.getAttributes().numInt(Attribute.AMOUNT_PAID) +".00</col>");
				return true;

			case 31615:
				player.sendMessage("The current skill with hourly bonus experience is: @whi@" + Skill.getBonusSkill().getName());
				return true;

			case 31119:
				ItemDropFinderInterface.openInterface(player);
				return true;
			case 31122:
				MonsterKillTracker.displayNPCList(player);
				return true;
			case 31123:
				player.getPacketSender().sendMessage("@dre@Opening the Staff's directory page..");
				player.getPacketSender().sendURL("https://wiki.grinderscape.org/Main_page/Server_team_hub/_Server_Teams/Staff_Team");
				return true;
			case 31124:
				StaffOnline.sendStaffList(player);
				return true;

		}
		return false;
	}
}
