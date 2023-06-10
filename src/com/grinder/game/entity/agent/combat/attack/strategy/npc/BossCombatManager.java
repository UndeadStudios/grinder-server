package com.grinder.game.entity.agent.combat.attack.strategy.npc;

import com.grinder.game.entity.agent.AgentUtil;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.boss.Boss;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;

/**
 * Handles combat for bosses
 * 
 * @author 2012
 *
 * @deprecated use the new boss combat system instead, see {@link Boss}.
 */
@Deprecated
public class BossCombatManager {

	public enum EffectType {
		FREEZE,
		POISON,
		TELEPORT,
		STUN,
		DISARM,
		TELEBLOCK,
	}

	public enum AttackProximity {
		MULTI,
		SINGLE,
	}

	public enum AttackStyle {
		MELEE,
		RANGED,
		MAGIC,
		MELEE_RANGE_MIX,
		MELEE_MAGIC_MIX,
		RANGE_MAGIC_MIX,
		ANY
	}

	public static void sendMultiGraphic(final NPC actor, final Graphic graphic, final Position position) {

		AgentUtil.getPlayersInProximity(actor, 30, true)
				.forEach(player -> player.getPacketSender().sendGraphic(graphic, position == null ? player.getPosition() : position));

	}
}
