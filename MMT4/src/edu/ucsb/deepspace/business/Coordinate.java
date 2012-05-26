package edu.ucsb.deepspace.business;

import smx.tracker.MeasurePointData;

/**
 * Represents a point in 3d space. <P>
 * Accepts both cartesian or spherical coordinate systems.<BR>
 * Objects of this class are immutable(I hope).
 * @author Reed Sanpore
 *
 */
public class Coordinate {
	
	/** X or radius*/
	final double first;
	/** Y or theta*/
	final double second;
	/** Z or phi*/
	final double third;
	/** true for cartesian, false for spherical*/
	private final boolean isCartesian;
	
	/**
	 * Constructs a new <code>Coordinate</code> from the passed-in values.
	 * @param first the value for the first component
	 * @param second the value for the second component
	 * @param third the value for the third component
	 * @param isCartesian is this coordinate cartesian?
	 */
	public Coordinate(double first, double second, double third, boolean isCartesian) {
		this.first = new Double(first);
		this.second = new Double(second);
		this.third = new Double(third);
		this.isCartesian = Boolean.valueOf(isCartesian);
	}
	
	/**
	 * Constructs a new <code>Coordinate</code> from a <code>MeasurePointData</code>.<P>
	 * Useful for converting a measurement from the tracker into a <code>Coordinate</code>.
	 * @param data the measurement from the tracker
	 */
	public Coordinate(MeasurePointData data) {
		this.first = new Double(data.distance());
		this.second = new Double(data.zenith());
		this.third = new Double(data.azimuth());
		this.isCartesian = false;
	}
	
	public Coordinate(MeasurePointData[] data) {
		Double radius = 0D;
		Double phi = 0D;
		Double theta = 0D;
		for (MeasurePointData d : data) {
				radius = radius + d.distance();
				phi = phi + d.azimuth();
				theta = theta + d.zenith();
		}
		int length = data.length;
		this.first = radius / length;
		this.second = theta / length;
		this.third = phi / length;
		this.isCartesian = false;
	}
	
	public Coordinate(Coordinate coord) {
		if (coord.getCartesian()) {
			this.first = coord.getX();
			this.second = coord.getY();
			this.third = coord.getZ();
			this.isCartesian = true;
		}
		else {
			this.first = coord.getRadius();
			this.second = coord.getTheta();
			this.third = coord.getPhi();
			this.isCartesian = false;
		}
	}
	
	/**
	 * @return the coordinate converted to spherical coordinates
	 */
	public Coordinate toSphere() {
		if (isCartesian) {
			Double radius = Math.sqrt((first*first + second*second + third*third));
			Double theta = Math.acos(third/radius);
			Double phi = Math.atan2(second, first);
			return new Coordinate(radius, phi, theta, false);
		}
		else {
			return new Coordinate(first, second, third, false);
		}
	}
	
	/**
	 * @return the coordinate converted to cartesian coordinates
	 */
	public Coordinate toCartesian() {
		if (isCartesian) {
			return new Coordinate(first, second, third, true);
		}
		else {
			Double x = first*Math.cos(third)*Math.sin(second);
			Double y = first*Math.sin(third)*Math.sin(second);
			Double z = first*Math.cos(second);
			return new Coordinate(x, y, z, true);
		}
	}
	
	/**
	 * This method and similar methods do not depend on whether the coordinate is cartesian or spherical.<P>
	 * Both cases are handled individually.
	 * @return the x-value of the coordinate
	 */
	public double getX() {
		if (isCartesian) {
			return new Double(first);
		}
		else {
			return new Double(first*Math.cos(third)*Math.sin(second));
		}
	}
	
	/**
	 * This method and similar methods do not depend on whether the coordinate is cartesian or spherical.<P>
	 * Both cases are handled individually.
	 * @return the y-value of the coordinate
	 */
	public double getY() {
		if (isCartesian) {
			return new Double(second);
		}
		else {
			return new Double(first*Math.sin(third)*Math.sin(second));
		}
	}
	
	/**
	 * This method and similar methods do not depend on whether the coordinate is cartesian or spherical.<P>
	 * Both cases are handled individually.
	 * @return the z-value of the coordinate
	 */
	public double getZ() {
		if (isCartesian) {
			return new Double(third);
		}
		else {
			return new Double(first*Math.cos(second));
		}
	}
	
	/**
	 * This method and similar methods do not depend on whether the coordinate is cartesian or spherical.<P>
	 * Both cases are handled individually.
	 * @return the radius of the coordinate
	 */
	public double getRadius() {
		if (isCartesian) {
			return new Double(Math.sqrt((first*first + second*second + third*third)));
		}
		else {
			return new Double(first);
		}
	}
	
	/**
	 * This method and similar methods do not depend on whether the coordinate is cartesian or spherical.<P>
	 * Both cases are handled individually.
	 * @return the phi-value of the coordinate
	 */
	public double getPhi() {
		if (isCartesian) {
			return new Double(Math.atan2(second, first));
		}
		else {
			return new Double(third);
		}
	}
	
	/**
	 * This method and similar methods do not depend on whether the coordinate is cartesian or spherical.<P>
	 * Both cases are handled individually.
	 * @return the theta-value of the coordinate
	 */
	public double getTheta() {
		if (isCartesian) {
			return new Double(Math.acos(third/getRadius()));
		}
		else {
			return new Double(second);
		}
	}
	
	public boolean getCartesian() {
		return isCartesian;
	}
	
	/**
	 * Converts this coordinate into a CSV representation.<P>
	 * @return firstCoordinate,secondCoordinate,thirdCoordinate,isCartesian
	 */
	public String toCsv() {
		return this.getRadius() + "," + this.getTheta() + "," + this.getPhi();
	}
	
	public String toString() {
		return "radius " + this.getRadius() + "theta " + this.getTheta() + "phi " + this.getPhi();
	}
	
	/**
	 * Computes the dot product of this coordinate dotted with another coordinate.<P>
	 * Currently does not check to make sure both coordinates are cartesian.  This should be fixed.
	 * @param other the second coordinate
	 * @return the value of the dot product
	 */
	//TODO check to make sure both coordinates are cartesian
	public double dotProduct(Coordinate other) {
		return this.getX()*other.getX() + this.getY()*other.getY() + this.getZ()*other.getZ();
	}
	
}