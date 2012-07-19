package edu.ucsb.deepspace.business;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.ftdi.BitModes;
import com.ftdi.FTD2XXException;
import com.ftdi.FTDevice;
import com.ftdi.Parity;
import com.ftdi.StopBits;
import com.ftdi.WordLength;


public class FTDI {
    private static final FTDI INSTANCE = new FTDI();
    private FTDevice ft232handle;
    private FTDevice ft245handle;
    public static FTDI getInstance() {return INSTANCE;}
    
    private FTDI() {
	System.out.println("Finding all devices...");
        try {
	    List<FTDevice> devices = FTDevice.getDevices(true);
	    for (FTDevice device : devices) {
		System.out.println("One has serial number "+device.getDevSerialNumber());
	    }
	} catch (FTD2XXException e) {
	    e.printStackTrace();
	}
	System.out.println("Finding FT232R USB UART...");
	try {
	    ft232handle = FTDevice.getDevicesByDescription("FT232R USB UART").get(0);
	} catch (FTD2XXException e) {
	    e.printStackTrace();
	}
	System.out.println("Finding FT245R USB FIFO...");
	try {
            ft245handle = FTDevice.getDevicesByDescription("FT245R USB FIFO").get(0);
        } catch (FTD2XXException e) {
            e.printStackTrace();
        }
        try {
            ft232handle.open();
            ft232handle.setBaudRate(9600);
            ft232handle.setDataCharacteristics(WordLength.BITS_8, StopBits.STOP_BITS_1, Parity.PARITY_NONE);
            
            ft245handle.open();
            ft245handle.setBitMode((byte) 0xFF, BitModes.BITMODE_ASYNC_BITBANG);
        } catch (FTD2XXException e) {
            e.printStackTrace();
        }
        
    }
    
    public void close() {
        try {
            ft245handle.close();
            ft232handle.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void set245Port(int port) throws IOException {
        // terrible hack to switch the actual bits of the port number
        // required because the computer and the propeller chip use  little-endian and big-endian numbering
        // example: if we want to use port 204
        // 204 is represented as 11001100
        // this next line of code will flip it to 00110011, which is 51 in decimal
        // source of this code: http://graphics.stanford.edu/~seander/bithacks.html#ReverseByteWith32Bits
        int flipped =  (char) (((port * 0x0802L & 0x22110L) | (port * 0x8020L & 0x88440L)) * 0x10101L >> 16);
        ft245handle.write(flipped);
    }
    
    private void purge245() throws FTD2XXException {
        ft245handle.purgeBuffer(true, true);
    }
    
    private void purge232() throws FTD2XXException {
        ft232handle.purgeBuffer(true, true);
    }
    
    private void pause() {
        try {
            Thread.sleep(25);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void write(byte[] data, int port) throws IOException {
        System.out.println(Arrays.toString(data));
        purge245(); //remove any leftovers from the queues
        purge232();
        set245Port(port);
        ft232handle.write(data);
        pause(); //pause a bit so that when read() is called, there is something to be read
        pause();
    }
    
    public String read(int port) throws IOException {
        set245Port(port);
        //pause();
        byte[] read = ft232handle.read(ft232handle.getQueueStatus());
        //ft232handle.read(ft232handle.getQueueStatus());
        System.out.println(Arrays.toString(read));
        return new String(read);
        //return new String(ft232handle.read(ft232handle.getQueueStatus()));
    }
}
