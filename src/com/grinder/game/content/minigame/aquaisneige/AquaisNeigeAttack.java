package com.grinder.game.content.minigame.aquaisneige;

import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackProvider;
import com.grinder.game.entity.agent.combat.hit.Hit;
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack;
import org.jetbrains.annotations.NotNull;


public class AquaisNeigeAttack extends BossAttack {
    public AquaisNeigeAttack(@NotNull AttackProvider provider) {
        super(provider);
    }

    @Override
    public void postHitEffect(@NotNull Hit hit) {
        var player = hit.getTarget();
        var prayer = PrayerHandler.getProtectingPrayer(hit.getAttackType());
        if(player.hasActivePrayer(prayer))
            hit.setTotalDamage(0);

        super.postHitEffect(hit);
    }

    @Override
    public int requiredDistance(@NotNull Agent actor) {
        switch (type()) {
            case MELEE:
                return 1;
            case MAGIC:
            case RANGED:
                return 15;
            default:
                return -1;//shouldn't occur
        }

    }

}
