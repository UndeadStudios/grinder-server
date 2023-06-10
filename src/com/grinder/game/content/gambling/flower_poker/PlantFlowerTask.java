package com.grinder.game.content.gambling.flower_poker;

import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.item.Item;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.TimedObjectSpawnTask;
import com.grinder.util.Misc;

import java.util.List;
import java.util.Optional;

public class PlantFlowerTask extends Task {

	public static final int INTERFACE_ID = 61000;
	private static boolean someonePlanting;

	private final FlowerGambleContainer firstContainer;
	private final FlowerGambleContainer secondContainer;
	private final Player first;
	private final List<Item> firstItems;
	private final Player second;
	private final List<Item> secondItems;

	private int cycle;

	public PlantFlowerTask(Player first, Player second, List<Item> firstItems, List<Item> secondItems) {
		this.first = first;
		this.second = second;
		this.firstItems = firstItems;
		this.secondItems = secondItems;
		this.firstContainer = new FlowerGambleContainer();
		this.secondContainer = new FlowerGambleContainer();
	}

	@Override
	protected void execute() {

		if (cycle == 0) {

			initiateGame();

		} else if (cycle == 1 || cycle == 4 || cycle == 7 || cycle == 10 || cycle == 13) {

			rollFlower(first, firstContainer);
			rollFlower(second, secondContainer);

		} else if (cycle == 15) {

			finishGame();
		}
		cycle++;
	}

	private void finishGame() {
		final FlowerResult firstType = firstContainer.getResult();
		final FlowerResult secondType = secondContainer.getResult();

		first.sendMessage("<img=770>@bla@ You have @whi@"+firstType.getIdentifier()+"@bla@.");
		second.sendMessage("<img=770>@bla@ You have @whi@"+secondType.getIdentifier()+"@bla@.");

		// Grab winner
		if (firstType != secondType) {
			if (firstType.ordinal() > secondType.ordinal()) {
				first.sendMessage("<img=770>@red@ You @bla@won with @whi@" + firstType.getIdentifier() + "@bla@!");
				second.sendMessage("<img=770>@dre@ "+first.getUsername() + " @bla@has won with @whi@" + firstType.getIdentifier() + "@bla@!");
			} else {
				first.sendMessage("<img=770>@dre@ "+second.getUsername() + " @bla@ has won with @whi@" + secondType.getIdentifier() + "@bla@!");
				second.sendMessage("<img=770>@red@ You @bla@ won with @whi@" + secondType.getIdentifier() + "@bla@!");
			}
		} else {
			first.sendMessage("<img=770>@red@ It's a draw.");
			second.sendMessage("<img=770>@red@ It's a draw.");
		}

		final boolean draw = firstType == secondType;

		first.getGambling().end(
				firstType.ordinal() > secondType.ordinal() ? first : second,
				firstType.ordinal() > secondType.ordinal() ? second : first,
				firstItems, secondItems, draw, true);

		//Clear container
		firstContainer.refreshItems();
		secondContainer.refreshItems();

		first.BLOCK_ALL_BUT_TALKING = false;
		second.BLOCK_ALL_BUT_TALKING = false;

		// Un-Lock
		someonePlanting = false;
		stop();
	}

	private void initiateGame() {

		someonePlanting = true;

		first.BLOCK_ALL_BUT_TALKING = true;
		second.BLOCK_ALL_BUT_TALKING = true;

		// Disables Logout Until The Code Is Set To False
		first.setBlockLogout(true);
		second.setBlockLogout(true);

		//Clear container
		firstContainer.refreshItems();
		secondContainer.refreshItems();

		// Teleport players
		teleToRandomFlowerArea(first, second);
	}

	/**
	 * Randomly chooses a location to teleport both players for flower gambling.
	 */
	public void teleToRandomFlowerArea(Player player, Player player2) {

		// Roll
		final int rollId = Misc.random(5);

		switch (rollId) { // Random positions
			case 1:
				player.moveTo(new Position(2857, 2598, 0));
				player2.moveTo(new Position(2857, 2599, 0));
				break;
			case 2:
				player.moveTo(new Position(2857, 2592, 0));
				player2.moveTo(new Position(2857, 2593, 0));
				break;
			case 3:
				player.moveTo(new Position(2857, 2586, 0));
				player2.moveTo(new Position(2857, 2587, 0));
				break;
			case 4:
				player.moveTo(new Position(2833, 2586, 0));
				player2.moveTo(new Position(2833, 2587, 0));
				break;
			case 5:
				player.moveTo(new Position(2833, 2592, 0));
				player2.moveTo(new Position(2833, 2593, 0));
				break;
			default:
				player.moveTo(new Position(2833, 2598, 0));
				player2.moveTo(new Position(2833, 2599, 0));
				break;
		}

	}


	public void rollFlower(Player player, FlowerGambleContainer container){
		final FlowersData flowers = FlowersData.generate();
		final GameObject flowerObject = DynamicGameObject.createPublic(flowers.getObjectId(), player.getPosition().clone());
		final Item flowerItem = new Item(flowers.getItemId());
		container.add(flowerItem);

		SkillUtil.stopSkillable(player);
		player.performAnimation(new Animation(827));
		player.getMotion().clearSteps();
		player.getMotion().enqueueStepAwayWithCollisionCheck();

		//Start a task which will spawn and then delete them after a period of time.
		TimedObjectSpawnTask to = new TimedObjectSpawnTask(flowerObject, 20, Optional.empty());
		TaskManager.submit(to);
		TaskManager.submit(1, () -> player.setPositionToFace(flowerObject.getPosition()));
	}

	public static boolean someonePlanting() {
		return someonePlanting;
	}

}
