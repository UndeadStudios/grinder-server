package com.grinder.game.entity.agent.combat;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.dueling.DuelState;
import com.grinder.game.content.item.charging.impl.BloodFuryAmulet;
import com.grinder.game.content.item.jewerly.RingOfRecoil;
import com.grinder.game.content.item.charging.impl.RingOfSuffering;
import com.grinder.game.content.item.charging.impl.RingOfSufferingI;
import com.grinder.game.content.item.degrading.DegradingType;
import com.grinder.game.content.minigame.castlewars.CastleWars;
import com.grinder.game.content.pvm.MonsterKillTracker;
import com.grinder.game.content.pvm.contract.MonsterHunting;
import com.grinder.game.content.pvp.bountyhunter.BountyHuntController;
import com.grinder.game.content.skill.skillable.impl.magic.Spell;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.AgentExtKt;
import com.grinder.game.entity.agent.combat.attack.AttackContext;
import com.grinder.game.entity.agent.combat.attack.AttackStrategy;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType;
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy;
import com.grinder.game.entity.agent.combat.attack.strategy.MeleeAttackStrategy;
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponFightType;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces;
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpellType;
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Ammunition;
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeapon;
import com.grinder.game.entity.agent.combat.event.CombatState;
import com.grinder.game.entity.agent.combat.event.impl.ActorAlreadyUnderAttack;
import com.grinder.game.entity.agent.combat.event.impl.IncomingHitQueued;
import com.grinder.game.entity.agent.combat.event.impl.TargetAlreadyUnderAttack;
import com.grinder.game.entity.agent.combat.event.impl.TargetIsImmuneToAttacks;
import com.grinder.game.entity.agent.combat.hit.Hit;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.entity.agent.combat.hit.damage.DamageTransform;
import com.grinder.game.entity.agent.combat.misc.CombatEquipment;
import com.grinder.game.entity.agent.combat.misc.CombatPrayer;
import com.grinder.game.entity.agent.movement.NPCMovementCoordinator.GoalState;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.boss.Boss;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.game.model.Skill;
import com.grinder.game.model.SkullType;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.player.Equipment;
import com.grinder.net.packet.PacketSender;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;
import com.grinder.util.oldgrinder.EquipSlot;
import com.grinder.util.time.SecondsTimer;
import com.grinder.util.timing.TimerKey;
import kotlin.Pair;

import java.util.Arrays;
import java.util.Optional;

import static com.grinder.game.content.skill.skillable.impl.slayer.SlayerEquipment.usingBroadAmmo;
import static com.grinder.game.content.skill.skillable.impl.slayer.SlayerEquipment.usingBroadWeapon;
import static com.grinder.game.entity.agent.player.equipment.EquipmentConstants.AMULET_SLOT;
import static com.grinder.game.entity.agent.player.equipment.EquipmentConstants.WEAPON_SLOT;
import static com.grinder.util.NpcID.*;

/**
 * This class forms a {@link Combat} implementation for {@link Player} typed entities.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-03-21
 */
public class PlayerCombat extends Combat<Player> {

    private static final int MAX_SECONDS_TILL_ATTACK_COOLDOWN = 9;

    private final BountyHuntController bountyHuntController = new BountyHuntController();

    private final SecondsTimer fireImmunityTimer = new SecondsTimer();
    private final SecondsTimer superFireImmunityTimer = new SecondsTimer();
    private final SecondsTimer teleportBlockTimer = new SecondsTimer();
    private final SecondsTimer prayerBlockTimer = new SecondsTimer();
    private final SecondsTimer protectPrayerBlockTimer = new SecondsTimer();
    private final SecondsTimer spellBlockTimer = new SecondsTimer();

    private CombatSpellType castSpellType;

    public PlayerCombat(Player actor) {
        super(actor);
        subscribe(event -> {

            if(event == CombatState.LOCKED_TARGET){

                if(target instanceof NPC)
                    MonsterHunting.INSTANCE.onNpcFight(actor,  (NPC) target);

            } else if(event == CombatState.SEQUENCED_COMBAT){

                if (actor.isSkulled() && actor.getAndDecrementSkullTimer() == 1)
                    actor.updateAppearance();

            } else if(event == CombatState.STARTING_ATTACK) {

                if (target instanceof NPC && !actor.isInTutorial())
                    handleMonsterKillTracker(actor);

            } else if(event == CombatState.FINISHED_ATTACK){

                actor.getItemDegradationManager().degrade(DegradingType.ATTACK, -1);

            } else if(event instanceof TargetAlreadyUnderAttack){
                if (target != null && target instanceof NPC) { // Handle combat dummies
                    if (target.getAsNpc().getId() == NpcID.COMBAT_DUMMY || target.getAsNpc().getId() == NpcID.UNDEAD_COMBAT_DUMMY) {
                        return true;
                    }
                }
                final TargetAlreadyUnderAttack alreadyUnderAttack = (TargetAlreadyUnderAttack) event;
                final Agent failedTarget = alreadyUnderAttack.getFailedTarget();
                final PacketSender packetSender = actor.getPacketSender();

                actor.setPositionToFace(failedTarget.getPosition());
                actor.getMotion().clearSteps();

                if (failedTarget instanceof NPC)
                    packetSender.sendMessage("Someone else is fighting that.");
                else
                    packetSender.sendMessage("That player is already under attack!", 500);

            } else if(event instanceof ActorAlreadyUnderAttack){

                final ActorAlreadyUnderAttack alreadyUnderAttack = (ActorAlreadyUnderAttack) event;
                final Agent failedTarget = alreadyUnderAttack.getFailedTarget();
                final PacketSender packetSender = actor.getPacketSender();

                actor.setPositionToFace(failedTarget.getPosition());
                actor.getMotion().clearSteps();

                packetSender.sendMessage("I'm already under attack!", 500);

            } else if(event instanceof TargetIsImmuneToAttacks){

                actor.sendMessage("This npc is currently immune to attacks.");

            } else if(event instanceof IncomingHitQueued){

                final IncomingHitQueued incomingHitQueued = (IncomingHitQueued) event;
                final Hit hit = incomingHitQueued.getHit();
                final Agent attacker = hit.getAttacker();

            }
            return false;
        });
    }

    @Override
    public void queueOutgoingHit(Hit outgoingHit) {

        final Agent targetBeingHit = outgoingHit.getTarget();

        if (targetBeingHit == null || !targetBeingHit.isAlive())
            return;

        super.queueOutgoingHit(outgoingHit);

        if (!outgoingHit.isRetaliatedHit() && targetBeingHit instanceof Player)
            skullByAttacking((Player) targetBeingHit);

        if(targetBeingHit.isNpc()) {
            NPC npc = (NPC) targetBeingHit;

            if(Arrays.stream(new int[]{KURASK, KURASK_410, KURASK_411, KING_KURASK, TUROTH, TUROTH_427, TUROTH_428, TUROTH_429, TUROTH_430, TUROTH_431, TUROTH_432, 10397}).anyMatch(id -> id == npc.getId())) {
                switch (outgoingHit.getAttackType()) {
                    case MELEE:
                        if (!usingBroadWeapon(actor.getEquipment())) {
                            outgoingHit.setTotalDamage(0);
                            outgoingHit.setRewardingExperience(false);
                        }
                        break;
                    case RANGED:
                        if (!usingBroadAmmo(actor.getEquipment())) {
                            outgoingHit.setTotalDamage(0);
                            outgoingHit.setRewardingExperience(false);
                        }
                        break;
                    case MAGIC:
                        if (actor.getCombat().getPreviousCast() != CombatSpellType.MAGIC_DART.getSpell()) {
                            outgoingHit.setTotalDamage(0);
                            outgoingHit.setRewardingExperience(false);
                        }
                        break;
                }
            }

            if (targetBeingHit instanceof Boss) {
                final AttackType type = outgoingHit.getAttackType();
                if (type != null) {
                    boolean negateAllIncomingDamage = ((Boss) targetBeingHit).negateAllIncomingDamage(outgoingHit.getAttackDetails());
                    if (negateAllIncomingDamage) {
                        outgoingHit.setTotalDamage(0);
                        outgoingHit.setRewardingExperience(false);
                    }
                }
            }
        }

        if(outgoingHit.isRewardingExperience())
            rewardHitExperience(actor, outgoingHit);
    }

    @Override
    public boolean canBeAttackedBy(Agent attacker, boolean ignoreActions) {
        if (super.canBeAttackedBy(attacker, ignoreActions)) {

            if (attacker instanceof NPC) {

                final NPC attackerNPC = ((NPC) attacker);
                final Player owner = attackerNPC.getOwner();

                return owner == null || actor == owner;
            }
            return true;
        }
        return false;
    }

    /**
     * Check whether the {@link #actor} can attack the provided target {@link Agent}.
     *
     * @param target the {@link Agent} that is targeted
     *
     * @return {@code true} if the actor can attack the target
     *          {@code false} if not
     */
    @Override
    public boolean canAttack(Agent target) {
        if (actor.getTimerRepository().has(TimerKey.STUN)) {
            actor.sendMessage("You're currently stunned and can't attack.");
            return false;
        }

        final AttackStrategy<? extends Agent> strategy = determineStrategy();

        final SpecialAttackType special = actor.getSpecialAttackType();
        final boolean specialActivated = actor.isSpecialActivated();
        final boolean performSpecial = specialActivated && special != null;

        if (performSpecial && !canPerformSpecialAttack(special))
            return false;

        if (target instanceof NPC && !canAttackNPC((NPC) target))
            return false;
        else if (target instanceof Player && !canAttackPlayer((Player) target))
            return false;

        return canAttackWith(target, strategy, false);
    }

    @Override
    public AttackStrategy<Agent> determineStrategy() {

        setRangedWeapon(RangedWeapon.getFor(actor));
        setAmmunition(Ammunition.getFor(actor));


        final SpecialAttackType special = actor.getSpecialAttackType();
        if (actor.isSpecialActivated())
            return special.getStrategy();

        if (getCastSpell() != null || getAutocastSpell() != null)
            return MagicAttackStrategy.INSTANCE;



        if (getRangedWeapon() != null)
            return RangedAttackStrategy.INSTANCE;

        return MeleeAttackStrategy.INSTANCE;
    }

    @Override
    public boolean isInReachForAttack(Agent target, boolean triggerActions) {

        final boolean isCloseQuarterCombat = attackStrategy.type() == AttackType.MELEE;

        if (isCloseQuarterCombat)
            return target.getCombat().canBeReachedInCloseQuarter(actor, requiredAttackDistance(), triggerActions);

        if(target.getCombat().canBeReachedInLongRange(actor, requiredAttackDistance(), triggerActions))
            return true;

        return false;
    }

    @Override
    public Damage modifyHitDamage(AttackContext context, Agent target, int baseDamage) {

        baseDamage = DamageTransform.transformPlayerOutgoingHitDamage(actor, target, context, baseDamage);
        baseDamage = DamageTransform.transformNPCIncomingHitDamage(target, getWeapon(), baseDamage);

        final Damage queuedDamage = new Damage(baseDamage, DamageMask.REGULAR_HIT);

        return transformOutgoingDamage(target, context, baseDamage, queuedDamage);
    }

    @Override
    public void setFightType(WeaponFightType fightType) {

        if (actor.isLoggedIn) {
            
            if(fightType.name().startsWith("UNARMED")){
                super.setFightType(fightType);
                return;
            }

            Equipment equipment = actor.getEquipment();

            Item weapon = equipment.getItems()[WEAPON_SLOT];

            ItemDefinition definition = weapon.getDefinition();

            Optional.ofNullable(definition).map(ItemDefinition::getWeaponInterface).ifPresent(weaponInterface -> {
                final WeaponFightType[] weaponTypes = weaponInterface.getFightType();
                if(!weapon.isValid() || (weaponTypes != null && Arrays.asList(weaponTypes).contains(fightType))) {
                    super.setFightType(fightType);
                    WeaponInterfaces.INSTANCE.getFightTypeMap(actor)
                            .put(definition.getId(), fightType);
                }
            });
        } else super.setFightType(fightType);
    }

    @Override
    boolean skipNextCombatTurn() {

        if (!actor.isRegistered())
            return true;

        try {

            if (opponent != null) {

                final long lastHitTimeStamp = secondsPastLastHitReceived();

                if (lastHitTimeStamp > MAX_SECONDS_TILL_ATTACK_COOLDOWN) {
                    actor.getTimerRepository().cancel(TimerKey.ATTACK_COOLDOWN);
                    opponent = null;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.skipNextCombatTurn();
    }

    public void skullByAttacking(final Player target) {

        if (target.isPlayer() && actor.isPlayer()) {
            if (!target.getAsPlayer().containsAttackedBy(actor.getAsPlayer().getUsername()) &&
                    !actor.getAsPlayer().containsAttackedBy(target.getAsPlayer().getUsername())) {
                target.getAsPlayer().addToAttackedBy(actor.getAsPlayer().getUsername());
            }

            if (actor.getAsPlayer().containsAttackedBy(target.getAsPlayer().getUsername())) {
                return;
            }
        }

        if (actor.isSkulled())
            return;

        if (!AgentExtKt.inWilderness(actor))
            return;

        actor.getPacketSender().sendInterfaceRemoval();
        skull(SkullType.WHITE_SKULL);
    }

    public void skull(SkullType type) {
        skull(type, type.getDuration());
    }

    public void skull(SkullType type, int durationInSeconds) {

        actor.setSkullType(type);
        actor.setSkullTimer(Misc.getTicks(durationInSeconds));
        actor.updateAppearance();


        AchievementManager.processFor(AchievementType.COMBAT_READY, actor);

        if (type == SkullType.RED_SKULL) {
            actor.sendMessage("@red@You have received a Red Skull. You can no longer use the Protect item prayer!");
            PrayerHandler.deactivatePrayer(actor, PrayerHandler.PROTECT_ITEM);
        } else if (type == SkullType.WHITE_SKULL) {
            actor.sendMessage("You've been skulled!");
        }
    }

    void handlePrayerEffect(int damage) {

        if (PrayerHandler.isActivated(actor, PrayerHandler.REDEMPTION))
            CombatPrayer.handleRedemption(actor, damage);

        if (PrayerHandler.isActivated(actor, PrayerHandler.SMITE))
            CombatPrayer.handleSmite(actor, damage);
    }

    void handleItemEffectAttack(Agent attacker, Hit hit) {
        final Equipment equipment = actor.getEquipment();
        final int damage = hit.getTotalDamage();

        if (EquipmentUtil.isWearingGuthanSet(equipment)) {
            if (Misc.randomChance(25f)) {
                CombatEquipment.handleGuthans(actor, attacker, damage);
            }
        }

        if (equipment.get(AMULET_SLOT).getId() == ItemID.AMULET_OF_BLOOD_FURY) {
            if (BloodFuryAmulet.INSTANCE.getCharges(actor.getEquipment().get(EquipSlot.AMULET)) > 0) {
                if (Misc.random(100) <= 20) {
                    actor.heal((int) (damage * 0.30));
                    BloodFuryAmulet.INSTANCE.decrementCharges(actor, actor.getEquipment().get(EquipSlot.AMULET));
                }
            }
        }
    }

    void handleItemEffectDefend(Agent defender, Hit hit) {
        final Equipment equipment = actor.getEquipment();
        final int damage = hit.getTotalDamage();

        handleRecoil(defender, hit, equipment);
    }

    private void handleRecoil(Agent attacker, Hit hit, Equipment equipment) {

        final int damage = hit.getTotalDamage();
        double recoilDamagePercentage = 0.0;
        int recoilDamageExtra = 0;

        /*
         * A ring of recoil is created by using Lvl-1 Enchant on a sapphire ring.
         * When worn, 10% + 1 of the damage received by the player
         * (rounded down) is dealt to the attacker.
         */
        if(EquipmentUtil.hasAnyRingOfRecoil(equipment)){
            recoilDamagePercentage += 0.10;
            recoilDamageExtra += 1;
            if(equipment.contains(RingOfSuffering.CHARGED))
                RingOfSuffering.INSTANCE.handleRecoil(damage, recoilDamagePercentage, recoilDamageExtra, actor);
            else if(equipment.contains(RingOfSufferingI.CHARGED))
                RingOfSufferingI.INSTANCE.handleRecoil(damage, recoilDamagePercentage, recoilDamageExtra, actor);
            else
                RingOfRecoil.INSTANCE.handleRecoil(equipment, damage, recoilDamagePercentage, recoilDamageExtra, actor);
        }

        if (EquipmentUtil.hasAnyAmuletOfTheDamned(equipment)){
            if(EquipmentUtil.isWearingDharokSet(equipment)) {
                if(Misc.randomChance(25f))
                    recoilDamagePercentage += 0.15;
            }
        }

        recoilDamageExtra += hit.getRecoilExtraDamage();
        recoilDamagePercentage += hit.getRecoilPercentage();
        hit.setRecoilExtraDamage(recoilDamageExtra);
        hit.setRecoilPercentage(recoilDamagePercentage);

        if (EquipmentUtil.isWearingLavaBlade(equipment))
            CombatEquipment.handleLavaBlade(actor, attacker);
        else if (EquipmentUtil.isWearingInfernalBlade(equipment))
            CombatEquipment.handleInfernalBlade(actor, attacker);
    }

    public boolean canPerformSpecialAttack(SpecialAttackType special) {

        final int specialPercentage = actor.getSpecialPercentage();
        final int specialDrain = special.getDrainAmount(actor);

        if (specialPercentage < specialDrain) {
            actor.sendMessage("You do not have enough special attack energy left!");
            actor.setSpecialActivatedAndSendState(false);
            SpecialAttackType.updateBar(actor);
            return false;
        }
        return true;
    }

    private boolean canAttackNPC(NPC targetNPC) {

        if (!targetNPC.fetchDefinition().isAttackable()) {
            actor.sendMessage("You can't attack this npc.");
            stopCombatWith(targetNPC, true);
            return false;
        }
        if (targetNPC.getMovementCoordinator().getGoalState() == GoalState.RETREAT_HOME) {
            actor.sendMessage("You're unable to attack the npc while it's retreating!");
            stopCombatWith(targetNPC, true);
            return false;
        }

        final NpcDefinition definition = targetNPC.fetchDefinition();
        final int requiredSlayerLevel = definition.getSlayerLevel();
        final int actorSlayerLevel = actor.getSkillManager().getCurrentLevel(Skill.SLAYER);

        if (actorSlayerLevel < requiredSlayerLevel) {
            actor.sendMessage("You don't have the correct Slayer level to attack this creature.");
            stopCombatWith(targetNPC, true);
            return false;
        }

        if(attackStrategy != null) {
            if (targetNPC instanceof Boss) {
                final AttackType type = attackStrategy.type();
                if (type != null) {
                    final Pair<Boolean, String> pair = ((Boss) targetNPC).immuneToAttack(type);
                    if (pair.getFirst()) {
                        Optional.ofNullable(pair.getSecond()).ifPresent(actor::sendMessage);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void stopCombatWith(Agent target, boolean stopTargetCombat) {
        actor.setPositionToFace(target.getPosition());
        if (stopTargetCombat)
            target.getCombat().reset(false);
        reset(false, true);
    }

    private boolean canAttackPlayer(Player targetPlayer) {

        if (targetPlayer.isInTutorial()) {
            actor.sendMessage("You can't attack players that are on tutorial mode.");
            stopCombatWith(targetPlayer, false);
            return false;
        }

        if (actor.isInTutorial()) {
            actor.sendMessage("You can't attack players while you're on tutorial mode.");
            stopCombatWith(targetPlayer, false);
            return false;
        }

        if (targetPlayer.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
            actor.sendMessage("You can't attack players that are AFK.");
            stopCombatWith(targetPlayer, false);
            return false;
        }

        if (actor.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
            actor.sendMessage("You can't attack while being AFK.");
            stopCombatWith(targetPlayer, false);
            return false;
        }

        final boolean inFightArena = AreaManager.DuelFightArena.contains(actor);
        final boolean inWilderness = AgentExtKt.inWilderness(actor);
        final boolean targetInWilderness = AgentExtKt.inWilderness(targetPlayer);


        if(actor.getCombat().getAutocastSpell() == CombatSpellType.TRIDENT_OF_THE_SEAS.getSpell() || actor.getCombat().getAutocastSpell() == CombatSpellType.TRIDENT_OF_THE_SWAMP.getSpell()
        || actor.getCombat().getCastSpell() == CombatSpellType.TRIDENT_OF_THE_SEAS.getSpell() || actor.getCombat().getCastSpell() == CombatSpellType.TRIDENT_OF_THE_SWAMP.getSpell()
                && inWilderness && targetPlayer.isPlayer()) {
            actor.sendMessage("You cannot attack players with using trident spells in the Wilderness.");
            stopCombatWith(targetPlayer, false);
            return false;
        }

        if (inWilderness || inFightArena) {
            if (EquipmentUtil.isWieldingTemporaryWeapon(actor)) {
                actor.sendMessage("You can't attack players in "+(inFightArena ? "duel arena" : "the wilderness")+" using temporary weapons.");
                stopCombatWith(targetPlayer, false);
                return false;
            }
        }

        if (inWilderness && targetInWilderness){
            final int combatDifference = Math.abs(AgentExtKt.combatLevelCapped_126(actor)-AgentExtKt.combatLevelCapped_126(targetPlayer));
            final int combatDifference_UnCapped = Math.abs(AgentExtKt.combatLevel(actor)-AgentExtKt.combatLevel(targetPlayer));
            if ((actor.getGameMode().isPureOrMaster() || targetPlayer.getGameMode().isPureOrMaster())
            && actor.getSkillManager().calculateCombatLevel() > 126 || targetPlayer.getSkillManager().calculateCombatLevel() > 126) {
                    if (combatDifference_UnCapped > actor.getWildernessLevel() || combatDifference_UnCapped > targetPlayer.getWildernessLevel()) {
                        actor.sendMessage("Your level difference is too great!");
                        actor.sendMessage("You need to move deeper into the Wilderness.");
                        stopCombatWith(targetPlayer, false);
                        return false;
                }
            } else {

                if (combatDifference > actor.getWildernessLevel() || combatDifference > targetPlayer.getWildernessLevel()) {
                    actor.sendMessage("Your level difference is too great!");
                    actor.sendMessage("You need to move deeper into the Wilderness.");
                    stopCombatWith(targetPlayer, false);
                    return false;
                }
            }

            final boolean pvpOff = actor.staffPvpToggled();
            final boolean targetPvpOff = targetPlayer.staffPvpToggled();

            /*final boolean pjTimer = target.getCombat().damageMap.values().stream()
                    .anyMatch(it -> !it.getStopwatch().elapsed(1000 * 10));*/ //broken.

            boolean alreadyAttackingActor = false;

            if (target.lastAgentHitBy != null) {
                if (!target.lastAgentHitBy.equals(actor) && target.getCombat().checkPJTimer()) {
                    alreadyAttackingActor = true;
                }
            }

            if((alreadyAttackingActor /*&& pjTimer*/) && !AreaManager.allInMulti(target, actor)) {
                actor.sendMessage("That player is already under attack!");
                stopCombatWith(targetPlayer, true);
                return false;
            }

            if (pvpOff || targetPvpOff) {
                final String message;
                if (pvpOff)
                    message = "You can't attack other players when you have the Wilderness safety toggle switched on.";
                else
                    message = "You can't attack players that have their Wilderness safety toggle switched on.";
                actor.sendMessage(message);
                stopCombatWith(targetPlayer, true);
                return false;
            }

            if (actor.getGameMode().isSpawn() && !targetPlayer.getGameMode().isSpawn()) {
                stopCombatWith(targetPlayer, true);
                actor.sendMessage("You can only attack players of your same spawn game mode in the Wilderness.");
                return false;
            }
        }else if (!AreaManager.canAttack(actor, targetPlayer)) {
            if (CastleWars.isInCastleWars(actor)) {
                return false;
            } else if (targetPlayer.isPlayer() && !targetInWilderness && !inWilderness) {
                actor.sendMessage("You can't attack players when you're not in the Wilderness.");
            } else if (targetPlayer.isPlayer() && targetInWilderness && !inWilderness) {
                actor.sendMessage("You can't attack players when you're not in the Wilderness.");
            } else if (actor.getGameMode().isSpawn() && !targetPlayer.getGameMode().isSpawn()) {
                actor.sendMessage("You can't attack players that are not in spawn game mode.");
                stopCombatWith(targetPlayer, true);
                return false;
            }
            stopCombatWith(targetPlayer, false);
            return false;
        }

        final boolean bothInDuel = actor.getDueling().getState() == DuelState.IN_DUEL && targetPlayer.getDueling().getState() == DuelState.IN_DUEL;
        final boolean duelingEachOther = actor.getDueling().getInteract() == targetPlayer && targetPlayer.getDueling().getInteract() == actor;

        if (bothInDuel && !duelingEachOther) {
            actor.sendMessage("That isn't your opponent.");
            actor.setPositionToFace(actor.getDueling().getInteract().getPosition());
            reset(false, true);
            return false;
        }

        return true;
    }

    private void handleSpellRunesRemoval(Player actor) {
        final Spell spell = getCastSpell();
        if(spell != null)
            spell.deleteItemsRequired(actor);
    }

    private void handleMonsterKillTracker(Player actor) {
        final MonsterKillTracker killTracker = actor.getKillTracker();
        final NPC npcTarget = (NPC) target;

        if (killTracker.getFighting() != npcTarget) {
            killTracker.setFighting(npcTarget);
            killTracker.setTime(System.currentTimeMillis());
        }
    }

    public void resetMagicCasting() {
        setCastSpellType(null);
        setCastSpell(null);
    }

    public SecondsTimer getFireImmunityTimer() {
        return fireImmunityTimer;
    }

    public SecondsTimer getSuperFireImmunityTimer() {
        return superFireImmunityTimer;
    }

    public SecondsTimer getTeleBlockTimer() {
        return teleportBlockTimer;
    }
    
    public SecondsTimer getSpellBlockTimer() {
        return spellBlockTimer;
    }

    public SecondsTimer getPrayerBlockTimer() {
        return prayerBlockTimer;
    }

    public SecondsTimer getProtectBlockTimer() {
        return protectPrayerBlockTimer;
    }

    public BountyHuntController getBountyHuntController() {
        return bountyHuntController;
    }

    public void setCastSpellType(CombatSpellType castSpellType) {
        this.castSpellType = castSpellType;
    }

    public CombatSpellType getCastSpellType() {
        return castSpellType;
    }

}
