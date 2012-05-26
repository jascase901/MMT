package edu.ucsb.deepspace.business;

import smx.tracker.MeasurePointData;


/**
 * Specifies what methods a reflectable object should have.<P>
 * Every reflectable object (target or actuator) needs to have a position, a name, a way to obtain the values of each of those,
 * and a way to get all the information in CSV format.
 * @author Reed Sanpore
 *
 */
public interface Reflectable {
	
	public String getType();
	
	public void setName(String name);
	
	public String getName();
	
	public String toCSV();
	
	public void setCoord(MeasurePointData data);
	
	public void setCoord(Coordinate coordinate);
	
	public Coordinate getCoord();
	
}