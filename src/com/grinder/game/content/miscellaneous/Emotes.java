package com.grinder.game.content.miscellaneous;

import com.grinder.game.content.item.MorphItems;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Skill;
import com.grinder.game.model.Skillcape;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.timing.TimerKey;

import java.util.HashMap;
import java.util.Map;

public class Emotes {
	
	/**
	 * The max cape animation
	 */
	public static final Animation MAX_CAPE_ANIM = new Animation(7121);

	/**
	 * The max cape graphic
	 */
	public static final Graphic MAX_CAPE_GRAPHIC = new Graphic(1286);

	public static boolean doEmote(Player player, int button) {

		if (player.getTimerRepository().has(TimerKey.BUTTON_DELAY))
			return false;

		player.getTimerRepository().replaceIfLongerOrRegister(TimerKey.BUTTON_DELAY, 2);
		EmoteData data = EmoteData.forId(button);

		if (data != null) {

			player.getClueScrollManager().onEmote(data);

			animation(player, data.animation, data.graphic);

			return true;
		}

		// Skill cape button
		if (button == 19052) {
			Skillcape cape = Skillcape.forId(player.getEquipment().getItems()[EquipmentConstants.CAPE_SLOT].getId());
			if (cape == null) {
				player.getPacketSender().sendMessage("You must be wearing a skill cape to perform this emote.", 1000);
			}
			if (cape != null) {

				if (cape != Skillcape.QUEST_POINT) {
					if (cape.ordinal() < Skill.values().length) {

						// Check if player is maxed in skill
						Skill skill = Skill.values()[cape.ordinal()];
						int level = SkillUtil.maximumAchievableLevel();
						if (player.getSkillManager().getMaxLevel(skill) < level) {
							player.getPacketSender()
									.sendMessage("You need " + Misc.anOrA(skill.getName()) + " "
											+ Misc.formatPlayerName(skill.getName().toLowerCase())
											+ " level of at least " + level + " to do this emote.");
							return false;
						}

					} else {

					}
				}
				EntityExtKt.setBoolean(player, Attribute.STALL_HITS, true, false);
				animation(player, cape.getAnimation(), cape.getGraphic());
				player.getPoints().increase(AttributeManager.Points.EMOTES_PLAYED, 1); // Increase points
				player.BLOCK_ALL_BUT_TALKING = true;
				player.getMotion().update(MovementStatus.DISABLED).clearSteps();
				EntityExtKt.setBoolean(player, Attribute.STALL_HITS, true, false);
				TaskManager.submit((cape.getDelay()),  () -> {
					player.BLOCK_ALL_BUT_TALKING = false;
					player.getMotion().update(MovementStatus.NONE).clearSteps();
					EntityExtKt.setBoolean(player, Attribute.STALL_HITS, false, false);
				});
			}
			return true;
		}

		return false;
	}

	private static void animation(Player player, Animation anim, Graphic graphic) {

		if (player.BLOCK_ALL_BUT_TALKING) {
			return;
		}
		if (player.getEquipment().contains(4084)) {
			player.sendMessage("You can't use this emote while on sled.");
			return;
		}
		if (player.isBlockingDisconnect()) {
			return;
		}
		if (player.getStatus().equals(PlayerStatus.TRADING)) {
			return;
		}
		if (player.getStatus().equals(PlayerStatus.SHOPPING)) {
			return;
		}
		if (player.getStatus().equals(PlayerStatus.DICING)) {
			return;
		}
		if (player.getStatus().equals(PlayerStatus.BANKING)) {
			return;
		}
		/*if (player.getCombat().isInCombat()) {
			player.getPacketSender().sendMessage("You can't use this emote right now.", 1000);
			return;
		}*/

		if (!MorphItems.INSTANCE.notTransformed(player, "do", true, false))
			return;

		if (player.isInTutorial()) {
			player.getPacketSender().sendMessage("You can't use this emote right now.", 1000);
			return;
		}
		// Stop skilling..
		SkillUtil.stopSkillable(player);

		PlayerExtKt.resetInteractions(player, true, true);

		if (anim != null)
			player.performAnimation(anim);
		if (graphic != null)
			player.performGraphic(graphic);

		player.getPoints().increase(AttributeManager.Points.EMOTES_PLAYED, 1); // Increase points
	}

	public enum EmoteData {
		YES(168, new Animation(855), null),
		NO(169, new Animation(856), null),
		BOW(164, new Animation(858), null),
		ANGRY(165, new Animation(859), null),
		THINK(162, new Animation(857), null),
		WAVE(163, new Animation(863), null),
		SHRUG(13370, new Animation(2113), null),
		CHEER(171, new Animation(862), null),
		BECKON(167, new Animation(864), null),
		LAUGH(170, new Animation(861), null),
		JUMP_FOR_JOY(13366, new Animation(2109), null),
		YAWN(13368, new Animation(2111), null),
		DANCE(166, new Animation(866), null),
		JIG(13363, new Animation(2106), null),
		SPIN(13364, new Animation(2107), null),
		HEADBANG(13365, new Animation(2108), null),
		CRY(161, new Animation(860), null), KISS(11100,
																		new Animation(1374),
																		new Graphic(574, 25)), PANIC(13362,
																				new Animation(2105), null), RASPBERRY(
																						13367, new Animation(2110),
																						null), CRAP(172,
																								new Animation(865),
																								null), SALUTE(13369,
																										new Animation(
																												2112),
																										null), GOBLIN_BOW(
																												13383,
																												new Animation(
																														2127),
																												null), GOBLIN_SALUTE(
																														13384,
																														new Animation(
																																2128),
																														null), GLASS_BOX(
																																667,
																																new Animation(
																																		1131),
																																null), CLIMB_ROPE(
																																		6503,
																																		new Animation(
																																				1130),
																																		null), LEAN(
																																				6506,
																																				new Animation(
																																						1129),
																																				null), GLASS_WALL(
																																						666,
																																						new Animation(
																																								1128),
																																						null), ZOMBIE_WALK(
																																								18464,
																																								new Animation(
																																										3544),
																																								null), ZOMBIE_DANCE(
																																										18465,
																																										new Animation(
																																												3543),
																																										null), SCARED(
																																												15166,
																																												new Animation(
																																														2836),
																																												null), RABBIT_HOP(
																																														18686,
																																														new Animation(
																																																6111),
																																														null),
		IDEA(22588, new Animation(4276), new Graphic(712)),
		STOMP(22589, new Animation(4278), new Graphic(713)),
		FLAP(22590, new Animation(4280), null),
		SLAP_HEAD(22591, new Animation(4275), null),
		ZOMBIE_HAND(22593, new Animation(2840), null),

		/*
		 * ZOMBIE_HAND(15166, new Animation(7272), new Graphic(1244)),
		 * SAFETY_FIRST(6540, new Animation(8770), new Graphic(1553)),
		 * AIR_GUITAR(11101, new Animation(2414), new Graphic(1537)),
		 * SNOWMAN_DANCE(11102, new Animation(7531), null), FREEZE(11103, new
		 * Animation(11044), new Graphic(1973))
		 */;

		private static Map<Integer, EmoteData> emotes = new HashMap<Integer, EmoteData>();

		static {
			for (EmoteData t : EmoteData.values()) {
				emotes.put(t.button, t);
			}
		}

		public Animation animation;
		public Graphic graphic;
		private int button;

		EmoteData(int button, Animation animation, Graphic graphic) {
			this.button = button;
			this.animation = animation;
			this.graphic = graphic;
		}

		public static EmoteData forId(int button) {
			return emotes.get(button);
		}

		public int getButton() {
			return button;
		}

		public static void main(String[] args){

		    for(EmoteData data : EmoteData.values()){
                //System.out.println(""+data.ordinal()+" -> "+data.animation.getId());
            }
        }
	}
}
