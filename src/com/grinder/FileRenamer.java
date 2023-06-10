package com.grinder;

import java.io.File;

public class FileRenamer {
    public static void main(String[] args) {
        String directoryPath = "./data/cache/idx7/rename"; // replace with the path to your directory
        int oldFirstDigit = 9; // replace with the old first digit you want to replace
        int newFirstDigit = 6; // replace with the new first digit you want to use

        File directory = new File(directoryPath);
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    String oldName = file.getName();
                    String newName = oldName.replaceFirst(Integer.toString(oldFirstDigit), Integer.toString(newFirstDigit));
                    File newFile = new File(directoryPath + File.separator + newName);
                    System.out.println("Renamed model file from " + oldName +" into " + newName);
                    file.renameTo(newFile);
                }
            }
        } else {
            System.out.println(directory.listFiles());
        }
    }
}