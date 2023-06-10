package com.grinder.util;

import java.io.*;

public class EcoManager
{
        public static void main(String args[])
        {
                /**
                  * Put the item ids in this array.
                  * Don't put notes, though. It will cause problems.
                  */
                int[] rares = {4151, 1038, 1040, 1042, 1044, 1046, 1048, 1050, 1053, 1055, 1057, 6585, 11694, 11696, 11698, 11718, 11720, 11722, 11724, 11726, 11728, 11732, 15039, 14484, 11235, 11283, 6570};
                File charFolder;
                BufferedWriter bw;
                BufferedReader br;
                BufferedWriter tmpwr;
                BufferedReader tmpr;
                boolean overwrite = false;
                String read;
                int count = 0;
                String dir = System.getProperty("user.home") + File.separator + "GrinderScapeCharacters/";
                double done = 0;
                int percent = 0;
                int percentRounded = 0;

                charFolder = new File(dir);

                if(!charFolder.exists())
                {
                        System.out.println("The directory "+dir+" was not found.");
                        return;
                }
                else
                if(charFolder.list().length == 0)
                {
                        System.out.println("The specified directory is empty.");
                        return;
                }
                String file[] = charFolder.list();
                for(String s : file)
                {
                        try
                        {
                                File charFile = new File(dir+s);
                                File tmpFile = new File(dir+s+".tmp");
                                tmpFile.createNewFile();
                                br = new BufferedReader(new FileReader(charFile));
                                tmpwr = new BufferedWriter(new FileWriter(tmpFile, true));

                                while((read = br.readLine()) != null)
                                {
                                        if(read.contains("ADMINISTRATOR"))
                                        {
                                                		count++;
                                                		System.out.println(count);
                                                        System.out.println("Bank saved for admin "+s.substring(0, s.indexOf(".")));
                                                }
                                        }
                                tmpwr.flush();
                                tmpwr.close();
                                br.close();

                                //charFile.delete();

                                bw = new BufferedWriter(new FileWriter(charFile, true));
                                tmpr = new BufferedReader(new FileReader(tmpFile));

                                while((read = tmpr.readLine()) != null)
                                {
                                        bw.write(read);
                                        bw.newLine();
                                }

                                bw.flush();
                                bw.close();
                                tmpr.close();
                                tmpFile.delete();

                                done++;

                                if((percent = (int)(done / file.length * 100)) % 5 < 5 && percent - percent % 5 != percentRounded)
                                        System.out.println((percentRounded = (int)(percent - percent % 5))+"%");
                        }
                        catch(IOException Ioe)
                        {
                                Ioe.printStackTrace();
                        }
                }
                System.out.println("Finished!");
        }
}