/* The MMT.actuators.Socket class provides a software interface to
 * a physical actuator. We send commands to a physical actuator by
 * writing special bytestrings (each corresponding to a command)
 * out a serial port (handled by the FTDI class), and receive
 * messages by reading the actuator's responses on that same port.
 *
 * The current actuator setup allows for two kinds of commands:
 * commands that require no arguments (like "turn the motor on" or
 * "tell me your status") and commands that take a Long as an
 * argument (like "move N steps relative to your current position"
 * or "move N steps relative to your home position"). A Socket
 * provides external code with two enums, SimpleCommand and
 * LongCommand, which enumerate the recognized commands, and a few
 * functions that will send a command to the actuator, optionally
 * wait a bit, and then return the actuator's response.
 *
 * Messages received from the actuator are essentially stored in
 * a queue. That's why you can't just send a command -- you have
 * to grab the response, too, so that the next call, where the
 * caller might actually care about the response, won't return
 * your response instead.
 * 
 *
 * See the ActuatorProtocol PDF in the help directory for an
 * explanation of the bytestrings used by actuators. The short
 * version is that a command looks like
 *   COMMAND_BYTE [argument bytes] CHECKSUM_BYTE
 * where the checksum is the XOR of all preceding bytes, and the
 * argument is generally either one byte or four bytes (the four-
 * byte case representing a Long).
 *
 * The Socket class is thread-safe: while one thread is in the
 * middle of a call to one of a Socket instance's public methods,
 * other threads' calls to that instance's methods will block.
 * This prevents the following situaton:
 *   Thread A            Thread B            Actuator
 *   Send command X --------------------------->
 *   (wait 100 ms)                           Starts action X
 *        .              Send command Y ------->
 *        .              (wait 50 ms)
 *        .                   .              Finishes action X; responds
 *        .              Reads response <-------
 *
 * which would result in thread B receiving a response to a
 * command it didn't send. That is bad.
 */

package MMT.actuators;

import MMT.communications.FTDI;

import java.util.EnumMap;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class Socket {
    
    // PUBLIC API
    
    // The port over which we communicate with the actuator:
    public int port;
    
    public Socket(int port) {
        this.port = port;
        this.lock = new ReentrantLock();
    }
    
    // SimpleCommand is an enum of the zero-argument commands we can
    //  send to an actuator.
    public static enum SimpleCommand {
        MOTOR_ON, MOTOR_OFF, MOTOR_OFF_HARD, GET_STATUS};
    // LongCommand is an enum of the one-argument commands we can
    //  send to the actuator.
    public static enum LongCommand {
        MOVE, MOVE_ABSOLUTE};
    
    // writeRead will send a command to the actuator and immediately
    //  return the next message in the actuator's response queue.
    public String writeRead(SimpleCommand command) throws IOException {
        this.lock.lock();
        try {
            this.write(command);
            return this.read();
        } finally {this.lock.unlock();}
    }
    public String writeRead(LongCommand command, long x) throws IOException {
        this.lock.lock();
        try {
            this.write(command, x);
            return this.read();
        } finally {this.lock.unlock();}
    }

    // writeWaitRead does the same thing as writeRead, but waits for some
    //  number of milliseconds before returning a response.
    public String writeWaitRead(SimpleCommand command, long msDelay)
    throws IOException, InterruptedException {
        this.lock.lock();
        try {
            this.write(command);
            Thread.sleep(msDelay);
            return this.read();
        } finally {this.lock.unlock();}
    }
    public String writeWaitRead(LongCommand command, long x, long msDelay)
    throws IOException, InterruptedException {
        this.lock.lock();
        try {
            this.write(command, x);
            Thread.sleep(msDelay);
            return this.read();
        } finally {this.lock.unlock();}
    }
    
    // PRIVATE STUFF
    
    // Static stuff
    
    // We use the same FTDI object to communicate with all the actuators.
    // (I'm not really sure how the FTDI thing works, and I'm not keen on
    //  having to learn. Sorry!)
    private static final FTDI ftdi = FTDI.getInstance();
    
    // simpleMessages and longMessages map their respective kinds of Command
    //  onto special byte arrays the actuator will interpret.
    private static final EnumMap<SimpleCommand, byte[]> simpleMessages
        = new EnumMap<SimpleCommand, byte[]>(SimpleCommand.class);
    private static final EnumMap<LongCommand, byte[]> longMessages
        = new EnumMap<LongCommand, byte[]>(LongCommand.class);
    static {
        simpleMessages.put(SimpleCommand.MOTOR_ON, new byte[] {0x11, (byte)0xFF});
        simpleMessages.put(SimpleCommand.MOTOR_OFF, new byte[] {0x11, 0x00});
        simpleMessages.put(SimpleCommand.MOTOR_OFF_HARD, new byte[] {0x15, 0x00});
        simpleMessages.put(SimpleCommand.GET_STATUS, new byte[] {0x3C});
        longMessages.put(LongCommand.MOVE, new byte[] {0x50});
        longMessages.put(LongCommand.MOVE_ABSOLUTE, new byte[] {(byte)0xB0});
    }
    
    // Any outgoing message must end with a checksum byte that is the
    //  XOR of all preceding bytes in the message.
    // appendChecksum will return the input byte array with the checksum
    //  tacked on at the end.
    private static byte[] appendChecksum(byte[] message) {
        byte[] finalMessage = new byte[message.length + 1];
        System.arraycopy(message, 0, finalMessage, 0, message.length);
        
        finalMessage[message.length] = (byte) 0;
        for (byte b : message) finalMessage[message.length] ^= b;
        
        return finalMessage;
    }
    // buildSimpleMessage takes a SimpleCommand and returns the
    //  (checksummed) byte array we would send to the actuator to
    //  make it obey that command.
    private static byte[] buildSimpleMessage(SimpleCommand command) {
        return appendChecksum(simpleMessages.get(command));
    }
    // buildLongMessage does the same thing as buildSimpleMessage, but
    //  with LongCommands (and arguments) instead of SimpleCommands.
    private static byte[] buildLongMessage(LongCommand command, long x) {
        byte[] commandBytes = longMessages.get(command);
        // We want to tack the four bytes of the Long argument onto
        //  the end of the command bytes to get the final (unchecksummed)
        //  message.
        byte[] message = new byte[commandBytes.length + 4];
        System.arraycopy(commandBytes, 0, message, 0, commandBytes.length);
        
        // This loop chops the Long argument into bytes and sticks them
        //  into the final message.
        int shift;
        for (int i=0; i<4; i++) {
            shift = (3-i)*8;
            message[commandBytes.length+i] = (byte) ((x>>shift) & 0xFF);
        }
        
        // Now we just have to append the checksum.
        return appendChecksum(message);
    }
    
    // Instance members
    
    // The lock that ensures only one thread at a time can be calling
    //  any method of an instance:
    private ReentrantLock lock;
    
    // Instance methods
    
    // write sends a command to the actuator. read pops a response off the queue.
    private void write(SimpleCommand command) throws IOException {
        this.lock.lock();
        try {Socket.ftdi.write(Socket.buildSimpleMessage(command), this.port);}
        finally {this.lock.unlock();}
    }
    private void write(LongCommand command, long x) throws IOException {
        this.lock.lock();
        try {Socket.ftdi.write(Socket.buildLongMessage(command, x), this.port);}
        finally {this.lock.unlock();}
    }
    private String read() throws IOException {
        this.lock.lock();
        try {return Socket.ftdi.read(this.port);}
        finally {this.lock.unlock();}
    }
}
