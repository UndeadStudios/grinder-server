package com.grinder.game.content.skill.skillable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.grinder.game.content.miscellaneous.PetHandler;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.impl.fletching.Fletching;
import com.grinder.game.content.skill.skillable.impl.herblore.PotionBrewing;
import com.grinder.game.content.skill.task.SkillMasterType;
import com.grinder.game.content.skill.task.SkillTaskManager;
import com.grinder.game.content.task_new.DailyTask;
import com.grinder.game.content.task_new.PlayerTaskManager;
import com.grinder.game.content.task_new.WeeklyTask;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.AnimationLoop;
import com.grinder.game.model.GraphicsLoop;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.RequiredItem;
import com.grinder.game.model.Skill;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.model.sound.SoundLoop;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;

/**
 * An implementation of {@link DefaultSkillable}.
 * <p>
 * This sub class handles the creation of an item. It's used by many skills such
 * as Fletching.
 *
 * @author Professor Oak
 */
public class ItemCreationSkillable extends DefaultSkillable {

	/**
	 * A {@link List} containing all the {@link RequiredItem}s.
	 */
	private final List<RequiredItem> requiredItems;

	/**
	 * The item we're making.
	 */
	private final Item product;
	/**
	 * The {@link AnimationLoop} the player will perform whilst performing this
	 * skillable.
	 */
	private final AnimationLoop animLoop;
	/**
	 * The {@link GraphicsLoop} the player will perform whilst performing this
	 * skillable.
	 */
	private final GraphicsLoop gfxLoop;
	/**
	 * The sound the player will perform whilst performing this skillable.
	 */
	private final SoundLoop soundLoop;
	/**
	 * The level required to make this item.
	 */
	private final int requiredLevel;
	/**
	 * The experience a player will receive in the said skill for making this.
	 * item.
	 */
	private int experience;
	/**
	 * The skill to reward the player experience in.
	 */
	private final Skill skill;
	/**
	 * The messages to loop while creating the item.
	 */
	private final String messageLoop;
	/**
	 * The cycles required to finish one item production.
	 */
	private final int cyclesRequired;
	/**
	 * The amount to make.
	 */
	private int amount;
	/*
	 * Messages that are sent to the player while training Crafting/Fletching skill
	 */
	private static final String[][] CRAFTING_MESSAGES = {
		{ "@whi@You can train in the Crafting guild after reaching level 60 Crafting!" },
		{ "@whi@Check out the skill guides or Wiki for fastest XP methods." },
		{ "@whi@You can take a Crafting skill task from your master for bonus rewards." },
		{ "@whi@You can use the Tanner in the Crafting guild or in Al-Kharid to tan hides." },
		{ "@whi@Crafting in the Wilderness Resource Area provides 20% bonus experience gain!" },
		{ "@whi@Crafting with the skillcape equipped will give you 20% bonus experience gain!" },
	};

	private static final String[][] FLETCHING_MESSAGES = {
		{ "@whi@You can take a look at Fletching skill guide or Wiki for fastest XP methods." },
		//{ "@whi@Every equipped Angler gear piece increases your experience gain in Fishing skill!" },
		{ "@whi@You can take a Fletching skill task from your master for bonus rewards." },
		{ "@whi@Fletching in the Wilderness Resource Area provides 20% bonus experience gain!" },
		{ "@whi@Fletching with the skillcape equipped will give you 20% bonus experience gain!" },

	};

	public static String currentMessage;

	public static void sendCraftingRandomMessages(Player player) {
		currentMessage = CRAFTING_MESSAGES[Misc.getRandomInclusive(CRAFTING_MESSAGES.length - 1)][0];
		player.getPacketSender().sendMessage("<img=779> " + currentMessage);
	}

	public static void sendFletchingRandomMessages(Player player) {
		currentMessage = FLETCHING_MESSAGES[Misc.getRandomInclusive(FLETCHING_MESSAGES.length - 1)][0];
		player.getPacketSender().sendMessage("<img=779> " + currentMessage);
	}

	public ItemCreationSkillable(List<RequiredItem> requiredItems, Item product, int amount,
								 AnimationLoop animLoop, GraphicsLoop gfxLoop, SoundLoop soundLoop, int requiredLevel, int experience, Skill skill, String messageLoop, int cyclesRequired) {
		this.requiredItems = requiredItems;
		this.product = product;
		this.amount = amount;
		this.animLoop = animLoop;
		this.gfxLoop = gfxLoop;
		this.soundLoop = soundLoop;
		this.requiredLevel = requiredLevel;
		this.experience = experience;
		this.skill = skill;
		this.messageLoop = messageLoop;
		this.cyclesRequired = cyclesRequired;
	}
	public ItemCreationSkillable(List<RequiredItem> requiredItems,
								 Item product,
								 int amount,
								 AnimationLoop animLoop,
								 SoundLoop soundLoop,
								 int requiredLevel,
								 int experience, Skill skill, String messageLoop, int cyclesRequired) {
		this(requiredItems, product, amount, animLoop, null, soundLoop, requiredLevel, experience, skill, messageLoop, cyclesRequired);
	}

	public ItemCreationSkillable(List<RequiredItem> requiredItems,
								 Item product,
								 int amount,
								 AnimationLoop animLoop,
								 int requiredLevel,
								 int experience, Skill skill, String messageLoop, int cyclesRequired) {
		this(requiredItems, product, amount, animLoop, null, null, requiredLevel, experience, skill, messageLoop, cyclesRequired);
	}

	public ItemCreationSkillable(List<RequiredItem> requiredItems,
								 Item product,
								 int amount,
								 AnimationLoop animLoop,
								 GraphicsLoop gfxLoop,
								 int requiredLevel,
								 int experience, Skill skill, String messageLoop, int cyclesRequired) {
		this(requiredItems, product, amount, animLoop, gfxLoop, null, requiredLevel, experience, skill, messageLoop, cyclesRequired);
	}

	@Override
	public void startAnimationLoop(Player player) {
		Optional.ofNullable(animLoop).map(animationLoop -> new Task(animationLoop.getLoopDelay(), player, true) {
			@Override
			protected void execute() {
				player.performAnimation(animationLoop.getAnim());
			}
		}).ifPresent(animLoopTask -> {
			TaskManager.submit(animLoopTask);
			getTasks().add(animLoopTask);
		});
	}

	@Override
	public void startGraphicsLoop(Player player) {
		Optional.ofNullable(gfxLoop).map(gfxLoop -> new Task(gfxLoop.getLoopDelay(), player, true) {
			@Override
			protected void execute() {
				player.performGraphic(gfxLoop.getGraphic());
			}
		}).ifPresent(gfxLoopTask -> {
			TaskManager.submit(gfxLoopTask);
			getTasks().add(gfxLoopTask);
		});
	}

	@Override
	public void startSoundLoop(Player player) {
		Optional.ofNullable(soundLoop).map(soundLoop -> new Task(soundLoop.getLoopDelay(), player, true) {
			@Override
			protected void execute() {
				if (amount <= 0) {
					stop();
					return;
				}
				final Sound[] sounds = soundLoop.getSounds();
				final int soundId = sounds.length > 1 ? Misc.randomElement(sounds).getId() : sounds[0].getId();
				player.getPacketSender().sendAreaPlayerSound(soundId);
			}
		}).ifPresent(soundLoopTask -> {
			TaskManager.submit(soundLoopTask);
			getTasks().add(soundLoopTask);
		});
	}

	@Override
	public int cyclesRequired(Player player) {
		return cyclesRequired;
	}

	@Override
	public void onCycle(Player player) {
	}

	@Override
	public void finishedCycle(Player player) {
		// Decrement amount to make and stop if we hit 0.
		if (amount-- <= 0) {
			cancel(player);
		}

		filterRequiredItems(RequiredItem::isDelete).forEach(r -> player.getInventory().delete(r.getItem()));

		if (skill == Skill.CRAFTING) {
			if (product.getId() == ItemID.LIMESTONE_BRICK && Misc.random(100) < 33 && player.getSkillManager().getMaxLevel(Skill.CRAFTING) < 40) {
				product.setId(ItemID.ROCK);
				player.sendMessage("Your too heavy handed and smash the limestone into bits.");
			}
			if (product.getId() == ItemID.GRANITE_2KG_) {
				ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.GRANITE_500G_, 2)); // Make sure its safe to add it this way.
			}
		}

		// Add product
		player.getInventory().add(product.clone());


		if (skill == Skill.SMITHING) {

			if (product.getId() == ItemID.GOLD_BAR && player.getEquipment().contains(ItemID.GOLDSMITH_GAUNTLETS)) {
					player.getSkillManager().addExperience(skill, experience * 2.50);
			} else {
				player.getSkillManager().addExperience(skill, experience);
			}
			// Money making mechanism
			/*int gpAmount = requiredLevel * 110;
			if (player.getEquipment().contains(9795)) {
				gpAmount *= 1.5;
			} else if (player.getEquipment().contains(9797)) {
				gpAmount *= 1.9;
			}*/
//			TaskList.ProgressTask(player, 6, ItemDefinition.forId(product.getId()).getName());
			// Process skill task
			SkillTaskManager.perform(player, product.getId(), 1, SkillMasterType.SMITHING);

			// Add money making for smithing
			//player.getInventory().add(new Item(995, gpAmount));

			// Process achievement
			AchievementManager.processFor(AchievementType.HEAT_UP, 1, player);
		}
		else if (skill == Skill.HERBLORE) {

			// Process achievement
            AchievementManager.processFor(AchievementType.HERBLORER, player);

            // Process skill task
			SkillTaskManager.perform(player, product.getId(), 1, SkillMasterType.HERBLORE);

			// Send random skill messages
			if (Misc.getRandomInclusive(8) == Misc.getRandomInclusive(8) && player.getSkillManager().getMaxLevel(Skill.HERBLORE) < SkillUtil.maximumAchievableLevel()) {
				PotionBrewing.INSTANCE.sendSkillRandomMessages(player);
			}

		} else if (skill == Skill.CRAFTING) {
			// Process skill task
			SkillTaskManager.perform(player, product.getId(), 1, SkillMasterType.CRAFTING);

			// Send random skill messages
			if (Misc.getRandomInclusive(8) == Misc.getRandomInclusive(8) && player.getSkillManager().getMaxLevel(Skill.CRAFTING) < SkillUtil.maximumAchievableLevel()) {
				sendCraftingRandomMessages(player);
			}
		} else if (skill == Skill.FLETCHING) {
			// Process skill task
			int amount = product.getDefinition().getName().contains("arrow") ? 15 : 1;

			SkillTaskManager.perform(player, product.getId(), amount, SkillMasterType.FLETCHING);

			switch(product.getId()) {
				case ItemID.OAK_SHORTBOW:
					PlayerTaskManager.progressTask(player, DailyTask.FLETCH_OAK_SHORTBOW);
					break;
				case ItemID.WILLOW_SHORTBOW:
					PlayerTaskManager.progressTask(player, DailyTask.FLETCH_WILLOW_SHORTBOW);
					break;
			}
//			TaskList.ProgressTask(player, 7, ItemDefinition.forId(product.getId()).getName());

			// Send random skill messages
			if (Misc.getRandomInclusive(10) == Misc.getRandomInclusive(10) && player.getSkillManager().getMaxLevel(Skill.CRAFTING) < SkillUtil.maximumAchievableLevel()) {
				sendFletchingRandomMessages(player);
			}
		}


		// Add exp..
		if (skill != Skill.SMITHING) {
			player.getSkillManager().addExperience(skill, experience);
		}

		// Send message..
		String name = product.getDefinition().getName();

		if (product.getId() == 1609
				|| product.getId() == 1613
				|| product.getId() == 1607
				|| product.getId() == 1603
				|| product.getId() == 1605
				|| product.getId() == 1601
				|| product.getId() == 1615
				|| product.getId() == 6573
				|| product.getId() == 19493) {
			AchievementManager.processFor(AchievementType.GEM_FISSURE, player);
			AchievementManager.processFor(AchievementType.GEM_CUTTER, player);
			player.getPoints().increase(AttributeManager.Points.GEMS_CUT, 1); // Increase points
			PlayerTaskManager.progressTask(player, DailyTask.CUTTING_GEMS);
			PlayerTaskManager.progressTask(player, WeeklyTask.CUTTING_GEMS);
		}
		sendProductMessage(player, product.getId());

		if (messageLoop != null)
			player.sendMessage(messageLoop);
	}

	public void sendProductMessage(Player player, int productId) {
		if (productId == 2349) {
			player.getPacketSender().sendMessage("You retrieve a bar of bronze.");
			return;
		} else if (productId == 2351) {
			player.getPacketSender().sendMessage("You retrieve a bar of iron.");
			return;
		} else if (productId == 2353) {
			player.getPacketSender().sendMessage("You retrieve a bar of steel.");
			return;
		} else if (productId == 2355) {
			player.getPacketSender().sendMessage("You retrieve a bar of silver.");
			return;
		} else if (productId == 2357) {
			player.getPacketSender().sendMessage("You retrieve a bar of gold.");
			return;
		} else if (productId == 2359) {
			player.getPacketSender().sendMessage("You retrieve a bar of mithril.");
			return;
		} else if (productId == 2361) {
			player.getPacketSender().sendMessage("You retrieve a bar of adamantite.");
			return;
		} else if (productId == 2363) {
			player.getPacketSender().sendMessage("You retrieve a bar of runite.");
			return;
		}
		if (skill == Skill.SMITHING) {
			if (product.getAmount() > 1) {
				player.getPacketSender().sendMessage("You hammer the " + requiredItems.get(1).getItem().getDefinition().getName().toLowerCase() + " and make" + Misc.convertLessThanOneThousand(product.getAmount()).toLowerCase() + " " + product.getDefinition().getName().toLowerCase() + ".");
				return;
			} else {
				player.getPacketSender().sendMessage("You hammer the " + requiredItems.get(1).getItem().getDefinition().getName().toLowerCase() + " and make " + Misc.anOrA(product.getDefinition().getName()) + " " + product.getDefinition().getName().toLowerCase() + ".");
				return;
			}
		}
	}

	public String getActionName(Skill skill) {
		switch (skill.getName()) {
			case "Cooking":
				return "cook";
			case "Smithing":
				return "smith";
			case "Fletching":
				return "fletch";
			case "Crafting":
				return "craft";
			default:
				return "do";
		}
	}

	@Override
	public boolean hasRequirements(Player player) {
		// Validate amount..
		if (amount <= 0) {
			return false;
		}

		// Validate required items..
		// Check if we have the required ores..
		boolean hasItems = true;
		for (int i = 0; i < getRequiredItems().size(); i++) {
			if (!player.getInventory().containsAll(getRequiredItems().get(i).getItem().getId()) && getRequiredItems().size() == 1) {
				//System.out.println("UP");
					if (skill.getName().equals("Smithing")) {
						if (getRequiredItems().get(0).getItem().getAmount() == 0) {
							DialogueManager.sendStatement(player, "You don't have enough " + requiredItems.get(0).getItem().getDefinition().getName().toLowerCase() + " to smelt.");
						} else {
							DialogueManager.sendStatement(player, "You don't have enough " + requiredItems.get(1).getItem().getDefinition().getName().toLowerCase() + " to smelt.");
						}
						} else if (skill.getName().equals("Cooking")) {
							if (getRequiredItems().get(i).getItem().getAmount() == 0) {
								DialogueManager.sendStatement(player, "You don't have enough " + requiredItems.get(0).getItem().getDefinition().getName().toLowerCase() + " to cook!");
							}
					} else {
						DialogueManager.sendStatement(player, "You don't have enough " + requiredItems.get(0).getItem().getDefinition().getName().toLowerCase() + " to continue.");
					}
				hasItems = false;
				return false;
				} else { // Contains some of the required items, but not all below.
				if (getRequiredItems().size() != 1 && !player.getInventory().contains(getRequiredItems().get(0).getItem().getId())) {
					//System.out.println("UP 3nd");
						if (skill.getName().equals("Smithing")) {
							String prefix = Integer.toString(getRequiredItems().get(0).getItem().getAmount());
							String required = (getRequiredItems().get(1).getItem().getAmount() == 1 ? "" + Misc.anOrA(requiredItems.get(1).getItem().getDefinition().getName().toLowerCase()) + "" : Integer.toString(getRequiredItems().get(1).getItem().getAmount()));
							if (getRequiredItems().get(0).getItem().getAmount() > 1) {
								DialogueManager.sendStatement(player, "You need " + required + " " + requiredItems.get(1).getItem().getDefinition().getName().toLowerCase() + " and " + prefix + " " + requiredItems.get(0).getItem().getDefinition().getName().toLowerCase() + " to make " + Misc.anOrA(product.getDefinition().getName().toLowerCase()) + " " + product.getDefinition().getName().toLowerCase() + ".");
							} else {
								DialogueManager.sendStatement(player, "You need " + required + " " + requiredItems.get(1).getItem().getDefinition().getName().toLowerCase() + " and " + Misc.anOrA(requiredItems.get(0).getItem().getDefinition().getName().toLowerCase()) + " " + requiredItems.get(0).getItem().getDefinition().getName().toLowerCase() + " to make " + Misc.anOrA(product.getDefinition().getName().toLowerCase()) + " " + product.getDefinition().getName().toLowerCase() + ".");
							}
						}
							hasItems = false;
							return false;
						}
				}
		}

		// Old method added as fail-safe because the above method is only cheap fixes and doesn't work for all skills.
		// Removing this method will let players make infinite items while skilling.
		hasItems = true;

		for (RequiredItem item : requiredItems) {
			if (!player.getInventory().contains(item.getItem())) {
				String prefix = item.getItem().getAmount() > 1 ? Integer.toString(item.getItem().getAmount()) : "some";
				DialogueManager.sendStatement(player, "You " + (!hasItems ? "also need to have" : "need to have") + " " + prefix + " "
						+ item.getItem().getDefinition().getName().toLowerCase() + " to continue.");
				hasItems = false;
			}
		}

		//Newer method to add support for our wooden shields
		if (Fletching.isAShield(product.getId())) {
			hasItems = Fletching.checkShieldRequirements(player, requiredItems, hasItems);
		}
		// Check if we have required stringing level..

		// Check if we have required stringing level..
		if (player.getSkillManager().getCurrentLevel(skill) < requiredLevel) {
			for (RequiredItem item : requiredItems) {
				if (skill.getName().equals("Smithing")) {
					DialogueManager.sendStatement(player, "You need a " + skill.getName() + " level of "
							+ Integer.toString(requiredLevel) + " to " + getActionName(skill) + " " + Misc.anOrA(product.getDefinition().getName()) + " " + product.getDefinition().getName().toLowerCase() + ".");
				} else {
					DialogueManager.sendStatement(player, "You need a " + skill.getName() + " level of "
							+ Integer.toString(requiredLevel) + " to " + getActionName(skill) + " " + Misc.anOrA(product.getDefinition().getName()) +" " + product.getDefinition().getName().toLowerCase() + ".");
				}
				return false;
			}
		}

		if (!hasItems) {
			return false;
		}

		return super.hasRequirements(player);
	}

	@Override
	public boolean loopRequirements() {
		return true;
	}

	@Override
	public boolean allowFullInventory() {
		return true;
	}

	public Item getProduct() {
		return product;
	}

	public void decrementAmount() {
		amount--;
	}

	public int getAmount() {
		return amount;
	}

	public List<RequiredItem> filterRequiredItems(Predicate<RequiredItem> criteria) {
		return requiredItems.stream().filter(criteria).collect(Collectors.toList());
	}

	public List<RequiredItem> getRequiredItems() {
		return requiredItems;
	}
}
