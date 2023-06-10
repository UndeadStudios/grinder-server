package com.grinder.util.tools;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.google.gson.Gson;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.util.DefaultInteger;
import com.grinder.util.JinDefault;

public class GrinderNPCDefinitionDumper {
	
	public static HashMap<Integer, JinrakesDefinition> definitions = new HashMap<Integer, JinrakesDefinition>();
	
	public static void loaddump() throws IOException {
        FileReader reader = new FileReader(new File("./data/GrinderDefs.json"));
        JinrakesDefinition[] defs = new Gson().fromJson(reader, JinrakesDefinition[].class);
        for (JinrakesDefinition def : defs) {
            definitions.put(def.id, def);
        }
        
        reader.close();
	}
	
	/**
	 * rewrite original data file
	 * @param args
	 * @throws IOException
	 */
	
	public static void main(String[] args) throws IOException {
		loaddump();
		
	}
	
	public enum Weakness {
		
		NONE("None"),
		CRUSH("Crush"),
		SLASH("Slash"),
		STAB("Stab"),
		ARROWS("Arrows"),
		BOLTS("Bolts"),
		THROWN("Thrown"),
		AIR_SPELLS("Air"),
		WATER_SPELLS("Water"),
		EARTH_SPELLS("Earth"),
		FIRE_SPELLS("Fire"),
		MAGIC("Magic"),
		RANGED("Ranged");

		private final String type;

		private Weakness(String type) {
			this.type = type;
		}

		@Override
		public String toString() {
			return type;
		}
	}

	
	/**
	 * Dumping all data and fields from old Grinder defintions
	 * to be rewritten into new def file
	 * @author j
	 *
	 */
	
	
	class JinrakesDefinition extends JinDefault {

		/**
		 * NPC Type
		 */
		public int id;
		/**
		 * Name of the npc.
		 */
		public String name;

		/**
		 * Description of the NPC.
		 */
		public String npcDescription;

		/**
		 * Max enemy level to become aggressive.
		 */
		@DefaultInteger(0)
		public int agressivityDistance = 0;

		@DefaultInteger(20)
		public int aggressivityTime = 20;

		@DefaultInteger(1)
		public int attackRange = 1;
		
		@DefaultInteger(4)
		public int attackDelay = 4;
		
		@DefaultInteger(-1)
		public int preferredDistance = -1;
		
		@DefaultInteger(8)
		public int followRange = 8;
		
		/**
		 * Hit through enemy prayer's defence.
		 */
		public float prayerPenetration = 0;
		
		/** Absorb damage percent **/

		public float prayerAbsorption = 100;
		/**
		 * Attack weakness type.
		 * TODO
		 */
		public Weakness weakness = Weakness.NONE;
		/**
		 * NPC Poison damage
		 */
		@DefaultInteger(0)
		public int poisonDamage = 0;

		/**
		 * Melee attack.
		 */
		@DefaultInteger(1)
		public int meleeAttack = 1;
		/**
		 * Melee defence.
		 */
		@DefaultInteger(1)
		public int meleeDefence = 1;

		/**
		 * Ranged attack.
		 */
		@DefaultInteger(1)
		public int rangedAttack = 1;
		/**
		 * Range defence.
		 */
		@DefaultInteger(1)
		public int rangedDefence = 1;
		/**
		 * Mage attack.
		 */
		@DefaultInteger(1)
		public int mageAttack = 1;
		/**
		 * Mage defence.
		 */
		@DefaultInteger(1)
		public int mageDefence = 1;

		/**
		 * Max Life.
		 */
		@DefaultInteger(0)
		public int health = 0;

		/**
		 * Max Hit.
		 */
		@DefaultInteger(0)
		public int meleeMaxHit = 0;
		
		@DefaultInteger(0)
		public int rangeMaxHit = 0;
		
		@DefaultInteger(0)
		public int mageMaxHit = 0;

		/**
		 * NPC Combat.
		 */
		@DefaultInteger(0)
		public int combat = 0;

		/**
		 * NPC Size.
		 */
		@DefaultInteger(1)
		public int size = 1;

		/**
		 * NPC Attack emote.
		 */
		@DefaultInteger(-1)
		public int attackEmote = -1;
		/**
		 * NPC Attack sound.
		 */
		@DefaultInteger(-1)
		public int attackSound = -1;

		/**
		 * NPC Block emote.
		 */
		@DefaultInteger(-1)
		public int blockEmote = -1;
		/**
		 * NPC Block sound.
		 */
		@DefaultInteger(-1)
		public int blockSound = -1;

		/**
		 * NPC Death emote.
		 */
		@DefaultInteger(-1)
		public int deathEmote = -1;
		/**
		 * NPC Death sound.
		 */
		@DefaultInteger(-1)
		public int deathSound = -1;
		/**
		 * Slayer XP rewarded for task kill.
		 */
		@DefaultInteger(0)
		public int slayerXP = 0;

		/**
		 * Slayer level requirement to attack this monster.
		 */
		@DefaultInteger(0)
		public int slayerLevel = 0;

		/**
		 * Define respawn time. time = ticks * 600ms
		 */
		@DefaultInteger(25)
		public int respawnTicks = 25;
		
		public int targetSwitchDelay = 0;
		
		public boolean blockEntityFacing;

		@DefaultInteger(0)
		public int maxCombatAggression;
		
		
		public BonusType attackType;
		public ImmunityType immunity;
		public Graphic attackGraphic;
		public Graphic attackSplashGraphic;
		public Projectile attackProjectile;
		
		public JinrakesDefinition() {

		}

		public JinrakesDefinition(final int id) {
			this.id = id;
			setDefault();
		}

		public void loadDefaultValues() {
			meleeMaxHit = combat / 10;
			meleeAttack = (int) (combat * 1.5);
			meleeDefence = (int) (combat * 0.80);
			rangedAttack = (int) (combat * 2.5);
			rangedDefence = (int) (combat * 1.2);
			mageAttack = combat * 2;
			mageDefence = (int) (combat * 1.5);
		}

		public void loadDefaultValues2() {
			meleeMaxHit = combat / 8;
			meleeAttack = (combat * 2);
			meleeDefence = (int) (combat * 1.2);
			rangedAttack = (int) (combat * 2.5);
			rangedDefence = (int) (combat * 1.15);
			mageAttack = combat * 2;
			mageDefence = (int) (combat * 1.5);
		}	

		private void setDefault() {
			this.immunity = ImmunityType.NONE;
			this.attackType = BonusType.ATTACK_CRUSH;
			this.attackDelay = 4;
			this.targetSwitchDelay = 0;
			this.agressivityDistance = 0;
			this.aggressivityTime = 20;
			this.attackRange = 1;
			this.prayerAbsorption = 0;
			this.prayerPenetration = 0;
			this.weakness = Weakness.NONE;
			this.poisonDamage = 0;

			this.meleeAttack = 1;
			this.meleeDefence = 1;

			this.rangedAttack = 1;
			this.rangedDefence = 1;

			this.mageAttack = 1;
			this.mageDefence = 1;

			this.health = 0;
			this.meleeMaxHit = 0;

			this.combat = 0;
			this.size = 1;
			this.name = null;
			this.npcDescription = null;

			this.attackEmote = -1;
			this.attackSound = -1;

			this.blockEmote = -1;
			this.blockSound = -1;

			this.deathEmote = -1;
			this.deathSound = -1;

			this.slayerXP = 0;
			this.slayerLevel = 0;

			this.respawnTicks = 25;
			
			this.preferredDistance = -1;

		}

		@Override
		public String toString() {
			return name;
		}

		public String getName() {
			return name;
		}
	}
	
	public static enum ImmunityType {
		NONE, POISON, RETALIATE, BOLT_ENCHANT, DREAD_NIP ;
	}
	
	public enum BonusType {
		ATTACK_STAB(0), ATTACK_SLASH(1), ATTACK_CRUSH(2), ATTACK_MAGIC(3), ATTACK_RANGE(4),
		DEFENCE_STAB(5), DEFENCE_SLASH(6), DEFENCE_CRUSH(7), DEFENCE_MAGIC(8), DEFENCE_RANGE(9),
		MELEE_ABSORB(10), MAGE_ABSORB(11), RANGE_ABSORB(12),
		STRENGTH_BONUS(13), RANGED_STRENGTH(14), PRAYER_BONUS(15), MAGIC_DAMAGE(16);

		private final int bonusID;

		private BonusType(int bonusID) {
			this.bonusID = bonusID;
		}

		public int getBonusID() {
			return bonusID;
		}
		
		public BonusType getCounterBonus() {
			return forID(bonusID + 5);
		}

		public static BonusType forID(int bonus) {
			for(BonusType bonusType : values()) {
				if(bonusType.bonusID == bonus){
					return bonusType;
				}
			}
			return null;
		}

		public static BonusType forSlot(int slot) {
			return null;
		}
	}
}