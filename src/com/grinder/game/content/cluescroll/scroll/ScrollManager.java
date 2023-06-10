package com.grinder.game.content.cluescroll.scroll;

import com.grinder.game.World;
import com.grinder.game.content.cluescroll.ClueConstants;
import com.grinder.game.content.cluescroll.ClueScrollManager;
import com.grinder.game.content.cluescroll.agent.ClueAgent;
import com.grinder.game.content.cluescroll.scroll.type.PuzzleType;
import com.grinder.game.content.cluescroll.task.ClueTask;
import com.grinder.game.content.cluescroll.task.ClueTaskFactory;
import com.grinder.game.content.cluescroll.task.impl.ScanDigSpotClueTask;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.math.Vector2i;
import com.grinder.util.oldgrinder.Area;
import com.grinder.util.oldgrinder.StreamHandler;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.grinder.game.content.cluescroll.scroll.ScrollConstants.NPC_KING_ROALD;

/**
 * Handle player's Clue Scroll actions.
 * 
 * @author Pb600
 *
 */
public class ScrollManager {

	private static Area roaldKingRoom = new Area(3220, 3472, 3224, 3478);
	private final Player player;
	private ClueAgent taskAgent;
	private PuzzleType currentPuzzle;
	private int[][] puzzleProgress;
	private boolean completed;
	private long lastBow;
	private int lastEmoteOrdinal;
	private long lastEmoteTimeStamp;
	private boolean killedAgent;

	public ScrollManager(Player player) {
		this.player = player;
	}


	public void reset() {
		completed = false;
		currentPuzzle = null;
		puzzleProgress = null;
		player.getClueScrollManager().puzzleProgress = null;
		player.getClueScrollManager().savedPuzzle = null;
	}

	public void startPuzzle(PuzzleType puzzleType) {
		completed = false;
		if (puzzleType == null) {
			puzzleType = Misc.random(PuzzleType.values());
		}
		currentPuzzle = puzzleType;
		int[][] solution = currentPuzzle.getSolution();

		if (player.getClueScrollManager().puzzleProgress != null && player.getClueScrollManager().savedPuzzle == puzzleType) {
			puzzleProgress = player.getClueScrollManager().puzzleProgress;
		} else {
			player.getClueScrollManager().puzzleProgress = puzzleProgress = Misc.clone(solution);
			player.getClueScrollManager().savedPuzzle = puzzleType;
			scramblePuzzle();
			player.getClueScrollManager().puzzleStartTime = System.currentTimeMillis();
		}

		for (int y = 0; y < puzzleProgress.length; y++) {
			for (int x = 0; x < puzzleProgress[y].length; x++) {
				int item = puzzleProgress[x][y];
				int originalItem = solution[x][y];
				int index = getIndex(x, y);
				updateProgressSlot(index, item);
				StreamHandler.displayItemOnInterface(player, 6985, originalItem, index++, 1);
			}
		}

		openPuzzleInterface();
	}

	private void openPuzzleInterface() {
		StreamHandler.showInterface(player, 6976);
	}

	private int getIndex(int x, int y) {
		return ((y * 5) + x);
	}

	private void updateProgressSlot(int x, int y, int item) {
		int slot = getIndex(x, y);
		StreamHandler.displayItemOnInterface(player, 6980, item, slot, 1);
	}

	private void updateProgressSlot(int slot, int item) {
		StreamHandler.displayItemOnInterface(player, 6980, item, slot, 1);
	}

	private boolean hasFinished() {
		int[][] solution = currentPuzzle.getSolution();
		for (int y = 0; y < solution.length; y++) {
			for (int x = 0; x < solution[y].length; x++) {
				if (solution[x][y] != puzzleProgress[x][y]) {
					return false;
				}
			}
		}
		return true;
	}

	public void solvePuzzle() {
		if (currentPuzzle == null) {
			player.sendMessage("You don't have any puzzle to be solved.");
			return;
		}
		
		if(completed) {
			player.sendMessage("You have already completed this puzzle!");
			return;
		}
		
		if (player.getInventory().contains(ScrollConstants.ITEM_PUZZLE_SOLVER)) {
			player.getInventory().delete(ScrollConstants.ITEM_PUZZLE_SOLVER, 1);
			player.sendMessage("<img=776> The mechanism in the package solves your puzzle and vanish.");
			this.completed = true;
			completePuzzle();
		}
	}

	private void finishPuzzle() {
		this.completed = true;
		player.sendMessage("<img=776> You've completed the puzzle!");
		if (player.getClueScrollManager().puzzleStartTime > 0) {
			long elapsed = System.currentTimeMillis() - player.getClueScrollManager().puzzleStartTime;
			if (elapsed < TimeUnit.MINUTES.toMillis(30)) {

				player.sendMessage("<�CI=1>@or3@You have completed the puzzle in " + Misc.convertTime(elapsed) + ".");
				if (elapsed < player.getClueScrollManager().fastestPuzzle) {
					player.sendMessage("<�CI=1>You have improved your puzzle completion time!");
					player.getClueScrollManager().fastestPuzzle = elapsed;
				}
			}
		}
	}

	public void move(int itemId, int slot) {
		if (puzzleProgress == null) {
			return;
		}

		if (completed) {
			player.sendMessage("You have already completed this puzzle.");
			return;
		}
		int x = slot % 5;
		int y = slot / 5;
		if (x < 0 || x > 4 || y < 0 || y > 4) {
			return;
		}
		int currentItem = puzzleProgress[x][y];

		if (currentItem == -1) {
			return;
		}

		if (currentItem == itemId) {
			Direction[] directions = Direction.values();
			for (Direction direction : directions) {

				if (direction.isPerpendicular()) {
					Vector2i vector = direction.getDirectionVector();
					int targetX = vector.getX() + x;
					int targetY = (vector.getY() * -1) + y;
					if (targetX >= 0 && targetX <= 4 && targetY >= 0 && targetY <= 4) {
						int destItem = puzzleProgress[targetX][targetY];
						if (destItem == -1) {
							puzzleProgress[targetX][targetY] = currentItem;
							puzzleProgress[x][y] = -1;
							updateProgressSlot(x, y, -1);
							updateProgressSlot(targetX, targetY, currentItem);
							if (x == 4 && y == 4) {
								if (hasFinished()) {
									finishPuzzle();
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	public void scramblePuzzle() {
		for (int i = 0; i < 500; i++) {
			moveRandom();
		}
	}

	private Vector2i emptySlot = new Vector2i(0, 0);

	public void moveRandom() {
		row : for (int y = 0; y < puzzleProgress.length; y++) {
			for (int x = 0; x < puzzleProgress[y].length; x++) {
				int itemID = puzzleProgress[x][y];
				if (itemID == -1) {
					emptySlot.set(x, y);
					break row;
				}
			}
		}
		int attempts = 0;
		while (attempts++ < 100) {
			if (emptySlot != null) {
				Direction direction = Misc.random(ClueConstants.PUZZLE_POSSIBLE_DIRECTIONS);
				Vector2i vector = direction.getDirectionVector();
				int targetX = emptySlot.getX() + vector.getX();
				int targetY = emptySlot.getY() + (vector.getY() * -1);
				if (targetX >= 0 && targetX <= 4 && targetY >= 0 && targetY <= 4) {
					puzzleProgress[emptySlot.getX()][emptySlot.getY()] = puzzleProgress[targetX][targetY];
					puzzleProgress[targetX][targetY] = -1;
					break;
				}
			}
		}
	}

	/**
	 * Give player reward
	 */
	public void processReward() {
		//c.BLOCK_ALL_BUT_TALKING = false;
		if (rewardItems != null) {
			for (Item gameItem : rewardItems) {
				if (player.getInventory().canDeposit(gameItem.getId(), gameItem.getAmount())) {
					player.getInventory().add(gameItem.getId(), gameItem.getAmount());
				} else {
					ItemContainerUtil.dropUnder(player,gameItem.getId(), gameItem.getAmount());
				}
			}
			rewardItems = null;
		}
	}

	public void resetCurrentPuzzle() {
		this.currentPuzzle = null;
	}

	public PuzzleType getCurrentPuzzle() {
		return currentPuzzle;
	}

	public boolean hasCompleted() {
		return completed;
	}

	public void openPuzzle(int puzzleBox) {
		if (getCurrentPuzzle() != null && getCurrentPuzzle().getPuzzleBox() == puzzleBox) {
			openPuzzleInterface();
		} else {
			startPuzzle(PuzzleType.forPuzzleID(puzzleBox));
		}
	}

	public void onBow() {

		if (player.getPosition().inside(roaldKingRoom)) {

			if (hasBown(10_000))
				return;

			World.findNpcById(NPC_KING_ROALD, player.getPosition().getZ()).ifPresent(king -> {
				lastBow = System.currentTimeMillis();
				player.setPositionToFace(king.getPosition());
				player.say("Majesty!");

				String gender = player.getAppearance().isMale() ? "Sir" : "Lady";
				TaskManager.submit(player, 2, () -> {
					king.say(gender+" "+player.getUsername() + "!");
					king.setPositionToFace(player.getPosition());
				});
			});
		}// else player.sendMessage("You're not inside the king's room.");

	}

	public boolean hasBown(long time) {
	    long timeBetween = System.currentTimeMillis() - lastBow;
		return timeBetween <= time;
	}

	/**
	 * Compares the last performed emote within the time limit requested
	 * @param emoteOrdinal
	 * @param timeLimit (Time in millisseconds)
	 * @return
	 */
	public boolean hasPerformedEmote(int emoteOrdinal, long timeLimit) {
		return lastEmoteOrdinal == emoteOrdinal && System.currentTimeMillis() - lastEmoteTimeStamp <= timeLimit;
	}

	public void setLastEmote(int emoteOrdinal) {
		this.lastEmoteOrdinal = emoteOrdinal;
		this.lastEmoteTimeStamp = System.currentTimeMillis();
	}

	public void completePuzzle() {
		player.getClueScrollManager().puzzleProgress = puzzleProgress = Misc.clone(currentPuzzle.getSolution());
		updatePuzzleSlots();
	}

	private void updatePuzzleSlots() {
		for (int y = 0; y < puzzleProgress.length; y++) {
			for (int x = 0; x < puzzleProgress[y].length; x++) {
				int item = puzzleProgress[x][y];
				int index = getIndex(x, y);
				updateProgressSlot(index, item);
			}
		}
	}

	public void openScrollBox(int scrollBox, int itemSlot) {
		ScrollType scrollType = ScrollType.forScrollBox(scrollBox);
		if (scrollType != null) {
			Item item = player.getInventory().get(itemSlot);

			if (item != null && item.getId() == scrollBox) {
				int scrollID = scrollType.getScrollType();
				ClueTaskFactory.sendNewClue(player, itemSlot, scrollID);
			}
		}
	}

	public boolean isTaskAgent(ClueAgent npc) {
		return taskAgent == npc;
	}

	public boolean hasActiveAgent() {
		if(taskAgent == null)
			return false;
		return taskAgent.isActive();
	}

	public void setTaskAgent(ClueAgent agent) {
		taskAgent = agent;
	}

	public boolean interactWithAgent(ClueAgent npc) {

		if (isTaskAgent(npc)) {

			final ClueTask clueTask = npc.getTask();

			if (clueTask != null) {
				if (clueTask.involvesNPCInteraction()) {
					//System.out.println("Has Equipment? " + clueTask.hasEquipments(player));
					if (clueTask.getFinishCondition() == null && clueTask.hasEquipments(player) || (clueTask.getFinishCondition() != null && clueTask.getFinishCondition().test(player)) && clueTask.hasEquipments(player)) {

						DialogueBuilder builder = new DialogueBuilder(DialogueType.NPC_STATEMENT)
								.setNpcChatHead(npc.getId())
								.setText(Misc.random(ScrollConstants.URI_MESSAGES))
								.setExpression(DialogueExpression.DISTRESSED)
								.add(DialogueType.OPTION)
								.firstOption("What?", player -> {
									clueTask.completeTask(player);
									TaskManager.submit(player, 1, () -> {
										npc.performAnimation(new Animation(863));
										npc.performGraphic(new Graphic(795, GraphicHeight.LOW));
										TaskManager.submit(4, () -> World.getNpcRemoveQueue().add(npc));
									});
									setTaskAgent(null);
									player.getPacketSender().sendInterfaceRemoval();
								}).addCancel();

						builder.start(player);
					} else {
						//c.sendMessage("Executing here!");
						new DialogueBuilder(DialogueType.NPC_STATEMENT)
								.setNpcChatHead(npc.getId())
								.setExpression(DialogueExpression.DISTRESSED)
								.setText("I do not believe we have any business, Comrade.")
								.start(player);
					}
				}
			}
			return true;
		}
		return false;
	}

	public boolean hasCombativeAgent(){
		return taskAgent != null && taskAgent.isCombative();
	}

	public boolean hasKilledAgent() {
		return killedAgent;
	}

	public void setKilledAgent(boolean killedAgent) {
		this.killedAgent = killedAgent;
	}

	public void reRollRewards() {

		if (rewardItems != null) {

			final ClueScrollManager clueScrollManager = player.getClueScrollManager();

			if (clueScrollManager.scrollReroll <= 0) {
				player.sendMessage("You don't have any re-roll credit left, you may obtain more as vote streak reward!");
				return;
			}
			int rollsLeft = --clueScrollManager.scrollReroll;
			player.sendMessage("You have re-rolled your reward, " + (rollsLeft > 0 ? "you have " + rollsLeft + " re-roll(s) left." : "you have used all your re-roll credits."));
			Item[] newRewards = ClueTaskFactory.getInstance().rollReward(player, rewardDifficulty);
			this.rewardItems = newRewards;
			ClueTaskFactory.getInstance().displayRewards(player, newRewards);
		}
	}

	public void scanOperation() {
		final ClueScrollManager clueScrollManager = player.getClueScrollManager();
		Optional.ofNullable(clueScrollManager.eliteScroll).ifPresent(task -> {
			if (task instanceof ScanDigSpotClueTask) {
				((ScanDigSpotClueTask) task).scanLocation(player);
			} else {
			  /*  player.sendMessage("You haven't started this clue yet.");
                else if(save.remaining == 1)
                player.sendMessage("There is 1 step remaining in this clue.");
            	else
                player.sendMessage("There are " + save.remaining + " steps remaining in this clue.");*/
				player.sendMessage("There is nothing to scan.");
			}
		});
	}

	private Item[] rewardItems;
	private ScrollDifficulty rewardDifficulty;

	public void setRewards(Item[] rewardItems, ScrollDifficulty rewardDifficulty) {
		this.rewardItems = rewardItems;
		this.rewardDifficulty = rewardDifficulty;
	}

	public int getCompletedScrolls() {
		final ClueScrollManager clueScrollManager = player.getClueScrollManager();
		return clueScrollManager.easyScrollCount
				+ clueScrollManager.mediumScrollCount
				+ clueScrollManager.hardScrollCount
				+ clueScrollManager.eliteScrollCount;
	}

}
