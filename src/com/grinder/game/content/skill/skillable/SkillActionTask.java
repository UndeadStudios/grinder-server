package com.grinder.game.content.skill.skillable;

import com.grinder.game.content.skill.SkillRequirement;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.Skill;
import com.grinder.game.task.Task;

/**
 * Represents a skill action
 *
 * @author 2012 <http://www.rune-server.org/members/dexter+morgan/>
 */
public abstract class SkillActionTask {

	/**
	 * The items required
	 */
	private Item[] required;

	/**
	 * The item to remove
	 */
	private Item[] remove;

	/**
	 * The items to add
	 */
	private Item[] add;

	/**
	 * The skill settings
	 */
	private SkillRequirement settings;

	/**
	 * The animation
	 */
	private Animation animation;

	/**
	 * The graphic
	 */
	private Graphic graphic;

	/**
	 * The amount of times to complete the action
	 */
	private int amount;

	/**
	 * The skill
	 */
	private Skill skill;

	/**
	 * The delay
	 */
	private int delay;

	/**
	 * Whether to reset animation
	 */
	private boolean resetAnimation;
	
	/**
	 * States if the task is executed immediately
	 */
	private boolean immediate;

	/**
	 * The task
	 */
	private Task task;

	/**
	 * Represents a skill action
	 *
	 * @param required
	 *            the required
	 * @param remove
	 *            the remove
	 * @param add
	 *            the add
	 * @param settings
	 *            the skill settings
	 * @param animation
	 *            the animation
	 * @param graphic
	 *            the graphic
	 * @param amount
	 *            the amount
	 * @param delay
	 *            the delay
	 */
	public SkillActionTask(Item[] required, Item[] remove, Item[] add, SkillRequirement settings, Animation animation,
			Graphic graphic, int amount, Skill skill, int delay) {
		this.setRequired(required);
		this.setRemove(remove);
		this.setAdd(add);
		this.setSettings(settings);
		this.setAnimation(animation);
		this.setGraphic(graphic);
		this.setAmount(amount);
		this.setSkill(skill);
		this.setDelay(delay);
	}

	/**
	 * Represents a basic skill action with no items involved
	 *
	 * @param settings
	 *            the skill settings
	 * @param amount
	 *            the amount
	 * @param delay
	 *            the delay
	 */
	public SkillActionTask(SkillRequirement settings, int amount, Skill skill, int delay) {
		this(settings, amount, skill, delay, false);
	}
	
	/**
	 * Represents a basic skill action with no items involved
	 *
	 * @param settings
	 *            the skill settings
	 * @param amount
	 *            the amount
	 * @param delay
	 *            the delay
	 * @param immediate
	 *            is the task immediate?
	 */
	public SkillActionTask(SkillRequirement settings, int amount, Skill skill, int delay, boolean immediate) {
		this.setRequired(null);
		this.setRemove(null);
		this.setAdd(null);
		this.setSettings(settings);
		this.setAnimation(null);
		this.setGraphic(null);
		this.setAmount(amount);
		this.setSkill(skill);
		this.setDelay(delay);
		this.immediate = immediate;
	}

	/**
	 * Represents a single skill action with no animation reset
	 *
	 * @param required
	 *            the required
	 * @param remove
	 *            the remove
	 * @param add
	 *            the add
	 * @param settings
	 *            the skill settings
	 * @param skill
	 *            the skill
	 */
	public SkillActionTask(Item[] required, Item[] remove, Item[] add, SkillRequirement settings, Skill skill) {
		this.setRequired(required);
		this.setRemove(remove);
		this.setAdd(add);
		this.setSettings(settings);
		this.setAnimation(null);
		this.setGraphic(null);
		this.setAmount(1);
		this.setSkill(skill);
		this.setDelay(1);
		this.setResetAnimation(true);
	}

	/**
	 * The skill precheck
	 *
	 * @param player
	 *            the player
	 * @return the check
	 */
	public boolean precheck(Player player) {
		/*
		 * Checks level
		 */
		if (player.getSkillManager().getCurrentLevel(getSkill()) < getSettings().getLevelRequired()) {
			DialogueManager.sendStatement(player, "You don't have the required " + getSkill().getName() + " level to do this. You need at least a level of " + getSettings().getLevelRequired() + " " + getSkill().getName() + " to do this.");
			return false;
		}
		if (player.getSkillManager().getCurrentLevel(getSkill()) < getSettings().getLevelRequired()) {
			player.getPacketSender()
					.sendMessage("You don't have the required " + getSkill().getName() + " to do this.");
			player.getPacketSender().sendMessage(
					"You need a " + getSettings().getLevelRequired() + " " + getSkill().getName() + " to do this.");
			return false;
		}
		/*
		 * Checks required items
		 */
		if (required != null) {
			for (Item req : required) {
				if (req == null) {
					continue;
				}
				if (req.getId() < 1) {
					continue;
				}
				if (!player.getInventory().contains(req) && !player.getEquipment().contains(req)) { // TODO:
																									// Might
																									// have
																									// unintentional
																									// side
																									// effects
					if (req.getAmount() > 1) {
						player.getPacketSender().sendMessage(
								"You don't have enough " + req.getDefinition().getName() + "s to do this.");
					} else {
						player.getPacketSender()
								.sendMessage("You need a " + req.getDefinition().getName() + " to do this.");
					}
					return false;
				}
			}
		}
		/*
		 * Checks inventory space
		 */
		if (remove == null && add != null) {
			for (Item toAdd : add) {
				if (player.getInventory().countFreeSlots() == 0) {
					if (!player.getInventory().contains(toAdd) && toAdd.getDefinition().isStackable()) {
						player.getPacketSender().sendMessage("You don't have any inventory space to do this.");
						return false;
					} else {
						player.getPacketSender().sendMessage("You don't have any inventory space to do this.");
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Skill action
	 *
	 * @param player
	 *            the player
	 */
	public Task action(Player player) {
		/*
		 * Removes interface
		 */
		player.getPacketSender().sendInterfaceRemoval();
		/*
		 * Skill precheck
		 */
		if (!precheck(player)) {
			return null;
		}
		/*
		 * Initiation performance
		 */
		player.performAnimation(getAnimation());
		player.performGraphic(getGraphic());
		sendBeforeSkillAction(player);

		/*
		 * The skill task
		 */
		task = new Task(getDelay()) {
			/*
			 * The amount of times to repeat task
			 */
			int repeat = getAmount();

			@Override
			protected void execute() {
				/*
				 * No repeat
				 */
				if (repeat == 0) {
					sendEndAction(player);
					stop();
					return;
				}
				/*
				 * No skill task
				 */
				if (player.getSkillAction().getTask() == null) {
					sendEndAction(player);
					stop();
					return;
				}
				/*
				 * Precheck
				 */
				if (!precheck(player)) {
					sendEndAction(player);
					stop();
					return;
				}
				/*
				 * Successful
				 */
				if (success(player)) {
					/*
					 * Removes items
					 */
					if (getRemove() != null) {
						player.getInventory().delete(getRemove());
					}
					/*
					 * Adds items
					 */
					if (getAdd() != null) {
						player.getInventory().addItemSet(getAdd());
					}

					/*
					 * Check Task List
					 */
					//TaskList.ProgressTaskSkill(player, getSkill().ordinal(), getAdd().toString());

					/*
					 * Adds experience
					 */
					if (getSettings().getExperienceReceived() != 0) {
						if (getSkill()!=Skill.AGILITY || (getSkill()==Skill.AGILITY && !EntityExtKt.getBoolean(player, Attribute.DID_FAIL_AGILITY_OBSTACLE, false)))
							player.getSkillManager().addExperience(getSkill(),
								(int) (getSettings().getExperienceReceived() * getExpModifier(player)));
					}
					/*
					 * The skill action
					 */
					sendSkillAction(player);
				}
				/*
				 * Repeating
				 */
				if (--repeat > 0) {
					player.performGraphic(getGraphic());
					player.performAnimation(getAnimation());
				} else if (!resetAnimation) {
					player.getPacketSender().sendAnimationReset();
					sendEndAction(player);
					stop();
				}
			}
		};
		return task;
	}

	/**
	 * The action before main action
	 *
	 * @param player
	 *            the player
	 */
	public void sendBeforeSkillAction(Player player) {

	}

	/**
	 * Custom skill action method
	 *
	 * @param player
	 *            the player
	 */
	public void sendSkillAction(Player player) {

	}

	/**
	 * The end action of the skill
	 * 
	 * @param player
	 *            the player
	 */
	public void sendEndAction(Player player) {

	}

	/**
	 * Gets the exp modifier
	 * 
	 * @param player
	 *            the player
	 * @return the modifier
	 */
	public double getExpModifier(Player player) {
		return 1.0;
	}

	/**
	 * Whether successful skill action
	 *
	 * @param player
	 *            the player
	 * @return success
	 */
	public boolean success(Player player) {
		return true;
	}

	/**
	 * Sets the required
	 *
	 * @return the required
	 */
	public Item[] getRequired() {
		return required;
	}

	/**
	 * Sets the required
	 *
	 * @param required
	 *            the required
	 */
	public void setRequired(Item[] required) {
		this.required = required;
	}

	/**
	 * Sets the remove
	 *
	 * @return the remove
	 */
	public Item[] getRemove() {
		return remove;
	}

	/**
	 * Sets the remove
	 *
	 * @param remove
	 *            the remove
	 */
	public void setRemove(Item[] remove) {
		this.remove = remove;
	}

	/**
	 * Sets the add
	 *
	 * @return the add
	 */
	public Item[] getAdd() {
		return add;
	}

	/**
	 * Sets the add
	 *
	 * @param add
	 *            the add
	 */
	public void setAdd(Item[] add) {
		this.add = add;
	}

	/**
	 * Sets the settings
	 *
	 * @return the settings
	 */
	public SkillRequirement getSettings() {
		return settings;
	}

	/**
	 * Sets the settings
	 * 
	 * @param settings
	 *            the settings
	 */
	public void setSettings(SkillRequirement settings) {
		this.settings = settings;
	}

	/**
	 * Sets the animation
	 *
	 * @return the animation
	 */
	public Animation getAnimation() {
		return animation;
	}

	/**
	 * Sets the animation
	 *
	 * @param animation
	 *            the animation
	 */
	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	/**
	 * Sets the graphic
	 *
	 * @return the graphic
	 */
	public Graphic getGraphic() {
		return graphic;
	}

	/**
	 * Sets the graphic
	 *
	 * @param graphic
	 *            the graphic
	 */
	public void setGraphic(Graphic graphic) {
		this.graphic = graphic;
	}

	/**
	 * Sets the amount
	 *
	 * @return the amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Sets the amount
	 *
	 * @param amount
	 *            the amount
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * Sets the skill
	 *
	 * @return the skill
	 */
	public Skill getSkill() {
		return skill;
	}

	/**
	 * Sets the skill
	 *
	 * @param skill
	 *            the skill
	 */
	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	/**
	 * Sets the delay
	 *
	 * @return the delay
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * Sets the delay
	 *
	 * @param delay
	 *            the delay
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	/**
	 * Sets the resetAnimation
	 *
	 * @return the resetAnimation
	 */
	public boolean isResetAnimation() {
		return resetAnimation;
	}

	/**
	 * Sets the resetAnimation
	 * 
	 * @param resetAnimation
	 *            the resetAnimation
	 */
	public void setResetAnimation(boolean resetAnimation) {
		this.resetAnimation = resetAnimation;
	}
	
	/**
	 * Gets if the {@code SkillActionTask} is immediate.
	 * 
	 * @return <code>true</code> if immediate
	 */
	public boolean isImmediate() {
		return immediate;
	}

	/**
	 * Gets the task
	 *
	 * @return the task
	 */
	public Task getTask() {
		return task;
	}

	/**
	 * Sets the task
	 * 
	 * @param task
	 *            the task
	 */
	public void setTask(Task task) {
		this.task = task;
	}
}
