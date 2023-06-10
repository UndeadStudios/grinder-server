//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.grinder.util.compress;

import org.apache.commons.compress.utils.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZip {

    public GZip() {
    }

    public static byte[] compress(byte[] bytes) throws IOException {
        InputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        OutputStream os = new GZIPOutputStream(bout);
        Throwable var4 = null;

        try {
            IOUtils.copy(is, os);
        } catch (Throwable var13) {
            var4 = var13;
            throw var13;
        } finally {
            if (os != null) {
                if (var4 != null) {
                    try {
                        os.close();
                    } catch (Throwable var12) {
                        var4.addSuppressed(var12);
                    }
                } else {
                    os.close();
                }
            }

        }

        return bout.toByteArray();
    }

}
