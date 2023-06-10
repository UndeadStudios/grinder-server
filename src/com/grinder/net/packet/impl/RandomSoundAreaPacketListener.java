package com.grinder.net.packet.impl;

import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.AgentUtil;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Boundaries;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.areas.impl.DuelArenaArea;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.util.Misc;
import com.grinder.util.timing.TimerKey;

public class RandomSoundAreaPacketListener implements PacketListener {

/**
 * A packet to be called every 45 seconds to play a random ambient sound
 * Only green area's are covered. It doesn't get called if your Busy/Jailed/InCombat..etc
 */
    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {


			if (AreaManager.BANK_AREAS.contains(player) && Misc.getRandomInclusive(5) == 1) {
				TaskManager.submit(new Task(60) {
					@Override
					public void execute() {
						stop();
						player.getPacketSender().sendSound(Misc.randomInt(Sounds.SOMETHING_IN_GRAND_EXCHANGE));
					}
				});
			} else {
				if (Misc.getRandomInclusive(1) == 1 && AreaManager.inside(player.getPosition(), Boundaries.GREEN_TREE_AREAS)) {
					TaskManager.submit(new Task(60) {
						@Override
						public void execute() {
							stop();
							player.getPacketSender().sendSound(Misc.randomInt(Sounds.WALKING_BETWEEN_TREES));
						}
					});
				}
			}
/*    	if (player.getWildernessLevel() > 0) {
			return;
		} else */if (player.BLOCK_ALL_BUT_TALKING) {
			return;
		} else if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false)) {
			return;
		} else if (player.isDying() || AgentUtil.isFrozen(player) || player.isJailed()
				|| player.isInTutorial() || player.isPoisoned()) {
			return;
		} else if (player.getCombat().isInCombat() || player.getTimerRepository().has(TimerKey.COMBAT_COOLDOWN)) {
			return;
		} else if (player.getCombat().isBeingAttacked()) {
			return;
		}
    	if (AreaManager.inside(player.getPosition(), AreaManager.DUEL_ARENA)) {
    		return;
    	}
		if (player.getPosition().getZ() != 0) {
			return;
		}
		if (player.getPosition().getY() >= 4143) {
			return;
		}
		if (player.getMinigame() != null) {
			return;
		}
    	if (Misc.getRandomInclusive(4) == 1) {
    		return;
    	}
    	int randomSound = 0;
    	randomSound = Misc.getRandomInclusive(20);
    	//System.out.println(randomSound);
    	if (randomSound == 0 || randomSound == 1) {
    		player.getPacketSender().sendSound(2015);
    	} else if(randomSound == 1) {
    		player.getPacketSender().sendSound(1997);
    	} else if(randomSound == 2) {
    		player.getPacketSender().sendSound(3047);
    	} else if(randomSound == 3) {
    		player.getPacketSender().sendSound(2018);
    	} else if(randomSound == 4) {
    		player.getPacketSender().sendSound(2019);
    	} else if(randomSound == 5) {
    		player.getPacketSender().sendSound(1986);
    	} else if(randomSound == 6) {
    		player.getPacketSender().sendSound(1987);
    	} else if(randomSound == 7 || randomSound == 8) {
    		player.getPacketSender().sendSound(3047);
    	} else if(randomSound == 9) {
    		player.getPacketSender().sendSound(2014);
    	} else if(randomSound == 10) {
    		player.getPacketSender().sendSound(2013);
    	} else if(randomSound == 11) {
    		player.getPacketSender().sendSound(2012);
    	} else if(randomSound == 12) {
    		player.getPacketSender().sendSound(2013);
    	} else if(randomSound == 13) {
    		player.getPacketSender().sendSound(2002);
    	} else if(randomSound == 14) {
    		player.getPacketSender().sendSound(2003);
    	} else if(randomSound == 15) {
    		player.getPacketSender().sendSound(2004);
    	} else if(randomSound == 16) {
    		player.getPacketSender().sendSound(2005);
    	} else if(randomSound == 17) {
    		player.getPacketSender().sendSound(2002);
    	} else if(randomSound == 18) {
    		player.getPacketSender().sendSound(1911);
    	} else if(randomSound == 19) {
    		player.getPacketSender().sendSound(1988);
    	} else if(randomSound == 20) {
    		player.getPacketSender().sendSound(1984);
    }
    }
}
