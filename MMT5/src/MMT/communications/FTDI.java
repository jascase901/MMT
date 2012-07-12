package MMT.communications;

import java.io.IOException;
import java.util.Arrays;

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
        try {
            ft232handle = FTDevice.getDevicesBySerialNumber("A6007pN3").get(0);
            ft245handle = FTDevice.getDevicesBySerialNumber("A3000wLU").get(0);
        } catch (FTD2XXException e1) {
            e1.printStackTrace();
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
        /* If I understand my predecessor's code correctly, the port is a one-byte unsigned integer,
         * and for some reason, the chip we're talking to reads that byte with big-endian bit order,
         * while our computer uses little-endian bit order (or maybe vice versa). Point is, we need
         * to reverse that byte. */
        ft245handle.write(0xff &
                          (((port << 7) & 128) | ((port << 5) & 64) | ((port << 3) & 32) | ((port << 1) & 16) |
                           ((port >> 1) & 8) | ((port >> 3) & 4) | ((port >> 5) & 2) | ((port >> 7) & 1)));
	// A more concise but infinitely more inscrutable implementation follows:
        /* terrible hack to switch the actual bits of the port number
         * required because the computer and the propeller chip use  little-endian and big-endian numbering
         * example: if we want to use port 123
         * 123 is represented as 01111011
         * this next line of code will flip it to 11011110 */
        //int flipped =  (char) (((port * 0x0802L & 0x22110L) | (port * 0x8020L & 0x88440L)) * 0x10101L >> 16);
        //ft245handle.write(flipped);
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
        byte[] read = ft232handle.read(ft232handle.getQueueStatus());
        return new String(read);
    }
}
