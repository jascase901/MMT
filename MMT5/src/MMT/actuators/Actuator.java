/* A MMT.actuators.Actuator is largely just a wrapper around
 * an MMT.actuators.Socket object, providing methods for
 * interacting with the physical actuator rather than forcing
 * client code to use enums and such.
 *
 * The only other things the Actuator class brings to the
 * party are:
 *  - It allows client code to talk in terms of microns
 *    rather than numbers of "steps," and
 *  - It provides a Status class to represent a status
 *    response from the physical actuator.
 */

package MMT.actuators;

import MMT.actuators.Socket;

import java.io.IOException;
import java.lang.InterruptedException;

public class Actuator {
    
    // PUBLIC API
    
    public Actuator(int port) {
        this.socket = new Socket(port);
    }
    
    // The Status class knows how to parse status messages
    //  received from the actuator. A Status object just
    //  stores the information contained by a message.
    public static class Status {
        long position;
        long potentiometerValue;
        long encoderValue;
        boolean isHome;
        public Status(String statusMessage) {
            String[] split = statusMessage.split(" ");
            this.position = Long.parseLong(split[3]);
            this.potentiometerValue = Long.parseLong(split[5]);
            this.encoderValue = Long.parseLong(split[7]);
            this.isHome = (split[8]=="MtrHome");
        }
        public String toString() {
            return "Status(position=" + this.position
            + ", potentiometerValue=" + this.potentiometerValue
            + ", encoderValue=" + this.encoderValue + ")";
        }
    }
    
    public String move(double microns) throws IOException {
        long steps = Math.round(microns * STEPS_PER_MICRON);
        return this.socket.writeRead(Socket.LongCommand.MOVE, steps);
    }
    public String moveAbsolute(double microns) throws IOException {
        long steps = Math.round(microns * STEPS_PER_MICRON);
        return this.socket.writeRead(Socket.LongCommand.MOVE_ABSOLUTE, steps);
    }
    public Status getStatus() throws IOException, InterruptedException {
        return new Status(this.socket.writeWaitRead(Socket.SimpleCommand.GET_STATUS, 100));
    }
    public String turnMotorOn() throws IOException {
        return this.socket.writeRead(Socket.SimpleCommand.MOTOR_ON);
    }
    public String turnMotorOff() throws IOException {
        return this.socket.writeRead(Socket.SimpleCommand.MOTOR_OFF);
    }
    public String turnMotorOffHard() throws IOException {
        return this.socket.writeRead(Socket.SimpleCommand.MOTOR_OFF_HARD);
    }
    
    public String toString() {
        return "Actuator("+this.socket.port+")";
    }
    
    // PRIVATE STUFF
    
    // Static stuff
    private static final double MICRONS_PER_STEP = 0.158;
    private static final double STEPS_PER_MICRON = 1 / MICRONS_PER_STEP;
    
    // Instance members
    private Socket socket;
}
