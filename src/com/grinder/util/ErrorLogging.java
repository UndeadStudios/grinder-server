package com.grinder.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A class that handles error logging.
 * 
 * @author Blake
 *
 */
public class ErrorLogging {
	
	/**
	 * The date format.
	 */
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	/**
	 * The error file's path.
	 */
	private static String ERROR_PATH = "./data/logs/";
	
	/**
	 * Logs the exception into a file.
	 * 
	 * @param fileName
	 *            The name of the log file.
	 * @param ex
	 *            The exception.
	 */
	public static void log(String fileName, Throwable t) {
		log(fileName, null, t);
	}
	
	/**
	 * Logs the exception into a file.
	 * 
	 * @param fileName
	 *            The name of the log file.
	 * @param param
	 *            The attached parameter.
	 * @param t
	 *            The throwable.
	 */
	public static void log(String fileName, String param, Throwable t) {
		File file = new File(ERROR_PATH, fileName + ".log");
		
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}
		
		try {
			FileWriter fw = new FileWriter(file, true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    
		    bw.write("========================================\n");
		    bw.write(" DATE: " + new Date().toString() + "\n");
		    
		    if (param != null) {
				bw.write(" PARAM: " + param + "\n");
			}
		    
		    bw.write("========================================\n");
		    bw.write(t.toString() + "\n");
		    
		    for (StackTraceElement ste : t.getStackTrace()) {
				bw.write("\tat " + ste.toString() + "\n");
			}
		    bw.newLine();
		    bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Logs the specified data into a file.
	 * 
	 * @param path
	 *            The name of the log file.
	 * @param data
	 *            The data.
	 */
	public static void log(String fileName, String data) {
		File file = new File(ERROR_PATH, fileName + ".log");
		
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}

		try {
			FileWriter fw = new FileWriter(fileName, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("---" + DATE_FORMAT.format(Calendar.getInstance().getTime()) + "---");
			bw.newLine();
			bw.write(data);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
