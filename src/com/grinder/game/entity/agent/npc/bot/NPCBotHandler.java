package com.grinder.game.entity.agent.npc.bot;

import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.model.consumable.edible.Edible;
import com.grinder.game.entity.agent.combat.attack.AttackStrategy;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.util.Priority;
import com.grinder.util.time.SecondsTimer;
import com.grinder.util.timing.Stopwatch;
import com.grinder.util.timing.TimerKey;

/**
 * Represents an NPC Bot.
 * 
 * @author Professor Oak
 */
public abstract class NPCBotHandler {

	/**
	 * The id of the Rune main bot npc.
	 */
	private static final int RUNE_MAIN_BOT_ID = 1158;
	
	/**
	 * The id of the archer bot npc.
	 */
	private static final int ARCHER_BOT_ID = 4096;
	
	/**
	 * Assigns a bot handler to specified {@link NPC}.
	 */
	public static void assignBotHandler(NPC npc) {
		switch(npc.getId()) {

			case 1200:
		case RUNE_MAIN_BOT_ID:
			npc.setBotHandler(new RuneMainBot(npc));
			break;
		case ARCHER_BOT_ID:
			npc.setBotHandler(new ArcherBot(npc));
			break;
		}
	}
	
	/**
	 * The npc, owner of this instance.
	 */
	public NPC npc;
	
	/**
	 * Constructs a new npc bot.
	 * @param id		The bot's npc id.
	 * @param position	The bot's default position.
	 */
	public NPCBotHandler(NPC npc) {
		this.npc = npc;
		this.eatDelay = new Stopwatch();
		this.vengeanceDelay = new SecondsTimer();
	}

	/**
	 * Processes this bot.
	 */
	public abstract void process();
	
	/**
	 * Handles what happens when the bot
	 * dies.
	 */
	public abstract void onDeath(Player killer);

	/**
	 * Gets the bot's combat method.
	 */
	public abstract AttackStrategy<? extends Agent> getMethod();

	/**
	 * The max amount of damage the bot can return
	 * using the Ring Of Recoil.
	 */
	public abstract int maxRecoilDamage();

	/**
	 * The amount of times we have eaten food.
	 */
	private int eatCounter;

	/**
	 * The amount of damage we've recoiled back
	 * to an attacker.
	 */
	private int recoiledDamage;

	/**
	 * The delay for eating food.
	 * Makes sure food isn't consumed too quick.
	 */
	private Stopwatch eatDelay;

	/**
	 * The delay for casting vengeance
	 * Makes sure vengeance is only cast every 30 seconds.
	 */
	private SecondsTimer vengeanceDelay;

	/**
	 * Resets all attributes.
	 */
	public void reset() {

		//Reset our attributes
		recoiledDamage = 0;
		eatCounter = 0;
		npc.setSpecialPercentage(100);
		npc.setSpecialActivated(false);

		//Reset hitpoints
		npc.setHitpoints(npc.fetchDefinition().getHitpoints());
	}

	/**
	 * Eats the specified {@link FoodType}.
	 * @param shark			The food to eat.
	 * @param minDelayMs	The minimum delay between each eat in ms.
	 */
	public void eat(Edible shark, int minDelayMs) {
		//Make sure delay has finished..
		if(eatDelay.elapsed(minDelayMs)) {
			int heal = 20;
			int currentHp = npc.getHitpoints();
			int maxHp = npc.fetchDefinition().getHitpoints();

			//Heal us..
			if(currentHp + heal > maxHp) {
				npc.setHitpoints(maxHp);
			} else {
				npc.setHitpoints(currentHp + heal);
			}

			//Increase attack delay..
			npc.getCombat().extendNextAttackDelay(5);
			//Perform eat animation..
			npc.performAnimation(new Animation(829, Priority.HIGH));

			//Increase counter..
			eatCounter++;

			//Reset the eat delay..
			eatDelay.reset();
		}
	}

	/**
	 * Cast vengeances.
	 * There's a delay, allowing it to only be cast every 30 seconds.
	 */
	public void castVengeance() {

		//Make sure we don't already have vengeance active.
		if(!npc.getVengeanceEffect().finished()) {
			return;
		}

		//Make sure delay has finished..
		if(vengeanceDelay.finished()) {

			//Perform veng animation..
			npc.performAnimation(new Animation(4409));

			//Perform veng graphic..
			npc.performGraphic(new Graphic(726, GraphicHeight.HIGH));

			//Force chat..
			//npc.say("Taste Vengeance!");

			//Set has vengeance..
			npc.getVengeanceEffect().start(30);

			//Reset the veng delay..
			vengeanceDelay.start(30);
		}
	}

	/**
	 * Attempts to get the bot's current opponent.
	 * Either it's the target or it's an attacker.
	 * @return		The opponent player.
	 */
	public Player getOpponent() {
		Agent p = npc.getCombat().getTarget();
		if(p == null) {
			p = npc.getCombat().getOpponent();
		}
		if(p != null && p.isPlayer()) {
			return p.getAsPlayer();
		}
		return null;
	}

	/**
	 * Gets the overhead prayer which the bot
	 * should currently be using, based on the opponent's
	 * choices.
	 * @return
	 */
	public int getOverheadPrayer(final Player p, final boolean inDistance) {
		int prayer = -1;
		
		//Check if the enemy isn't in range..
		if(inDistance) {

			//Check if enemy is in range and if they're smiting..
			//If so, we will do the same.
			if(PrayerHandler.isActivated(p, PrayerHandler.SMITE)) {
				prayer = PrayerHandler.SMITE;
			}

		}

		//Check if enemy is protecting against our combat type..
		//Or if they're farcasting..
		//If so, we will counter pray.
		if(prayer == -1) {
			int counterPrayer = PrayerHandler.getProtectingPrayer(getMethod().type());
			if(PrayerHandler.isActivated(p, counterPrayer) || (!inDistance && !npc.getTimerRepository().has(TimerKey.FREEZE))) {
				prayer = PrayerHandler.getProtectingPrayer(p.getCombat().determineStrategy().type());
			}
		}
		return prayer;
	}
	
	/**
	 * Transforms an npc into a different one.
	 * @param id		The new npc id.
	 */
	public void transform(int id) {
		
		//Check if we haven't already transformed..
		if(npc.getNpcTransformationId() == id) {
			return;
		}
		
		//Set the transformation id.
		npc.setNpcTransformationId(id);
	}

	public Stopwatch getEatDelay() {
		return eatDelay;
	}

	public int getEatCounter() {
		return eatCounter;
	}

	public int getRecoiledDamage() {
		return recoiledDamage;
	}

	public void incrementRecoiledDamage(int recoiledDamage) {
		this.recoiledDamage += recoiledDamage;
	}
}
