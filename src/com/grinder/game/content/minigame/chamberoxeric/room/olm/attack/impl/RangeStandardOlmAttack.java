package com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.impl;

import com.grinder.game.content.minigame.chamberoxeric.room.olm.OlmConfiguration;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.OlmAttack;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.model.projectile.ProjectileTemplate;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class RangeStandardOlmAttack implements OlmAttack {
    @Override
    public void execute(NPC npc, Player target) {

        final ProjectileTemplate temp = ProjectileTemplate.builder(OlmConfiguration.GREEN_CRYSTAL_FLYING).setStartHeight(43)
                .setEndHeight(21)
                .setSpeed(55)
                .setDelay(50).build();

        final Projectile p = new Projectile(npc, target, temp);

        p.sendProjectile();

        p.onArrival(() ->
        {
            int damage = 30;
            if(PrayerHandler.hasProtectionPrayer(target, AttackType.RANGED)) {
                damage *= 0.80;
            }
            target.getCombat().queue(Damage.create(0, damage));
        });
    }
}
