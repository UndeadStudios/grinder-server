package com.grinder.game.content.skill.skillable.impl.magic;

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
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.entity.object.ClippedMapObjects;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.model.Animation;
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

import java.util.Optional;

/**
 * Represents the Woodcutting skill.
 *
 * @author Professor Oak
 */
public class ChargeOrbSkillable extends DefaultSkillable {

	private Item requiredItems;
	private int experience;
	private Item reward;
	private Graphic gfx;
	private int requiredLevel;
	/**
	 * Constructs a new {@link ChargeOrbSkillable}.
	 */
	public ChargeOrbSkillable(Item requiredRunes, int exp, Item reward, Graphic gfx, int requiredLevel) {
		this.requiredItems = requiredRunes;
		this.experience = exp;
		this.reward = reward;
		this.gfx = gfx;
		this.requiredLevel = requiredLevel;
	}

	@Override
	public void start(Player player) {
		super.start(player);
	}

	@Override
	public void startAnimationLoop(Player player) {
		Task animLoop = new Task(6, player, true) {
			@Override
			protected void execute() {
				player.performAnimation(new Animation(726));
			}
		};
		TaskManager.submit(animLoop);
		getTasks().add(animLoop);
	}

	@Override
	public void startGraphicsLoop(Player player) {
		Task gfxLoop = new Task(6, player, true) {
			@Override
			protected void execute() {
				player.performGraphic(gfx);
			}
		};
		TaskManager.submit(gfxLoop);
		getTasks().add(gfxLoop);
	}

	@Override
	public void startSoundLoop(Player player) {

	}

	@Override
	public void onCycle(Player player) {

	}

	@Override
	public void finishedCycle(Player player) {
		// Add logs..
		player.getInventory().delete(requiredItems);
		player.getInventory().delete(new Item(ItemID.COSMIC_RUNE, 3));
		player.getInventory().delete(new Item(ItemID.UNPOWERED_ORB));
		player.getInventory().add(reward);
	}

	@Override
	public int cyclesRequired(Player player) {
		return 6;
	}
	
	@Override
	public void onCancel(Player player) {

	}

	@Override
	public boolean hasRequirements(Player player) {

		if (player.getInventory().contains(ItemID.UNPOWERED_ORB) && player.getInventory().contains(requiredItems) && player.getInventory().contains(new Item(ItemID.COSMIC_RUNE, 3)))
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean loopRequirements() {
		return true;
	}

	@Override
	public boolean allowFullInventory() {
		return false;
	}

}
