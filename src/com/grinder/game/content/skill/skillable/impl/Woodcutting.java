package com.grinder.game.content.skill.skillable.impl;

import com.grinder.game.collision.CollisionManager;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.item.degrading.DegradingType;
import com.grinder.game.content.miscellaneous.PetHandler;
import com.grinder.game.content.skill.skillable.DefaultSkillable;
import com.grinder.game.content.skill.skillable.impl.woodcutting.AxeType;
import com.grinder.game.content.skill.skillable.impl.woodcutting.TreeType;
import com.grinder.game.content.skill.task.SkillMasterType;
import com.grinder.game.content.skill.task.SkillTaskManager;
import com.grinder.game.content.task_new.DailyTask;
import com.grinder.game.content.task_new.PlayerTaskManager;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.entity.object.*;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.TimedObjectReplacementTask;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.ObjectID;

import java.util.Optional;

/**
 * Represents the Woodcutting skill.
 *
 * @author Professor Oak
 */
public class Woodcutting extends DefaultSkillable {

	private static final int TREE_STUMP = 1343;
	/**
	 * The {@link GameObject} to cut down.
	 */
	private final GameObject treeObject;
	/**
	 * The {@code treeObject} as an enumerated type which contains information
	 * about it, such as required level.
	 */
	private final TreeType tree;
	/**
	 * The axe we're using to cut down the tree.
	 */
	private Optional<AxeType> axe = Optional.empty();

	/**
	 * Constructs a new {@link Woodcutting}.
	 *
	 * @param treeObject
	 *            The tree to cut down.
	 * @param tree
	 *            The tree's data
	 */
	public Woodcutting(GameObject treeObject, TreeType tree) {
		this.treeObject = treeObject;
		this.tree = tree;
	}

	@Override
	public void start(Player player) {
		player.getPacketSender().sendMessage("You swing your axe at the tree..");

		super.start(player);
	}

	@Override
	public void startAnimationLoop(Player player) {
		Task animLoop = new Task(4, player, true) {
			@Override
			protected void execute() {
				player.performAnimation(axe.get().getAnimation());

				if (Misc.random(0, 500) == 0) {
					dropBirdsNest(player);
				}
			}
		};
		TaskManager.submit(animLoop);
		getTasks().add(animLoop);
	}

	@Override
	public void startGraphicsLoop(Player player) {

	}

	@Override
	public void startSoundLoop(Player player) {
//		Task soundLoop = new Task(2, player, true) {
//			@Override
//			protected void execute() {
//				player.getPacketSender().sendSound(Sounds.HIT_TREE, 200);
//			}
//		};
//		TaskManager.submit(soundLoop);
//		getTasks().add(soundLoop);
	}

	@Override
	public void onCycle(Player player) {
		if (!ClippedMapObjects.exists(getTreeObject()) || ObjectManager.existsAt(tree.getStumpId(), treeObject.getPosition())) {
				cancel(player);
		}
	}

	private void handleTask(Player player) {
		switch(tree) {
			case NORMAL:
				PlayerTaskManager.progressTask(player, DailyTask.CHOP_LOGS);
				break;
			case WILLOW:
				PlayerTaskManager.progressTask(player, DailyTask.CHOP_WILLOW_LOGS);
				break;
			case MAPLE:
				PlayerTaskManager.progressTask(player, DailyTask.CHOP_MAPLE_LOGS);
				break;
			case YEW:
				PlayerTaskManager.progressTask(player, DailyTask.CHOP_YEW_LOGS);
				break;
			case MAGIC:
				PlayerTaskManager.progressTask(player, DailyTask.CHOP_MAGIC_LOGS);
				break;
		}
	}

	@Override
	public void finishedCycle(Player player) {
		// Add logs..
		
		
		boolean infernalAxe = (player.getEquipment().contains(13241) || player.getInventory().contains(13241)) && Misc.getRandomInclusive(4) == 1;

		if (infernalAxe && axe.get().getId() == 13241 && tree != TreeType.VINES && tree != TreeType.DRAMEN) {
			player.getSkillManager().addExperience(Skill.FIREMAKING, tree.getBurnXpReward());
			if (player.getEquipment().contains(13241)) {
			player.getItemDegradationManager().degrade(DegradingType.SKILLING, -1);
			} else {
			player.getItemDegradationManager().degradeInventoryItems(DegradingType.SKILLING, -1, 13241);	
			}
			player.performGraphic(new Graphic(86));
		} else {
			player.getInventory().add(tree.getLogId(), 1);
		}
		if (tree == TreeType.VINES)
			player.getPacketSender().sendMessage("You chop off the tangling vines.");
		else
			player.getPacketSender().sendMessage("You get some logs.");

		// Roll pet
		PetHandler.onSkill(player, Skill.WOODCUTTING);

		player.getPoints().increase(AttributeManager.Points.CHOPPED_TREES, 1); // Increase points

		if (tree == TreeType.MAGIC) {
			player.getPoints().increase(AttributeManager.Points.MAGIC_TREES_CUT, 1); // Increase points
		}
		
		// Add exp..
		handleTask(player);
		SkillTaskManager.perform(player, tree.getLogId(), 1, SkillMasterType.WOODCUTTING);
		player.getSkillManager().addExperience(Skill.WOODCUTTING, tree.getXpReward());

		/*
		 * Cutting vine
		 */
		if (tree.equals(TreeType.VINES)) {
			// Despawn original object with a temporary object
			TaskManager.submit(new TimedObjectReplacementTask(treeObject,
					DynamicGameObject.createPublic(-1, treeObject.getPosition()),
					tree.getRespawnTimer()));
			player.getMotion()
					.enqueuePathToWithoutCollisionChecks(getVineMovePosition(player).getX(), getVineMovePosition(player).getY());
			// Stop skilling..
			cancel(player);
			return;
		}

		if (tree.equals(TreeType.NORMAL)) {
			AchievementManager.processFor(AchievementType.CHOP_CHOP, player);
		} else if (tree.equals(TreeType.WILLOW)) {
			AchievementManager.processFor(AchievementType.CHOPPING_AWAY, player);
		} else if (tree.equals(TreeType.MAGIC)) {
			AchievementManager.processFor(AchievementType.TREES_ARE_LIFE, player);
			AchievementManager.processFor(AchievementType.AXE_DOES_IT, player);
		}
		// Regular trees should always despawn.
		// Multi trees are random.
		if (tree.isMulti() && Misc.getRandomInclusive(tree.getCycles() * 2) > 2) {
			return;
		}
		// Despawn original object with a temporary object (tree stump id
		// 1343)
		if (tree == TreeType.REDWOOD) {
			TaskManager.submit(new TimedObjectReplacementTask(treeObject,
					DynamicGameObject.createPublic(treeObject.getId() + 1, treeObject.getPosition(), treeObject.getObjectType(), treeObject.getFace()),
					tree.getRespawnTimer() + Misc.getRandomInclusive(3) / 2));
		} else {
			TaskManager.submit(new TimedObjectReplacementTask(treeObject,
					DynamicGameObject.createPublic(tree.getStumpId(), treeObject.getPosition(), treeObject.getObjectType(), treeObject.getFace()),
					tree.getRespawnTimer() + Misc.getRandomInclusive(3) / 2));
		}
		player.getPacketSender().sendMessage("You chop down the tree.");
		player.getPacketSender().sendAreaPlayerSound(2734);
		// Stop skilling..
		cancel(player);
		PlayerExtKt.tryRandomEventTrigger(player, 2F);
	}

	public Position getVineMovePosition(Player player) {
		if (treeObject.getPosition().equals(new Position(2690, 9564, 0)) && player.getPosition().getX() >= 2691) {
			return new Position(2689, 9564, 0);
		} else if (treeObject.getPosition().equals(new Position(2690, 9564, 0)) && player.getPosition().getX() <= 2689) {
			return new Position(2691, 9564, 0);
		}  else if (treeObject.getPosition().equals(new Position(2683, 9569, 0)) && player.getPosition().getY() <= 9568) {
			return new Position(2683, 9570, 0);
		}  else if (treeObject.getPosition().equals(new Position(2683, 9569, 0)) && player.getPosition().getY() >= 9570) {
			return new Position(2683, 9568, 0);
		}  else if (treeObject.getPosition().equals(new Position(2673, 9499, 0)) && player.getPosition().getX() >= 2674) {
			return new Position(2672, 9499, 0);
		}  else if (treeObject.getPosition().equals(new Position(2673, 9499, 0)) && player.getPosition().getX() <= 2672) {
			return new Position(2674, 9499, 0);
		}  else if (treeObject.getPosition().equals(new Position(2694, 9482, 0)) && player.getPosition().getX() >= 2695) {
			return new Position(2693, 9482, 0);
		}  else if (treeObject.getPosition().equals(new Position(2694, 9482, 0)) && player.getPosition().getX() <= 2693) {
			return new Position(2695, 9482, 0);
		}  else if (treeObject.getPosition().equals(new Position(2675, 9479, 0)) && player.getPosition().getX() >= 2676) {
			return new Position(2674, 9479, 0);
		}  else if (treeObject.getPosition().equals(new Position(2675, 9479, 0)) && player.getPosition().getX() <= 2674) {
			return new Position(2676, 9479, 0);
		}
		return treeObject.getPosition();
	}

	@Override
	public int cyclesRequired(Player player) {
		int cycles = tree.getCycles() + Misc.getRandomInclusive(5);
		cycles -= (int) player.getSkillManager().getMaxLevel(Skill.WOODCUTTING) * 0.1;
		cycles -= cycles * axe.get().getSpeed();
		if(cycles < 3) {
			cycles = 3;
		}
		return cycles;
	}
	
	@Override
	public void onCancel(Player player) {
		player.getPacketSender().sendStopSound(Sounds.HIT_TREE);
	}

	public static Optional<AxeType> getAxe(Player player) {
		Optional<AxeType> axe = Optional.empty();
		for (AxeType a : AxeType.values()) {
			if (player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == a.getId()
					|| player.getInventory().contains(a.getId())) {

				// If we have already found an axe,
				// don't select others that are worse or can't be used
				if (axe.isPresent()) {
					if (player.getSkillManager().getMaxLevel(Skill.WOODCUTTING) < a.getRequiredLevel()) {
						continue;
					}
					if (a.getRequiredLevel() < axe.get().getRequiredLevel()) {
						continue;
					}
				}

				axe = Optional.of(a);
			}
		}

		return axe;
	}

	@Override
	public boolean hasRequirements(Player player) {
		// Attempt to find an axe..
		axe = getAxe(player);

		// Check if we found one..
		if (!axe.isPresent()) {
			if (tree == TreeType.VINES) {
				player.getPacketSender().sendMessage("You need an axe to chop down the vines.", 1000);
			} else {
				player.getPacketSender().sendMessage("You need an axe to chop down this tree.", 1000);
				player.getPacketSender()
						.sendMessage("You do not have an axe which you have the Woodcutting level to use.", 1000);
			}
			return false;
		}

		// Check if we have the required level to cut down this {@code tree}
		// using the {@link Axe} we found..
		if (player.getSkillManager().getCurrentLevel(Skill.WOODCUTTING) < axe.get().getRequiredLevel()) {
			player.getPacketSender()
					.sendMessage("You do not have an axe which you have the Woodcutting level to use.", 1000);
			return false;
		}

		// Check if we have the required level to cut down this {@code tree}..
		if (player.getSkillManager().getCurrentLevel(Skill.WOODCUTTING) < tree.getRequiredLevel()) {
			player.getPacketSender().sendMessage(
					"You need a Woodcutting level of " + tree.getRequiredLevel() + " to chop down this tree.", 1000);
			return false;
		}

		// Finally, check if the tree object remains there.
		// Another player may have cut it down already.
		if (!ClippedMapObjects.exists(treeObject) || ObjectManager.existsAt(TREE_STUMP, treeObject.getPosition())) {
			return false;
		}

		return super.hasRequirements(player);
	}

	private void dropBirdsNest(Player player) {
		Item item = new Item(ItemID.BIRD_NEST_4);

		if (Misc.randomChance(30F)) {
			item = new Item(ItemID.BIRD_NEST_5);
		}

		Position treePos = treeObject.getPosition();
		Position randomPosition;

		for (int i = 0; i < 50; i++) {
			randomPosition = new Position(treePos.getX()-2+Misc.random(5), treePos.getY()-2+Misc.random(5), treePos.getZ());

			if (!CollisionManager.blocked(randomPosition)) {
				ItemOnGroundManager.registerNonGlobal(player, item, randomPosition);
				return;
			}

		}


	}

	@Override
	public boolean loopRequirements() {
		return true;
	}

	@Override
	public boolean allowFullInventory() {
		return false;
	}

	public GameObject getTreeObject() {
		return treeObject;
	}

}
