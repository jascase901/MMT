package edu.ucsb.deepspace.business;

import edu.ucsb.deepspace.business.Coordinate;
import edu.ucsb.deepspace.business.Reflectable;
import smx.tracker.MeasurePointData;

/**
 * Represents a static, reflectable point on the MMT.
 * @author Reed Sanpore
 *
 */
public class Target implements Reflectable {
	
	static final private String type = "Target";
	protected String name;
	protected double azimuth;
	protected double zenith;
	protected double distance;
	protected Coordinate coord;
	
	public Target(Target t) {
		this.coord = t.getCoord();
	}
	
	public Target(String name, Coordinate coord) {
		if (name == null) throw new NullPointerException("Name of target is null.");
		if (coord == null) throw new NullPointerException("Coordinate of target is null.");
		this.name = name;
		this.coord = coord;
	}
	
	/**
	 * Sets the value of name to the parameter passed in.
	 * @param name the new name of the target
	 */
	public void setName(String name) {this.name = name;}
	
	/**
	 * @return the value of "name"
	 */
	public String getName() {return name;}
	
	/**
	 * @return the type of the reflectable, in this case "Target"
	 */
	@Override
	public String getType() {return type;}
	
	/**
	 * @return the target's data in string format.
	 */
	@Override
	public String toString() {
		return "type " + type + "name " + name + coord.toString();
	}
	
	/**
	 * @return Returns the target's data in CSV format for export to a file.
	 */
	@Override
	public String toCSV( ) {
		return "\"" + type + "\"," + name + "," + coord.toCsv();
	}
	
	@Override
	public void setCoord(MeasurePointData data) {
		if (data == null) throw new NullPointerException("New MeasurePointData is null.");
		coord = new Coordinate(data);
	}

	@Override
	public void setCoord(Coordinate coordinate) {
		if (coordinate == null) throw new NullPointerException("New coordinate is null.");
		coord = coordinate;
	}

	@Override
	public Coordinate getCoord() {
		return new Coordinate(coord);
	}
	
}