package com.grinder.game.model.consumable;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.dueling.DuelRule;
import com.grinder.game.content.item.MorphItems;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.definition.ItemDefinition;

import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.consumable.edible.Beverage;
import com.grinder.game.model.consumable.edible.Edible;
import com.grinder.game.model.consumable.potion.Potion;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.timing.Timer;
import com.grinder.util.timing.TimerKey;

import java.util.Optional;

/**
 * TODO: add documentation
 *
 * @version 1.0
 * @since 2019-04-25
 */
public class ConsumableUtil {

	private static final String[] CANDY_SHOUTS = {
			"Hmmm... candy!", "All that sugar!", "Wow I feel good!", "Halloween is the best!", "This tastes amazing!", "The sweet taste of candy!", "Sugar rush!", "Call me candyman!"
	};

    /**
     * Handles the player eating said food type.
     *
     * @param player The player eating the consumable.
     * @param item The food item being consumed.
     * @param slot The slot of the food being eaten.
     */
    public static boolean consumeEdible(Player player, int item, int slot) {

		if (player.isTeleporting()) {
			return false;
		}
		if (player.BLOCK_ALL_BUT_TALKING) {
			return false;
		}
		if (player.isInTutorial()) {
			return false;
		}
		if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
			return false;
		}
		if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
			player.getPacketSender().sendMessage("You can't teleport when you're AFK!", 1000);
			return false;
		}
		if (!MorphItems.INSTANCE.notTransformed(player, "do this", true, false))
			return false;
		if (player.getStatus() == PlayerStatus.TRADING) {
			player.getPacketSender().sendMessage("You can't use this command while trading!", 1000);
			return false;
		}
		if (player.getStatus() == PlayerStatus.BANKING) {
			player.getPacketSender().sendMessage("You can't do this while banking!", 1000);
			return false;
		}
		if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
			player.getPacketSender().sendMessage("You can't do this while price checking!", 1000);
			return false;
		}
		if (player.getStatus() == PlayerStatus.DUELING) {
			player.getPacketSender().sendMessage("You can't do this while dueling!", 1000);
			return false;
		}

		final Edible food = Edible.types.get(item);

		if (food == null && item != ItemID.ECTOPHIAL)
			return false;

		if (food == Edible.BIG_SHARK && player.getWildernessLevel() > 0) {
			if (AreaManager.inWilderness(player)) { // Extra check
				player.getPacketSender().sendMessage("It is not advised to eat this infront of the Wilderness spirits.");
				return false;
			}
		}

		if (player.getDueling().getRules()[DuelRule.NO_FOOD.ordinal()]) {
			player.getPacketSender().sendMessage("You're not allowed to eat during this duel.", 1000);
			return false;
		}

		if (player.getArea() != null) {
			if (!player.getArea().canEat(player, item)) {
				player.getPacketSender().sendMessage("You can't eat here.", 1000);
				return true;
			}
		}

		// Check if we're currently able to eat..
		if (player.getTimerRepository().has(TimerKey.STUN)) {
			player.getPacketSender().sendMessage("You're currently stunned!", 1000);
			return true;
		}

		if (food == Edible.KARAMBWAN) {
			if (player.getTimerRepository().has(TimerKey.KARAMBWAN) || player.getTimerRepository().has(TimerKey.POTION))
				return true;
		} else {
			if (player.getTimerRepository().has(TimerKey.FOOD)) {
				return true;
			}
		}

		final Timer timer = player.getCombat().getNextAttackTimer(true);

		if (timer != null)
			timer.extendOrCap(ConsumableConstants.EXTRA_ATTACK_DELAY_ON_EDIBLE_CONSUMPTION, 3);

		if (food == Edible.KARAMBWAN) {
			player.getTimerRepository().register(TimerKey.KARAMBWAN, 3); // Register karambwan timer too
			player.getTimerRepository().replaceIfLongerOrRegister(TimerKey.FOOD, 3);
		} else
			player.getTimerRepository().replaceIfLongerOrRegister(TimerKey.FOOD, 3);

		player.getPacketSender().sendInterfaceRemoval();

		SkillUtil.stopSkillable(player);

		player.getPoints().increase(AttributeManager.Points.FOOD_EATEN, 1); // Increase points

		if (item == ItemID.ECTOPHIAL) {
			player.getInventory().replaceFirst(ItemID.ECTOPHIAL, ItemID.ECTOPHIAL_2);
			player.getPacketSender().sendAreaPlayerSound(2401, 1, 0);
			TeleportHandler.teleport(player, new Position(3658 + Misc.random(3), 3515 + Misc.random(3)), TeleportType.ECTOPHIAL, true, true);
			TaskManager.submit(new Task(5) {
				@Override
				public void execute() {
					stop();
					player.getInventory().replaceFirst(ItemID.ECTOPHIAL_2, ItemID.ECTOPHIAL);

				}
			});
			return false;
		}

		player.performAnimation(ConsumableConstants.ANIMATION);

		player.getPacketSender().sendSound(ConsumableConstants.SOUND);

		player.getInventory().delete(food.item, slot);

		int currentHp = player.getSkillManager().getCurrentLevel(Skill.HITPOINTS);
		int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
		int healAmount = food.getHeal();

		boolean candy = false;
		if (food == Edible.STRANGE_FRUIT) {
			healAmount += Misc.random(30);
		} else if (food == Edible.PURPLE_SWEETS || food == Edible.PURPLE_SWEET) {
			healAmount = Misc.randomInclusive(1, 3);
		} else if (food == Edible.WHITE_CANDY || food == Edible.BROWN_CANDY || food == Edible.BLUE_CANDY || food == Edible.PURPLE_CANDY ||
				food == Edible.GREEN_CANDY || food == Edible.ORANGE_CANDY || food == Edible.PINK_CANDY) {
			healAmount = Misc.randomInclusive(2, 5);
			candy = true;
		} else if (food == Edible.RED_CANDY) {
			healAmount = Misc.randomInclusive(3, 6);
			candy = true;
		} else if (food == Edible.BLACK_CANDY) {
			healAmount = Misc.randomInclusive(4, 7);
			candy = true;
		}

		if (candy) {
			player.say(Misc.random(CANDY_SHOUTS));
		}

		if (food == Edible.ANGLERFISH) {
			healAmount = 22;
			if (maxHp > currentHp + 22)
				maxHp = maxHp + 22;
			maxHp += healAmount;
		} else if (food == Edible.CAVE_EEL) {
			healAmount += Misc.random(4);
		} else if (food == Edible.FROG_SPAWN) {
			healAmount += Misc.random(3);
		} else if (food == Edible.BANDAGES) {
			healAmount = (int) ((float) player.getSkills().getLevel(Skill.HITPOINTS) * (player.getEquipment().containsAny(ItemID.CASTLE_WARS_BRACELET_1_, ItemID.CASTLE_WARS_BRACELET_2_, ItemID.CASTLE_WARS_BRACELET_3_) ? 0.50f : 0.10f) + 1);
			int runRestore = (int) ((float) player.getSkills().getLevel(Skill.AGILITY) * 0.30F);
			player.setRunEnergy(player.getRunEnergy() + runRestore * 100);
		}

		if (healAmount + currentHp > maxHp)
			healAmount = maxHp - currentHp;

		if (healAmount < 0)
			healAmount = 0;

        player.setHitpoints(player.getHitpoints() + healAmount);

        final String action = food == Edible.BANDAGES ? "use" : "eat";

        if (food == Edible.TEA || food == Edible.BANANA_STEW || food == Edible.STEW || food == Edible.SPICY_STEW || food == Edible.COOKED_STEW || food == Edible.CURRY
				|| food == Edible.SPICY_SAUCE || food == Edible.BANANA_STEW || food == Edible.FRIED_MUSHROOMS || food == Edible.FRIED_ONIONS
			|| food == Edible.NETTLE_TEA || food == Edible.NETTLE_WATER/* || food == Edible.FRUIT_BLAST || food == Edible.PINEAPPLE_PUNCH || food == Edible.WIZARD_BLIZZARD
			|| food == Edible.SHORT_GREEN_GUY || food == Edible.DRUNK_DRAGON || food == Edible.CHOC_SATURDAY || food == Edible.BLURBERRY_SPECIAL || food == Edible.PREMADE_FRUIT_BLAST
			|| food == Edible.PREMADE_PINEAPPLE_PUNCH || food == Edible.PREMADE_WIZARD_BLIZZARD || food == Edible.PREMADE_SGG || food == Edible.PREMADE_DRUNK_DRAGON || food == Edible.PREMADE_CHOCS_DY
			|| food == Edible.PREMADE_BLURBERRY_SPECIAL || food == Edible.CIDER  || food == Edible.MATURE_CIDER || food == Edible.DRAWVEN_STOUT || food == Edible.DRAWVEN_STOUT_M || food == Edible.ASGARNIAN_ALE
			|| food == Edible.ASGARNIAN_ALE_M || food == Edible.GREEMANS_ALE || food == Edible.GREENMANS_ALE_M || food == Edible.WIZARD_MIND_BOMB || food == Edible.MATURE_WMB || food == Edible.DRAGON_BITTER
			|| food == Edible.DRAGON_BITTER_M || food == Edible.MOONLIGHT_MEAD || food == Edible.MOONLIGHT_MEAD_M || food == Edible.AXEMANS_FOLLY || food == Edible.AXEMANS_FOLLY_M || food == Edible.CHEFS_DELIGHT
			|| food == Edible.CHEFS_DELIGHT_M || food == Edible.SLAYERS_RESPITE || food == Edible.SLAYERS_RESPITE_M || food == Edible.VODKA || food == Edible.WHISKY || food == Edible.GIN
			|| food == Edible.RUM || food == Edible.BRAINDEATH_RUM || food == Edible.AHABS_BEER || food == Edible.ASGOLDIAN_ALE || food == Edible.BOTTLE_OF_WINE || food == Edible.HALF_FULL_WINE_JUG
			|| food == Edible.KEG_OF_BEER || food == Edible.BRANDY || food == Edible.BEER_TANKARD || food == Edible.GROG || food == Edible.BLOODY_BRACER || food == Edible.ELVEN_DAWN*/) {
			player.sendMessage("You drink the " + food.name + ".");
		} else if (food == Edible.FROG_SPAWN) {
			player.sendMessage("You " + action + " the " + food.name + ". Yuck.");
		} else {
			player.sendMessage("You " + action + " the " + food.name + ".");
		}


        // Cabbage eating
        if (food == Edible.CABBAGE)
            player.say("YUK!!");

		else if (food == Edible.POISON_KARAMBWAN) {
			if (player.getHitpoints() > 5)
				player.getCombat().queue(new Damage(5, DamageMask.POISON));
			player.sendMessage("It tastes...painful.");
			player.getPacketSender().sendSound(new Sound(1265, 30));
		}

        
        // Task
		if (healAmount > 0)
        AchievementManager.processFor(AchievementType.SAFETY_FIRST, healAmount, player);

        // Handle cake slices..
        if (food == Edible.CAKE || food == Edible.SECOND_CAKE_SLICE || food == Edible.CHOCOLATE_CAKE || food == Edible.CHOCOLATE_CAKE_SLICE
                || food == Edible.PLAIN_PIZA || food == Edible.MEAT_PIZZA || food == Edible.ANCHOVY_PIZZA || food == Edible.FISH_PIE
				|| food == Edible.BOTANICAL_PIE  || food == Edible.DRAGONFRUIT_PIE
                || food == Edible.ADMIRAL_PIE || food == Edible.WILD_PIE || food == Edible.SUMMER_PIE || food == Edible.GARDEN_PIE) {
            player.getInventory().add(new Item(food.item.getId() + 2, 1));
        } else if (food == Edible.APPLE_PIE) {
            player.getInventory().add(new Item(2335, 1));
		} else if (food == Edible.SPICY_SAUCE) {
			player.getInventory().add(new Item(ItemID.BOWL, 1));
		} else if (food == Edible.APPLE_PIE) {
			player.getInventory().add(new Item(2335, 1));
		} else if (food == Edible.MUSHROOM_PIE) {
			player.getInventory().add(new Item(21687, 1));
        } else if (food == Edible.MEAT_PIE) {
            player.getInventory().add(new Item(2331, 1));
        } else if (food == Edible.REDBERRY_PIE) {
            player.getInventory().add(new Item(2333, 1));
		} else if (food == Edible.BANANA_STEW || food == Edible.STEW || food == Edible.SPICY_STEW || food == Edible.COOKED_STEW || food == Edible.CURRY) {
			player.getInventory().add(new Item(1923, 1));
        } else if (food == Edible.TEA || food == Edible.NETTLE_TEA || food == Edible.NETTLE_WATER) {
            player.getInventory().add(new Item(1980, 1));
        } else if (food == Edible.HALF_ADMIRAL_PIE || food == Edible.HALF_APPLE_PIE || food == Edible.HALF_FISH_PIE
                || food == Edible.HALF_GARDEN_PIE || food == Edible.HALF_MEAT_PIE || food == Edible.HALF_REDBERRY_PIE
				|| food == Edible.HALF_BOTANICAL_PIE || food == Edible.HALF_DRAGONFRUIT_PIE
                || food == Edible.HALF_SUMMER_PIE || food == Edible.HALF_WILD_PIE || food == Edible.HALF_MUSHROOM_PIE) {
            player.getInventory().add(new Item(2313, 1));
        }

        return true;
    }

	/**
	 * Drinks a beverage, also considered a consumable but usually has stat affects.
	 * @param player The player drinking.
	 * @param item The item drunk.
	 * @param slot The slot of the drunk item.
	 */
    public static boolean drinkBeverage(Player player, int item, int slot) {
		Beverage beverage = Beverage.Companion.byID(item);
		if (beverage == null)
			return false;
		if (player.getDueling().getRules()[DuelRule.NO_FOOD.ordinal()]) {
			player.getPacketSender().sendMessage("You're not allowed to eat during this duel.", 1000);
			return false;
		}

		if (player.getArea() != null) {
			if (!player.getArea().canEat(player, item)) {
				player.getPacketSender().sendMessage("You can't eat here.", 1000);
				return true;
			}
		}

		// Check if we're currently able to eat..
		if (player.getTimerRepository().has(TimerKey.STUN)) {
			player.getPacketSender().sendMessage("You're currently stunned!", 1000);
			return true;
		}
		final Timer timer = player.getCombat().getNextAttackTimer(true);

		if(timer != null)
			timer.extendOrCap(ConsumableConstants.EXTRA_ATTACK_DELAY_ON_EDIBLE_CONSUMPTION, 3);
		player.getTimerRepository().replaceIfLongerOrRegister(TimerKey.FOOD, 3);
		player.getPacketSender().sendInterfaceRemoval();
		SkillUtil.stopSkillable(player);
		player.performAnimation(ConsumableConstants.ANIMATION);
		player.getPacketSender().sendSound(ConsumableConstants.DRINK_SOUND);
		TaskManager.submit(new Task(1) {
			@Override
			public void execute() {
				beverage.getDrink().invoke(player);
				player.getPacketSender().sendMessage(beverage.getMessage());
				stop();
			}
		});
		player.heal(beverage.getHealAmt());
		player.getInventory().setItem(slot, new Item(beverage.getReplaceWith())).refreshItems();
    	return true;
	}

    /**
	 * Attempts to consume {@code item} in {@code slot} for {@code player}.
	 *
	 * @param player the player attempting to consume the item.
	 * @param item the item being consumed by the player.
	 * @param slot the slot the player is consuming from.
     *
	 * @return {@code true} if the item was consumed, {@code false} otherwise.
	 */
	public static boolean drinkPotion(Player player, int item, int slot) {

		final Optional<Potion> potion = Potion.forId(item);

		if (potion.isEmpty())
			return false;

		if (player.getDueling().getRules()[DuelRule.NO_FOOD.ordinal()]) {
			player.getPacketSender().sendMessage("You're not allowed to drink during this duel.", 1000);
			return false;
		}

		if (player.getArea() != null) {
			if (!player.getArea().canDrink(player, item)) {
				player.getPacketSender().sendMessage("You can't use potions here.", 1000);
				return true;
			}
			if (potion.get() == Potion.GUTHIX_REST || potion.get() == Potion.SARADOMIN_BREW) {
				if (!player.getArea().canEat(player, item)) {
					player.getPacketSender().sendMessage("You can't eat here.", 1000);
					return true;
				}
			}
		}

		if (player.getTimerRepository().has(TimerKey.STUN)) {
			player.getPacketSender().sendMessage("You're currently stunned and can't use potions.", 1000);
			return true;
		}

		if (player.getTimerRepository().has(TimerKey.POTION))
			return true;

		if (potion.get().equals(Potion.OVERLOAD_POTIONS)) {
			if (player.getTimerRepository().has(TimerKey.OVERLOAD_POTION)) {
				player.sendMessage("You already have the effects of an overload potion.");
				return true;
			}
			if (player.getWildernessLevel() > 0) {
				player.sendMessage("You can't drink this in the Wilderness.");
				return true;
			}
			if (player.getSkillManager().getCurrentLevel(Skill.HITPOINTS) - 50 <= 0) {
				player.getPacketSender().sendMessage("You must have at least a level of 50 hitpoints to drink this.", 1000);
				return true;
			}
			if (player.getSkillManager().getCurrentLevel(Skill.HERBLORE) < 70) {
				player.getPacketSender().sendMessage("You must have at least a level of 70 herblore to drink this.", 1000);
				return true;
			}
			if (player.isJailed()) {
				return true;
			}
		}


		// Task
		AchievementManager.processFor(AchievementType.ENLIGHTMENT, player);

		onDrink(player);

		// Message
		final String itemName = ItemDefinition.forId(item).getName();
		if (itemName.startsWith("Aggressivity")) {
			player.getPacketSender().sendMessage("You drink your "
					+ itemName + ".");
		} else {
			player.getPacketSender().sendMessage("You drink some of your "
					+ itemName + ".");
		}
		String m = "";
		if (itemName.endsWith("(4)")) {
			m = "You have 3 doses of potion left.";
		} else if (itemName.endsWith("(3)")) {
			m = "You have 2 doses of potion left.";
		} else if (itemName.endsWith("(2)")) {
			m = "You have 1 dose of potion left.";
		} else if (itemName.endsWith("(1)")) {
			m = "You have finished your potion.";
		}  else if (itemName.startsWith("Aggressivity")) {
			m = "You have finished your potion.";
		}
		final String message = m;
		TaskManager.submit(new Task(1) {
			@Override
			public void execute() {
				player.getPacketSender().sendMessage(message);
				stop();
			}
		});

        // Increase points
		player.getPoints().increase(AttributeManager.Points.POTIONS_DRANK, 1); // Increase points


        // Barbarian crushing
        if (Potion.getReplacementItem(item).getId() == ConsumableConstants.VIAL && player.isVialCrushingToggled()) {
			TaskManager.submit(new Task(1) {
				@Override
				public void execute() {
					player.sendMessage("You quickly smash the empty vial using the trick a Barbarian taught you.");
					stop();
				}
			});
			player.getInventory().setItem(slot, new Item(-1)).refreshItems();
		} else {
			player.getInventory().setItem(slot, Potion.getReplacementItem(item)).refreshItems();
		}


		potion.get().onEffect(player);
		return true;
	}

	public static void onDrink(Player player) {
		player.getPacketSender().sendSound(ConsumableConstants.DRINK_SOUND);

		player.getTimerRepository().register(TimerKey.POTION, 3);
		player.getTimerRepository().register(TimerKey.FOOD, 3);

		SkillUtil.stopSkillable(player);

		player.getPacketSender().sendInterfaceRemoval();

		player.performAnimation(ConsumableConstants.ANIMATION);
	}
}
