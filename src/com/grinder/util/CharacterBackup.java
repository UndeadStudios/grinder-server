package com.grinder.util;

import com.grinder.game.GameConstants;
import com.grinder.game.World;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.util.timing.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CharacterBackup {

    private static final Logger LOGGER = LogManager.getLogger(CharacterBackup.class.getName());
    private static final int TIME = 86_400_000; //24 hours

    public static Stopwatch timer = new Stopwatch().reset();

    private static Thread backupThread;

    public CharacterBackup() {
        createBackup(true);
    }

    private static void createBackup(boolean force) {

        final File playerFileDirectory = Paths.get(GameConstants.PLAYER_DIRECTORY).toFile();
        final File backupDir = Paths.get(GameConstants.BACKUP_DIRECTORY).toFile();
        final File playerFileDirectoryCompressed =  backupDir.toPath().resolve("Backup " + getDate() + ".zip").toFile();

        if (backupDir.mkdirs())
            LOGGER.info("Created backup dir at " + backupDir);

        if (force || !playerFileDirectoryCompressed.exists()) {
            try {

                if (backupThread == null || !backupThread.isAlive()) {

                    if (playerFileDirectoryCompressed.createNewFile())
                        LOGGER.info("Created new file at " + playerFileDirectoryCompressed);

                    PlayerUtil.broadcastPlayerStaffMessage("Server is now making a backup!");
                    backupThread = new Thread(() -> {

                        try {
                            LOGGER.info("Starting player file back up process");
                            final long start = System.nanoTime();
                            zipDirectory(playerFileDirectory, playerFileDirectoryCompressed);
                            final long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                            LOGGER.info("Completed player file back up process in "+duration+" ms!");
                            World.submitGameThreadJob(() -> {
                                PlayerUtil.broadcastPlayerStaffMessage("Server finished making a backup, took " + duration + "ms !");
                                return null;
                            });
                        } catch (IOException e) {
                            LOGGER.error("Player file back up process failed!", e);
                        }
                    }, "Character-Backup");
                    backupThread.start();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            System.out.println("[BACKUP] Characters already backed up, backup canceled..");
    }

    public static void sequence() {
        if (timer.elapsed(TIME)) {
            timer.reset();
            createBackup(false);
        }
    }

    private static void zipDirectory(File f, File zf) throws IOException {
        ZipOutputStream z = new ZipOutputStream(new FileOutputStream(zf));
        zip(f, f, z);
        z.close();
    }

    private static void zip(File directory, File base, ZipOutputStream zos) throws IOException {

        final File[] files = directory.listFiles();
        final byte[] buffer = new byte[8192];

        int read;

        if (files == null) {
            System.err.println("CharacterBackup: failed, " + directory + " is null!");
            return;
        }

        for (File file : files) {

            if (file.isDirectory()) {

                zip(file, base, zos);

            } else {

                if (!file.getName().endsWith(".json"))
                    continue;

                final FileInputStream in = new FileInputStream(file);
                final ZipEntry entry = new ZipEntry(file.getPath().substring(base.getPath().length() + 1));
                zos.putNextEntry(entry);

                while (-1 != (read = in.read(buffer)))
                    zos.write(buffer, 0, read);

                in.close();
            }
        }
    }

    private static String getDate() {
        final DateFormat dateFormat = new SimpleDateFormat("MM dd yyyy");
        final Date date = new Date();
        return dateFormat.format(date);
    }
}