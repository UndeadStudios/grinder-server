package com.grinder.game.entity.agent.combat.hit.damage;

import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackContext;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType;
import com.grinder.game.entity.agent.combat.attack.weapon.Weapon;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface;
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Ammunition;
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.AmmunitionEffect;
import com.grinder.game.entity.agent.combat.misc.CombatEquipment;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.boss.impl.vorkath.VorkathBoss;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.item.container.player.Equipment;
import com.grinder.util.Misc;

public class DamageTransform {

    public static int transformPlayerOutgoingHitDamage(final Player actor, final Agent target, final AttackContext context, int damage) {

        final Equipment equipment = actor.getEquipment();

        if (context.isDefilerSetEffectActivated())
            damage = Math.max(1, damage);

        if(context.used(SpecialAttackType.SWEEP))
            damage = Math.min(34, damage);

        if(context.used(SpecialAttackType.DESCENT_OF_DARKNESS_OR_DRAGONS))
            damage = Math.max(8, Math.min(48, damage));

        if(context.used(WeaponInterface.CROSSBOW)){
            final Ammunition ammunition = context.getAmmunitionUsed();
            if(ammunition != null) {
                if(context.hasAmmunitionEffect()){
                    damage = AmmunitionEffect.transformDamage(ammunition, actor, target, damage);
                }
            }
        }

        if(context.used(AttackType.MAGIC)){
            if(EquipmentUtil.hasAnyAmuletOfTheDamned(equipment)){
                if(EquipmentUtil.isWearingAhrimsSet(equipment)){
                    if(Misc.randomChance(25f)){
                        damage += (damage * 0.3D);
                    }
                }
            }
        }

        return damage;
    }

    public static void transformPlayerIncomingHitDamage(Agent actor, Agent target, int damage, Damage queuedDamage) {

        if(actor instanceof Player){
            final Player actorPlayer = (Player) actor;
            if(EntityExtKt.getBoolean(actorPlayer, Attribute.FIRE_TOME_ACTIVATED, false) && !actorPlayer.isSpecialActivated()) // TOME_OF_FIRE
                queuedDamage.incrementDamage((int) (damage * 0.5));
            if(EntityExtKt.getBoolean(actorPlayer, Attribute.WATER_TOME_ACTIVATED, false) && !actorPlayer.isSpecialActivated()) // TOME_OF_WATER
                queuedDamage.incrementDamage((int) (damage * 0.5));
        }

        if (target instanceof Player) {

            final Player targetPlayer = ((Player) target);

            if (EquipmentUtil.isWearingElysianShield(targetPlayer))
                CombatEquipment.handleElysianShield(targetPlayer, queuedDamage);

            if (EquipmentUtil.isWearingDivineShield(targetPlayer))
                CombatEquipment.handleDivineShield(targetPlayer, queuedDamage);

            if (EquipmentUtil.isWearingDivineShield_2(targetPlayer))
                CombatEquipment.handleDivineShield(targetPlayer, queuedDamage);

            if (EquipmentUtil.isWearingJusticiarSet(targetPlayer))
                CombatEquipment.handleJusticiar(actor, targetPlayer, queuedDamage, damage);
        }
    }

    public static int transformNPCIncomingHitDamage(final Agent target, final Weapon weapon, int damage) {
        if (target instanceof NPC) {

            final NPC targetNPC = (NPC) target;
            final NpcDefinition definition = targetNPC.fetchDefinition();
            final String targetName = definition.getName().toLowerCase();

            if (weapon.uses(WeaponInterface.DRAGON_LANCE)) {

                if (targetNPC instanceof VorkathBoss || targetName.toLowerCase().contains("dragon") || targetName.toLowerCase().contains("wyvern") || targetName.toLowerCase().contains("olm"))
                    damage *= 1.25;

            }
        }
        return damage;
    }

}
