package edu.ucsb.deepspace.business;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import edu.ucsb.deepspace.business.Coordinate;


/**
 * Used to represent a single actuator in the MMT.<P>
 * This class provides functionality to check the actuators status and to move the actuator.
 * Future functionality will provide control over the LEDs.
 * @author Reed Sanpore
 *
 */
public class Actuator extends Target {
	final private String type = "Actuator";
	private Integer port;
	private double goalDist;
	private double minDist;
	private double maxDist;
	private double linPotVal;
	private double encodeVal;
	private Coordinate pVector;
	private double[] intTemps = new double[8];
	private double[] extTemps = new double[8];
	private static FTDI protocol = FTDI.getInstance();
	//private static FTDIact protocol = FTDIact.getInstance();

	/**
	 * @param name
	 * @param azimuth
	 * @param zenith
	 * @param distance
	 * @param port
	 * @param goalDist
	 * @param minDist
	 * @param maxDist
	 * @param linPotVal
	 * @param encodeVal
	 */
	public Actuator(String name, Coordinate coord, Integer port, Double goalDist, Double minDist,
			Double maxDist, Double linPotVal, Double encodeVal) {
		super(name, coord);
		this.port = port;
		this.goalDist = goalDist;
		this.minDist = minDist;
		this.maxDist = maxDist;
		this.linPotVal = linPotVal;
		this.encodeVal = encodeVal;
	}
	
	/**
	 * @return the type of the reflectable, in this case "Actuator"
	 */
	@Override
	public String getType() {
		return this.type;
	}

	/**
	 * Sets the port number of this actuator to the parameter passed in.
	 * @param port the new port number
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	/**
	 * @return the value of the port number
	 */
	public Integer getPort() {
		return this.port;
	}

	/**
	 * Sets the desired distance from the tracker for this actuator.  This is the distance that the actuator wants to be at.
	 * @param goalDist the new desired distance 
	 */
	public void setGoalDist(double goalDist) {
		this.goalDist = goalDist;
	}

	/**
	 * @return the desired distance
	 */
	public double getGoalDist() {
		return this.goalDist;
	}

	/**
	 * Set the minimum distance of the actuator.  This is to be used so that the actuator isn't moved into a position it can't reach.<BR>
	 * This feature is currently unimplemented. (8-23-2010)
	 * @param minDist the new minimum distance for this actuator
	 */
	public void setMinDist(double minDist) {
		this.minDist = minDist;
	}

	/**
	 * @return the minimum distance
	 */
	public double getMinDist() {
		return this.minDist;
	}

	/**
	 * Set the maximum distance for the actuator.  This is to be used so that the actuator isn't moved into a position it can't reach.<BR>
	 * This feature is currently unimplemented. (8-23-2010)
	 * @param maxDist the new maximum distance for this actuator
	 */
	public void setMaxDist(double maxDist) {
		this.maxDist = maxDist;
	}

	/**
	 * @return the maximum distance
	 */
	public double getMaxDist() {
		return this.maxDist;
	}

	/**
	 * Sets the value of the linear potentiometer.
	 * @param linPotVal the new value
	 */
	public void setLinPotVal(double linPotVal) {
		this.linPotVal = linPotVal;
	}

	/**
	 * NOTE:  This does not obtain a new value from the actuator itself.  It merely returns the value stored in this field.<BR>
	 * Make sure to update this field as much as possible so that this method returns relevant information.
	 * @return the value of the linear potentiometer
	 */
	public double getLinPotVal() {
		return this.linPotVal;
	}
	
	/**
	 * Sets the value of the digital encoder.
	 * @param encodeVal the new value
	 */
	public void setEncodeVal(double encodeVal) {
		this.encodeVal = encodeVal;
	}

	/**
	 * NOTE:  This does not obtain a new value from the actuator itself.  It merely returns the value stored in this field.<BR>
	 * Make sure to update this field as much as possible so that this method returns relevant information.
	 * @return the value of the digital encoder
	 */
	public double getEncodeVal() {
		return this.encodeVal;
	}
	
	/**
	 * Sets the value of the pointing vector associated with this actuator.
	 * @param pVector the new vector
	 */
	public void setPVector(Coordinate pVector) {
		this.pVector = pVector;
	}
	
	/**
	 * @return the pointing vector
	 */
	public Coordinate getPVector() {
		return this.pVector;
	}
	
	/**
	 * Sets the value of the internal temps array.
	 */
	public void setIntTemps(double[] intTemps) {
		this.intTemps = intTemps;
	}
	
	/**
	 * Gets the value of the internal temps array.
	 */
	public double[] getIntTemps() {
		return this.intTemps;
	}
	
	/**
	 * Sets the value of the external temps array.
	 */
	public void setExtTemps(double[] extTemps) {
		this.extTemps = extTemps;
	}
	
	/**
	 * Gets the value of the external temps array.
	 */
	public double[] getExtTemps() {
		return this.extTemps;
	}
	
	/**
	 * @return the target's data in string format.
	 */
	@Override
	public String toString() {
		return "type " + type + "name " + name + coord.toString() +
					"port " + port + "goalDist " + goalDist + "minDist " + minDist +
					"maxDist " + maxDist + "linPotVal " + linPotVal + "encodeVal " + encodeVal;
	}
	
	/**
	 * @return Returns the target's data in CSV format for export to a file.
	 */
	@Override
	public String toCSV() {
		return "\"" + type + "\"," + name + "," + coord.toCsv() +
					"," + port + "," + goalDist + "," + minDist +
					"," + maxDist + "," + linPotVal + "," + encodeVal + "," + pVector.toCsv();
	}
	
	/**
	 * Called at beginning of SendCommand class to see if actuator is working.  
	 * @return true if the FTDI chip receives a message back
	 * @throws IOException
	 */
	public boolean alive() throws IOException {
		byte[] out = {0x3C, 0x3C};
		protocol.write(out, port);
		String temp = protocol.read(port);
		if (temp.length() == 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * Sends a command to the physical actuator via the {@link edu.ucsb.deepspace.business.actuator.system.FTDIact} class.<P>
	 * For more information regarding the physical actuators, please see: actuators.txt
	 * @param command a string that MUST equal one of the following:  "Move Relative", "Move Absolute", "Set Absolute",
	 * or "LED"
	 * @param steps the value associated with these commands
	 * @return a simple status message that gives information about the success of the command
	 * @throws IOException if there was an error writing to the FTDI chip
	 */
	public String sendCommand2(String command, long steps) throws IOException {
		byte[] data = new byte[6];
		
		if (command.equals("Move Relative")) {
			data[0] = 0x50;
		}
		else if (command.equals("Move Absolute")) {
			data[0] = (byte) 0xB0;
		}
		else if (command.equals("Set Absolute")) {
			data[0] = 0x3A;
		}
		else if (command.equals("LED")) {
			data[0] = 0x75;
		}
		else {
			return "Error.  Bad command sent.";
		}
		
		data[5] = data[0];
    	for (int i = 1; i < 5; i++) {
    		data[i] = (byte) ((steps>>((4-i)<<3))&0xFF); // split the long into 4 bytes
    		data[5] ^= data[i]; // checksum byte is the other 5 bytes ORed together
    	}
    	//System.out.println(Arrays.toString(data));
    	protocol.write(data, port);
    	return protocol.read(port);
	}
	
	/**
	 * Turns the motor on or off.  Motor will not turn off unless it is in the home position.
	 * @param onOrOff FF - on.  00 - off.
	 * @throws IOException
	 */
	public String motorControl(byte onOrOff) throws IOException {
		byte[] out = {0x11, onOrOff, (byte) (0x11^onOrOff)};
		protocol.write(out, port);
		return protocol.read(port);
	}
	
	/**
	 * Turns the motor off without regard for the home position.
	 * @return
	 * @throws IOException
	 */
	public String motorReallyOff() throws IOException {
		byte[] out = {0x15, 0x00, 0x15^0x00};
		protocol.write(out, port);
		return protocol.read(port);
	}
	
	/**
	 * Sends a command to the physical actuator via the {@link edu.ucsb.deepspace.business.actuator.system.FTDIact} class.<P>
	 * For more information regarding the physical actuators, please see: actuators.txt
	 * @param command the command to be sent.  must be "Get Status", "Internal Temp", or "External Temp"
	 * @return
	 * @throws IOException
	 */
	public String sendCommand(ActuatorCommand command) throws IOException {
		byte[] out = new byte[2];
		switch (command) {
			case GETSTATUS:
				out = new byte[] {0x3C, 0x3C}; break;
			case INTERNALTEMP:
				out = new byte[] {0x3F, 0x3F}; break;
			case EXTERNALTEMP:
				out = new byte[] {0x30, 0x30}; break;
			default:
				assert false; //These are the only acceptable commands for this method.
		}
		
		
//		if (command.equals("Get Status")) {
//			out = new byte[] {0x3C, 0x3C};
//		}
//		else if (command.equals("Internal Temp")) {
//			out = new byte[] {0x3F, 0x3F};
//		}
//		else if (command.equals("External Temp")) {
//			out = new byte[] {0x30, 0x30};
//		}
//		else {
//			return "error.\n";
//		}
		protocol.write(out, port);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			System.out.println("This should have never happened." +
							   "Message from sendCommand in the Actuator class.");
			e.printStackTrace();
		}
		String temp = protocol.read(port);
//		if (command.equals("Get Status")) {
//			updateStuff(temp);
//		}
		return temp;
	}
	
	/**
	 * Causes the actuator to restart.<P>
	 * This is useful after the EEPROM is updated.  The new EEPROM is only used after the actuator is rebooted.
	 * @return
	 * @throws IOException
	 */
	public String reboot() throws IOException {
		byte[] out = {0x52, 0x45, 0x42, 0x4F, 0x4F, 0x54};
		protocol.write(out, port);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String result = protocol.read(port);
		return result.replace('\0', ' ');
	}
	
	public String readEeprom() throws IOException {
		byte[] out = {0x27, 0x55, (byte) 0xCC};
		protocol.write(out, port);
		try {
			Thread.sleep(40000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String response = protocol.read(port);
		//String eeprom = Eeprom.read2("main.EEPROM");
		String eeprom = new String(Eeprom.read("main.EEPROM"));
		if (response.contains(eeprom)) {
			return "EEPROM successfully written";
		}
		else {
			return "Error writing EEPROM.  Immediately try again.";
		}
	}
	
	public String writeThenReadEEPROM() throws IOException, InterruptedException {
		byte[] command = new byte[] {(byte) 0xAA, 0x55, (byte) 0xCC};
		byte[] eepromBytes = Eeprom.read("main2.EEPROM");
		byte[] output = ArrayUtils.addAll(command, eepromBytes);
		protocol.write(output, port);
		Thread.sleep(45000);
		String result = protocol.read(port);
		if (result.contains("Done Programming")) {
			command = new byte[] {0x27, 0x55, (byte) 0xCC};
			protocol.write(command, port);
			Thread.sleep(45000);
			String response = protocol.read(port);
			String eeprom = new String(eepromBytes);
			if (response.contains(eeprom)) {
				return "EEPROM successfully written";
			}
			else {
				return "Error writing EEPROM.  Immediately try again.";
			}
		}
		return "error";
	}
	
//	private void updateStuff(String temp) {
//		String[] info = temp.split(" ");
//		linPotVal = Double.parseDouble(info[5]);
//		encodeVal = Double.parseDouble(info[7]);
//	}
	
	/**
	 * Gets the position information from the physical actuator.
	 * @return a map with keys "Pos", "Pot", and "Enc"
	 * @throws IOException if there was an error writing to the FTDI chip
	 */
	public Map<String, Double> getPosInfo() throws IOException {
		Map<String, Double> data = new HashMap<String, Double>();

		sendCommand(ActuatorCommand.GETSTATUS);

		String[] info = protocol.read(port).split(" ");
		String t = "";
		for (String s : info) {
			t = t + s + " ";
		}
		setLinPotVal(Double.parseDouble(info[5]));
		setEncodeVal(Double.parseDouble(info[7]));
		
		data.put("Pos", Double.parseDouble(info[3]));
		data.put("Pot", Double.parseDouble(info[5]));
		data.put("Enc", Double.parseDouble(info[7]));
		return data;
	}
	
	public void test() throws IOException, InterruptedException {
		//protocol.read(port, 10);
	}
}
