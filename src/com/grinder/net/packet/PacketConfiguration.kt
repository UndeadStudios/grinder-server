package com.grinder.net.packet

import com.grinder.game.message.Message
import com.grinder.game.message.MessageDecoder
import com.grinder.game.message.MessageEncoder
import com.grinder.game.message.decoder.*
import com.grinder.game.message.encoder.*
import com.grinder.game.message.impl.*
import com.grinder.net.packet.impl.*

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class PacketConfiguration {

    val lengths = IntArray(255)
    val outgoingPacketTypes = arrayOfNulls<PacketType>(255)
    val listeners = arrayOfNulls<PacketListener>(257)
    val decoders = arrayOfNulls<MessageDecoder<*>>(257)

    /**
     * The map of message classes to message encoders.
     */
    private val encoders: HashMap<Class<out Message>, MessageEncoder<*>> = HashMap()

    // removed new client->server packet opcodes from this array in order
    // for the packet to function
    private val unusedOpcodes = intArrayOf(
            0, 1, 3, 5, 7, 8, 9, 10, 12,
            13, 15, 19, 20, 22, 23, 24,
            25, 26, 27, 28, 29, 30, 31,
            32, 33, 34, 36, 38,
            44, 45, 46, 47, 48, 49, 50,
            51, 52, 54, 55, 56, 58,
            61, 62, 63, 64, 65, 66, 67,
            68, 69, 71, 76, 77, 78, 79,
            80, 81, 82, 83, 84, 85, 86,
            88, 89, 90, 91, 92, 93, 94,
            96, 97, 99, 101, 102,
            105, 106, 107, 108, 110, 111,
            112, 113, 114, 115, 116, 118,
            119, 120, 123, 124, 125, 127,
            134, 136, 137, 140, 143, 144,
            146, 147, 148, 149, 150, 151,
            152, 154, 156, 157, 158, 159,
            160, 161, 162, 165, 166,
            167, 168, 169, 170, 171, 172,
            173, 174, 175, 176, 177, 178,
            179, 180, 187, 190, 191, 193,
            194, 195, 196, 197, 198, 199,
            201, 203, 204, 205, 206, 207,
            209, 211, 217, 218, 219, 221,
            222, 223, 224, 225, 226, 230,
            231, 232, 233, 238, 239, 240,
            241, 242, 243, 244, 245, 246,
            253, 254, 255, 256)


    init {

        /*
        Here you must set the length of the packet:
        - in the case of a fixed size packet this equals the amount of bytes
        - in the case of a variable sized packet this
          must be set to VARIABLE_SHORT or VARIABLE_BYTE (depending on the max length of the packet)
         */
        lengths[35] = VARIABLE_BYTE
        lengths[250] = VARIABLE_SHORT
        lengths[188] = 8
        lengths[133] = 8
        lengths[72] = 2
        lengths[216] = 8
        lengths[0] = 0
        lengths[11] = 13
        lengths[4] = VARIABLE_BYTE
        lengths[95] = 6
        lengths[128] = 2
        lengths[139] = 2
        lengths[39] = 2
        lengths[78] = 0
        lengths[185] = 4
        lengths[186] = 5
        lengths[183] = 2
        lengths[3] = 1
        lengths[130] = 0
        lengths[103] = VARIABLE_BYTE
        lengths[215] = 8
        lengths[74] = 8
        lengths[87] = 6
        lengths[208] = 4
        lengths[41] = 6
        lengths[2] = 4
        lengths[6] = 2
        lengths[121] = 0
        lengths[73] = 2
        lengths[145] = 8
        lengths[117] = 8
        lengths[43] = 8
        lengths[129] = 8
        lengths[135] = 8
        lengths[138] = 8
        lengths[141] = 8
        lengths[142] = 8
        lengths[109] = 12
        lengths[53] = 12
        lengths[57] = 8
        lengths[192] = 12
        lengths[14] = 8
        lengths[75] = 6
        lengths[16] = 6
        lengths[181] = 8
        lengths[237] = 8
        lengths[131] = 4
        lengths[249] = 4
        lengths[40] = 2
        lengths[155] = 2
        lengths[17] = 2
        lengths[21] = 2
        lengths[18] = 2
        lengths[132] = 8
        lengths[252] = 8
        lengths[70] = 8
        lengths[234] = 8
        lengths[228] = 8
        lengths[236] = 6
        lengths[153] = 2
        lengths[202] = 0
        lengths[126] = VARIABLE_SHORT
        lengths[220] = 0
        lengths[189] = VARIABLE_BYTE
        lengths[210] = 4
        lengths[235] = 6
        lengths[247] = VARIABLE_SHORT
        lengths[60] = VARIABLE_BYTE
        lengths[200] = 0
        lengths[187] = 6
        lengths[184] = 4
        lengths[212] = 8
        lengths[213] = 12
        lengths[214] = 9
        lengths[229] = 1
        lengths[122] = 6
        lengths[182] = VARIABLE_SHORT
        lengths[98] = VARIABLE_BYTE
        lengths[164] = VARIABLE_BYTE
        lengths[248] = VARIABLE_BYTE
        lengths[251] = VARIABLE_SHORT

        for (i in listeners.indices) {
            listeners[i] = UnusedPacketListener()
        }

        listeners[PacketConstants.ADD_BROADCAST] = AddBroadcastPacketListener()
        listeners[PacketConstants.RECOLOR_ITEM_OPCODE] = RecolorItemPacketListener()
        listeners[PacketConstants.YELL_EDIT_OPCODE] = YellEditPacketListener()
        listeners[PacketConstants.TELEPORT_OPCODE] = TeleportPacketListener()
        listeners[PacketConstants.SPECIAL_ATTACK_OPCODE] = SpecialAttackPacketListener()
        listeners[PacketConstants.BUTTON_CLICK_OPCODE] = ButtonClickPacketListener()
        listeners[PacketConstants.INTERFACE_ACTION_CLICK_OPCODE] = InterfaceActionClickPacketListener()
        listeners[PacketConstants.REGULAR_CHAT_OPCODE] = MessagePublicListener()
        listeners[PacketConstants.CLAN_CHAT_OPCODE] = MessageClanListener()

        listeners[PacketConstants.CHANGE_PASSWORD_REQUEST] = ChangePasswordPacketListener()

        listeners[PacketConstants.DROP_ITEM_OPCODE] = DropItemPacketListener()
        listeners[PacketConstants.FINALIZED_MAP_REGION_OPCODE] = FinalizedMapRegionChangePacketListener()
        listeners[PacketConstants.CHANGE_MAP_REGION_OPCODE] = RegionChangePacketListener()
        listeners[PacketConstants.CLOSE_INTERFACE_OPCODE] = CloseInterfacePacketListener()
        listeners[PacketConstants.EXAMINE_ITEM_OPCODE] = ExamineItemPacketListener()
        listeners[PacketConstants.EXAMINE_NPC_OPCODE] = ExamineOrEditNpcPacketListener()
        listeners[PacketConstants.CHANGE_APPEARANCE] = ChangeAppearancePacketListener()
        listeners[PacketConstants.DIALOGUE_OPCODE] = DialoguePacketListener()
        listeners[PacketConstants.ENTER_AMOUNT_OPCODE] = EnterInputPacketListener()
        listeners[PacketConstants.EQUIP_ITEM_OPCODE] = EquipPacketListener()
        listeners[PacketConstants.PLAYER_INACTIVE_OPCODE] = PlayerInactivePacketListener()
        listeners[PacketConstants.CHAT_SETTINGS_OPCODE] = ChatSettingsPacketListener()
        listeners[PacketConstants.COMMAND_OPCODE] = CommandPacketListener()
        listeners[PacketConstants.UPDATE_PLANE_OPCODE] = HeightCheckPacketListener()
        listeners[PacketConstants.COMMAND_MOVEMENT_OPCODE] = MovementPacketListener()
        listeners[PacketConstants.GAME_MOVEMENT_OPCODE] = MovementPacketListener()
        listeners[PacketConstants.MINIMAP_MOVEMENT_OPCODE] = MovementPacketListener()
        listeners[PacketConstants.PICKUP_ITEM_OPCODE] = PickupItemPacketListener()
        listeners[PacketConstants.SECOND_GROUNDITEM_OPTION_OPCODE] = SecondItemOnGroundOptionPacketListener()
        listeners[PacketConstants.SWITCH_ITEM_SLOT_OPCODE] = SwitchItemSlotPacketListener()
        listeners[PacketConstants.SWITCH_ITEM_CONTAINER_OPCODE] = SwitchItemContainerPacketListener()
        listeners[PacketConstants.SWITCH_BANK_TAB_SLOT_OPCODE] = SwitchBankTabSlotPacketListener()
        listeners[PacketConstants.THIRD_PLAYER_ACTION_OPCODE] = FollowPlayerPacketListener()
        listeners[PacketConstants.MAGIC_ON_OBJECT_OPCODE] = MagicOnObjectPacketListener()
        listeners[PacketConstants.MAGIC_ON_PLAYER_OPCODE] = MagicOnPlayerPacketListener()
        listeners[PacketConstants.MAGIC_ON_INVENTORY_ITEM_OPCODE] =
            MagicOnInventoryItemPacketListener()
        listeners[PacketConstants.MAGIC_ON_GROUND_ITEM_OPCODE] =
            MagicOnGroundItemPacketListener()
        listeners[PacketConstants.BANK_TAB_CREATION_OPCODE] = BankTabCreationPacketListener()
        //PACKETS[SPAWN_TAB_ACTION_OPCODE] = new SpawnItemPacketListener();

        //PACKETS[SPAWN_TAB_ACTION_OPCODE] = new SpawnItemPacketListener();
        listeners[PacketConstants.FIRST_ITEM_CONTAINER_ACTION_OPCODE] = ItemContainerActionPacketListener()
        listeners[PacketConstants.SECOND_ITEM_CONTAINER_ACTION_OPCODE] = ItemContainerActionPacketListener()
        listeners[PacketConstants.THIRD_ITEM_CONTAINER_ACTION_OPCODE] = ItemContainerActionPacketListener()
        listeners[PacketConstants.FOURTH_ITEM_CONTAINER_ACTION_OPCODE] = ItemContainerActionPacketListener()
        listeners[PacketConstants.FIFTH_ITEM_CONTAINER_ACTION_OPCODE] = ItemContainerActionPacketListener()
        listeners[PacketConstants.SIXTH_ITEM_CONTAINER_ACTION_OPCODE] = ItemContainerActionPacketListener()
        listeners[PacketConstants.SEVENTH_ITEM_CONTAINER_ACTION_OPCODE] = ItemContainerActionPacketListener()
        listeners[PacketConstants.EIGTH_ITEM_CONTAINER_ACTION_OPCODE] = ItemContainerActionPacketListener()

        listeners[PacketConstants.SECOND_PLAYER_ACTION] = PlayerOptionPacketListener()
        listeners[PacketConstants.FIRST_PLAYER_ACTION] = PlayerOptionPacketListener()
        listeners[PacketConstants.PLAYER_OPTION_2_OPCODE] = PlayerOptionPacketListener()
        listeners[PacketConstants.PLAYER_OPTION_3_OPCODE] = PlayerOptionPacketListener()

        listeners[PacketConstants.OBJECT_FIRST_CLICK_OPCODE] = ObjectActionPacketListener()
        listeners[PacketConstants.OBJECT_SECOND_CLICK_OPCODE] = ObjectActionPacketListener()
        listeners[PacketConstants.OBJECT_THIRD_CLICK_OPCODE] = ObjectActionPacketListener()
        listeners[PacketConstants.OBJECT_FOURTH_CLICK_OPCODE] = ObjectActionPacketListener()
        listeners[PacketConstants.OBJECT_FIFTH_CLICK_OPCODE] = ObjectActionPacketListener()

        listeners[PacketConstants.ATTACK_NPC_OPCODE] = NPCOptionPacketListener()
        listeners[PacketConstants.FIRST_CLICK_NPC_OPCODE] = NPCOptionPacketListener()
        listeners[PacketConstants.MAGE_NPC_OPCODE] = NPCOptionPacketListener()
        listeners[PacketConstants.SECOND_CLICK_NPC_OPCODE] = NPCOptionPacketListener()
        listeners[PacketConstants.THIRD_CLICK_NPC_OPCODE] = NPCOptionPacketListener()
        listeners[PacketConstants.FOURTH_CLICK_NPC_OPCODE] = NPCOptionPacketListener()

        listeners[PacketConstants.FIRST_ITEM_ACTION_OPCODE] = ItemActionPacketListener()
        listeners[PacketConstants.SECOND_ITEM_ACTION_OPCODE] = ItemActionPacketListener()
        listeners[PacketConstants.THIRD_ITEM_ACTION_OPCODE] = ItemActionPacketListener()

        listeners[PacketConstants.ITEM_ON_NPC] = UseItemPacketListener()
        listeners[PacketConstants.ITEM_ON_ITEM] = UseItemPacketListener()
        listeners[PacketConstants.ITEM_ON_OBJECT] = UseItemPacketListener()
        listeners[PacketConstants.ITEM_ON_GROUND_ITEM] = UseItemPacketListener()
        listeners[PacketConstants.ITEM_ON_PLAYER] = UseItemPacketListener()

        listeners[PacketConstants.ADD_FRIEND_OPCODE] = PlayerRelationPacketListener()
        listeners[PacketConstants.REMOVE_FRIEND_OPCODE] = PlayerRelationPacketListener()
        listeners[PacketConstants.ADD_IGNORE_OPCODE] = PlayerRelationPacketListener()
        listeners[PacketConstants.REMOVE_IGNORE_OPCODE] = PlayerRelationPacketListener()
        listeners[PacketConstants.SEND_PM_OPCODE] = PlayerRelationPacketListener()

        listeners[PacketConstants.ENTER_AMOUNT_OPCODE] = EnterInputPacketListener()
        listeners[PacketConstants.ENTER_SYNTAX_OPCODE] = EnterInputPacketListener()

        listeners[PacketConstants.TRADE_REQUEST_OPCODE] = TradeRequestPacketListener()
        listeners[PacketConstants.FIFTH_PLAYER_ACTION_OPCODE] = FifthPlayerOptionPacketListener()
        listeners[PacketConstants.SONG_FINISHED_OPCODE] = SongFinishedPacketListener()
        listeners[PacketConstants.RANDOM_SOUND_AREA_OPCODE] = RandomSoundAreaPacketListener()
        listeners[PacketConstants.SEND_INPUT] = SendInputPacketListener()

        // encoders

        register(SendProjectileMessage::class.java, SendProjectileMessageEncoder())

        register(SendTileGraphicMessage::class.java, SendTileGraphicMessageEncoder())

        register(SendTileItemMessage::class.java, AddTileItemMessageEncoder())
        register(SendMakeTileItemPublicMessage::class.java, MakeTileItemPublicMessageEncoder())
        register(RemoveTileItemMessage::class.java, RemoveTileItemMessageEncoder())
        register(UpdateTileItemMessage::class.java, UpdateTileItemMessageEncoder())

        register(SendObjectMessage::class.java, SendObjectMessageEncoder())
        register(RemoveObjectMessage::class.java, RemoveObjectMessageEncoder())

        register(SetUpdatedRegionMessage::class.java, SetUpdatedRegionMessageEncoder())
        register(GroupedRegionUpdateMessage::class.java, GroupedRegionUpdateMessageEncoder(this))
        register(ClearRegionMessage::class.java, ClearRegionMessageEncoder())
        register(RegionChangeMessage::class.java, RegionChangeMessageEncoder())

        register(PlayerSynchronizationMessage::class.java, PlayerSynchronizationMessageEncoder())
        register(NpcSynchronizationMessage::class.java, NpcSynchronizationMessageEncoder())

        // decoders

        /*
         * These are not used
         */
        decoders[PacketConstants.ADD_BROADCAST] = AddBroadcastMessageDecoder()
        decoders[PacketConstants.RECOLOR_ITEM_OPCODE] = RecolorItemMessageDecoder()
        decoders[PacketConstants.YELL_EDIT_OPCODE] = YellEditMessageDecoder()
        decoders[PacketConstants.TELEPORT_OPCODE] = TeleportMessageDecoder()
        decoders[PacketConstants.SPECIAL_ATTACK_OPCODE] = SpecialAttackMessageDecoder()
        decoders[PacketConstants.BUTTON_CLICK_OPCODE] = ButtonClickMessageDecoder()
        decoders[PacketConstants.INTERFACE_ACTION_CLICK_OPCODE] = InterfaceActionClickMessageDecoder()
        decoders[PacketConstants.REGULAR_CHAT_OPCODE] = ChatMessageDecoder()
        decoders[PacketConstants.CLAN_CHAT_OPCODE] = ChatMessageDecoder()
        decoders[PacketConstants.CHANGE_PASSWORD_REQUEST] = ChangePasswordMessageDecoder()
        decoders[PacketConstants.DROP_ITEM_OPCODE] = DropItemMessageDecoder()
        decoders[PacketConstants.FINALIZED_MAP_REGION_OPCODE] = FinalizedMapRegionChangeMessageDecoder()
        decoders[PacketConstants.CHANGE_MAP_REGION_OPCODE] = RegionChangeMessageDecoder()
        decoders[PacketConstants.CLOSE_INTERFACE_OPCODE] = CloseInterfaceMessageDecoder()
        decoders[PacketConstants.EXAMINE_ITEM_OPCODE] = ExamineItemMessageDecoder()
        decoders[PacketConstants.EXAMINE_NPC_OPCODE] = ExamineOrEditNpcMessageDecoder()
        decoders[PacketConstants.CHANGE_APPEARANCE] = ChangeAppearanceMessageDecoder()
        decoders[PacketConstants.DIALOGUE_OPCODE] = DialogueMessageDecoder()
        decoders[PacketConstants.ENTER_AMOUNT_OPCODE] = EnterInputMessageDecoder()
        decoders[PacketConstants.EQUIP_ITEM_OPCODE] = EquipMessageDecoder()
        decoders[PacketConstants.PLAYER_INACTIVE_OPCODE] = PlayerInactiveMessageDecoder()
        decoders[PacketConstants.CHAT_SETTINGS_OPCODE] = ChatSettingsMessageDecoder()
        decoders[PacketConstants.COMMAND_OPCODE] = CommandMessageDecoder()
        decoders[PacketConstants.UPDATE_PLANE_OPCODE] = HeightCheckMessageDecoder()
        decoders[PacketConstants.COMMAND_MOVEMENT_OPCODE] = MovementMessageDecoder()
        decoders[PacketConstants.GAME_MOVEMENT_OPCODE] = MovementMessageDecoder()
        decoders[PacketConstants.MINIMAP_MOVEMENT_OPCODE] = MovementMessageDecoder()
        decoders[PacketConstants.PICKUP_ITEM_OPCODE] = PickupItemMessageDecoder()
        decoders[PacketConstants.SECOND_GROUNDITEM_OPTION_OPCODE] = SecondItemOnGroundOptionMessageDecoder()
        decoders[PacketConstants.SWITCH_ITEM_SLOT_OPCODE] = SwitchItemSlotMessageDecoder()
        decoders[PacketConstants.SWITCH_ITEM_CONTAINER_OPCODE] = SwitchItemContainerMessageDecoder()
        decoders[PacketConstants.SWITCH_BANK_TAB_SLOT_OPCODE] = SwitchBankTabSlotMessageDecoder()
        decoders[PacketConstants.THIRD_PLAYER_ACTION_OPCODE] = FollowPlayerMessageDecoder()
        decoders[PacketConstants.MAGIC_ON_OBJECT_OPCODE] = MagicOnObjectMessageDecoder()
        decoders[PacketConstants.MAGIC_ON_PLAYER_OPCODE] = MagicOnPlayerMessageDecoder()
        decoders[PacketConstants.MAGIC_ON_INVENTORY_ITEM_OPCODE] = MagicOnInventoryItemMessageDecoder()
        decoders[PacketConstants.MAGIC_ON_GROUND_ITEM_OPCODE] = MagicOnGroundItemMessageDecoder()
        decoders[PacketConstants.BANK_TAB_CREATION_OPCODE] = BankTabCreationMessageDecoder()
        decoders[PacketConstants.FIRST_ITEM_CONTAINER_ACTION_OPCODE] = ItemContainerActionMessageDecoder()
        decoders[PacketConstants.SECOND_ITEM_CONTAINER_ACTION_OPCODE] = ItemContainerActionMessageDecoder()
        decoders[PacketConstants.THIRD_ITEM_CONTAINER_ACTION_OPCODE] = ItemContainerActionMessageDecoder()
        decoders[PacketConstants.FOURTH_ITEM_CONTAINER_ACTION_OPCODE] = ItemContainerActionMessageDecoder()
        decoders[PacketConstants.FIFTH_ITEM_CONTAINER_ACTION_OPCODE] = ItemContainerActionMessageDecoder()
        decoders[PacketConstants.SIXTH_ITEM_CONTAINER_ACTION_OPCODE] = ItemContainerActionMessageDecoder()
        decoders[PacketConstants.SEVENTH_ITEM_CONTAINER_ACTION_OPCODE] = ItemContainerActionMessageDecoder()
        decoders[PacketConstants.EIGTH_ITEM_CONTAINER_ACTION_OPCODE] = ItemContainerActionMessageDecoder()
        decoders[PacketConstants.SECOND_PLAYER_ACTION] = PlayerOptionMessageDecoder()
        decoders[PacketConstants.FIRST_PLAYER_ACTION] = PlayerOptionMessageDecoder()
        decoders[PacketConstants.PLAYER_OPTION_2_OPCODE] = PlayerOptionMessageDecoder()
        decoders[PacketConstants.PLAYER_OPTION_3_OPCODE] = PlayerOptionMessageDecoder()
        decoders[PacketConstants.OBJECT_FIRST_CLICK_OPCODE] = ObjectActionMessageDecoder()
        decoders[PacketConstants.OBJECT_SECOND_CLICK_OPCODE] = ObjectActionMessageDecoder()
        decoders[PacketConstants.OBJECT_THIRD_CLICK_OPCODE] = ObjectActionMessageDecoder()
        decoders[PacketConstants.OBJECT_FOURTH_CLICK_OPCODE] = ObjectActionMessageDecoder()
        decoders[PacketConstants.OBJECT_FIFTH_CLICK_OPCODE] = ObjectActionMessageDecoder()
        decoders[PacketConstants.ATTACK_NPC_OPCODE] = NPCOptionMessageDecoder()
        decoders[PacketConstants.FIRST_CLICK_NPC_OPCODE] = NPCOptionMessageDecoder()
        decoders[PacketConstants.MAGE_NPC_OPCODE] = NPCOptionMessageDecoder()
        decoders[PacketConstants.SECOND_CLICK_NPC_OPCODE] = NPCOptionMessageDecoder()
        decoders[PacketConstants.THIRD_CLICK_NPC_OPCODE] = NPCOptionMessageDecoder()
        decoders[PacketConstants.FOURTH_CLICK_NPC_OPCODE] = NPCOptionMessageDecoder()
        decoders[PacketConstants.FIRST_ITEM_ACTION_OPCODE] = ItemActionMessageDecoder()
        decoders[PacketConstants.SECOND_ITEM_ACTION_OPCODE] = ItemActionMessageDecoder()
        decoders[PacketConstants.THIRD_ITEM_ACTION_OPCODE] = ItemActionMessageDecoder()
        decoders[PacketConstants.ITEM_ON_NPC] = ItemOnNpcMessageDecoder()
        decoders[PacketConstants.ITEM_ON_ITEM] = ItemOnItemMessageDecoder()
        decoders[PacketConstants.ITEM_ON_OBJECT] = ItemOnObjectMessageDecoder()
        decoders[PacketConstants.ITEM_ON_GROUND_ITEM] = ItemOnGroundItemMessageDecoder()
        decoders[PacketConstants.ITEM_ON_PLAYER] = ItemOnPlayerMessageDecoder()
        decoders[PacketConstants.ADD_FRIEND_OPCODE] = PlayerRelationMessageDecoder()
        decoders[PacketConstants.REMOVE_FRIEND_OPCODE] = PlayerRelationMessageDecoder()
        decoders[PacketConstants.ADD_IGNORE_OPCODE] = PlayerRelationMessageDecoder()
        decoders[PacketConstants.REMOVE_IGNORE_OPCODE] = PlayerRelationMessageDecoder()
        decoders[PacketConstants.SEND_PM_OPCODE] = PlayerPrivateChatMessageDecoder()
        decoders[PacketConstants.ENTER_AMOUNT_OPCODE] = EnterInputMessageDecoder()
        decoders[PacketConstants.ENTER_SYNTAX_OPCODE] = EnterInputMessageDecoder()
        decoders[PacketConstants.TRADE_REQUEST_OPCODE] = TradeRequestMessageDecoder()
        decoders[PacketConstants.SONG_FINISHED_OPCODE] = SongFinishedMessageDecoder()
        decoders[PacketConstants.RANDOM_SOUND_AREA_OPCODE] = RandomSoundAreaMessageDecoder()

        /*
         * These are not used
         */
        outgoingPacketTypes[1] = PacketType.FIXED
        outgoingPacketTypes[4] = PacketType.FIXED
        outgoingPacketTypes[9] = PacketType.FIXED
        outgoingPacketTypes[10] = PacketType.FIXED
        outgoingPacketTypes[24] = PacketType.FIXED
        outgoingPacketTypes[27] = PacketType.VARIABLE_BYTE
        outgoingPacketTypes[28] = PacketType.FIXED
        outgoingPacketTypes[34] = PacketType.FIXED
        outgoingPacketTypes[35] = PacketType.FIXED
        outgoingPacketTypes[36] = PacketType.FIXED
        outgoingPacketTypes[38] = PacketType.FIXED
        outgoingPacketTypes[44] = PacketType.FIXED
        outgoingPacketTypes[50] = PacketType.FIXED
        outgoingPacketTypes[51] = PacketType.FIXED
        outgoingPacketTypes[53] = PacketType.VARIABLE_SHORT
        outgoingPacketTypes[54] = PacketType.FIXED
        outgoingPacketTypes[55] = PacketType.FIXED
        outgoingPacketTypes[56] = PacketType.FIXED
        outgoingPacketTypes[60] = PacketType.VARIABLE_SHORT
        outgoingPacketTypes[61] = PacketType.FIXED
        outgoingPacketTypes[62] = PacketType.FIXED
        outgoingPacketTypes[64] = PacketType.FIXED
        outgoingPacketTypes[65] = PacketType.VARIABLE_SHORT
        outgoingPacketTypes[66] = PacketType.VARIABLE_SHORT
        outgoingPacketTypes[68] = PacketType.FIXED
        outgoingPacketTypes[70] = PacketType.FIXED
        outgoingPacketTypes[71] = PacketType.FIXED
        outgoingPacketTypes[72] = PacketType.FIXED
        outgoingPacketTypes[73] = PacketType.FIXED
        outgoingPacketTypes[74] = PacketType.FIXED
        outgoingPacketTypes[75] = PacketType.FIXED
        outgoingPacketTypes[78] = PacketType.FIXED
        outgoingPacketTypes[81] = PacketType.VARIABLE_SHORT
        outgoingPacketTypes[84] = PacketType.FIXED
        outgoingPacketTypes[85] = PacketType.FIXED
        outgoingPacketTypes[86] = PacketType.FIXED
        outgoingPacketTypes[87] = PacketType.FIXED
        outgoingPacketTypes[89] = PacketType.FIXED
        outgoingPacketTypes[97] = PacketType.FIXED
        outgoingPacketTypes[98] = PacketType.FIXED
        outgoingPacketTypes[99] = PacketType.FIXED
        outgoingPacketTypes[101] = PacketType.FIXED
        outgoingPacketTypes[103] = PacketType.FIXED
        outgoingPacketTypes[104] = PacketType.VARIABLE_BYTE
        outgoingPacketTypes[105] = PacketType.FIXED
        outgoingPacketTypes[106] = PacketType.FIXED
        outgoingPacketTypes[107] = PacketType.FIXED
        outgoingPacketTypes[108] = PacketType.FIXED
        outgoingPacketTypes[109] = PacketType.FIXED
        outgoingPacketTypes[110] = PacketType.FIXED
        outgoingPacketTypes[111] = PacketType.FIXED
        outgoingPacketTypes[112] = PacketType.FIXED
        outgoingPacketTypes[113] = PacketType.FIXED
        outgoingPacketTypes[114] = PacketType.FIXED
        outgoingPacketTypes[115] = PacketType.FIXED
        outgoingPacketTypes[116] = PacketType.FIXED
        outgoingPacketTypes[117] = PacketType.FIXED
        outgoingPacketTypes[122] = PacketType.FIXED
        outgoingPacketTypes[123] = PacketType.VARIABLE_SHORT
        outgoingPacketTypes[126] = PacketType.VARIABLE_SHORT
        outgoingPacketTypes[127] = PacketType.FIXED
        outgoingPacketTypes[134] = PacketType.FIXED
        outgoingPacketTypes[137] = PacketType.FIXED
        outgoingPacketTypes[140] = PacketType.FIXED
        outgoingPacketTypes[151] = PacketType.FIXED
        outgoingPacketTypes[156] = PacketType.FIXED
        outgoingPacketTypes[160] = PacketType.FIXED
        outgoingPacketTypes[164] = PacketType.FIXED
        outgoingPacketTypes[166] = PacketType.FIXED
        outgoingPacketTypes[171] = PacketType.FIXED
        outgoingPacketTypes[174] = PacketType.FIXED
        outgoingPacketTypes[175] = PacketType.FIXED
        outgoingPacketTypes[177] = PacketType.FIXED
        outgoingPacketTypes[178] = PacketType.FIXED
        outgoingPacketTypes[183] = PacketType.FIXED
        outgoingPacketTypes[185] = PacketType.FIXED
        outgoingPacketTypes[186] = PacketType.FIXED
        outgoingPacketTypes[187] = PacketType.VARIABLE_BYTE
        outgoingPacketTypes[196] = PacketType.VARIABLE_BYTE
        outgoingPacketTypes[200] = PacketType.FIXED
        outgoingPacketTypes[203] = PacketType.FIXED
        outgoingPacketTypes[206] = PacketType.FIXED
        outgoingPacketTypes[208] = PacketType.FIXED
        outgoingPacketTypes[214] = PacketType.VARIABLE_SHORT
        outgoingPacketTypes[215] = PacketType.FIXED
        outgoingPacketTypes[219] = PacketType.FIXED
        outgoingPacketTypes[221] = PacketType.FIXED
        outgoingPacketTypes[231] = PacketType.VARIABLE_SHORT
        outgoingPacketTypes[239] = PacketType.FIXED
        outgoingPacketTypes[240] = PacketType.FIXED
        outgoingPacketTypes[239] = PacketType.FIXED
        outgoingPacketTypes[242] = PacketType.FIXED
        outgoingPacketTypes[243] = PacketType.VARIABLE_SHORT
        outgoingPacketTypes[244] = PacketType.VARIABLE_BYTE
        outgoingPacketTypes[245] = PacketType.VARIABLE_BYTE
        outgoingPacketTypes[246] = PacketType.FIXED
        outgoingPacketTypes[247] = PacketType.FIXED
        outgoingPacketTypes[248] = PacketType.FIXED
        outgoingPacketTypes[249] = PacketType.FIXED
        outgoingPacketTypes[250] = PacketType.FIXED
        outgoingPacketTypes[251] = PacketType.VARIABLE_BYTE
        outgoingPacketTypes[252] = PacketType.VARIABLE_SHORT
        outgoingPacketTypes[253] = PacketType.VARIABLE_BYTE
        outgoingPacketTypes[254] = PacketType.VARIABLE_SHORT
    }

    fun isIdlePacket(opcode: Int) : Boolean {
        return opcode == PacketConstants.PLAYER_INACTIVE_OPCODE
    }

    fun isUnusedPacket(opcode: Int): Boolean {
        for (op in unusedOpcodes) {
            if (op == opcode) return true
        }
        return false
    }

    fun isPriority(opcode: Int): Boolean {
        return opcode == 41 || opcode == 145 || opcode == 122 || opcode == 214
    }

    fun ignore(opcode: Int) = opcode == 0


    /**
     * Registers a [MessageEncoder] for the specified message type.
     *
     * @param type The message type.
     * @param encoder The message encoder.
     */
    fun <M : Message> register(type: Class<M>, encoder: MessageEncoder<M>) {
        encoders[type] = encoder
    }

    /**
     * Gets the [MessageEncoder] for the specified message type.
     *
     * @param type The type of message.
     * @return The message encoder.
     */
    fun <M : Message> getMessageEncoder(type: Class<M?>): MessageEncoder<M> {
        return encoders[type] as MessageEncoder<M>
    }

    companion object {
        private const val VARIABLE_SHORT = -2
        private const val VARIABLE_BYTE = -1
    }
}