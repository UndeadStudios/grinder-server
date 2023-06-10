package com.grinder.game.entity.agent.npc.monster.boss.impl.galvek;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.grinder.game.model.Animation;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.NpcID;

import java.util.EnumSet;

/**
 * Enum to keep track of the current phase. Can set phase specific variables like
 * the number of attacks before switching attacks, and unique phase transitions
 *
 * @author Pea2nuts
 */
public enum GalvekPhase {
// TODO: Change boss position when phase changes.
//  Ideally galvek should move to a different spot during his flying phases

	// PHASE I
	FIRE(NpcID.GALVEK_AIR, 3) {
		@Override
		public void onPhaseDeath(GalvekBoss npc) {
			transition(npc, GalvekConstants.sittingFlyUp, GalvekConstants.floatingLand, 3);// sitting to flying
		}
	},

	//Phase II
	AIR(NpcID.GALVEK_WATER, 3) {
		@Override
		public void onPhaseDeath(GalvekBoss npc) {
			transition(npc, GalvekConstants.floatingFlyUp, GalvekConstants.floatingLand, 2);// flying to flying
		}
	},

	// Phase III
	WATER(NpcID.GALVEK_EARTH, 3) {
		@Override
		public void onPhaseDeath(GalvekBoss npc) {
			transition(npc, GalvekConstants.floatingFlyUp, GalvekConstants.sittingLand, 2);// flying to sitting

		}
	},


	// Phase IV
	EARTH(NpcID.GALVEK_EARTH_DEAD,3) {

		/**
		 * All we need to do is set the transformation ID onDeath
		 * and make sure the death time is set high in the npc defs.
		 * No need for fancy phase transition code for the death.
		 **/
		@Override
		public void onPhaseDeath(GalvekBoss npc) {}
	};

	public void transition(GalvekBoss npc, Animation firstAnim, Animation secondAnim, int initialDelay) {
		npc.transformTotalDurationInGameTicks = 11;
		npc.performAnimation(firstAnim);
		npc.setRegistered(false);// Setting this forces the player to stop attacking during the phase change

		// the floating anim needs 1 less tick delay
		TaskManager.submit(new Task(initialDelay) {

			int count = 0;
			@Override
			public void execute() {

				if(count == 0) {
					npc.setVisible(false);
				}


				// transform while invisible and unregistered
				if(count == 1) {
					npc.setNpcTransformationId(transformId);
				}


				if(count == 3) {
					npc.setVisible(true);
					// Land in the air sitting phase
					npc.performAnimation(secondAnim);
				}

				if(count == 4) {
					npc.transformTotalDurationInGameTicks = 0;// end attack delay
					npc.setRegistered(true);// Make galvek attackable again
					stop();
				}

				count++;
			}
		});
	}

	public static final ImmutableSet<GalvekPhase> VALUES = Sets.immutableEnumSet(EnumSet.allOf(GalvekPhase.class));


	public static boolean isTransformAnim(int animId) {
		return animId == GalvekConstants.sittingFlyUp.getId() || animId == GalvekConstants.sittingLand.getId() || animId == GalvekConstants.floatingFlyUp.getId() || animId == GalvekConstants.floatingLand.getId();
	}
	public static boolean isTransformNpc(int npcId) {
		return npcId == NpcID.GALVEK_FIRE || npcId == NpcID.GALVEK_WATER || npcId == NpcID.GALVEK_AIR || npcId == NpcID.GALVEK_EARTH || npcId == NpcID.GALVEK_FIRE_2 || npcId == NpcID.GALVEK_EARTH_2 || npcId == NpcID.GALVEK_EARTH_DEAD;
	}
	private final int transformId, attackCount;


	GalvekPhase(int transformId, int attackCount) {
		this.transformId = transformId;
		this.attackCount = attackCount;
	}


	abstract void onPhaseDeath(GalvekBoss galvek);

	public int getTransformId() {
		return transformId;
	}

	public int getAttackCount() {
		return attackCount;
	}

}
