package com.grinder.util.io;

import com.grinder.util.compress.CompressionUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

public class FileUtil {

    public static byte[] readFile(String name) {
        try {
            RandomAccessFile raf = new RandomAccessFile(name, "r");
            try (raf) {
                ByteBuffer buf =
                        raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, raf.length());
                if (buf.hasArray()) {
                    return buf.array();
                } else {
                    byte[] array = new byte[buf.remaining()];
                    buf.get(array);
                    return array;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] getGZBuffer(String file) throws Exception {
        File f = new File(file);
        if (!f.exists())
            return null;
        byte[] buffer = new byte[(int) f.length()];
        DataInputStream dis = new DataInputStream(new FileInputStream(f));
        dis.readFully(buffer);
        dis.close();
        byte[] gzipInputBuffer = new byte[999999];
        int bufferlength = 0;
        GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(buffer));
        do {
            if (bufferlength == gzipInputBuffer.length) {
                System.out.println("Error inflating data.\nGZIP buffer overflow.");
                break;
            }
            int readByte = gzip.read(gzipInputBuffer, bufferlength, gzipInputBuffer.length - bufferlength);
            if (readByte == -1)
                break;
            bufferlength += readByte;
        } while (true);
        byte[] inflated = new byte[bufferlength];
        System.arraycopy(gzipInputBuffer, 0, inflated, 0, bufferlength);
        buffer = inflated;
        if (buffer.length < 10)
            return null;
        return buffer;
    }

    public static byte[] getDecompressedBuffer(String file) {
        try {
            byte[] buffer = getGZBuffer(file);
            return CompressionUtil.gunzip(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void appendToFile(Path file, String data, boolean flush) {
        appendToFile(file.toString(), data, flush);
    }

    public static void appendToFile(String file, String data, boolean flush) {
        try {
            try (BufferedWriter out = new BufferedWriter(new FileWriter(file, true))) {
                out.write(data);
                out.newLine();
                if(flush)
                    out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
