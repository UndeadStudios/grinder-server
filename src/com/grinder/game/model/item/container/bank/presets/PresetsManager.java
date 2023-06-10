package com.grinder.game.model.item.container.bank.presets;

import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.game.model.MagicSpellbook;
import com.grinder.game.model.interfaces.syntax.impl.ChangeBankPresetName;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.bank.BankConstants;
import com.grinder.game.model.item.container.bank.Banking;
import com.grinder.game.model.item.container.player.Equipment;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.net.packet.impl.EquipPacketListener;

import java.util.ArrayList;

import static com.grinder.game.model.item.container.bank.presets.PresetsConstants.*;

public class PresetsManager {

    public static boolean ClickButton(Player player, int buttonId)
    {
        switch(buttonId)
        {
            case CREATE_BUTTON_ID:
                CreatePreset(player);
                break;

            case OVERRIDE_BUTTON_ID:
                OverridePreset(player, player.selectedPresetId);
                break;

            case BANK_BUTTON_ID:
                player.getBankpin().openBank();
                break;
        }
        return false;
    }

    public static void ShowInterface(Player player)
    {
        player.presetInterfaceOpen = true;
        player.getPacketSender().sendInterface(63500);
        player.getPacketSender().sendSound(73);
        ResetInterface(player);
    }

    private static void ResetInterface(Player player)
    {
        player.getPacketSender().sendString(PRESETS_MADE_TEXT_ID, String.valueOf(player.presetsAmount));

        if (player.presetsAmount < 10) {
            for (int i = player.presetsAmount; i < 10; i++) {
                player.getPacketSender().sendInterfaceDisplayState(PRESET_BUTTON_START_ID + (i * 2), true);

                //if presets less than 3
                if (i >= 2) {
                    player.getPacketSender().sendInterfaceDisplayState(PRESET_DONOR_IMAGE_START + (i - 2), true);
                }
            }
        }

        if (player.presetsAmount > 0)
        {
            for (int i = 0; i < player.presetsAmount; i++) {
                player.getPacketSender().sendInterfaceDisplayState(PRESET_BUTTON_START_ID + (i * 2), false);
                player.getPacketSender().sendString(player.presetNames[i], PRESET_BUTTON_START_ID + (i * 2));

                //if presets less than 3
                if (i >= 2) {
                    player.getPacketSender().sendInterfaceDisplayState(PRESET_DONOR_IMAGE_START + (i - 2), false);
                }
            }
            SelectPreset(player, player.selectedPresetId);
        }
        else
        {
            player.getPacketSender().sendInterfaceDisplayState(63588, true); //Ancient book
            player.getPacketSender().sendInterfaceDisplayState(65118, true); //Normal book
            player.getPacketSender().sendInterfaceDisplayState(65119, true); //Lunar book

            //If no presets made
            player.getPacketSender().sendString("No preset selected", PRESET_TITLE_TEXT_ID);
            player.getPacketSender().sendString("", 63587);
            //Hide items..
        }
    }

    private static void CreatePreset(Player player) {
        if (player.presetsAmount >= 10)
        {
            player.sendMessage("Your presets tab is currently full!");
            return;
        }
        if (!player.presetInterfaceOpen)
        {
            return;
        }

        player.presetNames[player.presetsAmount] = "Preset " + (player.presetsAmount+1);

        player.presetEquipment[player.presetsAmount] = player.getEquipment().copyNoAttributes();
        player.presetInventory[player.presetsAmount] = player.getInventory().copyNoAttributes();

        player.getPacketSender().sendInterfaceDisplayState(PRESET_BUTTON_START_ID + (player.presetsAmount * 2), false);
        player.getPacketSender().sendString(player.presetNames[player.presetsAmount], PRESET_BUTTON_START_ID + (player.presetsAmount * 2));

        if (player.presetsAmount >= 2) {
            player.getPacketSender().sendInterfaceDisplayState(PRESET_DONOR_IMAGE_START + (player.presetsAmount - 2), false);
        }
        player.presetMagicBooks[player.presetsAmount] = player.getSpellbook();

        SelectPreset(player, player.presetsAmount);

        //Set presetsAmount as select preset, also show equipment and inventory ontop of interface
        player.presetsAmount++;
        player.getPacketSender().sendString(PRESETS_MADE_TEXT_ID, String.valueOf(player.presetsAmount));
    }

    private static void OverridePreset(Player player, int presetId)
    {
        if (!player.presetInterfaceOpen || presetId > player.presetsAmount)
        {
            return;
        }
        if (player.presetsAmount == 0) {
            player.sendMessage("You do not have a selected preset to override.");
            return;
        }
        player.presetEquipment[presetId] = player.getEquipment().copyNoAttributes();
        player.presetInventory[presetId] = player.getInventory().copyNoAttributes();
        player.presetMagicBooks[presetId] = player.getSpellbook();
        player.sendMessage("Your preset has been overriding with new settings.");
        SelectPreset(player, presetId);
    }

    public static boolean InterfaceButtonClick(Player player, int interfaceId, int actionId)
    {
        if (interfaceId == 50112 && actionId == 0)
        {
            ShowInterface(player);
            return true;
        }
        if (!player.presetInterfaceOpen)
        {
            return false;
        }
        int presetId = -1;

        for (int i = 0; i < 20; i += 2)
        {
            if (interfaceId - i == 63517)
            {
                presetId = i/2;
                break;
            }
        }
        if (presetId != -1) {
            switch (actionId) {
                case 0:
                    SelectPreset(player, presetId);
                    return true;
                case 1:
                    LoadPreset(player, presetId);
                    return true;
                case 2:
                    DeletePreset(player, presetId);
                    return true;
                case 3:
                    EditNamePreset(player, presetId);
                    return true;
            }
        }
        return false;
    }

    private static void SelectPreset(Player player, int presetId)
    {
        player.getPacketSender().sendString(player.presetNames[presetId], PRESET_TITLE_TEXT_ID);

        DisplayEquipment(player, presetId);
        DisplayInventory(player, presetId);
        player.selectedPresetId = presetId;

        player.getPacketSender().sendInterfaceDisplayState(63588, true); //Ancient book
        player.getPacketSender().sendInterfaceDisplayState(65118, true); //Normal book
        player.getPacketSender().sendInterfaceDisplayState(65119, true); //Lunar book
        player.getPacketSender().sendString("Spellbook:", 63587);

        switch (player.presetMagicBooks[presetId])
        {
            case NORMAL:
                player.getPacketSender().sendInterfaceDisplayState(65118, false); //Normal book
                break;

            case LUNAR:
                player.getPacketSender().sendInterfaceDisplayState(65119, false); //Lunar book
                break;

            case ARCEEUS:
            case ANCIENT:
                player.getPacketSender().sendInterfaceDisplayState(63588, false); //Ancient book
                break;
        }
    }

    private static void LoadPreset(Player player, int presetId)
    {
        switch (presetId)
        {
            case 2:
                if (!player.getRights().isHighStaff() && !PlayerUtil.isBronzeMember(player))
                {
                    player.sendMessage("<col=0040ff><img=1025> This preset is only available to Bronze members or higher.</col>");
                    return;
                }
                break;
            case 3:
                if (!player.getRights().isHighStaff() && !PlayerUtil.isRubyMember(player)) {
                    player.sendMessage("<col=0040ff><img=745> This preset is only available to Ruby members or higher.</col>");
                    return;
                }
                break;
            case 4:
                if (!player.getRights().isHighStaff() && !PlayerUtil.isTopazMember(player)) {
                    player.sendMessage("<col=0040ff><img=746> This preset is only available to Topaz members or higher.</col>");
                    return;
                }
                break;
            case 5:
                if (!player.getRights().isHighStaff() && !PlayerUtil.isAmethystMember(player)) {
                    player.sendMessage("<col=0040ff><img=747> This preset is only available to Amethyst members or higher.</col>");
                    return;
                }
                break;
            case 6:
                if (!player.getRights().isHighStaff() && !PlayerUtil.isLegendaryMember(player)) {
                    player.sendMessage("<col=0040ff><img=1026> This preset is only available to Legendary members or higher.</col>");
                    return;
                }
                break;
            case 7:
                if (!player.getRights().isHighStaff() && !PlayerUtil.isPlatinumMember(player)) {
                    player.sendMessage("<col=0040ff><img=1027> This preset is only available to Platinum members or higher.</col>");
                    return;
                }
                break;
            case 8:
                if (!player.getRights().isHighStaff() && !PlayerUtil.isTitaniumMember(player)) {
                    player.sendMessage("<col=0040ff><img=1227> This preset is only available to Titanium members or higher.</col>");
                    return;
                }
                break;
            case 9:
                if (!player.getRights().isHighStaff() && !PlayerUtil.isDiamondMember(player)) {
                    player.sendMessage("<col=0040ff><img=1228> This preset is only available to Diamond members.</col>");
                    return;
                }
                break;
        }

        if (!player.getInventory().isEmpty()) {
            player.sendMessage("You must bank all of your items and equipment before using the presets.");
            return;
        }
        if (!player.getEquipment().isEmpty()) {
            player.sendMessage("You must bank all of your items and equipment before using the presets.");
            return;
        }

        //Load preset
        Banking.depositItems(player, player.getInventory(), true);
        Banking.depositItems(player, player.getEquipment(), true);

        //Inventory
        for (int i = 0; i < player.presetInventory[presetId].getItems().length; i++)
        {
            Item presetItem = player.presetInventory[presetId].getItems()[i];
            boolean foundItem = false;
            //Check if has item in bank.. then withdraw if does - else show message
            for (int tab = BankConstants.TOTAL_BANK_TABS - 1; tab >= 0; tab--)
            {
                ArrayList<Item> items = player.getBank(tab).getValidItems();

                for (Item item : items) {
                    if (presetItem.getId() == item.getId())
                    {
                        int amount = presetItem.getAmount();


                        if (item.getAmount() > 0)
                        {
                            foundItem = true;
                        }

                        if (item.getAmount() < amount)
                        {
                            amount = item.getAmount();
                        }
                        if (item.getAmount() <= 0 || amount <= 0) {
                            item.setId(-1);
                        }
                        Item newItem = new Item(item.getId(), amount);
                        player.getInventory().setItem(i, newItem);
                        if (item.getAmount() == amount && player.hasPlaceHoldersEnabled()) {
                            player.getBank(tab).replace(item, item.setAmount(0));
                        } else {
                            player.getBank(tab).delete(item.getId(), amount);
                        }
                    }
                }
            }
            if (!foundItem && presetItem.getId() != -1) {
                player.sendMessage("@dre@Item could not be found: " + ItemDefinition.forId(presetItem.getId()).getName());
            }
        }

        player.getInventory().refreshItems();

        for (int i = 0; i < player.presetEquipment[presetId].getItems().length; i++)
        {
            Item presetItem = player.presetEquipment[presetId].getItems()[i];
            boolean foundItem = false;
            //Check if has item in bank.. then withdraw if does - else show message
            for (int tab = BankConstants.TOTAL_BANK_TABS - 1; tab >= 0; tab--)
            {
                ArrayList<Item> items = player.getBank(tab).getValidItems();

                for (Item item : items) {
                    if (presetItem.getId() == item.getId())
                    {
                        int amount = presetItem.getAmount();

                        if (item.getAmount() > 0)
                        {
                            foundItem = true;
                        }

                        if (item.getAmount() < amount)
                        {
                            amount = item.getAmount();
                        }
                        if (item.getAmount() <= 0 || amount <= 0) {
                            item.setId(-1);
                        }
                        Item newItem = new Item(item.getId(), amount);
                        player.getEquipment().set(i, newItem);
                        if (item.getAmount() == amount && player.hasPlaceHoldersEnabled()) {
                            player.getBank(tab).replace(item, item.setAmount(0));
                        } else {
                            player.getBank(tab).delete(item.getId(), amount);
                        }
                    }
                }
            }
            if (!foundItem && presetItem.getId() != -1) {
                player.sendMessage("@dre@Item could not be found: " + ItemDefinition.forId(presetItem.getId()).getName());
            }
        }

        //player.setSpellbook(player.presetMagicBooks[presetId]);
        MagicSpellbook.changeSpellbook(player, player.presetMagicBooks[presetId]);
        player.getEquipment().refreshItems();
        EquipPacketListener.resetWeapon(player);
        player.getCombat().reset(false);
        EquipmentBonuses.update(player);
        player.getEquipment().refreshItems();
        WeaponInterfaces.INSTANCE.assign(player);
        player.updateAppearance();
        player.getPacketSender().sendInterfaceRemoval();
        player.sendMessage("Your selected preset has been loaded!");
        player.getPacketSender().sendSound(59);
    }

    private static void DeletePreset(Player player, int presetId)
    {
        for (int i = presetId; i < 9; i++)
        {
            player.presetEquipment[i] = player.presetEquipment[i+1];
            player.presetInventory[i] = player.presetInventory[i+1];
            player.presetNames[i] = player.presetNames[i+1];
        }
        player.presetEquipment[9] = new Equipment(player);
        player.presetInventory[9] = new Inventory(player);

        player.presetsAmount--;

        if (player.selectedPresetId == presetId)
        {
            if (player.selectedPresetId > 0) {
                player.selectedPresetId--;
                SelectPreset(player, player.selectedPresetId);
            }
            else
            {
                SelectPreset(player, 0);
            }
        }

        ResetInterface(player);
    }

    private static void EditNamePreset(Player player, int presetId)
    {
        player.setEnterSyntax(new ChangeBankPresetName(presetId));
        player.getPacketSender().sendEnterInputPrompt("Enter the name of the preset:");
    }

    private static void DisplayEquipment(Player player, int presetId)
    {
        Equipment equipment = player.presetEquipment[presetId];

        Item head = equipment.getItems()[0];
        Item cape = equipment.getItems()[1];
        Item amulet = equipment.getItems()[2];
        Item weapon = equipment.getItems()[3];
        Item platebody = equipment.getItems()[4];
        Item shield = equipment.getItems()[5];
        Item platelegs = equipment.getItems()[7];
        Item gloves = equipment.getItems()[9];
        Item boots = equipment.getItems()[10];
        Item ring = equipment.getItems()[12];
        Item arrows = equipment.getItems()[13];

        player.getPacketSender().sendItemOnInterface(63556, head.getId(), head.getAmount());
        player.getPacketSender().sendItemOnInterface(63565, cape.getId(), cape.getAmount());
        player.getPacketSender().sendItemOnInterface(63559, amulet.getId(), amulet.getAmount());
        player.getPacketSender().sendItemOnInterface(63571, weapon.getId(), weapon.getAmount());
        player.getPacketSender().sendItemOnInterface(63568, platebody.getId(), platebody.getAmount());
        player.getPacketSender().sendItemOnInterface(63574, shield.getId(), shield.getAmount());
        player.getPacketSender().sendItemOnInterface(63577, platelegs.getId(), platelegs.getAmount());
        player.getPacketSender().sendItemOnInterface(63586, gloves.getId(), gloves.getAmount());
        player.getPacketSender().sendItemOnInterface(63580, boots.getId(), boots.getAmount());
        player.getPacketSender().sendItemOnInterface(63583, ring.getId(), ring.getAmount());
        player.getPacketSender().sendItemOnInterface(63562, arrows.getId(), arrows.getAmount());
    }

    private static void DisplayInventory(Player player, int presetId)
    {
        Inventory inventory = player.presetInventory[presetId];

        player.getPacketSender().sendItemOnInterface(63592, inventory.getItems()[0].getId(), inventory.getItems()[0].getAmount());
        player.getPacketSender().sendItemOnInterface(63593, inventory.getItems()[1].getId(), inventory.getItems()[1].getAmount());
        player.getPacketSender().sendItemOnInterface(63594, inventory.getItems()[2].getId(), inventory.getItems()[2].getAmount());
        player.getPacketSender().sendItemOnInterface(63595, inventory.getItems()[3].getId(), inventory.getItems()[3].getAmount());
        player.getPacketSender().sendItemOnInterface(63597, inventory.getItems()[4].getId(), inventory.getItems()[4].getAmount());
        player.getPacketSender().sendItemOnInterface(63598, inventory.getItems()[5].getId(), inventory.getItems()[5].getAmount());
        player.getPacketSender().sendItemOnInterface(63599, inventory.getItems()[6].getId(), inventory.getItems()[6].getAmount());
        player.getPacketSender().sendItemOnInterface(63600, inventory.getItems()[7].getId(), inventory.getItems()[7].getAmount());
        player.getPacketSender().sendItemOnInterface(63602, inventory.getItems()[8].getId(), inventory.getItems()[8].getAmount());
        player.getPacketSender().sendItemOnInterface(63603, inventory.getItems()[9].getId(), inventory.getItems()[9].getAmount());
        player.getPacketSender().sendItemOnInterface(63604, inventory.getItems()[10].getId(), inventory.getItems()[10].getAmount());
        player.getPacketSender().sendItemOnInterface(63605, inventory.getItems()[11].getId(), inventory.getItems()[11].getAmount());
        player.getPacketSender().sendItemOnInterface(63607, inventory.getItems()[12].getId(), inventory.getItems()[12].getAmount());
        player.getPacketSender().sendItemOnInterface(63608, inventory.getItems()[13].getId(), inventory.getItems()[13].getAmount());
        player.getPacketSender().sendItemOnInterface(63609, inventory.getItems()[14].getId(), inventory.getItems()[14].getAmount());
        player.getPacketSender().sendItemOnInterface(63610, inventory.getItems()[15].getId(), inventory.getItems()[15].getAmount());
        player.getPacketSender().sendItemOnInterface(63612, inventory.getItems()[16].getId(), inventory.getItems()[16].getAmount());
        player.getPacketSender().sendItemOnInterface(63613, inventory.getItems()[17].getId(), inventory.getItems()[17].getAmount());
        player.getPacketSender().sendItemOnInterface(63614, inventory.getItems()[18].getId(), inventory.getItems()[18].getAmount());
        player.getPacketSender().sendItemOnInterface(63615, inventory.getItems()[19].getId(), inventory.getItems()[19].getAmount());
        player.getPacketSender().sendItemOnInterface(63617, inventory.getItems()[20].getId(), inventory.getItems()[20].getAmount());
        player.getPacketSender().sendItemOnInterface(63618, inventory.getItems()[21].getId(), inventory.getItems()[21].getAmount());
        player.getPacketSender().sendItemOnInterface(63619, inventory.getItems()[22].getId(), inventory.getItems()[22].getAmount());
        player.getPacketSender().sendItemOnInterface(63620, inventory.getItems()[23].getId(), inventory.getItems()[23].getAmount());
        player.getPacketSender().sendItemOnInterface(63622, inventory.getItems()[24].getId(), inventory.getItems()[24].getAmount());
        player.getPacketSender().sendItemOnInterface(63623, inventory.getItems()[25].getId(), inventory.getItems()[25].getAmount());
        player.getPacketSender().sendItemOnInterface(63624, inventory.getItems()[26].getId(), inventory.getItems()[26].getAmount());
        player.getPacketSender().sendItemOnInterface(63625, inventory.getItems()[27].getId(), inventory.getItems()[27].getAmount());
    }

}
