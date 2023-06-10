package com.grinder.game.definition;

import com.grinder.game.World;
import com.grinder.util.Buffer;
import com.grinder.util.ObjectID;
import net.runelite.cache.ConfigType;
import net.runelite.cache.IndexType;
import net.runelite.cache.fs.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import static com.grinder.util.ObjectID.*;

public final class ObjectDefinition {

    private static final int[] OBELISK_IDS = {14829, 14830, 14827, 14828, 14826, 14831};
    public static boolean ObjectDefinition_isLowDetail;
    public static int[] streamIndices;
    public static int cacheIndex;
    public static ObjectDefinition[] cache;
    private static Buffer stream;
    private static int totalObjects;
    public boolean obstructsGround;
    public byte ambient;
    public int offsetX;
    public String name;
    public int modelSizeY;

    public byte lightDiffusion;
    public int sizeX;
    public int offsetHeight;
    public int minimapFunction;
    public int[] originalModelColors;
    public int modelSizeX;
    public int transformVarbit;
    public boolean isRotated;
    public int id;
    public boolean impenetrable;
    public int mapSceneId;
    public int[] transforms;
    public int supportItems;
    public int sizeY;
    public boolean clipType_contouredGround;
    public boolean modelClipped;
    public boolean isSolid;
    public boolean solid;
    public int surroundings;
    public int interactType;
    public boolean nonFlatShading;
    public int modelHeight;
    public int[] modelIds;
    public int transformConfigId;
    public int decorDisplacement;
    public int[] modelTypes;
    public String description;
    public boolean isInteractive;
    public boolean clipped;
    public int animationId;
    public int offsetY;
    public int[] modifiedModelColors;
    public String[] actions;
    private short[] originalTexture;
    private short[] modifiedTexture;
    public Map<Integer, Object> params;

    public ObjectDefinition() {
        id = -1;
    }

    public static void dumpNames() throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter("./Cache/object_names.txt"));
        for (int i = 0; i < totalObjects; i++) {
            ObjectDefinition def = forId(i);
            String name = def == null ? "null" : def.name;
            writer.write("ID: " + i + ", name: " + name + "");
            writer.newLine();
        }
        writer.close();
    }

    public int getSize() {
        switch (id) {
            case BARROWS_STAIRCASE_AHRIM:
            case BARROWS_STAIRCASE_DHAROK:
            case BARROWS_STAIRCASE_GUTHAN:
            case BARROWS_STAIRCASE_KARIL:
            case BARROWS_STAIRCASE_VERAC:
            case SPARKLING_POOL:
            case SPARKLING_POOL_2:
            case 31989:
                return 5;
            case BARROWS_STAIRCASE_TORAG:
                return 3;
            case STEPPING_STONE_16:
                return 3;
        }

        return (getSizeX() + getSizeY()) - 1;
    }

    public static ObjectDefinition forId(int id) {
        if (true) {
            ObjectDefinition def = definitions.get(id);

            if (def != null && def.transforms != null) {
                int morphismId = def.transforms[def.transforms.length - 1];

                if (morphismId != -1) {
                    def = definitions.get(def.transforms[0]); // TODO: Implement player based obj vars
                }
            }

            return def;
        }

        if (id > streamIndices.length) {
            id = streamIndices.length - 1;
        }

        if(id < 0) {
         //   System.err.println("Could not find object definition for id " + id + ".");
            return null;
        }

        for (int index = 0; index < 20; index++) {
            if (cache[index].id == id)
                return cache[index];
        }

        if (id == 25913)
            id = 15552;

        if (id == 1276)
            id = 1277;

        if (id == 25916 || id == 25926)
            id = 15553;

        if (id == 25917)
            id = 15554;

        cacheIndex = (cacheIndex + 1) % 20;
        ObjectDefinition objectDef = cache[cacheIndex];
        stream.index = streamIndices[id];
        objectDef.id = id;
        objectDef.reset();
        objectDef.readValues(stream);


        for (int obelisk : OBELISK_IDS) {
            if (id == obelisk) {
                objectDef.actions = new String[]{"Activate", null, null, null, null};
            }
        }

        if (id == 30282) {
            objectDef.sizeX = 9;
            objectDef.sizeY = 9;
        }

        if (id == 29241) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Restore-stats";
        }
        switch (id) {
            case 2091:
            case 1319:
                objectDef.sizeX = 2;
                objectDef.sizeY = 2;
                objectDef.modelSizeX = 100;
                objectDef.modelHeight = 100;
                objectDef.modelSizeY = 100;
                break;
            case 16683:
                objectDef.actions = new String[5];
                objectDef.actions[0] = "Climb-up";
            break;
            case 26782:
                objectDef.modelIds = new int[]{28258};
                objectDef.sizeX = 3;
                objectDef.sizeY = 3;
                objectDef.impenetrable = false;
                objectDef.isInteractive = true;
                objectDef.modelSizeX = 196;
                objectDef.modelHeight = 196;
                objectDef.modelSizeY = 196;
                objectDef.solid = false;
                break;
            case 13378:
            case 13127:
            case 13129:
            case 13131:
            case 13178:
            case 13132:
                objectDef.actions = null;
                break;
            case 22721:
                objectDef.actions = new String[5];
                objectDef.actions[0] = "Smelt";
                break;
            case 13370:
                objectDef.actions = new String[5];
                break;
            case 22822:
                objectDef.actions = new String[5];
                objectDef.actions[0] = "Deposit";
                break;
            case 27291:
                objectDef.copy(forId(18491));
                break;
            case 27253:
                objectDef.copy(forId(24101));
                break;
            case 6944:
                objectDef.copy(forId(6943));
                break;
            case 27264:
                objectDef.copy(forId(25808));
                break;
            case 7478:
                objectDef.copy(forId(7409));
                break;
            case 6948:
                objectDef.minimapFunction = 78;
                break;
            case 34586:
                objectDef.name = "Brimstone chest";
                break;
            case 31558:
                objectDef.name = "Exit stairs";
                break;
            case 11455:
                objectDef.actions = new String[5];
                objectDef.actions[0] = "Pull";
                objectDef.name = "Lever";
                break;
            case 1276:
                objectDef.modelHeight = 200;
                objectDef.modelSizeX = 150;
                break;
            case 18052:
                objectDef.actions = new String[5];
                objectDef.name = "Ancient rune case";
                break;
            case 20656:
                objectDef.name = "Gift of Christmas";
                break;
            case 21181:
                objectDef.name = "Snowy house";
                break;
            case 1758:
                objectDef.modelHeight = 250;
                objectDef.modelSizeX = 250;
                break;
            case 9074:
                objectDef.actions = new String[5];
                objectDef.name = "Gilded chest";
                break;
            case 28792:
                objectDef.actions = new String[5];
                objectDef.name = "Vault chest";
                break;
            case 31989:
                objectDef.sizeX = 7;
                objectDef.sizeY = 3;
                break;
        }


        if (id == 29241) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Restore-stats";
        }
        if (id == 21297) {
            objectDef.name = "Infernal cape (broken)";
        }
        if (id == 4874) {
            objectDef.name = "Miscellaneous Stall";
        }

        if (id == 7318) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Enter";
            objectDef.name = "HousePortal";
            objectDef.description = "No one even dares to get close to the queen's throne.";
        }
        if (id == 12260) {
            objectDef.actions[0] = "Enter";
            objectDef.description = "It is said a mysterious living creature lives there and who ever went never came back.";
        }
        if (id == 37501) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Teleport";
        }
        if (id == 29341) {
            objectDef.actions = new String[5];
            objectDef.name = "Wilderness Stalls (85 Thieving)";
            objectDef.actions[0] = "Teleport";
        }
        if (id == 4150) {
            objectDef.name = "Bank portal";
        } else if (id == 4151) {
            objectDef.name = "Ditch portal";
        }

        if (id == 26756) {
            objectDef.name = "Information";
            objectDef.actions = null;
        }
        if (id == 172) {
            objectDef.name = "Crystal Chest";
        }
        if (id == 18258 || id == 20377) {
            objectDef.name = "@yel@Gilded Altar";
            objectDef.description = "@yel@The altar is polished diorite, lighter and smoother than the basalt of the cave floor.";
        }

        if (id == 21175) {
            objectDef.name = "Broken boat";
            objectDef.actions = null;
        }

        if (id == 6447) {
            objectDef.minimapFunction = 12;
            objectDef.name = "Boss Entrance";
        }
        if (id == 29150) {
            int sizeX = objectDef.sizeX;
            int sizeY = objectDef.sizeY;
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Venerate";
            objectDef.actions[1] = "Switch-normal";
            objectDef.actions[2] = "Switch-ancient";
            objectDef.actions[3] = "Switch-lunar";
            objectDef.name = "Magical altar";
            objectDef.modelIds = new int[1];
            objectDef.modelIds[0] = 32160;
            objectDef.sizeX = sizeX;
            objectDef.sizeY = sizeY;
        }

        if (id == 6943 || id == 6084 || id == 6945
                || id == 6946 || id == 7409 || id == 7410 || id == 10083
                || id == 10517 || id == 11338 || id == 12798 || id == 12799
                || id == 12800 || id == 12801 || id == 2693
                || id == 4483 || id == 10562 || id == 14382 || id == 14886
                || id == 16995 || id == 16996 || id == 21301 || id == 34343
                || id == 18491 || id == 10355 || id == 10583 || id == 24101 || id == 24347 || id == 26711
                || id == 30267 || id == 14367 || id == 16700 || id == 3194 || id == ObjectID.BANK
                || id == ObjectID.BANK_BOOTH || id == ObjectID.BANK_BOOTH_2
                || id == ObjectID.BANK_BOOTH_3 || id == ObjectID.BANK_BOOTH_4
                || id == ObjectID.BANK_BOOTH_5 || id == ObjectID.BANK_BOOTH_6
                || id == ObjectID.BANK_BOOTH_7 || id == ObjectID.BANK_BOOTH_8
                || id == ObjectID.BANK_BOOTH_9 || id == ObjectID.BANK_BOOTH_10
                || id == ObjectID.BANK_BOOTH_11 || id == ObjectID.BANK_BOOTH_12
                || id == ObjectID.BANK_BOOTH_13 || id == ObjectID.BANK_BOOTH_14
                || id == ObjectID.BANK_BOOTH_15 || id == ObjectID.BANK_BOOTH_16
                || id == ObjectID.BANK_BOOTH_17 || id == ObjectID.BANK_BOOTH_18
                || id == ObjectID.BANK_BOOTH_19 || id == ObjectID.BANK_BOOTH_20
                || id == ObjectID.BANK_BOOTH_21 || id == ObjectID.BANK_BOOTH_22
                || id == ObjectID.BANK_BOOTH_23 || id == ObjectID.BANK_BOOTH_24
                || id == ObjectID.BANK_BOOTH_25 || id == ObjectID.BANK_BOOTH_26
                || id == ObjectID.BANK_BOOTH_27 || id == ObjectID.BANK_BOOTH_28
                || id == ObjectID.BANK_BOOTH_29 || id == ObjectID.BANK_BOOTH_30
                || id == ObjectID.BANK_BOOTH_33 || id == ObjectID.BANK_BOOTH_34
                || id == ObjectID.BANK_BOOTH_35 || id == ObjectID.BANK_BOOTH_36
                || id == ObjectID.BANK_BOOTH_37 || id == ObjectID.BANK_BOOTH_38
                || id == ObjectID.BANK_BOOTH_39 || id == ObjectID.BANK_BOOTH_40
                || id == ObjectID.BANK_BOOTH_41 || id == ObjectID.BANK_BOOTH_42
                || id == ObjectID.BANK_BOOTH_43 || id == ObjectID.BANK_BOOTH_44
                || id == ObjectID.BANK_BOOTH_45
                || id == ObjectID.BANK_BOOTH_10355
                || id == ObjectID.TIGHTROPE_4 // Varrock bank booths (W big bank)
                || id == ObjectID.CHEST_89 || id == 34343
                || id == 27291 || id == 32666 || id == 36559
                || id == ObjectID.BANK_CHEST || id == ObjectID.BANK_CHEST_2
                || id == ObjectID.BANK_CHEST_3 || id == ObjectID.BANK_CHEST_4
                || id == ObjectID.BANK_CHEST_5 || id == ObjectID.BANK_CHEST_6
                || id == ObjectID.BANK_CHEST_7 || id == ObjectID.BANK_CHEST_8
                || id == ObjectID.BANK_CHEST_9 || id == ObjectID.BANK_CHEST_10
                || id == ObjectID.BANK_CHEST_11 || id == ObjectID.BANK_CHEST_12
                || id == ObjectID.BANK_CHEST_13 || id == ObjectID.BANK_CHEST_14
                || id == ObjectID.BANK_CHEST_15 || id == ObjectID.BANK_CHEST_16
                || id == ObjectID.BANK_CHEST_17 || id == ObjectID.BANK_CHEST_18
                || id == ObjectID.OPEN_CHEST || id == ObjectID.OPEN_CHEST_2
                || id == ObjectID.OPEN_CHEST_3
                || id == ObjectID.OPEN_CHEST_5 || id == ObjectID.OPEN_CHEST_6
                || id == ObjectID.OPEN_CHEST_7 || id == ObjectID.OPEN_CHEST_8
                || id == 7478 || id == 27253 || id == 32666) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Bank";
            objectDef.actions[2] = "Collect";
            objectDef.actions[3] = "Open-presets";
        }

        if (id == 29170) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Admire";
            objectDef.actions[1] = "Claim";
            objectDef.actions[2] = "Gamble";
        }

        if (id == 12203) {
            objectDef.isInteractive = false;
            objectDef.solid = false;
        }

        if (id == 26760) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Open";
            objectDef.actions[1] = "Quick-Entry";
        }

        if (id == 6552) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Toggle-spells";
            objectDef.name = "Ancient altar";
        }

        if (id == -1) {
            objectDef.modelIds = null;
            objectDef.isInteractive = false;
            objectDef.solid = false;
        }

        if (id == 14911) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Toggle-spells";
            objectDef.name = "Lunar altar";
        }

        if (id == 10230 || id == 10177) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Climb-down";
        }
        if (id == 40417) { // Ingame the object shows as 3831 object id for some reason
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Climb-down";
        }
        if (id == 16683) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Climb-up";
            objectDef.actions[1] = "Climb-down";
        }

        if (id == 2164) {
            objectDef.isInteractive = true;
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Fix";
            objectDef.actions[1] = null;
            objectDef.name = "Trawler Net";
        }
        if (id == 1293) {
            objectDef.isInteractive = true;
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Teleport";
            objectDef.actions[1] = null;
            objectDef.name = "Spirit Tree";
        }

        if (id == 26757) {
            objectDef.isInteractive = true;
            objectDef.actions = new String[5];
            objectDef.actions[1] = "Search";
            objectDef.name = "Valueable Chest";
        }
        if (id == 2452) {
            objectDef.isInteractive = true;
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Go Through";
            objectDef.name = "Passage";
        }

        if (id == 2452 || id == 2455 || id == 2456 || id == 2454 || id == 2453
                || id == 2457 || id == 2461 || id == 2459
                || id == 2460) {
            objectDef.isInteractive = true;
            objectDef.name = "Mysterious Ruins";
        }
        if (id == 10638) {
            objectDef.isInteractive = true;
            return objectDef;
        }

        return objectDef;
    }

    private static final Map<Integer, ObjectDefinition> definitions = new HashMap<>();

    public static void init() {
        try {
            Storage storage = World.filestore.getStorage();
            Index index = World.filestore.getIndex(IndexType.CONFIGS);
            Archive archive = index.getArchive(ConfigType.OBJECT.getId());


            byte[] archiveData = storage.loadArchive(archive);
            ArchiveFiles files = archive.getFiles(archiveData);

            for (FSFile file : files.getFiles()) {
                ObjectDefinition objectDef = new ObjectDefinition();
                objectDef.id = file.getFileId();
                objectDef.reset();
                if (file.getContents() != null)
                    objectDef.readValues(new Buffer(file.getContents()));
                definitions.put(file.getFileId(), objectDef);
            }

            System.out.println("Loaded: " + definitions.size() + " object definitions.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        modelIds = null;
        modelTypes = null;
        name = null;
        description = null;
        modifiedModelColors = null;
        originalModelColors = null;
        sizeX = 1;
        sizeY = 1;
        solid = true;
        impenetrable = true;
        isInteractive = false;
        clipType_contouredGround = false;
        nonFlatShading = false;
        modelClipped = false;
        animationId = -1;
        decorDisplacement = 16;
        ambient = 0;
        lightDiffusion = 0;
        actions = null;
        minimapFunction = -1;
        mapSceneId = -1;
        isRotated = false;
        clipped = true;
        modelSizeX = 128;
        modelHeight = 128;
        modelSizeY = 128;
        surroundings = 0;
        offsetX = 0;
        offsetHeight = 0;
        offsetY = 0;
        obstructsGround = false;
        isSolid = false;
        supportItems = -1;
        transformConfigId = -1;
        transformVarbit = -1;
        transforms = null;
    }

    private void copy(ObjectDefinition copy) {
        name = copy.name;
        modelSizeX = copy.modelSizeX;
        modelHeight = copy.modelHeight;
        modelSizeY = copy.modelSizeY;
        obstructsGround = copy.obstructsGround;
        offsetX = copy.offsetX;
        sizeX = copy.sizeX;
        offsetHeight = copy.offsetHeight;
        impenetrable = copy.impenetrable;
        sizeY = copy.sizeY;
        modelClipped = copy.modelClipped;
        isSolid = copy.isSolid;
        solid = copy.solid;
        surroundings = copy.surroundings;
        nonFlatShading = copy.nonFlatShading;
        decorDisplacement = copy.decorDisplacement;
        modelTypes = copy.modelTypes;
        description = copy.description;
        isInteractive = copy.isInteractive;
        clipped = copy.clipped;
        offsetY = copy.offsetY;
        actions = copy.actions;
        modelIds = copy.modelIds;

    }

    public static boolean print = false;

    public void readValues(Buffer stream) {
        int flag = -1;
        do {
            int type = stream.readUnsignedByte();
            if (type == 0)
                break;
            if (type == 1) {
                int len = stream.readUnsignedByte();
                if (len > 0) {
                    if (modelIds == null || ObjectDefinition_isLowDetail) {
                        modelTypes = new int[len];
                        modelIds = new int[len];
                        for (int k1 = 0; k1 < len; k1++) {
                            modelIds[k1] = stream.readUShort();
                            modelTypes[k1] = stream.readUnsignedByte();
                        }
                    } else {
                        stream.index += len * 3;
                    }
                }
            } else if (type == 2) {
                name = stream.readStringCp1252NullTerminated();
                if (print) {
                    System.out.println(name);
                }
            }
            else if (type == 5) {
                int len = stream.readUnsignedByte();
                if (len > 0) {
                    if (modelIds == null || ObjectDefinition_isLowDetail) {
                        modelTypes = null;
                        modelIds = new int[len];
                        for (int l1 = 0; l1 < len; l1++)
                            modelIds[l1] = stream.readUShort();
                    } else {
                        stream.index += len * 2;
                    }
                }
            } else if (type == 14)
                sizeX = stream.readUnsignedByte();
            else if (type == 15)
                sizeY = stream.readUnsignedByte();
            else if (type == 17)
                solid = false;
            else if (type == 18)
                impenetrable = false;
            else if (type == 19)
                isInteractive = (stream.readUnsignedByte() == 1);
            else if (type == 21)
                clipType_contouredGround = true;
            else if (type == 22)
                nonFlatShading = true;
            else if (type == 23)
                modelClipped = true;
            else if (type == 24) {
                animationId = stream.readUShort();
                if (animationId == 65535)
                    animationId = -1;
            } else if (type == 27) {
                interactType = 1;
            } else if (type == 28)
                decorDisplacement = stream.readUnsignedByte();
            else if (type == 29)
                stream.readSignedByte();
            else if (type == 39)
                stream.readSignedByte();
            else if (type >= 30 && type < 35) {
                if (actions == null)
                    actions = new String[10];
                actions[type - 30] = stream.readStringCp1252NullTerminated();
                if (actions[type - 30].equalsIgnoreCase("hidden"))
                    actions[type - 30] = null;
            } else if (type == 40) {
                int i1 = stream.readUnsignedByte();
                for (int i2 = 0; i2 < i1; i2++) {
                    stream.readUShort();
                    stream.readUShort();
                }
            } else if (type == 41) {
                int j2 = stream.readUnsignedByte();
                for (int k = 0; k < j2; k++) {
                    stream.readUShort();
                    stream.readUShort();
                }
            } else if (type == 61) {
                stream.readUShort();
            } else if (type == 62)
                isRotated = true;
            else if (type == 64)
                clipped = false;
            else if (type == 65)
                modelSizeX = stream.readUShort();
            else if (type == 66)
                modelHeight = stream.readUShort();
            else if (type == 67)
                modelSizeY = stream.readUShort();
            else if (type == 68)
                mapSceneId = stream.readUShort();
            else if (type == 69)
                surroundings = stream.readUnsignedByte();
            else if (type == 70)
                offsetX = stream.readShort();
            else if (type == 71)
                offsetHeight = stream.readShort();
            else if (type == 72)
                offsetY = stream.readShort();
            else if (type == 73)
                obstructsGround = true;
            else if (type == 74)
                isSolid = true;
            else if (type == 75) {
                supportItems = stream.readUnsignedByte();
            } else if (type != 77 && type != 92) {
                if (type == 78) {
                    stream.readUShort(); // ambient sound id
                    stream.readUnsignedByte();
                } else if (type == 79) {
                    stream.readUShort();
                    stream.readUShort();
                    stream.readUnsignedByte();
                    int len = stream.readUnsignedByte();
                    for (int i = 0; i < len; i++) {
                        stream.readUShort();
                    }
                } else if (type == 81) {
                    stream.readUnsignedByte();
                } else if (type == 82) {
                    minimapFunction = stream.readUShort();
                } else if (type == 89) {

                } else if (type == 249) {
                    params = stream.readStringIntParams(params);
                }
            } else {
                transformVarbit = stream.readUShort();
                if (transformVarbit == 65535)
                    transformVarbit = -1;
                transformConfigId = stream.readUShort();
                if (transformConfigId == 65535)
                    transformConfigId = -1;
                int var3 = -1;
                if (type == 92)
                    var3 = stream.readUShort();

                int j1 = stream.readUnsignedByte();
                transforms = new int[j1 + 2];
                for (int j2 = 0; j2 <= j1; j2++) {
                    transforms[j2] = stream.readUShort();
                    if (transforms[j2] == 65535)
                        transforms[j2] = -1;
                }

                transforms[j1 + 1] = var3;
            }
        } while (true);
        if (flag == -1 && name != "null" && name != null) {
            isInteractive = modelIds != null
                    && (modelTypes == null || modelTypes[0] == 10);
            if (actions != null)
                isInteractive = true;
        }
        if (isSolid) {
            solid = false;
            impenetrable = false;
        }
        if (supportItems == -1)
            supportItems = solid ? 1 : 0;
    }

    public String getName() {
        return name;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public boolean hasActions() {
        return isInteractive;
    }

}