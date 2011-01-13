package edu.rosehulman;


import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaException;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.rtp.Participant;
import javax.media.rtp.RTPStream;
import javax.media.rtp.ReceiveStream;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.SessionListener;
import javax.media.rtp.SessionManager;
import javax.media.rtp.event.NewParticipantEvent;
import javax.media.rtp.event.NewReceiveStreamEvent;
import javax.media.rtp.event.ReceiveStreamEvent;
import javax.media.rtp.event.SessionEvent;
import javax.media.rtp.rtcp.SourceDescription;

import com.sun.media.rtp.RTPRemoteSourceInfo;
import com.sun.media.rtp.RecvSSRCInfo;

public class RTPMediaNode implements ControllerListener, ReceiveStreamListener, SessionListener  {
	public static final Format[] FORMATS = new Format[] { new AudioFormat(
			AudioFormat.MPEG_RTP, 48000, 16, 2)};
	public static final Format[] FORMATS1 = new Format[] { new AudioFormat(
			AudioFormat.LINEAR)};
	public static final ContentDescriptor CONTENT_DESCRIPTOR = new ContentDescriptor(
			ContentDescriptor.RAW_RTP);

	private MediaLocator mediaLocator = null;
	private DataSink dataSink = null;
	private Processor mediaProcessor = null;
	private Player p;
	private SessionManager mgr;

	/**
	 * Specifies a ProcessorModel for the RTPMediaNode using a DataSource
	 * @param sendStreamSource The DataSource to be set
	 * @throws IOException
	 * @throws MediaException
	 */
	public void setDataSource(DataSource sendStreamSource) throws IOException,
			MediaException {
		this.dataSink = Manager.createDataSink(sendStreamSource,
				this.mediaLocator);
		if (Switchboard.DEBUG)
			System.out.println("... target linked ...");
	}

	/**
	 * Specifies the Processor for the RTPMediaNode using a given Processor
	 * @param p The Processor to be set 
	 * @throws IOException
	 * @throws MediaException
	 */
	public void setDataSource(Processor p) throws IOException, MediaException {
		this.mediaProcessor = p;
		this.dataSink = Manager.createDataSink(this.mediaProcessor.getDataOutput(),
				this.mediaLocator);
		if (Switchboard.DEBUG)
			System.out.println("... target linked and media processed.");
	}

	/**
	 * Sets the MediaLocator, which will be used to create the audio player
	 * @param ml The MediaLocator to be used
	 */
	public void setMediaLocator(MediaLocator ml) {
		this.mediaLocator = ml;
		if (Switchboard.DEBUG)
			System.out.println("Creating the media locator was successful.");
	}

	/**
	 * Tells the node to begin sending sound
	 * @throws IOException
	 */
	public void startStreaming() throws IOException {

		this.dataSink.open();
		this.dataSink.start();
		if (Switchboard.DEBUG)
			System.out.println("... sending.");
	}

	/**
	 * Begin playing sound
	 */
	public void startPlayer() {
		try {
			this.p = Manager.createPlayer(this.mediaLocator);
			if (Switchboard.DEBUG)
				System.out.println("... attached to port ...");
			this.p.addControllerListener(this);
			if (Switchboard.DEBUG)
				System.out.println("... waiting for data ...");
			this.p.realize();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Stop playing sounds
	 * @throws IOException
	 */
	public void stop() throws IOException {
		if (Switchboard.DEBUG)
			System.out.println("... stopping ...");
		this.mediaProcessor.stop();
		this.dataSink.stop();
		this.dataSink.close();
	}

	@Override
	public void controllerUpdate(ControllerEvent event) {
		if (event instanceof RealizeCompleteEvent) {
			if (Switchboard.DEBUG)
				System.out.println("Data recieved ...");
			if (Switchboard.DEBUG)
				System.out.println("... playing stream.");
		}
	}

	/**
	 * Creates the Manager that will be used to control the node
	 * @param address
	 * @param port
	 * @param ttl
	 * @param listener
	 * @return
	 */
	 	@SuppressWarnings("deprecation")
	 
	public SessionManager createManager(String address, int port, int ttl,
			boolean listener) {
	 		
		this.mgr = (SessionManager) new com.sun.media.rtp.RTPSessionMgr();
		if (this.mgr == null)
			return null;

		//mgr.addFormat(new AudioFormat(AudioFormat.DVI_RTP, 44100, 4, 1), 9);
		this.mgr.addFormat(FORMATS[0], 18);

		if (listener)
		{
			this.mgr.addReceiveStreamListener(this);
			this.mgr.addSessionListener(this);
		}
		

		// ask session mgr to generate the local participant's CNAME
		String cname = this.mgr.generateCNAME();
		String username = null;

		try {
			username = System.getProperty("user.name");
		} catch (SecurityException e) {
			username = "jmf-user";
		}

		// create our local Session Address
		SessionAddress localaddr = new SessionAddress();

		try {
			InetAddress destaddr = InetAddress.getByName(address);

			SessionAddress sessaddr = new SessionAddress(destaddr, port,
					destaddr, port + 1);
			SourceDescription[] userdesclist = new SourceDescription[] {
					new SourceDescription(SourceDescription.SOURCE_DESC_EMAIL,
							"jmf-user@sun.com", 1, false),

					new SourceDescription(SourceDescription.SOURCE_DESC_CNAME,
							cname, 1, false),

					new SourceDescription(SourceDescription.SOURCE_DESC_TOOL,
							"JMF RTP Player v2.0", 1, false) };

			this.mgr.initSession(localaddr, userdesclist, 0.05, 0.25);

			this.mgr.startSession(sessaddr, ttl, null);
			if(Switchboard.DEBUG) System.out.println("Session Manager Started.");
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}

		return this.mgr;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void update(ReceiveStreamEvent event) {
	      Player newplayer;
	      DataSource dsource;

	         // create a new player if a new recvstream is detected
	         if (event instanceof NewReceiveStreamEvent)
	         {
	             ReceiveStream stream;
	             
	             try
	             {
	                 // get a handle over the ReceiveStream
	                 stream = ((NewReceiveStreamEvent)event).getReceiveStream();
	 
	 
	                 // get a handle over the ReceiveStream datasource
	                 dsource = stream.getDataSource();
	    	         if (dsource != null) ClientManager.addNewClientWithStream(dsource);

	                 // create a player by passing datasource to the 
	                 // Media Manager
	                 newplayer = Manager.createPlayer(dsource);
	                 newplayer.start();

	                 //TODO: instead of start(), broadcast to clients
	                 System.out.println("created player " + newplayer);


	                 
	             } catch (Exception e) {
	                 System.err.println("NewReceiveStreamEvent exception " 
	                                    + e.getMessage());
	                 e.printStackTrace();
	                 return;
	             }
	 
	             if (newplayer == null) return;
	 
	            	 newplayer.addControllerListener(this);
	            
	         }
	         else{
	        	 System.out.println("Don't know what to do with this: " + event.getParticipant());
	         }

	}

	@Override
	public void update(SessionEvent event) {
		if (event instanceof NewParticipantEvent)
		{
			
			NewParticipantEvent participantEvent = (NewParticipantEvent) event;
			Participant participant = participantEvent.getParticipant();
            System.err.println("  - A new participant had just joined: " + participant.getCNAME()); 
            try
			{
				System.out.println("ip: " + InetAddress.getByName(participant.getCNAME()));
			}
			catch (UnknownHostException exception)
			{
				System.err.println(exception.getClass() + " :: " + exception.getMessage());
			}
//            try
//			{
//				System.out.println(Inet4Address.getLocalHost());
//				InetAddress[] a = InetAddress.getAllByName(InetAddress.getLocalHost().toString().split("/")[0]);
//				for (Object b : a)
//				{
//					System.out.println(b);
//				}
//			}
//			catch (UnknownHostException exception)
//			{
//				// TODO Auto-generated catch-block stub.
//				exception.printStackTrace();
//			}
//            Vector streams = participant.getStreams();
//            if (streams.size() > 0){
//	            try
//				{         
//	            	Object a =  (Inet4Address) (((RTPStream)streams.get(0)).getSenderReport().getSSRC());
//	            	DataSource dS = ((RTPStream) streams.get(0)).getDataSource();
//	            	MediaLocator mL = dS.getLocator();
//					System.err.println("  - IP: " + mL.getURL());
//				}
//				catch (MalformedURLException exception)
//				{
//					// TODO Auto-generated catch-block stub.
//					exception.printStackTrace();
//				}
//            }
            
            	
		}
	}
}