package com.grinder.game.content.skill.skillable.impl.magic;

import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpell;
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpellType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.MagicSpellbook;
import com.grinder.game.model.Skill;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.util.ItemID;

import java.util.Optional;

public class SpellCasting {

    public static final int MAGIC_SPLASH_HIT_EXPERIENCE = 15;


    
    public static boolean toggleAutomaticCasting(final Player player, int childId) {

        final Optional<CombatSpellType> combatSpellTypeOptional = CombatSpellType.getCombatSpells(childId);


        if (combatSpellTypeOptional.isEmpty())
            return false;

        final CombatSpellType spellType = combatSpellTypeOptional.get();
        final CombatSpell selectedSpell = CombatSpellType.getCombatSpell(childId);

        if (selectedSpell.levelRequired() > player.getSkillManager().getCurrentLevel(Skill.MAGIC)) {
            player.getPacketSender().sendMessage("You need a Magic level of at least " + selectedSpell.levelRequired() + " to cast this spell.");
            setSpellToCastAutomatically(player, null);
            return true;
        }

        Item weapon = player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT];

		//Check if player is using a trident. If so, do not allow player to change autocast spell.
		if(weapon.getId() == ItemID.TRIDENT_OF_THE_SEAS_FULL_ || weapon.getId() == ItemID.TRIDENT_OF_THE_SEAS || weapon.getId() == ItemID.TRIDENT_OF_THE_SWAMP || weapon.getId() == ItemID.UNCHARGED_TRIDENT
        || weapon.getId() == ItemID.UNCHARGED_TOXIC_TRIDENT || weapon.getId() == 22290 || weapon.getId() == 22292 || weapon.getId() == 22294) {
			player.getPacketSender().sendMessage("You can't change your autocast spell whilst wearing a trident.");
			return false;
		}

		if (weapon.getId() != ItemID.AHRIMS_STAFF && weapon.getId() != ItemID.KODAI_WAND && weapon.getId() != 15021 && weapon.getId() != ItemID.MASTER_WAND
                && weapon.getId() != ItemID._3RD_AGE_WAND && weapon.getId() != ItemID.STAFF_OF_LIGHT && weapon.getId() != ItemID.ANCIENT_STAFF
                && weapon.getId() != ItemID.AHRIMS_STAFF_100 && weapon.getId() != ItemID.AHRIMS_STAFF_75 && weapon.getId() != ItemID.AHRIMS_STAFF_50 && weapon.getId() != ItemID.AHRIMS_STAFF_25
                && weapon.getId() != 15859
                && weapon.getId() != ItemID.NIGHTMARE_STAFF && weapon.getId() != ItemID.VOLATILE_NIGHTMARE_STAFF && weapon.getId() != ItemID.ELDRITCH_NIGHTMARE_STAFF
                && player.getSpellbook().equals(MagicSpellbook.ANCIENT)) {
			player.getPacketSender().sendMessage("You can only autocast ancient spells with an Ancient, Kodai, Nightmare, or Ahrim's staff!");
			return false;
		}
		
		if (weapon.getId() == ItemID.ANCIENT_STAFF && player.getSpellbook().equals(MagicSpellbook.NORMAL)) {
			player.getPacketSender().sendMessage("You cannot use this staff to autocast regular spells.");
			return false;
		}

		if (weapon.getId() != ItemID.STAFF_OF_LIGHT && weapon.getId() != ItemID.SLAYERS_STAFF_E_ && weapon.getId() != ItemID.TOXIC_STAFF_OF_THE_DEAD && weapon.getId() != ItemID.SLAYERS_STAFF && weapon.getId() != ItemID.TOXIC_STAFF_UNCHARGED_
                && weapon.getId() != ItemID.STAFF_OF_THE_DEAD && weapon.getId() != ItemID.VOID_KNIGHT_MACE && childId == 1171) { // Crumble Undead spell
			player.sendMessage("This spell can't be autocasted except with a Slayer's Staff, Staff of the dead, Staff of light or Void knight mace.");
			return false;
		}
		
		if (weapon.getId() != ItemID.IBANS_STAFF && weapon.getId() != ItemID.IBANS_STAFF_U_ && childId == 1539) { // Iban's blast spell
			player.sendMessage("This spell can't be autocasted except with Iban's staff.");
			return false;
		}

        if (player.getCombat().getAutocastSpell() != null && player.getCombat().getAutocastSpell() == selectedSpell)
            setSpellToCastAutomatically(player, null);
        else
            setSpellToCastAutomatically(player, spellType);

        return true;
    }

    public static void setSpellToCastAutomatically(Player player, CombatSpellType spellType) {

        final CombatSpell spell;

        if(spellType == null){
            player.getPacketSender().sendAutocastId(-1);
            player.getPacketSender().sendConfig(108, 3);
            spell = null;
        } else {
            spell = spellType.getSpell();
            player.getPacketSender().sendAutocastId(spell.spellId());
            player.getPacketSender().sendConfig(108, 1);
        }

        player.getCombat().setAutocastSpell(spell);
        player.getCombat().setCastSpellType(spellType);
        EquipmentBonuses.update(player);
    }
}
