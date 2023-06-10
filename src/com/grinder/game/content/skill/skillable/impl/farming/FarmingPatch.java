package com.grinder.game.content.skill.skillable.impl.farming;

import com.google.gson.annotations.Expose;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.miscellaneous.PetHandler;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Animation;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;

import java.util.concurrent.TimeUnit;


/**
 * This class contains the instance for each farming patch.
 * 
 * @author Austin
 *
 */
public class FarmingPatch {

	private Player player;

	private Task task;

	@Expose
	PatchType patchType;

	@Expose
	private Long lastAction;

	@Expose
	private PatchState state = PatchState.HERB_GRASS_4;

	@Expose
	private CompostType compostType = CompostType.NONE;

	@Expose
	private Plant plant = null;//set this to null before release

	@Expose
	private int herbsLeft;
	private int randomness;
	
	public FarmingPatch(PatchType patchType) {
		this.setLastAction(System.currentTimeMillis());
		this.patchType = patchType;
	}

	public void init(Player player) {
		this.player = player;
	}

	private static int getGrowCycleDurationInSeconds(Player player){
		return (PlayerUtil.isPlatinumMember(player) ? 250:
				PlayerUtil.isLegendaryMember(player) ? 450:
				PlayerUtil.isAmethystMember(player) ? 750:
				PlayerUtil.isTopazMember(player) ? 900:
				PlayerUtil.isRubyMember(player) ? 1050:
				1200) / 5;
	}

	public void process() {

		if (plant == null)
			return;

		final int growCycleDurationInSeconds = getGrowCycleDurationInSeconds(player);
		final int timePassed = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()-getLastAction());

//		player.sendDevelopersMessage("next_cycle_time_left = "+(growCycleDurationInSeconds-timePassed));

		if (Misc.elapsed(getLastAction(), 2_400_000)) {
			switch (getState()) {
			case HERB_DISEASED_1:
				tarnsformPatchObject(PatchState.HERB_DEAD_1);
				player.sendMessage("@red@Your " + patchType.name().toLowerCase().replace("_", " ") + " patch has died.");
				break;
			case HERB_DISEASED_2:
				tarnsformPatchObject(PatchState.HERB_DEAD_2);
				player.sendMessage("@red@Your " + patchType.name().toLowerCase().replace("_", " ") + " patch has died.");
				break;
			case HERB_DISEASED_3:
				tarnsformPatchObject(PatchState.HERB_DEAD_3);
				player.sendMessage("@red@Your " + patchType.name().toLowerCase().replace("_", " ") + " patch has died.");
				break;
			default:
				break;
			}
		}
		if (Misc.elapsed(getLastAction(), 60_000)) {
			switch (getState()) {
			case HERB_RAKED:
				tarnsformPatchObject(PatchState.HERB_GRASS_2);
				break;
			case HERB_GRASS_2:
				tarnsformPatchObject(PatchState.HERB_GRASS_3);
				break;
			case HERB_GRASS_3:
				tarnsformPatchObject(PatchState.HERB_GRASS_4);
				break;
			default:
				break;
			}
		}
		if (Misc.elapsed(getLastAction(), growCycleDurationInSeconds*1000)) {
			boolean disease = Math.max(0, Misc.getRandomInclusive((player.getSkillManager().getCurrentLevel(Skill.FARMING) - plant.getLevelReq()) / 4 + 8)) <= 2;
			switch (getState()) {
			case HERB_SEEDED:
				tarnsformPatchObject(PatchState.HERB_GROWING_1);
				break;
			case HERB_GROWING_1:
				tarnsformPatchObject(disease ? PatchState.HERB_DISEASED_1 : PatchState.HERB_GROWING_2);
				if (disease)
					player.sendMessage("@red@Your "+ patchType.name().toLowerCase().replace("_", " ") + " patch has been diseased.");
				break;
			case HERB_GROWING_2:
				tarnsformPatchObject(disease ? PatchState.HERB_DISEASED_2 : PatchState.HERB_GROWING_3);
				if (disease)
					player.sendMessage("@red@Your "+ patchType.name().toLowerCase().replace("_", " ") + " patch has been diseased.");
				break;
			case HERB_GROWING_3:
				tarnsformPatchObject(disease ? PatchState.HERB_DISEASED_3 : PatchState.HERB_GROWING_4);
				if (disease)
					player.sendMessage("@red@Your "+ patchType.name().toLowerCase().replace("_", " ") + " patch has been diseased.");
				break;
			case HERB_GROWING_4:
				randomness = getCompostType().equals(CompostType.COMPOST) ? 6 : getCompostType().equals(CompostType.SUPERCOMPOST) ? 8 : 4;
				if (player.getFarming().playerHasMagicSecateurs())
					randomness += 2;
				herbsLeft = (randomness/2) + 1;
				tarnsformPatchObject(PatchState.HERB_GROWN);
				player.sendMessage("@red@Your "+ patchType.name().toLowerCase().replace("_", " ") + " plant is fully grown.");
				break;
			default:
				break;
			}
		}
	}
	
	public void pick() {

		if (task != null && task.isRunning())
			return;

		Position position = player.getPosition().copy();

		task = new Task(3, player, true) {

			@Override
			protected void execute() {
				if (!player.getPosition().equals(position)) {
					stop();
					return;
				}
				if (!player.getInventory().isFull() && getPlant() != null && getPlant().getProduct() != null) {
					player.performAnimation(new Animation(FarmingConstants.PICKING_HERB_ANIM));
					player.sendMessage("You pick a " + getPlant().getProduct().getDefinition().getName().toLowerCase().replace("_", " ") + ".");
					player.getInventory().add(getPlant().getProduct());
					player.getSkillManager().addExperience(Skill.FARMING, getPlant().getHarvestXP());
					PetHandler.onSkill(player, Skill.FARMING);
				} else {
					player.sendMessage("Your inventory is full.");
					stop();
					return;
				}

				if(Misc.getRandomInclusive(randomness) == 0)
					herbsLeft--;

				if (herbsLeft <= 0) {
					tarnsformPatchObject(PatchState.HERB_RAKED);
					stop();
				}

			}
		};
		TaskManager.submit(task);
	}
	
	void digPlant() {
		if (!Misc.elapsed(getLastAction(), 1_500))
			return;
		player.performAnimation(new Animation(FarmingConstants.DIGGING_ANIMATION));
		reset();
	}
	
	void curePlant() {
		if (!Misc.elapsed(getLastAction(), 1_500))
			return;
		player.getInventory().delete(6036, 1);
		player.getInventory().add(new Item(229));
		player.performAnimation(new Animation(FarmingConstants.CURING_ANIM));
		switch (getState()) {
		case HERB_DISEASED_1:
			tarnsformPatchObject(PatchState.HERB_GROWING_2);
			break;
		case HERB_DISEASED_2:
			tarnsformPatchObject(PatchState.HERB_GROWING_3);
			break;
		case HERB_DISEASED_3:
			tarnsformPatchObject(PatchState.HERB_GROWING_4);
			break;
		default:
			break;
		}
		player.sendMessage("You cure the diseased plant.");
	}

	void rakeGrass() {

		if (task != null && task.isRunning())
			return;

		Position position = player.getPosition().copy();

		player.getPoints().increase(AttributeManager.Points.PATCHES_RAKED, 1); // Increase points

		task = new Task(2, player, true) {
			@Override
			protected void execute() {
				if (!player.getPosition().equals(position)) {
					stop();
					return;
				}
				if (player.getInventory().isFull()) {
					player.sendMessage("Your inventory is full.");
					stop();
					return;
				}
				switch (getState()) {
				case HERB_GRASS_4:
					player.performAnimation(new Animation(FarmingConstants.RAKING_ANIM));
					if (Misc.getRandomInclusive(1) == 1)
						tarnsformPatchObject(PatchState.HERB_GRASS_3);
					player.getInventory().add(new Item(FarmingConstants.WEEDS));
					player.getSkillManager().addExperience(Skill.FARMING, 1);
					break;
				case HERB_GRASS_3:
					player.performAnimation(new Animation(FarmingConstants.RAKING_ANIM));
					if (Misc.getRandomInclusive(1) == 1)
						tarnsformPatchObject(PatchState.HERB_GRASS_2);
					player.getInventory().add(new Item(FarmingConstants.WEEDS));
					player.getSkillManager().addExperience(Skill.FARMING, 1);
					break;
				case HERB_GRASS_2:
					player.performAnimation(new Animation(FarmingConstants.RAKING_ANIM));
					if (Misc.getRandomInclusive(1) == 1)
						tarnsformPatchObject(PatchState.HERB_RAKED);
					player.getInventory().add(new Item(FarmingConstants.WEEDS));
					player.getSkillManager().addExperience(Skill.FARMING, 1);
					break;
				default:
					stop();
					break;
				}
			}
		};
		TaskManager.submit(task);
	}
	
	void addCompost(CompostType compostType) {
		if (!Misc.elapsed(getLastAction(), 1_500))
			return;
		player.getInventory().delete(compostType.getItem().getId(), 1);
		player.getInventory().add(new Item(ItemID.BUCKET));
		player.performAnimation(new Animation(FarmingConstants.PUTTING_COMPOST));
		this.setCompostType(compostType);
		if (compostType.equals(CompostType.COMPOST)) {
			tarnsformPatchObject(PatchState.HERB_COMPOST);
			player.sendMessage("You've added Compost to the soil.");
		} else {
			tarnsformPatchObject(PatchState.HERB_SUPERCOMPOST);
			player.sendMessage("You've added Super compost to the soil.");
		}
	}
	
	void plantSeed(Plant plant) {
		if (!Misc.elapsed(getLastAction(), 1_500))
			return;
		if (!state.equals(PatchState.HERB_RAKED) && 
				!state.equals(PatchState.HERB_COMPOST) && 
				!state.equals(PatchState.HERB_SUPERCOMPOST)) {
			if (plant != null)
				player.sendMessage("The patch is already seeded.");
			else
				player.sendMessage("The patch is not ready to be seeded.");
			return;
		}

		AchievementManager.processFor(AchievementType.OVER_GREEN, player);
		AchievementManager.processFor(AchievementType.GREENLAND, player);
		player.sendMessage("You plant the seed.");
		player.getPoints().increase(AttributeManager.Points.SEEDS_PLANTED, 1); // Increase points
		player.getInventory().delete(plant.getSeed());
		player.performAnimation(new Animation(FarmingConstants.SEED_DIBBING));
		player.getSkillManager().addExperience(Skill.FARMING, plant.getPlantXP());
		tarnsformPatchObject(PatchState.HERB_GROWING_1);
		this.setPlant(plant);
	}

	public void tarnsformPatchObject(PatchState patchState) {
		setPatchState(patchState);

		reloadConfig();

		setLastAction(System.currentTimeMillis());
	}

	public void reloadConfig() {
		player.getPacketSender().sendVarbit(patchType.getVarbit(), state.getChildIndex());
	}
	
	public void reset() {
		setPlant(null);
		setCompostType(CompostType.NONE);
		tarnsformPatchObject(PatchState.HERB_RAKED);
	}

	public PatchState getState() {
		return state;
	}

	public void setPatchState(PatchState state) {
		this.state = state;
	}

	public Plant getPlant() {
		return plant;
	}

	public void setPlant(Plant plant) {
		this.plant = plant;
	}

	public CompostType getCompostType() {
		return compostType;
	}

	public void setCompostType(CompostType compostType) {
		this.compostType = compostType;
	}

	public Long getLastAction() {
		return lastAction;
	}

	public void setLastAction(Long lastAction) {
		this.lastAction = lastAction;
	}

	public void setHerbsLeft(int herbsLeft) {
		this.herbsLeft = herbsLeft;
	}

	public int getHerbsLeft() {
		return herbsLeft;
	}

}