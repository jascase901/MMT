package edu.ucsb.deepspace.business;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class ReflectableIO {
	
	public static List<String> readRef(final String filename) {
		Map<String, Reflectable> reflectables = new HashMap<String, Reflectable>();
		final List<String> targetList = new ArrayList<String>();
		final List<String> actuatorList = new ArrayList<String>();
		
		File asdf = new File(filename);
		List<String> lines = null;
		try {
			lines = FileUtils.readLines(asdf);
		} catch (IOException e) {
			System.out.println("Error reading file.  From business.IO.Reflectable");
			e.printStackTrace();
			List<String> asdf2 = new ArrayList<String>();
			return asdf2;
		}
		
		for (String s : lines) {
			String[] split = s.split(",");
			String type = StringUtils.strip(split[0], "\"");
			if (type.equals("Type")) continue;
			
			String name = StringUtils.strip(split[1], "\"");
			
			double radius = Double.parseDouble(split[2]);
			double theta = Double.parseDouble(split[3]);
			double phi = Double.parseDouble(split[4]);
			
			if (type.equals("Target")) {
				Target t = new Target(name, new Coordinate(radius, theta, phi, false));
				reflectables.put(name, t);
				targetList.add(name);
			}
			
			else if (type.equals("Actuator")) {
				Integer port = Integer.parseInt(split[5]);
				Double goalDist = Double.parseDouble(split[6]);
				Double minDist = Double.parseDouble(split[7]);
				Double maxDist = Double.parseDouble(split[8]);
				Double linPotVal = Double.parseDouble(split[9]);
				Double encodeVal = Double.parseDouble(split[10]);
				Double first = Double.parseDouble(split[11]);
				Double second = Double.parseDouble(split[12]);
				Double third = Double.parseDouble(split[13]);
				Coordinate coord = new Coordinate(radius, theta, phi, false);
				Actuator act = new Actuator(name, coord, port, goalDist, minDist, maxDist, linPotVal, encodeVal);
				act.setPVector(new Coordinate(first, second, third, true));
				reflectables.put(name, act);
				actuatorList.add(name);
			}
		}
		
		Bookkeeper.getInstance().setReflectables(reflectables);
		
		return lines;
	}
	
	public static void writeRef(String filename) {
		String out = "\"Type\",\"Name\",\"Radius\",\"Theta\",\"Phi\",\"Port\",\"Goal Dist\",\"minDist\",\"maxDist\",\"lin pot\",\"encoder\",\"pVector Dist\",\"azi\",\"zen\",\"cartesian?\"\n";
		Map<String, Reflectable> reflectables = Bookkeeper.getInstance().getReflectables();
		for (String i : new TreeSet<String>(reflectables.keySet())) {
			out = out + reflectables.get(i).toCSV() + "\n";
		}
		try {
			FileUtils.writeStringToFile(new File(filename), out);
		} catch (IOException e) {
			e.printStackTrace();
			throw new Error("File failed to write.");
		}
	}
	
}