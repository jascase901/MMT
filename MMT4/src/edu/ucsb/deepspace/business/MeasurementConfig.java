package edu.ucsb.deepspace.business;

public class MeasurementConfig {
	
	private String name;
	private int numObservations;
	private double observationRate;
	private int samplesPerObservation;
	private double preIdleTime, postIdleTime;
	private boolean servo, flag;
	
	
	public MeasurementConfig(String name, int numObservations, double observationRate, int samplesPerObservation, double postIdleTime, double preIdleTime, boolean servo, boolean flag) {
		this.name = name;
		this.numObservations = numObservations;
		this.observationRate = observationRate;
		this.samplesPerObservation = samplesPerObservation;
		this.preIdleTime = preIdleTime;
		this.postIdleTime = postIdleTime;
		this.servo = servo;
		this.flag = flag;
	}
	
	public String getName() {return name;}

	public int getNumObservations() {return numObservations;}

	public double getObservationRate() {return observationRate;}

	public int getSamplesPerObservation() {return samplesPerObservation;}
	
	public double getPreIdleTime() {return preIdleTime;}

	public double getPostIdleTime() {return postIdleTime;}
	
	public boolean getServo() {return servo;}
	
	public boolean getFlag() {return flag;}
	
}