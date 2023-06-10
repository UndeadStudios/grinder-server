package com.grinder.game.definition;

import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface;
import com.grinder.game.entity.agent.player.equipment.EquipmentType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents the definition of an item.
 *
 * @author Professor Oak
 */
public class ItemDefinition {

    /**
     * The map containing all our {@link ItemDefinition}s.
     */
    public static final Map<Integer, ItemDefinition> definitions = new HashMap<Integer, ItemDefinition>();
    public static final Set<String> definitionNames = new HashSet<>();

    /**
     * The default {@link ItemDefinition} that will be used.
     */
    public static final ItemDefinition DEFAULT = new ItemDefinition();
    private int id;
    private String name = "";
    private String examine = "";
    private WeaponInterface weaponInterface;
    private EquipmentType equipmentType = EquipmentType.NONE;
    private boolean doubleHanded;
    private boolean stackable;
    private boolean tradeable;
    private boolean dropable;
    private boolean sellable;
    private boolean noted;
    private int value;
    private int highAlch;
    private int lowAlch;
    private int dropValue;
    private int noteId = -1;
    private int blockAnim = 424;
    private int standAnim = 808;
    private int walkAnim = 819;
    private int runAnim = 824;
    private int standTurnAnim = 823;
    private int turn180Anim = 820;
    private int turn90CWAnim = 821;
    private int turn90CCWAnim = 821;
    private double weight;
    private double[] bonuses;
    private int[] requirements;

    /**
     * Determines whether this item is a part of
     * the collection log
     */
    private boolean isCollectable;

    /**
     * Attempts to get the {@link ItemDefinition} for the
     * given item.
     *
     * @param item
     * @return
     */
    public static ItemDefinition forId(int item) {
        return definitions.getOrDefault(item, DEFAULT);
    }
    public static ItemDefinition forName(String item) {
        for(ItemDefinition definition : definitions.values()){
            if(definition == null)
                continue;
            if(definition.name.equals(item))
                return definition;
        }
        return null;
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

    /**
     * @deprecated Please use {@link ItemValueDefinition#getItems_value()}
     */
    @Deprecated
    public int getValue() {
        return value;
    }
    /**
     * @deprecated Please use {@link ItemValueDefinition#getHigh_alch_value()}
     */
    @Deprecated
    public int getHighAlchValue() {
        return highAlch;
    }
    /**
     * @deprecated Please use {@link ItemValueDefinition#getLow_alch_value()} ()}
     */
    @Deprecated
    public int getLowAlchValue() {
        return lowAlch;
    }

    /**
     * This is not used anywhere!
     * TODO: remove!
     */
    @Deprecated
    public int getDropValue() {
        return dropValue;
    }

    public boolean isStackable() {
        return stackable;
    }

    public boolean isTradeable() {
        return tradeable;
    }

    public boolean isSellable() {
        return sellable;
    }

    public boolean isDropable() {
        return dropable;
    }

    public boolean isNoted() {
        return noted;
    }

    public int getNoteId() {
        return noteId;
    }

    public boolean isDoubleHanded() {
        return doubleHanded;
    }

    public int getBlockAnim() {
        return blockAnim;
    }

    public int getStandAnim() {
        return standAnim;
    }

    public int getWalkAnim() {
        return walkAnim;
    }

    public int getRunAnim() {
        return runAnim;
    }

    public int getStandTurnAnim() {
        return standTurnAnim;
    }

    public int getTurn180Anim() {
        return turn180Anim;
    }

    public int getTurn90CWAnim() {
        return turn90CWAnim;
    }

    public int getTurn90CCWAnim() {
        return turn90CCWAnim;
    }

    public double getWeight() {
        return weight;
    }

    public double[] getBonuses() {
        return bonuses;
    }

    public int[] getRequirements() {
        return requirements;
    }

    public WeaponInterface getWeaponInterface() {
        return weaponInterface;
    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public boolean isCollectable() {
        return isCollectable;
    }

    public void setCollectable(boolean collectable) {
        isCollectable = collectable;
    }


    /**
     * Attempts to get the unnoted version of the given item.
     *
     * @param id
     * @return
     */
    public int unNote() {
    	if (this.getNoteId() != -1) {
			return ItemDefinition.forId(this.getNoteId()).getId();
		}
    	
        return ItemDefinition.forId(id - 1).getName().equals(name) ? id - 1 : id;
    }

    public static String getName(int id) {
        ItemDefinition def = forId(id);

        if(def==null) {
            return "No definition";
        }

        return def.getName();
    }

}
