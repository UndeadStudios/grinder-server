package com.grinder.game.definition;

import com.grinder.game.World;
import com.grinder.util.Buffer;
import net.runelite.cache.ConfigType;
import net.runelite.cache.IndexType;
import net.runelite.cache.fs.*;

import java.io.IOException;

/**
 * TODO: Add player vars
 */
public class VarDefinition {

    public int varp;
    public int lowBit;
    public int highBit;

    public static void init() throws IOException {
        Storage storage = World.filestore.getStorage();
        Index index = World.filestore.getIndex(IndexType.CONFIGS);
        Archive archive = index.getArchive(ConfigType.VARBIT.getId());

        byte[] archiveData = storage.loadArchive(archive);
        ArchiveFiles files = archive.getFiles(archiveData);

        for (FSFile file : files.getFiles()) {
            VarDefinition varbit = new VarDefinition();
            if (file.getContents() != null)
                varbit.decode(new Buffer(file.getContents()));
        }
    }

    private void decode(Buffer stream) {
        while(true) {
            int opcode = stream.getUnsignedByte();

            if (opcode == 0) {
                return;
            }

            if (opcode == 1) {
                this.varp = stream.getUnsignedLEShort();
                this.lowBit = stream.getUnsignedByte();
                this.highBit = stream.getUnsignedByte();
            }
        }
    }

    static int[] shifts;

    static {
        shifts = new int[32];
        int var0 = 2;

        for(int var1 = 0; var1 < 32; ++var1) {
            shifts[var1] = var0 - 1;
            var0 += var0;
        }
    }

}
