package edu.ucsb.deepspace.business;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import edu.ucsb.deepspace.persistence.Writer;

public class Test {
	
	public static String makeDir(Calendar begin, String name) throws IOException {
		int month = begin.get(Calendar.MONTH)+ 1; 
		String filename = "data/" + name + " " + month + "_" + begin.get(Calendar.DATE) + "_" + 
						  begin.get(Calendar.YEAR) + " " + begin.get(Calendar.HOUR_OF_DAY) + "_" + begin.get(Calendar.MINUTE) +
						  "_" + begin.get(Calendar.SECOND);
		File dir = new File(filename);
		dir.mkdir();
		
		String out = "Name,Radius,Theta,Phi,Milliseconds since start," +
		"Linear Pot Val,Encoder Val,First Temp,Second Temp,Third Temp,Fourth Temp,Fifth Temp,Sixth Temp," +
		"Temp1,Pres1,Humid1,ExtTemp1,ExtTemp2,ExtTemp3,ExtTemp4,ExtTemp5,ExtTemp6,ExtTemp7,ExtTemp8\n";
		Writer.write(filename + "/data.csv", out);
		return filename;
	}
}