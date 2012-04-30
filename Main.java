/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dalek;
import java.awt.Robot;
import java.awt.AWTException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import javax.comm.*;

/**
 * Dalek - A daemon that listens on the given serial port for activity from a 
 * custom arcade switchboard, and refactors this serial input into keyboard
 * input, for interoperability with keyboard applications.
 * 
 * @author Chris Lockfort (devnull@csh.rit.edu)
 */
public class Main implements SerialPortEventListener{
static boolean debug=true;
static String serialDevice;
static Robot dalek;
static CommPortIdentifier portId;
static Enumeration portList;
static SerialPort serial;
static InputStream input;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        parseArgs(args);
        try{
            dalek = new Robot();
            
            portList = CommPortIdentifier.getPortIdentifiers();
            boolean foundOne=false;
            while (portList.hasMoreElements()) {
                portId = (CommPortIdentifier) portList.nextElement();
                if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                    if(debug){System.out.println("Found port: "+portid.getName());}
                    if (portId.getName().equals(serialDevice)) {
                        System.out.println("Found requested port: "+serialDevice);
                        foundOne=true;
                    }//if(requested device)
                }//if(serial)
            }//while
            if(!foundOne)
                throw new DalekPortNotFoundException();
	    serial = (SerialPort) portId.open("Dalek", 2000);
            input = serial.getInputStream();
            serial.addEventListener(this);
            serial.notifyOnDataAvailable(true);
            serial.setSerialPortParams(115200, SerialPort.DATABITS_8, 
					   SerialPort.STOPBITS_1, 
					   SerialPort.PARITY_NONE);
        }catch(AWTException e){
            if(debug){System.out.println(e.getMessage());}
            System.out.println("Platform configuration does not allow low-level input control! Exterminate!");
        }
        catch(SecurityException e){
            if(debug){System.out.println(e.getMessage());}
            System.out.println("Insufficient permissions to create robot! Try running as a higher priveledged user? Exterminate!");
        }
        catch(DalekPortNotFoundException e){
            System.out.println("Requested serial port "+serialDevice+" not found! Exterminate!");
        }
        catch (PortInUseException e) {
            if(debug){System.out.println(e.getMessage());}
            System.out.println("Requested serial port "+serialDevice+" in use! Exterminate!");
        }
        catch(IOException e){
            if(debug){System.out.println(e.getMessage());}
            System.out.println("IO Exception during stream creation! Exterminate!");
        }
        catch (TooManyListenersException e) {
            if(debug){System.out.println(e.getMessage());}
            System.out.println("Multiple listeners on single port?! Exterminate all Daleks!");
        }
        catch (UnsupportedCommOperationException e) {
            if(debug){System.out.println(e.getMessage());}
            System.out.println("Unsupported COMM operation? Get a better adapter. Exterminate all serial drivers!");
        }
    }

    public static void parseArgs(String[] args){
        if(args.length>0){
            if(args[0].equals("--debug") && args.length>1){
                debug=true;
                serialDevice=args[1];
            }
         else
             serialDevice = args[0];
        }
        else
            printAbout();
    }
    public static void printAbout(){
        System.out.println("Error! Expected command syntax: ");
        System.out.println("dalek SERIAL_DEVICE [--debug]");
        System.out.println("e.x. \"dalek /dev/ttyUSB0 --debug\"");
        System.out.println("Exterminate!");
    }
    
    public void serialEvent(SerialPortEvent event) {
	switch (event.getEventType()) {

	case SerialPortEvent.BI:

	case SerialPortEvent.OE:

	case SerialPortEvent.FE:

	case SerialPortEvent.PE:

	case SerialPortEvent.CD:

	case SerialPortEvent.CTS:

	case SerialPortEvent.DSR:

	case SerialPortEvent.RI:

	case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
	    break;

	case SerialPortEvent.DATA_AVAILABLE:
	    byte[] readBuffer = new byte[4];

	    try {
		while (input.available() > 0) {
		    int numBytes = input.read(readBuffer);
		} 

		System.out.print(new String(readBuffer));
	    } catch (IOException e) {}

	    break;
	}
    } 
}
