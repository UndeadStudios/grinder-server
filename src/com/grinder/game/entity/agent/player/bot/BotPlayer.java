package com.grinder.game.entity.agent.player.bot;

import com.grinder.game.World;
import com.grinder.game.content.clan.GlobalClanChatManager;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.bot.script.BotScript;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.task.TaskManager;
import com.grinder.net.session.PlayerSession;
import com.grinder.util.Misc;
import com.grinder.util.benchmark.SimpleBenchMarker;

public class BotPlayer extends Player {

	/**
	 * The currently active script.
	 */
	private BotScript activeScript;
	private boolean printMessages = false;

	/**
	 * Gets the active script.
	 * 
	 * @return The active script.
	 */
	public BotScript getActiveScript() {
		return activeScript;
	}

	/**
	 * Sets the active script.
	 * 
	 * @param activeScript
	 *            The active script.
	 */
	public void setActiveScript(BotScript activeScript) {
		this.activeScript = activeScript;
	}

	/**
	 * Gets the currently active script's name.
	 * 
	 * @return The name.
	 */
	public String getActiveScriptName() {
		if (activeScript == null) {
			return null;
		}

		return activeScript.getClass().getSimpleName();
	}

	public boolean isPrintMessages() {
		return printMessages;
	}

	public void setPrintMessages(boolean printMessages) {
		this.printMessages = printMessages;
	}

	/**
	 * The end of the session.
	 */
	private long sessionEnd;

	/**
	 * Gets the session end.
	 *
	 * @return
	 */
	public long getSessionEnd() {
		return sessionEnd;
	}

	/**
	 * Sets the session's length.
	 *
	 * @param sessionEnd
	 *            The session's length.
	 */
	public void setSessionEnd(long sessionEnd) {
		this.sessionEnd = sessionEnd;
	}
	
	/**
	 * Creates a new {@link BotPlayer}.
	 * 
	 * @param username
	 *            The username.
	 */
	public BotPlayer(String username, Position position) {
		super(new PlayerSession(null));
		
		getSession().setPlayer(this);

		setUsername(username);
		
		setLongUsername(Misc.stringToLong(username));

		getPosition().setAs(position);

		if (!World.getBotPlayerLoginQueue().contains(this)) {
			World.getBotPlayerLoginQueue().add(this);
		}

	}

	public BotPlayer(String username) {
		this(username, World.getStartPosition());
	}

	public void onLoginComplete(){
		GlobalClanChatManager.joinDefaultClan(this);
	}
	/**
	 * Walks to the specified location.
	 * 
	 * @param position
	 *            The location.
	 */
	public void walkTo(Position position) {
		//PathFinder.getPathFinder().findRoute(new String(), this, position.getX(), position.getY(), true, 1, 1);
	}

	/**
	 * Randomizes the bot's levels.
	 */
	public void randomizeLevels() {
		for (int i = 0; i <= 6; i++) {
			int level = Misc.randomInclusive(70, 99);
			
			getSkillManager().setCurrentLevel(Skill.values()[i], level, true);
			getSkillManager().setExperienceIfMoreThanCurrent(Skill.values()[i], SkillUtil.calculateExperienceForLevel(level));
			getSkillManager().setMaxLevel(Skill.values()[i], level, true);
		}
	}

	@Override
	public void sequence(SimpleBenchMarker benchMarker) {
		super.sequence(benchMarker);

//		if(Misc.randomChance(1)){
//			GlobalClanChatManager.sendMessage(this, "Hey "+getUsername()+" here!");
//		} else if(Misc.randomChance(10)){
//
//			if(Misc.randomChance(1) ) {
//				final Player nearby = Misc.random(getLocalPlayers());
//				if (nearby != null) {
//					getMotion().setTarget(nearby);
//				}
//			} else if(Misc.randomChance(20)) {
//				if (Misc.randomChance(40)) {
//					if(Misc.randomChance(1)){
//						Teleporting.handleButton(this, Misc.randomInt(19210, 21741, 30016));
//					} else {
//						getMotion().enqueueStepAwayWithCollisionCheck();
//					}
//				} else {
//					final Player nearby = Misc.random(getLocalPlayers());
//					if (nearby != null) {
//						say("How u doing "+nearby.getUsername());
//					}
//				}
//			} else {
//				Emotes.EmoteData randomEmote = Misc.randomEnum(Emotes.EmoteData.class);
//				performAnimation(randomEmote.animation);
//			}
//		}

	}

	/**
	 * Heals the bot player.
	 */
	public void heal(int heal) {
		int currentLevel = getSkillManager().getSkills().getLevels()[Skill.HITPOINTS.ordinal()];
		
		int maxLevel = getSkillManager().getSkills().getMaxLevels()[Skill.HITPOINTS.ordinal()];

		if (heal + currentLevel > maxLevel) {
			heal = maxLevel - currentLevel;
		}
		
		performAnimation(new Animation(829));
		getSkillManager().setCurrentLevel(Skill.HITPOINTS, heal + currentLevel, true);
	}
	
	@Override
	public void onLogout() {
		System.out.println("[World] Deregistering player - [username, host] : [" + getUsername() + ", " + getHostAddress() + "]");
		getRelations().updateLists(false);
		GlobalClanChatManager.leave(this, false);
		TaskManager.cancelTasks(this);
	}

}
