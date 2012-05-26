package edu.ucsb.deepspace.persistence;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class Reader {
	
	public static String read(String filename) {
		try {
			return FileUtils.readFileToString(new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
