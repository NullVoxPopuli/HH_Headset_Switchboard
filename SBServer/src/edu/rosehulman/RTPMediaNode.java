package edu.rosehulman;


import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaException;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.Processor;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.rtp.Participant;
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

import com.sun.media.rtp.RTPSessionMgr;

public class RTPMediaNode implements ControllerListener, ReceiveStreamListener, SessionListener {
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
	
	String kennyIP = "137.112.104.158";
	String kennyPort = "13340";
	String bennieIP = "137.112.104.163";
	String benniePort = "13376";
	
	

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
	
	public void sendStream() throws Exception{
			RTPMediaNode broadcaster = new RTPMediaNode();
			broadcaster.setMediaLocator(new MediaLocator("rtp://"+kennyIP+":"+kennyPort+"/audio"));
			Client kenny = ClientManager.getClients().get(0);
			DataSource d = (DataSource)kenny.getAudioStream(); 
			broadcaster.setDataSource(d);
			broadcaster.startStreaming();
			
			RTPMediaNode broadcaster2 = new RTPMediaNode();
			broadcaster2.setMediaLocator(new MediaLocator("rtp://"+bennieIP+":"+benniePort+"/audio"));
			Client bennie = ClientManager.getClients().get(1);
			DataSource d2 = (DataSource)bennie.getAudioStream();
			broadcaster2.setDataSource(d2);
			broadcaster2.startStreaming();
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
//		if (event instanceof RealizeCompleteEvent) {
//			if (Switchboard.DEBUG)
//				System.out.println("Data recieved ...");
//			p.start();
//			if (Switchboard.DEBUG)
//				System.out.println("... playing stream.");
//		}
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
		
		System.out.println(address);
	
		
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
	      Player newplayer = null;
	      DataSource dsource = null;

	      // find the sourceRTPSM for this event
	         SessionManager source = (SessionManager)event.getSource();
	         // create a new player if a new recvstream is detected
	         if (event instanceof NewReceiveStreamEvent)
	         {
	             String cname = "Switchboard Server Player";
	             ReceiveStream stream = null;
	             
	             try
	             {
	                 // get a handle over the ReceiveStream
	                 stream =((NewReceiveStreamEvent)event)
	                         .getReceiveStream();
	 
	                 Participant part = stream.getParticipant();
	 
	                 if (part != null) cname = part.getCNAME();
	                 // get a handle over the ReceiveStream datasource
	                 dsource = stream.getDataSource();
	                 @SuppressWarnings("unused")
					SessionAddress addr = ((RTPSessionMgr)((NewReceiveStreamEvent)event).getSessionManager()).getSessionAddress();
	    	         if (dsource != null) ClientManager.addNewClientWithStream(dsource);

	                 // create a player by passing datasource to the 
	                 // Media Manager
	                 newplayer = Manager.createPlayer(dsource);
	                 @SuppressWarnings("unused")
					Inet4Address net = (Inet4Address) InetAddress.getByName("1828261086");
	                 newplayer.start();

	                 if (ClientManager.getClients().size() == 2) sendStream();
	                 //TODO: instead of start(), broadcast to clients
	                 System.out.println("created player " + newplayer);
//	                 ((equip.ect.components.rtpviewer.RTPPlayer)newplayer).getAddress();
//	                 System.out.println("Data Source Content Type: " + dsource.getContentType());
//	                 System.out.println(source.generateCNAME());
//	                 System.out.println(source.generateSSRC());
//	                 System.out.println(source.getMulticastScope());
//	                 for (Object r : source.getAllParticipants()) {
////	                	 com.sun.media.rtp.RTPLocalSourceInfo cannot be cast to 
////	                	 com.sun.media.rtp.RTPRemoteSourceInfo
//	                	 RTPLocalSourceInfo t = (RTPLocalSourceInfo) r;
//						System.out.println(t.getCNAME());
//						System.out.println(t.getReports());
//						System.out.println(t.getSourceDescription());
//						System.out.println(t.getStreams());
//					}
//	                 System.out.println(source.getLocalSessionAddress());
//	                 System.out.println(event);
//	                 System.out.println(source.getPeers());
//	                 for (Object r : source.getRemoteParticipants()) {
//	                	 RTPLocalSourceInfo t = (RTPLocalSourceInfo)r;
//							System.out.println(t.getCNAME());
//							System.out.println(t.getReports());
//							System.out.println(t.getSourceDescription());
//							System.out.println(t.getStreams());
//					}
//	                System.out.println(source.getReceiveStreams());
//	                System.out.println(source.getSessionAddress()); 
//	                System.out.println(source.getDefaultSSRC());
//	                System.out.println(event.getSessionManager().generateCNAME());
//	                System.out.println(event.getSessionManager().getSessionAddress());
//	                System.out.println(event.getReceiveStream().getParticipant());


	                 
	             } catch (Exception e) {
	                 System.err.println("NewReceiveStreamEvent exception " 
	                                    + e.getMessage());
	                 e.printStackTrace();
	                 return;
	             }
	 
	             if (newplayer == null) return;
	 
//	             if(Switchboard.DEBUG) 
	            	 newplayer.addControllerListener(this);
	            
	             // send this player to player GUI
	         }
	         else{
	        	 System.out.println("Don't know what to do with this: " + event.getParticipant());
	         }
	         
//	         System.out.println(mgr.getPeers());
//	         System.out.println(mgr.getAllParticipants());
//             for (Object r : this.mgr.getRemoteParticipants()) {
//            	 RTPRemoteSourceInfo t = (RTPRemoteSourceInfo)r;
//					System.out.println(t.getCNAME()); // mac address
//					System.out.println(t.getSourceDescription());
//					System.out.println("source desc");
//					for (Object o : t.getSourceDescription()) {
//						SourceDescription sd = (SourceDescription)o;
//						System.out.println(sd.getDescription());
//						System.out.println(sd.getFrequency());
//						System.out.println(sd.getType());
//						System.out.println(sd.generateCNAME());
//					}
//					for (Object o : t.getStreams()) {
//						if (o instanceof RecvSSRCInfo){
//							RecvSSRCInfo oo = (RecvSSRCInfo)o;
//							System.out.println(oo.getSenderReport());
//						}else if (o instanceof RTPRemoteSourceInfo){
//							RTPRemoteSourceInfo oo = (RTPRemoteSourceInfo)o;
//							System.out.println(oo.getCNAME());
//						}else{
//							System.err.println();
//						}
//					}
//			}

	}

	@Override
	public void update(SessionEvent event) {
		if (event instanceof NewParticipantEvent)
		{
			
			NewParticipantEvent participantEvent = (NewParticipantEvent) event;
			Participant participant = participantEvent.getParticipant();
		}
	}
}
