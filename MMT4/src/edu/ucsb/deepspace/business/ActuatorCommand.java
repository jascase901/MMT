package edu.ucsb.deepspace.business;

public enum ActuatorCommand {
	MOVERELATIVE, MOTORON, 
	MOTORREALLYOFF, 
	MOTOROFF, GETSTATUS, 
	INTERNALTEMP, EXTERNALTEMP, 
	REBOOT, READEEPROM, 
	WRITEEPROM, LED;
}