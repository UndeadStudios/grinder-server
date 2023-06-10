package com.grinder.game.content.minigame.fightcave.monsters;

import com.grinder.game.content.minigame.fightcave.FightCaveNpc;
import com.grinder.game.entity.agent.combat.attack.AttackProvider;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.event.CombatEvent;
import com.grinder.game.entity.agent.combat.event.impl.IncomingHitApplied;
import com.grinder.game.entity.agent.combat.hit.Hit;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.model.Position;

/**
 * @author L E G E N D
 */
public final class TzKek extends FightCaveNpc implements AttackProvider {

    public TzKek(int npcId, Position position) {
        super(npcId, position);
        getCombat().subscribe(this::onCombatEvent);
    }

    private boolean onCombatEvent(CombatEvent event) {
        if (!(event instanceof IncomingHitApplied)) {
            return false;
        }
        Hit hit = ((IncomingHitApplied) event).getHit();
        if (hit.getAttackType() == AttackType.MELEE)
            getOwner().getCombat().queue(Damage.create(1));
        return true;
    }
}
