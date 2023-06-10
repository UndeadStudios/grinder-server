package com.grinder.game.entity.agent.player.equipment;

import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;

public enum EquipmentType {
    HOODED_CAPE(EquipmentConstants.CAPE_SLOT),
    CAPE(EquipmentConstants.CAPE_SLOT),

    SHIELD(EquipmentConstants.SHIELD_SLOT),

    GLOVES(EquipmentConstants.HANDS_SLOT),

    BOOTS(EquipmentConstants.FEET_SLOT),

    AMULET(EquipmentConstants.AMULET_SLOT),

    RING(EquipmentConstants.RING_SLOT),

    ARROWS(EquipmentConstants.AMMUNITION_SLOT),

    COIF(EquipmentConstants.HEAD_SLOT),
    HAT(EquipmentConstants.HEAD_SLOT),
    MASK(EquipmentConstants.HEAD_SLOT),
    MED_HELMET(EquipmentConstants.HEAD_SLOT),
    FULL_HELMET(EquipmentConstants.HEAD_SLOT),

    BODY(EquipmentConstants.BODY_SLOT),//TORSO REMOVAL
    PLATEBODY(EquipmentConstants.BODY_SLOT),

    LEGS(EquipmentConstants.LEG_SLOT),//REMOVES BOTTOM HALF OF BODY TO FEET IF ITEM HAS NO LEG DATA

    WEAPON(EquipmentConstants.WEAPON_SLOT),

    AURA(EquipmentConstants.AURA), // new slot 14
    SIGIL(EquipmentConstants.SIGIL), // new slot 15
    BRACELET(EquipmentConstants.BRACELET), // new slot 16

    NONE(-1);//DEFAULT/NOTHING IN SLOT

    private final int slot;

    private EquipmentType(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }
}
