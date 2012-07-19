import java.util.List;

import com.ftdi.FTD2XXException;
import com.ftdi.FTDevice;

public class ProbeFTDI {
    public static void main(String[] args) {
	System.out.println("Finding all devices...");
	List<FTDevice> devices;
	try {
	    devices = FTDevice.getDevices(true);
	} catch (FTD2XXException e) {
	    e.printStackTrace();
	    return;
	}
	printInfo(devices);
	System.out.println();

	System.out.println("Searching for A6007pN3...");
	try {
	    devices = FTDevice.getDevicesBySerialNumber("A6007pN3");
	} catch (FTD2XXException e) {
	    e.printStackTrace();
	    return;
	}
	printInfo(devices);
	System.out.println();

	System.out.println("Searching for A3000wLU...");
	try {
	    devices = FTDevice.getDevicesBySerialNumber("A3000wLU");
	} catch (FTD2XXException e) {
	    e.printStackTrace();
	    return;
	}
	printInfo(devices);
	System.out.println();

	System.out.println("Searching for FT232R USB UART...");
	try {
	    devices = FTDevice.getDevicesByDescription("FT232R USB UART");
	} catch (FTD2XXException e) {
	    e.printStackTrace();
	    return;
	}
	printInfo(devices);
	System.out.println();

	System.out.println("Searching for FT245R USB FIFO...");
	try {
	    devices = FTDevice.getDevicesByDescription("FT245R USB FIFO");
	} catch (FTD2XXException e) {
	    e.printStackTrace();
	    return;
	}
	printInfo(devices);
	System.out.println();
    }

    public static void printInfo(List<FTDevice> devices) {
	for (FTDevice device : devices) {
	    System.out.print("- ");
	    System.out.println(device);
	    System.out.println(" has ID "+device.getDevID());
	    System.out.println(" and type "+device.getDevType());
	    if (device.isOpened())
		System.out.println(" and it's taken");
	    else
		System.out.println(" and it's available");
	}
    }

}
