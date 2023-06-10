package com.grinder.game.entity.agent.npc;

import com.grinder.Config;
import com.grinder.game.World;
import com.grinder.game.content.minigame.castlewars.CastleWars;
import com.grinder.game.content.miscellaneous.npcs.CleverBot;
import com.grinder.game.content.pvm.Revenants;
import com.grinder.game.content.skill.Skills;
import com.grinder.game.content.skill.skillable.impl.hunter.HunterTraps;
import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.definition.NpcStatsDefinition;
import com.grinder.game.definition.factory.NpcStatsFactory;
import com.grinder.game.entity.Entity;
import com.grinder.game.entity.EntityType;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.NPCCombat;
import com.grinder.game.entity.agent.combat.attack.AttackStrategy;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.strategy.npc.NPCStrategyFactory;
import com.grinder.game.entity.agent.combat.attack.strategy.npc.monster.SpinolypAttack;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.entity.agent.combat.misc.CombatPrayer;
import com.grinder.game.entity.agent.movement.NPCMotion;
import com.grinder.game.entity.agent.movement.NPCMovementCoordinator;
import com.grinder.game.entity.agent.movement.pathfinding.target.BankerTargetStrategy;
import com.grinder.game.entity.agent.movement.pathfinding.target.RectangleTargetStrategy;
import com.grinder.game.entity.agent.movement.pathfinding.traverse.LargeTraversal;
import com.grinder.game.entity.agent.movement.pathfinding.traverse.MediumTraversal;
import com.grinder.game.entity.agent.movement.pathfinding.traverse.SmallTraversal;
import com.grinder.game.entity.agent.movement.pathfinding.traverse.TraversalType;
import com.grinder.game.entity.agent.npc.bot.NPCBotHandler;
import com.grinder.game.entity.agent.npc.monster.MonsterRace;
import com.grinder.game.entity.agent.npc.monster.aggression.MonsterAggressionTolerancePolicy;
import com.grinder.game.entity.agent.npc.monster.boss.impl.corporealbeast.CorporealBeastBoss;
import com.grinder.game.entity.agent.npc.monster.boss.impl.GiantMoleBoss;
import com.grinder.game.entity.agent.npc.monster.boss.impl.zulrah.ZulrahBoss;
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.updating.UpdateBlock;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.FacingDirection;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.areas.InstancedArea;
import com.grinder.game.model.areas.InstancedBossArea;
import com.grinder.game.model.areas.UntypedInstancedBossArea;
import com.grinder.game.model.areas.instanced.PestControlArea;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.NPCDeathTask;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;
import com.grinder.util.oldgrinder.Area;
import com.grinder.util.time.SecondsTimer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.grinder.net.packet.impl.NPCOptionPacketListener.isBanker;

/**
 * Represents a non-playable character, which players can interact with.
 *
 * @author Professor Oak
 */
public class NPC extends Agent {

    protected final int id;

    private final NPCCombat combat = new NPCCombat(this);
    private final NPCMotion motion = new NPCMotion(this);
    private final NPCMovementCoordinator movementCoordinator = new NPCMovementCoordinator(this);
    private final Skills skills = new Skills();

    /**
     * Is this npc supposed to fully sequence,
     * or only partially to reduce performance (no walking etc).
     * <p>
     * This value is {@code false} when there is no {@link Player}
     * within {@link Player#NORMAL_VIEW_DISTANCE view distance} of this npc.
     */
    private final AtomicBoolean sequence = new AtomicBoolean(false);

    /**
     * Is this npc supposed to be visible (i.e. included in npc updating),
     * or should this npc become invisible.
     * <p>
     * Setting this to {@code true} will completely remove the npc from the client of players,
     * thus will also no longer be visible on the minimap.
     */
    private final AtomicBoolean visible = new AtomicBoolean(true);

    private final List<String> debugMessages = new ArrayList<>();

    public final SecondsTimer increaseStats = new SecondsTimer();

    private NpcStatsDefinition statsDefinition = null;

    private NpcDefinition definition = null;

    /**
     * The direction this npc is facing towards.
     */
    private FacingDirection face = FacingDirection.NORTH;

    private FacingDirection spawnDirection = FacingDirection.NORTH;

    /**
     * The strategy the npc uses for launching attacks during combat.
     */
    private AttackStrategy<? extends Agent> attackStrategy;

    private Position spawnPosition;

    /**
     * The optional 'owner' of this npc, meaning this {@link Player}
     * will be the only one whom can interact with this npc.
     */
    private Player owner;

    private NPCBotHandler botHandler;

    private Area absoluteWalkableArea;

    private boolean isDying;
    private boolean pet;

    private final int maximumHitpoints;
    private int hitpoints;
    private int headIcon = -1;

    public boolean removeRespawnMessage;

    /**
     * Constructs a new npc.
     * <p>
     * Please do not use the constructor directly.
     *
     * @param id       The npc id.
     * @param position The npc spawn (default) {@link Position}.
     * @see NPCFactory#create(int, Position) for creating new {@link NPC} instances.
     */
    protected NPC(int id, Position position) {
        super(EntityType.NPC, position);
        this.id = id;
        this.spawnPosition = position;
        final NpcDefinition definition = fetchDefinition();
        if (definition != null) {
            maximumHitpoints = definition.getHitpoints();
            setHitpoints(definition.getHitpoints());
            setWalkableArea(definition.getWalkRadius());
            if (definition.getCombatLevel() > 0)
                NpcStatsFactory.produce(definition).ifPresent(it -> statsDefinition = it);

            final int[] npcStats = definition.getStats();
            skills.set(Skill.HITPOINTS, getHitpoints(), definition.getHitpoints());
            if (npcStats != null) {
                skills.set(Skill.ATTACK, npcStats[0], npcStats[0]);
                skills.set(Skill.STRENGTH, npcStats[1], npcStats[1]);
                skills.set(Skill.DEFENCE, npcStats[2], npcStats[2]);
                skills.set(Skill.RANGED, npcStats[3], npcStats[3]);
                skills.set(Skill.MAGIC, npcStats[4], npcStats[4]);
                increaseStats.start(60);
            }
            TraversalType type = TraversalType.values()[definition.getMoveType()];
            int size = getSize();
            if (size == 1) {
                traversal = new SmallTraversal(type, definition.collidesWithEntities());
            } else if (size == 2) {
                traversal = new MediumTraversal(type, definition.collidesWithEntities());
            } else {
                traversal = new LargeTraversal(type, definition.collidesWithEntities(), size, size);
            }
            unpassable = definition.isUnpassable();
        } else {
            traversal = new SmallTraversal(TraversalType.Land, true);
            maximumHitpoints = 10;
            setHitpoints(10);
        }
        NPCBotHandler.assignBotHandler(this);
        NPCStrategyFactory.assignAttackStrategy(this);
        if (isBanker(getId()) || getId() == NpcID.EMILY || getId() == 7663) {
            interactTarget = new BankerTargetStrategy(this, 1);
        } else if (AreaManager.inside(getPosition(), SHOP_AREAS) && isShopNPC(id) || getId() == NpcID.SAWMILL_OPERATOR) {
            interactTarget = new BankerTargetStrategy(this, 0);
        } else {
            interactTarget = new RectangleTargetStrategy(this);
        }
        getMotion().updateCollision();
        //AreaManager.sequence(this);
        AreaManager.checkAreaChanged(this);
    }


    @NotNull
    public MonsterRetreatPolicy getRetreatPolicy() {
        if (NpcID.DEMONIC_GORILLA == id || NpcID.DEMONIC_GORILLA_7145 == id || NpcID.DEMONIC_GORILLA_7146 == id) {
            return MonsterRetreatPolicy.NEVER;
        }
        if (fetchDefinition().getCombatLevel() < 300) {
            return MonsterRetreatPolicy.NEVER;
        }

        return MonsterRetreatPolicy.STANDARD;
    }

    /**
     * The shop areas boundaries.
     */
    public static final Boundary[] SHOP_AREAS = new Boundary[]{
            //new Boundary(3067, 3074, 3505, 3515), // Edge Shops
            //new Boundary(3064, 3065, 3505, 3508), // Edge Shops
            //new Boundary(3064, 3071, 3514, 3517), // Edge Shops
            //new Boundary(3074, 3075, 3507, 3509), // Edge Shops
            new Boundary(3076, 3101, 3507, 3513) // Edge Shops
    };

    /**
     * Shop NPC's that are located in home area
     *
     * @return
     */
    private boolean isShopNPC(int npcId) {
        switch (npcId) {
            case NpcID.HORVIK:
            case NpcID.APOTHECARY:
            case NpcID.EMBLEM_TRADER:
            case NpcID.LOWE:
            case NpcID.PERDU:
            case NpcID.MAGIC_INSTRUCTOR:
            case NpcID.ALRENA_4250:
            case NpcID.MAGE_OF_ZAMORAK:
            case NpcID.BORAT:
            case NpcID.WITCH_4409:
            case NpcID.HECKEL_FUNCH:
            case NpcID.PIRATE_JACKIE_THE_FRUIT:
            case NpcID.QUARTERMASTER:
            case NpcID.SIR_LANCELOT:
            case NpcID.DEATH:
            case NpcID.BARMAN:
            case NpcID.TZHAARHURTEL:
            case NpcID.ADVENTURER_EASY:
            case NpcID.TANNER:
            case NpcID.SHOP_KEEPER_2821:
            case NpcID.SHOP_ASSISTANT_2822:
            case NpcID.ALI_THE_FARMER:
            case NpcID.BOB_BARTER_HERBS:
            case NpcID.MAKEOVER_MAGE:
            case NpcID.ROMMIK:
            case NpcID.PHANTUWTI_FANSTUWI_FARSIGHT:
            case NpcID.ZAHUR:
            case NpcID.WISE_OLD_MAN:
            case NpcID.FANCY_DRESS_STORE:
            case NpcID.HUNTING_EXPERT_1504:
            case NpcID.VERMUNDI:
                return true;
            default:
                return false;
        }
    }

    @NotNull
    public MonsterAggressionTolerancePolicy getAggressionTolerancePolicy() {
        if (attackStrategy instanceof SpinolypAttack)
            return MonsterAggressionTolerancePolicy.NEVER;
        return fetchDefinition().getCombatLevel() > 150
                ? MonsterAggressionTolerancePolicy.NEVER
                : MonsterAggressionTolerancePolicy.IN_VICINITY;
    }

    @Override
    public double protectionPrayerReductionMultiplier(AttackType type) {

        final NpcDefinition definition = fetchDefinition();
        final int combatLevel = definition.getCombatLevel();

        if (!definition.hitsThroughProtectionPrayer(type)) {
            if (this instanceof GiantMoleBoss) // Giant mole
                // According to wiki The most common method of killing the Giant Mole is with Dharok the Wretched's equipment and 1 health
                return 0;
            else if (this instanceof CorporealBeastBoss && type == AttackType.MAGIC)
                // Certain NPCs may still ignore or partially bypass Protect from Magic, such as Corporeal Beast, whose attacks will deal 66% of the normal damage with the prayer active.
                return 64.00D;
            else if (type == AttackType.MAGIC && MonsterRace.Companion.isAnyRace(this, MonsterRace.DRAGON, MonsterRace.WYVERN))
                return 0.50;
            else if (id == NpcID.VERAC_THE_DEFILED && Misc.randomChance(CombatPrayer.VERAC_HIT_THROUGH_PRAYER_CHANCE))
                // Veracs attacks have a 25% chance of having a guaranteed hit, ignoring accuracy.
                return 1.0;
            else if (combatLevel > 378 && !(this instanceof CorporealBeastBoss))
                return CombatPrayer.PRAYER_DAMAGE_REDUCTION_RATIO_VS_BOSS_NPC;
            else
                return 0.0;
        }

        return 1.0;
    }

    @Override
    public void debug(final String string) {

        if (!Config.enable_debug_messages)
            return;

        if (sequence.get()) {
            debugMessages.add(string);
            onStateChange(string);
        }
    }


    public void debug(String s, boolean condition) {
        if (condition)
            debug(s);
    }

    protected int trackingRange() {
        return 16;
    }

    public boolean isInside(Entity entity) {
        return isInside(entity.getCenterPosition());
    }

    protected boolean isInside(Position position) {
        int xPos = getPosition().getX();
        int yPos = getPosition().getY();

        return position.getX() >= xPos && position.getX() < xPos + getSize() && position.getY() >= yPos && position.getY() < yPos + getSize();
    }

    public boolean inBoundaries(Entity entity, int distance) {

        int size = getSize();

        if (distance == -1) {
            distance = getAttackRange();
        }
        int max = distance + (size / 2);
        int offset = 0;

        if (size % 2 != 0) {
            offset++;
        }

        int myX = getX();
        int myY = getY();
        int startX = myX - max;
        int endX = myX + max + offset;
        int startY = myY - max;
        int endY = myY + max + offset;

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                if (entity.getX() == x && entity.getY() == y) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get max distance that this NPC can attack at.
     *
     * @return max attack distance.
     */
    protected int getAttackRange() {
        return getCombat().determineStrategy().requiredDistance(this);
    }

    /**
     * Calculate walking area in absolute coordinates<br>
     * <b>Note: It's necessary to have {@code initialPosition} value set in
     * order to calculate absolute area.</b>
     *
     * @param walkableArea Walk area relative to NPC initial position.
     */
    private void calculateAbsoluteWalkableArea(Area walkableArea) {
        Objects.requireNonNull(spawnPosition, "Initial position is not set.");
        if (walkableArea != null) {
            absoluteWalkableArea = walkableArea.getAbsolute(spawnPosition);
        }
    }

    protected void setWalkableArea(int walkableRange) {
        // this.walkableArea = new Area(walkableRange);
        calculateAbsoluteWalkableArea(new Area(walkableRange));

    }

    @Override
    public void onAdd() {
    }

    @Override
    public void onRemove() {
        getMotion().removeCollision();
    }

    private boolean isValidNPC() {
        return fetchDefinition() != null;
    }

    /**
     * This methods only sequences 'expensive' processes when this {@link NPC} is in close proximity of a {@link Player}.
     */
    public void preSequence() {

        if (isValidNPC()) {


            if (HunterTraps.INSTANCE.process(this) || sequence.get()) {
                movementCoordinator.sequence();
            } else
                debug("not sequencing");

            timerRepository.process();
            motion.sequenceMovement();

            sequenceTemporaryStats();


            Optional.ofNullable(botHandler)
                    .ifPresent(NPCBotHandler::process);
        }
    }

    /**
     * This methods only sequences {@link #combat} processes when this {@link NPC} is in close proximity of a {@link Player}.
     */
    public void sequence() {
        onStateChange("start of sequence");
        debugMessages.clear();

        // Only process the npc if they have properly been added
        // to the game with a definition.
        if (isValidNPC()) {
            motion.sequence();

            if (botHandler != null || fetchDefinition().getCombatLevel() > 0) {
                getCombat().sequence();
                getCombat().sequenceHitsAndDamages();
            }

            if (this instanceof CleverBot) {
                ((CleverBot) this).speechSequence();
            }

            pulse();

            AreaManager.sequence(this);
            onStateChange("sequenced areas");
            handleMiscellaneousProcesses();
            onStateChange("sequenced misc");
        } else
            debug("Npc is not valid");

    }

    public void pulse() {

    }

    private void handleMiscellaneousProcesses() {
        Revenants.onSequence(this);
    }

    public void handlePositionFacing() {

        if (face != null && id != NpcID.SNAKELING && !isPet()) {

            final boolean canTurn = motion.movementDisabled() || (!combat.isInCombat() && motion.completed() && motion.canMove());

            if (canTurn && !isDying() && movementCoordinator.getRadius() < 2) {
                setPositionToFace(NPCFactory.INSTANCE.getFacingPosition(getPosition(), getFace()));
            }
        }
    }

    public void resetEntityInteraction() {
        handlePositionFacing();
        setEntityInteraction(null);
    }

    public void regenerateFullHealth() {
        setHitpoints(fetchDefinition().getHitpoints());
    }

    @Override
    public void appendDeath() {
        if (getId() == NpcID.COMBAT_DUMMY || getId() == NpcID.UNDEAD_COMBAT_DUMMY)
            return;

        if (!isDying) {
            this.motion.clearSteps();
            TaskManager.submit(new NPCDeathTask(this, 2, false));
            isDying = true;
        }
    }

    @Override
    public int getHitpoints() {
        return hitpoints;
    }

    @Override
    public int getMaxHitpoints() {
        return maximumHitpoints;
    }

    @Override
    public NPC setHitpoints(int hitpoints) {
        this.hitpoints = hitpoints;
        skills.set(Skill.HITPOINTS, hitpoints, fetchDefinition().getHitpoints());
        if (this.hitpoints <= 0)
            appendDeath();
        return this;
    }

    @Override
    public void heal(int heal) {
        if ((this.hitpoints + heal) > fetchDefinition().getHitpoints()) {
            setHitpoints(fetchDefinition().getHitpoints());
            return;
        }
        setHitpoints(this.hitpoints + heal);
    }

    public void healWithHitsplat(int heal) {
        heal(heal);
        //TODO add proper support
        getBlockSet().add(UpdateBlock.Companion.createUpdateFirstHitBlock(this, new Damage(heal, DamageMask.HEAL)));
    }

    @Override
    public void updateAppearance() {
        blockSet.add(UpdateBlock.Companion.createTransformBlock(getNpcTransformationId(), getHeadIcon()));
    }

    @Override
    public boolean isNpc() {
        return true;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.NPC;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof NPC && isActive()
                && ((NPC) other).isActive()
                && ((NPC) other).getIndex() == getIndex()
                && ((NPC) other).getId() == getId();
    }

    @Override
    public int getSize() {
        if (super.getSize() > 1) {
            return super.getSize();
        }
        if (id == 1618 || id == 1613) {
            return 1;
        }
        return fetchDefinition() == null ? 1 : fetchDefinition().getSize();
    }

    @Override
    public int getBaseAttackSpeed() {
        return fetchDefinition().getAttackSpeed();
    }

    @Override
    public int getAttackAnim() {
        if (fetchDefinition().getName().contains("Portal")) {
            return -1;
        }
        return fetchDefinition().getAttackAnim();
    }

    @Override
    public int getBlockAnim() {
        return fetchDefinition().getDefenceAnim();
    }

    @Override
    public NPCMotion getMotion() {
        return motion;
    }

    @Override
    public NPCCombat getCombat() {
        return combat;
    }

    @Override
    public void resetTransformation() {
        setNpcTransformationId(npcId());
    }

    @Override
    public boolean isMorphed() {
        return getNpcTransformationId() != -1
                && getNpcTransformationId() != npcId();
    }

    public void sendBlockSound(Player target) {

        if (fetchDefinition().getBlockSound() <= 0)
            return;

        target.getPacketSender().sendSound(fetchDefinition().getBlockSound(), 5);
    }

    /**
     * NPC's that have no block animation but should send a sound
     *
     * @param npcId
     * @return
     */
    public boolean isNoRetaliateNPC(int npcId) {
        switch (npcId) {
            case NpcID.COMBAT_DUMMY:
            case NpcID.UNDEAD_COMBAT_DUMMY:
            case NpcID.PORTAL:
            case NpcID.PORTAL_1740:
            case NpcID.PORTAL_1741:
            case NpcID.PORTAL_1742:
            case NpcID.PORTAL_1743:
            case NpcID.PORTAL_1744:
            case NpcID.PORTAL_1745:
            case NpcID.PORTAL_1746:
            case NpcID.PORTAL_1747:
            case NpcID.PORTAL_1748:
            case NpcID.PORTAL_1749:
            case NpcID.PORTAL_1750:
            case NpcID.PORTAL_1751:
            case NpcID.PORTAL_1752:
            case NpcID.PORTAL_1753:
            case NpcID.PORTAL_1754:
                return true;
            default:
                return false;
        }
    }

    public void sendDeathSound(Player target) {
        if (fetchDefinition().getDeathSound() <= 0) {
            return;
        }

        target.getAsOptionalPlayer().ifPresent(player -> player.getPacketSender().sendSound(fetchDefinition().getDeathSound()));
    }

    public void respawn() {
        if (this.getArea() instanceof PestControlArea) // Pest control NPC's do not respawn
            return;
        if (this.getArea() instanceof InstancedBossArea)
            return;
        if (this.getArea() instanceof InstancedArea)
            return;
        if (this.getArea() instanceof UntypedInstancedBossArea) // Pest control NPC's do not respawn
            return;
        final NPC copy = NPCFactory.INSTANCE.create(id, getSpawnPosition());
        copy.getMovementCoordinator().setRadius(getMovementCoordinator().getRadius());
        copy.setFace(getFace());
        copy.setLastFacingDirection(getLastFacingDirection());
        World.getNpcAddQueue().add(copy);
    }

    /**
     * Decrease boosted stats Increase lowered stats
     */
    private void sequenceTemporaryStats() {
        if (getHitpoints() > 0) {
            if (increaseStats.finished()) {
                for (Skill skill : Skill.values()) {
                    int current = skills.getLevel(skill);
                    int max = skills.getMaximumLevel(skill);

                    // Should lowered stats be increased?
                    if (current < max) {
                        if (increaseStats.finished()) {
                            int restoreRate = 1;
                            if (skill != Skill.PRAYER) {
                                skills.set(skill, current + restoreRate, max);
                            }
                        }
                    }
                }

                // Reset timerRepository
                if (increaseStats.finished()) {
                    increaseStats.start(90);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "NPC{" +
                "index=" + getIndex() +
                "id=" + id +
                "name=" + fetchDefinition().getName() +
                '}';
    }

    @Override
    public Skills getSkills() {
        return skills;
    }

    /**
     * TODO: do this proper lol
     */
    public boolean defenceModifier() {
        return this instanceof ZulrahBoss;
    }

    public NpcDefinition fetchDefinition() {
        if (definition == null || definition == NpcDefinition.DEFAULT)
            definition = NpcDefinition.forId(id);
        return definition;
    }

    public AtomicBoolean sequenceProperty() {
        return sequence;
    }

    public AtomicBoolean visibilityProperty() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible.set(visible);
    }

    public int npcId() {
        return id;
    }

    public int getId() {
        if (getNpcTransformationId() != -1)
            return getNpcTransformationId();
        return id;
    }

    public int getMaxHit(AttackType type) {
        return fetchDefinition().getMaxHit();
    }

    public int getMaximumHitpoints() {
        return maximumHitpoints;
    }

    public boolean isPet() {
        return pet;
    }

    public void setPet(boolean pet) {
        this.pet = pet;
    }

    public Position getSpawnPosition() {
        return spawnPosition;
    }

    public void setSpawnPosition(Position spawnPosition) {
        this.spawnPosition = spawnPosition;
    }

    public int getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(int headIcon) {
        this.headIcon = headIcon;
        updateAppearance();
    }

    public boolean isDying() {
        return isDying;
    }

    public void setDying(boolean isDying) {
        this.isDying = isDying;
    }

    public Player getOwner() {
        return owner;
    }

    public NPC setOwner(Player owner) {
        this.owner = owner;
        return this;
    }

    public NPCMovementCoordinator getMovementCoordinator() {
        return movementCoordinator;
    }

    public NPCBotHandler getBotHandler() {
        return botHandler;
    }

    public void setBotHandler(NPCBotHandler botHandler) {
        this.botHandler = botHandler;
    }

    public AttackStrategy<? extends Agent> getAttackStrategy() {
        return attackStrategy;
    }

    public void setAttackStrategy(AttackStrategy<? extends Agent> attackStrategy) {
        this.attackStrategy = attackStrategy;
    }

    public FacingDirection getFace() {
        return face;
    }

    public void setFace(FacingDirection face) {
        this.face = face;
        setPositionToFace(getPosition().clone().move(face.getDirection()), true);
    }

    public FacingDirection getSpawnFace() {
        return spawnDirection;
    }

    public void setSpawnFace(FacingDirection face) {
        this.spawnDirection = face;
    }

    public NpcStatsDefinition getStatsDefinition() {
        return statsDefinition;
    }

    public List<String> getDebugMessages() {
        return debugMessages;
    }

    public Area getAbsoluteWalkableArea() {
        return absoluteWalkableArea;
    }

    public void onDeath() {
        if (id == NpcID.BARRICADE) {
            if (CastleWars.zammyBarricades.contains(this)) {
                CastleWars.zammyBarricades.remove(this);
            } else if (CastleWars.saraBarricades.contains(this)) {
                CastleWars.saraBarricades.remove(this);
            }
        }
    }

    public ArrayList<Player> getLocalPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        for (Player p : World.getPlayers()) {
            if (p == null) {
                continue;
            }
            if (getPosition().getDistance(p.getPosition()) < 32 && getPosition().getZ() == p.getPosition().getZ()) {
                players.add(p);
            }
        }
        return players;
    }

}
