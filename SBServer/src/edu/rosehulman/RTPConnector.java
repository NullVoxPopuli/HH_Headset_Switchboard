package edu.rosehulman;
import javax.media.rtp.RTPManager;


public class RTPConnector {

	RTPConnectionManager receiver = new RTPConnectionManager();
	private RTPManager mgr;
	boolean hasNotRecievedQuitCommand = true;

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
			System.out.println("Trying to open RTP switchboard port at " +
					Switchboard.serverIP + ":" + Switchboard.serverPort);
		
//		receiver.setMediaLocator(new MediaLocator("rtp://" + Switchboard.serverIP + 
//													":" + Switchboard.serverPort + "/audio"));
		this.mgr = this.receiver.createManager(Switchboard.serverIP, Switchboard.serverPort);
		
		
		
	}
	
	
	/**
	 * starts an RTP streaming session
	 */
	public void start(){
		// Start Reading (or listening... meh)
//		if(Switchboard.DEBUG )
//			System.out.println("Trying to get data from " + Switchboard.serverIP + 
//													":" + Switchboard.serverPort + "/audio");
//			receiver.startPlayer();

	}
	

	/**
	 * ends an RTP streaming session
	 */
	public void stop(){
		/*try {
			this.receiver.stop();
		} catch (IOException e) {
			System.err.println("We need to do something fancy here if something bad happens");
			e.printStackTrace();
		}*/
	}
	
	public void listenForStreams(){
		
		
	}
	
	public boolean connectionClosed(){
		
		return false;
	}
	
	public void mergeStreams(){
		
	}
	
}


