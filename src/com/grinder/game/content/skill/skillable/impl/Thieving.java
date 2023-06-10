package com.grinder.game.content.skill.skillable.impl;

import com.grinder.game.GameConstants;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.miscellaneous.PetHandler;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.task.SkillMasterType;
import com.grinder.game.content.skill.task.SkillTaskManager;
import com.grinder.game.content.task_new.DailyTask;
import com.grinder.game.content.task_new.PlayerTaskManager;
import com.grinder.game.content.task_new.WeeklyTask;
import com.grinder.game.entity.agent.combat.LineOfSight;
import com.grinder.game.entity.agent.combat.event.impl.StunEvent;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.aggression.MonsterAggressionTolerancePolicy;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.game.entity.object.ClippedMapObjects;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.Skill;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.model.item.container.bank.BankUtil;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.TimedObjectReplacementTask;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;
import com.grinder.util.ObjectID;
import com.grinder.util.chance.Chance;
import com.grinder.util.chance.WeightedChance;
import com.grinder.util.timing.TimerKey;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.grinder.util.ItemID.*;

/**
 * Handles actions related to the Thieving skill.
 * <p>
 * The Thieving skill allows a player to steal items in the game. Either from
 * npcs or objects.
 *
 * @author Professor Oak
 */
public class Thieving {

	/**
	 * The {@link Animation} a player will perform when pickpocketing.
	 */
	private static final Animation PICKPOCKET_ANIMATION = new Animation(881, 40);

	/**
	 * The {@link Animation} a player will perform when thieving.
	 */
	private static final Animation THIEVING_ANIMATION = new Animation(832, 5);

	/**
	 * The {@link Graphic} a player will perform when being stunned.
	 */
	public static final Graphic STUNNED_GFX = new Graphic(254, GraphicHeight.HIGH);

	/**
	 * The {@link Animation} an npc will perform when attacking a pickpocket.
	 */
	private static final Animation NPC_ATTACK_ANIMATION = new Animation(401);

	/**
	 * The {@link Animation} the player will perform when blocking an attacking
	 * {@link NPC}.
	 */
	public static final Animation PLAYER_BLOCK_ANIMATION = new Animation(404);

	/**
	 * The possible rare item rewards from theiving
	 */
	public static final int[] THIVING_STALL_BONUS_ITEMS = { 995 };

	public static int THIEVING_STALL_RANDOMS[] = { ItemID.CLOCKWORK, 10362, 1506, 7122, 7124, 7409, 7126, 7128, 7130, 7132, 7134, 10390, 10364, 10366, 10368, 10370, 10372, ItemID.CLOCKWORK, 10374, 10376, 10378, 10380, 10382, 10384, 10386, 10388, 6188, 12629, 13097, 6862, 6863, 6885, 7114,
			7116, 7928, 7929, 7930, 7931, 13099, 13095, 13103, 13105, 6853, 6856, 6857, 6858, 6859, 6861, 7136, 7138, 3057, 3058, 3059, 3060, ItemID.CLOCKWORK, 3061, 6180, 6181, 6182, 6184, 6185, 6186, 6187, 6188, 6654,
			6655, 6656, 7534, 7535, 2633, 12447, 12449, 12445, 2635, 2637, 2639, 2641, 7394, 7390, 7386, 9634, 9636, 9638, 9640, 9642, 9644, 10631, 7592, 7593, 7594, 7595, 7596, 10150, 7396, 7392, 7388, 452, 452, 452, 452, 452, 452, 452, 450, 450, 450, 450, 450, 2460, 2462, 2462, 2464, 2466, 2468, 2470,
			2472, 2474, 2476, 2631, 12451, 12453, 12455, 2645, 2647, 2649, 6856, 2952, 9946, 9944, 9945, 9921, 9922, 9923, 9924, 9925, 10069, 10063, 10061, 9241, 6773, 7141, 7142, 6857, 6858, 6859, 6860, 6861, 6862, 6863, 5291, 5292, 5293, 5294, 5295, 5296, 5297, 5298, 5299, 5300, 5301, 5302, 5303, 5304, 7122, 7124, 7126, 7128, 7130,
			7132, 7134, 7136, 7138, 3057, 3058, 3059, 3060, 3061, 6180, 6181, 6182, 6184, 6185, 6186, 6187, 6188, 6654, 6655, 6656, 7534, 7535, 1025, 2633, 2635, 2637, 2639, 2641, 7394, 7390, 7386, 7396, 7392,
			7388, 452, 452, 452, 452, 452, 452, 452, 450, 450, 450, 450, 450, 2460, 2462, 2462, 2464, 2466, 2468, 2470, 2472, 2474, 2476, 2631, 2645, 2647, 2649, 6856, 6857, 6858, 6859, 6860, 6861, 6862,
			6863, 5291, 5292, 5293, 5294, 5295, 6065, 6067, 6068, 6069, 6070, 10069, 10071, 5296, 7122, 7124, 7126, 7128, 7130, 7132, 7134, 7136, 7138, 3057, 3058, 3059, 3060, 3061, 6180, 6181, 6182, 6184, 6185, 6186, 6187, 6188, 6654, 6655, 6656, 7534, 7535,
			1025, 2633, 2635, 2637, 2639, 2641, 7394, 7390, 7386, 7396, 7392, 7388, 452, 452, 452, 452, 452, 452, 452, 450, 450, 450, 450, 450, 2460, 2462, 2462, 2464, 2466, 2468, 2470, 2472, 2474, 2476,
			2631, 2645, 2647, 2649, 6856, 6857, 13188, 6858, 6859, 6860, 6861, 6862, 6863, 5291, 5292, 5293, 5294, 5295, 5296, 5297, 5298, 5299, 5300, 5301, 5302, 5303, 5304, ItemID.CLOCKWORK, 7122, 7124, 7126, 7128, 7130, 7132, 7134,
			7136, 7138, 3057, 3058, 3059, 3060, 3061, 2952, 6180, 6181, 6182, 6184, 6185, 6186, 6187, 6188, ItemID.CLOCKWORK, 6654, 6655, 6656, 7534, 7535, 1025, 2633, 2635, 2637, 2639, 2641, 7394, 7390, 7386, 7396, 7392, 7388, 452,
			452, 452, 452, 452, 452, 452, 450, 450, 450, 450, 450, 2460, 2462, 2462, 2464, 2466, 2468, 2470, 2472, 2474, 2476, 2631, 2645, 2647, 2649, 6856, 6857, 6858, 6859, 6860, 6861, 6862, 6863, 5291,
			5292, 5293, 5294, 5295, 5296, 7122, 7124, 7126, 7128, 7130, 7132, 7134, 7136, 7138, 3057, 3058, 3059, 3060, 3061, 6180, 6181, 6182, 6184, 6185, 6186, 6187, 6188, 6654, 6655, 6656, 7534, 7535, 1025, 2633,
			2635, 2637, 2639, 2641, 7394, 7390, 7386, 7396, 7392, 7388, 452, 452, 452, 452, 452, 452, 452, 450, 450, 450, 450, 450, 2460, 2462, 2462, 2464, 2466, 2468, 2470, 2472, 2474, 2476, 2631, 2645,
			2647, 2649, 6856, 6857, 6858, 6859, 6860, 6861, 6862, 6863, 5291, 5292, 5293, 5294, 5295, 5296, 5297, 5298, 5299, ItemID.CLOCKWORK, 5300, 5301, 5302, 5303, 5304, 7122, 7124, 7126, 7128, 7130, 7132, 7134, 7136, 7138,
			3057, 3058, 3059, 3060, 3061, 6180, 6181, 6182, 6184, 6185, 6186, 6187, 6188, 6654, 6655, 6656, 7534, 7535, 1025, 2633, 2635, 2637, 2639, 2641, 7394, 7390, 7386, 7396, 7392, 7388, 452, 452, 452, 452,
			 452, 452, 452, 450, 450, 450, 450, 450, 2460, 2462, 2462, 2464, 2466, 2468, 2470, 2472, 2474, 2476, 2631, 2645, 2647, 2649, 6856, 6857, 6858, 6859, 6860, 6861, 6862, 6863, 5291, 5292, 5293, 5294,
			 5295, 5296, 5297, 5298, 5299, 5300, 5301, 5302, 5303, 5304, 5096, 5097, 5098, 5099, 5100, 5101, 5102, 5103, 5104, 5105, 5106, 5106, 5280, 5281, 5282,
			 5283, 5284, 5285, 5286, 5287, 5288, 5289, 5290, 5291, 5292, 5293, 5294, 5295, 5296, 5297, 5298, 5299, 5300, 5301, 5302, 5303, 5304, 5305, 5306, 5307, 5308, 5309, 5310, 5311, 5311, 5313, 5316, 5320, 5324, 5321,
			 5323, 5315, 405, 405, 405, 3698, 3696, 3757, 3758, 4464, 4613, 5345, 6885, 7934, 2568, 13354, 2520, 2520 };

	// imp mask removed due to buggy look item id 12249, item too 12251
	public static int CRYSTAL_CHEST_LOOT[] = { ItemID.SWAMPBARK_HELM, SWAMPBARK_BODY, SWAMPBARK_LEGS, SWAMPBARK_GAUNTLETS, SWAMPBARK_BOOTS, 7370, 20220, 12245, 12247, 20223, 20226, 20229, 20232, 20235, 3827, 3828, 3829, 3830, 3831, 3832, 3833, 3834, 3835, 3836, 3837, 3838, 12211, 12205, 12207, 12209, 12213, 12221, 12215, 12217, 12219, 12223,
			12241, 12235, 12237, 12239, 12243, 12279, 7445, 7447, 7451, 7441, 7443, 12231, 12225, 12227, 12229, 12233, 20178, 20169, 20172, 20175, 20181, 20193, 20184, 20187, 20190, 20196, 2595, 2591, 2593, 2597, 3473, 2587, 2583, 2585, 2589, 3472, 12283, 12277, 12285, 12281, 12293,
			12287, 12289, 12295, 12291, 2613, 2607, 2609, 2611, 3475, 2605, 2599, 2601, 2603, 3474, 2619, 2615, 2617, 2621, 3476, 2627, 2623, 2625, 2629, 3477, 2657, 2653, 2655, 2659, 3478, 2665, 2661, 2663, 2667, 3479, 2673, 2669, 2671, 2675, 3480, 12245,
			7362, 7366, 7372, 7374, 7364, 7368, 10306, 20756, 10308, 10310, 10312, 10314, 10316, 10318, 7332, 7338, 7344, 7350, 10404, 10406, 10408, 10410, 10412, 10414, 12245, 12769, 12771, 12773, 12757, 12759, 12761, 12763, 12247, 10450, 10446, 10448, 12197, 12261, 12273,
			12309, 12311, 12313, 12321, 12323, 12325, 12600, 13317, 13318, 13319,
			15325, 15326, 15327, 15328, 15320, 15321, 15322, 15323, 15315, 15316, 15317, 15318, // Black
			15335, 15336, 15337, 15338, 15340, 15341, 15342, 15343, 15330, 15331, 15332, 15333, 15310, 15311, 15312, 15313,// Rune
			15314, 15319, 15324, 15329, 15334, 15339, 15344 // Skirts
			//15437 // Birthday partyhat
	};

	public static int MUDDY_CHEST_ITEMS[] = { 10286, 10288, 10290, 10292, 10294, 7336, 7342, 7348, 7354, 7360, 1961, 1907, 12375, 12377, 12757, 12759, 12769, 10398, 1187, 11335, 6585, 10150, 6912, 6914, 5556, 5557, 2577, 2579, 21301, 21304, 12249, 20199, 20202, 6724, 10316, 10318, 10320, 10322, 10324, 10392, 10394, 10296, 10298, 10300, 10302, 10304, 7334, 7340, 7346, 7352, 7358,
			10286, 10288, 10290, 10292, 10294, 7336, 7342, 7348, 7354, 7360, 1961, 1907, 12375, 12377, 12757, 12759, 12769, 10398, 1187, 11335, 6585, 10150, 6912, 6914, 5556, 5557, 10286, 10288, 10290, 10292, 10294, 7336, 7342, 7348, 7354, 7360, 1961, 1907, 12375, 12377, 12757, 12759, 12769, 10398, 1187, 11335, 6585, 10150, 6912, 6914, 5556, 5557,
			6568, 20050, 21298, 11812, 20199, 20166, 10075, 12596, 14160, 11838, 11840, 88, 2577, 2579, 6731, 6733, 6735, 6737, 14161, 8322, 8465, 12771, 20050, 22284, 6918, 6916, 6924, 6920, 6922, 6585, 11128, 6524, 6528, 6568, 2417, 2415, 7462, 7462, 7462, 11838, 21009, 12954, 10551, 20223, 20226,
			11826, 11828, 11832, 11836, 20716, 11907, 12856, 12877, 12883, 12881, 4151, 4587, 5698, 4585, 4087, 3140, 4675, 9185, 7158, 4153, 11235, 11808, 2581,
			3204, 11284, 11770, 11772, 6737, 6733, 7398, 7399, 7400, 6916, 6918, 6920, 6922, 6924, 10446, 10448, 10450, 12197, 12273, 6889, 4224, 11759};

	public static int PVP_RESOURCES_ITEMS[] = { 386, 392, 21905, 21326, 21318, 2435, 386, 392, 2435, 892, 6522, 7947, 386, 392, 2435, 6686, 2449, 2437, 2441, 3041, 3145, 2445, 5953, 2451, 3025, 11212, 9244, 9245, 11230, 4740, 20849, 19484};

	// Max items to add in skilling or participation shop
	// 13331, 13332, 13333, 13334, 13335, 13336, 13337, 13338,

	public static int NOOBISH_ITEMS[] = { 9185, 4675, 2493, 1712, 6328, 1540, 276, 278, 464, 577, 1147, 1079, 1077, 1133, 1283, 1093, 1127, 1149, 3751, 3749, 2550, 1273, 1357, 4097,
			1623, 1631, 1718, 1907, 970, 975, 1005, 1476, 6605, 6611, 6617, 6619, 6623, 6625, 1326, 4551, 1185};

	public static Chance<Item> GEM_STALL_REWARDS = new Chance<Item>(Arrays.asList(
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(UNCUT_SAPPHIRE)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(UNCUT_EMERALD)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(UNCUT_RUBY)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(UNCUT_DIAMOND)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(UNCUT_DRAGONSTONE))
	));

	/*
	 * Messages that are sent to the player while stealing from stalls
	 */
	private static final String[][] STALL_MESSAGES = {
			{ "@whi@You can take Thieving skill tasks from thieving master for bonus rewards & XP!" },
			{ "@whi@Every equipped rogue gear piece increases your experience gain from stalls!" },
			{ "@whi@Drinking the Bandit's brew grants 2,000,000 Thieving experience!" },
			{ "@whi@Wearing gloves of silence has 1/5 chance of thieving double money from stalls!" },
			{ "@whi@Dodgy necklace allows you to thieve 50% faster from miscellaneous stalls!" },
			{ "@whi@Dodgy necklace grants 10% bonus experience when thieving from stalls!" },
			{ "@whi@Thieving with a skillcape grants 20% bonus experience froms stalls & pickpocketing!" },
			{ "@whi@Did you know: Wilderness stalls rewards you 2x the cash reward. Be aware from PK'ers!" },

	};

	public static String currentMessage;

	public static void stallMessage(Player player) {
		currentMessage = STALL_MESSAGES[Misc.getRandomInclusive(STALL_MESSAGES.length - 1)][0];
		player.getPacketSender().sendMessage("<img=779> " + currentMessage);
	}

	/**
	 * Handles Pickpocketing.
	 *
	 * @author Professor Oak
	 */
	public static final class Pickpocketing {

		/**
		 * Attempts to pickpocket an npc.
		 *
		 * @param player
		 * @param npc
		 * @return
		 */
		public static boolean init(Player player, NPC npc) {
			Optional<Pickpocketable> pickpocket = Pickpocketable.get(npc.getId());

			if (pickpocket.isPresent()) {
				if (hasRequirements(player, npc, pickpocket.get())) {
					// Stop movement..
					player.getMotion().clearSteps();

					npc.getMotion().clearSteps();

					// Start animation..
					player.performAnimation(PICKPOCKET_ANIMATION);

					// Send message..
					String name = npc.fetchDefinition().getName().toLowerCase();
					if (!name.endsWith("s")) {
						name += "'s";
					}

					// Block actions
					player.BLOCK_ALL_BUT_TALKING = true;

					player.getPacketSender().sendMessage("You attempt to pick the " + name + " pocket..");

					// Face npc..
					player.setPositionToFace(npc.getPosition());

					// Reset click delay..
					player.getClickDelay().reset();

					// Play sound
					player.getPacketSender().sendSound(Sounds.PICKPOCKET_SOUND);

					// Mark npc as immune for 5 seconds..
					// This makes it so other players can't attack it.
					npc.getTimerRepository().register(TimerKey.ATTACK_IMMUNITY, Misc.getTicks(5));

					// Submit new task..
					TaskManager.submit(new Task(2, player, false) {
						@Override
						protected void execute() {
							if (isSuccessful(player, pickpocket.get())) {
								// Get the loot..
								Item loot = pickpocket.get().getRewards()[Misc
										.getRandomInclusive(pickpocket.get().getRewards().length - 1)].clone();

								// If we're pickpocketing the Master farmer and
								// the required chance
								// isn't hit, make sure to reward the default
								// item.
								// This is to make sure the other seeds remain
								// semi-rare.
								if (pickpocket.get() == Pickpocketable.MASTER_FARMER) {
									if (Misc.getRandomInclusive(100) > 18) {
										loot = pickpocket.get().getRewards()[0];
									}

									// Mix up loot amounts aswell for seeds..
									if (loot.getAmount() > 1) {
										loot.setAmount(1 + Misc.getRandomInclusive(loot.getAmount()));
									}
								}

								if (loot.getId() == COINS) {
									loot.setAmount((int) ((loot.getAmount() / 1.05)));
									loot.setAmount(loot.getAmount() * 8);
									if (loot.getAmount() < 0)
										loot.setAmount(10_000 + Misc.getRandomInclusive(1500));
								}

								loot.setAmount(loot.getAmount());

								if (loot.getId() == COINS || loot.getDefinition().getName().contains(" seed")) {
									if (GameConstants.BONUS_DOUBLE_REWARDS) {
										loot.setAmount(loot.getAmount() * 2);
									}
								}

								// Reward loot
								if (!player.getInventory().isFull()) {
									player.getInventory().add(loot);
								}

								// Players who wear the rogue outfit have a chance of getting double loot when successfully pickpocketing NPCs.
								// This chance is guaranteed if the full outfit is worn. It does not increase success rate.
								if (!player.getInventory().isFull()) {
									if ((player.getEquipment().containsAny(ROGUE_BOOTS, ROGUE_GLOVES, ROGUE_TOP, ROGUE_MASK, ROGUE_TROUSERS) && Misc.random(100) <= 8) || EquipmentUtil.isWearingRogueSet(player))
									player.getInventory().add(loot);
								}

								// Send second item loot message..
								String name = loot.getDefinition().getName().toLowerCase();
								if (!name.endsWith("s") && loot.getAmount() > 1) {
									name += "s";
								}
								player.getPacketSender().sendMessage("You steal "
										+ (loot.getAmount() > 1 ? Integer.toString(loot.getAmount()) : Misc.anOrA(name))
										+ " " + name + ".");

								// Add experience..
								player.getSkillManager().addExperience(Skill.THIEVING, (pickpocket.get().getExp()));

								// Unblock actions
								player.BLOCK_ALL_BUT_TALKING = false;

								// Task
								AchievementManager.processFor(AchievementType.PROFESSIONAL_THIEF, player);

								// Add pet..
								PetHandler.onSkill(player, Skill.THIEVING);

								player.getPoints().increase(AttributeManager.Points.SUCCESFUL_PICKPOCKETS, 1); // Increase points
								PlayerTaskManager.progressTask(player, DailyTask.PICKPOCKET);
								PlayerTaskManager.progressTask(player, WeeklyTask.PICKPOCKET);
								if ((player.getPoints().get(AttributeManager.Points.SUCCESFUL_PICKPOCKETS) % 50) == 0) {
									player.sendMessage("Total successful pickpockets: @red@" + player.getPoints().get(AttributeManager.Points.SUCCESFUL_PICKPOCKETS) + "</col>.");
								}

							} else {

								// Unblock actions
								player.BLOCK_ALL_BUT_TALKING = false;

								// Make npc hit the player..
								npc.setPositionToFace(player.getPosition());
								npc.say((pickpocket.get() == Pickpocketable.MASTER_FARMER
										? "Cor blimey, mate! What are ye doing in me pockets?"
										: "What do you think you're doing?"));
								npc.performAnimation(NPC_ATTACK_ANIMATION);
								player.getPacketSender().sendMessage("You fail to pick the pocket.");

								// Play sound
								player.getPacketSender().sendSound(Sounds.PICKPOCKET_FAILURE_SOUND);
								player.getCombat().submit(new StunEvent(pickpocket.get().getStunTime(), true, true, true));
								player.getCombat()
										.queue(new Damage(pickpocket.get().getStunDamage(), DamageMask.REGULAR_HIT));
								PlayerExtKt.resetInteractions(player, true, false);
								player.getPoints().increase(AttributeManager.Points.FAILED_PICKPOCKETS, 1); // Increase points
							}

							// Stop task..
							stop();
						}
					});
				}
				return true;
			}
			return false;
		}

		/**
		 * Checks if a player has the requirements to thieve the given
		 * {@link Pickpocketable}.
		 *
		 * @param player
		 * @param p
		 * @return
		 */
		private static boolean hasRequirements(Player player, NPC npc, Pickpocketable p) {
			// Make sure they aren't spam clicking..
			if (!player.getClickDelay().elapsed(1800)) {
				return false;
			}

			if (!npc.isAlive() || npc.isDying() || npc.getHitpoints() <= 0) {
				return false;
			}

			// Check thieving level..
			if (player.getSkillManager().getCurrentLevel(Skill.THIEVING) < p.getLevel()) {
				DialogueManager.sendStatement(player,
						"You need a Thieving level of at least " + Integer.toString(p.getLevel()) + " to do this.");
				return false;
			}

			// Check stun..
			if (player.getTimerRepository().has(TimerKey.STUN)) {
				return false;
			}

			// Make sure we aren't in combat..
			if (player.getCombat().isUnderAttack()) {
				player.getPacketSender().sendMessage("You must wait a few seconds after being in combat to do this.");
				return false;
			}

			// Make sure they aren't in combat..
			if (npc.getCombat().isInCombat()) {
				player.getPacketSender().sendMessage("That npc is currently in combat and can't be pickpocketed.");
				return false;
			}

			// Make sure we have inventory space..
			if (player.getInventory().isFull()) {
				player.getInventory().full();
				return false;
			}

			return true;
		}

		/**
		 * Determines the chance of failure. method.
		 *
		 * @param player
		 *            The entity who is urging to reach for the pocket.
		 * @return the result of chance.
		 */
		private static boolean isSuccessful(Player player, Pickpocketable p) {
			int base = 20;
			int factor = (short) Misc.getRandomInclusive(player.getSkillManager().getCurrentLevel(Skill.THIEVING) + base);
			if (p == Pickpocketable.FEMALE_HAM_MEMBER || p == Pickpocketable.MALE_HAM_MEMBER) {
				// TODO: Handle ham clothing bonus chance of success
		        if (player.getEquipment().contains(4302) && player.getEquipment().contains(4298) && player.getEquipment().contains(4300)
		        		&& player.getEquipment().contains(4304) && player.getEquipment().contains(4308) && player.getEquipment().contains(4310)) {
		        	factor *= 2;
		        }
			} else {
			if (player.getEquipment().contains(4302) && player.getEquipment().contains(4298) && player.getEquipment().contains(4300)
	        		&& player.getEquipment().contains(4304) && player.getEquipment().contains(4308) && player.getEquipment().contains(4310)) {
	        	factor *= 1.15;
	        }
			if (player.getEquipment().contains(21143)) { // Dodgy necklace 5% bonus chance
				factor *= 1.05;
			}

			// Thieving cape provides an additional 10% chance of being successful when pickpocketing NPCs.
			if (player.getEquipment().containsAny(THIEVING_CAPE, THIEVING_CAPE_T_)) {
				factor *= 1.10;
			}

			}
			short fluke = (short) Misc.getRandomInclusive(p.getLevel());
			return factor > fluke;
		}

		public static boolean canPickpocket(Player player, NPC npc) {
				Optional<Pickpocketable> pickpocket = Pickpocketable.get(npc.getId());

				if (pickpocket.isPresent()) {
					return true;
				}
				return false;
			}

		/**
		 * Represents an npc which can be pickpocketed ingame.
		 *
		 * @author Professor Oak
		 */
		// TODO: Add the npc ids for the ones that are commented out.
		public enum Pickpocketable {
			MAN_WOMAN(1, 8, 5, 1,
					new Item[] {
							new Item(COINS, 352)
					},
					/*
					Man
					 */
					NpcID.MAN_3014,
					NpcID.MAN_3106,
					NpcID.MAN_3107,
					NpcID.MAN_3108,
					NpcID.MAN_3109,
					NpcID.MAN_3110,
					NpcID.MAN_3261,
					NpcID.MAN_3264,
					NpcID.MAN_3265,
					NpcID.MAN_3266,
					NpcID.MAN_3298,
					NpcID.MAN_3652,
					NpcID.MAN_6815,
					NpcID.MAN_6818,
					NpcID.MAN_6987,
					NpcID.MAN_6988,
					NpcID.MAN_6989,
					NpcID.STUDENT_3634,
					/*
					Woman
					 */
					NpcID.WOMAN_3015,
					NpcID.WOMAN_3111,
					NpcID.WOMAN_3112,
					NpcID.WOMAN_3113,
					NpcID.WOMAN_3268,
					NpcID.WOMAN_3299,
					NpcID.WOMAN_6990,
					NpcID.WOMAN_6991,
					NpcID.WOMAN_6992,
					NpcID.WARRIOR_WOMAN
			),
			FARMER(10, 15, 5, 1,
					new Item[] {
							new Item(COINS, 1023),
							new Item(POTATO_SEED)
					},
					NpcID.FARMER,
					NpcID.FARMER_3243,
					NpcID.FARMER_3244
			),
			FEMALE_HAM_MEMBER(15, 19, 4, 3,
					new Item[] {
							new Item(BUTTONS), new Item(RUSTY_SWORD), new Item(DAMAGED_ARMOUR), new Item(FEATHER, 5), new Item(BRONZE_ARROW), new Item(BRONZE_AXE), new Item(BRONZE_DAGGER),
							new Item(BRONZE_PICKAXE), new Item(COWHIDE), new Item(IRON_AXE), new Item(IRON_PICKAXE), new Item(LEATHER_BOOTS), new Item(LEATHER_GLOVES), new Item(LEATHER_BODY),
							new Item(LOGS), new Item(THREAD), new Item(RAW_ANCHOVIES), new Item(LOGS), new Item(RAW_CHICKEN), new Item(IRON_ORE), new Item(COAL), new Item(STEEL_ARROW, 2),
							new Item(STEEL_AXE), new Item(STEEL_PICKAXE), new Item(KNIFE), new Item(NEEDLE), new Item(STEEL_DAGGER), new Item(TINDERBOX), new Item(UNCUT_JADE), new Item(UNCUT_OPAL),
							new Item(COINS, 853), new Item(HAM_GLOVES), new Item(HAM_CLOAK), new Item(HAM_BOOTS), new Item(HAM_SHIRT), new Item(HAM_ROBE), new Item(HAM_LOGO), new Item(HAM_HOOD),
							new Item(GRIMY_GUAM_LEAF), new Item(GRIMY_MARRENTILL), new Item(GRIMY_TARROMIN), new Item(GRIMY_HARRALANDER)
					},
					NpcID.HAM_MEMBER
			),
			MALE_HAM_MEMBER(20, 23, 4, 3,
					new Item[] {
							new Item(BUTTONS), new Item(RUSTY_SWORD), new Item(DAMAGED_ARMOUR), new Item(FEATHER, 5), new Item(BRONZE_ARROW), new Item(BRONZE_AXE), new Item(BRONZE_DAGGER),
							new Item(BRONZE_PICKAXE), new Item(COWHIDE), new Item(IRON_AXE), new Item(IRON_PICKAXE), new Item(LEATHER_BOOTS), new Item(LEATHER_GLOVES), new Item(LEATHER_BODY),
							new Item(LOGS), new Item(THREAD), new Item(RAW_ANCHOVIES), new Item(LOGS), new Item(RAW_CHICKEN), new Item(IRON_ORE), new Item(COAL), new Item(STEEL_ARROW, 2),
							new Item(STEEL_AXE), new Item(STEEL_PICKAXE), new Item(KNIFE), new Item(NEEDLE), new Item(STEEL_DAGGER), new Item(TINDERBOX), new Item(UNCUT_JADE), new Item(UNCUT_OPAL),
							new Item(COINS, 853), new Item(HAM_GLOVES), new Item(HAM_CLOAK), new Item(HAM_BOOTS), new Item(HAM_SHIRT), new Item(HAM_ROBE), new Item(HAM_LOGO), new Item(HAM_HOOD),
							new Item(GRIMY_GUAM_LEAF), new Item(GRIMY_MARRENTILL), new Item(GRIMY_TARROMIN),
							new Item(GRIMY_HARRALANDER)
					},
					NpcID.HAM_MEMBER_2541
			),
			AL_KHARID_WARRIOR(25, 26, 5, 2,
					new Item[] {
							new Item(COINS, 853)
					},
					NpcID.AL_KHARID_WARRIOR
			),
			ROGUE(32, 36, 5, 2,
					new Item[] {
							new Item(COINS, 853), new Item(LOCKPICK), new Item(IRON_DAGGER_P_),
							new Item(JUG_OF_WINE), new Item(AIR_RUNE, 8)
					},
					NpcID.ROGUE
			),
			CAVE_GOBLIN(36, 40, 5, 1,
					new Item[] {
							new Item(COINS, 1855), new Item(IRON_ORE), new Item(TINDERBOX), new Item(SWAMP_TAR), new Item(OIL_LANTERN), new Item(TORCH), new Item(GREEN_GLOOP_SOUP),
							new Item(FROGSPAWN_GUMBO), new Item(FROGBURGER), new Item(COATED_FROGS_LEGS), new Item(BAT_SHISH), new Item(FINGERS), new Item(BULLSEYE_LANTERN),
							new Item(CAVE_GOBLIN_WIRE)
					},
					NpcID.CAVE_GOBLIN,
					NpcID.CAVE_GOBLIN_2269,
					NpcID.CAVE_GOBLIN_2270,
					NpcID.CAVE_GOBLIN_2271,
					NpcID.CAVE_GOBLIN_2272,
					NpcID.CAVE_GOBLIN_2273,
					NpcID.CAVE_GOBLIN_2274,
					NpcID.CAVE_GOBLIN_2275,
					NpcID.CAVE_GOBLIN_2276,
					NpcID.CAVE_GOBLIN_2277,
					NpcID.CAVE_GOBLIN_2278,
					NpcID.CAVE_GOBLIN_2279,
					NpcID.CAVE_GOBLIN_2280,
					NpcID.CAVE_GOBLIN_2281,
					NpcID.CAVE_GOBLIN_2282,
					NpcID.CAVE_GOBLIN_2283,
					NpcID.CAVE_GOBLIN_2284,
					NpcID.CAVE_GOBLIN_2285
			),
			MASTER_FARMER(38, 43, 5, 3,
					new Item[] {
							new Item(POTATO_SEED, 12), new Item(ONION_SEED, 8), new Item(CABBAGE_SEED, 5), new Item(TOMATO_SEED, 4), new Item(HAMMERSTONE_SEED, 4), new Item(BARLEY_SEED, 4),
							new Item(MARIGOLD_SEED, 4), new Item(ASGARNIAN_SEED, 4), new Item(JUTE_SEED, 4), new Item(REDBERRY_SEED, 4), new Item(NASTURTIUM_SEED, 4), new Item(YANILLIAN_SEED, 4),
							new Item(CADAVABERRY_SEED, 4), new Item(SWEETCORN_SEED, 4), new Item(ROSEMARY_SEED, 4), new Item(DWELLBERRY_SEED, 3), new Item(GUAM_SEED, 3), new Item(WOAD_SEED, 3),
							new Item(KRANDORIAN_SEED, 3), new Item(STRAWBERRY_SEED, 3), new Item(LIMPWURT_SEED, 3), new Item(MARRENTILL_SEED, 3), new Item(JANGERBERRY_SEED, 3),
							new Item(TARROMIN_SEED, 2), new Item(WILDBLOOD_SEED, 2), new Item(WATERMELON_SEED, 2), new Item(HARRALANDER_SEED, 2), new Item(RANARR_SEED, 1),
							new Item(WHITEBERRY_SEED, 2), new Item(TOADFLAX_SEED, 2), new Item(MUSHROOM_SPORE, 2), new Item(IRIT_SEED, 2), new Item(BELLADONNA_SEED, 2), new Item(POISON_IVY_SEED, 2),
							new Item(AVANTOE_SEED, 1), new Item(CACTUS_SEED, 1), new Item(KWUARM_SEED, 1), new Item(SNAPDRAGON_SEED, 1), new Item(CADANTINE_SEED, 1), new Item(LANTADYME_SEED, 1),
							new Item(DWARF_WEED_SEED, 1), new Item(TORSTOL_SEED, 1),
					},
					NpcID.MASTER_FARMER,
					NpcID.MASTER_FARMER_5731
			),
			GUARD(40, 47, 5, 2,
					new Item[] {
							new Item(COINS, 1855)
					},
					NpcID.GUARD_1546,
					NpcID.GUARD_1547,
					NpcID.GUARD_1548,
					NpcID.GUARD_1549,
					NpcID.GUARD_1550,
					NpcID.GUARD_3010,
					NpcID.GUARD_3011,
					NpcID.GUARD_3254,
					NpcID.GUARD_3269,
					NpcID.GUARD_3270,
					NpcID.GUARD_3271,
					NpcID.GUARD_3272,
					NpcID.GUARD_3273,
					NpcID.GUARD_3274,
					NpcID.GUARD_3283,
					NpcID.GUARD_4522,
					NpcID.GUARD_4523,
					NpcID.GUARD_4524,
					NpcID.GUARD_4525,
					NpcID.GUARD_4526,
					NpcID.GUARD_5418,

					NpcID.GUARD,
					NpcID.GUARD_398,
					NpcID.GUARD_399,
					NpcID.GUARD_400,

					NpcID.KOUREND_GUARD_7016,
					NpcID.KOUREND_HEAD_GUARD
			),
			FREMENNIK_CITIZEN(45, 65, 5, 2,
					new Item[] {
							new Item(COINS, 2341)
					},
					NpcID.FREIDIR,
					NpcID.INGA,
					NpcID.LANZIG,
					NpcID.SASSILIK_3945,
					NpcID.SIGMUND_5322,
					NpcID.FEANOR,
					NpcID.EARWEN,
					NpcID.HENGEL,
					NpcID.TWIG_4133,
					NpcID.TATIE,
					NpcID.CIRDAN
			),
			BEARDED_POLLNIVNIAN_BANDIT(45, 65, 5, 5,
					new Item[] {new Item(COINS, 3587)}
					/*
					TODO: find these? (probably there are in BANDIT)
					 */
			),
			DESERT_BANDIT(53, 80, 5, 3,
					new Item[] {
							new Item(COINS, 6482),
							new Item(ANTIPOISON_3_, 1),
							new Item(LOCKPICK, 1)
					},
					NpcID.BANDIT_695
			),
			KNIGHT(55, 84, 5, 3,
					new Item[] { new Item(COINS, 4211) },
					NpcID.KNIGHT_OF_ARDOUGNE,
					NpcID.KNIGHT_OF_ARDOUGNE_3300,
					NpcID.KNIGHT_OF_ARDOUGNE_8854
			),
			POLLNIVIAN_BANDIT(55, 84, 5, 5,
					new Item[] { new Item(COINS, 4821) }
					/*
					TODO: find these? (probably there are in BANDIT)
					 */
			),
			BANDIT(60, 65, 5, 5,
					new Item[] { new Item(COINS, 5444), new Item(ANTIPOISON_3_, 1), },
					NpcID.BANDIT,
					NpcID.BANDIT_734,
					NpcID.BANDIT_735,
					NpcID.BANDIT_736,
					NpcID.BANDIT_737
			),
			YANILLE_WATCHMAN(65, 137, 5, 3,
					new Item[] { new Item(COINS, 3219), new Item(BREAD) },
					NpcID.WATCHMAN
			),
			MENAPHITE_THUG(65, 137, 5, 5,
					new Item[] { new Item(COINS, 4855) },
					NpcID.MENAPHITE_THUG_3550
			),
			PALADIN(70, 152, 5, 3,
					new Item[] { new Item(COINS, 80), new Item(CHAOS_RUNE, 2) },
					NpcID.PALADIN_3293,
					NpcID.PALADIN_3294,
					NpcID.PALADIN_8853
			),
			GNOME(75, 199, 5, 2,
					new Item[] { new Item(COINS, 7202), new Item(EARTH_RUNE), new Item(GOLD_ORE), new Item(FIRE_ORB), new Item(SWAMP_TOAD), new Item(KING_WORM) },
					NpcID.GNOME_CHILD,
					NpcID.GNOME_CHILD_6078,
					NpcID.GNOME_CHILD_6079,
					NpcID.GNOME_WOMAN,
					NpcID.GNOME_WOMAN_6087,
					NpcID.GNOME_5130,
					NpcID.GNOME_6094,
					NpcID.GNOME_6095,
					NpcID.GNOME_6096
			),
			HERO(80, 240, 5, 2,
					new Item[] { new Item(COINS, 9709), new Item(DEATH_RUNE), new Item(BLOOD_RUNE), new Item(FIRE_ORB), new Item(DIAMOND), new Item(JUG_OF_WINE), new Item(GOLD_ORE) },
					NpcID.HERO
			);

			static Map<Integer, Pickpocketable> pickpockets = new HashMap<>();

			static {
				for (Pickpocketable p : Pickpocketable.values()) {
					for (int i : p.getNpcs()) {
						pickpockets.put(i, p);
					}
				}
			}

			private final int level;
			private final int exp;
			private final int stunTime;
			private final int stunDamage;
			private final Item[] rewards;
			private final int[] npcs;

			Pickpocketable(int level, int exp, int stunTime, int stunDamage, Item[] rewards, int... npcs) {
				this.level = level;
				this.exp = exp;
				this.stunTime = stunTime;
				this.stunDamage = stunDamage;
				this.rewards = rewards;
				this.npcs = npcs;
			}

			public static Optional<Pickpocketable> get(int npcId) {
				Pickpocketable p = pickpockets.get(npcId);
				if (p != null) {
					return Optional.of(p);
				}
				return Optional.empty();
			}

			public int getLevel() {
				return level;
			}

			public int getExp() {
				return exp;
			}

			public int getStunTime() {
				return stunTime;
			}

			public int getStunDamage() {
				return stunDamage;
			}

			public Item[] getRewards() {
				return rewards;
			}

			public int[] getNpcs() {
				return npcs;
			}
		}
	}

	/**
	 * Handles thieving from stalls.
	 *
	 * @author Professor Oak
	 */
	public static final class StallThieving {

		/**
		 * Checks if we're attempting to steal from a stall based on the clicked
		 * object.
		 *
		 * @param player
		 * @param object
		 * @return
		 */
		/*public static boolean init(Player player, GameObject object) {
			/*TaskManager.submit(new Task(1) {
				@Override
				public void execute() {
					thieve(player, object);
					stop();
				}
			});*/
			//thieve(player, object);
			//return Stall.get(object.getId()).isPresent();
		//}*/

		public static boolean init(Player player, GameObject object) {
			Optional<Stall> stall = Stall.get(object.getId());
			/*if (player.getPosition().getDistance(new Position(object.getPosition().getX(), object.getPosition().getY(), player.getPosition().getZ())) >= object.getSize()) {
				return false;
			}*/
			if (!ClippedMapObjects.exists(object) || ObjectManager.existsAt(ObjectID.MARKET_STALL, object.getPosition())) {
				return true;
			}
			if (stall.isPresent()) {
				if (player.getInventory().countFreeSlots() < 1) {
					DialogueManager.sendStatement(player, "You need to have free inventory slots to steal from stalls.");
					return true;
				}
				if (player.getCombat().isUnderAttack()) {
					DialogueManager.sendStatement(player, "You can't steal from stalls during combat!");
					return true;
				}
				if (object.getId() == 26757 && (player.getWildernessLevel() <= 0 && !AreaManager.LA_ISLA_EBANA.contains(player))) {
					return false;
				}
				if (PlayerExtKt.tryRandomEventTrigger(player, 1.3F))
					return true;

				// Make sure we have the required thieving level..
				if (player.getSkillManager().getCurrentLevel(Skill.THIEVING) >= stall.get().getReqLevel()) {
					boolean dodgyNecklace = player.getEquipment().contains(21143);

					/*int time = stall.get().getReqLevel() > 60 ? 1200 : 1200;
					if (dodgyNecklace) {
						time -= 1200;
					}*/
					int time = dodgyNecklace ? 1800 : 2400;
					// Make sure we aren't spam clicking..
					if (player.getClickDelay().elapsed(time)) {

						// Reset click delay..
						player.getClickDelay().reset();

						AtomicBoolean stopAction = new AtomicBoolean(false);


						// You can offer the thieving master some money so they don't attack you.
						// If you are wearing rogue set or the thieving skillcape they will never try to attack you.
						if (player.getEquipment().getItems()[EquipmentConstants.CAPE_SLOT].getId() != 9777 && player.getEquipment().getItems()[EquipmentConstants.CAPE_SLOT].getId() != 9778
							&& !EquipmentUtil.isWearingRogueSet(player)) {

							// Market Guards
							// The guards will only attempt to fight you if they are in line of sight.
							player.getLocalNpcs().stream().filter(npc -> npc.getId() == NpcID.MARKET_GUARD_5732 || npc.getId() == NpcID.MARKET_GUARD
									|| npc.getId() == NpcID.KNIGHT_OF_ARDOUGNE
									|| npc.getId() == NpcID.MARKET_GUARD_3949 || npc.getId() == NpcID.KOUREND_GUARD_7016)
									.min(Comparator.comparingInt(npc -> npc.getPosition().getDistance(player.getPosition())))
									.filter(guard -> guard.getHitpoints() > 0)
									.filter(guard -> !guard.getCombat().isInCombat())
									.ifPresent(marketGuard -> {

										// Line of sight

										if (!LineOfSight.withinSight(marketGuard, player, 4)) {
											return;
										}

										// If you are in the zone for more than 5 minutes they will no longer be attacking you.
										if (marketGuard.getAggressionTolerancePolicy() == MonsterAggressionTolerancePolicy.IN_VICINITY &&
												player.getCombat().getAggressivityTimer().finished() &&
												player.getAggressionTolerance().finished()) {
											return;
										}

										marketGuard.say("Hey! Get your hands off there!");
										marketGuard.setEntityInteraction(player);
										marketGuard.getCombat().initiateCombat(player);
										stopAction.set(true);
										return; // Is it needed?
									});
						}

						// Face stall..
						player.setPositionToFace(object.getPosition());

						// Stops your action in case a guard catches you
						if (stopAction.get()) {
							stopAction.set(false);
							return true;
						}

						// Reset flag
						player.getPacketSender().sendMinimapFlagRemoval();

						// Perform animation..
						player.performAnimation(THIEVING_ANIMATION);

						// Play sound
						player.getPacketSender().sendSound(Sounds.STEAL_STALL);

						boolean glovesOfSilence = player.getEquipment().contains(10075) && Misc.getRandomInclusive(5) == 1;

						// Rare item
						if (Misc.getRandomInclusive((450 - stall.get().getReqLevel())) == 1) {
							int reward = Misc.randomElement(THIVING_STALL_BONUS_ITEMS);
							Item item = new Item(reward, Misc.random(15_000_000));

							AchievementManager.processFor(AchievementType.DOUBLE_LUCK, player);
							PlayerUtil.broadcastMessage(
									"<img=765>@or2@ " + player.getUsername() + "</col> has just won bonus @dre@" + NumberFormat.getInstance().format(item.getAmount()) + " " + item.getDefinition().getName() + "</col> from a thieving stall! ");
							if (!player.getGameMode().isUltimate()) {
								if (player.getInventory().isFull()) {
									player.getPacketSender().sendMessage("The reward " + (player.getGameMode().isUltimate() ? "is dropped under you" : "was sent to your bank") + " because you don't have enough inventory space.");
									BankUtil.addToBank(player, new Item(reward, 1));
								} else {
									player.getInventory().add(item);
								}
							} else {
								ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(reward, 1));
							}
						} else {
							// Delay one tick
							TaskManager.submit(new Task(1) {
								@Override
								protected void execute() {
									stop();
									if (stall.get().getRewards() != null) {
										Item item = stall.get().getRewards()[Misc.getRandomInclusive(stall.get().getRewards().length - 1)];
										player.getInventory().add(item.getId(),
												item.getAmount() > 1 ? Misc.getRandomInclusive(item.getAmount()) : 1);
									}

									//money /= 4;

								/*if(money < 3) {
									money = 3;
								}*/
									int money = (int) (stall.get().getMoneyReward() * 1.15);
									if (money <= 10) {
										money = (int) (1000 + Misc.getRandomDouble(1.15));
									}
									if (money > 3000) {
										money *= 5;
									}
									if (GameConstants.BONUS_DOUBLE_REWARDS) {
										money *= 2;
									}
									if (PlayerUtil.isMember(player)) {
										money *= 1.25;
									}

									// Skill random messages
									if (Misc.getRandomInclusive(20) == Misc.getRandomInclusive(20) && player.getSkillManager().getMaxLevel(Skill.THIEVING) < SkillUtil.maximumAchievableLevel()) {
										stallMessage(player);
									}
									Item bonusReward = new Item(THIEVING_STALL_RANDOMS[Misc.getRandomInclusive(THIEVING_STALL_RANDOMS.length - 1)], 1);
									switch (stall.get()) {
										case BAKERS_STALL:
											AchievementManager.processFor(AchievementType.NEED_FOOD, player);
											player.getInventory().add(new Item(CAKE));
											break;
										case SILK_STALL:
											player.getInventory().add(new Item(SILK));
											break;
										case SILVER_STALL:
											player.getInventory().add(new Item(UNSTRUNG_SYMBOL));
											break;
										case SPICE_STALL:
											player.getInventory().add(new Item(SPICE));
											break;
										case FUR_STALL:
											AchievementManager.processFor(AchievementType.NEED_FUR, player);
											player.getInventory().add(new Item(FUR));
											break;
										case GEM_STALL:
											AchievementManager.processFor(AchievementType.NEED_GEM, player);
											player.getInventory().add(GEM_STALL_REWARDS.nextObject().get());
											break;
									/*case TEA_STALL:
										player.getInventory().add(new Item(CUP_OF_TEA));
										break;*/
										case SCIMITAR_STALL:
										case CRAFTING_STALL:
											player.getInventory().add(bonusReward);
											AchievementManager.processFor(AchievementType.RANDOM_STUFF, player);
											player.getCollectionLog().createOrUpdateEntry(player,  "Stall Miscellenous", bonusReward);
											break;
										case WILDERNESS_STALL:
											AchievementManager.processFor(AchievementType.RISK_ACTIVITY, player);
											break;
										default:
											break;
									}
									if (glovesOfSilence) {
										money *= 2;
										player.getPacketSender().sendMessage(
												"Your gloves of silence allow you to double steal from the stall!");
									}
									player.getInventory().add(new Item(COINS, money));
								}
							});
						}

						// TODO: INSTEAD OF EARNING INSTANT CASH U GET CASH BAGS AND U CAN OPEN THEM FOR RANDOM AMOUNTS OF CASH
						// SMALL BAG: 5-10K
						// MED BAG: 25K-40K
						// ETC
						// Delay one tick
						TaskManager.submit(new Task(1) {
							@Override
							protected void execute() {
								stop();

								// IMAGES 90-100 (Y)
								// Add pet..
								PetHandler.onSkill(player, Skill.THIEVING);

								player.getPoints().increase(AttributeManager.Points.STALL_STEALS, 1); // Increase points

								if ((player.getPoints().get(AttributeManager.Points.STALL_STEALS) % 100) == 0) {
									player.sendMessage("Total stall count: @red@" + player.getPoints().get(AttributeManager.Points.STALL_STEALS) + "</col>.");
								}

								// Add experience..
								player.getSkillManager().addExperience(Skill.THIEVING,
										(int) (stall.get().getExp() * 1.3));
								SkillTaskManager.perform(player, object.getId(), 1, SkillMasterType.THIEVING);

								// Respawn stall..
								for (StallDefinition stallDef : stall.get().getStalls()) {
									if (stallDef.getObjectId() == object.getId()) {
										if (stallDef.getReplacement().isPresent()) {
											TaskManager.submit(new TimedObjectReplacementTask(object,
													DynamicGameObject.createPublic(stallDef.getReplacement().get(), object.getPosition(),
															object.getObjectType(), object.getFace()),
													stall.get().getRespawnTicks()));
										}
										break;
									}
								}
							}
						});


						// End of Delay
					} else {
						player.getPacketSender().sendMessage("You must wait a few more seconds before trying to steal from the " + Misc.formatName(stall.get().name().toLowerCase()) +".", 1000);
						//return false;
					}
				} else {
					DialogueManager.sendStatement(player, "You need a Thieving level of at least "
							+ Integer.toString(stall.get().getReqLevel()) + " to do this.");
				}
				return true;
			}
			return false;
		}

		/**
		 * Represents a stall which can be stolen from using the Thieving skill.
		 * Stall(StallDefinition[] stalls, reqLevel, exp, respawnTicks, Item...
		 * rewards) {
		 *
		 * @author Professor Oak
		 */
		public enum Stall {
			BAKERS_STALL(new StallDefinition[] { new StallDefinition(11730, Optional.of(634)), }, 1, 16, 4, 2500, new Item(-1)), // Level 1

			SILK_STALL(new StallDefinition[] { new StallDefinition(11729, Optional.of(634)) }, 20, 24, 4, 4000, new Item(-1)), // Level 20

			FUR_STALL(new StallDefinition[] { new StallDefinition(11732, Optional.of(634)),
					new StallDefinition(4278, Optional.of(634)) }, 35, 36, 4, 6500, new Item(-1)), // Level 35

			SILVER_STALL(new StallDefinition[] { new StallDefinition(11734, Optional.of(634)),
					new StallDefinition(6164, Optional.of(6984)), }, 50, 54, 4, 8000, new Item(-1)), // Level 50

			SPICE_STALL(
					new StallDefinition[] { new StallDefinition(11733, Optional.of(634)),
							new StallDefinition(6162, Optional.of(6984)), },
					65, 70, 4, 9500, new Item(-1)), // Level 65

			GEM_STALL(new StallDefinition[] { new StallDefinition(11731, Optional.of(634)) }, 75, 92, 4, 11500, new Item(-1)), // Level 75

			WILDERNESS_STALL(new StallDefinition[] { new StallDefinition(26757, Optional.empty()) }, 85, 160, 4, 20000, new Item(UNCUT_SAPPHIRE), new Item(UNCUT_RUBY),
					new Item(UNCUT_DIAMOND), new Item(UNCUT_EMERALD), new Item(UNCUT_DRAGONSTONE), new Item(ADAMANTITE_ORE), new Item(RUNITE_ORE)), // Level 85

			CRAFTING_STALL(
					new StallDefinition[] { new StallDefinition(4874, Optional.empty()),
							new StallDefinition(6166, Optional.empty()) },
					99, 92, 4, 11500, new Item(-1)), // Level 1

			TEA_STALL(new StallDefinition[] { new StallDefinition(635, Optional.of(634)),
					new StallDefinition(6574, Optional.of(6573)), new StallDefinition(20350, Optional.of(20349)) }, 5, 20,
					4, 11500, new Item(CUP_OF_TEA)), // Level 55

			MONKEY_STALL(new StallDefinition[] { new StallDefinition(4875, Optional.empty()) }, 55, 16, 4, 4100,
					new Item(BANANA)), // Level 55

			MONKEY_GENERAL_STALL(new StallDefinition[] { new StallDefinition(4876, Optional.empty()) }, 16, 16, 4, 3250,
					new Item(POT), new Item(TINDERBOX), new Item(HAMMER)),



			WINE_STALL(new StallDefinition[] { new StallDefinition(14011, Optional.of(634)) }, 22, 27, 4, 3400,
					new Item(JUG_OF_WATER), new Item(JUG_OF_WINE), new Item(GRAPES), new Item(EMPTY_JUG),
					new Item(BOTTLE_OF_WINE)),

			SEED_STALL(new StallDefinition[] { new StallDefinition(7053, Optional.of(634)), }, 27, 10, 4, 2500,
					new Item(POTATO_SEED, 12), new Item(ONION_SEED, 11), new Item(CABBAGE_SEED, 10),
					new Item(TOMATO_SEED, 9), new Item(SWEETCORN_SEED, 7), new Item(STRAWBERRY_SEED, 5),
					new Item(WATERMELON_SEED, 3), new Item(BARLEY_SEED, 5), new Item(HAMMERSTONE_SEED, 5),
					new Item(ASGARNIAN_SEED, 5), new Item(JUTE_SEED, 5), new Item(YANILLIAN_SEED, 5),
					new Item(KRANDORIAN_SEED, 5), new Item(WILDBLOOD_SEED, 3), new Item(MARIGOLD_SEED, 4),
					new Item(ROSEMARY_SEED, 4), new Item(NASTURTIUM_SEED, 4)), // Level 27

			FISH_STALL(new StallDefinition[] { new StallDefinition(4277, Optional.of(4276)),
					new StallDefinition(4707, Optional.of(4276)), new StallDefinition(4705, Optional.of(4276)) }, 42,
					42, 4, 3550, new Item(RAW_SALMON), new Item(RAW_TROUT), new Item(RAW_TUNA)),

			VEGETABLE_STALL(new StallDefinition[] { new StallDefinition(4706, Optional.of(634)),
					new StallDefinition(4706, Optional.of(4706)), new StallDefinition(4706, Optional.of(4276)) }, 35,
					33, 4, 2867, new Item(TOMATO), new Item(CABBAGE), new Item(ONION), new Item(POTATO), new Item(GARLIC)),

			VEGETABLE_STALL2(new StallDefinition[] { new StallDefinition(4708, Optional.of(634)),
					new StallDefinition(4706, Optional.of(4706)), new StallDefinition(4706, Optional.of(4276)) }, 35,
					33, 4, 2867, new Item(TOMATO), new Item(CABBAGE), new Item(ONION), new Item(POTATO), new Item(GARLIC)),

			/*SPICE_STALL(new StallDefinition[] { new StallDefinition(11733, Optional.of(634)),
					new StallDefinition(6572, Optional.of(6573)), new StallDefinition(20348, Optional.of(20349)), }, 65,
					81, 4, 4700, new Item(SPICE)),*/

			MAGIC_STALL(new StallDefinition[] { new StallDefinition(4877, Optional.empty()), }, 90, 100, 4, 5400,
					new Item(FIRE_RUNE, 5), new Item(AIR_RUNE, 5), new Item(DEATH_RUNE, 2), new Item(221), new Item(771), new Item(4561, 2), new Item (577), new Item(579), new Item(WATER_RUNE, 5)),

			SCIMITAR_STALL(new StallDefinition[] { new StallDefinition(4878, Optional.empty()) }, 99, 100, 4, 6000,
					new Item(IRON_SCIMITAR), new Item(1321),new Item(1323),new Item(1329));



			private static Map<Integer, Stall> map = new HashMap<Integer, Stall>();

			static {
				for (Stall stall : Stall.values()) {
					for (StallDefinition def : stall.getStalls()) {
						map.put(def.getObjectId(), stall);
					}
				}
			}

			private final StallDefinition[] stalls;
			private final int reqLevel;
			private final int exp;
			private final int respawnTicks;
			private final int moneyReward;
			private final Item[] rewards;

			Stall(StallDefinition[] stalls, int reqLevel, int exp, int respawnTicks, int money,  Item... rewards) {
				this.stalls = stalls;
				this.reqLevel = reqLevel;
				this.exp = exp;
				this.respawnTicks = respawnTicks;
				this.moneyReward = money;
				this.rewards = rewards;
			}

			public static Optional<Stall> get(int objectId) {
				Stall stall = map.get(objectId);
				if (stall != null)
					return Optional.of(stall);
				return Optional.empty();
			}

			public StallDefinition[] getStalls() {
				return stalls;
			}

			public int getReqLevel() {
				return reqLevel;
			}

			public int getExp() {
				return exp;
			}

			public int getRespawnTicks() {
				return respawnTicks;
			}

			public int getMoneyReward() {
				return moneyReward;
			}

			public Item[] getRewards() {
				return rewards;
			}
		}

		/**
		 * Represents a stall's definition.
		 *
		 * @author Professor Oak
		 */
		public static final class StallDefinition {
			/**
			 * The stall's object id.
			 */
			private final int objectId;

			/**
			 * The replacement object for when this stall temporarily despawns.
			 */
			private final Optional<Integer> replacement;

			public StallDefinition(int objectId, Optional<Integer> replacement) {
				this.objectId = objectId;
				this.replacement = replacement;
			}

			public int getObjectId() {
				return objectId;
			}

			public Optional<Integer> getReplacement() {
				return replacement;
			}
		}
	}
}