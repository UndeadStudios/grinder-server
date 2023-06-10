package com.grinder.util.oldgrinder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class DataUtil {
	
	public static File getDefinition(String fileName) {
		return new File("data/definitions/" + fileName);
	}

}
