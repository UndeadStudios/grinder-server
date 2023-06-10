package com.grinder.game.model;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.grinder.game.GameConstants;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.collection.ShuffledCircularList;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This enum represents a skill in the game.
 * Every skill should be added with its
 * proper chatbox level up interface.
 *
 * @author Professor Oak
 */
public enum Skill {

    ATTACK(6247, 8654, 361), // 1
    DEFENCE(6253, 8660, 362), // 2
    STRENGTH(6206, 8657, 363), // 3
    HITPOINTS(6216, 8655, 364), // 4
    RANGED(4443, 8663, 365), // 5
    PRAYER(6242, 8666, 366), // 6
    MAGIC(6211, 8669, 367), // 7
    COOKING(6226, 8665, 80), // 8
    WOODCUTTING(4272, 8671, 81), // 9
    FLETCHING(6231, 8670, 82), // 10
    FISHING(6258, 8662, 83), // 11
    FIREMAKING(4282, 8668, 84), // 12
    CRAFTING(6263, 8667, 85), // 13
    SMITHING(6221, 8659, 86), // 14
    MINING(4416, 8656, 87), // 15
    HERBLORE(6237, 8661, 88), // 16
    AGILITY(4277, 8658, 89), // 17
    THIEVING(4261, 8664, 90), // 18
    SLAYER(12122, 12162, 91), // 19
    FARMING(5267, 13928, 92), // 20
    RUNECRAFTING(4267, 8672, 93), // 21
    CONSTRUCTION(7267, 18801, 95), // 22
    HUNTER(8267, 18829, 94) // 23
    ;

    /**
     * The {@link ImmutableSet} which represents the skills that a player can set to a desired level.
     */
    //private static final ImmutableSet<Skill> ALLOWED_TO_SET_LEVLES = Sets.immutableEnumSet(ATTACK, DEFENCE, STRENGTH, HITPOINTS, RANGED, MAGIC);
    private static final ImmutableSet<Skill> ALLOWED_TO_SET_LEVLES = Sets.immutableEnumSet(ATTACK, DEFENCE, STRENGTH, HITPOINTS, RANGED, MAGIC);
    private static Map<Integer, Skill> skillMap = new HashMap<Integer, Skill>();
	private static final ShuffledCircularList<Skill> BONUS_SKILLS = ShuffledCircularList.of(COOKING, WOODCUTTING, FLETCHING, FISHING, FIREMAKING, CRAFTING, SMITHING, MINING, HERBLORE, AGILITY, THIEVING, SLAYER, FARMING, RUNECRAFTING, /*CONSTRUCTION,*/ HUNTER);
	public static final ImmutableSet<Skill> COMBAT_SKILLS = Sets.immutableEnumSet(ATTACK, DEFENCE, STRENGTH, RANGED, MAGIC, PRAYER);
	private static long bonusSkillTimer = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);

	public static String getBonusSkillTimeLeft() {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(bonusSkillTimer - System.currentTimeMillis());
        if (minutes <= 0) {
            minutes = 1;
        }
	    return String.valueOf(minutes) + " Minute" + (minutes > 1 ? "s" : "");
    }

    static {
        for (Skill skill : Skill.values()) {
            skillMap.put(skill.button, skill);
        }
        
        if (GameConstants.BONUS_SKILL_ENABLED) {
            TaskManager.submit(new Task(6000) {
    			@Override
    			protected void execute() {
    				changeBonusSkill();
    			}
            	
            });
        }
    }

    /**
     * The {@link Skill}'s chatbox interface
     * The interface which will be sent
     * on levelup.
     */
    private final int chatboxInterface;
    /**
     * The {@link Skill}'s button in the skills tab
     * interface.
     */
    private final int button;

    private final int imageIcon;

    Skill(int chatboxInterface, int button, int imageIcon) {
        this.chatboxInterface = chatboxInterface;
        this.button = button;
        this.imageIcon = imageIcon;
    }

    /**
     * Gets a skill for its button id.
     *
     * @param button The button id.
     * @return The skill with the matching button.
     */
    public static Skill forButton(int button) {
        return skillMap.get(button);
    }

    /**
     * Checks if a skill can be manually set to a level by a player.
     *
     * @return true if the player can set their level in this skill, false otherwise.
     */
    public boolean canSetLevel() {
        return ALLOWED_TO_SET_LEVLES.contains(this);
    }

    /**
     * Gets the {@link Skill}'s chatbox interface.
     *
     * @return The interface which will be sent on levelup.
     */
    public int getChatboxInterface() {
        return chatboxInterface;
    }

    /**
     * Gets the {@link Skill}'s button id.
     *
     * @return The button for this skill.
     */
    public int getButton() {
        return button;
    }

    /**
     * Gets the {@link Skill}'s name.
     *
     * @return The {@link Skill}'s name in a suitable format.
     */
    public String getName() {
        return Misc.formatText(toString().toLowerCase());
    }

    public int getImageIcon() {
        return imageIcon;
    }

    /**
	 * Gets the bonus skill.
	 * 
	 * @return the bonus skill
	 */
	public static Skill getBonusSkill() {
		return BONUS_SKILLS.get();
	}
	
	/**
	 * Changes the bonus skill.
	 */
    public static void changeBonusSkill() {
        bonusSkillTimer = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
    	BONUS_SKILLS.increment();
    	
    	PlayerUtil.broadcastMessage("@red@The hourly Bonus Skill has been changed to: @blu@" + getBonusSkill().getName());
    }

    public static boolean hasCorrectLevel(Player player, Skill skill, int requirement) {
        if (player.getSkillManager().getCurrentLevel(skill) < requirement) {
            player.getPacketSender().sendMessage("You don't have the required " + skill.getName()
                    + " to do this. You need a level of " + requirement);
            player.getPacketSender().sendMessage(skill.getName() + " to do this.");
            return false;
        }
        return true;
    }
}