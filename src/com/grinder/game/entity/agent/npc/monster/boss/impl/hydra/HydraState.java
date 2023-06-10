package com.grinder.game.entity.agent.npc.monster.boss.impl.hydra;

import com.grinder.game.model.Animation;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

public enum HydraState {

	POISON(8616, 8237, 3, new Animation(8235), new Animation(8235), new Animation(8236)) {
		@Override
		public void renderDeath(AlchemicalHydraBoss hydra) {
			hydra.performAnimation(new Animation(getAnimation(), 2));
			hydra.transformDurationInGameTicks = 4;
			hydra.setNpcTransformationId(getTransformId());
		}
	},
	
	LIGHTNING(8619, 8244, 3, new Animation(8241), new Animation(8242), new Animation(8243)) {
		@Override
		public void renderDeath(AlchemicalHydraBoss hydra) {
			hydra.performAnimation(new Animation(getAnimation(), 2));
			hydra.transformDurationInGameTicks = 3;
			hydra.setNpcTransformationId(8617);
		}
	},

	FLAME(8620, 8251, 3, new Animation(8248), new Animation(8249), new Animation(8250)) {
		@Override
		public void renderDeath(AlchemicalHydraBoss hydra) {
			hydra.setNpcTransformationId(8618);
			hydra.performAnimation(new Animation(getAnimation(), 2));
			hydra.transformDurationInGameTicks = 4;
		}
	},
	
	ENRAGED(8621, 8262, 1, new Animation(8255), new Animation(8256)) {
		@Override
		public void renderDeath(AlchemicalHydraBoss hydra) {
			hydra.performAnimation(new Animation(8257));
			TaskManager.submit(new Task(3) {
				@Override
				public void execute() {
					stop();
					hydra.transformDurationInGameTicks = 0;
					hydra.setNpcTransformationId(8622);
					hydra.performAnimation(new Animation(8258, 1));
				}
			});
		}
	};

	public static boolean isTransformAnim(int id) { return id == 8237 || id == 8244 || id == 8251 || id == 8257 || id == 8258 || id == 8262; }

	private final int transformId, animation, attackCount;
	
	private final Animation[] attackAnimations;

	HydraState(int transformId, int animation, int attackCount, Animation... attackAnimations) {
		this.transformId = transformId;
		this.animation = animation;
		this.attackAnimations = attackAnimations;
		this.attackCount = attackCount;
	}


	abstract void renderDeath(AlchemicalHydraBoss hydra);

	public Animation[] getAttackAnimations() {
		return attackAnimations;
	}

	public int getTransformId() {
		return transformId;
	}

	public int getAnimation() {
		return animation;
	}

	public int getAttackCount() {
		return attackCount;
	}

}
