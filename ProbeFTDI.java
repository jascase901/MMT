/* This program prints information about any
 * FTDI devices connected to the computer,
 * particularly the ones corresponding to the
 * actuator control boards.
 */

import java.util.List;

import com.ftdi.FTD2XXException;
import com.ftdi.FTDevice;

public class ProbeFTDI {
    public static void main(String[] args) 
    throws InterruptedException, FTD2XXException {
	List<FTDevice> devices;
	/* We print information about:
	 * - all FTDI devices
	 * - devices with the serial numbers we want
	 * - devices with the descriptions we want. 
	 * We sleep after each call just in case the
	 *  boards can't keep up with the computer. */

	System.out.println("Finding all devices...");
	devices = FTDevice.getDevices(true);
	printInfo(devices);
	Thread.sleep(500);

	System.out.println("Searching for serial number A6007pN3...");
	devices = FTDevice.getDevicesBySerialNumber("A6007pN3");
	printInfo(devices);
	Thread.sleep(500);

	System.out.println("Searching for serial number A3000wLU...");
	devices = FTDevice.getDevicesBySerialNumber("A3000wLU");
	printInfo(devices);
	Thread.sleep(500);

	System.out.println("Searching for description FT232R USB UART...");
	devices = FTDevice.getDevicesByDescription("FT232R USB UART");
	printInfo(devices);
	Thread.sleep(500);

	System.out.println("Searching for description FT245R USB FIFO...");
	devices = FTDevice.getDevicesByDescription("FT245R USB FIFO");
	printInfo(devices);
    }

    public static void printInfo(List<FTDevice> devices) {
	for (FTDevice device : devices) {
	    System.out.print("- ");
	    System.out.println(device);
	    System.out.println(" has ID "+device.getDevID());
	    System.out.println(" and type "+device.getDevType());
	    if (device.isOpened())
		System.out.println(" and it's already been opened.");
	    else
		System.out.println(" and it's available.");
	}
	System.out.println();
    }
}