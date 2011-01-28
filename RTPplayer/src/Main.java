import java.net.InetAddress;
import java.util.Vector;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;


public class Main {
	
	public static boolean Debug = true;
	public static final Format[] FORMATS = new Format[] { new AudioFormat(AudioFormat.LINEAR) };
	
	public static void main(String[] arstring) throws Exception {
		//Switch for broadcasting and receiving
		boolean isServer = true;
		String myIp = InetAddress.getLocalHost().getHostAddress();
		String targetIp = "192.168.219.128";
		String myPort = "13378";
		String theirPort = "13378";
				
		
		Vector devices= CaptureDeviceManager.getDeviceList(null);
		System.out.println("Number of capture devices: " + devices.size());
		for (int n = 0; n < devices.size(); n++) {
		    CaptureDeviceInfo info = (CaptureDeviceInfo) devices.elementAt(n);
		    System.out.println(info.toString());
		}
		// READ ME!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// So there's two steps to take toward streaming the mic.
		// FIRST set up the ip addresses
		// the myIp variable needs to be your ip address
		// the targetIp variable needs to be the ip address of the computer you're streaming to
		// SECOND use "isServer" to switch whether the instance of the program is the broadcaster or receiver
		
		if (isServer)
		{
			//Create broadcaster
			RTPMediaNode broadcaster = new RTPMediaNode();
			
			//Set location to broadcast to
			if (Debug) System.out.println("Attempting to target " + targetIp + ":" + theirPort + " ...");
			broadcaster.setMediaLocator(new MediaLocator("rtp://"+targetIp+":"+theirPort+"/audio"));
			
			//Connect to microphone
			if (Debug) System.out.println("Attempting to connect to microphone ...");
			Processor p = Manager.createRealizedProcessor(new ProcessorModel(new MediaLocator("javasound://44100"), FORMATS, new ContentDescriptor(ContentDescriptor.RAW_RTP)));
			if (Debug) System.out.println("... connection established ...");
			broadcaster.setDataSource(p);
			
			//Start broadcasting
			if (Debug) System.out.println("Trying to send to target ...");
			broadcaster.startStreaming();
		}
		else
		{
			
			RTPMediaNode receiver = new RTPMediaNode();
			
			//Set location to read from
			if (Debug) System.out.println("Trying to open receiver port at " + myIp + ":" + myPort + " ...");
			receiver.setMediaLocator(new MediaLocator("rtp://"+myIp+":"+myPort+"/audio"));
			
			//Start reading
			receiver.initializePlayer();
		}
	}
}