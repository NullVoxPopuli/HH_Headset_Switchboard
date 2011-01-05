package edu.rosehulman;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaException;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.RealizeCompleteEvent;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.rtp.Participant;
import javax.media.rtp.ReceiveStream;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.SessionManager;
import javax.media.rtp.event.NewReceiveStreamEvent;
import javax.media.rtp.event.ReceiveStreamEvent;
import javax.media.rtp.rtcp.SourceDescription;

public class RTPMediaNode implements ControllerListener, ReceiveStreamListener {
	public static final Format[] FORMATS = new Format[] { new AudioFormat(
			AudioFormat.MPEG_RTP) };
	public static final ContentDescriptor CONTENT_DESCRIPTOR = new ContentDescriptor(
			ContentDescriptor.RAW_RTP);

	private MediaLocator mediaLocator = null;
	private DataSink dataSink = null;
	private Processor mediaProcessor = null;
	private Player p;
	private SessionManager mgr;
	private ArrayList<Player> playerlist = new ArrayList<Player>();

	public void setDataSource(DataSource sendStreamSource) throws IOException,
			MediaException {
		ProcessorModel model = new ProcessorModel(sendStreamSource, FORMATS,
				CONTENT_DESCRIPTOR);
		mediaProcessor = Manager.createRealizedProcessor(model);// (sendStreamSource);
		if (Switchboard.DEBUG)
			System.out.println("... media processed ...");
		dataSink = Manager.createDataSink(mediaProcessor.getDataOutput(),
				mediaLocator);
		if (Switchboard.DEBUG)
			System.out.println("... target linked ...");
	}

	public void setDataSource(Processor p) throws IOException, MediaException {
		mediaProcessor = p;
		dataSink = Manager.createDataSink(mediaProcessor.getDataOutput(),
				mediaLocator);
		if (Switchboard.DEBUG)
			System.out.println("... target linked and media processed.");
	}

	public void setMediaLocator(MediaLocator ml) {
		mediaLocator = ml;
		if (Switchboard.DEBUG)
			System.out.println("... successful.");
	}

	public void startStreaming() throws IOException {
		mediaProcessor.start();

		dataSink.open();
		dataSink.start();
		if (Switchboard.DEBUG)
			System.out.println("... sending.");
	}

	public void startPlayer() {
		try {
			p = Manager.createPlayer(mediaLocator);
			if (Switchboard.DEBUG)
				System.out.println("... attached to port ...");
			p.addControllerListener(this);
			if (Switchboard.DEBUG)
				System.out.println("... waiting for data ...");
			p.realize();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stop() throws IOException {
		if (Switchboard.DEBUG)
			System.out.println("... stopping ...");
		mediaProcessor.stop();
		dataSink.stop();
		dataSink.close();
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

	public SessionManager createManager(String address, int port, int ttl,
			boolean listener) {
		mgr = (SessionManager) new com.sun.media.rtp.RTPSessionMgr();

		if (mgr == null)
			return null;

		mgr.addFormat(new AudioFormat(AudioFormat.DVI_RTP, 44100, 4, 1), 18);

		if (listener)
			mgr.addReceiveStreamListener(this);
		

		// ask session mgr to generate the local participant's CNAME
		String cname = mgr.generateCNAME();
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

			mgr.initSession(localaddr, userdesclist, 0.05, 0.25);

			mgr.startSession(sessaddr, ttl, null);
			if(Switchboard.DEBUG) System.out.println("Session Manager Started.");
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}

		return mgr;
	}

	@Override
	public void update(ReceiveStreamEvent event) {
	      Player newplayer = null;
	 
	         // find the sourceRTPSM for this event
	         SessionManager source = (SessionManager)event.getSource();
	 
	         // create a new player if a new recvstream is detected
	         if (event instanceof NewReceiveStreamEvent)
	         {
	             String cname = "Java Media Player";
	             ReceiveStream stream = null;
	             
	             try
	             {
	                 // get a handle over the ReceiveStream
	                 stream =((NewReceiveStreamEvent)event)
	                         .getReceiveStream();
	 
	                 Participant part = stream.getParticipant();
	 
	                 if (part != null) cname = part.getCNAME();
	 
	                 // get a handle over the ReceiveStream datasource
	                 DataSource dsource = stream.getDataSource();
	                 
	                 // create a player by passing datasource to the 
	                 // Media Manager
	                 newplayer = Manager.createPlayer(dsource);
	                 newplayer.start();
	                 System.out.println("created player " + newplayer);
	             } catch (Exception e) {
	                 System.err.println("NewReceiveStreamEvent exception " 
	                                    + e.getMessage());
	                 return;
	             }
	 
	             if (newplayer == null) return;
	 
	             playerlist.add(newplayer);
	             newplayer.addControllerListener(this);
	            
	             // send this player to player GUI
	         }
	}
}
