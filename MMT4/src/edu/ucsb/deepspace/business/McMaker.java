package edu.ucsb.deepspace.business;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class McMaker {
	
	private int count = 1;
	private Calendar cal;
	private double testLength; //minutes
	private int[] order;
	private String next;
	private double time;

	public McMaker(Calendar begin, double testLength) {
		this.cal = begin;
		this.testLength = testLength;
		time = new GregorianCalendar(TimeZone.getDefault()).getTimeInMillis();
	}
	
	//does this need to be here?
	//or should this method call other methods, depending on what type of test is being performed?
	//once again, naming needs work
	public MeasurementConfig nextMc() {
		MeasurementConfig mc = new MeasurementConfig("1", 1, 1, 1024, 0, 0, true, true);
		if (time-cal.getTimeInMillis() > testLength*60*1000) {
			mc = new MeasurementConfig("1", 1, 1, 1024, 0, 0, true, false);
		}
		count++;
		if (count == 3) {
			count = 1;
		}
		return mc;
	}
	
	//name needs work
	public MeasurementConfig nextSingleReflTest() {
		String name = "";
		MeasurementConfig mc = new MeasurementConfig(name, 1, 1, 1024, 0, 0, true, true);
		return mc;
	}
	
	//naming and looping through in proper order needs work
	public MeasurementConfig nextMultReflTest() {
		MeasurementConfig mc = new MeasurementConfig(next, 1, 1, 1024, 0, 0, true, true);
		if (time-cal.getTimeInMillis() > testLength*60*1000) {
			mc = new MeasurementConfig("1", 1, 1, 1024, 0, 0, true, false);
		}
		next = String.valueOf((order[count]));
		count++;
		if (count == next.length()) {
			count = 0;
		}
		return mc;
	}
	
	//probably works, haven't tested
	//name needs to be fixed
	public MeasurementConfig fastSampe() {
		MeasurementConfig mc = new MeasurementConfig("name", 4096, 0.0009765625, 1, 0, 0, false, false);
		return mc;
	}
}
