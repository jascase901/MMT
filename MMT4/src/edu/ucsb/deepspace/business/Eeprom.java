package edu.ucsb.deepspace.business;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public final class Eeprom {
	public static byte[] read(String filename) {
		byte[] eeprom = null;
		try {
			eeprom = FileUtils.readFileToByteArray(new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return eeprom;
	}
	
	public static String read2(String filename) {
		String out = "";
		try {
			out = FileUtils.readFileToString(new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}
}