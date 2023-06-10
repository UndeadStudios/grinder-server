package com.grinder.game.definition;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.model.Graphic;
import com.grinder.util.DefaultInteger;
import com.grinder.util.JinDefault;
import com.grinder.util.NpcID;

import java.util.*;

/**
 * Represents an npc's definition.
 * Holds its information, such as
 * name and combat level.
 *
 * @author Professor Oak
 */
public final class NpcDefinition extends JinDefault {

    /**
     * The map containing all our {@link NpcDefinition}s.
     */
    private static final Map<Integer, NpcDefinition> definitions = new HashMap<>();
	private static final Map<String, NpcDefinition> nameDefinition = new HashMap<>();

    private static final List<Integer> NPCS_IMMUNE_TO_POISON =
			Arrays.asList(NpcID.ZULRAH, NpcID.ZULRAH_2043, NpcID.ZULRAH_2044,
					5908,5887,5888,5889,5890,5891,7273,7275,8081,8087,5862,2205,6493,
					2215,6494,1425,7100,7101,7102,7103,7115,2061,5634,5635,5636,3129,6495,3162,6492,7550,7551,7552,7553,
					7554,7555,8089,7104,7105,7106,7107,5257,6804,6914,6915,6916,6917,6918,7573,7574,7744,7745,6766,6767,
					5918,6177,2127,2128,2129,2130,2131,2132,2025,2046,2047,6639,6655,7406,498,499,6503,6609,2450,2451,
					2452,2453,2454,2455,2456,6504,6610,8061);
	/**
     * The default {@link NpcDefinition} that will be used.
     */
    public static final NpcDefinition DEFAULT = new NpcDefinition();
    private int id;
    private String name;
    private String examine;
    
    @DefaultInteger(1)    
    private int size;
   
    private int walkRadius;
    
    private boolean attackable;
    private boolean retreats;
    private boolean aggressive;
    private boolean poisonous;
    private int respawn;
    private int hitpoints = 10;
    

    @DefaultInteger(1)  
    private int maxHit;

    @DefaultInteger(4)  
    private int attackSpeed;
    private int attackAnim;
    
    private int rangeAnim;
    private int magicAnim;
    
    private int rangeHitDelay;
    private int rangeStartGfx;
    private int rangeProjectile;
    private int rangeEndGfx;
    private int rangeProjectileDelay;
    
    private int mageHitDelay;
    private Graphic mageStartGfx;
    private int magicProjectile;
    private Graphic magicEndGfx;
    private int mageProjectileDelay;
    
    private int deathTime = 1;
    
    private int defenceAnim;
    private int deathAnim;
    private int attackSound;
    private int blockSound;
    private int deathSound;
    private int combatLevel;
    private int[] stats;
    private int slayerLevel;    

    @DefaultInteger(8)  
    private int combatFollowDistance;
	private int prefferedDistance = -1;

	/**
	 * Whether an npc collides with other NPCs
	 */
	private boolean collidesWithEntities = true;

	/**
	 * How the npc navigates the world as index of {@link com.grinder.game.entity.agent.movement.pathfinding.traverse.TraversalType}
	 */
	private int moveType = 0;

	/**
	 * Whether players can pass through this npc
	 */
	private boolean unpassable = false;

	/**
     * Attempts to get the {@link NpcDefinition} for the
     * given NPC.
     *
     * @param npcId
     * @return
     */
    public static NpcDefinition forId(int npcId) {
        return definitions.getOrDefault(npcId, DEFAULT);
    }
    public static NpcDefinition forName(String npc) {
        return nameDefinition.get(npc);
    }

    public static boolean isImmuneToPoison(Agent agent) {
        if (agent instanceof NPC) {
  			return NPCS_IMMUNE_TO_POISON.contains(agent.getAsNpc().fetchDefinition().getId());
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getExamine() {
        return examine;
    }

    public int getSize() {
        return size;
    }

    public int getWalkRadius() {
        return walkRadius;
    }

    public boolean isAttackable() {
        return attackable;
    }

	public void setAttackable(boolean attackable) {
		this.attackable = attackable;
	}

    public boolean doesRetreat() {
        return retreats;
    }

    public boolean isAggressive() {
        return aggressive;
    }

	public void setAggressive(boolean aggressive) {
		this.aggressive = aggressive;
	}

	public boolean isPoisonous() {
        return poisonous;
    }

	/*public boolean isPoisonImmune() {
		return poisonImmune;
	}*/

    public int getRespawn() {

        return respawn;
    }

    public void setRespawn(int respawn){
    	this.respawn = respawn;
	}

    public int getMaxHit() {
    	if (maxHit == 0)
    		maxHit = 1;
    	if (combatLevel > 50 && maxHit < 10)
    		maxHit = combatLevel / 8;
        return maxHit;
    }

    public int getHitpoints() {
    	if (id != NpcID.SNAKELING && combatLevel > 20 && hitpoints < 5)
    		hitpoints = (int) (combatLevel * 1.15);
        return hitpoints;
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public int getAttackAnim() {
        return attackAnim;
    }
    
    public int getAttackSound() {
        return attackSound;
    }

    public int getDefenceAnim() {
        return defenceAnim;
    }
    
    public int getBlockSound() {
        return blockSound;
    }

    public int getDeathAnim() {
        return deathAnim;
    }
    
    public int getDeathSound() {
        return deathSound;
    }

    public int getCombatLevel() {
        return combatLevel;
    }

    public int[] getStats() {
        return stats;
    }

    public int getSlayerLevel() {
        return slayerLevel;
    }

	public int getCombatFollowDistance() {
		return combatFollowDistance;
	}
	/**
	 * Sets the rangeAnim
	 *
	 * @return the rangeAnim
	 */
	public int getRangeAnim() {
		return rangeAnim;
	}
	/**
	 * Sets the rangeAnim
	 * 
	 * @param rangeAnim the rangeAnim
	 */
	public void setRangeAnim(int rangeAnim) {
		this.rangeAnim = rangeAnim;
	}
	/**
	 * Sets the magicAnim
	 *
	 * @return the magicAnim
	 */
	public int getMagicAnim() {
		return magicAnim;
	}
	/**
	 * Sets the magicAnim
	 * 
	 * @param magicAnim the magicAnim
	 */
	public void setMagicAnim(int magicAnim) {
		this.magicAnim = magicAnim;
	}
	/**
	 * Sets the rangeProjectile
	 *
	 * @return the rangeProjectile
	 */
	public int getRangeProjectile() {
		return rangeProjectile;
	}
	/**
	 * Sets the rangeProjectile
	 * 
	 * @param rangeProjectile the rangeProjectile
	 */
	public void setRangeProjectile(int rangeProjectile) {
		this.rangeProjectile = rangeProjectile;
	}
	/**
	 * Sets the rangeEndGfx
	 *
	 * @return the rangeEndGfx
	 */
	public int getRangeEndGfx() {
		return rangeEndGfx;
	}
	/**
	 * Sets the rangeEndGfx
	 * 
	 * @param rangeEndGfx the rangeEndGfx
	 */
	public void setRangeEndGfx(int rangeEndGfx) {
		this.rangeEndGfx = rangeEndGfx;
	}
	/**
	 * Sets the magicProjectile
	 *
	 * @return the magicProjectile
	 */
	public int getMagicProjectile() {
		return magicProjectile;
	}
	/**
	 * Sets the magicProjectile
	 * 
	 * @param magicProjectile the magicProjectile
	 */
	public void setMagicProjectile(int magicProjectile) {
		this.magicProjectile = magicProjectile;
	}
	/**
	 * Sets the deathTime
	 *
	 * @return the deathTime
	 */
	public int getDeathTime() {
		return deathTime;
	}
	/**
	 * Sets the deathTime
	 * 
	 * @param deathTime the deathTime
	 */
	public void setDeathTime(int deathTime) {
		this.deathTime = deathTime;
	}
	/**
	 * Sets the rangeStartGfx
	 *
	 * @return the rangeStartGfx
	 */
	public int getRangeStartGfx() {
		return rangeStartGfx;
	}
	/**
	 * Sets the rangeStartGfx
	 * 
	 * @param rangeStartGfx the rangeStartGfx
	 */
	public void setRangeStartGfx(int rangeStartGfx) {
		this.rangeStartGfx = rangeStartGfx;
	}
	/**
	 * Sets the rangeProjectileDelay
	 *
	 * @return the rangeProjectileDelay
	 */
	public int getRangeProjectileDelay() {
		return rangeProjectileDelay;
	}
	/**
	 * Sets the rangeProjectileDelay
	 * 
	 * @param rangeProjectileDelay the rangeProjectileDelay
	 */
	public void setRangeProjectileDelay(int rangeProjectileDelay) {
		this.rangeProjectileDelay = rangeProjectileDelay;
	}
	/**
	 * Sets the mageProjectileDelay
	 *
	 * @return the mageProjectileDelay
	 */
	public int getMageProjectileDelay() {
		return mageProjectileDelay;
	}
	/**
	 * Sets the mageProjectileDelay
	 * 
	 * @param mageProjectileDelay the mageProjectileDelay
	 */
	public void setMageProjectileDelay(int mageProjectileDelay) {
		this.mageProjectileDelay = mageProjectileDelay;
	}
	/**
	 * Sets the rangeHitDelay
	 *
	 * @return the rangeHitDelay
	 */
	public int getRangeHitDelay() {
		return rangeHitDelay;
	}
	/**
	 * Sets the rangeHitDelay
	 * 
	 * @param rangeHitDelay the rangeHitDelay
	 */
	public void setRangeHitDelay(int rangeHitDelay) {
		this.rangeHitDelay = rangeHitDelay;
	}
	/**
	 * Sets the mageHitDelay
	 *
	 * @return the mageHitDelay
	 */
	public int getMageHitDelay() {
		return mageHitDelay;
	}
	/**
	 * Sets the mageHitDelay
	 * 
	 * @param mageHitDelay the mageHitDelay
	 */
	public void setMageHitDelay(int mageHitDelay) {
		this.mageHitDelay = mageHitDelay;
	}
	/**
	 * Sets the mageStartGfx
	 *
	 * @return the mageStartGfx
	 */
	public Graphic getMageStartGfx() {
		return mageStartGfx;
	}
	/**
	 * Sets the mageStartGfx
	 * 
	 * @param mageStartGfx the mageStartGfx
	 */
	public void setMageStartGfx(Graphic mageStartGfx) {
		this.mageStartGfx = mageStartGfx;
	}
	/**
	 * Sets the magicEndGfx
	 *
	 * @return the magicEndGfx
	 */
	public Graphic getMagicEndGfx() {
		return magicEndGfx;
	}
	/**
	 * Sets the magicEndGfx
	 * 
	 * @param magicEndGfx the magicEndGfx
	 */
	public void setMagicEndGfx(Graphic magicEndGfx) {
		this.magicEndGfx = magicEndGfx;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setExamine(String examine) {
		this.examine = examine;
	}
	
	public void setStats(int[] stats) {
		this.stats = stats;
	}
	
	public void setSize(int i) {
		this.size = i;
	}

    public int getPrefferedDistance() {
        return prefferedDistance;
    }

    public void setPrefferedDistance(int prefferedDistance) {
        this.prefferedDistance = prefferedDistance;
    }

	public Boolean collidesWithEntities() {
		return collidesWithEntities;
	}

	public Boolean isUnpassable() {
		return unpassable;
	}

	public int getMoveType() {
		return moveType;
	}

	public int getUnpassable() {
		return moveType;
	}

	/**
	 * Checks for typeless attack and general NPCs able to hit through protection prayers.
	 *
	 * @param type the {@link AttackType} used by the npc.
	 *
	 * @return {@code true} if the npc can hit through prayer,
	 * 			{@code false} otherwise.
	 */
	public boolean hitsThroughProtectionPrayer(final AttackType type) {

		if(type == AttackType.MAGIC){
			return id == NpcID.CORPOREAL_BEAST
					|| id == NpcID.KRAKEN;
		}

		if(type == AttackType.MELEE){
			return id == NpcID.GORAK
					|| id == NpcID.DEATH_SPAWN
					|| id == NpcID.GORAK_3141
					|| id == NpcID.ZULRAH_2043
					|| id == NpcID.ABYSSAL_SIRE;
		}

		if(type == AttackType.RANGED){
			return id == NpcID.ANCIENT_WYVERN || id == NpcID.TZKALZUK;
		}

		return id == NpcID.VERAC_THE_DEFILED
				|| id == NpcID.KRIL_TSUTSAROTH;
	}


	class TempNonAttackable {
		public TempNonAttackable(int id, String name, String examine, int walkRadius, int size) {
			this.id = id;
			this.name = name;
			this.examine = examine;
			this.walkRadius = walkRadius;
			this.size = size;
		}

		int id;
		String name, examine;
		int walkRadius, size;
		final boolean attackable = false;
	}

    public Object copy(int newId) {

		if(!attackable){
			return new TempNonAttackable(newId, name, examine, walkRadius, size);
		}

		NpcDefinition npcDefinition = new NpcDefinition();
		npcDefinition.id = newId;
		npcDefinition.name = name;
		npcDefinition.examine = examine;
		npcDefinition.size = size;
		npcDefinition.walkRadius = walkRadius;
		npcDefinition.attackable = attackable;
		npcDefinition.retreats = retreats;
		npcDefinition.aggressive = aggressive;
		npcDefinition.poisonous = poisonous;
		npcDefinition.respawn = respawn;
		npcDefinition.hitpoints = hitpoints;
		npcDefinition.maxHit = maxHit;
		npcDefinition.attackSpeed = attackSpeed;
		npcDefinition.attackAnim = attackAnim;
		npcDefinition.rangeAnim = rangeAnim;
		npcDefinition.magicAnim = magicAnim;
		npcDefinition.rangeHitDelay = rangeHitDelay;
		npcDefinition.rangeStartGfx = rangeStartGfx;
		npcDefinition.rangeProjectile = rangeProjectile;
		npcDefinition.rangeEndGfx = rangeEndGfx;
		npcDefinition.rangeProjectileDelay = rangeProjectileDelay;
		npcDefinition.mageHitDelay = mageHitDelay;
		npcDefinition.mageStartGfx = mageStartGfx;
		npcDefinition.magicProjectile = magicProjectile;
		npcDefinition.magicEndGfx = magicEndGfx;
		npcDefinition.mageProjectileDelay = mageProjectileDelay;
		npcDefinition.deathTime = deathTime;
		npcDefinition.defenceAnim = defenceAnim;
		npcDefinition.deathAnim = deathAnim;
		npcDefinition.attackSound = attackSound;
		npcDefinition.blockSound = blockSound;
		npcDefinition.deathSound = deathSound;
		npcDefinition.combatLevel = combatLevel;
		npcDefinition.stats = stats;
		npcDefinition.slayerLevel = slayerLevel;
		npcDefinition.combatFollowDistance = combatFollowDistance;
		npcDefinition.prefferedDistance = prefferedDistance;
		npcDefinition.collidesWithEntities = collidesWithEntities;
		npcDefinition.moveType = moveType;
		npcDefinition.unpassable = unpassable;
		return npcDefinition;
    }

    public static Map<Integer, NpcDefinition> getDefinitions(){
		return definitions;
	}
	public static Map<String, NpcDefinition> getDefinitionsByName(){
		return nameDefinition;
	}
}
