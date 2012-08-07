package MMT;

import MMT.tracker.Position;
import MMT.actuators.Actuator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;

public class Persistence {
    public static boolean exists(String filename) {
	return (new File(filename)).exists();
    }
    public static class Reader {
	private ObjectInputStream stream;
	public Reader(String filename) throws IOException {
	    this.stream = new ObjectInputStream(new FileInputStream(filename));
	}
	public Object read() throws IOException, ClassNotFoundException {
	    return this.stream.readObject();
	}
    }
    public static class Writer {
	private ObjectOutputStream stream;
	public Writer(String filename) throws IOException {
	    this.stream = new ObjectOutputStream(new FileOutputStream(filename));
	}
	public void write(Object o) throws IOException, InvalidClassException, NotSerializableException {
	    this.stream.writeObject(o);
	}
    }
}