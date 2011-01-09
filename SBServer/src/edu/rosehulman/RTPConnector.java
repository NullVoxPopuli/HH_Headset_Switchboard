package edu.rosehulman;
import java.io.IOException;

import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.rtp.SessionManager;


public class RTPConnector {

	RTPMediaNode receiver = new RTPMediaNode();
	private SessionManager mgr;
	boolean hasNotRecievedQuitCommand = true;

	public String[] getConnections(){
		return null;
		
	}
	
	String kennyIP = "137.112.104.158";
	String kennyPort = "13378";
	String bennieIP = "137.112.104.163";
	String benniePort = "13378";
	public static final Format[] FORMATS = new Format[] { new AudioFormat(AudioFormat.MPEG_RTP, 48000, 16, 1) };


	public void sendStream() throws Exception{
		RTPMediaNode broadcaster = new RTPMediaNode();
		broadcaster.setMediaLocator(new MediaLocator("rtp://"+kennyIP+":"+kennyPort+"/audio"));
		Processor p = Manager.createRealizedProcessor(new ProcessorModel(new MediaLocator("rtp://"+bennieIP+":"+benniePort+"/audio"), FORMATS, new ContentDescriptor(ContentDescriptor.RAW_RTP)));
		broadcaster.setDataSource(p);
		broadcaster.startStreaming();
		
		RTPMediaNode broadcaster2 = new RTPMediaNode();
		broadcaster2.setMediaLocator(new MediaLocator("rtp://"+bennieIP+":"+benniePort+"/audio"));
		Processor p2 = Manager.createRealizedProcessor(new ProcessorModel(new MediaLocator("rtp://"+kennyIP+":"+kennyPort+"/audio"), FORMATS, new ContentDescriptor(ContentDescriptor.RAW_RTP)));
		broadcaster2.setDataSource(p2);
		broadcaster2.startStreaming();
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
		
//		receiver.setMediaLocator(new MediaLocator("rtp://" + Switchboard.serverIP + 
//													":" + Switchboard.serverPort + "/audio"));
		mgr = receiver.createManager(Switchboard.serverIP, Switchboard.serverPort, 100, true);
		
		
		
	}
	
	
	/**
	 * starts an RTP streaming session
	 */
	public void start(){
		// Start Reading (or listening... meh)
		if(Switchboard.DEBUG )
			System.out.println("Trying to get data from " + Switchboard.serverIP + 
													":" + Switchboard.serverPort + "/audio");
//			receiver.startPlayer();
			Thread t = new Thread(new StayAwake(), "Switchboard Server Lifeline");
			t.start();

			try {
				sendStream();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	
	
	public class StayAwake implements Runnable{

		@Override
		public void run() {
			while(hasNotRecievedQuitCommand){
			//wheeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}	
}


