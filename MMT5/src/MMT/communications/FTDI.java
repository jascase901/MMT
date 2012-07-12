/* This is the dummy FTDI class I made.
 * It returns boring status messages.
 * It's not well-documented because this file should never
 *  be read by anybody. In the next day or two, I should be
 *  testing things for real, using the real FTDI class.
 */

package MMT.communications;

import java.lang.InterruptedException;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class FTDI {
    // PUBLIC API
    public static FTDI getInstance() {return instance;}
    
    // Writes a message out a given port:
    public void write(byte[] message, int port) throws IOException {
        byte checksum = (byte) 0;
        for (byte b : message) checksum ^= b;

        this.lock.lock();
        if (checksum == 0) this.buffer += dummyResponse;
        else this.buffer += badChecksumResponse;
        this.lock.unlock();
    }
    public String read(int port) throws IOException {
        this.lock.lock();
        try {
            String[] split = this.buffer.split("eol", 2);
            
            String result = split[0];
            if (split.length > 1) this.buffer = split[1];
            else this.buffer = "";
            
            return result;
        } finally {this.lock.unlock();}
    }
    
    // PRIVATE STUFF
    
    // Constructor
    private FTDI() {
        buffer = "";
        lock = new ReentrantLock();
    }
    
    // Static stuff
    private static final FTDI instance = new FTDI();
    private static final String dummyResponse =
        "AckB GSt Pos 32 Pot 9098 Enc 0 MtrHome eol";
    private static final String badChecksumResponse =
        "Bad checksum eol";
    
    // Instance members
    private String buffer;
    private ReentrantLock lock;
    
    
}
