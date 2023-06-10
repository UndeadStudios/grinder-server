package com.grinder.game.content.skill;

import com.grinder.game.World;
import com.grinder.game.content.GameMode;
import com.grinder.game.content.miscellaneous.MysteriousManEvent;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.model.*;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager.Points;
import com.grinder.game.content.points.ParticipationPoints;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Logging;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;

/**
 * Handle player skilling points.
 *
 *
 */
public class SkillingPoints {

	private static int EXPERIENCE_CAP = 500_000;

	private static int totalExperience = 0;

	public static int getExperienceCapForGameMode(GameMode gameMode) {
		switch (gameMode) {
			case IRONMAN:
			case HARDCORE_IRONMAN:
			case ULTIMATE_IRONMAN:
				return 400_000;
			case REALISM:
				return 400_000;
			case CLASSIC:
				return 500_000;
			case SPAWN:
			case NORMAL:
			case ONE_LIFE:
				return EXPERIENCE_CAP;
		}
		return EXPERIENCE_CAP;
	}

	public static void addExperience(Player player, Skill skill, int experience) {
		if (skill == Skill.FISHING) {
			experience *= 0.40;
		} else if (skill == Skill.CRAFTING) {
			experience *= 0.70;
		} else if (skill == Skill.HERBLORE) {
			experience *= 0.50;
		} else if (skill == Skill.FIREMAKING) {
			experience *= 0.50;
		}

		totalExperience += experience;
			if (totalExperience > EXPERIENCE_CAP) {
				int points = 3 + Misc.random(5);
				totalExperience = experience % EXPERIENCE_CAP;
				ParticipationPoints.addPoints(player, 1 + Misc.getRandomInclusive(3), "@dre@from skilling</col>.");
				addSkillingPoints(player, points);
			}
	}

	private static void addSkillingPoints(Player player, int points) {
		// Message
		player.getPacketSender().sendMessage("<img=779> You've gained @dre@" + points + " skilling " + (points > 1 ? "points</col>." : "point</col>."));
		player.getPacketSender().sendMessage("<img=779> Trade skilling point store at Edgeville to exchange your skilling points.");

		// Logging
		if (!player.getGameMode().isSpawn())
		Logging.log("addskillingpoints", ""+ player.getUsername() +" has received " + points +" skilling points.");
		PlayerUtil.broadcastPlayerDeveloperMessage(""+ player.getUsername() +" has received " + points +" skilling points.");

		// Add Points
		if (Misc.random(25) == Misc.random(25)) {
			//points += 100;
			player.getPoints().increase(Points.SKILLING_POINTS, 100);
			player.getPoints().increase(Points.TOTAL_SKILLING_POINTS_RECEIVED, 100);
			player.sendMessage("@red@You have received a lucky bonus reward of extra 100 skilling points!");
		}

		// Add points
		player.getPoints().increase(Points.SKILLING_POINTS, points);
		player.getPoints().increase(Points.TOTAL_SKILLING_POINTS_RECEIVED, points);

		// For anti botting attribute current session skilling points
		player.getAttributes().numAttr(Attribute.SESSION_SKILLING_POINTS, 0).incJ(points);

		// Stop anything below this code if the player is in a minigame
		if (player.getMinigame() != null) {
			return;
		}

		// Trigger anti botting on some circumsances
		if (Misc.random(3) == 1) {
			if (PlayerExtKt.tryRandomEventTrigger(player, 1.5F)) {
			return;
			}
		} else if (Misc.random(10) == 5) {
			player.BLOCK_ALL_BUT_TALKING = true;
			SkillUtil.stopSkillable(player); // Stop if they are skilling so they mess up
			TaskManager.submit(3, () -> player.BLOCK_ALL_BUT_TALKING = false);
			return;
		}

		// Logging
		if (player.getSkillManager().calculateCombatLevel() <= 10) {
			if (player.getAttributes().numInt(Attribute.SESSION_SKILLING_POINTS) % 250 == 0) {
				PlayerUtil.broadcastPlayerMediumStaffMessage(""+ player.getUsername() +" is possibly botting!");
				Logging.log("possiblebotters", ""+ player.getUsername() +" has received " + player.getAttributes().numInt(Attribute.SESSION_SKILLING_POINTS) +" skilling points during his session and his combat level is " + player.getSkillManager().calculateCombatLevel() +".");
			}
		}

		// Extra safety
		if (player.getAttributes().numInt(Attribute.SESSION_SKILLING_POINTS) % 500 == 0) {

			// Warn staff and logging
			PlayerUtil.broadcastPlayerMediumStaffMessage(""+ player.getUsername() +" is possibly botting!");
			Logging.log("possiblebotters", ""+ player.getUsername() +" has received " + player.getAttributes().numInt(Attribute.SESSION_SKILLING_POINTS) +" skilling points during his session.");

			// Weird pre-caution and trigger new anti botting
			if (Misc.random(2) == 1) {
				// Trigger the event
				MysteriousManEvent.INSTANCE.trigger(player);
			}
		}

		//ServerHandler.getHandler().addScore(LiveScoreCategories.SKILLING_POINTS, c.playerName, points);
	}

	public int getSkillingPointsExperienceCapForGameMode(Player player) {
		switch (player.getGameMode()) {
			case REALISM:
				return 185_000;
			case CLASSIC:
				return 290_000;
			case NORMAL:
				return 950_000;
			case PURE:
			case MASTER:
				return 95_000;
			case IRONMAN:
			case HARDCORE_IRONMAN:
				return 650_000;
			case ULTIMATE_IRONMAN:
				return 450_000;

			default:
				return 1_200_000;
		}
	}
}
