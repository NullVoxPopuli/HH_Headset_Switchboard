package edu.rosehulman;
import java.io.IOException;

import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.rtp.SessionManager;


public class RTPConnector {

	RTPMediaNode receiver = new RTPMediaNode();
	private SessionManager mgr;

	public String[] getConnections(){
		return null;
		
	}
	
	public void init(){		
		// Set location to read from.... could we maybe just set this
		// to local host? because the IP could potentially change.
		// otherwise, we might need to just have the system auto detect it's
		// IP address. 
		// We might want to consider a case where the IP address changes while 
		// the system is already running.
		if (Switchboard.DEBUG)
			System.out.println("Trying to open receiver port at " +
					Switchboard.serverIP + ":" + Switchboard.serverPort+"/audio");
		
		receiver.setMediaLocator(new MediaLocator("rtp://" + Switchboard.serverIP + 
													":" + Switchboard.serverPort + "/audio"));
		mgr = receiver.createManager(Switchboard.serverIP, Switchboard.serverPort + 2, 100, true);
		
		
		
	}
	
	
	/**
	 * starts an RTP streaming session
	 */
	public void start(){
		// Start Reading (or listening... meh)
		if(Switchboard.DEBUG )
			System.out.println("Trying to get data from " + Switchboard.serverIP + 
													":" + Switchboard.serverPort + "/audio");
			receiver.startPlayer();
		
	}
	
	/**
	 * ends an RTP streaming session
	 */
	public void stop(){
		try {
			receiver.stop();
		} catch (IOException e) {
			System.err.println("We need to do something fancy here if something bad happens");
			e.printStackTrace();
		}
	}
	
	public void listenForStreams(){
		
		
	}
	
	public boolean connectionClosed(){
		
		return false;
	}
	
	public void mergeStreams(){
		
	}
	
	
	
}
