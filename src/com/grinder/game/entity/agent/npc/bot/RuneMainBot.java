package com.grinder.game.entity.agent.npc.bot;

import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackStrategy;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType;
import com.grinder.game.entity.agent.combat.hit.Hit;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.model.Animation;
import com.grinder.game.model.consumable.edible.Edible;
import com.grinder.game.model.item.Item;
import com.grinder.util.Misc;
import com.grinder.util.timing.TimerKey;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a main rune bot.
 * 
 * @author Professor Oak
 */
public class RuneMainBot extends NPCBotHandler implements AttackStrategy<NPC> {

	/**
	 * The default npc.
	 */
	private static final int DEFAULT_BOT_ID = 1158;

	/**
	 * The npc which has a special attack weapon equipped.
	 */
	private static final int SPEC_BOT_ID = 1200;

	public RuneMainBot(NPC npc) {
		super(npc);
		npc.setSpecialPercentage(100);
	}

	@Override
	public void process() {

		// Check if npc is in combat..
		if (npc.getCombat().isInCombat()) {

			// Make sure our opponent is valid..
			Player opponent = getOpponent();
			if (opponent == null) {
				return;
			}

			// Are we in distance to the opponent?
			final boolean inDistance = (npc.getPosition().getDistance(opponent.getPosition()) <= getMethod()
					.requiredDistance(npc));

			// Activate piety..
			PrayerHandler.activatePrayer(npc, PrayerHandler.PIETY);

			// Activate any overheads..
			int overhead = getOverheadPrayer(opponent, inDistance);
			if (overhead != -1) {

				// Activate overhead!
				PrayerHandler.activatePrayer(npc, overhead);

			} else {

				// We shouldn't be using any overhead.
				// Make sure to turn off any headicons.
				if (npc.getHeadIcon() != -1) {
					npc.setHeadIcon(-1);
				}
			}

			// Eat whenever we need to.
			if (npc.getHitpoints() > 0) {
				if (npc.getHitpoints() < 40 + Misc.getRandomInclusive(15)) {
					if (getEatCounter() < 21) {
						super.eat(Edible.SHARK, 1200);
					}
				}

				// Cast vengeance when ever we can.
				super.castVengeance();

			} else {
				npc.say("Good fight!");
			}

			// Check farcasting..
			if (npc.getTimerRepository().has(TimerKey.FREEZE)) {
				if (Misc.getRandomInclusive(20) == 10) {
					npc.say("Farcasting...?");
				}
			}

			// Activate it randomly and if they're in distance..
			if (inDistance && npc.getSpecialPercentage() > SpecialAttackType.PUNCTURE.getCustomDrainAmount()) {

				if (opponent.getHitpoints() < 35) {
					npc.setSpecialActivated(true);
				} else if (Misc.getRandomInclusive(10) <= 4) {
					npc.setSpecialActivated(true);
				}
			}

			// Randomly turn it off..
			if (!inDistance || Misc.getRandomInclusive(5) == 1) {
				npc.setSpecialActivated(false);
			}

			// Update npc depending on the special attack state
			if (!npc.isSpecialActivated()) {
				transform(DEFAULT_BOT_ID);
			} else {
				transform(SPEC_BOT_ID);
			}

		} else {

			// Turn off prayers
			if (npc.getHeadIcon() != -1 || PrayerHandler.isActivated(npc, PrayerHandler.PIETY)) {
				PrayerHandler.deactivatePrayers(npc);
			}

			// Reset weapon
			transform(DEFAULT_BOT_ID);

			// Reset all attributes
			super.reset();
		}
	}

	@Override
	public void onDeath(Player killer) {

		transform(DEFAULT_BOT_ID);

		//There should be a small chance of receiving a tier 1 emblem
		if(Misc.getRandomInclusive(15) == 1 && (!killer.getGameMode().isIronman() && !killer.getGameMode().isHardcore() && !killer.getGameMode().isUltimate() && !killer.getGameMode().isSpawn())) {
			ItemOnGroundManager.register(killer, new Item(13307, 10000), npc.getPosition());
			killer.getPacketSender().sendMessage("@red@You have been awarded 10,000 Blood money for successfully killing the bot.");
			PlayerUtil.broadcastMessage("<img=764> @red@ " + PlayerUtil.getImages(killer) + "" + killer.getUsername() +" has been awarded 10,000 Blood money for successfully killing the Edgeville bot.");
		} else {
			killer.getPacketSender().sendMessage("The bot didn't drop anything. Maybe you're more lucky next time.");
			if (killer.getGameMode().isIronman() || killer.getGameMode().isHardcore() || killer.getGameMode().isUltimate()) {
				killer.getPacketSender().sendMessage("You're not eligible for a drop on an Iron Man account.");
			} else if (killer.getGameMode().isSpawn()) {
				killer.getPacketSender().sendMessage("You're not eligible for a drop on spawn game mode.");
			}
		}
	}

	@Override
	public AttackStrategy<? extends Agent> getMethod() {
		return npc.isSpecialActivated() ? SpecialAttackType.PUNCTURE.getStrategy() : this;
	}

	@Override
	public int maxRecoilDamage() {
		return 100;
	}

	@Override
	public boolean canAttack(@NotNull NPC actor, @NotNull Agent target) {
		return true;
	}

	@Override
	public int duration(NPC actor) {
		return actor.getBaseAttackSpeed();
	}

	@Override
	public int requiredDistance(@NotNull Agent actor) {
		return 1;
	}

	@Override
	public void animate(NPC actor) {
		int animation = actor.getAttackAnim();
		if (animation != -1)
			actor.performAnimation(new Animation(animation));
	}

	@Override
	public AttackType type() {
		return AttackType.MELEE;
	}

	@NotNull
	@Override
	public Hit[] createHits(@NotNull NPC actor, @NotNull Agent target) {
		return new Hit[] { new Hit(actor, target, this, true, 0) };
	}

	@Override
	public void postHitEffect(@NotNull Hit hit) {
	}

	@Override
	public void sequence(@NotNull NPC actor, @NotNull Agent target) {
	}

	@Override
	public void postHitAction(@NotNull NPC actor, @NotNull Agent target) {
	}

	@Override
	public void postIncomingHitEffect(@NotNull Hit hit) {

	}

}