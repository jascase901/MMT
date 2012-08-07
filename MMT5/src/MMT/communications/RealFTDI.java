package MMT.communications;

import java.io.IOException;
import java.util.Arrays;

import jd2xx.JD2XX;


public class FTDI {
    private static final String FT232_SERIAL_NUMBER = "A6007pN3";
    private static final String FT245_SERIAL_NUMBER = "A3000wLU";
    private static final JD2XX ft232handle = new JD2XX();
    private static final JD2XX ft245handle = new JD2XX();

    static {
        try {
	    System.out.println("Opening "+FT232_SERIAL_NUMBER);
	    ft232handle.openEx(FT232_SERIAL_NUMBER,
			       JD2XX.OPEN_BY_SERIAL_NUMBER);
	    System.out.println("Configuring");
	    ft232handle.setBaudRate(JD2XX.BAUD_9600);
	    ft232handle.setDataCharacteristics(JD2XX.BITS_8,
					       JD2XX.STOP_BITS_1,
					       JD2XX.PARITY_NONE);

	    System.out.println("Opening "+FT245_SERIAL_NUMBER);
	    ft245handle.openEx(FT245_SERIAL_NUMBER,
			       JD2XX.OPEN_BY_SERIAL_NUMBER);
	    System.out.println("Configuring");
	    ft245handle.setBitMode((byte) 0xFF,
				   JD2XX.BITMODE_ASYNC_BITBANG);
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }
    
    public static void close() {
        try {
            ft245handle.close();
            ft232handle.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void set245Port(int port) throws IOException {
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
    
    private static void pause() {
        try {
            Thread.sleep(25);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static void write(byte[] data, int port) throws IOException {
        System.out.println(Arrays.toString(data));
	ft232handle.purge(JD2XX.PURGE_RX | JD2XX.PURGE_TX);
	ft245handle.purge(JD2XX.PURGE_RX | JD2XX.PURGE_TX);
        set245Port(port);
        ft232handle.write(data);
        pause(); //pause a bit because serial communications are sloooooow
    }
    
    public static String read(int port) throws IOException {
        set245Port(port);
        byte[] read = ft232handle.read(ft232handle.getQueueStatus());
        return new String(read);
    }
}
