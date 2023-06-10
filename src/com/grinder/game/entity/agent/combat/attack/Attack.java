package com.grinder.game.entity.agent.combat.attack;

import com.grinder.game.content.dueling.DuelRule;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.hit.Hit;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.MonsterRace;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.model.sound.AreaSound;
import com.grinder.game.model.sound.Sound;
import org.jetbrains.annotations.NotNull;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-10
 */
public abstract class Attack<T extends Agent> implements AttackStrategy<T>{

    private final AttackProvider provider;

    public Attack(AttackProvider provider) {
        this.provider = provider;
    }

    private void playSound(Agent target, Sound sound, Player player) {
        if(sound instanceof AreaSound)
            player.getPacketSender().sendAreaPlayerSound((AreaSound) sound);
        else if(target instanceof Player){
            player.getPacketSender().sendAreaPlayerSound(sound.getId(), sound.getDelay());
        } else
            player.getPacketSender().sendSound(sound);
    }

    @Override
    public final void animate(T actor) {
        final AttackType type = type();
        actor.performAnimation(provider.getAttackAnimation(type));

        provider.fetchTextAboveHead(type).ifPresent(actor::say);
        provider.fetchAttackGraphic(type).ifPresent(actor::performGraphic);
    }

    @Override
    public void sequence(@NotNull T actor, @NotNull Agent target) {

        provider.fetchAttackSound(type())
                .ifPresent(sound -> {
                    if(actor instanceof Player)
                        playSound(target, sound, (Player) actor);
                    else if(target instanceof Player)
                        playSound(actor, sound, (Player) target);
                });

        provider.fetchProjectiles(type())
                .map(template -> new Projectile(actor, target, template))
                .forEach(Projectile::sendProjectile);

    }

    @Override
    public void postHitAction(@NotNull T actor, @NotNull Agent target) { }

    @Override
    public void postHitEffect(@NotNull Hit hit) { }

    @Override
    public void postIncomingHitEffect(@NotNull Hit hit) { }

    @Override
    public int duration(@NotNull T actor) {
        return provider.fetchAttackDuration(type());
    }

    @Override
    public boolean canAttack(T actor, @NotNull Agent target) {
        if (actor.isAlive() && target.isAlive() && target.isActive()) {
            if (type() == AttackType.MELEE) {
                if (actor instanceof Player) {
                    final Player player = (Player) actor;
                    if (player.getDueling().inDuel() && player.getDueling().selectedRule(DuelRule.NO_MELEE)) {
                        DialogueManager.sendStatement(player, "Melee has been disabled in this duel!");
                        player.getCombat().reset(false);
                        return false;
                    }
                }
                if (target instanceof NPC) {
                    if(MonsterRace.Companion.isRace(target, MonsterRace.AVIANSIE)){
                        actor.messageIfPlayer("The Aviansie is flying too high for you to attack using melee.");
                        actor.getCombat().reset(false);
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @NotNull
    @Override
    public Hit[] createHits(@NotNull T actor, @NotNull Agent target) {
        return provider.fetchHits(type())
                .map(template -> new Hit(actor, target, this, template))
                .toArray(Hit[]::new);
    }
}
