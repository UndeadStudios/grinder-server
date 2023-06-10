package com.grinder.game.entity.agent.npc.bot;

import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.consumable.edible.Edible;
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Ammunition;
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeapon;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;
import com.grinder.util.Misc;

/**
 * Represents a simple archer bot.
 * @author Professor Oak
 */
public class ArcherBot extends NPCBotHandler {

	private boolean ramboMode;
	private int ramboShots;

	public ArcherBot(NPC npc) {
		super(npc);
		npc.getCombat().setAmmunition(Ammunition.RUNE_JAVELIN);
		npc.getCombat().setRangedWeapon(RangedWeapon.BALLISTA);	
	}

	@Override
	public void process() {

		//Check if npc is in combat..
		if (npc.getCombat().isInCombat()) {

			//Make sure our opponent is valid..
			Player opponent = getOpponent();
			if(opponent == null) {
				return;
			}

			//Are we in distance to the opponent?
			final boolean inDistance = (npc.getPosition().getDistance(opponent.getPosition()) <= getMethod().requiredDistance(npc));

			//Activate prayers..
			PrayerHandler.activatePrayer(npc, PrayerHandler.EAGLE_EYE);
			PrayerHandler.activatePrayer(npc, PrayerHandler.STEEL_SKIN);
			
			//Activate any overheads..
			int overhead = getOverheadPrayer(opponent, inDistance);
			if(overhead != -1) {

				//Activate overhead!
				PrayerHandler.activatePrayer(npc, overhead);

			} else {

				//We shouldn't be using any overhead.
				//Make sure to turn off any headicons.
				if(npc.getHeadIcon() != -1) {
					npc.setHeadIcon(-1);
				}
			}

			//Eat whenever we need to.
			if(npc.getHitpoints() > 0) {
				if(npc.getHitpoints() < 40 + Misc.getRandomInclusive(15)) {
					if(getEatCounter() < 28) {
						super.eat(Edible.SHARK, 1100);
					}
				}

				//Cast vengeance when ever we can.
				super.castVengeance();

				//Sometimes go nuts
				if(Misc.getRandomInclusive(20) == 1) {
					ramboMode = true;
				}				
				if(ramboMode) {

					npc.say("Raaaaaarrrrgggghhhhhh!");
					npc.getCombat().sequenceCombatTurn(true);

					if(ramboShots++ >= 1) {
						ramboShots = 0;
						ramboMode = false;
					}
				}

			} else {
				npc.say("Gg");
			}

		} else {

			//Turn off prayers
			if(npc.getHeadIcon() != -1 || PrayerHandler.isActivated(npc, PrayerHandler.EAGLE_EYE)
					|| PrayerHandler.isActivated(npc, PrayerHandler.STEEL_SKIN)) {
				PrayerHandler.deactivatePrayers(npc);
			}

			//Reset all attributes
			super.reset();
		}
	}

	@Override
	public void onDeath(Player killer) {
		//There should be a small chance of receiving a tier 1 emblem
		if(Misc.getRandomInclusive(15) == 1 && (!killer.getGameMode().isIronman() && !killer.getGameMode().isHardcore() && !killer.getGameMode().isUltimate() && !killer.getGameMode().isSpawn())) {
			ItemOnGroundManager.register(killer, new Item(13307, 7500), npc.getPosition());
			killer.getPacketSender().sendMessage("@red@You have been awarded 7,500 Blood money for successfully killing the bot.");
			PlayerUtil.broadcastMessage("<img=764> @red@ " + PlayerUtil.getImages(killer) + "" + killer.getUsername() +" has been awarded 7,500 Blood money for successfully killing the Edgeville bot.");
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
	public int maxRecoilDamage() {
		return 100;
	}

	@Override
	public RangedAttackStrategy getMethod() {
		return RangedAttackStrategy.INSTANCE;
	}
}